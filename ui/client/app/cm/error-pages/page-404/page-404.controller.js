(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('Page404Controller', Page404Controller);

    Page404Controller.$inject = ['$rootScope'];

    function Page404Controller($rootScope) {
        var vm = this;

        vm.goHome = function (event) {
            event.preventDefault();
            // Home page is defined in otherwise-redirect
            $rootScope.$broadcast('redirect.home-page');
        };
    }
})();
