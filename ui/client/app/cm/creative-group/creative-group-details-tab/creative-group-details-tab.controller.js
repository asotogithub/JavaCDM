(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupDetailsTabController', CreativeGroupDetailsTabController);

    CreativeGroupDetailsTabController.$inject = [
        '$scope',
        '$state',
        '$translate',
        'CONSTANTS',
        'CreativeGroupService',
        'DialogFactory',
        'ErrorHandlingService',
        'ErrorRequestHandler'
    ];

    function CreativeGroupDetailsTabController(
        $scope,
        $state,
        $translate,
        CONSTANTS,
        CreativeGroupService,
        DialogFactory,
        ErrorHandlingService,
        ErrorRequestHandler) {
        var vm = this,
            creativeGroup;

        vm.CREATIVE_GROUP_DEFAULT_NAME = CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME;
        vm.close = close;
        vm.creativeGroup = null;
        vm.errorMessage = null;
        vm.pristine = true;
        vm.promise = null;
        vm.remove = remove;
        vm.save = save;
        vm.submitDisabled = false;

        activate();

        function activate() {
            var promise = vm.promise = CreativeGroupService.getCreativeGroup($scope.creativeGroupId);

            promise.then(
                function (_creativeGroup) {
                    creativeGroup = vm.creativeGroup = _creativeGroup;
                    $scope.$emit('creative-group-updated', creativeGroup);
                }).finally(clearPromise);
        }

        function clearPromise() {
            vm.promise = null;
        }

        function close(evt) {
            if (!vm.pristine) {
                evt.preventDefault();
                DialogFactory.showDialog(DialogFactory.DIALOG.SPECIFIC_TYPE.DISCARD_CHANGES).result.then(leave);
            }
        }

        function leave() {
            $state.go('creative-groups-tab', {
                campaignId: creativeGroup.campaignId
            });
        }

        function remove() {
            DialogFactory.showCustomDialog({
                type: DialogFactory.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('creativeGroup.confirm.deleteCreativeGroup'),
                buttons: DialogFactory.DIALOG.BUTTON_SET.YES_NO
            }).result.then(function () {
                var promise = vm.promise = CreativeGroupService.removeCreativeGroup(creativeGroup.id);

                promise.then(leave, function (error) {
                    if (error.status === 400) {
                        forceRemove();
                    }
                }).finally(clearPromise);
            });
        }

        function forceRemove() {
            DialogFactory.showCustomDialog({
                type: DialogFactory.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('creativeGroup.confirm.forceDeleteCreativeGroup'),
                buttons: DialogFactory.DIALOG.BUTTON_SET.YES_NO
            }).result.then(function () {
                var promise = vm.promise = CreativeGroupService.forceRemoveCreativeGroup(creativeGroup.id);

                promise.then(leave, function () {
                }).finally(clearPromise);
            });
        }

        function save() {
            vm.promise = CreativeGroupService.updateCreativeGroup(creativeGroup);

            vm.promise.then(
                function (_creativeGroup) {
                    vm.errorMessage = null;
                    vm.pristine = true;
                    angular.extend(creativeGroup, _creativeGroup);
                    $scope.$emit('creative-group-updated', creativeGroup);
                    DialogFactory.showDismissableMessage(
                        DialogFactory.DISMISS_TYPE.INFO,
                        $translate.instant('info.operationCompleted'));
                }).catch(function (error) {
                    var errorMessage = error && error.data && error.data.errors &&
                        ErrorHandlingService.getErrorMessage(error.data.errors);

                    vm.errorMessage = errorMessage;

                    if (!errorMessage) {
                        ErrorRequestHandler.handleAndReject('Error while updating the creative group: ' +
                            angular.toJson(error))(error);
                    }
                }).finally(clearPromise);
        }
    }
})();
