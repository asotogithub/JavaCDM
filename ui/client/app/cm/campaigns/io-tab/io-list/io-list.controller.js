(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('InsertionOrderListTabController', InsertionOrderListTabController);

    InsertionOrderListTabController.$inject = [
        '$interpolate',
        '$q',
        '$rootScope',
        '$state',
        '$stateParams',
        '$templateCache',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'CampaignsUtilService',
        'DateTimeService',
        'InsertionOrderService',
        'InsertionOrderUtilService',
        'UserService',
        'Utils',
        'dialogs',
        'lodash',
        'uuid'
    ];

    function InsertionOrderListTabController(
        $interpolate,
        $q,
        $rootScope,
        $state,
        $stateParams,
        $templateCache,
        $translate,
        CONSTANTS,
        CampaignsService,
        CampaignsUtilService,
        DateTimeService,
        InsertionOrderService,
        InsertionOrderUtilService,
        UserService,
        Utils,
        dialogs,
        lodash,
        uuid) {
        var campaignId = $stateParams.campaignId,
            vm = this,
            PARENT = 0,
            CHILDREN = 1,
            currentSelectedUUIDRow = null,
            totalIOsActivate,
            totalPlacementsActivate;

        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.TEMPLATE_PATH = CONSTANTS.INSERTION_ORDER.IMPORT.TEMPLATE_URL;
        vm.addIo = addIo;
        vm.cellClassName = cellClassName;
        vm.downloadSuccessful = false;
        vm.editIoPlacement = editIoPlacement;
        vm.exportAll = exportAll;
        vm.filterByLevel = filterByLevel;
        vm.getCheckboxRenderer = getCheckboxRenderer;
        vm.getStatusName = getStatusName;
        vm.hasExportMediaPermission = UserService.hasPermission(CONSTANTS.PERMISSION.MEDIA.EXPORT_MEDIA);
        vm.hasImportMediaPermission = UserService.hasPermission(CONSTANTS.PERMISSION.MEDIA.IMPORT_MEDIA);
        vm.legendMedia = null;
        vm.onClearFiltering = onClearFiltering;
        vm.onFilterApplied = onFilterApplied;
        vm.onSelect = onSelect;
        vm.openImport = openImport;
        vm.selection = null;
        vm.selectionLevel = null;
        vm.uuid = uuid.v4();

        activate();

        function activate() {
            InsertionOrderUtilService.setPlacementListFilter(null);

            totalIOsActivate = 0;
            totalPlacementsActivate = 0;

            vm.promise = $q.all([
                InsertionOrderService.getList(campaignId),
                CampaignsService.getPackagePlacements(campaignId)
            ]);

            vm.promise.then(function (args) {
                var insertionOrderList = args[0],
                    placements = lodash.groupBy(args[1], 'ioId');

                totalIOsActivate = Utils.isUndefinedOrNull(insertionOrderList) ? 0 : insertionOrderList.length;

                vm.insertionOrderList = lodash.map(insertionOrderList, function (insertionOrder) {
                    var children = applyLocalOffset(placements[insertionOrder.id]),
                        endDates = lodash.pluck(children, 'endDate'),
                        ioName = insertionOrder.name,
                        ioNumber = insertionOrder.ioNumber,
                        startDates = lodash.pluck(children, 'startDate');

                    totalPlacementsActivate += parseInt(insertionOrder.placementsCount);

                    return lodash.extend(lodash.omit(insertionOrder, 'name', 'placementsCount', 'totalAdSpend'), {
                        adSpend: insertionOrder.totalAdSpend,
                        endDate: endDates.length ? lodash.max(endDates, getTime) : null,
                        ioName: ioName,
                        placementName: parseInt(insertionOrder.placementsCount).toLocaleString(),
                        startDate: startDates.length ? lodash.min(startDates, getTime) : null,
                        status: getStatusName(insertionOrder.status),

                        children: lodash.map(children, function (placement) {
                            return lodash.extend(lodash.omit(placement, 'placementName'), {
                                ioName: ioName,
                                ioNumber: ioNumber,
                                placementName: placement.placementName,
                                status: getStatusName(placement.status)
                            });
                        })
                    });
                });

                updateLegendMedia(totalIOsActivate, totalPlacementsActivate);
            });
        }

        function addIo() {
            $state.go('io-add');
        }

        function applyLocalOffset(array) {
            var result = [];

            lodash.forEach(array, function (placement) {
                placement.startDate = DateTimeService.applyOffset(placement.startDate);
                placement.endDate = DateTimeService.applyOffset(placement.endDate);
                result.push(placement);
            });

            return result;
        }

        function cellClassName(row, rowData) {
            var className;

            if (!!rowData.level) {
                className = 'placement-cell-' + (row % 2 ? 'even' : 'odd');
            }

            return className;
        }

        function checkRow(uuidRow) {
            var treeGrid = angular.element('.te-tree-grid-delegate');

            if (treeGrid.length) {
                treeGrid.jqxTreeGrid('checkRow', uuidRow);
            }
        }

        function editIoPlacement(selection) {
            switch (vm.selectionLevel) {
                case PARENT:
                    $state.go('campaign-io-details', {
                        campaignId: campaignId,
                        ioId: selection.id,
                        io: selection
                    });
                    break;
                case CHILDREN:
                    if (angular.isDefined(selection.packageId)) {
                        $state.go('edit-package', {
                            campaignId: campaignId,
                            ioId: selection.ioId,
                            packageId: selection.packageId,
                            from: 'io-list'
                        });
                    }
                    else {
                        $state.go('edit-placement', {
                            campaignId: campaignId,
                            ioId: selection.ioId,
                            placementId: selection.placementId,
                            from: 'io-list'
                        });
                    }

                    break;
            }
        }

        function exportAll() {
            vm.promise = CampaignsService.exportResource(campaignId, CONSTANTS.INSERTION_ORDER.FILE_TYPE.XLSX,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION);
            return vm.promise.then(function (response) {
                var blob = new Blob([response.data],
                    {
                        type: response.type
                    });

                saveAs(blob, response.name);
                vm.downloadSuccessful = true;
            });
        }

        function filterByLevel(visibleLevel, rowData, cellText) {
            return visibleLevel === rowData.level ? cellText : '';
        }

        function getCheckboxRenderer(rowData, cellValue) {
            var template = $templateCache.get('media-checkbox-renderer.html'),
                scope = $rootScope.$new(true);

            scope.vm = {
                rowId: rowData.$$uuid,
                checked: cellValue === true
            };

            return $interpolate(template)(scope);
        }

        function getStatusName(key) {
            return CampaignsUtilService.getStatusName(key);
        }

        function onClearFiltering() {
            updateLegendMedia(totalIOsActivate, totalPlacementsActivate);
        }

        function onFilterApplied(rows) {
            var i,
                totalIOs = 0,
                totalPlacements = 0;

            if (!Utils.isUndefinedOrNull(rows)) {
                totalIOs = rows.length;
            }

            for (i = 0; i < totalIOs; i++) {
                if (!Utils.isUndefinedOrNull(rows[i].records)) {
                    totalPlacements += rows[i].records.length;
                }
            }

            updateLegendMedia(totalIOs, totalPlacements);
        }

        function onSelect(selection, level) {
            var checkCurrentRow = true,
                unselectRow = false;

            if (!Utils.isUndefinedOrNull(currentSelectedUUIDRow)) {
                if (currentSelectedUUIDRow === selection.$$uuid) {
                    unselectRow = true;
                    checkCurrentRow = false;
                }

                uncheckRow(currentSelectedUUIDRow, unselectRow);

                currentSelectedUUIDRow = null;
                vm.selection = null;
                vm.selectionLevel = null;
            }

            if (checkCurrentRow) {
                currentSelectedUUIDRow = selection.$$uuid;
                checkRow(currentSelectedUUIDRow);

                vm.selection = selection;
                vm.selectionLevel = level;
            }
        }

        function openImport() {
            var template = 'app/cm/campaigns/io-tab/io-list/io-import/io-import.html',
                dlg = dialogs.create(template,
                    'IOImportController as vm',
                    {
                        campaignId: campaignId
                    },
                    {
                        size: 'md',
                        keyboard: true,
                        key: false,
                        backdrop: 'static'
                    });

            dlg.result.then(
                function (response) {
                    if (!Utils.isUndefinedOrNull(response.info) && response.info.importSuccess) {
                        if (response.info.rowsUpdated > 0) {
                            vm.insertionOrderList = null;
                            activate();
                        }
                    }
                    else {
                        openImportErrors(response);
                    }
                });

            return dlg;
        }

        function openImportErrors(response) {
            var template = 'app/cm/campaigns/io-tab/io-list/io-import-errors/io-import-errors.html',
                dlg = dialogs.create(template,
                    'IOImportErrorsController as vm',
                    {
                        campaignId: campaignId,
                        response: response.data,
                        uuid: response.info.uuid
                    },
                    {
                        size: 'lg',
                        keyboard: true,
                        key: false,
                        backdrop: 'static'
                    });

            dlg.result.then(
                function (data) {
                    if (!Utils.isUndefinedOrNull(data)) {
                        activate();
                    }
                });

            return dlg;
        }

        function uncheckRow(uuidRow, unselectRow) {
            var treeGrid = angular.element('.te-tree-grid-delegate');

            if (treeGrid.length) {
                treeGrid.jqxTreeGrid('uncheckRow', uuidRow);
                if (unselectRow) {
                    treeGrid.jqxTreeGrid('unselectRow', uuidRow);
                }
            }
        }

        function updateLegendMedia(totalIOs, totalPlacements) {
            vm.legendMedia = $translate.instant('media.rowsIOsPlacements', {
                rowsIO: totalIOs,
                rowsPlacement: totalPlacements
            });
        }

        function getTime(dateString) {
            return dateString && new Date(dateString).getTime();
        }
    }
})();
