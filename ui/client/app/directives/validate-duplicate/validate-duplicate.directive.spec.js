'use strict';

describe('Directive: validateDuplicate', function () {
    var $scope,
        form;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($compile, $rootScope, $httpBackend) {
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope;
        var element = angular.element(
            '<form name="form">' +
            '<input type="text" name="inputDuplicate" data-ng-model="model.cookieName" ' +
            'validate-duplicate="model.listCookieName" form-duplicates="form"/>' +
            '</form>'
        );

        $scope.model = {
            cookieName: '',
            listCookieName: ['duplicate', 'duplicate', '01', '02', '03']
        };
        $compile(element)($scope);
        form = $scope.form;
    }));

    describe('validateDuplicate', function () {
        it('should validate duplicate as invalid', function () {
            form.inputDuplicate.$setViewValue('duplicate');
            $scope.$digest();
            expect(form.inputDuplicate.$invalid).toBe(true);
        });

        it('should input dates validated as valid', function () {
            form.inputDuplicate.$setViewValue('0');
            $scope.$digest();
            expect(form.inputDuplicate.$invalid).toBe(false);
        });
    });
});
