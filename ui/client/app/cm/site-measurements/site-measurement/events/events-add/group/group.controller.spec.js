'use strict';

describe('Controller: EventGroupController', function () {
    var $filter,
        $q,
        $scope,
        $stateParams,
        CONSTANTS,
        RESPONSE_GROUP_LIST,
        SORTED_GROUP_LIST,
        SiteMeasurementGroupsService,
        Utils,
        controller,
        event = {
            target: {
                value: ' aGroupName'
            }
        },
        smCheckGroupNamePromise,
        smGroupsPromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$filter_,
                                _$q_,
                                $rootScope,
                                $state,
                                _CONSTANTS_,
                                _SiteMeasurementGroupsService_,
                                _Utils_) {
        $filter = _$filter_;
        $q = _$q_;
        $scope = $rootScope.$new();
        $stateParams = {
            siteMeasurementId: 9046516
        };
        CONSTANTS = _CONSTANTS_;
        SiteMeasurementGroupsService = _SiteMeasurementGroupsService_;
        Utils = _Utils_;

        $rootScope.vmAddEvent = {
            STEP: {
                NAME: {
                    index: 1,
                    isValid: true,
                    key: 'addEvent.name'
                },
                GROUP: {
                    index: 2,
                    isValid: true,
                    key: 'addEvent.group'
                }
            },
            newEvent: {
                group: {}
            },
            promise: null
        };

        RESPONSE_GROUP_LIST = [
            {
                id: 1,
                groupName: 'zGroupName'
            },
            {
                id: 2,
                groupName: 'aGroupName'
            }
        ];

        SORTED_GROUP_LIST = [
            {
                id: 2,
                groupName: 'aGroupName'
            },
            {
                id: 1,
                groupName: 'zGroupName'
            }
        ];

        smCheckGroupNamePromise = $q.defer();
        smGroupsPromise = $q.defer();

        spyOn(SiteMeasurementGroupsService, 'getGroupList').andReturn(smGroupsPromise.promise);
        spyOn(SiteMeasurementGroupsService, 'checkGroupNameUnique').andReturn(smCheckGroupNamePromise.promise);
        spyOn($state, 'go');

        controller = $controller('EventGroupController', {
            $filter: $filter,
            $scope: $scope,
            $stateParams: $stateParams,
            CONSTANTS: CONSTANTS,
            SiteMeasurementGroupsService: SiteMeasurementGroupsService,
            Utils: Utils
        });
    }));

    describe('activate()', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.GROUP_NAME_STATE).toEqual({
                EMPTY: 0,
                DUPLICATED: 1,
                INVALID: 2,
                VALID: 3
            });
            expect(controller.OPTION).toEqual({
                EXISTING: 1,
                CREATE: 2
            });
            expect(controller.createGroup).toEqual(false);
            expect(controller.existingGroup).toEqual(true);
            expect(controller.groupList).toEqual([]);
            expect(controller.groupNameState).toEqual(0);
            expect(controller.groupPattern).toEqual(CONSTANTS.REGEX.WORD);
            expect(controller.maxLengthGroup).toEqual(20);
        });

        it('should load group list data', function () {
            smGroupsPromise.resolve(RESPONSE_GROUP_LIST);
            $scope.$apply();

            expect(controller.groupList).toEqual(SORTED_GROUP_LIST);
        });
    });

    describe('checkValidGroupName()', function () {
        it('should validate as invalid', function () {
            controller.checkValidGroupName(event, true);
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.INVALID);
            expect($scope.$parent.vmAddEvent.STEP.GROUP.isValid).toBe(false);
        });

        it('should validate as duplicated', function () {
            controller.groupNameState = controller.GROUP_NAME_STATE.EMPTY;
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.EMPTY);
            controller.checkValidGroupName(event, false);
            smCheckGroupNamePromise.resolve({
                result: true
            });
            $scope.$apply();
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.DUPLICATED);
            expect($scope.$parent.vmAddEvent.STEP.GROUP.isValid).toBe(false);
        });

        it('should validate as valid', function () {
            controller.groupNameState = controller.GROUP_NAME_STATE.EMPTY;
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.EMPTY);
            controller.checkValidGroupName(event, false);
            smCheckGroupNamePromise.resolve({
                result: false
            });
            $scope.$apply();
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.VALID);
            expect($scope.$parent.vmAddEvent.STEP.GROUP.isValid).toBe(true);
        });
    });

    describe('selectOption()', function () {
        it('should set CREATE option', function () {
            controller.groupForm = {
                eventGroupCreate: {
                    $invalid: true,
                    $validate: function () {
                    },

                    $viewValue: 'a Group Name'
                }
            };
            controller.selectOption(controller.OPTION.CREATE);
            expect(controller.createGroup).toBe(true);
            expect(controller.existingGroup).toBe(false);
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.INVALID);
        });

        it('should set EXISTING option', function () {
            controller.groupForm = {
                eventGroupCreate: {
                    $error: {
                        pattern: false
                    },
                    $invalid: false
                }
            };

            controller.selectOption(controller.OPTION.EXISTING);
            expect(controller.createGroup).toBe(false);
            expect(controller.existingGroup).toBe(true);
            expect(controller.groupNameState).toEqual(controller.GROUP_NAME_STATE.EMPTY);
        });
    });

    describe('setExistingGroup()', function () {
        it('should set step as invalid', function () {
            controller.group.existing = null;
            controller.setExistingGroup();
            expect($scope.$parent.vmAddEvent.STEP.GROUP.isValid).toBe(false);
        });

        it('should set step as valid', function () {
            controller.group.existing = 'AnExistingGroupName';
            controller.setExistingGroup();
            expect($scope.$parent.vmAddEvent.STEP.GROUP.isValid).toBe(true);
        });
    });
});
