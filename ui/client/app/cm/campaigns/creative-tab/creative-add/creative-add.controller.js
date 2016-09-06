(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignCreativeAddController', CampaignCreativeAddController);

    CampaignCreativeAddController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CreativeGroupService',
        'CreativeUtilService',
        'DialogFactory',
        'ErrorRequestHandler',
        'Utils'
    ];

    function CampaignCreativeAddController(
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CreativeGroupService,
        CreativeUtilService,
        DialogFactory,
        ErrorRequestHandler,
        Utils) {
        var vm = this,
            campaignId = $stateParams.campaignId;

        vm.STEP = {
            UPLOAD: {
                index: 1,
                isValid: false,
                key: 'addCreative.upload'
            },
            ASSIGN: {
                index: 2,
                isValid: false,
                key: 'addCreative.assign'
            }
        };
        vm.creativeGroups = [];
        vm.creativeList = [];
        vm.creativeVersionedList = [];
        vm.creatives = [];
        vm.listUpload = [];

        vm.activateStep = activateStep;
        vm.cancel = cancel;
        vm.save = save;

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function cancel() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('creative.confirm.discard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    angular.forEach(vm.listUpload, function (uploading) {
                        uploading.abort();
                    });

                    $state.go('creative-list');
                });
        }

        function save() {
            vm.promise = CreativeGroupService.createAssociations(campaignId, vm.creatives, vm.creativeGroups);
            vm.promise.then(
                function () {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    $state.go('creative-list');
                }).catch(function (error) {
                    if (error.status === CONSTANTS.CREATIVE.STATUS_UPLOAD.BAD_REQUEST &&
                        (!Utils.isUndefinedOrNull(error.data.error.code) && error.data.error.code.$ === '108' ||
                        error.data.error instanceof Array && error.data.error[0].code.$ === '108')) {
                        CreativeUtilService.duplicateAliasPopup(error, $scope, vm.creativeVersionedList,
                            function () {
                                if (!Utils.isUndefinedOrNull($scope.$$childHead) &&
                                    !Utils.isUndefinedOrNull($scope.$$childHead.wizard)) {
                                    $scope.$$childHead.wizard.go(1);
                                }
                            });
                    }
                    else {
                        ErrorRequestHandler.handle('Cannot create associations', error);
                    }
                });
        }
    }
})();

