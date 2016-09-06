(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('UtilsNumber', UtilsNumber);

    UtilsNumber.$inject = [];

    function UtilsNumber() {
        this.unmaskIntegerNumbers = function (maskedNumber) {
            return parseInt(maskedNumber.toString().replace(new RegExp(',', 'g'), ''));
        };
    }
})();
