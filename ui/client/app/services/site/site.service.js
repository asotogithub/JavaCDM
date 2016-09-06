(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteService', SiteService);

    SiteService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function SiteService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var siteResource = $resource(API_SERVICE + 'Sites/:id/:criteria', {
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
                var sitesList = $q.defer();

                siteResource.get().$promise
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get site list', sitesList));

                function getListComplete(response) {
                    sitesList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Site)));
                }

                return sitesList.promise;
            }
        };
    }
})();
