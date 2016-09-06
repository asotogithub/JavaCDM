(function () {
    'use strict';

    angular
        .module('te.components')
        .filter('valuesFilter', ValuesFilter);

    ValuesFilter.$inject = ['lodash', 'Utils'];

    function ValuesFilter(lodash, Utils) {
        return function (dataArray, options) {
            if (Utils.isUndefinedOrNull(options)) {
                return dataArray;
            }

            if (Utils.isUndefinedOrNull(options[0]) || !Array.isArray(options) ||
                !options[0].hasOwnProperty('fieldName') || !options[0].hasOwnProperty('values')) {
                throw new Error('`options` must be an array and each element must contain `fieldName` and `values`');
            }

            if (angular.isUndefined(dataArray)) {
                return;
            }

            return lodash.filter(dataArray, function (dataRow) {
                return lodash.every(options, function (field) {
                    return lodash.any(field.values, function (value) {
                        return dataRow[field.fieldName] === value;
                    });
                });
            });
        };
    }
})();
