(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('schedule-tab', {
                parent: 'campaign-details',
                url: 'schedule',
                templateUrl: 'app/cm/campaigns/schedule-tab/schedule-tab.html',
                controller: 'ScheduleTabController',
                controllerAs: 'vmList',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
