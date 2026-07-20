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
    // Fetches from students, staff and finance in parallel
    // ══════════════════════════════════════════════════════════════════════════
    async function loadSuperadmin() {
        try {
            // Fetch students, staff and finance data in parallel
            var [students, staff, payments, bills] = await Promise.all([
                fetchPost("getStudentsByInstitution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("get-staff-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("get-billPayments-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("get-bills-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
            ]);

            students = students || [];
            staff = staff || [];
            payments = payments || [];
            bills = bills || [];

            // ── Stat cards ────────────────────────────────────────────────────
            var activeStudents = students.filter(function (s) {
                return (s.status || '').toLowerCase() === "active";
            });
            setStat("stat-students", fmt(activeStudents.length));
            setStat("stat-staff", fmt(staff.length));

            // Total fees collected this term
            var totalCollected = payments.reduce(function (sum, p) {
                return sum + parseFloat(p.amountPaid || p.amount || 0);
            }, 0);
            setStat("stat-fees-collected", "₵" + fmt(Math.round(totalCollected).toLocaleString()));

            // Outstanding bills
            var owing = bills.filter(function (b) {
                return parseFloat(b.outstandingBalance || b.outstanding || 0) > 0;
            });
            setStat("stat-outstanding", fmt(owing.length));

            // Classes and departments
            var byClass = {};
            students.forEach(function (s) {
                var c = s.studentClass || "Unknown";
                byClass[c] = (byClass[c] || 0) + 1;
            });
            setStat("stat-classes", fmt(Object.keys(byClass).length));

            var byDept = {};
            staff.forEach(function (s) {
                var d = s.department || "Unknown";
                byDept[d] = (byDept[d] || 0) + 1;
            });
            setStat("stat-depts", fmt(Object.keys(byDept).length));

            // ── Charts ────────────────────────────────────────────────────────

            // 1. Students by Class — large bar (col-8)
            var classData = Object.keys(byClass)
                .sort(function (a, b) {
                    return byClass[b] - byClass[a];
                })
                .map(function (k) {
                    return {class: k, count: byClass[k]};
                });
            makeBar("chart-students-by-class", classData, [{name: "Students", field: "count"}], "class");

            // 2. Gender Distribution — donut (col-4)
            var genderData = [
                {
                    gender: "Male", count: students.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "male";
                    }).length
                },
                {
                    gender: "Female", count: students.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "female";
                    }).length
                },
            ];
            makeDonut("chart-gender-dist", "Gender", genderData, "count", "gender");

            // 3. Staff by Department — donut (col-4)
            var deptData = Object.keys(byDept).map(function (k) {
                return {dept: k, count: byDept[k]};
            });
            makeDonut("chart-staff-by-dept", "Dept", deptData, "count", "dept");

            // 4. Fee Collection by Class — bar (col-4)
            var feeByClass = {};
            payments.forEach(function (p) {
                var c = p.studentClass || "Unknown";
                feeByClass[c] = (feeByClass[c] || 0) + parseFloat(p.amountPaid || p.amount || 0);
            });
            var feeClassData = Object.keys(feeByClass)
                .sort(function (a, b) {
                    return feeByClass[b] - feeByClass[a];
                })
                .map(function (k) {
                    return {class: k, amount: Math.round(feeByClass[k])};
                });
            if (feeClassData.length > 0) {
                makeBar("chart-fee-by-class", feeClassData, [{name: "Amount (₵)", field: "amount"}], "class");
            }

            // 5. Student Status Distribution — donut (col-4)
            var statusMap = {};
            students.forEach(function (s) {
                var st = s.status || "Unknown";
                statusMap[st] = (statusMap[st] || 0) + 1;
            });
            var statusData = Object.keys(statusMap).map(function (k) {
                return {status: k, count: statusMap[k]};
            });
            makeDonut("chart-student-status", "Status", statusData, "count", "status");

            // 6. Enrollment Trend — line chart by class sorted by size (col-6)
            var trendData = classData.slice(0, 15); // top 15 classes
            makeLine("chart-enrollment-trend", trendData, "class", "count", "Students");

            // 7. New Admissions This Term — bar by month (col-6)
            var thisYear = new Date().getFullYear().toString();
            var monthMap = {};
            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            students.forEach(function (s) {
                var doa = s.dateOfAdmission || '';
                if (doa.startsWith(thisYear)) {
                    var m = parseInt(doa.split("-")[1], 10) - 1;
                    var label = monthNames[m] || doa.substring(0, 7);
                    monthMap[label] = (monthMap[label] || 0) + 1;
                }
            });
            var admData = monthNames
                .filter(function (m) {
                    return monthMap[m];
                })
                .map(function (m) {
                    return {month: m, count: monthMap[m]};
                });
            if (admData.length > 0) {
                makeBar("chart-new-admissions", admData, [{name: "Admissions", field: "count"}], "month");
            } else {
                // No admissions this year yet — show all-time by year
                var yearMap = {};
                students.forEach(function (s) {
                    var yr = (s.dateOfAdmission || '').substring(0, 4);
                    if (yr) yearMap[yr] = (yearMap[yr] || 0) + 1;
                });
                var yrData = Object.keys(yearMap).sort()
                    .map(function (k) {
                        return {month: k, count: yearMap[k]};
                    });
                makeBar("chart-new-admissions", yrData, [{name: "Admissions", field: "count"}], "month");
            }

        } catch (e) {
            console.error("Superadmin dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN
    // ══════════════════════════════════════════════════════════════════════════
    async function loadAdmin() {
        try {
            var [students, applicants] = await Promise.all([
                fetchPost("getStudentsByInstitution", {val: instCode}),
                fetchPost("getAllApplicants", {val: instCode}),
            ]);

            var active = students.filter(function (s) {
                return (s.status || '').toLowerCase() === "active";
            });
            var suspended = students.filter(function (s) {
                return (s.status || '').toLowerCase() === "suspended";
            });

            setStat("stat-students", fmt(active.length));
            setStat("stat-suspended", fmt(suspended.length));
            setStat("stat-applicants", fmt(applicants.length));

            // Admissions this term (dateOfAdmission in current year)
            var thisYear = new Date().getFullYear().toString();
            var newAdm = students.filter(function (s) {
                return (s.dateOfAdmission || '').startsWith(thisYear);
            });
            setStat("stat-admissions", fmt(newAdm.length));

            // Students by class
            var byClass = {};
            students.forEach(function (s) {
                var c = s.studentClass || "Unknown";
                byClass[c] = (byClass[c] || 0) + 1;
            });
            var classData = Object.keys(byClass).map(function (k) {
                return {class: k, count: byClass[k]};
            });
            makeBar("chart-students-by-class", classData, [{name: "Students", field: "count"}], "class");

            // Gender distribution
            var genderData = [
                {
                    gender: "Male", count: students.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "male";
                    }).length
                },
                {
                    gender: "Female", count: students.filter(function (s) {
                        return (s.gender || '').toLowerCase() === "female";
                    }).length
                },
            ];
            makeDonut("chart-gender-dist", "Gender", genderData, "count", "gender");

            // Status distribution
            var statusMap = {};
            students.forEach(function (s) {
                var st = s.status || "Unknown";
                statusMap[st] = (statusMap[st] || 0) + 1;
            });
            var statusData = Object.keys(statusMap).map(function (k) {
                return {status: k, count: statusMap[k]};
            });
            makeDonut("chart-enrollment-status", "Status", statusData, "count", "status");

            // Students by residential locality
            var locMap = {};
            students.forEach(function (s) {
                var loc = (s.residentialLocality || "Unknown").trim();
                locMap[loc] = (locMap[loc] || 0) + 1;
            });
            var locData = Object.keys(locMap)
                .sort(function (a, b) {
                    return locMap[b] - locMap[a];
                })
                .slice(0, 10)
                .map(function (k) {
                    return {locality: k, count: locMap[k]};
                });
            makeBar("chart-locality", locData, [{name: "Students", field: "count"}], "locality");

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
            var [bills, payments, staff] = await Promise.all([
                fetchPost("get-bills-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("get-billPayments-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
                fetchPost("get-staff-by-institution", {val: instCode}).catch(function () {
                    return [];
                }),
            ]);

            // Total collected
            var totalCollected = (payments || []).reduce(function (sum, p) {
                return sum + (parseFloat(p.amountPaid || p.amount || 0));
            }, 0);
            setStat("stat-fees-collected", "₵" + fmt(Math.round(totalCollected)));
            setStat("stat-bills", fmt((bills || []).length));
            setStat("stat-payroll", fmt((staff || []).length));

            // Owing students
            var owing = (bills || []).filter(function (b) {
                return parseFloat(b.outstandingBalance || b.outstanding || 0) > 0;
            });
            setStat("stat-outstanding", fmt(owing.length));

            // Fee collection by class
            var byClass = {};
            (payments || []).forEach(function (p) {
                var c = p.studentClass || "Unknown";
                byClass[c] = (byClass[c] || 0) + parseFloat(p.amountPaid || p.amount || 0);
            });
            var classData = Object.keys(byClass).map(function (k) {
                return {class: k, amount: Math.round(byClass[k])};
            });
            if (classData.length > 0) makeBar("chart-fee-by-class", classData, [{
                name: "Amount (₵)",
                field: "amount"
            }], "class");

            // Owing vs paid donut
            var owingData = [
                {status: "Paid", count: (bills || []).length - owing.length},
                {status: "Owing", count: owing.length},
            ];
            makeDonut("chart-owing-paid", "Bill Status", owingData, "count", "status");

            // Payment methods
            var methodMap = {};
            (payments || []).forEach(function (p) {
                var m = p.paymentMethod || p.method || "Cash";
                methodMap[m] = (methodMap[m] || 0) + 1;
            });
            var methodData = Object.keys(methodMap).map(function (k) {
                return {method: k, count: methodMap[k]};
            });
            if (methodData.length > 0) makeDonut("chart-payment-methods", "Methods", methodData, "count", "method");

            // Staff by department (for salary distribution proxy)
            var deptMap = {};
            (staff || []).forEach(function (s) {
                var d = s.department || "Unknown";
                deptMap[d] = (deptMap[d] || 0) + 1;
            });
            var deptData = Object.keys(deptMap).map(function (k) {
                return {dept: k, count: deptMap[k]};
            });
            if (deptData.length > 0) makeBar("chart-salary-dist", deptData, [{name: "Staff", field: "count"}], "dept");

        } catch (e) {
            console.error("Finance dashboard error:", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HR
    // ══════════════════════════════════════════════════════════════════════════
    async function loadHR() {
        try {
            var staff = await fetchPost("get-staff-by-institution", {val: instCode}).catch(function () {
                return [];
            });

            setStat("stat-staff", fmt((staff || []).length));
            setStat("stat-on-leave", "—");
            setStat("stat-appraisals", "—");

            // Designations
            var designations = [...new Set((staff || []).map(function (s) {
                return s.designation;
            }).filter(Boolean))];
            setStat("stat-designations", fmt(designations.length));

            // Staff by department
            var deptMap = {};
            (staff || []).forEach(function (s) {
                var d = s.department || "Unknown";
                deptMap[d] = (deptMap[d] || 0) + 1;
            });
            var deptData = Object.keys(deptMap).map(function (k) {
                return {dept: k, count: deptMap[k]};
            });
            makeBar("chart-staff-by-dept", deptData, [{name: "Staff", field: "count"}], "dept");

            // Gender ratio
            var genderData = [
                {
                    gender: "Male", count: (staff || []).filter(function (s) {
                        return (s.gender || '').toLowerCase() === "male";
                    }).length
                },
                {
                    gender: "Female", count: (staff || []).filter(function (s) {
                        return (s.gender || '').toLowerCase() === "female";
                    }).length
                },
            ];
            makeDonut("chart-staff-gender", "Gender", genderData, "count", "gender");

            // Designation distribution
            var desigMap = {};
            (staff || []).forEach(function (s) {
                var d = s.designation || "Unknown";
                desigMap[d] = (desigMap[d] || 0) + 1;
            });
            var desigData = Object.keys(desigMap).map(function (k) {
                return {desig: k, count: desigMap[k]};
            });
            makeDonut("chart-designation", "Designation", desigData, "count", "desig");

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