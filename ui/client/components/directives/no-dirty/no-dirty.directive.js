(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('noDirty', function () {
            return {
                require: 'ngModel',
                restrict: 'A',

                link: function (scope, element, attrs, controller) {
                    controller.$setDirty = angular.noop;
                }
            };
        });
})();
