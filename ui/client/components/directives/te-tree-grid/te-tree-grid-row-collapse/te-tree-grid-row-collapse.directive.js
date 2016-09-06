(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTreeGridRowCollapse', TeTreeGridRowCollapseDirective);

    TeTreeGridRowCollapseDirective.$inject = ['$parse'];

    function TeTreeGridRowCollapseDirective($parse) {
        return {
            require: 'teTreeGrid',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTreeGridRowCollapse, null, true);

                return function (scope, _element, _attrs, controller) {
                    _element.on('rowCollapse', function (evt) {
                        var row = evt.args.row,
                            id = row[controller.idField];

                        if (id) {
                            scope.$apply(function () {
                                fn(scope, {
                                    $row: controller.getRecord(id)
                                });
                            });
                        }
                    });
                };
            }
        };
    }
})();
