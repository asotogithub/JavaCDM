'use strict';

describe('Controller: AdmListController', function () {
    var $httpBackend,
        $q,
        $scope,
        $state,
        model,
        modelMetrics,
        AdmListController,
        AdmService,
        DatasetService,
        UserService,
        vm;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_,
                                $controller,
                                _$q_,
                                $rootScope,
                                _$state_,
                                _AdmService_,
                                _DatasetService_,
                                _UserService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        model = $q.defer();
        modelMetrics = $q.defer();
        AdmService = _AdmService_;
        DatasetService = _DatasetService_;
        UserService = _UserService_;

        spyOn($state, 'go');
        spyOn($state, 'transitionTo');
        spyOn(AdmService, 'getADMDatasetConfigs').andReturn(model.promise);
        spyOn(DatasetService, 'getDatasetMetrics').andReturn(modelMetrics.promise);

        vm = AdmListController = $controller('AdmListController', {
            $scope: $scope,
            $state: $state,
            admService: AdmService,
            datasetService: DatasetService
        });

        installPromiseMatchers();
    }));

    describe('activate()', function () {
        var response,
            responseMetrics;

        beforeEach(function () {
            response = [
                {
                    admTtlSeconds: 10080,
                    advertiserId: 10001,
                    advertiserName: 'Advertiser 01',
                    agencyId: 10001,
                    cookieExpirationDays: 7,
                    datasetId: '7689e9e3-ca41-444d-99e6-9136049d8b1b',
                    domain: 'extdev.adlegend.net',
                    fileNamePrefix: 'ivid',
                    ftpAccount: 'greg',
                    path: ''
                },
                {
                    admTtlSeconds: 10081,
                    advertiserId: 10002,
                    advertiserName: 'Advertiser 01',
                    agencyId: 10001,
                    cookieExpirationDays: 7,
                    datasetId: 'd63f65f9-2202-4a32-8940-c2d9c6e366a0',
                    domain: 'extdev.adlegend.net',
                    fileNamePrefix: 'ivid',
                    ftpAccount: 'greg',
                    path: ''
                }
            ];

            responseMetrics = {
                summary: {
                    cached: 31,
                    engagements: 15270578,
                    matchRate: 0.9999994106313461,
                    matched: 15270569,
                    updated: 15270538
                }
            };
        });

        it('should invoke AdmService.getADMDatasetConfigs()', function () {
            expect(AdmService.getADMDatasetConfigs).toHaveBeenCalledWith();
        });

        it('should set promise', function () {
            expect(AdmListController.promise).toBe(model.promise);
        });

        it('should set model when service call is resolved', function () {
            model.resolve(response);
            modelMetrics.resolve(responseMetrics);
            $scope.$apply();

            expect(AdmListController.model).toEqual(
                [
                    {
                        admTtlSeconds: 10080,
                        advertiserId: 10001,
                        advertiserName: 'Advertiser 01',
                        agencyId: 10001,
                        cookieExpirationDays: 7,
                        datasetId: '7689e9e3-ca41-444d-99e6-9136049d8b1b',
                        domain: 'extdev.adlegend.net',
                        fileNamePrefix: 'ivid',
                        ftpAccount: 'greg',
                        path: '',
                        attempts: 15270578,
                        matchRate: 0.9999994106313461
                    },
                    {
                        admTtlSeconds: 10081,
                        advertiserId: 10002,
                        advertiserName: 'Advertiser 01',
                        agencyId: 10001,
                        cookieExpirationDays: 7,
                        datasetId: 'd63f65f9-2202-4a32-8940-c2d9c6e366a0',
                        domain: 'extdev.adlegend.net',
                        fileNamePrefix: 'ivid',
                        ftpAccount: 'greg',
                        path: '',
                        attempts: 15270578,
                        matchRate: 0.9999994106313461
                    }
                ]);
        });
    });

    it('should go to Details page', function () {
        vm.goToDetails(
            {
                datasetId: 'data-set-id'
            }
        );
        expect($state.go).toHaveBeenCalled();
        expect($state.go).toHaveBeenCalledWith('adm-details', {
            id: 'data-set-id'
        });
    });
});
