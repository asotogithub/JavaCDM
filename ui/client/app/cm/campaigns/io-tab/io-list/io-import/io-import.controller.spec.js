'use strict';

describe('Controller: IOImportController', function () {
    var $scope,
        $state,
        $translate,
        $uibModalInstance,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        Utils,
        controller,
        data,
        message = 'message',
        warningURL = 'app/cm/campaigns/io-tab/io-list/io-import/global-validation-warning-dialog.html';

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$state_,
                                _$translate_,
                                _CONSTANTS_,
                                _CampaignsService_,
                                _DialogFactory_,
                                _Utils_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        CampaignsService = _CampaignsService_;
        DialogFactory = _DialogFactory_;
        Utils = _Utils_;
        data = {
            campaignId: 12345
        };

        $uibModalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };

        spyOn($state, 'go');
        spyOn(DialogFactory, 'showCustomDialog');
        spyOn(DialogFactory, 'showDismissableMessage');
        spyOn(CampaignsService, 'uploadResource');
        spyOn(CampaignsService, 'importResource');

        controller = $controller('IOImportController', {
            $scope: $scope,
            $translate: $translate,
            $uibModalInstance: $uibModalInstance,
            CONSTANTS: CONSTANTS,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            Utils: Utils,
            data: data
        });
    }));

    describe('activate()', function () {
        it('should set options configuration', function () {
            expect(controller.options.description).toEqual($translate.instant('media.import.description'));
            expect(controller.options.fileType).toEqual(CONSTANTS.INSERTION_ORDER.FILE_TYPE.XLSX);
            expect(controller.options.limitSize).toEqual(CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.LIMIT_SIZE);
            expect(controller.options.url).toEqual(CONSTANTS.INSERTION_ORDER.IMPORT.TEMPLATE_URL);
            expect(controller.options.urlTitle).toEqual($translate.instant('media.import.urlTitle'));
            expect(controller.options.statusEmptyFile).toEqual($translate.instant('media.import.status.cannotBeEmpty'));
            expect(controller.options.statusExceedFileOption).toEqual(
                CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.EXCEED_FILE_OPTION);
            expect(controller.options.statusLimitSize).toEqual($translate.instant('global.exceedSizeFile', {
                size: (CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.LIMIT_SIZE / 1024).toLocaleString()
            }));
            expect(controller.options.statusUnknown).toEqual($translate.instant('media.import.status.cannotUpload'));
            expect(controller.options.statusUnsupportedFormat).toEqual(
                $translate.instant('media.import.status.unsupportedFormat'));
        });
    });

    describe('close()', function () {
        it('should call close()', function () {
            controller.close({});

            expect($uibModalInstance.close).toHaveBeenCalled();
        });

        it('should call dismiss()', function () {
            controller.close();

            expect($uibModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe('importError()', function () {
        it('should invoke DialogFactory.showCustomDialog()', function () {
            controller.importError(message);

            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith(
                {
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    partialHTML: warningURL,
                    partialHTMLParams: {
                        message: message
                    }
                });
        });
    });

    describe('importMedia()', function () {
        it('should invoke CampaignsService.importResource', function () {
            var uuid = '123-456';

            controller.importMedia(uuid);

            expect(CampaignsService.importResource).toHaveBeenCalledWith(
                data.campaignId,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION,
                uuid,
                false);
        });
    });

    describe('uploadError()', function () {
        it('should invoke DialogFactory.showCustomDialog()', function () {
            controller.uploadError(message);

            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith(
                {
                    type: CONSTANTS.DIALOG.TYPE.WARNING,
                    title: $translate.instant('DIALOGS_WARNING'),
                    partialHTML: warningURL,
                    partialHTMLParams: {
                        message: message
                    }
                });
        });
    });

    describe('uploadMedia()', function () {
        it('should invoke CampaignsService.uploadResource', function () {
            var file = {
                name: 'name'
            };

            controller.uploadMedia(file);

            expect(CampaignsService.uploadResource).toHaveBeenCalledWith(
                data.campaignId,
                file,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION);
        });
    });

    describe('uploadSuccess()', function () {
        it('should invoke DialogFactory.showDismissableMessage()', function () {
            controller.uploadSuccess();

            expect(DialogFactory.showDismissableMessage).toHaveBeenCalledWith(
                DialogFactory.DISMISS_TYPE.SUCCESS, $translate.instant('info.operationCompleted'));
        });
    });
});
