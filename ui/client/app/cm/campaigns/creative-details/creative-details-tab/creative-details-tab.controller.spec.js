'use strict';

describe('Controller: CampaignCreativeDetailsTabController', function () {
    var $compile,
        $filter,
        $httpBackend,
        $q,
        $scope,
        $stateParams,
        $window,
        CampaignCreativeDetailsTabController,
        CreativeService,
        campaignId,
        creativeId,
        creativeDetails,
        creativePreview,
        domains;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, _$q_, $rootScope, _$filter_, _$compile_, _$window_) {
        $compile = _$compile_;
        $filter = _$filter_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        $window = _$window_;
        CreativeService = jasmine.createSpyObj('CreativeService', ['getCreative', 'getCreativeFile', 'updateCreative']);
        campaignId = $scope.campaignId = 1234;
        creativeId = $scope.creativeId = 12345;
        creativeDetails = $q.defer();
        creativePreview = $q.defer();
        domains = $q.defer();
        $stateParams = {};

        CreativeService.getCreative.andReturn(creativeDetails.promise);
        CreativeService.getCreativeFile.andReturn(creativePreview.promise);
        CampaignCreativeDetailsTabController = $controller('CampaignCreativeDetailsTabController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CreativeService: CreativeService
        });

        installPromiseMatchers();
    }));

    it('should invoke CreativeService.getCreative()', function () {
        expect(CreativeService.getCreative).toHaveBeenCalled();
    });

    describe('save()', function () {
        var deferred;

        beforeEach(function () {
            deferred = $q.defer();
            CreativeService.updateCreative.andReturn(deferred.promise);
        });

        it('should invoke CampaignsService.updateCampaignDetails()', function () {
            angular.extend(CampaignCreativeDetailsTabController, {
                clickThroughArray: [
                    {
                        sequence: null,
                        url: 'http://www.trueffect.com'
                    },
                    {
                        sequence: null,
                        url: 'http://www.msn.com'
                    }
                ],
                extendedPropertiesArray: [
                    {
                        value: 'extProp 1'
                    },
                    {
                        value: 'extProp 2'
                    },
                    {
                        value: 'extProp 3'
                    },
                    {
                        value: ''
                    },
                    {
                        value: ''
                    }
                ],
                creative: {
                    agencyId: 6031295,
                    alias: 'chicken - 3',
                    campaignId: 6040324,
                    clickthrough: 'http://www.trueffect.com',
                    clickthroughs: [
                        {
                            sequence: 2,
                            url: 'http://www.msn.com'
                        }
                    ],
                    createdDate: '2015-07-22T15:58:15-04:00',
                    createdTpwsKey: '766a05f8-7404-44fa-ae2e-890d65494e25',
                    creativeGroups: [
                        {
                            creativeGroups: [
                                {
                                    id: 29144,
                                    name: 'Default'
                                }
                            ]
                        }
                    ],
                    creativeType: 'zip',
                    externalId: 'external_Id_',
                    fileSize: 0,
                    filename: 'chicken-3.zip',
                    height: 100,
                    id: 6043656,
                    isExpandable: 0,
                    logicalDelete: 'N',
                    modifiedDate: '2015-07-22T15:58:15-04:00',
                    modifiedTpwsKey: '766a05f8-7404-44fa-ae2e-890d65494e25',
                    ownerCampaignId: 6040324,
                    released: 0,
                    richMediaId: 6043656,
                    scheduled: 0,
                    swfClickCount: 1,
                    width: 100
                }
            });

            CampaignCreativeDetailsTabController.save();

            expect(CreativeService.updateCreative).toHaveBeenCalledWith({
                agencyId: 6031295,
                alias: 'chicken - 3',
                campaignId: 6040324,
                clickthrough: 'http://www.trueffect.com',
                clickthroughs: [
                    {
                        sequence: 2,
                        url: 'http://www.msn.com'
                    }
                ],
                createdDate: '2015-07-22T15:58:15-04:00',
                createdTpwsKey: '766a05f8-7404-44fa-ae2e-890d65494e25',
                creativeGroups: [
                    {
                        creativeGroups: [
                            {
                                id: 29144,
                                name: 'Default'
                            }
                        ]
                    }
                ],
                creativeType: 'zip',
                externalId: 'external_Id_',
                fileSize: 0,
                filename: 'chicken-3.zip',
                height: 100,
                id: 6043656,
                isExpandable: 0,
                logicalDelete: 'N',
                modifiedDate: '2015-07-22T15:58:15-04:00',
                modifiedTpwsKey: '766a05f8-7404-44fa-ae2e-890d65494e25',
                ownerCampaignId: 6040324,
                released: 0,
                richMediaId: 6043656,
                scheduled: 0,
                swfClickCount: 1,
                width: 100,
                extProp1: 'extProp 1',
                extProp2: 'extProp 2',
                extProp3: 'extProp 3',
                extProp4: '',
                extProp5: ''
            });
            expect(CampaignCreativeDetailsTabController.promise).toBe(deferred.promise);
        });
    });

    describe('externalIdChange()', function () {
        it('Replace blank space with \'_\'', inject(function () {
            CampaignCreativeDetailsTabController.creative = {
                externalId: 'InitialValue'
            };

            $scope.vmEdit = CampaignCreativeDetailsTabController;

            var element =
                angular.element(
                        '<form name="creativeDetailsForm">' +
                        '    <input id="creativeExtId" ' +
                        '        name="creativeExtId" ' +
                        '        type="text" ' +
                        '        data-ng-trim="false" ' +
                        '        data-ng-change="vmEdit.externalIdChange()" ' +
                        '        data-ng-model="vmEdit.creative.externalId"/>' +
                        '</form>');

            $compile(element)($scope);
            $window.document.body.appendChild(element[0]);
            $scope.$digest();

            $scope.creativeDetailsForm.creativeExtId.$setViewValue('   Ext ID   ');
            expect($scope.vmEdit.creative.externalId).toEqual('___Ext_ID___');

            $scope.creativeDetailsForm.creativeExtId.$setViewValue('   Ext ID');
            expect($scope.vmEdit.creative.externalId).toEqual('___Ext_ID');

            $scope.creativeDetailsForm.creativeExtId.$setViewValue('Ext ID   ');
            expect($scope.vmEdit.creative.externalId).toEqual('Ext_ID___');
        }));
    });
});
