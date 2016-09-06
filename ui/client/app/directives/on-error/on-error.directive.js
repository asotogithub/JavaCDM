(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('onError', OnError);

    OnError.$inject = [
        '$parse'
    ];

    function OnError($parse) {
        return {
            restrict: 'A',
            compile: function ($element, attr) {
                var fn = $parse(attr.onError);

                return function (scope, element) {
                    element.on('error', function (event) {
                        scope.$apply(function () {
                            fn(scope, {
                                $event: event
                            });
                        });
                    });
                };
            }
        };
    }
})();
