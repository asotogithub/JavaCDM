(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('Page403Controller', Page403Controller);

    Page403Controller.$inject = ['$rootScope'];

    function Page403Controller($rootScope) {
        var vm = this;

        vm.goHome = function (event) {
            event.preventDefault();
            // Home page is defined in otherwise-redirect
            $rootScope.$broadcast('redirect.home-page');
        };
    }
})();
