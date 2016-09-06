(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CampaignsService', CampaignsService);

    CampaignsService.$inject = [
        '$q',
        '$resource',
        '$rootScope',
        '$translate',
        'API_SERVICE',
        'CONSTANTS',
        'DialogFactory',
        'ErrorRequestHandler',
        'Upload',
        'UserService',
        'Utils',
        'lodash'
    ];

    function CampaignsService(
        $q,
        $resource,
        $rootScope,
        $translate,
        API_SERVICE,
        CONSTANTS,
        DialogFactory,
        ErrorRequestHandler,
        Upload,
        UserService,
        Utils,
        lodash) {
        var campaignListResource = $resource(API_SERVICE + 'Agencies/:agencyId/campaigns'),
            campaignListMetricsResource = $resource(API_SERVICE + 'Agencies/:agencyId/metrics/campaigns'),
            campaignPubSiteSectionSizeResource = $resource(API_SERVICE + 'Agencies/bulkPublisherSiteSectionSize'),
            campaignResource = $resource(API_SERVICE + 'Campaigns/:id/:criteria',
                {
                    id: '@id'
                },
                {
                    update: {
                        method: 'PUT'
                    }
                }),

            campaignExportResource = $resource(API_SERVICE + 'Campaigns/:campaignId/:exportType',
                {
                    campaignId: '@campaignId',
                    exportType: '@exportType'
                },
                {
                    get: {
                        responseType: 'blob',
                        transformResponse: function (data, headersGetter, status) {
                            return status === 200 ? {
                                data: data,
                                type: headersGetter('content-type') ? headersGetter('content-type') :
                                    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                                name: headersGetter('content-disposition') ? headersGetter('content-disposition')
                                    .match(new RegExp('filename=(.*?)(\\;|$)'))[1] : 'campaignExport'
                            } : data;
                        }
                    }
                }),

            campaignCreativeInsertionResource = $resource(API_SERVICE + 'Campaigns/:id/creativeInsertions/:criteria',
                {
                    id: '@id'
                }),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getList: function () {
                var campaigns = $q.defer();

                UserService.getUser()
                    .then(function (user) {
                        return $q.all([
                            campaignListResource.get({
                                agencyId: user.agencyId
                            }).$promise,
                            $translate(['global.active', 'global.inactive'])
                        ]);
                    })
                    .then(getListComplete)
                    .catch(errorHandler('Cannot get campaign list', campaigns));

                function getListComplete(args) {
                    var response = args[0], // From campaignListResource.get()
                        translations = args[1], // From $translate()
                        _campaigns = lodash.compact([].concat(
                            response &&
                            response.records &&
                            response.records[0] &&
                            response.records[0].CampaignDTO));

                    angular.forEach(_campaigns, function (campaign) {
                        campaign.isActiveDisplay = campaign.isActive === 'Y' ?
                            translations['global.active'] :
                            translations['global.inactive'];
                    });

                    campaigns.resolve(_campaigns);
                }

                return campaigns.promise;
            },

            getListMetrics: function () {
                var metrics = $q.defer(),
                    that = this;

                UserService.getUser()
                    .then(function (user) {
                        return campaignListMetricsResource.get({
                            agencyId: user.agencyId
                        }).$promise;
                    })
                    .then(getListMetricsComplete)
                    .catch(errorHandler('Cannot get list of metrics', metrics));

                function getListMetricsComplete(response) {
                    metrics.resolve(that.getMetrics(response));
                }

                return metrics.promise;
            },

            getMetrics: function (response) {
                return lodash.compact([].concat(
                    response &&
                    response.records &&
                    response.records[0] &&
                    response.records[0].Metrics));
            },

            getCampaignMetrics: function (campaignId) {
                var campaignMetrics = $q.defer(),
                    that = this;

                campaignResource.get({
                    id: campaignId,
                    criteria: 'metrics'
                }).$promise
                    .then(getCampaignMetrics)
                    .catch(errorHandler('Cannot get campaign metrics', campaignMetrics));

                function getCampaignMetrics(response) {
                    campaignMetrics.resolve(that.getMetrics(response));
                }

                return campaignMetrics.promise;
            },

            getCampaign: function (campaignId) {
                var campaign = $q.defer();

                campaignResource.get({
                    id: campaignId
                }).$promise
                    .then(getCampaignComplete)
                    .catch(errorHandler('Cannot get campaign', campaign));

                function getCampaignComplete(response) {
                    $rootScope.$emit('update.model.campaign', response.toJSON());
                    campaign.resolve(response.toJSON());
                }

                return campaign.promise;
            },

            getCampaignDetails: function (campaignId) {
                var campaignDetails = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'detail'
                }).$promise
                    .then(getCampaignDetailsComplete)
                    .catch(errorHandler('Cannot get campaign details', campaignDetails));

                function getCampaignDetailsComplete(response) {
                    campaignDetails.resolve(response.toJSON());
                }

                return campaignDetails.promise;
            },

            updateCampaignDetails: function (campaignDetails) {
                var detailsDefer = $q.defer();

                $rootScope.$emit('update.model.campaign', campaignDetails);
                campaignResource.update(campaignDetails).$promise
                    .then(updateCampaignDetailsComplete)
                    .catch(errorHandler('Cannot update campaign details', detailsDefer));

                function updateCampaignDetailsComplete(response) {
                    detailsDefer.resolve(response);
                }

                return detailsDefer.promise;
            },

            getCreatives: function (campaignId) {
                var creativeList = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'creatives'
                }).$promise
                    .then(getCreativesComplete)
                    .catch(errorHandler('Cannot get creative list', creativeList));

                function getCreativesComplete(response) {
                    return creativeList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Creative)));
                }

                return creativeList.promise;
            },

            getCreativeGroups: function (campaignId) {
                var creativeGroups = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'creativeGroups'
                }).$promise
                    .then(getCreativeGroupsComplete)
                    .catch(errorHandler('Cannot get creative groups', creativeGroups));

                function getCreativeGroupsComplete(response) {
                    creativeGroups.resolve(lodash.chain([])
                        .concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].CreativeGroupDtoForCampaigns)
                        .compact()
                        .map(function (creativeGroup) {
                            var _creativeGroup = lodash.mapValues(creativeGroup, function (value) {
                                try {
                                    return angular.fromJson(value);
                                }
                                catch (e) {
                                    return value;
                                }
                            });

                            return lodash.extend(_creativeGroup, {
                                priority: creativeGroup.name === CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME ?
                                    null :
                                    _creativeGroup.priority
                            });
                        })
                        .value());
                }

                return creativeGroups.promise;
            },

            exportResource: function (campaignId, format, type) {
                var exportFile = $q.defer();

                campaignExportResource.get({
                    exportType: 'export',
                    campaignId: campaignId,
                    format: format,
                    type: type
                }).$promise
                    .then(exportResourceComplete)
                    .catch(errorHandler('Cannot connect to file export.', exportFile));

                function exportResourceComplete(response) {
                    exportFile.resolve(response.toJSON());
                }

                return exportFile.promise;
            },

            importResource: function (campaignId, type, uuid, ignoreErrors, actions) {
                var importProcess = $q.defer(),
                    payload;

                payload = Utils.isUndefinedOrNull(actions) ? {} : actions;
                campaignResource.update({
                    id: campaignId,
                    criteria: 'import',
                    type: type,
                    uuid: uuid,
                    ignoreErrors: ignoreErrors
                }, payload).$promise
                    .then(importResourceComplete)
                    .catch(importResourceFailed);

                function importResourceComplete(response) {
                    importProcess.resolve(response);
                }

                function importResourceFailed(error) {
                    importProcess.reject(error);
                }

                return importProcess.promise;
            },

            uploadResource: function (campaignId, file, type) {
                return Upload.http({
                    url: API_SERVICE + 'Campaigns/' + campaignId + '/upload?type=' + type,
                    data: file,
                    method: 'POST',
                    transformRequest: function (data, headersGetter) {
                        var formData = new FormData(),
                            headers;

                        formData.append('file', data);
                        headers = headersGetter();
                        delete headers['Content-Type'];

                        return formData;
                    }
                });
            },

            exportIssuesResource: function (campaignId, format, type, uuid) {
                var exportFile = $q.defer();

                campaignExportResource.get({
                    exportType: 'issues',
                    campaignId: campaignId,
                    format: format,
                    type: type,
                    uuid: uuid
                }).$promise
                    .then(exportIssuesResourceComplete)
                    .catch(errorHandler('Cannot connect to file export.', exportFile));

                function exportIssuesResourceComplete(response) {
                    exportFile.resolve(response.toJSON());
                }

                return exportFile.promise;
            },

            saveCampaignDetails: function (campaignDetails) {
                var detailsDefer = $q.defer();

                campaignResource.save(campaignDetails).$promise
                    .then(saveCampaignDetailsComplete)
                    .catch(saveCampaignDetailsFailed);

                function saveCampaignDetailsComplete(response) {
                    detailsDefer.resolve(response);
                }

                function saveCampaignDetailsFailed(error) {
                    detailsDefer.reject(error);
                }

                return detailsDefer.promise;
            },

            uploadCreative: function (campaignId, file) {
                return Upload.http({
                    url: API_SERVICE + 'Campaigns/' + campaignId + '/creativeUpload?filename=' + file.name,
                    file: file,
                    data: file,
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/octet-stream'
                    }
                });
            },

            /**
             * Converts a denormalized metrics collection and reduces it to a single object containing
             * summarized metrics.
             * @param metrics - The metrics object to summarize.
             * @returns Aggregated metrics.
             */
            reduceMetricsData: function (metrics) {
                return lodash.reduce(metrics, function (result, val) {
                        result.impressions += val.impressions;
                        result.conversions += val.conversions;
                        result.clicks += val.clicks;
                        result.cost += val.cost;
                        return result;
                    },

                    {
                        clicks: 0,
                        impressions: 0,
                        conversions: 0,
                        cost: 0,
                        eCpa: function () {
                            return this.conversions / this.cost;
                        },

                        ctr: function () {
                            return this.clicks / this.impressions;
                        }
                    });
            },

            buildKpiDisplay: function (metrics) {
                var grouped = this.reduceMetricsData(metrics);

                return [
                    {
                        key: 'Impressions',
                        value: grouped.impressions,
                        icon: 'fa-eye',
                        type: 'number'
                    },
                    {
                        key: 'CTR',
                        value: grouped.ctr(),
                        icon: 'fa-mouse-pointer',
                        type: 'percentage'
                    },
                    {
                        key: 'Cost',
                        value: grouped.cost,
                        icon: 'fa-usd',
                        type: 'currency'
                    },
                    {
                        key: 'Conversions',
                        value: grouped.conversions,
                        icon: 'fa-exchange',
                        type: 'number'
                    },
                    {
                        key: 'eCPA',
                        value: grouped.eCpa(),
                        icon: 'fa-usd',
                        type: 'currency'
                    }
                ];
            },

            getPackagePlacements: function (campaignId) {
                var packagePlacements = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'packagePlacementView'
                }).$promise
                    .then(getPackagePlacementsComplete)
                    .catch(errorHandler('Cannot get package placements list', packagePlacements));

                function getPackagePlacementsComplete(response) {
                    packagePlacements.resolve(
                        [].concat(lodash.result(response, 'records[0].PackagePlacementView', [])));
                }

                return packagePlacements.promise;
            },

            getCreativeGroupCreatives: function (campaignId, queryParameters) {
                var creativeGroupCreatives = $q.defer(),
                    promiseParams = {
                        id: campaignId,
                        criteria: 'groupCreatives',
                        creativeId: angular.isObject(queryParameters) ? queryParameters.creativeId : undefined,
                        groupId: angular.isObject(queryParameters) ? queryParameters.groupId : undefined,
                        pivotType: angular.isObject(queryParameters) ? queryParameters.pivotType : undefined,
                        placementId: angular.isObject(queryParameters) ? queryParameters.placementId : undefined,
                        sectionId: angular.isObject(queryParameters) ? queryParameters.sectionId : undefined,
                        siteId: angular.isObject(queryParameters) ? queryParameters.siteId : undefined,
                        type: angular.isObject(queryParameters) ? queryParameters.type : undefined
                    };

                campaignCreativeInsertionResource.get(promiseParams).$promise
                    .then(getCreativeGroupCreativesComplete)
                    .catch(errorHandler('Cannot get creative group creatives list', creativeGroupCreatives));

                function getCreativeGroupCreativesComplete(response) {
                    creativeGroupCreatives.resolve(
                        [].concat(lodash.result(response, 'records[0].CreativeGroupCreativeView', [])));
                }

                return creativeGroupCreatives.promise;
            },

            getPlacements: function (campaignId, queryParameters) {
                var placements = $q.defer(),
                    promiseParams = {
                        id: campaignId,
                        criteria: 'placements',
                        creativeId: angular.isObject(queryParameters) ? queryParameters.creativeId : undefined,
                        groupId: angular.isObject(queryParameters) ? queryParameters.groupId : undefined,
                        pivotType: angular.isObject(queryParameters) ? queryParameters.pivotType : undefined,
                        placementId: angular.isObject(queryParameters) ? queryParameters.placementId : undefined,
                        siteId: angular.isObject(queryParameters) ? queryParameters.siteId : undefined,
                        type: angular.isObject(queryParameters) ? queryParameters.type : undefined
                    };

                campaignCreativeInsertionResource.get(promiseParams).$promise
                    .then(getPlacementsComplete)
                    .catch(errorHandler('Cannot get placement list', placements));

                function getPlacementsComplete(response) {
                    placements.resolve([].concat(lodash.result(response, 'records[0].PlacementView', [])));
                }

                return placements.promise;
            },

            getCreativeInsertions: function (campaignId,
                                             pivotType,
                                             type,
                                             siteId,
                                             sectionId,
                                             placementId,
                                             groupId,
                                             creativeId) {
                var ciDynamic = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'creativeInsertions',
                    pivotType: pivotType,
                    type: type,
                    siteId: siteId,
                    sectionId: sectionId,
                    placementId: placementId,
                    groupId: groupId,
                    creativeId: creativeId
                }).$promise
                    .then(getCreativeInsertionsComplete)
                    .catch(errorHandler('Cannot get Creative Insertions', ciDynamic));

                function getCreativeInsertionsComplete(response) {
                    ciDynamic.resolve([].concat(
                        lodash.result(response, 'records[0].CreativeInsertionView', [])));
                }

                return ciDynamic.promise;
            },

            searchCreativeInsertions: function (campaignId,
                                                pattern,
                                                pivotType,
                                                searchOn,
                                                type,
                                                ids) {
                var ciSearch = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'searchCreativeInsertions',
                    pattern: pattern,
                    pivotType: pivotType,
                    soSite: angular.isObject(searchOn) ? searchOn.site : undefined,
                    soSection: angular.isObject(searchOn) ? searchOn.section : undefined,
                    soPlacement: angular.isObject(searchOn) ? searchOn.placement : undefined,
                    soGroup: angular.isObject(searchOn) ? searchOn.group : undefined,
                    soCreative: angular.isObject(searchOn) ? searchOn.creative : undefined,
                    type: type,
                    siteId: angular.isObject(ids) ? ids.site : undefined,
                    sectionId: angular.isObject(ids) ? ids.section : undefined,
                    placementId: angular.isObject(ids) ? ids.placement : undefined,
                    groupId: angular.isObject(ids) ? ids.group : undefined,
                    creativeId: angular.isObject(ids) ? ids.creative : undefined
                }).$promise
                    .then(searchCreativeInsertionsComplete)
                    .catch(errorHandler('Cannot get Creative Insertions search result', ciSearch));

                function searchCreativeInsertionsComplete(response) {
                    ciSearch.resolve([].concat(
                        lodash.result(response, 'records[0].CreativeInsertionView', [])));
                }

                return ciSearch.promise;
            },

            bulkDeleteCreativeInsertions: function (campaignId, nodeElements) {
                var deletePromise = $q.defer();

                campaignResource.update({
                        id: campaignId,
                        criteria: 'creativeInsertionsBulkDelete'
                    },
                    {
                        records: {
                            CreativeInsertionFilterParam: nodeElements
                        }
                    }).$promise
                    .then(bulkDeleteCreativeInsertionsComplete)
                    .catch(errorHandler('Error while performing delete creative insertion bulk request',
                        deletePromise));

                function bulkDeleteCreativeInsertionsComplete(response) {
                    deletePromise.resolve(response);
                }

                return deletePromise.promise;
            },

            bulkSaveSiteSectionSize: function (postData, flags) {
                var bulkSave = $q.defer();

                campaignPubSiteSectionSizeResource.save(flags, postData).$promise
                    .then(bulkSaveSiteSectionSizeComplete)
                    .catch(bulkSaveSiteSectionSizeFailed);

                function bulkSaveSiteSectionSizeComplete(response) {
                    bulkSave.resolve(response);
                }

                function bulkSaveSiteSectionSizeFailed(error) {
                    bulkSave.reject(error);
                }

                return bulkSave.promise;
            },

            getSiteContacts: function (campaignId) {
                var traffickingSiteContacts = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    criteria: 'siteContacts'
                }).$promise
                    .then(getSiteContactsComplete)
                    .catch(errorHandler('Cannot get site contacts', traffickingSiteContacts));

                function getSiteContactsComplete(response) {
                    traffickingSiteContacts.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].SiteContactView)));
                }

                return traffickingSiteContacts.promise;
            },

            bulkSave: function (campaignId, creativeInsertionList) {
                var deferred = $q.defer();

                campaignResource.save({
                        id: campaignId,
                        criteria: 'bulkCreativeInsertion'
                    },
                    {
                        creativeInsertions: creativeInsertionList
                    }).$promise
                    .then(bulkSaveComplete)
                    .catch(bulkSaveFailed);

                function bulkSaveComplete(response) {
                    deferred.resolve(response);
                }

                function bulkSaveFailed(error) {
                    deferred.reject(error);
                }

                return deferred.promise;
            },

            getPackageList: function (campaignId, ioId) {
                var packageList = $q.defer();

                campaignResource.get({
                    id: campaignId,
                    ioId: ioId,
                    criteria: 'packages'
                }).$promise
                    .then(getPackageListComplete)
                    .catch(errorHandler('Cannot get Package List', packageList));

                function getPackageListComplete(response) {
                    packageList.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Package)));
                }

                return packageList.promise;
            }
        };
    }
})();
