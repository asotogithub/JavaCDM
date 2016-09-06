(function () {
    'use strict';

    angular.module('uiApp')
        .service('TagInjectionService', TagInjectionService);

    TagInjectionService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function TagInjectionService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var htmlInjectionTagsResource = $resource(API_SERVICE + 'HtmlInjectionTags/:id/:criteria', {
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
            getHtmlInjectionTagsById: function (id) {
                var htmlInjectionTagsDetails = $q.defer();

                htmlInjectionTagsResource.get({
                    id: id
                }).$promise
                    .then(getHtmlInjectionTagsByIdComplete)
                    .catch(errorHandler('Cannot get HTML Injection Tags', htmlInjectionTagsDetails));

                function getHtmlInjectionTagsByIdComplete(response) {
                    htmlInjectionTagsDetails.resolve(response);
                }

                return htmlInjectionTagsDetails.promise;
            },

            getTagPlacementsAssociated: function (tagInjectionId) {
                var deferred = $q.defer();

                htmlInjectionTagsResource.get({
                    id: tagInjectionId,
                    criteria: 'placementAssociated'
                }).$promise
                    .then(getTagPlacementsAssociatedComplete)
                    .catch(errorHandler('Cannot get associated placements', deferred));

                function getTagPlacementsAssociatedComplete(response) {
                    deferred.resolve({
                        placements: [].concat(lodash.result(response, 'records[0].PlacementView', [])),
                        totalNumberOfRecords: response.totalNumberOfRecords
                    });
                }

                return deferred.promise;
            },

            updateHtmlInjectionTag: function (htmlInjectionTag) {
                var deferred = $q.defer();

                htmlInjectionTagsResource.update({
                    id: htmlInjectionTag.id
                }, htmlInjectionTag).$promise
                    .then(updateHtmlInjectionTagComplete)
                    .catch(errorHandler('Error while performing update HTML Injection Tag request', deferred));

                function updateHtmlInjectionTagComplete(response) {
                    deferred.resolve(response.toJSON());
                }

                return deferred.promise;
            },

            deleteHtmlInjectionTag: function (htmlInjectionTag) {
                var deferred = $q.defer();

                htmlInjectionTagsResource.update({
                    criteria: 'bulkDelete'
                }, htmlInjectionTag).$promise
                    .then(deleteHtmlInjectionTagComplete)
                    .catch(errorHandler('Error while performing update HTML Injection Tag request', deferred));

                function deleteHtmlInjectionTagComplete(response) {
                    deferred.resolve(response.toJSON());
                }

                return deferred.promise;
            },

            searchPlacementView: function (
                tagId,
                pattern,
                soPlacement,
                soSite,
                soCampaign
            ) {
                var resultSearch = $q.defer();

                htmlInjectionTagsResource.get({
                    id: tagId,
                    criteria: 'searchPlacementsAssociated',
                    soCampaign: soCampaign,
                    soSite: soSite,
                    soPlacement: soPlacement,
                    pattern: pattern
                }).$promise
                    .then(getSearchViewComplete)
                    .catch(errorHandler('Cannot get result from search', resultSearch));

                function getSearchViewComplete(response) {
                    resultSearch.resolve({
                        placements: [].concat(lodash.result(response, 'records[0].PlacementView', [])),
                        totalNumberOfRecords: response.totalNumberOfRecords
                    });
                }

                return resultSearch.promise;
            }
        };
    }
}());
