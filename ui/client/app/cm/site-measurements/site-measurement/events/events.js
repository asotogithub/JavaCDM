(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('sm-events', {
                parent: 'site-measurement',
                url: 'events',
                templateUrl: 'app/cm/site-measurements/site-measurement/events/events.html',
                redirectTo: 'events-list',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
