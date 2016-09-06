'use strict';

describe('Service: TraffickingService', function () {
    var $httpBackend,
        API_SERVICE,
        TraffickingService;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
        _$httpBackend_,
        $state,
        _API_SERVICE_,
        _ErrorRequestHandler_,
        _TraffickingService_) {
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        TraffickingService = _TraffickingService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('validateCampaign()', function () {
        it('should verify if a campaign is valid for trafficking', function () {
            var promise,
                response = {
                    message: 'Campaign 1090893 successfully validated.'
                };

            $httpBackend.whenGET(API_SERVICE + 'Trafficking/validate?campaignId=1090893').respond(response);
            promise = TraffickingService.validateCampaign(1090893);
            $httpBackend.flush();
            expect(promise).toBeResolvedWith(response);
        });
    });

    describe('on failure', function () {
        it('should reject promise', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'Trafficking/validate?campaignId=1090893').respond(404, 'FAILED');
            promise = TraffickingService.validateCampaign(1090893);
            $httpBackend.flush();

            expect(promise).toBeRejectedWith(jasmine.objectContaining({
                data: 'FAILED',
                status: 404
            }));
        });
    });
});
