'use strict';

describe('Directive: associationMaker', function () {
    var $compile,
        $httpBackend,
        element,
        scope;

    beforeEach(module('uiApp'));

    beforeEach(module('app/directives/association-maker/association-maker.html'));
    beforeEach(module('app/cm/campaigns/io-tab/package-tab/associate-placements/package-association/' +
        'templates/left-table.html'));
    beforeEach(module('app/cm/campaigns/io-tab/package-tab/associate-placements/package-association/' +
        'templates/right-table.html'));
    beforeEach(module('components/directives/te-table/te-table.html'));
    beforeEach(module('components/directives/te-table/te-table-select-all-checkbox/' +
        'te-table-select-all-checkbox.html'));

    beforeEach(inject(function (_$httpBackend_, $rootScope, _$compile_) {
        $compile = _$compile_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        scope = _.extend($rootScope.$new(), {
            vm: {
                inputModel: [
                    {
                        name: 'IO',
                        siteName: 'Sites'
                    },
                    {
                        name: 'Sports',
                        siteName: '',
                        sizeName: '100x200'
                    }
                ],
                outputModel: [],
                filterOptions: {
                    filterText: [
                        {
                            id: 'filterInput',
                            text: '',
                            field: ['name', 'siteName', 'sizeName']
                        },
                        {
                            id: 'filterOutput',
                            text: '',
                            field: ['name', 'siteName']
                        }
                    ]
                }
            }
        });

        element = $compile(angular.element(
            '<association-maker input-model="vm.inputModel" ' +
            '                   output-model="vm.outputModel" ' +
            '                   output-map="vm.outputMap" ' +
            '                   filter-options="vm.filterOptions"' +
            '                   template-left-table="\'app/cm/campaigns/io-tab/package-tab/' +
            'associate-placements/package-association/templates/left-table.html\'"' +
            '                   template-right-table="\'app/cm/campaigns/io-tab/package-tab/' +
            'associate-placements/package-association/templates/right-table.html\'">' +
            '</association-maker>'
        ))(scope);
        scope.$apply();
    }));

    it('should have two rows in input panel', inject(function () {
        var $inputModelTable = element.find('table tbody'),
            $rows = $inputModelTable.children('tr');

        expect($rows.length).toBe(2);
    }));
});
