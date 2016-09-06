'use strict';

describe('Controller: Page404Ctrl', function () {
    beforeEach(module('uiApp'));

    var Page404Ctrl, scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        Page404Ctrl = $controller('Page404Controller', {
            $scope: scope
        });
    }));

    it('should initialize controller correctly', function () {
        expect(Page404Ctrl).not.toBeUndefined();
    });
});
