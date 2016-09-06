(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('cookieTargeting', CookieTargetingDirective);

    function CookieTargetingDirective() {
        return {
            bindToController: true,
            controller: 'CookieTargetingController',
            controllerAs: 'vm',
            require: '^form',
            restrict: 'E',
            templateUrl: 'app/cm/creative-group/creative-group-details-form/cookie-targeting/cookie-targeting.html',

            scope: {
                model: '=',
                visible: '='
            },

            link: function (scope, element, attrs, controller) {
                scope.vm.$form = controller;
            }
        };
    }
})();
