'use strict';

describe('Controller: ScheduleTabController', function () {
    var $scope,
        $stateParams,
        $timezones,
        $translate,
        CampaignsService,
        DateTimeService,
        DialogFactory,
        ScheduleTabController,
        campaignId,
        creativeInsertionList,
        creativeList,
        lodash,
        placementList,
        $compile,
        element;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $q,
                                $rootScope,
                                $state,
                                _$translate_,
                                _CampaignsService_,
                                _DialogFactory_,
                                _DateTimeService_,
                                _lodash_,
                                $templateCache,
                                _$compile_) {
        $compile = _$compile_;
        $scope = $rootScope.$new();
        $stateParams = {};
        $timezones = {};
        $translate = _$translate_;
        CampaignsService = _CampaignsService_;
        DateTimeService = _DateTimeService_;
        DialogFactory = _DialogFactory_;
        campaignId = $stateParams.campaignId = 1337;
        creativeInsertionList = $q.defer();
        creativeList = $q.defer();
        lodash = _lodash_;
        placementList = $q.defer();

        spyOn(CampaignsService, 'getCreativeInsertions').andReturn(creativeInsertionList.promise);
        spyOn(CampaignsService, 'getCreativeGroupCreatives').andReturn(creativeList.promise);
        spyOn(CampaignsService, 'getPlacements').andReturn(placementList.promise);
        spyOn($state, 'go');
        $scope.mainVm = {
            cm: {
                schedule: {
                    isFlyoutVisible: false
                }
            }
        };
        ScheduleTabController = $controller('ScheduleTabController', {
            $scope: $scope,
            $stateParams: $stateParams,
            $timezones: $timezones,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            DateTimeService: DateTimeService,
            lodash: lodash
        });
    }));

    describe('activate()', function () {
        var response;

        beforeEach(function () {
            response = [
                {
                    campaignId: 9078016,
                    createdDate: '2015-08-04T09:48:51-06:00',
                    createdTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    creativeAlias: 'image4100x100',
                    primaryClickthrough: 'http://foo.com',
                    creativeGroupId: 9111244,
                    creativeGroupName: 'group 2',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116106,
                    creativeInsertionRootId: 9160839,
                    endDate: '2015-09-17T00:00:00-07:00',
                    id: 10000078,
                    logicalDelete: 'N',
                    modifiedDate: '2015-08-04T09:48:51-06:00',
                    modifiedTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    placementEndDate: '2015-10-03T21:00:00-07:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-09-03T21:00:00-07:00',
                    placementStatus: 'Accepted',
                    released: 0,
                    sequence: 0,
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    startDate: '2015-06-17T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    createdDate: '2015-08-04T10:05:59-06:00',
                    createdTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    creativeAlias: '3image160x600',
                    primaryClickthrough: 'http://bar.com',
                    creativeGroupId: 9111251,
                    creativeGroupName: 'group 3',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9160869,
                    creativeInsertionRootId: 9160881,
                    endDate: '2015-09-17T00:00:00-07:00',
                    id: 10000081,
                    logicalDelete: 'N',
                    modifiedDate: '2015-08-04T10:05:59-06:00',
                    modifiedTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    placementEndDate: '2015-10-03T21:00:00-07:00',
                    placementId: 9160681,
                    placementName: 'sport - Header - 160x600',
                    placementStartDate: '2015-09-03T21:00:00-07:00',
                    placementStatus: 'New',
                    released: 0,
                    sequence: 0,
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    startDate: '2015-06-17T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                }
            ];
        });

        it('should invoke CampaignsService.getCreativeInsertions()', function () {
            expect(CampaignsService.getCreativeInsertions).toHaveBeenCalledWith(campaignId, 'site', 'site');
        });

        it('should set promise', function () {
            expect(ScheduleTabController.promise).toBe(creativeInsertionList.promise);
        });

        it('should set creativeInsertionList when service call is resolved', function () {
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual(
                [
                    {
                        id: undefined,
                        siteId: 9107159,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        creativeGroupId: undefined,
                        loadData: false,
                        field: 'site',
                        nextLevelName: 'section',
                        siteLabel: 'IOS SITE (9107159)',
                        siteName: 'IOS SITE',
                        siteDetailLabel: 'IOS SITE (9107159)',
                        creativeGroupLabel: undefined,
                        expanded: false,
                        children: [
                            {
                                siteLabel: 'IOS SITE (9107159)',
                                siteSectionLabel: null,
                                placementLabel: null,
                                creativeGroupLabel: undefined,
                                creativeLabel: undefined
                            }
                        ],
                        treeRowId: '78ce3c0f32db3877a90f54e060993929bf0c9316',
                        backUpChildren: []
                    },
                    {
                        id: undefined,
                        siteId: 9107208,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        creativeGroupId: undefined,
                        loadData: false,
                        field: 'site',
                        nextLevelName: 'section',
                        siteLabel: 'sport (9107208)',
                        siteName: 'sport',
                        siteDetailLabel: 'sport (9107208)',
                        creativeGroupLabel: undefined,
                        expanded: false,
                        children: [
                            {
                                siteLabel: 'sport (9107208)',
                                siteSectionLabel: null,
                                placementLabel: null,
                                creativeGroupLabel: undefined,
                                creativeLabel: undefined
                            }
                        ],
                        treeRowId: '19f940f2a0bbd85d3bc5f671e6f3c4438294c223',
                        backUpChildren: []
                    }
                ]);
        });

        it('should updateModel() when service call is resolved', function () {
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual([
                {
                    id: undefined,
                    siteId: 9107159,
                    creativeId: undefined,
                    creativeLabel: undefined,
                    creativeGroupId: undefined,
                    loadData: false,
                    field: 'site',
                    nextLevelName: 'section',
                    siteLabel: 'IOS SITE (9107159)',
                    siteName: 'IOS SITE',
                    siteDetailLabel: 'IOS SITE (9107159)',
                    creativeGroupLabel: undefined,
                    expanded: false,
                    children: [
                        {
                            siteLabel: 'IOS SITE (9107159)',
                            siteSectionLabel: null,
                            placementLabel: null,
                            creativeGroupLabel: undefined,
                            creativeLabel: undefined
                        }
                    ],
                    treeRowId: '78ce3c0f32db3877a90f54e060993929bf0c9316',
                    backUpChildren: []
                },
                {
                    id: undefined,
                    siteId: 9107208,
                    creativeId: undefined,
                    creativeLabel: undefined,
                    creativeGroupId: undefined,
                    loadData: false,
                    field: 'site',
                    nextLevelName: 'section',
                    siteLabel: 'sport (9107208)',
                    siteName: 'sport',
                    siteDetailLabel: 'sport (9107208)',
                    creativeGroupLabel: undefined,
                    expanded: false,
                    children: [
                        {
                            siteLabel: 'sport (9107208)',
                            siteSectionLabel: null,
                            placementLabel: null,
                            creativeGroupLabel: undefined,
                            creativeLabel: undefined
                        }
                    ],
                    treeRowId: '19f940f2a0bbd85d3bc5f671e6f3c4438294c223',
                    backUpChildren: []
                }
            ]);
        });
    });

    describe('addScheduleAssignments()', function () {
        var result,
            response;

        beforeEach(inject(function ($q) {
            result = $q.defer();
            response = [
                {
                    campaignId: 9078016,
                    creativeAlias: 'image4100x100',
                    creativeGroupId: 9111244,
                    creativeGroupName: 'group 2',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116106,
                    endDate: '2015-09-03T00:00:00-06:00',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '100x100',
                    startDate: '2015-08-04T00:00:00-07:00',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    creativeAlias: 'image2100x100',
                    creativeGroupId: 9111244,
                    creativeGroupName: 'group 2',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116105,
                    endDate: '2015-09-03T00:00:00-06:00',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '100x100',
                    startDate: '2015-08-04T00:00:00-07:00',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    creativeAlias: 'image2100x100',
                    creativeGroupId: 9111251,
                    creativeGroupName: 'group 3',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116105,
                    endDate: '2015-09-03T00:00:00-06:00',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '100x100',
                    startDate: '2015-08-04T00:00:00-07:00',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    creativeAlias: 'image4100x100',
                    creativeGroupId: 9111251,
                    creativeGroupName: 'group 3',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116106,
                    endDate: '2015-09-03T00:00:00-06:00',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-08-04T00:00:00-06:00',
                    placementStatus: 'New',
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    sizeName: '100x100',
                    startDate: '2015-08-04T00:00:00-07:00',
                    weight: 100
                }
            ];
            ScheduleTabController.creativeInsertionList = [
                {
                    campaignId: 9078016,
                    createdDate: '2015-08-04T09:48:51-06:00',
                    createdTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    creativeAlias: 'image4100x100',
                    primaryClickthrough: 'http://foo.com',
                    creativeGroupId: 9111244,
                    creativeGroupName: 'group 2',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9116106,
                    creativeInsertionRootId: 9160839,
                    endDate: '2015-09-17T00:00:00-07:00',
                    id: 10000078,
                    logicalDelete: 'N',
                    modifiedDate: '2015-08-04T09:48:51-06:00',
                    modifiedTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160671,
                    placementName: 'IOS SITE - SITE IOS - 100x100',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    released: 0,
                    sequence: 0,
                    siteId: 9107159,
                    siteName: 'IOS SITE',
                    siteSectionId: 9107160,
                    siteSectionName: 'SITE IOS',
                    startDate: '2015-06-17T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    createdDate: '2015-08-04T10:05:59-06:00',
                    createdTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    creativeAlias: '3image160x600',
                    primaryClickthrough: 'http://bar.com',
                    creativeGroupId: 9111251,
                    creativeGroupName: 'group 3',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9160869,
                    creativeInsertionRootId: 9160881,
                    endDate: '2015-09-17T00:00:00-07:00',
                    id: 10000081,
                    logicalDelete: 'N',
                    modifiedDate: '2015-08-04T10:05:59-06:00',
                    modifiedTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    placementEndDate: '2015-09-03T00:00:00-07:00',
                    placementId: 9160681,
                    placementName: 'sport - Header - 160x600',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'New',
                    released: 0,
                    sequence: 0,
                    siteId: 9107208,
                    siteName: 'sport',
                    siteSectionId: 9107209,
                    siteSectionName: 'Header',
                    startDate: '2015-06-17T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                },
                {
                    campaignId: 9078016,
                    createdDate: '2015-08-04T10:05:59-06:00',
                    createdTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    creativeAlias: 'image003',
                    primaryClickthrough: 'http://foo.bar.com',
                    creativeGroupId: 9111251,
                    creativeGroupName: 'group 3',
                    creativeGroupWeight: 100,
                    creativeGroupWeightEnabled: false,
                    creativeId: 9160868,
                    creativeInsertionRootId: 9160880,
                    endDate: '2015-09-17T00:00:00-07:00',
                    id: 10000080,
                    logicalDelete: 'N',
                    modifiedDate: '2015-08-04T10:05:59-06:00',
                    modifiedTpwsKey: '5204f286-6349-4ef0-aae5-05149836de14',
                    placementEndDate: '2015-09-03T00:00:00-06:00',
                    placementId: 9160678,
                    placementName: 'ESPN - Homepage - 120x600',
                    placementStartDate: '2015-08-04T00:00:00-07:00',
                    placementStatus: 'Rejected',
                    released: 0,
                    sequence: 0,
                    siteId: 9107198,
                    siteName: 'ESPN',
                    siteSectionId: 9107199,
                    siteSectionName: 'Homepage',
                    startDate: '2015-06-17T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                }
            ];

            spyOn(DialogFactory, 'showCustomDialog').andReturn({
                result: result.promise
            });
        }));

        it('should showCustomDialog()', function () {
            ScheduleTabController.addScheduleAssignments();

            creativeList.resolve([]);
            placementList.resolve([]);
            $scope.$apply();

            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                controller: 'AddScheduleAssignmentsController',
                size: DialogFactory.DIALOG.SIZE.LARGE,
                template: 'app/cm/campaigns/schedule-tab/add-schedule-assignments/add-schedule-assignments.html',
                type: DialogFactory.DIALOG.TYPE.CUSTOM,
                windowClass: 'modal-' + DialogFactory.DIALOG.SIZE.EXTRA_LARGE,
                data: {
                    campaignId: campaignId,
                    name: $translate.instant('schedule.availablePlacementsAndCreative'),
                    title: $translate.instant('schedule.addScheduleAssignments'),
                    toolTip: undefined,
                    creatives: [],
                    placements: []
                }
            });
        });

        it('should not showDuplicateScheduleAssignments() if result has no duplicate assignments', function () {
            ScheduleTabController.addScheduleAssignments();
            creativeList.resolve([]);
            placementList.resolve([]);
            $scope.$apply();

            expect(DialogFactory.showCustomDialog.calls.length).toBe(1);
        });

        it('should not update creativeInsertionList when there are no new assignments', function () {
            var expected = ScheduleTabController.creativeInsertionList;

            ScheduleTabController.addScheduleAssignments();
            creativeList.resolve([]);
            placementList.resolve([]);
            $scope.$apply();

            expect(ScheduleTabController.creativeInsertionList).toBe(expected);
        });

        it('should not updateModel() when there are no new assignments', function () {
            var expected = ScheduleTabController.model;

            ScheduleTabController.addScheduleAssignments();
            creativeList.resolve([]);
            placementList.resolve([]);
            $scope.$apply();

            expect(ScheduleTabController.model).toBe(expected);
        });
    });

    describe('associationsCellsRenderer()', function () {
        it('should render creative group associations at placement level', function () {
            expect(ScheduleTabController.associationsCellsRenderer({
                field: 'placement',
                creativeGroupAssociations: 69
            })).toEqual('<span class="association"><i class="creative-group fa fa-users"></i>69</span>');
        });

        it('should render placement and creative associations at creative group level', function () {
            expect(ScheduleTabController.associationsCellsRenderer({
                field: 'group',
                placementAssociations: 13,
                creativeAssociations: 37
            })).toEqual(
                '<span class="association"><i class="placement fa fa-hand-paper-o"></i>13</span>' +
                '<span class="association"><i class="creative fa fa-camera"></i>37</span>');
        });

        it('should render placement and creative group associations at creative level', function () {
            expect(ScheduleTabController.associationsCellsRenderer({
                field: 'creative',
                placementAssociations: 4,
                creativeGroupAssociations: 2
            })).toEqual(
                '<span class="association"><i class="placement fa fa-hand-paper-o"></i>4</span>' +
                '<span class="association"><i class="creative-group fa fa-users"></i>2</span>');
        });
    });

    describe('clickThroughUrlCellsRenderer()', function () {
        it('should render clickthrough URLs', function () {
            expect(ScheduleTabController.clickThroughUrlCellsRenderer()).toBeUndefined();
            expect(ScheduleTabController.clickThroughUrlCellsRenderer(null)).toBeUndefined();
            expect(ScheduleTabController.clickThroughUrlCellsRenderer([])).toBeUndefined();
            expect(ScheduleTabController.clickThroughUrlCellsRenderer([
                'http://foo.com',
                'http://bar.com',
                'http://foo.bar.com'
            ])).toEqual(
                '<ul class="clickthrough-urls">' +
                '<li title="http://foo.com">http://foo.com</li>' +
                '<li title="http://bar.com">http://bar.com</li>' +
                '<li title="http://foo.bar.com">http://foo.bar.com</li>' +
                '</ul>'
            );
        });
    });

    describe('placementStatusCellsRenderer()', function () {
        it('should render placement status w/ `status-active` styling when `Accepted`', function () {
            expect(ScheduleTabController.placementStatusCellsRenderer({
                placementStatus: 'Accepted'
            }, 'Active')).toEqual('<span class="status-active">Active</span>');
        });

        it('should render placement status w/ `status-planning` styling when `New`', function () {
            expect(ScheduleTabController.placementStatusCellsRenderer({
                placementStatus: 'New'
            }, 'Planning')).toEqual('<span class="status-planning">Planning</span>');
        });

        it('should render placement status w/ `status-inactive` styling when `Rejected`', function () {
            expect(ScheduleTabController.placementStatusCellsRenderer({
                placementStatus: 'Rejected'
            }, 'Inactive')).toEqual('<span class="status-inactive">Inactive</span>');
        });

        it('should render placement status w/o styling when not a valid value', function () {
            expect(ScheduleTabController.placementStatusCellsRenderer({
                placementStatus: 'notValid'
            }, 'notValid')).toEqual('<span class="">notValid</span>');
        });
    });

    describe('rowCollapsed()', function () {
        it('should remove entries from expanded by row IDs', function () {
            ScheduleTabController.expanded = [
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                },
                {
                    siteId: 5
                },
                {
                    siteId: 6,
                    siteSectionId: 7,
                    placementId: 8,
                    creativeGroupId: 9
                }
            ];

            ScheduleTabController.rowCollapsed({
                siteId: 1,
                siteSectionId: 2,
                placementId: 3,
                creativeGroupId: 4
            });
            expect(ScheduleTabController.expanded).toEqual([
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                }, {
                    siteId: 5
                }, {
                    siteId: 6,
                    siteSectionId: 7,
                    placementId: 8,
                    creativeGroupId: 9
                }
            ]);

            ScheduleTabController.rowCollapsed({
                siteId: 6
            });
            expect(ScheduleTabController.expanded).toEqual([
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                }, {
                    siteId: 5
                }, {
                    siteId: 6,
                    siteSectionId: 7,
                    placementId: 8,
                    creativeGroupId: 9
                }
            ]);
        });
    });

    describe('rowExpanded()', function () {
        it('should push row IDs to expanded', function () {
            ScheduleTabController.expanded = [
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                }
            ];

            ScheduleTabController.rowExpanded({
                siteId: 5
            });
            expect(ScheduleTabController.expanded).toEqual([
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                }
            ]);

            ScheduleTabController.rowExpanded({
                siteId: 6,
                siteSectionId: 7,
                placementId: 8,
                creativeGroupId: 9,
                nonIdProperty: 0
            });
            expect(ScheduleTabController.expanded).toEqual([
                {
                    siteId: 1,
                    siteSectionId: 2,
                    placementId: 3,
                    creativeGroupId: 4
                }
            ]);
        });
    });

    describe('getRowRenderer()', function () {
        beforeEach(function () {
            var html = '<script type="text/ng-template" id="schedule-row-renderer.html">' +
                '<div class="display-inline">' +
                '<i class="{{vm.class}}"></i>' +
                '<span class="wrapped-text" title="{{vm.label}}">{{vm.label}}</span>' +
                '</div>' +
                '</script>';

            element = $compile(
                angular.element(
                    html
                )
            )($scope);
        });

        it('should render placement icon at placement level', function () {
            expect(
                ScheduleTabController.getRowRenderer({
                    field: 'placement'
                }, 'IOS SITE - SITE IOS - 100x100 (9160671)')
            ).toEqual('<div class="display-inline">' +
                '<i class="placement fa fa-hand-paper-o"></i>' +
                '<span class="wrapped-text" title="IOS SITE - SITE IOS - 100x100 (9160671)">' +
                'IOS SITE - SITE IOS - 100x100 (9160671)' +
                '</span></div>');
        });

        it('should render creative group icon at creative group level', function () {
            expect(
                ScheduleTabController.getRowRenderer({
                    field: 'group'
                }, 'group 2 (9111244)')
            ).toEqual('<div class="display-inline">' +
                '<i class="creative-group fa fa-users"></i>' +
                '<span class="wrapped-text" title="group 2 (9111244)">' +
                'group 2 (9111244)' +
                '</span></div>');
        });

        it('should render creative icon at schedule level', function () {
            expect(
                ScheduleTabController.getRowRenderer({
                    field: 'schedule'
                }, 'image4100x100 (9116106)')
            ).toEqual('<div class="display-inline">' +
                '<i class="creative fa fa-camera"></i>' +
                '<span class="wrapped-text" title="image4100x100 (9116106)">' +
                'image4100x100 (9116106)' +
                '</span></div>');
        });
    });

    describe('getExportAll()', function () {
        it('should invoke CampaignsService.exportResource()', function () {
            spyOn(CampaignsService, 'exportResource').andReturn(ScheduleTabController.promise);
            ScheduleTabController.getExportAll();
            expect(CampaignsService.exportResource).toHaveBeenCalledWith(
                campaignId, 'xlsx', 'CreativeInsertion');
        });
    });

    describe('Search Server with all pivots.', function () {
        var response;

        beforeEach(function () {
            spyOn(CampaignsService, 'searchCreativeInsertions').andReturn(creativeInsertionList.promise);
        });

        it('should set Creative List when service call a search server with Site Pivot', function () {
            response = [
                {
                    siteId: 6086072,
                    siteName: 'SITE - Mare tests',
                    siteSectionId: 6086073,
                    siteSectionName: 'SiteSection - Mare tests'
                },
                {
                    siteId: 6086072,
                    siteName: 'SITE - Mare tests'
                },
                {
                    placementAssociationsWithCreativeGroups: 3,
                    placementEndDate: '2016-02-19T23:59:59-07:00',
                    placementId: 6512359,
                    placementName: '1-Site-Mare - 1-Section-Mare - 100x100',
                    placementStartDate: '2016-01-20T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    siteSectionId: 6291690,
                    siteSectionName: '1-Section-Mare'
                },
                {
                    siteId: 6291689,
                    siteName: '1-Site-Mare'
                }
            ];

            ScheduleTabController.serverSideSearch('site');
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual(
                [
                    {
                        id: undefined,
                        siteId: 6086072,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        creativeGroupId: undefined,
                        loadData: true,
                        field: 'site',
                        nextLevelName: 'section',
                        siteLabel: 'SITE - Mare tests (6086072)',
                        siteName: 'SITE - Mare tests',
                        siteDetailLabel: 'SITE - Mare tests (6086072)',
                        creativeGroupLabel: undefined,
                        expanded: true,
                        children: [
                            {
                                siteId: 6086072,
                                loadData: false,
                                field: 'section',
                                nextLevelName: 'placement',
                                siteSectionId: 6086073,
                                siteLabel: 'SITE - Mare tests (6086072)',
                                siteName: 'SITE - Mare tests',
                                siteSectionLabel: 'SiteSection - Mare tests (6086073)',
                                siteSectionName: 'SiteSection - Mare tests',
                                siteDetailLabel: 'SiteSection - Mare tests (6086073)',
                                expanded: false,
                                children: [
                                    {
                                        siteLabel: 'SITE - Mare tests (6086072)',
                                        siteSectionLabel: 'SiteSection - Mare tests (6086073)',
                                        placementLabel: null,
                                        creativeGroupLabel: null,
                                        creativeLabel: null
                                    }
                                ],
                                treeRowId: '077bd281b883faf5549b543a173b2e6243910352',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: 'aead5c7574311ea3621063fae272a4bbc4fad489',
                        backUpChildren: []
                    },
                    {
                        id: undefined,
                        siteId: 6291689,
                        creativeId: undefined,
                        creativeLabel: undefined,
                        creativeGroupId: undefined,
                        loadData: true,
                        field: 'site',
                        nextLevelName: 'section',
                        siteLabel: '1-Site-Mare (6291689)',
                        siteName: '1-Site-Mare',
                        siteDetailLabel: '1-Site-Mare (6291689)',
                        creativeGroupLabel: undefined,
                        expanded: true,
                        children: [
                            {
                                siteId: 6291689,
                                loadData: true,
                                field: 'section',
                                nextLevelName: 'placement',
                                siteSectionId: 6291690,
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteSectionLabel: '1-Section-Mare (6291690)',
                                siteSectionName: '1-Section-Mare',
                                siteDetailLabel: '1-Section-Mare (6291690)',
                                expanded: true,
                                children: [
                                    {
                                        id: undefined,
                                        siteId: 6291689,
                                        loadData: false,
                                        field: 'placement',
                                        nextLevelName: 'group',
                                        siteSectionId: 6291690,
                                        creativeId: undefined,
                                        creativeGroupId: undefined,
                                        creativeGroupLabel: undefined,
                                        creativeLabel: undefined,
                                        placementId: 6512359,
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteName: undefined,
                                        siteSectionLabel: '1-Section-Mare (6291690)',
                                        siteSectionName: '1-Section-Mare',
                                        placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                        siteDetailLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                        placementStatus: 'Accepted',
                                        placementStatusLabel: 'Active',
                                        creativeGroupAssociations: 3,
                                        flightDates: undefined,
                                        flightDateStart: undefined,
                                        flightDateEnd: undefined,
                                        sizeName: undefined,
                                        expanded: false,
                                        children: [
                                            {
                                                siteLabel: '1-Site-Mare (6291689)',
                                                siteSectionLabel: '1-Section-Mare (6291690)',
                                                placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                                creativeGroupLabel: undefined,
                                                creativeLabel: undefined
                                            }
                                        ],
                                        treeRowId: 'eb130c9ab67e28726ac9e03dcc1ee3c8c131d03a',
                                        backUpChildren: [],
                                        placementTagAssociationIds: {
                                            siteId: 6291689,
                                            sectionId: 6291690
                                        }
                                    }
                                ],
                                treeRowId: '144168c929b4b79f1e960e88c813214023d6a1f3',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: '826200a842893ad9a66bf990b534b6d86b4ff034',
                        backUpChildren: []
                    }
                ]);
        });

        it('should set  Creative List when service call a search server with Placement Pivot', function () {
            response = [
                {
                    creativeGroupAssociationsWithCreatives: 1,
                    creativeGroupAssociationsWithPlacements: 1,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 34158,
                    creativeGroupName: 'Default',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 21,
                    creativeGroupWeightEnabled: 0,
                    placementAssociationsWithCreativeGroups: 3,
                    placementEndDate: '2016-02-19T23:59:59-07:00',
                    placementId: 6512359,
                    placementName: '1-Site-Mare - 1-Section-Mare - 100x100',
                    placementStartDate: '2016-01-20T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteSectionId: 6291690
                }
            ];

            ScheduleTabController.selectedPivot = ScheduleTabController.SCHEDULE_LEVEL.PLACEMENT;
            ScheduleTabController.serverSideSearch('default');
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual(
                [
                    {
                        id: undefined,
                        siteId: undefined,
                        loadData: true,
                        field: 'placement',
                        nextLevelName: 'group',
                        siteSectionId: undefined,
                        creativeId: undefined,
                        creativeGroupId: undefined,
                        creativeGroupLabel: undefined,
                        creativeLabel: undefined,
                        placementId: 6512359,
                        siteLabel: undefined,
                        siteName: undefined,
                        siteSectionLabel: undefined,
                        siteSectionName: undefined,
                        placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                        siteDetailLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                        placementStatus: 'Accepted',
                        placementStatusLabel: 'Active',
                        creativeGroupAssociations: 3,
                        flightDates: undefined,
                        flightDateStart: undefined,
                        flightDateEnd: undefined,
                        sizeName: undefined,
                        expanded: true,
                        children: [
                            {
                                siteId: undefined,
                                loadData: false,
                                field: 'group',
                                nextLevelName: 'schedule',
                                creativeId: undefined,
                                siteSectionId: undefined,
                                placementId: 6512359,
                                creativeGroupId: 34158,
                                siteLabel: undefined,
                                creativeLabel: undefined,
                                siteName: undefined,
                                siteSectionLabel: undefined,
                                siteSectionName: undefined,
                                placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                creativeGroupLabel: 'Default (34158)',
                                siteDetailLabel: 'Default (34158)',
                                placementAssociations: 1,
                                creativeAssociations: 1,
                                weight: 21,
                                frequencyCap: 1,
                                frequencyCapWindow: 24,
                                creativeGroupPriority: 0,
                                creativeGroupDoCookieTarget: 0,
                                creativeGroupDoGeoTarget: 0,
                                creativeGroupDoDayPartTarget: 0,
                                flightDates: undefined,
                                flightDateStart: undefined,
                                flightDateEnd: undefined,
                                expanded: false,
                                editSupport: {
                                    weight: true
                                },
                                children: [
                                    {
                                        siteLabel: undefined,
                                        siteSectionLabel: undefined,
                                        placementLabel: '1-Site-Mare - 1-Section-Mare - 100x100 (6512359)',
                                        creativeGroupLabel: 'Default (34158)',
                                        creativeLabel: undefined
                                    }
                                ],
                                treeRowId: '7f77bba28c997ef2a482f2c3252df3997c561772',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: '2b166875c06f5784a5f85c13b1c4d61f885a4af9',
                        backUpChildren: [],
                        placementTagAssociationIds: {
                            siteId: 6291689,
                            sectionId: 6291690
                        }
                    }
                ]);
        });

        it('should set Creative List when service call a search server with Creative Group Pivot', function () {
            response = [
                {
                    creativeGroupAssociationsWithCreatives: 1,
                    creativeGroupAssociationsWithPlacements: 2,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 6345544,
                    creativeGroupName: 'My GC',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 55,
                    creativeGroupWeightEnabled: 0,
                    placementAssociationsWithCreativeGroups: 2,
                    placementEndDate: '2015-12-13T23:59:59-07:00',
                    placementId: 6315747,
                    placementName: 'chicken-rechicken',
                    placementStartDate: '2015-11-13T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteName: '1-Site-Mare'
                },
                {
                    creativeGroupAssociationsWithCreatives: 7,
                    creativeGroupAssociationsWithPlacements: 3,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 84,
                    creativeGroupId: 6158649,
                    creativeGroupName: 'ACG1 AA',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 1,
                    creativeGroupWeightEnabled: 1,
                    placementAssociationsWithCreativeGroups: 2,
                    placementEndDate: '2015-12-13T23:59:59-07:00',
                    placementId: 6315747,
                    placementName: 'chicken-rechicken',
                    placementStartDate: '2015-11-13T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    siteId: 6291689,
                    siteName: '1-Site-Mare'
                }
            ];

            ScheduleTabController.selectedPivot = ScheduleTabController.SCHEDULE_LEVEL.CREATIVE_GROUP;
            ScheduleTabController.serverSideSearch('chicken');
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual(
                [
                    {
                        siteId: undefined,
                        loadData: true,
                        field: 'group',
                        nextLevelName: 'site',
                        creativeId: undefined,
                        siteSectionId: undefined,
                        placementId: undefined,
                        creativeGroupId: 6345544,
                        siteLabel: undefined,
                        creativeLabel: undefined,
                        siteName: undefined,
                        siteSectionLabel: undefined,
                        siteSectionName: undefined,
                        placementLabel: undefined,
                        creativeGroupLabel: 'My GC (6345544)',
                        siteDetailLabel: 'My GC (6345544)',
                        placementAssociations: 2,
                        creativeAssociations: 1,
                        weight: 55,
                        frequencyCap: 1,
                        frequencyCapWindow: 24,
                        creativeGroupPriority: 0,
                        creativeGroupDoCookieTarget: 0,
                        creativeGroupDoGeoTarget: 0,
                        creativeGroupDoDayPartTarget: 0,
                        flightDates: undefined,
                        flightDateStart: undefined,
                        flightDateEnd: undefined,
                        expanded: true,
                        editSupport: {
                            weight: true
                        },
                        children: [
                            {
                                id: undefined,
                                siteId: 6291689,
                                creativeId: undefined,
                                creativeLabel: undefined,
                                creativeGroupId: 6345544,
                                loadData: true,
                                field: 'site',
                                nextLevelName: 'placement',
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteDetailLabel: '1-Site-Mare (6291689)',
                                creativeGroupLabel: 'My GC (6345544)',
                                expanded: true,
                                children: [
                                    {
                                        id: undefined,
                                        siteId: 6291689,
                                        loadData: false,
                                        field: 'placement',
                                        nextLevelName: 'schedule',
                                        siteSectionId: undefined,
                                        creativeId: undefined,
                                        creativeGroupId: 6345544,
                                        creativeGroupLabel: 'My GC (6345544)',
                                        creativeLabel: undefined,
                                        placementId: 6315747,
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteName: '1-Site-Mare',
                                        siteSectionLabel: undefined,
                                        siteSectionName: undefined,
                                        placementLabel: 'chicken-rechicken (6315747)',
                                        siteDetailLabel: 'chicken-rechicken (6315747)',
                                        placementStatus: 'Accepted',
                                        placementStatusLabel: 'Active',
                                        creativeGroupAssociations: 2,
                                        flightDates: undefined,
                                        flightDateStart: undefined,
                                        flightDateEnd: undefined,
                                        sizeName: undefined,
                                        expanded: false,
                                        children: [
                                            {
                                                siteLabel: '1-Site-Mare (6291689)',
                                                siteSectionLabel: undefined,
                                                placementLabel: 'chicken-rechicken (6315747)',
                                                creativeGroupLabel: 'My GC (6345544)',
                                                creativeLabel: undefined
                                            }
                                        ],
                                        treeRowId: '27ea319380fa714bb1f9b224910f9dd555a904c6',
                                        backUpChildren: [],
                                        placementTagAssociationIds: {
                                            siteId: 6291689,
                                            sectionId: undefined
                                        }
                                    }
                                ],
                                treeRowId: '826200a842893ad9a66bf990b534b6d86b4ff034',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: '7cf8680ba5327e03a931e3d9b648a0f156833597',
                        backUpChildren: []
                    },
                    {
                        siteId: undefined,
                        loadData: true,
                        field: 'group',
                        nextLevelName: 'site',
                        creativeId: undefined,
                        siteSectionId: undefined,
                        placementId: undefined,
                        creativeGroupId: 6158649,
                        siteLabel: undefined,
                        creativeLabel: undefined,
                        siteName: undefined,
                        siteSectionLabel: undefined,
                        siteSectionName: undefined,
                        placementLabel: undefined,
                        creativeGroupLabel: 'ACG1 AA (6158649)',
                        siteDetailLabel: 'ACG1 AA (6158649)',
                        placementAssociations: 3,
                        creativeAssociations: 7,
                        weight: 1,
                        frequencyCap: 1,
                        frequencyCapWindow: 84,
                        creativeGroupPriority: 0,
                        creativeGroupDoCookieTarget: 0,
                        creativeGroupDoGeoTarget: 0,
                        creativeGroupDoDayPartTarget: 0,
                        flightDates: undefined,
                        flightDateStart: undefined,
                        flightDateEnd: undefined,
                        expanded: true,
                        editSupport: {
                            weight: true
                        },
                        children: [
                            {
                                id: undefined,
                                siteId: 6291689,
                                creativeId: undefined,
                                creativeLabel: undefined,
                                creativeGroupId: 6158649,
                                loadData: true,
                                field: 'site',
                                nextLevelName: 'placement',
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteDetailLabel: '1-Site-Mare (6291689)',
                                creativeGroupLabel: 'ACG1 AA (6158649)',
                                expanded: true,
                                children: [
                                    {
                                        id: undefined,
                                        siteId: 6291689,
                                        loadData: false,
                                        field: 'placement',
                                        nextLevelName: 'schedule',
                                        siteSectionId: undefined,
                                        creativeId: undefined,
                                        creativeGroupId: 6158649,
                                        creativeGroupLabel: 'ACG1 AA (6158649)',
                                        creativeLabel: undefined,
                                        placementId: 6315747,
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteName: '1-Site-Mare',
                                        siteSectionLabel: undefined,
                                        siteSectionName: undefined,
                                        placementLabel: 'chicken-rechicken (6315747)',
                                        siteDetailLabel: 'chicken-rechicken (6315747)',
                                        placementStatus: 'Accepted',
                                        placementStatusLabel: 'Active',
                                        creativeGroupAssociations: 2,
                                        flightDates: undefined,
                                        flightDateStart: undefined,
                                        flightDateEnd: undefined,
                                        sizeName: undefined,
                                        expanded: false,
                                        children: [
                                            {
                                                siteLabel: '1-Site-Mare (6291689)',
                                                siteSectionLabel: undefined,
                                                placementLabel: 'chicken-rechicken (6315747)',
                                                creativeGroupLabel: 'ACG1 AA (6158649)',
                                                creativeLabel: undefined
                                            }
                                        ],
                                        treeRowId: '27ea319380fa714bb1f9b224910f9dd555a904c6',
                                        backUpChildren: [],
                                        placementTagAssociationIds: {
                                            siteId: 6291689,
                                            sectionId: undefined
                                        }
                                    }
                                ],
                                treeRowId: '826200a842893ad9a66bf990b534b6d86b4ff034',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: '8dc1630cf214f2d063880debb2b2a5ee00707d82',
                        backUpChildren: []
                    }
                ]);
        });

        it('should set Creative List when service call a search server with Creative Pivot', function () {
            response = [
                {
                    campaignId: 6158572,
                    createdDate: '2016-03-28T15:55:07-07:00',
                    createdTpwsKey: '3f066c60-2087-4611-a5ca-a9eff1807dfd',
                    creativeAlias: 'Pro',
                    creativeAssociationsWithCreativeGroups: 1,
                    creativeAssociationsWithPlacements: 2,
                    creativeGroupAssociationsWithCreatives: 4,
                    creativeGroupAssociationsWithPlacements: 2,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 6345544,
                    creativeGroupName: 'My GC',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 55,
                    creativeGroupWeightEnabled: 0,
                    creativeId: 6405348,
                    creativeInsertionRootId: 6467249,
                    creativeType: 'jpg',
                    endDate: '2015-12-13T23:59:59-07:00',
                    filename: 'Pro.jpg',
                    id: 10462218,
                    logicalDelete: 'N',
                    modifiedDate: '2016-03-28T15:55:07-07:00',
                    modifiedTpwsKey: '3f066c60-2087-4611-a5ca-a9eff1807dfd',
                    placementAssociationsWithCreativeGroups: 2,
                    placementEndDate: '2015-12-13T23:59:59-07:00',
                    placementId: 6315747,
                    placementName: 'chicken-rechicken',
                    placementStartDate: '2015-11-13T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    primaryClickthrough: 'http://www.trueffect.com',
                    released: 0,
                    sequence: 0,
                    siteId: 6291689,
                    siteName: '1-Site-Mare',
                    sizeName: '300x250',
                    startDate: '2015-11-13T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                },
                {
                    campaignId: 6158572,
                    createdDate: '2016-03-28T15:59:46-07:00',
                    createdTpwsKey: 'def18f0e-58ae-4a45-8475-2821015d26f2',
                    creativeAlias: 'Pro',
                    creativeAssociationsWithCreativeGroups: 1,
                    creativeAssociationsWithPlacements: 2,
                    creativeGroupAssociationsWithCreatives: 5,
                    creativeGroupAssociationsWithPlacements: 2,
                    creativeGroupDoCookieTargeting: 0,
                    creativeGroupDoDaypartTarget: 0,
                    creativeGroupDoGeoTargeting: 0,
                    creativeGroupFrequencyCap: 1,
                    creativeGroupFrequencyCapWindow: 24,
                    creativeGroupId: 6345544,
                    creativeGroupName: 'My GC',
                    creativeGroupPriority: 0,
                    creativeGroupRotationType: 'Weighted',
                    creativeGroupWeight: 55,
                    creativeGroupWeightEnabled: 0,
                    creativeId: 6405348,
                    creativeInsertionRootId: 6476000,
                    creativeType: 'jpg',
                    endDate: '2015-10-18T00:00:00-07:00',
                    filename: 'Pro.jpg',
                    id: 10462238,
                    logicalDelete: 'N',
                    modifiedDate: '2016-03-28T15:59:46-07:00',
                    modifiedTpwsKey: 'def18f0e-58ae-4a45-8475-2821015d26f2',
                    placementAssociationsWithCreativeGroups: 2,
                    placementEndDate: '2015-10-18T00:00:00-07:00',
                    placementId: 6170061,
                    placementName: 'MP300x250',
                    placementStartDate: '2015-09-18T00:00:00-07:00',
                    placementStatus: 'Accepted',
                    primaryClickthrough: 'http://www.trueffect.com',
                    released: 0,
                    sequence: 0,
                    siteId: 6086072,
                    siteName: 'SITE - Mare tests',
                    sizeName: '300x250',
                    startDate: '2015-09-18T00:00:00-07:00',
                    timeZone: 'MST',
                    weight: 100
                },
                {
                    campaignId: 6158572,
                    creativeAlias: 'Pro',
                    creativeAssociationsWithCreativeGroups: 1,
                    creativeAssociationsWithPlacements: 2,
                    creativeId: 6405348,
                    creativeType: 'jpg',
                    filename: 'Pro.jpg',
                    primaryClickthrough: 'http://www.trueffect.com',
                    sizeName: '300x250'
                }
            ];

            ScheduleTabController.selectedPivot = ScheduleTabController.SCHEDULE_LEVEL.CREATIVE;
            ScheduleTabController.serverSideSearch('default');
            creativeInsertionList.resolve(response);
            $scope.$apply();

            expect(ScheduleTabController.model).toEqual(
                [
                    {
                        id: 10462218,
                        field: 'creative',
                        loadData: true,
                        nextLevelName: 'site',
                        siteId: undefined,
                        siteSectionId: undefined,
                        placementId: undefined,
                        creativeGroupId: undefined,
                        creativeId: 6405348,
                        siteLabel: undefined,
                        siteName: undefined,
                        siteSectionLabel: undefined,
                        siteSectionName: undefined,
                        placementLabel: undefined,
                        creativeGroupLabel: undefined,
                        creativeLabel: 'Pro (6405348)',
                        creativeName: 'Pro',
                        creativeType: 'jpg',
                        siteDetailLabel: 'Pro (6405348)',
                        sizeName: '300x250',
                        filename: 'Pro.jpg',
                        placementAssociations: 2,
                        creativeGroupAssociations: 1,
                        weight: undefined,
                        expanded: true,
                        flightDates: undefined,
                        flightDateStart: undefined,
                        flightDateEnd: undefined,
                        clickThroughUrl: undefined,
                        editSupport: {
                            clickThroughUrl: true,
                            flightDateStart: true,
                            flightDateEnd: true,
                            weight: true
                        },
                        children: [
                            {
                                id: 10462218,
                                siteId: 6291689,
                                creativeId: 6405348,
                                creativeLabel: 'Pro',
                                creativeGroupId: undefined,
                                loadData: true,
                                field: 'site',
                                nextLevelName: 'placement',
                                siteLabel: '1-Site-Mare (6291689)',
                                siteName: '1-Site-Mare',
                                siteDetailLabel: '1-Site-Mare (6291689)',
                                creativeGroupLabel: undefined,
                                expanded: true,
                                children: [
                                    {
                                        id: 10462218,
                                        siteId: 6291689,
                                        loadData: true,
                                        field: 'placement',
                                        nextLevelName: 'group',
                                        siteSectionId: undefined,
                                        creativeId: 6405348,
                                        creativeGroupId: undefined,
                                        creativeGroupLabel: undefined,
                                        creativeLabel: 'Pro',
                                        placementId: 6315747,
                                        siteLabel: '1-Site-Mare (6291689)',
                                        siteName: '1-Site-Mare',
                                        siteSectionLabel: undefined,
                                        siteSectionName: undefined,
                                        placementLabel: 'chicken-rechicken (6315747)',
                                        siteDetailLabel: 'chicken-rechicken (6315747)',
                                        placementStatus: 'Accepted',
                                        placementStatusLabel: 'Active',
                                        creativeGroupAssociations: 2,
                                        flightDates: undefined,
                                        flightDateStart: undefined,
                                        flightDateEnd: undefined,
                                        sizeName: '300x250',
                                        expanded: true,
                                        children: [
                                            {
                                                siteId: 6291689,
                                                loadData: false,
                                                field: 'group',
                                                nextLevelName: 'schedule',
                                                creativeId: 6405348,
                                                siteSectionId: undefined,
                                                placementId: 6315747,
                                                creativeGroupId: 6345544,
                                                siteLabel: '1-Site-Mare (6291689)',
                                                creativeLabel: 'Pro',
                                                siteName: '1-Site-Mare',
                                                siteSectionLabel: undefined,
                                                siteSectionName: undefined,
                                                placementLabel: 'chicken-rechicken (6315747)',
                                                creativeGroupLabel: 'My GC (6345544)',
                                                siteDetailLabel: 'My GC (6345544)',
                                                placementAssociations: 2,
                                                creativeAssociations: 4,
                                                weight: 55,
                                                frequencyCap: 1,
                                                frequencyCapWindow: 24,
                                                creativeGroupPriority: 0,
                                                creativeGroupDoCookieTarget: 0,
                                                creativeGroupDoGeoTarget: 0,
                                                creativeGroupDoDayPartTarget: 0,
                                                flightDates: undefined,
                                                flightDateStart: undefined,
                                                flightDateEnd: undefined,
                                                expanded: false,
                                                editSupport: {
                                                    weight: true
                                                },
                                                children: [
                                                    {
                                                        siteLabel: '1-Site-Mare (6291689)',
                                                        siteSectionLabel: undefined,
                                                        placementLabel: 'chicken-rechicken (6315747)',
                                                        creativeGroupLabel: 'My GC (6345544)',
                                                        creativeLabel: 'Pro'
                                                    }
                                                ],
                                                treeRowId: 'c81e5f86fb459230e51d47986838a3abe032332d',
                                                backUpChildren: []
                                            }
                                        ],
                                        treeRowId: '27ea319380fa714bb1f9b224910f9dd555a904c6',
                                        backUpChildren: [],
                                        placementTagAssociationIds: {
                                            siteId: 6291689,
                                            sectionId: undefined
                                        }
                                    }
                                ],
                                treeRowId: '826200a842893ad9a66bf990b534b6d86b4ff034',
                                backUpChildren: []
                            },
                            {
                                id: 10462218,
                                siteId: 6086072,
                                creativeId: 6405348,
                                creativeLabel: 'Pro',
                                creativeGroupId: undefined,
                                loadData: true,
                                field: 'site',
                                nextLevelName: 'placement',
                                siteLabel: 'SITE - Mare tests (6086072)',
                                siteName: 'SITE - Mare tests',
                                siteDetailLabel: 'SITE - Mare tests (6086072)',
                                creativeGroupLabel: undefined,
                                expanded: true,
                                children: [
                                    {
                                        id: 10462218,
                                        siteId: 6086072,
                                        loadData: true,
                                        field: 'placement',
                                        nextLevelName: 'group',
                                        siteSectionId: undefined,
                                        creativeId: 6405348,
                                        creativeGroupId: undefined,
                                        creativeGroupLabel: undefined,
                                        creativeLabel: 'Pro',
                                        placementId: 6170061,
                                        siteLabel: 'SITE - Mare tests (6086072)',
                                        siteName: 'SITE - Mare tests',
                                        siteSectionLabel: undefined,
                                        siteSectionName: undefined,
                                        placementLabel: 'MP300x250 (6170061)',
                                        siteDetailLabel: 'MP300x250 (6170061)',
                                        placementStatus: 'Accepted',
                                        placementStatusLabel: 'Active',
                                        creativeGroupAssociations: 2,
                                        flightDates: undefined,
                                        flightDateStart: undefined,
                                        flightDateEnd: undefined,
                                        sizeName: '300x250',
                                        expanded: true,
                                        children: [
                                            {
                                                siteId: 6086072,
                                                loadData: false,
                                                field: 'group',
                                                nextLevelName: 'schedule',
                                                creativeId: 6405348,
                                                siteSectionId: undefined,
                                                placementId: 6170061,
                                                creativeGroupId: 6345544,
                                                siteLabel: 'SITE - Mare tests (6086072)',
                                                creativeLabel: 'Pro',
                                                siteName: 'SITE - Mare tests',
                                                siteSectionLabel: undefined,
                                                siteSectionName: undefined,
                                                placementLabel: 'MP300x250 (6170061)',
                                                creativeGroupLabel: 'My GC (6345544)',
                                                siteDetailLabel: 'My GC (6345544)',
                                                placementAssociations: 2,
                                                creativeAssociations: 5,
                                                weight: 55,
                                                frequencyCap: 1,
                                                frequencyCapWindow: 24,
                                                creativeGroupPriority: 0,
                                                creativeGroupDoCookieTarget: 0,
                                                creativeGroupDoGeoTarget: 0,
                                                creativeGroupDoDayPartTarget: 0,
                                                flightDates: undefined,
                                                flightDateStart: undefined,
                                                flightDateEnd: undefined,
                                                expanded: false,
                                                editSupport: {
                                                    weight: true
                                                },
                                                children: [
                                                    {
                                                        siteLabel: 'SITE - Mare tests (6086072)',
                                                        siteSectionLabel: undefined,
                                                        placementLabel: 'MP300x250 (6170061)',
                                                        creativeGroupLabel: 'My GC (6345544)',
                                                        creativeLabel: 'Pro'
                                                    }
                                                ],
                                                treeRowId: 'df97e1bb01a033c64525f6b9418bc7f64672e4bd',
                                                backUpChildren: []
                                            }
                                        ],
                                        treeRowId: '7b3c5552b78dbabc17fef06270e29ac917017593',
                                        backUpChildren: [],
                                        placementTagAssociationIds: {
                                            siteId: 6086072,
                                            sectionId: undefined
                                        }
                                    }
                                ],
                                treeRowId: 'aead5c7574311ea3621063fae272a4bbc4fad489',
                                backUpChildren: []
                            }
                        ],
                        treeRowId: undefined,
                        backUpChildren: []
                    }
                ]);
        });
    });
});
