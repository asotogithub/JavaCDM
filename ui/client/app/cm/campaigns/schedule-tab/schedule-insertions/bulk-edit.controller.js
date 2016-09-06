(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('BulkEditController', BulkEditController);

    BulkEditController.$inject = [
        '$scope',
        '$translate',
        'CONSTANTS',
        'DateTimeService',
        'Utils',
        'lodash'
    ];

    function BulkEditController($scope,
                                $translate,
                                CONSTANTS,
                                DateTimeService,
                                Utils,
                                lodash) {
        var vm = this,
            PATTERN_URL = new RegExp(CONSTANTS.REGEX.URL, 'i');

        vm.applyEnabled = false;
        vm.applyTo = applyTo;
        vm.bulkEditData = $scope.$parent.vm.bulkEditData;
        vm.bulkEditDataStatus = {
            weight: {
                valid: false,
                dirty: true
            },
            startDate: {
                valid: false,
                dirty: true
            },
            endDate: {
                valid: false,
                dirty: true
            },
            ctUrl: {
                valid: false,
                dirty: true
            }
        };
        vm.isValidDateInput = isValidDateInput;
        vm.onChange = onChange;
        vm.settings = {
            weight: {
                height: '30px',
                inputMode: 'simple',
                spinButtons: true,
                disabled: true,
                decimal: null,
                decimalDigits: 0
            },
            date: {
                height: '30px',
                disabled: !$scope.$parent.vm.isCreativeVisible,
                value: null,
                formatString: CONSTANTS.DATE.JQX.DATE_TIME_US,
                textAlign: 'center',
                showFooter: true,
                clearString: 'Clear'
            },
            clickThrough: {
                height: '30px',
                width: '100%',
                disabled: !$scope.$parent.vm.isCreativeVisible
            }
        };

        activate();

        $scope.$parent.$watch('vm.isCreativeGroupVisible', function (isGroupVisible) {
            vm.isCreativeGroupVisible = isGroupVisible;
            var isCreativeVisible = $scope.$parent.vm.isCreativeVisible;

            disableFields(isCreativeVisible, isGroupVisible);
            onChangeModel();
        });

        $scope.$parent.$watch('vm.isCreativeVisible', function (isCreativeVisible) {
            var isGroupVisible = $scope.$parent.vm.isCreativeGroupVisible;

            disableFields(isCreativeVisible, isGroupVisible);
            onChangeModel();
        });

        $scope.$watch('vmEdit.bulkEditData', function (newValue, oldValue) {
            if (lodash.isEqual(newValue, oldValue)) {
                return;
            }

            onChange();
        }, true);

        function activate() {
            // Will not doing nothing for now.
        }

        function applyTo() {
            var updateAttributes,
                weight = vm.bulkEditData.weight,
                startDate = vm.bulkEditData.startDate,
                endDate = vm.bulkEditData.endDate,
                ctUrl = vm.bulkEditData.ctUrl;

            if (vm.isCreativeGroupVisible) {
                updateAttributes = {
                    field: CONSTANTS.SCHEDULE.LEVEL.CREATIVE_GROUP.KEY,
                    weight: isUndefinedOrEmpty(weight) ? null : weight
                };
            }
            else {
                updateAttributes = {
                    field: CONSTANTS.SCHEDULE.LEVEL.SCHEDULE.KEY,
                    weight: isUndefinedOrEmpty(weight) ? null : weight,
                    flightDateStart: isUndefinedOrEmpty(startDate) ? null : startDate,
                    flightDateEnd: isUndefinedOrEmpty(endDate) ? null : endDate,
                    clickThroughUrl: isUndefinedOrEmpty(ctUrl) ? null : ctUrl
                };
            }

            $scope.$parent.$parent.vm.applyTo(updateAttributes);
        }

        function disableFields(isCreativeVisible, isGroupVisible) {
            var startDateElement = angular.element('#startDate'),
                endDateElement = angular.element('#endDate'),
                ctUrlElement = angular.element('#ctUrl');

            if (startDateElement.length) {
                dataInputProperties(startDateElement,
                    {
                        disabled: !isCreativeVisible
                    });
                dataInputProperties(startDateElement,
                    {
                        formatString: CONSTANTS.DATE_FORMAT
                    });
            }

            if (endDateElement.length) {
                dataInputProperties(endDateElement,
                    {
                        disabled: !isCreativeVisible
                    });
                dataInputProperties(endDateElement,
                    {
                        formatString: CONSTANTS.DATE_FORMAT
                    });
            }

            if (ctUrlElement.length) {
                ctUrlElement.jqxInput(
                    {
                        disabled: !isCreativeVisible
                    });
            }

            vm.settings.weight.disabled = !(isCreativeVisible || isGroupVisible);
        }

        function dataInputProperties(element, propertie) {
            element.jqxDateTimeInput(propertie);
        }

        function displayErrors() {
            var weightElement = angular.element('#weight'),
                startDateElement = angular.element('#startDate'),
                endDateElement = angular.element('#endDate'),
                ctUrlElement = angular.element('#ctUrl'),
                weightTooltip = angular.element('#weightTooltip'),
                startDateTooltip = angular.element('#startDateTooltip'),
                endDateTooltip = angular.element('#endDateTooltip'),
                ctUrlTooltip = angular.element('#ctUrlTooltip');

            if (shouldMarkWithError(vm.bulkEditDataStatus.weight)) {
                weightElement.addClass('jqx-validator-error-element');
                weightTooltip.jqxTooltip({
                    content: $translate.instant('validation.error.positiveNumberBetween',
                        {
                            start: vm.isCreativeGroupVisible ?
                                CONSTANTS.CREATIVE_GROUP.WEIGHT.MIN : CONSTANTS.SCHEDULE.WEIGHT.MIN,
                            end: vm.isCreativeGroupVisible ?
                                CONSTANTS.CREATIVE_GROUP.WEIGHT.MAX : CONSTANTS.SCHEDULE.WEIGHT.MAX
                        }),
                    disabled: false,
                    position: 'mouse'
                });
            }

            if (shouldMarkWithError(vm.bulkEditDataStatus.startDate)) {
                startDateElement.addClass('jqx-validator-error-element');
                if (startDateTooltip.length > 0) {
                    startDateTooltip.jqxTooltip({
                        content: $translate.instant('validation.error.wrongStartDate'),
                        disabled: false,
                        position: 'mouse'
                    });
                }
            }

            if (shouldMarkWithError(vm.bulkEditDataStatus.endDate)) {
                endDateElement.addClass('jqx-validator-error-element');
                if (endDateTooltip.length > 0) {
                    endDateTooltip.jqxTooltip({
                        content: $translate.instant('validation.error.wrongEndDate'),
                        disabled: false,
                        position: 'mouse'
                    });
                }
            }

            if (shouldMarkWithError(vm.bulkEditDataStatus.ctUrl)) {
                ctUrlElement.addClass('jqx-validator-error-element');
                if (ctUrlTooltip.length > 0) {
                    ctUrlTooltip.jqxTooltip({
                        content: $translate.instant('validation.error.invalidURL'),
                        disabled: false,
                        position: 'mouse'
                    });
                }
            }
        }

        function destroyErrors() {
            var weightElement = angular.element('#weight'),
                startDateElement = angular.element('#startDate'),
                endDateElement = angular.element('#endDate'),
                ctUrlElement = angular.element('#ctUrl'),
                weightTooltip = angular.element('#weightTooltip'),
                startDateTooltip = angular.element('#startDateTooltip'),
                endDateTooltip = angular.element('#endDateTooltip'),
                ctUrlTooltip = angular.element('#ctUrlTooltip');

            if (weightTooltip.length) {
                weightTooltip.jqxTooltip('destroy');
            }

            if (startDateTooltip.length) {
                startDateTooltip.jqxTooltip('destroy');
            }

            if (endDateTooltip.length) {
                endDateTooltip.jqxTooltip('destroy');
            }

            if (ctUrlTooltip.length) {
                ctUrlTooltip.jqxTooltip('destroy');
            }

            weightElement.removeClass('jqx-validator-error-element');
            startDateElement.removeClass('jqx-validator-error-element');
            endDateElement.removeClass('jqx-validator-error-element');
            ctUrlElement.removeClass('jqx-validator-error-element');
        }

        function getFieldState(value, isValid) {
            return {
                valid: isValid,
                dirty: isUndefinedOrEmpty(value) ? false : true
            };
        }

        function isValidClickThrough(value) {
            if (Utils.isUndefinedOrNull(value) || value === '') {
                return true;
            }

            return PATTERN_URL.test(value);
        }

        /**
         * Prevents the user to insert characters 'a' 'A' 'p' 'P' on date picker
         * event.preventDefault() function Cancels the event if it is cancelable,
         * without stopping further propagation of the event,
         * @param event
         */
        function isValidDateInput(event) {
            if (!Utils.isUndefinedOrNull(event)) {
                var keycode = event.keyCode ? event.keyCode : event.which;

                if (keycode === 97 || keycode === 65 || keycode === 80 || keycode === 112 || keycode === 222) {
                    event.preventDefault();
                }
            }
        }

        function isValidDateInterval(startDate, endDate) {
            if (isUndefinedOrEmpty(startDate) && isUndefinedOrEmpty(endDate)) {
                return true;
            }

            if (!isUndefinedOrEmpty(startDate) && isUndefinedOrEmpty(endDate)) {
                return true;
            }

            if (isUndefinedOrEmpty(startDate) && !isUndefinedOrEmpty(endDate)) {
                return true;
            }

            return DateTimeService.isBefore(
                startDate,
                endDate,
                CONSTANTS.DATE.MOMENT.DATE_TIME_US);
        }

        function isValidWeight(value, minValue, maxValue) {
            if (isUndefinedOrEmpty(value)) {
                return true;
            }

            return value >= minValue &&
                value <= maxValue;
        }

        function isUndefinedOrEmpty(value) {
            return Utils.isUndefinedOrNull(value) || value === '';
        }

        //TODO improve performance of this function, to allow call only once
        // when the current model is different than previous one
        /**
         * This function is called once a value is changed.
         */
        function onChange() {
            onChangeModel();
        }

        function onChangeModel() {
            destroyErrors();
            validateModel();
            displayErrors();
        }

        function shouldEnableApply(field) {
            return field.valid && field.dirty;
        }

        function shouldMarkWithError(field) {
            return !field.valid && field.dirty;
        }

        function validateModel() {
            var validWeight = false,
                validDateInterval = false,
                validCT = false;

            if (vm.isCreativeGroupVisible === true) {
                validWeight = isValidWeight(vm.bulkEditData.weight,
                    CONSTANTS.CREATIVE_GROUP.WEIGHT.MIN, CONSTANTS.CREATIVE_GROUP.WEIGHT.MAX);
                vm.bulkEditDataStatus.weight = getFieldState(vm.bulkEditData.weight, validWeight);
                vm.applyEnabled = shouldEnableApply(vm.bulkEditDataStatus.weight);
                return;
            }

            //Showing creatives
            // Validate creative data
            validWeight = isValidWeight(vm.bulkEditData.weight,
                CONSTANTS.SCHEDULE.WEIGHT.MIN, CONSTANTS.SCHEDULE.WEIGHT.MAX);
            vm.bulkEditDataStatus.weight = getFieldState(vm.bulkEditData.weight, validWeight);

            validDateInterval = isValidDateInterval(vm.bulkEditData.startDate, vm.bulkEditData.endDate);
            vm.bulkEditDataStatus.startDate = getFieldState(vm.bulkEditData.startDate, validDateInterval);
            vm.bulkEditDataStatus.endDate = getFieldState(vm.bulkEditData.endDate, validDateInterval);

            validCT = isValidClickThrough(vm.bulkEditData.ctUrl);
            vm.bulkEditDataStatus.ctUrl = getFieldState(vm.bulkEditData.ctUrl, validCT);

            vm.applyEnabled = validWeight && validDateInterval && validCT &&
                (shouldEnableApply(vm.bulkEditDataStatus.weight) ||
                shouldEnableApply(vm.bulkEditDataStatus.startDate) ||
                shouldEnableApply(vm.bulkEditDataStatus.endDate) ||
                shouldEnableApply(vm.bulkEditDataStatus.ctUrl));
        }
    }
})();

