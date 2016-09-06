'use strict';

describe('Controller: UploadCreativeController', function () {
    var $scope,
        $state,
        CampaignsService,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_, _CampaignsService_) {
        $rootScope.vmAdd = {};
        $rootScope.vmAdd.creatives = [];
        $rootScope.vmAdd.STEP = {
            UPLOAD: {
                index: 1,
                isValid: true,
                key: 'addCreative.upload'
            },
            ASSIGN: {
                index: 2,
                isValid: true,
                key: 'addCreative.assign'
            }
        };

        $scope = $rootScope.$new();
        $state = _$state_;
        CampaignsService = _CampaignsService_;
        controller = $controller('UploadCreativeController', {
            $scope: $scope,
            $state: _$state_
        });
        controller.creativesModel = [];
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should delete all creative from a list.', function () {
        controller.creativeList = [
            {
                name: 'creative1',
                filename: 'creative1',
                size: 20,
                fileType: 'gif'
            },
            {
                name: 'creative2',
                filename: 'creative2',
                size: 20,
                fileType: 'jpeg'
            }
        ];
        expect(controller.creativeList.length).toBe(2);
        controller.deleteAllCreatives(controller.creativeList);
        expect(controller.creativeList.length).toBe(0);
    });

    it('Should delete a creative from list.', function () {
        controller.creativeList = [
            {
                name: 'creative1',
                filename: 'creative1',
                size: 20,
                fileType: 'gif'
            },
            {
                name: 'creative2',
                filename: 'creative2',
                size: 20,
                fileType: 'jpeg'
            }
        ];
        expect(controller.creativeList.length).toBe(2);
        controller.deleteSingleCreative(controller.creativeList, controller.creativeList[0]);
        expect(controller.creativeList.length).toBe(1);
    });
});
