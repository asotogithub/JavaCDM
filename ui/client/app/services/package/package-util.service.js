(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('PackageUtilService', PackageUtilService);

    PackageUtilService.$inject = [];

    function PackageUtilService() {
        var hasPendingChanges,
            service = {
                getHasPendingChanges: getHasPendingChanges,
                setHasPendingChanges: setHasPendingChanges
            };

        return service;

        function getHasPendingChanges() {
            return hasPendingChanges;
        }

        function setHasPendingChanges(status) {
            hasPendingChanges = status;
        }
    }
})();
