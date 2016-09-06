'use strict';

describe('Directive: aDisabled', function () {
    var $httpBackend,
        element,
        scope;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        scope = $rootScope.$new();
    }));

    it('should make element disabled', inject(function ($compile) {
        scope.disabled = true;
        element = angular.element('<a a-disabled="disabled" href="">Text</a>');
        element = $compile(element)(scope);
        scope.$apply();
        expect(element.text()).toBe('Text');
        expect(element.hasClass('disabled')).toBe(true);
    }));

    it('should make element enabled', inject(function ($compile) {
        scope.disabled = false;
        element = angular.element('<a a-disabled="disabled" href="">Text</a>');
        element = $compile(element)(scope);
        scope.$apply();
        expect(element.text()).toBe('Text');
        expect(element.hasClass('disabled')).toBe(false);
    }));
});
