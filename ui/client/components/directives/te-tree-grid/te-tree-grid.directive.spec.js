'use strict';

describe('Directive: teTreeGrid', function () {
    var $rootScope,
        $scope,
        element;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-tree-grid/te-tree-grid.html'));

    beforeEach(inject(function ($compile, _$rootScope_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        element = $compile(angular.element(
            '<te-tree-grid data-model="vm.insertionOrderList"' +
            '              data-children-field="children">' +
            '  <te-btns>' +
            '    <button id="primaryButton1" class="btn"></button>' +
            '    <button id="primaryButton2" class="btn"></button>' +
            '  </te-btns>' +
            '  <te-secondary-btns>' +
            '    <button id="secondaryButton1" class="btn"></button>' +
            '    <button id="secondaryButton2" class="btn"></button>' +
            '  </te-secondary-btns>' +
            '  <te-columns>' +
            '    <te-column data-field="hiddenField"' +
            '               data-filterable="true"' +
            '               data-hidden="true"' +
            '               data-title="\'Hidden Field\'">' +
            '    </te-column>' +
            '    <te-column data-field="status"' +
            '               data-title="\'global.status\' | translate"' +
            '               data-width="10%">' +
            '    </te-column>' +
            '    <te-column data-field="ioName"' +
            '               data-filterable="true"' +
            '               data-sortable="true"' +
            '               data-title="\'insertionOrder.ioName\' | translate"' +
            '               data-width="17.5%">' +
            '    </te-column>' +
            '    <te-column data-field="ioNumber"' +
            '               data-sortable="true"' +
            '               data-title="\'insertionOrder.ioNumber\' | translate"' +
            '               data-type="number"' +
            '               data-width="10%">' +
            '    </te-column>' +
            '    <te-column data-field="placementName"' +
            '               data-filterable="true"' +
            '               data-sortable="true"' +
            '               data-title="\'global.placements\' | translate"' +
            '               data-width="17.5%">' +
            '    </te-column>' +
            '    <te-column data-field="siteName"' +
            '               data-sortable="true"' +
            '               data-title="\'global.site\' | translate"' +
            '               data-width="10%">' +
            '    </te-column>' +
            '    <te-column data-field="sizeLabel"' +
            '               data-sortable="true"' +
            '               data-title="\'global.size\' | translate"' +
            '               data-width="7.5%">' +
            '    </te-column>' +
            '    <te-column data-field="totalAdSpend"' +
            '               data-cells-align="right"' +
            '               data-cells-format="\'c2\'"' +
            '               data-sortable="true"' +
            '               data-title="\'insertionOrder.ioTotalAdSpend\' | translate"' +
            '               data-type="number"' +
            '               data-width="12.5%">' +
            '    </te-column>' +
            '    <te-column data-field="startDate"' +
            '               data-cells-align="right"' +
            '               data-cells-format="\'M/d/yyyy\'"' +
            '               data-sortable="true"' +
            '               data-title="\'global.startDate\' | translate"' +
            '               data-type="date"' +
            '               data-width="7.5%">' +
            '    </te-column>' +
            '    <te-column data-field="endDate"' +
            '               data-cells-align="right"' +
            '               data-cells-format="\'M/d/yyyy\'"' +
            '               data-sortable="true"' +
            '               data-title="\'global.endDate\' | translate"' +
            '               data-type="date"' +
            '               data-width="7.5%">' +
            '    </te-column>' +
            '  </te-columns>' +
            '</te-tree-grid>'
        ))($scope);
        $scope.$apply();
    }));

    describe('link()', function () {
        describe('controller', function () {
            var controller;

            beforeEach(function () {
                controller = element.isolateScope().vm;
            });

            describe('columns', function () {
                it('should be set from <te-columns/>', function () {
                    expect(controller.columns).toEqual([
                        {
                            cellsAlign: 'left',
                            dataField: 'hiddenField',
                            editable: false,
                            hidden: true,
                            sortable: false,
                            text: 'Hidden Field'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'status',
                            editable: false,
                            hidden: false,
                            sortable: false,
                            text: 'Status',
                            width: '10%'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'ioName',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'IO Name',
                            width: '17.5%'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'ioNumber',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'IO Number',
                            width: '10%'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'placementName',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'Placements',
                            width: '17.5%'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'siteName',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'Site',
                            width: '10%'
                        },
                        {
                            cellsAlign: 'left',
                            dataField: 'sizeLabel',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'Size',
                            width: '7.5%'
                        },
                        {
                            cellsAlign: 'right',
                            cellsFormat: 'c2',
                            dataField: 'totalAdSpend',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'Ad Spend',
                            width: '12.5%'
                        },
                        {
                            cellsAlign: 'right',
                            cellsFormat: 'M/d/yyyy',
                            dataField: 'startDate',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'Start Date',
                            width: '7.5%'
                        },
                        {
                            cellsAlign: 'right',
                            cellsFormat: 'M/d/yyyy',
                            dataField: 'endDate',
                            editable: false,
                            hidden: false,
                            sortable: true,
                            text: 'End Date',
                            width: '7.5%'
                        }
                    ]);
                });

                describe('cellClassName()', function () {
                    it('should invoke parsed function', inject(function ($compile) {
                        var formatSpy = jasmine.createSpy('formatSpy');

                        $scope = _.extend($rootScope.$new(), {
                            vm: {
                                format: formatSpy
                            }
                        });
                        element = $compile(angular.element(
                            '<te-tree-grid data-model="vm.insertionOrderList"' +
                            '              data-children-field="children">' +
                            '  <te-columns>' +
                            '    <te-column data-field="ioName"' +
                            '               data-filterable="true"' +
                            '               data-sortable="true"' +
                            '               data-title="\'insertionOrder.ioName\' | translate"' +
                            '               data-width="17.5%"' +
                            '               data-cell-class-name="vm.format($row, ' +
                            '                                               $dataField, ' +
                            '                                               $cellValue, ' +
                            '                                               $rowData, ' +
                            '                                               $cellText)">' +
                            '    </te-column>' +
                            '  </te-columns>' +
                            '</te-tree-grid>'
                        ))($scope);
                        $scope.$apply();

                        element.isolateScope()
                            .vm
                            .columns[0]
                            .cellClassName('theRow', 'theDataField', 'theCellValue', 'theRowData', 'theCellText');

                        expect(formatSpy).toHaveBeenCalledWith(
                            'theRow',
                            'theDataField',
                            'theCellValue',
                            'theRowData',
                            'theCellText');
                    }));
                });

                describe('cellsRenderer()', function () {
                    it('should invoke parsed function', inject(function ($compile) {
                        var formatSpy = jasmine.createSpy('formatSpy');

                        $scope = _.extend($rootScope.$new(), {
                            vm: {
                                format: formatSpy
                            }
                        });
                        element = $compile(angular.element(
                            '<te-tree-grid data-model="vm.insertionOrderList"' +
                            '              data-children-field="children">' +
                            '  <te-columns>' +
                            '    <te-column data-field="ioName"' +
                            '               data-filterable="true"' +
                            '               data-sortable="true"' +
                            '               data-title="\'insertionOrder.ioName\' | translate"' +
                            '               data-width="17.5%"' +
                            '               data-cells-renderer="vm.format($row, ' +
                            '                                              $dataField, ' +
                            '                                              $cellValue, ' +
                            '                                              $rowData, ' +
                            '                                              $cellText)">' +
                            '    </te-column>' +
                            '  </te-columns>' +
                            '</te-tree-grid>'
                        ))($scope);
                        $scope.$apply();

                        element.isolateScope()
                            .vm
                            .columns[0]
                            .cellsRenderer('theRow', 'theDataField', 'theCellValue', 'theRowData', 'theCellText');

                        expect(formatSpy).toHaveBeenCalledWith(
                            'theRow',
                            'theDataField',
                            'theCellValue',
                            'theRowData',
                            'theCellText');
                    }));
                });

                describe('cellsEditor()', function () {
                    it('should invoke parsed edition configurations function', inject(function ($compile) {
                        var getWeightEditorConfSpy = jasmine.createSpy('editorSpy');

                        $scope = _.extend($rootScope.$new(), {
                            vm: {
                                getWeightEditorConf: getWeightEditorConfSpy
                            }
                        });
                        element = $compile(angular.element(
                            '<te-tree-grid data-model="vm.model"' +
                            '              data-children-field="children">' +
                            '  <te-columns>' +
                            '    <te-column data-editable="true"' +
                            '               data-editor-conf="vm.getWeightEditorConf()"' +
                            '               data-field="weight"' +
                            '               data-cells-align="center"' +
                            '               data-title="\'insertionOrder.ioName\' | translate"' +
                            '               data-width="50%">' +
                            '    </te-column>' +
                            '  </te-columns>' +
                            '</te-tree-grid>'
                        ))($scope);
                        $scope.$apply();

                        expect(getWeightEditorConfSpy).toHaveBeenCalled();
                    }));
                });
            });

            describe('dataFields', function () {
                it('should be set from <te-columns/>', function () {
                    expect(controller.dataFields).toEqual([
                        {
                            name: 'hiddenField',
                            type: 'string'
                        },
                        {
                            name: 'status',
                            type: 'string'
                        },
                        {
                            name: 'ioName',
                            type: 'string'
                        },
                        {
                            name: 'ioNumber',
                            type: 'number'
                        },
                        {
                            name: 'placementName',
                            type: 'string'
                        },
                        {
                            name: 'siteName',
                            type: 'string'
                        },
                        {
                            name: 'sizeLabel',
                            type: 'string'
                        },
                        {
                            name: 'totalAdSpend',
                            type: 'number'
                        },
                        {
                            name: 'startDate',
                            type: 'date'
                        },
                        {
                            name: 'endDate',
                            type: 'date'
                        }
                    ]);
                });
            });

            describe('defaultSort', function () {
                it('should be initialized as the first sortable field when no column is specified as the default sort',
                    function () {
                        expect(controller.defaultSort).toEqual('ioName');
                    });

                it('should be initialized as the first field specified as the default sort',
                    inject(function ($compile) {
                        $scope = $rootScope.$new();
                        element = $compile(angular.element(
                            '<te-tree-grid data-model="vm.insertionOrderList"' +
                            '              data-children-field="children">' +
                            '  <te-columns>' +
                            '    <te-column data-field="status"' +
                            '               data-title="\'global.status\' | translate"' +
                            '               data-width="10%">' +
                            '    </te-column>' +
                            '    <te-column data-field="ioName"' +
                            '               data-filterable="true"' +
                            '               data-sortable="true"' +
                            '               data-title="\'insertionOrder.ioName\' | translate"' +
                            '               data-width="17.5%">' +
                            '    </te-column>' +
                            '    <te-column data-field="ioNumber"' +
                            '               data-sortable-default="true"' +
                            '               data-sortable="true"' +
                            '               data-title="\'insertionOrder.ioNumber\' | translate"' +
                            '               data-type="number"' +
                            '               data-width="10%">' +
                            '    </te-column>' +
                            '  </te-columns>' +
                            '</te-tree-grid>'
                        ))($scope);
                        $scope.$apply();
                        controller = element.isolateScope().vm;

                        expect(controller.defaultSort).toEqual('ioNumber');
                    }));
            });

            describe('searchFields', function () {
                it('should be initialized with the searchable fields', function () {
                    expect(controller.searchFields).toEqual([
                        {
                            enabled: true,
                            field: 'hiddenField',
                            title: 'Hidden Field'
                        },
                        {
                            enabled: true,
                            field: 'ioName',
                            title: 'IO Name'
                        },
                        {
                            enabled: true,
                            field: 'placementName',
                            title: 'Placements'
                        }
                    ]);
                });
            });

            describe('activate()', function () {
                it('should be invoked', function () {
                    expect(controller.settings).not.toBeNull();
                });
            });
        });

        describe('transclude()', function () {
            it('should transclude primary buttons', function () {
                expect(element.find('#primaryButton1').closest('.te-tree-grid-btns')).toBeTruthy();
                expect(element.find('#primaryButton2').closest('.te-tree-grid-btns')).toBeTruthy();
            });

            it('should transclude secondary buttons', function () {
                expect(element.find('#secondaryButton1').closest('.te-tree-grid-secondary-btns')).toBeTruthy();
                expect(element.find('#secondaryButton2').closest('.te-tree-grid-secondary-btns')).toBeTruthy();
            });
        });
    });
});
