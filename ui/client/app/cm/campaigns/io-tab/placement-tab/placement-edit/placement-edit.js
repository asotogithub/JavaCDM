(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('edit-placement', {
                parent: 'placement-tab',
                url: '/:placementId/edit?from&packageName',
                views: {
                    'placement.edit': {
                        templateUrl: 'app/cm/campaigns/io-tab/placement-tab/placement-edit/placement-edit.html',
                        controller: 'PlacementEditController',
                        controllerAs: 'vmEdit'
                    }
                },
                ncyBreadcrumb: {
                    label: '{{vmEdit.placementName || placementName}}',
                    parent: 'placement-tab'
                }
            });
    }
})();
