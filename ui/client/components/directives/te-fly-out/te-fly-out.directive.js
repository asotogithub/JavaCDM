(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('teFlyOut', TeFlyOut);

    TeFlyOut.$inject = [
        'CONSTANTS'
    ];

    function TeFlyOut() {
        return {
            bindToController: true,
            controller: 'TeFlyOutController',
            controllerAs: 'vmTeFlyOutController',
            templateUrl: 'components/directives/te-fly-out/te-fly-out.html',
            scope: {
                isOpen: '=',
                templateContent: '=',
                flyOutModel: '=',
                flyOutDefaultState: '=',
                onCloseFlyOut: '&'
            }
        };
    }
})();
