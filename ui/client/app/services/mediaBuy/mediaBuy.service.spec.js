'use strict';

describe('Service: MediaBuyService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        MediaBuyService,
        ErrorRequestHandler;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _MediaBuyService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        MediaBuyService = _MediaBuyService_;
        installPromiseMatchers();
    }));

    describe('getMediaBuyByCampaign()', function () {
        describe('on success', function () {
            it('should resolve promise with campaign', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'MediaBuys/byCampaign/1337').respond({
                    id: '1337',
                    name: 'Some name'
                });
                promise = MediaBuyService.getMediaBuyByCampaign(1337);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith({
                    id: '1337',
                    name: 'Some name'
                });
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'MediaBuys/byCampaign/69').respond(404);
                MediaBuyService.getMediaBuyByCampaign(69);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'MediaBuys/byCampaign/42').respond(404, 'FAILED');
                promise = MediaBuyService.getMediaBuyByCampaign(42);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
