'use strict';

describe('Controller: AssociatePlacementsController', function () {
    // load the controller's module
    beforeEach(module('uiApp'));

    var AssociatePlacementsController, scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        scope.$parent = {};
        scope.$parent.$parent = {};
        scope.$parent.$parent.$parent = {};
        scope.$parent.$parent.$parent.vm = {};
        scope.$parent.$parent.$parent.vm.metrics = [
            {
                value: 1
            },
            {
                value: 1
            },
            {
                value: 1
            }
        ];

        AssociatePlacementsController = $controller('AssociatePlacementsController', {
            $scope: scope
        });
    }));

    it('should create an instance of AssociatePlacementsController', function () {
        expect(AssociatePlacementsController).not.toBeUndefined();
    });
});
