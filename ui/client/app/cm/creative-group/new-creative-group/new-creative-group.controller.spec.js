'use strict';

describe('Controller: NewCreativeGroupController', function () {
    var $q,
        $scope,
        $state,
        $translate,
        CreativeGroupService,
        DialogFactory,
        ErrorRequestHandler,
        NewCreativeGroupController,
        campaignId;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                $rootScope,
                                _$q_,
                                _$state_,
                                _$translate_,
                                _CreativeGroupService_,
                                _DialogFactory_,
                                _ErrorRequestHandler_) {
        var $stateParams = {};

        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $translate = _$translate_;
        CreativeGroupService = _CreativeGroupService_;
        DialogFactory = _DialogFactory_;
        ErrorRequestHandler = _ErrorRequestHandler_;
        campaignId = $stateParams.campaignId = 1337;

        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        spyOn($state, 'go');
        spyOn(CreativeGroupService, 'saveCreativeGroup');
        spyOn(DialogFactory, 'showDialog');
        spyOn(DialogFactory, 'showDismissableMessage');

        NewCreativeGroupController = $controller('NewCreativeGroupController', {
            $scope: $scope,
            $state: $state,
            $stateParams: $stateParams,
            CreativeGroupService: CreativeGroupService,
            DialogFactory: DialogFactory
        });
    }));

    describe('campaignId', function () {
        it('should be initialized with $stateParams.campaignId', function () {
            expect(NewCreativeGroupController.campaignId).toEqual(campaignId);
        });
    });

    describe('activate()', function () {
        it('should initialize creativeGroup', function () {
            expect(NewCreativeGroupController.creativeGroup).toEqual({
                campaignId: campaignId,
                cookieTarget: null,
                daypartTarget: null,
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                enableFrequencyCap: 0,
                enableGroupWeight: 0,
                enablePriority: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                },
                isDefault: 0,
                name: null,
                priority: 0,
                weight: 100
            });
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
            NewCreativeGroupController.pristine = true;
            NewCreativeGroupController.close(evt);

            expect(evt.isDefaultPrevented()).toBe(false);
            expect(DialogFactory.showDialog).not.toHaveBeenCalled();
        });

        it('should preventDefault() on event and invoke DialogFactory.showDialog() if not pristine', function () {
            NewCreativeGroupController.pristine = false;
            NewCreativeGroupController.close(evt);

            expect(evt.isDefaultPrevented()).toBe(true);
            expect(DialogFactory.showDialog).toHaveBeenCalledWith(DialogFactory.DIALOG.SPECIFIC_TYPE.DISCARD_CHANGES);
        });

        it('should leave() when DialogFactory.showDialog() is resolved', function () {
            NewCreativeGroupController.pristine = false;
            NewCreativeGroupController.close(evt);
            result.resolve();
            $scope.$apply();

            expect($state.go).toHaveBeenCalledWith('creative-groups-tab', {
                campaignId: campaignId
            });
        });
    });

    describe('save()', function () {
        var creativeGroup,
            deferred;

        beforeEach(function () {
            creativeGroup = NewCreativeGroupController.creativeGroup = {
                foo: 'bar'
            };
            deferred = $q.defer();

            CreativeGroupService.saveCreativeGroup.andReturn(deferred.promise);
            NewCreativeGroupController.save();
        });

        it('should invoke CreativeGroupService.saveCreativeGroup()', function () {
            expect(CreativeGroupService.saveCreativeGroup).toHaveBeenCalledWith(creativeGroup);
            expect(NewCreativeGroupController.promise).toEqual(deferred.promise);
        });

        describe('on success', function () {
            it('should invoke DialogFactory.showDismissableMessage()', function () {
                deferred.resolve({});
                $scope.$apply();

                expect(DialogFactory.showDismissableMessage).toHaveBeenCalledWith(
                    DialogFactory.DISMISS_TYPE.INFO,
                    $translate.instant('info.operationCompleted'));
            });

            it('should go() to the `creative-group` state', function () {
                var creativeGroupId = 69;

                deferred.resolve({
                    campaignId: campaignId,
                    id: creativeGroupId
                });
                $scope.$apply();

                expect($state.go).toHaveBeenCalledWith('creative-group', {
                    campaignId: campaignId,
                    creativeGroupId: creativeGroupId
                });
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

                expect(NewCreativeGroupController.errorMessage).toEqual('foobar');
            });

            it('should invoke DialogFactory.showDialog() if errorMessage is not available', function () {
                deferred.reject({});
                $scope.$apply();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            });
        });
    });
});
