(function () {
    'use strict';

    angular
        .module('te.components')
        .controller('TeTableController', TeTableController);

    TeTableController.$inject = [
        '$filter',
        '$scope',
        'CONSTANTS',
        'Utils',
        'lodash',
        'ngTableParams'
    ];

    function TeTableController($filter, $scope, CONSTANTS, Utils, lodash, NgTableParams) {
        var SELECTION_MODE = CONSTANTS.TE_TABLE.SELECTION_MODE,
            vm = this,
            $data,
            KEY_CODE_ENTER = 13;

        vm.activate = activate;
        vm.isSearchCollapsed = Utils.isUndefinedOrNull(vm.collapseSearch) ? false : vm.collapseSearch;
        vm.clearSearch = clearSearch;
        vm.defaultSort = null;
        vm.getSelection = getSelection;
        vm.filtered = null;
        vm.clearPaging = true;
        vm.isRowSelected = isRowSelected;
        vm.searchFields = vm.teTableCustomSearchEnabled ? vm.searchFields : null;
        vm.selection = [];
        vm.tableParams = null;
        vm.toggleSearchCollapse = toggleSearchCollapse;
        vm.toggleSearchField = toggleSearchField;
        vm.toggleSelection = toggleSelection;
        vm.onKeyPress = onKeyPress;
        vm.customSearch = customSearch;
        vm.applySearch = applySearch;

        function activate() {
            initTableParams();
            initAllSelected();

            if (!vm.teTableCustomSearchEnabled) {
                watch('searchTerm', resetTable);
            }

            watch('model', function () {
                vm.selection = lodash.xor(vm.selection, lodash.difference(vm.selection, vm.model));
                resetTable();
            });

            // NOTE: rlt 20150629 - This is part of a hack to resolve a rare issue when tableParams loses its scope.
            $scope.$parent.$broadcast('te-table-activated');
        }

        function initTableParams() {
            var defaultSort = vm.defaultSort,
                sorting = {},
                sortHistory = [];

            if (defaultSort) {
                sorting[defaultSort] = 'asc';
            }

            vm.tableParams = new NgTableParams({
                page: 1,
                count: vm.pageSize,
                sorting: sorting
            }, {
                counts: null,
                defaultSort: 'asc',

                getData: function ($defer, params) {
                    var pageSize = params.count(),
                        sort = params.sorting(),
                        orderBy = lodash.uniq([
                            {
                                field: lodash.keys(sort)[0],
                                fieldDirection: lodash.values(sort)[0]
                            }
                        ].concat(sortHistory), 'field'),
                        data = pinRows($filter('orderBy')(
                            $filter('gridFilter')(
                                $filter('valuesFilter')(
                                    vm.model || [], vm.filterValues
                                ),
                                {
                                    searchVal: vm.teTableCustomSearchEnabled ? null : vm.searchTerm,
                                    searchFields: lodash.chain(vm.searchFields)
                                        .filter({
                                            enabled: true
                                        })
                                        .pluck('field')
                                        .value()
                                }
                            ),
                            lodash.map(orderBy, function (_orderBy) {
                                return (_orderBy.fieldDirection === 'desc' ? '-' : '+') + _orderBy.field;
                            })));

                    sortHistory = orderBy;
                    vm.filtered = $data = data;
                    params.settings({
                        total: data.length
                    });

                    if (pageSize) {
                        $defer.resolve(data.slice((params.page() - 1) * pageSize, params.page() * pageSize));
                    }
                    else {
                        params.settings({
                            counts: data.length
                        });
                        $defer.resolve(data);
                    }

                    vm.clearPaging = Utils.isUndefinedOrNull(vm.pageSize) ? true : data.length <= parseInt(vm.pageSize);
                    vm.onCounterSearch({
                        counterSearch: data.length
                    });
                }
            });
        }

        function pinRows(data) {
            var criteria = vm.pinnedRows,
                pinned = criteria && lodash.where(data, criteria);

            return pinned ? pinned.concat(lodash.difference(data, pinned)) : data;
        }

        function initAllSelected() {
            Object.defineProperty(vm, 'allSelected', {
                get: function () {
                    var data = $data || [];

                    return !!data.length && !lodash.difference(data, vm.selection).length;
                },

                set: function (value) {
                    var selection = vm.selection || [],
                        data = $data || [];

                    vm.selection = lodash.uniq(value ? selection.concat(data) : lodash.difference(selection, data));

                    reloadTable();
                }
            });
        }

        function resetTable() {
            vm.tableParams.page(1);
            reloadTable();
        }

        function reloadTable() {
            vm.tableParams.reload();
        }

        function watch(property, listener) {
            $scope.$watch(function () {
                return vm[property];
            }, listener);
        }

        function clearSearch() {
            vm.searchTerm = null;
            if (vm.teTableCustomSearchEnabled) {
                vm.teTableOnClearSearchField();
            }
        }

        function getSelection() {
            var selection = (vm.selection || []).concat();

            return vm.selectionMode === SELECTION_MODE.MULTI ? selection : selection[0];
        }

        function isRowSelected(row) {
            return lodash.contains(vm.selection, row);
        }

        function toggleSearchCollapse() {
            vm.isSearchCollapsed = !vm.isSearchCollapsed;
        }

        function toggleSearchField(evt, searchField) {
            evt.preventDefault();
            evt.stopImmediatePropagation();

            if (!searchField.enabled ||
                lodash.where(vm.searchFields, {
                    enabled: true
                }).length > 1) {
                searchField.enabled = !searchField.enabled;
                resetTable();
            }
        }

        function toggleSelection(evt, row) {
            if (!angular.element(evt.currentTarget).closest('fieldset').is(':disabled')) {
                evt.preventDefault();
                evt.stopImmediatePropagation();

                switch (vm.selectionMode) {
                    case SELECTION_MODE.MULTI:
                        toggleMultiSelection(row);
                        break;

                    case SELECTION_MODE.NONE:
                        break;

                    default:
                        toggleSingleSelection(row);
                        break;
                }

                reloadTable();
            }
        }

        function toggleMultiSelection(row) {
            var selection = vm.selection;

            vm.selection = lodash.contains(selection, row) ? lodash.without(selection, row) : selection.concat(row);
        }

        function toggleSingleSelection(row) {
            vm.selection = lodash.contains(vm.selection, row) ? [] : [row];
        }

        function onKeyPress($event) {
            if (vm.teTableCustomSearchEnabled && $event.keyCode === KEY_CODE_ENTER) {
                vm.customSearch();
            }
        }

        function customSearch() {
            vm.isSearchEditable = false;

            vm.teTableOnCustomSearch({
                searchText: vm.searchTerm
            });
        }

        function applySearch() {
            if (vm.teTableCustomSearchEnabled) {
                if (vm.searchTerm === null || vm.searchTerm.length === 0) {
                    vm.clearSearch();
                }
            }
        }

        $scope.$on('te-table:updateSelectionList', function (event, args) {
            if (vm.uuid === args.uuid) {
                vm.selection = args.selection;
                reloadTable();
            }
        });
    }
})();
