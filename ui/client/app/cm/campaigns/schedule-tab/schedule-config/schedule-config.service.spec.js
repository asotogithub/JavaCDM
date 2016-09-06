'use strict';

describe('Service: scheduleConfig', function () {
    var $compile,
        $delegate,
        $element,
        $scope,
        $window,
        CONSTANTS,
        ScheduleConfigService,
        attachChildElements = 2,
        rowIdentifier = '08783ea9-792a-46b5-bd8b-66edeeca16b3';

    // load the service's module
    beforeEach(module('uiApp'));

    describe('Schedule Config', function () {
        beforeEach(inject(function (_ScheduleConfigService_, _CONSTANTS_, $rootScope, _$compile_, _$window_) {
            CONSTANTS = _CONSTANTS_;
            $compile = _$compile_;
            $window = _$window_;
            ScheduleConfigService = _ScheduleConfigService_;
            $scope = $rootScope.$new();

            if (attachChildElements) {
                $delegate = angular.element('<div class="te-tree-grid-delegate"></div>');
                var creativeInsertionObject = {
                    $$uuid: rowIdentifier,
                    editSupport: {
                        clickThroughUrl: true,
                        flightDateEnd: true,
                        flightDateStart: true,
                        weight: true
                    },
                    id: 10335538,
                    weight: 9999,
                    flightDateEnd: '12/17/2015 06:00:00 PM',
                    flightDateStart: '11/13/2015 09:00:00 AM'
                };

                $delegate.data('jqxTreeGrid', {
                    instance: {
                        base: {
                            getRow: function () {
                                return creativeInsertionObject;
                            }
                        }
                    }
                });

                $element = angular.element($delegate);
                $compile($element)($scope);
                $window.document.body.appendChild($element[0]);
                $scope.$digest();
                attachChildElements--;
            }
        }));

        it('should return schedule weight ', function () {
            var scheduleWeight = {
                MAX: 10000,
                MIN: 0,
                DEFAULT: 100
            };

            expect(CONSTANTS.SCHEDULE.WEIGHT).toEqual(scheduleWeight);
        });

        it('should allow put max weight value for creative insertion', function () {
            var data = {
                row: rowIdentifier
            };

            expect(ScheduleConfigService.getWeightEditorConf().validation(data, 10000)).toEqual(true);
        });

        it('should not allow put higher than max weight value for creative insertion', function () {
            var data = {
                row: rowIdentifier
            };

            expect(ScheduleConfigService.getWeightEditorConf().validation(data, 10001)).not.toBe(true);
        });

        it('should allow set a valid start date', function () {
            var data = {
                    row: rowIdentifier,
                    datafield: 'flightDateStart'
                },
                startDate = '10/17/2015 05:00:00 PM';

            expect(ScheduleConfigService.getDateEditorConf().validation(data, startDate)).toEqual(true);
        });

        it('should allow set a valid end date', function () {
            var data = {
                    row: rowIdentifier,
                    datafield: 'flightDateEnd'
                },
                endDate = '11/13/2015 11:00:00 AM';

            expect(ScheduleConfigService.getDateEditorConf().validation(data, endDate)).toEqual(true);
        });

        it('should not allow start date higher than end date', function () {
            var data = {
                    row: rowIdentifier,
                    datafield: 'flightDateStart'
                },
                higherStartDate = '12/17/2015 07:00:00 PM';

            expect(ScheduleConfigService.getDateEditorConf().validation(data, higherStartDate)).not.toBe(true);
        });

        it('should not allow end date lower than start date', function () {
            var data = {
                    row: rowIdentifier,
                    datafield: 'flightDateEnd'
                },
                lowerEndDate = '11/13/2015 08:00:00 AM';

            expect(ScheduleConfigService.getDateEditorConf().validation(data, lowerEndDate)).not.toBe(true);
        });
    });
});
