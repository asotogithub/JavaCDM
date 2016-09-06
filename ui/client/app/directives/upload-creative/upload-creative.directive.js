(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('uploadCreative', UploadCreative);

    function UploadCreative() {
        return {
            bindToController: true,
            controller: 'UploadCreativeController',
            controllerAs: 'vm',
            restrict: 'E',
            templateUrl: 'app/directives/upload-creative/upload-creative.html',
            transclude: true,

            scope: {
                addExisting: '&',
                creativesModel: '=',
                creativeList: '=',
                creativeVersionedList: '=',
                isValidModel: '=',
                options: '='
            }
        };
    }
})();
