/*
 * Administration -> ID Cards
 *
 * Usage (see templates/fragments/id-cards.html):
 *   OrbIdCards.mount(document.getElementById("idCardsApp"));
 *
 * Requires id-card-templates.js and the qrcode-generator library (global `qrcode`).
 *
 * The school, its details and its crest colours all come from the server,
 * resolved from the signed-in user's account - nothing on this page chooses
 * the school or the colours.
 */
(function () {
    "use strict";

    var OrbIdCards = (window.OrbIdCards = window.OrbIdCards || {});

    var API = {
        context: "/idcards/context",
        students: "/idcards/students",
        csrf: "/csrf-token",
    };

    // Card sizes (ISO/IEC 7810 ID-1) and A4 sheet layouts.
    var CARD_MM = {long: 85.6, short: 54};
    var SHEET_LAYOUT = {
        landscape: {cols: 2, rows: 5}, // 10 cards per A4 page
        portrait: {cols: 3, rows: 3},  // 9 cards per A4 page
    };

    var SAMPLE_STUDENT = {
        studentId: "0000000000",
        fullName: "Student name",
        studentClass: "Class",
        dateOfBirth: null,
        picture: null,
    };

    // ── Small helpers ────────────────────────────────────────────────────

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) node.className = className;
        if (text != null) node.textContent = text;
        return node;
    }

    function clamp(v, min, max) {
        return Math.min(max, Math.max(min, v));
    }

    function formatDate(value) {
        if (!value) return "DD/MM/YYYY";
        var y, m, d;
        if (Array.isArray(value)) {
            y = value[0];
            m = value[1];
            d = value[2];
        } else {
            var parts = String(value).slice(0, 10).split("-");
            if (parts.length !== 3) return String(value);
            y = parts[0];
            m = parts[1];
            d = parts[2];
        }
        return String(d).padStart(2, "0") + "/" + String(m).padStart(2, "0") + "/" + y;
    }

    /**
     * Stored images come in several formats; all are turned into a usable src:
     *   "data:image/...;base64,..."           data URL (used as is)
     *   "photo.jpg_image/jpeg_<base64>"       saved by Student Record when a picture is updated
     *   "/9j/..." or "iVBOR..."               raw base64 (JPEG / PNG), e.g. from enrolment
     *   "https://..." or "/path"              URL
     */
    function imageSrc(value) {
        if (!value) return null;
        value = String(value).trim();
        if (/^(data:|https?:\/\/)/.test(value)) return value;

        // "filename_image/jpeg_<base64>" - base64 never contains "_", so the data
        // starts right after the "_image/<type>_" marker.
        var prefixed = value.match(/_(image\/[a-z0-9.+-]+)_/i);
        if (prefixed) {
            return "data:" + prefixed[1] + ";base64," + value.slice(prefixed.index + prefixed[0].length);
        }

        // Base64 JPEGs start with "/9j/", so only treat other leading "/" values as URL paths.
        if (value.charAt(0) === "/" && value.indexOf("/9j/") !== 0) return value;
        var type = value.indexOf("iVBOR") === 0 ? "image/png" : "image/jpeg";
        return "data:" + type + ";base64," + value;
    }

    // ── Server calls ─────────────────────────────────────────────────────

    var csrfPromise = null;

    /** CSRF token: the page's meta tags if present, else the /csrf-token endpoint, else the cookie. */
    function getCsrf() {
        if (csrfPromise) return csrfPromise;
        var meta = document.querySelector('meta[name="_csrf"]');
        var headerMeta = document.querySelector('meta[name="_csrf_header"]');
        if (meta && meta.content) {
            csrfPromise = Promise.resolve({
                header: (headerMeta && headerMeta.content) || "X-XSRF-TOKEN",
                token: meta.content,
            });
            return csrfPromise;
        }
        csrfPromise = fetch(API.csrf, {credentials: "same-origin"})
            .then(function (res) {
                return res.ok ? res.json() : {};
            })
            .then(function (body) {
                return body.csrfToken;
            })
            .catch(function () {
                return null;
            })
            .then(function (token) {
                if (!token) {
                    var match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]+)/);
                    token = match ? decodeURIComponent(match[1]) : "";
                }
                return {header: "X-XSRF-TOKEN", token: token};
            });
        return csrfPromise;
    }

    function postJson(url, body) {
        return getCsrf().then(function (csrf) {
            var headers = {"Content-Type": "application/json", Accept: "application/json"};
            if (csrf.token) headers[csrf.header] = csrf.token;
            return fetch(url, {
                method: "POST",
                credentials: "same-origin",
                headers: headers,
                body: JSON.stringify(body || {}),
            });
        }).then(function (res) {
            return res.json().catch(function () {
                return {};
            }).then(function (data) {
                if (!res.ok) {
                    var error = new Error(data.message || "Something went wrong (" + res.status + "). Try again.");
                    error.status = res.status;
                    throw error;
                }
                return data;
            });
        });
    }

    // ── Colour: recolour template artwork with the school's crest colours ─

    function hexToRgb(hex) {
        var h = hex.replace("#", "");
        return [parseInt(h.slice(0, 2), 16), parseInt(h.slice(2, 4), 16), parseInt(h.slice(4, 6), 16)];
    }

    function rgbToHsl(r, g, b) {
        r /= 255;
        g /= 255;
        b /= 255;
        var max = Math.max(r, g, b), min = Math.min(r, g, b);
        var l = (max + min) / 2, h = 0, s = 0;
        if (max !== min) {
            var d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max === r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max === g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;
            h /= 6;
        }
        return [h, s, l];
    }

    function hueToRgb(p, q, t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1 / 6) return p + (q - p) * 6 * t;
        if (t < 1 / 2) return q;
        if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
        return p;
    }

    function hslToRgb(h, s, l) {
        if (s === 0) return [l * 255, l * 255, l * 255];
        var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        var p = 2 * l - q;
        return [hueToRgb(p, q, h + 1 / 3) * 255, hueToRgb(p, q, h) * 255, hueToRgb(p, q, h - 1 / 3) * 255];
    }

    function smoothstep(edge0, edge1, x) {
        var t = clamp((x - edge0) / (edge1 - edge0), 0, 1);
        return t * t * (3 - 2 * t);
    }

    var artCache = {};

    /**
     * Builds the colour mapping from the artwork's brand colours to the school's.
     * Each coloured pixel takes the hue of whichever school colour matches its
     * side of the artwork (darker shades -> primary, lighter -> secondary) and
     * keeps its own shading relative to that colour. Hues are never blended, so
     * e.g. navy and gold don't produce green in between. White, black and grey
     * pixels are untouched, so text and outlines stay as drawn.
     */
    function colourMapper(baseColors, palette) {
        var bp = rgbToHsl.apply(null, hexToRgb(baseColors.primary));
        var bs = rgbToHsl.apply(null, hexToRgb(baseColors.secondary));
        var tp = rgbToHsl.apply(null, hexToRgb(palette.primary));
        var ts = rgbToHsl.apply(null, hexToRgb(palette.secondary));
        var midpoint = (bp[2] + bs[2]) / 2;

        /** Returns the recoloured [r, g, b], or null when the pixel stays as it is. */
        return function (r, g, b) {
            var hsl = rgbToHsl(r, g, b);
            var amount = smoothstep(0.15, 0.35, hsl[1]);
            if (amount === 0) return null;
            var usePrimary = hsl[2] < midpoint;
            var base = usePrimary ? bp : bs;
            var target = usePrimary ? tp : ts;
            var l = clamp(target[2] + (hsl[2] - base[2]), 0, 1);
            var s = target[1];
            // Very light tints (card backgrounds, boxes) keep the design's lightness
            // and stay pastel, so a bright school colour like yellow doesn't flood the card.
            if (hsl[2] > 0.85) {
                l = hsl[2];
                s = target[1] * 0.5;
            }
            var rgb = hslToRgb(target[0], s, l);
            return [r + (rgb[0] - r) * amount, g + (rgb[1] - g) * amount, b + (rgb[2] - b) * amount];
        };
    }

    /** A colour from the artwork, as it looks after recolouring - used for cover patches. */
    function recolouredHex(hex, baseColors, palette) {
        var rgb = hexToRgb(hex);
        var mapped = colourMapper(baseColors, palette)(rgb[0], rgb[1], rgb[2]) || rgb;
        return "#" + mapped.map(function (v) {
            return Math.round(clamp(v, 0, 255)).toString(16).padStart(2, "0");
        }).join("");
    }

    /** The artwork repainted in the school's colours (transparent photo/crest windows are kept). */
    function recolouredArt(src, baseColors, palette) {
        var key = src + "|" + palette.primary + "|" + palette.secondary;
        if (artCache[key]) return artCache[key];

        artCache[key] = new Promise(function (resolve) {
            var img = new Image();
            img.onload = function () {
                try {
                    var canvas = document.createElement("canvas");
                    canvas.width = img.naturalWidth;
                    canvas.height = img.naturalHeight;
                    var ctx = canvas.getContext("2d");
                    ctx.drawImage(img, 0, 0);
                    var pixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
                    var d = pixels.data;
                    var map = colourMapper(baseColors, palette);

                    for (var i = 0; i < d.length; i += 4) {
                        if (d[i + 3] === 0) continue;
                        var rgb = map(d[i], d[i + 1], d[i + 2]);
                        if (!rgb) continue;
                        d[i] = rgb[0];
                        d[i + 1] = rgb[1];
                        d[i + 2] = rgb[2];
                    }
                    ctx.putImageData(pixels, 0, 0);
                    resolve(canvas.toDataURL("image/png"));
                } catch (e) {
                    console.warn("ID cards: couldn't recolour " + src + ", using original artwork.", e);
                    resolve(src);
                }
            };
            img.onerror = function () {
                console.warn("ID cards: artwork not found at " + src);
                resolve(src);
            };
            img.src = src;
        });
        return artCache[key];
    }

    // ── QR ────────────────────────────────────────────────────────────────

    function qrSvg(text) {
        if (typeof window.qrcode !== "function" || !text) return null;
        var qr = window.qrcode(0, "M");
        qr.addData(String(text));
        qr.make();
        var n = qr.getModuleCount();
        var path = "";
        for (var row = 0; row < n; row++) {
            for (var col = 0; col < n; col++) {
                if (qr.isDark(row, col)) path += "M" + col + " " + row + "h1v1h-1z";
            }
        }
        var ns = "http://www.w3.org/2000/svg";
        var svg = document.createElementNS(ns, "svg");
        svg.setAttribute("viewBox", "-1 -1 " + (n + 2) + " " + (n + 2));
        svg.setAttribute("shape-rendering", "crispEdges");
        svg.setAttribute("role", "img");
        svg.setAttribute("aria-label", "QR code for student " + text);
        var bg = document.createElementNS(ns, "rect");
        bg.setAttribute("x", "-1");
        bg.setAttribute("y", "-1");
        bg.setAttribute("width", String(n + 2));
        bg.setAttribute("height", String(n + 2));
        bg.setAttribute("fill", "#FFFFFF");
        var p = document.createElementNS(ns, "path");
        p.setAttribute("d", path);
        p.setAttribute("fill", "#000000");
        svg.appendChild(bg);
        svg.appendChild(p);
        return svg;
    }

    // ── Card rendering ───────────────────────────────────────────────────

    var DEFAULT_TERMS = [
        "Carry this card at all times on campus and during school activities, and show it when asked by school staff.",
        "This card belongs to {school}. It must not be lent, altered or duplicated. Report a lost card to the school office immediately.",
    ];

    // Shown on templates with numbered "Access Privileges" slots (privileges.1-4).
    var DEFAULT_PRIVILEGES = [
        "Campus and classroom access",
        "Library and reading room",
        "School events and activities",
        "Sports and recreation facilities",
    ];

    // Cards are valid for this many months from the day they're printed.
    var CARD_VALID_MONTHS = 12;

    function expiryDate() {
        var d = new Date();
        d.setMonth(d.getMonth() + CARD_VALID_MONTHS);
        return [d.getFullYear(), d.getMonth() + 1, d.getDate()];
    }

    /** Maps a template field's `bind` to a value. */
    OrbIdCards.valueFor = function (bind, ctx) {
        var school = ctx.school || {};
        var student = ctx.student || {};
        var schoolName = school.name || "the school";
        var terms = DEFAULT_TERMS.map(function (line) {
            return line.replace("{school}", schoolName);
        });
        switch (bind) {
            case "school.name":
                return school.name || "School name";
            case "school.phone":
                return school.contact1 || school.contact2 || "";
            case "school.website":
                return school.website || school.email || "";
            case "school.address":
                return [school.postalAddress, school.city].filter(Boolean).join(", ");
            case "school.returnNote":
                return "If found, please return to the " + schoolName + " main office.";
            case "student.fullName":
                return student.fullName || "";
            case "student.studentId":
                return student.studentId || "";
            case "student.studentClass":
                return student.studentClass || "";
            case "student.dateOfBirth":
                return formatDate(student.dateOfBirth);
            case "student.dateOfAdmission":
                return formatDate(student.dateOfAdmission);
            case "card.expiry":
                return formatDate(expiryDate());
            case "terms":
                return terms;
            case "terms.1":
                return terms[0];
            case "terms.2":
                return terms[1];
            case "label.terms":
                return "Terms & conditions";
            case "privileges.1":
                return DEFAULT_PRIVILEGES[0];
            case "privileges.2":
                return DEFAULT_PRIVILEGES[1];
            case "privileges.3":
                return DEFAULT_PRIVILEGES[2];
            case "privileges.4":
                return DEFAULT_PRIVILEGES[3];
            default:
                return "";
        }
    };

    /** "primary" / "secondary" mean the school's colours; any other value is used as given. */
    function schoolColour(value, ctx) {
        return value === "primary" || value === "secondary" ? ctx.palette[value] : value;
    }

    function positionField(node, f) {
        node.style.left = f.x + "%";
        node.style.top = f.y + "%";
        node.style.width = f.w + "%";
        if (f.h != null) node.style.height = f.h + "%";
    }

    function missingImage(label) {
        var box = el("div", "idc-missing");
        box.setAttribute("aria-label", label);
        return box;
    }

    function renderField(f, ctx) {
        var node = el("div", "idc-field idc-field--" + f.type);
        positionField(node, f);

        if (f.type === "cover") {
            // A colour taken from the artwork is recoloured like the artwork, so the patch blends in.
            node.style.background = f.color === "primary" || f.color === "secondary"
                ? ctx.palette[f.color]
                : recolouredHex(f.color, ctx.baseColors, ctx.palette);
            return node;
        }

        if (f.type === "image") {
            var src = imageSrc(f.source === "crest" ? ctx.school.crest : ctx.student.picture);
            if (src) {
                var img = el("img");
                img.src = src;
                img.alt = f.source === "crest" ? "School crest" : "Student photo";
                img.style.objectFit = f.fit || "cover";
                img.style.objectPosition = f.source === "crest" ? "center" : "center 30%";
                node.appendChild(img);
            } else {
                node.appendChild(missingImage(f.source === "crest" ? "No crest uploaded" : "No photo"));
            }
            return node;
        }

        if (f.type === "qr") {
            var svg = qrSvg(OrbIdCards.valueFor(f.bind, ctx));
            node.appendChild(svg || missingImage("QR code unavailable"));
            return node;
        }

        if (f.type === "paragraphs") {
            node.style.fontSize = f.size + "cqw";
            node.style.setProperty("--idc-bullet", ctx.palette.primary);
            if (f.color) node.style.color = schoolColour(f.color, ctx);
            OrbIdCards.valueFor(f.bind, ctx).forEach(function (line) {
                node.appendChild(el("p", null, line));
            });
            return node;
        }

        // text
        node.style.fontSize = f.size + "cqw";
        if (f.weight) node.style.fontWeight = f.weight;
        if (f.color) node.style.color = schoolColour(f.color, ctx);
        if (f.spacing) node.style.letterSpacing = f.spacing;
        if (f.align) node.dataset.align = f.align;
        if (f.valign) node.dataset.valign = f.valign;
        if (f.mono) node.classList.add("idc-field--mono");
        if (f.wrap) {
            node.classList.add("idc-field--wrap");
            node.style.setProperty("--idc-lines", String(f.lines || 2));
        }
        if (f.uppercase) node.style.textTransform = "uppercase";
        node.appendChild(el("span", null, OrbIdCards.valueFor(f.bind, ctx)));
        return node;
    }

    function renderCard(template, sideKey, ctx, artUrl) {
        var side = template[sideKey];
        var card = el("div", "idc-card");
        card.dataset.orientation = template.orientation;
        card.setAttribute("role", "img");
        card.setAttribute("aria-label",
            (sideKey === "front" ? "Front" : "Back") + " of ID card" +
            (ctx.student && ctx.student.fullName ? " for " + ctx.student.fullName : ""));

        side.fields.filter(function (f) {
            return f.layer === "under";
        })
            .forEach(function (f) {
                card.appendChild(renderField(f, ctx));
            });

        var art = el("img", "idc-card__art");
        art.src = artUrl;
        art.alt = "";
        card.appendChild(art);

        side.fields.filter(function (f) {
            return f.layer !== "under";
        })
            .forEach(function (f) {
                card.appendChild(renderField(f, ctx));
            });

        return card;
    }

    // ── Page ─────────────────────────────────────────────────────────────

    var SKELETON =
        '<div class="idc-layout">' +
        '  <aside class="idc-panel">' +
        '    <div class="idc-school">' +
        '      <div class="idc-school__crest" data-ref="crest"></div>' +
        '      <div>' +
        '        <h2 class="idc-school__name" data-ref="schoolName">Loading your school…</h2>' +
        '        <div class="idc-palette" data-ref="palette" hidden>' +
        '          <span class="idc-palette__swatch" data-ref="swatchPrimary"></span>' +
        '          <span class="idc-palette__swatch" data-ref="swatchSecondary"></span>' +
        '          <span class="idc-palette__note" data-ref="paletteNote"></span>' +
        '        </div>' +
        '      </div>' +
        '    </div>' +
        '    <fieldset class="idc-group">' +
        '      <legend>Template</legend>' +
        '      <div class="idc-options" data-ref="templates"></div>' +
        '    </fieldset>' +
        '    <fieldset class="idc-group">' +
        '      <legend>Classes</legend>' +
        '      <label class="idc-check idc-check--all"><input type="checkbox" data-ref="allClasses"> All classes</label>' +
        '      <div class="idc-options idc-options--scroll" data-ref="classes"></div>' +
        '    </fieldset>' +
        '    <button type="button" class="idc-button" data-ref="load" disabled>Load students</button>' +
        '    <fieldset class="idc-group">' +
        '      <legend>Print on</legend>' +
        '      <label class="idc-check"><input type="radio" name="idc-mode" value="sheet" checked> A4 paper, cut by hand</label>' +
        '      <label class="idc-check"><input type="radio" name="idc-mode" value="printer"> Card printer</label>' +
        '    </fieldset>' +
        '    <fieldset class="idc-group">' +
        '      <legend>Sides</legend>' +
        '      <label class="idc-check"><input type="radio" name="idc-sides" value="both" checked> Front and back</label>' +
        '      <label class="idc-check"><input type="radio" name="idc-sides" value="front"> Front only</label>' +
        '    </fieldset>' +
        '    <button type="button" class="idc-button idc-button--primary" data-ref="print" disabled>Print cards</button>' +
        '    <p class="idc-status" role="status" aria-live="polite" data-ref="status"></p>' +
        '  </aside>' +
        '  <section class="idc-preview" aria-labelledby="idc-preview-title">' +
        '    <h2 class="idc-preview__title" id="idc-preview-title" data-ref="previewTitle">Preview</h2>' +
        '    <div class="idc-preview__grid" data-ref="preview"></div>' +
        '  </section>' +
        '</div>';

    OrbIdCards.mount = function (root) {
        if (!root) return;
        if (!OrbIdCards.templates || !OrbIdCards.templates.length) {
            root.textContent = "No ID card templates are installed.";
            return;
        }

        root.classList.add("idc-app");
        root.innerHTML = SKELETON;
        var refs = {};
        root.querySelectorAll("[data-ref]").forEach(function (node) {
            refs[node.dataset.ref] = node;
        });

        var state = {
            school: null,
            palette: null,
            template: OrbIdCards.templates[0],
            students: [],
            loadedClasses: [],
        };

        function setStatus(message, isError) {
            refs.status.textContent = message || "";
            refs.status.classList.toggle("idc-status--error", !!isError);
        }

        function selectedClasses() {
            return Array.prototype.map.call(
                refs.classes.querySelectorAll("input:checked"),
                function (input) {
                    return input.value;
                }
            );
        }

        function contextFor(student) {
            return {
                school: state.school,
                palette: state.palette,
                baseColors: state.template.baseColors,
                student: student
            };
        }

        function artFor(sideKey) {
            return recolouredArt(state.template[sideKey].image, state.template.baseColors, state.palette);
        }

        function renderPreview() {
            if (!state.school) return;
            var students = state.students.length ? state.students : [SAMPLE_STUDENT];
            refs.previewTitle.textContent = state.students.length
                ? state.students.length + (state.students.length === 1 ? " card" : " cards")
                : "Preview with sample details";

            Promise.all([artFor("front"), artFor("back")]).then(function (art) {
                refs.preview.textContent = "";
                refs.preview.dataset.orientation = state.template.orientation;
                students.forEach(function (student) {
                    var pair = el("div", "idc-pair");
                    pair.appendChild(renderCard(state.template, "front", contextFor(student), art[0]));
                    pair.appendChild(renderCard(state.template, "back", contextFor(student), art[1]));
                    refs.preview.appendChild(pair);
                });
            });
        }

        function renderTemplates() {
            OrbIdCards.templates.forEach(function (template, index) {
                var label = el("label", "idc-check");
                var input = el("input");
                input.type = "radio";
                input.name = "idc-template";
                input.value = template.id;
                input.checked = index === 0;
                input.addEventListener("change", function () {
                    state.template = template;
                    renderPreview();
                });
                label.appendChild(input);
                label.appendChild(document.createTextNode(" " + template.name));
                refs.templates.appendChild(label);
            });
        }

        function renderClasses(classes) {
            refs.classes.textContent = "";
            if (!classes.length) {
                refs.classes.appendChild(el("p", "idc-empty", "No classes are set up for your school yet. Add them under Setup."));
                refs.allClasses.disabled = true;
                return;
            }
            classes.forEach(function (name) {
                var label = el("label", "idc-check");
                var input = el("input");
                input.type = "checkbox";
                input.value = name;
                input.addEventListener("change", syncClassControls);
                label.appendChild(input);
                label.appendChild(document.createTextNode(" " + name));
                refs.classes.appendChild(label);
            });
        }

        function syncClassControls() {
            var boxes = refs.classes.querySelectorAll("input");
            var checked = refs.classes.querySelectorAll("input:checked").length;
            refs.allClasses.checked = boxes.length > 0 && checked === boxes.length;
            refs.allClasses.indeterminate = checked > 0 && checked < boxes.length;
            refs.load.disabled = checked === 0;
        }

        refs.allClasses.addEventListener("change", function () {
            refs.classes.querySelectorAll("input").forEach(function (input) {
                input.checked = refs.allClasses.checked;
            });
            syncClassControls();
        });

        refs.load.addEventListener("click", function () {
            var classes = selectedClasses();
            refs.load.disabled = true;
            refs.print.disabled = true;
            setStatus("Loading students…");
            postJson(API.students, {classNames: classes})
                .then(function (data) {
                    state.students = data.students || [];
                    state.loadedClasses = classes;
                    if (!state.students.length) {
                        setStatus("No current students in the selected classes.");
                    } else {
                        var noPhoto = state.students.filter(function (s) {
                            return !s.picture;
                        }).length;
                        setStatus(noPhoto
                            ? noPhoto + " of " + state.students.length + " students have no photo. Their cards show a blank photo area."
                            : "");
                    }
                    refs.print.disabled = state.students.length === 0;
                    renderPreview();
                })
                .catch(function (error) {
                    setStatus(error.message, true);
                })
                .then(function () {
                    refs.load.disabled = selectedClasses().length === 0;
                });
        });

        refs.print.addEventListener("click", function () {
            var mode = root.querySelector('input[name="idc-mode"]:checked').value;
            var sides = root.querySelector('input[name="idc-sides"]:checked').value;
            refs.print.disabled = true;
            setStatus("Preparing " + state.students.length + " cards for printing…");
            Promise.all([artFor("front"), artFor("back")])
                .then(function (art) {
                    return printCards(state, art, mode, sides);
                })
                .then(function () {
                    setStatus("");
                })
                .catch(function (error) {
                    setStatus(error.message || "Couldn't prepare the cards for printing.", true);
                })
                .then(function () {
                    refs.print.disabled = state.students.length === 0;
                });
        });

        renderTemplates();

        postJson(API.context, {})
            .then(function (data) {
                state.school = data.institution || {};
                state.palette = data.palette;
                root.style.setProperty("--idc-accent", state.palette.primary);

                refs.schoolName.textContent = state.school.name || "Your school";
                var crest = imageSrc(state.school.crest);
                if (crest) {
                    var img = el("img");
                    img.src = crest;
                    img.alt = "";
                    refs.crest.appendChild(img);
                }
                refs.swatchPrimary.style.background = state.palette.primary;
                refs.swatchSecondary.style.background = state.palette.secondary;
                refs.paletteNote.textContent = state.palette.source === "crest"
                    ? "Card colours from your crest"
                    : "Upload a coloured crest under Setup to use your school colours";
                refs.palette.hidden = false;

                renderClasses(data.classes || []);
                syncClassControls();
                renderPreview();
            })
            .catch(function (error) {
                refs.schoolName.textContent = "ID cards";
                setStatus(error.message, true);
            });
    };

    // ── Printing ─────────────────────────────────────────────────────────

    function cardSizeMm(orientation) {
        return orientation === "portrait"
            ? {w: CARD_MM.short, h: CARD_MM.long}
            : {w: CARD_MM.long, h: CARD_MM.short};
    }

    function waitForImages(root) {
        var images = Array.prototype.slice.call(root.querySelectorAll("img"));
        return Promise.all(images.map(function (img) {
            if (img.complete) return Promise.resolve();
            return new Promise(function (resolve) {
                img.addEventListener("load", resolve, {once: true});
                img.addEventListener("error", resolve, {once: true});
            });
        }));
    }

    function chunk(list, size) {
        var out = [];
        for (var i = 0; i < list.length; i += size) out.push(list.slice(i, i + size));
        return out;
    }

    /**
     * One A4 page. Back pages mirror each row left-to-right, so with
     * double-sided printing (flip on long edge) every back lands behind its front.
     */
    function sheetPage(cards, layout, mirrored) {
        var page = el("div", "idc-sheet");
        page.dataset.cols = String(layout.cols);
        var perPage = layout.cols * layout.rows;
        var cells = [];
        for (var i = 0; i < perPage; i++) cells.push(cards[i] || el("div", "idc-sheet__empty"));
        for (var row = 0; row < layout.rows; row++) {
            var rowCells = cells.slice(row * layout.cols, (row + 1) * layout.cols);
            if (mirrored) rowCells.reverse();
            rowCells.forEach(function (cell) {
                page.appendChild(cell);
            });
        }
        return page;
    }

    function printCards(state, art, mode, sides) {
        var template = state.template;
        var size = cardSizeMm(template.orientation);
        var ctxFor = function (student) {
            return {school: state.school, palette: state.palette, baseColors: template.baseColors, student: student};
        };

        var root = el("div", "idc-print");
        root.id = "idc-print-root";
        root.dataset.orientation = template.orientation;

        if (mode === "sheet") {
            var layout = SHEET_LAYOUT[template.orientation];
            chunk(state.students, layout.cols * layout.rows).forEach(function (group) {
                root.appendChild(sheetPage(group.map(function (s) {
                    return renderCard(template, "front", ctxFor(s), art[0]);
                }), layout, false));
                if (sides === "both") {
                    root.appendChild(sheetPage(group.map(function (s) {
                        return renderCard(template, "back", ctxFor(s), art[1]);
                    }), layout, true));
                }
            });
        } else {
            state.students.forEach(function (s) {
                var front = el("div", "idc-card-page");
                front.appendChild(renderCard(template, "front", ctxFor(s), art[0]));
                root.appendChild(front);
                if (sides === "both") {
                    var back = el("div", "idc-card-page");
                    back.appendChild(renderCard(template, "back", ctxFor(s), art[1]));
                    root.appendChild(back);
                }
            });
        }

        var pageStyle = el("style");
        pageStyle.id = "idc-print-page";
        pageStyle.textContent = mode === "sheet"
            ? "@page { size: A4 portrait; margin: 0; }"
            : "@page { size: " + size.w + "mm " + size.h + "mm; margin: 0; }" +
            " .idc-card-page { width: " + size.w + "mm; height: " + size.h + "mm; }";

        var old = document.getElementById("idc-print-root");
        if (old) old.remove();
        var oldStyle = document.getElementById("idc-print-page");
        if (oldStyle) oldStyle.remove();

        document.head.appendChild(pageStyle);
        document.body.appendChild(root);

        return waitForImages(root).then(function () {
            var cleanup = function () {
                document.body.classList.remove("idc-printing");
                root.remove();
                pageStyle.remove();
            };
            window.addEventListener("afterprint", cleanup, {once: true});
            document.body.classList.add("idc-printing");
            window.print();
        });
    }
})();