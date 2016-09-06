(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('daypartTargeting', DaypartTargetingDirective);

    function DaypartTargetingDirective() {
        return {
            bindToController: true,
            controller: 'DaypartTargetingController',
            controllerAs: 'vm',
            require: '^form',
            restrict: 'E',
            templateUrl: 'app/cm/creative-group/creative-group-details-form/daypart-targeting/daypart-targeting.html',

            scope: {
                model: '='
            },

            link: function (scope, element, attrs, controller) {
                scope.vm.$form = controller;
            }
        };
    }
})();

