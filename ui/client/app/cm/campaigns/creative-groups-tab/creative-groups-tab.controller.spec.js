'use strict';

describe('Controller: CreativeGroupsTabController', function () {
    var $q,
        $scope,
        $state,
        $stateParams,
        CONSTANTS,
        CampaignsService,
        CreativeGroupsTabController,
        campaignId,
        creativeGroups,
        mockCreativeGroups;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$stateParams_,
                                _$q_,
                                _$state_,
                                _CONSTANTS_,
                                _CampaignsService_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams = _$stateParams_;
        CONSTANTS = _CONSTANTS_;
        CampaignsService = _CampaignsService_;
        campaignId = $stateParams.campaignId = 69;
        creativeGroups = $q.defer();
        mockCreativeGroups = [
            {
                name: CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME
            },
            {
                name: 'b'
            },
            {
                name: 'c'
            },
            {
                name: 'a'
            }
        ];

        spyOn($state, 'go');
        spyOn(CampaignsService, 'getCreativeGroups').andReturn(creativeGroups.promise);

        CreativeGroupsTabController = $controller('CreativeGroupsTabController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CampaignsService: CampaignsService
        });
    }));

    describe('creativeGroups', function () {
        it('should be initialized as null', function () {
            expect(CreativeGroupsTabController.creativeGroups).toBeNull();
        });
    });

    describe('activate()', function () {
        it('should invoke CampaignsService.getCreativeGroups()', function () {
            expect(CampaignsService.getCreativeGroups).toHaveBeenCalledWith(campaignId);
        });

        it('should set promise from CampaignsService.getCreativeGroups()', function () {
            expect(CreativeGroupsTabController.promise).toBe(creativeGroups.promise);
        });

        it('should set creativeGroups when CampaignsService.getCreativeGroups() is resolved', function () {
            creativeGroups.resolve(mockCreativeGroups);
            $scope.$apply();
            expect(CreativeGroupsTabController.creativeGroups).toBe(mockCreativeGroups);
        });

        it('should have label counter to Creative Groups Grid', function () {
            creativeGroups.resolve(mockCreativeGroups);
            $scope.$apply();
            CreativeGroupsTabController.onCounterSearch(mockCreativeGroups.length);
            expect(CreativeGroupsTabController.legendCreativeGroups).toBe(mockCreativeGroups.length +
                ' Creative Groups');
        });
    });

    describe('getCreativeGroupDetails()', function () {
        it('should invoke $state.go()', function () {
            CreativeGroupsTabController.getCreativeGroupDetails(1337);

            expect($state.go).toHaveBeenCalledWith('creative-group', {
                campaignId: campaignId,
                creativeGroupId: 1337
            });
        });
    });
});
