(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AdmDetailsController', AdmDetailsController);

    AdmDetailsController.$inject = [
        '$scope',
        '$stateParams',
        'CONSTANTS',
        'DatasetService',
        'UserService'
    ];

    function AdmDetailsController(
        $scope,
        $stateParams,
        CONSTANTS,
        DatasetService,
        UserService) {
        var vm = this,
            dataId = $stateParams.id;

        vm.ADM_CONSTANTS = CONSTANTS.ADM_CONSTANTS;
        vm.activate = activate;
        vm.adm = {};
        vm.permission = {
            config: hasPermission(CONSTANTS.PERMISSION.ADM.CONFIG),
            update: hasPermission(CONSTANTS.PERMISSION.ADM.UPDATE)
        };

        activate();

        function activate() {
            DatasetService.getDatasetMetrics(dataId).then(function (promises) {
                vm.admMetrics = promises;
                $scope.$broadcast('adm.dataset.metrics', {
                    metrics: vm.admMetrics,
                    dataSetId: dataId
                });
            });
        }

        function hasPermission(permission) {
            return UserService.hasPermission(permission);
        }
    }
})();
