(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('AuthInterceptor', AuthInterceptor);

    AuthInterceptor.$inject = [
        '$log',
        '$q',
        '$rootScope',
        'API_HOST',
        'AuthenticationService',
        'ErrorHandlingService',
        'httpBuffer'
    ];

    function AuthInterceptor(
        $log,
        $q,
        $rootScope,
        API_HOST,
        AuthenticationService,
        ErrorHandlingService,
        httpBuffer) {
        var OAUTH_ERROR = {
                INVALID_REQUEST: 'invalid_request',
                INVALID_TOKEN: 'invalid_token'
            },
            TOKEN_EXPIRED = 'The access token has expired',
            TOKEN_INVALID = 'Invalid access token',
            TOKEN_INVALID_BEARER = 'Bearer error=\"invalid_token\"',
            that = this;

        that.refreshInProgress = false;

        that.request = function (config) {
            // Interceptor will work only with API host.
            if (config.url.indexOf(API_HOST) < 0) {
                return config;
            }

            var authorization = AuthenticationService.getAuthorization(),
                accessToken = null,
                currentToken = AuthenticationService.getToken();

            if (config.data && config.data.refresh) {
                accessToken = currentToken ? currentToken.refreshToken : null;
            }
            else {
                accessToken = currentToken ? currentToken.accessToken : null;
            }

            if (accessToken) {
                config.headers.authorization = that.getAuthHeader(accessToken);
            }
            else if (authorization) {
                config.headers.authorization = that.getBasicHeader(authorization);
            }

            return config;
        };

        that.responseError = function (response) {
            var authHeaders = null,
                responseData = response && response.data,
                oauthError = responseData && responseData.error && responseData.error.oauthErrorCode,
                errorMessage = responseData && responseData.errors &&
                               ErrorHandlingService.getErrorMessage(responseData.errors);

            switch (response.status) {
                case 400:
                    if (angular.isUndefined(oauthError)) {
                        return $q.reject(response);
                    }

                    if (oauthError === OAUTH_ERROR.INVALID_REQUEST) {
                        that.unauthorize();
                    }

                    break;
                case 401:
                    if (angular.isUndefined(oauthError)) {
                        authHeaders = response.headers('WWW-Authenticate');
                        if (authHeaders && authHeaders.indexOf(OAUTH_ERROR.INVALID_TOKEN) >= 0) {
                            return requestRejection(response);
                        }

                        return $q.reject(response);
                    }

                    if (oauthError === OAUTH_ERROR.INVALID_TOKEN) {
                        return requestRejection(response);
                    }

                    break;
                case 403:
                    if (angular.isUndefined(errorMessage)) {
                        that.forbidden();
                        return $q.reject(response);
                    }

                    if (errorMessage.indexOf(TOKEN_EXPIRED) >= 0) {
                        return requestRejection(response);
                    }
                    else if (errorMessage.indexOf(TOKEN_INVALID) >= 0 ||
                        errorMessage.indexOf(TOKEN_INVALID_BEARER) >= 0) {
                        return requestRejection(response);
                    }
                    else {
                        that.forbidden();
                    }

                    break;
                case 404:
                    that.notFound();
                    break;
            }

            return $q.reject(response);
        };

        that.getBasicHeader = function (authorization) {
            return 'Basic ' + authorization;
        };

        that.getAuthHeader = function (accessToken) {
            return 'Bearer ' + accessToken;
        };

        that.forbidden = function () {
            $rootScope.$broadcast('forbidden');
        };

        that.notFound = function () {
            $rootScope.$broadcast('notFound');
        };

        that.unauthorize = function () {
            $rootScope.$broadcast('unauthorized');
        };

        /**
         * This function will use http-aut-interceptor logic to deal with rejected calls.
         * @param rejection
         * @returns {*}
         */
        function requestRejection(rejection) {
            var deferred = $q.defer();

            httpBuffer.append(rejection.config, deferred);
            $rootScope.$broadcast('event:auth-loginRequired', rejection);

            return deferred.promise;
        }
    }
})();
