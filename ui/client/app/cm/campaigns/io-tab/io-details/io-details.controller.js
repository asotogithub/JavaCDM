(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignIoDetailsController', CampaignIoDetailsController);

    CampaignIoDetailsController.$inject = [
        '$q',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'InsertionOrderService'
    ];

    function CampaignIoDetailsController(
        $q,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        InsertionOrderService) {
        var vm = this,
            ioId = $stateParams.ioId;

        vm.activate = activate;
        vm.backToList = backToList;

        activate();

        function activate() {
            var id = ioId,
                ioPromise = InsertionOrderService.getInsertionOrder(id);

            vm.promise = $q.all([ioPromise]).then(function (promises) {
                vm.io = promises[0];
                vm.metrics = [
                    {
                        key: $translate.instant('placement.totalPlacements'),
                        value: vm.io.placementsCount,
                        icon: CONSTANTS.KPI.ICONS.TOTAL,
                        type: 'number'
                    },
                    {
                        key: $translate.instant('placement.activePlacements'),
                        value: vm.io.activePlacementCounter,
                        icon: CONSTANTS.KPI.ICONS.ACTIVE,
                        type: 'number'
                    },
                    {
                        key: $translate.instant('placement.totalIOAdSpend'),
                        value: vm.io.totalAdSpend,
                        icon: CONSTANTS.KPI.ICONS.SPEND,
                        type: 'currency'
                    }
                ];
            });
        }

        function backToList() {
            $state.go('campaign-io-list', {
                campaignId: $stateParams.campaignId
            });
        }
    }
})();
