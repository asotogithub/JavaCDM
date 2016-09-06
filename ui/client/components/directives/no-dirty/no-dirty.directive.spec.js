'use strict';

describe('Directive: noDirty', function () {
    var element,
        scope;

    beforeEach(module('te.components'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should not set the form as dirty when the input value is modified', inject(function ($compile) {
        element = $compile(angular.element(
          '<form name="theForm">' +
          '  <input name="theField" data-ng-model="theField" data-no-dirty />' +
          '</form>'
        ))(scope);
        scope.theForm.theField.$commitViewValue();

        expect(scope.theForm.$pristine).toBe(true);
    }));
});
