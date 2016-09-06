(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PackageAddPlacementController', PackageAddPlacementController);

    PackageAddPlacementController.$inject = [
        '$scope',
        '$stateParams',
        'PackageService'
    ];

    function PackageAddPlacementController(
        $scope,
        $stateParams,
        PackageService) {
        var vm = this,
            packageId = $stateParams.packageId;

        vm.step = $scope.$parent.vmAssoc.STEP.ADD_PLACEMENTS;
        activate();

        function activate() {
            $scope.$parent.vmAssoc.promise = PackageService.getPackage(packageId);
            $scope.$parent.vmAssoc.promise.then(function (pack) {
                $scope.$parent.vmAssoc.package = pack;
            });
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                activate();
            }
        });
    }
})();
