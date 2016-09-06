'use strict';

describe('Controller: PingBoxController', function () {
    var $q,
        $scope,
        $timeout,
        DATA,
        TAG_TYPE_LIST,
        PingBoxController,
        otherTag,
        pingTitle,
        selectedTag,
        sites;

    TAG_TYPE_LIST = [
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
    ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$timeout_,
                                _$q_,
                                CONSTANTS,
                                Utils,
                                _lodash_) {
        $timeout = _$timeout_;
        $scope = $rootScope.$new();
        $q = _$q_;
        selectedTag = {
            key: 0
        };
        DATA = {
            model: {
                eventName: 'Event02T',
                eventType: 1,
                groupName: 'Group02',
                id: 15388,
                location: 'description ',
                pingContent: 'http://ad.adlegend.com',
                pingId: 18068,
                pingTagType: 0,
                pingTagTypeField: 'Img',
                pingTagTypeList: TAG_TYPE_LIST,
                pingType: 1,
                pingTypeField: 'Broadcast',
                siteId: 9428174,
                siteName: 'Site Name',
                smEventType: 0,
                smGroupId: 11732,
                description: 'description Name',
                editMode: false,
                pingPatternList: [
                    new RegExp(CONSTANTS.REGEX.URL, 'i'),
                    new RegExp(CONSTANTS.REGEX.URL, 'i'),
                    new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'i')
                ]
            },
            selectedSite: {
                id: 1254
            },
            pingBoxForm: {
                siteSelectionName: {
                },
                tagTypeSelectionName: {
                },
                pingContent: {
                },
                $valid: true
            },
            onDeletePing: function () {
                return true;
            },

            onSavePing: function () {
                return true;
            }
        };

        spyOn(DATA, 'onDeletePing');
        spyOn(DATA, 'onSavePing').andReturn($q.when({}));
        otherTag = {
            key: 1,
            name: 'Tag'
        };
        pingTitle = 'Broadcast';
        sites = [
        {
            eventName: 'Event01',
            eventType: 1,
            groupName: '01_PZ_Group',
            id: 15375,
            pingContent: 'http://ad.adlegend.com',
            pingId: 18057,
            pingTagType: 0,
            pingType: 1,
            siteId: 9428174,
            siteName: 'site name',
            smEventType: 2,
            smGroupId: 11665
        },
        {
            eventName: 'Event01',
            eventType: 1,
            groupName: '01_PZ_Group',
            id: 15375,
            pingContent: 'http://ad.adlegend.com',
            pingId: 18058,
            pingTagType: 0,
            pingType: 2,
            siteId: 1111111,
            siteName: 'site Name ',
            smEventType: 2,
            smGroupId: 11665
        }
    ];

        PingBoxController = $controller('PingBoxController', {
            $scope: $scope,
            $timeout: $timeout,
            CONSTANTS: CONSTANTS,
            Utils: Utils,
            lodash: _lodash_
        }, DATA);
    }));

    describe('Initialize controller', function () {
        it('should set properties', function () {
            expect(PingBoxController.pingTitle).toEqual(pingTitle);
            expect(PingBoxController.tagTypeList).toEqual(TAG_TYPE_LIST);
            expect(PingBoxController.selectedTagType).toEqual(selectedTag);
        });

        it('should execute onDeletePing callback', function () {
            PingBoxController.deletePing();
            expect(PingBoxController.onDeletePing).toHaveBeenCalled();
        });

        it('should edit ping', function () {
            PingBoxController.editPing();
            expect(PingBoxController.disableEditMode).toBeFalsy();
            expect(PingBoxController.editModeEnabled).toBeTruthy();
        });

        it('should enter container', function () {
            expect(PingBoxController.hasFocus).toBeFalsy();
            PingBoxController.enterContainer();
            expect(PingBoxController.hasFocus).toBeTruthy();
        });

        it('should exit container', function () {
            PingBoxController.enterContainer();
            expect(PingBoxController.hasFocus).toBeTruthy();
            PingBoxController.exitContainer();
            expect(PingBoxController.hasFocus).toBeFalsy();
        });

        it('should execute save ping', function () {
            PingBoxController.selectedTagType = otherTag;
            PingBoxController.savePing();
            expect(PingBoxController.onSavePing).toHaveBeenCalled();
        });

        it('should change site', function () {
            expect(PingBoxController.selectedSite.id).toBe(sites[0].siteId);
            PingBoxController.selectedSite.id = sites[1].siteId;
            PingBoxController.onSiteChange();
            expect(PingBoxController.model.siteId).toBe(sites[1].siteId);
        });

        it('should change Tag Type', function () {
            expect(PingBoxController.selectedTagType.key).toBe(DATA.model.pingTagType);
            PingBoxController.selectedTagType = otherTag;
            PingBoxController.onTagTypeChange();
            expect(PingBoxController.model.pingTagType).toBe(PingBoxController.selectedTagType.key);
            expect(PingBoxController.model.pingTagTypeField).toBe(PingBoxController.selectedTagType.name);
        });

        it('should disable Site Dropdown if edit a Ping', function () {
            PingBoxController.editPing();
            expect(PingBoxController.shouldDisableSiteDropdown()).toBeTruthy();
        });
    });
});
