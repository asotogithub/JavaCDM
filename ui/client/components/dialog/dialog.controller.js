(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('DialogController', DialogController);

    DialogController.$inject = ['$scope', '$modalInstance', 'data', 'storage'];

    function DialogController($scope, $modalInstance, data, storage) {
        $scope.data = data;

        $scope.yes = function () {
            updateLocalStorage(data, storage);
            $modalInstance.close('yes');
        };

        $scope.no = function () {
            updateLocalStorage(data, storage);
            $modalInstance.dismiss('no');
        };
    }

    function updateLocalStorage(data, storage) {
        if (data.dontShowAgainCheck) {
            storage.set(data.dontShowAgainKey, true);
        }
    }
})();
