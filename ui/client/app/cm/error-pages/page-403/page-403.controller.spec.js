'use strict';

describe('Controller: Page403Ctrl', function () {
    var $controller,
        $state,
        AuthenticationService,
        Page403Controller,
        scope;

    beforeEach(module('uiApp', function ($provide) {
        AuthenticationService = jasmine.createSpyObj('AuthenticationService', [
            'clear',
            'getAuthorization',
            'getToken',
            'refresh',
            'setToken'
        ]);

        $provide.value('AuthenticationService', AuthenticationService);
    }));

    beforeEach(inject(function (_$controller_, $rootScope, _$state_) {
        $controller = _$controller_;
        $state = _$state_;
        scope = $rootScope.$new();
        Page403Controller = $controller('Page403Controller', {
            $scope: scope
        });
        spyOn($state, 'go');
    }));

    it('should initialize controller correctly', function () {
        expect(Page403Controller).not.toBeUndefined();
    });
});
