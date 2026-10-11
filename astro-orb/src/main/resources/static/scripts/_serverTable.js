// _serverTable.js — scripts/_serverTable.js
//
// A DataTable that asks the server for one page of rows at a time.
//
// The student and staff tables used to download every record and let DataTables page, search and sort
// them in the browser - tens of megabytes for a big school before the first row appeared. With this the
// browser asks for 25 rows; paging, searching and sorting are done by the server (from its in-memory
// lists), so the table opens at once however many records the school has.
//
// Copy / CSV / PDF / Print still export everything that matches the current search and filters, not just
// the page on screen: they fetch the remaining pages (200 rows at a time) before exporting.
//
// Usage:
//   var table = ServerTable.create({
//       table: "#studentRecordsTable",          // the <table> (its <thead> has one <th> per column)
//       endpoint: "getStudentsPage",             // POST; answers {total, filtered, page, size, rows}
//       columns: [{data: "studentId", sort: "studentId"}, ...],
//       params: function () { return {status: "Active"}; },   // extra filters sent with every request
//       order: [[1, "asc"]],
//       title: "Student Records",                // heading and file name of the exports
//       onResult: function (result, firstLoad) {},
//       onDraw: function () {}
//   });
//   table.reload(true);      // fetch again, staying on the same page
//   table.exportAll("csv");  // "copy" | "csv" | "pdf" | "print"
//   table.destroy();
//
// Column options: data (field name, or null for a button column), sort (the server's name for sorting by
// this column; leave out for columns that cannot be sorted), render (function(value, row) returning HTML -
// escape anything that came from the server with ServerTable.esc), className,
// exportable (false to leave a column out of the exports), exportText (function(row) returning the text).
(function (global) {
    "use strict";

    var EXPORT_PAGE_SIZE = 200;
    var MAX_EXPORT_PAGES = 500;

    function esc(value) {
        return String(value === undefined || value === null ? "" : value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function fileSafe(name) {
        return String(name || "export").replace(/[^A-Za-z0-9_-]+/g, "_");
    }

    function message(title, text, type) {
        if (global.swal) global.swal({title: title, text: text, type: type});
        else global.alert(title + ": " + text);
    }

    function create(cfg) {
        var $ = global.jQuery;
        var $table = $(cfg.table);
        var columns = cfg.columns;
        var title = cfg.title || "Export";
        var firstLoad = true;
        var headings = [];
        $table.find("thead th").each(function () {
            headings.push($(this).text().trim());
        });

        // A table built earlier for this page: take it down first
        if ($.fn.DataTable.isDataTable($table)) $table.DataTable().destroy();
        $table.find("tbody").empty();

        function buildPayload(request) {
            var payload = $.extend({}, cfg.params ? cfg.params() : {});
            payload.page = Math.floor(request.start / request.length);
            payload.size = request.length;
            payload.search = request.search ? request.search.value : "";
            var order = request.order && request.order[0];
            var column = order ? columns[order.column] : null;
            if (column && column.sort) {
                payload.sortBy = column.sort;
                payload.sortDir = order.dir;
            }
            return payload;
        }

        var dtColumns = columns.map(function (c) {
            return {
                data: c.data,
                orderable: !!c.sort,
                className: c.className || "",
                defaultContent: "",
                render: c.render
                    ? function (value, type, row) {
                        return type === "display" ? c.render(value, row) : value;
                    }
                    : function (value, type) {
                        return type === "display" ? esc(value) : value;
                    }
            };
        });

        var api = $table.DataTable({
            serverSide: true,
            processing: true,
            searchDelay: 400,
            deferRender: true,
            pageLength: cfg.pageLength || 25,
            lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
            order: cfg.order || [],
            dom: cfg.dom || "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tr<'row'<'col-sm-5'i><'col-sm-7'p>>",
            responsive: !!cfg.responsive,
            buttons: [
                {text: "Copy", className: "btn-sm buttons-copy", action: function () { exportAll("copy"); }},
                {text: "CSV", className: "btn-sm buttons-csv", action: function () { exportAll("csv"); }},
                {text: "PDF", className: "btn-sm buttons-pdf", action: function () { exportAll("pdf"); }},
                {text: "Print", className: "btn-sm buttons-print", action: function () { exportAll("print"); }}
            ],
            columns: dtColumns,
            ajax: function (request, callback) {
                global.fetchPost(cfg.endpoint, buildPayload(request)).then(function (result) {
                    result = result || {total: 0, filtered: 0, rows: []};
                    callback({
                        draw: request.draw,
                        recordsTotal: result.total || 0,
                        recordsFiltered: result.filtered || 0,
                        data: result.rows || []
                    });
                    var first = firstLoad;
                    firstLoad = false;
                    if (cfg.onResult) cfg.onResult(result, first);
                }).catch(function (error) {
                    console.error("Could not load " + cfg.endpoint + ":", error);
                    callback({draw: request.draw, recordsTotal: 0, recordsFiltered: 0, data: []});
                    if (cfg.onError) cfg.onError(error);
                    else message("Error", "The list could not be loaded. Try again.", "error");
                });
            },
            drawCallback: function () {
                if (cfg.onDraw) cfg.onDraw();
            }
        });

        // ── Exports: everything that matches, not just the page on screen ──

        function currentRequest(page, size) {
            var order = api.order()[0];
            return buildPayload({
                start: page * size,
                length: size,
                search: {value: api.search()},
                order: order ? [{column: order[0], dir: order[1]}] : []
            });
        }

        async function fetchEverything() {
            var rows = [];
            for (var page = 0; page < MAX_EXPORT_PAGES; page++) {
                var result = await global.fetchPost(cfg.endpoint, currentRequest(page, EXPORT_PAGE_SIZE));
                var got = (result && result.rows) || [];
                rows = rows.concat(got);
                if (got.length === 0 || rows.length >= ((result && result.filtered) || 0)) break;
            }
            return rows;
        }

        function exportColumns() {
            var out = [];
            columns.forEach(function (c, i) {
                if (c.exportable === false || c.data === null && !c.exportText) return;
                out.push({
                    title: headings[i] || c.data || "",
                    text: c.exportText || function (row) {
                        var v = row[c.data];
                        return v === undefined || v === null ? "" : String(v);
                    }
                });
            });
            return out;
        }

        function toCsv(head, body) {
            var line = function (cells) {
                return cells.map(function (v) {
                    return '"' + String(v).replace(/"/g, '""') + '"';
                }).join(",");
            };
            return "﻿" + [line(head)].concat(body.map(line)).join("\r\n");
        }

        function download(text, name, type) {
            var blob = new Blob([text], {type: type});
            var link = global.document.createElement("a");
            link.href = global.URL.createObjectURL(blob);
            link.download = name;
            global.document.body.appendChild(link);
            link.click();
            global.document.body.removeChild(link);
            setTimeout(function () {
                global.URL.revokeObjectURL(link.href);
            }, 1000);
        }

        function copyText(text) {
            if (global.navigator && global.navigator.clipboard && global.navigator.clipboard.writeText) {
                return global.navigator.clipboard.writeText(text);
            }
            return new Promise(function (resolve, reject) {
                var area = global.document.createElement("textarea");
                area.value = text;
                global.document.body.appendChild(area);
                area.select();
                var ok = global.document.execCommand && global.document.execCommand("copy");
                global.document.body.removeChild(area);
                ok ? resolve() : reject(new Error("copy is not available"));
            });
        }

        async function exportAll(kind) {
            // A new window has to be opened straight from the click, before the rows are fetched
            var printWindow = kind === "print" ? global.open("", "_blank") : null;
            try {
                var cols = exportColumns();
                var rows = await fetchEverything();
                var head = cols.map(function (c) { return c.title; });
                var body = rows.map(function (row) {
                    return cols.map(function (c) { return c.text(row); });
                });

                if (kind === "csv") {
                    download(toCsv(head, body), fileSafe(title) + ".csv", "text/csv;charset=utf-8");
                } else if (kind === "copy") {
                    await copyText([head].concat(body).map(function (r) { return r.join("\t"); }).join("\n"));
                    message("Copied", body.length + " rows copied to the clipboard.", "success");
                } else if (kind === "print") {
                    var html = "<html><head><title>" + esc(title) + "</title><style>" +
                        "body{font-family:Arial,sans-serif;font-size:11px}table{border-collapse:collapse;width:100%}" +
                        "th,td{border:1px solid #999;padding:3px 5px;text-align:left}th{background:#eee}</style></head><body>" +
                        "<h3>" + esc(title) + "</h3><table><thead><tr>" +
                        head.map(function (h) { return "<th>" + esc(h) + "</th>"; }).join("") + "</tr></thead><tbody>" +
                        body.map(function (r) {
                            return "<tr>" + r.map(function (v) { return "<td>" + esc(v) + "</td>"; }).join("") + "</tr>";
                        }).join("") + "</tbody></table></body></html>";
                    if (!printWindow) throw new Error("The print window was blocked by the browser");
                    printWindow.document.open();
                    printWindow.document.write(html);
                    printWindow.document.close();
                    printWindow.focus();
                    printWindow.print();
                } else if (kind === "pdf") {
                    if (!global.ensurePdfMake) throw new Error("PDF export is not available");
                    var pdfMake = await global.ensurePdfMake();
                    var definition = {
                        pageSize: "A4",
                        pageOrientation: cols.length > 7 ? "landscape" : "portrait",
                        content: [
                            {text: title, style: "title", margin: [0, 0, 0, 12]},
                            {
                                table: {
                                    headerRows: 1,
                                    body: [head.map(function (h) { return {text: h, style: "tableHeader"}; })]
                                        .concat(body.map(function (r, i) {
                                            return r.map(function (v) {
                                                return {text: String(v), fillColor: i % 2 ? undefined : "#f3f3f3"};
                                            });
                                        }))
                                },
                                layout: "noBorders"
                            }
                        ],
                        styles: {
                            title: {alignment: "center", fontSize: 15},
                            tableHeader: {bold: true, fontSize: 9, color: "white", fillColor: "#2d4154", alignment: "center"}
                        },
                        defaultStyle: {fontSize: 8}
                    };
                    pdfMake.createPdf(definition).download(fileSafe(title) + ".pdf");
                }
            } catch (error) {
                if (printWindow) printWindow.close();
                console.error("Export failed:", error);
                message("Error", "The export could not be completed. Try again.", "error");
            }
        }

        return {
            table: api,
            reload: function (keepPage) {
                api.ajax.reload(null, !keepPage);
            },
            exportAll: exportAll,
            destroy: function () {
                api.destroy();
                $table.find("tbody").empty();
            }
        };
    }

    global.ServerTable = {create: create, esc: esc};
})(window);
