'use strict';

describe('Controller: CreativeGroupDetailsFormController', function () {
    var $scope,
        CreativeGroupDetailsFormController,
        detailsForm,
        model;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope) {
        detailsForm = jasmine.createSpyObj('detailsForm', ['$setPristine']);
        $scope = $rootScope.$new();
        model = {};

        $scope.detailsForm = detailsForm;

        CreativeGroupDetailsFormController = $controller('CreativeGroupDetailsFormController', {
            $scope: $scope
        });
        CreativeGroupDetailsFormController.model = model;
    }));

    describe('CREATIVE_GROUP', function () {
        it('should be CONSTANTS.CREATIVE_GROUP', inject(function (CONSTANTS) {
            expect(CreativeGroupDetailsFormController.CREATIVE_GROUP).toBe(CONSTANTS.CREATIVE_GROUP);
        }));
    });

    describe('REGEX_CREATIVE_GROUP_NAME', function () {
        it('should be CONSTANTS.REGEX.CREATIVE_GROUP_NAME', inject(function (CONSTANTS) {
            expect(CreativeGroupDetailsFormController.REGEX_CREATIVE_GROUP_NAME)
                .toBe(CONSTANTS.REGEX.CREATIVE_GROUP_NAME);
        }));
    });

    describe('cookieTargetInvalid', function () {
        it('should be true if $scope.detailsForm.cookieTarget is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.cookieTarget = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.cookieTargetInvalid).toBe(false);

                model.doCookieTargeting = 1;
                expect(CreativeGroupDetailsFormController.cookieTargetInvalid).toBe(false);

                model.isDefault = 0;
                expect(CreativeGroupDetailsFormController.cookieTargetInvalid).toBe(true);
            });
    });

    describe('daypartTargetInvalid', function () {
        it('should be true if $scope.detailsForm.daypartTarget is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.daypartTarget = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.daypartTargetInvalid).toBe(false);

                model.doDaypartTargeting = 1;
                expect(CreativeGroupDetailsFormController.daypartTargetInvalid).toBe(false);

                model.isDefault = 0;
                expect(CreativeGroupDetailsFormController.daypartTargetInvalid).toBe(true);
            });
    });

    describe('frequencyCapInvalid', function () {
        it('should be true if $scope.detailsForm.frequencyCap is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.frequencyCap = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.frequencyCapInvalid).toBe(false);

                model.enableFrequencyCap = 1;
                expect(CreativeGroupDetailsFormController.frequencyCapInvalid).toBe(false);

                model.isDefault = 0;
                expect(CreativeGroupDetailsFormController.frequencyCapInvalid).toBe(true);
            });
    });

    describe('frequencyCapWindowInvalid', function () {
        it('should be true if $scope.detailsForm.frequencyCapWindow is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.frequencyCapWindow = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.frequencyCapWindowInvalid).toBe(false);

                model.enableFrequencyCap = 1;
                expect(CreativeGroupDetailsFormController.frequencyCapWindowInvalid).toBe(false);

                model.isDefault = 0;
                expect(CreativeGroupDetailsFormController.frequencyCapWindowInvalid).toBe(true);
            });
    });

    describe('nameInvalid', function () {
        it('should be true if $scope.detailsForm.creativeGroupName is $invalid',
            function () {
                detailsForm.creativeGroupName = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.nameInvalid).toBe(true);
            });
    });

    describe('priorityInvalid', function () {
        it('should be true if $scope.detailsForm.priority is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.priority = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.priorityInvalid).toBe(false);

                model.enablePriority = 1;
                expect(CreativeGroupDetailsFormController.priorityInvalid).toBe(false);

                model.isDefault = 0;
                expect(CreativeGroupDetailsFormController.priorityInvalid).toBe(true);
            });
    });

    describe('weightInvalid', function () {
        it('should be true if $scope.detailsForm.weight is $invalid, but only when prerequisites are met',
            function () {
                detailsForm.weight = {
                    $invalid: true
                };
                expect(CreativeGroupDetailsFormController.weightInvalid).toBe(false);

                model.enableGroupWeight = 1;
                expect(CreativeGroupDetailsFormController.weightInvalid).toBe(true);
            });
    });

    describe('submitDisabled', function () {
        it('should be true if $scope.detailsForm is pristine', function () {
            detailsForm.$pristine = true;

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if cookieTargetInvalid is true', function () {
            model.doCookieTargeting = 1;
            model.isDefault = 0;
            detailsForm.cookieTarget = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if daypartTargetInvalid is true', function () {
            model.doDaypartTargeting = 1;
            model.isDefault = 0;
            detailsForm.daypartTarget = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if frequencyCapInvalid is true', function () {
            model.enableFrequencyCap = 1;
            model.isDefault = 0;
            detailsForm.frequencyCap = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if frequencyCapWindowInvalid is true', function () {
            model.enableFrequencyCap = 1;
            model.isDefault = 0;
            detailsForm.frequencyCapWindow = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if nameInvalid is true', function () {
            detailsForm.creativeGroupName = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if priorityInvalid is true', function () {
            model.enablePriority = 1;
            model.isDefault = 0;
            detailsForm.priority = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });

        it('should be true if weightInvalid is true', function () {
            model.enableGroupWeight = 1;
            detailsForm.weight = {
                $invalid: true
            };

            expect(CreativeGroupDetailsFormController.submitDisabled).toBe(true);
        });
    });

    describe('pristine', function () {
        describe('get', function () {
            it('should return $scope.detailsForm.$pristine', function () {
                detailsForm.$pristine = true;
                expect(CreativeGroupDetailsFormController.pristine).toBe(true);

                detailsForm.$pristine = false;
                expect(CreativeGroupDetailsFormController.pristine).toBe(false);
            });
        });

        describe('set', function () {
            it('should invoke $scope.detailsForm.$setPristine()', function () {
                CreativeGroupDetailsFormController.pristine = false;
                expect(detailsForm.$setPristine).not.toHaveBeenCalled();

                CreativeGroupDetailsFormController.pristine = true;
                expect(detailsForm.$setPristine).toHaveBeenCalled();
            });
        });
    });
});
