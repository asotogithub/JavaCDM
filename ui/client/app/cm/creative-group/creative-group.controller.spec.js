'use strict';

describe('Controller: CreativeGroupController', function () {
    var $scope,
        CreativeGroupController,
        CreativeGroupService,
        DialogFactory,
        creativeGroupId,
        creativeGroup;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
            $controller,
            $q,
            $rootScope,
            $state,
            _CreativeGroupService_,
            _DialogFactory_) {
        var $stateParams = {};

        $scope = $rootScope.$new();

        CreativeGroupService = _CreativeGroupService_;
        DialogFactory = _DialogFactory_;
        creativeGroupId = $stateParams.creativeGroupId = 1337;
        creativeGroup = $q.defer();

        spyOn(CreativeGroupService, 'getCreativeGroup').andReturn(creativeGroup.promise);
        spyOn(DialogFactory, 'showDialog');
        spyOn($state, 'go');

        CreativeGroupController = $controller('CreativeGroupController', {
            $scope: $scope,
            $stateParams: $stateParams,
            CreativeGroupService: CreativeGroupService,
            DialogFactory: DialogFactory
        });
    }));

    describe('activate()', function () {
        it('should invoke CreativeGroupService.getCreativeGroup()', function () {
            expect(CreativeGroupService.getCreativeGroup).toHaveBeenCalledWith(creativeGroupId);
            expect(CreativeGroupController.promise).toBe(creativeGroup.promise);
        });

        it('should set name when CreativeGroupService.getCreativeList() is resolved', function () {
            creativeGroup.resolve({
                name: 'foo'
            });
            $scope.$apply();

            expect(CreativeGroupController.crtvGrpName).toEqual('foo');
        });
    });

    describe('$scope.on(`creative-group-updated`)', function () {
        it('should set name', function () {
            $scope.$emit('creative-group-updated', {
                name: 'bar'
            });
            expect(CreativeGroupController.crtvGrpName).toEqual('bar');

            $scope.$emit('creative-group-updated', {
                name: 'foobar'
            });
            expect(CreativeGroupController.crtvGrpName).toEqual('foobar');
        });
    });
});
