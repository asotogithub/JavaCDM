(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PlacementListController', PlacementListController);

    PlacementListController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsUtilService',
        'DateTimeService',
        'DialogFactory',
        'InsertionOrderService',
        'InsertionOrderUtilService',
        'PlacementService',
        'Utils',
        'lodash'
    ];

    function PlacementListController(
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsUtilService,
        DateTimeService,
        DialogFactory,
        InsertionOrderService,
        InsertionOrderUtilService,
        PlacementService,
        Utils,
        lodash) {
        var vm = this,
            ioId = $stateParams.ioId;

        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.FILTER_OPTION = CONSTANTS.PLACEMENT.PLACEMENT_LIST_FILTER_OPTION;
        vm.STATUS = CONSTANTS.STATUS;
        vm.addPlacement = addPlacement;
        vm.changePlacementsStatus = changePlacementsStatus;
        vm.close = close;
        vm.editPlacement = editPlacement;
        vm.getStatusName = getStatusName;
        vm.originalPlacementList = [];
        vm.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.save = save;
        vm.selectRows = selectRows;
        vm.selectedRows = [];
        vm.statusList = lodash.values(CampaignsUtilService.getIOStatusList());

        activate();

        function activate() {
            InsertionOrderUtilService.setHasPendingChanges(false);
            vm.promise = InsertionOrderService.getPackagePlacements(ioId);
            vm.promise.then(function (placements) {
                vm.updatedList = [];
                vm.placementList = placements;
                vm.selectedFilterOption = InsertionOrderUtilService.getPlacementListFilter();
                formatPlacementsDates();
                initFilterWatcher();
                angular.copy(vm.placementList, vm.originalPlacementList);
            });
        }

        function formatPlacementsDates() {
            angular.forEach(vm.placementList, function (placement) {
                // Apply local offset into dates
                placement.formattedStartDate = DateTimeService.format(placement.startDate);
                placement.formattedEndDate = DateTimeService.format(placement.endDate);
                placement.startDate = DateTimeService.parse(placement.startDate);
                placement.endDate = DateTimeService.parse(placement.endDate);
                placement.scheduledIcon = placement.isScheduled === CONSTANTS.PLACEMENT.SCHEDULED.YES;
            });
        }

        function filterPlacements() {
            vm.placementList = [];
            angular.copy(vm.originalPlacementList, vm.placementList);
            updatePlacementListStatus();
            InsertionOrderUtilService.setPlacementListFilter(vm.selectedFilterOption);
            if (vm.selectedFilterOption === vm.FILTER_OPTION.PACKAGES) {
                vm.placementList = lodash.filter(vm.placementList, function (item) {
                    return !Utils.isUndefinedOrNull(item.packageName);
                });
            }
            else if (vm.selectedFilterOption === vm.FILTER_OPTION.NO_PACKAGES) {
                vm.placementList = lodash.filter(vm.placementList, function (item) {
                    return Utils.isUndefinedOrNull(item.packageName);
                });
            }
        }

        function updatePlacementListStatus() {
            angular.forEach(vm.updatedList, function (placement) {
                var found = lodash.find(vm.placementList, function (updatedPlacement) {
                    return updatedPlacement.placementId === placement.placementId;
                });

                lodash.extend(found, placement);
            });
        }

        function addPlacement() {
            $state.go('add-placement');
        }

        function selectRows(selection) {
            vm.selectedRows = selection;
            vm.enableActivate = false;
            vm.enableDeactivate = false;

            return lodash.find(selection, function (item) {
                if (item.status !== vm.STATUS.ACCEPTED) {
                    vm.enableActivate = true;
                }

                if (item.status !== vm.STATUS.REJECTED) {
                    vm.enableDeactivate = true;
                }
            });
        }

        function changePlacementsStatus(newStatus) {
            angular.forEach(vm.selectedRows, function (placement) {
                placement.status = newStatus;

                var found = lodash.find(vm.updatedList, function (updatedPlacement) {
                    return updatedPlacement.placementId === placement.placementId;
                });

                if (found) {
                    lodash.extend(found, placement);
                }
                else {
                    placement.id = placement.placementId; //Creating id property for API packagePlacementView object.
                    vm.updatedList.push(placement);
                }

                InsertionOrderUtilService.setHasPendingChanges(true);
            });

            checkInactiveStatus();
            refreshTable();
        }

        function checkInactiveStatus() {
            var inactivePlacement = lodash.find(vm.placementList, function (placement) {
                return placement.status !== vm.STATUS.REJECTED;
            });

            if (lodash.isUndefined(inactivePlacement)) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.INFORMATIONAL,
                    title: $translate.instant('global.warning'),
                    description: $translate.instant('placement.warningInactive'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.OK
                });
            }
        }

        function save() {
            angular.forEach(vm.updatedList, function (placement) {
                placement.startDate = DateTimeService.inverseParse(DateTimeService.getStartDate(placement.startDate));
                placement.endDate = DateTimeService.inverseParse(DateTimeService.getEndDate(placement.endDate));
            });

            vm.promise = PlacementService.updatePlacementsStatus(ioId, vm.updatedList);
            vm.promise.then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    $scope.$parent.$parent.vm.activate();
                    activate();
                });
        }

        function close() {
            $state.go('campaign-io-list', {
                campaignId: $stateParams.campaignId
            });
        }

        function getStatusName(key) {
            return lodash.find(vm.statusList, function (item) {
                return item.key === key;
            }).name;
        }

        function refreshTable() {
            var placementListBackup = [];

            angular.copy(vm.placementList, placementListBackup);
            vm.placementList = [];
            angular.copy(placementListBackup, vm.placementList);
        }

        function editPlacement(placement) {
            if (angular.isDefined(placement.packageId)) {
                $state.go('edit-package', {
                    campaignId: placement.campaignId,
                    ioId: placement.ioId,
                    packageId: placement.packageId,
                    from: 'placement-list'
                });
            }
            else {
                $state.go('edit-placement', {
                    campaignId: placement.campaignId,
                    ioId: placement.ioId,
                    placementId: placement.placementId,
                    from: 'placement-list'
                });
            }
        }

        function navigateDestination(data) {
            InsertionOrderUtilService.setHasPendingChanges(false);
            if (data.action) {
                data.action();
            }
            else {
                InsertionOrderUtilService.goRouteDestiny();
            }
        }

        $scope.$on('ioPlacements.hasUnsavedChanges', function (event, data) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('global.confirm.closeUnsavedChanges'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
            }).result.then(
                function () {
                    navigateDestination(data);
                });
        });

        function initFilterWatcher() {
            $scope.$watch('vm.selectedFilterOption', function (newVal) {
                if (Utils.isUndefinedOrNull(vm.selectedFilterOption)) {
                    vm.selectedFilterOption = vm.FILTER_OPTION.ALL;
                }
                else {
                    vm.selectedFilterOption = newVal;
                    filterPlacements();
                }
            });
        }
    }
})();
