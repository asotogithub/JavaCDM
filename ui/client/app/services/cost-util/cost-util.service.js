(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CostUtilService', CostUtilService);

    CostUtilService.$inject = [
        '$log',
        'CONSTANTS',
        'lodash'
    ];

    function CostUtilService(
        $log,
        CONSTANTS,
        lodash) {
        return {
            calculateInventory: function (adSpend, rate, rateType) {
                var RATE_TYPE = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST,
                    res = null;

                switch (rateType) {
                    case RATE_TYPE.CPM:
                        res = calculateVolume(adSpend, rate, 1000);
                        break;
                    case RATE_TYPE.CPC:
                        res = calculateVolume(adSpend, rate);
                        break;
                    case RATE_TYPE.CPA:
                        res = calculateVolume(adSpend, rate);
                        break;
                    case RATE_TYPE.FLT:
                        res = CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT;
                        break;
                    case RATE_TYPE.CPL:
                        res = calculateVolume(adSpend, rate);
                        break;
                    default:
                        $log.warn('Rate Type not supported: ' + angular.toJson(rateType));
                        res = calculateVolume(adSpend, rate);
                }

                return res.toString();
            },

            calculateNetAdSpend: function (adSpend, margin) {
                return calculateGrossValue(adSpend, margin);
            },

            calculateNetRate: function (rate, margin) {
                return calculateGrossValue(rate, margin);
            },

            getDefaultRateType: function () {
                var key = lodash.findKey(CONSTANTS.COST_DETAIL.RATE_TYPE.LIST, function (item) {
                    return angular.equals(item.KEY, CONSTANTS.COST_DETAIL.RATE_TYPE.DEFAULT);
                });

                return CONSTANTS.COST_DETAIL.RATE_TYPE.LIST[key];
            },

            getRateType: function (rateTypeValue) {
                var rateType = lodash.find(lodash.map(CONSTANTS.COST_DETAIL.RATE_TYPE.LIST), function (item) {
                    return item.VALUE === rateTypeValue;
                });

                return rateType;
            }
        };

        function calculateGrossValue(grossValue, margin) {
            return grossValue - grossValue * margin / 100;
        }

        function calculateVolume(adSpend, rate, factor) {
            if (!factor) {
                factor = 1;
            }

            if (!rate || rate === 0 || !adSpend || adSpend === 0) {
                return CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT;
            }

            var res = adSpend / rate * factor;

            res = res <= 1 ? CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT : res;
            res = Math.ceil(parseFloat(res));
            if (res.toString().indexOf('e') > -1) {
                res = changeNotation(res.toString());
            }

            return res;
        }

        function changeNotation(n) {
            var parts = n.split('e+'),
                first = parts[0].replace('.', ''),
                zeroes = parseInt(parts[1], 10) - (first.length - 1),
                i;

            for (i = 0; i < zeroes; i++) {
                first += '0';
            }

            return first;
        }
    }
})();
