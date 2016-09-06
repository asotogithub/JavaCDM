(function () {
    'use strict';

    angular
        .module('te.components')
        .filter('useFilter', UseFilter);

    UseFilter.$inject = ['$filter'];

    function UseFilter($filter) {
        return function (value, filterName) {
            return $filter(filterName)(value);
        };
    }
})();
