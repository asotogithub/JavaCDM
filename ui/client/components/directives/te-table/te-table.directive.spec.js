'use strict';

describe('Directive: teTable', function () {
    var $compile,
        element,
        onCounterSearch,
        scope;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-add-btn/te-table-add-btn.html'));
    beforeEach(module('components/directives/te-table/te-table-edit-btn/te-table-edit-btn.html'));
    beforeEach(module('components/directives/te-table/te-table-remove-btn/te-table-remove-btn.html'));
    beforeEach(module('components/directives/te-table/te-table-select-all-checkbox/te-table-select-all-checkbox.html'));

    beforeEach(inject(function ($rootScope, _$compile_) {
        $compile = _$compile_;
        onCounterSearch = jasmine.createSpy('onCounterSearch');
        scope = _.extend($rootScope.$new(), {
            vm: {
                model: [
                    {
                        foo: 'foo1',
                        bar: 'bar1',
                        foobar: 'foobar1',
                        foobard: 'foobard1'
                    },
                    {
                        foo: 'foo2',
                        bar: 'bar2',
                        foobar: 'foobar2',
                        foobard: 'foobard2'
                    }
                ],
                onCounterSearch: onCounterSearch
            }
        });

        element = $compile(angular.element(
            '<te-table data-empty-message="\'someEmptyMessage\'"' +
            '          data-model="vm.model"' +
            '          data-page-size="10"' +
            '          data-pinned-rows="{foo: \'bar\'}"' +
            '          data-selection-mode="MULTI">' +
            '  <te-table-btns>' +
            '    <te-table-add-btn id="addBtn"></te-table-add-btn>' +
            '    <te-table-remove-btn id="removeBtn"></te-table-remove-btn>' +
            '  </te-table-btns>' +
            '  <table>' +
            '    <tr data-ng-repeat="row in $data">' +
            '      <td data-title="\'Foo\'">{{row.foo}}</td>' +
            '      <td data-title="\'Bar\'" data-sortable="\'bar\'" data-searchable="true">{{row.bar}}</td>' +
            '      <td data-title="\'Foobar\'" data-searchable="\'foobar\'">{{row.foobar}}</td>' +
            '      <td class="no-title">{{row.foobard}}</td>' +
            '    </tr>' +
            '  </table>' +
            '</te-table>'
        ))(scope);
        scope.$apply();
    }));

    describe('compile()', function () {
        describe('table', function () {
            var $table;

            beforeEach(function () {
                $table = element.find('table');
            });

            it('should have class `table`', function () {
                expect($table.hasClass('table')).toBe(true);
            });

            it('should have class `table-bordered`', function () {
                expect($table.hasClass('table-bordered')).toBe(true);
            });

            it('should have class `table-hover`', function () {
                expect($table.hasClass('table-hover')).toBe(true);
            });

            it('should have class `table-striped`', function () {
                expect($table.hasClass('table-striped')).toBe(true);
            });

            it('should have attribute data-ng-table set', function () {
                expect($table.data('ng-table')).toEqual('$table.tableParams');
            });

            it('should have attribute data-te-table-body set"', function () {
                expect(element.find('[data-te-table-body]')[0]).toBe($table[0]);
            });
        });

        describe('td', function () {
            it('should have attribute data-header-title set', function () {
                expect(element.find('td[data-title="\'Foo\'"]').data('header-title')).toEqual('\'Foo\'');
                expect(element.find('td[data-title="\'Bar\'"]').data('header-title')).toEqual('\'Bar\'');
                expect(element.find('td[data-title="\'Foobar\'"]').data('header-title')).toEqual('\'Foobar\'');
                expect(element.find('td.no-title').data('header-title')).toEqual('\'\'');
            });
        });

        describe('tr', function () {
            var $tr;

            beforeEach(function () {
                $tr = element.find('tbody tr');
            });

            it('should have attribute data-ng-class set', function () {
                expect($tr.data('ng-class')).toEqual('{info: $table.isRowSelected(row)}');
            });

            it('should have attribute data-ng-click set', function () {
                expect($tr.data('ng-click')).toEqual('$table.toggleSelection($event, row)');
            });

            it('should have select checkbox as first element in `MULTI` selection mode', function () {
                var $td = $tr.children().first(),
                    $div = $td.children().first(),
                    $label = $div.children().first(),
                    $input = $label.children('input'),
                    $span = $label.children('span');

                expect($td.data('header-title')).toEqual('\'\'');
                expect($td.data('header')).toEqual('\'components/directives/te-table/te-table-select-all-checkbox/' +
                    'te-table-select-all-checkbox.html\'');
                expect($td.children().length).toBe(1);

                expect($div.hasClass('checkbox')).toBe(true);
                expect($div.hasClass('c-checkbox')).toBe(true);
                expect($div.children().length).toBe(1);

                expect($label.children().length).toBe(2);

                expect($input.hasClass('te-table-select-checkbox')).toBe(true);
                expect($input.attr('type')).toEqual('checkbox');
                expect($input.data('ng-checked')).toEqual('$table.isRowSelected(row)');
                expect($input.data('ng-click')).toEqual('$table.toggleSelection($event, row)');

                expect($span.hasClass('fa')).toBe(true);
                expect($span.hasClass('fa-check')).toBe(true);
            });

            it('should not have select checkbox in `SINGLE` selection mode', function () {
                var _element = $compile(angular.element(
                    '<te-table data-empty-message="someEmptyMessage"' +
                    '          data-model="vm.model"' +
                    '          data-pinned-rows="{foo: \'bar\'}">' +
                    '  <te-table-btns>' +
                    '    <te-table-add-btn id="addBtn"></te-table-add-btn>' +
                    '    <te-table-remove-btn id="removeBtn"></te-table-remove-btn>' +
                    '  </te-table-btns>' +
                    '  <table>' +
                    '    <tr data-ng-repeat="row in $data">' +
                    '      <td data-title="\'Foo\'">{{row.foo}}</td>' +
                    '      <td data-title="\'Bar\'" data-sortable="\'bar\'" data-searchable="true">{{row.bar}}</td>' +
                    '      <td data-title="\'Foobar\'" data-searchable="\'foobar\'">{{row.foobar}}</td>' +
                    '      <td class="no-title">{{row.foobard}}</td>' +
                    '    </tr>' +
                    '  </table>' +
                    '</te-table>'
                ))(scope);

                scope.$apply();
                $tr = _element.find('tbody tr');

                expect($tr.children().first().data('title')).toEqual('\'Foo\'');
            });
        });

        describe('data options', function () {
            var $tr;

            beforeEach(function () {
                element = $compile(angular.element(
                    '<te-table data-empty-message="\'someEmptyMessage\'"' +
                    '          data-model="vm.model"' +
                    '          data-page-size="10"' +
                    '          data-pinned-rows="{foo: \'bar\'}"' +
                    '          data-on-counter-search="vm.onCounterSearch(counterSearch)"' +
                    '          data-selection-mode="SINGLE_CHECKBOX">' +
                    '  <te-table-btns>' +
                    '    <te-table-add-btn id="addBtn"></te-table-add-btn>' +
                    '    <te-table-edit-btn id="ediBtn"></te-table-edit-btn>' +
                    '  </te-table-btns>' +
                    '  <table>' +
                    '    <tr data-ng-repeat="row in $data">' +
                    '      <td data-title="\'Foo\'">{{row.foo}}</td>' +
                    '      <td data-title="\'Bar\'" data-sortable="\'bar\'" data-searchable="true">{{row.bar}}</td>' +
                    '      <td data-title="\'Foobar\'" data-searchable="\'foobar\'">{{row.foobar}}</td>' +
                    '      <td class="no-title">{{row.foobard}}</td>' +
                    '    </tr>' +
                    '  </table>' +
                    '</te-table>'
                ))(scope);
                scope.$apply();
                $tr = element.find('tbody tr');
            });

            it('should have select checkbox in `SINGLE_CHECKBOX` selection mode without option checkbox all',
                function () {
                var $td = $tr.children().first(),
                    $div = $td.children().first(),
                    $label = $div.children().first(),
                    $input = $label.children('input'),
                    $span = $label.children('span');

                expect($td.data('header-title')).toEqual('\'\'');
                expect($td.data('header')).toEqual('');
                expect($td.children().length).toBe(1);

                expect($div.hasClass('checkbox')).toBe(true);
                expect($div.hasClass('c-checkbox')).toBe(true);
                expect($div.children().length).toBe(1);

                expect($label.children().length).toBe(2);

                expect($input.hasClass('te-table-select-checkbox')).toBe(true);
                expect($input.attr('type')).toEqual('checkbox');
                expect($input.data('ng-checked')).toEqual('$table.isRowSelected(row)');
                expect($input.data('ng-click')).toEqual('$table.toggleSelection($event, row)');

                expect($span.hasClass('fa')).toBe(true);
                expect($span.hasClass('fa-check')).toBe(true);
            });

            it('should callback counter', function () {
                expect(onCounterSearch).toHaveBeenCalled();
            });
        });
    });

    describe('link()', function () {
        describe('controller', function () {
            var controller;

            beforeEach(function () {
                controller = element.isolateScope().$table;
            });

            describe('emptyMessage', function () {
                it('should be bound to the scope', function () {
                    expect(controller.emptyMessage).toEqual('someEmptyMessage');
                });
            });

            describe('model', function () {
                it('should be bound to the scope', function () {
                    expect(controller.model).toBe(scope.vm.model);
                });
            });

            describe('pageSize', function () {
                it('should be bound to the scope', function () {
                    expect(controller.pageSize).toEqual('10');
                });
            });

            describe('pinnedRows', function () {
                it('should be bound to the scope', function () {
                    expect(controller.pinnedRows).toEqual({
                        foo: 'bar'
                    });
                });
            });

            describe('selectionMode', function () {
                it('should be bound to the scope', function () {
                    expect(controller.selectionMode).toEqual('MULTI');
                });
            });

            describe('defaultSort', function () {
                it('should be initialized as the first sortable field', function () {
                    expect(controller.defaultSort).toEqual('bar');
                });

                it('should be the column with data-sortable-default set to true', function () {
                    var _element = $compile(angular.element(
                            '<te-table data-empty-message="someEmptyMessage"' +
                            '          data-model="vm.model"' +
                            '          data-pinned-rows="{foo: \'bar\'}">' +
                            '  <te-table-btns>' +
                            '    <te-table-add-btn id="addBtn"></te-table-add-btn>' +
                            '    <te-table-remove-btn id="removeBtn"></te-table-remove-btn>' +
                            '  </te-table-btns>' +
                            '  <table>' +
                            '    <tr data-ng-repeat="row in $data">' +
                            '      <td data-title="\'Foo\'" data-sortable="\'foo\'">{{row.foo}}</td>' +
                            '      <td data-title="\'Bar\'"' +
                            '          data-sortable="\'bar\'"' +
                            '          data-sortable-default="true"' +
                            '          data-searchable="true">{{row.bar}}</td>' +
                            '      <td data-title="\'Foobar\'" data-searchable="\'foobar\'">{{row.foobar}}</td>' +
                            '      <td class="no-title">{{row.foobard}}</td>' +
                            '    </tr>' +
                            '  </table>' +
                            '</te-table>'
                    ))(scope);

                    scope.$apply();
                    controller = _element.isolateScope().$table;

                    expect(controller.defaultSort).toEqual('bar');
                });
            });

            describe('searchFields', function () {
                it('should be initialized with the searchable fields', function () {
                    var actual = _.map(controller.searchFields, function (field) {
                        return _.omit(field, '$$hashKey');
                    });

                    expect(actual).toEqual([
                        {
                            enabled: true,
                            title: 'Bar',
                            field: 'bar'
                        },
                        {
                            enabled: true,
                            title: 'Foobar',
                            field: 'foobar'
                        }
                    ]);
                });
            });

            describe('activate()', function () {
                it('should be invoked', function () {
                    expect(controller.tableParams.sorting()).toEqual({
                        bar: 'asc'
                    });
                });
            });
        });

        describe('transclude()', function () {
            it('should transclude table', function () {
                expect(element.find('table').parent().hasClass('te-table-body')).toBe(true);
                expect(element.find('tbody tr').length).toEqual(2);
            });

            it('should transclude buttons', function () {
                expect(element.find('#addBtn').closest('.te-table-btns')).toBeTruthy();
                expect(element.find('#removeBtn').closest('.te-table-btns')).toBeTruthy();
            });
        });
    });
});

