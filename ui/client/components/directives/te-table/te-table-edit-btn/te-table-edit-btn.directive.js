(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableEditBtn', TeTableEditBtnDirective);

    function TeTableEditBtnDirective() {
        return {
            replace: true,
            require: '^teTable',
            restrict: 'E',
            templateUrl: 'components/directives/te-table/te-table-edit-btn/te-table-edit-btn.html',
            transclude: true
        };
    }
})();
