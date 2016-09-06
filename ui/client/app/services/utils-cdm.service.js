(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('UtilsCDMService', UtilsCDMService);

    UtilsCDMService.$inject = ['$translate', 'CONSTANTS', 'lodash'];

    function UtilsCDMService($translate, CONSTANTS, lodash) {
        this.states = [
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.UNKNOWN,
                name: $translate.instant('global.unknown')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.NEW,
                name: $translate.instant('global.new')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.UPDATED,
                name: $translate.instant('global.updated')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.TRAFFICKED,
                name: $translate.instant('global.trafficked')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.COMP,
                name: $translate.instant('global.comp')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.STATES.TRAFFICKING,
                name: $translate.instant('global.trafficking')
            }
        ];

        this.eventType = [
            {
                id: CONSTANTS.SITE_MEASUREMENT.EVENT_TYPE.STANDARD,
                name: $translate.instant('global.standard')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.EVENT_TYPE.TRU_TAG,
                name: $translate.instant('global.truTag')
            }
        ];

        this.smEventType = [
            {
                id: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.OTHER,
                name: $translate.instant('global.other')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.CONVERSION,
                name: $translate.instant('global.conversion')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.CONVERSION_REVENUE,
                name: $translate.instant('global.conversionRevenue')
            },
            {
                id: CONSTANTS.SITE_MEASUREMENT.SM_EVENT_TYPE.MEASURED,
                name: $translate.instant('global.measured')
            }
        ];

        this.getEnum = function (key, emun) {
            var result = lodash.find(emun, function (item) {
                return item.id === key;
            });

            if (result) {
                return result;
            }

            return emun[0];
        };

        this.getCount = function (obj) {
            if (obj) {
                return obj.length;
            }
            else {
                return 0;
            }
        };
    }
})();
