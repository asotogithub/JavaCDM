'use strict';

describe('Controller: CampaignCreativeAddController', function () {
    var $scope,
        $state,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('CampaignCreativeAddController', {
            $scope: $scope,
            $state: $state
        });
    }));

    describe('Steps', function () {
        it('should define steps for the wizard', function () {
            expect(controller.STEP).not.toBeUndefined();
        });
    });
});
