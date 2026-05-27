am5.ready(function() {

// Create root element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var root = am5.Root.new("radarChart");

// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
root.setThemes([
  am5themes_Animated.new(root)
]);


var tempC=root.container;
// 👇 Add this block:
root.container.setAll({
  width: am5.percent(100),
  height: am5.percent(100),
  layout: root.verticalLayout
});

// Data
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Setting_data
var data = [{
  category: "Science",
  Year_1: 8,
  Year_2: 2,
  Year_3: 5
}, {
  category: "General Arts",
  Year_1: 11,
  Year_2: 4,
  Year_3: 9
}, {
  category: "Home Econs",
  Year_1: 7,
  Year_2: 6,
  Year_3: 12
}, {
  category: "Visual Arts",
  Year_1: 13,
  Year_2: 8,
  Year_3: 6
}, {
  category: "Business",
  Year_1: 12,
  Year_2: 10,
  Year_3: 15
}];

// Create chart
// https://www.amcharts.com/docs/v5/charts/radar-chart/
var chart = root.container.children.push(
  am5radar.RadarChart.new(root, {
    panX: false,
    panY: false,
    wheelX: "panX",
    wheelY: "zoomX"
  })
);

// Add cursor
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Cursor
var cursor = chart.set("cursor", am5radar.RadarCursor.new(root, {
  behavior: "zoomX"
}));

cursor.lineY.set("visible", false);

// Create axes and their renderers
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Adding_axes
var xRenderer = am5radar.AxisRendererCircular.new(root, {
  cellStartLocation: 0.2,
  cellEndLocation: 0.8
});

xRenderer.labels.template.setAll({
  radius: 10
});

var xAxis = chart.xAxes.push(
  am5xy.CategoryAxis.new(root, {
    maxDeviation: 0,
    categoryField: "category",
    renderer: xRenderer,
    tooltip: am5.Tooltip.new(root, {})
  })
);

xAxis.data.setAll(data);

var yAxis = chart.yAxes.push(
  am5xy.ValueAxis.new(root, {
    renderer: am5radar.AxisRendererRadial.new(root, {})
  })
);

// Create series
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Adding_series
for (var i = 1; i <= 3; i++) {
  var series = chart.series.push(
    am5radar.RadarColumnSeries.new(root, {
      name: "Year " + i,
      xAxis: xAxis,
      yAxis: yAxis,
      valueYField: "Year_" + i,
      categoryXField: "category"
    })
  );

  series.columns.template.setAll({
    tooltipText: "{name}: {valueY}",
    width: am5.percent(100)
  });

  series.data.setAll(data);

  series.appear(1000);
}

// Add scrollbars
//chart.set("scrollbarX", am5.Scrollbar.new(root, { orientation: "horizontal", exportable: false }));
//chart.set("scrollbarY", am5.Scrollbar.new(root, { orientation: "vertical", exportable: false }));


// Animate chart
// https://www.amcharts.com/docs/v5/concepts/animations/#Initial_animation
chart.appear(1000, 100);

setTimeout(() => {
  root.resize();
}, 500);


/*==============POLAR CHARTS==========================================*/



// Create polar element
var polar = am5.Root.new("polarOptions");

// Set themes
polar.setThemes([
  am5themes_Animated.new(polar)
]);

// Generate and set data
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Setting_data
var cat = -1;
var value = 10;

function generateData() {
  value = Math.round(Math.random() * 10);
  cat++;
  return {
    category: "cat" + cat,
    value: value
  };
}

function generateDatas(count) {
  cat = -1;
  var data = [];
  for (var i = 0; i < count; ++i) {
    data.push(generateData());
  }
  return data;
}

// Create chart
// https://www.amcharts.com/docs/v5/charts/radar-chart/
var chart = polar.container.children.push(am5radar.RadarChart.new(polar, {
  panX: false,
  panY: false,
  wheelX: "panX",
  wheelY: "zoomX"
}));

// Add cursor
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Cursor
var cursor = chart.set("cursor", am5radar.RadarCursor.new(polar, {
  behavior: "none"
}));

cursor.lineY.set("visible", false);
cursor.lineX.set("visible", false);

// Create axes and their renderers
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Adding_axes
var xRenderer = am5radar.AxisRendererCircular.new(polar, {});
xRenderer.labels.template.setAll({
  radius: 10
});

var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(polar, {
  maxDeviation: 0,
  categoryField: "direction",
  renderer: xRenderer
}));

var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(polar, {
  renderer: am5radar.AxisRendererRadial.new(polar, {})
}));

