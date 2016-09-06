(function () {
    'use strict';

    angular
        .module('te.components')
        .filter('gridFilter', GridFilter);

    GridFilter.$inject = ['lodash'];

    function GridFilter(lodash) {
        return function (dataArray, options) {
            var searchVal = (options && options.searchVal || '').toLowerCase(),
                searchFields = options && options.searchFields;

            if (angular.isUndefined(options) || !options.hasOwnProperty('searchVal')) {
                throw new Error('`options` must be a hash and contain `searchVal` key');
            }

            if (angular.isUndefined(dataArray)) {
                return;
            }

            if (!searchVal.length) {
                return dataArray;
            }

            return lodash.filter(dataArray, function (dataRow) {
                return lodash.any(searchFields || lodash.keys(dataRow), function (searchField) {
                    return (dataRow[searchField] || '').toString().toLowerCase()
                            .indexOf(searchVal) !== -1;
                });
            });
        };
    }
})();
