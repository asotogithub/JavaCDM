(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeDetailsController', CampaignCreativeDetailsController);

    CampaignCreativeDetailsController.$inject = [
        '$scope',
        '$stateParams',
        '$state',
        '$translate'
    ];

    function CampaignCreativeDetailsController(
        $scope,
        $stateParams,
        $state,
        $translate) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            creativeGroupId = $stateParams.creativeGroupId;

        $scope.callingState = $stateParams.from;
        vm.backToList = backToList;
        vm.labelBackButton = $scope.callingState === 'creative-group-creative-list' ?
            $translate.instant('creativeGroup.listOfCreative') : $translate.instant('creative.listOfCreative');

        function backToList() {
            if ($scope.callingState === 'creative-group-creative-list') {
                $state.go('creative-group-creative-list', {
                    campaignId: campaignId,
                    creativeGroupId: creativeGroupId
                });
            }
            else {
                $state.go('creative-list', {
                    campaignId: campaignId
                });
            }
        }
    }
})();
