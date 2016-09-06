(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableAddBtn', TeTableAddBtnDirective);

    function TeTableAddBtnDirective() {
        return {
            replace: true,
            require: '^teTable',
            restrict: 'E',
            templateUrl: 'components/directives/te-table/te-table-add-btn/te-table-add-btn.html',
            transclude: true
        };
    }
})();
