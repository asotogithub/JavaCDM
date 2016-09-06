(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('pingBox', PingBoxDirective);

    PingBoxDirective.$inject = [
    ];

    function PingBoxDirective() {
        return {
            bindToController: true,
            controller: 'PingBoxController',
            controllerAs: 'vmPingbox',
            restrict: 'E',
            templateUrl: 'app/directives/ping-box/ping-box.html',
            transclude: true,
            scope: {
                model: '=',
                editModeEnabled: '=',
                sitePromise: '=',
                onDeletePing: '&',
                onSavePing: '&'
            }
        };
    }
})();
