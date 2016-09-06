'use strict';

describe('Controller: AddPlacementDetailsController', function () {
    var $scope,
        $state,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmAdd = {};
        $rootScope.vmAdd.STEP = {
            DETAILS: {
                index: 1,
                isValid: false,
                key: 'addPlacement.details'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddPlacementDetailsController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });
});
