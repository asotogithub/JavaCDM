'use strict';

describe('Directive: onError', function () {
    var $scope,
        compile,
        element;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($compile, $rootScope, $httpBackend) {
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = $rootScope;
        element = angular.element(
                '<form name="form">' +
                '<img id="img" data-on-error="vm.onError()"/>' +
                '</form>'
        );

        $scope.vm = {
            error: 1,
            onError: function () {
                $scope.vm.error = 2;
            }
        };

        compile = $compile(element)($scope);
    }));

    describe('onError', function () {
        it('should test on-error directive"', function () {
            expect($scope.vm.error).toBe(1);
            angular.element(compile.find('img')).triggerHandler(angular.element.Event('error'));
            $scope.$apply();
            expect($scope.vm.error).toBe(2);
        });
    });
});
