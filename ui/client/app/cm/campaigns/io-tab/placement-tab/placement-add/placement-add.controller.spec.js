'use strict';

describe('Controller: AddPlacementController', function () {
    var $q,
        $scope,
        $state,
        $stateParams,
        DialogFactory,
        controller,
        dialogDeferred;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$q_, $rootScope, _$state_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $stateParams = {};

        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showDialog', 'showCustomDialog']);
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });

        controller = $controller('AddPlacementController', {
            $scope: $scope,
            DialogFactory: DialogFactory
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should call a modal dialog when cancel() is resolved', inject(function (CONSTANTS, $translate) {
        var message = 'placement.confirm.discard',
            title = 'DIALOGS_CONFIRMATION_MSG';

        $stateParams.creativeId = 5959481;

        controller.cancel();
        expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
            type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
            title: $translate.instant(title),
            description: $translate.instant(message),
            buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL
        });
    }));
});
