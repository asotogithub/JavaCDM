'use strict';

describe('Controller: AddSMCampaignController', function () {
    var $q,
        $scope,
        $translate,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementsService,
        SiteMeasurementsUtilService,
        Utils,
        controller,
        dialogDeferred,
        saveCampaignPromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$q_,
                                $rootScope,
                                $state,
                                _$translate_,
                                _CONSTANTS_,
                                _SiteMeasurementsService_,
                                _SiteMeasurementsUtilService_,
                                _Utils_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        SiteMeasurementsService = _SiteMeasurementsService_;
        SiteMeasurementsUtilService = _SiteMeasurementsUtilService_;
        Utils = _Utils_;

        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog', 'showDismissableMessage']);
        DialogFactory.DISMISS_TYPE = {
            SUCCESS: 'success'
        };
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });

        saveCampaignPromise = $q.defer();
        spyOn(SiteMeasurementsService, 'saveCampaign').andReturn(saveCampaignPromise.promise);
        spyOn($state, 'go');

        controller = $controller('AddSMCampaignController', {
            $scope: $scope,
            $state: $state,
            $translate: _$translate_,
            CONSTANTS: CONSTANTS,
            DialogFactory: DialogFactory,
            SiteMeasurementsService: SiteMeasurementsService,
            SiteMeasurementsUtilService: SiteMeasurementsUtilService,
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
                    key: 'addSMCampaign.name'
                },
                DOMAIN: {
                    index: 2,
                    isValid: false,
                    key: 'addSMCampaign.domain'
                }
            });
        });
    });

    describe('cancel()', function () {
        it('should call a modal dialog', function () {
            controller.cancel();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.confirmDiscard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            });
        });
    });

    describe('save()', function () {
        it('should call a modal dialog', function () {
            controller.campaign = {
                advertiser: {
                    id: 12345
                },
                brand: {
                    id: 54321
                },
                cookieDomain: {
                    id: 69837
                },
                status: {
                    key: true
                },
                name: 'A New Campaign',
                description: 'A description'
            };

            controller.save();
            saveCampaignPromise.resolve({
                id: 11123
            });
            $scope.$apply();

            expect(DialogFactory.showDismissableMessage).toHaveBeenCalledWith(
                DialogFactory.DISMISS_TYPE.SUCCESS,
                'info.operationCompleted');
        });
    });
});
