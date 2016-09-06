'use strict';

describe('Controller: IssuesContainerController', function () {
    var $scope,
        controller,
        file,
        request,
        successMessage,
        DialogFactory,
        DATA = {
            config: {
                campaignId: '6158572',
                rowsErrors: 2,
                totalRows: 3,
                rowsUpdate: 1,
                isValidComplete: false,
                pageSize: 10,
                uuid: 'f95b099c-e451-4036-96f4-bc9560cd3220',
                resources: {
                    completeImport: 'Select "Continue" below to complete your media import...',
                    mainDescription: 'We found 2 issue(s) with your media import sheet...'
                }
            },
            model: [
                {
                    row: 6,
                    description: 'Creative Weight is invalid. It must be a whole number equal ' +
                        'to or between 0 and 10,000'
                },
                {
                    row: 10,
                    description: 'Creative Weight is invalid. It must be a whole number equal ' +
                        'to or between 0 and 10,000'
                }
            ]
        };

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $q, $translate) {
        $scope = $rootScope.$new();
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog']);

        file = {
            size: 24,
            name: 'Schedule_6158572_issues.xlsx'
        };

        successMessage = {
            status: '200',
            message: '200 scheduling insertions were successfully updated!'
        };

        controller = $controller('IssuesContainerController', {
            $scope: $scope,
            $translate: $translate,
            DialogFactory: DialogFactory
        });
        controller.model = DATA.model;
        controller.config = DATA.config;
        controller.activate();
    }));

    describe('Issues Model', function () {
        it('Should load issues model and rows validates', function () {
            expect(controller.rowsErrors).toBe(2);
            expect(controller.rowsUpdate).toBe(1);
            expect(controller.model.length).toBe(2);
        });

        it('Should Call Complete Bulk and return message success.', function () {
            controller.completeBulk = jasmine.createSpy('completeBulk').andCallFake(function () {
                return successMessage;
            });

            request = controller.completeBulk();
            expect(request.status).toEqual(successMessage.status);
            expect(request.message).toEqual(successMessage.message);
        });

        it('Should Call Export Issues and return a file.', function () {
            controller.exportIssues = jasmine.createSpy('exportIssues').andCallFake(function () {
                return file;
            });

            request = controller.exportIssues();
            expect(request.size).toEqual(file.size);
            expect(request.name).toEqual(file.name);
        });
    });
});
