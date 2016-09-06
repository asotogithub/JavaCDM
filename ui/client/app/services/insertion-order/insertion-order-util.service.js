(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('InsertionOrderUtilService', InsertionOrderUtilService);

    InsertionOrderUtilService.$inject = ['$rootScope', '$state'];

    function InsertionOrderUtilService($rootScope, $state) {
        var hasPendingChanges,
            destiny,
            filter,
            service = {
                getHasPendingChanges: getHasPendingChanges,
                getPlacementListFilter: getPlacementListFilter,
                goRouteDestiny: goRouteDestiny,
                isStateChangeAllowed: isStateChangeAllowed,
                setHasPendingChanges: setHasPendingChanges,
                setPlacementListFilter: setPlacementListFilter
            };

        return service;

        function getHasPendingChanges() {
            return hasPendingChanges;
        }

        function getPlacementListFilter() {
            return filter;
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
                $rootScope.$broadcast('ioPlacements.hasUnsavedChanges', {});
            }

            return !hasPendingChanges;
        }

        function setHasPendingChanges(status) {
            hasPendingChanges = status;
        }

        function setPlacementListFilter(selectedFilter) {
            filter = selectedFilter;
        }
    }
})();
