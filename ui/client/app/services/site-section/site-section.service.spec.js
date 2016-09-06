'use strict';

describe('Service: SiteSectionService', function () {
    var API_SERVICE,
        $httpBackend,
        ErrorRequestHandler,
        SiteSectionService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($state, _$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _SiteSectionService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        API_SERVICE = _API_SERVICE_;
        $httpBackend = _$httpBackend_;
        SiteSectionService = _SiteSectionService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getSiteSection()', function () {
        it('should resolve promise with site section', function () {
            var promise,
                expected = {
                    createdDate: '2015-07-06T10:11:58-06:00',
                    createdTpwsKey: '9330ea1c-2e69-46e1-b668-f0e4f3675c1b',
                    id: 9107199,
                    modifiedDate: '2015-07-06T10:11:58-06:00',
                    modifiedTpwsKey: '9330ea1c-2e69-46e1-b668-f0e4f3675c1b',
                    name: 'Homepage',
                    siteId: 9107198
                };

            $httpBackend.expectGET(API_SERVICE + 'SiteSections/9107199').respond(expected);
            promise = SiteSectionService.getSiteSection(9107199);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(expected);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteSections/9107199').respond(404);
                SiteSectionService.getSiteSection(9107199);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'SiteSections/9107199').respond(404, 'FAILED');
                promise = SiteSectionService.getSiteSection(9107199);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
