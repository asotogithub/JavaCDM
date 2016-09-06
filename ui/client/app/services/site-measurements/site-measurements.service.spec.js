'use strict';

describe('Service: Site Measurement Campaigns', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        SiteMeasurementsService;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _SiteMeasurementsService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        SiteMeasurementsService = _SiteMeasurementsService_;
        installPromiseMatchers();
    }));

    describe('getCampaigns()', function () {
        describe('on success', function () {
            var promise,
                campaignList = [
                    {
                        id: 1,
                        campaignName: 'campaign01'
                    },
                    {
                        id: 2,
                        campaignName: 'campaign02'
                    },
                    {
                        id: 3,
                        campaignName: 'campaign03'
                    },
                    {
                        id: 4,
                        campaignName: 'campaign04'
                    }
                ],
                response = {
                    startIndex: '0',
                    pageSize: '1000',
                    totalNumberOfRecords: '4',
                    records: [
                        {
                            SiteMeasurementCampaignDTO: campaignList
                        }
                    ]
                };

            it('should get a list of associated campaigns', function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/campaigns?type=ASSOCIATED')
                    .respond(response);
                promise = SiteMeasurementsService.getCampaigns(9063954, true);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(campaignList);
            });

            it('should get a list of unassociated campaigns', function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/campaigns?type=UNASSOCIATED')
                    .respond(response);
                promise = SiteMeasurementsService.getCampaigns(9063954, false);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(campaignList);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog getting associated campaigns', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/campaigns?type=ASSOCIATED').respond(404);
                SiteMeasurementsService.getCampaigns(9063954, true);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should show generic error dialog getting unassociated campaigns', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/9063954/campaigns?type=UNASSOCIATED').respond(404);
                SiteMeasurementsService.getCampaigns(9063954, false);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('getSiteMeasurements()', function () {
        describe('on success', function () {
            it('should get object for all Site Measurement Campaigns', function () {
                var promise,
                    measurementObject =
                    {
                        startIndex: '0',
                        pageSize: '100',
                        totalNumberOfRecords: '2',
                        records: {
                            SiteMeasurementDTO: [
                                {
                                    assocMethod: 'AdvertiserName0',
                                    brandId: '0',
                                    clWindow: '0',
                                    cookieDomainId: '0',
                                    createdDate: '2015-05-27T12:21:14.474-04:00',
                                    createdTpwsKey: '000000',
                                    dupName: 'DupName0',
                                    expirationDate: '2015-05-27T12:21:14.474-04:00',
                                    id: '0',
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                                    modifiedTpwsKey: '000000',
                                    name: 'Name0',
                                    notes: 'Notes0',
                                    resourcePathId: '0',
                                    siteId: '0',
                                    state: '2',
                                    ttVer: '0',
                                    vtWindow: '0',
                                    advertiserId: '0'
                                },
                                {
                                    assocMethod: 'AdvertiserName4015',
                                    brandId: '4015',
                                    clWindow: '4015',
                                    cookieDomainId: '4015',
                                    createdDate: '2015-05-27T12:21:14.474-04:00',
                                    createdTpwsKey: '000000',
                                    dupName: 'DupName4015',
                                    expirationDate: '2015-05-27T12:21:14.474-04:00',
                                    id: '4015',
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                                    modifiedTpwsKey: '000000',
                                    name: 'Name4015',
                                    notes: 'Notes4015',
                                    resourcePathId: '4015',
                                    siteId: '4015',
                                    state: '3',
                                    ttVer: '4015',
                                    vtWindow: '4015',
                                    advertiserId: '4015'
                                }
                            ]
                        }
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements').respond(measurementObject);

                promise = SiteMeasurementsService.getSiteMeasurements();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(measurementObject);
            });

            it('should get an array of all Site Measurement Campaigns', function () {
                var promise,
                    measurementCollection =
                    {
                        startIndex: '0',
                        pageSize: '100',
                        totalNumberOfRecords: '2',
                        records: {
                            SiteMeasurementDTO: [
                                {
                                    assocMethod: 'AdvertiserName0',
                                    brandId: '0',
                                    clWindow: '0',
                                    cookieDomainId: '0',
                                    createdDate: '2015-05-27T12:21:14.474-04:00',
                                    createdTpwsKey: '000000',
                                    dupName: 'DupName0',
                                    expirationDate: '2015-05-27T12:21:14.474-04:00',
                                    id: '0',
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                                    modifiedTpwsKey: '000000',
                                    name: 'Name0',
                                    notes: 'Notes0',
                                    resourcePathId: '0',
                                    siteId: '0',
                                    state: '2',
                                    ttVer: '0',
                                    vtWindow: '0',
                                    advertiserId: '0'
                                },
                                {
                                    assocMethod: 'AdvertiserName4015',
                                    brandId: '4015',
                                    clWindow: '4015',
                                    cookieDomainId: '4015',
                                    createdDate: '2015-05-27T12:21:14.474-04:00',
                                    createdTpwsKey: '000000',
                                    dupName: 'DupName4015',
                                    expirationDate: '2015-05-27T12:21:14.474-04:00',
                                    id: '4015',
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                                    modifiedTpwsKey: '000000',
                                    name: 'Name4015',
                                    notes: 'Notes4015',
                                    resourcePathId: '4015',
                                    siteId: '4015',
                                    state: '3',
                                    ttVer: '4015',
                                    vtWindow: '4015',
                                    advertiserId: '4015'
                                }
                            ]
                        }
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements').respond(measurementCollection);

                promise = SiteMeasurementsService.getSiteMeasurements();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(measurementCollection);
            });

            it('should get an object of Site Measurement Campaign by an id given', function () {
                var promise,
                    measurementCollection =
                    {
                        assocMethod: 'AdvertiserName4015',
                        brandId: '4015',
                        clWindow: '4015',
                        cookieDomainId: '4015',
                        createdDate: '2015-05-27T12:21:14.474-04:00',
                        createdTpwsKey: '000000',
                        dupName: 'DupName4015',
                        expirationDate: '2015-05-27T12:21:14.474-04:00',
                        id: '4015',
                        logicalDelete: 'N',
                        modifiedDate: '2015-05-27T12:21:14.474-04:00',
                        modifiedTpwsKey: '000000',
                        name: 'Name4015',
                        notes: 'Notes4015',
                        resourcePathId: '4015',
                        siteId: '4015',
                        state: '3',
                        ttVer: '4015',
                        vtWindow: '4015',
                        advertiserId: '4015'
                    };

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements/4015').respond(measurementCollection);

                promise = SiteMeasurementsService.getSiteMeasurements(4015);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(measurementCollection);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements').respond(404);
                SiteMeasurementsService.getSiteMeasurements();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'SiteMeasurements').respond(404, 'FAILED');
                promise = SiteMeasurementsService.getSiteMeasurements();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateSiteMeasurements()', function () {
        describe('on success', function () {
            it('should update an object of Site Measurement Campaign by a state given', function () {
                var promise,
                    measurementObject =
                    {
                        assocMethod: 'AdvertiserName0',
                        brandId: '0',
                        clWindow: '0',
                        cookieDomainId: '0',
                        createdDate: '2015-05-27T12:21:14.474-04:00',
                        createdTpwsKey: '000000',
                        dupName: 'DupName0',
                        expirationDate: '2015-05-27T12:21:14.474-04:00',
                        id: '77',
                        logicalDelete: 'N',
                        modifiedDate: '2015-05-27T12:21:14.474-04:00',
                        modifiedTpwsKey: '000000',
                        name: 'Name77 UPDATED PUT',
                        notes: 'Notes0',
                        resourcePathId: '0',
                        siteId: '0',
                        state: '2',
                        ttVer: '0',
                        vtWindow: '0',
                        advertiserId: '0'
                    };

                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurements/77').respond(measurementObject);

                promise = SiteMeasurementsService.updateSiteMeasurement(measurementObject, 77);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(measurementObject);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurements/77').respond(404);
                SiteMeasurementsService.updateSiteMeasurement({}, 77);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurements/77').respond(404, 'FAILED');
                promise = SiteMeasurementsService.updateSiteMeasurement({}, 77);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('saveCampaign()', function () {
        describe('on success', function () {
            it('should save a campaign', function () {
                var promise;

                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurements').respond({});
                promise = SiteMeasurementsService.saveCampaign({});
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurements').respond(404);
                SiteMeasurementsService.saveCampaign({});
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('updateCampaignAssociation()', function () {
        describe('on success', function () {
            it('should update a campaign association', function () {
                var promise;

                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurements/9063954/campaigns').respond({});
                promise = SiteMeasurementsService.updateCampaignAssociation(9063954, []);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurements/9063954/campaigns').respond(404);
                SiteMeasurementsService.updateCampaignAssociation(9063954, []);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });
});
