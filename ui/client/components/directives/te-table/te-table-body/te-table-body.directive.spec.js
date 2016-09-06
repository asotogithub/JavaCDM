'use strict';

describe('Directive: teTableBody', function () {
    var element,
        scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
        installPromiseMatchers();
    }));

    it('should set scope.$table with TeTableController', inject(function ($compile, $q) {
        var deferred = $q.defer(),
            $table;

        scope.vm = {
            model: [
                {
                    foo: 13,
                    bar: 37
                },
                {
                    foo: 6,
                    bar: 9
                }
            ]
        };
        element = $compile(angular.element(
            '<te-table data-model="vm.model">' +
            '    <table></table>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        $table = element.find('[data-te-table-body]').scope().$table;

        expect($table).toBe(element.isolateScope().$table);
        $table.tableParams.settings().getData(deferred, $table.tableParams);

        expect(deferred.promise).toBeResolvedWith([
            {
                foo: 13,
                bar: 37
            },
            {
                foo: 6,
                bar: 9
            }
        ]);
    }));

    it('should set $scope on controller.tableParams.settings() $on(`te-table-activated`)', inject(function ($compile) {
        var tableScope,
            tableParams;

        element = $compile(angular.element(
            '<te-table data-model="vm.model">' +
            '    <table></table>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
        tableScope = element.find('[data-te-table-body]').scope();
        tableParams = tableScope.$table.tableParams;
        tableParams.settings().$scope = null;

        scope.$broadcast('te-table-activated');

        expect(tableParams.settings().$scope).toBe(tableScope);
    }));
});
