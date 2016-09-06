'use strict';

describe('Controller: CampaignCreativeDetailsController', function () {
    var $scope,
        $state,
        CampaignCreativeDetailsController,
        deferred;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $q, _$state_) {
        var $stateParams = {};

        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams.campaignId = 123;
        $stateParams.creativeGroupId = 1234;
        deferred = $q.defer();
        spyOn($state, 'go');

        CampaignCreativeDetailsController = $controller('CampaignCreativeDetailsController', {
            $scope: $scope,
            $stateParams: $stateParams,
            $state: $state
        });
    }));

    it('should go back to creative-list $state', function () {
        $scope.callingState = 'creative-group-creative-list';
        CampaignCreativeDetailsController.backToList();

        expect($state.go).toHaveBeenCalled();
        expect($state.go).toHaveBeenCalledWith('creative-group-creative-list', {
            campaignId: 123,
            creativeGroupId: 1234
        });
    });

    it('should go back to creative-group-creative-list $state', function () {
        CampaignCreativeDetailsController.backToList();

        expect($state.go).toHaveBeenCalled();
        expect($state.go).toHaveBeenCalledWith('creative-list', {
            campaignId: 123
        });
    });
});
