'use strict';

describe('Controller: PlacementListController', function () {
    var $q,
        $scope,
        $state,
        DateTimeService,
        InsertionOrderService,
        controller,
        placementList,
        stateParams;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$q_,
                                _$state_,
                                _CampaignsService_,
                                _DateTimeService_,
                                _InsertionOrderService_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        DateTimeService = _DateTimeService_;
        InsertionOrderService = _InsertionOrderService_;
        stateParams = {
            ioId: 9110478
        };
        placementList = $q.defer();

        spyOn($state, 'go');
        spyOn(InsertionOrderService, 'getPackagePlacements').andReturn(placementList.promise);

        controller = $controller('PlacementListController', {
            $scope: $scope,
            $stateParams: stateParams,
            DateTimeService: DateTimeService,
            InsertionOrderService: InsertionOrderService
        });
    }));

    describe('activate()', function () {
        it('should invoke CampaignsService.getPackagePlacements()', function () {
            expect(InsertionOrderService.getPackagePlacements).toHaveBeenCalledWith(stateParams.ioId);
        });

        it('should set promise from CampaignsService.getPackagePlacements()', function () {
            expect(controller.promise).toBe(placementList.promise);
        });

        it('should set placementList when CampaignsService.getPackagePlacements() is resolved',
            inject(function () {
                var _packageList = [
                        {
                            adSpend: 0,
                            campaignId: 6031386,
                            countryCurrencyId: 1,
                            endDate: DateTimeService.applyOffset('2015-09-19T00:00:00-04:00'),
                            height: 150,
                            id: 6101580,
                            inventory: 1,
                            ioId: 6061636,
                            isScheduled: 'N',
                            isTrafficked: 0,
                            logicalDelete: 'N',
                            maxFileSize: 1,
                            name: 'Test',
                            packageId: 6101578,
                            packageName: 'Test',
                            rate: 0,
                            rateType: 'CPM',
                            resendTags: 0,
                            siteId: 6086072,
                            siteName: 'SITE - tests',
                            siteSectionId: 6101579,
                            sizeId: 5006631,
                            sizeName: '180x150',
                            startDate: DateTimeService.applyOffset('2015-08-20T00:00:00-04:00'),
                            status: 'New',
                            utcOffset: 0,
                            width: 180,
                            scheduledIcon: false
                        }
                    ],
                    _placementList = [
                        {
                            adSpend: 0,
                            campaignId: 6031386,
                            countryCurrencyId: 1,
                            endDate: new Date(DateTimeService.applyOffset('2015-09-19T00:00:00-04:00')),
                            height: 150,
                            id: 6101580,
                            inventory: 1,
                            ioId: 6061636,
                            isScheduled: 'N',
                            isTrafficked: 0,
                            logicalDelete: 'N',
                            maxFileSize: 1,
                            name: 'Test',
                            packageId: 6101578,
                            packageName: 'Test',
                            rate: 0,
                            rateType: 'CPM',
                            resendTags: 0,
                            siteId: 6086072,
                            siteName: 'SITE - tests',
                            siteSectionId: 6101579,
                            sizeId: 5006631,
                            sizeName: '180x150',
                            startDate: new Date(DateTimeService.applyOffset('2015-08-20T00:00:00-04:00')),
                            status: 'New',
                            utcOffset: 0,
                            width: 180,
                            formattedStartDate: '08/20/2015',
                            formattedEndDate: '09/19/2015',
                            scheduledIcon: false
                        }
                    ];

                placementList.resolve(_packageList);
                $scope.$apply();

                expect(controller.placementList).toEqual(_placementList);
            }));
    });
});
