(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreatePlacementsController', CreatePlacementsController);

    CreatePlacementsController.$inject = [
        '$log',
        '$scope',
        '$stateParams',
        'PackageService',
        'PlacementUtilService',
        'Utils'
    ];

    function CreatePlacementsController(
        $log,
        $scope,
        $stateParams,
        PackageService,
        PlacementUtilService,
        Utils) {
        $log.debug('Running CreatePlacementsController');
        var vm = this,
            campaignId = $stateParams.campaignId,
            ioId = $stateParams.ioId,
            packageId = $stateParams.packageId;

        vm.placementList = [];
        vm.options = {
            showPackageName: false
        };
        vm.step = $scope.$parent.vmAssoc.STEP.PACKAGE_ASSOCIATION;
        vm.validate = $scope.$parent.vmAssoc.validate;
        vm.addToAssociatedPlacements = addToAssociatedPlacements;

        function activate() {
            $scope.$parent.vmAssoc.promise = PackageService.getPackage(packageId);
            $scope.$parent.vmAssoc.promise.then(function (pack) {
                vm.currentPackage = pack;
            });
        }

        function addToAssociatedPlacements() {
            vm.placementList = PlacementUtilService.parserPlacementList(vm.placementList, campaignId, ioId);
            PackageService.addAllowRemoveProperty(vm.placementList);
            $scope.$parent.vmAssoc.createdPlacements = vm.placementList;

            if (Utils.isUndefinedOrNull(vm.currentPackage.placements)) {
                vm.currentPackage.placements = [];
            }

            $scope.$parent.vmAssoc.associatedPlacements = vm.currentPackage.placements =
                vm.currentPackage.placements.concat(vm.placementList);

            var newPlacement = {
                packageName: '',
                name: '',
                site: {},
                section: [],
                size: []
            };

            vm.placementList = [];
            vm.placementList.push(newPlacement);
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                activate();
            }
        });

        $scope.$on('rootScope:placementRemoved', function () {
            vm.validate(vm.currentPackage);
        });

        $scope.$watch('vmPkgCreatePlac.currentPackage.placements', function (newValue) {
            if (angular.isDefined(newValue)) {
                vm.validate(vm.currentPackage);
            }
        });
    }
})();
