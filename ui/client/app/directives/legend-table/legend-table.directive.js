(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('legendTable', LegendTable);

    LegendTable.$inject = [];

    function LegendTable() {
        return {
            restrict: 'E',
            scope: {
                legend: '='
            },
            templateUrl: 'app/directives/legend-table/legend-table.html',
            replace: true,
            link: function ($scope) {
                $scope.getLegend = function () {
                    return $scope.legend;
                };
            }
        };
    }
})();
