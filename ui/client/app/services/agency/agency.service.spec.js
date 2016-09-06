'use strict';

describe('Service: AgencyService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        AgencyService,
        ErrorRequestHandler,
        UserService,
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
        },
        PlacementListFromAPI = {
            totalNumberOfRecords: 3,
            records: [
                {
                    PlacementView: [
                        {
                            campaignId: 10423795,
                            campaignName: 'Brand1'
                        },
                        {
                            campaignId: 10423796,
                            campaignName: 'Brand2'
                        },
                        {
                            campaignId: 10423797,
                            campaignName: 'Brand3'
                        }
                    ]
                }
            ]
        };

    beforeEach(module('uiApp', function ($provide) {
        UserService = jasmine.createSpyObj('UserService', ['getUser', 'hasPermission', 'clear']);

        $provide.value('UserService', UserService);
    }));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _AgencyService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        AgencyService = _AgencyService_;

        installPromiseMatchers();
    }));

    describe('getUsersTrafficking()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        describe('on success', function () {
            it('Should get a list of records with trafficking users', function () {
                var promise,
                    userView = [
                        {
                            id: 1,
                            contactId: 1,
                            userName: 'foo@bar.com',
                            realName: 'Foo Bar'
                        },
                        {
                            id: 2,
                            contactId: 2,
                            userName: 'one@bar.com',
                            realName: 'One Bar'
                        }
                    ];

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/traffickingUsers').respond({
                    records: [
                        {
                            UserView: userView
                        }
                    ]
                });
                promise = AgencyService.getUsersTrafficking();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(userView);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    userView = {
                        id: 2,
                        contactId: 2,
                        userName: 'one@bar.com',
                        realName: 'One Bar'
                    };

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/traffickingUsers').respond({
                    records: [
                        {
                            UserView: userView
                        }
                    ]
                });
                promise = AgencyService.getUsersTrafficking();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([userView]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/traffickingUsers').respond(404);
                AgencyService.getUsersTrafficking();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/traffickingUsers').respond(404, 'FAILED');
                promise = AgencyService.getUsersTrafficking();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getHTMLTagInjections()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        describe('on success', function () {
            it('Should get a list of tag injections', function () {
                var promise,
                    tagInjection = [
                        {
                            id: 1,
                            name: 'Tag A'
                        },
                        {
                            id: 2,
                            name: 'Tag B'
                        }
                    ];

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/htmlInjectionTags').respond({
                    records: [
                        {
                            HtmlInjectionTags: tagInjection
                        }
                    ]
                });
                promise = AgencyService.getHTMLTagInjections();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(tagInjection);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    tagInjection = {
                        id: 2,
                        name: 'Tag B'
                    };

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/htmlInjectionTags').respond({
                    records: [
                        {
                            HtmlInjectionTags: tagInjection
                        }
                    ]
                });
                promise = AgencyService.getHTMLTagInjections();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([tagInjection]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/htmlInjectionTags').respond(404);
                AgencyService.getHTMLTagInjections();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/htmlInjectionTags').respond(404, 'FAILED');
                promise = AgencyService.getHTMLTagInjections();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getHtmlInjectionTagAssociation()', function () {
        describe('on success', function () {
            it('should get all tags associations', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/9071473/htmlInjectionTagAssociation?campaignId=9225053&' +
                    'levelType=placements&placementId=10568211&sectionId=9439377&siteId=9439376'
                ).respond(TAG_ASSOCIATION_FROM_API);
                promise = AgencyService.getHtmlInjectionTagAssociation(
                    9071473,
                    'placements',
                    9225053,
                    9439376,
                    9439377,
                    10568211
                );
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(TAG_ASSOCIATION_FROM_API);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Agencies/9071473/htmlInjectionTagAssociation?campaignId=9225053&' +
                    'levelType=placements&placementId=10568211&sectionId=9439377&siteId=9439376').respond(404);
                AgencyService.getHtmlInjectionTagAssociation(
                    9071473,
                    'placements',
                    9225053,
                    9439376,
                    9439377,
                    10568211
                );
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/9071473/htmlInjectionTagAssociation?campaignId=9225053&' +
                    'levelType=placements&placementId=10568211&sectionId=9439377&siteId=9439376'
                ).respond(404, 'FAILED');
                promise = AgencyService.getHtmlInjectionTagAssociation(
                    9071473,
                    'placements',
                    9225053,
                    9439376,
                    9439377,
                    10568211
                );
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('saveTrackingTag()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        it('should create new tracking-tag', function () {
            var promise,
                trackingTag = {
                    optOutUrl: 'http://www.truefect.com',
                    name: 'Test-Tracking-Tag'
                };

            $httpBackend.whenPOST(API_SERVICE + 'Agencies/foobar/htmlInjectionTypeAdChoices').respond(200);
            promise = AgencyService.saveTrackingTag(trackingTag, 'htmlInjectionTypeAdChoices');
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(trackingTag);
        });
    });

    describe('bulkSaveHtmlInjectionTagAssociations()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        it('should bulk save tag associations', function () {
            var promise,
                payload = {
                    PlacementCreateTagAssocParam: [
                        {
                            levelType: 'placement',
                            all: false,
                            htmlTagInjectionId: 10695037,
                            campaignId: 9225053,
                            siteId: 9439376,
                            sectionId: 9439377,
                            placementId: 10719346
                        },
                        {
                            levelType: 'site',
                            all: true,
                            htmlTagInjectionId: 10695034,
                            campaignId: 9225053
                        }
                    ]
                };

            $httpBackend.whenPUT(API_SERVICE +
                'Agencies/foobar/htmlInjectionTagAssociationsBulk?advertiserId=9191904&brandId=9191947')
                .respond(TAG_ASSOCIATION_FROM_API);
            promise = AgencyService.bulkSaveHtmlInjectionTagAssociations(payload, 9191904, 9191947);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(TAG_ASSOCIATION_FROM_API);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPUT(API_SERVICE +
                    'Agencies/foobar/htmlInjectionTagAssociationsBulk?advertiserId=9191904&brandId=9191947'
                ).respond(404);
                AgencyService.bulkSaveHtmlInjectionTagAssociations({}, 9191904, 9191947);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenPUT(API_SERVICE +
                    'Agencies/foobar/htmlInjectionTagAssociationsBulk?advertiserId=9191904&brandId=9191947'
                ).respond(404, 'FAILED');
                promise = AgencyService.bulkSaveHtmlInjectionTagAssociations({}, 9191904, 9191947);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('searchPlacementView()', function () {
        describe('on success', function () {
            it('should resolve promise with response.records[0].PlacementView', function () {
                var promise,
                    tagId = 246;

                $httpBackend.expectGET(
                    API_SERVICE + 'Agencies/' + tagId + '/searchPlacementView?advertiserId=9024562&brandId=10423794&' +
                    'pattern=Brand2&soCampaign=true&soPlacement=false&soSection=false&' +
                    'soSite=false').respond(PlacementListFromAPI);
                promise = AgencyService.searchPlacementView(
                    246,
                    9024562,
                    10423794,
                    true,
                    false,
                    false,
                    false,
                    'Brand2');
                $httpBackend.flush();

                promise.then(function (response) {
                    expect(response).toBeResolvedWith({
                        placements: [
                            {
                                campaignId: 10423795,
                                campaignName: 'Brand1'
                            },
                            {
                                campaignId: 10423796,
                                campaignName: 'Brand2'
                            },
                            {
                                campaignId: 10423797,
                                campaignName: 'Brand3'
                            }
                        ],
                        totalNumberOfRecords: 3
                    });
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                var tagId = 246;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/' + tagId + '/searchPlacementView?advertiserId=9024562&' +
                    'brandId=10423794&pattern=Brand2&soCampaign=true&soPlacement=false&soSection=false&soSite=false')
                    .respond(404);
                AgencyService.searchPlacementView(
                    246,
                    9024562,
                    10423794,
                    true,
                    false,
                    false,
                    false,
                    'Brand2');
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise,
                    tagId = 246;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/' + tagId + '/searchPlacementView?advertiserId=9024562&' +
                    'brandId=10423794&pattern=Brand2&soCampaign=true&soPlacement=false&soSection=false&soSite=false')
                    .respond(404, 'FAILED');
                promise = AgencyService.searchPlacementView(
                    246,
                    9024562,
                    10423794,
                    true,
                    false,
                    false,
                    false,
                    'Brand2');
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
