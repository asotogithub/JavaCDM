'use strict';

describe('Controller: CampaignCreativeAssignController', function () {
    var $q,
        $scope,
        $state,
        $stateParams,
        CampaignsService,
        campaignId,
        controller,
        creativeGroups;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$q_, $rootScope, _$stateParams_, _$state_, _CampaignsService_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams = _$stateParams_;
        CampaignsService = _CampaignsService_;
        $rootScope.vmAdd = {};
        $rootScope.vmAdd.STEP = {
            UPLOAD: {
                index: 1,
                isValid: true,
                key: 'addCreative.upload'
            },
            ASSIGN: {
                index: 2,
                isValid: true,
                key: 'addCreative.assign'
            }
        };

        campaignId = '5959474';
        creativeGroups = $q.defer();

        spyOn($state, 'go');
        spyOn(CampaignsService, 'getCreativeGroups').andReturn(creativeGroups.promise);

        controller = $controller('CampaignCreativeAssignController', {
            $scope: $scope,
            $stateParams: $stateParams,
            $state: _$state_
        });
    }));

    it('should create an instance of the controller', function () {
        expect(controller).not.toBeUndefined();
    });

    it('should set creativeList when CampaignsService.getCreativeGroups() is resolved', function () {
        var _creativeGroups = [
            {
                cookieTarget: '',
                geoTarget: '',
                daypartTarget: '',
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                doGeoTargeting: 0,
                enableFrequencyCap: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                id: 9080620,
                impressionCap: 1,
                isDefault: 1,
                priority: 0,
                weight: 100
            },
            {
                cookieTarget: '',
                geoTarget: '',
                daypartTarget: '',
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                doGeoTargeting: 0,
                enableFrequencyCap: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                id: 9080490,
                impressionCap: 0,
                isDefault: 0,
                priority: 0,
                weight: 100
            }
        ];

        creativeGroups.resolve(_creativeGroups);
        $scope.$apply();

        expect(controller.creativeGroups).toBe(_creativeGroups);
    });
});
