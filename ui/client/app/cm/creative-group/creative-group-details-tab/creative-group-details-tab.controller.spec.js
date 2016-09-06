'use strict';

describe('Controller: CreativeGroupDetailsTabController', function () {
    var $q,
        $scope,
        $state,
        $translate,
        CreativeGroupDetailsTabController,
        CreativeGroupService,
        DialogFactory,
        ErrorRequestHandler,
        creativeGroupDeferred;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
        $controller,
        $rootScope,
        _$state_,
        _$q_,
        _$translate_,
        _CreativeGroupService_,
        _DialogFactory_,
        _ErrorRequestHandler_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $translate = _$translate_;
        CreativeGroupService = _CreativeGroupService_;
        DialogFactory = _DialogFactory_;
        creativeGroupDeferred = $q.defer();
        ErrorRequestHandler = _ErrorRequestHandler_;

        $scope.creativeGroupId = 1337;
        spyOn($scope, '$emit');
        spyOn($state, 'go');
        spyOn(CreativeGroupService, 'forceRemoveCreativeGroup');
        spyOn(CreativeGroupService, 'getCreativeGroup').andReturn(creativeGroupDeferred.promise);
        spyOn(CreativeGroupService, 'removeCreativeGroup');
        spyOn(CreativeGroupService, 'updateCreativeGroup');
        spyOn(DialogFactory, 'showCustomDialog');
        spyOn(DialogFactory, 'showDialog');
        spyOn(DialogFactory, 'showDismissableMessage');
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();

        CreativeGroupDetailsTabController = $controller('CreativeGroupDetailsTabController', {
            $scope: $scope,
            $state: $state,
            CreativeGroupService: CreativeGroupService,
            DialogFactory: DialogFactory
        });
    }));

    describe('CREATIVE_GROUP_DEFAULT_NAME', function () {
        it('should be initialized with CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME', inject(function (CONSTANTS) {
            expect(CreativeGroupDetailsTabController.CREATIVE_GROUP_DEFAULT_NAME)
                .toEqual(CONSTANTS.CREATIVE_GROUP.DEFAULT_NAME);
        }));
    });

    describe('activate()', function () {
        it('should invoke CreativeGroupService.getCreativeGroup()', function () {
            expect(CreativeGroupService.getCreativeGroup).toHaveBeenCalledWith($scope.creativeGroupId);
            expect(CreativeGroupDetailsTabController.promise).toBe(creativeGroupDeferred.promise);
        });

        it('should set creativeGroup, $emit(`creative-group-updated`), and clearPromise() when promise is resolved',
            function () {
                var expected = {
                    foo: 'bar',
                    foobar: 1337
                };

                creativeGroupDeferred.resolve(expected);
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.creativeGroup).toBe(expected);
                expect(CreativeGroupDetailsTabController.promise).toBe(null);
                expect($scope.$emit).toHaveBeenCalledWith('creative-group-updated', expected);
            });

        it('should clearPromise() when promise is rejected', function () {
            creativeGroupDeferred.reject();
            $scope.$apply();

            expect(CreativeGroupDetailsTabController.promise).toBe(null);
        });
    });

    describe('close()', function () {
        var evt,
            result;

        beforeEach(function () {
            evt = new $.Event();
            result = $q.defer();

            DialogFactory.showDialog.andReturn({
                result: result.promise
            });
        });

        it('should do nothing if pristine', function () {
            CreativeGroupDetailsTabController.pristine = true;
            CreativeGroupDetailsTabController.close(evt);

            expect(evt.isDefaultPrevented()).toBe(false);
            expect(DialogFactory.showDialog).not.toHaveBeenCalled();
        });

        it('should preventDefault() on event and invoke DialogFactory.showDialog() if not pristine', function () {
            CreativeGroupDetailsTabController.pristine = false;
            CreativeGroupDetailsTabController.close(evt);

            expect(evt.isDefaultPrevented()).toBe(true);
            expect(DialogFactory.showDialog).toHaveBeenCalledWith(DialogFactory.DIALOG.SPECIFIC_TYPE.DISCARD_CHANGES);
        });

        it('should leave() when DialogFactory.showDialog() is resolved', function () {
            var campaignId = 1337;

            creativeGroupDeferred.resolve({
                campaignId: campaignId
            });
            $scope.$apply();

            CreativeGroupDetailsTabController.pristine = false;
            CreativeGroupDetailsTabController.close(evt);
            result.resolve();
            $scope.$apply();

            expect($state.go).toHaveBeenCalledWith('creative-groups-tab', {
                campaignId: campaignId
            });
        });
    });

    describe('remove()', function () {
        var dialogDeferred;

        beforeEach(function () {
            dialogDeferred = $q.defer();
            DialogFactory.showCustomDialog.andReturn({
                result: dialogDeferred.promise
            });

            CreativeGroupDetailsTabController.remove();
        });

        it('should invoke DialogFactory.showCustomDialog()', function () {
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: DialogFactory.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('creativeGroup.confirm.deleteCreativeGroup'),
                buttons: DialogFactory.DIALOG.BUTTON_SET.YES_NO
            });
        });

        it('should do nothing if DialogFactory.showCustomDialog() is rejected', function () {
            dialogDeferred.reject({});
            $scope.$apply();

            expect(CreativeGroupService.removeCreativeGroup).not.toHaveBeenCalled();
        });

        describe('CreativeGroupService.removeCreativeGroup()', function () {
            var serviceDeferred,
                creativeGroupId,
                campaignId;

            beforeEach(function () {
                serviceDeferred = $q.defer();
                creativeGroupId = 42;
                campaignId = 1337;
                CreativeGroupService.removeCreativeGroup.andReturn(serviceDeferred.promise);
                creativeGroupDeferred.resolve({
                    id: creativeGroupId,
                    campaignId: campaignId
                });
                $scope.$apply();

                dialogDeferred.resolve({});
                $scope.$apply();
            });

            it('should be invoked if DialogFactory.showCustomDialog() is resolved', function () {
                expect(CreativeGroupService.removeCreativeGroup).toHaveBeenCalledWith(creativeGroupId);
                expect(CreativeGroupDetailsTabController.promise).toBe(serviceDeferred.promise);
            });

            describe('on success', function () {
                it('should leave()', function () {
                    serviceDeferred.resolve({});
                    $scope.$apply();

                    expect($state.go).toHaveBeenCalledWith('creative-groups-tab', {
                        campaignId: campaignId
                    });
                });

                it('should clearPromise()', function () {
                    serviceDeferred.resolve({});
                    $scope.$apply();

                    expect(CreativeGroupDetailsTabController.promise).toBe(null);
                });
            });
        });
    });

    describe('forceRemove()', function () {
        var dialogDeferred,
            creativeGroupId,
            campaignId;

        beforeEach(function () {
            var serviceDeferred = $q.defer();

            creativeGroupId = 42;
            campaignId = 1337;
            creativeGroupDeferred.resolve({
                id: creativeGroupId,
                campaignId: campaignId
            });
            $scope.$apply();

            dialogDeferred = $q.defer();
            DialogFactory.showCustomDialog.andCallFake((function () {
                var calledOnce;

                return function () {
                    if (calledOnce) {
                        return {
                            result: dialogDeferred.promise
                        };
                    }
                    else {
                        calledOnce = true;
                        var deferred = $q.defer();

                        deferred.resolve({});

                        return {
                            result: deferred.promise
                        };
                    }
                };
            })());

            CreativeGroupService.removeCreativeGroup.andReturn(serviceDeferred.promise);
            CreativeGroupDetailsTabController.remove();
            $scope.$apply();

            serviceDeferred.reject({
                status: 400
            });
            $scope.$apply();
        });

        it('should invoke DialogFactory.showCustomDialog()', function () {
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: DialogFactory.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('creativeGroup.confirm.forceDeleteCreativeGroup'),
                buttons: DialogFactory.DIALOG.BUTTON_SET.YES_NO
            });
        });

        it('should do nothing if DialogFactory.showCustomDialog() is rejected', function () {
            dialogDeferred.reject({});
            $scope.$apply();

            expect(CreativeGroupService.forceRemoveCreativeGroup).not.toHaveBeenCalled();
        });

        describe('CreativeGroupService.forceRemoveCreativeGroup()', function () {
            var serviceDeferred;

            beforeEach(function () {
                serviceDeferred = $q.defer();
                CreativeGroupService.forceRemoveCreativeGroup.andReturn(serviceDeferred.promise);
                dialogDeferred.resolve({});
                $scope.$apply();
            });

            it('should be invoked if DialogFactory.showCustomDialog() is resolved', function () {
                expect(CreativeGroupService.forceRemoveCreativeGroup).toHaveBeenCalledWith(creativeGroupId);
                expect(CreativeGroupDetailsTabController.promise).toBe(serviceDeferred.promise);
            });

            describe('on success', function () {
                it('should leave()', function () {
                    serviceDeferred.resolve({});
                    $scope.$apply();

                    expect($state.go).toHaveBeenCalledWith('creative-groups-tab', {
                        campaignId: campaignId
                    });
                });

                it('should clearPromise()', function () {
                    serviceDeferred.resolve({});
                    $scope.$apply();

                    expect(CreativeGroupDetailsTabController.promise).toBe(null);
                });
            });
        });
    });

    describe('save()', function () {
        var creativeGroup,
            deferred;

        beforeEach(function () {
            creativeGroup = {
                foo: 'bar'
            };
            deferred = $q.defer();

            creativeGroupDeferred.resolve(creativeGroup);
            $scope.$apply();
            CreativeGroupService.updateCreativeGroup.andReturn(deferred.promise);
            CreativeGroupDetailsTabController.save();
        });

        it('should invoke CreativeGroupService.updateCreativeGroup()', function () {
            CreativeGroupDetailsTabController.save();

            expect(CreativeGroupService.updateCreativeGroup).toHaveBeenCalledWith(creativeGroup);
            expect(CreativeGroupDetailsTabController.promise).toEqual(deferred.promise);
        });

        describe('on success', function () {
            it('should set errorMessage', function () {
                CreativeGroupDetailsTabController.errorMessage = 'foobar';
                deferred.resolve({});
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.errorMessage).toBeNull();
            });

            it('should set pristine', function () {
                CreativeGroupDetailsTabController.pristine = false;
                deferred.resolve({});
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.pristine).toBe(true);
            });

            it('should update creativeGroup', function () {
                deferred.resolve({
                    foobar: 1337
                });
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.creativeGroup).toBe(creativeGroup);
                expect(creativeGroup).toEqual({
                    foo: 'bar',
                    foobar: 1337
                });
            });

            it('should $emit(`creative-group-updated`) on $scope', function () {
                deferred.resolve({
                    foobar: 1337
                });
                $scope.$apply();

                expect($scope.$emit).toHaveBeenCalledWith('creative-group-updated', {
                    foo: 'bar',
                    foobar: 1337
                });
            });

            it('should invoke DialogFactory.showDismissableMessage()', function () {
                deferred.resolve({});
                $scope.$apply();

                expect(DialogFactory.showDismissableMessage).toHaveBeenCalledWith(
                    DialogFactory.DISMISS_TYPE.INFO,
                    $translate.instant('info.operationCompleted'));
            });

            it('should clearPromise()', function () {
                deferred.resolve({});
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.promise).toBe(null);
            });
        });

        describe('on failure', function () {
            it('should set errorMessage', function () {
                deferred.reject({
                    data: {
                        errors: [
                            {
                                message: 'foobar'
                            }
                        ]
                    }
                });
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.errorMessage).toEqual('foobar');
            });

            it('should invoke DialogFactory.showDialog() if errorMessage is not available', function () {
                deferred.reject({});
                $scope.$apply();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            });

            it('should clearPromise()', function () {
                deferred.reject({});
                $scope.$apply();

                expect(CreativeGroupDetailsTabController.promise).toBe(null);
            });
        });
    });
});
