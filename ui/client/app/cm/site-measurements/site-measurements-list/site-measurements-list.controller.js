(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementsListController', SiteMeasurementsListController);

    SiteMeasurementsListController.$inject = [
        '$state',
        '$translate',
        'CONSTANTS',
        'DateTimeService',
        'DialogFactory',
        'SiteMeasurementModel',
        'SiteMeasurementsService',
        'UserService',
        'Utils',
        'UtilsCDMService'
    ];

    function SiteMeasurementsListController(
        $state,
        $translate,
        CONSTANTS,
        DateTimeService,
        DialogFactory,
        SiteMeasurementModel,
        SiteMeasurementsService,
        UserService,
        Utils,
        UtilsCDMService) {
        var vm = this;

        vm.addSiteMeasurement = addSiteMeasurement;
        vm.getSiteMeasurementDetail = getSiteMeasurementDetail;
        vm.hasAddPermission = UserService.hasPermission(CONSTANTS.PERMISSION.SITE_MEASUREMENT.CREATE_SITE_MEASUREMENT);
        vm.onSearchCounter = onSearchCounter;
        vm.pageSize = CONSTANTS.SITE_MEASUREMENT.PAGE_SIZE;
        vm.retrieveSiteMeasurementList = retrieveSiteMeasurementList;
        vm.siteMeasurementsTotal = 0;
        vm.searchInputSize = CONSTANTS.SEARCH_INPUT_SIZE;
        vm.siteMeasurementList = null;
        vm.utilsCDM = UtilsCDMService;

        activate();

        function activate() {
            vm.retrieveSiteMeasurementList().then(function (result) {
                var array = result.records[0].SiteMeasurementDTO;

                vm.siteMeasurementList = array ? array : [];
                vm.siteMeasurementsTotal = vm.siteMeasurementList.length;

                angular.forEach(vm.siteMeasurementList, function (siteMeasurement) {
                    if (siteMeasurement.state === CONSTANTS.SITE_MEASUREMENT.STATES.TRAFFICKED) {
                        siteMeasurement.trafficked = true;
                    }
                    else {
                        siteMeasurement.trafficked = false;
                    }

                    siteMeasurement.events = Utils.isUndefinedOrNull(siteMeasurement.numberOfEvents) ?
                        0 : siteMeasurement.numberOfEvents;
                    siteMeasurement.status = DateTimeService.isBeforeCurrentDate(siteMeasurement.expirationDate,
                        DateTimeService.FORMAT.DATE_TIME_US) ? 'global.active' : 'global.inactive';
                });
            }).catch(showServiceError);
        }

        function addSiteMeasurement() {
            $state.go('add-sm-campaign');
        }

        function onSearchCounter(counterSearch) {
            var legendResource = 'siteMeasurement.rowsSiteMeasurement',
                legendData = {
                    rows: vm.siteMeasurementsTotal
                };

            if (parseInt(counterSearch) !== vm.siteMeasurementsTotal) {
                legendResource = 'siteMeasurement.rowsSearchSiteMeasurement';
                legendData.rowsSearch = counterSearch;
            }

            vm.measurementLegend = $translate.instant(legendResource, legendData);
        }

        function retrieveSiteMeasurementList() {
            vm.promiseSiteMeasurements = SiteMeasurementsService.getSiteMeasurements();

            return vm.promiseSiteMeasurements;
        }

        function getSiteMeasurementDetail(siteMeasurementId) {
            SiteMeasurementModel.clear();
            $state.go('sm-details', {
                siteMeasurementId: siteMeasurementId
            });
        }

        function showServiceError() {
            DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
        }
    }
})();
