(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SizeService', SizeService);

    SizeService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function SizeService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var sizeResource = $resource(API_SERVICE + 'Sizes/:id/:criteria', {
                    id: '@id',
                    criteria: '@criteria'
                }, {
                    update: {
                        method: 'PUT'
                    }
                }
            ),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getList: function () {
                var sizeList = $q.defer();

                sizeResource.get().$promise
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get size list', sizeList));

                function getListComplete(response) {
                    sizeList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Size)));
                }

                return sizeList.promise;
            }
        };
    }
})();
