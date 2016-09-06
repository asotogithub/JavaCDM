'use strict';

describe('Controller: SiteMeasurementDetailsController', function () {
    var $scope,
        $state,
        SiteMeasurementModel,
        SiteMeasurementsService,
        controller,
        modelPromise,
        mockModelService,
        selectedStatus,
        siteMeasurementData;

    beforeEach(function () {
        mockModelService = {};

        siteMeasurementData = {
            advertiserId: 6484890,
            advertiserName: 'API QA TOYOTA',
            assocMethod: 'FIRST',
            brandId: 6484896,
            brandName: 'API QA GT',
            cookieDomainId: 6150332,
            createdDate: '2016-07-25T16:50:13-07:00',
            domain: 'ad.adlegend.com',
            expirationDate: '2026-07-26T11:02:46-07:00',
            id: 7423434,
            name: 'SM Testing 3a',
            notes: 'hola'
        };

        selectedStatus = {
            key: true,
            name: 'Active'
        };
    });

    beforeEach(function () {
        module('uiApp', function ($provide) {
            $provide.value('SiteMeasurementModel', mockModelService);
            SiteMeasurementsService = jasmine.createSpyObj('SiteMeasurementsService', [
                                                           'getSiteMeasurements',
                                                           'updateSiteMeasurement'
                                                          ]);
            $provide.value('SiteMeasurementsService', SiteMeasurementsService);
        });

        inject(function ($q) {
            var mockRetrievedObject = {
                    assocMethod: 'AssocMethod0',
                    brandId: '0',
                    clWindow: '0',
                    cookieDomainId: '0',
                    createdDate: '2015-06-03T09:58:34.539-04:00',
                    createdTpwsKey: '000000',
                    dupName: 'DupName0',
                    expirationDate: '2015-06-03T09:58:34.539-04:00',
                    id: '44',
                    logicalDelete: 'N',
                    modifiedDate: '2015-06-03T09:58:34.539-04:00',
                    modifiedTpwsKey: '000000',
                    name: 'Name0',
                    notes: 'Notes0',
                    resourcePathId: '0',
                    siteId: '0',
                    state: '2',
                    ttVer: '0',
                    vtWindow: '0',
                    advertiserId: '0',
                    advertiserName: 'AdvertiserName0',
                    brandName: 'BrandName0',
                    domain: 'Domain0'
                },
                deferGet = $q.defer(),
                deferUpdate = $q.defer();

            deferGet.resolve(mockRetrievedObject);

            SiteMeasurementsService.getSiteMeasurements.andReturn(deferGet.promise);
            SiteMeasurementsService.updateSiteMeasurement.andReturn(deferUpdate.promise);

            mockModelService.tabs = [
                {
                    key: 'details',
                    heading: 'global.summary',
                    active: false,
                    disabled: false,
                    state: 'site-measurement.details'
                },
                {
                    key: 'events',
                    heading: 'global.eventsPings',
                    active: false,
                    disabled: false,
                    state: 'site-measurement.events'
                },
                {
                    key: 'attribution-settings',
                    heading: 'siteMeasurement.attributionSettings',
                    active: false,
                    disabled: true,
                    state: ''
                },
                {
                    key: 'campaign-associations',
                    heading: 'siteMeasurement.campaignAssociations',
                    active: false,
                    disabled: true,
                    state: ''
                }
            ];

            mockModelService.selectTab = function () {
                var tab = this.tabs[0];

                tab.active = true;
                $state.go(tab.state);
            };

            mockModelService.setSiteMeasurementId = function () {
                this.siteMeasurementId = 44;
            };

            mockModelService.getSiteMeasurement = function () {
                this.siteMeasurement = SiteMeasurementsService.getSiteMeasurements();
            };
        });
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                _$state_,
                                _SiteMeasurementsService_,
                                _SiteMeasurementModel_) {
        $rootScope.vm = {
            selectTab: function () {}
        };

        $scope = $rootScope.$new();
        $state = _$state_;
        $state.current = {
            name: 'site-measurement.details'
        };
        SiteMeasurementModel = _SiteMeasurementModel_;
        modelPromise = $q.defer();

        spyOn(SiteMeasurementModel, 'getSiteMeasurement').andReturn(modelPromise.promise);
        spyOn($state, 'go');
        controller = $controller('SiteMeasurementDetailsController', {
            $scope: $scope,
            $state: $state,
            SiteMeasurementsService: _SiteMeasurementsService_,
            SiteMeasurementModel: SiteMeasurementModel

        });

        $scope.$digest();
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should save site measurement', function () {
        controller.selectedStatus = selectedStatus;
        controller.save(siteMeasurementData);
        expect(SiteMeasurementsService.updateSiteMeasurement).toHaveBeenCalled();
    });
});
