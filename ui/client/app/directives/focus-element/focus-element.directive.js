(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('focusElement', focusElement);

    focusElement.$inject = ['$timeout'];

    function focusElement($timeout) {
        return {
            restrict: 'A',
            link: function (scope, element) {
                $timeout(function () {
                    element[0].focus();
                });
            }
        };
    }
})();
