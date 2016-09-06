(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AssociatedPlacementsController', AssociatedPlacementsController);

    AssociatedPlacementsController.$inject = [
        '$rootScope',
        '$translate',
        'CONSTANTS',
        'CampaignsUtilService',
        'DialogFactory',
        'Utils',
        'lodash'
    ];

    function AssociatedPlacementsController(
        $rootScope,
        $translate,
        CONSTANTS,
        CampaignsUtilService,
        DialogFactory,
        Utils,
        lodash) {
        var vm = this;

        vm.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.statusList = lodash.values(CampaignsUtilService.getIOStatusList());
        vm.add = add;
        vm.edit = edit;
        vm.selectRows = selectRows;
        vm.getStatusName = getStatusName;
        vm.removePlacement = removePlacement;
        vm.removeItems = removeItems;
        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;

        function add() {
            if (angular.isFunction(vm.addAction)) {
                vm.addAction();
            }
        }

        function edit(placement) {
            if (angular.isFunction(vm.editAction)) {
                vm.editAction(placement);
            }
        }

        function selectRows(placement) {
            if (angular.isFunction(vm.selectRowAction)) {
                vm.selectRowAction(placement);
            }
        }

        function refreshPlacements() {
            var outputModelBackup = [];

            angular.copy(vm.model, outputModelBackup);
            vm.model = [];
            angular.copy(outputModelBackup, vm.model);
        }

        function removePlacement(placement, event) {
            if (event) {
                event.stopPropagation();
            }

            if (!placement) {
                return;
            }

            if (!Utils.isUndefinedOrNull(vm.removeLast) && !vm.removeLast && vm.model.length === 1) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    description: $translate.instant('placement.disassociateFromPackage'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.OK,
                    dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
                }).result.then(
                    function () {
                        return;
                    });
            }
            else {
                showDialogConfirmation(placement);
            }
        }

        function showDialogConfirmation(placement) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('package.placementAssociatedRemoveWarning'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL,
                dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
            }).result.then(
                function () {
                    var index = vm.model.indexOf(placement);

                    vm.model.splice(index, 1);
                    refreshPlacements();
                    $rootScope.$broadcast('rootScope:placementRemoved', placement);
                });
        }

        function removeItems(obj) {
            if (!obj) {
                return;
            }

            var index = vm.model.indexOf(obj);

            vm.model.splice(index, 1);
            refreshPlacements();
            $rootScope.$broadcast('rootScope:itemsAssociatedRemoved', obj);
        }

        function getStatusName(key) {
            var status = lodash.find(vm.statusList, function (item) {
                return item.key === key;
            });

            return status ? status.name : '';
        }
    }
})();
