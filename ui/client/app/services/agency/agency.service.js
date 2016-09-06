(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('AgencyService', AgencyService);

    AgencyService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'UserService',
        'lodash'
    ];

    function AgencyService(
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        UserService,
        lodash) {
        var agencyResource = $resource(API_SERVICE + 'Agencies/:id/:criteria', null, {
                update: {
                    method: 'PUT'
                }
            }),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getUsersTrafficking: function () {
                var usersTrafficking = $q.defer();

                UserService.getUser().then(function (user) {
                    return agencyResource.get({
                        id: user.agencyId,
                        criteria: 'traffickingUsers'
                    }).$promise;
                }).then(getUsersTraffickingComplete)
                    .catch(errorHandler('Cannot get trafficking users', usersTrafficking));

                function getUsersTraffickingComplete(response) {
                    usersTrafficking.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].UserView)));
                }

                return usersTrafficking.promise;
            },

            getPlacements: function (params) {
                var placements = $q.defer();

                UserService.getUser()
                    .then(function (user) {
                        return agencyResource.get({
                            criteria: 'placementView',
                            id: user.agencyId,
                            levelType: params.levelType,
                            advertiserId: params.advertiserId,
                            brandId: params.brandId,
                            campaignId: params.campaignId,
                            siteId: params.siteId,
                            sectionId: params.sectionId
                        }).$promise;
                    })
                    .then(getPlacementsComplete)
                    .catch(errorHandler('Cannot get placements', placements));

                function getPlacementsComplete(response) {
                    placements.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].PlacementView)));
                }

                return placements.promise;
            },

            getHTMLTagInjections: function () {
                var tagInjectionList = $q.defer();

                UserService.getUser().then(function (user) {
                    return agencyResource.get({
                        id: user.agencyId,
                        criteria: 'htmlInjectionTags'
                    }).$promise;
                }).then(getTajInjectionComplete)
                    .catch(errorHandler('Cannot get tag injection list', tagInjectionList));

                function getTajInjectionComplete(response) {
                    return tagInjectionList.resolve(lodash.compact([].concat(
                            response &&
                            response.records &&
                            response.records[0] &&
                            response.records[0].HtmlInjectionTags)));
                }

                return tagInjectionList.promise;
            },

            getHtmlInjectionTagAssociation: function (
                agencyId,
                levelType,
                campaignId,
                siteId,
                sectionId,
                placementId
            ) {
                var placement = $q.defer();

                agencyResource.get({
                    id: agencyId,
                    criteria: 'htmlInjectionTagAssociation',
                    levelType: levelType,
                    campaignId: campaignId,
                    siteId: siteId,
                    sectionId: sectionId,
                    placementId: placementId
                }).$promise
                    .then(getHtmlInjectionTagAssociationComplete)
                    .catch(errorHandler('Error while performing get HtmlInjectionTagAssociation request', placement));
                function getHtmlInjectionTagAssociationComplete(response) {
                    placement.resolve(response);
                }

                return placement.promise;
            },

            saveTrackingTag: function (trackingTag, criteria) {
                var deferred = $q.defer();

                UserService.getUser()
                    .then(function (user) {
                        return agencyResource.save({
                            id: user.agencyId,
                            criteria: criteria
                        }, trackingTag).$promise;
                    })
                    .then(getPlacementsComplete)
                    .catch(errorHandler('Error while performing save request', deferred));

                function getPlacementsComplete(response) {
                    deferred.resolve(response);
                }

                return deferred.promise;
            },

            searchPlacementView: function (
                agencyId,
                advertiserId,
                brandId,
                soCampaign,
                soSite,
                soSection,
                soPlacement,
                pattern
            ) {
                var resultSearch = $q.defer();

                agencyResource.get({
                    id: agencyId,
                    criteria: 'searchPlacementView',
                    advertiserId: advertiserId,
                    brandId: brandId,
                    soCampaign: soCampaign,
                    soSite: soSite,
                    soSection: soSection,
                    soPlacement: soPlacement,
                    pattern: pattern
                }).$promise
                    .then(getSearchViewComplete)
                    .catch(errorHandler('Cannot get result from search', resultSearch));

                function getSearchViewComplete(response) {
                    resultSearch.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].PlacementView)));
                }

                return resultSearch.promise;
            },

            bulkSaveHtmlInjectionTagAssociations: function (tagAssociations, advertiserId, brandId) {
                var deferred = $q.defer();

                UserService.getUser()
                    .then(function (user) {
                        return agencyResource.update({
                            id: user.agencyId,
                            criteria: 'htmlInjectionTagAssociationsBulk',
                            advertiserId: advertiserId,
                            brandId: brandId
                        }, tagAssociations).$promise;
                    })
                    .then(bulkSaveHtmlInjectionTagAssociationsComplete)
                    .catch(errorHandler('Error while performing bulk save request', deferred));

                function bulkSaveHtmlInjectionTagAssociationsComplete(response) {
                    deferred.resolve(response);
                }

                return deferred.promise;
            }
        };
    }
})();

