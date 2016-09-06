(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTableBody', TeTableBodyDirective);

    function TeTableBodyDirective() {
        return {
            // IMPORTANT: rlt 20150628 - ng-table directive priority + 1
            //             See: https://github.com/esvit/ng-table/blob/v0.5.4/dist/ng-table.js#L774
            priority: 1002,

            require: '^teTable',
            restrict: 'A',
            scope: true,

            link: function (scope, element, attrs, controller) {
                scope.$table = controller;

                // NOTE: rlt 20150629 - This is part of a hack to resolve a rare issue when tableParams loses its scope.
                scope.$on('te-table-activated', function () {
                    controller.tableParams.settings().$scope = scope;
                });
            }
        };
    }
})();
