'use strict';

describe('Controller: TeFlyOutController', function () {
    var $scope,
        controller;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, CONSTANTS) {
        $scope = $rootScope.$new();

        controller = $controller('TeFlyOutController', {
            $scope: $scope,
            CONSTANTS: CONSTANTS
        });
    }));

    describe('Fly-Out Directive', function () {
        it('should open half fly-out', inject(function (CONSTANTS) {
            controller.close(true);
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.HIDDEN);
            controller.half();
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.HALF_VIEW);
        }));

        it('should open full fly-out', inject(function (CONSTANTS) {
            controller.close(true);
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.HIDDEN);
            controller.full();
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.FULL_VIEW);
        }));

        it('should close fly-out', inject(function (CONSTANTS) {
            controller.full();
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.FULL_VIEW);
            controller.close(true);
            expect(controller.flyoutState).toBe(CONSTANTS.FLY_OUT.STATE.HIDDEN);
        }));
    });
});
