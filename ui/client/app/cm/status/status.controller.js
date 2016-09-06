(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('StatusController', StatusController);

    StatusController.$inject = [
        '$log',
        '$translate',
        'StatusService'
    ];

    function StatusController($log,
                              $translate,
                              StatusService) {
        var vm = this;

        vm.promise = null;
        vm.status = null;
        vm.error = null;
        vm.calculatedStatus = null;

        activate();

        function activate() {
            var promise = vm.promise = StatusService.getStatus();

            promise.then(function (data) {
                $log.debug('got status data', data);
                vm.status = data;
                calcStatus(data);
            }).catch(function (error) {
                $log.warn('didn\'t get status', error);
                vm.error = $translate.instant('status.error');
            });
        }

        function calcStatus(status) {
            vm.calculatedStatus = status.cmDbConnectionValid && status.metricsDbConnectionValid;
        }
    }
})();
