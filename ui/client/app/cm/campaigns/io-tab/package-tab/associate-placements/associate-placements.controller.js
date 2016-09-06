(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AssociatePlacementsController', AssociatePlacementsController);

    AssociatePlacementsController.$inject = [
        '$log',
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'InsertionOrderService',
        'PackageService',
        'PlacementService',
        'lodash'
    ];

    function AssociatePlacementsController(
        $log,
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        InsertionOrderService,
        PackageService,
        PlacementService,
        lodash) {
        $log.debug('Running AssociatePlacementsController');
        var vm = this,
            ioId = $stateParams.ioId,
            from = $stateParams.from,
            packageId = $stateParams.packageId;

        vm.associatedPlacements = [];
        vm.createdPlacements = [];
        vm.package = {};
        vm.metrics = $scope.$parent.$parent.$parent.vm.metrics;
        vm.activateStep = activateStep;
        vm.cancel = cancel;
        vm.validate = validate;
        vm.save = save;
        vm.OPTION = {
            CREATE_NEW: {
                key: 'createNew'
            },
            SELECT_EXISTING: {
                key: 'selectExisting'
            }
        };
        //Default selection value
        vm.option = vm.OPTION.CREATE_NEW;
        vm.STEP = {
            ADD_PLACEMENTS: {
                index: 1,
                isValid: false,
                key: 'associatePlacements.addPlacements'
            },
            PACKAGE_ASSOCIATION: {
                index: 2,
                isValid: false,
                key: 'associatePlacements.packageAssociation'
            }
        };

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function cancel() {
            $log.debug('Cancel');
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('package.confirm.exitAssociatePlacementsWizard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    $log.debug('Discard');
                    goToEditPackagePage();
                },

                function () {
                    $log.debug('Keep');
                });
        }

        function goToEditPackagePage() {
            $state.go('edit-package', {
                packageId: packageId,
                from: from
            });
        }

        function save() {
            vm.package.placements = vm.associatedPlacements;
            if (vm.option === vm.OPTION.CREATE_NEW) {
                vm.promise = PlacementService.saveBulkPlacements(ioId, packageId, vm.createdPlacements);
            }
            else {
                vm.promise = PackageService.updatePackage(packageId, vm.package);
            }

            vm.promise.then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    InsertionOrderService.getInsertionOrder(ioId).then(function (ioResponse) {
                        vm.metrics[0].value = ioResponse.placementsCount;
                        vm.metrics[1].value = ioResponse.activePlacementCounter;
                        vm.metrics[2].value = ioResponse.totalAdSpend;
                    });

                    goToEditPackagePage();
                });
        }

        function validate(currentPackage) {
            if (currentPackage && currentPackage.placements) {
                var count = lodash.filter(currentPackage.placements, function (p) {
                    return p.allowRemove;
                }).length;

                vm.STEP.PACKAGE_ASSOCIATION.isValid = count > 0;
            }
        }
    }
})();
