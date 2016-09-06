'use strict';

describe('Controller: AddCampaignDatesController', function () {
    var controller,
        $scope,
        $state;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmCampaign = {};
        $rootScope.vmCampaign.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addCampaign.name'
            },
            DOMAIN: {
                index: 2,
                isValid: false,
                key: 'addCampaign.domain'
            },
            DATES_BUDGET: {
                index: 3,
                isValid: false,
                key: 'addCampaign.dates&Budget'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddCampaignDatesController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });
});
