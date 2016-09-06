(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableRemoveBtn', TeTableRemoveBtnDirective);

    function TeTableRemoveBtnDirective() {
        return {
            replace: true,
            require: '^teTable',
            restrict: 'E',
            templateUrl: 'components/directives/te-table/te-table-remove-btn/te-table-remove-btn.html',
            transclude: true
        };
    }
})();
