'use strict';

describe('Controller: PackageListController', function () {
    var $q,
        $scope,
        $state,
        CampaignsService,
        controller,
        packageListDefer,
        stateParams,
        packageListResponse = [
            {
                campaignId: 7402305,
                changed: false,
                costDetails: [
                    {
                        createdDate: '2016-08-17T11:57:15-07:00',
                        createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                        foreignId: 7513366,
                        id: 7513368,
                        inventory: 1,
                        logicalDelete: 'N',
                        margin: 0.0,
                        modifiedDate: '2016-08-17T11:57:15-07:00',
                        modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                        plannedGrossAdSpend: 0.0,
                        plannedGrossRate: 0.0,
                        plannedNetAdSpend: 0.0,
                        plannedNetRate: 0.0,
                        rateType: 4,
                        startDate: '2016-09-17T00:00:00-07:00'
                    }, {
                        createdDate: '2016-08-17T11:57:15-07:00',
                        createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                        endDate: '2016-09-16T23:59:59-07:00',
                        foreignId: 7513366,
                        id: 7513367,
                        inventory: 2000,
                        logicalDelete: 'N',
                        margin: 100.0,
                        modifiedDate: '2016-08-17T11:57:15-07:00',
                        modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                        plannedGrossAdSpend: 20.0,
                        plannedGrossRate: 10.0,
                        plannedNetAdSpend: 0.0,
                        plannedNetRate: 0.0,
                        rateType: 4,
                        startDate: '2016-08-17T00:00:00-07:00'
                    }
                ],
                countryCurrencyId: 1,
                createdDate: '2016-08-17T11:57:15-07:00',
                createdTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                description: 'PKG01',
                id: 7513366,
                logicalDelete: 'N',
                modifiedDate: '2016-08-17T11:57:15-07:00',
                modifiedTpwsKey: '9e0856ea-d340-415e-9f5a-a750011fa0f0',
                name: 'PKG01',
                placementCount: 1,
                placements: [
                    {
                        adSpend: 10.0,
                        endDate: '2016-09-16T23:59:59-07:00',
                        height: 1000,
                        id: 7513369,
                        inventory: 1000,
                        ioId: 7402306,
                        name: '000 - 888888 - 1000x1000',
                        rate: 10.0,
                        rateType: 'CPM',
                        siteId: 7109383,
                        siteName: '000',
                        siteSectionId: 7109384,
                        sizeId: 5014113,
                        startDate: '2016-08-17T00:00:00-07:00',
                        width: 1000
                    }
                ]
            }
        ],
        packageListModel = [
            {
                id: 7513366,
                expanded: false,
                name: 'PKG01',
                isParent: true,
                size: '1000x1000',
                site: '000',
                adSpend: '$20.00',
                startDate: '2016-08-17T00:00:00-07:00',
                endDate: '2016-09-16T23:59:59-07:00',
                checked: false,
                placementName: 1,
                children: [
                    {
                        placementId: 7513369,
                        placementName: '000 - 888888 - 1000x1000',
                        size: '1000x1000',
                        site: '000',
                        adSpend: '$10.00',
                        startDate: '2016-08-17T00:00:00-07:00',
                        endDate: '2016-09-16T23:59:59-07:00'
                    }
                ]
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$q_,
                                _$state_,
                                _CampaignsService_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        CampaignsService = _CampaignsService_;
        stateParams = {
            campaignId: 9110477,
            ioId: 9110478
        };
        packageListDefer = $q.defer();

        spyOn($state, 'go');
        spyOn(CampaignsService, 'getPackageList').andReturn(packageListDefer.promise);

        controller = $controller('PackageListController', {
            $scope: $scope,
            $stateParams: stateParams,
            CampaignsService: CampaignsService
        });
    }));

    describe('activate()', function () {
        it('should invoke CampaignsService.getPackageList()', function () {
            expect(CampaignsService.getPackageList).toHaveBeenCalledWith(stateParams.campaignId, stateParams.ioId);
        });

        it('should set promise from CampaignsService.getPackageList()', function () {
            expect(controller.promise).toBe(packageListDefer.promise);
        });

        it('should set placementList when CampaignsService.getPackageList() is resolved',
            inject(function () {
                packageListDefer.resolve(packageListResponse);
                $scope.$apply();

                expect(controller.packageList).toEqual(packageListModel);
            }));
    });

    describe('Controller functions', function () {
        beforeEach(function () {
            packageListDefer.resolve(packageListResponse);
            $scope.$apply();
        });

        it('should invoke getAllCheckedData()', function () {
            expect(controller.countRowSelected).toEqual(0);
            controller.packageList[0].checked = true;
            controller.getAllCheckedData();
            expect(controller.countRowSelected).toEqual(1);
        });

        it('should expand/collapse row', function () {
            expect(controller.packageList[0].expanded).toBeFalsy();
            controller.rowExpanded(controller.packageList[0]);
            expect(controller.packageList[0].expanded).toBeTruthy();
            controller.rowCollapsed(controller.packageList[0]);
            expect(controller.packageList[0].expanded).toBeFalsy();
        });

        it('should invoke $state.go()', function () {
            controller.packageList[0].checked = true;
            controller.editPackage();

            expect($state.go).toHaveBeenCalledWith('edit-package', {
                campaignId: stateParams.campaignId,
                ioId: stateParams.ioId,
                packageId: controller.packageList[0].id,
                from: 'package-list'
            });
        });
    });
});
