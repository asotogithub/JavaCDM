(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('UsernameMemoryService', UsernameMemoryService);

    UsernameMemoryService.$inject = ['storage'];

    var REMEMBER_USERNAME_KEY = 'teRememberUsername',
        REMEMBER_EXPIRY_KEY = 'teRememberExpiry',
        REMEMBER_EXPIRY_DAYS = 30;

    function UsernameMemoryService(storage) {
        var service = {

            isRememberUser: function () {
                var username = this.getRememberUser();

                return username !== null;
            },

            setRememberUser: function (username) {
                storage.set(REMEMBER_USERNAME_KEY, username);
                storage.set(REMEMBER_EXPIRY_KEY, getFutureExpireTimestamp());
            },

            getRememberUser: function () {
                var datetime = storage.get(REMEMBER_EXPIRY_KEY),
                    expired = isExpired(datetime);

                if (expired) {
                    this.forgetUser();
                }

                return storage.get(REMEMBER_USERNAME_KEY);
            },

            forgetUser: function () {
                storage.remove(REMEMBER_USERNAME_KEY);
                storage.remove(REMEMBER_EXPIRY_KEY);
            }
        };

        return service;
    }

    function isExpired(datetime) {
        if (datetime === null) {
            return true;
        }

        return (new Date()).getTime() > datetime;
    }

    function getFutureExpireTimestamp() {
        var rv = new Date();

        rv.setDate(rv.getDate() + REMEMBER_EXPIRY_DAYS);

        return rv.getTime();
    }
})();
