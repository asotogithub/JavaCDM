'use strict';

describe('Controller: EventDetailsFlyOutController', function () {
    var $httpBackend,
        $scope,
        $translate,
        CONSTANTS,
        EXPECTED_RESPONSE,
        TYPE_LIST,
        EventDetailsFlyOutController,
        EventsService,
        SiteService,
        DialogFactory,
        dialogDeferred,
        eventDetails,
        model,
        response,
        responseSiteList,
        siteList;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
        _$httpBackend_,
        $controller,
        $q,
        _$rootScope_,
        _$translate_,
        $state,
        _CONSTANTS_,
        _DialogFactory_,
        _EventsService_,
        _SiteService_,
        _Utils_,
        _UtilsCDMService_,
        lodash
        ) {
        eventDetails = $q.defer();
        CONSTANTS = _CONSTANTS_;
        $translate = _$translate_;
        DialogFactory = _DialogFactory_;
        EventsService = _EventsService_;
        spyOn(EventsService, 'getPings').andReturn(eventDetails.promise);

        siteList = $q.defer();
        SiteService = _SiteService_;
        spyOn(SiteService, 'getList').andReturn(siteList.promise);

        spyOn($state, 'go');

        $scope = _$rootScope_.$new();
        model = {
            createdDate: '2015-06-11T10:30:04-07:00',
            createdTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            eventName: 'Event01',
            eventType: 1,
            eventTypeName: 'Measured',
            groupName: 'A_Name_Group',
            id: 15261,
            isTrafficked: 1,
            location: 'ed laoreet cstibulum posuere purus quam, at tempus risus tempus at.',
            logicalDelete: 'N',
            modifiedDate: '2016-08-03T10:23:36-07:00',
            modifiedTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            smEventType: 3,
            smGroupId: 11664,
            tagTypeName: 'Standard'
        };

        $scope.vmTeFlyOutController =
        {
            flyOutModel: {
                data: model
            }
        };

        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        installPromiseMatchers();

        response = {
            createdDate: '2015-06-11T10:30:04-07:00',
            createdTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            eventName: 'Event01',
            eventType: 1,
            id: 15261,
            isTrafficked: 1,
            location: 'ed laoreet convallis diam. Donec sagittis dolor placerat, finibus leo nec, suscipit lacus.',
            logicalDelete: 'N',
            measurementState: 3,
            modifiedDate: '2016-08-03T10:23:36-07:00',
            modifiedTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            pingEvents: [
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
                    siteId: 9428174,
                    siteName: 'site Name ',
                    smEventType: 2,
                    smGroupId: 11665
                }

            ],
            smEventType: 3,
            smGroupId: 11664
        };

        EXPECTED_RESPONSE = {
            createdDate: '2015-06-11T10:30:04-07:00',
            createdTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            eventName: 'Event01',
            eventType: 1,
            id: 15261,
            isTrafficked: 1,
            location: 'ed laoreet convallis diam. Donec sagittis dolor placerat, finibus leo nec, suscipit lacus.',
            logicalDelete: 'N',
            measurementState: 3,
            modifiedDate: '2016-08-03T10:23:36-07:00',
            modifiedTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
            pingEvents: [
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
                    smGroupId: 11665,
                    pingTypeField: 'Broadcast',
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
                    pingTagTypeField: 'Img',
                    pingPatternList: [
                        new RegExp(CONSTANTS.REGEX.URL, 'i'),
                        new RegExp(CONSTANTS.REGEX.URL, 'i'),
                        new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'i')
                    ],
                    pingCardTypes: CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE,
                    maxLength: CONSTANTS.SITE_MEASUREMENT.PING_CARD.MAX_LENGTH
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
                    siteId: 9428174,
                    siteName: 'site Name ',
                    smEventType: 2,
                    smGroupId: 11665,
                    pingTypeField: 'Selective',
                    pingTagTypeList: [
                        {
                            key: 0,
                            name: 'Img'
                        }
                    ],
                    pingTagTypeField: 'Img',
                    pingPatternList: [
                        new RegExp(CONSTANTS.REGEX.URL, 'i'),
                        new RegExp(CONSTANTS.REGEX.URL, 'i'),
                        new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'i')
                    ],
                    pingCardTypes: CONSTANTS.SITE_MEASUREMENT.PING_CARD.TYPE,
                    maxLength: CONSTANTS.SITE_MEASUREMENT.PING_CARD.MAX_LENGTH
                }
            ],
            smEventType: 3,
            smGroupId: 11664,
            eventTypeSelected: {
                id: 3,
                name: 'Measured'
            },
            groupName: 'A_Name_Group',
            isReadOnly: false,
            loadPings: true,
            tagTypeSelected: {
                id: 1,
                name: 'TruTag'
            }
        };

        responseSiteList = [
            {
                acceptsFlash: 'N',
                clickTrack: 'N',
                createdDate: '2016-05-27T15:02:52-07:00',
                createdTpwsKey: '79806c58-831b-41b7-960f-0fffda72e036',
                dupName: 'site 61',
                encode: 'N',
                id: 10527557,
                logicalDelete: 'N',
                modifiedDate: '2016-05-27T15:02:52-07:00',
                modifiedTpwsKey: '79806c58-831b-41b7-960f-0fffda72e036',
                name: 'Site 61',
                preferredTag: 'IFRAME',
                publisherId: 10527555,
                publisherName: 'Publisher 61',
                richMedia: 'N'
            },
            {
                acceptsFlash: 'N',
                clickTrack: 'N',
                createdDate: '2016-05-27T15:02:52-07:00',
                createdTpwsKey: '79806c58-831b-41b7-960f-0fffda72e036',
                dupName: 'site 61',
                encode: 'N',
                id: 10527564,
                logicalDelete: 'N',
                modifiedDate: '2016-05-27T15:02:52-07:00',
                modifiedTpwsKey: '79806c58-831b-41b7-960f-0fffda72e036',
                name: 'Site 62',
                preferredTag: 'IFRAME',
                publisherId: 10527562,
                publisherName: 'Publisher 62',
                richMedia: 'N'
            }
        ];

        TYPE_LIST = [
            {
                id: 0,
                name: 'Other'
            },
            {
                id: 1,
                name: 'Conversion'
            },
            {
                id: 2,
                name: 'Conversion w/Revenue'
            },
            {
                id: 3,
                name: 'Measured'
            }
        ];

        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog', 'showCustomDialog']);
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });

        EventDetailsFlyOutController = $controller('EventDetailsFlyOutController', {
            $scope: $scope,
            $translate: _$translate_,
            CONSTANTS: CONSTANTS,
            DialogFactory: DialogFactory,
            EventService: _EventsService_,
            SiteService: _SiteService_,
            Utils: _Utils_,
            UtilsCDMService: _UtilsCDMService_,
            lodash: lodash
        });
    }));

    describe('activate()', function () {
        it('should load details value in model', function () {
            eventDetails.resolve(response);
            siteList.resolve(responseSiteList);
            $scope.$apply();

            expect(EventDetailsFlyOutController.model).toEqual(EXPECTED_RESPONSE);
            expect(EventDetailsFlyOutController.siteList).toEqual(responseSiteList);
            expect(EventDetailsFlyOutController.eventTypeList).toEqual(TYPE_LIST);
        });

        it('should update value in model', function () {
            EventDetailsFlyOutController.model = EXPECTED_RESPONSE;
            EventDetailsFlyOutController.model.eventTypeSelected = EventDetailsFlyOutController.eventTypeList[1];
            EventDetailsFlyOutController.flyOutForm = {};
            EventDetailsFlyOutController.updateEventModel(EventDetailsFlyOutController.model);
            expect(EventDetailsFlyOutController.model.smEventType).toEqual(
                EventDetailsFlyOutController.eventTypeList[1].id);
        });

        it('should add a new ping in the first position', function () {
            EventDetailsFlyOutController.model = EXPECTED_RESPONSE;
            expect(EventDetailsFlyOutController.model.pingEvents.length).toEqual(2);
            EventDetailsFlyOutController.addPingCard(1);
            expect(EventDetailsFlyOutController.model.pingEvents.length).toEqual(3);
            expect(EventDetailsFlyOutController.model.pingEvents[0].editMode).toEqual(true);
        });

        it('should show Dialog when delete a ping', function () {
            EventDetailsFlyOutController.model = EXPECTED_RESPONSE;
            EventDetailsFlyOutController.onDeletePing(15375);
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.eventsPings.deleteEventPing'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
            });
        });
    });
});
