document.getElementById('wrapper').innerHTML = `
<div class="normalheader transition animated fadeIn">
  <div class="hpanel">
    <div class="panel-body">
      <a class="small-header-action" href="#">
        <div class="clip-header">
          <i class="fa fa-arrow-up"></i>
        </div>
      </a>

      <div id="hbreadcrumb" class="pull-right m-t-lg">
        <ol class="hbreadcrumb breadcrumb">
          <li>Dashboard</li>
          <li>
            <span>Charts</span>
          </li>
          <li class="active">
            <span>Basic School Dashboard</span>
          </li>
        </ol>
      </div>
      <h2 class="font-light m-b-xs">📊 Basic School Admin Dashboard</h2>
      <small>Track student performance, enrollment, and staff metrics across all departments</small>
    </div>
  </div>
</div>

<div class="content animate-panel">
  <!-- Summary Statistics Row -->
  <div class="row">
    <div class="col-lg-3">
      <div class="hpanel stats">
        <div class="panel-body h-100">
          <div class="stats-title pull-left">
            <h4>Total Students</h4>
          </div>
          <div class="stats-icon pull-right">
            <i class="fa fa-users fa-2x text-success"></i>
          </div>
          <div class="m-t-xl">
            <h1 class="font-extra-bold">1,284</h1>
            <small>+12% from last term</small>
          </div>
        </div>
      </div>
    </div>
    <div class="col-lg-3">
      <div class="hpanel stats">
        <div class="panel-body h-100">
          <div class="stats-title pull-left">
            <h4>Total Staff</h4>
          </div>
          <div class="stats-icon pull-right">
            <i class="fa fa-chalkboard-teacher fa-2x text-info"></i>
          </div>
          <div class="m-t-xl">
            <h1 class="font-extra-bold">86</h1>
            <small>Teaching: 72, Non-teaching: 14</small>
          </div>
        </div>
      </div>
    </div>
    <div class="col-lg-3">
      <div class="hpanel stats">
        <div class="panel-body h-100">
          <div class="stats-title pull-left">
            <h4>Pass Rate</h4>
          </div>
          <div class="stats-icon pull-right">
            <i class="fa fa-graduation-cap fa-2x text-warning"></i>
          </div>
          <div class="m-t-xl">
            <h1 class="font-extra-bold">87.5%</h1>
            <small>Target: 90% by next term</small>
          </div>
        </div>
      </div>
    </div>
    <div class="col-lg-3">
      <div class="hpanel stats">
        <div class="panel-body h-100">
          <div class="stats-title pull-left">
            <h4>Fee Collection</h4>
          </div>
          <div class="stats-icon pull-right">
            <i class="fa fa-cedi-sign fa-2x text-danger"></i>
          </div>
          <div class="m-t-xl">
            <h1 class="font-extra-bold">₵284K</h1>
            <small>78% of target</small>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <!-- Radar Chart: Subject Performance by Class -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          🎯 Subject Performance by Class (BECE Preparation)
        </div>
        <div class="panel-body">
          <div id="radarChart" style="height: 400px;"></div>
        </div>
      </div>
    </div>

    <!-- Doughnut Chart: Grade Distribution -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          📈 Overall Grade Distribution (BECE Standards)
        </div>
        <div class="panel-body">
          <div id="doughnutChart" style="height: 400px;"></div>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <!-- Line Chart: Enrollment Trends -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          📅 Student Enrollment Trends (2019-2024)
        </div>
        <div class="panel-body">
          <div id="lineOptions" style="height: 400px;"></div>
        </div>
      </div>
    </div>

    <!-- Bar Chart: Staff Distribution -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          👨‍🏫 Staff Distribution by Department & Gender
        </div>
        <div class="panel-body">
          <div id="barOptions" style="height: 400px;"></div>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <!-- Pie Chart: Subject Performance -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          📚 Subject Performance (BECE Level)
        </div>
        <div class="panel-body">
          <div id="polarOptions" style="height: 400px;"></div>
        </div>
      </div>
    </div>

    <!-- Hierarchical Chart: Department Structure -->
    <div class="col-lg-6">
      <div class="hpanel">
        <div class="panel-heading">
          <div class="panel-tools">
            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
            <a class="closebox"><i class="fa fa-times"></i></a>
          </div>
          🏫 School Organizational Structure
        </div>
        <div class="panel-body">
          <div id="forceDirectedTreeOption" style="height: 400px;"></div>
        </div>
      </div>
    </div>
  </div>
</div>
`;

