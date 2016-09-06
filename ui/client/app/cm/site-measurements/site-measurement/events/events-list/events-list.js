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
            .state('events-list', {
                parent: 'sm-events',
                url: '/list',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                views: {
                    'events.list': {
                        templateUrl: 'app/cm/site-measurements/site-measurement/events/events-list/events-list.html',
                        controller: 'SiteMeasurementEventsController',
                        controllerAs: 'vmEvents'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
