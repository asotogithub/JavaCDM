'use strict';

describe('Controller: CreativeGroupCreativeAddController', function () {
    var $scope,
        $state,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('CreativeGroupCreativeAddController', {
            $scope: $scope,
            $state: $state
        });
    }));
});

