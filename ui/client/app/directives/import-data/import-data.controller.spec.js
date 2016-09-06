'use strict';

describe('Controller: ImportDataController', function () {
    var $scope,
        $translate,
        ImportDataController,
        closeEndValue,
        closeStartValue,
        data,
        file;

    file = {
        size: 24,
        name: 'Media_import.docx'
    };

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _$translate_, _CONSTANTS_, _Utils_) {
        data = {
            options: {
                description: 'description',
                url: 'url',
                utlTitle: 'urlTitle',
                statusEmptyFile: 'statusEmptyFile',
                statusExceedFileOption: 'statusExceedFileOption',
                statusLimitSize: 'statusLimitSize',
                statusUnknown: 'statusUnknown',
                statusUnsupportedFormat: 'statusUnsupportedFormat',
                fileType: 'XLSX',
                limitSize: 60
            },
            onClose: function () {
                closeStartValue = closeEndValue;
            }
        };

        $scope = $rootScope.$new();
        $translate = _$translate_;

        ImportDataController = $controller('ImportDataController', {
            $scope: $scope,
            $translate: $translate,
            CONSTANTS: _CONSTANTS_,
            Utils: _Utils_
        }, data);

        ImportDataController.isValidFile = jasmine.createSpy('isValidFile').andCallFake(function (myFile) {
            var str = angular.lowercase(myFile.name),
                fileType = str.split(/[\s.]+/);

            fileType = fileType[fileType.length - 1];

            return fileType === 'xlsx';
        });
    }));

    it('Should create an instance of the controller Import Data.', function () {
        expect(ImportDataController).not.toBeUndefined();
    });

    it('Should set popUp texts.', function () {
        expect(ImportDataController.popupText.description).toEqual(data.options.description);
        expect(ImportDataController.popupText.url).toEqual(data.options.url);
        expect(ImportDataController.popupText.urlTitle).toEqual(data.options.urlTitle);
        expect(ImportDataController.popupText.cancelButton).toEqual($translate.instant('global.cancel'));
        expect(ImportDataController.popupText.dragDrop).toEqual($translate.instant('global.dropYourFile'));
        expect(ImportDataController.popupText.note).toEqual($translate.instant('global.note'));
        expect(ImportDataController.popupText.importButton).toEqual($translate.instant('global.import'));
        expect(ImportDataController.popupText.openButton).toEqual($translate.instant('global.single'));
    });

    it('should define isValidFile', function () {
        expect(ImportDataController.upload).toBeDefined();
    });

    it('Should be call upload function.', function () {
        spyOn(ImportDataController, 'upload');
        ImportDataController.upload([file]);
        expect(ImportDataController.upload).toHaveBeenCalled();
    });

    it('Should return false to invalid file.', function () {
        expect(ImportDataController.isValidFile(file)).toEqual(false);
    });

    it('Should return true to valid file.', function () {
        file.name = 'Media_import.xlsx';
        expect(ImportDataController.isValidFile(file)).toEqual(true);
    });

    it('Should be call cancel function.', function () {
        var abortStartValue = 1,
            abortEndValue = 2;

        closeStartValue = 1;
        closeEndValue = 2;

        ImportDataController.uploading = {
            isCancelByUser: false,
            upload: {
                abort: function () {
                    abortStartValue = abortStartValue + 1;
                }
            }
        };

        ImportDataController.cancel();

        expect(ImportDataController.uploading.isCancelByUser).toEqual(true);
        expect(closeStartValue).toEqual(closeEndValue);
        expect(abortStartValue).toEqual(abortEndValue);
    });

    it('Should be call importProcess function.', function () {
        spyOn(ImportDataController, 'importProcess');
        ImportDataController.importProcess();
        expect(ImportDataController.importProcess).toHaveBeenCalled();
    });
});
