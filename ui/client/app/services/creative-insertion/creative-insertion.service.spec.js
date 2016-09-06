'use strict';

describe('Service: CreativeInsertionService', function () {
    var API_SERVICE,
        $httpBackend,
        CreativeInsertionService,
        data;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($state, _$httpBackend_, _API_SERVICE_, _CreativeInsertionService_) {
        API_SERVICE = _API_SERVICE_;
        $httpBackend = _$httpBackend_;
        CreativeInsertionService = _CreativeInsertionService_;

        spyOn($state, 'go');

        installPromiseMatchers();

        data = [
            {
                campaignId: 9084263,
                createdDate: '2015-08-17T11:49:34-06:00',
                createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                creativeAlias: 'Super Expert chiken',
                creativeClickthrough: 'http://www.trueffect.com',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: 0,
                creativeId: 9128897,
                creativeInsertionRootId: 9184057,
                endDate: '2014-07-12T00:00:00-06:00',
                id: 10000368,
                logicalDelete: 'N',
                modifiedDate: '2015-08-17T11:49:34-06:00',
                modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                placementEndDate: '2014-07-12T00:00:00-06:00',
                placementId: 9175602,
                placementName: 'Cox Media - Cox Media - 100x100',
                placementStartDate: '2014-05-15T00:00:00-06:00',
                placementStatus: 'Rejected',
                released: 0,
                sequence: 0,
                siteId: 9175592,
                siteName: 'Cox Media',
                siteSectionId: 9175601,
                siteSectionName: 'Cox Media',
                startDate: '2014-05-15T00:00:00-06:00',
                timeZone: 'MDT',
                weight: 100
            },
            {
                campaignId: 9084263,
                createdDate: '2015-08-17T13:08:00-06:00',
                createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                creativeAlias: 'Super Expert chiken',
                creativeClickthrough: 'http://www.trueffect.com',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: 0,
                creativeId: 9128897,
                creativeInsertionRootId: 9184421,
                endDate: '2014-04-06T00:00:00-06:00',
                id: 10000369,
                logicalDelete: 'N',
                modifiedDate: '2015-08-17T13:08:00-06:00',
                modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                placementEndDate: '2014-04-06T00:00:00-06:00',
                placementId: 9175594,
                placementName: 'Cox Media-Cox Media_ROS_wsbradio_b985_kiss1041_BAN_ATL_728x90-100x100',
                placementStartDate: '2014-03-03T00:00:00-07:00',
                placementStatus: 'Rejected',
                released: 0,
                sequence: 0,
                siteId: 9175592,
                siteName: 'Cox Media',
                siteSectionId: 9175593,
                siteSectionName: 'Cox Media_ROS_wsbradio_b985_kiss1041_BAN_ATL_728x90',
                startDate: '2014-03-03T00:00:00-07:00',
                timeZone: 'MDT',
                weight: 100
            },
            {
                campaignId: 9084263,
                createdDate: '2015-08-17T13:17:42-06:00',
                createdTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                creativeAlias: 'Super Expert chiken',
                creativeClickthrough: 'http://www.trueffect.com',
                creativeGroupId: 31878,
                creativeGroupName: 'Default00',
                creativeGroupWeight: 100,
                creativeGroupWeightEnabled: 0,
                creativeId: 9128897,
                creativeInsertionRootId: 9184422,
                endDate: '2014-04-06T00:00:00-06:00',
                id: 10000370,
                logicalDelete: 'N',
                modifiedDate: '2015-08-17T13:17:42-06:00',
                modifiedTpwsKey: '36ffd215-6b08-4cd4-bc60-9412ce9e131f',
                placementEndDate: '2014-04-06T00:00:00-06:00',
                placementId: 9175598,
                placementName: 'Cox Media-Cox Media_ROS_wsbradio_b985_kiss1041_REC_ATL_300x250-100x100',
                placementStartDate: '2014-03-03T00:00:00-07:00',
                placementStatus: 'Rejected',
                released: 0,
                sequence: 0,
                siteId: 9175592,
                siteName: 'Cox Media',
                siteSectionId: 9175597,
                siteSectionName: 'Cox Media_ROS_wsbradio_b985_kiss1041_REC_ATL_300x250',
                startDate: '2014-03-03T00:00:00-07:00',
                timeZone: 'MDT',
                weight: 100
            }
        ];
    }));

    describe('bulkUpdate()', function () {
        it('should bulk update a set of creatives and creativeGroups', function () {
            var promise,
                modifiedData = {
                    creatives: [
                        {
                            creativeClickthrough: 'http://www.trueffect.com',
                            endDate: '2014-04-06T00:00:00-06:00',
                            id: 10000369,
                            startDate: '2014-03-03T00:00:00-07:00',
                            weight: 100
                        },
                        {
                            creativeClickthrough: 'http://www.trueffect.com',
                            endDate: '2014-04-06T00:00:00-06:00',
                            id: 10000370,
                            startDate: '2014-03-03T00:00:00-07:00',
                            weight: 100
                        }
                    ],
                    creativeGroups: []
                };

            promise = CreativeInsertionService.bulkUpdate(modifiedData);

            $httpBackend.expectPUT(API_SERVICE + 'CreativeInsertions/bulkUpdate', modifiedData).respond(modifiedData);

            $httpBackend.flush();

            expect(promise).toBeResolvedWith(modifiedData);
        });
    });
});
