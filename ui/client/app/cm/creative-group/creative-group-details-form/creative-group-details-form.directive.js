(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('creativeGroupDetailsForm', CreativeGroupDetailsFormDirective);

    function CreativeGroupDetailsFormDirective() {
        return {
            bindToController: true,
            controller: 'CreativeGroupDetailsFormController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/cm/creative-group/creative-group-details-form/creative-group-details-form.html',
            transclude: true,

            scope: {
                model: '=',
                pristine: '=?',
                submitDisabled: '=?'
            }
        };
    }
})();
