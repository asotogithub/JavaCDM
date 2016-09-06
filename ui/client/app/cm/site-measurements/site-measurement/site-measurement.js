(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider',
        'CONSTANTS'
    ];

    function State(
        $stateProvider,
        CONSTANTS) {
        $stateProvider
            .state('site-measurement', {
                parent: 'site-measurements',
                url: '/id/:siteMeasurementId/',
                templateUrl: 'app/cm/site-measurements/site-measurement/site-measurement.html',
                controller: 'SiteMeasurementController',
                controllerAs: 'vm',
                redirectTo: 'sm-details',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                ncyBreadcrumb: {
                    label: '{{vm.model.siteMeasurement.shortName}}',
                    parent: 'site-measurements-list'
                }
            });
    }
})();
