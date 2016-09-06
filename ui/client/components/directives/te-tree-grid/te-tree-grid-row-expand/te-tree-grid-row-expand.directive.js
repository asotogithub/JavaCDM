(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTreeGridRowExpand', TeTreeGridRowExpandDirective);

    TeTreeGridRowExpandDirective.$inject = ['$parse'];

    function TeTreeGridRowExpandDirective($parse) {
        return {
            require: 'teTreeGrid',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTreeGridRowExpand, null, true);

                return function (scope, _element, _attrs, controller) {
                    _element.on('rowExpand', function (evt) {
                        var row = evt.args.row,
                            id = row[controller.idField];

                        if (id) {
                            scope.$apply(function () {
                                fn(scope, {
                                    $row: controller.getRecord(id) || evt.args.row
                                });
                            });
                        }
                    });
                };
            }
        };
    }
})();
