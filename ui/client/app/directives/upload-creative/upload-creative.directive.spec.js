'use strict';

describe('Directive: uploadCreative', function () {
    var $httpBackend,
        controller,
        element,
        scope;

    beforeEach(module('uiApp', function ($controllerProvider) {
        $controllerProvider.register('UploadCreativeController', function () {
            var that = this;

            controller = that;
        });
    }));

    beforeEach(module('app/directives/upload-creative/upload-creative.html'));

    beforeEach(inject(function (_$httpBackend_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $httpBackend.whenGET('components/directives/te-table/te-table.html').respond(200, '');
        scope = $rootScope.$new();
    }));

    it('should make hidden element visible', inject(function ($compile) {
        scope.model = [];
        scope.isValid = false;
        element = angular.element('<upload-creative creatives-model="model" is-valid-model="isValid">');
        element = $compile(element)(scope);
        scope.$apply();
        $httpBackend.expectGET('components/directives/te-table-te-table.html').respond('');
    }));
});
