(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('validateDuplicate', ValidateDuplicate);

    ValidateDuplicate.$inject = [
        'lodash',
        'Utils'
    ];

    function ValidateDuplicate(lodash, Utils) {
        var directive = {
            restrict: 'A',
            require: 'ngModel',
            link: link
        };

        return directive;

        function link(scope, elem, attrs, ngModel) {
            var findDuplicates,
                validateForm,
                isDuplicate;

            scope.$watch(attrs.ngModel, function (value) {
                if (value !== '') {
                    var occurrences = findDuplicates(scope.$eval(attrs.validateDuplicate));

                    validateForm(scope.$eval(attrs.formDuplicates), scope.$eval(attrs.validateDuplicate));
                    ngModel.$setValidity('duplicatedField', !isDuplicate(occurrences[value]));
                }
            });

            isDuplicate = function (objectDuplicates) {
                return objectDuplicates > 1;
            };

            validateForm = function (form, elementsToFind) {
                var occurrences;

                if (Utils.isUndefinedOrNull(form.$error)) {
                    return;
                }

                if (!Utils.isUndefinedOrNull(form.$error.duplicatedField)) {
                    angular.forEach(form.$error.duplicatedField, function (errors) {
                        occurrences = findDuplicates(elementsToFind);

                        errors.$setValidity('duplicatedField', !isDuplicate(occurrences[errors.$modelValue]));
                    });
                }
            };

            findDuplicates = function (elementsToFind) {
                return lodash.countBy(elementsToFind);
            };
        }
    }
})();

