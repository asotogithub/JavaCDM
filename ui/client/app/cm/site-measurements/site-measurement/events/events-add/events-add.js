(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider'
    ];

    function State(
        $stateProvider) {
        $stateProvider
            .state('add-event', {
                parent: 'sm-events',
                url: '/add',
                views: {
                    'events.add': {
                        templateUrl: 'app/cm/site-measurements/site-measurement/events/events-add/events-add.html',
                        controller: 'AddEventController',
                        controllerAs: 'vmAddEvent'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
