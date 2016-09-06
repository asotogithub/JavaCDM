(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('StatusService', StatusService);

    StatusService.$inject = [
        '$resource',
        'API_SERVICE'
    ];

    function StatusService($resource,
                           API_SERVICE) {
        var statusResource = $resource(API_SERVICE + 'Status');

        return {
            getStatus: function () {
                return statusResource.get().$promise;
            }
        };
    }
})();
