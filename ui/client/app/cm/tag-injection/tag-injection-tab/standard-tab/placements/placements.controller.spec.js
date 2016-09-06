'use strict';

describe('Controller: PlacementsController', function () {
    var $scope,
        CAMPAIGNS,
        SITES,
        CAMPAIGNS_AS_CHILDREN,
        SITES_AS_CHILDREN,
        AgencyService,
        PlacementsController,
        TagInjectionAssociation,
        TagInjectionTree,
        params,
        placementList,
        tagsAssociations,
        uuid,
        searchResult,
        TAG_ASSOCIATION_FROM_API;

    beforeEach(module('uiApp'));

    beforeEach(function () {
        params = {
            advertiser: {
                id: 1
            },
            brand: {
                id: 2
            }
        };
        CAMPAIGNS = [
            {
                campaignId: 6582990,
                campaignName: 'Frank Performance test'
            }, {
                campaignId: 7097610,
                campaignName: 'Frank camp 996'
            }
        ];

        SITES = [
            {
                campaignId: 6582990,
                siteId: 6064873,
                siteName: 'UI Test Site 1'
            }, {
                campaignId: 6582990,
                siteId: 6064874,
                siteName: 'UI Test Site 2'
            }
        ];

        CAMPAIGNS_AS_CHILDREN = [
            {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                name: 'Frank Performance test',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'aaaaaaaa-bbbb-cccc-dddd-eeeeffff8888',
                rowId: 'd96eece5faa0cf8881c68dd7c8b528eab1b91a8b'
            }, {
                advertiserId: 1,
                brandId: 2,
                campaignId: 7097610,
                name: 'Frank camp 996',
                field: {
                    rowLevel: 0,
                    key: 'campaign',
                    entityId: 'campaignId'
                },
                children: [
                    {}
                ],
                uuidRow: 'aaaaaaaa-bbbb-cccc-dddd-eeeeffff8888',
                rowId: 'c54750716d72f61cdbf7c620ec22dac9644b2d60'
            }
        ];

        SITES_AS_CHILDREN = [
            {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                siteId: 6064873,
                name: 'UI Test Site 1',
                field: {
                    rowLevel: 1,
                    key: 'site',
                    entityId: 'siteId'
                },
                children: [
                    {}
                ],
                uuidRow: 'aaaaaaaa-bbbb-cccc-dddd-eeeeffff8888',
                rowId: '7aa06bbb734a1b281e3911aa197cd68fd7696bad'
            }, {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                siteId: 6064874,
                name: 'UI Test Site 2',
                field: {
                    rowLevel: 1,
                    key: 'site',
                    entityId: 'siteId'
                },
                children: [
                    {}
                ],
                uuidRow: 'aaaaaaaa-bbbb-cccc-dddd-eeeeffff8888',
                rowId: 'fa08cf64a0f8f6aa90a507707f26f0f2c8ed1ef3'
            }
        ];

        TAG_ASSOCIATION_FROM_API = {
            directAssociations: [
                {
                    campaignId: 9225053,
                    entityId: 10568211,
                    entityType: 5,
                    htmlInjectionId: 70821,
                    htmlInjectionName: 'Face',
                    id: 70837,
                    isEnabled: 1,
                    isInherited: 0
                },
                {
                    campaignId: 9225053,
                    entityId: 10568211,
                    entityType: 5,
                    htmlInjectionId: 70827,
                    htmlInjectionName: 'Custom',
                    id: 70894,
                    isEnabled: 1,
                    isInherited: 0
                }
            ],
            inheritedAssociations: [
                {
                    campaignId: 9225053,
                    entityId: 10568211,
                    entityType: 5,
                    htmlInjectionId: 70826,
                    htmlInjectionName: 'Ad Tag',
                    id: 70841,
                    isEnabled: 1,
                    isInherited: 1
                }
            ]
        };
    });

    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                $state,
                                _AgencyService_,
                                _TagInjectionAssociation_,
                                _TagInjectionTree_,
                                _uuid_) {
        $scope = $rootScope.$new();
        $scope.vmTagInjectionTab = {
            promiseTab: {},
            advertiser: {
                agencyId: 1
            }
        };
        $scope.vmTIStandard = {
            clearAssociations: function () {
            },

            setAssociations: function () {
            }
        };

        AgencyService = _AgencyService_;
        TagInjectionAssociation = _TagInjectionAssociation_;
        TagInjectionTree = _TagInjectionTree_;
        placementList = $q.defer();
        tagsAssociations = $q.defer();
        uuid = _uuid_;
        searchResult = $q.defer();

        spyOn(AgencyService, 'getPlacements').andReturn(placementList.promise);
        spyOn(AgencyService, 'getHtmlInjectionTagAssociation').andReturn(tagsAssociations.promise);
        spyOn(AgencyService, 'searchPlacementView').andReturn(searchResult.promise);
        spyOn(TagInjectionTree, 'getModel').andCallThrough();
        spyOn(TagInjectionTree, 'getModelSearch').andCallThrough();
        spyOn($scope.vmTIStandard, 'setAssociations').andCallThrough();
        spyOn($scope.vmTIStandard, 'clearAssociations').andCallThrough();
        spyOn($state, 'go');
        spyOn(uuid, 'v4').andReturn('aaaaaaaa-bbbb-cccc-dddd-eeeeffff8888');

        $scope.vmTagInjectionTab = {
            advertiser: {
                agencyId: 123456,
                id: 654321
            },
            brand: {
                id: 123456
            },
            promiseTab: {
                $$state: {}
            }
        };

        PlacementsController = $controller('PlacementsController', {
            $scope: $scope,
            AgencyService: AgencyService,
            TagInjectionAssociation: TagInjectionAssociation,
            TagInjectionTree: TagInjectionTree
        });
    }));

    it('should assign initial model after activate()', function () {
        expect(PlacementsController.model).not.toBeDefined();
        $scope.$broadcast('tagInjection.reloadPlacementsTree', params);
        placementList.resolve(CAMPAIGNS);
        $scope.$apply();
        expect(PlacementsController.model).toBeDefined();
        expect(PlacementsController.model).toEqual(CAMPAIGNS_AS_CHILDREN);
    });

    it('should allow clear placements tree model', function () {
        expect(PlacementsController.model).not.toBeDefined();
        $scope.$broadcast('tagInjection.reloadPlacementsTree', params);
        placementList.resolve(CAMPAIGNS);
        $scope.$apply();

        expect(PlacementsController.model).toBeDefined();
        $scope.$broadcast('tagInjection.clearPlacementsTree');
        expect(PlacementsController.model).toEqual([]);
    });

    describe('onCollapseRow()', function () {
        it('should change expanded value to false', function () {
            var row = {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                field: TagInjectionAssociation.LEVEL.CAMPAIGN
            };

            PlacementsController.onExpandCollapse(false, null, null, row);
            expect(row.expanded).toBeFalsy();
        });
    });

    describe('onExpandRow()', function () {
        it('should load SITE level after expand a CAMPAIGN', function () {
            var row = {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                field: TagInjectionAssociation.LEVEL.CAMPAIGN
            };

            placementList.resolve(SITES);
            tagsAssociations.resolve(TAG_ASSOCIATION_FROM_API);
            expect(row.expanded).toBeUndefined();
            PlacementsController.onExpandCollapse(true, null, null, row);
            $scope.$apply();

            expect(row.expanded).toBeTruthy();
        });
    });

    describe('selectRow()', function () {
        it('should load tags association from row selected', function () {
            var row = {
                advertiserId: 1,
                brandId: 2,
                campaignId: 6582990,
                siteId: 2,
                sectionId: 3,
                field: TagInjectionAssociation.LEVEL.CAMPAIGN
            };

            tagsAssociations.resolve(TAG_ASSOCIATION_FROM_API);
            PlacementsController.onSelect(row);
            $scope.$apply();
            expect($scope.vmTIStandard.setAssociations).toHaveBeenCalledWith(row, {
                directAssociations: TAG_ASSOCIATION_FROM_API.directAssociations,
                inheritedAssociations: TAG_ASSOCIATION_FROM_API.inheritedAssociations
            });
        });
    });

    describe('searchTree()', function () {
        it('should load data from search', function () {
            var result = [
                {
                    campaignId: 6031386,
                    campaignName: 'Campaign Test 01',
                    campaignsTotal: 98
                },
                {
                    campaignId: 6031387,
                    campaignName: 'Campaign Test 02',
                    campaignsTotal: 98
                },
                {
                    campaignId: 6031388,
                    campaignName: 'Campaign Test 03',
                    campaignsTotal: 98
                }
            ];

            searchResult.resolve(result);
            PlacementsController.searchTree('Test');
            $scope.$apply();
            expect(PlacementsController.model).toEqual([
                {
                    advertiserId: 654321,
                    brandId: 123456,
                    campaignId: 6031386,
                    name: 'Campaign Test 01',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],
                    uuidRow: PlacementsController.model[0].uuidRow,
                    rowId: PlacementsController.model[0].rowId,
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 1,
                        total: 98
                    },
                    isFilterVisible: false
                },
                {
                    advertiserId: 654321,
                    brandId: 123456,
                    campaignId: 6031387,
                    name: 'Campaign Test 02',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],

                    uuidRow: PlacementsController.model[1].uuidRow,
                    rowId: PlacementsController.model[1].rowId,
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 2,
                        total: 98
                    },
                    isFilterVisible: false
                },
                {
                    advertiserId: 654321,
                    brandId: 123456,
                    campaignId: 6031388,
                    name: 'Campaign Test 03',
                    field: {
                        rowLevel: 0,
                        key: 'campaign',
                        entityId: 'campaignId'
                    },
                    children: [{}],
                    uuidRow: PlacementsController.model[2].uuidRow,
                    rowId: PlacementsController.model[2].rowId,
                    expanded: false,
                    dataLoaded: false,
                    searchContext: {
                        position: 3,
                        total: 98
                    },
                    isFilterVisible: false
                }
            ]);
        });
    });
});
