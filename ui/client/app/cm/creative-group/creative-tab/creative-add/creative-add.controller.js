(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupCreativeAddController', CreativeGroupCreativeAddController);

    CreativeGroupCreativeAddController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CreativeGroupService',
        'CreativeUtilService',
        'DialogFactory',
        'ErrorRequestHandler',
        'Utils',
        'dialogs'
    ];

    function CreativeGroupCreativeAddController(
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CreativeGroupService,
        CreativeUtilService,
        DialogFactory,
        ErrorRequestHandler,
        Utils,
        dialogs) {
        var vm = this,
            campaignId = $stateParams.campaignId,
            creativeGroupId = $stateParams.creativeGroupId,
            creativeListBackup = [];

        vm.creativeGroups = [
            {
                id: creativeGroupId
            }
        ];
        vm.creativeList = [];
        vm.creativeVersionedList = [];
        vm.creatives = [];
        vm.isAddFormValid = false;
        vm.uploadOptions = {
            addExisting: true,
            title: $translate.instant('creative.addCreative')
        };

        vm.addExisting = addExisting;
        vm.save = save;

        function addExisting() {
            var template = 'app/cm/creative-group/creative-tab/creative-add/existing-creatives/existing-creatives.html',
                dlg = dialogs.create(template,
                'ExistingCreativesController as vm',
                {
                    creativeList: vm.creativeList
                },
                {
                    size: 'xl',
                    keyboard: true,
                    key: false,
                    backdrop: 'static'
                });

            dlg.result.then(
                function (result) {
                    vm.creativeList.push.apply(vm.creativeList, result);
                    vm.creatives.push.apply(vm.creatives, result);
                    refreshTable();
                });

            return dlg;
        }

        function buildCreativeGroupCreativesPayload(existingAssociations, newAssociations) {
            var payload = {
                creativeGroupId: creativeGroupId,
                creatives: []
            };

            angular.forEach(existingAssociations, function (creative) {
                payload.creatives.push({
                    id: creative.creativeId
                });
            });

            angular.forEach(newAssociations, function (creative) {
                payload.creatives.push({
                    id: creative.id
                });
            });

            return payload;
        }

        function save() {
            var newCreatives = [],
                existingCreatives = [];

            angular.forEach(vm.creatives, function (creative) {
                if (Utils.isUndefinedOrNull(creative.isExisting) || !creative.isExisting) {
                    newCreatives.push(creative);
                }
                else {
                    existingCreatives.push(creative);
                }
            });

            if (newCreatives.length > 0) {
                vm.promise = CreativeGroupService.createAssociations(campaignId, newCreatives, vm.creativeGroups);
                vm.promise.then(
                    function () {
                        vm.promise = CreativeGroupService.getCreativeList(creativeGroupId);
                        vm.promise.then(
                            function (creatives) {
                                vm.promise = CreativeGroupService.updateCreativeGroupCreatives(creativeGroupId,
                                    buildCreativeGroupCreativesPayload(creatives, existingCreatives));
                                vm.promise.then(
                                    function () {
                                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                                            'info.operationCompleted');
                                        $state.go('creative-group-creative-list');
                                    }).catch(function (error) {
                                        ErrorRequestHandler.handle('Cannot add existing creative to group', error);
                                    });
                            }

                        ).catch(function (error) {
                                ErrorRequestHandler.handle('Cannot get group associations', error);
                            });
                    }).catch(function (error) {
                        if (error.status === CONSTANTS.CREATIVE.STATUS_UPLOAD.BAD_REQUEST &&
                            (!Utils.isUndefinedOrNull(error.data.error.code) && error.data.error.code.$ === '108' ||
                            error.data.error instanceof Array && error.data.error[0].code.$ === '108')) {
                            CreativeUtilService.duplicateAliasPopup(error, $scope, vm.creativeVersionedList);
                        }
                        else {
                            ErrorRequestHandler.handle('Cannot create associations', error);
                        }
                    });
            }
            else {
                vm.promise = CreativeGroupService.getCreativeList(creativeGroupId);
                vm.promise.then(
                    function (creatives) {
                        vm.promise = CreativeGroupService.updateCreativeGroupCreatives(creativeGroupId,
                            buildCreativeGroupCreativesPayload(creatives, existingCreatives));
                        vm.promise.then(
                            function () {
                                DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                                    'info.operationCompleted');
                                $state.go('creative-group-creative-list');
                            }).catch(function (error) {
                                ErrorRequestHandler.handle('Cannot add existing creative to group', error);
                            });
                    }

                ).catch(function (error) {
                        ErrorRequestHandler.handle('Cannot get group associations', error);
                    });
            }
        }

        function refreshTable() {
            angular.copy(vm.creativeList, creativeListBackup);
            vm.creativeList = [];
            angular.copy(creativeListBackup, vm.creativeList);
        }
    }
})();

