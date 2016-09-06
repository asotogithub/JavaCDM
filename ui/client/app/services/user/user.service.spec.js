'use strict';

describe('Service: UserService', function () {
    var API_SERVICE,
        $cookies,
        $httpBackend,
        DialogFactory,
        ErrorRequestHandler,
        UserService;

    beforeEach(module('uiApp', function ($provide) {
        $cookies = {};
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog']);

        $provide.value('$cookies', $cookies);
        $provide.value('DialogFactory', DialogFactory);
    }));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _UserService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        API_SERVICE = _API_SERVICE_;
        UserService = _UserService_;

        installPromiseMatchers();
    }));

    describe('setUsername()', function () {
        it('should set $cookies.username', function () {
            UserService.setUsername('foo');
            expect($cookies.username).toEqual('foo');

            UserService.setUsername('bar');
            expect($cookies.username).toEqual('bar');

            UserService.setUsername('foobar');
            expect($cookies.username).toEqual('foobar');
        });
    });

    describe('getUser()', function () {
        describe('on success', function () {
            it('should get user info for $cookies.username', function () {
                var promise;

                UserService.setUsername('some@user.com');
                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com').respond({
                    id: 13,
                    agencyId: 37
                });

                promise = UserService.getUser();
                expect(UserService.getUser()).toEqual(promise);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    id: 13,
                    agencyId: 37
                });

                UserService.setUsername('another@user.com');
                $httpBackend.whenGET(API_SERVICE + 'Users/another@user.com').respond({
                    id: 42,
                    agencyId: 69
                });
                expect(UserService.getUser()).not.toEqual(promise);

                promise = UserService.getUser();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    id: 42,
                    agencyId: 69
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                UserService.setUsername('some@user.com');
                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com').respond(404);
                UserService.getUser();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                UserService.setUsername('some@user.com');
                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com').respond(404, 'FAILED');
                promise = UserService.getUser();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
                expect(UserService.getUser()).not.toBe(promise);
            });
        });
    });

    describe('getDomains()', function () {
        beforeEach(function () {
            UserService.setUsername('some@user.com');
        });

        describe('on success', function () {
            it('should resolve promise with response.records[0].CookieDomain', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/domains').respond({
                    records: [
                        {
                            CookieDomainDTO: [
                                {
                                    id: 1,
                                    domain: 'foo.com'
                                },
                                {
                                    id: 2,
                                    domain: 'bar.com'
                                },
                                {
                                    id: 3,
                                    domain: 'foo.bar.com'
                                }
                            ]
                        }
                    ]
                });
                promise = UserService.getDomains();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        id: 1,
                        domain: 'foo.com'
                    },
                    {
                        id: 2,
                        domain: 'bar.com'
                    },
                    {
                        id: 3,
                        domain: 'foo.bar.com'
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/domains').respond({
                    records: [
                        {
                            CookieDomainDTO: {
                                id: 1,
                                domain: 'foo.com'
                            }
                        }
                    ]
                });
                promise = UserService.getDomains();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        id: 1,
                        domain: 'foo.com'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/domains').respond(404);
                UserService.getDomains();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/domains').respond(404, 'FAILED');
                promise = UserService.getDomains();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('getAdvertisers()', function () {
        beforeEach(function () {
            UserService.setUsername('some@user.com');
        });

        describe('on success', function () {
            it('should resolve promise with response.records[0].Advertiser', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/advertisers').respond({
                    records: [
                        {
                            Advertiser: [
                                {
                                    address1: 'test1',
                                    agencyId: 9024559,
                                    city: 'test1',
                                    country: 'test1',
                                    createdDate: '2015-05-18T13:30:36-04:00',
                                    enableHtmlTag: 1,
                                    id: 9024562,
                                    isHidden: 'N',
                                    modifiedDate: '2015-06-15T13:27:38-04:00',
                                    name: 'test1',
                                    state: 'test1',
                                    url: 'http://www.test1.com'
                                },
                                {
                                    agencyId: 9024559,
                                    createdDate: '2015-06-11T09:13:51-04:00',
                                    enableHtmlTag: 0,
                                    id: 9063840,
                                    isHidden: 'N',
                                    modifiedDate: '2015-06-11T09:14:07-04:00',
                                    name: 'test2',
                                    phoneNumber: '123456789',
                                    url: 'http://www.test2.com'
                                },
                                {
                                    agencyId: 9024559,
                                    createdDate: '2015-06-11T09:13:51-04:00',
                                    enableHtmlTag: 0,
                                    id: 9063841,
                                    isHidden: 'N',
                                    modifiedDate: '2015-06-11T09:13:51-04:00',
                                    name: 'test3',
                                    url: 'http://www.test3.com'
                                }
                            ]
                        }
                    ]
                });
                promise = UserService.getAdvertisers();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        address1: 'test1',
                        agencyId: 9024559,
                        city: 'test1',
                        country: 'test1',
                        createdDate: '2015-05-18T13:30:36-04:00',
                        enableHtmlTag: 1,
                        id: 9024562,
                        isHidden: 'N',
                        modifiedDate: '2015-06-15T13:27:38-04:00',
                        name: 'test1',
                        state: 'test1',
                        url: 'http://www.test1.com'
                    },
                    {
                        agencyId: 9024559,
                        createdDate: '2015-06-11T09:13:51-04:00',
                        enableHtmlTag: 0,
                        id: 9063840,
                        isHidden: 'N',
                        modifiedDate: '2015-06-11T09:14:07-04:00',
                        name: 'test2',
                        phoneNumber: '123456789',
                        url: 'http://www.test2.com'
                    },
                    {
                        agencyId: 9024559,
                        createdDate: '2015-06-11T09:13:51-04:00',
                        enableHtmlTag: 0,
                        id: 9063841,
                        isHidden: 'N',
                        modifiedDate: '2015-06-11T09:13:51-04:00',
                        name: 'test3',
                        url: 'http://www.test3.com'
                    }
                ]);
            });

            it('should wrap single record response in an array', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/advertisers').respond({
                    records: [
                        {
                            Advertiser: {
                                address1: 'test1',
                                agencyId: 9024559,
                                city: 'test1',
                                country: 'test1',
                                createdDate: '2015-05-18T13:30:36-04:00',
                                enableHtmlTag: 1,
                                id: 9024562,
                                isHidden: 'N',
                                modifiedDate: '2015-06-15T13:27:38-04:00',
                                name: 'test1',
                                state: 'test1',
                                url: 'http://www.test1.com'
                            }
                        }
                    ]
                });
                promise = UserService.getAdvertisers();
                $httpBackend.flush();

                expect(promise).toBeResolvedWith([
                    {
                        address1: 'test1',
                        agencyId: 9024559,
                        city: 'test1',
                        country: 'test1',
                        createdDate: '2015-05-18T13:30:36-04:00',
                        enableHtmlTag: 1,
                        id: 9024562,
                        isHidden: 'N',
                        modifiedDate: '2015-06-15T13:27:38-04:00',
                        name: 'test1',
                        state: 'test1',
                        url: 'http://www.test1.com'
                    }
                ]);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/advertisers').respond(404);
                UserService.getAdvertisers();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Users/some@user.com/advertisers').respond(404, 'FAILED');
                promise = UserService.getAdvertisers();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
