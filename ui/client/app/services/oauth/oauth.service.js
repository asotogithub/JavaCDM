(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('OauthService', OauthService);

    OauthService.$inject = [
        '$log',
        '$q',
        '$resource',
        'API_OAUTH',
        'CONSTANTS',
        'AuthenticationService',
        'authService'
    ];

    function OauthService(
        $log,
        $q,
        $resource,
        API_OAUTH,
        CONSTANTS,
        AuthenticationService,
        authService) {
        var oauthResource = $resource(
                API_OAUTH + ':criteria',
            {
                criteria: '@criteria'
            },
            {
                post: {
                    method: 'POST'
                }
            }),

            errorHandler = function (logMessage, deferred) {
                return function (error) {
                    $log.error(logMessage + ': ' + angular.toJson(error));
                    deferred.reject(error);
                };
            },

            promiseRefresh = null,

            refresh = function (d) {
                var deferred = d;

                oauthResource.post({
                    criteria: 'refreshaccesstoken',
                    refresh: true
                }).$promise.then(
                    function (response) {
                        AuthenticationService.setToken(response);
                        authService.loginConfirmed();
                        deferred.resolve(response);
                    },

                    function (error) {
                        deferred.reject(error);
                    });

                return deferred.promise;
            };

        return {
            accessToken: $resource(API_OAUTH + 'accesstoken',
                {
                }, {
                    post: {
                        method: 'POST'
                    }
                }
            ),

            getAccessToken: function () {
                var token = $q.defer();

                oauthResource.post({
                    criteria: 'accesstoken'
                }).$promise.then(
                    function (response) {
                        token.resolve(response);
                    },

                    function (error) {
                        token.reject(error);
                    });

                return token.promise;
            },

            isRefreshInProgress: function () {
                if (!promiseRefresh) {
                    return false;
                }

                return promiseRefresh.$$state.status === CONSTANTS.PROMISE.STATUS.PENDING;
            },

            logout: function () {
                var logout = $q.defer();

                oauthResource.post({
                    criteria: 'logout'
                }).$promise.then(
                    function (response) {
                        logout.resolve(response);
                    },

                    errorHandler('Cannot perform logout', logout));

                return logout.promise;
            },

            refreshToken: function (params) {
                if (this.isRefreshInProgress()) {
                    return null;
                }

                var deferred = $q.defer();

                if (!params || !params.verifyToken) {
                    promiseRefresh = refresh(deferred);
                    return promiseRefresh;
                }

                promiseRefresh = this.verifyToken();
                promiseRefresh.then(function (result) {
                    authService.loginConfirmed();
                    deferred.resolve(result);
                }).catch(function () {
                    promiseRefresh = refresh(deferred);
                    return promiseRefresh;
                });

                return deferred.promise;
            },

            verifyToken: function () {
                var deferred = $q.defer();

                oauthResource.post({
                    criteria: 'verifytoken'
                }).$promise.then(
                    function (response) {
                        deferred.resolve(response);
                    },

                    function (error) {
                        deferred.reject(error);
                    });

                return deferred.promise;
            }
        };
    }
})();
