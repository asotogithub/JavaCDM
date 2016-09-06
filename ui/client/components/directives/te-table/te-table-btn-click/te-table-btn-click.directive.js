(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableBtnClick', TeTableBtnClickDirective);

    TeTableBtnClickDirective.$inject = ['$parse'];

    function TeTableBtnClickDirective($parse) {
        return {
            require: '^teTable',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTableBtnClick, null, true);

                return function (scope, _element, _attrs, controller) {
                    _element.on('click', function (evt) {
                        fn(scope, {
                            $event: evt,
                            $selection: controller.getSelection()
                        });
                    });
                };
            }
        };
    }
})();
