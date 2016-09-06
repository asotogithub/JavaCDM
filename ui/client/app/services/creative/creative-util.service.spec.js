'use strict';

describe('Service: CreativeUtilService', function () {
    var $scope,
        $translate,
        DialogFactory,
        UtilVar;

    beforeEach(module('uiApp'));

    beforeEach(
        inject(function (_$rootScope_, _$translate_, CreativeUtilService, _DialogFactory_) {
            DialogFactory = _DialogFactory_;
            UtilVar = CreativeUtilService;
            $scope = _$rootScope_.$new();
            $translate = _$translate_;
        })

    );

    it('should verify if a creative type is a XML type', function () {
        var creativeType = 'xml';

        expect(UtilVar.isCreativeXML(creativeType)).toBeTruthy();

        creativeType = 'vmap';

        expect(UtilVar.isCreativeXML(creativeType)).toBeTruthy();

        creativeType = 'jpg';

        expect(UtilVar.isCreativeXML(creativeType)).toBeFalsy();
    });

    it('should verify if a creative type is a Video Template type', function () {
        var creativeType = 'xml';

        expect(UtilVar.isCreativeVideoTemplate(creativeType)).toBeFalsy();

        creativeType = 'vmap';

        expect(UtilVar.isCreativeVideoTemplate(creativeType)).toBeTruthy();

        creativeType = 'jpg';

        expect(UtilVar.isCreativeVideoTemplate(creativeType)).toBeFalsy();
    });

    it('should verify if a creative type is a text', function () {
        var creativeType = 'txt';

        expect(UtilVar.isCreativeText(creativeType)).toBeTruthy();

        creativeType = 'png';

        expect(UtilVar.isCreativeText(creativeType)).toBeFalsy();
    });

    it('should verify if a creative has clickthrough by its type', function () {
        var creativeType = 'jpg';

        expect(UtilVar.hasClickThrough(creativeType)).toBeTruthy();

        creativeType = '3rd';

        expect(UtilVar.hasClickThrough(creativeType)).toBeFalsy();
    });

    it('should display a popup listing creatives with duplicate alias', function () {
        var error = {
                data: {
                    error: {
                        objectName: 'myCreative.jpg',
                        rejectedValue: 'duplicated'
                    }
                }
            },
            creativeList = [
                {
                    alias: 'duplicated',
                    filename: 'myCreative.jpg',
                    file: {
                        progress: 100,
                        status: 201,
                        statusTooltip: 'Success'
                    },
                    isValid: true,
                    agencyId: 9024559,
                    campaignId: 10423639,
                    creativeType: 'jpg',
                    height: 2000,
                    id: 11260215,
                    width: 3000,
                    creativeVersion: 2,
                    versions: [
                        {
                            alias: 'testAlias',
                            creativeId: 11260215,
                            isDateSet: 0,
                            startDate: '2016-08-12T15:07:17-07:00',
                            versionNumber: 1
                        }
                    ],
                    isDuplicate: false
                }
            ];

        spyOn(DialogFactory, 'showCustomDialog').andCallThrough();
        spyOn($scope, '$broadcast').andCallThrough();
        expect(creativeList[0].versions.length).toEqual(1);
        UtilVar.duplicateAliasPopup(error, $scope, creativeList);
        expect(DialogFactory.showCustomDialog).toHaveBeenCalled();
        expect($scope.$broadcast).toHaveBeenCalledWith('validateAllAlias');
        expect(creativeList[0].versions.length).toEqual(2);
    });
});
