(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('TagInjectionAssociation', TagInjectionAssociation);

    TagInjectionAssociation.$inject = [
        '$log',
        '$q',
        'lodash',
        'Sha1'
    ];

    function TagInjectionAssociation($log,
                                     $q,
                                     lodash,
                                     Sha1) {
        var PLACEMENT_ACTION = {
                CREATE: 'c',
                DELETE: 'd'
            },
            LEVEL = {
                CAMPAIGN: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                SITE: {
                    rowLevel: 1,
                    key: 'site',
                    entityId: 'siteId'
                },
                SECTION: {
                    rowLevel: 2,
                    key: 'section',
                    entityId: 'sectionId'
                },
                PLACEMENT: {
                    rowLevel: 3,
                    key: 'placement',
                    entityId: 'placementId'
                }
            },
            ENTITY_TYPE = {
                CAMPAIGN: {
                    id: 11,
                    field: LEVEL.CAMPAIGN
                },
                SITE: {
                    id: 10,
                    field: LEVEL.SITE
                },
                SECTION: {
                    id: 9,
                    field: LEVEL.SECTION
                },
                PLACEMENT: {
                    id: 5,
                    field: LEVEL.PLACEMENT
                }
            },
            ASSOCIATION_TYPE = {
                DIRECT: {
                    id: 0,
                    key: 'directAssociations'
                },
                INHERITED: {
                    id: 0,
                    key: 'inheritedAssociations'
                }
            },
            ENTITY_TYPE_MAP = lodash.map(ENTITY_TYPE),
            LEVEL_MAP = lodash.map(LEVEL),
            service = {
            LEVEL: LEVEL,
            LEVEL_MAP: LEVEL_MAP,
            ASSOCIATION_TYPE: ASSOCIATION_TYPE,
            buildKey: buildKey,
            buildPlacementActionCreate: buildPlacementActionCreate,
            buildPlacementActionRemove: buildPlacementActionRemove,
            isAlreadyAssociated: isAlreadyAssociated,
            mapPlacementProperties: mapPlacementProperties
        };

        return service;

        function buildKey(placementRow) {
            var level = placementRow.field ? placementRow.field.rowLevel : -1;

            switch (level) {
                case LEVEL.CAMPAIGN.rowLevel:
                    return Sha1.hash('' + placementRow.campaignId);
                case LEVEL.SITE.rowLevel:
                    return Sha1.hash('' + placementRow.campaignId + placementRow.siteId);
                case LEVEL.SECTION.rowLevel:
                    return Sha1.hash('' + placementRow.campaignId + placementRow.siteId +
                        placementRow.sectionId);
                case LEVEL.PLACEMENT.rowLevel:
                    return Sha1.hash('' + placementRow.campaignId + placementRow.siteId +
                        placementRow.sectionId + placementRow.placementId);
            }
        }

        function buildPlacementActionCreate(placementRow, tag) {
            var level = placementRow.field.rowLevel,
                result;

            switch (level) {
                case LEVEL.CAMPAIGN.rowLevel:
                    result = getCampaignsAssoc(placementRow, tag);
                    break;
                case LEVEL.SITE.rowLevel:
                    result = getSitesAssoc(placementRow, tag);
                    break;
                case LEVEL.SECTION.rowLevel:
                    result = getSectionsAssoc(placementRow, tag);
                    break;
                case LEVEL.PLACEMENT.rowLevel:
                    result = getPlacementsAssoc(placementRow, tag);
                    break;
            }

            result.field = placementRow.field;
            result.action = PLACEMENT_ACTION.CREATE;
            result.entityType = getEntityTypeByField(placementRow.field).id;
            result.name = placementRow.name;

            return result;
        }

        function buildPlacementActionRemove(association) {
            return {
                field: association.field,
                levelType: association.field.key,
                action: PLACEMENT_ACTION.DELETE,
                campaignId: association.campaignId,
                siteId: association.siteId,
                sectionId: association.sectionId,
                placementId: association.placementId,
                htmlInjectionId: association.htmlInjectionId,
                htmlInjectionName: association.htmlInjectionName,
                placementName: association.placementName
            };
        }

        function getCampaignsAssoc(placementRow, tag) {
            return {
                levelType: LEVEL.CAMPAIGN.key,
                htmlInjectionId: tag.id,
                campaignId: placementRow.campaignId,
                htmlInjectionName: tag.name,
                params: {
                    placementRow: placementRow.field
                }
            };
        }

        function getEntityTypeByField(field) {
            return lodash.find(ENTITY_TYPE_MAP, function (item) {
                return item.field.rowLevel === field.rowLevel;
            });
        }

        function getSectionsAssoc(placementRow, tag) {
            return {
                levelType: LEVEL.SECTION.key,
                htmlInjectionId: tag.id,
                campaignId: placementRow.campaignId,
                siteId: placementRow.siteId,
                sectionId: placementRow.sectionId,
                htmlInjectionName: tag.name,
                params: {
                    placementRow: placementRow.field
                }
            };
        }

        function getPlacementsAssoc(placementRow, tag) {
            return {
                levelType: LEVEL.PLACEMENT.key,
                htmlInjectionId: tag.id,
                campaignId: placementRow.campaignId,
                siteId: placementRow.siteId,
                sectionId: placementRow.sectionId,
                placementId: placementRow.placementId,
                htmlInjectionName: tag.name,
                params: {
                    placementRow: placementRow.field
                }
            };
        }

        function getSitesAssoc(placementRow, tag) {
            return {
                levelType: LEVEL.SITE.key,
                htmlInjectionId: tag.id,
                campaignId: placementRow.campaignId,
                siteId: placementRow.siteId,
                htmlInjectionName: tag.name,
                params: {
                    placementRow: placementRow.field
                }
            };
        }

        function isAlreadyAssociated(associations, association) {
            var isAlreadyDirect = lodash.findWhere(associations.directAssociations, {
                    htmlInjectionId: association.htmlInjectionId
                }),
                isAlreadyInherited = lodash.findWhere(associations.directAssociations, {
                    htmlInjectionId: association.htmlInjectionId
                });

            return isAlreadyDirect || isAlreadyInherited;
        }

        function mapPlacementProperties(array, placementRow, associationType) {
            return lodash.map(lodash.cloneDeep(array), function (element) {
                element.campaignId = placementRow.campaignId;
                element.siteId = placementRow.siteId;
                element.sectionId = placementRow.sectionId;
                element.placementId = placementRow.placementId;
                element.field = placementRow.field;
                element.isInherited = associationType.id;
                element.entityType = getEntityTypeByField(element.field).id;
                element.placementName = placementRow.name;
                return element;
            });
        }
    }
})();

