(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('ScheduleConfigService', ScheduleConfigService);

    ScheduleConfigService.$inject = [
        '$interpolate',
        '$moment',
        '$q',
        '$rootScope',
        '$templateCache',
        '$translate',
        'CONSTANTS',
        'DateTimeService',
        'dialogs'
    ];

    function ScheduleConfigService(
        $interpolate,
        $moment,
        $q,
        $rootScope,
        $templateCache,
        $translate,
        CONSTANTS,
        DateTimeService,
        dialogs) {
        var FIELD = {
                DATE: 'date',
                WEIGHT: 'weight',
                FLIGHT_DATE_START: 'flightDateStart',
                FLIGHT_DATE_END: 'flightDateEnd',
                CLICK_THROUGH_URL: 'clickThroughUrl'
            },
            ICONS = CONSTANTS.SCHEDULE.ICONS,
            SCHEDULE_LEVEL = CONSTANTS.SCHEDULE.LEVEL,

            getEntityRow = function (row) {
                var rowArray = $('.te-tree-grid-delegate').jqxTreeGrid('getRow', row);

                return rowArray[0];
            },

            isBefore = function (startDate, endDate) {
                startDate = getRightDateRendering(startDate);
                endDate = getRightDateRendering(endDate);

                var start = DateTimeService.customParse(startDate, DateTimeService.FORMAT.DATE_TIME_US),
                    end = DateTimeService.customParse(endDate, DateTimeService.FORMAT.DATE_TIME_US);

                return $moment(start).isBefore($moment(end));
            },

            getRightDateRendering = function (dateText) {
                // Expected format for dates: 'MM/dd/yyyy hh:mm:ss tt'
                if (angular.isString(dateText)) {
                    return dateText;
                }
                else {
                    return 'Invalid date';
                }
            },

            removeValidationLabel = function (editor) {
                if (!editor) {
                    return;
                }

                editor.removeClass('jqx-grid-validation-label');
            },

            supportEdit = function (entity, dataField) {
                if (!entity || !dataField) {
                    return false;
                }

                if (dataField === FIELD.CLICK_THROUGH_URL || dataField === FIELD.WEIGHT) {
                    return entity.editSupport && entity.editSupport[dataField];
                }
                else if (dataField === FIELD.DATE) {
                    return entity.editSupport &&
                        (entity.editSupport[FIELD.FLIGHT_DATE_START] || entity.editSupport[FIELD.FLIGHT_DATE_END]);
                }
            },

            deferred = $q.defer(),
            isGridValid = function () {
                return deferred.promise;
            };

        function dispatchUpdateEvent(entityRow) {
            $rootScope.$broadcast('scheduling-update', entityRow);
        }

        return {
            getCheckboxHeaderRenderer: function (submodel) {
                if (submodel !== null) {
                    var template = $templateCache.get('schedule-checkbox-header-renderer.html'),
                        scope = $rootScope.$new(true),
                        displayNone = 'display: none;',
                        displayBlock = 'display: block;',
                        defaultRow = {
                            rowId: null,
                            uncheckedStyle: displayBlock
                        };

                    if (submodel.length > 0) {
                        scope.vm = {
                            rowId: submodel[0].$$uuid,
                            checkedStyle: submodel[0].checked === true ? displayBlock : displayNone,
                            uncheckedStyle: submodel[0].checked === true ? displayNone : displayBlock
                        };
                    }
                    else {
                        scope.vm = defaultRow;
                        template = $templateCache.get('schedule-checkbox-header-disabled-renderer.html');
                    }

                    return $interpolate(template)(scope);
                }
            },

            getCheckboxRenderer: function (row, cellValue) {
                var template = $templateCache.get('schedule-checkbox-renderer.html'),
                    scope = $rootScope.$new(true);

                scope.vm = {
                    rowId: row.$$uuid,
                    checked: cellValue === true
                };

                return $interpolate(template)(scope);
            },

            getClickThroughEditorConf: function () {
                var isCTUrlDialogOpen = false,
                    editorCtrl;

                return {
                    columnType: 'template',
                    createEditor: function (row, cellvalue, editor) {
                        var inputElement = $('<input/>').prependTo(editor);

                        inputElement.jqxInput({
                            width: '99%',
                            height: '99%'
                        });
                    },

                    initEditor: function (row, cellvalue, editor) {
                        // set the editor's current value. The callback is called each time the editor is displayed.
                        var inputField = editor.find('input'),
                            value = angular.isArray(cellvalue) ? cellvalue.join(',') : cellvalue,
                            entityRow = getEntityRow(row),
                            dlg,
                            template;

                        editorCtrl = editor;
                        isCTUrlDialogOpen = false;
                        removeValidationLabel(editor);
                        inputField.val(value);

                        if (entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.ZIP ||
                            entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.HTML5) {
                            isCTUrlDialogOpen = true;
                            inputField.val('(' + entityRow.clickThroughUrl.length + ') ' +
                                entityRow.clickThroughUrl[0] || cellvalue);
                            template = 'app/cm/campaigns/schedule-tab/schedule-insertions/schedule-edit-click-through' +
                                '/schedule-edit-click-through.html';
                            dlg = dialogs.create(template, 'ScheduleEditClickThroughController as vm',
                                {
                                    propertiesModel: {
                                        clickThrough: entityRow.clickThroughUrl,
                                        mainTitle: entityRow.creativeName || entityRow.creativeLabel
                                    }
                                },
                                {
                                    size: 'md',
                                    keyboard: true,
                                    key: false,
                                    backdrop: 'static'
                                });

                            dlg.result.then(
                                function (response) {
                                    isCTUrlDialogOpen = false;
                                    if (response.status && response.cturls !== null) {
                                        entityRow.clickThroughUrl = response.cturls;
                                        inputField.val(entityRow.clickThroughUrl[0]);
                                        dispatchUpdateEvent(entityRow);
                                        deferred.notify(true);
                                    }

                                    angular.element('.jqx-input').trigger($.Event('keydown', {
                                        keyCode: 27
                                    }));
                                });

                            return dlg;
                        }
                    },

                    getEditorValue: function (row, cellvalue, editor) {
                        var entityRow = getEntityRow(row);

                        // return the editor's value.
                        if (supportEdit(entityRow, FIELD.CLICK_THROUGH_URL)) {
                            dispatchUpdateEvent(entityRow);

                            return editor.find('input').val().split(',');
                        }
                    },

                    validation: function (cell, value) {
                        if (isCTUrlDialogOpen) {
                            editorCtrl.addClass('validation-ctrls');
                            return false;
                        }

                        var entityRow = getEntityRow(cell.row),
                            pattern = new RegExp(CONSTANTS.REGEX.URL, 'i'),
                            urls,
                            msgKey,
                            i;

                        if (!supportEdit(entityRow, FIELD.CLICK_THROUGH_URL)) {
                            deferred.notify(true);

                            return true;
                        }

                        if (!value) {
                            deferred.notify(false);
                            return {
                                message: $translate.instant('validation.error.required'),
                                result: false
                            };
                        }

                        urls = value;
                        if (entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.ZIP ||
                            entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.HTML5) {
                            msgKey = 'validation.error.invalidURLs';
                        }
                        else {
                            urls = [];
                            urls.push(value.join(','));
                            msgKey = 'validation.error.invalidURL';
                        }

                        for (i = 0; i < urls.length; i++) {
                            // Check if well formed URL
                            if (!pattern.test(urls[i])) {
                                deferred.notify(false);
                                return {
                                    message: $translate.instant(msgKey),
                                    result: false
                                };
                            }
                        }

                        deferred.notify(true);
                        return true;
                    }
                };
            },

            getDateEditorConf: function () {
                return {
                    columnType: 'template',
                    createEditor: function (row, cellvalue, editor) {
                        // construct the editor.
                        editor.jqxDateTimeInput({
                            formatString: CONSTANTS.DATE.JQX.DATE_TIME_US,
                            width: '100%',
                            height: '100%',
                            textAlign: 'center'
                        });
                    },

                    initEditor: function (row, cellvalue, editor) {
                        // set the editor's current value. The callback is called each time the editor is displayed.
                        cellvalue = getRightDateRendering(cellvalue);
                        removeValidationLabel(editor);
                        editor.jqxDateTimeInput('value', new Date(cellvalue));
                    },

                    getEditorValue: function (row, cellvalue, editor) {
                        var entityRow = getEntityRow(row),
                            editorValue = editor.val();

                        // return the editor's value.
                        if (cellvalue || supportEdit(entityRow, FIELD.DATE)) {
                            dispatchUpdateEvent(entityRow);

                            return editorValue ? editorValue : undefined;
                        }
                    },

                    validation: function (cell, value) {
                        var rowEntity = getEntityRow(cell.row);

                        if (value || supportEdit(rowEntity, FIELD.DATE)) {
                            // Check if start date greater than end date
                            if (cell.datafield === FIELD.FLIGHT_DATE_START &&
                                !isBefore(value, rowEntity.flightDateEnd)) {
                                deferred.notify(false);
                                return {
                                    message: $translate.instant('validation.error.wrongStartDate'),
                                    result: false
                                };
                            }
                            else if (cell.datafield === FIELD.FLIGHT_DATE_END &&
                                !isBefore(rowEntity.flightDateStart, value)) {
                                deferred.notify(false);
                                return {
                                    message: $translate.instant('validation.error.wrongEndDate'),
                                    result: false
                                };
                            }
                        }

                        deferred.notify(true);
                        return true;
                    }
                };
            },

            getDateRenderer: function (cellValue) {
                if (cellValue === '\u2014' || cellValue === '') {
                    return cellValue;
                }

                return getRightDateRendering(cellValue);
            },

            getCTRenderer: function (row, cellValue) {
                var ctUrlCount = '',
                    entityRow = getEntityRow(row);

                if (cellValue.length >= 1 && (entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.ZIP ||
                    entityRow.creativeType === CONSTANTS.CREATIVE.FILE_TYPE.HTML5)) {
                    ctUrlCount = '(' + cellValue.length + ')';
                    cellValue = cellValue[0];
                }

                return angular.isDefined(cellValue) && cellValue !== '' ?
                    '<span class="text-nowrap" title="' + cellValue + '">' + ctUrlCount + ' ' + cellValue + '</span>' :
                    cellValue;
            },

            getRowRenderer: function (rowData, cellValue) {
                var template = $templateCache.get('schedule-row-renderer.html'),
                    scope = $rootScope.$new(true),
                    iconClass;

                switch (rowData.field) {
                    case SCHEDULE_LEVEL.SITE.KEY:
                        iconClass = 'site fa ' + ICONS.SITE;
                        break;
                    case SCHEDULE_LEVEL.PLACEMENT.KEY:
                        iconClass = 'placement fa ' + ICONS.PLACEMENT;
                        break;

                    case SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                        iconClass = 'creative-group fa ' + ICONS.CREATIVE_GROUP;
                        break;

                    case SCHEDULE_LEVEL.SCHEDULE.KEY:
                        iconClass = 'creative fa ' + ICONS.CREATIVE;
                        break;

                    default:
                        iconClass = '';
                        break;
                }

                scope.vm = {
                    field: rowData.field,
                    label: cellValue,
                    class: iconClass
                };

                return $interpolate(template)(scope);
            },

            getWeightEditorConf: function () {
                return {
                    columnType: 'template',
                    createEditor: function (row, cellvalue, editor) {
                        // construct the editor.
                        editor.jqxNumberInput({
                            inputMode: 'simple',
                            spinButtons: true,
                            decimalDigits: 0,
                            width: '100%',
                            height: '100%'
                        });
                    },

                    initEditor: function (row, cellvalue, editor) {
                        // set the editor's current value. The callback is called each time the editor is displayed.
                        var value = parseInt(cellvalue);

                        if (isNaN(value)) {
                            value = 0;
                        }

                        removeValidationLabel(editor);
                        editor.jqxNumberInput('val', value);
                    },

                    getEditorValue: function (row, cellvalue, editor) {
                        var entityRow = getEntityRow(row);

                        // return the editor's value.
                        if (supportEdit(entityRow, FIELD.WEIGHT)) {
                            dispatchUpdateEvent(entityRow);
                            return editor.val();
                        }
                    },

                    validation: function (cell, value) {
                        if (supportEdit(getEntityRow(cell.row), FIELD.WEIGHT)) {
                            if (value === '') {
                                deferred.notify(false);
                                return {
                                    message: $translate.instant('validation.error.required'),
                                    result: false
                                };
                            }

                            var selectedRow = getEntityRow(cell.row),
                                errorMessage,
                                min,
                                max,
                                number = Number(value);

                            if (selectedRow.id === undefined) {
                                min = CONSTANTS.CREATIVE_GROUP.WEIGHT.MIN;
                                max = CONSTANTS.CREATIVE_GROUP.WEIGHT.MAX;
                                errorMessage = $translate.instant('validation.error.creativeGroupPositiveNumberBetween',
                                    {
                                        start: min,
                                        end: max
                                    });
                            }
                            else {
                                min = CONSTANTS.SCHEDULE.WEIGHT.MIN;
                                max = CONSTANTS.SCHEDULE.WEIGHT.MAX;
                                errorMessage = $translate.instant('validation.error.positiveNumberBetween',
                                    {
                                        start: min,
                                        end: max
                                    });
                            }

                            if (number === Number.NaN || number % 1 !== 0 || number < min || number > max) {
                                deferred.notify(false);
                                return {
                                    message: errorMessage,
                                    result: false
                                };
                            }
                        }

                        deferred.notify(true);
                        return true;
                    }
                };
            },

            getRightDateRendering: function (dateText) {
                return getRightDateRendering(dateText);
            },

            isGridValid: isGridValid
        };
    }
})();
