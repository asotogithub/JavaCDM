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
            .state('sm-details', {
                parent: 'site-measurement',
                url: 'details',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                templateUrl: 'app/cm/site-measurements/site-measurement/details/details.html',
                controller: 'SiteMeasurementDetailsController',
                controllerAs: 'vmDetails',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
