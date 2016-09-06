'use strict';

describe('Controller: SMCampaignNameController', function () {
    var $filter,
        $q,
        $scope,
        AdvertiserService,
        CONSTANTS,
        RESPONSE_ADVERTISE_LIST,
        RESPONSE_BRAND_LIST,
        SORTED_ADVERTISE_LIST,
        SORTED_BRAND_LIST,
        UserService,
        Utils,
        advertiseGetAdvertiserBrandsPromise,
        controller,
        userGetAdvertisersPromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$filter_,
                                _$q_,
                                $rootScope,
                                $state,
                                _AdvertiserService_,
                                _CONSTANTS_,
                                _UserService_,
                                _Utils_) {
        $filter = _$filter_;
        $q = _$q_;
        $scope = $rootScope.$new();
        AdvertiserService = _AdvertiserService_;
        CONSTANTS = _CONSTANTS_;
        UserService = _UserService_;
        Utils = _Utils_;

        $rootScope.vmSMCampaign = {
            STEP: {
                NAME: {
                    index: 1,
                    isValid: false,
                    key: 'addSMCampaign.name'
                },
                DOMAIN: {
                    index: 2,
                    isValid: false,
                    key: 'addSMCampaign.domain'
                }
            },
            campaign: {},
            promise: null
        };

        RESPONSE_ADVERTISE_LIST = [
            {
                id: 1,
                name: 'zAdvertiseName'
            },
            {
                id: 2,
                name: 'aAdvertiseName'
            }
        ];

        SORTED_ADVERTISE_LIST = [
            {
                id: 2,
                name: 'aAdvertiseName'
            },
            {
                id: 1,
                name: 'zAdvertiseName'
            }
        ];

        RESPONSE_BRAND_LIST = [
            {
                id: 1,
                name: 'zBrandName'
            },
            {
                id: 2,
                name: 'aBrandName'
            },
            {
                id: 3,
                name: 'mBrandName'
            }
        ];

        SORTED_BRAND_LIST = [
            {
                id: 2,
                name: 'aBrandName'
            },
            {
                id: 3,
                name: 'mBrandName'
            },
            {
                id: 1,
                name: 'zBrandName'
            }
        ];

        advertiseGetAdvertiserBrandsPromise = $q.defer();
        userGetAdvertisersPromise = $q.defer();

        spyOn(AdvertiserService, 'getAdvertiserBrands').andReturn(advertiseGetAdvertiserBrandsPromise.promise);
        spyOn(UserService, 'getAdvertisers').andReturn(userGetAdvertisersPromise.promise);
        spyOn($state, 'go');

        controller = $controller('SMCampaignNameController', {
            $filter: $filter,
            $scope: $scope,
            AdvertiserService: AdvertiserService,
            CONSTANTS: CONSTANTS,
            UserService: UserService,
            Utils: Utils
        });
    }));

    describe('activate()', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.advertiserList).toEqual([]);
            expect(controller.brandList).toEqual([]);
            expect(controller.maxLength).toEqual(128);
        });

        it('should load advertiser list data', function () {
            userGetAdvertisersPromise.resolve(RESPONSE_ADVERTISE_LIST);
            $scope.$apply();

            expect(controller.advertiserList).toEqual(SORTED_ADVERTISE_LIST);
        });
    });

    describe('reloadBrand()', function () {
        it('should load brand list data', function () {
            controller.reloadBrand();
            expect(controller.brandList).toEqual([]);

            $scope.$parent.vmSMCampaign.campaign.advertiser = {
                id: 1
            };
            controller.reloadBrand();

            advertiseGetAdvertiserBrandsPromise.resolve(RESPONSE_BRAND_LIST);
            $scope.$apply();

            expect(controller.brandList).toEqual(SORTED_BRAND_LIST);
        });
    });
});
