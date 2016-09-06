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
            .state('site-measurements-list', {
                parent: 'site-measurements',
                url: '/',
                templateUrl: 'app/cm/site-measurements/site-measurements-list/site-measurements-list.html',
                controller: 'SiteMeasurementsListController',
                controllerAs: 'mainSiteMeasurements',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                ncyBreadcrumb: {
                    label: '{{"nav.siteMeasurement"| translate}}'
                }
            });
    }
})();
