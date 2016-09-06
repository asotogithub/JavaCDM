'use strict';

describe('Controller: AddEventController', function () {
    var $q,
        $scope,
        $stateParams,
        $translate,
        CONSTANTS,
        DialogFactory,
        EventsService,
        SiteMeasurementGroupsService,
        Utils,
        controller,
        dialogDeferred;

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
        $stateParams = {
            siteMeasurementId: 9046516
        };
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        EventsService = _EventsService_;
        Utils = _Utils_;

        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog']);
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });

        controller = $controller('AddEventController', {
            $scope: $scope,
            $state: $state,
            $stateParams: $stateParams,
            $translate: _$translate_,
            CONSTANTS: CONSTANTS,
            DialogFactory: DialogFactory,
            EventsService: EventsService,
            SiteMeasurementGroupsService: SiteMeasurementGroupsService,
            Utils: Utils
        });
    }));

    describe('Initialize controller', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.STEP).toEqual({
                NAME: {
                    index: 1,
                    isValid: false,
                    key: 'addEvent.name'
                },
                GROUP: {
                    index: 2,
                    isValid: false,
                    key: 'addEvent.group'
                }
            });
            expect(controller.newEvent).toEqual({
                description: null,
                group: {
                    creating: false,
                    existing: false,
                    id: null,
                    name: null
                },
                name: null,
                tagType: null,
                type: null
            });
        });
    });

    describe('cancel()', function () {
        it('should call a modal dialog', function () {
            controller.cancel();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.eventsPings.confirmDiscard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            });
        });
    });

    describe('save()', function () {
        it('should call a modal dialog', function () {
            controller.save();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('siteMeasurement.eventsPings.createEventDialogWarning'),
                buttons: {
                    yes: $translate.instant('global.save'),
                    no: $translate.instant('global.back')
                },
                dontShowAgainID: CONSTANTS.SITE_MEASUREMENT.CREATE_EVENT_WARNING_DIALOG_ID
            });
        });
    });
});
