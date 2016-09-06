(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('AuthenticationService', AuthenticationService);

    AuthenticationService.$inject = ['$cookies', '$base64'];

    function AuthenticationService($cookies, $base64) {
        var authorization = null,
            service = {
                setAuthorization: function (credentials) {
                    authorization = $base64.encode(credentials.username + ':' + credentials.password);
                },

                getAuthorization: function () {
                    return authorization;
                },

                setToken: function (token) {
                    if (token) {
                        $cookies.token = angular.toJson(token);
                    }
                },

                getToken: function () {
                    if ($cookies.token) {
                        return angular.fromJson($cookies.token);
                    }

                    return null;
                },

                clear: function () {
                    authorization = null;
                    $cookies.token = null;
                }
            };

        return service;
    }
})();
