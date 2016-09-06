(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('MainController', MainController);

    MainController.$inject = [
        '$rootScope',
        '$scope',
        '$window',
        'CONSTANTS',
        'InsertionOrderUtilService',
        'OauthService',
        'ScheduleUtilService',
        'SiteMeasurementsUtilService',
        'TagInjectionUtilService',
        'UserService',
        'MEASUREMENT_URL'
    ];

    function MainController(
        $rootScope,
        $scope,
        $window,
        CONSTANTS,
        InsertionOrderUtilService,
        OauthService,
        ScheduleUtilService,
        SiteMeasurementsUtilService,
        TagInjectionUtilService,
        UserService,
        MEASUREMENT_URL) {
        var vm = this;

        vm.hasPermission = hasPermission;
        vm.logout = logout;
        vm.MEASUREMENT_URL = MEASUREMENT_URL;
        vm.PERMISSION = CONSTANTS.PERMISSION;
        vm.userName = UserService.getUsername();
        vm.status = getInitialStatus();
        vm.toggleMenu = toggleMenu;

        $rootScope.app = {
            layout: {
                isCollapsed: false
            }
        };

        $scope.$on('cm.change-status.schedule', function (event, params) {
            vm.status.cm.schedule[params.field] = params.value;
        });

        function doLogout() {
            var promise = OauthService.logout();

            promise.then().finally(function () {
                unauthorize();
            });
        }

        function getInitialStatus() {
            return {
                cm: {
                    schedule: {
                        isFlyoutVisible: false
                    }
                }
            };
        }

        function hasPermission(permission) {
            return UserService.hasPermission(permission);
        }

        function logout() {
            if (InsertionOrderUtilService.getHasPendingChanges()) {
                $rootScope.$broadcast('ioPlacements.hasUnsavedChanges', {
                    action: function () {
                        doLogout();
                    }
                });
                return;
            }

            if (!ScheduleUtilService.isChangeAllowed()) {
                $rootScope.$broadcast('status.traffic', {
                    option: 'modal',
                    action: function () {
                        doLogout();
                    }
                });
                return;
            }

            if (TagInjectionUtilService.getHasPendingChanges()) {
                $rootScope.$broadcast('status.newTagAssociation', {
                    action: function () {
                        doLogout();
                    }
                });
                return;
            }

            if (SiteMeasurementsUtilService.getHasPendingChanges()) {
                $rootScope.$broadcast('smContent.hasUnsavedChanges', {
                    action: function () {
                        doLogout();
                    }
                });
                return;
            }

            doLogout();
        }

        function toggleMenu() {
            $rootScope.app.layout.isCollapsed = !$rootScope.app.layout.isCollapsed;
            angular.element($window).triggerHandler('resize');
        }

        function unauthorize() {
            $rootScope.$broadcast('unauthorized');
        }
    }
})();
