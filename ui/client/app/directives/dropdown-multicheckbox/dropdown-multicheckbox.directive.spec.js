'use strict';

describe('Directive: dropdownMulticheckbox', function () {
    var $compile,
        $scope,
        element,
        DATA = {
            inputModel: [
                'foo',
                'bar',
                'baz'
            ],
            outputModel: [
                'foo'
            ],
            text: 'Advertiser',
            messages: {
                allSelected: 'All',
                checkAll: 'Select All',
                searchPlaceholder: 'Search…',
                uncheckAll: 'Clear Selected'
            }
        };

    beforeEach(module('uiApp'));

    beforeEach(module('app/directives/dropdown-multicheckbox/dropdown-multicheckbox.html'));

    beforeEach(inject(function ($rootScope, $state, $templateCache, _$compile_) {
        $compile = _$compile_;
        $scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
        spyOn($state, 'go');
        element = $compile(
            angular.element('<dropdown-multicheckbox input-model="vm.inputModel"' +
                'output-model="vm.outputModel"' +
                'title="vm.text">' +
                '</dropdown-multicheckbox>'
            )
        )($scope);
        $scope.$digest();
    }));

    describe('Directive rendering', function () {
        it('should render the button and the dropdown and have 3 options', function () {
            expect(element.find('.btn')).toBeTruthy();
            expect(element.find('ul.dropdown-menu')).toBeTruthy();
            expect(element.find('div.checkbox input').length).toEqual(3);
        });
    });

    describe('Message strings applied', function () {
        it('should apply the message strings on the messages array', function () {
            expect(element.find('.btn').html().trim()).toEqual(
                element.isolateScope().$parent.vm.text +
                ': 1&nbsp;&nbsp;<span class="caret"></span>');
            expect(element.find('.dropdown-menu .dropdown-header input.form-control').prop('placeholder'))
                .toEqual(element.isolateScope().$parent.vm.messages.searchPlaceholder);
        });
    });

    describe('setSelectedItem()', function () {
        it('should select single option', function () {
            $scope.$$childHead.deselectAll();
            expect($scope.vm.outputModel.length).toEqual(0);
            $scope.$$childHead.setSelectedItem('bar');
            expect($scope.vm.outputModel.length).toEqual(1);
            expect($scope.vm.outputModel[0]).toEqual('bar');
        });

        it('should deselect single option', function () {
            expect($scope.vm.outputModel.length).toEqual(1);
            expect($scope.vm.outputModel[0]).toEqual('bar');
            $scope.$$childHead.setSelectedItem('bar');
            expect($scope.vm.outputModel.length).toEqual(0);
        });
    });

    describe('selectAll()', function () {
        it('should set all options as selected', function () {
            $scope.$$childHead.selectAll();
            expect($scope.vm.outputModel.length).toEqual(3);
        });

        it('should select all options that match search key', function () {
            $scope.$$childHead.deselectAll();
            expect($scope.vm.outputModel.length).toEqual(0);
            $scope.$$childHead.searchFilter = 'ba';
            $scope.$$childHead.selectAll();
            expect($scope.vm.outputModel.length).toEqual(2);
        });
    });

    describe('deselectAll()', function () {
        it('should set all options as not selected', function () {
            $scope.$$childHead.searchFilter = undefined;
            expect($scope.vm.outputModel.length).toEqual(2);
            $scope.$$childHead.deselectAll();
            expect($scope.vm.outputModel.length).toEqual(0);
        });

        it('should deselect all options that match search key', function () {
            $scope.$$childHead.selectAll();
            expect($scope.vm.outputModel.length).toEqual(3);
            $scope.$$childHead.searchFilter = 'ba';
            $scope.$$childHead.deselectAll();
            expect($scope.vm.outputModel.length).toEqual(1);
        });
    });
});
