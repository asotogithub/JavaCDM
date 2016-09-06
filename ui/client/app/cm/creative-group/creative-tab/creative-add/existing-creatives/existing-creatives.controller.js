(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ExistingCreativesController', ExistingCreativesController);

    ExistingCreativesController.$inject = [
        '$modalInstance',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'CreativeGroupService',
        'CreativeService',
        'ErrorRequestHandler',
        'data',
        'lodash'
    ];

    function ExistingCreativesController(
        $modalInstance,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsService,
        CreativeGroupService,
        CreativeService,
        ErrorRequestHandler,
        data,
        lodash) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            creativeGroupId = $stateParams.creativeGroupId;

        vm.selectedRows = [];
        vm.existingCreative = [];
        vm.SUCCESS = CONSTANTS.CREATIVE.STATUS_UPLOAD.SUCCESS;

        vm.add = add;
        vm.closeModal = closeModal;
        vm.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.selectRows = selectRows;

        activate();

        function activate() {
            var promise = vm.promise = CreativeGroupService.getCreativeList(creativeGroupId),
                promiseQuery;

            promise.then(
                function (creatives) {
                    if (creatives.length > 0) {
                        promiseQuery = vm.promise =
                            CreativeService.getCreativeNotAssociatedByCampaignAndGroup(campaignId, creativeGroupId);
                        promiseQuery.then(
                            function (unassociatedCreatives) {
                                setCreatives(unassociatedCreatives);
                            });
                    }
                    else {
                        promiseQuery = vm.promise = CampaignsService.getCreatives(campaignId);
                        promiseQuery.then(function (creativesList) {
                            setCreatives(creativesList);
                        });
                    }
                }).catch(function (error) {
                    ErrorRequestHandler.handle('Cannot get creative list', error, function () {
                        $modalInstance.dismiss();
                    });
                });
        }

        function add() {
            angular.forEach(vm.selectedRows, function (row) {
                row.file = {
                    status: vm.SUCCESS,
                    statusTooltip: $translate.instant('creative.upload.status.success')
                };
                row.isValid = true;
                row.isExisting = true;
            });

            $modalInstance.close(vm.selectedRows);
        }

        function closeModal() {
            $modalInstance.dismiss();
        }

        function selectRows(selection) {
            vm.selectedRows = selection;
        }

        function setCreatives(creatives) {
            vm.existingCreative = lodash.select(creatives, function (item) {
                return !lodash.findWhere(data.creativeList || [], {
                    id: item.id
                });
            });
        }
    }
})();
