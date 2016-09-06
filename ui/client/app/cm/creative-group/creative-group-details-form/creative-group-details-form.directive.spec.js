'use strict';

describe('Directive: creativeGroupDetailsForm', function () {
    var $compile,
        controller,
        element,
        scope;

    beforeEach(module('uiApp', function ($controllerProvider) {
        $controllerProvider.register('CreativeGroupDetailsFormController', function () {
            var that = this;

            controller = that;
        });
    }));

    beforeEach(inject(function ($rootScope, $state, $templateCache, _$compile_) {
        $compile = _$compile_;
        scope = $rootScope.$new();

        spyOn($state, 'go');
        $templateCache.put(
          'app/cm/creative-group/creative-group-details-form/creative-group-details-form.html',
          '<form><ng-transclude></ng-transclude></form>');
    }));

    describe('controllerAs', function () {
        it('should be `vm`', function () {
            scope.model = {};
            element = $compile(
                angular.element(
                    '<creative-group-details-form data-model="model">' +
                    '</creative-group-details-form>'
                )
            )(scope);
            scope.$digest();

            expect(element.isolateScope().vm).toBe(controller);
        });
    });

    describe('bindToController', function () {
        it('should be true', function () {
            var vm;

            _.extend(scope, {
                model: {
                    foo: 'bar'
                },
                pristine: 'foobar',
                submitDisabled: 1337
            });
            element = $compile(
                angular.element(
                    '<creative-group-details-form data-model="model"' +
                    '                             data-pristine="pristine"' +
                    '                             data-submit-disabled="submitDisabled">' +
                    '</creative-group-details-form>'
                )
            )(scope);
            scope.$digest();
            vm = element.isolateScope().vm;

            expect(vm.model).toBe(scope.model);
            expect(vm.pristine).toEqual('foobar');
            expect(vm.submitDisabled).toEqual(1337);
        });
    });

    describe('transclude', function () {
        it('should be true', function () {
            scope.model = {};
            element = $compile(
                angular.element(
                    '<creative-group-details-form data-model="model">' +
                    '  <button type="submit"/>' +
                    '</creative-group-details-form>'
                )
            )(scope);
            scope.$digest();

            expect(element.find('button').attr('type')).toEqual('submit');
        });
    });
});
