// charts1.js — static/scripts/charts1.js
// Loaded by landing.js AFTER all amCharts vendor scripts are ready.
// am5 is guaranteed to exist here.

am5.ready(function () {

    /* ── RADAR ─────────────────────────────────────────── */
    var root = am5.Root.new("radarChart");
    root.setThemes([am5themes_Animated.new(root)]);

    var radarData = [
        {category: "Science", Year_1: 8, Year_2: 2, Year_3: 5},
        {category: "General Arts", Year_1: 11, Year_2: 4, Year_3: 9},
        {category: "Home Econs", Year_1: 7, Year_2: 6, Year_3: 12},
        {category: "Visual Arts", Year_1: 13, Year_2: 8, Year_3: 6},
        {category: "Business", Year_1: 12, Year_2: 10, Year_3: 15}
    ];

    var radarChart = root.container.children.push(
        am5radar.RadarChart.new(root, {panX: false, panY: false})
    );
    var rc = radarChart.set("cursor", am5radar.RadarCursor.new(root, {behavior: "zoomX"}));
    rc.lineY.set("visible", false);

    var rxRend = am5radar.AxisRendererCircular.new(root, {cellStartLocation: 0.2, cellEndLocation: 0.8});
    rxRend.labels.template.setAll({radius: 10});
    var rxAxis = radarChart.xAxes.push(am5xy.CategoryAxis.new(root, {
        maxDeviation: 0, categoryField: "category", renderer: rxRend,
        tooltip: am5.Tooltip.new(root, {})
    }));
    rxAxis.data.setAll(radarData);
    var ryAxis = radarChart.yAxes.push(am5xy.ValueAxis.new(root, {
        renderer: am5radar.AxisRendererRadial.new(root, {})
    }));
    for (var ri = 1; ri <= 3; ri++) {
        var rs = radarChart.series.push(am5radar.RadarColumnSeries.new(root, {
            name: "Year " + ri, xAxis: rxAxis, yAxis: ryAxis,
            valueYField: "Year_" + ri, categoryXField: "category"
        }));
        rs.columns.template.setAll({tooltipText: "{name}: {valueY}", width: am5.percent(100)});
        rs.data.setAll(radarData);
        rs.appear(1000);
    }
    radarChart.appear(1000, 100);
    setTimeout(function () {
        root.resize();
    }, 500);

    /* ── POLAR ─────────────────────────────────────────── */
    var polar = am5.Root.new("polarOptions");
    polar.setThemes([am5themes_Animated.new(polar)]);
    var polarChart = polar.container.children.push(am5radar.RadarChart.new(polar, {panX: false, panY: false}));
    var pc = polarChart.set("cursor", am5radar.RadarCursor.new(polar, {behavior: "none"}));
    pc.lineY.set("visible", false);
    pc.lineX.set("visible", false);
    var pxRend = am5radar.AxisRendererCircular.new(polar, {});
    pxRend.labels.template.setAll({radius: 10});
    var pxAxis = polarChart.xAxes.push(am5xy.CategoryAxis.new(polar, {
        maxDeviation: 0, categoryField: "direction", renderer: pxRend
    }));
    var pyAxis = polarChart.yAxes.push(am5xy.ValueAxis.new(polar, {
        renderer: am5radar.AxisRendererRadial.new(polar, {})
    }));
    var polarSeries = polarChart.series.push(am5radar.RadarLineSeries.new(polar, {
        xAxis: pxAxis, yAxis: pyAxis, valueYField: "value", categoryXField: "direction",
        tooltip: am5.Tooltip.new(polar, {labelText: "{categoryX}: {valueY}"})
    }));
    polarSeries.strokes.template.set("strokeWidth", 2);
    polarSeries.bullets.push(function () {
        return am5.Bullet.new(polar, {
            sprite: am5.Circle.new(polar, {
                radius: 5, fill: polarSeries.get("fill"),
                strokeWidth: 2, stroke: polar.interfaceColors.get("background")
            })
        });
    });
    var polarData = [
        {direction: "N", value: 8}, {direction: "NE", value: 9},
        {direction: "E", value: 4.5}, {direction: "SE", value: 3.5},
        {direction: "S", value: 9.2}, {direction: "SW", value: 8.4},
        {direction: "W", value: 11.1}, {direction: "NW", value: 10}
    ];
    polarSeries.data.setAll(polarData);
    pxAxis.data.setAll(polarData);
    polarChart.radarContainer.children.moveValue(polarChart.topGridContainer, 0);
    polarSeries.appear(1000);
    polarChart.appear(1000, 100);
    setTimeout(function () {
        polar.resize();
    }, 500);

    /* ── DOUGHNUT ───────────────────────────────────────── */
    var radius = am5.Root.new("doughnutChart");
    radius.setThemes([am5themes_Animated.new(radius)]);
    var pieChart = radius.container.children.push(am5percent.PieChart.new(radius, {layout: radius.verticalLayout}));
    var pieSeries = pieChart.series.push(am5percent.PieSeries.new(radius, {
        alignLabels: true, calculateAggregates: true, valueField: "value", categoryField: "category"
    }));
    pieSeries.slices.template.setAll({strokeWidth: 3, stroke: am5.color(0xffffff)});
    pieSeries.labelsContainer.set("paddingTop", 30);
    pieSeries.slices.template.adapters.add("radius", function (r, target) {
        var di = target.dataItem, high = pieSeries.getPrivate("valueHigh");
        if (di) return r * target.dataItem.get("valueWorking", 0) / high;
        return r;
    });
    pieSeries.data.setAll([
        {value: 10, category: "A1"}, {value: 9, category: "B2"}, {value: 6, category: "B3"},
        {value: 5, category: "C4"}, {value: 4, category: "C5"}, {value: 3, category: "C6"},
        {value: 5, category: "D7"}, {value: 4, category: "E8"}, {value: 3, category: "F9"}
    ]);
    var pieLegend = pieChart.children.push(am5.Legend.new(radius, {
        centerX: am5.p50, x: am5.p50, marginTop: 15, marginBottom: 15
    }));
    pieLegend.data.setAll(pieSeries.dataItems);
    pieSeries.appear(1000, 100);
    setTimeout(function () {
        radius.resize();
    }, 500);

    /* ── LINE ───────────────────────────────────────────── */
    var linear = am5.Root.new("lineOptions");
    linear.setThemes([am5themes_Animated.new(linear)]);
    var lineChart = linear.container.children.push(am5xy.XYChart.new(linear, {
        panX: true, panY: true, wheelX: "panX", wheelY: "zoomX", maxTooltipDistance: 0, pinchZoomX: true
    }));
    var lxAxis = lineChart.xAxes.push(am5xy.DateAxis.new(linear, {
        maxDeviation: 0.2, baseInterval: {timeUnit: "day", count: 1},
        renderer: am5xy.AxisRendererX.new(linear, {minorGridEnabled: true}),
        tooltip: am5.Tooltip.new(linear, {})
    }));
    var lyAxis = lineChart.yAxes.push(am5xy.ValueAxis.new(linear, {
        renderer: am5xy.AxisRendererY.new(linear, {})
    }));

    function genLine(count) {
        var d = new Date();
        d.setHours(0, 0, 0, 0);
        var v = 100;
        var out = [];
        for (var i = 0; i < count; i++) {
            v = Math.round((Math.random() * 10 - 4.2) + v);
            am5.time.add(d, "day", 1);
            out.push({date: d.getTime(), value: v});
        }
        return out;
    }

    for (var li = 0; li < 10; li++) {
        var ls = lineChart.series.push(am5xy.LineSeries.new(linear, {
            name: "Series " + li, xAxis: lxAxis, yAxis: lyAxis,
            valueYField: "value", valueXField: "date",
            tooltip: am5.Tooltip.new(linear, {pointerOrientation: "horizontal", labelText: "{valueY}"})
        }));
        ls.data.setAll(genLine(100));
        ls.appear();
    }
    lineChart.set("cursor", am5xy.XYCursor.new(linear, {behavior: "none"}));
    lineChart.appear(1000, 100);
    setTimeout(function () {
        linear.resize();
    }, 300);

    /* ── BAR ────────────────────────────────────────────── */
    var barRoot = am5.Root.new("barOptions");
    barRoot.setThemes([am5themes_Animated.new(barRoot)]);
    var barChart = barRoot.container.children.push(am5xy.XYChart.new(barRoot, {
        panX: false, panY: false, paddingLeft: 0, wheelX: "panX", wheelY: "zoomX",
        layout: barRoot.verticalLayout
    }));
    var barLegend = barChart.children.push(am5.Legend.new(barRoot, {centerX: am5.p50, x: am5.p50}));
    var barData = [
        {Department: "Science", "Over 50": 2.5, "Under 50": 2.5, "Under 30": 2.1},
        {Department: "General Arts", "Over 50": 2.6, "Under 50": 2.7, "Under 30": 2.2},
        {Department: "Home Economics", "Over 50": 2.8, "Under 50": 2.9, "Under 30": 2.4},
        {Department: "Visual Arts", "Over 50": 2.6, "Under 50": 2.7, "Under 30": 2.2},
        {Department: "Business", "Over 50": 2.8, "Under 50": 2.9, "Under 30": 2.4}
    ];
    var bxRend = am5xy.AxisRendererX.new(barRoot, {cellStartLocation: 0.1, cellEndLocation: 0.9});
    var bxAxis = barChart.xAxes.push(am5xy.CategoryAxis.new(barRoot, {
        categoryField: "Department", renderer: bxRend, tooltip: am5.Tooltip.new(barRoot, {})
    }));
    bxAxis.data.setAll(barData);
    var byAxis = barChart.yAxes.push(am5xy.ValueAxis.new(barRoot, {
        renderer: am5xy.AxisRendererY.new(barRoot, {})
    }));

    function makeBar(name, field) {
        var bs = barChart.series.push(am5xy.ColumnSeries.new(barRoot, {
            name: name, xAxis: bxAxis, yAxis: byAxis, valueYField: field, categoryXField: "Department"
        }));
        bs.columns.template.setAll({tooltipText: "{name}: {valueY}", width: am5.percent(90), strokeOpacity: 0});
        bs.data.setAll(barData);
        bs.appear();
        barLegend.data.push(bs);
    }

    makeBar("Over 50 yrs", "Over 50");
    makeBar("Under 50 yrs", "Under 50");
    makeBar("Under 30 yrs", "Under 30");
    barChart.appear(1000, 100);
    setTimeout(function () {
        barRoot.resize();
    }, 300);

    /* ── CANDLESTICK ────────────────────────────────────── */
    var candle = am5.Root.new("candleChartOptions");
    candle.setThemes([am5themes_Animated.new(candle)]);

    function genCandle() {
        var out = [], fd = new Date();
        fd.setDate(fd.getDate() - 200);
        fd.setHours(0, 0, 0, 0);
        var v = 1200;
        for (var i = 0; i < 200; i++) {
            var nd = new Date(fd);
            nd.setDate(nd.getDate() + i);
            v += Math.round((Math.random() < 0.5 ? 1 : -1) * Math.random() * 10);
            var o = v + Math.round(Math.random() * 16 - 8);
            out.push({
                date: nd.getTime(), value: v, open: o,
                low: Math.min(v, o) - Math.round(Math.random() * 5),
                high: Math.max(v, o) + Math.round(Math.random() * 5)
            });
        }
        return out;
    }

    var candleData = genCandle();
    var candleChart = candle.container.children.push(am5xy.XYChart.new(candle, {
        focusable: true, panX: true, panY: true, wheelX: "panX", wheelY: "zoomX", paddingLeft: 0
    }));
    var cxAxis = candleChart.xAxes.push(am5xy.DateAxis.new(candle, {
        groupData: true, maxDeviation: 0.5, baseInterval: {timeUnit: "day", count: 1},
        renderer: am5xy.AxisRendererX.new(candle, {pan: "zoom"}),
        tooltip: am5.Tooltip.new(candle, {})
    }));
    var cyAxis = candleChart.yAxes.push(am5xy.ValueAxis.new(candle, {
        maxDeviation: 1, renderer: am5xy.AxisRendererY.new(candle, {pan: "zoom"})
    }));
    var candleSeries = candleChart.series.push(am5xy.CandlestickSeries.new(candle, {
        name: "MDXI", xAxis: cxAxis, yAxis: cyAxis,
        valueYField: "value", openValueYField: "open", lowValueYField: "low", highValueYField: "high",
        valueXField: "date",
        tooltip: am5.Tooltip.new(candle, {
            labelText: "open: {openValueY}\nlow: {lowValueY}\nhigh: {highValueY}\nclose: {valueY}"
        })
    }));
    var sb = am5xy.XYChartScrollbar.new(candle, {orientation: "horizontal", height: 50});
    candleChart.set("scrollbarX", sb);
    var sbx = sb.chart.xAxes.push(am5xy.DateAxis.new(candle, {
        groupData: true, baseInterval: {timeUnit: "day", count: 1},
        renderer: am5xy.AxisRendererX.new(candle, {strokeOpacity: 0})
    }));
    var sby = sb.chart.yAxes.push(am5xy.ValueAxis.new(candle, {renderer: am5xy.AxisRendererY.new(candle, {})}));
    sb.chart.series.push(am5xy.LineSeries.new(candle, {
        xAxis: sbx, yAxis: sby, valueYField: "value", valueXField: "date"
    })).data.setAll(candleData);
    candleSeries.data.setAll(candleData);
    candleSeries.appear(1000);
    candleChart.appear(1000, 100);
    setTimeout(function () {
        candle.resize();
    }, 300);

    /* ── FORCE DIRECTED ─────────────────────────────────── */
    var fdt = am5.Root.new("forceDirectedTreeOption");
    fdt.setThemes([am5themes_Animated.new(fdt)]);
    var zc = fdt.container.children.push(am5.ZoomableContainer.new(fdt, {
        width: am5.p100, height: am5.p100, wheelable: true, pinchZoom: true
    }));
    zc.children.push(am5.ZoomTools.new(fdt, {target: zc}));
    var fdSeries = zc.contents.children.push(am5hierarchy.ForceDirected.new(fdt, {
        singleBranchOnly: false, downDepth: 1, initialDepth: 10,
        nodePadding: 20, valueField: "value", categoryField: "name", childDataField: "children"
    }));
    fdSeries.labels.template.set("minScale", 0);

    function genFDT(data, name, level) {
        for (var i = 0; i < Math.ceil(3 * Math.random()) + 1; i++) {
            var nn = name + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[i];
            var child = level < 1
                ? {name: nn + level, children: []}
                : {name: name + i, value: Math.round(Math.random() * 100)};
            if (child.children) genFDT(child, nn + i, level + 1);
            data.children.push(child);
        }
    }

    var fdData = {name: "Root", children: []};
    genFDT(fdData, "", 0);
    fdSeries.data.setAll([fdData]);
    fdSeries.set("selectedDataItem", fdSeries.dataItems[0]);
    fdSeries.appear(1000, 100);
    setTimeout(function () {
        fdt.resize();
    }, 300);

}); // end am5.ready