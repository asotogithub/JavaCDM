'use strict';

describe('Service: TagInjectionAssociation', function () {
    var TagInjectionAssociation;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_TagInjectionAssociation_) {
        TagInjectionAssociation = _TagInjectionAssociation_;
    }));

    it('should build a placement action to create associations', function () {
        var placementRow = {
                advertiserId: 9024562,
                brandId: 10423794,
                campaignId: 10869638,
                name: 'Test Campaign',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {
                        isFilterVisible: false
                    }
                ],
                uuidRow: 'cdcc45e6-455a-4610-83c8-80257f4fbe15',
                rowId: '11afbf1a8eaa2ad0445eb47ccebb3079581dbdff',
                isFilterVisible: true
            },
            tag = {
                agencyId: 9024559,
                createdDate: '2016-07-25T10:59:26-07:00',
                createdTpwsKey: 'fb6fbbc7-49e2-490e-9ff3-45095706b63e',
                htmlContent: '<div style=\"position: relative; left: 0; top: 0;\">' +
                '<a title=\"Proudly Supporting Consumer Ad Choices\" target=\"_blank\" ' +
                'href=\"http://www.trueffect.com\"><img width=\"15\" height=\"15\" ' +
                'style=\"position: absolute; z-index: 100;border: none; top: 3px; padding: 0px;\" ' +
                'src=\"http://ad.adlegend.com/cdn/trueffect/adchoices/15x15.png\"></a></div>',
                id: 129904,
                isEnabled: 1,
                isVisible: 1,
                modifiedDate: '2016-07-25T10:59:26-07:00',
                modifiedTpwsKey: 'fb6fbbc7-49e2-490e-9ff3-45095706b63e',
                name: 'New Tag',
                secureHtmlContent: '<div style=\"position: relative; left: 0; top: 0;\">' +
                '<a title=\"Proudly Supporting Consumer Ad Choices\" target=\"_blank\" ' +
                'href=\"http://www.trueffect.com\"><img width=\"15\" height=\"15\" ' +
                'style=\"position: absolute; z-index: 100;border: none; top: 3px; padding: 0px;\" ' +
                'src=\"https://ad.adlegend.com/cdn/trueffect/adchoices/15x15.png\"></a></div>'
            },
            actionCreate = {
                levelType: 'campaign',
                htmlInjectionId: 129904,
                campaignId: 10869638,
                htmlInjectionName: 'New Tag',
                params: {
                    placementRow: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    }
                },
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                action: 'c',
                entityType: 11,
                name: 'Test Campaign'
            };

        expect(TagInjectionAssociation.buildPlacementActionCreate(placementRow, tag)).toEqual(actionCreate);
    });

    it('should build a placement action to remove associations', function () {
        var association = {
                campaignId: 10869638,
                entityId: 10869638,
                entityType: 11,
                htmlInjectionId: 129904,
                htmlInjectionName: 'New Tag',
                id: 135319,
                isEnabled: 1,
                isInherited: 0,
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                placementName: 'Test Campaign'
            },
            actionRemove = {
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                levelType: 'campaign',
                action: 'd',
                campaignId: 10869638,
                htmlInjectionId: 129904,
                htmlInjectionName: 'New Tag',
                placementName: 'Test Campaign'
            };

        expect(TagInjectionAssociation.buildPlacementActionRemove(association)).toEqual(actionRemove);
    });

    it('should verify if an association already exists', function () {
        var associations = {
                directAssociations: [
                    {
                        campaignId: 10869638,
                        entityId: 10869638,
                        entityType: 11,
                        htmlInjectionId: 77516,
                        htmlInjectionName: 'Tag Name 1',
                        id: 86483,
                        isEnabled: 1,
                        isInherited: 0
                    },
                    {
                        campaignId: 10869638,
                        entityId: 10869638,
                        entityType: 11,
                        htmlInjectionId: 129904,
                        htmlInjectionName: 'New Tag',
                        id: 135319,
                        isEnabled: 1,
                        isInherited: 0
                    }
                ],
                inheritedAssociations: [
                    {
                        campaignId: 10869638,
                        entityId: 10869638,
                        entityType: 11,
                        htmlInjectionId: 77521,
                        htmlInjectionName: 'Other tag',
                        id: 86483,
                        isEnabled: 1,
                        isInherited: 0
                    },
                    {
                        campaignId: 10869638,
                        entityId: 10869638,
                        entityType: 11,
                        htmlInjectionId: 129934,
                        htmlInjectionName: 'Other tag name',
                        id: 135319,
                        isEnabled: 1,
                        isInherited: 0
                    }
                ],
                pageSize: 1000,
                startIndex: 0,
                totalNumberOfRecords: 2
            },
            associationA = {
                levelType: 'campaign',
                htmlInjectionId: 129904,
                campaignId: 10869638,
                htmlInjectionName: 'New Tag',
                params: {
                    placementRow: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    }
                },
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                action: 'c',
                entityType: 11,
                name: 'Test Campaign'
            },
            associationB = {
                levelType: 'campaign',
                htmlInjectionId: 129905,
                campaignId: 10869638,
                htmlInjectionName: 'New Tag Example',
                params: {
                    placementRow: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    }
                },
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                action: 'c',
                entityType: 11,
                name: 'Test Campaign'
            };

        expect(TagInjectionAssociation.isAlreadyAssociated(associations, associationA)).toBeTruthy();
        expect(TagInjectionAssociation.isAlreadyAssociated(associations, associationB)).toBeFalsy();
    });
});
