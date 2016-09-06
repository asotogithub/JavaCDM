(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddPlacementDetailsController', AddPlacementDetailsController);

    AddPlacementDetailsController.$inject = [
        '$log',
        '$scope'
    ];

    function AddPlacementDetailsController(
        $log,
        $scope) {
        $log.debug('Running AddPlacementDetailsController');
        var vm = this;

        vm.options = {
            showPackageName: true
        };
        vm.placementList = [];
        vm.step = $scope.$parent.vmAdd.STEP.DETAILS;

        $scope.$watch('vm.placementList.isValid', function (newVal) {
            $scope.$parent.vmAdd.placementListDetails = vm.placementList;
            vm.step.isValid = newVal ? vm.placementList.isValid : false;
        });
    }
})();
