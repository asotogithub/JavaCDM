(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupController', CreativeGroupController);

    CreativeGroupController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        'CreativeGroupService'
    ];

    function CreativeGroupController(
        $scope,
        $state,
        $stateParams,
        CreativeGroupService) {
        var vm = this,
            creativeGroupId = $scope.creativeGroupId = $stateParams.creativeGroupId,
            campaignId = $stateParams.campaignId;

        vm.goToCreativeGroupsTab = goToCreativeGroupsTab;

        activate();

        function activate() {
            vm.promise = CreativeGroupService.getCreativeGroup(creativeGroupId);
            $scope.$on('creative-group-updated', function (evt, creativeGroup) {
                updateCreativeGroup(creativeGroup);
            });

            vm.promise.then(
                function (creativeGroup) {
                    updateCreativeGroup(creativeGroup);
                });
        }

        function goToCreativeGroupsTab() {
            $state.go('creative-groups-tab', {
                campaignId: campaignId
            });
        }

        function updateCreativeGroup(creativeGroup) {
            vm.crtvGrpName = creativeGroup.name;
            $scope.creativeGroup = vm.crtvGrpName;
        }
    }
})();
