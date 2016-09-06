'use strict';

describe('Service: tagInjectionTree', function () {
    var TagInjectionAssociation,
        TagInjectionTree,
        uuid,
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
        CAMPAIGNS = [
            {
                campaignId: 6031386,
                campaignName: 'campaign Jim test'
            },
            {
                campaignId: 6031581,
                campaignName: 'Richards Campaign TEST -01'
            },
            {
                campaignId: 6040324,
                campaignName: 'Campaign-LA'
            },
            {
                campaignId: 6175020,
                campaignName: 'siamak campaign'
            }
        ],

        CAMPAIGNS_OUTPUT_MODEL = [
            {
                advertiserId: 6031299,
                brandId: 6031300,
                campaignId: 6031386,
                name: 'campaign Jim test',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                rowId: '549f018e7e3dfedcc482eea16b0a819fe4fc9e5d'
            },
            {
                advertiserId: 6031299,
                brandId: 6031300,
                campaignId: 6031581,
                name: 'Richards Campaign TEST -01',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                rowId: '2b6073a7e9bd589748a257586ae68f549a3dc997'
            },
            {
                advertiserId: 6031299,
                brandId: 6031300,
                campaignId: 6040324,
                name: 'Campaign-LA',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                rowId: '23b5db5701f7a3aa8be7b8a9c2b2374368c24f47'
            },
            {
                advertiserId: 6031299,
                brandId: 6031300,
                campaignId: 6175020,
                name: 'siamak campaign',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                rowId: 'def16bf980c8ee79e0fad3e11e9a4de7254a67bd'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_TagInjectionAssociation_, _TagInjectionTree_, _uuid_) {
        TagInjectionAssociation = _TagInjectionAssociation_;
        TagInjectionTree = _TagInjectionTree_;
        uuid = _uuid_;

        spyOn(uuid, 'v4').andReturn('d20a1ef6-e520-43eb-a539-853c56472997');
    }));

    describe('getModel()', function () {
        it('should get formatted model', function () {
            var params = {
                advertiserId: 6031299,
                brandId: 6031300
            };

            expect(TagInjectionTree.getModel(TagInjectionAssociation.LEVEL.CAMPAIGN, CAMPAIGNS, params))
                .toEqual(CAMPAIGNS_OUTPUT_MODEL);
        });
    });

    describe('getNextLevel()', function () {
        it('should get children level', function () {
            expect(TagInjectionTree.getNextLevel(TagInjectionAssociation.LEVEL.CAMPAIGN)).toEqual(LEVEL.SITE);
        });
    });

    describe('getModelSearch()', function () {
        it('should get search model', function () {
            var searchResult = [
                    {
                        campaignId: 7417860,
                        campaignName: 'Campaign Test 01',
                        campaignsTotal: 98,
                        id: 7421409
                    },
                    {
                        campaignId: 7417861,
                        campaignName: 'Campaign Test 02',
                        campaignsTotal: 98,
                        id: 7421410
                    },
                    {
                        campaignId: 7417862,
                        campaignName: 'Campaign Test 03',
                        campaignsTotal: 98,
                        id: 7421411
                    }
                ],
                resultModel = TagInjectionTree.getModelSearch(searchResult, 123456, 123456);

            expect(resultModel).toEqual([
                {
                    advertiserId: 123456,
                    brandId: 123456,
                    campaignId: 7417860,
                    name: 'Campaign Test 01',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],
                    uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                    rowId: '99cec129cce87a70468ab9825cdfb42bc85927a7',
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 1,
                        total: 98
                    }
                },
                {
                    advertiserId: 123456,
                    brandId: 123456,
                    campaignId: 7417861,
                    name: 'Campaign Test 02',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],
                    uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                    rowId: '0dd63c035ea441a1d0eb67f22ff9f293914d5c54',
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 2,
                        total: 98
                    }
                },
                {
                    advertiserId: 123456,
                    brandId: 123456,
                    campaignId: 7417862,
                    name: 'Campaign Test 03',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],
                    uuidRow: 'd20a1ef6-e520-43eb-a539-853c56472997',
                    rowId: '9c43a4b1183daf6a0aa459ef89a70fccb1430b35',
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 3,
                        total: 98
                    }
                }
            ]);
        });
    });
});
