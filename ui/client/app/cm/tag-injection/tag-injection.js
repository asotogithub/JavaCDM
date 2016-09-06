(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('tag-injection', {
                parent: 'main',
                url: 'tag-injection',
                templateUrl: 'app/cm/tag-injection/tag-injection.html',
                controller: 'TagInjectionController',
                controllerAs: 'vmTagInjection',
                redirectTo: 'tag-injection-tab',
                ncyBreadcrumb: {
                    label: '{{"global.tagInjection"| translate}}'
                }
            });
    }
})();
