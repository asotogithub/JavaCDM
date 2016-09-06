'use strict';

describe('Controller: CostDetailsController', function () {
    var controller,
        mockModel,
        $scope,
        $state;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();

        $scope.$parent = {};
        $scope.$parent.$parent = {};
        $scope.$parent.$parent.vmEdit = {
            costDetails: [],
            editForm: {
                $dirty: false,
                $pristine: true
            }
        };

        $state = _$state_;
        spyOn($state, 'go');
        spyOn($scope, '$on');

        controller = $controller('CostDetailsController', {
            $scope: $scope
        });

        mockModel = [
            {
                costKey: 0,
                endDate: '2015-09-16T23:59:59-07:00',
                startDate: '2015-06-16T00:00:00-07:00',
                id: 6269687,
                inventory: 100,
                isLast: false,
                margin: 100,
                plannedGrossAdSpend: 10,
                plannedGrossRate: 100,
                plannedNetAdSpend: 0,
                plannedNetRate: 0,
                rateType: {
                    KEY: 'CPM',
                    NAME: 'placement.rateTypeList.cpm'
                }
            },
            {
                costKey: 1,
                startDate: '2015-09-17T00:00:00-07:00',
                id: 6269688,
                inventory: 100,
                isLast: true,
                margin: 100,
                plannedGrossAdSpend: 10,
                plannedGrossRate: 100,
                plannedNetAdSpend: 0,
                plannedNetRate: 0,
                rateType: {
                    KEY: 'CPM',
                    NAME: 'placement.rateTypeList.cpm'
                }
            }
        ];
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    describe('CostDetail', function () {
        beforeEach(inject(function () {
            controller.activate(mockModel);
            controller.maxInputNumber = 10000;
        }));

        it('should add new costDetails', function () {
            controller.addCost(true);
            expect(controller.model.length).toBe(3);
        });

        it('should add placeHolder costDetails', function () {
            controller.addCost(true);

            expect(controller.model[2].endDate).toEqual(null);
            expect(controller.model[2].startDate).toNotEqual(null);
            expect(controller.model[2].costKey).toEqual(2);
            expect(controller.model[2].inventory).toEqual(1);
            expect(controller.model[2].isLast).toEqual(true);
        });

        it('should calculate cost inventory', function () {
            mockModel = {
                costKey: 0,
                endDate: '2015-09-16T23:59:59-07:00',
                startDate: '2015-06-16T00:00:00-07:00',
                id: 6269687,
                inventory: '100',
                isLast: false,
                margin: 100,
                plannedGrossAdSpend: 0,
                plannedGrossRate: 0,
                plannedNetAdSpend: 0,
                plannedNetRate: 0,
                rateType: {
                    KEY: 'CPM',
                    NAME: 'placement.rateTypeList.cpm'
                }
            };

            expect(mockModel.inventory).toEqual('100');
            controller.calculateCostInventory(mockModel);
            expect(mockModel.inventory).toEqual('1');
        });

        it('should disable startDate from first costDetail', function () {
            expect(controller.disableFirstStartDate(0)).toEqual(false);
        });

        it('should remove costDetail', function () {
            controller.addCost(true);
            expect(controller.model.length).toBe(3);
            controller.removeCost();
            expect(controller.model.length).toBe(2);
        });

        it('should update placeHolder when remove a costDetails', function () {
            controller.addCost(true);
            expect(controller.model.length).toBe(3);
            controller.removeCost();
            expect(controller.model.length).toBe(2);

            expect(controller.model[1].endDate).toEqual(null);
            expect(controller.model[1].startDate).toNotEqual(null);
            expect(controller.model[1].costKey).toEqual(1);
            expect(controller.model[1].inventory).toEqual(100);
            expect(controller.model[1].isLast).toEqual(true);
        });
    });
});
