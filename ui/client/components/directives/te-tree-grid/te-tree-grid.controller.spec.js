'use strict';

describe('Controller: TeTreeGridController', function () {
    var CONSTANTS,
        $element,
        $scope,
        $timeout,
        $window,
        TeTreeGridController,
        creativeInsertionList,
        dataview;

    beforeEach(module('te.components'));

    beforeEach(inject(function ($controller, $q, $rootScope, _$timeout_, _CONSTANTS_) {
        var $delegate = angular.element('<div class="te-tree-grid-delegate"></div>');

        CONSTANTS = _CONSTANTS_;
        $element = angular.element('<div></div>').append($delegate);
        $scope = $rootScope.$new();
        $timeout = _$timeout_;
        $window = {};
        dataview = {};
        creativeInsertionList = $q.defer();

        $delegate.data('jqxTreeGrid', {
            instance: {
                base: {
                    dataview: dataview
                }
            }
        });

        TeTreeGridController = $scope.vm = $controller('TeTreeGridController', {
            $element: $element,
            $scope: $scope,
            $timeout: $timeout,
            $window: $window
        });
    }));

    describe('activate()', function () {
        it('should set settings', function () {
            var columns = TeTreeGridController.columns = [
                {
                    cellsAlign: 'left',
                    dataField: 'status',
                    sortable: false,
                    text: 'Status',
                    width: '10%'
                },
                {
                    cellsAlign: 'left',
                    dataField: 'ioName',
                    sortable: true,
                    text: 'IO Name',
                    width: '17.5%'
                }
            ];

            TeTreeGridController.paginationEnabled = true;
            TeTreeGridController.pageSize = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.SMALL;
            TeTreeGridController.activate();

            expect(_.omit(TeTreeGridController.settings, _.isFunction)).toEqual({
                editable: false,
                pageable: true,
                pageSize: 30,
                editSettings: {
                    saveOnPageChange: true,
                    saveOnBlur: true,
                    saveOnSelectionChange: true,
                    cancelOnEsc: true,
                    saveOnEnter: true,
                    editSingleCell: true,
                    editOnDoubleClick: true,
                    editOnF2: true
                },
                columns: columns,
                columnsHeight: 49,
                columnsResize: false,
                selectionMode: 'singlerow',
                sortable: true,
                theme: 'bootstrap',
                width: '100%'
            });
        });

        it('should set source on changes to model', function () {
            var model = TeTreeGridController.model = [
                    {
                        id: 1,
                        name: 'foo'
                    },
                    {
                        id: 2,
                        name: 'bar'
                    },
                    {
                        id: 3,
                        name: 'foobar'
                    }
                ],
                dataAdapter = new $.jqx.dataAdapter({
                    id: '$$uuid',
                    dataType: 'json',
                    localData: function () {
                        return model;
                    },

                    dataFields: [
                        {
                            name: '$$uuid',
                            type: 'string'
                        },
                        {
                            name: 'children',
                            type: 'array'
                        },
                        {
                            name: 'status',
                            type: 'string'
                        },
                        {
                            name: 'ioName',
                            type: 'string'
                        }
                    ],
                    hierarchy: {
                        root: 'children'
                    }
                });

            TeTreeGridController.dataFields = [
                {
                    name: 'status',
                    type: 'string'
                },
                {
                    name: 'ioName',
                    type: 'string'
                }
            ];
            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.dataAdapter._source.id).toEqual(dataAdapter._source.id);
            expect(TeTreeGridController.dataAdapter._source.dataType).toEqual(dataAdapter._source.dataType);
            expect(TeTreeGridController.dataAdapter._source.localData()).toEqual(dataAdapter._source.localData());
            expect(TeTreeGridController.dataAdapter._source.dataFields).toEqual(dataAdapter._source.dataFields);
            expect(TeTreeGridController.dataAdapter._source.hierarchy).toEqual(dataAdapter._source.hierarchy);
        });

        it('should set source w/ childrenField on changes to model', function () {
            var model = TeTreeGridController.model = [
                    {
                        id: 1,
                        name: 'foo'
                    },
                    {
                        id: 2,
                        name: 'bar'
                    },
                    {
                        id: 3,
                        name: 'foobar'
                    }
                ],
                dataAdapter = new $.jqx.dataAdapter({
                    id: '$$uuid',
                    dataType: 'json',
                    localData: function () {
                        return model;
                    },

                    dataFields: [
                        {
                            name: '$$uuid',
                            type: 'string'
                        },
                        {
                            name: 'childRows',
                            type: 'array'
                        },
                        {
                            name: 'status',
                            type: 'string'
                        },
                        {
                            name: 'ioName',
                            type: 'string'
                        }
                    ],
                    hierarchy: {
                        root: 'childRows'
                    }
                });

            TeTreeGridController.childrenField = 'childRows';
            TeTreeGridController.dataFields = [
                {
                    name: 'status',
                    type: 'string'
                },
                {
                    name: 'ioName',
                    type: 'string'
                }
            ];
            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.dataAdapter._source.id).toEqual(dataAdapter._source.id);
            expect(TeTreeGridController.dataAdapter._source.dataType).toEqual(dataAdapter._source.dataType);
            expect(TeTreeGridController.dataAdapter._source.localData()).toEqual(dataAdapter._source.localData());
            expect(TeTreeGridController.dataAdapter._source.dataFields).toEqual(dataAdapter._source.dataFields);
            expect(TeTreeGridController.dataAdapter._source.hierarchy).toEqual(dataAdapter._source.hierarchy);
        });

        it('should setRecordUuids() on changes to model', function () {
            var uuidRegexp = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/,
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

            TeTreeGridController.model = [bar, foobar];
            TeTreeGridController.activate();
            $scope.$apply();

            expect(foo.$$uuid).toMatch(uuidRegexp);
            expect(bar.$$uuid).toMatch(uuidRegexp);
            expect(foobar.$$uuid).toMatch(uuidRegexp);
        });

        it('should not overwrite existing record UUIDs on changes to model', function () {
            var
                foo = {
                    $$uuid: 'foo-uuid',
                    id: 1,
                    name: 'foo'
                },
                bar = {
                    $$uuid: 'bar-uuid',
                    id: 2,
                    name: 'bar',
                    children: [foo]
                },
                foobar = {
                    $$uuid: 'foobar-uuid',
                    id: 3,
                    name: 'foobar'
                };

            TeTreeGridController.model = [bar, foobar];
            TeTreeGridController.activate();
            $scope.$apply();

            expect(foo.$$uuid).toEqual('foo-uuid');
            expect(bar.$$uuid).toEqual('bar-uuid');
            expect(foobar.$$uuid).toEqual('foobar-uuid');
        });

        it('should triggerResize() on changes to model', function () {
            TeTreeGridController.activate();
            $scope.$apply();

            var resizeHandler = jasmine.createSpy();

            angular.element($window).resize(resizeHandler);
            TeTreeGridController.model = [];
            $scope.$apply();

            expect(resizeHandler).toHaveBeenCalled();
        });

        it('should cellEndEdit() on changes to model', function () {
            var eventMock = {
                args: {
                    dataField: 'weight',
                    row: {
                        editSupport: {
                            clickThroughUrl: true,
                            flightDateEnd: true,
                            flightDateStart: true,
                            weight: true
                        },
                        level: 0,
                        weight: 50
                    }

                }
            };

            TeTreeGridController.model = [
                {
                    level: 4,
                    weight: 100
                }
            ];

            expect(TeTreeGridController.model[0].weight).toEqual(100);
            TeTreeGridController.cellEndEdit(eventMock);
            expect(TeTreeGridController.model[0].weight).toEqual(50);
        });

        it('should customSearchEnabled be true', function () {
            TeTreeGridController.teTreeGridCustomSearch = 'true';
            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.customSearchEnabled).toBe(true);
        });

        it('should customSearchEnabled be false', function () {
            TeTreeGridController.teTreeGridCustomSearch = 'false';
            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.customSearchEnabled).toBe(false);
        });

        it('should customSearchEnabled be false when teTreeGridCustomSearch is not defined', function () {
            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.customSearchEnabled).toBe(false);
        });

        it('should read teTreeGridSearchOptions', function () {
            TeTreeGridController.teTreeGridCustomSearch = 'true';
            TeTreeGridController.teTreeGridSearchOptions = {
                searchInterval: 1000,
                searchMinLength: 3
            };

            TeTreeGridController.activate();
            $scope.$apply();

            expect(TeTreeGridController.customSearchMinLength).toBe(3);
        });
    });

    describe('applySearch()', function () {
        var _filter,
            filterInstance,
            filterGroupInstance,
            settings;

        beforeEach(function () {
            function Filter() {
                var that = this;

                filterInstance = that;
            }

            function FilterGroup() {
                var that = this;

                filterGroupInstance = that;
            }

            _.extend(FilterGroup.prototype, {
                addfilter: _.noop,

                createfilter: function () {
                    return new Filter();
                }
            });

            _filter = $.jqx.filter;
            $.jqx.filter = FilterGroup;

            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.apply = jasmine.createSpy('apply');
        });

        afterEach(function () {
            $.jqx.filter = _filter;
        });

        it('should clearFilters()', function () {
            TeTreeGridController.applySearch();

            expect(settings.apply).toHaveBeenCalledWith('clearFilters');
        });

        it('should addFilter() for enabled searchFields and applyFilters()', function () {
            TeTreeGridController.searchTerm = 'foobar';
            TeTreeGridController.searchFields = [
                {
                    field: 'foo',
                    enabled: true
                },
                {
                    field: 'bar',
                    enabled: false
                }
            ];

            spyOn($.jqx.filter.prototype, 'createfilter').andCallThrough();
            spyOn($.jqx.filter.prototype, 'addfilter');

            TeTreeGridController.applySearch();

            expect(filterGroupInstance.operator).toEqual('or');
            expect(filterGroupInstance.createfilter).toHaveBeenCalledWith('stringfilter', 'foobar', 'contains');
            expect(filterGroupInstance.addfilter).toHaveBeenCalledWith(0, filterInstance);
            expect(settings.apply).toHaveBeenCalledWith('addFilter', 'foo', filterGroupInstance);
            expect(settings.apply).not.toHaveBeenCalledWith('addFilter', 'bar', filterGroupInstance);
            expect(settings.apply).toHaveBeenCalledWith('applyFilters');
        });
    });

    describe('clearSearch()', function () {
        var settings;

        beforeEach(function () {
            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.apply = jasmine.createSpy('apply');
        });

        it('should clear searchTerm', function () {
            TeTreeGridController.searchTerm = 'foobar';
            TeTreeGridController.clearSearch();

            expect(TeTreeGridController.searchTerm).toBeNull();
        });

        it('should applySearch()', function () {
            spyOn(TeTreeGridController, 'applySearch');

            TeTreeGridController.searchTerm = 'foobar';
            TeTreeGridController.clearSearch();

            expect(TeTreeGridController.applySearch).toHaveBeenCalled();
        });

        it('should call teTreeGridOnClearSearchField()', function () {
            TeTreeGridController.customSearchEnabled = true;
            TeTreeGridController.teTreeGridOnClearSearchField = jasmine.createSpy('teTreeGridOnClearSearchField');
            TeTreeGridController.reloadSearchOptions = jasmine.createSpy('reloadSearchOptions');
            TeTreeGridController.teTreeGridOnReloadSearchOptions =
                jasmine.createSpy('teTreeGridOnReloadSearchOptions').andReturn(creativeInsertionList.promise);
            creativeInsertionList.resolve('');
            TeTreeGridController.searchTerm = 'foobar';
            TeTreeGridController.clearSearch();

            expect(TeTreeGridController.teTreeGridOnClearSearchField).toHaveBeenCalled();
        });

        it('should call teTreeGridOnClearFiltering()', function () {
            TeTreeGridController.customSearchEnabled = false;
            TeTreeGridController.teTreeGridOnClearFiltering = jasmine.createSpy('teTreeGridOnClearFiltering');
            TeTreeGridController.reloadSearchOptions = jasmine.createSpy('reloadSearchOptions');
            TeTreeGridController.teTreeGridOnReloadSearchOptions =
                jasmine.createSpy('teTreeGridOnReloadSearchOptions').andReturn(creativeInsertionList.promise);
            creativeInsertionList.resolve('');
            TeTreeGridController.searchTerm = 'foobar';
            TeTreeGridController.clearSearch();

            expect(TeTreeGridController.teTreeGridOnClearFiltering).toHaveBeenCalled();
        });
    });

    describe('customSearch()', function () {
        beforeEach(function () {
            TeTreeGridController.customSearchEnabled = true;
            TeTreeGridController.teTreeGridOnClearSearchField = jasmine.createSpy('teTreeGridOnClearSearchField');
            spyOn(TeTreeGridController, 'clearSearch');
        });

        it('should call clearSearch() when searchTerm is null', function () {
            TeTreeGridController.searchTerm = null;
            TeTreeGridController.applySearch();

            expect(TeTreeGridController.clearSearch).toHaveBeenCalled();
        });

        it('should call clearSearch() when searchTerm is empty', function () {
            TeTreeGridController.searchTerm = '';
            TeTreeGridController.applySearch();

            expect(TeTreeGridController.clearSearch).toHaveBeenCalled();
        });

        describe('Should execute function callback', function () {
            beforeEach(function () {
                TeTreeGridController.teTreeGridOnSearch = jasmine.createSpy('teTreeGridOnSearch').andReturn(
                    creativeInsertionList.promise);
                creativeInsertionList.resolve('');
            });

            it('should call teTreeGridOnSearch()', function () {
                TeTreeGridController.customSearchMinLength = 1;
                TeTreeGridController.searchTerm = 'foobar';
                TeTreeGridController.customSearch();

                expect(TeTreeGridController.teTreeGridOnSearch).toHaveBeenCalled();
            });

            it('should not call teTreeGridOnSearch() when searchTerm length less than customSearchMinLength',
                function () {
                    TeTreeGridController.customSearchMinLength = 7;
                    TeTreeGridController.searchTerm = 'foobar';
                    TeTreeGridController.customSearch();

                    expect(TeTreeGridController.teTreeGridOnSearch).not.toHaveBeenCalled();
                });
        });
    });

    describe('getRecord()', function () {
        var idField,
            foo,
            bar,
            foobar;

        beforeEach(function () {
            idField = TeTreeGridController.idField;
            foo = {
                id: 1,
                name: 'foo'
            };
            bar = {
                id: 2,
                name: 'bar',
                children: [foo]
            };
            foobar = {
                id: 3,
                name: 'foobar'
            };

            TeTreeGridController.model = [bar, foobar];
            TeTreeGridController.activate();
            $scope.$apply();
        });

        it('should get record by idField', function () {
            expect(TeTreeGridController.getRecord(foo[idField])).toBe(foo);
            expect(TeTreeGridController.getRecord(bar[idField])).toBe(bar);
            expect(TeTreeGridController.getRecord(foobar[idField])).toBe(foobar);
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

            TeTreeGridController.searchFields = [foo, bar, foobar];
        });

        it('should enable searchField if disabled', function () {
            foo.enabled = false;
            bar.enabled = false;

            TeTreeGridController.toggleSearchField(new $.Event(), foo);
            expect(foo.enabled).toBe(true);

            TeTreeGridController.toggleSearchField(new $.Event(), bar);
            expect(bar.enabled).toBe(true);
        });

        it('should applySearch() if searchField is toggled', function () {
            spyOn(TeTreeGridController, 'applySearch');

            TeTreeGridController.toggleSearchField(new $.Event(), foo);
            expect(TeTreeGridController.applySearch.calls.length).toBe(1);

            TeTreeGridController.toggleSearchField(new $.Event(), foo);
            expect(TeTreeGridController.applySearch.calls.length).toBe(2);
        });

        it('should disable searchField if disabled', function () {
            TeTreeGridController.toggleSearchField(new $.Event(), foo);
            expect(foo.enabled).toBe(false);

            TeTreeGridController.toggleSearchField(new $.Event(), bar);
            expect(bar.enabled).toBe(false);
        });

        it('should not disable searchField if it is the last enabled field', function () {
            foo.enabled = false;
            bar.enabled = false;
            TeTreeGridController.toggleSearchField(new $.Event(), foobar);

            expect(foobar.enabled).toBe(true);
        });

        it('should not applySearch() if searchField is not toggled', function () {
            foo.enabled = false;
            bar.enabled = false;
            spyOn(TeTreeGridController, 'applySearch');

            TeTreeGridController.toggleSearchField(new $.Event(), foobar);
            expect(TeTreeGridController.applySearch).not.toHaveBeenCalled();
        });

        it('should preventDefault() and stopImmediatePropagation() of event', function () {
            var evt = new $.Event();

            TeTreeGridController.toggleSearchField(evt, foo);

            expect(evt.isDefaultPrevented()).toBe(true);
            expect(evt.isImmediatePropagationStopped()).toBe(true);
        });
    });

    describe('onReady()', function () {
        var settings;

        beforeEach(function () {
            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.apply = jasmine.createSpy('apply');
        });

        it('should sortBy() defaultSort', function () {
            TeTreeGridController.defaultSort = 'foobar';

            settings.ready();

            expect(settings.apply).toHaveBeenCalledWith('sortBy', 'foobar', 'asc');
        });

        it('should do nothing if defaultSort is not set', function () {
            settings.ready();

            expect(settings.apply).not.toHaveBeenCalled();
        });
    });

    describe('onSort()', function () {
        var settings;

        beforeEach(function () {
            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.apply = jasmine.createSpy('apply');
        });

        it('should sortBy() lastSortColumn if sortDirection is neither ascending nor descending', function () {
            settings.ready();
            dataview.sortfield = 'theLastColumn';
            dataview.sortfielddirection = 'asc';
            dataview._sort();

            settings.sort(_.extend(new $.Event(), {
                args: {
                    sortdirection: {
                        ascending: false,
                        descending: false
                    }
                }
            }));

            expect(settings.apply).toHaveBeenCalledWith('sortBy', 'theLastColumn', 'asc');
        });
    });

    describe('triggerResize()', function () {
        var settings,
            resizeHandler;

        beforeEach(function () {
            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            resizeHandler = jasmine.createSpy();
            angular.element($window).resize(resizeHandler);
        });

        it('should triggerHandler() for `resize` on `rowCollapse`', function () {
            settings.rowCollapse();

            expect(resizeHandler).toHaveBeenCalled();
        });

        it('should triggerHandler() for `resize` on `rowExpand`', function () {
            settings.rowExpand();

            $timeout(function () {
                expect(resizeHandler).toHaveBeenCalled();
            }, 1000);
        });
    });

    describe('sortHack()', function () {
        beforeEach(function () {
            TeTreeGridController.activate();
            TeTreeGridController.settings.ready();
        });

        it('should hack dataview._sort() to maintain sort history', function () {
            var data = [
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'dFoo',
                    bar: null
                }
            ];

            dataview.sortfield = 'foo';
            dataview.sortfielddirection = 'asc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'dFoo',
                    bar: null
                }
            ]);

            dataview.sortfield = 'foo';
            dataview.sortfielddirection = 'desc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'dFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                }
            ]);

            dataview.sortfield = 'bar';
            dataview.sortfielddirection = 'asc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'dFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                }
            ]);

            dataview.sortfield = 'bar';
            dataview.sortfielddirection = 'desc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                },
                {
                    foo: 'dFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'bFoo',
                    bar: null
                }
            ]);

            dataview.sortfield = 'foo';
            dataview.sortfielddirection = 'asc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'dFoo',
                    bar: null
                }
            ]);

            dataview.sortfield = 'bar';
            dataview.sortfielddirection = 'desc';
            expect(dataview._sort(data)).toEqual([
                {
                    foo: 'aFoo',
                    bar: 'aBar'
                },
                {
                    foo: 'bFoo',
                    bar: null
                },
                {
                    foo: 'cFoo',
                    bar: null
                },
                {
                    foo: 'dFoo',
                    bar: null
                }
            ]);
        });
    });

    describe('Checking into Tree grid', function () {
        it('should call setDataChecked() function and append the checked row into a checked data list', function () {
            TeTreeGridController.setDataChecked({
                $$uuid: '1111',
                label: 'data1',
                level: 1,
                treeRowId: '111'
            });

            expect(TeTreeGridController.allChecked.length).toBe(1);

            TeTreeGridController.setDataChecked({
                $$uuid: '2222',
                label: 'data2',
                level: 1,
                parent: null,
                treeRowId: '222'
            });
            TeTreeGridController.setDataChecked({
                $$uuid: '333',
                label: 'data3',
                level: 2,
                parent: null,
                treeRowId: '333'
            });

            expect(TeTreeGridController.getAllCheckedData().length).toBe(3);
        });

        it('should unsetDataChecked() function and remove the checked row from the checked data list', function () {
            TeTreeGridController.setDataChecked({
                $$uuid: '1111',
                treeRowId: '1111',
                label: 'data1',
                level: 1,
                parent: null
            });
            TeTreeGridController.setDataChecked({
                $$uuid: '2222',
                treeRowId: '2222',
                label: 'data2',
                level: 1,
                parent: null
            });
            TeTreeGridController.setDataChecked({
                $$uuid: '333',
                treeRowId: '333',
                label: 'data3',
                level: 2,
                parent: null
            });

            TeTreeGridController.unsetDataChecked({
                $$uuid: '2222',
                treeRowId: '2222',
                label: 'data2',
                level: 1
            });

            expect(TeTreeGridController.getAllCheckedData().length).toBe(2);
        });

        it('should cleanAllChecked() function and remove all checked data list', function () {
            TeTreeGridController.setDataChecked({
                $$uuid: '1111',
                label: 'data1',
                level: 1,
                parent: null
            });
            TeTreeGridController.setDataChecked({
                $$uuid: '2222',
                label: 'data2',
                level: 1,
                parent: null
            });
            TeTreeGridController.setDataChecked({
                $$uuid: '333',
                label: 'data3',
                level: 2,
                parent: null
            });

            TeTreeGridController.unsetDataChecked({
                $$uuid: '2222',
                label: 'data2',
                level: 1
            });
            TeTreeGridController.cleanAllChecked();

            expect(TeTreeGridController.getAllCheckedData().length).toBe(0);
        });
    });

    describe('refreshColumns()', function () {
        it('should change the columns with new values', function () {
            var settings,
                columns = TeTreeGridController.columns = [
                    {
                        dataField: 'firstColumn',
                        sortable: false,
                        text: 'First Column',
                        width: '50%'
                    },
                    {
                        dataField: 'secondColumn',
                        sortable: false,
                        text: 'Second Column',
                        width: '50%'
                    }
                ],
                newColumnsArray = [
                    {
                        dataField: 'newFirstColumn',
                        sortable: false,
                        text: 'New First Column',
                        width: '40%'
                    },
                    {
                        dataField: 'newSecondColumn',
                        sortable: false,
                        text: 'New Second Column',
                        width: '40%'
                    },
                    {
                        dataField: 'newThirdColumn',
                        sortable: false,
                        text: 'New Third Column',
                        width: '20%'
                    }
                ];

            TeTreeGridController.activate();
            settings = TeTreeGridController.settings;
            settings.apply = jasmine.createSpy('apply');
            settings.ready();

            expect(TeTreeGridController.settings.columns).toEqual(columns);
            TeTreeGridController.refreshColumns(newColumnsArray);
            expect(settings.apply).toHaveBeenCalledWith('columns', newColumnsArray);
        });
    });

    describe('rendered()', function () {
        var $delegate,
            settings,
            instanceData,
            uncheck,
            check;

        beforeEach(inject(function ($rootScope, _$window_) {
            $scope = $rootScope.$new();
            $window = _$window_;
            uncheck = jasmine.createSpy();
            check = jasmine.createSpy();

            instanceData = {
                getChecked: {
                    base: {
                        getRow: function () {
                            return [
                                {
                                    checked: true
                                }
                            ];
                        },

                        uncheckRow: uncheck,
                        checkRow: check
                    }
                },

                getUnchecked: {
                    base: {
                        getRow: function () {
                            return [
                                {
                                    checked: false
                                }
                            ];
                        },

                        uncheckRow: uncheck,
                        checkRow: check
                    }
                }
            };
        }));

        it('should call uncheck function when header checkbox triggers event', inject(function ($compile, $controller) {
            $delegate = angular.element(
                    '<div class="te-tree-grid-delegate">' +
                        '<div id="customTreeGridHeaderCheckboxCheckedContainer" class="checkbox c-checkbox"' +
                        'data-row-key="123">' +
                        '<label>' +
                        '<input type="checkbox" checked="checked" id="customTreeGridHeaderCheckboxChecked"/>' +
                        '<span class="fa fa-check"></span>' +
                        '</label>' +
                        '</div>' +
                    '</div>'
            );

            $delegate.data('jqxTreeGrid', {
                instance: instanceData.getChecked
            });

            $element = angular.element($delegate);
            $compile($element)($scope);
            $window.document.body.appendChild($element[0]);
            $scope.$digest();

            TeTreeGridController = $scope.vm = $controller('TeTreeGridController', {
                $element: $element,
                $scope: $scope,
                $window: $window
            });

            TeTreeGridController.activate();
            settings = TeTreeGridController.settings;
            settings.rendered();

            angular.element('#customTreeGridHeaderCheckboxCheckedContainer').trigger('click');
            expect(uncheck).toHaveBeenCalled();
        }));

        it('should call check function when header checkbox triggers event', inject(function ($compile, $controller) {
            $delegate = angular.element(
                    '<div class="te-tree-grid-delegate">' +
                        '<div id="customTreeGridHeaderCheckboxCheckedContainer" class="checkbox c-checkbox"' +
                        'data-row-key="123">' +
                        '<label>' +
                        '<input type="checkbox" id="customTreeGridHeaderCheckboxChecked"/>' +
                        '<span class="fa fa-check"></span>' +
                        '</label>' +
                        '</div>' +
                    '</div>'
            );

            $delegate.data('jqxTreeGrid', {
                instance: instanceData.getUnchecked
            });

            $element = angular.element($delegate);
            $compile($element)($scope);
            $window.document.body.appendChild($element[0]);
            $scope.$digest();

            TeTreeGridController = $scope.vm = $controller('TeTreeGridController', {
                $element: $element,
                $scope: $scope,
                $window: $window
            });

            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.rendered();

            angular.element('#customTreeGridHeaderCheckboxCheckedContainer').trigger('click');
            expect(check).toHaveBeenCalled();
        }));

        it('should call uncheck function when a checkbox triggers event', inject(function ($compile, $controller) {
            $delegate = angular.element(
                    '<div class="te-tree-grid-delegate">' +
                        '<div class="checkbox c-checkbox customTreeGridColumnCheckbox">' +
                        '<label>' +
                        '<input data-row-key="456" data-check-value="true" type="checkbox" ' +
                        'class="customTreeGridCheckbox"' +
                        'checked="checked"/>' +
                        '<span class="fa fa-check"></span>' +
                        '</label>' +
                        '</div>' +
                    '</div>'
            );

            $delegate.data('jqxTreeGrid', {
                instance: instanceData.getChecked
            });

            $element = angular.element($delegate);
            $compile($element)($scope);
            $window.document.body.appendChild($element[0]);
            $scope.$digest();

            TeTreeGridController = $scope.vm = $controller('TeTreeGridController', {
                $element: $element,
                $scope: $scope,
                $window: $window
            });

            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.rendered();

            angular.element('.customTreeGridCheckbox').trigger('change');
            expect(uncheck).toHaveBeenCalled();
        }));

        it('should call check function when a checkbox triggers event', inject(function ($compile, $controller) {
            $delegate = angular.element(
                    '<div class="te-tree-grid-delegate">' +
                    '<div class="checkbox c-checkbox customTreeGridColumnCheckbox">' +
                    '<label>' +
                    '<input data-row-key="456" data-check-value="false" type="checkbox" ' +
                        'class="customTreeGridCheckbox"/>' +
                    '<span class="fa fa-check"></span>' +
                    '</label>' +
                    '</div>' +
                    '</div>'
            );

            $delegate.data('jqxTreeGrid', {
                instance: instanceData.getUnchecked
            });

            $element = angular.element($delegate);
            $compile($element)($scope);
            $window.document.body.appendChild($element[0]);
            $scope.$digest();

            TeTreeGridController = $scope.vm = $controller('TeTreeGridController', {
                $element: $element,
                $scope: $scope,
                $window: $window
            });

            TeTreeGridController.activate();

            settings = TeTreeGridController.settings;
            settings.rendered();

            angular.element('.customTreeGridCheckbox').trigger('change');
            expect(check).toHaveBeenCalled();
        }));
    });
});
