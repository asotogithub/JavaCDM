(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementEventsController', SiteMeasurementEventsController);

    SiteMeasurementEventsController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'EventsService',
        'SiteMeasurementsUtilService',
        'SMEventsFilterService',
        'SiteMeasurementModel',
        'UserService',
        'Utils',
        'UtilsCDMService'
    ];

    function SiteMeasurementEventsController(
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        EventsService,
        SiteMeasurementsUtilService,
        SMEventsFilterService,
        SiteMeasurementModel,
        UserService,
        Utils,
        UtilsCDMService) {
        var vm = this;

        vm.addEvent = addEvent;
        vm.eventsList = null;
        vm.flyOutDefaultState = CONSTANTS.FLY_OUT.STATE.HALF_VIEW;
        vm.flyOutModel = {};
        vm.getData = getData;
        vm.getEventDetails = getEventDetails;
        vm.hasAddEventPermission = true; //TODO: get/set from API
        vm.hasDetailsPermission = UserService.hasPermission(CONSTANTS.PERMISSION.SITE_MEASUREMENT.DETAILS);
        vm.isOpenFlyOut = false;
        vm.model = SiteMeasurementModel;
        vm.onCloseFlyout = onCloseFlyout;
        vm.onSearchCounter = onSearchCounter;
        vm.retrieveEventsList = retrieveEventsList;
        vm.totalEvents = 0;
        vm.updateModelEventList = updateModelEventList;

        //This work with the te-table
        vm.filterValues = [
            {
                fieldName: 'eventTypeName',
                values: []
            },
            {
                fieldName: 'groupName',
                values: []
            },
            {
                fieldName: 'tagTypeName',
                values: []
            }
        ];

        //This work with the directive filters
        vm.filterOption = [
            {
                text: $translate.instant('global.eventType'),
                value: []
            },
            {
                text: $translate.instant('global.group'),
                value: []
            },
            {
                text: $translate.instant('global.tagType'),
                value: []
            }
        ];

        //action with directive
        vm.actionFilter = [
            {
                onSelectAll: eventTypeAllAction,
                onDeselectAll: eventTypeDeselectAll,
                onItemAction: eventTypeItemAction
            },
            {
                onSelectAll: groupAllAction,
                onDeselectAll: groupDeselectAll,
                onItemAction: groupItemAction
            },
            {
                onSelectAll: tagTypeAllAction,
                onDeselectAll: tagTypeDeselectAll,
                onItemAction: tagTypeItemAction
            }
        ];

        activate();

        function activate() {
            SiteMeasurementModel.setSiteMeasurementId($stateParams.siteMeasurementId);
            SiteMeasurementModel.getSiteMeasurement(SiteMeasurementModel.siteMeasurementId);
            getData();
        }

        function addEvent() {
            $state.go('add-event');
        }

        function getData() {
            vm.totalEvents = 0;

            vm.retrieveEventsList($stateParams.siteMeasurementId).then(function (result) {
                var array = result.records[0].SmEventDTO;

                vm.eventsList = array ? array : [];
                vm.updateModelEventList(vm.eventsList);
                vm.totalEvents = vm.eventsList.length;
                SMEventsFilterService.loadFilters(vm.eventsList, vm.filterOption, vm.filterValues);
            }).catch(SiteMeasurementModel.showServiceError);
        }

        function onSearchCounter(counterSearch) {
            var legendResource = 'siteMeasurement.eventsPings.rowsEvents',
                legendData = {
                    rows: vm.totalEvents
                };

            if (parseInt(counterSearch) !== vm.totalEvents) {
                legendResource = 'siteMeasurement.eventsPings.rowsSearchEvents';
                legendData.rowsSearch = counterSearch;
            }

            vm.eventPingLegend = $translate.instant(legendResource, legendData);
        }

        function refreshTable() {
            var eventListBackup = [];

            angular.copy(vm.eventsList , eventListBackup);
            vm.eventsList = [];
            angular.copy(eventListBackup, vm.eventsList);
        }

        function retrieveEventsList(idSiteMeasurement) {
            vm.promiseEventList = EventsService.getEvents(idSiteMeasurement);
            return vm.promiseEventList;
        }

        function updateModelEventList(modelevent) {
            angular.forEach(modelevent, function (event) {
                event.tagTypeName = UtilsCDMService.getEnum(event.eventType, UtilsCDMService.eventType).name;
                event.eventTypeName = UtilsCDMService.getEnum(event.smEventType, UtilsCDMService.smEventType).name;
            });

            return modelevent;
        }

        function eventTypeAllAction(oldFilterValues) {
            SMEventsFilterService.updateGroupFilter(vm.filterOption, vm.filterValues, oldFilterValues);
            refreshTable();
        }

        function eventTypeDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    SMEventsFilterService.updateGroupFilter(vm.filterOption, vm.filterValues, oldFilterValues);
                    refreshTable();
                    return;
                }

                vm.filterValues[1].values = [];
                vm.filterOption[1].value = [];
                vm.filterValues[2].values = [];
                vm.filterOption[2].value = [];
                refreshTable();
            }
        }

        function eventTypeItemAction(id, isSelectedAll, oldFilterValues) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                SMEventsFilterService.updateGroupFilter(vm.filterOption, vm.filterValues, oldFilterValues);
                refreshTable();
            }
        }

        function groupAllAction() {
            SMEventsFilterService.updateTagTypeFilter(vm.filterOption, vm.filterValues);
            refreshTable();
        }

        function groupDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    refreshTable();
                    return;
                }

                vm.filterValues[2].values = [];
                vm.filterOption[2].value = [];
                refreshTable();
            }
        }

        function groupItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                SMEventsFilterService.updateTagTypeFilter(vm.filterOption, vm.filterValues);
                refreshTable();
            }
        }

        function tagTypeAllAction() {
            refreshTable();
        }

        function tagTypeDeselectAll(isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        function tagTypeItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        function getEventDetails(selected) {
            if (SiteMeasurementsUtilService.isAllowedToPerformAction(function () {
                    openFlyOut(selected[0]);
                }

            )) {
                openFlyOut(selected[0]);
            }
        }

        //Open Flyout
        function openFlyOut(model) {
            vm.isOpenflyout = true;
            vm.flyOutModel = {
                title: model.eventName,
                data: model
            };
        }

        function onCloseFlyout(param) {
            if (!Utils.isUndefinedOrNull(param) && param.reloadEvents) {
                activate();
            }
            else {
                $scope.$broadcast('eventDetails:closeFlyout');
            }
        }
    }
})();
