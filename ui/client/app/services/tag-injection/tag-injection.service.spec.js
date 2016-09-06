'use strict';

describe('Service: TagInjectionService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        TagInjectionService,
        PlacementListFromAPI = {
            totalNumberOfRecords: 3,
            records: [
                {
                    PlacementView: [
                        {
                            campaignId: 5011922,
                            campaignName: '24c_test',
                            endDate: '2013-01-13T23:59:59-07:00',
                            id: 5011925,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-14T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-10T23:59:59-07:00',
                            id: 5011520,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-11T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-11T23:59:59-07:00',
                            id: 5011707,
                            siteId: 5011005,
                            siteName: 'PubSite',
                            startDate: '2012-12-12T00:00:00-07:00'
                        }
                    ]
                }
            ]
        };

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _TagInjectionService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        TagInjectionService = _TagInjectionService_;

        installPromiseMatchers();
    }));

    describe('getHtmlInjectionTagsById()', function () {
        it('should get an HTML Tag Injection based on the given id', function () {
            var tagId = 246,
                promise,
                tagResponse = {
                    agencyId: 6031295,
                    createdDate: '2012-12-11T13:22:20-07:00',
                    htmlContent: '<SCRIPT SRC=\"http://qa.adlegend.net/jscript?NIF=N&target=_blank&@CPSC@=\"></SCRIPT>',
                    id: 246,
                    isEnabled: 1,
                    isVisible: 0,
                    modifiedDate: '2016-06-23T13:23:33-07:00',
                    name: 'Adserver JSCRIPt1'
                };

            $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + tagId).respond(tagResponse);
            promise = TagInjectionService.getHtmlInjectionTagsById(tagId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(tagResponse);
        });

        it('should get an error due a request of aan HTML Tag Injection with a wrong id', inject(function () {
            var tagId = 0,
                tagResponse = 404;

            $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + tagId).respond(tagResponse);
            TagInjectionService.getHtmlInjectionTagsById(tagId);
            $httpBackend.flush();

            expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
        }));
    });

    describe('getTagPlacementsAssociated()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].PlacementView', function () {
                var promise,
                    tagId = 246;

                $httpBackend.expectGET(
                    API_SERVICE + 'HtmlInjectionTags/' + tagId + '/placementAssociated').respond(PlacementListFromAPI);
                promise = TagInjectionService.getTagPlacementsAssociated(246);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    placements: [
                        {
                            campaignId: 5011922,
                            campaignName: '24c_test',
                            endDate: '2013-01-13T23:59:59-07:00',
                            id: 5011925,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-14T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-10T23:59:59-07:00',
                            id: 5011520,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-11T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-11T23:59:59-07:00',
                            id: 5011707,
                            siteId: 5011005,
                            siteName: 'PubSite',
                            startDate: '2012-12-12T00:00:00-07:00'
                        }
                    ],
                    totalNumberOfRecords: 3
                });
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    tagId = 246;

                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + tagId + '/placementAssociated')
                    .respond({
                        totalNumberOfRecords: 1,
                        records: [
                            {
                                PlacementView: {
                                    campaignId: 5011922,
                                    campaignName: '24c_test',
                                    endDate: '2013-01-13T23:59:59-07:00',
                                    id: 5011925,
                                    siteId: 5011518,
                                    siteName: 'Site2',
                                    startDate: '2012-12-14T00:00:00-07:00'
                                }
                            }
                        ]
                    });
                promise = TagInjectionService.getTagPlacementsAssociated(246);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    placements: [
                        {
                            campaignId: 5011922,
                            campaignName: '24c_test',
                            endDate: '2013-01-13T23:59:59-07:00',
                            id: 5011925,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-14T00:00:00-07:00'
                        }
                    ],
                    totalNumberOfRecords: 1
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + 246 + '/placementAssociated')
                    .respond(404);
                TagInjectionService.getTagPlacementsAssociated(246);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + 246 + '/placementAssociated')
                    .respond(404, 'FAILED');
                promise = TagInjectionService.getTagPlacementsAssociated(246);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateHtmlInjectionTag()', function () {
        var tagInjection;

        beforeEach(function () {
            tagInjection = {
                agencyId: 6031295,
                createdDate: '2012-12-11T13:22:20-07:00',
                htmlContent: '<SCRIPT SRC=\"http://qa.adlegend.net/jscript?NIF=N&target=_blank&@CPSC@=\"></SCRIPT>',
                id: 246,
                isEnabled: 1,
                isVisible: 0,
                modifiedDate: '2016-06-23T13:23:33-07:00',
                name: 'Adserver JSCRIPt1'
            };
        });

        it('should PUT all data', function () {
            var promise;

            tagInjection.name = 'newTagInjectionName';
            tagInjection.isVisible = 1;
            tagInjection.htmlContent = '<SCRIPT SRC=\"http://www.google.com"></SCRIPT>';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'HtmlInjectionTags/246',
                {
                    agencyId: 6031295,
                    createdDate: '2012-12-11T13:22:20-07:00',
                    htmlContent: '<SCRIPT SRC=\"http://www.google.com"></SCRIPT>',
                    id: 246,
                    isEnabled: 1,
                    isVisible: 1,
                    modifiedDate: '2016-06-23T13:23:33-07:00',
                    name: 'newTagInjectionName'
                }).respond({});

            promise = TagInjectionService.updateHtmlInjectionTag(tagInjection);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('searchPlacementView()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].PlacementView', function () {
                var promise,
                    tagId = 246;

                $httpBackend.expectGET(
                    API_SERVICE + 'HtmlInjectionTags/' + tagId + '/searchPlacementsAssociated?' +
                    'pattern=test&soCampaign=true&soPlacement=true&soSite=true').respond(PlacementListFromAPI);
                promise = TagInjectionService.searchPlacementView(246, 'test', true, true, true);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    placements: [
                        {
                            campaignId: 5011922,
                            campaignName: '24c_test',
                            endDate: '2013-01-13T23:59:59-07:00',
                            id: 5011925,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-14T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-10T23:59:59-07:00',
                            id: 5011520,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-11T00:00:00-07:00'
                        },
                        {
                            campaignId: 5011061,
                            campaignName: 'Piggy_back_QA',
                            endDate: '2013-01-11T23:59:59-07:00',
                            id: 5011707,
                            siteId: 5011005,
                            siteName: 'PubSite',
                            startDate: '2012-12-12T00:00:00-07:00'
                        }
                    ],
                    totalNumberOfRecords: 3
                });
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    tagId = 246;

                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + tagId + '/searchPlacementsAssociated?' +
                    'pattern=test&soCampaign=true&soPlacement=true&soSite=true')
                    .respond({
                        totalNumberOfRecords: 1,
                        records: [
                            {
                                PlacementView: {
                                    campaignId: 5011922,
                                    campaignName: '24c_test',
                                    endDate: '2013-01-13T23:59:59-07:00',
                                    id: 5011925,
                                    siteId: 5011518,
                                    siteName: 'Site2',
                                    startDate: '2012-12-14T00:00:00-07:00'
                                }
                            }
                        ]
                    });
                promise = TagInjectionService.searchPlacementView(246, 'test', true, true, true);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    placements: [
                        {
                            campaignId: 5011922,
                            campaignName: '24c_test',
                            endDate: '2013-01-13T23:59:59-07:00',
                            id: 5011925,
                            siteId: 5011518,
                            siteName: 'Site2',
                            startDate: '2012-12-14T00:00:00-07:00'
                        }
                    ],
                    totalNumberOfRecords: 1
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + 246 + '/searchPlacementsAssociated?' +
                    'pattern=test&soCampaign=true&soPlacement=true&soSite=true')
                    .respond(404);
                TagInjectionService.searchPlacementView(246, 'test', true, true, true);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'HtmlInjectionTags/' + 246 + '/searchPlacementsAssociated?' +
                    'pattern=test&soCampaign=true&soPlacement=true&soSite=true')
                    .respond(404, 'FAILED');
                promise = TagInjectionService.searchPlacementView(246, 'test', true, true, true);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
