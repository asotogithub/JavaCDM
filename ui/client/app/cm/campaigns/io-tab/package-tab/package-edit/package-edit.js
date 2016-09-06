(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('edit-package', {
                parent: 'package-tab',
                url: '/:packageId/edit?from',
                views: {
                    'package.edit': {
                        templateUrl: 'app/cm/campaigns/io-tab/package-tab/package-edit/package-edit.html',
                        controller: 'PackageEditController',
                        controllerAs: 'vmEdit'
                    }
                },
                ncyBreadcrumb: {
                    label: '{{packageName}}',
                    parent: 'package-tab'
                }
            });
    }
})();
