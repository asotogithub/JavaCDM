(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ScheduleTrackingTagAssociationsController', ScheduleTrackingTagAssociationsController);

    ScheduleTrackingTagAssociationsController.$inject = [
        '$document',
        '$rootScope',
        '$scope',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'PlacementService'
    ];

    function ScheduleTrackingTagAssociationsController(
        $document,
        $rootScope,
        $scope,
        $translate,
        CONSTANTS,
        DialogFactory,
        PlacementService) {
        var vm = this,
            siteId,
            sectionId,
            placementId;

        vm.PAGE_SIZE = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.contentTypes = {
            CONTENT: 'content',
            SECURE_CONTENT: 'secure-content'
        };
        vm.deleteAssociations = deleteAssociations;
        vm.viewHTMLContent = viewHTMLContent;

        activate();

        function activate() {
            $scope.$watch('$parent.vm.placementId', function (newValue) {
                placementId = newValue;
                siteId = $scope.$parent.vm.modelWithoutFilter.placementTagAssociationIds.siteId;
                sectionId = $scope.$parent.vm.modelWithoutFilter.placementTagAssociationIds.sectionId;
                getAssociatedTags();
            });
        }

        function getAssociatedTags() {
            vm.promise = PlacementService.getHtmlInjectionTags(placementId).then(function (response) {
                vm.associations = response;
                initializePopoversState();
            });
        }

        function initializePopoversState() {
            vm.contentPopoverIsOpen = [];
            vm.secureContentPopoverIsOpen = [];
            for (var i = 0; i < vm.associations.length; i++) {
                vm.contentPopoverIsOpen.push(false);
                vm.secureContentPopoverIsOpen.push(false);
            }
        }

        function viewHTMLContent(e, contentType) {
            vm.contentType = contentType;
            closeOpenedPopoverState();
            e.stopPropagation();
        }

        function closeOpenedPopoverState() {
            for (var i = 0; i < vm.associations.length; i++) {
                vm.contentPopoverIsOpen[i] = false;
                vm.secureContentPopoverIsOpen[i] = false;
            }
        }

        function deleteAssociations(tags) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('tagInjection.confirm.removeAssociations'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
            }).result.then(
                function () {
                    vm.promise = PlacementService.deleteHtmlInjectionTagsBulk(placementId, buildPayload(tags))
                        .then(function () {
                            getAssociatedTags();
                            DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                                'info.operationCompleted');
                        });
                });
        }

        function buildPayload(tags) {
            var payload = [];

            angular.forEach(tags, function (tag) {
                payload.push(tag.id);
            });

            return payload;
        }

        $document.on('click', function () {
            var popover = angular.element('.popover');

            if (popover.hasClass('in')) {
                popover.removeClass('in');
                popover.hide();
                closeOpenedPopoverState();
            }
        });
    }
})();
