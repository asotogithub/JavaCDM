(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('EventDetailsFlyOutController', EventDetailsFlyOutController);

    EventDetailsFlyOutController.$inject = [
        '$scope',
        '$translate',
        '$window',
        'CONSTANTS',
        'DialogFactory',
        'ErrorRequestHandler',
        'EventsService',
        'PingService',
        'SiteMeasurementsUtilService',
        'SiteService',
        'Utils',
        'UtilsCDMService',
        'lodash'
    ];

    function EventDetailsFlyOutController(
        $scope,
        $translate,
        $window,
        CONSTANTS,
        DialogFactory,
        ErrorRequestHandler,
        EventsService,
        PingService,
        SiteMeasurementsUtilService,
        SiteService,
        Utils,
        UtilsCDMService,
        lodash
        ) {
        var vm = this,
            flyOutController = $scope.vmTeFlyOutController,
            event = flyOutController.flyOutModel.data;

        vm.FLYOUT_STATE = CONSTANTS.FLY_OUT.STATE;
        vm.PING_CARD = CONSTANTS.SITE_MEASUREMENT.PING_CARD;
        vm.PING_VALIDATE_HTML = new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'i');
        vm.PING_VALIDATE_URL = new RegExp(CONSTANTS.REGEX.URL, 'i');
        vm.REGEX_EVENT_NAME = CONSTANTS.REGEX.WORD;
        vm.PENDING_CHANGES_ID = 'ping-Card';
        vm.addPingCard = addPingCard;
        vm.close = close;
        vm.eventNameMaxLength = CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.EVENT_NAME;
        vm.eventDescriptionMaxLength = CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.EVENT_DESCRIPTION;
        vm.eventTypeList = UtilsCDMService.smEventType;
        vm.flyOutController = flyOutController;
        vm.flyoutState = flyOutController.flyoutState;
        vm.hasChanged = hasChanged;
        vm.initializingPingEvents = true;
        vm.onDeletePing = onDeletePing;
        vm.onSavePing = onSavePing;
        vm.tagTypeList = UtilsCDMService.eventType;
        vm.updateEventModel = updateEventModel;
        vm.save = save;
        vm.siteList = [];
        vm.searchFields = [
            {
                enabled: true,
                field: 'pingTagTypeField',
                position: 1,
                title: $translate.instant('global.tagType')
            },
            {
                enabled: true,
                field: 'siteName',
                position: 2,
                title: $translate.instant('global.site')
            },
            {
                enabled: true,
                field: 'pingContent',
                position: 3,
                title: $translate.instant('global.ping')
            }
        ];
        vm.filterValues = [
            {
                fieldName: 'pingType',
                values: []
            }
        ];
        vm.filterOption = {
            PING_TYPE: {
                text: $translate.instant('siteMeasurement.events.details.pingType'),
                value: []
            }
        };
        //action with directive
        vm.actionFilter = [
            {
                onSelectAll: publisherAllAction,
                onDeselectAll: publisherTypeDeselectAll,
                onItemAction: publisherTypeItemAction
            },
            {
                onSelectAll: pingTypeAllAction,
                onDeselectAll: pingTypeDeselectAll,
                onItemAction: pingTypeItemAction
            }
        ];

        vm.filterControlsVisible = true;
        vm.toggleFilter = toggleFilter;

        activate();

        function activate() {
            vm.model = {};
            vm.model.loadPings = false;
            vm.isSmallResolution = $window.innerWidth < CONSTANTS.RESOLUTION.FHD.WIDTH ? true : false;

            vm.promiseEDFlyOut = EventsService.getPings(event.id).then(function (result) {
                var tagType = UtilsCDMService.getEnum(result.eventType, vm.tagTypeList);

                vm.model = result;
                vm.model.eventTypeSelected = UtilsCDMService.getEnum(result.smEventType, vm.eventTypeList);
                vm.model.groupName = event.groupName;
                vm.model.isReadOnly = false;
                vm.model.loadPings = tagType.id === CONSTANTS.SITE_MEASUREMENT.EVENT_TYPE.TRU_TAG;
                vm.model.tagTypeSelected = tagType;
                vm.model.pingEvents = Utils.isUndefinedOrNull(result.pingEvents) ? [] : result.pingEvents;
                getSiteList();
                updatePingModel();
            });
        }

        function addPingCard(pingType) {
            vm.model.pingEvents.unshift({
                type: 'smPingEventDTO',
                id: vm.model.id,
                eventName: '',
                eventType: 1,
                groupName: '',
                location: '',
                maxLength: vm.isSmallResolution ? CONSTANTS.SITE_MEASUREMENT.PING_CARD.MIN_LENGTH :
                                                  CONSTANTS.SITE_MEASUREMENT.PING_CARD.MAX_LENGTH,
                pingCardTypes: CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE,
                pingContent: '',
                pingId: undefined,
                pingPatternList: getRegexPatternList(),
                pingTagType: undefined,
                pingType: pingType,
                pingTypeField: SiteMeasurementsUtilService.getPingCardTitleByType(pingType),
                pingTagTypeList: SiteMeasurementsUtilService.getPingCardTagTypeList(pingType),
                pingTagTypeField: '',
                description: '',
                siteId: undefined,
                siteName: '',
                smEventType: 0,
                smGroupId: vm.model.smGroupId,
                editMode: true,
                siteList: vm.siteList
            });
            vm.model.pingEditMode = true;
        }

        function close() {
            if (SiteMeasurementsUtilService.isAllowedToPerformAction(flyOutController.close)) {
                flyOutController.close();
            }
        }

        function getSiteList() {
            if (vm.model.loadPings) {
                vm.promiseEDFlyOut = SiteService.getList().then(function (result) {
                    vm.siteList = result;
                });
            }
        }

        function getRegexPatternList() {
            return [
                vm.PING_VALIDATE_URL,
                vm.PING_VALIDATE_URL,
                vm.PING_VALIDATE_HTML
            ];
        }

        function updatePingModel() {
            lodash.forEach(vm.model.pingEvents, function (ping) {
                if (!Utils.isUndefinedOrNull(ping.pingType)) {
                    ping.pingTypeField = SiteMeasurementsUtilService.getPingCardTitleByType(ping.pingType);
                    ping.pingTagTypeList = SiteMeasurementsUtilService.getPingCardTagTypeList(ping.pingType);
                    ping.pingTagTypeField = SiteMeasurementsUtilService.getPingCardTagTypeName(ping.pingTagType);
                    ping.pingPatternList = getRegexPatternList();
                    ping.pingCardTypes = CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE;
                    ping.maxLength = CONSTANTS.SITE_MEASUREMENT.PING_CARD.MAX_LENGTH;
                }
            });

            $scope.$watch(function () {
                return vm.model.pingEvents;
            },

                function () {
                    if (vm.initializingPingEvents) {
                        vm.initializingPingEvents = false;
                    }
                    else {
                        if (vm.model.pingEditMode) {
                            SiteMeasurementsUtilService.setHasPendingChanges(true, vm.PENDING_CHANGES_ID);
                        }
                    }
                }, true);
        }

        function hasChanged() {
            SiteMeasurementsUtilService.setHasPendingChanges(true);
        }

        function onDeletePing(pingId) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.eventsPings.deleteEventPing'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
            }).result.then(
                function () {
                    if (Utils.isUndefinedOrNull(pingId)) {
                        removePingModel(pingId);
                    }
                    else {
                        vm.promiseEDFlyOut = PingService.delete(pingId).then(function () {
                            removePingModel(pingId);
                        });
                    }

                    SiteMeasurementsUtilService.setHasPendingChanges(false, vm.PENDING_CHANGES_ID);
                });
        }

        function removePingModel(pingId) {
            lodash.remove(vm.model.pingEvents, function (ping) {
                return ping.pingId === pingId;
            });

            vm.model.pingEditMode = false;
        }

        //SAVE PING
        function onSavePing(pingId) {
            var ping,
                pingSave;

            ping = lodash.find(vm.model.pingEvents, function (pingItem) {
                    return pingItem.pingId === pingId;
                }

            );

            pingSave = getModelPing(ping);

            vm.promiseEDFlyOut = pingSave.isNew ? PingService.save([pingSave]) : PingService.update([pingSave]);

            return vm.promiseEDFlyOut
               .then(function (result) {
                    ping.pingId = result.records[0].SmEventDTO[0].pingId;
                    if (!Utils.isUndefinedOrNull(ping.siteList)) {
                        ping.siteList = [];
                        ping.editMode = false;
                    }

                    return ping;
                })
               .catch(function (error) {
                    ErrorRequestHandler.handle('Error while performing ping request', error);
                });
        }

        function getModelPing(ping) {
            var pingSave = {};

            vm.model.pingEditMode = false;
            SiteMeasurementsUtilService.setHasPendingChanges(false, vm.PENDING_CHANGES_ID);

            pingSave.type = 'smPingEventDTO';
            pingSave.id = ping.id;
            pingSave.isNew = Utils.isUndefinedOrNull(ping.pingId);
            pingSave.pingId = Utils.isUndefinedOrNull(ping.pingId) ? 0 : ping.pingId;
            pingSave.pingContent = ping.pingContent;
            pingSave.description = Utils.isUndefinedOrNull(ping.description) ? '' : ping.description;
            pingSave.siteId = ping.siteId;
            pingSave.pingType = ping.pingType;
            pingSave.pingTagType = ping.pingTagType;
            pingSave.smGroupId = ping.smGroupId;

            return pingSave;
        }

        function updateEventModel(model) {
            model.smEventType = model.eventTypeSelected.id;
            model.eventType = model.tagTypeSelected.id;
            vm.flyOutForm.$pristine = false;
        }

        //TODO:ADD ACTIONS FiLTERS
        function publisherAllAction() {}

        function publisherTypeDeselectAll() {}

        function publisherTypeItemAction() {}

        function pingTypeAllAction() {}

        function pingTypeDeselectAll() {}

        function pingTypeItemAction() {}

        //SAVE SUMMARY
        function save() {
            vm.flyOutForm.$pristine = true;
            vm.promiseEDFlyOut = EventsService.updateDetails(vm.model.id, vm.model).then(function (response) {
                if (!Utils.isUndefinedOrNull(response)) {
                    SiteMeasurementsUtilService.setHasPendingChanges(false);
                    close();
                    flyOutController.closeAction({
                        reloadEvents: true
                    });
                }
            }).catch(function () {
                vm.flyOutForm.$pristine = false;
            });
        }

        //COLLAPSE-EXPAND
        function toggleFilter() {
            if (flyOutController.flyoutState === vm.FLYOUT_STATE.HALF_VIEW) {
                flyOutController.full();
                vm.showFilter = true;
            }
            else {
                vm.showFilter = !vm.showFilter;
            }
        }

        function resetValidationSave() {
            vm.flyOutForm.$pristine = true;
        }

        //Fly-Out Actions
        $scope.$watch('vmTeFlyOutController.flyoutState', function (newValue) {
            vm.flyoutState = newValue;
        });

        $scope.$watch('vmTeFlyOutController.flyOutModel.data', function (newValue, oldValue) {
            if (!lodash.isEqual(newValue, oldValue)) {
                resetValidationSave();
                event = newValue;
                activate();
            }
        }, true);

        $scope.$watch('vmEDFlyOut.flyOutForm.$dirty', function (newValue) {
            if (newValue) {
                hasChanged();
            }
        });

        $scope.$on('eventDetails:closeFlyout', function () {
            close();
        });
    }
})();
