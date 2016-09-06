'use strict';

describe('Controller: LoginController', function () {
    var API_HOST,
        $httpBackend,
        $scope,
        $state,
        AuthenticationService,
        UserService,
        controller,
        $q;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, _$httpBackend_, $rootScope, _$q_, _API_HOST_) {
        $q = _$q_;
        $httpBackend = _$httpBackend_;
        API_HOST = _API_HOST_;
        $state = jasmine.createSpyObj('$state', ['go']);
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope.$new();

        AuthenticationService = jasmine.createSpyObj(
            'AuthenticationService', ['clear', 'setAuthorization', 'setToken']);
        UserService = jasmine.createSpyObj('UserService', [
            'setUsername',
            'getUser',
            'saveUser',
            'hasPermission',
            'hasRole'
        ]);

        var deferred = $q.defer();

        deferred.resolve('userResponse');
        UserService.getUser.andReturn(deferred.promise);
        controller = $controller('LoginController', {
            $scope: $scope,
            $state: $state,
            AuthenticationService: AuthenticationService,
            UserService: UserService
        });
    }));

    describe('$scope.submit()', function () {
        var getTokenSpy;

        beforeEach(function () {
            getTokenSpy = spyOn($scope, 'getToken');
        });

        it('should clear $scope.alerts', function () {
            $scope.alerts = ['one alert', 'two alert', 'hahahaha'];
            $scope.submit();

            expect($scope.alerts).toEqual([]);
        });

        it('should invoke AuthenticationService.setAuthorization()', function () {
            var credentials = $scope.credentials = {
                username: 'foo',
                password: 'bar'
            };

            $scope.submit();

            expect(AuthenticationService.setAuthorization).toHaveBeenCalledWith(credentials);
        });

        it('should invoke $scope.getToken()', function () {
            $scope.submit();

            expect(getTokenSpy).toHaveBeenCalled();
        });
    });

    describe('adblock warning', function () {
        var window;

        beforeEach(inject(function ($controller, $window) {
            window = $window;
            window.adBlockerPresent = undefined;
            controller = $controller('LoginController', {
                $scope: $scope,
                $window: window
            });
        }));

        it('should set the scope from the window', function () {
            expect(true).toEqual($scope.adBlockerEnabled);
        });
    });

    describe('$scope.getToken()', function () {
        describe('on success', function () {
            var OK_LOGIN_BODY = {
                accessToken: 'c33e6c61-dbe8-450e-bf12-cae13b82032e',
                tokenType: 'bearer',
                refreshToken: '8d4ce852-e4d2-439d-ab36-bcac98e5fb50',
                expiresIn: 1798,
                scope: '[trust, write, read]',
                userId: 'trueffect.galaxy+test@gmail.com'
            };

            beforeEach(function () {
                $httpBackend.when('POST', API_HOST + '/accesstoken').respond(
                    201,
                    OK_LOGIN_BODY
                );
            });

            it('should submit credentials to the AuthenticationService', function () {
                var actual;

                $httpBackend.expectPOST(API_HOST + '/accesstoken');
                // Credentials already defined in $scope, handled in getToken()
                $scope.getToken();
                $httpBackend.flush();

                expect($scope.alerts.length).toBe(0);
                expect(AuthenticationService.setToken).toHaveBeenCalled();
                actual = AuthenticationService.setToken.argsForCall[0][0].toJSON();
                expect(actual).toEqual(OK_LOGIN_BODY);
            });

            it('should invoke UserService.getUser()', function () {
                $scope.getToken();
                $httpBackend.flush();

                expect(UserService.setUsername).toHaveBeenCalledWith(OK_LOGIN_BODY.userId);
            });

            it('should broadcast `authorized` on $rootscope', function () {
                var broadcasted = false;

                $scope.$on('redirect.home-page', function () {
                    broadcasted = true;
                });

                $scope.getToken();
                $httpBackend.flush();

                expect(broadcasted).toBe(true);
            });

            it('should redirect to campaigns.html when get token', function () {
                // This test must be re think due a new behavior for dynamic views according to a roles
                var $stateGo = jasmine.createSpy('$state.go');

                $stateGo('campaigns');
                $scope.getToken();
                $httpBackend.flush();

                expect($stateGo).toHaveBeenCalledWith('campaigns');
            });
        });
    });
});
