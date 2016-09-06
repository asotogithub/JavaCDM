(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableBtnSingleSelected', TeTableBtnSingleSelectedDirective);

    function TeTableBtnSingleSelectedDirective() {
        return {
            require: '^teTable',
            restrict: 'A',

            link: function (scope, element, attrs, controller) {
                scope.$watch(
                    function () {
                        return controller.model &&
                            controller.model.length &&
                            controller.selection &&
                            controller.selection.length;
                    },

                    function (length) {
                        if (length === 1) {
                            element.removeAttr('disabled');
                        }
                        else {
                            element.attr('disabled', 'disabled');
                        }
                    });
            }
        };
    }
})();

