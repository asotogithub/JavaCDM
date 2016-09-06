'use strict';

describe('Controller: AddPlacementPropertiesController', function () {
    var $q,
        $scope,
        $state,
        CONSTANTS,
        controller,
        placList = [
            {
                name: '',
                packageName: 'Media Package 1',
                site: {
                    id: 6064873,
                    name: 'Site 1'
                },
                section: [
                    {
                        id: 6064880,
                        name: 'Section1/S1'
                    },
                    {
                        id: 6064880,
                        name: 'Section2/S2'
                    }
                ],
                size: [
                    {
                        id: 5006631,
                        width: 120,
                        height: 110,
                        label: '120x110'
                    },
                    {
                        id: 5006631,
                        width: 180,
                        height: 150,
                        label: '180x150'
                    }
                ]
            }
        ];

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$q_, $rootScope, _$state_, _CONSTANTS_, _lodash_) {
        var $stateParams = {};

        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        $scope.$parent.vmAdd = {
            STEP: {
                PROPERTIES: {
                    index: 2,
                    isValid: false,
                    key: 'addPlacement.properties'
                }
            }
        };

        $stateParams.campaignId = 1337;
        $stateParams.ioId = 1337;

        CONSTANTS = _CONSTANTS_;
        controller = $controller('AddPlacementPropertiesController', {
            $scope: $scope,
            $stateParams: $stateParams,
            lodash: _lodash_
        });
    }));

    describe('activate()', function () {
        it('should parse all data from previuos step into a grid', function () {
            controller.activate(placList);
            expect(controller.propertiesParsedList.length).toBe(4);
        });

        it('should auto generated value to placement name ', function () {
            controller.activate(placList);

            expect(controller.propertiesParsedList[0].name).toEqual(placList[0].site.name + ' - ' +
                placList[0].section[0].name + ' - ' + placList[0].size[0].label);
            expect(controller.propertiesParsedList[1].name).toEqual(placList[0].site.name + ' - ' +
                placList[0].section[0].name + ' - ' + placList[0].size[1].label);
            expect(controller.propertiesParsedList[2].name).toEqual(placList[0].site.name + ' - ' +
                placList[0].section[1].name + ' - ' + placList[0].size[0].label);
            expect(controller.propertiesParsedList[3].name).toEqual(placList[0].site.name + ' - ' +
                placList[0].section[1].name + ' - ' + placList[0].size[1].label);
        });

        it('should truncate placement name to 256 max length', function () {
            placList[0].site.name = 'On the other hand, we denounce with righteous indignation and ' +
                'dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded ' +
                'by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame ' +
                'belongs to those who fail in their duty through weakness of will, which is the same as saying ' +
                'through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. ' +
                'In a free hour, when our power of choice is untrammelled and when nothing prevents our being able ' +
                'to do what we like best, every pleasure i';

            controller.activate(placList);

            expect(controller.propertiesParsedList[0].name.length).toEqual(CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME);
            expect(controller.propertiesParsedList[0].name).toEqual('On the other hand, we denounce with righteous ' +
                'indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the ' +
                'moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ' +
                'ensue; and equal bl');
        });
    });
});
