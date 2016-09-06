(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CreativeGroupDetailsFormController', CreativeGroupDetailsFormController);

    CreativeGroupDetailsFormController.$inject = [
        '$scope',
        'CONSTANTS',
        'Utils',
        'lodash'
    ];

    function CreativeGroupDetailsFormController($scope, CONSTANTS, Utils, lodash) {
        var vm = this;

        vm.CREATIVE_GROUP = CONSTANTS.CREATIVE_GROUP;
        vm.CREATIVE_GROUP_MAX_LENGTH = CONSTANTS.INPUT.MAX_LENGTH.GROUP_NAME;
        vm.REGEX_CREATIVE_GROUP_NAME = CONSTANTS.REGEX.CREATIVE_GROUP_NAME;
        vm.geoTargetingVisible = false;
        vm.numeric = CONSTANTS.REGEX.NUMERIC;
        vm.updateSavedPriority = updateSavedPriority;

        activate();

        function activate() {
            if (!Utils.isUndefinedOrNull(vm.model)) {
                if (vm.model.enablePriority === vm.CREATIVE_GROUP.PRIORITY.DISABLED) {
                    vm.savedPriority = vm.CREATIVE_GROUP.PRIORITY.MIN;
                    vm.model.priority = vm.CREATIVE_GROUP.PRIORITY.DISABLED;
                }
                else {
                    vm.savedPriority = vm.model.priority;
                }
            }

            Object.defineProperty(vm, 'cookieTargetInvalid', invalidPropertyDescriptor('cookieTarget', {
                doCookieTargeting: 1,
                isDefault: 0
            }));
            Object.defineProperty(vm, 'daypartTargetInvalid', invalidPropertyDescriptor('daypartTarget', {
                doDaypartTargeting: 1,
                isDefault: 0
            }));
            Object.defineProperty(vm, 'frequencyCapInvalid', invalidPropertyDescriptor('frequencyCap', {
                enableFrequencyCap: 1,
                isDefault: 0
            }));
            Object.defineProperty(vm, 'frequencyCapWindowInvalid', invalidPropertyDescriptor('frequencyCapWindow', {
                enableFrequencyCap: 1,
                isDefault: 0
            }));
            Object.defineProperty(vm, 'nameInvalid', invalidPropertyDescriptor('creativeGroupName'));
            Object.defineProperty(vm, 'priorityInvalid', invalidPropertyDescriptor('priority', {
                enablePriority: 1,
                isDefault: 0
            }));
            Object.defineProperty(vm, 'weightInvalid', invalidPropertyDescriptor('weight', {
                enableGroupWeight: 1
            }));

            Object.defineProperty(vm, 'submitDisabled', {
                set: lodash.noop,
                get: function () {
                    return $scope.detailsForm && $scope.detailsForm.$pristine ||
                        vm.cookieTargetInvalid ||
                        vm.daypartTargetInvalid ||
                        vm.frequencyCapInvalid ||
                        vm.frequencyCapWindowInvalid ||
                        vm.nameInvalid ||
                        vm.priorityInvalid ||
                        vm.weightInvalid;
                }
            });

            Object.defineProperty(vm, 'pristine', {
                get: function () {
                    return $scope.detailsForm && $scope.detailsForm.$pristine;
                },

                set: function (value) {
                    if (value && $scope.detailsForm) {
                        $scope.detailsForm.$setPristine();
                    }
                }
            });
        }

        function invalidPropertyDescriptor(field, precondition) {
            var _precondition = precondition || {};

            return {
                get: function () {
                    return vm.model &&
                        lodash.all(_precondition, function (value, key) {
                            return vm.model[key] === value;
                        }) &&

                        $scope.detailsForm &&
                        $scope.detailsForm[field] &&
                        $scope.detailsForm[field].$invalid;
                }
            };
        }

        function updateSavedPriority() {
            if (vm.model.enablePriority === vm.CREATIVE_GROUP.PRIORITY.DISABLED) {
                vm.savedPriority = lodash.isEmpty($scope.detailsForm.priority.$error) ?
                    vm.model.priority : vm.CREATIVE_GROUP.PRIORITY.MIN;
                vm.model.priority = vm.CREATIVE_GROUP.PRIORITY.DISABLED;
            }
            else {
                if (Utils.isUndefinedOrNull(vm.savedPriority)) {
                    vm.savedPriority = vm.CREATIVE_GROUP.PRIORITY.MIN;
                }

                vm.model.priority = vm.savedPriority;
            }
        }
    }
})();
