'use strict';

describe('Directive: focusElement', function () {
    var $httpBackend,
        element,
        scope;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        scope = $rootScope.$new();
    }));

    it('should set focus in the second input', inject(function ($compile, $timeout) {
        element = $compile(angular.element(
            '<form name="form">' +
            '  <input data-ng-model="inputOne" name="inputOne" />' +
            '  <input data-ng-model="inputTwo" name="inputTwo" focus-element/>' +
            '</form>'
        ))(scope);
        scope.$digest();

        var input = element.find('input'),
            inputTwo = input[1];

        spyOn(inputTwo, 'focus');
        $timeout.flush(1);
        expect(inputTwo.focus).toHaveBeenCalled();
    }));

    it('should set focus in the last input', inject(function ($compile, $timeout) {
        element = $compile(angular.element(
                '<form name="form">' +
                '  <input data-ng-model="inputOne" name="inputOne" />' +
                '  <input data-ng-model="inputTwo" name="inputTwo" />' +
                '  <input data-ng-model="inputThree" name="inputThree" />' +
                '  <input data-ng-model="inputFour" name="inputFour" focus-element/>' +
                '</form>'
        ))(scope);
        scope.$digest();

        var input = element.find('input'),
            inputLast = input[3];

        spyOn(inputLast, 'focus');
        $timeout.flush(1);
        expect(inputLast.focus).toHaveBeenCalled();
    }));
});
