(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('integerInput', IntegerInput);

    function IntegerInput() {
        return {
            require: '?ngModel',
            link: function (scope, element) {
                element.on('keydown', function (event) {
                    // Avoid these characters: e|.|,|.
                    if ([69, 110, 188, 190].indexOf(event.which) > -1) {
                        return false;
                    }

                    return true;
                });
            }
        };
    }
})();
