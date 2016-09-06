(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CreativeGroupService', CreativeGroupService);

    CreativeGroupService.$inject = [
        '$q',
        '$resource',
        '$rootScope',
        'API_SERVICE',
        'CONSTANTS',
        'ErrorRequestHandler',
        'lodash'
    ];

    function CreativeGroupService(
        $q,
        $resource,
        $rootScope,
        API_SERVICE,
        CONSTANTS,
        ErrorRequestHandler,
        lodash) {
        var CREATIVE_GROUP = CONSTANTS.CREATIVE_GROUP,
            creativeGroupsResource = $resource(
            API_SERVICE + 'CreativeGroups/:id/:criteria',
            {
                id: '@id',
                criteria: '@criteria'
            },
            {
                get: {
                    method: 'GET'
                },
                update: {
                    method: 'PUT'
                }
            }),

            formatInputModelGeoTargets = function (geoTargets) {
                return lodash.extend(
                    {
                        geoCountry: {
                            antiTarget: 0,
                            values: [],
                            typeCode: 'geo_country'
                        },
                        geoState: {
                            antiTarget: 0,
                            values: [],
                            typeCode: 'geo_state'
                        },
                        geoDma: {
                            antiTarget: 0,
                            values: [],
                            typeCode: 'geo_dma'
                        },
                        geoZip: {
                            antiTarget: 0,
                            values: [],
                            typeCode: 'geo_zip'
                        }
                    },
                    lodash.mapKeys(
                        lodash.mapValues(
                            lodash.indexBy(geoTargets, 'typeCode'),

                            function (geoTarget) {
                                geoTarget.values = lodash.flatten(lodash.pluck(geoTarget.values, 'value'));

                                return geoTarget;
                            }),

                        function (value, key) {
                            return lodash.camelCase(key);
                        }));
            },

            formatInputModel = function (creativeGroup) {
                return angular.extend({}, creativeGroup, {
                    enablePriority: creativeGroup.priority === 0 ? 0 : 1,
                    originalName: creativeGroup.name,
                    geoTargets: formatInputModelGeoTargets(lodash.result(creativeGroup, 'geoTargets[0].geoTarget'))
                });
            },

            toNumber = function (input, minMax) {
                return Math.min(Math.max(+input || 0, minMax.MIN), minMax.MAX);
            },

            formatOutputModelGeoTargets = function (geoTargets) {
                var geoTarget = lodash
                    .chain(lodash.values(geoTargets))
                    .filter(function (_geoTarget) {
                        return _geoTarget.values && _geoTarget.values.length;
                    })
                    .map(function (_geoTarget) {
                        _geoTarget.values = [
                            {
                                value: lodash.map(_geoTarget.values, function (value) {
                                    return lodash.pick(value, 'id', 'label');
                                })
                            }
                        ];

                        return _geoTarget;
                    })
                    .value();

                return geoTarget.length ?
                    [
                        {
                            geoTarget: geoTarget
                        }
                    ] :
                    null;
            },

            formatOutputModel = function (creativeGroup) {
                var isDefault = creativeGroup.isDefault,
                    enablePriority = isDefault ? 0 : creativeGroup.enablePriority,
                    geoTargets = formatOutputModelGeoTargets(creativeGroup.geoTargets),
                    daypartTarget = creativeGroup.daypartTarget;

                return lodash.extend({}, lodash.omit(creativeGroup, 'enablePriority', 'originalName'), {
                    doCookieTargeting: isDefault ? 0 : creativeGroup.doCookieTargeting,
                    doDaypartTargeting: isDefault || !daypartTarget ? 0 : creativeGroup.doDaypartTargeting,
                    doGeoTargeting: isDefault || !geoTargets ? 0 : creativeGroup.doGeoTargeting,
                    enableFrequencyCap: isDefault ? 0 : creativeGroup.enableFrequencyCap,
                    frequencyCap: toNumber(creativeGroup.frequencyCap, CREATIVE_GROUP.FREQUENCY_CAP),
                    frequencyCapWindow: toNumber(creativeGroup.frequencyCapWindow, CREATIVE_GROUP.FREQUENCY_CAP_WINDOW),
                    geoTargets: geoTargets,
                    priority: enablePriority && toNumber(creativeGroup.priority, CREATIVE_GROUP.PRIORITY),
                    weight: toNumber(creativeGroup.weight, CREATIVE_GROUP.WEIGHT),

                    // IMPORTANT: rlt 20150622 - Needed to pass API validation...  :(
                    clickthroughCap: +creativeGroup.clickthroughCap || -1,
                    doOptimization: +creativeGroup.doOptimization || 0,
                    impressionCap: Math.max(+creativeGroup.impressionCap || 0, 0),
                    isReleased: +creativeGroup.isReleased || 0,
                    minOptimizationWeight: +creativeGroup.minOptimizationWeight || -1
                });
            },

            formatAssociations = function (campaignId, creativeList, creativeGroupList) {
                var creatives = [],
                    creativeGroupIds = [];

                angular.forEach(creativeGroupList, function (creativeGroup) {
                    creativeGroupIds.push(creativeGroup.id);
                });

                angular.forEach(creativeList, function (creative) {
                    creatives.push({
                        alias: creative.alias,
                        filename: creative.filename
                    });
                });

                return {
                    campaignId: campaignId,
                    creatives: creatives,
                    creativeGroupIds: creativeGroupIds
                };
            },

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            getCreativeGroup: function (creativeGroupId) {
                var groupDefer = $q.defer();

                creativeGroupsResource.get({
                    id: creativeGroupId
                }).$promise
                    .then(getCreativeGroupComplete)
                    .catch(errorHandler('Cannot get creative group', groupDefer));

                function getCreativeGroupComplete(creativeGroup) {
                    $rootScope.$emit('update.model.creativeGroup', creativeGroup.toJSON());
                    groupDefer.resolve(formatInputModel(creativeGroup.toJSON()));
                }

                return groupDefer.promise;
            },

            updateCreativeGroup: function (creativeGroup) {
                var _creativeGroup = formatOutputModel(creativeGroup),
                    groupDefer = $q.defer();

                creativeGroupsResource.update({
                    id: _creativeGroup.id
                }, _creativeGroup).$promise
                    .then(updateCreativeGroupComplete)
                    .catch(updateCreativeGroupFailed);

                function updateCreativeGroupComplete(__creativeGroup) {
                    $rootScope.$emit('update.model.creativeGroup', __creativeGroup);
                    groupDefer.resolve(formatInputModel(__creativeGroup.toJSON()));
                }

                function updateCreativeGroupFailed(error) {
                    groupDefer.reject(error);
                }

                return groupDefer.promise;
            },

            getCreativeList: function (creativeGroupId) {
                var creativeDefer = $q.defer();

                creativeGroupsResource.get({
                    id: creativeGroupId,
                    criteria: 'creatives'
                }).$promise
                    .then(getCreativeListComplete)
                    .catch(getCreativeListFailed);

                function getCreativeListComplete(creativeList) {
                    creativeDefer.resolve(lodash.compact([].concat(
                        creativeList &&
                        creativeList.records &&
                        creativeList.records[0] &&
                        creativeList.records[0].CreativeGroupCreative)));
                }

                function getCreativeListFailed(error) {
                    creativeDefer.reject(error);
                }

                return creativeDefer.promise;
            },

            updateCreativeGroupCreatives: function (creativeGroupId, creativeList) {
                var creativeDefer = $q.defer();

                creativeGroupsResource.update({
                    id: creativeGroupId,
                    criteria: 'creatives'
                }, creativeList).$promise
                    .then(updateCreativeGroupCreativesComplete)
                    .catch(updateCreativeGroupCreativesFailed);

                function updateCreativeGroupCreativesComplete(response) {
                    creativeDefer.resolve(response);
                }

                function updateCreativeGroupCreativesFailed(error) {
                    creativeDefer.reject(error);
                }

                return creativeDefer.promise;
            },

            removeCreativeGroup: function (creativeGroupId) {
                var creativeGroup = $q.defer();

                creativeGroupsResource.remove({
                    id: creativeGroupId
                }).$promise
                    .then(removeCreativeGroupComplete)
                    .catch(removeCreativeGroupFailed);

                function removeCreativeGroupComplete(response) {
                    creativeGroup.resolve(response);
                }

                function removeCreativeGroupFailed(error) {
                    creativeGroup.reject(error);
                }

                return creativeGroup.promise;
            },

            forceRemoveCreativeGroup: function (creativeGroupId) {
                var creativeGroup = $q.defer();

                creativeGroupsResource.remove({
                    id: creativeGroupId,
                    recursiveDelete: true
                }).$promise
                    .then(forceRemoveCreativeGroupComplete)
                    .catch(errorHandler('Cannot force remove creative group', creativeGroup));

                function forceRemoveCreativeGroupComplete(response) {
                    creativeGroup.resolve(response);
                }

                return creativeGroup.promise;
            },

            saveCreativeGroup: function (creativeGroup) {
                var groupDefer = $q.defer();

                creativeGroupsResource.save(formatOutputModel(creativeGroup))
                    .$promise
                    .then(saveCreativeGroupComplete)
                    .catch(saveCreativeGroupFailed);

                function saveCreativeGroupComplete(_creativeGroup) {
                    groupDefer.resolve(formatInputModel(_creativeGroup.toJSON()));
                }

                function saveCreativeGroupFailed(error) {
                    groupDefer.reject(error);
                }

                return groupDefer.promise;
            },

            createAssociations: function (campaignId, creatives, creativeGroups) {
                var associations = $q.defer();

                creativeGroupsResource.update({
                    criteria: 'createAssociations'
                }, formatAssociations(campaignId, creatives, creativeGroups))
                    .$promise
                    .then(createAssociationsComplete)
                    .catch(createAssociationsFailed);

                function createAssociationsComplete(response) {
                    associations.resolve(response);
                }

                function createAssociationsFailed(error) {
                    associations.reject(error);
                }

                return associations.promise;
            },

            searchByCampaignAndName: function (campaignId, name) {
                var search = $q.defer(),
                    query = CONSTANTS.CREATIVE_GROUP.GET_BY_NAME_AND_CAMPAIGN_ID
                    .replace('_name', name)
                    .replace('_campaignId', campaignId);

                creativeGroupsResource.get({
                    query: query
                }).$promise
                    .then(searchByCampaignAndNameComplete)
                    .catch(errorHandler('Cannot create associations', search));

                function searchByCampaignAndNameComplete(response) {
                    search.resolve(response);
                }

                return search.promise;
            }
        };
    }
})();
