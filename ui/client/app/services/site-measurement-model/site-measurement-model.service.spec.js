'use strict';

describe('Service: SiteMeasurementModel', function () {
    var SiteMeasurementsService,
        modelService,
        $httpBackend,
        mockRetrievedObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            SiteMeasurementsService = jasmine.createSpyObj('SiteMeasurementsService', ['getSiteMeasurements']);
            $provide.value('SiteMeasurementsService', SiteMeasurementsService);
        });

        inject(function ($q) {
            mockRetrievedObject = {
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
            };

            var defer = $q.defer();

            defer.resolve(mockRetrievedObject);
            defer.$promise = defer.promise;
            SiteMeasurementsService.getSiteMeasurements.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    beforeEach(inject(function (SiteMeasurementModel, _$httpBackend_) {
        $httpBackend = _$httpBackend_;
        modelService = SiteMeasurementModel;

        $httpBackend.whenGET('app/cm/login/login.html').respond('');
    }));

    it('should initialize Model Service', function () {
        expect(modelService).toBeDefined();
    });

    it('should verify if the promise that retrieves the site measurement object with id 44 is resolvable', function () {
        modelService.siteMeasurementId = 44;

        expect(modelService.getPromiseDetails(modelService.siteMeasurementId)).toBeResolved();
    });

    it('should retrieve the site measurement object with id 44 has all the data', function () {
        modelService.siteMeasurementId = 44;

        expect(modelService.getPromiseDetails(modelService.siteMeasurementId))
            .toBeResolvedWith(jasmine.objectContaining(mockRetrievedObject));
    });
});
