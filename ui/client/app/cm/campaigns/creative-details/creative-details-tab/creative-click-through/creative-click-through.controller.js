(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeEditClickThroughController', CampaignCreativeEditClickThroughController);

    CampaignCreativeEditClickThroughController.$inject = [
        '$scope',
        'CONSTANTS',
        'Utils',
        'lodash'
    ];

    function CampaignCreativeEditClickThroughController(
        $scope,
        CONSTANTS,
        Utils,
        lodash) {
        var vm = this;

        vm.add = add;
        vm.clickthroughs = $scope.$parent.vmEdit.clickThroughArray;
        vm.dragAndDropEnabled = dragAndDropEnabled;
        vm.remove = remove;
        vm.urlPattern = new RegExp(CONSTANTS.REGEX.URL, 'i');
        vm.validationClickThroughs = validationClickThroughs;

        function add() {
            if (containEmptyClickThroughs()) {
                return;
            }

            var sequence = vm.clickthroughs.length + 1;

            vm.clickthroughs.push({
                sequence: sequence,
                url: ''
            });
        }

        function containEmptyClickThroughs() {
            var emptyClick = lodash.find(vm.clickthroughs,
                function (item) {
                    return item.url ? item.url.trim() === '' : true;
                });

            return emptyClick !== undefined;
        }

        function dragAndDropEnabled() {
            //TODO: Get a better way to calculate this value only when 'clickthroughs' has changed
            return vm.clickthroughs.length > 1;
        }

        function remove(index) {
            if (!vm.clickthroughs || vm.clickthroughs.length < 2 || typeof vm.clickthroughs === undefined) {
                return;
            }

            vm.clickthroughs.splice(index, 1);
            $scope.$parent.vmEdit.editCreativeForm.$setDirty();
            refreshClickthroughsSequence();
        }

        function refreshClickthroughsSequence() {
            angular.forEach(vm.clickthroughs, function (clickthrough, index) {
                clickthrough.sequence = index + 1;
            });
        }

        function validationClickThroughs(item) {
            $scope.$parent.vmEdit.editCreativeForm.$setDirty();
            if (!Utils.isUndefinedOrNull(item.sequence)) {
                vm.clickthroughsForm[item.sequence].$dirty = true;
            }

            refreshClickthroughsSequence();
        }
    }
})();
