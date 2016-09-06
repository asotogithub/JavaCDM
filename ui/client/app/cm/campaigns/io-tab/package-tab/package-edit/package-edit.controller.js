(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PackageEditController', PackageEditController);

    PackageEditController.$inject = [
        '$rootScope',
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsUtilService',
        'CostUtilService',
        'DateTimeService',
        'DialogFactory',
        'PackageService',
        'Utils',
        'lodash'
    ];

    function PackageEditController(
        $rootScope,
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsUtilService,
        CostUtilService,
        DateTimeService,
        DialogFactory,
        PackageService,
        Utils,
        lodash) {
        var vm = this,
            packageId = $stateParams.packageId,
            ioId = $stateParams.io,
            fromState = $stateParams.from,
            removingFirst = CONSTANTS.PLACEMENT.COST_DETAIL.REMOVING.FIRST,
            removingLast = CONSTANTS.PLACEMENT.COST_DETAIL.REMOVING.LAST;

        vm.options = {
            showAdd: true
        };

        vm.addPlacement = addPlacement;
        vm.cancel = cancel;
        vm.getStatusName = getStatusName;
        vm.maxLength = CONSTANTS.INPUT.MAX_LENGTH.MEDIA_PACKAGE;
        vm.save = save;
        vm.selectRows = selectRows;
        vm.statusList = lodash.values(CampaignsUtilService.getIOStatusList());
        vm.costDetails = [];
        vm.backButton = $translate.instant('package.listOfPackages');

        activate();

        function selectRows(placement) {
            $state.go('edit-placement', {
                campaignId: placement.campaignId,
                ioId: placement.ioId,
                placementId: placement.id,
                from: 'edit-package',
                packageName: vm.package.name
            });
        }

        function activate() {
            if (fromState === 'placement-list') {
                vm.backButton = $translate.instant('placement.listOfPlacements');
            }

            vm.promise = PackageService.getPackage(packageId);
            vm.promise.then(function (pack) {
                vm.name = pack.name;
                vm.package = pack;
                if (!Utils.isUndefinedOrNull(pack.placements)) {
                    vm.placementsCount = pack.placements.length;
                    PackageService.addAllowRemoveProperty(vm.package.placements);
                }

                $scope.$broadcast('loadListCost', {
                    activate: true,
                    rootData: vm.package.costDetails
                });
            });
        }

        function addPlacement() {
            $state.go('associate-placements', {
                packageId: packageId,
                from: fromState
            });
        }

        function save() {
            var newArrayCostDetail;

            if (!Utils.isUndefinedOrNull(vm.package.placements) && vm.package.placements.length > 0) {
                //Removing last 10 cost detail.
                newArrayCostDetail = vm.package.costDetails.splice(removingFirst, removingLast);
                newArrayCostDetail.push.apply(newArrayCostDetail, vm.costDetails);
                vm.package.costDetails = newArrayCostDetail;
                vm.promise = PackageService.updatePackage(packageId, formatPackage(vm.package));
                vm.promise.then(
                    function () {
                        DialogFactory.showDismissableMessage(
                            DialogFactory.DISMISS_TYPE.SUCCESS,
                            'info.operationCompleted'
                        );
                        goBack();
                    });
            }
            else {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.ERROR,
                    title: $translate.instant('global.error'),
                    description: $translate.instant('placement.disassociateFromPackage')
                });
            }
        }

        function formatPackage(pack) {
            var packageResult = {};

            packageResult.id = pack.id;
            packageResult.campaignId = pack.campaignId;
            packageResult.countryCurrencyId = pack.countryCurrencyId;
            packageResult.description = pack.description;
            packageResult.name = pack.name;
            packageResult.costDetails = formatCostDetails(pack.costDetails);
            packageResult.placements = formatPlacements(pack.placements);

            return packageResult;
        }

        function formatCostDetails(costDetailsPlacement) {
            var arrayCostDetails = [],
                newCostDetail;

            angular.forEach(costDetailsPlacement, function (costDetail) {
                newCostDetail = {};
                newCostDetail.rateType = (CostUtilService.getRateType(costDetail.rateType.VALUE)).VALUE;
                newCostDetail.startDate = DateTimeService.inverseParse(
                    DateTimeService.getStartDate(costDetail.startDate)
                );
                newCostDetail.inventory = costDetail.inventory;
                newCostDetail.margin = costDetail.margin;
                newCostDetail.plannedGrossAdSpend = costDetail.plannedGrossAdSpend;
                newCostDetail.plannedGrossRate = costDetail.plannedGrossRate;
                newCostDetail.plannedNetAdSpend = costDetail.plannedNetAdSpend;
                newCostDetail.plannedNetRate = costDetail.plannedNetRate;
                if (costDetail.endDate !== null) {
                    newCostDetail.endDate = DateTimeService.inverseParse(
                        DateTimeService.getEndDate(costDetail.endDate)
                    );
                }

                if (costDetail.id !== '') {
                    newCostDetail.id = costDetail.id;
                }

                arrayCostDetails.push(newCostDetail);
            });

            return arrayCostDetails;
        }

        function formatPlacements(placementList) {
            var arrayPlacements = [],
                newPlacement;

            angular.forEach(placementList, function (placement) {
                newPlacement = {};
                newPlacement.id = placement.id;
                arrayPlacements.push(newPlacement);
            });

            return arrayPlacements;
        }

        function cancel() {
            goBack();
        }

        function goBack() {
            switch (fromState) {
                case 'io-list':
                    goToPackageList();
                    break;
                case 'placement-list':
                    $state.go('placement-list');
                    break;
                case 'package-list':
                    goToPackageList();
                    break;
                default:
                    $rootScope.$broadcast('redirect.home-page');
            }
        }

        function goToPackageList() {
            $state.go('package-list', {
                campaignId: $stateParams.campaignId,
                io: ioId,
                packageId: packageId,
                from: 'package-list'
            });
        }

        function getStatusName(key) {
            return lodash.find(vm.statusList, function (item) {
                return item.key === key;
            }).name;
        }
    }
})();
