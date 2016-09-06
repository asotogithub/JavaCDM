'use strict';

describe('Directive: geoTargeting', function () {
    var GeoTargetingService,
        element,
        scope;

    beforeEach(module('app/cm/creative-group/creative-group-details-form/geo-targeting/geo-targeting.html'));
    beforeEach(module('components/directives/te-table/te-table-select-all-checkbox/te-table-select-all-checkbox.html'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(module('uiApp', function ($provide) {
        GeoTargetingService = jasmine.createSpyObj('GeoTargetingService', ['getCountries']);
        $provide.value('GeoTargetingService', GeoTargetingService);
    }));

    beforeEach(inject(function ($q, $rootScope, $state) {
        scope = $rootScope.$new();

        GeoTargetingService.getCountries.andReturn($q.defer().promise);
        spyOn($state, 'go');
    }));

    describe('link()', function () {
        it('should set $form with FormController', inject(function ($compile) {
            element = $compile(angular.element(
              '<form>' +
              '  <geo-targeting data-model="model" data-visible="visible"></geo-targeting>' +
              '</form>'
            ))(scope);

            scope.$apply();

            expect(element.find('geo-targeting').isolateScope().vm.$form).toBe(element.controller('form'));
        }));
    });
});
