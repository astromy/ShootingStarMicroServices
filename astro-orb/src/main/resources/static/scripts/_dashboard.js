// _dashboard.js — scripts/_dashboard.js
// Fetches real data and renders charts based on the role set by dashboard.js

(function () {

    var scriptEl = document.querySelector("script[data-dashboard-role]");
    var role = scriptEl ? scriptEl.getAttribute("data-dashboard-role") : "admin";
    var instCode = scriptEl ? scriptEl.getAttribute("data-dashboard-inst") : "";
    var staffId = scriptEl ? scriptEl.getAttribute("data-dashboard-staff") : "";

    // ── Helpers ────────────────────────────────────────────────────────────────

    function setStat(id, value) {
        var el = document.getElementById(id);
        if (el) el.textContent = value;
    }

    function fmt(n) {
        if (n === undefined || n === null) return "—";
        if (typeof n === "number") return n.toLocaleString();
        return n;
    }

    function safeRoot(id) {
        var el = document.getElementById(id);
        if (!el) return null;
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

    function makeDonut(id, title, data, valueField, categoryField) {
        var root = safeRoot(id);
        if (!root) return;
        root.setThemes([am5themes_Animated.new(root)]);
        var chart = root.container.children.push(
            am5percent.PieChart.new(root, {innerRadius: am5.percent(50), layout: root.horizontalLayout})
        );
        var series = chart.series.push(
            am5percent.PieSeries.new(root, {valueField: valueField, categoryField: categoryField, alignLabels: false})
        );
        series.labels.template.setAll({fontSize: 11, text: "{category}: {value}", radius: 5});
        series.data.setAll(data);
        var legend = chart.children.push(am5.Legend.new(root, {
            centerY: am5.percent(50),
            y: am5.percent(50),
            layout: root.verticalLayout
        }));
        legend.data.setAll(series.dataItems);
    }

    function makeBar(id, categories, seriesData, categoryField) {
        var root = safeRoot(id);
        if (!root) return;
        root.setThemes([am5themes_Animated.new(root)]);
        var chart = root.container.children.push(
            am5xy.XYChart.new(root, {panX: false, panY: false, layout: root.verticalLayout})
        );
        var xRenderer = am5xy.AxisRendererX.new(root, {minGridDistance: 20});
        xRenderer.labels.template.setAll({fontSize: 11, rotation: -30, centerY: am5.p50, centerX: am5.p100});
        var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {categoryField: categoryField, renderer: xRenderer}));
        var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {renderer: am5xy.AxisRendererY.new(root, {})}));
        xAxis.data.setAll(categories);

        seriesData.forEach(function (s) {
            var series = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: s.name,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: s.field,
                    categoryXField: categoryField
                })
            );
            series.columns.template.setAll({tooltipText: "{name}: {valueY}", width: am5.percent(80)});
            series.data.setAll(categories);
        });

        chart.set("scrollbarX", am5.Scrollbar.new(root, {orientation: "horizontal"}));
        var legend = chart.children.push(am5.Legend.new(root, {}));
        legend.data.setAll(chart.series.values);
    }

    function makeLine(id, data, categoryField, valueField, title) {
        var root = safeRoot(id);
        if (!root) return;
        root.setThemes([am5themes_Animated.new(root)]);
        var chart = root.container.children.push(am5xy.XYChart.new(root, {panX: true, panY: false}));
        var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
            categoryField: categoryField,
            renderer: am5xy.AxisRendererX.new(root, {minGridDistance: 30})
        }));
        var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {renderer: am5xy.AxisRendererY.new(root, {})}));
        var series = chart.series.push(am5xy.LineSeries.new(root, {
            name: title, xAxis: xAxis, yAxis: yAxis,
            valueYField: valueField, categoryXField: categoryField,
            tooltip: am5.Tooltip.new(root, {labelText: "{valueY}"})
        }));
        series.strokes.template.setAll({strokeWidth: 2});
        series.bullets.push(function () {
            return am5.Bullet.new(root, {sprite: am5.Circle.new(root, {radius: 4, fill: series.get("fill")})});
        });
        xAxis.data.setAll(data);
        series.data.setAll(data);
    }

    // ── Summary numbers ────────────────────────────────────────────────────────
    // The dashboards used to download every student, staff member, bill and payment
    // (tens of megabytes) and count them in the browser. The server now counts them
    // from its in-memory lists and sends back only the numbers.

    function loadSummary(parts) {
        return fetchPost("dashboard-summary", {parts: parts});
    }

    // [{label, count}] -> [{<labelKey>: label, count}] for the charts
    function asRows(list, labelKey) {
        return (list || []).map(function (x) {
            var row = {count: x.count};
            row[labelKey] = x.label;
            return row;
        });
    }

    // A figure for a stat card, or "—" when that part could not be loaded
    function figure(part, value) {
        return part ? fmt(value || 0) : fmt(undefined);
    }

    // ── Role dispatchers ───────────────────────────────────────────────────────

    am5.ready(function () {
        switch (role) {
            case "superadmin":
                loadSuperadmin();
                break;
            case "admin":
                loadAdmin();
                break;
            case "teacher":
                loadTeacher();
                break;
            case "finance":
                loadFinance();
                break;
            case "hr":
                loadHR();
                break;
            case "stores":
                loadStores();
                break;
            case "health":
                loadHealth();
                break;
            default:
                loadAdmin();
        }
    });

    // ══════════════════════════════════════════════════════════════════════════
    // SUPERADMIN — Executive Dashboard
    // One request returns the student, staff and finance numbers
    // ══════════════════════════════════════════════════════════════════════════
    async function loadSuperadmin() {
        try {
            var summary = await loadSummary(["students", "staff", "finance"]);
            var students = summary.students, staff = summary.staff, finance = summary.finance;

            // ── Stat cards ────────────────────────────────────────────────────
            setStat("stat-students", figure(students, students && students.active));
            setStat("stat-staff", figure(staff, staff && staff.total));
            setStat("stat-fees-collected", finance ? "₵" + Math.round(finance.totalCollected || 0).toLocaleString() : fmt(undefined));
            setStat("stat-outstanding", figure(finance, finance && finance.studentsOwing));
            setStat("stat-classes", figure(students, students && students.byClass.length));
            setStat("stat-depts", figure(staff, staff && staff.byDepartment.length));

            // ── Charts ────────────────────────────────────────────────────────
            if (students) {
                // 1. Students by Class — large bar (col-8), biggest class first
                var classData = asRows(students.byClass, "class");
                makeBar("chart-students-by-class", classData, [{name: "Students", field: "count"}], "class");

                // 2. Gender Distribution — donut (col-4)
                makeDonut("chart-gender-dist", "Gender", asRows(students.byGender, "gender"), "count", "gender");

                // 5. Student Status Distribution — donut (col-4)
                makeDonut("chart-student-status", "Status", asRows(students.byStatus, "status"), "count", "status");

                // 6. Enrollment Trend — line chart by class sorted by size (col-6)
                makeLine("chart-enrollment-trend", classData.slice(0, 15), "class", "count", "Students");

                // 7. New Admissions — this year by month, or all years if none yet this year (col-6)
                var admData = asRows(students.admissionsByMonth, "month");
                if (admData.length === 0) admData = asRows(students.admissionsByYear, "month");
                makeBar("chart-new-admissions", admData, [{name: "Admissions", field: "count"}], "month");
            }

            // 3. Staff by Department — donut (col-4)
            if (staff) makeDonut("chart-staff-by-dept", "Dept", asRows(staff.byDepartment, "dept"), "count", "dept");

            // 4. Fee Collection by Class — bar (col-4)
            if (finance && finance.collectedByClass && finance.collectedByClass.length > 0) {
                var feeClassData = finance.collectedByClass.map(function (x) {
                    return {class: x.label, amount: Math.round(x.amount)};
                });
                makeBar("chart-fee-by-class", feeClassData, [{name: "Amount (₵)", field: "amount"}], "class");
            }

            if (summary.errors) console.warn("Some dashboard numbers could not be loaded:", summary.errors);
        } catch (e) {
            console.error("Superadmin dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN
    // ══════════════════════════════════════════════════════════════════════════
    async function loadAdmin() {
        try {
            var [summary, applicants] = await Promise.all([
                loadSummary(["students"]),
                fetchPost("getAllApplicants", {val: instCode}),
            ]);
            var students = summary.students;
            if (!students) throw new Error("Student numbers are not available");

            setStat("stat-students", fmt(students.active));
            setStat("stat-suspended", fmt(students.suspended));
            setStat("stat-applicants", fmt((applicants || []).length));

            // Admissions this term (dateOfAdmission in the current year)
            setStat("stat-admissions", fmt(students.newAdmissionsThisYear));

            // Students by class
            makeBar("chart-students-by-class", asRows(students.byClass, "class"), [{name: "Students", field: "count"}], "class");

            // Gender distribution
            makeDonut("chart-gender-dist", "Gender", asRows(students.byGender, "gender"), "count", "gender");

            // Status distribution
            makeDonut("chart-enrollment-status", "Status", asRows(students.byStatus, "status"), "count", "status");

            // Students by residential locality (top 10)
            makeBar("chart-locality", asRows(students.topLocalities, "locality"), [{name: "Students", field: "count"}], "locality");

        } catch (e) {
            console.error("Admin dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TEACHER
    // ══════════════════════════════════════════════════════════════════════════
    async function loadTeacher() {
        try {
            var staffProfile = await fetchPost("getStaffByStaffId", {val: staffId});
            var subjects = staffProfile.staffSubjectsResponseList || [];
            var myClasses = [...new Set(subjects.map(function (s) {
                return s.classId || s.studentClass;
            }).filter(Boolean))];

            setStat("stat-subjects", fmt(subjects.length));

            // Fetch students for each class this teacher handles
            var allStudents = [];
            if (myClasses.length > 0) {
                var studentResults = await Promise.all(
                    myClasses.map(function (cls) {
                        return fetchPost("getStudentsByDynamicData", {
                            key: ["institutionCode", "studentClass"],
                            val: [instCode, cls]
                        }).catch(function () {
                            return [];
                        });
                    })
                );
                studentResults.forEach(function (r) {
                    allStudents = allStudents.concat(r || []);
                });
            }
            setStat("stat-students", fmt(allStudents.length));

            // Questions in bank for teacher's subjects
            var qCount = 0;
            if (subjects.length > 0) {
                try {
                    var qResults = await Promise.all(
                        subjects.slice(0, 3).map(function (s) {
                            return fetchPost("fetchQuestionsByInstAndClassAndSubj", {
                                institutionCode: instCode,
                                classId: s.classId || s.studentClass || '',
                                subjectId: s.subjectName || s.name || ''
                            }).catch(function () {
                                return [];
                            });
                        })
                    );
                    qResults.forEach(function (r) {
                        qCount += (r || []).length;
                    });
                } catch (e) {
                }
            }
            setStat("stat-questions", fmt(qCount));
            setStat("stat-avg-score", "—");

            // My subjects donut
            var subjData = subjects.map(function (s) {
                return {subject: s.subjectName || s.name || "Subject", count: 1};
            });
            if (subjData.length > 0) makeDonut("chart-my-subjects", "Subjects", subjData, "count", "subject");

            // Students per class bar
            var byClass = {};
            allStudents.forEach(function (s) {
                var c = s.studentClass || "Unknown";
                byClass[c] = (byClass[c] || 0) + 1;
            });
            var classData = Object.keys(byClass).map(function (k) {
                return {class: k, count: byClass[k]};
            });
            if (classData.length > 0) makeBar("chart-students-per-class", classData, [{
                name: "Students",
                field: "count"
            }], "class");

            // Gender distribution of teacher's students
            var genderData = [
                {
                    gender: "Male", count: allStudents.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "male";
                    }).length
                },
                {
                    gender: "Female", count: allStudents.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "female";
                    }).length
                },
            ];
            makeDonut("chart-score-dist", "Student Gender", genderData, "count", "gender");

        } catch (e) {
            console.error("Teacher dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FINANCE
    // ══════════════════════════════════════════════════════════════════════════
    async function loadFinance() {
        try {
            var summary = await loadSummary(["finance", "staff"]);
            var finance = summary.finance, staff = summary.staff;

            // Total collected
            setStat("stat-fees-collected", finance ? "₵" + fmt(Math.round(finance.totalCollected || 0)) : fmt(undefined));
            setStat("stat-bills", figure(finance, finance && finance.billCount));
            setStat("stat-payroll", figure(staff, staff && staff.total));

            // Students who still owe
            setStat("stat-outstanding", figure(finance, finance && finance.studentsOwing));

            if (finance) {
                // Fee collection by class
                if (finance.collectedByClass && finance.collectedByClass.length > 0) {
                    makeBar("chart-fee-by-class", finance.collectedByClass.map(function (x) {
                        return {class: x.label, amount: Math.round(x.amount)};
                    }), [{name: "Amount (₵)", field: "amount"}], "class");
                }

                // Owing vs paid donut
                var owing = finance.studentsOwing || 0;
                makeDonut("chart-owing-paid", "Bill Status", [
                    {status: "Paid", count: Math.max(0, (finance.studentsBilled || 0) - owing)},
                    {status: "Owing", count: owing},
                ], "count", "status");

                // Payment methods
                var methodData = asRows(finance.paymentMethods, "method");
                if (methodData.length > 0) makeDonut("chart-payment-methods", "Methods", methodData, "count", "method");
            }

            // Staff by department (for salary distribution proxy)
            if (staff) {
                var deptData = asRows(staff.byDepartment, "dept");
                if (deptData.length > 0) makeBar("chart-salary-dist", deptData, [{name: "Staff", field: "count"}], "dept");
            }

        } catch (e) {
            console.error("Finance dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HR
    // ══════════════════════════════════════════════════════════════════════════
    async function loadHR() {
        try {
            var summary = await loadSummary(["staff"]);
            var staff = summary.staff;
            if (!staff) throw new Error("Staff numbers are not available");

            setStat("stat-staff", fmt(staff.total));
            setStat("stat-on-leave", "—");
            setStat("stat-appraisals", "—");

            // Designations
            setStat("stat-designations", fmt(staff.designationCount));

            // Staff by department
            makeBar("chart-staff-by-dept", asRows(staff.byDepartment, "dept"), [{name: "Staff", field: "count"}], "dept");

            // Gender ratio
            makeDonut("chart-staff-gender", "Gender", asRows(staff.byGender, "gender"), "count", "gender");

            // Designation distribution
            makeDonut("chart-designation", "Designation", asRows(staff.byDesignation, "desig"), "count", "desig");

            // Leave by type — placeholder until leave endpoint added
            var leaveData = [
                {type: "Annual", count: 0},
                {type: "Sick", count: 0},
                {type: "Maternity", count: 0},
            ];
            makeDonut("chart-leave-type", "Leave Type", leaveData, "count", "type");

        } catch (e) {
            console.error("HR dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STORES
    // ══════════════════════════════════════════════════════════════════════════
    async function loadStores() {
        try {
            var [allItems, lowStock, movements, orders] = await Promise.all([
                fetchPost("stores/items/get-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("stores/items/low-stock/" + instCode, null).catch(function () {
                    return [];
                }),
                fetchPost("stores/movements/" + instCode, null).catch(function () {
                    return [];
                }),
                fetchPost("stores/orders/get-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
            ]);

            setStat("stat-items", fmt((allItems || []).length));
            setStat("stat-low-stock", fmt((lowStock || []).length));
            setStat("stat-orders", fmt((orders || []).filter(function (o) {
                return o.status === "PENDING";
            }).length));
            setStat("stat-movements", fmt((movements || []).length));

            // Stock by category
            var catMap = {};
            (allItems || []).forEach(function (i) {
                var c = i.category || "Uncategorised";
                catMap[c] = (catMap[c] || 0) + (i.quantity || i.stockLevel || 1);
            });
            var catData = Object.keys(catMap).map(function (k) {
                return {category: k, qty: catMap[k]};
            });
            if (catData.length > 0) makeBar("chart-stock-levels", catData, [{
                name: "Quantity",
                field: "qty"
            }], "category");

            // Order status
            var orderMap = {};
            (orders || []).forEach(function (o) {
                var s = o.status || "Unknown";
                orderMap[s] = (orderMap[s] || 0) + 1;
            });
            var orderData = Object.keys(orderMap).map(function (k) {
                return {status: k, count: orderMap[k]};
            });
            if (orderData.length > 0) makeDonut("chart-order-status", "Orders", orderData, "count", "status");

            // Low stock items bar
            var lowData = (lowStock || []).slice(0, 10).map(function (i) {
                return {name: i.itemName || i.name || "Item", qty: i.quantity || i.stockLevel || 0};
            });
            if (lowData.length > 0) makeBar("chart-low-stock", lowData, [{name: "Qty", field: "qty"}], "name");

            // Movements trend
            var moveMap = {};
            (movements || []).forEach(function (m) {
                var d = (m.movementDate || m.date || "").substring(0, 7); // YYYY-MM
                moveMap[d] = (moveMap[d] || 0) + 1;
            });
            var moveData = Object.keys(moveMap).sort().map(function (k) {
                return {month: k, count: moveMap[k]};
            });
            if (moveData.length > 0) makeLine("chart-movements", moveData, "month", "count", "Movements");

        } catch (e) {
            console.error("Stores dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HEALTH — placeholder (no health endpoints found yet)
    // ══════════════════════════════════════════════════════════════════════════
    async function loadHealth() {
        setStat("stat-records", "—");
        setStat("stat-visits", "—");
        setStat("stat-referrals", "—");
        setStat("stat-treatments", "—");
        // Charts will render empty until health endpoints are available
        makeDonut("chart-visit-type", "Visit Type", [], "count", "type");
        makeLine("chart-visit-trend", [], "month", "count", "Visits");
        makeDonut("chart-diagnoses", "Diagnoses", [], "count", "name");
    }

})();