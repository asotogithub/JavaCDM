(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableFilter', TeTableFilterDirective);

    TeTableFilterDirective.$inject = ['$parse', 'lodash'];

    function TeTableFilterDirective($parse, lodash) {
        return {
            require: 'teTable',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTableFilter, null, true);

                return function (scope, _element, _attrs, controller) {
                    scope.$watch(
                        function () {
                            return controller.filtered;
                        },

                        function (newValue, oldValue) {
                            if (lodash.xor(newValue, oldValue).length) {
                                fn(scope, {
                                    $filtered: controller.filtered
                                });
                            }
                        });
                };
            }
        };
    }
})();
