(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ConfigurationController', ConfigurationController);

    ConfigurationController.$inject = [
        '$scope',
        'CONSTANTS',
        'Utils',
        'lodash'
    ];

    function ConfigurationController(
        $scope,
        CONSTANTS,
        Utils,
        lodash) {
        var vm = this,
            ANIMATION_DELAY = 1000,
            MIN_ROWS_TO_SCROLL = 2;

        vm.FAIL_THROUGH_DEFAULT_OPTION = CONSTANTS.ADM_CONSTANTS.FAIL_THROUGH_DEFAULT_OPTION;
        vm.KEY_TYPE_OPTION = CONSTANTS.ADM_CONSTANTS.KEY_TYPE_OPTION;
        vm.addCookie = addCookie;
        vm.admConfigForm = $scope.$parent.$parent.admDetails.admConfigForm;
        vm.cookiesValidation = cookiesValidation;
        vm.getArrayFromObjectByField = getArrayFromObjectByField;
        vm.removeCookie = removeCookie;
        vm.removeEmptyCookies = removeEmptyCookies;
        vm.styleFunction = styleFunction;

        $scope.$on('admDetails-ready', function () {
            vm.admConfig = $scope.$parent.$parent.admDetails.model;
            activate();

            $scope.$watch('vmConfig.extractableCookiesForm.$valid', function (newVal) {
                vm.admConfigForm.extractableCookiesForm = vm.admConfig.cookiesToCapture.enabled ? newVal : true;
            });

            $scope.$watch('vmConfig.cookieOverwriteExceptionsForm.$valid', function (newVal) {
                vm.admConfigForm.cookieOverwriteExceptionsForm = vm.admConfig.durableCookies.enabled ? newVal : true;
            });

            $scope.$watch('vmConfig.formKeyType.$valid', function (newVal) {
                vm.admConfigForm.formKeyType = vm.admConfig.keyTypeOption === vm.KEY_TYPE_OPTION.COOKIE ?
                    newVal : true;
            });

            $scope.$watch('vmConfig.formKey.$valid', function (newVal) {
                vm.admConfigForm.formKey = vm.admConfig.failThroughDefaults.enabled &&
                    vm.admConfig.failThroughDefaults.defaultType === vm.FAIL_THROUGH_DEFAULT_OPTION.KEY_DEFAULT ?
                    newVal : true;
            });

            $scope.$watch('vmConfig.failThroughDefaultsForm.$valid', function (newVal) {
                vm.admConfigForm.failThroughDefaultsForm = vm.admConfig.failThroughDefaults.enabled &&
                    vm.admConfig.failThroughDefaults.defaultType === vm.FAIL_THROUGH_DEFAULT_OPTION.COOKIE_DEFAULT ?
                    newVal : true;
            });
        });

        function activate() {
            vm.admConfig.cookieOverwriteExceptionsArray = getCookies(vm.admConfig.durableCookies.cookies);
            vm.admConfig.extractableCookiesArray = getCookies(vm.admConfig.cookiesToCapture.cookies);
            vm.admConfig.failThroughDefaultsArray = getCookies(vm.admConfig.failThroughDefaults.defaultCookieList);
            vm.admConfig.keyTypeOption = angular.isDefined(vm.admConfig.matchCookieName) === true ?
                vm.KEY_TYPE_OPTION.COOKIE : vm.KEY_TYPE_OPTION.URL_PATH;
        }

        function getCookies(cookies) {
            var cookiesResult = [],
                sequence;

            if (Utils.isUndefinedOrNull(cookies)) {
                cookiesResult.push({
                    sequence: 0,
                    cookieName: '',
                    cookieValue: ''
                });
            }
            else {
                angular.forEach(cookies, function (cookie, index) {
                    sequence = index++;
                    if (angular.isUndefined(cookie.name)) {
                        cookiesResult.push({
                            sequence: sequence,
                            cookieName: cookie
                        });
                    }
                    else {
                        cookiesResult.push({
                            sequence: sequence,
                            cookieName: cookie.name,
                            cookieValue: cookie.value
                        });
                    }
                });
            }

            return cookiesResult;
        }

        function addCookie(cookiesArray, elementToScroll) {
            var sequence = cookiesArray.length;

            cookiesArray.push({
                sequence: sequence,
                cookieName: '',
                cookieValue: ''
            });

            if (!Utils.isUndefinedOrNull(elementToScroll)) {
                if (elementToScroll !== '#failThroughDefaultsList') {
                    delete cookiesArray[cookiesArray.length - 1].cookieValue;
                }

                if (sequence >= MIN_ROWS_TO_SCROLL) {
                    angular.element(elementToScroll).animate({
                        scrollTop: angular.element(elementToScroll)[0].scrollHeight
                    }, ANIMATION_DELAY);
                }
            }
        }

        function removeCookie(cookiesArray, index, form) {
            if (!cookiesArray || cookiesArray.length < 2 || typeof cookiesArray === undefined) {
                return;
            }

            cookiesArray.splice(index, 1);
            $scope.$parent.$parent.admDetails.admParentForm.$setDirty();
            refreshCookiesSequence(cookiesArray);
            refreshDuplicatesValidation(cookiesArray, form);
        }

        function refreshDuplicatesValidation(cookiesArray, form) {
            angular.forEach(form.$error.duplicatedField, function (errors) {
                var occurrences = findDuplicates(getArrayFromObjectByField(cookiesArray));

                errors.$setValidity('duplicatedField', occurrences[errors.$modelValue] < 2);
            });
        }

        function findDuplicates(elementsToFind) {
            return lodash.countBy(elementsToFind);
        }

        function cookiesValidation(item, cookiesForm, cookiesArray, index) {
            cookiesArray.splice(index, 1);
            $scope.$parent.$parent.admDetails.admParentForm.$setDirty();
            if (cookiesForm === vm.failThroughDefaultsForm) {
                cookiesForm['cookieName-' + item].$dirty = true;
                cookiesForm['cookieValue-' + item].$dirty = true;
            }
            else {
                cookiesForm[item].$dirty = true;
            }

            refreshCookiesSequence(cookiesArray);
        }

        function refreshCookiesSequence(cookiesArray) {
            angular.forEach(cookiesArray, function (cookie, index) {
                cookie.sequence = index;
            });
        }

        function removeEmptyCookies(cookiesArray, enabled, form) {
            if (enabled) {
                return;
            }

            var index = cookiesArray.length - 1;

            while (index >= 0) {
                if (cookiesArray.length > 1 && (cookiesArray[index].cookieName === '' ||
                    cookiesArray[index].cookieValue === '' && form === vm.failThroughDefaultsForm)) {
                    cookiesArray.splice(index, 1);
                }

                index--;
            }

            refreshCookiesSequence(cookiesArray);
        }

        function styleFunction(ind) {
            var cookieName = vm.failThroughDefaultsForm['cookieName-' + ind],
                cookieValue = vm.failThroughDefaultsForm['cookieValue-' + ind];

            if (cookieName.$dirty && cookieName.$invalid || cookieValue.$dirty && cookieValue.$invalid) {
                return 'has-error';
            }

            return '';
        }

        function getArrayFromObjectByField(model) {
            var uniqueList = lodash.uniq(model, function (item) {
                    return item.sequence;
                }),

                result = uniqueList.map(function (res) {
                    return res.cookieName;
                });

            return result;
        }
    }
})();
