'use strict';

describe('Controller: AddCampaignNameController', function () {
    var $scope,
        $state,
        controller,
        AdvertiserService,
        UserService,
        mockRetrievedAdvertiserListObject,
        mockRetrievedBrandListObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            UserService = jasmine.createSpyObj('UserService', ['getAdvertisers', 'hasPermission']);
            $provide.value('UserService', UserService);
            AdvertiserService = jasmine.createSpyObj('AdvertiserService', ['getAdvertiserBrands']);
            $provide.value('AdvertiserService', AdvertiserService);
        });

        inject(function ($q) {
            mockRetrievedAdvertiserListObject = {
                totalNumberOfRecords: '1',
                records: [
                    {
                        Advertiser: [
                            {
                                address1: 'Advertiser 5116',
                                address2: 'Advertiser 5116',
                                agencyId: '5116',
                                city: 'uitest@trueforce.com',
                                contactDefault: 'Advertiser 5116',
                                country: 'Advertiser 5116',
                                createdDate: '2015-06-19T10:25:42.935-04:00',
                                enableHtmlTag: '5116',
                                faxNumber: 'Advertiser 5116',
                                id: '5116',
                                isHidden: 'N',
                                modifiedDate: '2015-06-19T10:25:42.935-04:00',
                                name: 'Advertiser 5116',
                                notes: 'Advertiser 5116',
                                phoneNumber: 'Advertiser 5116',
                                state: 'Advertiser 5116',
                                url: 'Advertiser 5116',
                                zipCode: 'Advertiser 5116'
                            }
                        ]
                    }
                ]
            };

            mockRetrievedBrandListObject = {
                totalNumberOfRecords: '2',
                records: [
                    {
                        Brand: [
                            {
                                advertiserId: '54300',
                                createdDate: '2015-06-19T10:56:50.541-04:00',
                                description: 'Brand 7340',
                                id: '7340',
                                isHidden: 'N',
                                name: 'Brand 7340'
                            },
                            {
                                advertiserId: '54300',
                                createdDate: '2015-06-19T10:56:50.541-04:00',
                                description: 'Brand 7341',
                                id: '7341',
                                isHidden: 'N',
                                name: 'Brand 7341'
                            }
                        ]
                    }
                ]
            };

            var defer = $q.defer(),
                deferBrands = $q.defer();

            defer.resolve(mockRetrievedAdvertiserListObject);
            deferBrands.resolve(mockRetrievedBrandListObject);

            UserService.getAdvertisers.andReturn(defer.promise);
            AdvertiserService.getAdvertiserBrands.andReturn(deferBrands.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmCampaign = {};
        $rootScope.vmCampaign.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addCampaign.name'
            },
            DOMAIN: {
                index: 2,
                isValid: false,
                key: 'addCampaign.domain'
            },
            DATES_BUDGET: {
                index: 3,
                isValid: false,
                key: 'addCampaign.dates&Budget'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddCampaignNameController', {
            $scope: $scope
        });
    }));

    it('Should resolve the promise to get the list of advertisers', function () {
        expect(controller.retrieveAdvertisers()).toBeResolved();
    });

    it('Should resolve the promise to get the list of brands', function () {
        expect(controller.retrieveBrands()).toBeResolved();
    });

    it('Should obtain all the advertisers to create a new campaign', function () {
        expect(controller.retrieveAdvertisers().$$state.value.records[0].Advertiser.length).toBe(1);
    });

    it('Should obtain all the brands for the selected advertiser', function () {
        expect(controller.retrieveBrands().$$state.value.records[0].Brand.length).toBe(2);
    });
});
