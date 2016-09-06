(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementAttributionSettingsController', SiteMeasurementAttributionSettingsController);

    SiteMeasurementAttributionSettingsController.$inject = [
        '$log',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'SiteMeasurementModel',
        'SiteMeasurementsService',
        'lodash'
    ];

    function SiteMeasurementAttributionSettingsController(
        $log,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementModel,
        SiteMeasurementsService,
        lodash) {
        $log.debug('Running SiteMeasurementAttributionSettingsController');

        var vm = this;

        vm.ATTRIBUTION_METHODOLOGY_OPTIONS = [
            {
                id: 'CLICK',
                text: 'global.lastClick'
            },
            {
                id: 'FIRST',
                text: 'global.lastTouch'
            }
        ];
        vm.ATTRIBUTION_SETTINGS = CONSTANTS.ATTRIBUTION_SETTINGS;
        vm.enableLeftSide = enableLeftSide;
        vm.enableRightSide = enableRightSide;
        vm.leftSideDisabled = true;
        vm.model = SiteMeasurementModel;
        vm.put = put;
        vm.rightSideDisabled = true;
        vm.save = save;

        activate();

        function activate() {
            SiteMeasurementModel.setSiteMeasurementId($stateParams.siteMeasurementId);
            getData();
        }

        function disableAll() {
            vm.leftSideDisabled = true;
            vm.rightSideDisabled = true;
        }

        function enableLeftSide() {
            showWarningMessage();
            vm.leftSideDisabled = false;
        }

        function enableRightSide() {
            showWarningMessage();
            vm.rightSideDisabled = false;
        }

        function formatInput(siteMeasurement) {
            if (siteMeasurement.assocMethod === null || siteMeasurement.assocMethod === undefined) {
                siteMeasurement.assocMethodView = vm.ATTRIBUTION_METHODOLOGY_OPTIONS[0];
            }
            else {
                var selected = lodash.find(vm.ATTRIBUTION_METHODOLOGY_OPTIONS, function (item) {
                    return item.id === siteMeasurement.assocMethod;
                });

                siteMeasurement.assocMethodView = selected;
            }

            if (siteMeasurement.clWindow === null || siteMeasurement.clWindow === undefined ||
                siteMeasurement.clWindow === 0) {
                siteMeasurement.clWindowView = vm.ATTRIBUTION_SETTINGS.CLICK.DEFAULT;
            }
            else {
                siteMeasurement.clWindowView = siteMeasurement.clWindow;
            }

            if (siteMeasurement.vtWindow === null || siteMeasurement.vtWindow === undefined ||
                siteMeasurement.vtWindow === 0) {
                siteMeasurement.vtWindowView = vm.ATTRIBUTION_SETTINGS.VIEW.DEFAULT;
            }
            else {
                siteMeasurement.vtWindowView = siteMeasurement.vtWindow;
            }
        }

        function formatOutput(siteMeasurement) {
            siteMeasurement.assocMethod = siteMeasurement.assocMethodView.id;
            siteMeasurement.clWindow = siteMeasurement.clWindowView;
            siteMeasurement.vtWindow = siteMeasurement.vtWindowView;

            return siteMeasurement;
        }

        function getData() {
            SiteMeasurementModel.getData($stateParams.siteMeasurementId).then(function (result) {
                var siteMeasurement = result[0];

                SiteMeasurementModel.setSiteMeasurement(siteMeasurement);
                formatInput(siteMeasurement);
            }).catch(SiteMeasurementModel.showServiceError);
        }

        function processPutPromise(putPromise) {
            putPromise.then(function (result) {
                    vm.editForm.$setPristine();
                    SiteMeasurementModel.setSiteMeasurement(result);
                    formatInput(result);
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    disableAll();
                },

                function (error) {
                    $log.error('Error while updating the Site Measurement: ' + angular.toJson(error));
                    DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
                });
        }

        function put(siteMeasurement) {
            return SiteMeasurementsService.updateSiteMeasurement(siteMeasurement, $stateParams.siteMeasurementId);
        }

        function save(siteMeasurement) {
            var output = formatOutput(siteMeasurement);

            vm.model.promiseRequest = put(output);
            processPutPromise(vm.model.promiseRequest);
        }

        function showWarningMessage() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.INFORMATIONAL,
                title: $translate.instant('global.warning'),
                description: $translate.instant('attributionSettings.warningMessage'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK
            });
        }
    }
})();
