/**
 * _financeBilling.js  —  Data & logic layer for the Student Billing System.
 *
 * Loaded at runtime by financeBilling.js.
 * All API calls use the existing `fetchPost` helper already on the page.
 *
 * Sequence:
 *   1. fetchLookup()             → class groups  → billingData.classGroups
 *   2. fetchInstitutionBills()   → bill list      → billingData.billSections
 *   3. (on group change) fetchClassesByGroup()  → billingData.classes
 *   4. (on class change) fetchStudentsForClass() → billingData.classes[name].students
 *   5. (on confirm)     submitBilling()          → bill-students-by-institution
 */

(function () {
    "use strict";

    // ─── Institution code (same extraction as original file) ─────────────────
    var _rawInst = (typeof instId !== "undefined" ? instId : "").split(",")[0];
    var _instCode = _rawInst.replace(/[\[\]']+/g, "").replace(/\//g, "");

    // ─── STATE ────────────────────────────────────────────────────────────────
    window.billingState = {
        institutionCode: _instCode,
        selectedGroup: "",
        selectedClass: "",
        selectedTerm: "1st Term",
        selectedYear: "",
        selectedStudents: new Set(),
        selectedBills: new Set(),
        selectAllChecked: false,
        searchQuery: "",
        _studentCache: {},     // className → student array
    };

    // ─── DATA SKELETON ────────────────────────────────────────────────────────
    window.billingData = {
        classGroups: [],           // [{ id, name }]
        classes: {},           // { "ClassName": { className, students:[] } }
        billSections: {
            general: {
                name: "General Bills",
                icon: "fas fa-file-invoice-dollar",
                bills: [],
            },
            specific: {
                name: "Specific Bills",
                icon: "fas fa-clipboard-list",
                bills: [],
            },
        },
        terms: [
            {value: "1st Term", label: "1st Term (Sep – Dec)"},
            {value: "2nd Term", label: "2nd Term (Jan – Apr)"},
            {value: "3rd Term", label: "3rd Term (May – Aug)"},
        ],
        academicYears: [],
        _classGroupsLoaded: false, // UI polls this flag
    };

    // ─── ACADEMIC YEAR GENERATOR ──────────────────────────────────────────────
    function buildAcademicYears() {
        var currentYear = new Date().getFullYear();
        var years = [];
        for (var i = 4; i >= 0; i--) {
            var s = currentYear - i;
            years.push(s + "/" + (s + 1));
        }
        window.billingData.academicYears = years;
        window.billingState.selectedYear = years[years.length - 1];
    }

    // ─── MAPPERS ──────────────────────────────────────────────────────────────
    function mapApiBill(item, index) {
        return {
            id: item.billId || item.id || ("BILL-" + index),
            name: item.bill_Name || item.billName || item.name || "",
            price: parseFloat(item.bill_Amount || item.amount || item.price || 0),
            isMandatory: !!(item.isMandatory || item.mandatory || false),
            _raw: item,
        };
    }

    function mapApiStudent(item) {
        return {
            id: item.studentId || item.id || "",
            name: [item.lastName, item.firstName, item.otherName]
                    .filter(Boolean).join(" ").trim()
                || item.name || item.studentName || "",
            email: item.email || item.emailAddress || "",
            parentPhone: item.parentPhone || item.phoneNumber || item.phone || "",
            _raw: item,
        };
    }

    // ─── SPLASH ───────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== "undefined") {
            $(".splash")
                .css({display: "block", background: "#ffffff3d"})
                .find("h1, p").remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== "undefined") $(".splash").css("display", "none");
    }

    // ─── 1. FETCH CLASS GROUPS + BILLS (initial load) ─────────────────────────
    async function fetchInitialData() {
        try {
            showSplash();
            buildAcademicYears();

            // Run class-group fetch and bills fetch in parallel
            var instCode = _instCode;
            var [groups, bills] = await Promise.all([
                fetchPost("getLookUpByType", {val: "ClassGroup"}),
                fetchPost("get-bills-by-institution", {val: instCode}),
            ]);

            processClassGroups(groups);
            processBills(bills);

            window.billingData._classGroupsLoaded = true;
            hideSplash();
            console.log("[_financeBilling] Initial data loaded.", window.billingData);

        } catch (err) {
            hideSplash();
            window.billingData._classGroupsLoaded = true; // unblock UI
            console.error("[_financeBilling] fetchInitialData failed:", err);
        }
    }

    function processClassGroups(data) {
        if (!Array.isArray(data)) return;
        window.billingData.classGroups = data.map(function (d) {
            return {id: d.id, name: d.name};
        });
    }

    function processBills(data) {
        if (!Array.isArray(data)) return;
        // Clear before repopulating
        window.billingData.billSections.general.bills = [];
        window.billingData.billSections.specific.bills = [];

        data.forEach(function (item, i) {
            var bill = mapApiBill(item, i);
            var cat = (item.bill_Cat || item.category || "").toLowerCase();
            if (cat === "general") {
                window.billingData.billSections.general.bills.push(bill);
            } else {
                window.billingData.billSections.specific.bills.push(bill);
            }
        });
    }

    // ─── 2. FETCH CLASSES BY GROUP (on group dropdown change) ─────────────────
    window.fetchClassesByGroup = async function (groupId) {
        try {
            showSplash();
            // Reset classes whenever the group changes
            window.billingData.classes = {};
            window.billingState._studentCache = {};

            var result = await fetchPost("getInstitutionClassesByClassGroup", {
                institution: _instCode,
                classGroup: groupId,
            });

            if (Array.isArray(result)) {
                result.forEach(function (d) {
                    var name = d.name || d.className || "";
                    if (!name) return;
                    window.billingData.classes[name] = {
                        className: name,
                        students: [],
                    };
                });
            }

            hideSplash();
        } catch (err) {
            hideSplash();
            console.error("[_financeBilling] fetchClassesByGroup failed:", err);
        }
    };

    // ─── 3. FETCH STUDENTS FOR A CLASS (lazy, cached) ─────────────────────────
    window.fetchStudentsForClass = async function (className) {
        // Return cache hit
        if (window.billingState._studentCache[className]) {
            window.billingData.classes[className].students =
                window.billingState._studentCache[className];
            return;
        }

        try {
            showSplash();
            var result = await fetchPost("getSkimpStudentsByClass", {
                institutionCode: _instCode,
                studentClass: className,
                dateOfAdmission: "",
                denomination: "",
                dateOfBirth: "",
                nationality: "",
                studentId: "",
                gender: "",
                status: "",
            });

            var mapped = (result || []).map(mapApiStudent);
            if (!window.billingData.classes[className]) {
                window.billingData.classes[className] = {className: className, students: []};
            }
            window.billingData.classes[className].students = mapped;
            window.billingState._studentCache[className] = mapped;
            hideSplash();

        } catch (err) {
            hideSplash();
            console.error("[_financeBilling] fetchStudentsForClass failed:", err);
        }
    };

    // ─── 4. SUBMIT BILLING ────────────────────────────────────────────────────
    window.submitBilling = async function () {
        try {
            showSplash();
            var payload = {
                term: window.billingState.selectedTerm,
                studentClass: window.billingState.selectedClass,
                studentId: Array.from(window.billingState.selectedStudents),
                billname: Array.from(window.billingState.selectedBills),
                academicYear: window.billingState.selectedYear,
                institutionCode: _instCode,
            };
            var result = await fetchPost("bill-students-by-institution", payload);
            hideSplash();

            if (typeof swal === "function") {
                swal({title: "Success!", text: "Billing submitted successfully.", type: "success"});
            }
            return result;

        } catch (err) {
            hideSplash();
            console.error("[_financeBilling] submitBilling failed:", err);
            throw err;
        }
    };

    // ─── 5. FETCH EXISTING BILLINGS (billing history table) ───────────────────
    window.fetchExistingBillings = async function (className, term) {
        try {
            showSplash();
            var result = await fetchPost("get-billing-by-institutionClass", {
                institutionCode: _instCode,
                studentId: "",
                studentClass: className || window.billingState.selectedClass,
                term: term || window.billingState.selectedTerm,
            });
            hideSplash();
            return result || [];
        } catch (err) {
            hideSplash();
            console.error("[_financeBilling] fetchExistingBillings failed:", err);
            return [];
        }
    };

    // ─── HELPERS (used by financeBilling.js renderer) ────────────────────────

    window.filterStudentsBySearch = function (students) {
        var q = (window.billingState.searchQuery || "").toLowerCase().trim();
        if (!q) return students;
        return students.filter(function (s) {
            return s.name.toLowerCase().includes(q) || s.id.toLowerCase().includes(q);
        });
    };

    window.calculateTotalAmount = function () {
        var total = 0;
        var selected = window.billingState.selectedBills;
        Object.values(window.billingData.billSections).forEach(function (section) {
            section.bills.forEach(function (bill) {
                if (selected.has(bill.id)) total += (bill.price || 0);
            });
        });
        return total;
    };

    window.getSelectedStudentsDetails = function () {
        var classData = window.billingData.classes[window.billingState.selectedClass];
        if (!classData) return [];
        return (classData.students || []).filter(function (s) {
            return window.billingState.selectedStudents.has(s.id);
        });
    };

    window.getSelectedBillsDetails = function () {
        var results = [];
        var selected = window.billingState.selectedBills;
        Object.values(window.billingData.billSections).forEach(function (section) {
            section.bills.forEach(function (bill) {
                if (selected.has(bill.id)) results.push(bill);
            });
        });
        return results;
    };

    window.buildPostPayload = function () {
        return {
            institutionCode: _instCode,
            studentClass: window.billingState.selectedClass,
            term: window.billingState.selectedTerm,
            academicYear: window.billingState.selectedYear,
            studentId: Array.from(window.billingState.selectedStudents),
            billname: Array.from(window.billingState.selectedBills),
        };
    };

    // ─── KICK OFF ─────────────────────────────────────────────────────────────
    if (window.copyrights) window.copyrights();

    fetchInitialData();

})();