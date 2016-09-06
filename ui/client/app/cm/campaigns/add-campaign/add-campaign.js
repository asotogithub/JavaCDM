(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider',
        'CONSTANTS'
    ];

    function State(
        $stateProvider,
        CONSTANTS) {
        $stateProvider
            .state('add-campaign', {
                parent: 'campaigns',
                url: '/add-campaign',
                templateUrl: 'app/cm/campaigns/add-campaign/add-campaign.html',
                controller: 'AddCampaignController',
                controllerAs: 'vmCampaign',
                permission: CONSTANTS.PERMISSION.CAMPAIGN.ADD,
                ncyBreadcrumb: {
                    label: '{{"campaign.newCampaign"| translate}}',
                    parent: 'campaigns'
                }
            });
    }
})();
