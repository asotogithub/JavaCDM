'use strict';

describe('Controller: ScheduleImportController', function () {
    var $modalInstance,
        $scope,
        CampaignsService,
        ScheduleImportController,
        data,
        file;

    data = {
        campaignId: 6158572
    };

    file = {
        size: 24,
        name: 'Schedule_import.docx'
    };

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

        ScheduleImportController = $controller('ScheduleImportController', {
            $scope: $scope,
            $modalInstance: $modalInstance,
            CampaignsService: CampaignsService,
            data: data
        });

        ScheduleImportController.isValidFile = jasmine.createSpy('isValidFile').andCallFake(function (myFile) {
            var str = angular.lowercase(myFile.name),
                fileType = str.split(/[\s.]+/);

            fileType = fileType[fileType.length - 1];

            return fileType === 'xlsx';
        });
    }));

    it('Should create an instance of the controller Schedule Import.', function () {
        expect(ScheduleImportController).not.toBeUndefined();
    });

    it('should define isValidFile', function () {
        expect(ScheduleImportController.upload).toBeDefined();
    });

    it('Should be call upload function.', function () {
        spyOn(ScheduleImportController, 'upload');
        ScheduleImportController.upload([file]);
        expect(ScheduleImportController.upload).toHaveBeenCalled();
    });

    it('Should return false to invalid file.', function () {
        expect(ScheduleImportController.isValidFile(file)).toEqual(false);
    });

    it('Should return true to valid file.', function () {
        file.name = 'Schedule_import.xlsx';
        expect(ScheduleImportController.isValidFile(file)).toEqual(true);
    });

    it('Should Call Service Upload.', function () {
        spyOn(CampaignsService, 'importResource');
        CampaignsService.importResource(data.campaignId, file);
        expect(CampaignsService.importResource).toHaveBeenCalled();
    });
});
