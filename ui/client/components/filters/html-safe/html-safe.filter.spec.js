'use strict';

describe('Filter: HtmlSafeFilter', function () {
    var $compile,
        $translate,
        DATA = {},
        element,
        html = '<div><p class="pb ng-binding" data-ng-bind-html="vm.description | translate | htmlSafe"></p></div>',
        scope,
        translations = {
            description: 'We found {{value}} issues.'
        };

    beforeEach(module('te.components'));

    beforeEach(module(function ($translateProvider) {
        $translateProvider
            .translations('en', translations)
            .preferredLanguage('en');
    }));

    beforeEach(inject(function ($rootScope, _$compile_, _$translate_) {
        $translate = _$translate_;

        DATA.description = $translate.instant('description', {
            value: '<span class="h4"><strong>6</strong></span>'
        });

        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
    }));

    it('should process htmlText with htmlSafe filter', function () {
        element = $compile(
            angular.element(html)
        )(scope);
        scope.$apply();
        expect(element.find('.pb').html()).toEqual('We found <span class="h4"><strong>6</strong></span> issues.');
    });
});
