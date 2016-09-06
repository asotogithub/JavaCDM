'use strict';

describe('Service: Advertiser', function () {
    var API_SERVICE,
        BRANDS_FROM_API,
        $httpBackend,
        $q,
        AdvertiserService,
        ErrorRequestHandler;

    beforeEach(module('uiApp'));

    beforeEach(function () {
        BRANDS_FROM_API = {
            records: [
                {
                    Brand: [
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
                        }
                    ]
                }
            ]
        };
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _AdvertiserService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        AdvertiserService = _AdvertiserService_;
        installPromiseMatchers();
    }));

    describe('getList()', function () {
        describe('on success', function () {
            it('should get all advertisers', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Advertisers/9071473/brands').respond(BRANDS_FROM_API);
                promise = AdvertiserService.getAdvertiserBrands(9071473);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(BRANDS_FROM_API.records[0].Brand);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Advertisers/9071473/brands').respond(404);
                AdvertiserService.getAdvertiserBrands(9071473);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Advertisers/9071473/brands').respond(404, 'FAILED');
                promise = AdvertiserService.getAdvertiserBrands(9071473);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
