'use strict';

describe('Directive: daypartTargeting', function () {
    var element,
      scope;

    beforeEach(module('app/cm/creative-group/creative-group-details-form/daypart-targeting/daypart-targeting.html'));
    beforeEach(module('uiApp'));

    beforeEach(inject(function ($rootScope, $state) {
        scope = $rootScope.$new();

        spyOn($state, 'go');
    }));

    describe('link()', function () {
        it('should set $form with FormController', inject(function ($compile) {
            element = $compile(angular.element(
              '<form>' +
              '  <daypart-targeting></daypart-targeting>' +
              '</form>'
            ))(scope);

            scope.$apply();

            expect(element.find('daypart-targeting').isolateScope().vm.$form).toBe(element.controller('form'));
        }));
    });
});