// Create series
// https://www.amcharts.com/docs/v5/charts/radar-chart/#Adding_series

var series = chart.series.push(am5radar.RadarLineSeries.new(polar, {
  stacked: true,
  name: "Series ",
  xAxis: xAxis,
  yAxis: yAxis,
  valueYField: "value",
  categoryXField: "direction",
  tooltip: am5.Tooltip.new(polar, {
    labelText: "{categoryX}: {valueY}"
  })
}));


series.strokes.template.set("strokeWidth", 2);
series.bullets.push(function() {
  return am5.Bullet.new(polar, {
    sprite: am5.Circle.new(polar, {
      radius: 5,
      fill: series.get("fill"),
      strokeWidth: 2,
      stroke: polar.interfaceColors.get("background")
    })
  })
})

var data = [{
  "direction": "N",
  "value": 8
}, {
  "direction": "NE",
  "value": 9
}, {
  "direction": "E",
  "value": 4.5
}, {
  "direction": "SE",
  "value": 3.5
}, {
  "direction": "S",
  "value": 9.2
}, {
  "direction": "SW",
  "value": 8.4
}, {
  "direction": "W",
  "value": 11.1
}, {
  "direction": "NW",
  "value": 10
}]

series.data.setAll(data);
xAxis.data.setAll(data);

var range0 = xAxis.createAxisRange(xAxis.makeDataItem({ category: "NW", endCategory: "NW" }));
range0.get("axisFill").setAll({
  visible: true,
  fill: am5.color(0x0000ff),
  fillOpacity: 0.3
})

var range1 = xAxis.createAxisRange(xAxis.makeDataItem({ category: "N", endCategory: "N" }));
range1.get("axisFill").setAll({
  visible: true,
  fill: am5.color(0x0000ff),
  fillOpacity: 0.3
})

var range2 = xAxis.createAxisRange(xAxis.makeDataItem({ category: "SE", endCategory: "S" }));
range2.get("axisFill").setAll({
  visible: true,
  fill: am5.color(0xFF0000),
  fillOpacity: 0.3
})

chart.radarContainer.children.moveValue(chart.topGridContainer, 0);

// Animate chart
// https://www.amcharts.com/docs/v5/concepts/animations/#Initial_animation
series.appear(1000);
chart.appear(1000, 100);


setTimeout(() => {
  polar.resize();
}, 500);



 /*============================  RADIUS ========================================*/



// Create radius element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var radius = am5.Root.new("doughnutChart");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
radius.setThemes([
  am5themes_Animated.new(radius)
]);


// Create chart
// https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/
var chart = radius.container.children.push(am5percent.PieChart.new(radius, {
  layout: radius.verticalLayout
}));


// Create series
// https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/#Series
var series = chart.series.push(am5percent.PieSeries.new(radius, {
  alignLabels: true,
  calculateAggregates: true,
  valueField: "value",
  categoryField: "category"
}));

series.slices.template.setAll({
  strokeWidth: 3,
  stroke: am5.color(0xffffff)
});

series.labelsContainer.set("paddingTop", 30)


// Set up adapters for variable slice radius
// https://www.amcharts.com/docs/v5/concepts/settings/adapters/
series.slices.template.adapters.add("radius", function (radius, target) {
  var dataItem = target.dataItem;
  var high = series.getPrivate("valueHigh");

  if (dataItem) {
    var value = target.dataItem.get("valueWorking", 0);
    return radius * value / high
  }
  return radius;
});


// Set data
// https://www.amcharts.com/docs/v5/charts/percent-charts/pie-chart/#Setting_data
series.data.setAll([{
  value: 10,
  category: "A1"
}, {
  value: 9,
  category: "B2"
}, {
  value: 6,
  category: "B3"
}, {
  value: 5,
  category: "C4"
}, {
  value: 4,
  category: "C5"
}, {
  value: 3,
  category: "C6"
}, {
   value: 5,
   category: "D7"
 }, {
   value: 4,
   category: "E8"
 }, {
   value: 3,
   category: "F9"
 }]);


