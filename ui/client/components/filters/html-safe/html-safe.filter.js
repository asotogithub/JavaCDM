(function () {
    'use strict';

    angular
        .module('te.components')
        .filter('htmlSafe', HtmlSafeFilter);

    HtmlSafeFilter.$inject = ['$sce'];

    function HtmlSafeFilter($sce) {
        return function (htmlCode) {
            return $sce.trustAsHtml(htmlCode);
        };
    }
})();
