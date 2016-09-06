'use strict';

describe('Controller: ExistingCreativesController', function () {
    var existingCreativesController,
        $httpBackend,
        $q,
        $scope,
        $state,
        $translate,
        CreativeService,
        creatives,
        modalInstance,
        selectedCreatives;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, _$q_, _$translate_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = jasmine.createSpyObj('$state', ['go']);
        $translate = _$translate_;
        creatives = $q.defer();

        CreativeService = jasmine.createSpyObj('CreativeService', ['searchCreativeNotAssociatedByCampaign']);
        CreativeService.searchCreativeNotAssociatedByCampaign.andReturn(creatives.promise);

        modalInstance = {
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
                then: jasmine.createSpy('modalInstance.result.then')
            }
        };

        existingCreativesController = $controller('ExistingCreativesController', {
            $scope: $scope,
            $modalInstance: modalInstance,
            $state: $state,
            CreativeService: CreativeService,
            data: []
        });
    }));

    describe('Add() Get Creatives selected', function () {
        it('should get all creatives selected when existingCreativesController.add() is resolved', function () {
            selectedCreatives = existingCreativesController.selectedRows = [
                {
                    alias: 'creativeAlias',
                    filename: 'creativeFileName.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2'
                },
                {
                    alias: 'creativeAlias2',
                    filename: 'creativeFileName2.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2'
                },
                {
                    alias: 'creativeAlias3',
                    filename: 'creativeFileName2.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2'
                }
            ];
            existingCreativesController.add();
            expect(modalInstance.close).toHaveBeenCalledWith([
                {
                    alias: 'creativeAlias',
                    filename: 'creativeFileName.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2',
                    file: {
                        status: 201,
                        statusTooltip: $translate.instant('creative.upload.status.success')
                    },
                    isValid: true,
                    isExisting: true
                },
                {
                    alias: 'creativeAlias2',
                    filename: 'creativeFileName2.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2',
                    file: {
                        status: 201,
                        statusTooltip: $translate.instant('creative.upload.status.success')
                    },
                    isValid: true,
                    isExisting: true
                },
                {
                    alias: 'creativeAlias3',
                    filename: 'creativeFileName2.gif',
                    width: '500',
                    height: '500',
                    creativeType: 'Image',
                    groupsCount: '2',
                    file: {
                        status: 201,
                        statusTooltip: $translate.instant('creative.upload.status.success')
                    },
                    isValid: true,
                    isExisting: true
                }
            ]);
        });
    });
});
