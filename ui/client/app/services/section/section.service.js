(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SectionService', SectionService);

    SectionService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function SectionService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var sectionResource = $resource(API_SERVICE + 'SiteSections/:id/:criteria', {
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
            getList: function (siteId) {
                var sectionList = $q.defer();

                sectionResource.get({
                    query: 'siteId=' + siteId
                }).$promise
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get section list', sectionList));

                function getListComplete(response) {
                    sectionList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].SiteSection)));
                }

                return sectionList.promise;
            }
        };
    }
})();
