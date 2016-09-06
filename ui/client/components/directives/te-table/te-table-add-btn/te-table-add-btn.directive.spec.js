'use strict';

describe('Directive: teTableAddBtn', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-add-btn/te-table-add-btn.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should transclude button content', inject(function ($compile) {
        element = $compile(angular.element(
          '<te-table>' +
          '  <te-table-btns>' +
          '    <te-table-add-btn id="teTableAddBtn">foobar</te-table-add-btn>' +
          '  </te-table-btns>' +
          '</te-table>'
        ))(scope);
        scope.$apply();

        expect(element.find('#teTableAddBtn').text().trim()).toBe('foobar');
    }));
});
