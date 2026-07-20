/**
 * _financeBilling.js  —  Data & logic layer for the Student Billing System.
 *
 * Loaded at runtime by financeBilling.js.
 * All API calls use the existing `fetchPost` helper already on the page.
 *
 * Sequence:
 *   1. fetchInstitution()        → institution object → cached as billingData.institution
 *                                   Classes, classGroups, terms, years derived from
 *                                   this single response — NO extra server calls.
 *   2. getLookUpByType(ClassGroup) → class groups (lookup only, as before)
 *   3. get-bills-by-institution  → bill list (general + specific)
 *   4. (on group change) filterClassesByGroupText() → classes from institution object using group text
 *   5. (on class change) fetchStudentsForClass() → students in that class
 *   6. (on confirm)     submitBilling()          → bill-students-by-institution
 */

(function () {
    "use strict";

    // ─── Resolve institution code from the page-level instId variable ─────────
    var _rawInst = (typeof instId !== "undefined" ? instId : "").split(",")[0];
    var _instCode = _rawInst.replace(/[\[\]']+/g, "").replace(/\//g, "");

    // ─── STATE ────────────────────────────────────────────────────────────────
    window.billingState = {
        institutionCode: _instCode,
        selectedGroup: "",
        selectedGroupText: "",
        selectedClass: "",
        selectedTerm: "1st Term",
        selectedYear: "",
        selectedStudents: new Set(),
        selectedBills: new Set(),
        selectAllChecked: false,
        searchQuery: "",
        _studentCache: {},       // className → student[] (no repeat fetches)
    };

    // ─── DATA SKELETON ────────────────────────────────────────────────────────
    window.billingData = {
        institution: null,   // raw institution object from API
        classGroups: [],     // [{ id, name }]  ← from getLookUpByType
        classesList: [],     // [{ name, classGroup }]  ← from institution, filtered by group
        classes: {},         // { name: { className, students[] } }
        billSections: {
            general: {name: "General Bills", icon: "fas fa-file-invoice-dollar", bills: []},
            specific: {name: "Specific Bills", icon: "fas fa-clipboard-list", bills: []},
        },
        terms: [],     // [{ value, label }]  ← derived from institution
        academicYears: [],     // ["2022/2023", …]    ← generated locally
        _classGroupsLoaded: false,  // UI polls this flag before rendering
    };

    // ─── SPLASH ───────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== "undefined") {
            $(".splash").css({display: "block", background: "#ffffff3d"}).find("h1, p").remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== "undefined") $(".splash").css("display", "none");
    }

    // ─── ACADEMIC YEAR GENERATOR (local, no API) ──────────────────────────────
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

    // ─── DERIVE TERMS from institution data ───────────────────────────────────
    function deriveTerms(institution) {
        if (Array.isArray(institution.terms) && institution.terms.length) {
            return institution.terms.map(function (t) {
                if (typeof t === "string") return {value: t, label: t};
                return {
                    value: t.value || t.term || t.name || t,
                    label: t.label || t.term || t.name || t,
                };
            });
        }

        return [
            {value: "1st Term", label: "1st Term (Sep – Dec)"},
            {value: "2nd Term", label: "2nd Term (Jan – Apr)"},
            {value: "3rd Term", label: "3rd Term (May – Aug)"},
        ];
    }

    // ─── MAPPERS ──────────────────────────────────────────────────────────────
    function mapApiBill(item, index) {
        var name = item.bill_Name || item.billName || item.name || "";
        return {
            id: name || item.billId || item.id || ("BILL-" + index), // name is the ID
            name: name,
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

    // ─── 1. INITIAL LOAD ──────────────────────────────────────────────────────
    async function fetchInitialData() {
        try {
            showSplash();
            buildAcademicYears();

            var [institution, groups, bills] = await Promise.all([
                fetchPost("getInstitutionByCode", {val: _instCode}),
                fetchPost("getLookUpByType", {val: "ClassGroup"}),
                fetchPost("get-bills-by-institution", {val: _instCode}),
            ]);

            if (institution) {
                window.billingData.institution = institution;
                window.billingData.terms = deriveTerms(institution);
                window.billingState.selectedTerm = window.billingData.terms[0]
                    ? window.billingData.terms[0].value
                    : "1st Term";

                // Extract classes from institution object
                extractClassesFromInstitution(institution);
            }

            processClassGroups(groups);
            processBills(bills);
            window.billingData._classGroupsLoaded = true;
            hideSplash();

        } catch (err) {
            hideSplash();
            window.billingData._classGroupsLoaded = true;
            console.error("[_financeBilling] fetchInitialData failed:", err);
        }
    }

    // ─── EXTRACT CLASSES FROM INSTITUTION OBJECT ──────────────────────────────
    function extractClassesFromInstitution(institution) {
        var classesArray = [];

        if (institution.classes && Array.isArray(institution.classes)) {
            classesArray = institution.classes;
        } else if (institution.classList && Array.isArray(institution.classList)) {
            classesArray = institution.classList;
        } else if (institution.courses && Array.isArray(institution.courses)) {
            classesArray = institution.courses;
        }

        window.billingData.classesList = classesArray.map(function (cls) {
            return {
                name: cls.name || cls.className || cls.class,
                classGroup: cls.classGroup || cls.group || cls.category || "",
            };
        }).filter(function (cls) {
            return cls.name;
        });

        // Also populate classes object skeleton
        window.billingData.classesList.forEach(function (cls) {
            if (!window.billingData.classes[cls.name]) {
                window.billingData.classes[cls.name] = {
                    className: cls.name,
                    classGroup: cls.classGroup,
                    students: []
                };
            }
        });
    }

    function processClassGroups(data) {
        if (!Array.isArray(data)) return;
        window.billingData.classGroups = data.map(function (d) {
            return {id: d.id, name: d.name};
        });
    }

    function processBills(data) {
        if (!Array.isArray(data)) return;
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

    // ─── 2. FILTER CLASSES BY GROUP TEXT (NO SERVER CALL) ────────────────────
    window.filterClassesByGroupText = function (groupText) {
        if (!groupText) {
            return window.billingData.classesList;
        }

        var filtered = window.billingData.classesList.filter(function (cls) {
            if (cls.classGroup && cls.classGroup.toLowerCase() === groupText.toLowerCase()) {
                return true;
            }
            if (cls.name.toLowerCase().includes(groupText.toLowerCase())) {
                return true;
            }
            return false;
        });

        return filtered;
    };

    // ─── 3. GET CLASSES FOR A GROUP (used by UI) ──────────────────────────────
    window.getClassesByGroupText = function (groupText) {
        var filtered = window.filterClassesByGroupText(groupText);

        var newClasses = {};
        filtered.forEach(function (cls) {
            if (window.billingData.classes[cls.name]) {
                newClasses[cls.name] = window.billingData.classes[cls.name];
            } else {
                newClasses[cls.name] = {
                    className: cls.name,
                    classGroup: cls.classGroup,
                    students: []
                };
            }
        });

        // Preserve existing student data for classes already fetched
        Object.keys(window.billingData.classes).forEach(function (className) {
            if (newClasses[className] && window.billingData.classes[className]) {
                newClasses[className].students = window.billingData.classes[className].students || [];
            }
        });

        window.billingData.classes = newClasses;
        return filtered;
    };

    // ─── 4. FETCH STUDENTS FOR A CLASS (server call) ─────────────────────────
    window.fetchStudentsForClass = async function (className) {
        if (window.billingState._studentCache[className]) {
            if (window.billingData.classes[className]) {
                window.billingData.classes[className].students =
                    window.billingState._studentCache[className];
            }
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

    // ─── HELPERS: split selected bills by category ───────────────────────────
    function splitBillsByCategory() {
        var generalBillNames = [];
        var specificBillNames = [];
        var selected = window.billingState.selectedBills;

        // general section
        (window.billingData.billSections.general.bills || []).forEach(function (bill) {
            if (selected.has(bill.id)) generalBillNames.push(bill.name);
        });

        // specific section
        (window.billingData.billSections.specific.bills || []).forEach(function (bill) {
            if (selected.has(bill.id)) specificBillNames.push(bill.name);
        });

        return {generalBillNames: generalBillNames, specificBillNames: specificBillNames};
    }

    // ─── HELPERS: get ALL student IDs in the loaded class ────────────────────
    function getAllClassStudentIds() {
        var classData = window.billingData.classes[window.billingState.selectedClass];
        if (!classData || !classData.students || !classData.students.length) {
            throw new Error(
                "No students loaded for class [" + window.billingState.selectedClass + "]. " +
                "Please select the class from the Class filter first so students are loaded into memory."
            );
        }
        return classData.students.map(function (s) {
            return s.id;
        });
    }

    // ─── 5. SUBMIT BILLING ────────────────────────────────────────────────────
    // Rules:
    //   General bills  → post ALL students in the loaded class
    //   Specific bills → post ONLY manually selected students
    //   Both mixed     → two separate posts (General first, then Specific)
    window.submitBilling = async function () {
        var split = splitBillsByCategory();
        var hasGeneral = split.generalBillNames.length > 0;
        var hasSpecific = split.specificBillNames.length > 0;

        // Guard: at least one bill must be selected
        if (!hasGeneral && !hasSpecific) {
            throw new Error("No bills selected.");
        }

        // Guard: specific bills require at least one student manually selected
        if (hasSpecific && window.billingState.selectedStudents.size === 0) {
            throw new Error(
                "Specific bills require at least one student to be selected manually."
            );
        }

        var basePayload = {
            term: window.billingState.selectedTerm,
            studentClass: window.billingState.selectedClass,
            academicYear: window.billingState.selectedYear,
            institutionCode: _instCode,
        };

        var results = [];

        try {
            showSplash();

            // ── POST 1: General bills → all students in the class ─────────────
            if (hasGeneral) {
                var allStudentIds = getAllClassStudentIds(); // throws if class not loaded
                var generalPayload = Object.assign({}, basePayload, {
                    studentId: allStudentIds,
                    billname: split.generalBillNames,
                });
                console.log("[_financeBilling] General billing payload:", generalPayload);
                var generalResult = await fetchPost("bill-students-by-institution", generalPayload);
                results.push({type: "general", result: generalResult});
            }

            // ── POST 2: Specific bills → manually selected students only ──────
            if (hasSpecific) {
                var specificPayload = Object.assign({}, basePayload, {
                    studentId: Array.from(window.billingState.selectedStudents),
                    billname: split.specificBillNames,
                });
                console.log("[_financeBilling] Specific billing payload:", specificPayload);
                var specificResult = await fetchPost("bill-students-by-institution", specificPayload);
                results.push({type: "specific", result: specificResult});
            }

            hideSplash();

            // Build summary message
            var summaryParts = [];
            if (hasGeneral) summaryParts.push(split.generalBillNames.length + " general bill(s) applied to entire class");
            if (hasSpecific) summaryParts.push(split.specificBillNames.length + " specific bill(s) applied to " + window.billingState.selectedStudents.size + " selected student(s)");

            if (typeof swal === "function") {
                swal({title: "Success!", text: summaryParts.join("\n"), type: "success"});
            }

            // Return combined results so handleProcess can build the invoice
            return results;

        } catch (err) {
            hideSplash();
            console.error("[_financeBilling] submitBilling failed:", err);
            throw err;
        }
    };

    // ─── 6. FETCH EXISTING BILLINGS ───────────────────────────────────────────
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

    // ─── HELPERS ──────────────────────────────────────────────────────────────

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