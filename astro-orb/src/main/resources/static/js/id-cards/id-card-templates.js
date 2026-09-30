/*
 * ID card templates.
 *
 * Each template is artwork (a PNG per side) plus the placeholders drawn on it.
 * To add a template: put its front/back PNGs in /img/id-cards/ and add an
 * entry below. The template picker lists every entry automatically.
 *
 * Artwork: export at card proportions (landscape ~1350 x 850 px, portrait
 * ~620 x 1050 px). Make photo and crest windows transparent; the photo/crest
 * placed with layer: "under" shows through them.
 *
 * Positions (x, y, w, h) are percentages of the card's width/height, so the
 * same layout works for the on-screen preview and for print. Text sizes
 * ("size") are in cqw - percent of the card's width.
 *
 * baseColors: the artwork's two brand colours - primary is the darker one.
 * Pixels in these colours (and their shades and tints) are repainted with the
 * school's crest colours; white, black and grey pixels are left alone.
 *
 * Field types
 *   cover       Patch hiding content baked into the artwork. color: a colour
 *               picked from the artwork (it's recoloured like the artwork, so
 *               it blends in), or "primary" / "secondary" for the school colour.
 *   image       source: "crest" | "photo"; fit: "contain" | "cover".
 *               layer: "under" draws it beneath the artwork (transparent windows).
 *   text        bind: see OrbIdCards.valueFor in id-cards.js.
 *               color: hex, or "primary" / "secondary" for the school colour.
 *               wrap: true + lines: n for multi-line text; valign: "top".
 *   paragraphs  Several lines with ring bullets (bind: "terms").
 *   qr          QR code of the student ID. Square: set w only.
 *
 * The student's class is deliberately not printed: a card is kept for the
 * child's whole stay at the school.
 */
