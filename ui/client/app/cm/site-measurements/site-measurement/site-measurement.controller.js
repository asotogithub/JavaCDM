(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementController', SiteMeasurementController);

    SiteMeasurementController.$inject = [
        '$scope',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'SiteMeasurementModel',
        'SiteMeasurementsUtilService'
    ];

    function SiteMeasurementController(
        $scope,
        $translate,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementModel,
        SiteMeasurementsUtilService) {
        var vm = this;

        vm.nameTruncateLength = CONSTANTS.SITE_MEASUREMENT.NAME_TRUNCATE_LENGTH;
        vm.model = SiteMeasurementModel;

        function navigateDestination(data) {
            SiteMeasurementsUtilService.clearPendingChanges();
            if (data.action) {
                data.action();
            }
            else {
                SiteMeasurementsUtilService.goRouteDestiny();
            }
        }

        $scope.$on('smContent.hasUnsavedChanges', function (event, data) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('global.confirm.closeUnsavedChanges'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
            }).result.then(
                function () {
                    navigateDestination(data);
                });
        });
    }
})();
