(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTreeGrid', TeTreeGridDirective);

    TeTreeGridDirective.$inject = ['$parse', 'lodash'];

    function TeTreeGridDirective($parse, lodash) {
        return {
            bindToController: true,
            controller: 'TeTreeGridController',
            controllerAs: 'vm',
            replace: true,
            restrict: 'E',
            templateUrl: 'components/directives/te-tree-grid/te-tree-grid.html',
            transclude: true,

            scope: {
                childrenField: '@?',
                model: '=',
                columnsResize: '=',
                paginationEnabled: '@?',
                pageSize: '@?',
                searchFields: '=?',
                selectionMode: '@?',
                uuid: '=',
                legend: '=',
                teTreeGridCustomSearch: '@?',
                teTreeGridHideCustomSearchButton: '=?',
                teTreeGridSearchOptions: '@?',
                teTreeGridOnSearch: '&',
                teTreeGridOnClearSearchField: '&',
                teTreeGridOnReloadSearchOptions: '&',
                teTreeGridOnFilterApplied: '&',
                teTreeGridOnClearFiltering: '&'
            },

            link: function (scope, element, attrs, controller, transclude) {
                var $parent = scope.$parent,
                    columns,
                    dataFields,
                    defaultSort,
                    editable,

                    getRenderer = function (renderer) {
                        var fn = renderer && $parse(renderer, null, true);

                        return fn && function (row, dataField, cellValue, rowData, cellText) {
                                return fn($parent, {
                                    $cellText: cellText,
                                    $cellValue: cellValue,
                                    $dataField: dataField,
                                    $row: row,
                                    $rowData: rowData
                                });
                            };
                    },

                    isSearchFieldChecked = function (searchField, fields) {
                        if (!searchField || !fields) {
                            return true;
                        }

                        var result = lodash.findWhere(fields, {
                            field: searchField.field
                        });

                        return result ? result.enabled : true;
                    };

                activate();

                scope.$on('te-tree-grid:reloadConfiguration', function (evt, args) {
                    if (scope.vm.uuid === args.uuid) {
                        activate(args);
                    }
                });

                function activate(params) {
                    transclude($parent, function (clone) {
                        var data = clone.filter('te-columns')
                            .find('te-column')
                            .map(function (i, column) {
                                var _data = angular.element(column).data();

                                return lodash.extend(_data, {
                                    position: $parse(_data.position)($parent) || _data.position,
                                    cellsFormat: $parse(_data.cellsFormat)($parent),
                                    title: $parse(_data.title)($parent) || _data.field,
                                    editorConf: $parse(_data.editorConf)($parent),
                                    filterable: $parse(_data.filterable)($parent) || _data.filterable || false,
                                    width: $parse(_data.dynamicWidth)($parent) || _data.width
                                });
                            })
                            .get();

                        columns = lodash.chain(data)
                            .map(function (_data) {
                                var editorConf = _data.editorConf;

                                if (angular.isUndefined(editorConf)) {
                                    editorConf = {
                                        columnType: undefined,
                                        createEditor: undefined,
                                        initEditor: undefined,
                                        getEditorValue: undefined,
                                        validation: undefined
                                    };
                                }

                                return lodash.omit({
                                    cellClassName: getRenderer(_data.cellClassName),
                                    cellsAlign: _data.cellsAlign || 'left',
                                    cellsFormat: _data.cellsFormat,
                                    cellsRenderer: getRenderer(_data.cellsRenderer),
                                    dataField: _data.field,
                                    editable: _data.editable || false,
                                    hidden: _data.hidden || false,
                                    sortable: _data.sortable || false,
                                    text: _data.title,
                                    width: _data.width,
                                    minWidth: _data.minWidth,
                                    renderer: getRenderer(_data.headerRenderer),
                                    resizable: _data.resizable,
                                    columnType: editorConf.columnType,
                                    createEditor: editorConf.createEditor,
                                    initEditor: editorConf.initEditor,
                                    getEditorValue: editorConf.getEditorValue,
                                    validation: editorConf.validation
                                }, lodash.isUndefined);
                            })
                            .value();

                        dataFields = lodash.chain(data)
                            .map(function (_data) {
                                return {
                                    name: _data.field,
                                    type: _data.type || 'string'
                                };
                            })
                            .value();

                        defaultSort = lodash.result(lodash.findWhere(data, {
                                sortable: true,
                                sortableDefault: true
                            }) || lodash.findWhere(data, {
                                sortable: true
                            }), 'field');

                        editable = lodash.result(lodash.findWhere(data, {
                            editable: true
                        }), 'field');

                        scope.vm.searchFields = lodash.chain(data)
                            .where({
                                filterable: true
                            })
                            .map(function (_data) {
                                var field = lodash.pick(_data, 'field', 'title', 'position');

                                return lodash.extend({
                                    enabled: params && params.resetSearchFields === true ? true :
                                        isSearchFieldChecked(field, scope.vm.searchFields)
                                }, field);
                            })
                            .value();

                        controller.reloadSearchOptions();

                        scope.vm.searchTerm = angular.isDefined(params) && params.resetSearchFieldText ?
                            null : scope.vm.searchTerm;
                        element.find('.te-tree-grid-btns').empty();
                        element.find('.te-tree-grid-secondary-btns').empty();
                        element.find('.te-tree-grid-btns').append(clone.filter('te-btns'));
                        element.find('.te-tree-grid-secondary-btns').append(clone.filter('te-secondary-btns'));
                        element.find('.te-tree-grid-thirdly-btns').empty();
                        element.find('.te-tree-grid-thirdly-btns').append(clone.filter('te-thirdly-btns'));
                    });

                    if (angular.isDefined(params) && params.clearSearch === true) {
                        controller.clearSearch();
                    }

                    if (angular.isUndefined(params) || params.refreshColumns) {
                        controller.refreshColumns(columns);
                    }

                    controller.searchFields = scope.vm.searchFields;
                }

                controller.columns = columns;
                controller.dataFields = dataFields;
                controller.defaultSort = defaultSort;
                controller.editable = editable;
                controller.activate();
            }
        };
    }
})();
