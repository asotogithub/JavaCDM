(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PackageListController', PackageListController);

    PackageListController.$inject = [
        '$filter',
        '$interpolate',
        '$state',
        '$stateParams',
        '$rootScope',
        '$templateCache',
        '$timeout',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'PackageUtilService',
        'Utils',
        'lodash',
        'uuid'
    ];

    function PackageListController(
        $filter,
        $interpolate,
        $state,
        $stateParams,
        $rootScope,
        $templateCache,
        $timeout,
        $translate,
        CONSTANTS,
        CampaignsService,
        PackageUtilService,
        Utils,
        lodash,
        uuid
    ) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            ioId = $stateParams.ioId,
            ICONS = CONSTANTS.SCHEDULE.ICONS;

        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.flyoutUUID = uuid.v4();
        vm.PAGE_SIZE = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.MEDIUM;
        vm.selectionMode = 'MULTI';
        vm.packageList = [];
        vm.countRowSelected = 0;
        vm.getAllCheckedData = getAllCheckedData;
        vm.editPackage = editPackage;
        vm.getCheckboxRenderer = getCheckboxRenderer;
        vm.getRowRenderer = getRowRenderer;
        vm.rowCollapsed = rowCollapsed;
        vm.rowExpanded = rowExpanded;

        activate();

        function activate() {
            vm.promise = CampaignsService.getPackageList(campaignId, ioId);
            vm.promise.then(function (packageList) {
                vm.packageList = lodash.map(packageList, toPackageRows());
                expandRows(PackageUtilService.getHasPendingChanges(), vm.packageList);
            });
        }

        function expandRows(expandedRows, model) {
            var index = 0,
                element;

            if (!Utils.isUndefinedOrNull(expandedRows)) {
                for (index; index < expandedRows.length; index++) {
                    element = findElementById(model, expandedRows[index].id);
                    element.expanded = true;
                    expandCollapseRow('expandRow', element);
                }
            }
        }

        function findElementById(model, id) {
            return lodash.find(model, function (elem) {
                return elem.id === id;
            });
        }

        function expandCollapseRow(action, row) {
            $timeout(function () {
                $('.te-tree-grid-delegate').jqxTreeGrid(action, row.$$uuid);
            }, 0, false);
        }

        function toPackageRows() {
            return function (row) {
                var index = 0,
                    children,
                    adSpend = '-',
                    sizePackage = '-',
                    sitePackage = '-',
                    startDate = '-',
                    endDate = '-';

                if (row.placementCount > 0) {
                    adSpend = 0;
                    children = lodash.map(row.placements, toPlacementsRows());
                    startDate = children[0].startDate;
                    endDate = children[children.length - 1].endDate;
                    sizePackage = formatValue(
                        Object.keys(lodash.groupBy(children, 'size')).length,
                        children[0].size
                    );
                    sitePackage = formatValue(
                        Object.keys(lodash.groupBy(children, 'site')).length,
                        children[0].site);
                    for (index; index < row.costDetails.length; index++) {
                        adSpend = row.costDetails[index].plannedGrossAdSpend + adSpend;
                    }

                    adSpend = $filter('currency')(adSpend);
                }

                return {
                    id: row.id,
                    expanded: false,
                    name: row.name,
                    isParent: true,
                    size: sizePackage,
                    site: sitePackage,
                    adSpend: adSpend,
                    startDate: startDate,
                    endDate: endDate,
                    checked: false,
                    placementName: row.placementCount,
                    children: children
                };
            };
        }

        function toPlacementsRows() {
            return function (row) {
                return {
                    placementId: row.id,
                    placementName: row.name,
                    size: $translate.instant(
                        'global.sizeFormat',
                        {
                            height: row.height,
                            width: row.width
                        }),
                    site: row.siteName,
                    adSpend: $filter('currency')(row.adSpend),
                    startDate: row.startDate,
                    endDate: row.endDate
                };
            };
        }

        function formatValue(countValue, model) {
            var result;

            switch (countValue) {
                case 1:
                    result = model;
                    break;
                default:
                    result = $translate.instant(
                        'global.multiple',
                        {
                            number: countValue
                        });
                    break;
            }

            return result;
        }

        function getAllCheckedData() {
            vm.countRowSelected = getRowBy(
                {
                    checked: true
                }
            ).length;
        }

        function editPackage() {
            var rowSelected = getRowBy(
                {
                    checked: true
                }
            );

            $state.go('edit-package', {
                campaignId: campaignId,
                ioId: ioId,
                packageId: rowSelected[0].id,
                from: 'package-list'
            });
        }

        function getRowBy(condition) {
            return lodash.filter(
                vm.packageList,
                condition
            );
        }

        function getCheckboxRenderer(row, cellValue) {
            if (!Utils.isUndefinedOrNull(row.isParent)) {
                var template = $templateCache.get('package-list-checkbox-renderer.html'),
                    scope = $rootScope.$new(true);

                scope.vm = {
                    rowId: row.$$uuid,
                    checked: cellValue === true
                };

                return $interpolate(template)(scope);
            }
        }

        function getRowRenderer(row, cellValue) {
            var template = $templateCache.get('package-row-renderer.html'),
                scope = $rootScope.$new(true);

            scope.vm = {
                field: row.field,
                label: cellValue,
                class: 'placement fa ' + ICONS.PLACEMENT
            };

            return $interpolate(template)(scope);
        }

        function rowExpanded(row) {
            row.expanded = true;
            PackageUtilService.setHasPendingChanges(
                getRowBy(
                    {
                        expanded: true
                    }
                )
            );
        }

        function rowCollapsed(row) {
            row.expanded = false;
            PackageUtilService.setHasPendingChanges(
                getRowBy(
                    {
                        expanded: true
                    }
                )
            );
        }
    }
})();