// Create legend
// https://www.amcharts.com/docs/v5/charts/percent-charts/legend-percent-series/
var legend = chart.children.push(am5.Legend.new(radius, {
  centerX: am5.p50,
  x: am5.p50,
  marginTop: 15,
  marginBottom: 15
}));

legend.data.setAll(series.dataItems);


// Play initial series animation
// https://www.amcharts.com/docs/v5/concepts/animations/#Animation_of_series
series.appear(1000, 100);


setTimeout(() => {
  radius.resize();
}, 500);



/* ============================================ LINEAR  ========================================*/



// Create linear element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var linear = am5.Root.new("lineOptions");

const myTheme = am5.Theme.new(linear);

myTheme.rule("AxisLabel", ["minor"]).setAll({
  dy:1
});

myTheme.rule("Grid", ["x"]).setAll({
  strokeOpacity: 0.05
});

myTheme.rule("Grid", ["x", "minor"]).setAll({
  strokeOpacity: 0.05
});

// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
linear.setThemes([
  am5themes_Animated.new(linear),
  myTheme
]);

// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart = linear.container.children.push(am5xy.XYChart.new(linear, {
  panX: true,
  panY: true,
  wheelX: "panX",
  wheelY: "zoomX",
  maxTooltipDistance: 0,
  pinchZoomX:true
}));


var date = new Date();
date.setHours(0, 0, 0, 0);
var value = 100;

function generateData() {
  value = Math.round((Math.random() * 10 - 4.2) + value);
  am5.time.add(date, "day", 1);
  return {
    date: date.getTime(),
    value: value
  };
}

function generateDatas(count) {
  var data = [];
  for (var i = 0; i < count; ++i) {
    data.push(generateData());
  }
  return data;
}


// Create axes
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
var xAxis = chart.xAxes.push(am5xy.DateAxis.new(linear, {
  maxDeviation: 0.2,
  baseInterval: {
    timeUnit: "day",
    count: 1
  },
  renderer: am5xy.AxisRendererX.new(linear, {
    minorGridEnabled: true
  }),
  tooltip: am5.Tooltip.new(linear, {})
}));

var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(linear, {
  renderer: am5xy.AxisRendererY.new(linear, {})
}));


// Add series1
// https://www.amcharts.com/docs/v5/charts/xy-chart/series1/
for (var i = 0; i < 10; i++) {
  var series1 = chart.series.push(am5xy.LineSeries.new(linear, {
    name: "Series " + i,
    xAxis: xAxis,
    yAxis: yAxis,
    valueYField: "value",
    valueXField: "date",
    legendValueText: "{valueY}",
    tooltip: am5.Tooltip.new(linear, {
      pointerOrientation: "horizontal",
      labelText: "{valueY}"
    })
  }));

  date = new Date();
  date.setHours(0, 0, 0, 0);
  value = 0;

  var data = generateDatas(100);
  series1.data.setAll(data);

  // Make stuff animate on load
  // https://www.amcharts.com/docs/v5/concepts/animations/
  series1.appear();
}


// Add cursor
// https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
var cursor = chart.set("cursor", am5xy.XYCursor.new(linear, {
  behavior: "none"
}));
cursor.lineY.set("visible", false);


// Add scrollbar
// https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
/*chart.set("scrollbarX", am5.Scrollbar.new(linear, {
  orientation: "horizontal"
}));

chart.set("scrollbarY", am5.Scrollbar.new(linear, {
  orientation: "vertical"
}));
*/


// Add legend
// https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series1/
var legend = chart.rightAxesContainer.children.push(am5.Legend.new(linear, {
  width: 200,
  paddingLeft: 15,
  height: am5.percent(100)
}));

// When legend item container is hovered, dim all the series1 except the hovered one
legend.itemContainers.template.events.on("pointerover", function(e) {
  var itemContainer = e.target;

  // As series1 list is data of a legend, dataContext is series1
  var series1 = itemContainer.dataItem.dataContext;

  chart.series.each(function(chartSeries) {
    if (chartSeries != series1) {
      chartSeries.strokes.template.setAll({
        strokeOpacity: 0.15,
        stroke: am5.color(0x000000)
      });
    } else {
      chartSeries.strokes.template.setAll({
        strokeWidth: 3
      });
    }
  })
})

