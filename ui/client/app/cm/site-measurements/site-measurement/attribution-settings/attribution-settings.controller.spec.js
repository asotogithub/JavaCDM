'use strict';

describe('Controller: SiteMeasurementAttributionSettingsController', function () {
    var $httpBackend,
        $scope,
        $state,
        $stateParams,
        SiteMeasurementsService,
        controller,
        mockData,
        mockDataUpdated,
        mockModelService;

    beforeEach(function () {
        $stateParams = {};
        mockModelService = {};
    });

    beforeEach(function () {
        module('uiApp', function ($provide) {
            SiteMeasurementsService = jasmine.createSpyObj('SiteMeasurementsService',
                ['getSiteMeasurements', 'updateSiteMeasurement']);
            $provide.value('SiteMeasurementsService', SiteMeasurementsService);
        });

        inject(function ($q) {
            mockData =
            {
                advertiserId: 9024562,
                advertiserName: 'UI QA Advertiser TEST',
                brandId: 9024563,
                brandName: 'QA UITEST Brand TEST - 01',
                clWindow: 0,
                cookieDomainId: 9034120,
                createdDate: '2015-06-09T09:44:37-04:00',
                createdTpwsKey: '86b53888-2e92-4fa8-a91e-db3a6b6a46cf',
                domain: 'ad.adlegend.com',
                dupName: 'QA TESTING',
                expirationDate: '2017-06-09T12:44:05-04:00',
                id: 9060737,
                logicalDelete: 'N',
                modifiedDate: '2015-06-11T09:22:24-04:00',
                modifiedTpwsKey: '97b52c7a-5243-4902-96e5-69f44fd2e12f',
                name: 'QA TESTing',
                siteId: -1,
                state: 3,
                ttVer: 2,
                vtWindow: 0
            };

            mockDataUpdated = mockData;

            var deferGet = $q.defer();

            deferGet.resolve(mockData);

            SiteMeasurementsService.getSiteMeasurements.andReturn(deferGet.promise);

            SiteMeasurementsService.updateSiteMeasurement.andCallFake(function (params, object) {
                var defer = $q.defer();

                defer.resolve(object);
                return defer.promise;
            });
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$httpBackend_, $rootScope, _$state_, CONSTANTS, SiteMeasurementModel) {
        $rootScope.vm = {
            selectTab: function () {}
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        $httpBackend = _$httpBackend_;
        $state.current = {
            name: 'sm-attibution-settings'
        };
        $stateParams.siteMeasurementId = 9060737;
        controller = $controller('SiteMeasurementAttributionSettingsController', {
            $scope: $scope,
            $state: $state,
            $stateParams: $stateParams,
            CONSTANTS: CONSTANTS,
            SiteMeasurementModel: SiteMeasurementModel
        });

        $httpBackend.whenGET('app/cm/main/main.html').respond('');
        $scope.$digest();
    }));

    it('Should initialize the controller', function () {
        expect(controller).toBeDefined();
    });

    it('Should initialize the model for the attribution settings', function () {
        expect(controller.model.siteMeasurement).toBeDefined();
    });

    it('Should handle the assocMethod as a data for UI', function () {
        expect(controller.model.siteMeasurement.assocMethodView).toBeDefined();
    });

    it('Should update and save the attribution settings', function () {
        mockDataUpdated.assocMethod = 'FIRST';
        $httpBackend.whenGET('app/cm/login/login.html').respond('');
        expect(controller.put(mockDataUpdated)).toBeResolved();
    });
});
