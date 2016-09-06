'use strict';

describe('Directive: teTableSelect', function () {
    var element,
      scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should invoke expression when selection changes in `SINGLE` selection mode', inject(function ($compile) {
        var select = jasmine.createSpy('select'),
            selection = {
                foo: 'bar'
            },
            $table;

        scope.vm = {
            select: select
        };
        element = $compile(angular.element(
            '<te-table data-selection-mode="SINGLE" te-table-select="vm.select($selection)">' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(select).not.toHaveBeenCalled();

        $table.selection = [selection];
        scope.$apply();
        expect(select).toHaveBeenCalledWith(selection);

        $table.selection = [];
        scope.$apply();
        expect(select).toHaveBeenCalledWith(undefined);
    }));

    it('should invoke expression when selection changes in `MULTI` selection mode', inject(function ($compile) {
        var select = jasmine.createSpy('select'),
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
            select: select
        };
        element = $compile(angular.element(
            '<te-table data-selection-mode="MULTI" te-table-select="vm.select($selection)">' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(select).not.toHaveBeenCalled();

        $table.selection = [selection[0]];
        scope.$apply();
        expect(select).toHaveBeenCalledWith([selection[0]]);

        $table.selection = [selection[1]];
        scope.$apply();
        expect(select).toHaveBeenCalledWith([selection[1]]);

        $table.selection = selection;
        scope.$apply();
        expect(select).toHaveBeenCalledWith(selection);
    }));
});
