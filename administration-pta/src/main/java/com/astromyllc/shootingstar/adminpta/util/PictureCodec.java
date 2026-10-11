package com.astromyllc.shootingstar.adminpta.util;

import java.util.Base64;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a stored picture into bytes and a content type, so a picture can be sent as an
 * image (cached by the browser) instead of as text inside a list.
 *
 * Pictures are stored in more than one shape; all of these are understood:
 * <pre>
 *   profile.jpg_image/jpeg_&lt;base64&gt;     (what the upload screens save)
 *   data:image/png;base64,&lt;base64&gt;
 *   /9j/4AAQ...  or  iVBOR...                (bare base64)
 * </pre>
 */
public final class PictureCodec {

    /** A picture ready to send. */
    public record Decoded(String contentType, byte[] bytes) {
    }

    private static final Pattern PREFIXED = Pattern.compile("^.*?_(image/[A-Za-z0-9.+\\-]+)_", Pattern.DOTALL);
    private static final Pattern DATA_URL = Pattern.compile("^data:(image/[A-Za-z0-9.+\\-]+);base64,", Pattern.CASE_INSENSITIVE);
    // Only real image types are ever sent back, so a stored value can never make a browser run something.
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private PictureCodec() {
    }

    /** @return the picture, or null when the stored value is empty or is not an image */
    public static Decoded decode(String stored) {
        if (stored == null) return null;
        String value = stored.trim();
        if (value.length() < 100) return null; // a file name or a stub, not a picture

        String type = null;
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
}
