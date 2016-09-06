(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('SiteSectionService', SiteSectionService);

    SiteSectionService.$inject = ['$q', '$resource', 'API_SERVICE', 'ErrorRequestHandler'];

    function SiteSectionService($q, $resource, API_SERVICE, ErrorRequestHandler) {
        var siteSectionResource = $resource(API_SERVICE + 'SiteSections/:id'),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getSiteSection: function (siteSectionId) {
                var siteSection = $q.defer();

                siteSectionResource.get({
                    id: siteSectionId
                }).$promise
                    .then(getSiteSectionComplete)
                    .catch(errorHandler('Cannot get site section', siteSection));

                function getSiteSectionComplete(response) {
                    siteSection.resolve(response.toJSON());
                }

                return siteSection.promise;
            }
        };
    }
})();
