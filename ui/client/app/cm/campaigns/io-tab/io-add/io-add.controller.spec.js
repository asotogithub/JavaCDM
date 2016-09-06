'use strict';

describe('Controller: IoAddController', function () {
    var $scope,
        $state,
        InsertionOrderService,
        MediaBuyService,
        UserService,
        controller,
        mockObject;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            InsertionOrderService = jasmine.createSpyObj('InsertionOrderService', ['saveInsertionOrder']);
            UserService = jasmine.createSpyObj('UserService', ['getUsername']);
            MediaBuyService = jasmine.createSpyObj('MediaBuyService', ['getMediaBuyByCampaign']);
            $provide.value('InsertionOrderService', InsertionOrderService);
            $provide.value('UserService', UserService);
            $provide.value('MediaBuyService', MediaBuyService);
        });

        inject(function ($q) {
            mockObject = {
                status: 'New',
                logicalDelete: 'N',
                name: 'InsertionOrderName',
                notes: 'InsertionOrderNotes',
                mediaBuyId: 5625268,
                ioNumber: 5265871
            };

            var defer = $q.defer();

            defer.resolve(mockObject);
            defer.$promise = defer.promise;

            InsertionOrderService.saveInsertionOrder.andReturn(defer.promise);
            MediaBuyService.getMediaBuyByCampaign.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('IoAddController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should resolve the promise to get the campaign to be saved', function () {
        controller.insertionOrder = {
            status: 'New',
            logicalDelete: 'N',
            name: 'InsertionOrderName',
            notes: 'InsertionOrderNotes',
            mediaBuyId: 5625268,
            ioNumber: 5265871
        };
        controller.promiseRequest = null;
        controller.save();

        expect(controller.promiseRequest).toBeResolved();
    });

    it('should simulate a save operation successfully', function () {
        controller.insertionOrder = {
            status: 'New',
            logicalDelete: 'N',
            name: 'InsertionOrderName',
            notes: 'InsertionOrderNotes',
            mediaBuyId: 5625268,
            ioNumber: 5265871
        };
        controller.promiseRequest = null;
        controller.save();

        expect(controller.promiseRequest.$$state.value.status).toBe(mockObject.status);
        expect(controller.promiseRequest.$$state.value.logicalDelete).toBe(mockObject.logicalDelete);
        expect(controller.promiseRequest.$$state.value.name).toBe(mockObject.name);
        expect(controller.promiseRequest.$$state.value.notes).toBe(mockObject.notes);
        expect(controller.promiseRequest.$$state.value.mediaBuyId).toBe(mockObject.mediaBuyId);
        expect(controller.promiseRequest.$$state.value.ioNumber).toBe(mockObject.ioNumber);
    });

    it('should validate max length from input IoName', inject(function ($compile) {
        var element = angular.element(
                '<form name="form"><input type="text" required ng-model="valueModel" ' +
                'name="ioName" ng-maxlength="' + controller.maxLength + '" /></form>'
            ),
            form;

        $compile(element)($scope);
        form = $scope.form;
        form.ioName.$setViewValue('aaaaaaaaaa');
        $scope.$digest();
        expect(form.ioName.$valid).toBe(true);
        form.ioName.$setViewValue('1234567890123456789012345678901234567890123456789012345678901234567890123456789' +
            '01234567890123456789012345678901234567890123456ABC');
        $scope.$digest();
        expect(form.ioName.$valid).toBe(false);
    }));
});
