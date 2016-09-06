'use strict';

describe('Service: PackageService', function () {
    var $httpBackend,
        $q,
        API_SERVICE,
        ErrorRequestHandler,
        PackageService,
        placementsArray;

    beforeEach(module('uiApp'));

    beforeEach(
        inject(function ($state,
                         _$httpBackend_,
                         _$q_,
                         _API_SERVICE_,
                         _ErrorRequestHandler_,
                         _PackageService_) {
            ErrorRequestHandler = _ErrorRequestHandler_;
            spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
            $httpBackend = _$httpBackend_;
            $q = _$q_;
            API_SERVICE = _API_SERVICE_;
            PackageService = _PackageService_;
            placementsArray = [
                {
                    adSpend: 0,
                    endDate: '2015-08-08T23:59:59-07:00',
                    height: 100,
                    id: 9110480,
                    inventory: 1,
                    name: 'sport - Header - 100x100',
                    rate: 0,
                    rateType: 'CPM',
                    siteName: 'sport',
                    startDate: '2015-07-09T00:00:00-07:00',
                    status: 'Accepted',
                    width: 100
                },
                {
                    adSpend: 0,
                    endDate: '2015-08-08T23:59:59-07:00',
                    height: 150,
                    id: 9110481,
                    inventory: 1,
                    name: 'AOL - Header - 150x150',
                    rate: 0,
                    rateType: 'CPM',
                    siteName: 'sport',
                    startDate: '2015-07-09T00:00:00-07:00',
                    status: 'Accepted',
                    width: 150
                }
            ];

            spyOn($state, 'go');

            installPromiseMatchers();
        }));

    describe('updatePackage()', function () {
        it('should update the package', function () {
            var promise;

            placementsArray[0].name = 'updatedPlacement - 01';
            placementsArray[0].status = 'Rejected';
            placementsArray[1].name = 'updatedPlacement - 02';
            placementsArray[1].status = 'Rejected';

            $httpBackend.expectPUT(API_SERVICE + 'Packages/9110478', placementsArray).respond({});

            promise = PackageService.updatePackage(9110478, placementsArray);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenPUT(API_SERVICE + 'Packages/9110478', placementsArray).respond(404);
                PackageService.updatePackage(9110478, placementsArray);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenPUT(API_SERVICE + 'Packages/9110478', placementsArray).respond(404, 'FAILED');
                promise = PackageService.updatePackage(9110478, placementsArray);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
