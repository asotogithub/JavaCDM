'use strict';

describe('Directive: teTableBtnSingleSelected', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should only enable button when table rows available and has one selected row', inject(function ($compile) {
        var $table;

        scope.model = null;
        element = $compile(angular.element(
            '<te-table data-model="model">' +
            '  <te-table-btns>' +
            '    <button id="btn" data-te-table-btn-single-selected>foobar</button>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(element.find('#btn').is(':disabled')).toBe(true);

        scope.model = [];
        scope.$apply();
        expect(element.find('#btn').is(':disabled')).toBe(true);

        scope.model = [{}];
        scope.$apply();
        expect(element.find('#btn').is(':disabled')).toBe(true);

        $table.selection = [{}];
        scope.$apply();
        expect(element.find('#btn').is(':disabled')).toBe(false);

        $table.selection = [{}, {}];
        scope.$apply();
        expect(element.find('#btn').is(':disabled')).toBe(true);

        scope.model.pop();
        scope.$apply();
        expect(element.find('#btn').is(':disabled')).toBe(true);
    }));
});
