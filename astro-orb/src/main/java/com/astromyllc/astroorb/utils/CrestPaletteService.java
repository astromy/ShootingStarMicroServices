package com.astromyllc.astroorb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Works out a school's two brand colours from its crest - no manual selection.
 *
 * How: sample the crest's pixels, ignore transparent, near-white, near-black
 * and grey pixels (crest outlines and backgrounds), and group the rest by hue.
 * The two strongest hue groups at least 45 degrees apart become the school's
 * colours, with the darker one as the primary. If the crest has only one real
 * colour, the secondary is a lighter shade of it.
 *
 * The primary is darkened if needed so it keeps at least 3:1 contrast against
 * white - it colours large text such as "STUDENT ID CARD" on white card areas.
 *
 * Results are cached per institution and recalculated automatically when the
 * crest changes (the cache key includes a hash of the crest).
 */
@Component
@Slf4j
public class CrestPaletteService {

    public record Palette(String primary, String secondary, String source) {
    }

    /** Used when a school has no crest, or its crest has no usable colour (e.g. black and white). */
    public static final Palette DEFAULT_PALETTE = new Palette("#1F3A5F", "#3A7BC8", "default");

    private static final int HUE_BINS = 24;               // 15 degrees each
    private static final int MIN_SECONDARY_DISTANCE = 3;  // bins, i.e. 45 degrees of hue
    private static final double MIN_SECONDARY_SHARE = 0.10;
    private static final int SAMPLE_GRID = 96;             // sample about 96x96 points
    private static final double MIN_PRIMARY_CONTRAST = 3.0;

    private record CacheEntry(int crestHash, Palette palette) {
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public Palette paletteFor(String institutionCode, String crestBase64) {
        if (crestBase64 == null || crestBase64.isBlank()) {
            return DEFAULT_PALETTE;
        }
        int crestHash = crestBase64.hashCode();
        CacheEntry cached = cache.get(institutionCode);
        if (cached != null && cached.crestHash() == crestHash) {
            return cached.palette();
        }
        Palette palette = extract(crestBase64);
        cache.put(institutionCode, new CacheEntry(crestHash, palette));
        log.info("Crest palette for {}: primary={}, secondary={} ({})",
                institutionCode, palette.primary(), palette.secondary(), palette.source());
        return palette;
    }

    private Palette extract(String crestBase64) {
        BufferedImage image;
        try {
            String data = crestBase64.contains(",")
                    ? crestBase64.substring(crestBase64.indexOf(',') + 1)
                    : crestBase64;
            image = ImageIO.read(new ByteArrayInputStream(Base64.getMimeDecoder().decode(data)));
        } catch (IllegalArgumentException | IOException e) {
            log.warn("Could not read crest image, using default card colours: {}", e.getMessage());
            return DEFAULT_PALETTE;
        }
        if (image == null) {
            return DEFAULT_PALETTE;
        }

        double[] weight = new double[HUE_BINS];
        double[] red = new double[HUE_BINS];
        double[] green = new double[HUE_BINS];
        double[] blue = new double[HUE_BINS];

        int step = Math.max(1, Math.max(image.getWidth(), image.getHeight()) / SAMPLE_GRID);
        for (int y = 0; y < image.getHeight(); y += step) {
            for (int x = 0; x < image.getWidth(); x += step) {
                int argb = image.getRGB(x, y);
                if ((argb >>> 24) < 128) {
                    continue; // transparent
                }
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                double[] hsl = toHsl(r, g, b);
                if (hsl[1] < 0.25 || hsl[2] < 0.12 || hsl[2] > 0.88) {
                    continue; // grey, near-black or near-white: outlines and backgrounds
                }
                int bin = (int) (hsl[0] * HUE_BINS) % HUE_BINS;
                double w = hsl[1]; // more saturated pixels say more about the brand colour
                weight[bin] += w;
                red[bin] += r * w;
                green[bin] += g * w;
                blue[bin] += b * w;
            }
        }

        int first = strongestBin(weight, -1);
        if (first < 0) {
            return DEFAULT_PALETTE;
        }
        int[] primary = averageAround(first, weight, red, green, blue);

        int second = strongestBin(weight, first);
        int[] secondary;
        if (second >= 0 && weight[second] >= weight[first] * MIN_SECONDARY_SHARE) {
            secondary = averageAround(second, weight, red, green, blue);
            // The primary colours the darker parts of the artwork and large text on
            // white, so the darker of the two crest colours takes that role
            // (e.g. gold and navy -> navy primary, gold secondary).
            if (contrastWithWhite(secondary) > contrastWithWhite(primary)) {
                int[] swap = primary;
                primary = secondary;
                secondary = swap;
            }
        } else {
            secondary = lighterShade(primary);
        }

        primary = ensureContrastWithWhite(primary, MIN_PRIMARY_CONTRAST);
        secondary = capLightness(secondary, 0.75);

        return new Palette(toHex(primary), toHex(secondary), "crest");
    }

