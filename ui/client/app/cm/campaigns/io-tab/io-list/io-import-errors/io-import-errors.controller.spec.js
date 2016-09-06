'use strict';

describe('Controller: IOImportErrorsController', function () {
    var $uibModalInstance,
        $scope,
        CONSTANTS,
        CampaignsService,
        Utils,
        controller,
        data,
        request,
        resourcesMedia = {
            messageSuccess: 'media.import.success',
            messageNoUpdate: 'media.import.noChanges',
            mainDescription: 'media.warningIssues.mainDescription',
            completeImport: 'media.warningIssues.completeImport'
        },
        successMessage;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _CampaignsService_, _CONSTANTS_, _Utils_) {
        $scope = $rootScope.$new();
        CONSTANTS = _CONSTANTS_;
        CampaignsService = _CampaignsService_;
        Utils = _Utils_;
        data = {
            campaignId: 123456,
            response: {
                error: [
                    {
                        message: 'Creative Weight is invalid. It must be a whole number equal ' +
                            'to or between 0 and 10,000',
                        rownum: 6,
                        type: 'ERROR'
                    },
                    {
                        message: 'Creative Weight is invalid. It must be a whole number equal ' +
                            'to or between 0 and 10,000',
                        rownum: 10,
                        type: 'WARNING'
                    },
                    {
                        message: 'The placement with Site, Section and Size combination: RichardSite - ' +
                            'RichardSection - 100x80, already exists. What would you like to do?',
                        inAppType: 'DUPLICATED_PLACEMENT',
                        rownum: 6,
                        type: 'WARNING',
                        options: ['DUPLICATE', 'DO_NOT_IMPORT'],
                        defaultOption: 'DUPLICATE'
                    }
                ],
                invalidCount: 2,
                total: 3,
                validCount: 1,
                status: false
            },
            uuid: 'f95b099c-e451-4036-96f4-bc9560cd3220'
        };

        $uibModalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };

        successMessage = {
            status: '200',
            message: '200 scheduling insertions were successfully updated!'
        };

        controller = $controller('IOImportErrorsController', {
            $scope: $scope,
            $uibModalInstance: $uibModalInstance,
            CONSTANTS: CONSTANTS,
            CampaignsService: CampaignsService,
            Utils: Utils,
            data: data
        });
    }));

    describe('Active() IOImportErrors', function () {
        it('Should has summary errors and Media Resources', function () {
            expect(controller.config.campaignId).toEqual(data.campaignId);
            expect(controller.model.errors.length +
                controller.model.warnings.length + controller.model.actions.length).toEqual(data.response.error.length);
            expect(controller.model.actions.length).toEqual(1);
            expect(controller.config.totalRows).toEqual(data.response.total);
            expect(controller.config.invalidRows).toEqual(data.response.invalidCount);
            expect(controller.config.rowsUpdate).toEqual(data.response.validCount);
            expect(controller.config.resources.messageSuccess).toEqual(resourcesMedia.messageSuccess);
            expect(controller.config.resources.messageNoUpdate).toEqual(resourcesMedia.messageNoUpdate);
            expect(controller.config.resources.mainDescription).toEqual
                ('We found <span id="rowsIssues" class="text-highlight">2</span>' +
                    ' row(s) with issues inside your media import sheet...');
            expect(controller.config.resources.completeImport).toEqual
                ('Select "Import" below to complete your media import...');
        });

        it('Should select default action', function () {
            expect(controller.model.actions[0].actionSelected.action).toEqual(data.response.error[2].defaultOption);
        });
    });

    describe('Close Modal', function () {
        it('Should call close modal', function () {
            controller.closeModal({});

            expect($uibModalInstance.close).toHaveBeenCalled();
        });

        it('Should call dismiss modal', function () {
            controller.closeModal();

            expect($uibModalInstance.dismiss).toHaveBeenCalled();
        });
    });

    describe('Call Complete Bulk', function () {
        it('Should  return message success.', function () {
            controller.completeBulk = jasmine.createSpy('completeBulk').andCallFake(function () {
                return successMessage;
            });

            request = controller.completeBulk();
            expect(request.status).toEqual(successMessage.status);
            expect(request.message).toEqual(successMessage.message);
        });
    });
});
