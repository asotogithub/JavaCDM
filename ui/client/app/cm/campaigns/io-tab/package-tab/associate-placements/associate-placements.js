(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('associate-placements', {
                parent: 'package-tab',
                url: '/:packageId/edit/associate-placements?from',
                views: {
                    'package.associations': {
                        templateUrl:
                        'app/cm/campaigns/io-tab/package-tab/associate-placements/associate-placements.html',
                        controller: 'AssociatePlacementsController',
                        controllerAs: 'vmAssoc'
                    }
                },
                ncyBreadcrumb: {
                    label: '{{"package.associateToPackage" | translate}}',
                    parent: 'edit-package'
                }
            });
    }
})();
