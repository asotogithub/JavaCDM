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
            .state('sm-attribution-settings', {
                parent: 'site-measurement',
                url: 'attribution-settings',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                templateUrl:
                    'app/cm/site-measurements/site-measurement/attribution-settings/attribution-settings.html',
                controller: 'SiteMeasurementAttributionSettingsController',
                controllerAs: 'vm',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
