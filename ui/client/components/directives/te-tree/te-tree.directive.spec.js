'use strict';

describe('Directive: teTree', function () {
    var $rootScope,
        $scope,
        element;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-tree/te-tree.html'));

    beforeEach(inject(function ($compile, _$rootScope_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        element = $compile(angular.element(
            '<te-tree data-model="vm.list">' +
            '  <te-btns>' +
            '    <button id="primaryButton1" class="btn"></button>' +
            '    <button id="primaryButton2" class="btn"></button>' +
            '  </te-btns>' +
            '  <te-secondary-btns>' +
            '    <button id="secondaryButton1" class="btn"></button>' +
            '    <button id="secondaryButton2" class="btn"></button>' +
            '  </te-secondary-btns>' +
            '</te-tree>'
        ))($scope);
        $scope.$apply();
    }));

    describe('link()', function () {
        describe('controller', function () {
            var controller;

            beforeEach(function () {
                controller = element.isolateScope().vm;
            });

            describe('activate()', function () {
                it('should be invoked', function () {
                    expect(controller.model).not.toBeNull();
                });
            });
        });

        describe('transclude()', function () {
            it('should transclude primary buttons', function () {
                expect(element.find('#primaryButton1').closest('.te-tree-btns')).toBeTruthy();
                expect(element.find('#primaryButton2').closest('.te-tree-btns')).toBeTruthy();
            });

            it('should transclude secondary buttons', function () {
                expect(element.find('#secondaryButton1').closest('.te-tree-secondary-btns')).toBeTruthy();
                expect(element.find('#secondaryButton2').closest('.te-tree-secondary-btns')).toBeTruthy();
            });
        });
    });
});
