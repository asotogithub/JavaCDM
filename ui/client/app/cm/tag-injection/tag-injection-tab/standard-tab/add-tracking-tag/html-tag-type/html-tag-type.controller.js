(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddTrackingTagHtmlTagTypeController', AddTrackingTagHtmlTagTypeController);

    AddTrackingTagHtmlTagTypeController.$inject = [
        '$scope',
        'lodash'
    ];

    function AddTrackingTagHtmlTagTypeController(
        $scope,
        lodash) {
        var vm = this;

        vm.htmlTagTypeList = lodash.values($scope.$parent.vmAddTrackingTag.HTML_TAG_TYPE);
        vm.htmlTagTypeForm = {};
        vm.step = $scope.$parent.vmAddTrackingTag.STEP.TAG_TYPE;

        activate();

        function activate() {
            watchHtmlTagTypeChanges();
        }

        function watchHtmlTagTypeChanges() {
            $scope.$watch('vm.htmlTagType', function (newValue) {
                if (angular.isDefined(newValue)) {
                    $scope.$parent.vmAddTrackingTag.htmlTagType = newValue;
                    vm.step.isValid = true;
                }
            });
        }
    }
})();
