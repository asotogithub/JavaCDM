'use strict';

describe('Service: PackageUtilService', function () {
    var PackageUtilService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_PackageUtilService_) {
        PackageUtilService = _PackageUtilService_;
    }));

    describe('setHasPendingChanges()/getHasPendingChanges()', function () {
        it('should set and get the pending changes state', function () {
            PackageUtilService.setHasPendingChanges(true);
            expect(PackageUtilService.getHasPendingChanges()).toBe(true);
            PackageUtilService.setHasPendingChanges(false);
            expect(PackageUtilService.getHasPendingChanges()).toBe(false);
        });
    });
});
