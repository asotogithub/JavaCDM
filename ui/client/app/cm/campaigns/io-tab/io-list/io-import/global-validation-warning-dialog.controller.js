(function () {
    'use strict';

    angular.module('uiApp')
        .controller('GlobalValidationWarningController', GlobalValidationWarningController);

    GlobalValidationWarningController.$inject = [
        'CONSTANTS'
    ];

    function GlobalValidationWarningController(
        CONSTANTS) {
        var vm = this;

        vm.MAX_ROWS_IMPORT = CONSTANTS.INSERTION_ORDER.IMPORT.MAX_ROWS.toLocaleString();
        vm.MAX_FILE_SIZE = (CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.LIMIT_SIZE / 1024).toLocaleString() + ' KB';

        activate();

        function activate() {
            angular.element('#modalNo').addClass('global-validation-hide-no-button');
        }
    }
})();
