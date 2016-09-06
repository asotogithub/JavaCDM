'use strict';

describe('Service: PlacementUtilService', function () {
    var PlacementUtilService,
        $httpBackend,
        CONSTANTS,
        placementList = [
            {
                name: '',
                packageName: '',
                section: [
                    {
                        id: 6064880,
                        name: 'UI test',
                        siteId: 6064873
                    },
                    {
                        id: 6064881,
                        name: 'UI test 2',
                        siteId: 6064873
                    }
                ],
                site: {
                    id: 6064873,
                    logicalDelete: 'N',
                    name: 'UI Test Site'
                },
                size: [
                    {
                        height: 150,
                        id: 5006631,
                        label: '180x150',
                        width: 180
                    }
                ]
            }
        ],
        placementListResponse = [
            {
                siteSectionId: 6064880,
                sectionName: 'UI test',
                sizeName: '180x150',

                sizeId: 5006631,
                height: 150,
                width: 180,
                name: 'UI Test Site - UI test - 180x150',
                packageName: '',
                siteName: 'UI Test Site',
                campaignId: 231233,
                ioId: 231234,
                siteId: 6064873,
                utcOffset: 0,
                countryCurrencyId: 1,
                inventory: 1,
                status: 'New'
            },
            {
                siteSectionId: 6064881,
                sectionName: 'UI test 2',
                sizeName: '180x150',
                sizeId: 5006631,
                height: 150,
                width: 180,
                name: 'UI Test Site - UI test 2 - 180x150',
                packageName: '',
                siteName: 'UI Test Site',
                campaignId: 231233,
                ioId: 231234,
                siteId: 6064873,
                utcOffset: 0,
                countryCurrencyId: 1,
                inventory: 1,
                status: 'New'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _PlacementUtilService_, _CONSTANTS_) {
        $httpBackend = _$httpBackend_;
        CONSTANTS = _CONSTANTS_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        PlacementUtilService = _PlacementUtilService_;
    }));

    describe('Placement utils', function () {
        it('should get the placement list parsed ', function () {
            expect(
                PlacementUtilService.parserPlacementList(placementList, 231233, 231234)
            ).toEqual(placementListResponse);
        });
    });
});
