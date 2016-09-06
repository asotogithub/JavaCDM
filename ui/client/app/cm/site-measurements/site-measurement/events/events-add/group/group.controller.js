(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('EventGroupController', EventGroupController);

    EventGroupController.$inject = [
        '$filter',
        '$scope',
        '$stateParams',
        'CONSTANTS',
        'SiteMeasurementGroupsService',
        'Utils'
    ];

    function EventGroupController(
        $filter,
        $scope,
        $stateParams,
        CONSTANTS,
        SiteMeasurementGroupsService,
        Utils) {
        var currentGroupName = '',
            groupObject = $scope.$parent.vmAddEvent.newEvent.group,
            siteMeasurementId = $stateParams.siteMeasurementId,
            step = $scope.$parent.vmAddEvent.STEP.GROUP,
            vm = this;

        vm.GROUP_NAME_STATE = CONSTANTS.SITE_MEASUREMENT.NAME_STATE;
        vm.OPTION = {
            EXISTING: 1,
            CREATE: 2
        };
        vm.checkValidGroupName = checkValidGroupName;
        vm.createGroup = false;
        vm.existingGroup = true;
        vm.group = {
            existing: null,
            new: ''
        };
        vm.groupForm = {};
        vm.groupList = [];
        vm.groupNameState = vm.GROUP_NAME_STATE.EMPTY;
        vm.groupPattern = CONSTANTS.REGEX.WORD;
        vm.maxLengthGroup = CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.GROUP_NAME;
        vm.selectOption = selectOption;
        vm.setExistingGroup = setExistingGroup;

        activate();

        function activate() {
            getGroupList();
        }

        function getGroupList() {
            var promise = $scope.$parent.vmAddEvent.promise =
                SiteMeasurementGroupsService.getGroupList(siteMeasurementId);

            promise.then(function (response) {
                vm.groupList = $filter('orderBy')(response, 'groupName');
                vm.createGroup = vm.groupList.length === 0;
                vm.existingGroup = vm.groupList.length > 0;
            });
        }

        function selectOption(option) {
            vm.createGroup = option === vm.OPTION.CREATE;
            vm.existingGroup = option === vm.OPTION.EXISTING;

            if (option === vm.OPTION.EXISTING) {
                setExistingGroup();
                vm.groupNameState = vm.GROUP_NAME_STATE.EMPTY;
                vm.groupForm.eventGroupCreate.$invalid = false;
                vm.groupForm.eventGroupCreate.$error.pattern = false;
            }
            else if (option === vm.OPTION.CREATE) {
                currentGroupName = 'default name';
                vm.groupForm.eventGroupCreate.$validate();
                checkValidGroupName({
                        target: {
                            value: vm.groupForm.eventGroupCreate.$viewValue
                        }
                    },
                    vm.groupForm.eventGroupCreate.$invalid
                );
            }
        }

        function checkValidGroupName(event, invalid) {
            var groupName = event.target.value.trim(),
                promise;

            if (groupName === currentGroupName) {
                return;
            }
            else {
                currentGroupName = groupName;
            }

            step.isValid = false;

            if (!Utils.isUndefinedOrNull(invalid) && invalid) {
                vm.groupNameState = vm.GROUP_NAME_STATE.INVALID;
            }
            else if (!Utils.isUndefinedOrNull(groupName) && groupName !== '') {
                promise = $scope.$parent.vmAddEvent.promise = SiteMeasurementGroupsService.checkGroupNameUnique(
                    groupName);

                promise.then(function (response) {
                    if (response.result) {
                        vm.groupNameState = vm.GROUP_NAME_STATE.DUPLICATED;
                        vm.group.new = groupName;
                    }
                    else {
                        vm.groupNameState = vm.GROUP_NAME_STATE.VALID;
                        step.isValid = true;
                        groupObject.id = null;
                        groupObject.name = vm.group.new;
                        groupObject.existing = false;
                        groupObject.creating = true;
                    }
                });
            }
            else {
                vm.groupNameState = vm.GROUP_NAME_STATE.EMPTY;
                vm.group.new = '';
            }
        }

        function setExistingGroup() {
            if (Utils.isUndefinedOrNull(vm.group.existing)) {
                step.isValid = false;
            }
            else {
                groupObject.id = vm.group.existing.id;
                groupObject.name = '';
                groupObject.existing = true;
                groupObject.creating = false;

                step.isValid = true;
            }
        }
    }
})();
