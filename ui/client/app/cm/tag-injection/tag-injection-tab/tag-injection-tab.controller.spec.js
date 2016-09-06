'use strict';

describe('Controller: TagInjectionTabController', function () {
    var $scope,
        FIRST_ADVERTISER,
        FIRST_BRAND,
        ADVERTISER_LIST,
        BRAND_LIST,
        AdvertiserService,
        UserService,
        TagInjectionTabController,
        Utils,
        expectedAdvertiserList,
        expectedBrandList,
        advertiserList,
        brandList;

    beforeEach(module('uiApp'));

    beforeEach(function () {
        FIRST_ADVERTISER = {
            id: -1,
            name: 'Select Advertiser'
        };
        FIRST_BRAND = {
            id: -1,
            name: 'Select Brand'
        };
        ADVERTISER_LIST = [
            {
                address1: 'Broadway',
                agencyId: 6031295,
                city: 'NY',
                contactDefault: 'Thomas',
                country: 'USA',
                createdDate: '2015-07-15T11:01:27-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                enableHtmlTag: 1,
                id: 6031299,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:27-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'UI QA Advertiser TEST',
                notes: 'API QA note',
                state: 'NJ',
                url: 'http://www.test.com'
            }, {
                address1: 'Cochabamba',
                agencyId: 6031295,
                city: 'Cochabamba',
                contactDefault: 'Mare',
                country: 'BOL',
                createdDate: '2015-07-15T17:08:25-07:00',
                createdTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                enableHtmlTag: 1,
                id: 6032345,
                isHidden: 'N',
                modifiedDate: '2015-07-15T17:08:25-07:00',
                modifiedTpwsKey: '825c5c77-d024-485c-9be1-d1fb07049c9f',
                name: 'Advertiser MARE',
                notes: 'Some notes',
                state: 'NJ',
                url: 'http://www.test.com'
            }
        ];

        BRAND_LIST = [
            {
                id: -1,
                name: 'Select Brand'
            }, {
                advertiserId: 6031299,
                createdDate: '2015-07-15T11:01:27-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                description: 'Automated created UITEST QA Brand',
                id: 6031300,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:27-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'QA UITEST Brand TEST - 01'
            }, {
                advertiserId: 6031299,
                createdDate: '2015-07-15T11:01:28-07:00',
                createdTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                description: 'Automated created UITEST QA Brand',
                id: 6031301,
                isHidden: 'N',
                modifiedDate: '2015-07-15T11:01:28-07:00',
                modifiedTpwsKey: 'c8976f29-1751-4f97-bce1-878d712eaec5',
                name: 'QA UITEST Brand TEST - 02'
            }
        ];

        expectedAdvertiserList = [FIRST_ADVERTISER].concat(ADVERTISER_LIST);
        expectedBrandList = [FIRST_BRAND].concat(BRAND_LIST);
    });

    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                $state,
                                _AdvertiserService_,
                                _UserService_,
                                _Utils_) {
        $scope = $rootScope.$new();
        AdvertiserService = _AdvertiserService_;
        UserService = _UserService_;
        Utils = _Utils_;
        advertiserList = $q.defer();
        brandList = $q.defer();

        spyOn(UserService, 'getAdvertisers').andReturn(advertiserList.promise);
        spyOn(AdvertiserService, 'getAdvertiserBrands').andReturn(brandList.promise);
        spyOn($state, 'go');

        TagInjectionTabController = $controller('TagInjectionTabController', {
            $scope: $scope,
            AdvertiserService: AdvertiserService,
            UserService: UserService,
            Utils: Utils
        });
    }));

    describe('activate()', function () {
        it('should load list of Advertisers', function () {
            expect(TagInjectionTabController.listAdvertisers).not.toBeDefined();
            advertiserList.resolve(ADVERTISER_LIST);
            $scope.$apply();
            expect(TagInjectionTabController.listAdvertisers).toEqual(expectedAdvertiserList);
        });

        it('should load list of Brands', function () {
            expect(TagInjectionTabController.listBrands).not.toBeDefined();
            expect(TagInjectionTabController.campaignSelected.id).not.toBeDefined();
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            $scope.$apply();
            expect(TagInjectionTabController.listBrands).toEqual(expectedBrandList);
        });

        it('should auto-select first Advertiser and first Brand', function () {
            expect(TagInjectionTabController.campaignSelected.id).not.toBeDefined();
            advertiserList.resolve(ADVERTISER_LIST);
            brandList.resolve(BRAND_LIST);
            $scope.$apply();
            expect(TagInjectionTabController.listAdvertisers).toEqual(expectedAdvertiserList);
            expect(TagInjectionTabController.advertiser).toEqual(ADVERTISER_LIST[0]);
            expect(TagInjectionTabController.listBrands).toEqual(expectedBrandList);
            expect(TagInjectionTabController.brand).toEqual(BRAND_LIST[0]);
        });
    });

    it('should allow select Advertiser', function () {
        expect(TagInjectionTabController.advertiser).toBeNull();
        TagInjectionTabController.selectAdvertiser(ADVERTISER_LIST[0]);
        expect(TagInjectionTabController.advertiser).toEqual(ADVERTISER_LIST[0]);
    });

    it('should load Brands after select Advertiser', function () {
        brandList.resolve(BRAND_LIST);
        TagInjectionTabController.selectAdvertiser(ADVERTISER_LIST[0]);
        $scope.$apply();
        expect(TagInjectionTabController.advertiser).toEqual(ADVERTISER_LIST[0]);
        expect(TagInjectionTabController.listBrands).toEqual(expectedBrandList);
    });

    it('should allow select Brand', function () {
        expect(TagInjectionTabController.brand).toBeNull();
        TagInjectionTabController.selectBrand(BRAND_LIST[0]);
        expect(TagInjectionTabController.brand).toEqual(BRAND_LIST[0]);
    });
});
