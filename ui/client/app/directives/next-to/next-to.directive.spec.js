'use strict';

describe('Directive: nextTo', function () {
    var $scope,
        form;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($compile, $rootScope, $httpBackend) {
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope;
        var element = angular.element(
                '<form name="form">' +
                '<input type="text" name="inputDate" data-ng-model="model.dateValue" data-next-to="01/01/2015"/>' +
                '</form>'
        );

        $scope.model = {
            dateValue: null
        };
        $compile(element)($scope);
        form = $scope.form;
    }));

    describe('nextTo', function () {
        it('should input date validated as the next day of "01/01/2015"', function () {
            form.inputDate.$setViewValue('01/02/2015');
            $scope.$digest();
            expect(form.inputDate.$invalid).toBe(true);
        });

        it('should input dates validated as invalid, the date is not the next day of "01/01/2015"', function () {
            form.inputDate.$setViewValue('01/05/2015');
            $scope.$digest();
            expect(form.inputDate.$invalid).toBe(true);
        });

        it('should input dates validated as invalid, the date is before "01/01/2015"', function () {
            form.inputDate.$setViewValue('12/01/2014');
            $scope.$digest();
            expect(form.inputDate.$invalid).toBe(true);
        });
    });
});