(function () {
  "use strict";

  window.OrbIdCards = window.OrbIdCards || {};

  window.OrbIdCards.templates = [
    // ── Template 1: blue, landscape ───────────────────────────────────────
    {
      id: "template-1",
      name: "Template 1 (blue, landscape)",
      orientation: "landscape",
      baseColors: { primary: "#0E4983", secondary: "#2878C7" },

      front: {
        image: "/img/id-cards/template1-front.png",
        fields: [
          // Photo and crest show through the transparent diamond and rounded box.
          { type: "image", source: "photo", layer: "under", x: 53.5, y: 9.8, w: 46.5, h: 85.6, fit: "cover" },
          { type: "image", source: "crest", layer: "under", x: 5.16, y: 13.46, w: 9.24, h: 11.78, fit: "contain" },
          { type: "text", bind: "school.name", x: 15.8, y: 14.9, w: 40.0, h: 9.0, size: 2.2, weight: 600, wrap: true, lines: 2 },

          // After the NAME / ID / D.O.B colons.
          { type: "text", bind: "student.fullName", x: 23.3, y: 41.5, w: 28.0, h: 5.6, size: 2.45, weight: 500 },
          { type: "text", bind: "student.studentId", x: 23.3, y: 48.0, w: 28.0, h: 5.6, size: 2.3, weight: 600, mono: true },
          { type: "text", bind: "student.dateOfBirth", x: 23.3, y: 54.4, w: 28.0, h: 5.6, size: 2.45, weight: 500 },

          { type: "qr", bind: "student.studentId", x: 7.4, y: 70.5, w: 11.5 },
          { type: "text", bind: "student.studentId", x: 20.2, y: 78.0, w: 22.0, h: 5.0, size: 1.9, weight: 500, mono: true, spacing: "0.12em" },
        ],
      },

      back: {
        image: "/img/id-cards/template1-back.png",
        fields: [
          { type: "image", source: "crest", x: 81.57, y: 7.05, w: 12.08, h: 15.37, fit: "contain" },
          { type: "text", bind: "school.name", x: 80.14, y: 24.82, w: 16.46, h: 6.77, size: 1.6, weight: 700, align: "center", color: "#FFFFFF", uppercase: true, wrap: true, lines: 2 },

          // Beside the two ring bullets.
          { type: "text", bind: "terms.1", x: 11.18, y: 32.72, w: 66.64, h: 26.09, size: 1.95, color: "#1F2933", wrap: true, lines: 5, valign: "top" },
          { type: "text", bind: "terms.2", x: 11.18, y: 61.21, w: 72.0, h: 11.28, size: 1.95, color: "#1F2933", wrap: true, lines: 2, valign: "top" },

          // After ADMITTED: and EXPIRES:.
          { type: "text", bind: "student.dateOfAdmission", x: 19.32, y: 73.06, w: 25.0, h: 5.08, size: 2.1, weight: 500 },
          { type: "text", bind: "card.expiry", x: 55.46, y: 72.5, w: 20.0, h: 5.08, size: 2.1, weight: 500 },

          // Beside the phone and globe icons.
          { type: "text", bind: "school.phone", x: 15.21, y: 83.5, w: 17.0, h: 4.8, size: 2.0, weight: 500 },
          { type: "text", bind: "school.website", x: 39.36, y: 83.5, w: 40.0, h: 4.8, size: 2.0, weight: 500 },
        ],
      },
    },

    // ── Template 2: brown, portrait ───────────────────────────────────────
    {
      id: "template-2",
      name: "Template 2 (brown, portrait)",
      orientation: "portrait",
      baseColors: { primary: "#A75E31", secondary: "#DE8751" },

      front: {
        image: "/img/id-cards/template2-front.png",
        fields: [
          // Crest and photo show through the transparent circle and rectangle.
          { type: "image", source: "crest", layer: "under", x: 6.95, y: 5.51, w: 16.96, h: 9.6, fit: "contain" },
          { type: "image", source: "photo", layer: "under", x: 29.08, y: 22.81, w: 42.0, h: 37.45, fit: "cover" },

          { type: "text", bind: "school.name", x: 28.76, y: 5.89, w: 46.85, h: 9.51, size: 3.6, weight: 700, color: "#FFFFFF", uppercase: true, spacing: "0.04em", wrap: true, lines: 2 },
          { type: "qr", bind: "student.studentId", x: 73.02, y: 47.05, w: 20.68 },

          // After the Name / ID / D.O.B colons.
          { type: "text", bind: "student.fullName", x: 32.8, y: 61.31, w: 62.0, h: 3.8, size: 3.7, weight: 500, color: "primary" },
          { type: "text", bind: "student.studentId", x: 32.8, y: 67.4, w: 62.0, h: 3.8, size: 3.5, weight: 600, mono: true, color: "primary" },
          { type: "text", bind: "student.dateOfBirth", x: 32.8, y: 73.86, w: 62.0, h: 3.8, size: 3.7, weight: 500, color: "primary" },
        ],
      },

      back: {
        image: "/img/id-cards/template2-back.png",
        fields: [
          { type: "image", source: "crest", layer: "under", x: 9.53, y: 4.95, w: 18.58, h: 10.67, fit: "contain" },
          { type: "text", bind: "school.name", x: 32.31, y: 5.24, w: 42.81, h: 9.52, size: 3.6, weight: 700, color: "#FFFFFF", uppercase: true, spacing: "0.04em", wrap: true, lines: 2 },

          // Beside the two bullets.
          { type: "text", bind: "terms.1", x: 22.29, y: 33.71, w: 66.24, h: 8.48, size: 2.8, color: "primary", wrap: true, lines: 3, valign: "top" },
          { type: "text", bind: "terms.2", x: 22.29, y: 42.38, w: 66.24, h: 10.38, size: 2.8, color: "primary", wrap: true, lines: 4, valign: "top" },

          // After ADMITTED and EXPIRED.
          { type: "text", bind: "student.dateOfAdmission", x: 47.98, y: 54.24, w: 35.0, h: 3.43, size: 3.6, color: "primary" },
          { type: "text", bind: "card.expiry", x: 47.98, y: 59.19, w: 35.0, h: 3.43, size: 3.6, color: "primary" },

          { type: "text", bind: "school.returnNote", x: 14.86, y: 69.52, w: 76.3, h: 6.67, size: 3.1, color: "primary", wrap: true, lines: 2 },

          // Beside the phone and globe icons.
          { type: "text", bind: "school.phone", x: 25.36, y: 79.19, w: 60.0, h: 3.43, size: 3.3, color: "primary" },
          { type: "text", bind: "school.website", x: 25.36, y: 84.33, w: 60.0, h: 3.43, size: 3.3, color: "primary" },
        ],
      },
    },

    // ── Template 3: puzzle, landscape ─────────────────────────────────────
    {
      id: "template-3",
      name: "Template 3 (puzzle, landscape)",
      orientation: "landscape",
      // Blue is the darker brand colour; the red and yellow pieces both take the school's secondary.
      baseColors: { primary: "#004AAD", secondary: "#E44C3F" },

      front: {
        image: "/img/id-cards/template3-front.png",
        fields: [
          // Crest and photo show through the transparent square windows.
          { type: "image", source: "crest", layer: "under", x: 6.68, y: 13.31, w: 6.24, h: 7.3, fit: "contain" },
          { type: "image", source: "photo", layer: "under", x: 61.32, y: 29.21, w: 26.13, h: 41.34, fit: "cover" },

          { type: "text", bind: "school.name", x: 14.1, y: 13.9, w: 29.3, h: 6.4, size: 1.9, weight: 600, color: "#1F2933", wrap: true, lines: 2 },

          // After the Name / ID / D.O.B colons.
          { type: "text", bind: "student.fullName", x: 23.39, y: 56.95, w: 31.5, h: 4.71, size: 2.5, weight: 500, color: "#1F2933" },
          { type: "text", bind: "student.studentId", x: 23.39, y: 64.31, w: 31.5, h: 4.71, size: 2.3, weight: 600, mono: true, color: "#1F2933" },
          { type: "text", bind: "student.dateOfBirth", x: 23.39, y: 71.61, w: 31.5, h: 4.71, size: 2.5, weight: 500, color: "#1F2933" },

          { type: "qr", bind: "student.studentId", x: 8.46, y: 80.0, w: 9.65 },
          { type: "text", bind: "student.studentId", x: 19.3, y: 86.0, w: 25.0, h: 4.0, size: 1.6, weight: 500, mono: true, spacing: "0.12em", color: "#1F2933" },
        ],
      },

      back: {
        image: "/img/id-cards/template3-back.png",
        fields: [
          // Beside the two bullets.
          { type: "text", bind: "terms.1", x: 11.09, y: 47.9, w: 81.4, h: 9.6, size: 2.0, color: "#1F2933", wrap: true, lines: 2, valign: "top" },
          { type: "text", bind: "terms.2", x: 11.09, y: 58.2, w: 81.4, h: 10.6, size: 2.0, color: "#1F2933", wrap: true, lines: 2, valign: "top" },

          // Beside the phone and globe icons.
          { type: "text", bind: "school.phone", x: 14.2, y: 80.59, w: 39.0, h: 4.3, size: 2.1, weight: 500, color: "#1F2933" },
          { type: "text", bind: "school.website", x: 60.21, y: 80.59, w: 35.0, h: 4.3, size: 2.1, weight: 500, color: "#1F2933" },

          // After Admitted: and Expires:.
          { type: "text", bind: "student.dateOfAdmission", x: 20.56, y: 89.35, w: 25.0, h: 4.3, size: 2.1, weight: 500, color: "#1F2933" },
          { type: "text", bind: "card.expiry", x: 64.72, y: 88.7, w: 25.0, h: 4.3, size: 2.1, weight: 500, color: "#1F2933" },
        ],
      },
    },

    // ── Template 4: purple wave, landscape ────────────────────────────────
    {
      id: "template-4",
      name: "Template 4 (wave, landscape)",
      orientation: "landscape",
      // The lavender shapes are light tints, so they become pale tints of the school's secondary.
      baseColors: { primary: "#1800AC", secondary: "#D1CBF7" },

      front: {
        image: "/img/id-cards/template4-front.png",
        fields: [
          // Photo and crest show through the transparent frame and circle.
          { type: "image", source: "photo", layer: "under", x: 7.21, y: 23.36, w: 28.23, h: 55.4, fit: "cover" },
          { type: "image", source: "crest", layer: "under", x: 38.78, y: 27.23, w: 7.73, h: 11.38, fit: "contain" },

          { type: "text", bind: "school.name", x: 48.66, y: 28.76, w: 37.15, h: 10.56, size: 2.4, weight: 700, color: "primary", wrap: true, lines: 2 },
          { type: "qr", bind: "student.studentId", x: 87.67, y: 29.34, w: 9.66 },

          // After Name: / Student ID: / Date of Birth:.
          { type: "text", bind: "student.fullName", x: 57.21, y: 48.18, w: 40.8, h: 4.69, size: 2.5, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "student.studentId", x: 57.21, y: 55.11, w: 40.8, h: 4.69, size: 2.3, weight: 600, mono: true, color: "#1A1A1A" },
          { type: "text", bind: "student.dateOfBirth", x: 57.21, y: 62.32, w: 40.8, h: 4.69, size: 2.5, weight: 500, color: "#1A1A1A" },
        ],
      },

      back: {
        image: "/img/id-cards/template4-back.png",
        fields: [
          { type: "image", source: "crest", layer: "under", x: 7.49, y: 8.64, w: 7.79, h: 11.8, fit: "contain" },
          { type: "text", bind: "school.name", x: 17.8, y: 9.93, w: 21.5, h: 8.76, size: 2.0, weight: 700, color: "#FFFFFF", uppercase: true, wrap: true, lines: 2 },

          // Inside the four numbered "Access Privileges" pills.
          { type: "text", bind: "privileges.1", x: 14.1, y: 49.88, w: 25.96, h: 10.75, size: 2.0, weight: 500, color: "primary", wrap: true, lines: 2 },
          { type: "text", bind: "privileges.2", x: 14.1, y: 62.38, w: 25.96, h: 10.75, size: 2.0, weight: 500, color: "primary", wrap: true, lines: 2 },
          { type: "text", bind: "privileges.3", x: 51.19, y: 49.77, w: 25.96, h: 10.75, size: 2.0, weight: 500, color: "primary", wrap: true, lines: 2 },
          { type: "text", bind: "privileges.4", x: 51.19, y: 62.27, w: 25.96, h: 10.75, size: 2.0, weight: 500, color: "primary", wrap: true, lines: 2 },

          // After Admitted: and Expires:.
          { type: "text", bind: "student.dateOfAdmission", x: 22.63, y: 77.8, w: 25.0, h: 4.44, size: 2.1, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "card.expiry", x: 20.92, y: 91.94, w: 25.0, h: 4.44, size: 2.1, weight: 500, color: "#1A1A1A" },
        ],
      },
    },

    // ── Template 5: green ribbon, landscape ───────────────────────────────
    {
      id: "template-5",
      name: "Template 5 (ribbon, landscape)",
      orientation: "landscape",
      baseColors: { primary: "#336667", secondary: "#33CB98" },

      front: {
        image: "/img/id-cards/template5-front.png",
        fields: [
          // Crest and photo show through the transparent circle and rounded frame.
          { type: "image", source: "crest", layer: "under", x: 8.02, y: 5.88, w: 4.75, h: 8.0, fit: "contain" },
          { type: "image", source: "photo", layer: "under", x: 7.94, y: 24.12, w: 25.84, h: 55.06, fit: "cover" },

          { type: "text", bind: "school.name", x: 14.48, y: 6.47, w: 39.7, h: 7.06, size: 1.9, weight: 700, color: "#FFFFFF", uppercase: true, wrap: true, lines: 2 },

          // The artwork has an extra colon between the ID and date-of-birth rows.
          { type: "cover", color: "#F6F5F3", x: 60.0, y: 52.4, w: 1.6, h: 3.1 },

          // After the Name / Student ID / Date of Birth colons.
          { type: "text", bind: "student.fullName", x: 62.73, y: 41.0, w: 27.5, h: 4.71, size: 2.3, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "student.studentId", x: 62.73, y: 47.59, w: 27.5, h: 4.71, size: 2.1, weight: 600, mono: true, color: "#1A1A1A" },
          { type: "text", bind: "student.dateOfBirth", x: 62.73, y: 55.71, w: 27.5, h: 4.71, size: 2.3, weight: 500, color: "#1A1A1A" },

          { type: "qr", bind: "student.studentId", x: 40.46, y: 64.47, w: 10.39 },
          { type: "text", bind: "student.studentId", x: 51.97, y: 70.59, w: 22.0, h: 4.7, size: 1.6, weight: 500, mono: true, spacing: "0.12em", color: "#1A1A1A" },
        ],
      },

      back: {
        image: "/img/id-cards/template5-back.png",
        fields: [
          { type: "image", source: "crest", layer: "under", x: 7.6, y: 6.57, w: 4.91, h: 6.92, fit: "contain" },
          { type: "text", bind: "school.name", x: 14.15, y: 6.46, w: 40.2, h: 7.04, size: 1.9, weight: 700, color: "#FFFFFF", uppercase: true, wrap: true, lines: 2 },

          // The body is open space: heading, terms and the "If found" note.
          { type: "text", bind: "label.terms", x: 8.19, y: 21.7, w: 60.0, h: 5.9, size: 2.6, weight: 700, color: "primary", uppercase: true },
          { type: "paragraphs", bind: "terms", x: 8.19, y: 29.34, w: 81.9, h: 27.0, size: 2.1, color: "#1F2933" },
          { type: "text", bind: "school.returnNote", x: 8.19, y: 58.69, w: 81.9, h: 7.04, size: 2.1, color: "#1F2933", wrap: true, lines: 2 },

          // Beside the phone, globe and location icons.
          { type: "text", bind: "school.phone", x: 14.52, y: 70.42, w: 18.2, h: 4.2, size: 1.7, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "school.website", x: 38.35, y: 70.42, w: 23.4, h: 4.2, size: 1.7, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "school.address", x: 67.01, y: 70.42, w: 29.8, h: 4.2, size: 1.7, weight: 500, color: "#1A1A1A" },

          // After Admitted: and Expires:.
          { type: "text", bind: "student.dateOfAdmission", x: 27.92, y: 81.22, w: 30.0, h: 4.4, size: 2.1, weight: 500, color: "#1A1A1A" },
          { type: "text", bind: "card.expiry", x: 69.84, y: 81.92, w: 25.0, h: 4.4, size: 2.1, weight: 500, color: "#1A1A1A" },
        ],
      },
    },
  ];
})();
