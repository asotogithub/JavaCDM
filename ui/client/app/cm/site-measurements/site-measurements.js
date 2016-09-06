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
            .state('site-measurements', {
                parent: 'main',
                url: 'site-measurements',
                templateUrl: 'app/cm/site-measurements/site-measurements.html',
                redirectTo: 'site-measurements-list',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
