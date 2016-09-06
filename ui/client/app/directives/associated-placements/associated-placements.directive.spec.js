'use strict';

describe('Directive: associatedPlacements', function () {
    var $compile,
        controller,
        element,
        scope,
        PLACEMENT_LIST = [
            {
                id: 6101571,
                name: 'Placement 01',
                siteName: 'SITE - QA test',
                sectionName: 'Section name',
                sizeName: '180x150',
                status: 'Rejected',
                allowRemove: true
            },
            {
                id: 6101572,
                name: 'Placement 02',
                siteName: 'SITE - QA test',
                sectionName: 'Section name',
                sizeName: '180x150',
                status: 'Accepted'
            }
        ];

    beforeEach(module('app/directives/associated-placements/associated-placements.html'));
    beforeEach(module('app/cm/campaigns/io-tab/package-tab/associate-placements/package-association/' +
        'templates/associated-placements.html'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-add-btn/te-table-add-btn.html'));
    beforeEach(module('components/directives/te-table/te-table-edit-btn/te-table-edit-btn.html'));

    // load the directive's module and view
    beforeEach(module('uiApp', function ($controllerProvider) {
        $controllerProvider.register('CreativeGroupDetailsFormController', function () {
            var vm = this;

            controller = vm;
        });
    }));

    beforeEach(inject(function ($rootScope, $state, $templateCache, _$compile_) {
        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: {
                model: PLACEMENT_LIST
            }
        });
        spyOn($state, 'go');
    }));

    describe('controllerAs', function () {
        it('should keep the model', function () {
            element = $compile(
                angular.element(
                        '<associated-placements data-model="vm.model" ' +
                        'table-template="\'app/cm/campaigns/io-tab/package-tab/associate-placements/' +
                        'package-association/templates/associated-placements.html\'">' +
                        '</associated-placements>'
                )
            )(scope);
            scope.$digest();

            expect(element.isolateScope().vm.model).toEqual(PLACEMENT_LIST);
        });
    });

    describe('directive rendering', function () {
        it('should have two rows', function () {
            element = $compile(
                angular.element(
                        '<associated-placements data-model="vm.model" ' +
                        'table-template="\'app/cm/campaigns/io-tab/package-tab/associate-placements/' +
                        'package-association/templates/associated-placements.html\'">' +
                        '</associated-placements>'
                )
            )(scope);
            scope.$apply();

            var inputModelTable = element.find('tbody'),
                rows = inputModelTable.children('tr');

            expect(rows.length).toBe(2);
        });

        it('should have six columns', function () {
            element = $compile(
                angular.element(
                        '<associated-placements data-model="vm.model" ' +
                        'table-template="\'app/cm/campaigns/io-tab/package-tab/associate-placements/' +
                        'package-association/templates/associated-placements.html\'">' +
                        '</associated-placements>'
                )
            )(scope);
            scope.$apply();

            var inputModelTable = element.find('thead tr.ng-table-filters'),
                columns = inputModelTable.children('th');

            expect(columns.length).toBe(6);
        });
    });
});
