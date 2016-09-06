'use strict';

describe('Controller: AdmDetailsController', function () {
    var API_SERVICE,
        CONSTANTS,
        $controller,
        $httpBackend,
        $q,
        $scope,
        $stateParams,
        admMetrics,
        AdmDetailsController,
        DatasetService,
        datasetId;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_,
                                _$q_,
                                _$controller_,
                                _$rootScope_,
                                _$stateParams_,
                                _API_SERVICE_,
                                _DatasetService_,
                                _CONSTANTS_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        CONSTANTS = _CONSTANTS_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = _$rootScope_.$new();
        $stateParams = _$stateParams_;
        datasetId = $stateParams.id = 'data001';
        DatasetService = _DatasetService_;
        admMetrics = $q.defer();
        spyOn(DatasetService, 'getDatasetMetrics').andReturn(admMetrics.promise);
        AdmDetailsController = $controller('AdmDetailsController', {
            $scope: $scope,
            $stateParams: $stateParams,
            DatasetService: DatasetService,
            CONSTANTS: CONSTANTS
        });
        installPromiseMatchers();
    }));

    it('should resolve metrics data', function () {
        var admMetricsObject = {
                dates: [
                    {
                        date: [
                            {
                                cached: 0,
                                engagements: 1,
                                matchRate: 1.0,
                                matched: 1,
                                updated: 1,
                                date: '2015-12-08T00:00:00-07:00'
                            },
                            {
                                cached: 4,
                                engagements: 13,
                                matchRate: 1.0,
                                matched: 13,
                                updated: 9,
                                date: '2015-12-11T00:00:00-07:00'
                            }
                        ]
                    }
                ],
                summary: {
                    cached: 4,
                    engagements: 14,
                    matchRate: 1.0,
                    matched: 14,
                    updated: 10
                }
            };

        admMetrics.resolve(admMetricsObject);

        $scope.$apply();
        expect(admMetricsObject).toEqual(AdmDetailsController.admMetrics);
    });
});
