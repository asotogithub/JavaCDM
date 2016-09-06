(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableSelect', TeTableSelectDirective);

    TeTableSelectDirective.$inject = ['$parse', 'lodash'];

    function TeTableSelectDirective($parse, lodash) {
        return {
            require: 'teTable',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTableSelect, null, true);

                return function (scope, _element, _attrs, controller) {
                    scope.$watch(
                        function () {
                            return controller.selection;
                        },

                        function (newValue, oldValue) {
                            if (lodash.xor(newValue, oldValue).length) {
                                fn(scope, {
                                    $selection: controller.getSelection()
                                });
                            }
                        });
                };
            }
        };
    }
})();
