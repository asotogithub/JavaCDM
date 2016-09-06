(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeAssignController', CampaignCreativeAssignController);

    CampaignCreativeAssignController.$inject = [
        '$log',
        '$scope',
        '$stateParams',
        'CONSTANTS',
        'CampaignsService',
        'lodash'
    ];

    function CampaignCreativeAssignController(
        $log,
        $scope,
        $stateParams,
        CONSTANTS,
        CampaignsService,
        lodash) {
        $log.debug('Running CampaignCreativeAssignController');
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.DEFAULT_NAME = CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME;
        vm.hasNewCreatives = false;
        vm.hasVersionedCreatives = false;
        vm.selectRows = selectRows;
        vm.selectedRows = [];
        vm.step = $scope.$parent.vmAdd.STEP.ASSIGN;

        activate();

        function activate() {
            vm.promise = CampaignsService.getCreativeGroups(campaignId);
            vm.promise.then(function (creativeGroups) {
                vm.creativeGroups = creativeGroups;
            });
        }

        function selectRows(selection) {
            $scope.$parent.vmAdd.creativeGroups = vm.selectedRows = selection;
        }

        $scope.$watch('vm.selectedRows.length', function (newVal) {
            vm.step.isValid = newVal > 0;
        });

        $scope.$on('addCreative.assign', function () {
            var totalCreatives = $scope.$parent.vmAdd.creatives.length,
                versionedCreatives = lodash.filter($scope.$parent.vmAdd.creatives, 'versions').length;

            vm.hasNewCreatives = versionedCreatives < totalCreatives;
            vm.hasVersionedCreatives = versionedCreatives > 0;

            if (versionedCreatives === totalCreatives) {
                vm.step.isValid = true;
            }
            else {
                vm.step.isValid = vm.selectedRows.length > 0;
            }
        });
    }
})();
