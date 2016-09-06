(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('associatedPlacements', AssociatedPlacements);

    function AssociatedPlacements() {
        return {
            bindToController: true,
            controller: 'AssociatedPlacementsController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/directives/associated-placements/associated-placements.html',
            transclude: true,
            scope: {
                model: '=',
                options: '=',
                tableTemplate: '=',
                tableTitle: '=',
                addAction: '&',
                editAction: '&',
                selectRowAction: '=?',
                removeLast: '=?'
            }
        };
    }
})();
