(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('package-list', {
                parent: 'package-tab',
                url: '/list',
                views: {
                    'package.list': {
                        templateUrl: 'app/cm/campaigns/io-tab/package-tab/package-list/package-list.html',
                        controller: 'PackageListController',
                        controllerAs: 'vmPackageList'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
