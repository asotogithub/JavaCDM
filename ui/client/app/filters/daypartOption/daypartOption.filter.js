(function () {
    'use strict';

    angular
        .module('uiApp')
        .filter('daypartOption', DaypartOptionFilter);

    DaypartOptionFilter.$inject = ['$moment', '$translate', 'CONSTANTS', 'lodash'];

    function DaypartOptionFilter($moment, $translate, CONSTANTS, lodash) {
        var CUSTOM = CONSTANTS.CREATIVE_GROUP.DAYPART_TARGETING.CUSTOM,
          DAYPART_OPTION_FILTER_TEMPLATE = lodash.template(CUSTOM.DAYPART_OPTION_FILTER_TEMPLATE),
          DAYPART_OPTION_REGEXP = new RegExp(CUSTOM.DAYPART_OPTION_REGEXP),
          TIME_INPUT_FORMAT = CUSTOM.DAYPART_OPTION_TIME_INPUT_FORMAT,
          TIME_OUTPUT_FORMAT = CUSTOM.DAYPART_OPTION_TIME_OUTPUT_FORMAT;

        function format(parsed) {
            return DAYPART_OPTION_FILTER_TEMPLATE({
                day: $translate.instant('daypartTargeting.day.' + parsed[1]),
                startTime: $moment(parsed[2], TIME_INPUT_FORMAT).format(TIME_OUTPUT_FORMAT),
                endTime: $moment(parsed[3], TIME_INPUT_FORMAT).format(TIME_OUTPUT_FORMAT)
            });
        }

        return function (input) {
            var parsed = input.match(DAYPART_OPTION_REGEXP);

            return parsed ? format(parsed) : input;
        };
    }
})();
