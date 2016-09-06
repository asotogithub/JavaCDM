'use strict';

describe('Service: costUtil', function () {
    var CostUtilService,
        $httpBackend,
        CONSTANTS;

    // load the service's module
    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _CostUtilService_, _CONSTANTS_) {
        $httpBackend = _$httpBackend_;
        CONSTANTS = _CONSTANTS_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        CostUtilService = _CostUtilService_;
    }));

    describe('Cost utils', function () {
        it('should return the rate type list ', function () {
            var rateTypeList = {
                CPM: {
                    KEY: 'CPM',
                    NAME: 'placement.rateTypeList.cpm',
                    VALUE: 4
                },
                CPC: {
                    KEY: 'CPC',
                    NAME: 'placement.rateTypeList.cpc',
                    VALUE: 2
                },
                CPA: {
                    KEY: 'CPA',
                    NAME: 'placement.rateTypeList.cpa',
                    VALUE: 1
                },
                FLT: {
                    KEY: 'FLT',
                    NAME: 'placement.rateTypeList.flt',
                    VALUE: 5
                },
                CPL: {
                    KEY: 'CPL',
                    NAME: 'placement.rateTypeList.cpl',
                    VALUE: 3
                }
            };

            expect(CONSTANTS.COST_DETAIL.RATE_TYPE.LIST).toEqual(rateTypeList);
        });

        it('should return the default rate type along with its translation string', function () {
            expect(CostUtilService.getDefaultRateType().KEY).toEqual(CONSTANTS.COST_DETAIL.RATE_TYPE.DEFAULT);
            expect(CostUtilService.getDefaultRateType().NAME).toEqual('placement.rateTypeList.cpm');
        });

        it('should calculate the inventory value for CPM rate type ', function () {
            var rateType = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST.CPM,
                rate = 10,
                adSpend = 1000,
                inventory = adSpend / rate * 1000;

            expect(CostUtilService.calculateInventory(adSpend, rate, rateType)).toBe(inventory.toString());
        });

        it('should calculate the inventory value for CPC rate type ', function () {
            var rateType = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST.CPC,
                rate = 10,
                adSpend = 1000,
                inventory = adSpend / rate;

            expect(CostUtilService.calculateInventory(adSpend, rate, rateType)).toBe(inventory.toString());
        });

        it('should calculate the inventory value for CPA rate type ', function () {
            var rateType = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST.CPA,
                rate = 10,
                adSpend = 1000,
                inventory = adSpend / rate;

            expect(CostUtilService.calculateInventory(adSpend, rate, rateType)).toBe(inventory.toString());
        });

        it('should calculate the inventory value for FLT rate type ', function () {
            var rateType = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST.FLT,
                rate = 10,
                adSpend = 1000,
                inventory = 1;

            expect(CostUtilService.calculateInventory(adSpend, rate, rateType)).toBe(inventory.toString());
        });

        it('should calculate the inventory value for CPL rate type ', function () {
            var rateType = CONSTANTS.COST_DETAIL.RATE_TYPE.LIST.CPL,
                rate = 10,
                adSpend = 1000,
                inventory = adSpend / rate;

            expect(CostUtilService.calculateInventory(adSpend, rate, rateType)).toBe(inventory.toString());
        });

        it('should calculate the Net Ad Spend based on Gross Ad Spend and Margin', function () {
            var grossAdSpend = 1000,
                netAdSpend = 800,
                margin = 20;

            expect(CostUtilService.calculateNetAdSpend(grossAdSpend, margin)).toBe(netAdSpend);
        });

        it('should calculate the Net Rate based on Gross Rate and Margin ', function () {
            var grossRate = 10,
                netRate = 8,
                margin = 20;

            expect(CostUtilService.calculateNetRate(grossRate, margin)).toBe(netRate);
        });
    });
});
