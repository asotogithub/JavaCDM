(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('teTablePing', TeTablePingDirective);

    TeTablePingDirective.$inject = [];

    function TeTablePingDirective() {
        return {
            bindToController: true,
            controller: 'TeTablePingController',
            controllerAs: '$tablePing',
            restrict: 'E',
            templateUrl: 'app/directives/te-table-ping/te-table-ping.html',
            transclude: true,
            scope: {
                emptyMessage: '=',
                filterValues: '=',
                model: '=',
                editModeEnabled: '=',
                siteValues: '=',
                teTableContainerState: '=',
                teTableCustomSearchEnabled: '=?',
                searchFields: '=?',
                onDeletePing: '&',
                onSavePing: '&'
            },
            link: function (scope, element, attrs, controller, transclude) {
                var searchFields;

                transclude(scope.$parent, function (clone) {
                    var $config = clone.filter('te-table-config');

                    element.find('.te-table-btns').append(clone.filter('te-table-btns'));
                    element.find('.te-table-secondary-btns').append(clone.filter('te-table-secondary-btns'));
                    searchFields = $config.data('search-fields');
                });

                controller.activate();
            }
        };
    }
})();