    /** Strongest bin; when {@code awayFrom} >= 0, only bins at least MIN_SECONDARY_DISTANCE from it. */
    private static int strongestBin(double[] weight, int awayFrom) {
        int best = -1;
        for (int i = 0; i < HUE_BINS; i++) {
            if (weight[i] <= 0) {
                continue;
            }
            if (awayFrom >= 0 && circularDistance(i, awayFrom) < MIN_SECONDARY_DISTANCE) {
                continue;
            }
            if (best < 0 || weight[i] > weight[best]) {
                best = i;
            }
        }
        return best;
    }

    private static int circularDistance(int a, int b) {
        int d = Math.abs(a - b) % HUE_BINS;
        return Math.min(d, HUE_BINS - d);
    }

    /** Weighted average colour of a bin and its two neighbours, so a hue split across a bin edge isn't lost. */
    private static int[] averageAround(int bin, double[] weight, double[] r, double[] g, double[] b) {
        double w = 0, sr = 0, sg = 0, sb = 0;
        for (int offset = -1; offset <= 1; offset++) {
            int i = (bin + offset + HUE_BINS) % HUE_BINS;
            w += weight[i];
            sr += r[i];
            sg += g[i];
            sb += b[i];
        }
        return new int[]{(int) Math.round(sr / w), (int) Math.round(sg / w), (int) Math.round(sb / w)};
    }

    private static int[] lighterShade(int[] rgb) {
        double[] hsl = toHsl(rgb[0], rgb[1], rgb[2]);
        return fromHsl(hsl[0], hsl[1] * 0.85, Math.min(0.70, hsl[2] + 0.20));
    }

    private static int[] ensureContrastWithWhite(int[] rgb, double minContrast) {
        double[] hsl = toHsl(rgb[0], rgb[1], rgb[2]);
        int[] current = rgb;
        while (contrastWithWhite(current) < minContrast && hsl[2] > 0.10) {
            hsl[2] -= 0.04;
            current = fromHsl(hsl[0], hsl[1], hsl[2]);
        }
        return current;
    }

    private static int[] capLightness(int[] rgb, double maxLightness) {
        double[] hsl = toHsl(rgb[0], rgb[1], rgb[2]);
        return hsl[2] <= maxLightness ? rgb : fromHsl(hsl[0], hsl[1], maxLightness);
    }

    private static double contrastWithWhite(int[] rgb) {
        return 1.05 / (relativeLuminance(rgb) + 0.05);
    }

    private static double relativeLuminance(int[] rgb) {
        double[] c = new double[3];
        for (int i = 0; i < 3; i++) {
            double v = rgb[i] / 255.0;
            c[i] = v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
        }
        return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];
    }

    /** Returns {hue 0-1, saturation 0-1, lightness 0-1}. */
    private static double[] toHsl(int r, int g, int b) {
        double rf = r / 255.0, gf = g / 255.0, bf = b / 255.0;
        double max = Math.max(rf, Math.max(gf, bf));
        double min = Math.min(rf, Math.min(gf, bf));
        double l = (max + min) / 2;
        if (max == min) {
            return new double[]{0, 0, l};
        }
        double d = max - min;
        double s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
        double h;
        if (max == rf) {
            h = (gf - bf) / d + (gf < bf ? 6 : 0);
        } else if (max == gf) {
            h = (bf - rf) / d + 2;
        } else {
            h = (rf - gf) / d + 4;
        }
        return new double[]{h / 6, s, l};
    }

    private static int[] fromHsl(double h, double s, double l) {
        if (s == 0) {
            int v = (int) Math.round(l * 255);
            return new int[]{v, v, v};
        }
        double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        double p = 2 * l - q;
        return new int[]{
                (int) Math.round(hueToRgb(p, q, h + 1.0 / 3) * 255),
                (int) Math.round(hueToRgb(p, q, h) * 255),
                (int) Math.round(hueToRgb(p, q, h - 1.0 / 3) * 255)
        };
    }

    private static double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2) return q;
        if (t < 2.0 / 3) return p + (q - p) * (2.0 / 3 - t) * 6;
        return p;
    }

    private static String toHex(int[] rgb) {
        return String.format("#%02X%02X%02X",
                Math.max(0, Math.min(255, rgb[0])),
                Math.max(0, Math.min(255, rgb[1])),
                Math.max(0, Math.min(255, rgb[2])));
    }
}
