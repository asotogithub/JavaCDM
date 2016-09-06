'use strict';

describe('Service: CampaignsUtilService', function () {
    var CampaignsUtilService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_CampaignsUtilService_) {
        CampaignsUtilService = _CampaignsUtilService_;
    }));

    describe('getIOStatusList()', function () {
        it('should return the IOs status list', function () {
            var statusList = {
                NEW: {
                    key: 'New',
                    name: 'global.planning'
                },
                ACCEPTED: {
                    key: 'Accepted',
                    name: 'global.active'
                },
                REJECTED: {
                    key: 'Rejected',
                    name: 'global.inactive'
                }
            };

            expect(CampaignsUtilService.getIOStatusList()).toEqual(statusList);
        });
    });

    describe('getStatusName()', function () {
        it('should $translate status name', function () {
            expect(CampaignsUtilService.getStatusName('New')).toEqual('Planning');
            expect(CampaignsUtilService.getStatusName('Accepted')).toEqual('Active');
            expect(CampaignsUtilService.getStatusName('Rejected')).toEqual('Inactive');
            expect(CampaignsUtilService.getStatusName('notValid')).toEqual('notValid');
        });
    });
});
