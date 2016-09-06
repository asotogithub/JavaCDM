'use strict';

describe('Controller: DaypartTargetingController', function () {
    var $form,
        $moment,
        $scope,
        DaypartTargetingController,
        model;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $state, _$moment_) {
        $form = jasmine.createSpyObj('$form', ['$setDirty']);
        $moment = _$moment_;
        $scope = $rootScope.$new();
        model = {};

        spyOn($state, 'go');

        DaypartTargetingController = $scope.vm = $controller('DaypartTargetingController', {
            $scope: $scope
        });
        DaypartTargetingController.$form = $form;
        DaypartTargetingController.model = model;
    }));

    describe('activate()', function () {
        it('should reconcileModel() when model is changed', function () {
            DaypartTargetingController.model = {
                daypartTarget:
                    'browserlocalday in (\'mon\',\'tue\',\'wed\',\'thu\',\'fri\') AND ' +
                    '(browserlocaltime >= 0800 AND browserlocaltime < 1700) OR ' +
                    'browserlocalday in (\'mon\',\'tue\',\'wed\',\'thu\',\'fri\') AND ' +
                    '(browserlocaltime >= 1700 AND browserlocaltime < 2300)'
            };

            $scope.$apply();

            expect(DaypartTargetingController.custom).toBe(false);
            expect(DaypartTargetingController.iabStandard.earlyMorning).toBe(false);
            expect(DaypartTargetingController.iabStandard.daytime).toBe(true);
            expect(DaypartTargetingController.iabStandard.evening).toBe(true);
            expect(DaypartTargetingController.iabStandard.lateNight).toBe(false);
            expect(DaypartTargetingController.iabStandard.weekends).toBe(false);

            DaypartTargetingController.model = {
                daypartTarget:
                    '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                    '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                    '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            };

            $scope.$apply();

            expect(DaypartTargetingController.custom).toBe(true);
            expect(DaypartTargetingController.customOptions).toEqual([
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ]);
        });
    });

    describe('addCustomOption()', function () {
        var customOption;

        beforeEach(function () {
            customOption = DaypartTargetingController.customOption;
            DaypartTargetingController.custom = true;
        });

        it('should add to customOptions', function () {
            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];
            customOption.startTime = $moment({
                hour: 10,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
            customOption.endTime = $moment({
                hour: 15,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
            customOption.mon = true;
            customOption.tue = true;
            customOption.thu = true;
            customOption.sat = true;

            DaypartTargetingController.addCustomOption();

            expect(DaypartTargetingController.customOptions).toEqual([
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'thu\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'sat\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ]);
        });

        it('should update() if entries are added to customOptions', function () {
            customOption.startTime = $moment({
                hour: 8,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
            customOption.endTime = $moment({
                hour: 17,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
            customOption.mon = true;
            customOption.fri = true;

            DaypartTargetingController.addCustomOption();

            expect(model.daypartTarget).toEqual(
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'0800\' And ' +
                '[browserlocaltime] < \'1700\') Or ([browserlocalday] = \'fri\' And ' +
                '[browserlocaltime] >= \'0800\' And [browserlocaltime] < \'1700\')'
            );
            expect($form.$setDirty).toHaveBeenCalled();
        });

        it('should not update() if no entries are added', function () {
            var expected = model.daypartTarget =
                    '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                    '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                    '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')';

            customOption.startTime = $moment({
                hour: 8,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();
            customOption.endTime = $moment({
                hour: 17,
                minute: 0,
                second: 0,
                millisecond: 0
            }).format();

            DaypartTargetingController.addCustomOption();

            expect(model.daypartTarget).toEqual(expected);
            expect($form.$setDirty).not.toHaveBeenCalled();
        });
    });

    describe('addDisabled()', function () {
        var customOption;

        beforeEach(function () {
            customOption = DaypartTargetingController.customOption;
        });

        it('should return true if no days are selected', function () {
            customOption.sun = false;
            customOption.mon = false;
            customOption.tue = false;
            customOption.wed = false;
            customOption.thu = false;
            customOption.fri = false;
            customOption.sat = false;

            expect(DaypartTargetingController.addDisabled()).toBe(true);
        });

        it('should return false if days are selected', function () {
            customOption.sun = false;
            customOption.mon = false;
            customOption.tue = false;
            customOption.wed = true;
            customOption.thu = false;
            customOption.fri = false;
            customOption.sat = false;

            expect(DaypartTargetingController.addDisabled()).toBe(false);
        });
    });

    describe('clearCustomOptions()', function () {
        beforeEach(function () {
            DaypartTargetingController.custom = true;
        });

        it('should clear customOptions', function () {
            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];

            DaypartTargetingController.clearCustomOptions();

            expect(DaypartTargetingController.customOptions).toEqual([]);
        });

        it('should update() if customOptions is cleared', function () {
            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];

            DaypartTargetingController.clearCustomOptions();

            expect(model.daypartTarget).toEqual('');
            expect($form.$setDirty).toHaveBeenCalled();
        });

        it('should not update() if there were no customOptions to clear', function () {
            DaypartTargetingController.clearCustomOptions();

            expect(model.daypartTarget).toBeUndefined();
            expect($form.$setDirty).not.toHaveBeenCalled();
        });
    });

    describe('clearSelectedCustomOptions()', function () {
        beforeEach(function () {
            DaypartTargetingController.custom = true;
        });

        it('should remove selected entries from customOptions', function () {
            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];
            DaypartTargetingController.customOption.selected = [
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];

            DaypartTargetingController.clearSelectedCustomOptions();

            expect(DaypartTargetingController.customOptions).toEqual([
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ]);
        });

        it('should update if entires are removed from customOptions', function () {
            model.daypartTarget =
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')';

            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];
            DaypartTargetingController.customOption.selected = [
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];

            DaypartTargetingController.clearSelectedCustomOptions();

            expect(model.daypartTarget).toEqual(
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            );
            expect($form.$setDirty).toHaveBeenCalled();
        });

        it('shoud not update() if no entries are removed', function () {
            model.daypartTarget =
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')';

            DaypartTargetingController.customOption.selected = [];

            DaypartTargetingController.clearSelectedCustomOptions();

            expect(model.daypartTarget).toEqual(
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            );
            expect($form.$setDirty).not.toHaveBeenCalled();
        });
    });

    describe('ensureValidTimes()', function () {
        var customOption;

        beforeEach(function () {
            customOption = DaypartTargetingController.customOption;
        });

        it('should leave endTime as is if after startTime', function () {
            customOption.startTime = $moment({
                hour: 8,
                minute: 0,
                millisecond: 0
            }).format();
            customOption.endTime = $moment({
                hour: 17,
                minute: 0,
                millisecond: 0
            }).format();

            DaypartTargetingController.ensureValidTimes();

            expect(customOption.endTime).toEqual($moment({
                hour: 17,
                minute: 0,
                millisecond: 0
            }).format());
        });

        it('should set endTime to startTime if not after startTime', function () {
            customOption.startTime = $moment({
                hour: 17,
                minute: 0,
                millisecond: 0
            }).format();
            customOption.endTime = $moment({
                hour: 8,
                minute: 0,
                millisecond: 0
            }).format();

            DaypartTargetingController.ensureValidTimes();

            expect(customOption.endTime).toEqual($moment({
                hour: 17,
                minute: 0,
                millisecond: 0
            }).format());
        });
    });

    describe('update()', function () {
        it('should set daypartTarget with IAB standard options if custom is false', function () {
            DaypartTargetingController.custom = false;
            DaypartTargetingController.iabStandard.lateNight = true;
            DaypartTargetingController.iabStandard.weekends = true;

            DaypartTargetingController.update();

            expect(model.daypartTarget).toEqual(
                'browserlocalday in (\'mon\',\'tue\',\'wed\',\'thu\',\'fri\') AND (browserlocaltime >= 2300 AND ' +
                'browserlocaltime < 0600) OR browserlocalday in (\'sat\',\'sun\')'
            );
        });

        it('should set daypartTarget with custom options if custom is true', function () {
            DaypartTargetingController.custom = true;
            DaypartTargetingController.customOptions = [
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')',
                '([browserlocalday] = \'tue\' And [browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            ];

            DaypartTargetingController.update();

            expect(model.daypartTarget).toEqual(
                '([browserlocalday] = \'mon\' And [browserlocaltime] >= \'1000\' And ' +
                '[browserlocaltime] < \'1500\') Or ([browserlocalday] = \'tue\' And ' +
                '[browserlocaltime] >= \'1000\' And [browserlocaltime] < \'1500\')'
            );
        });

        it('should setDirty()', function () {
            DaypartTargetingController.update();

            expect($form.$setDirty).toHaveBeenCalled();
        });
    });
});
