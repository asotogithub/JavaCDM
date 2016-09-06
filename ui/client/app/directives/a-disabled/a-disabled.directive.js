(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('aDisabled', ADisabled);

    ADisabled.$inject = [];

    function ADisabled() {
        return {
            restrict: 'A',
            scope: {
                aDisabled: '=aDisabled'
            },
            compile: function () {
                //Toggle "disabled" to class when aDisabled becomes true
                return function (scope, iElement) {
                    scope.$watch('aDisabled', function (newValue) {
                        if (newValue !== undefined) {
                            iElement.toggleClass('disabled', newValue);
                        }
                    });

                    //Disable href on click
                    iElement.on('click', function (e) {
                        if (scope.aDisabled) {
                            e.preventDefault();
                        }
                    });
                };
            }
        };
    }
})();

