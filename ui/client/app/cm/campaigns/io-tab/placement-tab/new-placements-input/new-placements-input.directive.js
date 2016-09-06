(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('newPlacementsInput', NewPlacementsInput);

    function NewPlacementsInput() {
        return {
            bindToController: true,
            controller: 'NewPlacementsInputController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/cm/campaigns/io-tab/placement-tab/new-placements-input/new-placements-input.html',
            transclude: true,
            scope: {
                model: '=',
                options: '='
            }
        };
    }
})();
