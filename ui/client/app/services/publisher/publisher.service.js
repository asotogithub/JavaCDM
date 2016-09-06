(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PublisherService', PublisherService);

    PublisherService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function PublisherService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var publisherResource = $resource(API_SERVICE + 'Publishers', {
                }, {
                    get: {
                        method: 'GET'
                    }
                }
            ),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getList: function () {
                var deferredList = $q.defer();

                publisherResource.get().$promise
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get publisher list', deferredList));

                function getListComplete(response) {
                    deferredList.resolve([].concat(lodash.result(response, 'records[0].Publisher', [])));
                }

                return deferredList.promise;
            }
        };
    }
})();
