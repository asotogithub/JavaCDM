'use strict';

describe('Directive: integerInput', function () {
    var $scope,
        element,
        triggerKeyDown = function (el, keyCode) {
            var e = angular.element.Event('keydown');

            e.which = keyCode;
            el.trigger(e);
        };

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($compile, $rootScope, $httpBackend) {
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope;
        element = angular.element(
                '<input type="number" name="inputNumber" data-ng-model="model.value" integer-input>'
        );

        $scope.model = {
            value: 1
        };
        $compile(element)($scope);
    }));

    describe('integerInput', function () {
        it('should input number without', function () {
            triggerKeyDown(element, 49); //1
            triggerKeyDown(element, 69); //e
            $scope.$digest();
            expect($scope.model.value).toBe(1);
        });
    });
});
