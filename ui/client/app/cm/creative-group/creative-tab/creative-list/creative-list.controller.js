(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupCreativeListController', CreativeGroupCreativeListController);

    CreativeGroupCreativeListController.$inject = [
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CreativeGroupService',
        'CreativeUtilService',
        'ErrorRequestHandler',
        'Utils',
        'lodash'
    ];

    function CreativeGroupCreativeListController(
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CreativeGroupService,
        CreativeUtilService,
        ErrorRequestHandler,
        Utils,
        lodash) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            creativeGroupId = $stateParams.creativeGroupId;

        vm.addCreative = addCreative;
        vm.creativeTotal = 0;
        vm.creatives = null;
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

        function getCreatives() {
            vm.promise = CreativeGroupService.getCreativeList(creativeGroupId);
            vm.promise.then(
                function (creatives) {
                    vm.creatives = lodash.pluck(creatives || [], 'creative');
                    vm.creativeTotal = Utils.isUndefinedOrNull(creatives) ? 0 : creatives.length;
                }).catch(function (error) {
                    ErrorRequestHandler.handle('Cannot get the creatives list', error);
                });
        }

        function addCreative() {
            $state.go('creative-group-creative-add');
        }

        function editCreative(creative) {
            $state.go('campaign-creative-details', {
                campaignId: campaignId,
                creativeId: creative.id,
                creativeGroupId: creativeGroupId,
                from: 'creative-group-creative-list'
            });
        }

        function deleteCreative(creativeId) {
            var params = {
                tab: 'creative-group',
                creativeId: creativeId,
                creativeGroupId: creativeGroupId
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
