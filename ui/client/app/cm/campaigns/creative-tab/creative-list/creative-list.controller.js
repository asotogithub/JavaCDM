(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeListController', CampaignCreativeListController);

    CampaignCreativeListController.$inject = [
        '$state',
        '$stateParams',
        '$translate',
        'CampaignsService',
        'CreativeUtilService',
        'CONSTANTS',
        'Utils'
    ];

    function CampaignCreativeListController(
        $state,
        $stateParams,
        $translate,
        CampaignsService,
        CreativeUtilService,
        CONSTANTS,
        Utils) {
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.addCreative = addCreative;
        vm.creativeTotal = 0;
        vm.deleteCreative = deleteCreative;
        vm.editCreative = editCreative;
        vm.getCreatives = getCreatives;
        vm.isCreativeXML = CreativeUtilService.isCreativeXML;
        vm.onSearchCounter = onSearchCounter;
        vm.pageSize = CONSTANTS.CREATIVE_LIST.PAGE_SIZE;

        activate();

        function activate() {
            getCreatives();
        }

        function addCreative() {
            $state.go('add-creative');
        }

        function editCreative(creative) {
            $state.go('campaign-creative-details', {
                campaignId: campaignId,
                creativeId: creative.id,
                from: 'creative-list'
            });
        }

        function getCreatives() {
            vm.promise = CampaignsService.getCreatives(campaignId);
            vm.promise.then(function (creativeList) {
                vm.creativeList = creativeList;
                vm.creativeTotal = Utils.isUndefinedOrNull(creativeList) ? 0 : creativeList.length;
            });
        }

        function deleteCreative(creativeId) {
            var params = {
                tab: 'creative',
                creativeId: creativeId
            };

            vm.promise = CreativeUtilService.deleteCreativeWithConfirmation(params, getCreatives);
        }

        function onSearchCounter(counterSearch) {
            if (parseInt(counterSearch) === vm.creativeTotal) {
                vm.creativeCounterLegend = $translate.instant('creative.rowsCreatives', {
                    rows: vm.creativeTotal
                });
            }
            else {
                vm.creativeCounterLegend = $translate.instant('creative.rowsSearchCreatives', {
                    rows: vm.creativeTotal,
                    rowsSearch: counterSearch
                });
            }
        }
    }
})();
