'use strict';

describe('Directive: teTableEditBtn', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-edit-btn/te-table-edit-btn.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should transclude button content', inject(function ($compile) {
        element = $compile(angular.element(
            '<te-table>' +
            '  <te-table-btns>' +
            '    <te-table-edit-btn id="teTableEditBtn">foobar</te-table-edit-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();

        expect(element.find('#teTableEditBtn').text().trim()).toBe('foobar');
    }));

    it('should use data-te-table-btn-single-selected directive', inject(function ($compile) {
        var $table;

        scope.model = [{}];
        element = $compile(angular.element(
            '<te-table data-model="model">' +
            '  <te-table-btns>' +
            '    <te-table-edit-btn id="teTableEditBtn">foobar</te-table-edit-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(element.find('#teTableEditBtn').is(':disabled')).toBe(true);

        $table.selection = [{}];
        scope.$apply();
        expect(element.find('#teTableEditBtn').is(':disabled')).toBe(false);

        $table.selection = [{}, {}];
        scope.$apply();
        expect(element.find('#teTableEditBtn').is(':disabled')).toBe(true);

        scope.model.pop();
        scope.$apply();
        expect(element.find('#teTableEditBtn').is(':disabled')).toBe(true);
    }));
});
