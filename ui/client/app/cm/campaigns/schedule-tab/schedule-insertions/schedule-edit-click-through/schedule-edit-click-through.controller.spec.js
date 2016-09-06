'use strict';

describe('Controller: ScheduleEditClickThroughController', function () {
    var $scope,
        controller,
        data,
        modalInstance;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _lodash_) {
        $scope = $rootScope.$new();
        data = {};
        data.propertiesModel = {
            mainTitle: 'Test_CT_Url_01',
            clickThrough: [
                'http://www.test01.com',
                'http://www.test02.com',
                'http://www.test03.com'
            ]
        };
        modalInstance = {
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
                then: jasmine.createSpy('modalInstance.result.then')
            }
        };
        controller = $controller('ScheduleEditClickThroughController', {
            $scope: $scope,
            $modalInstance: modalInstance,
            lodash: _lodash_,
            data: data
        });
        controller.clickthroughsForm = {
            $setDirty: function () {
            }
        };
    }));

    describe('initializeModel()', function () {
        it('should set the controller data model', function () {
            expect(controller.mainTitle).toBeDefined();
            expect(controller.mainTitle).toBe('Test_CT_Url_01');
            expect(controller.clickthroughs).toBeDefined();
            expect(controller.clickthroughs.length).toBe(3);
        });
    });

    describe('Clickthrough list', function () {
        it('should add a new click-through element', function () {
            expect(controller.clickthroughs.length).toBe(3);
            controller.addCTUrl();
            expect(controller.clickthroughs.length).toBe(4);
        });

        it('should NOT add a new click-through element, if some element is empty', function () {
            controller.clickthroughs[0].url = '';
            expect(controller.clickthroughs.length).toBe(3);
            controller.addCTUrl();
            expect(controller.clickthroughs.length).toBe(3);
        });

        it('should remove element of the specified index', function () {
            controller.addCTUrl();
            expect(controller.clickthroughs.length).toBe(4);
            controller.removeCTUrl(0);
            expect(controller.clickthroughs.length).toBe(3);
        });

        it('should NOT remove the element if it is the last one', function () {
            controller.addCTUrl();
            expect(controller.clickthroughs.length).toBe(4);
            controller.removeCTUrl(0);
            expect(controller.clickthroughs.length).toBe(3);
        });

        it('should NOT remove the element if the index does not exists', function () {
            controller.addCTUrl();
            expect(controller.clickthroughs.length).toBe(4);
            controller.removeCTUrl(100);
            expect(controller.clickthroughs.length).toBe(4);
        });
    });
});
