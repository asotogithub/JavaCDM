(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('ScheduleUtilService', ScheduleUtilService);

    ScheduleUtilService.$inject = ['$rootScope', '$state'];

    function ScheduleUtilService($rootScope, $state) {
        var sharedCreativeGroups = [],
            statusTraffic = {
                hasChangeTraffic: false,
                isOpenWarning: false,
                isTrafficLater: false
            };

        return {

            isChangeAllowed: function () {
                return !(statusTraffic.hasChangeTraffic && !statusTraffic.isTrafficLater);
            },

            setStatusChange: function (status) {
                statusTraffic.hasChangeTraffic = status;
            },

            setStatusWarning: function (status) {
                statusTraffic.isOpenWarning = status;
            },

            setTrafficLater: function (status) {
                statusTraffic.isTrafficLater = status;
            },

            resetStatusTraffic: function () {
                statusTraffic = {
                    hasChangeTraffic: false,
                    isOpenWarning: false,
                    isTrafficLater: false
                };
            },

            getStatusTraffic: function () {
                return statusTraffic;
            },

            goRouteDestiny: function () {
                $state.go(statusTraffic.toStateName, statusTraffic.toParams);
            },

            isStateChangeAllowed: function (event, toState, toParams) {
                var allowed = this.isChangeAllowed();

                if (allowed) {
                    $rootScope.$broadcast('cm.change-status.schedule', {
                        field: 'isFlyoutVisible',
                        value: false
                    });
                }
                else {
                    statusTraffic.toStateName = toState.name;
                    statusTraffic.toParams = toParams;
                    event.preventDefault();
                    $rootScope.$broadcast('status.traffic', {
                        option: 'modal'
                    });
                }

                return allowed;
            },

            getSharedCreativeGroups: function () {
                return sharedCreativeGroups;
            },

            setSharedCreativeGroups: function (value) {
                sharedCreativeGroups = value;
            }
        };
    }
})();
