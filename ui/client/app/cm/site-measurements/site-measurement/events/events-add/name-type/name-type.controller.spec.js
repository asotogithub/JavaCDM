'use strict';

describe('Controller: EventNameController', function () {
    var $q,
        $scope,
        $translate,
        CONSTANTS,
        EVENT_TAG_TYPE_LIST,
        EVENT_TYPE_LIST,
        EventsService,
        Utils,
        controller,
        event = {
            target: {
                value: ' anEventName'
            }
        },
        smCheckEventNamePromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$q_,
                                $rootScope,
                                $state,
                                _$translate_,
                                _CONSTANTS_,
                                _EventsService_,
                                _Utils_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        EventsService = _EventsService_;
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
                description: null
            },
            promise: null
        };

        EVENT_TAG_TYPE_LIST = [
            {
                key: 'Conversion',
                name: 'Conversion',
                value: 1
            },
            {
                key: 'Conversion w/Revenue',
                name: 'Conversion w/Revenue',
                value: 2
            },
            {
                key: 'Measured',
                name: 'Measured',
                value: 3
            },
            {
                key: 'Other',
                name: 'Other',
                value: 0
            }
        ];

        EVENT_TYPE_LIST = [
            {
                key: 'Standard',
                name: 'Standard',
                value: 0
            },
            {
                key: 'TruTag',
                name: 'TruTag',
                value: 1
            }
        ];

        smCheckEventNamePromise = $q.defer();

        spyOn(EventsService, 'checkEventNameUnique').andReturn(smCheckEventNamePromise.promise);
        spyOn($state, 'go');

        controller = $controller('EventNameController', {
            $scope: $scope,
            $translate: _$translate_,
            CONSTANTS: CONSTANTS,
            EventsService: EventsService,
            Utils: Utils
        });
    }));

    describe('Initilize controller', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.EVENT_NAME_STATE).toEqual({
                EMPTY: 0,
                DUPLICATED: 1,
                INVALID: 2,
                VALID: 3
            });
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.EMPTY);
            expect(controller.eventTypeList).toEqual(EVENT_TYPE_LIST);
            expect(controller.eventTagTypeList).toEqual(EVENT_TAG_TYPE_LIST);
            expect(controller.maxLength).toEqual({
                description: 256,
                name: 20
            });
            expect(controller.namePattern).toEqual(CONSTANTS.REGEX.WORD);
        });
    });

    describe('checkValidEventName()', function () {
        it('should validate as invalid', function () {
            controller.checkValidEventName(event, true);
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.INVALID);
        });

        it('should validate as duplicated', function () {
            controller.eventNameState = controller.EVENT_NAME_STATE.EMPTY;
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.EMPTY);
            controller.checkValidEventName(event, false);
            smCheckEventNamePromise.resolve({
                result: true
            });
            $scope.$apply();
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.DUPLICATED);
        });

        it('should validate as valid', function () {
            controller.eventNameState = controller.EVENT_NAME_STATE.EMPTY;
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.EMPTY);
            controller.checkValidEventName(event, false);
            smCheckEventNamePromise.resolve({
                result: false
            });
            $scope.$apply();
            expect(controller.eventNameState).toEqual(controller.EVENT_NAME_STATE.VALID);
        });
    });

    describe('checkValidInputType()', function () {
        it('should set as valid step', function () {
            controller.event.tagType = 'tagType';
            controller.event.type = 'type';
            controller.eventNameState = controller.EVENT_NAME_STATE.VALID;
            controller.checkValidInputType();
            expect($scope.$parent.vmAddEvent.STEP.NAME.isValid).toBe(true);
        });

        it('should set an invalid step', function () {
            controller.event.tagType = null;
            controller.event.type = null;
            controller.checkValidInputType();
            expect($scope.$parent.vmAddEvent.STEP.NAME.isValid).toBe(false);

            controller.event.tagType = null;
            controller.event.type = 'type';
            controller.checkValidInputType();
            expect($scope.$parent.vmAddEvent.STEP.NAME.isValid).toBe(false);

            controller.event.tagType = 'tagType';
            controller.event.type = null;
            controller.checkValidInputType();
            expect($scope.$parent.vmAddEvent.STEP.NAME.isValid).toBe(false);

            controller.event.tagType = 'tagType';
            controller.event.type = 'type';
            controller.eventNameState = controller.EVENT_NAME_STATE.INVALID;
            controller.checkValidInputType();
            expect($scope.$parent.vmAddEvent.STEP.NAME.isValid).toBe(false);
        });
    });

    describe('setDescription()', function () {
        it('should set event description', function () {
            var eventDescription = 'A Description';

            controller.event.description = eventDescription;
            controller.setDescription();
            expect($scope.$parent.vmAddEvent.newEvent.description).toEqual(eventDescription);
        });
    });
});
