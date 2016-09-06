(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('issuesContainer', IssuesContainerDirective);

    IssuesContainerDirective.$inject = [];

    function IssuesContainerDirective() {
        return {
            bindToController: true,
            controller: 'IssuesContainerController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/directives/issues-container/issues-container.html',
            transclude: true,
            scope: {
                model: '=',
                config: '=',
                hasExportPermission: '=',
                onCancel: '&',
                onContinue: '&',
                onExportIssues: '&',
                onModelActions: '&'
            },
            link: function (scope, element, attrs, controller) {
                controller.activate();
            }
        };
    }
})();
