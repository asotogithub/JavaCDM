'use strict';

describe('Controller: TeTablePingController', function () {
    var $q,
        $scope,
        DATA,
        TeTablePingController,
        eventFake;

    beforeEach(module('uiApp'));
    beforeEach(module('te.components'));

    beforeEach(inject(function ($controller,
                                $filter,
                                $rootScope,
                                _$q_,
                                CONSTANTS,
                                Utils,
                                _lodash_) {
        $q = _$q_;
        DATA = {
            model: [
                {
                    eventName: 'Event1',
                    eventType: 1,
                    groupName: '01_PZ_Group',
                    id: 15375,
                    pingContent: '<!-- iframe/script/href/image tag -->' +
                        '<IFRAME SRC="http://ad.adlegend.com/iframe?spacedesc=9224667_9184913_1x1_9184922_9224667' +
                        '&target=_blank&random=&@CPSC@=" WIDTH=1 HEIGHT=1 SCROLLING="No"',
                    pingId: 18095,
                    pingTagType: 2,
                    pingTagTypeField: 'Tag',
                    pingTagTypeList: [
                        {
                            key: 0,
                            name: 'Img'
                        },
                        {
                            key: 1,
                            name: 'Iframe'
                        },
                        {
                            key: 2,
                            name: 'Tag'
                        }
                    ],
                    pingType: 1,
                    pingTypeField: 'Broadcast',
                    description: 'Description)1',
                    siteId: 10598437,
                    siteName: '1-Site-tags',
                    smEventType: 0,
                    smGroupId: 11665
                },
                {
                    eventName: 'Event1',
                    eventType: 1,
                    groupName: '01_PZ_Group',
                    id: 15375,
                    pingContent: 'http://ad.adlegend.com',
                    pingId: 18058,
                    pingTagType: 0,
                    pingTagTypeField: 'Img',
                    pingTagTypeList: [
                        {
                            key: 0,
                            name: 'Img'
                        }
                    ],
                    pingType: 2,
                    pingTypeField: 'Selective',
                    description: 'description 02',
                    siteId: 9428174,
                    siteName: 'Site Name',
                    smEventType: 0,
                    smGroupId: 11665
                },
                {
                    eventName: 'Event1',
                    eventType: 1,
                    groupName: '01_PZ_Group',
                    id: 15375,
                    pingContent: 'http://usa.com',
                    pingId: 18061,
                    pingTagType: 0,
                    pingTagTypeField: 'Img',
                    pingTagTypeList: [
                        {
                            key: 0,
                            name: 'Img'
                        },
                        {
                            key: 1,
                            name: 'Iframe'
                        },
                        {
                            key: 2,
                            name: 'Tag'
                        }
                    ],
                    pingType: 1,
                    pingTypeField: 'Broadcast',
                    description: 'description 03',
                    siteId: 9428174,
                    siteName: 'Site-Name-2',
                    smEventType: 0,
                    smGroupId: 11665
                }
            ],
            filterValues: [
                {
                    fieldName: 'publisherName',
                    values: []
                },
                {
                    fieldName: 'pingType',
                    values: []
                }
            ],
            filterOption: {
                PUBLISHER: {
                    text: 'Publisher',
                    value: []
                },
                PING_TYPE: {
                    text: 'Ping Type',
                    value: []
                }
            },
            searchFields: [
                {
                    enabled: true,
                    field: 'pingTagTypeField',
                    position: 1,
                    title: 'Tag Type'
                },
                {
                    enabled: true,
                    field: 'siteName',
                    position: 2,
                    title: 'Site'
                },
                {
                    enabled: true,
                    field: 'pingContent',
                    position: 3,
                    title: 'Ping'
                }
            ],
            teTableCustomSearchEnabled: true,
            onDeletePing: function () {
                return true;
            },

            onSavePing: function () {
                return true;
            }
        };

        spyOn(DATA, 'onDeletePing');
        spyOn(DATA, 'onSavePing');

        eventFake = {
            preventDefault: function () {
                return true;
            },

            stopImmediatePropagation: function () {
                return true;
            }
        };
        $scope = $rootScope.$new();

        TeTablePingController = $controller('TeTablePingController', {
            $filter: $filter,
            $scope: $scope,
            CONSTANTS: CONSTANTS,
            Utils: Utils,
            lodash: _lodash_
        }, DATA);
    }));

    describe('load properties of table', function () {
        it('search fields ', function () {
            TeTablePingController.activate();
            expect(TeTablePingController.isSearchCollapsed).toBe(false);
            expect(TeTablePingController.searchFields).toEqual(DATA.searchFields);
            expect(TeTablePingController.hasPings).toBe(true);
        });

        it('should change status filter search', function () {
            TeTablePingController.toggleSearchField(eventFake, DATA.searchFields[0]);
            expect(TeTablePingController.searchFields[0].enabled).toBeFalsy();

            TeTablePingController.toggleSearchField(eventFake, DATA.searchFields[1]);
            expect(TeTablePingController.searchFields[1].enabled).toBeFalsy();

            TeTablePingController.toggleSearchField(eventFake, DATA.searchFields[2]);
            expect(TeTablePingController.searchFields[2].enabled).toBeTruthy();
        });

        it('should disable search when edit mode is enabled', function () {
            TeTablePingController.editModeEnabled = true;
            expect(TeTablePingController.isSearchDisabled()).toBeTruthy();
        });

        it('should clear search term', function () {
            TeTablePingController.searchTerm = 'Name';
            expect(TeTablePingController.searchTerm).not.toBeNull();
            TeTablePingController.clearSearch();
            expect(TeTablePingController.searchTerm).toBeNull();
        });

        it('should execute delete callback', function () {
            TeTablePingController.deletePing(DATA.model[0].pingId);
            expect(TeTablePingController.onDeletePing).toHaveBeenCalled();
        });

        it('should execute save callback', function () {
            TeTablePingController.savePing(DATA.model[0].pingId);
            expect(TeTablePingController.onSavePing).toHaveBeenCalled();
        });

        it('should get values Ping', function () {
            TeTablePingController.activate();
            expect(TeTablePingController.getData().length).toEqual(DATA.model.length);
        });
    });
});
