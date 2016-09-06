'use strict';

describe('Controller: GeoTargetingController', function () {
    var $form,
        $q,
        $scope,
        GeoTargetingController,
        GeoTargetingService,
        model;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $state, _$q_, _GeoTargetingService_) {
        $form = jasmine.createSpyObj('$form', ['$setDirty']);
        $q = _$q_;
        $scope = $rootScope.$new();
        GeoTargetingService = _GeoTargetingService_;
        model = {};

        spyOn($state, 'go');

        GeoTargetingController = $controller('GeoTargetingController', {
            $scope: $scope,
            GeoTargetingService: GeoTargetingService
        });
        $scope.vm = GeoTargetingController;
        GeoTargetingController.$form = $form;
        GeoTargetingController.model = model;
    }));

    describe('model', function () {
        it('should reconcileCountries() when new model is set', function () {
            var _model;

            GeoTargetingController.countries = [
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'MX',
                    id: 229,
                    label: 'Mexico',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ];
            _model = GeoTargetingController.model = {
                geoCountry: {
                    values: [
                        {
                            id: 114
                        },
                        {
                            id: 302
                        }
                    ]
                }
            };
            $scope.$apply();

            expect(_model.geoCountry.values).toEqual([
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ]);
        });

        it('should reconcileStates() when new model is set', function () {
            var _model;

            GeoTargetingController.states = [
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                },
                {
                    code: 'TX',
                    id: 67,
                    label: 'Texas',
                    typeId: 1
                }
            ];
            _model = GeoTargetingController.model = {
                geoState: {
                    values: [
                        {
                            id: 23
                        }
                    ]
                }
            };
            $scope.$apply();

            expect(_model.geoState.values).toEqual([
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                }
            ]);
        });

        it('should reconcileDMAs() when new model is set', function () {
            var _model;

            GeoTargetingController.dmas = [
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                },
                {
                    code: '752',
                    id: 1090,
                    label: 'Colorado Springs, CO',
                    typeId: 3
                },
                {
                    code: '773',
                    id: 1122,
                    label: 'Grand Junction, CO',
                    typeId: 3
                }
            ];
            _model = GeoTargetingController.model = {
                geoDma: {
                    values: [
                        {
                            id: 1088
                        }
                    ]
                }
            };
            $scope.$apply();

            expect(_model.geoDma.values).toEqual([
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                }
            ]);
        });

        it('should reconcileZips() when new model is set', function () {
            var _model;

            GeoTargetingController.zips = [
                {
                    code: '80021',
                    id: 29692,
                    label: '80021 - Broomfield - CO',
                    typeId: 4
                },
                {
                    code: '80002',
                    id: 29675,
                    label: '80002 - Arvada - CO',
                    typeId: 4
                }
            ];
            _model = GeoTargetingController.model = {
                geoZip: {
                    values: [
                        {
                            id: 29692
                        }
                    ]
                }
            };
            $scope.$apply();

            expect(_model.geoZip.values).toEqual([
                {
                    code: '80021',
                    id: 29692,
                    label: '80021 - Broomfield - CO',
                    typeId: 4
                }
            ]);
        });
    });

    describe('getCountries()', function () {
        var countries;

        beforeEach(function () {
            countries = $q.defer();

            spyOn(GeoTargetingService, 'getCountries').andReturn(countries.promise);
        });

        it('should invoke GeoTargetingService.getCountries() when visible', function () {
            expect(GeoTargetingService.getCountries).not.toHaveBeenCalled();

            GeoTargetingController.visible = true;
            $scope.$apply();

            expect(GeoTargetingService.getCountries).toHaveBeenCalled();
        });

        it('should set promise', function () {
            GeoTargetingController.visible = true;
            $scope.$apply();

            expect(GeoTargetingController.promise).toBe(countries.promise);
        });

        it('should set countries when GeoTargetingService.getCountries() is resolved', function () {
            var _countries = [
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'MX',
                    id: 229,
                    label: 'Mexico',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ];

            GeoTargetingController.visible = true;
            $scope.$apply();

            countries.resolve(_countries);
            $scope.$apply();

            expect(GeoTargetingController.countries).toBe(_countries);
        });

        it('should reconcileCountries() when GeoTargetingService.getCountries() is resolved', function () {
            var _countries = [
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'MX',
                    id: 229,
                    label: 'Mexico',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ];

            model.geoCountry = {
                values: [
                    {
                        id: 114
                    },
                    {
                        id: 302
                    }
                ]
            };

            GeoTargetingController.visible = true;
            $scope.$apply();

            countries.resolve(_countries);
            $scope.$apply();

            expect(model.geoCountry.values).toEqual([
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ]);
        });
    });

    describe('getStates()', function () {
        var states;

        beforeEach(function () {
            states = $q.defer();

            spyOn(GeoTargetingService, 'getStates').andReturn(states.promise);
        });

        it('should invoke GeoTargetingService.getStates()', function () {
            GeoTargetingController.getStates();

            expect(GeoTargetingService.getStates).toHaveBeenCalled();
        });

        it('should set promise', function () {
            GeoTargetingController.getStates();

            expect(GeoTargetingController.promise).toBe(states.promise);
        });

        it('should set states when GeoTargetingService.getStates() is resolved', function () {
            var _states = [
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                },
                {
                    code: 'TX',
                    id: 67,
                    label: 'Texas',
                    typeId: 1
                }
            ];

            GeoTargetingController.getStates();
            states.resolve(_states);
            $scope.$apply();

            expect(GeoTargetingController.states).toBe(_states);
        });

        it('should reconcileStates() when GeoTargetingService.getStates() is resolved', function () {
            var _states = [
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                },
                {
                    code: 'TX',
                    id: 67,
                    label: 'Texas',
                    typeId: 1
                }
            ];

            model.geoState = {
                values: [
                    {
                        id: 23
                    }
                ]
            };
            GeoTargetingController.getStates();
            states.resolve(_states);
            $scope.$apply();

            expect(model.geoState.values).toEqual([
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                }
            ]);
        });

        it('should do nothing if states is already set', function () {
            GeoTargetingController.states = [];
            GeoTargetingController.getStates();

            expect(GeoTargetingService.getStates).not.toHaveBeenCalled();
        });
    });

    describe('getDMAs()', function () {
        var dmas;

        beforeEach(function () {
            dmas = $q.defer();

            spyOn(GeoTargetingService, 'getDMAs').andReturn(dmas.promise);
        });

        it('should invoke GeoTargetingService.getDMAs()', function () {
            GeoTargetingController.getDMAs();

            expect(GeoTargetingService.getDMAs).toHaveBeenCalled();
        });

        it('should set promise', function () {
            GeoTargetingController.getDMAs();

            expect(GeoTargetingController.promise).toBe(dmas.promise);
        });

        it('should set dmas when GeoTargetingService.getDMAs() is resolved', function () {
            var _dmas = [
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                },
                {
                    code: '752',
                    id: 1090,
                    label: 'Colorado Springs, CO',
                    typeId: 3
                },
                {
                    code: '773',
                    id: 1122,
                    label: 'Grand Junction, CO',
                    typeId: 3
                }
            ];

            GeoTargetingController.getDMAs();
            dmas.resolve(_dmas);
            $scope.$apply();

            expect(GeoTargetingController.dmas).toBe(_dmas);
        });

        it('should reconcileDMAs() when GeoTargetingService.getDMAs() is resolved', function () {
            var _dmas = [
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                },
                {
                    code: '752',
                    id: 1090,
                    label: 'Colorado Springs, CO',
                    typeId: 3
                },
                {
                    code: '773',
                    id: 1122,
                    label: 'Grand Junction, CO',
                    typeId: 3
                }
            ];

            model.geoDma = {
                values: [
                    {
                        id: 1088
                    }
                ]
            };
            GeoTargetingController.getDMAs();
            dmas.resolve(_dmas);
            $scope.$apply();

            expect(model.geoDma.values).toEqual([
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                }
            ]);
        });

        it('should do nothing if dmas is already set', function () {
            GeoTargetingController.dmas = [];
            GeoTargetingController.getDMAs();

            expect(GeoTargetingService.getDMAs).not.toHaveBeenCalled();
        });
    });

    describe('getZips()', function () {
        var zips;

        beforeEach(function () {
            zips = $q.defer();

            spyOn(GeoTargetingService, 'getZips').andReturn(zips.promise);
        });

        it('should invoke GeoTargetingService.getZips()', function () {
            GeoTargetingController.getZips();

            expect(GeoTargetingService.getZips).toHaveBeenCalled();
        });

        it('should set promise', function () {
            GeoTargetingController.getZips();

            expect(GeoTargetingController.promise).toBe(zips.promise);
        });

        it('should set zips when GeoTargetingService.getZips() is resolved', function () {
            var _zips = [
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                },
                {
                    code: 'TX',
                    id: 67,
                    label: 'Texas',
                    typeId: 1
                }
            ];

            GeoTargetingController.getZips();
            zips.resolve(_zips);
            $scope.$apply();

            expect(GeoTargetingController.zips).toBe(_zips);
        });

        it('should reconcileZips() when GeoTargetingService.getZips() is resolved', function () {
            var _zips = [
                {
                    code: '80021',
                    id: 29692,
                    label: '80021 - Broomfield - CO',
                    typeId: 4
                },
                {
                    code: '80002',
                    id: 29675,
                    label: '80002 - Arvada - CO',
                    typeId: 4
                }
            ];

            model.geoZip = {
                values: [
                    {
                        id: 29692
                    }
                ]
            };
            GeoTargetingController.getZips();
            zips.resolve(_zips);
            $scope.$apply();

            expect(model.geoZip.values).toEqual([
                {
                    code: '80021',
                    id: 29692,
                    label: '80021 - Broomfield - CO',
                    typeId: 4
                }
            ]);
        });

        it('should do nothing if zips is already set', function () {
            GeoTargetingController.zips = [];
            GeoTargetingController.getZips();

            expect(GeoTargetingService.getZips).not.toHaveBeenCalled();
        });
    });

    describe('removeGeo()', function () {
        var geo,
            values;

        beforeEach(function () {
            geo = {
                id: 1
            };
            values = [
                geo,
                {
                    id: 2
                },
                {
                    id: 3
                }
            ];
        });

        it('should remove the specified geo from the list of values', function () {
            GeoTargetingController.removeGeo(new $.Event(), values, geo);

            expect(values).toEqual([
                {
                    id: 2
                },
                {
                    id: 3
                }
            ]);
        });

        it('should set $form as dirty', function () {
            GeoTargetingController.removeGeo(new $.Event(), values, geo);

            expect($form.$setDirty).toHaveBeenCalled();
        });

        it('should do nothing if the parent fieldset is disabled', function () {
            var fieldSet = angular.element('<fieldset disabled="disabled"></fieldset>'),
                currentTarget = angular.element('<div></div>').appendTo(fieldSet),
                evt = new $.Event();

            evt.currentTarget = currentTarget;
            GeoTargetingController.removeGeo(evt, values, geo);

            expect($form.$setDirty).not.toHaveBeenCalled();
            expect(values).toEqual([
                {
                    id: 1
                },
                {
                    id: 2
                },
                {
                    id: 3
                }
            ]);
        });
    });

    describe('setDirty()', function () {
        it('should set $form as dirty', function () {
            GeoTargetingController.setDirty();

            expect($form.$setDirty).toHaveBeenCalled();
        });
    });
});
