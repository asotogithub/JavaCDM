'use strict';

describe('Controller: FlyoutFiltersController', function () {
    var $scope,
        controller,
        filterValues = [
            {
                fieldName: 'site',
                values: [],
                key: 'siteId'
            },
            {
                fieldName: 'placement',
                values: [589864, 589865],
                key: 'placementId'
            },
            {
                fieldName: 'group',
                values: [],
                key: 'creativeGroupId'
            },
            {
                fieldName: 'schedule',
                values: [],
                key: 'creativeId'
            }
        ],
        filterOptions = {
            SITE: {
                text: 'Sites',
                value: [
                    {
                        id: 256893,
                        label: 'Site 01'
                    },
                    {
                        id: 256894,
                        label: 'Site 02'
                    },
                    {
                        id: 256895,
                        label: 'Site 03'
                    }
                ],
                index: 0,
                fieldName: 'site'
            },
            PLACEMENT: {
                text: 'Placements',
                value: [
                    {
                        id: 589864,
                        label: 'Placement 01'
                    },
                    {
                        id: 589865,
                        label: 'Placement 02'
                    },
                    {
                        id: 589866,
                        label: 'Placement 03'
                    }
                ],
                index: 1,
                fieldName: 'placement'
            },
            GROUP: {
                text: 'Creative Groups',
                value: [
                    {
                        id: 754752,
                        label: 'Creative Groups 01'
                    },
                    {
                        id: 754753,
                        label: 'Creative Groups 02'
                    },
                    {
                        id: 754754,
                        label: 'Creative Groups 03'
                    }
                ],
                index: 2,
                fieldName: 'group'
            },
            CREATIVE: {
                text: 'Creative',
                value: [
                    {
                        id: 269403,
                        label: 'Creative 01'
                    },
                    {
                        id: 269404,
                        label: 'Creative 02'
                    },
                    {
                        id: 269405,
                        label: 'Creative 03'
                    }
                ],
                index: 3,
                fieldName: 'schedule'
            }
        },
        subModel = [
            {
                backUpChildren: [],
                children: [
                    {
                        backUpChildren: [],
                        children: [
                            {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589864,
                                placementLabel: 'Placement 01',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            },
                            {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589865,
                                placementLabel: 'Placement 02',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            },
                            {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589866,
                                placementLabel: 'Placement 03',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            }
                        ],
                        checked: false,
                        field: 'section',
                        level: 1,
                        loadData: true,
                        nextLevelName: 'placement',
                        siteDetailLabel: '1-Section-Mare (6291690)',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteSectionId: 6291690,
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        siteSectionName: '1-Section-Mare'
                    }
                ],
                checked: false,
                field: 'site',
                loadData: true,
                nextLevelName: 'section',
                siteDetailLabel: '1-Site-Mare (6291689)',
                siteId: 6291689,
                siteLabel: '1-Site-Mare (6291689)',
                siteName: '1-Site-Mare'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                $state,
                                CONSTANTS) {
        $scope = $rootScope.$new();
        $scope.$parent = {
            $parent: {
                vm: {
                    selectedPivot: CONSTANTS.SCHEDULE.LEVEL.SITE,
                    submodel: subModel
                },
                $parent: {
                    vmList: {
                        filterOptions: filterOptions,
                        filterValues: filterValues
                    }
                }
            }
        };

        spyOn($state, 'go');

        controller = $controller('FlyoutFiltersController', {
            $scope: $scope
        });
    }));

    describe('Filter controller', function () {
        beforeEach(inject(function () {
            $scope.$parent.$parent.$parent.vmList.filterOptions = filterOptions;
        }));

        it('Should create an instance of the controller.', function () {
            expect(controller).not.toBeUndefined();
        });

        it('should invoke activate()', function () {
            expect(controller.filterOptions).toEqual({
                PLACEMENT: {
                    text: 'Placements',
                    value: [
                        {
                            id: 589864,
                            label: 'Placement 01'
                        },
                        {
                            id: 589865,
                            label: 'Placement 02'
                        },
                        {
                            id: 589866,
                            label: 'Placement 03'
                        }
                    ],
                    index: 1,
                    fieldName: 'placement',
                    order: 0
                },
                GROUP: {
                    text: 'Creative Groups',
                    value: [
                        {
                            id: 754752,
                            label: 'Creative Groups 01'
                        },
                        {
                            id: 754753,
                            label: 'Creative Groups 02'
                        },
                        {
                            id: 754754,
                            label: 'Creative Groups 03'
                        }
                    ],
                    index: 2,
                    fieldName: 'group',
                    order: 1
                },
                CREATIVE: {
                    text: 'Creative',
                    value: [
                        {
                            id: 269403,
                            label: 'Creative 01'
                        },
                        {
                            id: 269404,
                            label: 'Creative 02'
                        },
                        {
                            id: 269405,
                            label: 'Creative 03'
                        }
                    ],
                    index: 3,
                    fieldName: 'schedule',
                    order: 2
                }
            });
        });

        it('should log after timeout', (function () {
            expect(controller.getModelFilter(subModel[0])).toEqual({
                backUpChildren: [],
                children: [
                    {
                        backUpChildren: [
                            {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589866,
                                placementLabel: 'Placement 03',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            }
                        ],
                        children: [
                            {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589864,
                                placementLabel: 'Placement 01',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            }, {
                                checked: false,
                                field: 'placement',
                                level: 2,
                                loadData: false,
                                nextLevelName: 'group',
                                placementId: 589865,
                                placementLabel: 'Placement 02',
                                placementStatus: 'Accepted',
                                placementStatusLabel: 'Active',
                                siteId: 6291689,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionId: 6291690,
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare'
                            }
                        ],
                        checked: false,
                        field: 'section',
                        level: 1,
                        loadData: true,
                        nextLevelName: 'placement',
                        siteDetailLabel: '1-Section-Mare (6291690)',
                        siteId: 6291689,
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteSectionId: 6291690,
                        siteSectionLabel: '1-Section-Mare (6291690)',
                        siteSectionName: '1-Section-Mare'
                    }
                ],
                checked: false,
                field: 'site',
                loadData: true,
                nextLevelName: 'section',
                siteDetailLabel: '1-Site-Mare (6291689)',
                siteId: 6291689,
                siteLabel: '1-Site-Mare (6291689)',
                siteName: '1-Site-Mare'
            });
        }));
    });
});
