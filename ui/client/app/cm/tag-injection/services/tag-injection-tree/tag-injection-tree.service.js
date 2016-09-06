(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('TagInjectionTree', TagInjectionTree);

    TagInjectionTree.$inject = [
        'lodash',
        'TagInjectionAssociation',
        'uuid'
    ];

    function TagInjectionTree(lodash, TagInjectionAssociation, uuid) {
        var INITIAL_POSITION = 1,
            LEVEL = TagInjectionAssociation.LEVEL,
            LEVEL_MAP = lodash.map(LEVEL),
            service = {
                getModel: getModel,
                getNextLevel: getNextLevel,
                getModelSearch: getModelSearch
            };

        return service;

        function getCampaignsLevel(data, parent) {
            return lodash.map(data, function (element) {
                var entity = {
                    advertiserId: parent.advertiserId,
                    brandId: parent.brandId,
                    campaignId: element.campaignId,
                    name: element.campaignName,
                    field: LEVEL.CAMPAIGN,
                    children: [
                        {}
                    ],
                    uuidRow: uuid.v4()
                };

                entity.rowId = TagInjectionAssociation.buildKey(entity);

                return entity;
            });
        }

        function getSitesLevel(data, parent) {
            return lodash.map(data, function (element) {
                var entity = {
                    advertiserId: parent.advertiserId,
                    brandId: parent.brandId,
                    campaignId: parent.campaignId,
                    siteId: element.siteId,
                    name: element.siteName,
                    field: LEVEL.SITE,
                    children: [
                        {}
                    ],
                    uuidRow: uuid.v4()
                };

                entity.rowId = TagInjectionAssociation.buildKey(entity);

                return entity;
            });
        }

        function getSectionsLevel(data, parent) {
            return lodash.map(data, function (element) {
                var entity = {
                    advertiserId: parent.advertiserId,
                    brandId: parent.brandId,
                    campaignId: parent.campaignId,
                    siteId: parent.siteId,
                    sectionId: element.siteSectionId,
                    name: element.siteSectionName,
                    field: LEVEL.SECTION,
                    children: [
                        {}
                    ],
                    uuidRow: uuid.v4()
                };

                entity.rowId = TagInjectionAssociation.buildKey(entity);

                return entity;
            });
        }

        function getPlacementsLevel(data, parent) {
            return lodash.map(data, function (element) {
                var entity = {
                    advertiserId: parent.advertiserId,
                    brandId: parent.brandId,
                    campaignId: parent.campaignId,
                    siteId: parent.siteId,
                    sectionId: element.siteSectionId,
                    placementId: element.id,
                    name: element.placementAlias,
                    field: LEVEL.PLACEMENT,
                    children: [
                        {}
                    ],
                    lastLevel: true,
                    uuidRow: uuid.v4()
                };

                entity.rowId = TagInjectionAssociation.buildKey(entity);

                return entity;
            });
        }

        function getNextLevel(current) {
            return lodash.find(LEVEL_MAP, function (item) {
                return item.rowLevel === current.rowLevel + 1;
            });
        }

        function getModel(level, data, parent) {
            switch (level) {
                case LEVEL.CAMPAIGN:
                    return getCampaignsLevel(data, parent);
                case LEVEL.SITE:
                    return getSitesLevel(data, parent);
                case LEVEL.SECTION:
                    return getSectionsLevel(data, parent);
                case LEVEL.PLACEMENT:
                    return getPlacementsLevel(data, parent);
            }
        }

        function getModelSearch(model, advertiserId, brandId) {
            var modelCampaign = lodash.groupBy(model, 'campaignId');

            return lodash.map(modelCampaign, toCampaignRows(advertiserId, brandId, INITIAL_POSITION));
        }

        function toCampaignRows(advertiserId, brandId, position) {
            return function (row) {
                var sites = lodash.groupBy(row, 'siteId'),
                    collapse = sites.hasOwnProperty('undefined') && Object.keys(sites).length === 1,
                    newCampaign = getModel(LEVEL.CAMPAIGN, [row[0]], {
                        advertiserId: advertiserId,
                        brandId: brandId
                    });

                newCampaign[0].children = collapse ? [{}] :
                    lodash.map(sites, toSiteRows(newCampaign[0], INITIAL_POSITION));
                newCampaign[0].expanded = !collapse;
                newCampaign[0].dataLoaded = !collapse;
                newCampaign[0].searchContext = {
                    position: position++,
                    total: row[0].campaignsTotal
                };

                return newCampaign[0];
            };
        }

        function toSiteRows(parent, position) {
            return function (row) {
                var section = lodash.groupBy(row, 'siteSectionId'),
                    collapse = section.hasOwnProperty('undefined') && Object.keys(section).length === 1,
                    newSite = getModel(LEVEL.SITE, [row[0]], parent);

                newSite[0].children = collapse ? [{}] :
                    lodash.map(section, toSectionRows(newSite[0], INITIAL_POSITION));
                newSite[0].expanded = !collapse;
                newSite[0].dataLoaded = !collapse;
                newSite[0].searchContext = {
                    position: position++,
                    total: row[0].sitesTotal
                };

                return newSite[0];
            };
        }

        function toSectionRows(parent, position) {
            return function (row) {
                var placement = lodash.groupBy(row, 'id'),
                    collapse = placement.hasOwnProperty('undefined') && Object.keys(placement).length === 1,
                    newSection = getModel(LEVEL.SECTION, [row[0]], parent);

                newSection[0].children = collapse ? [{}] :
                    lodash.map(placement, toPlacementRows(newSection[0], INITIAL_POSITION));
                newSection[0].expanded = !collapse;
                newSection[0].dataLoaded = !collapse;
                newSection[0].searchContext = {
                    position: position++,
                    total: row[0].sectionsTotal
                };

                return newSection[0];
            };
        }

        function toPlacementRows(parent, position) {
            return function (row) {
                var newSection = getModel(LEVEL.PLACEMENT, [row[0]], parent);

                newSection[0].searchContext = {
                    position: position++,
                    total: row[0].placementsTotal
                };

                return newSection[0];
            };
        }
    }
})();

