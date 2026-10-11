package com.astromyllc.shootingstar.hr.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a stored staff picture into image bytes (and a small version of it), so a picture can be
 * sent as an image the browser keeps, instead of as text inside every list.
 *
 * Stored shapes understood: {@code name_image/jpeg_<base64>}, {@code data:image/png;base64,<base64>}
 * and bare base64.
 */
public final class StaffPhoto {

    /** A picture ready to send. */
    public record Decoded(String contentType, byte[] bytes) {
    }

    private static final int THUMBNAIL_SIZE = 128;
    private static final int MAX_CACHED = 5000;
    private static final Pattern PREFIXED = Pattern.compile("^.*?_(image/[A-Za-z0-9.+\\-]+)_", Pattern.DOTALL);
    private static final Pattern DATA_URL = Pattern.compile("^data:(image/[A-Za-z0-9.+\\-]+);base64,", Pattern.CASE_INSENSITIVE);
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    // staffCode -> thumbnail of the picture as it was when the thumbnail was made
    private static final Map<String, Cached> THUMBNAILS = new ConcurrentHashMap<>();

    private record Cached(int sourceHash, Decoded thumbnail) {
    }

    private StaffPhoto() {
    }

    /** @return the picture, or null when the stored value is empty or is not an image */
    public static Decoded decode(String stored) {
        if (stored == null) return null;
        String value = stored.trim();
        if (value.length() < 100) return null; // a file name or a stub, not a picture

        String type;
        String base64;
        Matcher dataUrl = DATA_URL.matcher(value);
        Matcher prefixed = PREFIXED.matcher(value);
        if (dataUrl.find()) {
            type = dataUrl.group(1);
            base64 = value.substring(dataUrl.end());
        } else if (prefixed.find()) {
            type = prefixed.group(1);
            base64 = value.substring(prefixed.end());
        } else {
            base64 = value;
            type = value.startsWith("iVBOR") ? "image/png" : "image/jpeg";
        }
        type = type.toLowerCase(Locale.ROOT);
        if (type.equals("image/jpg")) type = "image/jpeg";
        if (!IMAGE_TYPES.contains(type)) type = "image/jpeg";
        try {
            byte[] bytes = Base64.getMimeDecoder().decode(base64);
            return bytes.length == 0 ? null : new Decoded(type, bytes);
        } catch (IllegalArgumentException notBase64) {
            return null;
        }
    }

    /**
     * A small version of the picture. Made once per picture and kept in memory;
     * made again when the picture changes. Falls back to the original if it cannot be read as an image.
     */
    public static Decoded thumbnail(String staffCode, String stored) {
        Decoded original = decode(stored);
        if (original == null) return null;
        int hash = stored.hashCode();
        Cached cached = THUMBNAILS.get(staffCode);
        if (cached != null && cached.sourceHash() == hash) return cached.thumbnail();

        Decoded small = shrink(original);
        if (THUMBNAILS.size() >= MAX_CACHED) THUMBNAILS.clear();
        THUMBNAILS.put(staffCode, new Cached(hash, small));
        return small;
    }

    static Decoded shrink(Decoded original) {
        try {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(original.bytes()));
            if (source == null) return original;
            int w = source.getWidth(), h = source.getHeight();
            double scale = Math.min(1.0, (double) THUMBNAIL_SIZE / Math.max(w, h));
            int tw = Math.max(1, (int) Math.round(w * scale)), th = Math.max(1, (int) Math.round(h * scale));

            // JPEG has no transparency: draw on white
            BufferedImage target = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = target.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, tw, th);
                g.drawImage(source.getScaledInstance(tw, th, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            } finally {
                g.dispose();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            try (MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(out)) {
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.7f);
                writer.setOutput(stream);
                writer.write(null, new IIOImage(target, null, null), param);
            } finally {
                writer.dispose();
            }
            byte[] bytes = out.toByteArray();
            return bytes.length < original.bytes().length ? new Decoded("image/jpeg", bytes) : original;
        } catch (Exception e) {
            return original;
        }
    }
}
