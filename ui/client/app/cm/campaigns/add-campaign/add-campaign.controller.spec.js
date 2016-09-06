'use strict';

describe('Controller: AddCampaignController', function () {
    var $scope,
        $state,
        CampaignsService,
        DateTimeService,
        controller,
        mockObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            CampaignsService = jasmine.createSpyObj('CampaignsService', ['saveCampaignDetails']);
            $provide.value('CampaignsService', CampaignsService);
        });

        inject(function ($q, _DateTimeService_) {
            DateTimeService = _DateTimeService_;
            mockObject = {
                advertiserId: '9074362',
                agencyId: '9024559',
                brandId: '9074363',
                cookieDomainId: '9034120',
                createdDate: '2015-06-16T13:46:56-04:00',
                createdTpwsKey: 'a91d01ba-88ad-4466-9632-dabc02960c42',
                description: 'Campaign-5051-test',
                endDate: '2015-09-16T00:00:00-07:00',
                isActive: 'Y',
                isHidden: 'N',
                logicalDelete: 'N',
                modifiedDate: '2015-06-16T13:47:54-04:00',
                modifiedTpwsKey: 'a91d01ba-88ad-4466-9632-dabc02960c42',
                name: 'Campaign-5051-test',
                overallBudget: '1000',
                startDate: '2015-06-16T00:00:00-07:00',
                statusId: '1'
            };

            var defer = $q.defer();

            defer.resolve(mockObject);
            defer.$promise = defer.promise;

            CampaignsService.saveCampaignDetails.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddCampaignController', {
            $scope: $scope,
            DateTimeService: DateTimeService
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should resolve the promise to get the campaign to be saved', function () {
        controller.campaign = {
            advertiser: {
                id: '9074362',
                agencyId: '9024559'
            },
            brand: {
                id: '9074363'
            },
            domain: {
                id: '9034120'
            },
            description: 'Campaign-5051-test',
            startDate: new Date('2015-09-16T00:00:00-07:00'),
            endDate: new Date('2015-09-16T00:00:00-07:00'),
            name: 'Campaign-5051-test',
            budget: '1000'
        };
        controller.promiseRequest = null;
        controller.save();

        expect(controller.promiseRequest).toBeResolved();
    });

    it('should simulate a save operation successfully', function () {
        controller.campaign = {
            advertiser: {
                id: '9074362',
                agencyId: '9024559'
            },
            brand: {
                id: '9074363'
            },
            domain: {
                id: '9034120'
            },
            description: 'Campaign-5051-test',
            startDate: new Date('2015-09-16T00:00:00-07:00'),
            endDate: new Date('2015-09-16T00:00:00-07:00'),
            name: 'Campaign-5051-test',
            budget: '1000'
        };
        controller.promiseRequest = null;
        controller.save();

        expect(controller.promiseRequest.$$state.value.advertiserId).toBe(mockObject.advertiserId);
        expect(controller.promiseRequest.$$state.value.agencyId).toBe(mockObject.agencyId);
        expect(controller.promiseRequest.$$state.value.brandId).toBe(mockObject.brandId);
        expect(controller.promiseRequest.$$state.value.cookieDomainId).toBe(mockObject.cookieDomainId);
        expect(controller.promiseRequest.$$state.value.description).toBe(mockObject.description);
        expect(controller.promiseRequest.$$state.value.name).toBe(mockObject.name);
        expect(controller.promiseRequest.$$state.value.createdDate).toBe(mockObject.createdDate);
        expect(controller.promiseRequest.$$state.value.overallBudget).toBe(mockObject.overallBudget);
    });
});
