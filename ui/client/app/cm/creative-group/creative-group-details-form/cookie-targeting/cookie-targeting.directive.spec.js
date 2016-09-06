'use strict';

describe('Directive: cookieTargeting', function () {
    var CookieDomainsService,
        element,
        scope;

    beforeEach(module('app/cm/creative-group/creative-group-details-form/cookie-targeting/cookie-targeting.html'));

    beforeEach(module('uiApp', function ($provide) {
        CookieDomainsService = jasmine.createSpyObj('CookieDomainsService', ['getCookies']);
        $provide.value('CookieDomainsService', CookieDomainsService);
    }));

    beforeEach(inject(function ($q, $rootScope, $state) {
        scope = $rootScope.$new();

        CookieDomainsService.getCookies.andReturn($q.defer().promise);
        spyOn($state, 'go');
    }));

    describe('link()', function () {
        it('should set $form with FormController', inject(function ($compile) {
            element = $compile(angular.element(
                '<form>' +
                '  <cookie-targeting data-model="model" data-visible="visible"></cookie-targeting>' +
                '</form>'
            ))(scope);

            scope.$apply();

            expect(element.find('cookie-targeting').isolateScope().vm.$form).toBe(element.controller('form'));
        }));
    });
});
