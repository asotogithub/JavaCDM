(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TeFlyOutController', TeFlyOutController);

    TeFlyOutController.$inject = [
        '$scope',
        'CONSTANTS'
    ];

    function TeFlyOutController(
        $scope,
        CONSTANTS) {
        var vm = this,
            flyOutElement = angular.element(angular.element('#mySidenav'));

        vm.FLYOUT_STATE = CONSTANTS.FLY_OUT.STATE;
        vm.close = close;
        vm.closeAction = closeAction;
        vm.full = full;
        vm.half = half;

        function half() {
            vm.flyoutState = vm.FLYOUT_STATE.HALF_VIEW;
        }

        function full() {
            vm.flyoutState = vm.FLYOUT_STATE.FULL_VIEW;
        }

        function closeAction(params) {
            vm.onCloseFlyOut({
                params: params
            });
        }

        function close() {
            vm.flyoutState = vm.FLYOUT_STATE.HIDDEN;
            vm.isOpen = false;
        }

        function transitionEnd() {
            if (!vm.isOpen) {
                flyOutElement.removeClass('panel');
                flyOutElement.removeClass('panel-primary');
            }
        }

        $scope.$watch('vmTeFlyOutController.isOpen', function (newValue) {
            var closeFromController = true;

            if (newValue) {
                if (vm.flyOutDefaultState === vm.FLYOUT_STATE.FULL_VIEW) {
                    full();
                }
                else {
                    half();
                }

                flyOutElement.addClass('panel');
                flyOutElement.addClass('panel-primary');
            }
            else {
                close(closeFromController);
                flyOutElement[0].addEventListener('transitionend', transitionEnd, false);
            }
        });
    }
})();
