(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('DuplicateScheduleAssignmentsController', DuplicateScheduleAssignmentsController);

    DuplicateScheduleAssignmentsController.$inject = ['$modalInstance', '$scope', 'data'];

    function DuplicateScheduleAssignmentsController($modalInstance, $scope, data) {
        var vm = $scope.vm = this;

        vm.dismiss = dismiss;
        vm.duplicates = null;

        activate();

        function activate() {
            vm.duplicates = data.duplicates;
        }

        function dismiss() {
            $modalInstance.close();
        }
    }
})();
