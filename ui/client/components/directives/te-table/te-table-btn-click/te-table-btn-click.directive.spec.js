'use strict';

/*global $:true*/
describe('Directive: teTableBtnClick', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-edit-btn/te-table-edit-btn.html'));
    beforeEach(module('components/directives/te-table/te-table-remove-btn/te-table-remove-btn.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should include $selection for `SINGLE` selectionMode', inject(function ($compile) {
        var edit = jasmine.createSpy('edit'),
            selection = {
                foo: 'bar'
            },
            $table;

        scope.vm = {
            edit: edit
        };
        element = $compile(angular.element(
            '<te-table data-selection-mode="SINGLE">' +
            '  <te-table-btns>' +
            '    <te-table-edit-btn id="teTableEditBtn" data-te-table-btn-click="vm.edit($event, $selection)">' +
            '    </te-table-edit-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;
        $table.selection = [selection];

        element.find('#teTableEditBtn').click();
        expect(scope.vm.edit).toHaveBeenCalledWith(jasmine.any($.Event), selection);
    }));

    it('should include $selection for `MULTI` selectionMode', inject(function ($compile) {
        var remove = jasmine.createSpy('remove'),
            selection = [
                {
                    foo: 'bar'
                },
                {
                    bar: 'foobar'
                }
            ],
            $table;

        scope.vm = {
            remove: remove
        };
        element = $compile(angular.element(
            '<te-table data-selection-mode="MULTI">' +
            '  <te-table-btns>' +
            '    <te-table-remove-btn id="teTableRemoveBtn" data-te-table-btn-click="vm.remove($event, $selection)">' +
            '    </te-table-remove-btn>' +
            '  </te-table-btns>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;
        $table.selection = selection;

        element.find('#teTableRemoveBtn').click();
        expect(scope.vm.remove).toHaveBeenCalledWith(jasmine.any($.Event), selection);
    }));
});
