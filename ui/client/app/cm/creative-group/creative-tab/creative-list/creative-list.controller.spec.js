'use strict';

describe('Controller: CreativeGroupCreativeListController', function () {
    var $q,
        $scope,
        $state,
        CreativeGroupService,
        CreativeService,
        DialogFactory,
        CreativeGroupCreativeListController,
        creativeGroupId,
        creatives,
        campaignId,
        creativeId,
        creativeObj = {};

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
                      $controller,
                      $rootScope,
                      _$state_,
                      _$q_,
                      _CreativeGroupService_,
                      _DialogFactory_,
                      _CreativeService_
                      ) {
        var $stateParams = {};

        creativeId = creativeObj.id = 1111;
        $q = _$q_;
        $state = _$state_;
        $scope = $rootScope.$new();
        CreativeGroupService = _CreativeGroupService_;
        CreativeService = _CreativeService_;
        DialogFactory = _DialogFactory_;
        creativeGroupId = $stateParams.creativeGroupId = 1337;
        campaignId = $stateParams.campaignId = 1111;
        creatives = $q.defer();

        spyOn(CreativeGroupService, 'getCreativeList').andReturn(creatives.promise);
        spyOn(DialogFactory, 'showDialog');
        spyOn($state, 'go');

        CreativeGroupCreativeListController = $controller('CreativeGroupCreativeListController', {
            $scope: $scope,
            $state: $state,
            $stateParams: $stateParams,
            CreativeGroupService: CreativeGroupService,
            CreativeService: CreativeService,
            DialogFactory: DialogFactory
        });
    }));

    describe('activate()', function () {
        var mockCreativeList = [
            {
                id: 1,
                name: 'creative',
                status: 'active',
                logicalDelete: 'N',
                creative: {
                    id: 5959479,
                    alias: '100x50 1',
                    creativeType: 'gif',
                    filename: '120x20gif.gif',
                    height: 20,
                    width: 120
                }
            },
            {
                id: 2,
                name: 'creative',
                status: 'active',
                logicalDelete: 'N',
                creative: {
                    id: 5959482,
                    alias: '100x50-3',
                    creativeType: 'jpg',
                    filename: '728x90jpg.jpg',
                    height: 90,
                    width: 728
                }
            }
        ];

        it('should invoke CreativeGroupService.getCreativeList()', function () {
            expect(CreativeGroupService.getCreativeList).toHaveBeenCalledWith(creativeGroupId);
            expect(CreativeGroupCreativeListController.promise).toBe(creatives.promise);
        });

        it('should set creatives when CreativeGroupService.getCreativeList() is resolved', function () {
            creatives.resolve(mockCreativeList);
            $scope.$apply();

            expect(CreativeGroupCreativeListController.creatives).toEqual([
                {
                    id: 5959479,
                    alias: '100x50 1',
                    creativeType: 'gif',
                    filename: '120x20gif.gif',
                    height: 20,
                    width: 120
                },
                {
                    id: 5959482,
                    alias: '100x50-3',
                    creativeType: 'jpg',
                    filename: '728x90jpg.jpg',
                    height: 90,
                    width: 728
                }
            ]);
        });

        it('should update the Creative List counter when CreativeService.getList() is resolved', function () {
            creatives.resolve(mockCreativeList);
            $scope.$apply();
            CreativeGroupCreativeListController.onSearchCounter(mockCreativeList.length);
            expect(CreativeGroupCreativeListController.creativeCounterLegend).toContain(mockCreativeList.length);
        });

        it('should redirect to edit creative details when call editCreative()',
            function () {
                CreativeGroupCreativeListController.editCreative(creativeObj);
                expect($state.go).toHaveBeenCalledWith('campaign-creative-details', {
                    campaignId: campaignId,
                    creativeId: creativeId,
                    from: 'creative-group-creative-list',
                    creativeGroupId: creativeGroupId
                });
            });
    });
});
