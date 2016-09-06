'use strict';

describe('Directive: teTableFilter', function () {
    var element,
        scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    it('should invoke filter function when filtered elements changes in `SINGLE` selection mode',
        inject(function ($compile) {
        var filter = jasmine.createSpy('filter'),
            filtered = [
                {
                    foo: 'bar'
                }
            ],
            $table;

        scope.vm = {
            filter: filter
        };
        element = $compile(angular.element(
                '<te-table data-selection-mode="SINGLE" te-table-filter="vm.filter($filtered)">' +
                '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(filter).not.toHaveBeenCalled();

        $table.filtered = filtered;
        scope.$apply();
        expect(filter).toHaveBeenCalledWith(filtered);

        $table.filtered = undefined;
        scope.$apply();
        expect(filter).toHaveBeenCalledWith(undefined);
    }));

    it('should invoke filter function when filtered elements changes in `MULTI` selection mode',
        inject(function ($compile) {
        var filter = jasmine.createSpy('filter'),
            filtered = [
                {
                    foo: 'bar'
                },
                {
                    bar: 'foobar'
                }
            ],
            $table;

        scope.vm = {
            filter: filter
        };
        element = $compile(angular.element(
                '<te-table data-selection-mode="MULTI" te-table-filter="vm.filter($filtered)">' +
                '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(filter).not.toHaveBeenCalled();

        $table.filtered = [filtered[0]];
        scope.$apply();
        expect(filter).toHaveBeenCalledWith([filtered[0]]);

        $table.filtered = [filtered[1]];
        scope.$apply();
        expect(filter).toHaveBeenCalledWith([filtered[1]]);

        $table.filtered = filtered;
        scope.$apply();
        expect(filter).toHaveBeenCalledWith(filtered);
    }));

    it('should get only filtered elements according searchTerm value',
        inject(function ($compile) {
        var filter = jasmine.createSpy('filter'),
            data = [
                {
                    name: 'name 1',
                    size: '200x200'
                },
                {
                    name: 'name 2',
                    size: '100x200'
                },
                {
                    name: 'name 3',
                    size: '300x300'
                }
            ],
            $table;

        scope.vm = {
            filter: filter,
            model: data
        };
        element = $compile(angular.element(
                '<te-table data-model="vm.model" te-table-filter="vm.filter($filtered)">' +
                    '<table>' +
                        '<tr class="actionable-row" data-ng-repeat="myData in $data">' +
                            '<td data-title="Name" data-searchable="true" data-sortable="\'name\'">' +
                                '{{myData.name}}' +
                            '</td>' +
                            '<td data-title="Size" data-searchable="true" data-sortable="\'size\'">' +
                                '{{myData.size}}' +
                            '</td>' +
                        '</tr>' +
                    '</table>' +
                '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.isolateScope().$table;

        expect(filter).toHaveBeenCalledWith(data);

        $table.searchTerm = '200';
        scope.$apply();
        expect(filter).toHaveBeenCalledWith([data[0], data[1]]);

        $table.searchTerm = 'name 3';
        scope.$apply();
        expect(filter).toHaveBeenCalledWith([data[2]]);

        $table.searchTerm = 'not existing';
        scope.$apply();
        expect(filter).toHaveBeenCalledWith([]);
    }));
});
