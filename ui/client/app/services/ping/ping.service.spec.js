'use strict';

describe('Service: Ping', function () {
    var API_SERVICE,
       PAYLOAD,
       $httpBackend,
       $q,
       ErrorRequestHandler,
       PingService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
        _$httpBackend_,
        _$q_,
        _API_SERVICE_,
        _ErrorRequestHandler_,
        _PingService_) {
        PAYLOAD = {
            type: 'smPingEventDTO',
            id: 6803433,
            pingId: 0,
            pingContent: 'http://ad.adlegend.com',
            description: 'Update Ping',
            siteId: 896955,
            pingType: 1,
            pingTagType: 2,
            smGroupId: 5466575
        };

        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        PingService = _PingService_;
        installPromiseMatchers();
    }));

    describe('Save Ping', function () {
        describe('on success', function () {
            it('should save an Ping', function () {
                var promise;

                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementEventPings/bulkCreate').respond(200);
                promise = PingService.save([PAYLOAD]);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should reject promise', function () {
                var promise;

                $httpBackend.whenPOST(API_SERVICE + 'SiteMeasurementEventPings/bulkCreate').respond(404, 'FAILED');
                promise = PingService.save([]);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('update Ping', function () {
        describe('on success', function () {
            it('should update ping', function () {
                var promise;

                PAYLOAD.pingId = 89797;
                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEventPings/bulkUpdate').respond(200);
                promise = PingService.update([PAYLOAD]);
                $httpBackend.flush();

                expect(promise).toBeResolved();
            });
        });

        describe('on failure', function () {
            it('should reject promise', function () {
                var promise;

                $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEventPings/bulkUpdate').respond(404, 'FAILED');
                promise = PingService.update([]);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('delete ping', function () {
        it('on success', function () {
            var promise;

            $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEventPings/deletePingEvents').respond(200);
            promise = PingService.delete(9110478);
            $httpBackend.flush();
            expect(promise).toBeResolved();
        });

        it('on failure', function () {
            var promise;

            $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEventPings/deletePingEvents').respond(404, 'FAILED');
            promise = PingService.delete([]);
            $httpBackend.flush();

            expect(promise).toBeRejectedWith(jasmine.objectContaining({
                data: 'FAILED',
                status: 404
            }));
        });

        it('should show generic error dialog', function () {
            $httpBackend.whenPUT(API_SERVICE + 'SiteMeasurementEventPings/deletePingEvents').respond(404);
            PingService.delete([]);
            $httpBackend.flush();

            expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
        });
    });
});
