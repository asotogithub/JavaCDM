(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupsTabController', CreativeGroupsTabController);

    CreativeGroupsTabController.$inject = [
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'Utils'
    ];

    function CreativeGroupsTabController($state, $stateParams, $translate, CONSTANTS, CampaignsService, Utils) {
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.DEFAULT_NAME = CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME;
        vm.campaignId = campaignId;
        vm.creativeGroups = null;
        vm.getCreativeGroupDetails = getCreativeGroupDetails;
        vm.onCounterSearch = onCounterSearch;
        vm.promise = null;
        vm.totalCreativeGroups = 0;
        activate();

        function activate() {
            var promise = vm.promise = CampaignsService.getCreativeGroups(campaignId);

            promise.then(function (creativeGroups) {
                vm.creativeGroups = creativeGroups;
                vm.totalCreativeGroups = Utils.isUndefinedOrNull(creativeGroups) ? 0 : creativeGroups.length;
            });
        }

        function getCreativeGroupDetails(creativeGroupId) {
            $state.go('creative-group', {
                campaignId: campaignId,
                creativeGroupId: creativeGroupId
            });
        }

        function onCounterSearch(counterSearch) {
            var legendResource = 'creativeGroup.rowsCreativeGroups',
                legendData = {
                    rows: vm.totalCreativeGroups
                };

            if (parseInt(counterSearch) !== vm.totalCreativeGroups) {
                legendResource = 'creativeGroup.rowsSearchCreativeGroups';
                legendData.rowsSearch = counterSearch;
            }

            vm.legendCreativeGroups = $translate.instant(legendResource, legendData);
        }
    }
})();
