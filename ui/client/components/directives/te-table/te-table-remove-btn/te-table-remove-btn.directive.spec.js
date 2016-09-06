'use strict';

describe('Directive: teTableRemoveBtn', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-remove-btn/te-table-remove-btn.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should transclude button content', inject(function ($compile) {
        element = $compile(angular.element(
            '<te-table>' +
            '  <te-table-btns>' +
            '    <te-table-remove-btn id="teTableRemoveBtn">foobar</te-table-remove-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();

        expect(element.find('#teTableRemoveBtn').text().trim()).toBe('foobar');
    }));

    it('should use data-te-table-btn-selected directive', inject(function ($compile) {
        var $table;

        scope.model = [{}];
        element = $compile(angular.element(
            '<te-table data-model="model">' +
            '  <te-table-btns>' +
            '    <te-table-remove-btn id="teTableRemoveBtn">foobar</te-table-remove-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(element.find('#teTableRemoveBtn').is(':disabled')).toBe(true);

        $table.selection = [{}];
        scope.$apply();
        expect(element.find('#teTableRemoveBtn').is(':disabled')).toBe(false);

        $table.selection = [{}, {}];
        scope.$apply();
        expect(element.find('#teTableRemoveBtn').is(':disabled')).toBe(false);

        scope.model.pop();
        scope.$apply();
        expect(element.find('#teTableRemoveBtn').is(':disabled')).toBe(true);
    }));
});