// When legend item container is unhovered, make all series1 as they are
legend.itemContainers.template.events.on("pointerout", function(e) {
  var itemContainer = e.target;
  var series1 = itemContainer.dataItem.dataContext;

  chart.series.each(function(chartSeries) {
    chartSeries.strokes.template.setAll({
      strokeOpacity: 1,
      strokeWidth: 1,
      stroke: chartSeries.get("fill")
    });
  });
})

legend.itemContainers.template.set("width", am5.p100);
legend.valueLabels.template.setAll({
  width: am5.p100,
  textAlign: "right"
});

// It's is important to set legend data after all the events are set on template, otherwise events won't be copied
legend.data.setAll(chart.series.values);


// Make stuff animate on load
// https://www.amcharts.com/docs/v5/concepts/animations/
chart.appear(1000, 100);


    setTimeout(() => {
      linear.resize();
    }, 300);

/*=============================================== BAR CHARTS ==============================================*/

var barChart = am5.Root.new("barOptions");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
barChart.setThemes([
  am5themes_Animated.new(barChart)
]);


// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart = barChart.container.children.push(am5xy.XYChart.new(barChart, {
  panX: false,
  panY: false,
  paddingLeft: 0,
  wheelX: "panX",
  wheelY: "zoomX",
  layout: barChart.verticalLayout
}));


// Add legend
// https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
var legend = chart.children.push(
  am5.Legend.new(barChart, {
    centerX: am5.p50,
    x: am5.p50
  })
);

var data = [{
  "Department": "Science",
  "Over 50 yrs": 2.5,
  "Under 50 yrs": 2.5,
  "Under 30 yrs": 2.1
}, {
  "Department": "General Arts",
  "Over 50 yrs": 2.6,
  "Under 50 yrs": 2.7,
  "Under 30 yrs": 2.2
}, {
  "Department": "Home Economics",
  "Over 50 yrs": 2.8,
  "Under 50 yrs": 2.9,
  "Under 30 yrs": 2.4
}, {
  "Department": "Visual Arts",
  "Over 50 yrs": 2.6,
  "Under 50 yrs": 2.7,
  "Under 30 yrs": 2.2
}, {
  "Department": "Business",
  "Over 50 yrs": 2.8,
  "Under 50 yrs": 2.9,
  "Under 30 yrs": 2.4
}]

// Create axes
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
var xRenderer = am5xy.AxisRendererX.new(barChart, {
  cellStartLocation: 0.1,
  cellEndLocation: 0.9,
  minorGridEnabled: true
})

var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(barChart, {
  categoryField: "Department",
  renderer: xRenderer,
  tooltip: am5.Tooltip.new(barChart, {})
}));

xRenderer.grid.template.setAll({
  location: 1
})

xAxis.data.setAll(data);

var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(barChart, {
  renderer: am5xy.AxisRendererY.new(barChart, {
    strokeOpacity: 0.1
  })
}));


// Add series
// https://www.amcharts.com/docs/v5/charts/xy-chart/series/
function makeSeries(name, fieldName) {
  var barSeries = chart.series.push(am5xy.ColumnSeries.new(barChart, {
    name: name,
    xAxis: xAxis,
    yAxis: yAxis,
    valueYField: fieldName,
    categoryXField: "Department"
  }));

  barSeries.columns.template.setAll({
    tooltipText: "{name}, {categoryX}:{valueY}",
    width: am5.percent(90),
    tooltipY: 0,
    strokeOpacity: 0
  });

  barSeries.data.setAll(data);

  // Make stuff animate on load
  // https://www.amcharts.com/docs/v5/concepts/animations/
  barSeries.appear();

  barSeries.bullets.push(function () {
    return am5.Bullet.new(barChart, {
      locationY: 0,
      sprite: am5.Label.new(barChart, {
        text: "{valueY}",
        fill: barChart.interfaceColors.get("alternativeText"),
        centerY: 0,
        centerX: am5.p50,
        populateText: true
      })
    });
  });

  legend.data.push(barSeries);
}

makeSeries("Over 50 yrs", "Over 50 yrs");
makeSeries("Under 50 yrs", "Under 50 yrs");
makeSeries("Under 30 yrs", "Under 30 yrs");


