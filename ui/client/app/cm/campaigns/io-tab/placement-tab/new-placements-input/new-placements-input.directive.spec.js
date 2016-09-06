'use strict';

describe('Directive: newPlacementsInput', function () {
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

    // load the directive's module and view
    beforeEach(module('uiApp', function ($controllerProvider) {
        $controllerProvider.register('NewPlacementsInputController', function () {
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
                        '<new-placements-input data-model="vm.model">' +
                        '</new-placements-input>'
                )
            )(scope);

            expect(scope.vm.model).toEqual(PLACEMENT_LIST);
        });
    });
});
