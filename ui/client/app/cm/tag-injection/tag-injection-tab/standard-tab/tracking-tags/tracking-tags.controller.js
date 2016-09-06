(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TrackingTagsController', TrackingTagsController);

    TrackingTagsController.$inject = [
        '$q',
        '$scope',
        '$state',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'TagInjectionService',
        'TagInjectionUtilService',
        'lodash'
    ];

    function TrackingTagsController($q,
                                    $scope,
                                    $state,
                                    $translate,
                                    CONSTANTS,
                                    DialogFactory,
                                    TagInjectionService,
                                    TagInjectionUtilService,
                                    lodash) {
        var vm = this;

        vm.addTrackingTag = addTrackingTag;
        vm.deleteTrackingTag = deleteTrackingTag;
        vm.editTrackingTag = editTrackingTag;
        vm.pageSize = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.SMALL;
        vm.selectEditRow = {};
        vm.selectRows = selectRows;
        vm.vmTagInjectionTab = $scope.vmTagInjectionTab;
        vm.vmTIStandard = $scope.vmTIStandard;

        function addTrackingTag() {
            $state.go('add-tracking-tag');
        }

        function deleteTrackingTag(selection) {
            var reloadAssociations;

            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('tagInjection.delete.confirmation'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
            }).result.then(
                function () {
                    vm.vmTIStandard.closeFlyOut();
                    vm.vmTagInjectionTab.promiseTab = $q.all([
                        TagInjectionService.deleteHtmlInjectionTag({
                            records: {
                                Long: lodash.map(
                                    selection,
                                    'id')
                            }
                        })
                    ]).then(function () {
                        reloadAssociations = true;
                        vm.vmTIStandard.loadTrackingTag(reloadAssociations);
                        vm.vmTIStandard.removeTagsFromAssociations(selection);
                    });
                });
        }

        function editTrackingTag() {
            vm.vmTIStandard.openFlyOut(vm.selectEditRow[0].name, vm.selectEditRow[0]);
        }

        function selectRows(selection) {
            if (selection.length === 1) {
                vm.selectEditRow = selection;
            }
        }
    }
})();
