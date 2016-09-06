(function () {
    'use strict';

    angular
        .module('te.components')
        .controller('TeTreeGridController', TeTreeGridController);

    TeTreeGridController.$inject = [
        '$element',
        '$filter',
        '$scope',
        '$timeout',
        '$window',
        'lodash',
        'uuid'
    ];

    function TeTreeGridController(
        $element,
        $filter,
        $scope,
        $timeout,
        $window,
        lodash,
        uuid) {
        var $orderBy = $filter('orderBy'),
            CUSTOM_SEARCH_MIN_LENGTH = 1,
            KEY_CODE_ENTER = 13,
            SEARCH_INPUT_MAX_LENGTH = 250,
            sortHistory = [],
            vm = this;

        vm.activate = activate;
        vm.allChecked = [];
        vm.applySearch = applySearch;
        vm.cellBeginEdit = cellBeginEdit;
        vm.cellEndEdit = cellEndEdit;
        vm.cleanAllChecked = cleanAllChecked;
        vm.clearSearch = clearSearch;
        vm.columns = null;
        vm.customSearch = customSearch;
        vm.customSearchEnabled = false;
        vm.dataFields = null;
        vm.defaultSort = null;
        vm.disableSearch = disableSearch;
        vm.getAllCheckedData = getAllCheckedData;
        vm.getRecord = getRecord;
        vm.getRecords = getRecords;
        vm.grid = {};
        vm.idField = '$$uuid';
        vm.onKeyPress = onKeyPress;
        vm.refreshColumns = refreshColumns;
        vm.reloadSearchOptions = reloadSearchOptions;
        vm.removeLeafNodes = removeLeafNodes;
        vm.searchInputMaxLength = SEARCH_INPUT_MAX_LENGTH;
        vm.searchTerm = null;
        vm.setDataChecked = setDataChecked;
        vm.settings = null;
        vm.source = null;
        vm.toggleSearchField = toggleSearchField;
        vm.unsetDataChecked = unsetDataChecked;
        vm.updateAllChecked = updateAllChecked;
        vm.isSearchEditable = true;

        function activate() {
            var searchOptions = null;

            vm.childrenField = vm.childrenField || 'children';
            vm.source = {
                id: vm.idField,
                dataType: 'json',
                localData: function () {
                    return vm.model;
                },

                dataFields: [
                    {
                        name: vm.idField,
                        type: 'string'
                    },
                    {
                        name: vm.childrenField,
                        type: 'array'
                    }
                ].concat(vm.dataFields),
                hierarchy: {
                    root: vm.childrenField
                }
            };
            vm.dataAdapter = new angular.element.jqx.dataAdapter(vm.source);
            vm.settings = {
                editable: vm.editable || false,
                pageable: vm.paginationEnabled || false,
                pageSize: angular.isDefined(vm.pageSize) ? parseInt(vm.pageSize) : 10,
                editSettings: {
                    saveOnPageChange: true,
                    saveOnBlur: true,
                    saveOnSelectionChange: true,
                    cancelOnEsc: true,
                    saveOnEnter: true,
                    editSingleCell: true,
                    editOnDoubleClick: true,
                    editOnF2: true
                },
                rendered: function () {
                    if (angular.element('.customTreeGridCheckbox').length > 0) {
                        angular.element('.customTreeGridCheckbox').off('change').on('change', function (event) {
                            var rowKey = event.target.getAttribute('data-row-key'),
                                checked = event.target.getAttribute('data-check-value');

                            if (vm.model) {
                                setModelChecked(rowKey, checked);
                            }

                            if (checked === 'true') {
                                angular.element('.te-tree-grid-delegate').jqxTreeGrid('uncheckRow', rowKey);
                            }
                            else {
                                angular.element('.te-tree-grid-delegate').jqxTreeGrid('checkRow', rowKey);
                            }

                            propagateCheckUncheckToHeaderColumn();
                        });
                    }

                    if (angular.element('#customTreeGridHeaderCheckboxContainer').length > 0) {
                        angular.element('#customTreeGridHeaderCheckboxContainer').off('click').on('click', function () {
                            checkUncheckHeaderColumn();
                        });
                    }

                    if (angular.element('#customTreeGridHeaderCheckboxCheckedContainer').length > 0) {
                        angular.element('#customTreeGridHeaderCheckboxContainer').siblings('.iconscontainer')
                            .css('display', 'none');
                        angular.element('#customTreeGridHeaderCheckboxCheckedContainer').off('click')
                            .on('click', function () {
                                checkUncheckHeaderColumn();
                            });
                    }
                },

                columns: vm.columns,
                columnsHeight: 49,
                columnsResize: vm.columnsResize || false,
                ready: onReady,
                rowCollapse: triggerResize,
                rowExpand: triggerResize,
                selectionMode: vm.selectionMode || 'singlerow',
                sort: onSort,
                sortable: true,
                theme: 'bootstrap',
                width: '100%'
            };

            vm.customSearchEnabled = angular.isDefined(vm.teTreeGridCustomSearch) ?
                vm.teTreeGridCustomSearch === 'true' : false;

            if (vm.customSearchEnabled &&
                angular.isDefined(vm.teTreeGridSearchOptions)) {
                searchOptions = angular.fromJson(vm.teTreeGridSearchOptions);
                vm.customSearchMinLength = angular.isDefined(searchOptions.searchMinLength) ?
                    searchOptions.searchMinLength : CUSTOM_SEARCH_MIN_LENGTH;
            }

            $scope.$watch('vm.model', function (model) {
                updateModel(model);
            });
        }

        function checkUncheckHeaderColumn() {
            var rowKey = angular.element('#customTreeGridHeaderCheckboxContainer').attr('data-row-key'),
                selectedRow = angular.element('.te-tree-grid-delegate').jqxTreeGrid('getRow', rowKey);

            if (selectedRow[0].checked === true) {
                angular.element('.te-tree-grid-delegate').jqxTreeGrid('uncheckRow', rowKey);
                unCheckHeaderCheckbox();
            }
            else {
                angular.element('.te-tree-grid-delegate').jqxTreeGrid('checkRow', rowKey);
                checkHeaderCheckbox();
            }
        }

        function propagateCheckUncheckToHeaderColumn() {
            var rowKey = angular.element('#customTreeGridHeaderCheckboxContainer').attr('data-row-key'),
                selectedRow = angular.element('.te-tree-grid-delegate').jqxTreeGrid('getRow', rowKey);

            if (angular.isDefined(selectedRow)) {
                if (selectedRow[0].checked === true) {
                    checkHeaderCheckbox();
                }
                else {
                    unCheckHeaderCheckbox();
                }
            }
        }

        function checkHeaderCheckbox() {
            angular.element('#customTreeGridHeaderCheckboxContainer').css('display', 'none');
            angular.element('#customTreeGridHeaderCheckboxCheckedContainer').css('display', 'block');
        }

        function unCheckHeaderCheckbox() {
            angular.element('#customTreeGridHeaderCheckboxContainer').css('display', 'block');
            angular.element('#customTreeGridHeaderCheckboxCheckedContainer').css('display', 'none');
        }

        function customSearch() {
            if (vm.searchTerm.length >= vm.customSearchMinLength) {
                vm.isSearchEditable = false;

                vm.teTreeGridOnSearch({
                    searchText: vm.searchTerm
                }).then(function () {
                    vm.isSearchEditable = true;
                    vm.settings.apply('goToPage', 0);
                    $timeout(function () {
                        angular.element('#' + vm.uuid).focus();
                    });
                });
            }
        }

        function setDataChecked(row) {
            var res;

            res = lodash.find(vm.allChecked, function (elem) {
                return elem.treeRowId === row.treeRowId;
            });

            if (res === undefined) {
                vm.allChecked.push(row);
                vm.allChecked = lodash.uniq(vm.allChecked);
            }
        }

        function setModelChecked(rowKey, checked) {
            var index = 0;

            for (index; index < vm.model.length; index++) {
                if (vm.model[index].$$uuid === rowKey) {
                    vm.model[index].checked = checked !== 'true';
                }
            }
        }

        function cleanAllChecked() {
            vm.allChecked = [];
        }

        function unsetDataChecked(row) {
            lodash.remove(vm.allChecked, function (value) {
                return value.treeRowId === row.treeRowId;
            });
        }

        function getAllCheckedData() {
            return vm.allChecked;
        }

        function getLeafNodes(node, resultList) {
            if (isUndefinedOrNull(node)) {
                return;
            }

            var length = node.length - 1;

            while (length > -1) {
                if (isUndefinedOrNull(node[length].loadData) || node[length].loadData === false) {
                    resultList.push(node[length]);
                }
                else {
                    getLeafNodes(node[length].children, resultList);
                }

                length--;
            }
        }

        function isUndefinedOrNull(obj) {
            return angular.isUndefined(obj) || obj === null;
        }

        function applySearch() {
            if (vm.customSearchEnabled) {
                if (vm.searchTerm === null || vm.searchTerm.length === 0) {
                    vm.clearSearch();
                }
            }
            else {
                var searchTerm = vm.searchTerm,
                    filterGroup = searchTerm && new angular.element.jqx.filter(),
                    filter = filterGroup && filterGroup.createfilter('stringfilter', searchTerm, 'contains');

                clearFilters();

                if (filter) {
                    filterGroup.operator = 'or';
                    filterGroup.addfilter(0, filter);

                    lodash.chain(vm.searchFields)
                        .where({
                            enabled: true
                        })
                        .pluck('field')
                        .forEach(function (field) {
                            addFilter(field, filterGroup);
                        })
                        .value();

                    applyFilters();
                }
            }

            if (vm.searchTerm === null || angular.isDefined(vm.searchTerm) && vm.searchTerm.length === 0) {
                if (angular.isDefined(vm.teTreeGridOnClearFiltering)) {
                    vm.teTreeGridOnClearFiltering();
                }
            }
        }

        function cellBeginEdit(event) {
            var args = event.args,
                row = args.row,
                dataField = args.dataField;

            if (row.editSupport && row.editSupport[dataField]) {
                return;
            }

            vm.settings.apply('endCellEdit', event.args.row.$$uuid, dataField);
        }

        function cellEndEdit(event) {
            var args = event.args,
                row = args.row,
                dataField = args.dataField;

            if (row && row.editSupport) {
                if (row.editSupport[dataField]) {
                    updateModelAfterEdit(row, dataField);
                }
            }
        }

        function updateModelAfterEdit(row, dataField) {
            if (vm.model[0].level === 3) {
                if (row.level === 0) {
                    vm.model[0][dataField] = row[dataField];
                }

                if (row.level === 1) {
                    var result = lodash.findWhere(vm.model[0].children, {
                        id: row.id
                    });

                    result[dataField] = row[dataField];
                }
            }

            if (vm.model[0].level === 4) {
                vm.model[0][dataField] = row[dataField];
            }
        }

        function clearSearch() {
            vm.searchTerm = null;
            if (vm.customSearchEnabled) {
                vm.teTreeGridOnClearSearchField();
                vm.settings.apply('goToPage', 0);
                reloadSearchOptions();
                $timeout(function () {
                    angular.element('#' + vm.uuid).focus();
                });
            }
            else {
                vm.applySearch();
            }
        }

        function reloadSearchOptions() {
            if (vm.customSearchEnabled) {
                if (angular.isDefined(vm.teTreeGridOnReloadSearchOptions())) {
                    vm.teTreeGridOnReloadSearchOptions().then(function (data) {
                        vm.searchFields = [];
                        vm.searchFields = data.searchFields;
                    });
                }
            }
        }

        function removeLeafNodes(row) {
            var leafToRemove = [];

            getLeafNodes(row.children, leafToRemove);
            lodash.forEach(leafToRemove, function (leaf) {
                lodash.remove(vm.allChecked, function (value) {
                    return value.treeRowId === leaf.treeRowId;
                });
            });
        }

        function disableSearch() {
            if (vm.customSearchEnabled) {
                return vm.isSearchEditable ? vm.model && !vm.model.length && !vm.searchTerm : !vm.isSearchEditable;
            }
            else {
                return vm.model && !vm.model.length ;
            }
        }

        function getRecord(id) {
            var criteria = {};

            criteria[vm.idField] = id;

            return lodash.findWhere(vm.allRecords, criteria);
        }

        function getRecords(model) {
            var childrenField = vm.childrenField,
                records = lodash.compact(model || []),
                ret = [];

            while (records.length) {
                ret = ret.concat(records);
                records = lodash.chain(records).pluck(childrenField).compact().flatten().value();
            }

            return ret;
        }

        function onKeyPress($event) {
            if (vm.customSearchEnabled && $event.keyCode === KEY_CODE_ENTER) {
                vm.customSearch();
            }
        }

        function toggleSearchField(evt, searchField) {
            evt.preventDefault();
            evt.stopImmediatePropagation();

            if (!searchField.enabled ||
                lodash.where(vm.searchFields, {
                    enabled: true
                }).length > 1) {
                searchField.enabled = !searchField.enabled;
                vm.applySearch();
            }
        }

        function onReady() {
            var defaultSort = vm.defaultSort;

            sortHack();

            if (defaultSort) {
                sortBy(defaultSort, 'asc');
            }
        }

        function onSort(evt) {
            var args = evt.args,
                sortDirection = args.sortdirection,
                lastSortColumn = lodash.pluck(sortHistory, 'field').shift();

            if (!sortDirection.ascending && !sortDirection.descending && lastSortColumn) {
                sortBy(lastSortColumn, 'asc');
            }
        }

        function triggerResize() {
            angular.element($window).triggerHandler('resize');
        }

        function updateModel(model) {
            if (!model) {
                return;
            }

            vm.allRecords = getRecords(model);

            setRecordUuids();

            vm.source.hierarchy = {
                root: vm.childrenField
            };
            vm.source.localData = function () {
                return model;
            };

            triggerResize();
        }

        function setRecordUuids() {
            var idField = vm.idField;

            lodash.forEach(vm.allRecords, function (record) {
                if (!record[idField]) {
                    record[idField] = uuid.v4();
                }
            });
        }

        // NOTE: rlt 20150805 - This is a *HACK* that modifies non-public APIs from jqxTreeGrid to ensure deterministic
        //                      sort order when sorting on child row fields for which the parent rows have no values
        //                      (nor can they logically have an aggregate value).  We maintain all the previous columns
        //                      that the data has been sorted by and and essentially use the previous sorted columns as
        //                      a tie-breaker.
        function sortHack() {
            var dataview = $element.find('.te-tree-grid-delegate').data('jqxTreeGrid').instance.base.dataview;

            dataview._sort = function (data) {
                sortHistory = lodash.uniq([
                    {
                        field: dataview.sortfield,
                        fieldDirection: dataview.sortfielddirection
                    }
                ].concat(sortHistory), 'field');

                return $orderBy(data, lodash.map(sortHistory, function (_sort) {
                    return (_sort.fieldDirection === 'desc' ? '-' : '+') + _sort.field;
                }));
            };
        }

        function sortBy(dataField, sortOrder) {
            if (isReady()) {
                vm.settings.apply('sortBy', dataField, sortOrder);
            }
        }

        function clearFilters() {
            if (isReady()) {
                vm.settings.apply('clearFilters');
            }
        }

        function addFilter(dataField, filterGroup) {
            if (isReady()) {
                vm.settings.apply('addFilter', dataField, filterGroup);
            }
        }

        function applyFilters() {
            if (isReady()) {
                vm.settings.apply('applyFilters');
                if (angular.isDefined(vm.teTreeGridOnFilterApplied)) {
                    vm.teTreeGridOnFilterApplied({
                        rows: angular.element('.te-tree-grid-delegate').jqxTreeGrid('getView')
                    });
                }
            }
        }

        function isReady() {
            return lodash.isFunction(lodash.get(vm, 'settings.apply'));
        }

        function refreshColumns(columns) {
            if (isReady()) {
                vm.settings.apply('columns', columns);
            }
        }

        function updateAllChecked(checkedList) {
            vm.allChecked = checkedList;
        }

        $scope.$on('te-tree-grid:checked:updateAll', function (event, args) {
            if (vm.uuid === args.uuid) {
                vm.updateAllChecked(args.allMarkedAsChecked);
            }
        });

        $scope.$on('te-tree-grid:checked:cleanAll', function (event, args) {
            if (vm.uuid === args.uuid) {
                vm.cleanAllChecked();
            }
        });
    }
})();
