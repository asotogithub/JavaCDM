(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('TagInjectionUtilService', TagInjectionUtilService);

    TagInjectionUtilService.$inject = ['$rootScope', '$state'];

    function TagInjectionUtilService($rootScope, $state) {
        var hasPendingChanges,
            destiny,
            service = {
                getHasPendingChanges: getHasPendingChanges,
                goRouteDestiny: goRouteDestiny,
                isStateChangeAllowed: isStateChangeAllowed,
                setHasPendingChanges: setHasPendingChanges
            };

        return service;

        function getHasPendingChanges() {
            return hasPendingChanges;
        }

        function goRouteDestiny() {
            if (destiny) {
                $state.go(destiny.toStateName, destiny.toParams);
            }
        }

        function isStateChangeAllowed(event, toState, toParams) {
            if (hasPendingChanges) {
                destiny = {
                    toStateName: toState.name,
                    toParams: toParams
                };
                event.preventDefault();
                $rootScope.$broadcast('status.newTagAssociation', {});
            }

            return !hasPendingChanges;
        }

        function setHasPendingChanges(status) {
            if (!hasPendingChanges && status) {
                $rootScope.$broadcast('status.hasPendingChanges', {});
            }

            hasPendingChanges = status;
        }
    }
})();
