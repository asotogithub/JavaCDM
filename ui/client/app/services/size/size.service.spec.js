'use strict';

describe('Service: sizeService', function () {
    var API_SERVICE,
        $httpBackend,
        ErrorRequestHandler,
        SizeService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _SizeService_, $state) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        SizeService = _SizeService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getList()', function () {
        it('should resolve promise with response.records[0].Size', function () {
            var promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 1,
                    records: [
                        {
                            Size: [
                                {
                                    agencyId: 6031295,
                                    createdDate: '2015-07-30T18:41:19-04:00',
                                    height: 150,
                                    id: 5006631,
                                    label: '180x150',
                                    modifiedDate: '2015-07-30T18:41:19-04:00',
                                    width: 180
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.whenGET(API_SERVICE + 'Sizes').respond(response);
            promise = SizeService.getList();
            $httpBackend.flush();

            expect(promise).toBeResolved();
            expect(promise).toBeResolvedWith(response.records.Size);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Sizes').respond(404);
                SizeService.getList();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Sizes').respond(404, 'FAILED');
                promise = SizeService.getList();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
