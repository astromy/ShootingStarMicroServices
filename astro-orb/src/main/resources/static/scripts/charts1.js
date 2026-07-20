// charts1.js — scripts/charts1.js
// Loaded by common.js AFTER all amCharts vendor scripts are ready.
// Renders all six dashboard charts using the element IDs defined in dashboard.js HTML:
//   #radarChart              → Subject Performance by Class (radar line)
//   #doughnutChart           → Grade Distribution (donut)
//   #lineOptions             → Student Enrollment Trends (line)
//   #barOptions              → Staff Distribution by Dept & Gender (grouped bar)
//   #polarOptions            → Subject Performance BECE Level (pie)
//   #forceDirectedTreeOption → School Org Structure (force-directed)

function initCharts() {
    am5.ready(function () {

        /* ── SAFE ROOT HELPER ───────────────────────────────────────────────
         * Disposes any existing amCharts root on the element before creating
         * a new one, preventing the "already in use" error on re-navigation.
         * ─────────────────────────────────────────────────────────────────── */
        function safeRoot(id) {
            var el = document.getElementById(id);
            if (!el) return null;
            // Dispose previous root if present
            if (el.__am5root) {
                try {
                    el.__am5root.dispose();
                } catch (e) {
                }
            }
            var root = am5.Root.new(id);
            el.__am5root = root;
            return root;
        }

        /* ── 1. RADAR — Subject Performance by Class ────────────────────── */
        (function () {
            var root = safeRoot('radarChart');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var chart = root.container.children.push(
                am5radar.RadarChart.new(root, {
                    panX: false, panY: false,
                    innerRadius: am5.percent(20),
                    radius: am5.percent(90),
                })
            );

            var cursor = chart.set('cursor', am5radar.RadarCursor.new(root, {behavior: 'none'}));
            cursor.lineY.set('visible', false);

            var xRenderer = am5radar.AxisRendererCircular.new(root, {});
            xRenderer.labels.template.setAll({fontSize: 11, radius: 10});

            var xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: 'subject',
                    renderer: xRenderer,
                })
            );

            var yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    renderer: am5radar.AxisRendererRadial.new(root, {}),
                    min: 0, max: 100,
                })
            );

            var subjects = ['Mathematics', 'English', 'Science', 'Social Studies', 'ICT', 'French'];
            var classNames = ['Primary 4', 'Primary 5', 'Primary 6', 'JHS 1', 'JHS 2', 'JHS 3'];
            var scores = [
                [85, 88, 82, 86, 90, 75],
                [82, 85, 80, 84, 88, 72],
                [78, 82, 78, 82, 86, 70],
                [75, 80, 76, 80, 84, 68],
                [72, 78, 74, 78, 82, 65],
                [70, 75, 72, 76, 80, 62],
            ];
            var colors = [0x0073B7, 0x00A65A, 0xF39C12, 0xDD4B39, 0x605CA8, 0x00C0EF];

            xAxis.data.setAll(subjects.map(function (s) {
                return {subject: s};
            }));

            classNames.forEach(function (name, ci) {
                var series = chart.series.push(
                    am5radar.RadarLineSeries.new(root, {
                        name: name,
                        xAxis: xAxis, yAxis: yAxis,
                        valueYField: 'score', categoryXField: 'subject',
                        stroke: am5.color(colors[ci]),
                        fill: am5.color(colors[ci]),
                        tooltip: am5.Tooltip.new(root, {labelText: '{name}: {valueY}%'}),
                    })
                );
                series.strokes.template.setAll({strokeWidth: 2});
                series.fills.template.setAll({visible: true, fillOpacity: 0.08});
                series.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Circle.new(root, {
                            radius: 4,
                            fill: am5.color(colors[ci]),
                            stroke: root.interfaceColors.get('background'),
                            strokeWidth: 1,
                        }),
                    });
                });
                series.data.setAll(subjects.map(function (s, si) {
                    return {subject: s, score: scores[ci][si]};
                }));
                series.appear(1000);
            });

            var legend = chart.children.push(
                am5.Legend.new(root, {centerX: am5.p50, x: am5.p50, marginTop: 10})
            );
            legend.labels.template.setAll({fontSize: 11});
            legend.data.setAll(chart.series.values);
            chart.appear(1000, 100);
        })();

        /* ── 2. DONUT — Grade Distribution ──────────────────────────────── */
        (function () {
            var root = safeRoot('doughnutChart');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var chart = root.container.children.push(
                am5percent.PieChart.new(root, {
                    layout: root.horizontalLayout,
                    innerRadius: am5.percent(55),
                    paddingTop: 10, paddingBottom: 10,
                })
            );

            var series = chart.series.push(
                am5percent.PieSeries.new(root, {
                    categoryField: 'grade', valueField: 'count',
                    alignLabels: false,
                    tooltip: am5.Tooltip.new(root, {labelText: '{category}: {value} students'}),
                })
            );
            series.labels.template.set('visible', false);
            series.ticks.template.set('visible', false);
            series.slices.template.setAll({
                strokeWidth: 2,
                stroke: am5.color(0xffffff),
                cornerRadiusTL: 3, cornerRadiusTR: 3,
                cornerRadiusBL: 3, cornerRadiusBR: 3,
            });
            series.get('colors').set('colors', [
                am5.color(0x00A65A), am5.color(0x0073B7),
                am5.color(0xF39C12), am5.color(0xDD4B39), am5.color(0x932ab6),
            ]);

            series.data.setAll([
                {grade: 'A (80–100%)', count: 245},
                {grade: 'B (70–79%)', count: 386},
                {grade: 'C (60–69%)', count: 312},
                {grade: 'D (50–59%)', count: 198},
                {grade: 'E (Below 50%)', count: 143},
            ]);

            // Percent labels on big enough slices
            series.bullets.push(function (r, s, dataItem) {
                if (dataItem.get('valuePercentTotal') < 8) return;
                return am5.Bullet.new(root, {
                    locationRadius: 0.7,
                    sprite: am5.Label.new(root, {
                        text: "{valuePercentTotal.formatNumber('0.')}%",
                        fill: am5.color(0xffffff),
                        fontSize: 11, fontWeight: '600',
                        centerX: am5.p50, centerY: am5.p50,
                        populateText: true,
                    }),
                });
            });

            var legend = chart.children.push(
                am5.Legend.new(root, {
                    centerY: am5.p50, y: am5.p50,
                    layout: root.verticalLayout,
                    paddingLeft: 20,
                })
            );
            legend.labels.template.setAll({fontSize: 12});
            legend.markers.template.setAll({width: 12, height: 12});
            legend.data.setAll(series.dataItems);
            series.appear(1000, 100);
            chart.appear(1000, 100);
        })();

        /* ── 3. LINE — Student Enrollment Trends ────────────────────────── */
        (function () {
            var root = safeRoot('lineOptions');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    panX: true, panY: false,
                    wheelX: 'panX', wheelY: 'zoomX',
                    pinchZoomX: true,
                    paddingLeft: 0,
                })
            );

            chart.set('cursor', am5xy.XYCursor.new(root, {behavior: 'none'}));

            var xRenderer = am5xy.AxisRendererX.new(root, {minGridDistance: 40});
            xRenderer.grid.template.setAll({strokeOpacity: 0.05});

            var xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: 'year',
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {}),
                })
            );

            var yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    renderer: am5xy.AxisRendererY.new(root, {}),
                })
            );

            var years = ['2019', '2020', '2021', '2022', '2023', '2024'];
            xAxis.data.setAll(years.map(function (y) {
                return {year: y};
            }));

            function makeLine(name, field, color, data) {
                var series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis, yAxis: yAxis,
                        valueYField: field, categoryXField: 'year',
                        stroke: am5.color(color), fill: am5.color(color),
                        tooltip: am5.Tooltip.new(root, {labelText: '{name}: {valueY}'}),
                    })
                );
                series.strokes.template.setAll({strokeWidth: 3});
                series.fills.template.setAll({visible: true, fillOpacity: 0.08});
                series.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Circle.new(root, {
                            radius: 5, fill: am5.color(color),
                            stroke: root.interfaceColors.get('background'), strokeWidth: 2,
                        }),
                    });
                });
                series.data.setAll(data);
                series.appear(1000);
            }

            makeLine('Boys', 'boys', 0x0073B7, [
                {year: '2019', boys: 520}, {year: '2020', boys: 548},
                {year: '2021', boys: 567}, {year: '2022', boys: 589},
                {year: '2023', boys: 612}, {year: '2024', boys: 645},
            ]);
            makeLine('Girls', 'girls', 0xDD4B39, [
                {year: '2019', girls: 498}, {year: '2020', girls: 523},
                {year: '2021', girls: 541}, {year: '2022', girls: 567},
                {year: '2023', girls: 589}, {year: '2024', girls: 639},
            ]);

            var legend = chart.children.push(
                am5.Legend.new(root, {centerX: am5.p50, x: am5.p50, marginTop: 8})
            );
            legend.data.setAll(chart.series.values);
            chart.appear(1000, 100);
        })();

        /* ── 4. GROUPED BAR — Staff Distribution by Dept & Gender ───────── */
        (function () {
            var root = safeRoot('barOptions');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    panX: false, panY: false,
                    paddingLeft: 0,
                    layout: root.verticalLayout,
                })
            );

            var xRenderer = am5xy.AxisRendererX.new(root, {
                cellStartLocation: 0.1, cellEndLocation: 0.9, minGridDistance: 30,
            });
            xRenderer.grid.template.setAll({strokeOpacity: 0.05});

            var xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: 'department',
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {}),
                })
            );

            var yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    renderer: am5xy.AxisRendererY.new(root, {}),
                })
            );

            var depts = ['Mathematics', 'English', 'Science', 'Social Studies', 'ICT', 'Languages'];
            xAxis.data.setAll(depts.map(function (d) {
                return {department: d};
            }));

            function makeBar(name, field, color, data) {
                var series = chart.series.push(
                    am5xy.ColumnSeries.new(root, {
                        name: name,
                        xAxis: xAxis, yAxis: yAxis,
                        valueYField: field, categoryXField: 'department',
                        tooltip: am5.Tooltip.new(root, {labelText: '{name}: {valueY}'}),
                    })
                );
                series.columns.template.setAll({
                    width: am5.percent(90), strokeOpacity: 0,
                    fill: am5.color(color),
                    cornerRadiusTL: 4, cornerRadiusTR: 4,
                });
                series.data.setAll(data);
                series.appear();
                return series;
            }

            var maleData = [
                {department: 'Mathematics', male: 8}, {department: 'English', male: 5},
                {department: 'Science', male: 7}, {department: 'Social Studies', male: 6},
                {department: 'ICT', male: 4}, {department: 'Languages', male: 3},
            ];
            var femaleData = [
                {department: 'Mathematics', female: 6}, {department: 'English', female: 9},
                {department: 'Science', female: 5}, {department: 'Social Studies', female: 7},
                {department: 'ICT', female: 3}, {department: 'Languages', female: 8},
            ];

            var ms = makeBar('Male Staff', 'male', 0x0073B7, maleData);
            var fs = makeBar('Female Staff', 'female', 0xDD4B39, femaleData);

            var legend = chart.children.push(
                am5.Legend.new(root, {centerX: am5.p50, x: am5.p50, marginTop: 8})
            );
            legend.data.setAll([ms, fs]);
            chart.appear(1000, 100);
        })();

        /* ── 5. PIE — Subject Performance BECE Level ─────────────────────── */
        (function () {
            var root = safeRoot('polarOptions');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var chart = root.container.children.push(
                am5percent.PieChart.new(root, {
                    layout: root.verticalLayout,
                    paddingTop: 10, paddingBottom: 0,
                })
            );

            var series = chart.series.push(
                am5percent.PieSeries.new(root, {
                    categoryField: 'subject', valueField: 'performance',
                    alignLabels: true,
                    tooltip: am5.Tooltip.new(root, {labelText: '{category}: {value}%'}),
                })
            );
            series.slices.template.setAll({strokeWidth: 2, stroke: am5.color(0xffffff)});
            series.get('colors').set('colors', [
                am5.color(0x0073B7), am5.color(0x00A65A), am5.color(0xF39C12),
                am5.color(0x605CA8), am5.color(0x00C0EF), am5.color(0xDD4B39),
            ]);

            series.data.setAll([
                {subject: 'Mathematics', performance: 82},
                {subject: 'English', performance: 85},
                {subject: 'Science', performance: 79},
                {subject: 'Social Studies', performance: 84},
                {subject: 'ICT', performance: 88},
                {subject: 'French', performance: 72},
            ]);

            var legend = chart.children.push(
                am5.Legend.new(root, {centerX: am5.p50, x: am5.p50, marginTop: 10})
            );
            legend.labels.template.setAll({fontSize: 11});
            legend.data.setAll(series.dataItems);
            series.appear(1000, 100);
            chart.appear(1000, 100);
        })();

        /* ── 6. FORCE DIRECTED — School Org Structure ───────────────────── */
        (function () {
            var root = safeRoot('forceDirectedTreeOption');
            if (!root) return;
            root.setThemes([am5themes_Animated.new(root)]);

            var zc = root.container.children.push(
                am5.ZoomableContainer.new(root, {
                    width: am5.p100, height: am5.p100,
                    wheelable: true, pinchZoom: true,
                })
            );
            zc.children.push(am5.ZoomTools.new(root, {target: zc}));

            var series = zc.contents.children.push(
                am5hierarchy.ForceDirected.new(root, {
                    singleBranchOnly: false,
                    downDepth: 2, initialDepth: 2,
                    nodePadding: 20,
                    maxRadius: 40, minRadius: 15,
                    valueField: 'value',
                    categoryField: 'name',
                    childDataField: 'children',
                })
            );
            series.labels.template.setAll({fontSize: 10, fontWeight: '600'});
            series.circles.template.setAll({strokeWidth: 2, stroke: am5.color(0xffffff)});

            series.data.setAll([{
                name: 'Headmaster', value: 100,
                children: [
                    {
                        name: 'Academics', value: 80,
                        children: [
                            {name: 'Mathematics', value: 60}, {name: 'English', value: 60},
                            {name: 'Science', value: 60}, {name: 'Social Studies', value: 60},
                        ],
                    },
                    {
                        name: 'Administration', value: 70,
                        children: [
                            {name: 'Finance', value: 50}, {name: 'HR', value: 50},
                            {name: 'Records', value: 50},
                        ],
                    },
                    {
                        name: 'Student Affairs', value: 60,
                        children: [
                            {name: 'Guidance', value: 40}, {name: 'Discipline', value: 40},
                            {name: 'Sports', value: 40},
                        ],
                    },
                ],
            }]);

            series.set('selectedDataItem', series.dataItems[0]);
            series.appear(1000, 100);
        })();

    }); // end am5.ready
}