'use strict';

describe('Controller: TagPlacementListController', function () {
    var $httpBackend,
        $q,
        $scope,
        ADVERTISER_LIST,
        BRAND_LIST,
        FIRST_ADVERTISER,
        FIRST_BRAND,
        PLACEMENT_LIST,
        AdvertiserService,
        BrandService,
        TagPlacementController,
        UserService,
        Utils,
        advertiserList,
        brandList,
        placementList,
        sortedAdvertiser,
        sortedBrand;

    beforeEach(module('uiApp'));

    beforeEach(function () {
        FIRST_ADVERTISER = {
            id: -1,
            name: 'Select Advertiser'
        };
        FIRST_BRAND = {
            id: -1,
            name: 'Select Brand'
        };
        ADVERTISER_LIST = [
            {
                address1: 'Broadway',
                agencyId: 6031295,
                city: 'NY',
                contactDefault: 'Thomas',
                country: 'USA',
                createdDate: '2015-07-15T11:01:27-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                enableHtmlTag: 1,
                id: 6031299,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:27-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'UI QA Advertiser TEST',
                notes: 'API QA note',
                state: 'NJ',
                url: 'http://www.test.com'
            },
            {
                address1: 'Cochabamba',
                agencyId: 6031295,
                city: 'Cochabamba',
                contactDefault: 'Mare',
                country: 'BOL',
                createdDate: '2015-07-15T17:08:25-07:00',
                createdTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                enableHtmlTag: 1,
                id: 6032345,
                isHidden: 'N',
                modifiedDate: '2015-07-15T17:08:25-07:00',
                modifiedTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                name: 'Advertiser MARE',
                notes: 'Some notes',
                state: 'NJ',
                url: 'http://www.test.com'
            }
        ];

        BRAND_LIST = [
            {
                advertiserId: 6031299,
                createdDate: '2015-07-15T11:01:27-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                description: 'Automated created UITEST QA Brand',
                id: 6031300,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:27-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'QA UITEST Brand TEST - 01'
            }, {
                advertiserId: 6031299,
                createdDate: '2015-07-15T11:01:28-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                description: 'Automated created UITEST QA Brand',
                id: 6031301,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:28-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'QA UITEST Brand TEST - 02'
            },
            {
                advertiserId: 6031299,
                createdDate: '2015-07-15T11:01:28-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                description: 'Automated created A-QA Brand',
                id: 6031302,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:28-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'A-QA Brand'
            }
        ];

        PLACEMENT_LIST = {
            records: [
                {
                    PlacementView: [
                        {
                            campaignId: 6485070,
                            campaignName: 'RCD-NISSAN',
                            id: 7300114,
                            isTrafficked: 0,
                            name: 'Test',
                            siteId: 7285210,
                            siteName: 'AOL2',
                            siteSectionId: 7299329,
                            siteSectionName: 'testing1',
                            sizeName: '2x2',
                            status: 'New'
                        },
                        {
                            campaignId: 6485070,
                            campaignName: 'RCD-NISSAN',
                            id: 7299821,
                            isTrafficked: 1,
                            name: 'Test',
                            siteId: 7285210,
                            siteName: 'AOL2',
                            siteSectionId: 7299329,
                            siteSectionName: 'testing1',
                            sizeName: '2x2',
                            status: 'New'
                        },
                        {
                            campaignId: 6485070,
                            campaignName: 'RCD-NISSAN',
                            id: 7299824,
                            isTrafficked: 0,
                            name: 'Test',
                            siteId: 7285211,
                            siteName: 'AOL2-RCD',
                            siteSectionId: 7299329,
                            siteSectionName: 'testing1',
                            sizeName: '2x2',
                            status: 'New'
                        }
                    ]
                }
            ]
        };
    });

    beforeEach(inject(function (_$httpBackend_,
                                $controller,
                                _$q_,
                                $rootScope,
                                $state,
                                _AdvertiserService_,
                                _BrandService_,
                                _CampaignsUtilService_,
                                _UserService_,
                                _Utils_) {
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        $scope = $rootScope.$new();
        AdvertiserService = _AdvertiserService_;
        BrandService = _BrandService_;
        UserService = _UserService_;
        Utils = _Utils_;

        advertiserList = $q.defer();
        brandList = $q.defer();
        placementList = $q.defer();

        spyOn(UserService, 'getAdvertisers').andReturn(advertiserList.promise);
        spyOn(AdvertiserService, 'getAdvertiserBrands').andReturn(brandList.promise);
        spyOn(BrandService, 'getPlacements').andReturn(placementList.promise);
        spyOn($state, 'go');

        TagPlacementController = $controller('TagPlacementListController', {
            $scope: $scope,
            AdvertiserService: AdvertiserService,
            BrandService: BrandService,
            Utils: Utils,
            UserService: UserService
        });
    }));

    describe('activate()', function () {
        it('should load list of Advertisers sorted by name', function () {
           expect(TagPlacementController.listAdvertisers).not.toBeDefined();
           advertiserList.resolve(ADVERTISER_LIST);
           $scope.$apply();
           sortedAdvertiser = TagPlacementController.listAdvertisers;
           expect(TagPlacementController.listAdvertisers).toEqual(
               [
                   {
                       id: -1,
                       name: 'Select Advertiser'
                   },
                   {
                       address1: 'Cochabamba',
                       agencyId: 6031295,
                       city: 'Cochabamba',
                       contactDefault: 'Mare',
                       country: 'BOL',
                       createdDate: '2015-07-15T17:08:25-07:00',
                       createdTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                       enableHtmlTag: 1,
                       id: 6032345,
                       isHidden: 'N',
                       modifiedDate: '2015-07-15T17:08:25-07:00',
                       modifiedTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                       name: 'Advertiser MARE',
                       notes: 'Some notes',
                       state: 'NJ',
                       url: 'http://www.test.com'
                   },
                   {
                       address1: 'Broadway',
                       agencyId: 6031295,
                       city: 'NY',
                       contactDefault: 'Thomas',
                       country: 'USA',
                       createdDate: '2015-07-15T11:01:27-07:00',
                       createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                       enableHtmlTag: 1,
                       id: 6031299,
                       isHidden: 'N',
                       modifiedDate: '2015-07-15T11:01:27-07:00',
                       modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                       name: 'UI QA Advertiser TEST',
                       notes: 'API QA note',
                       state: 'NJ',
                       url: 'http://www.test.com'
                   }
               ]
           );
       });

        it('should load list of Brand sorted by name', function () {
            expect(TagPlacementController.listBrands).not.toBeDefined();
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            $scope.$apply();
            sortedBrand = TagPlacementController.listBrands;
            expect(TagPlacementController.listBrands).toEqual(
                [
                    {
                        id: -1,
                        name: 'Select Brand'
                    },
                    {
                        advertiserId: 6031299,
                        createdDate: '2015-07-15T11:01:28-07:00',
                        createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        description: 'Automated created A-QA Brand',
                        id: 6031302,
                        isHidden: 'N',
                        modifiedDate: '2015-07-15T11:01:28-07:00',
                        modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        name: 'A-QA Brand'
                    },
                    {
                        advertiserId: 6031299,
                        createdDate: '2015-07-15T11:01:27-07:00',
                        createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        description: 'Automated created UITEST QA Brand',
                        id: 6031300,
                        isHidden: 'N',
                        modifiedDate: '2015-07-15T11:01:27-07:00',
                        modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        name: 'QA UITEST Brand TEST - 01'
                    },
                    {
                        advertiserId: 6031299,
                        createdDate: '2015-07-15T11:01:28-07:00',
                        createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        description: 'Automated created UITEST QA Brand',
                        id: 6031301,
                        isHidden: 'N',
                        modifiedDate: '2015-07-15T11:01:28-07:00',
                        modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                        name: 'QA UITEST Brand TEST - 02'
                    }
                ]
            );
        });

        it('should auto-select first Advertiser and first Brand', function () {
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            $scope.$apply();
            expect(TagPlacementController.listAdvertisers).toEqual(sortedAdvertiser);
            expect(TagPlacementController.filtersParents.advertiser).toEqual(sortedAdvertiser[1]);
            expect(TagPlacementController.listBrands).toEqual(sortedBrand);
            expect(TagPlacementController.filtersParents.brand).toEqual(sortedBrand[1]);
        });

        it('should load placements model', function () {
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            placementList.resolve(PLACEMENT_LIST);
            $scope.$apply();

            expect(TagPlacementController.placements.length).toBe(3);
            expect(TagPlacementController.placements).toEqual(
                [
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7300114,
                        isTrafficked: 0,
                        name: 'Test',
                        siteId: 7285210,
                        siteName: 'AOL2',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Pending'
                    },
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7299821,
                        isTrafficked: 1,
                        name: 'Test',
                        siteId: 7285210,
                        siteName: 'AOL2',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Trafficked'
                    },
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7299824,
                        isTrafficked: 0,
                        name: 'Test',
                        siteId: 7285211,
                        siteName: 'AOL2-RCD',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Pending'
                    }
            ]);
        });

        it('should load filters', function () {
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            placementList.resolve(PLACEMENT_LIST);
            $scope.$apply();

            expect(TagPlacementController.placements.length).toBe(3);
            expect(TagPlacementController.filterOption.CAMPAIGN.value.length).toBe
            (TagPlacementController.filterValues[0].values.length);
            expect(TagPlacementController.filterOption.SITE.value.length).toBe
            (TagPlacementController.filterValues[1].values.length);
            expect(TagPlacementController.filterOption.TRAFFICKED.value.length).toBe
            (TagPlacementController.filterValues[2].values.length);
            expect([TagPlacementController.filterOption]).toEqual(
                [
                    {
                        CAMPAIGN: {
                            text: 'Campaign',
                            value: ['RCD-NISSAN']
                        },
                        SITE: {
                            text: 'Site',
                            value: ['AOL2', 'AOL2-RCD']
                        },
                        TRAFFICKED: {
                            text: 'Trafficked',
                            value: ['Pending', 'Trafficked']
                        }
                    }
                ]
            );
            expect(TagPlacementController.filterValues).toEqual(
                [
                    {
                        fieldName: 'campaignName',
                        values: ['RCD-NISSAN']
                    },
                    {
                        fieldName: 'siteName',
                        values: ['AOL2', 'AOL2-RCD']
                    },
                    {
                        fieldName: 'statusTraffic',
                        values: ['Pending', 'Trafficked']
                    }
                ]
            );
        });
    });

    it('should allow select Advertiser', function () {
        expect(TagPlacementController.filtersParents.advertiser).toBeNull();
        TagPlacementController.selectAdvertiser(ADVERTISER_LIST[0]);
        expect(TagPlacementController.filtersParents.advertiser).toEqual(ADVERTISER_LIST[0]);
    });

    it('should load Brands after select Advertiser', function () {
        brandList.resolve(BRAND_LIST);
        TagPlacementController.selectAdvertiser(ADVERTISER_LIST[0]);
        $scope.$apply();
        expect(TagPlacementController.filtersParents.advertiser).toEqual(ADVERTISER_LIST[0]);
        expect(TagPlacementController.listBrands).toEqual(sortedBrand);
    });

    it('should allow select Brand', function () {
        expect(TagPlacementController.filtersParents.brand).toBeNull();
        TagPlacementController.selectBrand(BRAND_LIST[0]);
        expect(TagPlacementController.filtersParents.brand).toEqual(BRAND_LIST[0]);
    });

    it('should enable send button', function () {
        var placement = [
            {
                campaignId: 6485070,
                campaignName: 'RCD-NISSAN',
                id: 7299824,
                isTrafficked: 1,
                name: 'Test',
                siteId: 7285211,
                siteName: 'AOL2-RCD',
                siteSectionId: 7299329,
                siteSectionName: 'testing1',
                sizeName: '2x2',
                status: 'New',
                statusTraffic: 'Trafficked'
            }
        ];

        TagPlacementController.selectRows(placement);
        $scope.$apply();
        expect(TagPlacementController.enableActivate).toBe(true);
    });

    it('should disable send button', function () {
        var placement = [
            {
                campaignId: 6485070,
                campaignName: 'RCD-NISSAN',
                id: 7299824,
                isTrafficked: 1,
                name: 'Test',
                siteId: 7285211,
                siteName: 'AOL2-RCD',
                siteSectionId: 7299329,
                siteSectionName: 'testing1',
                sizeName: '2x2',
                status: 'Rejected',
                statusTraffic: 'Trafficked'
            }
        ];

        TagPlacementController.selectRows(placement);
        expect(TagPlacementController.enableActivate).toBe(false);
    });
});
