'use strict';

describe('Controller: CampaignCreativeListController', function () {
    var $q,
        $scope,
        $state,
        CampaignsService,
        campaignId,
        controller,
        creativeList;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _$q_, _$state_, _CampaignsService_) {
        var $stateParams = {};

        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        CampaignsService = _CampaignsService_;
        campaignId = $stateParams.campaignId = '5959474';
        creativeList = $q.defer();

        spyOn($state, 'go');
        spyOn(CampaignsService, 'getCreatives').andReturn(creativeList.promise);

        controller = $controller('CampaignCreativeListController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CampaignsService: CampaignsService
        });
    }));

    describe('activate()', function () {
        var mockCreativeList = [
            {
                alias: '100x50-3',
                campaignId: '5959474',
                creativeType: 'jpg',
                filename: '180x150jpg.jpg',
                groupsCount: '0',
                height: '150',
                id: '5959481',
                width: '180'
            },
            {
                alias: '100x50-3',
                campaignId: '5959474',
                creativeType: 'jpg',
                filename: '180x150jpg.jpg',
                groupsCount: '0',
                height: '150',
                id: '5959482',
                width: '180'
            }
        ];

        it('should invoke CampaignService.getCreatives()', function () {
            expect(CampaignsService.getCreatives).toHaveBeenCalledWith(campaignId);
        });

        it('should set promise from CreativeService.getList()', function () {
            expect(controller.promise).toBe(creativeList.promise);
        });

        it('should set creativeList when CreativeService.getList() is resolved', function () {
            creativeList.resolve(mockCreativeList);
            $scope.$apply();
            expect(controller.creativeList).toBe(mockCreativeList);
        });

        it('should update the Creative List counter when CreativeService.getList() is resolved', function () {
            creativeList.resolve(mockCreativeList);
            $scope.$apply();
            controller.onSearchCounter(mockCreativeList.length);
            expect(controller.creativeCounterLegend).toContain(mockCreativeList.length);
        });
    });
});