// Make stuff animate on load
// https://www.amcharts.com/docs/v5/concepts/animations/
chart.appear(1000, 100);


    setTimeout(() => {
      barChart.resize();
    }, 300);

/*=================================================    CANDLE    ====================================*/


// Create candle element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var candle = am5.Root.new("candleChartOptions");

const myTheme2 = am5.Theme.new(candle);

myTheme2.rule("Grid", ["scrollbar", "minor"]).setAll({
  visible:false
});

candle.setThemes([
  am5themes_Animated.new(candle),
  myTheme2
]);

function generateChartData() {
  var chartData = [];
  var firstDate = new Date();
  firstDate.setDate(firstDate.getDate() - 2000);
  firstDate.setHours(0, 0, 0, 0);
  var value = 1200;
  for (var i = 0; i < 200; i++) {
    var newDate = new Date(firstDate);
    newDate.setDate(newDate.getDate() + i);

    value += Math.round((Math.random() < 0.5 ? 1 : -1) * Math.random() * 10);
    var open = value + Math.round(Math.random() * 16 - 8);
    var low = Math.min(value, open) - Math.round(Math.random() * 5);
    var high = Math.max(value, open) + Math.round(Math.random() * 5);

    chartData.push({
      date: newDate.getTime(),
      value: value,
      open: open,
      low: low,
      high: high
    });
  }
  return chartData;
}

var data = generateChartData();

// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart = candle.container.children.push(
  am5xy.XYChart.new(candle, {
    focusable: true,
    panX: true,
    panY: true,
    wheelX: "panX",
    wheelY: "zoomX",
    paddingLeft: 0
  })
);

// Create axes
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
var xAxis = chart.xAxes.push(
  am5xy.DateAxis.new(candle, {
    groupData: true,
    maxDeviation: 0.5,
    baseInterval: { timeUnit: "day", count: 1 },
    renderer: am5xy.AxisRendererX.new(candle, {
      pan: "zoom",
      minorGridEnabled: true
    }),
    tooltip: am5.Tooltip.new(candle, {})
  })
);

var yAxis = chart.yAxes.push(
  am5xy.ValueAxis.new(candle, {
    maxDeviation: 1,
    renderer: am5xy.AxisRendererY.new(candle, {
      pan: "zoom"
    })
  })
);

var color = candle.interfaceColors.get("background");

// Add series
// https://www.amcharts.com/docs/v5/charts/xy-chart/series/
var candleSeries = chart.series.push(
  am5xy.CandlestickSeries.new(candle, {
    fill: color,
    calculateAggregates: true,
    stroke: color,
    name: "MDXI",
    xAxis: xAxis,
    yAxis: yAxis,
    valueYField: "value",
    openValueYField: "open",
    lowValueYField: "low",
    highValueYField: "high",
    valueXField: "date",
    lowValueYGrouped: "low",
    highValueYGrouped: "high",
    openValueYGrouped: "open",
    valueYGrouped: "close",
    legendValueText:
      "open: {openValueY} low: {lowValueY} high: {highValueY} close: {valueY}",
    legendRangeValueText: "{valueYClose}",
    tooltip: am5.Tooltip.new(candle, {
      pointerOrientation: "horizontal",
      labelText: "open: {openValueY}\nlow: {lowValueY}\nhigh: {highValueY}\nclose: {valueY}"
    })
  })
);

// Add cursor
// https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
var cursor = chart.set(
  "cursor",
  am5xy.XYCursor.new(candle, {
    xAxis: xAxis
  })
);
cursor.lineY.set("visible", false);

// Stack axes vertically
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/#Stacked_axes
chart.leftAxesContainer.set("layout", candle.verticalLayout);

// Add scrollbar
// https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
var scrollbar = am5xy.XYChartScrollbar.new(candle, {
  orientation: "horizontal",
  height: 50
});
chart.set("scrollbarX", scrollbar);

var sbxAxis = scrollbar.chart.xAxes.push(
  am5xy.DateAxis.new(candle, {
    groupData: true,
    groupIntervals: [{
      timeUnit: "week",
      count: 1
    }],
    baseInterval: { timeUnit: "day", count: 1 },
    renderer: am5xy.AxisRendererX.new(candle, {
      minorGridEnabled: true,
      strokeOpacity: 0
    })
  })
);

