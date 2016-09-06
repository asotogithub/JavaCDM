(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('tag-injection-tab', {
                parent: 'tag-injection',
                templateUrl: 'app/cm/tag-injection/tag-injection-tab/tag-injection-tab.html',
                controller: 'TagInjectionTabController',
                controllerAs: 'vmTagInjectionTab',
                redirectTo: 'tag-injection-standard',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
