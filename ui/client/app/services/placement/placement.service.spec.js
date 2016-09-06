'use strict';

describe('Service: PlacementService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        PlacementService,
        placementResponse,
        placementsArray,
        htmlInjectionTagsFromAPI = {
            records: [
                {
                    HtmlInjectionTags: [
                        {
                            agencyId: 9024559,
                            htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                            id: 70864,
                            isEnabled: 1,
                            isVisible: 1,
                            name: 'ChoicesLSD',
                            secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
                        }, {
                            agencyId: 9024559,
                            htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                            id: 10725912,
                            isEnabled: 1,
                            isVisible: 1,
                            name: 'TA3628',
                            secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
                        }, {
                            agencyId: 9024559,
                            htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                            id: 10659132,
                            isEnabled: 1,
                            isVisible: 1,
                            name: 'AdChoicesNewTag11'
                        }
                    ]
                }
            ]
        };

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($state,
                                _$httpBackend_,
                                _$log_,
                                _$q_,
                                _API_SERVICE_,
                                _ErrorRequestHandler_,
                                _PlacementService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        PlacementService = _PlacementService_;
        placementResponse = {
            adSpend: 0,
            campaignId: 6031386,
            countryCurrencyId: 1,
            createdDate: '2015-08-14T17:03:35-04:00',
            createdTpwsKey: 'a9d15433-100b-4502-ba57-7244f240b5d9',
            endDate: '2025-08-14T00:00:00-04:00',
            height: 150,
            id: 6092111,
            inventory: 1,
            ioId: 6061636,
            isScheduled: 'N',
            isTrafficked: 0,
            logicalDelete: 'N',
            maxFileSize: 1,
            modifiedDate: '2015-08-14T17:03:35-04:00',
            modifiedTpwsKey: 'a9d15433-100b-4502-ba57-7244f240b5d9',
            name: 'UI test - Placement Marcelo - 08',
            rate: 0,
            rateType: 'CPM',
            resendTags: 0,
            siteId: 6064873,
            siteName: 'UI Test For M. Amed ',
            siteSectionId: 6064876,
            sizeId: 5006631,
            sizeName: '180x150',
            startDate: '2015-08-14T00:00:00-04:00',
            status: 'New',
            utcOffset: 0,
            width: 180
        };
        placementsArray = [
            {
                adSpend: 0,
                endDate: new Date('2015-08-08T23:59:59-07:00'),
                height: 100,
                inventory: 1,
                name: 'sport - Header - 100x100',
                rate: 0,
                rateType: 'CPM',
                siteName: 'sport',
                startDate: new Date('2015-07-09T00:00:00-07:00'),
                status: 'Accepted',
                width: 100
            },
            {
                adSpend: 0,
                endDate: new Date('2015-08-08T23:59:59-07:00'),
                height: 150,
                inventory: 1,
                name: 'AOL - Header - 150x150',
                rate: 0,
                rateType: 'CPM',
                siteName: 'sport',
                startDate: new Date('2015-07-09T00:00:00-07:00'),
                status: 'Accepted',
                width: 150
            }
        ];
        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getPlacement()', function () {
        it('should get a placement based on the given id', function () {
            var placementId = 6092111,
                promise;

            $httpBackend.expectGET(API_SERVICE + 'Placements/' + placementId).respond(placementResponse);
            promise = PlacementService.getPlacement(placementId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(placementResponse);
        });

        it('should get an error due a request of a placement with a wrong id', inject(function () {
            var placementId = 0;

            placementResponse = 404;

            $httpBackend.whenGET(API_SERVICE + 'Placements/' + placementId).respond(placementResponse);
            PlacementService.getPlacement(placementId);
            $httpBackend.flush();

            expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
        }));
    });

    describe('updatePlacement()', function () {
        it('should update a placement', function () {
            var placementId = 6092111,
                promise;

            $httpBackend.expect('PUT', API_SERVICE + 'Placements/' + placementId,
                placementResponse)
                .respond(placementResponse);
            promise = PlacementService.updatePlacement(placementResponse);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(placementResponse);
        });
    });

    describe('saveBulkPlacements()', function () {
        it('should save the placements under Package', function () {
            var promise;

            $httpBackend.expectPOST(API_SERVICE + 'Placements/bulk?ioId=9110478&packageId=9113378', {
                records: {
                    Placement: placementsArray
                }
            }).respond({});

            promise = PlacementService.saveBulkPlacements(9110478, 9113378, placementsArray);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('updatePlacementsStatus()', function () {
        it('should save the new placements status', function () {
            var promise;

            $httpBackend.expectPUT(API_SERVICE + 'Placements/status?ioId=9110478', {
                records: {
                    Placement: placementsArray
                }
            }).respond({});

            promise = PlacementService.updatePlacementsStatus(9110478, placementsArray);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('getHtmlInjectionTags()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].HtmlInjectionTags', function () {
                var promise,
                    placementId = 9724182;

                $httpBackend.expectGET(
                    API_SERVICE + 'Placements/' + placementId + '/htmlInjectionTags').respond(htmlInjectionTagsFromAPI);
                promise = PlacementService.getHtmlInjectionTags(placementId);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        agencyId: 9024559,
                        htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                        id: 70864,
                        isEnabled: 1,
                        isVisible: 1,
                        name: 'ChoicesLSD',
                        secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
                    },
                    {
                        agencyId: 9024559,
                        htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                        id: 10725912,
                        isEnabled: 1,
                        isVisible: 1,
                        name: 'TA3628',
                        secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
                    },
                    {
                        agencyId: 9024559,
                        htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                        id: 10659132,
                        isEnabled: 1,
                        isVisible: 1,
                        name: 'AdChoicesNewTag11'
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    placementId = 9724182;

                $httpBackend.whenGET(API_SERVICE + 'Placements/' + placementId + '/htmlInjectionTags').respond({
                    records: [
                        {
                            HtmlInjectionTags: {
                                agencyId: 9024559,
                                htmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                                id: 10725912,
                                isEnabled: 1,
                                isVisible: 1,
                                name: 'TA3628'
                            }
                        }
                    ]
                });
                promise = PlacementService.getHtmlInjectionTags(placementId);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        agencyId: 9024559,
                        htmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                        id: 10725912,
                        isEnabled: 1,
                        isVisible: 1,
                        name: 'TA3628'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Placements/' + 9724182 + '/htmlInjectionTags')
                    .respond(404);
                PlacementService.getHtmlInjectionTags(9724182);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Placements/' + 9724182 + '/htmlInjectionTags')
                    .respond(404, 'FAILED');
                promise = PlacementService.getHtmlInjectionTags(9724182);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('sendTagEmail()', function () {
        it('should send tag email', function () {
            var promise,
                tagEmails = {};

            $httpBackend.expectPOST(API_SERVICE + 'Placements/sendTagEmail')
                .respond({});

            promise = PlacementService.sendTagEmail(tagEmails);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('get Ad Tags details()', function () {
        it('should get tag details', function () {
            var promise,
                placementId = 9724182,
                details = {
                    campaignName: 'UITEST QA Support-04',
                    endDate: '2016-03-31T00:00:00-07:00',
                    impressions: 4353345,
                    ioDescription: 'IO_01',
                    ioNumber: '1',
                    startDate: '2016-03-31T00:00:00-07:00',
                    clickRedirect: 'http://ui.qa.test.com/click.ng?spacedesc=10234758_9426318_100x' +
                        '100_9446669_10234758&af=72507&ml_pbi=-10234758&ml_camp=10234756&ml_crid=10234779&click=',
                    noScriptVersion: '<NOSCRIPT><A HREF="http://ui.qa.test.com/click?spacedesc=ML_NIF=Y" TARGET=' +
                        '"_blank"><IMG SRC="http://ui.qa.test.com/image?spacedesc=10234758&random=' +
                        '&ML_NIF=Y" WIDTH=100 HEIGHT=100 ALT="Click Here" BORDER=0></A></NOSCRIPT>',
                    scriptVersion: '<SCRIPT SRC="http://ui.qa.test.com/jscript?spacedesct=_blank&random=&@CPSC@=">' +
                        '</SCRIPT><NOSCRIPT><A HREF="http://ui.qa.test.com/click?spacedesc=ML_NIF=Y" ' +
                        'TARGET="_blank"><IMG SRC="http://ui.qa.test.com/image?spacedesc=10234758&random=' +
                        '&ML_NIF=Y" WIDTH=100 HEIGHT=100 ALT="Click Here" BORDER=0></A></NOSCRIPT>'
                };

            $httpBackend.whenGET(API_SERVICE + 'Placements/' + placementId + '/getAdTagsByPlacement').respond(details);
            promise = PlacementService.getAdTags(placementId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(details);
        });
    });

    describe('disassociateFromPackage()', function () {
        it('should disassociate placement From Package', function () {
            var promise;

            $httpBackend.expectPUT(API_SERVICE + 'Placements/9110478/disassociateFromPackage').respond({});

            promise = PlacementService.disassociateFromPackage(9110478);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('deleteHtmlInjectionTagsBulk()', function () {
        it('should call deleteHtmlInjectionTagsBulk() and resolve it', function () {
            var promise;

            $httpBackend.expectPUT(API_SERVICE + 'Placements/9110478/deleteHtmlInjectionTagsBulk').respond({});

            promise = PlacementService.deleteHtmlInjectionTagsBulk(9110478);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });
});