// Initialize all charts after DOM is ready
setTimeout(() => {
    initializeBasicSchoolCharts();
}, 100);

function initializeBasicSchoolCharts() {
    // Radar Chart - Subject Performance by Class (BECE preparation)
    if (document.getElementById('radarChart')) {
        const radarRoot = am5.Root.new("radarChart");
        radarRoot.setThemes([am5themes_Animated.new(radarRoot)]);

        const radarChart = radarRoot.container.children.push(
            am5radar.RadarChart.new(radarRoot, {
                panX: false,
                panY: false,
                wheelX: "panX",
                wheelY: "zoomX",
                innerRadius: am5.percent(20),
                radius: am5.percent(90)
            })
        );

        const xAxis = radarChart.xAxes.push(
            am5radar.CategoryAxis.new(radarRoot, {
                categoryField: "subject",
                renderer: am5radar.AxisRendererCircular.new(radarRoot, {})
            })
        );

        xAxis.data.setAll([
            {subject: "Mathematics"},
            {subject: "English"},
            {subject: "Science"},
            {subject: "Social Studies"},
            {subject: "ICT"},
            {subject: "French"}
        ]);

        const yAxis = radarChart.yAxes.push(
            am5radar.ValueAxis.new(radarRoot, {
                renderer: am5radar.AxisRendererRadial.new(radarRoot, {})
            })
        );

        // Create series for different classes
        const classNames = ["Primary 4", "Primary 5", "Primary 6", "JHS 1", "JHS 2", "JHS 3"];
        const colors = [0x0073B7, 0x00A65A, 0xF39C12, 0xDD4B39, 0x605CA8, 0x00C0EF];

        classNames.forEach((className, idx) => {
            const series = radarChart.series.push(
                am5radar.RadarLineSeries.new(radarRoot, {
                    name: className,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "score",
                    categoryXField: "subject",
                    fill: am5.color(colors[idx]),
                    stroke: am5.color(colors[idx]),
                    tooltip: am5.Tooltip.new(radarRoot, {
                        labelText: "{name}: {valueY}%"
                    })
                })
            );

            // Sample data - replace with actual data
            series.data.setAll([
                {subject: "Mathematics", score: [85, 82, 78, 75, 72, 70][idx]},
                {subject: "English", score: [88, 85, 82, 80, 78, 75][idx]},
                {subject: "Science", score: [82, 80, 78, 76, 74, 72][idx]},
                {subject: "Social Studies", score: [86, 84, 82, 80, 78, 76][idx]},
                {subject: "ICT", score: [90, 88, 86, 84, 82, 80][idx]},
                {subject: "French", score: [75, 72, 70, 68, 65, 62][idx]}
            ]);
        });

        radarChart.seriesContainer.children.push(
            am5radar.RadarChart.new(radarRoot, {})
        );

        radarChart.set("legend", am5.Legend.new(radarRoot, {centerX: am5.p50, x: am5.p50}));
    }

    // Doughnut Chart - Grade Distribution
    if (document.getElementById('doughnutChart')) {
        const doughnutRoot = am5.Root.new("doughnutChart");
        doughnutRoot.setThemes([am5themes_Animated.new(doughnutRoot)]);

        const doughnutChart = doughnutRoot.container.children.push(
            am5percent.PieChart.new(doughnutRoot, {
                layout: doughnutRoot.verticalLayout,
                innerRadius: am5.percent(50)
            })
        );

        const series = doughnutChart.series.push(
            am5percent.PieSeries.new(doughnutRoot, {
                name: "Grades",
                categoryField: "grade",
                valueField: "count",
                alignLabels: true
            })
        );

        series.data.setAll([
            {grade: "A (80-100%)", count: 245, color: 0x00A65A},
            {grade: "B (70-79%)", count: 386, color: 0x0073B7},
            {grade: "C (60-69%)", count: 312, color: 0xF39C12},
            {grade: "D (50-59%)", count: 198, color: 0xDD4B39},
            {grade: "E (Below 50%)", count: 143, color: 0x932ab6}
        ]);

        series.get("colors").set("colors", [
            am5.color(0x00A65A), am5.color(0x0073B7),
            am5.color(0xF39C12), am5.color(0xDD4B39), am5.color(0x932ab6)
        ]);

        series.appear(1000, 100);
        doughnutChart.set("legend", am5.Legend.new(doughnutRoot, {centerX: am5.p50, x: am5.p50}));
    }

    // Line Chart - Enrollment Trends
    if (document.getElementById('lineOptions')) {
        const lineRoot = am5.Root.new("lineOptions");
        lineRoot.setThemes([am5themes_Animated.new(lineRoot)]);

        const lineChart = lineRoot.container.children.push(
            am5xy.XYChart.new(lineRoot, {
                panX: true,
                panY: true,
                wheelX: "panX",
                wheelY: "zoomX",
                pinchZoomX: true
            })
        );

        const xAxis = lineChart.xAxes.push(
            am5xy.CategoryAxis.new(lineRoot, {
                categoryField: "year",
                renderer: am5xy.AxisRendererX.new(lineRoot, {minGridDistance: 30})
            })
        );

        const yAxis = lineChart.yAxes.push(
            am5xy.ValueAxis.new(lineRoot, {
                renderer: am5xy.AxisRendererY.new(lineRoot, {})
            })
        );

        xAxis.data.setAll([
            {year: "2019"}, {year: "2020"}, {year: "2021"},
            {year: "2022"}, {year: "2023"}, {year: "2024"}
        ]);

        // Boys enrollment
        const boysSeries = lineChart.series.push(
            am5xy.LineSeries.new(lineRoot, {
                name: "Boys",
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: "boys",
                categoryXField: "year",
                tooltip: am5.Tooltip.new(lineRoot, {
                    labelText: "{name}: {valueY}"
                })
            })
        );

        // Girls enrollment
        const girlsSeries = lineChart.series.push(
            am5xy.LineSeries.new(lineRoot, {
                name: "Girls",
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: "girls",
                categoryXField: "year",
                tooltip: am5.Tooltip.new(lineRoot, {
                    labelText: "{name}: {valueY}"
                })
            })
        );

        boysSeries.data.setAll([
            {year: "2019", boys: 520}, {year: "2020", boys: 548},
            {year: "2021", boys: 567}, {year: "2022", boys: 589},
            {year: "2023", boys: 612}, {year: "2024", boys: 645}
        ]);

        girlsSeries.data.setAll([
            {year: "2019", girls: 498}, {year: "2020", girls: 523},
            {year: "2021", girls: 541}, {year: "2022", girls: 567},
            {year: "2023", girls: 589}, {year: "2024", girls: 639}
        ]);

        boysSeries.strokes.template.setAll({strokeWidth: 3});
        girlsSeries.strokes.template.setAll({strokeWidth: 3});

        lineChart.set("legend", am5.Legend.new(lineRoot, {centerX: am5.p50, x: am5.p50}));
        boysSeries.appear(1000);
        girlsSeries.appear(1000);
    }

    // Bar Chart - Staff Distribution
    if (document.getElementById('barOptions')) {
        const barRoot = am5.Root.new("barOptions");
        barRoot.setThemes([am5themes_Animated.new(barRoot)]);

        const barChart = barRoot.container.children.push(
            am5xy.XYChart.new(barRoot, {
                panX: false,
                panY: false,
                layout: barRoot.verticalLayout
            })
        );

        const xAxis = barChart.xAxes.push(
            am5xy.CategoryAxis.new(barRoot, {
                categoryField: "department",
                renderer: am5xy.AxisRendererX.new(barRoot, {minGridDistance: 30})
            })
        );

        const yAxis = barChart.yAxes.push(
            am5xy.ValueAxis.new(barRoot, {
                renderer: am5xy.AxisRendererY.new(barRoot, {})
            })
        );

        xAxis.data.setAll([
            {department: "Mathematics"}, {department: "English"},
            {department: "Science"}, {department: "Social Studies"},
            {department: "ICT"}, {department: "Languages"}
        ]);

        const maleSeries = barChart.series.push(
            am5xy.ColumnSeries.new(barRoot, {
                name: "Male Staff",
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: "male",
                categoryXField: "department",
                tooltip: am5.Tooltip.new(barRoot, {labelText: "Male: {valueY}"})
            })
        );

        const femaleSeries = barChart.series.push(
            am5xy.ColumnSeries.new(barRoot, {
                name: "Female Staff",
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: "female",
                categoryXField: "department",
                tooltip: am5.Tooltip.new(barRoot, {labelText: "Female: {valueY}"})
            })
        );

        maleSeries.data.setAll([
            {department: "Mathematics", male: 8}, {department: "English", male: 5},
            {department: "Science", male: 7}, {department: "Social Studies", male: 6},
            {department: "ICT", male: 4}, {department: "Languages", male: 3}
        ]);

        femaleSeries.data.setAll([
            {department: "Mathematics", female: 6}, {department: "English", female: 9},
            {department: "Science", female: 5}, {department: "Social Studies", female: 7},
            {department: "ICT", female: 3}, {department: "Languages", female: 8}
        ]);

        maleSeries.columns.template.setAll({fill: am5.color(0x0073B7)});
        femaleSeries.columns.template.setAll({fill: am5.color(0xDD4B39)});

        barChart.set("legend", am5.Legend.new(barRoot, {centerX: am5.p50, x: am5.p50}));
    }

    // Pie Chart - Subject Performance (BECE)
    if (document.getElementById('polarOptions')) {
        const polarRoot = am5.Root.new("polarOptions");
        polarRoot.setThemes([am5themes_Animated.new(polarRoot)]);

        const polarChart = polarRoot.container.children.push(
            am5percent.PieChart.new(polarRoot, {
                layout: polarRoot.verticalLayout
            })
        );

        const polarSeries = polarChart.series.push(
            am5percent.PieSeries.new(polarRoot, {
                name: "Subjects",
                categoryField: "subject",
                valueField: "performance",
                alignLabels: true
            })
        );

        polarSeries.data.setAll([
            {subject: "Mathematics", performance: 82, color: 0x0073B7},
            {subject: "English", performance: 85, color: 0x00A65A},
            {subject: "Science", performance: 79, color: 0xF39C12},
            {subject: "Social Studies", performance: 84, color: 0x605CA8},
            {subject: "ICT", performance: 88, color: 0x00C0EF},
            {subject: "French", performance: 72, color: 0xDD4B39}
        ]);

        polarSeries.get("colors").set("colors", [
            am5.color(0x0073B7), am5.color(0x00A65A), am5.color(0xF39C12),
            am5.color(0x605CA8), am5.color(0x00C0EF), am5.color(0xDD4B39)
        ]);

        polarChart.set("legend", am5.Legend.new(polarRoot, {centerX: am5.p50, x: am5.p50}));
    }

    // Force Directed Tree - School Structure
    if (document.getElementById('forceDirectedTreeOption')) {
        const treeRoot = am5.Root.new("forceDirectedTreeOption");
        treeRoot.setThemes([am5themes_Animated.new(treeRoot)]);

        const treeChart = treeRoot.container.children.push(
            am5hierarchy.ForceDirected.new(treeRoot, {
                nodePadding: 10,
                maxRadius: 40,
                minRadius: 15,
                centerX: am5.p50,
                centerY: am5.p50
            })
        );

        treeChart.data.setAll([
            {
                name: "Headmaster",
                value: 100,
                children: [
                    {
                        name: "Academics",
                        value: 80,
                        children: [
                            {name: "Mathematics Dept", value: 60},
                            {name: "English Dept", value: 60},
                            {name: "Science Dept", value: 60},
                            {name: "Social Studies Dept", value: 60}
                        ]
                    },
                    {
                        name: "Administration",
                        value: 70,
                        children: [
                            {name: "Finance", value: 50},
                            {name: "HR", value: 50},
                            {name: "Records", value: 50}
                        ]
                    },
                    {
                        name: "Student Affairs",
                        value: 60,
                        children: [
                            {name: "Guidance", value: 40},
                            {name: "Discipline", value: 40},
                            {name: "Sports", value: 40}
                        ]
                    }
                ]
            }
        ]);

        treeChart.set("legend", am5.Legend.new(treeRoot, {centerX: am5.p50, x: am5.p50}));
    }
}