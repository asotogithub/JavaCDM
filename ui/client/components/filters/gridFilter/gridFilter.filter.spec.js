'use strict';

describe('Filter: gridFilter', function () {
    var gridFilter;

    beforeEach(module('te.components'));

    // initialize a new instance of the filter before each test
    beforeEach(inject(function ($filter) {
        gridFilter = $filter('gridFilter');
    }));

    it('should filter tables given a search string', function () {
        var filteredTable,
            testObjects = [
                {
                    data: [
                        {
                            itemOne: 'aaa',
                            itemTwo: 'bbb'
                        },
                        {
                            itemOne: 'ccc',
                            itemTwo: ''
                        }
                    ],
                    searchFields: [
                        'itemOne',
                        'itemTwo'
                    ],
                    expectedLen: 1,
                    searchTerm: 'aaa'
                },
                {
                    data: [
                        {
                            name: 'aaa',
                            priority: '',
                            numberOfCreativesInGroup: ''
                        },
                        {
                            name: '',
                            priority: '',
                            numberOfCreativesInGroup: ''
                        }
                    ],
                    searchFields: [
                        'name',
                        'advertiserName',
                        'brandName'
                    ],
                    expectedLen: 1,
                    searchTerm: 'aaa'
                },
                {
                    data: [
                        {
                            name: 'aaa',
                            priority: '0',
                            numberOfCreativesInGroup: ''
                        },
                        {
                            name: '',
                            priority: '',
                            numberOfCreativesInGroup: ''
                        }
                    ],
                    searchFields: [
                        'name'
                    ],
                    expectedLen: 0,
                    searchTerm: '0'
                },
                {
                    data: [
                        {
                            name: '0',
                            priority: '',
                            numberOfCreativesInGroup: ''
                        },
                        {
                            name: '',
                            priority: '0',
                            numberOfCreativesInGroup: ''
                        }
                    ],
                    searchFields: [
                        'name'
                    ],
                    expectedLen: 1,
                    searchTerm: '0'
                },
                {
                    data: [
                        {
                            name: 'aaa',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: '',
                            brandName: '',
                            domain: 'aaa',
                            isActiveDisplay: ''
                        }
                    ],
                    searchFields: [
                        'name',
                        'advertiserName',
                        'brandName'
                    ],
                    expectedLen: 1,
                    searchTerm: 'aaa'
                },
                {
                    data: [
                        {
                            name: 'aaa',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: 'aaa',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        }
                    ],
                    searchFields: [
                        'name',
                        'advertiserName',
                        'brandName'
                    ],
                    expectedLen: 2,
                    searchTerm: 'aaa'
                },
                {
                    data: [
                        {
                            name: '',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: 'aaa'
                        },
                        {
                            name: '',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        }
                    ],
                    searchFields: [
                        'name',
                        'advertiserName',
                        'brandName'
                    ],
                    expectedLen: 0,
                    searchTerm: 'aaa'
                },
                {
                    data: [
                        {
                            name: 'Aaa',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: '',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        }
                    ],
                    searchFields: [
                        'name',
                        'advertiserName',
                        'brandName'
                    ],
                    expectedLen: 1,
                    searchTerm: 'aAa'
                }
            ];

        angular.forEach(testObjects, function (testObj) {
            filteredTable = gridFilter(testObj.data, {
                searchVal: testObj.searchTerm,
                searchFields: testObj.searchFields
            });
            expect(filteredTable.length).toBe(testObj.expectedLen);
        });
    });
});
