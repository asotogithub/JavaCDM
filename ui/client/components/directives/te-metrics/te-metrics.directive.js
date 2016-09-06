(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teMetrics', TeMetricsDirective);

    function TeMetricsDirective() {
        return {
            scope: {
                model: '=',
                legend: '='
            },
            templateUrl: 'components/directives/te-metrics/te-metrics.html'
        };
    }
})();
