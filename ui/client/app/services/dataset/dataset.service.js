(function () {
    'use strict';

    angular.module('uiApp')
        .service('DatasetService', DatasetService);

    DatasetService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'Utils'
    ];

    function DatasetService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        Utils) {
        var datasetResource = $resource(API_SERVICE + 'Datasets/:id/:criteria', {
                id: '@id',
                criteria: '@criteria'
            }, {
                update: {
                    method: 'PUT'
                }
            }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getDatasetMetrics: function (datasetId, startDate, endDate, options) {
                var datasetMetrics = $q.defer();

                datasetResource.get({
                    id: datasetId,
                    criteria: 'metrics',
                    startDate: startDate,
                    endDate: endDate
                }).$promise
                    .then(getDatasetMetricsComplete)
                    .catch(getDatasetMetricsFailed);

                function getDatasetMetricsComplete(response) {
                    datasetMetrics.resolve(response);
                }

                function getDatasetMetricsFailed(error) {
                    if (!Utils.isUndefinedOrNull(options) && options.showError === true) {
                        errorHandler('Cannot get dataset metrics', datasetMetrics)(error);
                    }
                    else {
                        datasetMetrics.resolve();
                    }
                }

                return datasetMetrics.promise;
            }
        };
    }
}());
