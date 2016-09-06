'use strict';

describe('Service: DatasetService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        DatasetService,
        ErrorRequestHandler;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _DatasetService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        DatasetService = _DatasetService_;
        installPromiseMatchers();
    }));

    describe('getDatasetMetrics()', function () {
        describe('on success', function () {
            it('should resolve promise with dataObject', function () {
                var promise,
                    dataObject = {
                        DatasetMetrics: {
                            summary: {
                                engagements: 619,
                                cached: 59,
                                matched: 560,
                                updated: 59,
                                matchRate: 0.9046849757673667
                            },
                            dates: [
                                {
                                    date: '2015-11-16T00:00:00-07:00',
                                    engagements: 90,
                                    cached: 58,
                                    matched: 32,
                                    updated: 58,
                                    matchRate: 0.35555555555555557
                                },
                                {
                                    date: '2015-11-17T00:00:00-07:00',
                                    engagements: 529,
                                    cached: 1,
                                    matched: 528,
                                    updated: 1,
                                    matchRate: 0.998109640831758
                                },
                                {
                                    date: '2015-11-18T00:00:00-07:00',
                                    engagements: 400,
                                    cached: 1,
                                    matched: 528,
                                    updated: 1,
                                    matchRate: 0.298109640831758
                                },
                                {
                                    date: '2015-11-19T00:00:00-07:00',
                                    engagements: 180,
                                    cached: 1,
                                    matched: 528,
                                    updated: 1,
                                    matchRate: 0.658109640831758
                                }
                            ]
                        }
                    };

                $httpBackend.whenGET(API_SERVICE + 'Datasets/758/metrics').respond(dataObject);
                promise = DatasetService.getDatasetMetrics(758);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(dataObject);
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE +
                    'Datasets/758/metrics?endDate=10%2F10%2F2017&startDate=10%2F10%2F2016').respond(404);
                DatasetService.getDatasetMetrics(758, '10/10/2016', '10/10/2017', {
                    showError: true
                });
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE +
                    'Datasets/758/metrics?endDate=10%2F10%2F2017&startDate=10%2F10%2F2016').respond(404, 'FAILED');
                promise = DatasetService.getDatasetMetrics(758, '10/10/2016', '10/10/2017', {
                    showError: true
                });
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
