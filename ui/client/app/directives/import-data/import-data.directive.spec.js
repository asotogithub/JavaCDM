'use strict';

describe('Directive: importData', function () {
    var $scope,
        element;

    beforeEach(module('uiApp'));

    beforeEach(module('app/directives/import-data/import-data.html'));

    beforeEach(inject(function ($compile, $httpBackend, $state, $rootScope) {
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $scope = _.extend($rootScope.$new(), {
            vm: {
                options: {
                    description: 'description',
                    url: 'url',
                    utlTitle: 'urlTitle',
                    statusEmptyFile: 'statusEmptyFile',
                    statusExceedFileOption: 'statusExceedFileOption',
                    statusLimitSize: 'statusLimitSize',
                    statusUnknown: 'statusUnknown',
                    statusUnsupportedFormat: 'statusUnsupportedFormat',
                    fileType: 'XLSX',
                    limitSize: 60
                }
            }
        });

        element = angular.element(
            '<import-data on-close="vm.close(importData)" on-import-error="vm.importError(message)"' +
            '    on-import-resource="vm.importMedia(uuid)" options="vm.options"' +
            '    on-upload-error="vm.uploadError(message)" on-upload-resource="vm.uploadMedia(file)"' +
            '    on-upload-success="vm.uploadSuccess()">' +
            '</import-data>'
        );
        $compile(element)($scope);
        $scope.$apply();

        spyOn($state, 'go');
    }));

    describe('importData', function () {
        it('should render directive content', function () {
            expect(element.find('#fileName')).toBeTruthy();
            expect(element.find('#select-file')).toBeTruthy();
            expect(element.find('#cancel-import')).toBeTruthy();
            expect(element.find('#process-import')).toBeTruthy();
        });
    });
});
