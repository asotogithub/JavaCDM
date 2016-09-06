'use strict';

describe('Service: CreativeService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        CreativeService,
        DialogFactory,
        ErrorRequestHandler;

    beforeEach(module('uiApp', function ($provide) {
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog', 'showCustomDialog']);
        $provide.value('DialogFactory', DialogFactory);
    }));

    beforeEach(inject(function (_$httpBackend_, _$q_, _API_SERVICE_, _CreativeService_, _ErrorRequestHandler_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        spyOn(ErrorRequestHandler, 'handle').andCallThrough();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        CreativeService = _CreativeService_;

        installPromiseMatchers();
    }));

    describe('deleteCreative()', function () {
        describe('on success', function () {
            it('should delete one creative with a given id', function () {
                var promise,
                    creativeId = 5959481,
                    response = {
                        message: 'Creative 5959481 successfully deleted'
                    };

                $httpBackend.expectPUT(
                    API_SERVICE + 'Creatives/' + creativeId +
                    '/remove').respond(response);
                promise = CreativeService.deleteCreative(creativeId, [123458]);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(response);
            });
        });

        describe('on failure', function () {
            it('should fail attempting to delete one creative with a given id', inject(function () {
                var promise,
                    creativeId = 5959481;

                $httpBackend.expectPUT(
                    API_SERVICE + 'Creatives/' + creativeId +
                    '/remove').respond(400, 'FAILED');
                promise = CreativeService.deleteCreative(creativeId, [123458]);
                $httpBackend.flush();

                expect(promise).toBeRejected();
            }));
        });
    });

    describe('hasSchedule()', function () {
        describe('on success', function () {
            it('should get hasSchedules response', function () {
                var promise,
                    creativeId = 5959481,
                    response = {
                        result: false
                    };

                $httpBackend.expectGET(API_SERVICE + 'Creatives/' + creativeId + '/hasSchedules').respond(response);
                promise = CreativeService.hasSchedules(creativeId);
                $httpBackend.flush();

                expect(promise).toBeResolvedWith(response);
            });
        });

        describe('on failure', function () {
            it('should fail attempting to get hasSchedules', inject(function () {
                var promise,
                    creativeId = 5959481,
                    response = 404;

                $httpBackend.expectGET(API_SERVICE + 'Creatives/' + creativeId + '/hasSchedules').respond(response);
                promise = CreativeService.hasSchedules(creativeId);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));
        });
    });

    describe('getCreative()', function () {
        it('should get a creative based on the given id', function () {
            var creativeId = 6017139,
                promise,
                creativeResponse = {
                    creative: {
                        agencyId: 65854,
                        alias: 'Creative Details 65854',
                        campaignId: 6017139,
                        clickthrough: 'Creative Details 65854',
                        createdDate: '2015-07-15T09:46:59.594-04:00',
                        createdTpwsKey: 'Creative Details 65854',
                        extProp1: 'Creative Details 65854',
                        extProp2: 'Creative Details 65854',
                        extProp3: 'Creative Details 65854',
                        extProp4: 'Creative Details 65854',
                        extProp5: 'Creative Details 65854',
                        externalId: 'Creative Details 65854',
                        fileSize: 6017139,
                        filename: 'Creative Details 65854',
                        groupsCount: 65854,
                        height: 6017139,
                        id: 6017139,
                        isExpandable: 1,
                        logicalDelete: 'N',
                        modifiedDate: '2015-07-15T09:46:59.594-04:00',
                        modifiedTpwsKey: 'Creative Details 65854',
                        ownerCampaignId: 65854,
                        purpose: 'Creative Details 65854',
                        released: 1,
                        richMediaId: 6017139,
                        scheduled: 1,
                        setCookieString: 'Creative Details 65854',
                        swfClickCount: 65854,
                        width: 6017139
                    },
                    creativeGroupCreative: {
                        createdDate: '2015-07-15T09:46:59.594-04:00',
                        createdTpwsKey: 'Creative Details 65854',
                        creative: {
                            createdDate: '2015-07-15T09:46:59.594-04:00',
                            createdTpwsKey: 'Creative Details 65854',
                            creative: {
                                agencyId: 65854,
                                alias: 'Creative Details 65854',
                                campaignId: 6017139,
                                clickthrough: 'Creative Details 65854',
                                createdDate: '2015-07-15T09:46:59.594-04:00',
                                createdTpwsKey: 'Creative Details 65854',
                                extProp1: 'Creative Details 65854',
                                extProp2: 'Creative Details 65854',
                                extProp3: 'Creative Details 65854',
                                extProp4: 'Creative Details 65854',
                                extProp5: 'Creative Details 65854',
                                externalId: 'Creative Details 65854',
                                fileSize: 6017139,
                                filename: 'Creative Details 65854',
                                groupsCount: 65854,
                                height: 6017139,
                                id: 6017139,
                                isExpandable: 1,
                                logicalDelete: 'N',
                                modifiedDate: '2015-07-15T09:46:59.594-04:00',
                                modifiedTpwsKey: 'Creative Details 65854',
                                ownerCampaignId: 65854,
                                purpose: 'Creative Details 65854',
                                released: 1,
                                richMediaId: 6017139,
                                scheduled: 1,
                                setCookieString: 'Creative Details 65854',
                                swfClickCount: 65854,
                                width: 6017139
                            }
                        },
                        creativeAlias: 'Creative Details 65854',
                        creativeGroupId: 6017139,
                        creativeGroupName: 'Creative Details 65854',
                        creativeId: 6017139,
                        displayOrder: 6017139,
                        displayQuantity: 65854,
                        logicalDelete: 'N',
                        modifiedDate: '2015-07-15T09:46:59.594-04:00',
                        modifiedTpwsKey: 'Creative Details 65854'
                    }
                };

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId).respond(creativeResponse);
            promise = CreativeService.getCreative(creativeId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(creativeResponse);
        });

        it('should get an error due a request of a creative with a wrong id', inject(function () {
            var creativeId = 0,
                creativeResponse = 404;

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId).respond(creativeResponse);
            CreativeService.getCreative(creativeId);
            $httpBackend.flush();

            expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
        }));
    });

    describe('getCreativeFile()', function () {
        it('should get a creative file based on the given id', function () {
            var creativeId = 6031428,
                promise,
                creativeResponse = 200;

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId + '/preview').respond(creativeResponse);
            promise = CreativeService.getCreativeFile(creativeId);
            $httpBackend.flush();
            expect(promise).toBeResolved();
        });

        it('should get an error due a request of a creative that has no file related', inject(function () {
            var creativeId = 6031428,
                creativeResponse = 500,
                promise,
                rejected = {
                    $$state: {
                        status: 2,
                        value: 'Failed to get image'
                    }
                };

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId + '/preview').respond(creativeResponse);
            promise = CreativeService.getCreativeFile(creativeId);
            $httpBackend.flush();
            expect(promise.value).toEqual(rejected.value);
        }));

        it('should send an update request', function () {
            var cretiveId = 6031580,
                promise,
                creative = {
                    agencyId: 6031295,
                    alias: '100x100pic',
                    campaignId: 6031304,
                    createdDate: '2015-07-15T12:24:01-04:00',
                    createdTpwsKey: 'a78a4dd8-8bc1-4149-a0a9-e43368ddbc6a',
                    creativeType: 'jpg',
                    fileSize: 0,
                    filename: '100x100pic.jpg',
                    groupsCount: 1,
                    height: 100,
                    id: 6031580,
                    isExpandable: 0,
                    logicalDelete: 'N',
                    modifiedDate: '2015-07-15T12:24:01-04:00',
                    modifiedTpwsKey: 'a78a4dd8-8bc1-4149-a0a9-e43368ddbc6a',
                    ownerCampaignId: 6031304,
                    released: 0,
                    scheduled: 0,
                    width: 100
                };

            $httpBackend.expect('PUT', API_SERVICE + 'Creatives/' + cretiveId, creative)
                .respond(creative);
            promise = CreativeService.updateCreative(creative);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(creative);
        });
    });

    describe('getCreativeTemplateFile()', function () {
        it('should get a creative template file based on the given id', function () {
            var creativeId = 6031428,
                promise,
                creativeResponse = 200;

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId + '/file').respond(creativeResponse);
            promise = CreativeService.getCreativeTemplateFile(creativeId);
            $httpBackend.flush();
            expect(promise).toBeResolved();
        });

        it('should get an error due a request of a creative that has no file related', inject(function () {
            var creativeId = 6031428,
                creativeResponse = 500,
                promise,
                rejected = {
                    $$state: {
                        status: 2,
                        value: 'Failed to get file'
                    }
                };

            $httpBackend.whenGET(API_SERVICE + 'Creatives/' + creativeId + '/file').respond(creativeResponse);
            promise = CreativeService.getCreativeTemplateFile(creativeId);
            $httpBackend.flush();
            expect(promise.value).toEqual(rejected.value);
        }));
    });

    describe('getCreativeNotAssociatedByCampaignAndGroup()', function () {
        it('should get an array of all unassociated Creative based on CampaignId and CreativeGroupId', function () {
            var campaignId = 9125367,
                groupId = 9125583,
                promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 2,
                    records: [
                        {
                            Creative: [
                                {
                                    agencyId: 9024559,
                                    alias: '2010-ford-raptor-bumper',
                                    campaignId: 9125367,
                                    clickthrough: 'http://www.trueffect.com',
                                    createdDate: '2015-07-22T16:01:45-04:00',
                                    createdTpwsKey: '6163af69-7ce8-4a0c-9e26-bc2ee39192e0',
                                    creativeType: 'jpg',
                                    fileSize: 0,
                                    filename: '2010-ford-raptor-bumper.jpg',
                                    groupsCount: 1,
                                    height: 600,
                                    id: 9126773,
                                    isExpandable: 0,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-22T16:01:45-04:00',
                                    modifiedTpwsKey: '6163af69-7ce8-4a0c-9e26-bc2ee39192e0',
                                    ownerCampaignId: 9125367,
                                    released: 0,
                                    scheduled: 0,
                                    swfClickCount: 1,
                                    width: 900
                                },
                                {
                                    agencyId: 9024559,
                                    alias: '2012_ford_f-150_extended-cab-pickup_svt-raptor_fq_oem_2_300',
                                    campaignId: 9125367,
                                    clickthrough: 'http://www.trueffect.com',
                                    createdDate: '2015-07-22T16:01:45-04:00',
                                    createdTpwsKey: '6163af69-7ce8-4a0c-9e26-bc2ee39192e0',
                                    creativeType: 'jpg',
                                    fileSize: 0,
                                    filename: '2012_ford_f-150_extended-cab-pickup_svt-raptor_fq_oem_2_300.jpg',
                                    groupsCount: 1,
                                    height: 200,
                                    id: 9126775,
                                    isExpandable: 0,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-22T16:01:45-04:00',
                                    modifiedTpwsKey: '6163af69-7ce8-4a0c-9e26-bc2ee39192e0',
                                    ownerCampaignId: 9125367,
                                    released: 0,
                                    scheduled: 0,
                                    swfClickCount: 1,
                                    width: 300
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.expectGET(API_SERVICE +
                'Creatives/unassociated?campaignId=9125367&groupId=9125583').respond(response);
            promise = CreativeService.getCreativeNotAssociatedByCampaignAndGroup(campaignId, groupId);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('getCreativeAssociationsCount()', function () {
        it('should get number of associations', function () {
            var creativeId = 9125367,
                promise,
                response = {
                    groups: 1,
                    schedules: 1
                };

            $httpBackend.expectGET(API_SERVICE +
                'Creatives/' + creativeId + '/creativeAssociationsCount').respond(response);
            promise = CreativeService.getCreativeAssociationsCount(creativeId);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('getScheduleByCreative()', function () {
        it('should get an array of all schedules associated to creative', function () {
            var creativeId = 9125367,
                promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 2,
                    records: [
                        {
                            Creative: [
                                {
                                    campaignId: 6158572,
                                    creativeAlias: '300x1050image2',
                                    creativeGroupId: 6345544,
                                    creativeGroupName: 'My GC',
                                    creativeGroupWeight: 77,
                                    creativeId: 6158654,
                                    creativeType: 'jpg',
                                    filename: '300x1050image2.jpg',
                                    id: 10513305,
                                    placementEndDate: '2015-11-21T00:00:00-07:00',
                                    placementId: 6197447,
                                    placementName: '222',
                                    placementStartDate: '2015-11-11T00:00:00-07:00',
                                    siteId: 6064873,
                                    siteName: 'UI Test',
                                    siteSectionId: 6158578,
                                    siteSectionName: 'Placement UI Test - Section Test - 021441982360287',
                                    sizeName: '300x1050'
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.expectGET(API_SERVICE +
                'Creatives/' + creativeId + '/schedules').respond(response);
            promise = CreativeService.getScheduleByCreative(creativeId);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });
});
