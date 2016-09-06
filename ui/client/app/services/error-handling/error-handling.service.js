(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('ErrorHandlingService', ErrorHandlingService);

    ErrorHandlingService.$inject = ['lodash'];

    function ErrorHandlingService(lodash) {
        this.getErrorMessage = function (errorsInput) {
            var errors = [];

            lodash.forEach(errorsInput, function (error) {
                errors.push(error.message);
            });

            return errors.join('\n') || '';
        };
    }
})();
