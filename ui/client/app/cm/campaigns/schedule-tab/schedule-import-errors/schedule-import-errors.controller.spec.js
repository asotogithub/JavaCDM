'use strict';

describe('Controller: ScheduleImportErrorsController', function () {
    var $modalInstance,
        $scope,
        CampaignsService,
        ScheduleImportErrorsController,
        data,
        file,
        request,
        successMessage;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _CampaignsService_) {
        $modalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };
        $scope = $rootScope.$new();
        CampaignsService = _CampaignsService_;

        data = {
            campaignId: 6158572
        };

        file = {
            size: 24,
            name: 'Schedule_6158572_issues.xlsx'
        };

        successMessage = {
            status: '200',
            message: '200 scheduling insertions were successfully updated!'
        };

        ScheduleImportErrorsController = $controller('ScheduleImportController', {
            $scope: $scope,
            $modalInstance: $modalInstance,
            CampaignsService: CampaignsService,
            data: data
        });
    }));

    it('Should Call Complete Bulk and return message success.', function () {
        ScheduleImportErrorsController.completeBulk = jasmine.createSpy('completeBulk').andCallFake(function () {
            return successMessage;
        });

        request = ScheduleImportErrorsController.completeBulk();
        expect(request.status).toEqual(successMessage.status);
        expect(request.message).toEqual(successMessage.message);
    });

    it('Should Call Export Issues and return a file.', function () {
        ScheduleImportErrorsController.exportIssues = jasmine.createSpy('exportIssues').andCallFake(function () {
            return file;
        });

        request = ScheduleImportErrorsController.exportIssues();
        expect(request.size).toEqual(file.size);
        expect(request.name).toEqual(file.name);
    });
});
