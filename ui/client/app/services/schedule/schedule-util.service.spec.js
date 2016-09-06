'use strict';

describe('Service:ScheduleUtilService', function () {
    var ScheduleUtilService,
        sharedCreativeGroups = [],
        statusTraffic = {
            hasChangeTraffic: false,
            isOpenWarning: false,
            isTrafficLater: false
        },
        $window;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$window_, _ScheduleUtilService_) {
        ScheduleUtilService = _ScheduleUtilService_;
        $window = _$window_;
    }));

    describe('getStatusTraffic()', function () {
        it('should return list status of traffic', function () {
            expect(ScheduleUtilService.getStatusTraffic()).toEqual(statusTraffic);
        });
    });

    describe('set status traffic', function () {
        it('should set status if there is changed', function () {
            ScheduleUtilService.setStatusChange(true);
            expect(ScheduleUtilService.getStatusTraffic().hasChangeTraffic).toEqual(true);
        });

        it('should set status if warning is open', function () {
            ScheduleUtilService.setStatusWarning(true);
            expect(ScheduleUtilService.getStatusTraffic().isOpenWarning).toEqual(true);
        });

        it('should set status if traffic Later button is push', function () {
            ScheduleUtilService.setTrafficLater(true);
            expect(ScheduleUtilService.getStatusTraffic().isTrafficLater).toEqual(true);
        });

        it('should reset status traffic', function () {
            ScheduleUtilService.setStatusChange(true);
            ScheduleUtilService.resetStatusTraffic();
            expect(ScheduleUtilService.getStatusTraffic()).toEqual(statusTraffic);
        });

        it('should verify the state change is allowed', function () {
            var event = $window.document.createEvent('Event');

            ScheduleUtilService.setStatusChange(true);
            expect(ScheduleUtilService.isStateChangeAllowed(event, {}, {})).toEqual(false);
        });
    });

    describe('getSharedCreativeGroups()', function () {
        it('should return list status of traffic', function () {
            expect(ScheduleUtilService.getSharedCreativeGroups()).toEqual(sharedCreativeGroups);
        });
    });

    describe('set shared creative groups', function () {
        it('should return list status of traffic', function () {
            sharedCreativeGroups = [
                {
                    creativeGroupId: 1,
                    weight: 100
                }
            ];

            ScheduleUtilService.setSharedCreativeGroups(sharedCreativeGroups);
            expect(ScheduleUtilService.getSharedCreativeGroups()).toEqual(sharedCreativeGroups);
        });
    });
});
