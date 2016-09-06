(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PlacementEditController', PlacementEditController);

    PlacementEditController.$inject = [
        '$q',
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
        'ErrorRequestHandler',
        'InsertionOrderService',
        'PlacementService',
        'PackageService',
        'SectionService',
        'SizeService',
        'Utils',
        'lodash'
    ];

    function PlacementEditController(
        $q,
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
        ErrorRequestHandler,
        InsertionOrderService,
        PlacementService,
        PackageService,
        SectionService,
        SizeService,
        Utils,
        lodash) {
        var vm = this,
            fromState = $stateParams.from,
            packageName = $stateParams.packageName,
            COUNT_PROPERTIES = 5,
            packageId;

        vm.REGEX_PLACEMENT_NAME = CONSTANTS.REGEX.PLACEMENT_SECTION_SITE_NAME;
        vm.extendedPropertiesArray = [];
        vm.extProp = 'extProp';
        vm.placement = null;
        vm.statusList = lodash.values(CampaignsUtilService.getIOStatusList());
        vm.maxLength = CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME;
        vm.extendedPropertiesmaxLength = CONSTANTS.INPUT_MAX_LENGTH;
        vm.save = save;
        vm.cancel = cancel;
        vm.checkLastActive = checkLastActive;
        vm.costDetails = [];
        vm.editForm = {};
        vm.compareSize = compareSize;
        vm.removeAssociation = removeAssociation;
        vm.backButton = $translate.instant('placement.listOfPlacements');

        activate();

        function activate() {
            vm.promise = $q.all([
                PlacementService.getPlacement($stateParams.placementId),
                SizeService.getList()
            ]);

            vm.promise.then(function (args) {
                var status,
                    sizeInfo;

                vm.placement = args[0];
                vm.sizeList = args[1];
                getSectionList(vm.placement.siteId, vm.placement.siteSectionId);
                vm.placement.sizeSelected = filterById(vm.placement.sizeId, vm.sizeList);

                if (Utils.isUndefinedOrNull(vm.placement.sizeSelected)) {
                    sizeInfo = {
                        height: vm.placement.height,
                        id: vm.placement.sizeId,
                        label: vm.placement.sizeName,
                        width: vm.placement.width
                    };

                    vm.sizeList.push(sizeInfo);
                    vm.placement.sizeSelected = sizeInfo;
                }

                if (vm.sizeList.length > 0) {
                    vm.sizeList.sort(compareSize);
                }

                vm.statusList.splice(0, 1);
                status = filterById(vm.placement.status, vm.statusList);
                vm.placement.statusSelected = status ? status : vm.statusList[0];
                vm.placementName = vm.placement.name;

                if (Utils.isUndefinedOrNull(vm.placement.packageId)) {
                    $scope.$broadcast('loadListCost', {
                        activate: true,
                        rootData: vm.placement.costDetails
                    });
                }
                else {
                    packageId = vm.placement.packageId;
                    vm.backButton = $translate.instant('placement.summary');
                    vm.placement.packageName = packageName;

                    vm.promise = PackageService.getPackage(packageId);
                    vm.promise.then(function (pack) {
                        vm.placement.isLast = pack.placementCount === 1;
                    });
                }

                populateExtendedProperties(vm.placement);
            });
        }

        function compareSize(sizeA, sizeB) {
            if (sizeA.width < sizeB.width) {
                return -1;
            }

            if (sizeA.width > sizeB.width) {
                return 1;
            }

            if (sizeA.height < sizeB.height) {
                return -1;
            }

            if (sizeA.height > sizeB.height) {
                return 1;
            }

            return 0;
        }

        function getSectionList(idSite, idSection) {
            var sectionSelected = null;

            vm.promise = SectionService.getList(idSite);
            vm.promise.then(function (result) {
                vm.sectionList = result;
                sectionSelected = filterById(idSection, vm.sectionList);
                if (sectionSelected) {
                    vm.placement.sectionName = sectionSelected.name;
                }
            });
        }

        function filterById(id, arrayElements) {
            if (arrayElements.length < 1) {
                return null;
            }

            var res = lodash.find(arrayElements, function (item) {
                if (item.id) {
                    return item.id === id;
                }

                if (item.key) {
                    return item.key === id;
                }
            });

            if (!res) {
                return null;
            }

            return res;
        }

        function save() {
            var newArrayCostDetail;

            if (Utils.isUndefinedOrNull(vm.placement.packageId)) {
                newArrayCostDetail = vm.placement.costDetails.splice(0, -10); //Removing last 10 cost detail.

                newArrayCostDetail.push.apply(newArrayCostDetail, vm.costDetails);
                vm.placement.costDetails = newArrayCostDetail;
            }
            else {
                vm.placement.costDetails = [];
            }

            vm.promise = PlacementService.updatePlacement(formatPlacement(vm.placement));
            vm.promise.then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    vm.placementName = vm.placement.name;
                    vm.editForm.$invalid = true;
                    vm.editForm.$dirty = false;
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

        function formatPlacement(placement) {
            var placementResult = {},
                name,
                i;

            placementResult.id = placement.id;
            placementResult.campaignId = placement.campaignId;
            placementResult.ioId = placement.ioId;
            placementResult.name = placement.name;
            placementResult.countryCurrencyId = placement.countryCurrencyId;
            placementResult.height = placement.height;
            placementResult.width = placement.width;
            placementResult.sectionName = placement.sectionName;
            placementResult.siteId = placement.siteId;
            placementResult.siteSectionId = placement.siteSectionId;
            placementResult.sizeId = placement.sizeSelected.id;
            placementResult.status = placement.statusSelected.key;
            placementResult.utcOffset = placement.utcOffset;
            if (placement.costDetails.length <= 0) {
                placementResult.packageId = placement.packageId;
            }
            else {
                placementResult.costDetails = formatCostDetails(placement.costDetails);
            }

            if (vm.extendedPropertiesArray.length) {
                for (i = 1; i <= COUNT_PROPERTIES; i++) {
                    name = vm.extProp + i;
                    placementResult[name] = vm.extendedPropertiesArray[i - 1].value;
                }
            }

            return placementResult;
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

        function cancel() {
            if (vm.editForm.$dirty) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                    title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                    description: $translate.instant('global.confirm.closeUnsavedChanges'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
                }).result.then(
                    function () {
                        goBack();
                    });
            }
            else {
                goBack();
            }
        }

        function goBack() {
            if (fromState === 'io-list') {
                $state.go('placement-list');
            }
            else if (fromState === 'placement-list') {
                $state.go('placement-list');
            }
            else if (fromState === 'edit-package') {
                $state.go(fromState, {
                    campaignId: $stateParams.campaignId,
                    ioId: vm.placement.ioId,
                    packageId: packageId,
                    from: 'placement-list'
                });
            }
            else {
                $rootScope.$broadcast('redirect.home-page');
            }
        }

        function checkLastActive() {
            if (vm.placement.statusSelected === vm.statusList[1]) {
                vm.promise = InsertionOrderService.getPackagePlacements($stateParams.ioId);
                vm.promise.then(function (placements) {
                    var inactivePlacement = lodash.find(placements, function (placement) {
                        return placement.status !== CONSTANTS.STATUS.REJECTED &&
                            placement.placementId !== parseInt($stateParams.placementId);
                    });

                    if (Utils.isUndefinedOrNull(inactivePlacement)) {
                        DialogFactory.showCustomDialog({
                            type: CONSTANTS.DIALOG.TYPE.INFORMATIONAL,
                            title: $translate.instant('global.warning'),
                            description: $translate.instant('placement.warningInactive'),
                            buttons: CONSTANTS.DIALOG.BUTTON_SET.OK
                        });
                    }
                });
            }
        }

        function populateExtendedProperties(placement) {
            var i,
                name,
                value;

            for (i = 1; i <= COUNT_PROPERTIES; i++) {
                name = vm.extProp + i;
                if (placement !== null) {
                    value = placement[name];
                }

                vm.extendedPropertiesArray.push({
                    value: value,
                    name: name
                });
            }
        }

        function showDialogError(error) {
            ErrorRequestHandler.handle('Error while performing update placement request', error);
        }

        function removeAssociation() {
            if (vm.placement.isLast) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    description: $translate.instant('placement.disassociateFromPackage'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.OK,
                    dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
                });
            }
            else {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    description: $translate.instant('package.placementAssociatedRemoveWarning'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL,
                    dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
                }).result.then(
                    function () {
                        vm.promise = PlacementService.disassociateFromPackage(vm.placement.id);
                        vm.promise.then(
                            function () {
                                DialogFactory.showDismissableMessage(
                                    DialogFactory.DISMISS_TYPE.SUCCESS,
                                    'info.operationCompleted'
                                );
                                activate();
                            }).catch(function (error) {
                                if (!Utils.isUndefinedOrNull(error.data) &&
                                    !Utils.isUndefinedOrNull(error.data.error) &&
                                    !Utils.isUndefinedOrNull(error.data.error.code)) {
                                    DialogFactory.showCustomDialog({
                                        type: CONSTANTS.DIALOG.TYPE.ERROR,
                                        title: $translate.instant('global.error'),
                                        description: $translate.instant('placement.disassociateFromPackage')
                                    });
                                }
                                else {
                                    ErrorRequestHandler.handleAndReject(error.data.error.message)(error);
                                }
                            });
                    });
            }
        }
    }
})();
