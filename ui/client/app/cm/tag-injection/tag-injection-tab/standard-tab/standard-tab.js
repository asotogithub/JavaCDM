(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('tag-injection-standard', {
                parent: 'tag-injection-tab',
                url: '/standard',
                templateUrl: 'app/cm/tag-injection/tag-injection-tab/standard-tab/standard-tab.html',
                controller: 'StandardTabController',
                controllerAs: 'vmTIStandard',
                ncyBreadcrumb: {
                    skip: true
                },
                params: {
                    campaign: {
                        id: null,
                        brandId: null,
                        advertiserId: null,
                        currentState: null
                    }
                }
            });
    }
})();
