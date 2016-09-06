(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('importData', ImportData);

    function ImportData() {
        return {
            bindToController: true,
            controller: 'ImportDataController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/directives/import-data/import-data.html',
            transclude: true,

            scope: {
                onClose: '&',
                onImportResource: '&',
                onImportError: '&',
                options: '=',
                onUploadError: '&',
                onUploadResource: '&',
                onUploadSuccess: '&'
            }
        };
    }
})();
