(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementDetailsController', SiteMeasurementDetailsController);

    SiteMeasurementDetailsController.$inject = [
        '$state',
        '$stateParams',
        'CONSTANTS',
        'DateTimeService',
        'DialogFactory',
        'SiteMeasurementModel',
        'SiteMeasurementsService',
        'SiteMeasurementsUtilService'
    ];

    function SiteMeasurementDetailsController(
        $state,
        $stateParams,
        CONSTANTS,
        DateTimeService,
        DialogFactory,
        SiteMeasurementModel,
        SiteMeasurementsService,
        SiteMeasurementsUtilService) {
        var vm = this;

        vm.close = close;
        vm.hasChanged = hasChanged;
        vm.siteMeasurementConstants = CONSTANTS.SITE_MEASUREMENT;
        vm.model = SiteMeasurementModel;
        vm.save = save;
        vm.put = put;
        vm.goBack = goBack;

        activate();

        function activate() {
            vm.statusList = SiteMeasurementsUtilService.getStatusList();
            SiteMeasurementModel.getSiteMeasurement($stateParams.siteMeasurementId).then(function () {
                vm.selectedStatus = {
                    key: DateTimeService.isBeforeCurrentDate(vm.model.siteMeasurement.expirationDate)
                };
            });
        }

        function close() {
            $state.go('site-measurements');
        }

        function save(siteMeasurement) {
            siteMeasurement.expirationDate = SiteMeasurementsUtilService.getExpirationDate(vm.selectedStatus.key);
            vm.model.promiseRequest = put(siteMeasurement);
            processPutPromise(vm.model.promiseRequest);
            SiteMeasurementsUtilService.setHasPendingChanges(false);
        }

        function put(siteMeasurement) {
            return SiteMeasurementsService.updateSiteMeasurement(siteMeasurement, siteMeasurement.id);
        }

        function processPutPromise(putPromise) {
            putPromise.then(function (result) {
                vm.editForm.$setPristine();
                SiteMeasurementModel.setSiteMeasurement(result);
                DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
            }).catch(SiteMeasurementModel.showServiceError);
        }

        function goBack() {
            $state.go('site-measurements');
        }

        function hasChanged() {
            SiteMeasurementsUtilService.setHasPendingChanges(true);
        }
    }
})();
