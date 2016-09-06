'use strict';

describe('Controller: DialogController', function () {
    var controller,
        $scope,
        modalInstance;

    beforeEach(module('uiApp'));

    // instantiate service
    beforeEach(inject(function ($controller, $rootScope) {
        $scope = $rootScope.$new();

        modalInstance = {
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
                then: jasmine.createSpy('modalInstance.result.then')
            }
        };

        controller = $controller('DialogController', {
            $scope: $scope,
            $modalInstance: modalInstance,
            data: {}
        });
    }));

    describe('Initial state', function () {
        it('should instantiate the controller', function () {
            expect(controller).not.toBeUndefined();
        });

        it('Should close the modal with result "yes" when accepted', function () {
            $scope.yes();
            expect(modalInstance.close).toHaveBeenCalledWith('yes');
        });

        it('Should close the modal with result "no" when rejected', function () {
            $scope.no();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('no');
        });
    });
});
