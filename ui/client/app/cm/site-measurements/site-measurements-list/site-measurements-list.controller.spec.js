'use strict';

describe('Controller: SiteMeasurementsListController', function () {
    var $scope,
        EXPECTED_RESPONSE,
        SiteMeasurementsService,
        controller,
        model,
        searchCount = 1;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            SiteMeasurementsService = jasmine.createSpyObj('SiteMeasurementsService', ['getSiteMeasurements']);
            $provide.value('SiteMeasurementsService', SiteMeasurementsService);
        });

        inject(function ($q) {
            var mockData,
                deferGet;

            model = [
                {
                    assocMethod: 'AdvertiserName0',
                    brandId: '0',
                    clWindow: '0',
                    cookieDomainId: '0',
                    createdDate: '2015-05-27T12:21:14.474-04:00',
                    createdTpwsKey: '000000',
                    dupName: 'DupName0',
                    expirationDate: '2015-05-27T12:21:14.474-04:00',
                    id: '0',
                    logicalDelete: 'N',
                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                    modifiedTpwsKey: '000000',
                    name: 'Name0',
                    notes: 'Notes0',
                    resourcePathId: '0',
                    siteId: '0',
                    state: 2,
                    ttVer: '0',
                    vtWindow: '0',
                    advertiserId: '0',
                    events: 45
                },
                {
                    assocMethod: 'AdvertiserName4015',
                    brandId: '4015',
                    clWindow: '4015',
                    cookieDomainId: '4015',
                    createdDate: '2015-05-27T12:21:14.474-04:00',
                    createdTpwsKey: '000000',
                    dupName: 'DupName4015',
                    expirationDate: '2040-05-27T12:21:14.474-04:00',
                    id: '4015',
                    logicalDelete: 'N',
                    modifiedDate: '2015-05-27T12:21:14.474-04:00',
                    modifiedTpwsKey: '000000',
                    name: 'Name4015',
                    notes: 'Notes4015',
                    resourcePathId: '4015',
                    siteId: '4015',
                    state: 3,
                    ttVer: '4015',
                    vtWindow: '4015',
                    advertiserId: '4015',
                    events: 45
                }
            ];

            EXPECTED_RESPONSE = model;
            EXPECTED_RESPONSE[0].trafficked = false;
            EXPECTED_RESPONSE[0].status = 'global.inactive';
            EXPECTED_RESPONSE[1].trafficked = true;
            EXPECTED_RESPONSE[1].status = 'global.active';

            mockData = {
                    startIndex: '0',
                    pageSize: '100',
                    totalNumberOfRecords: '2',
                    records: [
                        {
                            SiteMeasurementDTO: model
                        }
                    ]
                };
            deferGet = $q.defer();

            deferGet.resolve(mockData);

            SiteMeasurementsService.getSiteMeasurements.andReturn(deferGet.promise);
        });
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _SiteMeasurementsService_) {
        $scope = $rootScope.$new();

        controller = $controller('SiteMeasurementsListController', {
            $scope: $scope,
            SiteMeasurementsService: _SiteMeasurementsService_
        });

        $scope.$digest();
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should obtain all the site measurements and set to a model', function () {
        expect(controller.retrieveSiteMeasurementList().$$state.value.records[0].SiteMeasurementDTO.length).toBe(2);
    });

    it('Should load model to Site Measurement grid', function () {
        expect(controller.siteMeasurementList).toEqual(EXPECTED_RESPONSE);
        expect(controller.pageSize).toEqual(50);
        expect(controller.siteMeasurementsTotal).toEqual(model.length);
        controller.onSearchCounter(searchCount);
        expect(controller.measurementLegend).toContain(searchCount + ' of ' + model.length);
    });
});