var sbyAxis = scrollbar.chart.yAxes.push(
  am5xy.ValueAxis.new(candle, {
    renderer: am5xy.AxisRendererY.new(candle, {})
  })
);

var sbseries = scrollbar.chart.series.push(
  am5xy.LineSeries.new(candle, {
    xAxis: sbxAxis,
    yAxis: sbyAxis,
    valueYField: "value",
    valueXField: "date"
  })
);

// Add legend
// https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
var legend = yAxis.axisHeader.children.push(am5.Legend.new(candle, {}));

legend.data.push(candleSeries);

legend.markers.template.setAll({
  width: 10
});

legend.markerRectangles.template.setAll({
  cornerRadiusTR: 0,
  cornerRadiusBR: 0,
  cornerRadiusTL: 0,
  cornerRadiusBL: 0
});

// set data
candleSeries.data.setAll(data);
sbseries.data.setAll(data);

// Make stuff animate on load
// https://www.amcharts.com/docs/v5/concepts/animations/
candleSeries.appear(1000);
chart.appear(1000, 100);

   setTimeout(() => {
      candle.resize();
    }, 300);


/*========================================   Force-Directed Tree   ========================================*/

// Create forceDirectedTree element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var forceDirectedTree = am5.Root.new("forceDirectedTreeOption");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
forceDirectedTree.setThemes([
  am5themes_Animated.new(forceDirectedTree)
]);


var zoomableContainer = forceDirectedTree.container.children.push(
  am5.ZoomableContainer.new(forceDirectedTree, {
    width: am5.p100,
    height: am5.p100,
    wheelable: true,
    pinchZoom: true
  })
);

var zoomTools = zoomableContainer.children.push(am5.ZoomTools.new(forceDirectedTree, {
  target: zoomableContainer
}));

// Create series
// https://www.amcharts.com/docs/v5/charts/hierarchy/#Adding
var fdtSeries = zoomableContainer.contents.children.push(am5hierarchy.ForceDirected.new(forceDirectedTree, {
  singleBranchOnly: false,
  downDepth: 1,
  initialDepth: 10,
  nodePadding: 20,
  valueField: "value",
  categoryField: "name",
  childDataField: "children"
}));

fdtSeries.linkBullets.push(function(forceDirectedTree, source, target) {
  const bullet = am5.Bullet.new(forceDirectedTree, {
    locationX: 0.5,
    autoRotate: true,
    autoRotateAngle: 180,
    sprite: am5.Graphics.new(forceDirectedTree, {
      fill: source.get("fill"),
      centerY: am5.percent(50),
      centerX: am5.percent(50),
      draw: function(display) {
        display.moveTo(0, -6);
        display.lineTo(16, 0);
        display.lineTo(0, 6);
        display.lineTo(3, 0);
        display.lineTo(0, -6);
      }
    })
  });

  bullet.animate({
    key: "locationX",
    to: -0.1,
    from: 1.1,
    duration: Math.random() * 500 + 1000,
    loops: Infinity,
    easing: am5.ease.quad
  });

  return bullet;
});

fdtSeries.labels.template.set("minScale", 0);

// Generate and set data
// https://www.amcharts.com/docs/v5/charts/hierarchy/#Setting_data
var maxLevels = 1;
var maxNodes = 3;
var maxValue = 100;

var data = {
  name: "Root",
  children: []
}
generateLevel(data, "", 0);

fdtSeries.data.setAll([data]);
fdtSeries.set("selectedDataItem", fdtSeries.dataItems[0]);

function generateLevel(data, name, level) {
  for (var i = 0; i < Math.ceil(maxNodes * Math.random()) + 1; i++) {
    var nodeName = name + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[i];
    var child;
    if (level < maxLevels) {
      child = {
        name: nodeName + level
      }

      if (level > 0 && Math.random() < 0.5) {
        child.value = Math.round(Math.random() * maxValue);
      }
      else {
        child.children = [];
        generateLevel(child, nodeName + i, level + 1)
      }
    }
    else {
      child = {
        name: name + i,
        value: Math.round(Math.random() * maxValue)
      }
    }
    data.children.push(child);
  }

  level++;
  return data;
}


// Make stuff animate on load
fdtSeries.appear(1000, 100);

   setTimeout(() => {
      forceDirectedTree.resize();
    }, 300);

});