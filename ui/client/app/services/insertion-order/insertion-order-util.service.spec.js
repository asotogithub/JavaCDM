'use strict';

describe('Service: InsertionOrderUtilService', function () {
    var InsertionOrderUtilService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_InsertionOrderUtilService_) {
        InsertionOrderUtilService = _InsertionOrderUtilService_;
    }));

    describe('setHasPendingChanges()/getHasPendingChanges()', function () {
        it('should set and get the pending changes state', function () {
            InsertionOrderUtilService.setHasPendingChanges(true);
            expect(InsertionOrderUtilService.getHasPendingChanges()).toBe(true);
            InsertionOrderUtilService.setHasPendingChanges(false);
            expect(InsertionOrderUtilService.getHasPendingChanges()).toBe(false);
        });
    });
});
