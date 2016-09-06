(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SharedCreativeGroupsWarningController', SharedCreativeGroupsWarningController);

    SharedCreativeGroupsWarningController.$inject = [
        '$timeout',
        '$translate',
        'CONSTANTS',
        'ScheduleUtilService'
    ];

    function SharedCreativeGroupsWarningController(
        $timeout,
        $translate,
        CONSTANTS,
        ScheduleUtilService) {
        var vm = this;

        vm.isValidInput = true;
        vm.sharedCreativeGroups = ScheduleUtilService.getSharedCreativeGroups();
        vm.setInputFocus = setInputFocus;
        vm.validate = validate;

        function setInputFocus(inputId) {
            if (vm.isValidInput) {
                $timeout(function () {
                    angular.element('#' + inputId).focus();
                }, 50);
            }
        }

        function validate(creativeGroupId) {
            var inputElement = angular.element('#' + creativeGroupId),
                updateButton = angular.element('#modalYes'),
                value = inputElement[0].value,
                min = CONSTANTS.CREATIVE_GROUP.WEIGHT.MIN,
                max = CONSTANTS.CREATIVE_GROUP.WEIGHT.MAX;

            vm.isValidInput = !(value === '' ||
                value < min ||
                value > max);

            if (vm.isValidInput) {
                updateButton.prop('disabled', false);
                inputElement.jqxTooltip('destroy');
            }
            else {
                updateButton.prop('disabled', true);
                inputElement.jqxTooltip({
                    content: $translate.instant('validation.error.positiveNumberBetween',
                        {
                            start: min,
                            end: max
                        }),
                    disabled: false,
                    position: 'mouse'
                });
            }
        }
    }
})();

