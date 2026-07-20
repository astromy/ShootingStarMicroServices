// landing.js — static/scripts/landing.js
// Loads amCharts vendor scripts then initialises charts1.js inline.
// Do NOT register charts1.js in common.js — let this file handle it.

(function () {

    var BASE = "vendor/amcharts5/";
    var VENDORS = [
        BASE + "index.js",
        BASE + "xy.js",
        BASE + "radar.js",
        BASE + "hierarchy.js",
        BASE + "percent.js",
        BASE + "themes/Animated.js"
    ];

    function loadScript(src, cb) {
        var s = document.createElement("script");
        s.src = src;
        s.async = false;
        s.onload = cb;
        s.onerror = function () {
            console.error("Failed: " + src);
            cb();
        };
        document.head.appendChild(s);
    }

    function loadSequential(list, done) {
        if (!list.length) return done();
        loadScript(list[0], function () {
            loadSequential(list.slice(1), done);
        });
    }

    // Load all vendor scripts, then charts1.js
    loadSequential(VENDORS, function () {
        if (typeof am5 === "undefined") {
            console.error("[landing.js] am5 still not defined after loading vendor scripts.");
            return;
        }
        loadScript("scripts/charts1.js", function () {
        });
    });

})();