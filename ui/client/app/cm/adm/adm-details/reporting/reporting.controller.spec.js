'use strict';

describe('Controller: AdmReporting', function () {
    var $httpBackend,
        $scope,
        controller,
        AdmReportingController,
        DateTimeService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, $rootScope, _DateTimeService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope.$new();
        controller = $controller;
        DateTimeService = _DateTimeService_;
        AdmReportingController = $controller('AdmReporting', {
            $scope: $scope,
            DateTimeService: DateTimeService
        });
        installPromiseMatchers();
    }));

    it('should configure chart series data using metrics data', function () {
        var metrics = {
                dates: [
                    {
                        date: [
                            {
                                cached: 0,
                                engagements: 1,
                                matchRate: 0.923,
                                matched: 1,
                                updated: 1,
                                date: '2015-12-08T00:00:00-07:00'
                            },
                            {
                                cached: 4,
                                engagements: 13,
                                matchRate: 0.323,
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
            },
            xAxisExpected = [
                DateTimeService.parse('2015-12-08T00:00:00-07:00'),
                DateTimeService.parse('2015-12-11T00:00:00-07:00')
            ],
            seriesExpected = [
                {
                    name: 'Engagements',
                    type: 'column',
                    color: '#0F5880',
                    data: [
                        1,
                        13
                    ]
                },
                {
                    name: 'Match Rate',
                    type: 'line',
                    color: '#1C9AD6',
                    yAxis: 1,
                    data: [
                        92,
                        32
                    ],
                    tooltip: {
                        valueSuffix: '%'
                    }
                }
            ];

        $scope.$broadcast('adm.dataset.metrics', {
            metrics: metrics
        });

        expect(xAxisExpected).toEqual(AdmReportingController.chartConfig.xAxis[0].categories);
        expect(seriesExpected).toEqual(AdmReportingController.chartConfig.series);
        expect(AdmReportingController.metrics).toBe(metrics);
        expect(AdmReportingController.validMetrics).toBe(true);
    });
});
