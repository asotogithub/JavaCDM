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
            .state('add-creative', {
                parent: 'creative-tab',
                url: '/add',
                views: {
                    'creative.add': {
                        templateUrl: 'app/cm/campaigns/creative-tab/creative-add/creative-add.html',
                        controller: 'CampaignCreativeAddController',
                        controllerAs: 'vmAdd'
                    }
                },
                ncyBreadcrumb: {
                    skip: true
                }
                //TODO define if any kind of permission is needed
                //permission: CONSTANTS.PERMISSION.CREATIVE.ADD
            });
    }
})();
