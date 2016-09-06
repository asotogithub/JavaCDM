(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('EventNameController', EventNameController);

    EventNameController.$inject = [
        '$scope',
        '$translate',
        'CONSTANTS',
        'EventsService',
        'Utils'
    ];

    function EventNameController(
        $scope,
        $translate,
        CONSTANTS,
        EventsService,
        Utils) {
        var currentEventName = '',
            eventObject = $scope.$parent.vmAddEvent.newEvent,
            step = $scope.$parent.vmAddEvent.STEP.NAME,
            vm = this;

        vm.EVENT_NAME_STATE = CONSTANTS.SITE_MEASUREMENT.NAME_STATE;
        vm.checkValidEventName = checkValidEventName;
        vm.checkValidInputType = checkValidInputType;
        vm.event = {
            description: '',
            name: '',
            tagType: null,
            type: null
        };
        vm.eventNameState = vm.EVENT_NAME_STATE.EMPTY;
        vm.eventTypeList = [
            {
                key: $translate.instant('global.standard'),
                name: $translate.instant('global.standard'),
                value: CONSTANTS.SITE_MEASUREMENT.EVENT_TYPE.STANDARD
            },
            {
                key: $translate.instant('global.truTag'),
                name: $translate.instant('global.truTag'),
                value: CONSTANTS.SITE_MEASUREMENT.EVENT_TYPE.TRU_TAG
            }
        ];
        vm.eventTagTypeList = [
            {
                key: $translate.instant('global.conversion'),
                name: $translate.instant('global.conversion'),
                value: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.CONVERSION
            },
            {
                key: $translate.instant('global.conversionRevenue'),
                name: $translate.instant('global.conversionRevenue'),
                value: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.CONVERSION_REVENUE
            },
            {
                key: $translate.instant('global.measured'),
                name: $translate.instant('global.measured'),
                value: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.MEASURED
            },
            {
                key: $translate.instant('global.other'),
                name: $translate.instant('global.other'),
                value: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.OTHER
            }
        ];
        vm.maxLength = {
            description: CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.EVENT_DESCRIPTION,
            name: CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.EVENT_NAME
        };
        vm.namePattern = CONSTANTS.REGEX.WORD;
        vm.setDescription = setDescription;

        function checkValidEventName(event, invalid) {
            var eventName = event.target.value.trim(),
                promise;

            if (eventName === currentEventName) {
                return;
            }
            else {
                currentEventName = eventName;
            }

            if (!Utils.isUndefinedOrNull(invalid) && invalid) {
                vm.eventNameState = vm.EVENT_NAME_STATE.INVALID;
            }
            else if (!Utils.isUndefinedOrNull(eventName) && eventName !== '') {
                promise = $scope.$parent.vmAddEvent.promise = EventsService.checkEventNameUnique(eventName);

                promise.then(function (response) {
                    if (response.result) {
                        vm.eventNameState = vm.EVENT_NAME_STATE.DUPLICATED;
                        vm.event.name = eventName;
                    }
                    else {
                        vm.eventNameState = vm.EVENT_NAME_STATE.VALID;
                        eventObject.name = vm.event.name;
                    }

                    checkValidInputType();
                });
            }
            else {
                vm.eventNameState = vm.EVENT_NAME_STATE.EMPTY;
                vm.event.name = '';

                checkValidInputType();
            }
        }

        function checkValidInputType() {
            step.isValid = false;

            if (!Utils.isUndefinedOrNull(vm.event.tagType) && !Utils.isUndefinedOrNull(vm.event.type)) {
                eventObject.tagType = vm.event.tagType.value;
                eventObject.type = vm.event.type.value;
                step.isValid = vm.eventNameState === vm.EVENT_NAME_STATE.VALID;
            }
        }

        function setDescription() {
            $scope.$parent.vmAddEvent.newEvent.description = vm.event.description;
        }
    }
})();
