'use strict';

describe('Controller: TeTreeController', function () {
    var $scope,
        TeTreeController,
        list;

    beforeEach(module('te.components'));

    beforeEach(inject(function ($controller, $q, $rootScope) {
        $scope = $rootScope.$new();
        list = $q.defer();

        TeTreeController = $scope.vm = $controller('TeTreeController', {
            $scope: $scope
        });

        $scope.vm.model = [
            {
                id: 1,
                isFilterVisible: true,
                title: 'node1',
                nodes: [
                    {
                        id: 11,
                        isFilterVisible: true,
                        title: 'node1.1',
                        nodes: []
                    },
                    {
                        id: 12,
                        isFilterVisible: true,
                        title: 'node1.2',
                        nodes: []
                    }
                ]
            },
            {
                id: 2,
                isFilterVisible: true,
                title: 'node2',
                nodes: [
                    {
                        id: 21,
                        isFilterVisible: true,
                        title: 'node2.1',
                        nodes: []
                    }
                ]
            }
        ];
    }));

    //FIXME fix these broken unit tests
    describe('applySearch()', function () {
        xit('should apply the search, setting the isFilterVisible Attribute on all children', function () {
            $scope.vm.searchTerm = 'node1';
            TeTreeController.applySearch();
            expect($scope.vm.model[0].isFilterVisible).toEqual(true);
            expect($scope.vm.model[0].nodes[0].isFilterVisible).toEqual(true);
            expect($scope.vm.model[0].nodes[1].isFilterVisible).toEqual(true);
            expect($scope.vm.model[1].isFilterVisible).toEqual(false);
        });
    });

    describe('clearSearch()', function () {
        xit('should clear search input and set isFilterVisible = true for all elements', function () {
            $scope.vm.searchTerm = 'node2';
            TeTreeController.applySearch();
            TeTreeController.clearSearch();
            expect($scope.vm.searchTerm).toEqual(null);
            expect($scope.vm.model[0].isFilterVisible).toEqual(true);
            expect($scope.vm.model[0].nodes[0].isFilterVisible).toEqual(true);
            expect($scope.vm.model[0].nodes[1].isFilterVisible).toEqual(true);
            expect($scope.vm.model[1].isFilterVisible).toEqual(true);
        });
    });

    describe('toggleSearchField()', function () {
        it('should change value on search fields', function () {
            var field = [
                {
                    enabled: true,
                    field: 'campaign',
                    position: 1,
                    title: 'Campaign'
                },
                {
                    enabled: true,
                    field: 'site',
                    position: 2,
                    title: 'Site'
                },
                {
                    enabled: true,
                    field: 'section',
                    position: 3,
                    title: 'Section'
                },
                {
                    enabled: true,
                    field: 'placement',
                    position: 4,
                    title: 'Placement'
                }
            ];

            TeTreeController.searchFields = field;

            TeTreeController.toggleSearchField(null, field[0]);
            expect(field[0].enabled).toEqual(false);
            expect(field[1].enabled).toEqual(true);
            expect(field[2].enabled).toEqual(true);
            expect(field[3].enabled).toEqual(true);
        });
    });
});
