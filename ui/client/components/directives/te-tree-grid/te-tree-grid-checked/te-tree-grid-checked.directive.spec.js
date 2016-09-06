'use strict';

describe('Directive: teTreeGridChecked', function () {
    var $scope,
        element;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-tree-grid/te-tree-grid.html'));

    beforeEach(inject(function ($rootScope) {
        $scope = $rootScope.$new();
    }));

    it('should invoke expression on `rowCheck` event', inject(function ($compile) {
        var
            foo = {
                id: 1,
                name: 'foo'
            },
            bar = {
                id: 2,
                name: 'bar',
                children: [foo]
            },
            foobar = {
                id: 3,
                name: 'foobar'
            };

        _.extend($scope, {
            getAllChecked: jasmine.createSpy('getAllChecked'),
            model: [bar, foobar]
        });
        element = $compile(angular.element(
                '<te-tree-grid data-model="model"' +
                '              data-te-tree-grid-checked="getAllChecked($allChecked)"' +
                '              data-selection-mode="MULTI"' +
                '              data-selection-hierarchy="true">' +
                '  <te-columns>' +
                '    <te-column data-field="name"></te-column>' +
                '  </te-columns>' +
                '</te-tree-grid>'
        ))($scope);
        $scope.$apply();

        element.trigger(_.extend(new $.Event('rowCheck'), {
            args: {
                row: {
                    id: 1,
                    name: 'foo',
                    $$uuid: foo.$$uuid
                }
            }
        }));
        expect($scope.getAllChecked).toHaveBeenCalledWith([foo]);
    }));
});
