'use strict';

describe('Controller: CampaignIoEditController', function () {
    var $httpBackend,
        $scope,
        $rootScope,
        $translate,
        API_SERVICE,
        CONSTANTS,
        DialogFactory,
        InsertionOrderService,
        controller,
        parentsIo,
        stateParams;

    // load the controller's module
    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
        $controller,
        _$httpBackend_,
        _$rootScope_,
        _lodash_,
        _API_SERVICE_,
        _InsertionOrderService_,
        _CONSTANTS_,
        _$state_,
        _$translate_,
        _DialogFactory_
    ) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        API_SERVICE = _API_SERVICE_;
        InsertionOrderService = _InsertionOrderService_;
        $translate = _$translate_;
        CONSTANTS = _CONSTANTS_;
        DialogFactory = _DialogFactory_;
        stateParams = {
            ioId: 6031583
        };

        controller = $controller('CampaignIoEditController', {
            $scope: $scope,
            lodash: _lodash_,
            InsertionOrderService: _InsertionOrderService_,
            CONSTANTS: CONSTANTS,
            $state: _$state_,
            $stateParams: stateParams,
            $translate: $translate,
            DialogFactory: DialogFactory
        });

        parentsIo = {
            campaignId: 6031581,
            createdDate: '2015-07-15T12:51:35-04:00',
            createdTpwsKey: 'e2c8a0bb-71b0-4fb5-8cec-bf4d37806eba',
            id: 6031583,
            ioNumber: 123456,
            lastUpdated: '2015-07-29T18:18:33-04:00',
            lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
            logicalDelete: 'N',
            mediaBuyId: 5034699,
            modifiedDate: '2015-07-29T18:18:33-04:00',
            modifiedTpwsKey: '920b249c-0eaa-4dfd-beea-158d22bbd7fd',
            name: 'john',
            notes: 'somenotes',
            placementsCount: 0,
            publisherId: 5011330,
            status: 'New',
            totalAdSpend: 0.0
        };

        $scope.vmEdit = controller;
        $rootScope.vm = {
            io: parentsIo
        };
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $httpBackend.whenGET(API_SERVICE + 'InsertionOrders/' + parentsIo.id).respond(parentsIo);
        $httpBackend.flush();
    }));

    it('should load, edit, format and save a IO', function () {
        var ioResponse = {
            campaignId: 6031581,
            createdDate: '2015-07-15T12:51:35-04:00',
            createdTpwsKey: 'e2c8a0bb-71b0-4fb5-8cec-bf4d37806eba',
            id: 6031583,
            ioNumber: 1234567,
            lastUpdated: '2015-07-29T18:18:33-04:00',
            lastUpdatedAuthor: 'UI Truforce USER UI Truforce USER',
            logicalDelete: 'N',
            mediaBuyId: 5034699,
            modifiedDate: '2015-07-29T18:18:33-04:00',
            modifiedTpwsKey: '920b249c-0eaa-4dfd-beea-158d22bbd7fd',
            name: 'Ikaro',
            notes: 'new notes',
            placementsCount: 0,
            publisherId: 5011330,
            status: 'Accepted',
            totalAdSpend: 0.0
        };

        $scope.$digest();

        $scope.$parent.vm = {
            activate: function () {}
        };

        expect(controller.io.status).toEqual(controller.statusList[0]);

        //Editing IO
        controller.io.status = controller.statusList[1];
        controller.io.name = 'Ikaro';
        controller.io.ioNumber = 1234567;
        controller.io.notes = 'new notes';

        $httpBackend.whenPUT(API_SERVICE + 'InsertionOrders/' + ioResponse.id, controller.io).respond(ioResponse);

        controller.save(controller.io);

        $httpBackend.flush();

        expect(controller.io.status).toEqual(controller.statusList[0]);
        expect(controller.io.status.key).toBe(controller.statusList[0].key);
        expect(controller.io.name).toBe('Ikaro');
        expect(controller.io.ioNumber).toBe(1234567);
        expect(controller.io.notes).toBe('new notes');
    });

    it('should show the warnings according to the status changes', function () {
        var warningDescription = $translate.instant('insertionOrder.toInactiveStatusWarning');

        spyOn(DialogFactory, 'showCustomDialog');
        $scope.$digest();
        $scope.vmEdit.io.status = $scope.vmEdit.statusList[1];
        $scope.$digest();
        $scope.vmEdit.io.status = $scope.vmEdit.statusList[2];
        $httpBackend.whenGET('components/dialog/informational.html').respond(200, '');
        $scope.$digest();
        expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
            type: CONSTANTS.DIALOG.TYPE.INFORMATIONAL,
            title: $translate.instant('global.warning'),
            description: warningDescription
        });
    });

    it('should validate max length from input IoName', inject(function ($compile) {
        var element = angular.element(
                '<form name="form"><input type="text" required ng-model="valueModel" ' +
                'name="ioName" ng-maxlength="' + controller.validations.maxLength + '" /></form>'
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
