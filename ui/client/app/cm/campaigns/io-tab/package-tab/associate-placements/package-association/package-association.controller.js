(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PackageAssociationController', PackageAssociationController);

    PackageAssociationController.$inject = [
        '$q',
        '$scope',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'PackageService',
        'PlacementService',
        'Utils'
    ];

    function PackageAssociationController(
        $q,
        $scope,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        PackageService,
        PlacementService,
        Utils) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            packageId = $stateParams.packageId,
            wURL = 'app/cm/campaigns/io-tab/package-tab/associate-placements/package-association/warning-dialog.html';

        vm.outputPlacements = [];
        vm.filterOptions = {
            filterText: [
                {
                    id: 'filterInput',
                    text: '',
                    field: ['name', 'siteName', 'sizeName', 'sectionName']
                },
                {
                    id: 'filterOutput',
                    text: '',
                    field: ['name', 'siteName', 'sizeName', 'sectionName']
                }
            ]
        };

        vm.addToAssociatedPlacements = addToAssociatedPlacements;
        vm.step = $scope.$parent.vmAssoc.STEP.PACKAGE_ASSOCIATION;
        vm.validate = $scope.$parent.vmAssoc.validate;

        function activate() {
            $scope.$parent.vmAssoc.promise = $q.all([
                PackageService.getPackage(packageId),
                PlacementService.getStandAlonePlacements(campaignId)
            ]);

            $scope.$parent.vmAssoc.promise.then(function (args) {
                vm.currentPackage = args[0];
                vm.standAlonePlacements = args[1];
                vm.outputPlacements = [];
            });
        }

        function addToAssociatedPlacements() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                partialHTML: wURL,
                partialHTMLParams: {
                    placementList: vm.outputPlacements
                },
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL,
                dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATION_ADD_WARNING_DIALOG_ID
            }).result.then(
                function () {
                    PackageService.addAllowRemoveProperty(vm.outputPlacements);

                    if (Utils.isUndefinedOrNull(vm.currentPackage.placements)) {
                        vm.currentPackage.placements = [];
                    }

                    $scope.$parent.vmAssoc.associatedPlacements = vm.currentPackage.placements =
                    vm.currentPackage.placements.concat(vm.outputPlacements);
                    vm.outputPlacements = [];
                });
        }

        function refreshStandAlonePlacements() {
            var outputModelBackup = [];

            angular.copy(vm.standAlonePlacements, outputModelBackup);
            vm.standAlonePlacements = [];
            angular.copy(outputModelBackup, vm.standAlonePlacements);
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                activate();
            }
        });

        $scope.$on('rootScope:placementRemoved', function (event, placement) {
            vm.standAlonePlacements.push(placement);
            refreshStandAlonePlacements();
            vm.validate(vm.currentPackage);
        });

        $scope.$watch('vmPkgAssoc.currentPackage.placements', function (newValue) {
            if (angular.isDefined(newValue)) {
                vm.validate(vm.currentPackage);
            }
        });
    }
})();
