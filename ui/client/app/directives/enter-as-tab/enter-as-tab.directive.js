(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('enterAsTab', EnterAsTab);

    EnterAsTab.$inject = [];

    function EnterAsTab() {
        return function (scope, element) {
            element.bind('keydown keypress', function (event) {
                if (event.which === 13) {
                    event.preventDefault();
                    var fields = $(this).parents('form:eq(0),body').find('input, textarea, select'),
                        index = fields.index(this);

                    if (index > -1 && index + 1 < fields.length) {
                        fields.eq(index + 1).focus().select();
                    }
                }
            });
        };
    }
})();
