'use strict';

describe('Controller: DuplicateScheduleAssignmentsController', function () {
    var $modalInstance,
        $scope,
        DuplicateScheduleAssignmentsController,
        duplicates;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope) {
        var data = {};

        $modalInstance = {
            close: jasmine.createSpy('dismiss')
        };
        $scope = $rootScope.$new();
        duplicates = data.duplicates = [
            {
                campaignId: 9084263,
                creativeAlias: '200x100',
                creativeClickthrough: 'http://www.google.com',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: false,
                creativeId: 9108242,
                endDate: '2015-08-06T00:00:00-06:00',
                placementEndDate: '2015-08-06T00:00:00-06:00',
                placementId: 9108260,
                placementName: 'IO',
                placementStartDate: '2015-07-07T00:00:00-06:00',
                placementStatus: 'New',
                siteId: 9107159,
                siteName: 'IOS SITE',
                siteSectionId: 9107160,
                siteSectionName: 'SITE IOS',
                sizeName: '200x100',
                startDate: '2015-07-07T00:00:00-06:00',
                weight: 100
            },
            {
                campaignId: 9084263,
                creativeAlias: 'Super Expert chiken',
                creativeClickthrough: 'http://www.trueffect.com',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: false,
                creativeId: 9128897,
                endDate: '2015-08-08T00:00:00-06:00',
                placementEndDate: '2015-08-08T00:00:00-06:00',
                placementId: 9110480,
                placementName: 'sport - Header - 100x100',
                placementStartDate: '2015-07-09T00:00:00-06:00',
                placementStatus: 'New',
                siteId: 9107208,
                siteName: 'sport',
                siteSectionId: 9107209,
                siteSectionName: 'Header',
                sizeName: '100x100',
                startDate: '2015-07-09T00:00:00-06:00',
                weight: 100
            }
        ];

        DuplicateScheduleAssignmentsController = $controller('DuplicateScheduleAssignmentsController', {
            $modalInstance: $modalInstance,
            $scope: $scope,
            data: data
        });
    }));

    describe('$scope', function () {
        describe('vm', function () {
            it('should be the controller', function () {
                expect($scope.vm).toBe(DuplicateScheduleAssignmentsController);
            });
        });
    });

    describe('activate()', function () {
        it('should set duplicates', function () {
            expect(DuplicateScheduleAssignmentsController.duplicates).toBe(duplicates);
        });
    });

    describe('dismiss()', function () {
        it('should dismiss() $modalInstance', function () {
            DuplicateScheduleAssignmentsController.dismiss();

            expect($modalInstance.close).toHaveBeenCalled();
        });
    });
});
