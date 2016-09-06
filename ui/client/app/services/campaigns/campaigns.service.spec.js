'use strict';

describe('Service: CampaignsService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        CampaignsService,
        ErrorRequestHandler,
        UserService,
        creativeListFromAPI = {
            records: [
                {
                    Creative: [
                        {
                            agencyId: '5959467',
                            alias: '100x50-3',
                            campaignId: '5959474',
                            clickthrough: 'http://www.clickthrough3.com',
                            createdDate: '2015-05-21T13:15:40-04:00',
                            creativeType: 'jpg',
                            fileSize: '0',
                            filename: '180x150jpg.jpg',
                            groupsCount: '0',
                            height: '150',
                            id: '5959481',
                            isExpandable: '0',
                            logicalDelete: 'N',
                            modifiedDate: '2015-05-21T13:15:44-04:00',
                            ownerCampaignId: '5959474',
                            purpose: '...',
                            released: '0',
                            scheduled: '0',
                            swfClickCount: '1',
                            width: '180'
                        },
                        {
                            agencyId: '5959467',
                            alias: '100x100-4-3',
                            campaignId: '5959474',
                            clickthrough: 'http://www.clickthrough3.com',
                            createdDate: '2015-05-21T13:15:40-04:00',
                            creativeType: 'jpg',
                            fileSize: '0',
                            filename: '100x100.jpg',
                            groupsCount: '0',
                            height: '100',
                            id: '5959481',
                            isExpandable: '0',
                            logicalDelete: 'N',
                            modifiedDate: '2015-05-21T13:15:44-04:00',
                            ownerCampaignId: '5959474',
                            purpose: '...',
                            released: '0',
                            scheduled: '0',
                            swfClickCount: '1',
                            width: '100'
                        }
                    ]
                }
            ]
        },
        creativeGroupListFromAPI = {
            records: [
                {
                    CreativeGroupDtoForCampaigns: [
                        {
                            campaignId: 1090896,
                            doCookieTargeting: 1,
                            doDaypartTargeting: 0,
                            doGeoTargeting: 0,
                            enableGroupWeight: 0,
                            id: 1195714,
                            name: 'All DMA Prospects',
                            numberOfCreativesInGroup: 6,
                            priority: 0
                        },
                        {
                            campaignId: 1090896,
                            doCookieTargeting: 1,
                            doDaypartTargeting: 0,
                            doGeoTargeting: 0,
                            enableGroupWeight: 0,
                            id: 1101180,
                            name: 'Geo/Cookie Variable',
                            numberOfCreativesInGroup: 6,
                            priority: 0
                        },
                        {
                            campaignId: 1090896,
                            doCookieTargeting: 0,
                            doDaypartTargeting: 0,
                            doGeoTargeting: 0,
                            enableGroupWeight: 0,
                            id: 8340103,
                            name: 'NFL',
                            numberOfCreativesInGroup: 12,
                            priority: 0
                        }
                    ]
                }
            ]
        },
        packageListFromAPI = {
            startIndex: 0,
            pageSize: 1000,
            totalNumberOfRecords: 1,
            records: [
                {
                    Package: [
                        {
                            campaignId: 7402305,
                            changed: false,
                            costDetails: [
                                {
                                    createdDate: '2016-08-17T11:57:15-07:00',
                                    createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                                    foreignId: 7513366,
                                    id: 7513368,
                                    inventory: 1,
                                    logicalDelete: 'N',
                                    margin: 0.0,
                                    modifiedDate: '2016-08-17T11:57:15-07:00',
                                    modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                                    plannedGrossAdSpend: 0.0,
                                    plannedGrossRate: 0.0,
                                    plannedNetAdSpend: 0.0,
                                    plannedNetRate: 0.0,
                                    rateType: 4,
                                    startDate: '2016-09-17T00:00:00-07:00'
                                }, {
                                    createdDate: '2016-08-17T11:57:15-07:00',
                                    createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                                    endDate: '2016-09-16T23:59:59-07:00',
                                    foreignId: 7513366,
                                    id: 7513367,
                                    inventory: 2000,
                                    logicalDelete: 'N',
                                    margin: 100.0,
                                    modifiedDate: '2016-08-17T11:57:15-07:00',
                                    modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                                    plannedGrossAdSpend: 20.0,
                                    plannedGrossRate: 10.0,
                                    plannedNetAdSpend: 0.0,
                                    plannedNetRate: 0.0,
                                    rateType: 4,
                                    startDate: '2016-08-17T00:00:00-07:00'
                                }
                            ],
                            countryCurrencyId: 1,
                            createdDate: '2016-08-17T11:57:15-07:00',
                            createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                            description: 'PKG01',
                            id: 7513366,
                            logicalDelete: 'N',
                            modifiedDate: '2016-08-17T11:57:15-07:00',
                            modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                            name: 'PKG01',
                            placementCount: 1,
                            placements: [
                                {
                                    adSpend: 10.0,
                                    endDate: '2016-09-16T23:59:59-07:00',
                                    height: 1000,
                                    id: 7513369,
                                    inventory: 1000,
                                    ioId: 7402306,
                                    name: '000 - 888888 - 1000x1000',
                                    rate: 10.0,
                                    rateType: 'CPM',
                                    siteId: 7109383,
                                    siteName: '000',
                                    siteSectionId: 7109384,
                                    sizeId: 5014113,
                                    startDate: '2016-08-17T00:00:00-07:00',
                                    width: 1000
                                }
                            ]
                        }
                    ]
                }
            ]
        };

    beforeEach(module('uiApp', function ($provide) {
        UserService = jasmine.createSpyObj('UserService', ['getUser', 'hasPermission', 'clear']);

        $provide.value('UserService', UserService);
    }));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _CampaignsService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        CampaignsService = _CampaignsService_;
        installPromiseMatchers();
    }));

    describe('getList()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        describe('on success', function () {
            it('should process isActiveDisplay and resolve promise with response.records[0].CampaignDTO', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/campaigns').respond({
                    records: [
                        {
                            CampaignDTO: [
                                {
                                    name: 'a',
                                    isActive: 'Y'
                                },
                                {
                                    name: 'b',
                                    isActive: 'N'
                                },
                                {
                                    name: 'c',
                                    isActive: 'malformedData'
                                }
                            ]
                        }
                    ]
                });
                promise = CampaignsService.getList();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        name: 'a',
                        isActive: 'Y',
                        isActiveDisplay: 'Active'
                    },
                    {
                        name: 'b',
                        isActive: 'N',
                        isActiveDisplay: 'Inactive'
                    },
                    {
                        name: 'c',
                        isActive: 'malformedData',
                        isActiveDisplay: 'Inactive'
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/campaigns').respond({
                    records: [
                        {
                            CampaignDTO: {
                                name: 'a',
                                isActive: 'Y'
                            }
                        }
                    ]
                });
                promise = CampaignsService.getList();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        name: 'a',
                        isActive: 'Y',
                        isActiveDisplay: 'Active'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/campaigns').respond(404);
                CampaignsService.getList();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/campaigns').respond(404, 'FAILED');
                promise = CampaignsService.getList();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getCampaign()', function () {
        describe('on success', function () {
            it('should resolve promise with campaign', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337').respond({
                    id: '1337',
                    name: 'Some name'
                });
                promise = CampaignsService.getCampaign(1337);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    id: '1337',
                    name: 'Some name'
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/69').respond(404);
                CampaignsService.getCampaign(69);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/42').respond(404, 'FAILED');
                promise = CampaignsService.getCampaign(42);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getCampaignDetails()', function () {
        describe('on success', function () {
            it('should resolve promise with campaign details', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/detail').respond({
                    campaign: {
                        id: '1337',
                        name: 'Some name'
                    }
                });
                promise = CampaignsService.getCampaignDetails(1337);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    campaign: {
                        id: '1337',
                        name: 'Some name'
                    }
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/69/detail').respond(404);
                CampaignsService.getCampaignDetails(69);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/42/detail').respond(404, 'FAILED');
                promise = CampaignsService.getCampaignDetails(42);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateCampaignDetails()', function () {
        it('should update campaign details', function () {
            var promise;

            $httpBackend.whenPUT(API_SERVICE + 'Campaigns/1337').respond(200);
            promise = CampaignsService.updateCampaignDetails({
                id: '1337'
            });
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('getCreatives()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].Creative', function () {
                var promise;

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/5959474/creatives')
                    .respond(creativeListFromAPI);
                promise = CampaignsService.getCreatives('5959474');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(creativeListFromAPI.records[0].Creative);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/creatives')
                    .respond({
                        records: [
                            {
                                Creative: [
                                    creativeListFromAPI.records[0].Creative[0]
                                ]

                            }
                        ]
                    });
                promise = CampaignsService.getCreatives('5959474');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([creativeListFromAPI.records[0].Creative[0]]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/creatives')
                    .respond(404);
                CampaignsService.getCreatives('5959474');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/creatives')
                    .respond(404, 'FAILED');
                promise = CampaignsService.getCreatives('5959474');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getCreativeGroups()', function () {
        beforeEach(function () {
            installPromiseMatchers();
        });

        describe('on success', function () {
            it('should resolve promise with response.records[0].CreativeGroupDtoForCampaigns', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/creativeGroups').respond(creativeGroupListFromAPI);
                promise = CampaignsService.getCreativeGroups(1337);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(creativeGroupListFromAPI.records[0].CreativeGroupDtoForCampaigns);
            });

            it('should return null for priority if the creativeGroup.name is Default', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/creativeGroups').respond({
                    records: [
                        {
                            CreativeGroupDtoForCampaigns: [
                                {
                                    campaignId: '1090896',
                                    doCookieTargeting: '1',
                                    doDaypartTargeting: '1',
                                    doGeoTargeting: '1',
                                    enableGroupWeight: '1',
                                    id: '1195714',
                                    name: 'Default',
                                    numberOfCreativesInGroup: '6',
                                    priority: '4'
                                },
                                {
                                    campaignId: '1090896',
                                    doCookieTargeting: '1',
                                    doDaypartTargeting: '1',
                                    doGeoTargeting: '1',
                                    enableGroupWeight: '1',
                                    id: '1101180',
                                    name: 'Geo/Cookie Variable',
                                    numberOfCreativesInGroup: '6',
                                    priority: '5'
                                }
                            ]
                        }
                    ]
                });
                promise = CampaignsService.getCreativeGroups(1337);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        campaignId: 1090896,
                        doCookieTargeting: 1,
                        doDaypartTargeting: 1,
                        doGeoTargeting: 1,
                        enableGroupWeight: 1,
                        id: 1195714,
                        name: 'Default',
                        numberOfCreativesInGroup: 6,
                        priority: null
                    },
                    {
                        campaignId: 1090896,
                        doCookieTargeting: 1,
                        doDaypartTargeting: 1,
                        doGeoTargeting: 1,
                        enableGroupWeight: 1,
                        id: 1101180,
                        name: 'Geo/Cookie Variable',
                        numberOfCreativesInGroup: 6,
                        priority: 5
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/69/creativeGroups').respond({
                    records: [
                        {
                            CreativeGroupDtoForCampaigns: {
                                name: 'a'
                            }
                        }
                    ]
                });
                promise = CampaignsService.getCreativeGroups(69);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        name: 'a'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/creativeGroups').respond(404);
                CampaignsService.getCreativeGroups(1337);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/69/creativeGroups').respond(404, 'FAILED');
                promise = CampaignsService.getCreativeGroups(69);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('saveCampaignDetails()', function () {
        it('should create new campaign with its details', function () {
            var promise,
                campaignDetails = {
                advertiserId: 9074362,
                agencyId: 9024559,
                brandId: 9074363,
                cookieDomainId: 9034120,
                createdDate: '2015-06-17T13:50:01-04:00',
                createdTpwsKey: 'a91d01ba-88ad-4466-9632-dabc02960c42',
                description: 'Created on 6/17/2015 at 10:00 PM',
                dupName: 'campaign paola inactive',
                endDate: '2015-09-16T00:00:00-04:00',
                isActive: 'Y',
                isHidden: 'N',
                logicalDelete: 'N',
                modifiedDate: '2015-06-16T13:50:19-04:00',
                modifiedTpwsKey: 'a91d01ba-88ad-4466-9632-dabc02960c42',
                name: 'Campaign - TEST POST',
                startDate: '2015-06-16T00:00:00-04:00',
                statusId: 1
            };

            $httpBackend.whenPOST(API_SERVICE + 'Campaigns').respond(200);
            promise = CampaignsService.saveCampaignDetails(campaignDetails);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(campaignDetails);
        });
    });

    describe('uploadCreative()', function () {
        it('upload a file for a new creative', function () {
            var promise,
                campaignId = 9075184,
                file = {
                    name: 'ngbbs46ec9d4f361c4.jpg'
                } ,
                response = {
                    agencyId: 9024559,
                    alias: 'ngbbs46ec9d4f361c4',
                    campaignId: 9075184,
                    clickthroughs: {},
                    createdDate: '2015-07-02T15:55:00-04:00',
                    createdTpwsKey: 'bc5b2221-1a5c-4ea9-be1f-e88139c683da',
                    creativeType: 'jpg',
                    fileSize: 0,
                    filename: file.name,
                    height: 600,
                    id: 9101538,
                    isExpandable: 0,
                    logicalDelete: 'N',
                    modifiedDate: '2015-07-02T15:55:00-04:00',
                    modifiedTpwsKey: 'bc5b2221-1a5c-4ea9-be1f-e88139c683da',
                    ownerCampaignId: 9075184,
                    released: 0,
                    scheduled: 0,
                    width: 300
                };

            $httpBackend.expectPOST(API_SERVICE + 'Campaigns/' + campaignId + '/creativeUpload?filename=' +
                file.name).respond(response);
            promise = CampaignsService.uploadCreative(campaignId, file);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(response.data);
        });
    });

    describe('reduceMetricsData()', function () {
        it('should roll up key metrics', function () {
            var data = [
                    {
                        campaignId: 1,
                        clicks: 5,
                        conversions: 20,
                        cost: 100.0,
                        day: '2015-07-22T00:00:00-06:00',
                        impressions: 50
                    },
                    {
                        campaignId: 1,
                        clicks: 10,
                        conversions: 30,
                        cost: 100.0,
                        day: '2015-07-23T00:00:00-06:00',
                        impressions: 100
                    }
                ],
                result = CampaignsService.reduceMetricsData(data);

            expect(15).toEqual(result.clicks);
            expect(150).toEqual(result.impressions);
            expect(50).toEqual(result.conversions);
            expect(200).toEqual(result.cost);
            expect(0.25).toEqual(result.eCpa());
            expect(0.10).toEqual(result.ctr());
        });
    });

    describe('getPackagePlacements()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].PackagePlacementView', function () {
                var promise,
                    packagePlacementResponse = [
                    {
                        adSpend: 0.0,
                        campaignId: 9024566,
                        countryCurrencyId: 1,
                        createdDate: '2015-08-01T09:01:22-06:00',
                        createdTpwsKey: 'be271b2f-b195-4969-b700-d2ac09cc27e3',
                        endDate: '2015-08-31T00:00:00-06:00',
                        height: 1,
                        id: 9150479,
                        inventory: 1,
                        ioId: 9150289,
                        isSecure: 0,
                        isTrafficked: 0,
                        logicalDelete: 'N',
                        maxFileSize: 1,
                        modifiedDate: '2015-08-01T09:01:22-06:00',
                        modifiedTpwsKey: 'be271b2f-b195-4969-b700-d2ac09cc27e3',
                        name: 'ESPN - Homepage - 1x1',
                        rate: 0.0,
                        rateType: 'CPM',
                        resendTags: 0,
                        siteId: 9107198,
                        siteName: 'ESPN',
                        siteSectionId: 9107199,
                        sizeId: 22154,
                        sizeName: '1x1',
                        smEventId: -1,
                        startDate: '2015-08-01T00:00:00-06:00',
                        status: 'New',
                        utcOffset: 0,
                        width: 1
                    },
                    {
                        adSpend: 33.0,
                        campaignId: 9024566,
                        countryCurrencyId: 1,
                        createdDate: '2015-07-09T09:55:10-06:00',
                        createdTpwsKey: '960b2af4-a838-4679-9e9a-1fec753c73d3',
                        endDate: '2015-08-08T00:00:00-06:00',
                        height: 100,
                        id: 9108817,
                        inventory: 1,
                        ioId: 9108809,
                        isSecure: 0,
                        isTrafficked: 0,
                        logicalDelete: 'N',
                        maxFileSize: 1,
                        modifiedDate: '2015-07-30T15:12:30-06:00',
                        modifiedTpwsKey: '3d42c624-ba69-4a66-9bd1-618ca27ccc19',
                        name: 'IOS SITE - SITE IOS - 100x100',
                        rate: 0.0,
                        rateType: 'CPM',
                        resendTags: 0,
                        siteId: 9107159,
                        siteName: 'IOS SITE',
                        siteSectionId: 9107160,
                        sizeId: 22164,
                        sizeName: '100x100',
                        smEventId: -1,
                        startDate: '2015-07-09T00:00:00-06:00',
                        status: 'Accepted',
                        utcOffset: 0,
                        width: 100
                    }
                ];

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/packagePlacementView')
                    .respond({
                        startIndex: 0,
                        pageSize: 1000,
                        totalNumberOfRecords: 2,
                        records: [
                            {
                                PackagePlacementView: packagePlacementResponse
                            }
                        ]
                    });

                promise = CampaignsService.getPackagePlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(packagePlacementResponse);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    packagePlacementResponse = {
                        adSpend: 0.0,
                        campaignId: 9024566,
                        countryCurrencyId: 1,
                        createdDate: '2015-08-01T09:01:22-06:00',
                        createdTpwsKey: 'be271b2f-b195-4969-b700-d2ac09cc27e3',
                        endDate: '2015-08-31T00:00:00-06:00',
                        height: 1,
                        id: 9150479,
                        inventory: 1,
                        ioId: 9150289,
                        isSecure: 0,
                        isTrafficked: 0,
                        logicalDelete: 'N',
                        maxFileSize: 1,
                        modifiedDate: '2015-08-01T09:01:22-06:00',
                        modifiedTpwsKey: 'be271b2f-b195-4969-b700-d2ac09cc27e3',
                        name: 'ESPN - Homepage - 1x1',
                        rate: 0.0,
                        rateType: 'CPM',
                        resendTags: 0,
                        siteId: 9107198,
                        siteName: 'ESPN',
                        siteSectionId: 9107199,
                        sizeId: 22154,
                        sizeName: '1x1',
                        smEventId: -1,
                        startDate: '2015-08-01T00:00:00-06:00',
                        status: 'New',
                        utcOffset: 0,
                        width: 1
                    };

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/packagePlacementView')
                    .respond({
                        records: [
                            {
                                PackagePlacementView: packagePlacementResponse
                            }
                        ]
                    });

                promise = CampaignsService.getPackagePlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    packagePlacementResponse
                ]);
            });

            it('should resolve promise with empty array when no records in response', function () {
                var promise;

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/packagePlacementView')
                    .respond({});

                promise = CampaignsService.getPackagePlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/9024566/packagePlacementView')
                    .respond(404);
                CampaignsService.getPackagePlacements(9024566);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/9024566/packagePlacementView')
                    .respond(404, 'FAILED');
                promise = CampaignsService.getPackagePlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getCreativeGroupCreatives()', function () {
        it('should resolve promise with response.records[0].CreativeGroupCreativeView', function () {
            var promise,
                expected = [
                    {
                        campaignId: 9077518,
                        creativeAlias: 'Leaderboard-MultiClick-AS3',
                        creativeClickthroughs: [
                            {
                                sequence: 2,
                                url: 'http://www.second.com'
                            },
                            {
                                sequence: 3,
                                url: 'http://www.third.com'
                            }
                        ],
                        creativeDefaultClickthrough: 'http://www.first.com',
                        creativeFileName: 'Leaderboard-MultiClick-AS3.zip',
                        creativeGroupId: 9077529,
                        creativeGroupName: 'cookie targeting 2',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeHeight: 90,
                        creativeId: 9186180,
                        creativeWidth: 728
                    },
                    {
                        campaignId: 9077518,
                        creativeAlias: 'FolksFest-300x250',
                        creativeDefaultClickthrough: 'http://www.yahoo.com',
                        creativeFileName: 'FolksFest-300x250.gif',
                        creativeGroupId: 9077530,
                        creativeGroupName: 'Day Part Targeting',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeHeight: 250,
                        creativeId: 9077543,
                        creativeWidth: 300
                    },
                    {
                        campaignId: 9077518,
                        creativeAlias: 'F4UCorsair',
                        creativeDefaultClickthrough: 'http://www.weatherunderground.com',
                        creativeFileName: 'F4UCorsair.jpg',
                        creativeGroupId: 9077531,
                        creativeGroupName: 'geo Targeting',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeHeight: 399,
                        creativeId: 9077541,
                        creativeWidth: 600
                    }
                ];

            $httpBackend.expectGET(API_SERVICE + 'Campaigns/9084263/creativeInsertions/groupCreatives')
                .respond({
                    startIndex: 0,
                    pageSize: 3,
                    totalNumberOfRecords: 3,
                    records: [
                        {
                            CreativeGroupCreativeView: expected
                        }
                    ]
                });

            promise = CampaignsService.getCreativeGroupCreatives(9084263);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });
    });

    describe('getPlacements()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].Placement', function () {
                var promise,
                    expected = [
                        {
                            adSpend: 0.0,
                            endDate: '2015-08-06T00:00:00-06:00',
                            height: 400,
                            id: 9108257,
                            ioId: 9108256,
                            name: 'sport - Header - 100x400',
                            siteId: 9107208,
                            siteName: 'sport',
                            siteSectionId: 9107209,
                            siteSectionName: 'Header',
                            sizeId: 22157,
                            sizeName: '100x400',
                            startDate: '2015-07-07T00:00:00-06:00',
                            status: 'Rejected',
                            width: 100
                        },
                        {
                            adSpend: 0.0,
                            endDate: '2015-08-06T00:00:00-06:00',
                            height: 100,
                            id: 9108260,
                            ioId: 9108256,
                            name: 'IO',
                            siteId: 9107159,
                            siteName: 'IOS SITE',
                            siteSectionId: 9107160,
                            siteSectionName: 'SITE IOS',
                            sizeId: 22158,
                            sizeName: '200x100',
                            startDate: '2015-07-07T00:00:00-06:00',
                            status: 'Rejected',
                            width: 200
                        },
                        {
                            adSpend: 0.0,
                            endDate: '2015-08-06T00:00:00-06:00',
                            height: 90,
                            id: 9108263,
                            ioId: 9108256,
                            name: 'IO',
                            siteId: 9107198,
                            siteName: 'ESPN',
                            siteSectionId: 9107199,
                            siteSectionName: 'Homepage',
                            sizeId: 22159,
                            sizeName: '728x90',
                            startDate: '2015-07-07T00:00:00-06:00',
                            status: 'Rejected',
                            width: 728
                        }
                    ];

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/creativeInsertions/placements')
                    .respond({
                        startIndex: 0,
                        pageSize: 1000,
                        totalNumberOfRecords: 2,
                        records: [
                            {
                                PlacementView: expected
                            }
                        ]
                    });

                promise = CampaignsService.getPlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(expected);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    expected = {
                        adSpend: 0.0,
                        endDate: '2015-08-06T00:00:00-06:00',
                        height: 400,
                        id: 9108257,
                        ioId: 9108256,
                        name: 'sport - Header - 100x400',
                        siteId: 9107208,
                        siteName: 'sport',
                        siteSectionId: 9107209,
                        siteSectionName: 'Header',
                        sizeId: 22157,
                        sizeName: '100x400',
                        startDate: '2015-07-07T00:00:00-06:00',
                        status: 'Rejected',
                        width: 100
                    };

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/creativeInsertions/placements')
                    .respond({
                        startIndex: 0,
                        pageSize: 1000,
                        totalNumberOfRecords: 2,
                        records: [
                            {
                                PlacementView: expected
                            }
                        ]
                    });

                promise = CampaignsService.getPlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([expected]);
            });

            it('should resolve promise with empty array when no records in response', function () {
                var promise;

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/9024566/creativeInsertions/placements')
                    .respond({});

                promise = CampaignsService.getPlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/9024566/creativeInsertions/placements')
                    .respond(404);
                CampaignsService.getPlacements(9024566);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/9024566/creativeInsertions/placements')
                    .respond(404, 'FAILED');
                promise = CampaignsService.getPlacements(9024566);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getCreativeInsertions()', function () {
        it('should resolve promise with creative insertions', function () {
            var promise,
                expected = [
                    {
                        campaignId: 9084263,
                        createdDate: '2015-08-17T11:49:34-06:00',
                        createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        creativeAlias: 'Super Expert chiken',
                        creativeClickthrough: 'http://www.trueffect.com',
                        creativeGroupId: 31878,
                        creativeGroupName: 'Default00',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeId: 9128897,
                        creativeInsertionRootId: 9184057,
                        endDate: '2014-07-12T00:00:00-06:00',
                        id: 10000368,
                        logicalDelete: 'N',
                        modifiedDate: '2015-08-17T11:49:34-06:00',
                        modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        placementEndDate: '2014-07-12T00:00:00-06:00',
                        placementId: 9175602,
                        placementName: 'Cox Media - Cox Media - 100x100',
                        placementStartDate: '2014-05-15T00:00:00-06:00',
                        placementStatus: 'Rejected',
                        released: 0,
                        sequence: 0,
                        siteId: 9175592,
                        siteName: 'Cox Media',
                        siteSectionId: 9175601,
                        siteSectionName: 'Cox Media',
                        startDate: '2014-05-15T00:00:00-06:00',
                        timeZone: 'MDT',
                        weight: 100
                    },
                    {
                        campaignId: 9084263,
                        createdDate: '2015-08-17T13:08:00-06:00',
                        createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        creativeAlias: 'Super Expert chiken',
                        creativeClickthrough: 'http://www.trueffect.com',
                        creativeGroupId: 31878,
                        creativeGroupName: 'Default00',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeId: 9128897,
                        creativeInsertionRootId: 9184421,
                        endDate: '2014-04-06T00:00:00-06:00',
                        id: 10000369,
                        logicalDelete: 'N',
                        modifiedDate: '2015-08-17T13:08:00-06:00',
                        modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        placementEndDate: '2014-04-06T00:00:00-06:00',
                        placementId: 9175594,
                        placementName: 'Cox Media-Cox Media_ROS_wsbradio_b985_kiss1041_BAN_ATL_728x90-100x100',
                        placementStartDate: '2014-03-03T00:00:00-07:00',
                        placementStatus: 'Rejected',
                        released: 0,
                        sequence: 0,
                        siteId: 9175592,
                        siteName: 'Cox Media',
                        siteSectionId: 9175593,
                        siteSectionName: 'Cox Media_ROS_wsbradio_b985_kiss1041_BAN_ATL_728x90',
                        startDate: '2014-03-03T00:00:00-07:00',
                        timeZone: 'MDT',
                        weight: 100
                    },
                    {
                        campaignId: 9084263,
                        createdDate: '2015-08-17T13:17:42-06:00',
                        createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        creativeAlias: 'Super Expert chiken',
                        creativeClickthrough: 'http://www.trueffect.com',
                        creativeGroupId: 31878,
                        creativeGroupName: 'Default00',
                        creativeGroupWeight: 100,
                        creativeGroupWeightEnabled: 0,
                        creativeId: 9128897,
                        creativeInsertionRootId: 9184422,
                        endDate: '2014-04-06T00:00:00-06:00',
                        id: 10000370,
                        logicalDelete: 'N',
                        modifiedDate: '2015-08-17T13:17:42-06:00',
                        modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                        placementEndDate: '2014-04-06T00:00:00-06:00',
                        placementId: 9175598,
                        placementName: 'Cox Media-Cox Media_ROS_wsbradio_b985_kiss1041_REC_ATL_300x250-100x100',
                        placementStartDate: '2014-03-03T00:00:00-07:00',
                        placementStatus: 'Rejected',
                        released: 0,
                        sequence: 0,
                        siteId: 9175592,
                        siteName: 'Cox Media',
                        siteSectionId: 9175597,
                        siteSectionName: 'Cox Media_ROS_wsbradio_b985_kiss1041_REC_ATL_300x250',
                        startDate: '2014-03-03T00:00:00-07:00',
                        timeZone: 'MDT',
                        weight: 100
                    }
                ];

            $httpBackend.expectGET(API_SERVICE + 'Campaigns/9084263/creativeInsertions').respond({
                startIndex: 0,
                pageSize: 3,
                totalNumberOfRecords: 3,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.getCreativeInsertions(9084263);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });
    });

    describe('getCreativeInsertions()', function () {
        it('should resolve promise with a list of creative insertions according to parameters', function () {
            var promise,
                expected = [
                    {
                        associationsWithCreativeGroups: 1,
                        associationsWithPlacements: 1,
                        createdDate: '2015-12-30T08:08:07.631-07:00',
                        creativeAlias: 'creative100x80-5',
                        creativeInsertionRootId: 6,
                        creativeType: 'gif',
                        endDate: '0116-01-30T23:59:59.631-07:00',
                        filename: 'creative100x80-5.gif',
                        id: 5,
                        modifiedDate: '2015-12-30T08:08:07.631-07:00',
                        primaryClickthrough: 'http://www.some-dummy-url.com/',
                        released: 0,
                        sequence: 0,
                        startDate: '0115-12-30T00:00:00.631-07:00',
                        timeZone: 'MST',
                        weight: 85
                    },
                    {
                        associationsWithCreativeGroups: 1,
                        associationsWithPlacements: 1,
                        createdDate: '2015-12-30T08:08:07.631-07:00',
                        creativeAlias: 'creative100x80-7',
                        creativeInsertionRootId: 8,
                        creativeType: 'gif',
                        endDate: '0116-01-30T23:59:59.631-07:00',
                        filename: 'creative100x80-7.gif',
                        id: 7,
                        modifiedDate: '2015-12-30T08:08:07.631-07:00',
                        primaryClickthrough: 'http://www.some-dummy-url.com/',
                        released: 0,
                        sequence: 0,
                        startDate: '0115-12-30T00:00:00.631-07:00',
                        timeZone: 'MST',
                        weight: 85
                    },
                    {
                        associationsWithCreativeGroups: 1,
                        associationsWithPlacements: 1,
                        createdDate: '2015-12-30T08:08:07.631-07:00',
                        creativeAlias: 'creative100x80-9',
                        creativeInsertionRootId: 10,
                        creativeType: 'gif',
                        endDate: '0116-01-30T23:59:59.631-07:00',
                        filename: 'creative100x80-9.gif',
                        id: 9,
                        modifiedDate: '2015-12-30T08:08:07.631-07:00',
                        primaryClickthrough: 'http://www.some-dummy-url.com/',
                        released: 0,
                        sequence: 0,
                        startDate: '0115-12-30T00:00:00.631-07:00',
                        timeZone: 'MST',
                        weight: 85
                    }
                ];

            $httpBackend.expectGET(API_SERVICE +
                'Campaigns/9084263/creativeInsertions?groupId=8681452&pivotType=SITE&placementId=8149152&' +
                'sectionId=7221524&siteId=6241523&type=CREATIVE').respond({
                startIndex: 0,
                pageSize: 3,
                totalNumberOfRecords: 3,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.getCreativeInsertions(9084263, 'SITE', 'CREATIVE',
                6241523, 7221524, 8149152, 8681452);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });

        it('should resolve promise with a list of placements according to parameters', function () {
            var promise,
                expected = [
                    {
                        associationsWithCreativeGroups: 5,
                        placementEndDate: '0116-01-30T23:59:59.652-07:00',
                        placementId: 3,
                        placementName: 'Placement 3',
                        placementStartDate: '0115-12-30T00:00:00.652-07:00',
                        placementStatus: 'Accepted',
                        sizeName: '100x80'
                    },
                    {
                        associationsWithCreativeGroups: 5,
                        placementEndDate: '0116-01-30T23:59:59.652-07:00',
                        placementId: 3,
                        placementName: 'Placement 3',
                        placementStartDate: '0115-12-30T00:00:00.652-07:00',
                        placementStatus: 'Accepted',
                        sizeName: '100x80'
                    }
                ];

            $httpBackend.expectGET(API_SERVICE +
                'Campaigns/9084263/creativeInsertions?pivotType=SITE&sectionId=7491283&' +
                'siteId=6158578&type=PLACEMENT').respond({
                startIndex: 0,
                pageSize: 2,
                totalNumberOfRecords: 2,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.getCreativeInsertions(9084263, 'SITE', 'PLACEMENT', 6158578, 7491283);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });
    });

    describe('searchCreativeInsertions()', function () {
        it('should resolve promise with matching results', function () {
            var promise,
                expected = [
                    {
                        siteId: 6086072,
                        siteName: 'SITE - Unit tests',
                        siteSectionId: 6086073,
                        siteSectionName: 'SiteSection - Unit tests'
                    },
                    {
                        siteId: 6086072,
                        siteName: 'SITE - Unit tests'
                    },
                    {
                        siteId: 6291689,
                        siteName: '1-Site-tests'
                    }
                ],
                searchOn = {
                    site: true,
                    section: true,
                    placement: true,
                    group: true,
                    creative: true
                };

            $httpBackend.expectGET(API_SERVICE + 'Campaigns/9084272/searchCreativeInsertions?pattern=test' +
                '&pivotType=site&soCreative=true&soGroup=true&soPlacement=true&soSection=true&soSite=true').respond({
                startIndex: 0,
                pageSize: 3,
                totalNumberOfRecords: 3,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.searchCreativeInsertions(9084272, 'test', 'site', searchOn);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });

        it('should return an empty object when there are no matches', function () {
            var promise,
                expected = [
                    {}
                ],
                searchOn = {
                    site: true,
                    section: true,
                    placement: true
                };

            $httpBackend.expectGET(API_SERVICE + 'Campaigns/9084313/searchCreativeInsertions?pattern=j2mnaw9' +
                '&pivotType=site&soPlacement=true&soSection=true&soSite=true').respond({
                startIndex: 0,
                pageSize: 0,
                totalNumberOfRecords: 0,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.searchCreativeInsertions(9084313, 'j2mnaw9', 'site', searchOn);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });

        it('should resolve promise with search results within sent id\'s scope', function () {
            var promise,
                expected = [
                    {
                        siteId: 6086072,
                        siteName: 'SITE - Unit tests',
                        siteSectionId: 6086073,
                        siteSectionName: 'SiteSection - Unit tests'
                    }
                ],
                ids = {
                    site: 6086072,
                    section: 6086073
                },
                searchOn = {
                    site: true,
                    section: true,
                    placement: false,
                    group: false,
                    creative: false
                };

            $httpBackend.expectGET(API_SERVICE + 'Campaigns/9084272/searchCreativeInsertions?pattern=test' +
                '&pivotType=site&sectionId=6086073&siteId=6086072&soCreative=false&soGroup=false&soPlacement=false' +
                '&soSection=true&soSite=true&type=section').respond({
                startIndex: 0,
                pageSize: 1,
                totalNumberOfRecords: 1,
                records: [
                    {
                        CreativeInsertionView: expected
                    }
                ]
            });
            promise = CampaignsService.searchCreativeInsertions(9084272, 'test', 'site', searchOn, 'section', ids);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });
    });

    describe('bulkSaveSiteSectionSize()', function () {
        it('should make a bulk save for a new Publisher, Site, Section and Size', function () {
            var promise,
                postData = {
                    publisher: {
                        name: 'publisher 1'
                    },
                    site: {
                        name: 'Site 1'
                    },
                    section: {
                        name: 'Section 1'
                    },
                    size: {
                        name: 'Size 1'
                    }
                };

            $httpBackend.whenPOST(API_SERVICE + 'Agencies/bulkPublisherSiteSectionSize').respond(200);
            promise = CampaignsService.bulkSaveSiteSectionSize(postData);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(postData);
        });

        it('should make a bulk save even though there is a duplicate name already in the DB', function () {
            var promise,
                postData = {
                    publisher: {
                        name: 'publisher 1'
                    },
                    site: {
                        name: 'Site 1'
                    },
                    section: {
                        name: 'Section 1'
                    },
                    size: {
                        name: 'Size 1'
                    }
                };

            $httpBackend.expectPOST(API_SERVICE + 'Agencies/bulkPublisherSiteSectionSize?ignoreDupSite=true')
                .respond(200);
            promise = CampaignsService.bulkSaveSiteSectionSize(postData,
                {
                    ignoreDupSite: true
                }
            );
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(postData);
        });
    });

    describe('exportResource()', function () {
        describe('on success', function () {
            it('should resolve promise with a file', function () {
                var promise,
                    response = new ArrayBuffer(1),
                    expected = {
                        data: new ArrayBuffer(1),
                        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                        name: 'campaignExport'
                    };

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/export?format=xlsx&type=schedule').respond(response);
                promise = CampaignsService.exportResource(1337, 'xlsx', 'schedule');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(expected);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/export?format=xlsx&type=schedule').respond(404);
                CampaignsService.exportResource(1337, 'xlsx', 'schedule');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337/export?format=xlsx&type=schedule')
                    .respond(404, 'FAILED');
                promise = CampaignsService.exportResource(1337, 'xlsx', 'schedule');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('importResource()', function () {
        it('import a file for Shedule', function () {
            var promise,
                campaignId = 9084263,
                file = {
                    name: 'import_excel.xlsx'
                } ,
                response = {
                    rows: '200 rows success'
                },
                type = 'CreativeInsertionExportView';

            $httpBackend.expectPOST(API_SERVICE + 'Campaigns/' + campaignId + '/upload?type=' + type).respond(response);
            promise = CampaignsService.uploadResource(campaignId, file, type);
            $httpBackend.flush();
            expect(promise).toBeResolvedWith(response.data);
        });
    });

    describe('exportIssuesResource()', function () {
        describe('on success', function () {
            it('should resolve the promise with a file', function () {
                var promise,
                    response = new ArrayBuffer(1),
                    expected = {
                        data: new ArrayBuffer(1),
                        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                        name: 'campaignExport'
                    };

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/7777/issues?format=xlsx&type=CreativeInsertion' +
                                     '&uuid=379c916a-8c5a-4755-a571-937a4c94b8c1').respond(response);
                promise = CampaignsService.exportIssuesResource(7777, 'xlsx', 'CreativeInsertion',
                                                                '379c916a-8c5a-4755-a571-937a4c94b8c1');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(expected);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/7777/issues?format=xlsx&type=CreativeInsertion' +
                                     '&uuid=379c916a-8c5a-4755-a571-937a4c94b8c1').respond(404);
                CampaignsService.exportIssuesResource(7777, 'xlsx', 'CreativeInsertion',
                                                      '379c916a-8c5a-4755-a571-937a4c94b8c1');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/7777/issues?format=xlsx&type=CreativeInsertion' +
                                     '&uuid=379c916a-8c5a-4755-a571-937a4c94b8c1')
                    .respond(404, 'FAILED');
                promise = CampaignsService.exportIssuesResource(7777, 'xlsx', 'CreativeInsertion',
                                                                '379c916a-8c5a-4755-a571-937a4c94b8c1');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });

        describe('getSiteContacts()', function () {
            describe('on success', function () {
                it('should resolve promise with campaign', function () {
                    var promise;

                    $httpBackend.whenGET(API_SERVICE + 'Campaigns/1337').respond({
                        id: '1337',
                        contactEmail: 'terrell.wildermna@yahoo.com',
                        contactId: '3285917543006555600',
                        contactName: 'Dahlia Batz',
                        publisherId: '503295575874071700',
                        publisherName: 'Amanda'
                    });
                    promise = CampaignsService.getCampaign(1337);
                    $httpBackend.flush();

                    expect(promise).toBeResolvedWith({
                        id: '1337',
                        contactEmail: 'terrell.wildermna@yahoo.com',
                        contactId: '3285917543006555600',
                        contactName: 'Dahlia Batz',
                        publisherId: '503295575874071700',
                        publisherName: 'Amanda'
                    });
                });
            });
        });

        describe('bulkUpdate()', function () {
            it('should associate creative with placement', function () {
                var promise,
                    creativeInsertions = [
                        {
                            campaignId: 12345,
                            clickthrough: 'http://www.trueffect.com',
                            creativeGroupId: 9074363,
                            creativeId: 9034120,
                            endDate: '2016-04-01T23:59:59-07:00',
                            placementId: 89567,
                            released: 0,
                            sequence: 0,
                            startDate: '2016-03-02T00:00:00-07:00',
                            timeZone: 'MST',
                            weight: 100
                        }
                    ];

                $httpBackend.whenPOST(API_SERVICE + 'Campaigns/12345/bulkCreativeInsertion').respond(201);
                promise = CampaignsService.bulkSave(12345, creativeInsertions);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });
    });

    describe('getPackageList()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].Package', function () {
                var promise;

                $httpBackend.expectGET(API_SERVICE + 'Campaigns/5959474/packages?ioId=123456')
                    .respond(packageListFromAPI);
                promise = CampaignsService.getPackageList('5959474', '123456');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(packageListFromAPI.records[0].Package);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/packages?ioId=123456')
                    .respond({
                        records: [
                            {
                                Package: [
                                    packageListFromAPI.records[0].Package[0]
                                ]

                            }
                        ]
                    });
                promise = CampaignsService.getPackageList('5959474', '123456');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([packageListFromAPI.records[0].Package[0]]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/packages?ioId=123456')
                    .respond(404);
                CampaignsService.getPackageList('5959474', '123456');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Campaigns/5959474/packages?ioId=123456')
                    .respond(404, 'FAILED');
                promise = CampaignsService.getPackageList('5959474', '123456');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
