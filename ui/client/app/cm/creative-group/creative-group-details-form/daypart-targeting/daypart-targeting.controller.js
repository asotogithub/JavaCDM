(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('DaypartTargetingController', DaypartTargetingController);

    DaypartTargetingController.$inject = ['$moment', '$scope', 'CONSTANTS', 'lodash'];

    function DaypartTargetingController($moment, $scope, CONSTANTS, lodash) {
        var DAYPART_OPTION_TEMPLATE = initDaypartOptionTemplate(),
            DAYPART_OPTION_TIME_INPUT_FORMAT = initDaypartOptionTimeInputFormat(),
            IAB_STANDARDS = initIabStandards(),
            vm = this;

        vm.addCustomOption = addCustomOption;
        vm.addDisabled = addDisabled;
        vm.clearCustomOptions = clearCustomOptions;
        vm.clearSelectedCustomOptions = clearSelectedCustomOptions;
        vm.custom = false;
        vm.customOption = {};
        vm.customOption.endTime = initTime();
        vm.customOption.selected = [];
        vm.customOption.startTime = initTime();
        vm.customOptions = [];
        vm.days = ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'];
        vm.ensureValidTimes = ensureValidTimes;
        vm.iabStandard = {};
        vm.iabStandardOptions = lodash.keys(IAB_STANDARDS);
        vm.update = update;

        activate();

        function activate() {
            $scope.$watch('vm.model', reconcileModel);
        }

        function reconcileModel() {
            var daypartTarget = lodash.result(vm, 'model.daypartTarget', ''),
                custom;

            if (daypartTarget) {
                daypartTarget = lodash.compact(daypartTarget.split(/ OR /i));
                custom = !!daypartTarget.length &&
                    !!lodash.difference(daypartTarget, lodash.values(IAB_STANDARDS)).length;

                vm.custom = custom;

                if (custom) {
                    vm.customOptions = daypartTarget;
                }
                else {
                    lodash.forEach(IAB_STANDARDS, function (value, key) {
                        vm.iabStandard[key] = lodash.contains(daypartTarget, value);
                    });
                }
            }
        }

        function initDaypartOptionTemplate() {
            return lodash.template(CONSTANTS.CREATIVE_GROUP.DAYPART_TARGETING.CUSTOM.DAYPART_OPTION_TEMPLATE);
        }

        function initDaypartOptionTimeInputFormat() {
            return CONSTANTS.CREATIVE_GROUP.DAYPART_TARGETING.CUSTOM.DAYPART_OPTION_TIME_INPUT_FORMAT;
        }

        function initIabStandards() {
            return lodash.mapKeys(
                CONSTANTS.CREATIVE_GROUP.DAYPART_TARGETING.IAB_STANDARDS,
                lodash.rearg(lodash.camelCase, 1));
        }

        function initTime() {
            return $moment({
                hour: 0,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
        }

        function addCustomOption() {
            var customOption = vm.customOption,
                startTime = $moment(customOption.startTime).format(DAYPART_OPTION_TIME_INPUT_FORMAT),
                endTime = $moment(customOption.endTime).format(DAYPART_OPTION_TIME_INPUT_FORMAT),
                newCustomOptions = lodash.chain(vm.days)
                    .map(function (key) {
                        return customOption[key] && DAYPART_OPTION_TEMPLATE({
                            key: key,
                            startTime: startTime,
                            endTime: endTime
                        });
                    })
                    .compact()
                    .value();

            if (newCustomOptions.length) {
                vm.customOptions = lodash.chain(newCustomOptions).concat(vm.customOptions).compact().uniq().value();
                update();
            }
        }

        function addDisabled() {
            return !lodash.chain(vm.days)
                .filter(function (key) {
                    return vm.customOption[key];
                })
                .value()
                .length;
        }

        function clearCustomOptions() {
            if (vm.customOptions && vm.customOptions.length) {
                vm.customOptions = [];
                update();
            }
        }

        function clearSelectedCustomOptions() {
            var selected = vm.customOption.selected;

            if (selected && selected.length) {
                vm.customOptions = lodash.difference(vm.customOptions, selected);
                vm.customOption.selected = [];
                update();
            }
        }

        function ensureValidTimes() {
            var customOption = vm.customOption,
                start = $moment(customOption.startTime),
                end = $moment(customOption.endTime);

            customOption.endTime = start.isAfter(end) ? start.format() : end.format();
        }

        function update() {
            vm.model.daypartTarget = vm.custom ?
                (vm.customOptions || []).join(' Or ') :
                lodash.chain(IAB_STANDARDS)
                    .map(function (value, key) {
                        return vm.iabStandard[key] && value;
                    })
                    .compact()
                    .value()
                    .join(' OR ');

            setDirty();
        }

        function setDirty() {
            vm.$form.$setDirty();
        }
    }
})();
