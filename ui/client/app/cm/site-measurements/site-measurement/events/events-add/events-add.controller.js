(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddEventController', AddEventController);

    AddEventController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'EventsService',
        'SiteMeasurementGroupsService',
        'Utils'
    ];

    function AddEventController(
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        EventsService,
        SiteMeasurementGroupsService,
        Utils) {
        var siteMeasurementId = $stateParams.siteMeasurementId,
            vm = this;

        vm.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addEvent.name'
            },
            GROUP: {
                index: 2,
                isValid: false,
                key: 'addEvent.group'
            }
        };
        vm.activateStep = activateStep;
        vm.cancel = cancel;
        vm.newEvent = {
            description: null,
            group: {
                creating: false,
                existing: false,
                id: null,
                name: null
            },
            name: null,
            tagType: null,
            type: null
        };
        vm.save = save;

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function buildPayload() {
            var payload = {},
                eventObject = {
                    eventName: vm.newEvent.name,
                    location: Utils.isUndefinedOrNull(vm.newEvent.description) ? '' : vm.newEvent.description,
                    eventType: vm.newEvent.type,
                    smEventType: vm.newEvent.tagType
                };

            if (vm.newEvent.group.creating) {
                payload.groupName = vm.newEvent.group.name;
                payload.measurementId = siteMeasurementId;
                payload.smEvent = eventObject;
            }
            else if (vm.newEvent.group.existing) {
                eventObject.smGroupId = vm.newEvent.group.id;
                payload = eventObject;
            }

            return payload;
        }

        function cancel() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.eventsPings.confirmDiscard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    $state.go('events-list');
                });
        }

        function save() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('siteMeasurement.eventsPings.createEventDialogWarning'),
                buttons: {
                    yes: $translate.instant('global.save'),
                    no: $translate.instant('global.back')
                },
                dontShowAgainID: CONSTANTS.SITE_MEASUREMENT.CREATE_EVENT_WARNING_DIALOG_ID
            }).result.then(
                function () {
                    if (vm.newEvent.group.creating) {
                        vm.promise = SiteMeasurementGroupsService.saveEvent(buildPayload());
                    }
                    else if (vm.newEvent.group.existing) {
                        vm.promise = EventsService.saveEvent(buildPayload());
                    }

                    vm.promise.then(
                        function () {
                            DialogFactory.showDismissableMessage(
                                DialogFactory.DISMISS_TYPE.SUCCESS,
                                'info.operationCompleted');
                            $state.go('events-list');
                        });
                });
        }
    }
})();
