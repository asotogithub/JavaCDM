(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('LoginController', LoginController);

    LoginController.$inject = [
        '$log',
        '$rootScope',
        '$scope',
        '$state',
        '$window',
        'AuthenticationService',
        'OauthService',
        'UsernameMemoryService',
        'UserService'
    ];

    function LoginController(
        $log,
        $rootScope,
        $scope,
        $state,
        $window,
        AuthenticationService,
        OauthService,
        UsernameMemoryService,
        UserService) {
        var isRememberUser = UsernameMemoryService.isRememberUser();

        $scope.credentials = {
            remember: isRememberUser,
            username: isRememberUser ? UsernameMemoryService.getRememberUser() : '',
            password: ''
        };
        $scope.alerts = [];

        $scope.adBlockerEnabled = $window.adBlockerPresent === undefined;

        $scope.setUsername = function (username, remember) {
            UserService.setUsername(username);
            if (remember) {
                UsernameMemoryService.setRememberUser(username);
            }
            else {
                UsernameMemoryService.forgetUser();
            }
        };

        $scope.submit = function () {
            $scope.alerts = [];
            AuthenticationService.setAuthorization($scope.credentials);
            $scope.getToken();
        };

        $scope.getToken = function () {
            var promise = $scope.promiseRequest = OauthService.getAccessToken();

            promise.then(function (token) {
                if (token.accessToken) {
                    AuthenticationService.setToken(token);
                    $scope.setUsername(token.userId, $scope.credentials.remember);
                    promise = $scope.promiseRequest = UserService.getUser().then(function (user) {
                        UserService.saveUser(user);
                        $rootScope.$broadcast('redirect.home-page');
                    });
                }
            }).catch(function (error) {
                $log.error('Cannot get Token: ' + angular.toJson(error));
                $scope.alerts.push({
                    type: 'danger',
                    msg: 'login.error'
                });
                $scope.credentials.password = '';
            });
        };
    }
})();
