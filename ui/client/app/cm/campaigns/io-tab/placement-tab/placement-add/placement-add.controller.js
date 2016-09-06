(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddPlacementController', AddPlacementController);

    AddPlacementController.$inject = [
        '$log',
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CostUtilService',
        'DateTimeService',
        'DialogFactory',
        'ErrorRequestHandler',
        'InsertionOrderService',
        'Utils',
        'lodash'
    ];

    function AddPlacementController(
        $log,
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CostUtilService,
        DateTimeService,
        DialogFactory,
        ErrorRequestHandler,
        InsertionOrderService,
        Utils,
        lodash) {
        $log.debug('Running AddPlacementController');
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.placementListDetails = [];
        vm.placementList = [];
        vm.STEP = {
            DETAILS: {
                index: 1,
                isValid: false,
                key: 'addPlacement.details'
            },
            PROPERTIES: {
                index: 2,
                isValid: false,
                key: 'addPlacement.properties'
            }
        };

        vm.activateStep = activateStep;
        vm.cancel = cancel;
        vm.save = save;

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function cancel() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('placement.confirm.discard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL
            }).result.then(
                function () {
                    $state.go('placement-list');
                });
        }

        function save() {
            vm.promiseRequest = InsertionOrderService.saveBulkPackagePlacement(
                $stateParams.ioId,
                formatPlacements(vm.placementList)
            ).then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'placement.successful');
                    $scope.$parent.$parent.vm.activate();
                    $state.go('placement-list');
                }).catch(function (error) {
                    if (!Utils.isUndefinedOrNull(error.data) && !Utils.isUndefinedOrNull(error.data.errors)) {
                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR,
                            error.data.errors[0].message);
                    }
                    else {
                        showDialogError(error);
                    }
                });
        }

        function addCostDetailsPlacement(listItem) {
            var costDetail = [];

            angular.forEach(listItem, function (item) {
                costDetail = [
                    {
                        startDate: DateTimeService.inverseParse(item.startDate),
                        endDate: DateTimeService.inverseParse(item.endDate),
                        rateType: (CostUtilService.getRateType(item.rateType.VALUE)).VALUE,
                        plannedGrossRate: item.rate,
                        plannedGrossAdSpend: item.adSpend,
                        countryCurrencyId: item.countryCurrencyId
                    }
                ];
                item.costDetails = costDetail;
            });
        }

        function addCostDetailsPackage(listItem) {
            var packageTemp,
                listTemp = [];

            angular.forEach(listItem, function (item) {
                packageTemp = {
                    campaignId: campaignId,
                    name: item[0].packageName,
                    description: item[0].packageName,
                    countryCurrencyId: item[0].countryCurrencyId,
                    startDate: DateTimeService.inverseParse(item[0].startDate),
                    endDate: DateTimeService.inverseParse(item[0].endDate),
                    rateType: item[0].rateType.KEY,
                    rate: item[0].rate,
                    adSpend: item[0].adSpend,
                    costDetails: [
                        {
                            startDate: DateTimeService.inverseParse(item[0].startDate),
                            endDate: DateTimeService.inverseParse(item[0].endDate),
                            rateType: (CostUtilService.getRateType(item[0].rateType.VALUE)).VALUE,
                            plannedGrossRate: item[0].rate,
                            plannedGrossAdSpend: item[0].adSpend,
                            countryCurrencyId: item[0].countryCurrencyId
                        }
                    ],
                    placements: []
                };

                angular.forEach(item, function (placementItem) {
                    packageTemp.placements.push(placementItem);
                });

                listTemp.push(packageTemp);
            });

            return listTemp;
        }

        function formatPlacements(placementList) {
            var packageList = [],
                placementStandAloneList = [],
                formatList = {},
                groupByTemp = lodash.groupBy(placementList, 'packageName');

            angular.forEach(groupByTemp, function (placement) {
                if (placement[0].packageName === '') {
                    placementStandAloneList = placement;
                }
                else {
                    packageList.push(placement);
                }
            });

            addCostDetailsPlacement(placementStandAloneList);
            formatList.packages = addCostDetailsPackage(packageList);
            formatList.placements = placementStandAloneList;

            return formatList;
        }

        function showDialogError(error) {
            ErrorRequestHandler.handle('Error while performing save package/placement request', error);
        }
    }
})();
