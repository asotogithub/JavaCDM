(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('UserService', UserService);

    UserService.$inject = [
        '$cookies',
        '$q',
        '$resource',
        'API_SERVICE',
        'ErrorRequestHandler',
        'lodash'
    ];

    function UserService(
        $cookies,
        $q,
        $resource,
        API_SERVICE,
        ErrorRequestHandler,
        lodash) {
        var resourceGetUser = $resource(API_SERVICE + 'Users/:username'),
            resourceGetDomains = $resource(API_SERVICE + 'Users/:username/domains'),
            resourceGetAdvertisers = $resource(API_SERVICE + 'Users/:username/advertisers'),
            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            };

        return {
            clear: function () {
                $cookies.user = null;
            },

            hasPermission: function (permissionKey) {
                if (!permissionKey) {
                    return false;
                }

                var user = this.getSavedUser();

                if (!user) {
                    return false;
                }

                //User comes with permissions
                return lodash.indexOf(user.permissions, permissionKey) >= 0;
            },

            hasRole: function (roleKey) {
                if (!roleKey) {
                    return false;
                }

                var user = this.getSavedUser();

                if (!user) {
                    return false;
                }

                return lodash.indexOf(user.roles[0].role, roleKey) >= 0;
            },

            getSavedUser: function () {
                if ($cookies.user) {
                    return angular.fromJson($cookies.user);
                }

                return null;
            },

            saveUser: function (user) {
                if (user) {
                    $cookies.user = angular.toJson(user);
                }
            },

            setUsername: function (username) {
                $cookies.username = username;
            },

            getUsername: function () {
                return $cookies.username;
            },

            getUser: function () {
                var userDefer = $q.defer();

                resourceGetUser.get({
                    username: $cookies.username
                }).$promise
                    .then(getUserComplete)
                    .catch(errorHandler('Cannot get user', userDefer));

                function getUserComplete(response) {
                    userDefer.resolve(response);
                }

                return userDefer.promise;
            },

            getDomains: function () {
                var domains = $q.defer();

                resourceGetDomains.get({
                    username: $cookies.username
                }).$promise
                    .then(getDomainsComplete)
                    .catch(errorHandler('Cannot get domains', domains));

                function getDomainsComplete(response) {
                    domains.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].CookieDomainDTO)));
                }

                return domains.promise;
            },

            getAdvertisers: function () {
                var advertisers = $q.defer();

                resourceGetAdvertisers.get({
                    username: $cookies.username
                }).$promise
                    .then(getAdvertisersComplete)
                    .catch(errorHandler('Cannot get advertisers', advertisers));

                function getAdvertisersComplete(response) {
                    advertisers.resolve(lodash.compact([].concat(
                        response &&
                        response.records &&
                        response.records[0] &&
                        response.records[0].Advertiser)));
                }

                return advertisers.promise;
            }

        };
    }
})();

