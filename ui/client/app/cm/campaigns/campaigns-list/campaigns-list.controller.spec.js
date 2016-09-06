'use strict';

describe('Controller: CampaignsListController', function () {
    var $httpBackend,
        $q,
        $scope,
        $state,
        campaigns,
        CampaignsListController,
        CampaignsService,
        metrics;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, _$q_, $rootScope, _CampaignsService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = jasmine.createSpyObj('$state', ['go']);
        campaigns = $q.defer();
        metrics = $q.defer();

        CampaignsService = _CampaignsService_;
        spyOn(CampaignsService, 'getList').andReturn(campaigns.promise);
        spyOn(CampaignsService, 'getListMetrics').andReturn(metrics.promise);

        CampaignsListController = $controller('CampaignsListController', {
            $state: $state,
            $scope: $scope,
            CampaignsService: CampaignsService
        });
        installPromiseMatchers();
    }));

    it('should initialize campaigns as null', function () {
        expect(CampaignsListController.campaigns).toBeNull();
    });

    it('should initialize metrics as null', function () {
        expect(CampaignsListController.metrics).toBeNull();
    });

    it('should invoke CampaignService.getList()', function () {
        expect(CampaignsService.getList).toHaveBeenCalled();
    });

    it('should invoke CampaignService.getListMetrics()', function () {
        expect(CampaignsService.getListMetrics).toHaveBeenCalled();
    });

    it('should set promise for CampaignsListController', function () {
        expect(CampaignsListController.promise).not.toBeNull();
        expect(CampaignsListController.promise).not.toBeResolved();
    });

    it('should not resolve promise until all service calls have been resolved', function () {
        var _campaigns = [
            {
                name: 'b'
            },
            {
                name: 'c'
            },
            {
                name: 'a'
            }
            ],
            _metrics = [
            {
                name: 'b'
            },
            {
                name: 'c'
            },
            {
                name: 'a'
            }
        ];

        campaigns.resolve(_campaigns);
        expect(CampaignsListController.promise).not.toBeResolved();
        metrics.resolve(_metrics);

        expect(CampaignsListController.promise).toBeResolved();
    });

    it('should correlate metric and campaign data when promises are resolved',
        function () {
            var _campaigns = [
                    {
                        id: 123,
                        name: 'b'
                    }
                ],
                _metrics = [
                    {
                        id: 123,
                        clicks: 50,
                        conversions: 25,
                        cost: 10.0,
                        day: '2015-07-20T00:00:00-06:00',
                        impressions: 400
                    },
                    {
                        id: 123,
                        clicks: 100,
                        conversions: 25,
                        cost: 10.0,
                        day: '2015-07-21T00:00:00-06:00',
                        impressions: 400
                    },
                    {
                        id: 400, // ignored
                        clicks: 1,
                        conversions: 1,
                        cost: 10.0,
                        day: '2015-07-21T00:00:00-06:00',
                        impressions: 10
                    }
                ],
                metricsExpected = [
                    {
                        key: 'Impressions', value: 810, icon: 'fa-eye', type: 'number'
                    }, {
                        key: 'CTR', value: 0.18641975308641975, icon: 'fa-mouse-pointer', type: 'percentage'
                    }, {
                        key: 'Cost', value: 30, icon: 'fa-usd', type: 'currency'
                    }, {
                        key: 'Conversions', value: 51, icon: 'fa-exchange', type: 'number'
                    }, {
                        key: 'eCPA', value: 1.7, icon: 'fa-usd', type: 'currency'
                    }
                ],
                campaignsExpected = [
                    {
                        id: 123, name: 'b', eCpa: 2.5, cost: 20, conversions: 50
                    }
                ];

            metrics.resolve(_metrics);
            campaigns.resolve(_campaigns);
            $scope.$apply();

            expect(campaignsExpected).toEqual(CampaignsListController.campaigns);
            expect(metricsExpected).toEqual(CampaignsListController.metrics);
        });

    it('should configure chart series data using metrics data',
        function () {
            var _campaigns = [
                    // blank
                ],
                _metrics = [
                    {
                        id: 123,
                        clicks: 50,
                        conversions: 25,
                        cost: 10.0,
                        day: '2015-07-20T00:00:00-06:00',
                        impressions: 400
                    },
                    {
                        id: 123,
                        clicks: 100,
                        conversions: 25,
                        cost: 10.0,
                        day: '2015-07-21T00:00:00-06:00',
                        impressions: 400
                    },
                    {
                        id: 400, // ignored
                        clicks: 1,
                        conversions: 1,
                        cost: 10.0,
                        day: '2015-07-21T00:00:00-06:00',
                        impressions: 10
                    }
                ],
                xAxisExpected = [
                    new Date('Tue Jul 20 2015 00:00:00 GMT-0600 (MDT)'),
                    new Date('Mon Jul 21 2015 00:00:00 GMT-0600 (MDT)')
                ],
                seriesExpected = [
                    {
                        name: 'Impressions', type: 'spline', color: '#0F5880', data: [400, 410]
                    }, {
                        name: 'Conversions', type: 'spline', color: '#1C9AD6', yAxis: 1, data: [25, 26]
                    }
                ];

            metrics.resolve(_metrics);
            campaigns.resolve(_campaigns);
            $scope.$apply();

            expect(xAxisExpected).toEqual(CampaignsListController.chartConfig.xAxis[0].categories);
            expect(seriesExpected).toEqual(CampaignsListController.chartConfig.series);
        });
});

