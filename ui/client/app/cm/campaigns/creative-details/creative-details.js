(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-creative-details', {
                parent: 'campaigns',
                url: '/:campaignId/creative/:creativeId?from&creativeGroupId',
                templateUrl: 'app/cm/campaigns/creative-details/creative-details.html',
                controller: 'CampaignCreativeDetailsController',
                controllerAs: 'vmDetails',
                redirectTo: 'campaign-creative-details-tab',
                ncyBreadcrumb: {
                    label: '{{vmDetails.creativeName || creativeName || "creative.newCreative" | translate}}',
                    parent: function ($scope) {
                        if ($scope.callingState === 'creative-group-creative-list') {
                            return 'creative-group';
                        }
                        else {
                            return 'campaign-details';
                        }
                    }
                }
            });
    }
})();
