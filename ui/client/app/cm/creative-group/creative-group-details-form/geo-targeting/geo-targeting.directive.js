(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('geoTargeting', GeoTargetingDirective);

    function GeoTargetingDirective() {
        return {
            bindToController: true,
            controller: 'GeoTargetingController',
            controllerAs: 'vm',
            require: '^form',
            restrict: 'E',
            templateUrl: 'app/cm/creative-group/creative-group-details-form/geo-targeting/geo-targeting.html',

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
