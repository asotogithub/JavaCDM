'use strict';

describe('Service: AdmService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        AdmService,
        ErrorRequestHandler,
        UserService;

    beforeEach(module('uiApp', function ($provide) {
        UserService = jasmine.createSpyObj('UserService', ['getUser', 'hasPermission', 'clear']);

        $provide.value('UserService', UserService);
    }));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _AdmService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        AdmService = _AdmService_;
        installPromiseMatchers();
    }));

    describe('getADMDatasetConfigs()', function () {
        beforeEach(function () {
            var deferred = $q.defer();

            deferred.resolve({
                agencyId: 'foobar'
            });
            UserService.getUser.andReturn(deferred.promise);
        });

        describe('on success', function () {
            it('should resolve promise with response.records[0].Dataset', function () {
                var promise,
                    dataset = [
                        {
                            id: 'data001',
                            advertiserId: 100,
                            advertiserName: 'ABC Company',
                            domain: 'meta.cmm.abc.com',
                            fileNamePrefix: 'alphaABC'
                        },
                        {
                            id: 'data002',
                            advertiserId: 100,
                            advertiserName: 'ABC Company',
                            domain: 'meta.cmm.xyz.com',
                            fileNamePrefix: 'alphaXYZ'
                        },
                        {
                            id: 'data003',
                            advertiserId: 50,
                            advertiserName: 'ACME, Inc.',
                            domain: 'meta.cmm.acme.com',
                            fileNamePrefix: 'wecarr'
                        }
                    ],
                    records = [
                        {
                            Dataset: dataset
                        }
                    ];

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/datasets').respond(
                    {
                        records: records
                    }
                );
                promise = AdmService.getADMDatasetConfigs();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(dataset);
            });

            it('should wrap single record response in an array', function () {
                var promise,
                    dataset = [
                        {
                            id: 'data001',
                            advertiserId: 100,
                            advertiserName: 'ABC Company',
                            domain: 'meta.cmm.abc.com',
                            fileNamePrefix: 'alphaABC'
                        }
                    ],
                    records = [
                        {
                            Dataset: dataset
                        }
                    ];

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/datasets').respond(
                    {
                        records: records
                    }
                );
                promise = AdmService.getADMDatasetConfigs();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(dataset);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/datasets').respond(404);
                AdmService.getADMDatasetConfigs();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Agencies/foobar/datasets').respond(404, 'FAILED');
                promise = AdmService.getADMDatasetConfigs();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getAdmDetails()', function () {
        describe('on success', function () {
            it('should resolve promise with dataObject', function () {
                var promise,
                    dataObject = {
                        datasetId: 'data001',
                        agencyId: 10010,
                        advertiserId: 100,
                        domain: 'meta.cmm.abc.acme.com',
                        fileNamePrefix: 'alphaABC',
                        cookieExpirationDays: 7,
                        admTtlSeconds: 60,
                        cookiesToCapture: ['oreo', 'macadamia'],
                        defaultKey: 'abc123',
                        durableCookies: ['plastic', 'metal'],
                        matchCookieName: 'windsor'
                    };

                $httpBackend.whenGET(API_SERVICE + 'Datasets/data001').respond(dataObject);
                promise = AdmService.getAdmDetails('data001');
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(dataObject);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Datasets/69').respond(404);
                AdmService.getAdmDetails(69);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Datasets/42').respond(404, 'FAILED');
                promise = AdmService.getAdmDetails(42);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateAdmDetails()', function () {
        it('should update data set configuration', function () {
            var promise,
                dataObject = {
                    datasetId: 'data001',
                    agencyId: 10010,
                    advertiserId: 100,
                    domain: 'meta.cmm.abc.acme.com',
                    fileNamePrefix: 'alphaABC',
                    cookieExpirationDays: 10,
                    admTtlSeconds: 10500,
                    defaultKey: 'abc',
                    matchCookieName: 'matchCookie'
                };

            $httpBackend.whenPUT(API_SERVICE + 'Datasets/data001').respond(200);
            promise = AdmService.updateAdmDetails(dataObject);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });
});
