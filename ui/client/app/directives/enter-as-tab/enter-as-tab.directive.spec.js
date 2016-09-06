'use strict';

describe('Directive: enterAsTab', function () {
    var $httpBackend,
        element,
        scope;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        scope = $rootScope.$new();
    }));

    it('should lost focus when enter key is pressed', inject(function ($compile) {
        element = $compile(angular.element(
          '<form name="form">' +
          '  <input data-ng-model="input1" name="input1" enter-as-tab/>' +
          '  <input data-ng-model="input2" name="input2"/>' +
          '</form>'
        ))(scope);
        scope.$digest();

        var input = element.find('input'),
            input1 = input[0],
            input2 = input[1],
            enterKey = angular.element.Event('keydown');

        enterKey.which = 13;
        spyOn(input1, 'focus');
        spyOn(input2, 'focus');

        input1.focus();
        expect(input1.focus).toHaveBeenCalled();

        input.trigger(enterKey);
        expect(input2.focus).toHaveBeenCalled();
    }));
});
