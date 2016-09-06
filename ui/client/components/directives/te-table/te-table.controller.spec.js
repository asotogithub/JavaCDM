'use strict';

describe('Controller: TeTableController', function () {
    var $q,
        $scope,
        TeTableController,
        creativeGroupsList;

    beforeEach(module('te.components'));

    beforeEach(inject(function ($controller, $rootScope, _$q_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        creativeGroupsList = $q.defer();

        TeTableController = $controller('TeTableController', {
            $scope: $scope
        });

        TeTableController.onCounterSearch = jasmine.createSpy('onCounterSearch').andReturn(
            creativeGroupsList.promise);
    }));

    describe('activate()', function () {
        it('should $broadcast(`te-table-activated`) on $scope.$parent', function () {
            spyOn($scope.$parent, '$broadcast');
            TeTableController.activate();

            expect($scope.$parent.$broadcast).toHaveBeenCalledWith('te-table-activated');
        });
    });

    describe('tableParams', function () {
        describe('page', function () {
            it('should be set to 1', function () {
                TeTableController.activate();

                expect(TeTableController.tableParams.page()).toEqual(1);
            });
        });

        describe('count', function () {
            it('should be set to pageSize', function () {
                TeTableController.pageSize = 69;
                TeTableController.activate();

                expect(TeTableController.tableParams.count()).toEqual(69);
            });
        });

        describe('sorting', function () {
            it('should be set if defaultSort is set', function () {
                TeTableController.defaultSort = 'name';
                TeTableController.activate();

                expect(TeTableController.tableParams.sorting()).toEqual({
                    name: 'asc'
                });
            });

            it('should be empty if defaultSort is not set', function () {
                TeTableController.activate();

                expect(TeTableController.tableParams.sorting()).toEqual({});
            });
        });

        describe('defaultSort', function () {
            it('should be `asc`', function () {
                TeTableController.activate();

                expect(TeTableController.tableParams.settings().defaultSort).toEqual('asc');
            });
        });

        describe('getData()', function () {
            var deferred,
                tableParams;

            beforeEach(function () {
                deferred = $q.defer();

                TeTableController.model = [
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    }
                ];
                TeTableController.activate();

                tableParams = TeTableController.tableParams;

                installPromiseMatchers();
            });

            it('should pinRows()', function () {
                TeTableController.pinnedRows = {
                    foo: 'foo20'
                };
                tableParams.settings().getData(deferred, tableParams);

                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    },
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    }
                ]);
                expect(TeTableController.onCounterSearch).toHaveBeenCalled();
            });

            it('should $filter() with `gridFilter`', function () {
                TeTableController.searchTerm = 'bar2';
                TeTableController.searchFields = [
                    {
                        field: 'foo',
                        enabled: false
                    },
                    {
                        field: 'bar',
                        enabled: true
                    },
                    {
                        field: 'foobar',
                        enabled: true
                    }
                ];

                tableParams.settings().getData(deferred, tableParams);

                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    }
                ]);
                expect(TeTableController.onCounterSearch).toHaveBeenCalled();
            });

            it('should $filter() with `orderBy`', function () {
                tableParams.sorting('foo', 'asc');
                tableParams.settings().getData(deferred, tableParams);
                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    },
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    }
                ]);
                expect(TeTableController.onCounterSearch).toHaveBeenCalled();

                deferred = $q.defer();
                tableParams.sorting('fieldWithTies', 'desc');
                tableParams.settings().getData(deferred, tableParams);
                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    },
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    }
                ]);
                expect(TeTableController.onCounterSearch).toHaveBeenCalled();

                deferred = $q.defer();
                tableParams.sorting('fieldWithTies', 'asc');
                tableParams.settings().getData(deferred, tableParams);
                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    },
                    {
                        foo: 'foo20',
                        bar: 'bar21',
                        foobar: 'foobar22',
                        foobard: 'foobard23',
                        fieldWithTies: 'b'
                    }
                ]);
                expect(TeTableController.onCounterSearch).toHaveBeenCalled();
            });

            it('should $filter() with `valuesFilter`', function () {
                TeTableController.filterValues = [
                    {
                        fieldName: 'foo',
                        values: [
                            'foo30',
                            'foo10'
                        ]
                    },
                    {
                        fieldName: 'bar',
                        values: [
                            'bar31',
                            'bar99'
                        ]
                    }
                ];

                tableParams.settings().getData(deferred, tableParams);

                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo30',
                        bar: 'bar31',
                        foobar: 'foobar32',
                        foobard: 'foobard33',
                        fieldWithTies: 'a'
                    }
                ]);
            });

            it('should set total', function () {
                tableParams.settings().getData(deferred, tableParams);
                $scope.$apply();

                expect(tableParams.settings().total).toEqual(3);
            });

            it('should page data if pageSize is set', function () {
                spyOn(tableParams, 'page').andReturn(2);
                spyOn(tableParams, 'count').andReturn(1);
                tableParams.settings().getData(deferred, tableParams);

                expect(deferred.promise).toBeResolvedWith([
                    {
                        foo: 'foo10',
                        bar: 'bar11',
                        foobar: 'foobar12',
                        foobard: 'foobard13',
                        fieldWithTies: 'a'
                    }
                ]);
            });

            it('should set count if pageSize is not set', function () {
                tableParams.settings().getData(deferred, tableParams);
                $scope.$apply();

                expect(tableParams.settings().counts).toEqual(3);
            });
        });
    });

    describe('allSelected', function () {
        var model,
            tableParams;

        beforeEach(function () {
            model = TeTableController.model = [];
            TeTableController.activate();

            tableParams = TeTableController.tableParams;
        });

        describe('get()', function () {
            it('should be false if no $data is available', function () {
                tableParams.settings().getData($q.defer(), tableParams);
                TeTableController.selection = [];

                expect(TeTableController.allSelected).toBe(false);
            });

            it('should be false if no $data is selected', function () {
                model.push({
                    foo: 'bar'
                });
                tableParams.settings().getData($q.defer(), tableParams);
                TeTableController.selection = [];

                expect(TeTableController.allSelected).toBe(false);
            });

            it('should be false if all $data is not selected', function () {
                var
                    row1 = {
                        foo: 'bar'
                    },
                    row2 = {
                        foo: 'foobar'
                    };

                model.push(row1);
                model.push(row2);
                tableParams.settings().getData($q.defer(), tableParams);
                TeTableController.selection = [row1];

                expect(TeTableController.allSelected).toBe(false);
            });

            it('should be true if all $data is selected', function () {
                var
                    row1 = {
                        foo: 'bar'
                    },
                    row2 = {
                        foo: 'foobar'
                    };

                model.push(row1);
                model.push(row2);
                tableParams.settings().getData($q.defer(), tableParams);
                TeTableController.selection = [row1, row2];

                expect(TeTableController.allSelected).toBe(true);
            });
        });

        describe('set()', function () {
            var row1,
                row2;

            beforeEach(function () {
                row1 = {
                    foo: 'bar'
                };
                row2 = {
                    foo: 'foobar'
                };
                model.push(row1);
                model.push(row2);
                tableParams.settings().getData($q.defer(), tableParams);
            });

            it('should add all $data to selection if set to true', function () {
                var row3 = {
                    foo: 'foobar`d'
                };

                TeTableController.selection = [row3, row2];
                TeTableController.allSelected = true;

                expect(TeTableController.selection).toEqual([row3, row2, row1]);
            });

            it('should remove all $data from selection if set to false', function () {
                var row3 = {
                    foo: 'foobar`d'
                };

                TeTableController.selection = [row1, row2, row3];
                TeTableController.allSelected = false;

                expect(TeTableController.selection).toEqual([row3]);
            });

            it('should reloadTable()', function () {
                spyOn(tableParams, 'reload');
                TeTableController.allSelected = false;

                expect(tableParams.reload).toHaveBeenCalled();
            });
        });
    });

    describe('model', function () {
        it('should resetTable() when changed', function () {
            var tableParams;

            TeTableController.activate();
            tableParams = TeTableController.tableParams;
            $scope.$apply();

            spyOn(tableParams, 'page');
            spyOn(tableParams, 'reload');
            $scope.$apply();
            expect(tableParams.page).not.toHaveBeenCalled();
            expect(tableParams.reload).not.toHaveBeenCalled();

            TeTableController.model = [{}];
            $scope.$apply();
            expect(tableParams.page).toHaveBeenCalledWith(1);
            expect(tableParams.reload).toHaveBeenCalled();
        });

        it('should remove missing selections', function () {
            var
                row1 = {
                    id: 1
                },
                row2 = {
                    id: 2
                },
                row3 = {
                    id: 3
                };

            TeTableController.activate();

            TeTableController.model = [row1, row2, row3];
            TeTableController.selection = [row2, row3];
            $scope.$apply();
            expect(TeTableController.selection).toEqual([row2, row3]);

            TeTableController.model = [row1, row2];
            TeTableController.selection = [row2, row3];
            $scope.$apply();
            expect(TeTableController.selection).toEqual([row2]);
        });
    });

    describe('searchTerm', function () {
        it('should resetTable() when changed', function () {
            var tableParams;

            TeTableController.activate();
            tableParams = TeTableController.tableParams;
            $scope.$apply();

            spyOn(tableParams, 'page');
            spyOn(tableParams, 'reload');
            $scope.$apply();
            expect(tableParams.page).not.toHaveBeenCalled();
            expect(tableParams.reload).not.toHaveBeenCalled();

            TeTableController.searchTerm = 'foobar';
            $scope.$apply();
            expect(tableParams.page).toHaveBeenCalledWith(1);
            expect(tableParams.reload).toHaveBeenCalled();
        });
    });

    describe('clearSearch()', function () {
        it('should clear searchTerm', function () {
            TeTableController.searchTerm = 'foobar';
            TeTableController.clearSearch();

            expect(TeTableController.searchTerm).toBeNull();
        });
    });

    describe('getSelection()', function () {
        var foo,
            bar,
            foobar;

        beforeEach(function () {
            foo = {
                id: 'foo'
            };
            bar = {
                id: 'bar'
            };
            foobar = {
                id: 'foobar'
            };
        });

        it('should return selection in `MULTI` selection mode', inject(function (CONSTANTS) {
            TeTableController.selectionMode = CONSTANTS.TE_TABLE.SELECTION_MODE.MULTI;

            TeTableController.selection = [foo, bar];
            expect(TeTableController.getSelection()).toEqual([foo, bar]);

            TeTableController.selection = [foobar];
            expect(TeTableController.getSelection()).toEqual([foobar]);
        }));

        it('should return selection[0] in `SINGLE` selection mode', inject(function (CONSTANTS) {
            TeTableController.selectionMode = CONSTANTS.TE_TABLE.SELECTION_MODE.SINGLE;

            TeTableController.selection = [foo, bar];
            expect(TeTableController.getSelection()).toEqual(foo);

            TeTableController.selection = [bar, foo];
            expect(TeTableController.getSelection()).toEqual(bar);

            TeTableController.selection = [foobar];
            expect(TeTableController.getSelection()).toEqual(foobar);
        }));
    });

    describe('isRowSelected()', function () {
        var foo,
            bar,
            foobar;

        beforeEach(function () {
            foo = {
                id: 'foo'
            };
            bar = {
                id: 'bar'
            };
            foobar = {
                id: 'foobar'
            };
        });

        it('should return true if row is in selection', function () {
            TeTableController.selection = [foo, bar, foobar];

            expect(TeTableController.isRowSelected(foo)).toBe(true);
            expect(TeTableController.isRowSelected(bar)).toBe(true);
            expect(TeTableController.isRowSelected(foobar)).toBe(true);
        });

        it('should return false otherwise', function () {
            TeTableController.selection = [];

            expect(TeTableController.isRowSelected(foo)).toBe(false);
            expect(TeTableController.isRowSelected(bar)).toBe(false);
            expect(TeTableController.isRowSelected(foobar)).toBe(false);
        });
    });

    describe('toggleSearchField()', function () {
        var foo,
            bar,
            foobar;

        beforeEach(function () {
            foo = {
                enabled: true
            };
            bar = {
                enabled: true
            };
            foobar = {
                enabled: true
            };

            TeTableController.searchFields = [foo, bar, foobar];
            TeTableController.activate();
        });

        it('should enable searchField if disabled', function () {
            foo.enabled = false;
            bar.enabled = false;

            TeTableController.toggleSearchField(new $.Event(), foo);
            expect(foo.enabled).toBe(true);

            TeTableController.toggleSearchField(new $.Event(), bar);
            expect(bar.enabled).toBe(true);
        });

        it('should resetTable() if searchField is toggled', function () {
            var tableParams = TeTableController.tableParams;

            spyOn(tableParams, 'page');
            spyOn(tableParams, 'reload');

            TeTableController.toggleSearchField(new $.Event(), foo);
            expect(tableParams.page.calls.length).toBe(1);
            expect(tableParams.reload.calls.length).toBe(1);

            TeTableController.toggleSearchField(new $.Event(), foo);
            expect(tableParams.page.calls.length).toBe(2);
            expect(tableParams.reload.calls.length).toBe(2);
        });

        it('should disable searchField if disabled', function () {
            TeTableController.toggleSearchField(new $.Event(), foo);
            expect(foo.enabled).toBe(false);

            TeTableController.toggleSearchField(new $.Event(), bar);
            expect(bar.enabled).toBe(false);
        });

        it('should not disable searchField if it is the last enabled field', function () {
            foo.enabled = false;
            bar.enabled = false;
            TeTableController.toggleSearchField(new $.Event(), foobar);

            expect(foobar.enabled).toBe(true);
        });

        it('should not reloadTable() if searchField is not toggled', function () {
            var tableParams = TeTableController.tableParams;

            foo.enabled = false;
            bar.enabled = false;
            spyOn(tableParams, 'reload');

            TeTableController.toggleSearchField(new $.Event(), foobar);
            expect(tableParams.reload).not.toHaveBeenCalled();
        });

        it('should preventDefault() and stopImmediatePropagation() of event', function () {
            var evt = new $.Event();

            TeTableController.toggleSearchField(evt, foo);

            expect(evt.isDefaultPrevented()).toBe(true);
            expect(evt.isImmediatePropagationStopped()).toBe(true);
        });
    });

    describe('toggleSelection()', function () {
        var foo,
            bar,
            foobar;

        beforeEach(function () {
            foo = {
                id: 'foo'
            };
            bar = {
                id: 'bar'
            };
            foobar = {
                id: 'foobar'
            };

            TeTableController.activate();
        });

        it('should do nothing if the parent fieldset is disabled', function () {
            var tableParams = TeTableController.tableParams,
                fieldSet = angular.element('<fieldset disabled="disabled"></fieldset>'),
                currentTarget = angular.element('<div></div>').appendTo(fieldSet),
                evt = new $.Event();

            evt.currentTarget = currentTarget;
            spyOn(tableParams, 'reload');
            TeTableController.toggleSelection(evt, foo);

            expect(evt.isDefaultPrevented()).toBe(false);
            expect(evt.isImmediatePropagationStopped()).toBe(false);
            expect(tableParams.reload).not.toHaveBeenCalled();
        });

        it('should preventDefault() and stopImmediatePropagation() of event', function () {
            var evt = new $.Event();

            TeTableController.toggleSelection(evt, foo);

            expect(evt.isDefaultPrevented()).toBe(true);
            expect(evt.isImmediatePropagationStopped()).toBe(true);
        });

        it('should reloadTable()', function () {
            var tableParams = TeTableController.tableParams;

            spyOn(tableParams, 'reload');
            TeTableController.toggleSelection(new $.Event(), foo);

            expect(tableParams.reload).toHaveBeenCalled();
        });

        describe('`MULTI` selection mode', function () {
            beforeEach(inject(function (CONSTANTS) {
                TeTableController.selectionMode = CONSTANTS.TE_TABLE.SELECTION_MODE.MULTI;
            }));

            it('should add row to selection if not already selected', function () {
                TeTableController.selection = [foo, bar];
                TeTableController.toggleSelection(new $.Event(), foobar);

                expect(TeTableController.selection).toEqual([foo, bar, foobar]);
            });

            it('should remove row from selection if already selected', function () {
                TeTableController.selection = [foo, bar, foobar];
                TeTableController.toggleSelection(new $.Event(), foobar);

                expect(TeTableController.selection).toEqual([foo, bar]);
            });
        });

        describe('`SINGLE` selection mode', function () {
            beforeEach(inject(function (CONSTANTS) {
                TeTableController.selectionMode = CONSTANTS.TE_TABLE.SELECTION_MODE.SINGLE;
            }));

            it('should replace selection with row if not already selected', function () {
                TeTableController.selection = [foo];

                TeTableController.toggleSelection(new $.Event(), bar);
                expect(TeTableController.selection).toEqual([bar]);

                TeTableController.toggleSelection(new $.Event(), foobar);
                expect(TeTableController.selection).toEqual([foobar]);
            });

            it('should remove row from selection if already selected', function () {
                TeTableController.selection = [foo];

                TeTableController.toggleSelection(new $.Event(), foo);
                expect(TeTableController.selection).toEqual([]);
            });
        });
    });
});
