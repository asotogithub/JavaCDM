(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTree', TeTreeDirective);

    TeTreeDirective.$inject = [];

    function TeTreeDirective() {
        return {
            bindToController: true,
            controller: 'TeTreeController',
            controllerAs: 'vm',
            replace: true,
            restrict: 'E',
            templateUrl: 'components/directives/te-tree/te-tree.html',
            transclude: true,

            scope: {
                customNodeTemplate: '=',
                onDrop: '=',
                header: '=',
                model: '=',
                onExpandCollapse: '=',
                onSelect: '=',
                onCustomSearch: '=?',
                onClearSearchField: '=?',
                customSearchEnabled: '=?',
                customSearchTerm: '=?',
                searchFields: '=?',
                rowSelectedUuid: '=?'
            },

            link: function (scope, element, attrs, controller, transclude) {
                var $parent = scope.$parent;

                activate();

                function activate() {
                    transclude($parent, function (clone) {
                        scope.vm.searchTerm = null;
                        element.find('.te-tree-btns').empty();
                        element.find('.te-tree-secondary-btns').empty();
                        element.find('.te-tree-thirdly-btns').empty();
                        element.find('.te-tree-btns').append(clone.filter('te-tree-btns'));
                        element.find('.te-tree-secondary-btns').append(clone.filter('te-tree-secondary-btns'));
                        element.find('.te-tree-thirdly-btns').append(clone.filter('te-tree-thirdly-btns'));
                    });
                }

                controller.activate();
            }
        };
    }
})();

