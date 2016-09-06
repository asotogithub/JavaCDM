'use strict';

describe('Controller: CampaignDetailsTabController', function () {
    var $filter,
        $httpBackend,
        $q,
        $scope,
        CampaignDetailsTabController,
        CampaignsService,
        DialogFactory,
        UserService,
        campaignId,
        campaignDetails,
        metrics,
        domains,
        DateTimeService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_,
                                $controller,
                                _$q_,
                                $rootScope,
                                _$filter_,
                                _CampaignsService_,
                                _UserService_,
                                _DateTimeService_) {
        $filter = _$filter_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        CampaignsService = _CampaignsService_;
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog']);
        UserService = _UserService_;
        campaignId = $scope.campaignId = 1337;
        campaignDetails = $q.defer();
        domains = $q.defer();
        metrics = $q.defer();
        DateTimeService = _DateTimeService_;

        spyOn(CampaignsService, 'getCampaignDetails').andReturn(campaignDetails.promise);
        spyOn(CampaignsService, 'getCampaignMetrics').andReturn(metrics.promise);
        spyOn(CampaignsService, 'updateCampaignDetails').andCallThrough();
        spyOn(CampaignsService, 'reduceMetricsData').andCallThrough();
        spyOn(UserService, 'getDomains').andReturn(domains.promise);
        CampaignDetailsTabController = $controller('CampaignDetailsTabController', {
            $scope: $scope,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            UserService: UserService,
            DateTimeService: DateTimeService
        });

        installPromiseMatchers();

        it('should invoke CampaignsService.getCampaignDetails()', function () {
            expect(CampaignsService.getCampaignDetails).toHaveBeenCalledWith(campaignId);
        });

        it('should invoke UserService.getDomains()', function () {
            expect(UserService.getDomains).toHaveBeenCalled();
        });

        it('should set promise on vm', function () {
            expect(CampaignDetailsTabController.promise).not.toBeResolved();
        });

        describe('when service calls are resolved', function () {
            it('should extend vm with campaignDetails', function () {
                campaignDetails.resolve({
                    campaign: {
                        id: '1063972',
                        cookieDomainId: '69',
                        startDate: '2015-06-28T07:00:00-07:00',
                        endDate: '2015-06-29T07:00:00-07:00'
                    },
                    brand: {
                        id: '9036439'
                    },
                    advertiser: {
                        id: '9036415'
                    }
                });
                metrics.resolve([
                    {
                        clicks: 98,
                        conversions: 0,
                        cost: 398.409,
                        ctr: 2.459771E-4,
                        day: '2015-07-04T07:00:00-07:00',
                        id: 123,
                        impressions: 398411,
                        eCPA: 0.0
                    }, {
                        clicks: 106,
                        conversions: 0,
                        cost: 407.74,
                        ctr: 2.599632E-4,
                        day: '2015-07-05T07:00:00-07:00',
                        id: 123,
                        impressions: 407750,
                        eCPA: 0.0
                    }
                ]);

                $scope.$digest();

                expect(CampaignDetailsTabController.promise).not.toBeResolved();

                domains.resolve([
                    {
                        id: '69',
                        domain: 'foo.com'
                    },
                    {
                        id: '42',
                        domain: 'bar.com'
                    }
                ]);
                $scope.$digest();

                expect(CampaignDetailsTabController.promise).toBeResolved();

                expect(
                    _.pick(
                        CampaignDetailsTabController,
                        ['campaign', 'brand', 'advertiser', 'selectedDomain', 'domains']
                    )
                ).toEqual({
                        campaign: {
                            id: '1063972',
                            cookieDomainId: '69',
                            startDate: DateTimeService.parse('2015-06-28T07:00:00-07:00'),
                            endDate: DateTimeService.parse('2015-06-29T07:00:00-07:00')
                        },
                        brand: {
                            id: '9036439'
                        },
                        advertiser: {
                            id: '9036415'
                        },
                        selectedDomain: {
                            id: '69',
                            domain: 'foo.com'
                        },
                        domains: [
                            {
                                id: '69',
                                domain: 'foo.com'
                            },
                            {
                                id: '42',
                                domain: 'bar.com'
                            }
                        ]
                    });

                expect(CampaignDetailsTabController.chartConfig.series).toEqual(
                    [
                        {
                            name: 'Impressions', type: 'spline', color: '#000000', data: [398411, 407750]
                        },
                        {
                            name: 'Conversions', type: 'spline', color: '#7cb5ec', yAxis: 1, data: [0, 0]
                        }
                    ]);
            });

            it('should create domain if selectedDomain is not returned from UserService', function () {
                campaignDetails.resolve({
                    campaign: {
                        id: '1063972',
                        cookieDomainId: '1337',
                        domain: 'foo.bar.com',
                        startDate: '2015-06-28T00:00:00-07:00',
                        endDate: '2015-06-29T00:00:00-07:00'
                    },
                    brand: {
                        id: '9036439'
                    },
                    advertiser: {
                        id: '9036415'
                    }
                });
                metrics.resolve([]);
                domains.resolve([
                    {
                        id: '69',
                        domain: 'foo.com'
                    },
                    {
                        id: '42',
                        domain: 'bar.com'
                    }
                ]);
                $scope.$digest();

                expect(
                    _.pick(
                        CampaignDetailsTabController,
                        ['campaign', 'brand', 'advertiser', 'selectedDomain', 'domains']
                    )
                ).toEqual({
                        campaign: {
                            id: '1063972',
                            cookieDomainId: '1337',
                            domain: 'foo.bar.com',
                            startDate: DateTimeService.parse('2015-06-28T00:00:00-07:00'),
                            endDate: DateTimeService.parse('2015-06-29T00:00:00-07:00')
                        },
                        brand: {
                            id: '9036439'
                        },
                        advertiser: {
                            id: '9036415'
                        },
                        selectedDomain: {
                            id: '1337',
                            domain: 'foo.bar.com'
                        },
                        domains: [
                            {
                                id: '1337',
                                domain: 'foo.bar.com'
                            },
                            {
                                id: '69',
                                domain: 'foo.com'
                            },
                            {
                                id: '42',
                                domain: 'bar.com'
                            }
                        ]
                    });
            });
        });

        describe('when service calls are rejected', function () {
            it('should show generic error dialog for failure from CampaignsService', inject(function (CONSTANTS) {
                campaignDetails.reject();
                $scope.$digest();

                expect(DialogFactory.showDialog).toHaveBeenCalledWith(CONSTANTS.DIALOG.TYPE.ERROR);
            }));

            it('should show generic error dialog for failure from UserService', inject(function (CONSTANTS) {
                domains.reject();
                $scope.$digest();

                expect(DialogFactory.showDialog).toHaveBeenCalledWith(CONSTANTS.DIALOG.TYPE.ERROR);
            }));
        });

        describe('submit()', function () {
            var deferred;

            beforeEach(inject(function () {
                deferred = $q.defer();
                CampaignsService.updateCampaignDetails.andReturn(deferred.promise);
            }));

            CampaignDetailsTabController.campaign = {
                id: '1063972',
                cookieDomainId: '69',
                startDate: DateTimeService.inverseParse(new Date('2015-06-28T07:00:00-07:00')),
                endDate: DateTimeService.inverseParse(new Date('2015-06-29T07:00:00-07:00'))
            };

            it('should invoke CampaignsService.updateCampaignDetails()', function () {
                angular.extend(CampaignDetailsTabController, {
                    selectedDomain: {
                        id: '1'
                    },
                    campaign: {
                        advertiserId: '2',
                        agencyId: '3',
                        brandId: '4',
                        description: 'Some description.',
                        endDate: '2015-09-16T00:00:00-07:00',
                        id: '5',
                        isActive: 'Y',
                        logicalDelete: 'N',
                        name: 'Some name',
                        startDate: '2015-09-16T00:00:00-07:00',
                        statusId: '1'
                    }
                });

                CampaignDetailsTabController.submit();

                expect(CampaignsService.updateCampaignDetails).toHaveBeenCalledWith({
                    cookieDomainId: '1',
                    advertiserId: '2',
                    agencyId: '3',
                    brandId: '4',
                    description: 'Some description.',
                    endDate: '2015-09-16T00:00:00-07:00',
                    id: '5',
                    isActive: 'Y',
                    logicalDelete: 'N',
                    name: 'Some name',
                    startDate: '2015-09-16T00:00:00-07:00',
                    statusId: '1'
                });
                expect(CampaignDetailsTabController.promise).toBe(deferred.promise);
            });

            describe('on success', function () {
                var campaignDetailsForm;

                beforeEach(function () {
                    campaignDetailsForm = CampaignDetailsTabController.campaignDetailsForm = jasmine.createSpyObj(
                        'campaignDetailsForm',
                        ['$setPristine']);

                    CampaignDetailsTabController.submit();
                });

                it('should set $scope.campaignDetailsForm as pristine', function () {
                    deferred.resolve({});
                    $scope.$digest();

                    expect(campaignDetailsForm.$setPristine).toHaveBeenCalled();
                });

                it('should clear errorMessage from vm', function () {
                    CampaignDetailsTabController.errorMessage = 'Some error message.';
                    deferred.resolve({});
                    $scope.$digest();
                    expect(CampaignDetailsTabController.errorMessage).toBeNull();
                });
            });

            describe('on failure', function () {
                beforeEach(function () {
                    CampaignDetailsTabController.submit();
                });

                it('should set errorMessage on vm', function () {
                    deferred.reject({
                        data: {
                            errors: [
                                {
                                    message: 'Some error message.'
                                }
                            ]
                        }
                    });
                    $scope.$digest();

                    expect(CampaignDetailsTabController.errorMessage).toEqual('Some error message.');
                });

                it('should show generic error dialog if no error message is available', inject(function (CONSTANTS) {
                    deferred.reject({});
                    $scope.$digest();

                    expect(DialogFactory.showDialog).toHaveBeenCalledWith(CONSTANTS.DIALOG.TYPE.ERROR);
                }));
            });
        });
    }));
});
