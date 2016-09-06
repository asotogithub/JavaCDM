'use strict';

describe('Service: ScheduleFlyoutUtilService', function () {
    // instantiate service
    var ScheduleFlyoutUtilService,
        bulkEditOpt,
        site = [
                {
                    children: [//SECTION
                        {
                            children: [//PLACEMENT
                                {
                                    children: [//CREATIVE GROUP
                                        {
                                            children: [//CREATIVE INSERTION
                                                {
                                                    creativeGroupLabel: 'New CG (6158592)',
                                                    placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                                    siteLabel: '1-Site-Mare (6291689)',
                                                    siteSectionLabel: '1-Section-Mare (6291690)',
                                                    uid: '2e6ea307-ddcc-4ba3-95b3-715211d7b444'
                                                }
                                            ],
                                            creativeAssociations: 1,
                                            creativeGroupDoCookieTarget: 0,
                                            creativeGroupDoDayPartTarget: 0,
                                            creativeGroupDoGeoTarget: 0,
                                            creativeGroupId: 6158592,
                                            creativeGroupLabel: 'New CG (6158592)',
                                            creativeGroupPriority: 0,
                                            expanded: true,
                                            field: 'group',
                                            flightDateEnd: '',
                                            flightDateStart: '',
                                            flightDates: '',
                                            frequencyCap: 1,
                                            frequencyCapWindow: 24,
                                            level: 3,
                                            loadData: false,
                                            nextLevelName: 'creative',
                                            placementAssociations: 30,
                                            placementId: 6512359,
                                            placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                            placementStatus: '',
                                            placementStatusLabel: '',
                                            siteDetailLabel: 'New CG (6158592)',
                                            siteId: 6291689,
                                            siteLabel: '1-Site-Mare (6291689)',
                                            siteName: '1-Site-Mare',
                                            siteSectionId: 6291690,
                                            siteSectionLabel: '1-Section-Mare (6291690)',
                                            siteSectionName: '1-Section-Mare (6291690)',
                                            uid: '9052e00c-7d19-4a09-8732-fc50cd666852',
                                            weight: 42
                                        }
                                    ],
                                    creativeGroupAssociations: 2,
                                    expanded: true,
                                    field: 'placement',
                                    level: 2,
                                    nextLevelName: 'group',
                                    placementId: 6315747,
                                    placementLabel: 'chicken-rechicken (6315747)',
                                    placementStatus: 'Accepted',
                                    placementStatusLabel: 'Active',
                                    siteDetailLabel: 'chicken-rechicken (6315747)',
                                    siteId: 6291689,
                                    siteLabel: '1-Site-Mare (6291689)',
                                    siteName: '1-Site-Mare',
                                    siteSectionId: 6291690,
                                    siteSectionLabel: '1-Section-Mare (6291690)',
                                    siteSectionName: '1-Section-Mare (6291690)',
                                    uid: '16eb6cb7-ddc8-4976-99de-3794ade536c9'
                                }
                            ],
                            expanded: true,
                            field: 'section',
                            level: 1,
                            nextLevelName: 'placement',
                            placementAssociations: '',
                            siteDetailLabel: '1-Section-Mare',
                            siteId: 6291689,
                            siteLabel: '1-Site-Mare (6291689)',
                            siteName: '1-Site-Mare',
                            siteSectionId: 6291690,
                            siteSectionLabel: '1-Section-Mare (6291690)',
                            siteSectionName: '1-Section-Mare (6291690)',
                            uid: '443217bf-f595-48f7-9a33-90a41932f567'
                        }
                    ],
                    expanded: true,
                    field: 'site',
                    id: undefined,
                    loadData: true,
                    nextLevelName: 'section',
                    siteDetailLabel: '1-Site-Mare (6291689)',
                    siteId: 6291689,
                    siteLabel: '1-Site-Mare (6291689)',
                    siteName: '1-Site-Mare'
                }
            ],
        creativeGroup = [
            {
                children: [//CREATIVE INSERTION
                    {
                        creativeGroupLabel: 'New CG (6158592)',
                        placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                        siteLabel: '1-Site-Mare (6291689)',
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        uid: '2e6ea307-ddcc-4ba3-95b3-715211d7b444'
                    }
                ],
                creativeAssociations: 1,
                creativeGroupDoCookieTarget: 0,
                creativeGroupDoDayPartTarget: 0,
                creativeGroupDoGeoTarget: 0,
                creativeGroupId: 6158592,
                creativeGroupLabel: 'New CG (6158592)',
                creativeGroupPriority: 0,
                expanded: true,
                field: 'group'
            }
        ],
        creativeGroupWithNoCreatives = [
            {
                creativeAssociations: 1,
                creativeGroupDoCookieTarget: 0,
                creativeGroupDoDayPartTarget: 0,
                creativeGroupDoGeoTarget: 0,
                creativeGroupId: 6158592,
                creativeGroupLabel: 'New CG (6158592)',
                creativeGroupPriority: 0,
                expanded: false,
                field: 'group'
            }
        ];

    // load the service's module
    beforeEach(module('uiApp'));

    beforeEach(inject(function (_ScheduleFlyoutUtilService_) {
        ScheduleFlyoutUtilService = _ScheduleFlyoutUtilService_;
        bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
    }));

    describe('Placement utils', function () {
        it('should site has visible creative insertions', function () {
            ScheduleFlyoutUtilService.hasSiteVisibleCreatives(site[0]);
            bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
            expect(bulkEditOpt.isCreativeVisible).toBe(true);
        });

        it('should group has visible creative insertions', function () {
            ScheduleFlyoutUtilService.hasCreativeGroupVisibleCreatives(creativeGroup[0]);
            bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
            expect(bulkEditOpt.isCreativeVisible).toBe(true);
            expect(bulkEditOpt.isCreativeGroupVisible).toBe(false);
        });

        it('should group has visible creative insertions', function () {
            ScheduleFlyoutUtilService.hasCreativeGroupVisibleCreatives(creativeGroupWithNoCreatives[0]);
            bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
            expect(bulkEditOpt.isCreativeVisible).toBe(false);
            expect(bulkEditOpt.isCreativeGroupVisible).toBe(true);
        });
    });
});
