'use strict';

describe('Filter: valuesFilter', function () {
    var valuesFilter;

    beforeEach(module('te.components'));

    // initialize a new instance of the filter before each test
    beforeEach(inject(function ($filter) {
        valuesFilter = $filter('valuesFilter');
    }));

    it('should filter a table given a array of expected values on different fields', function () {
        var filteredTable,
            testObjects = [
                {
                    data: [
                        {
                            itemOne: 'aaa',
                            itemTwo: 'bbb'
                        },
                        {
                            itemOne: 'aaa',
                            itemTwo: 'ccc'
                        }
                    ],
                    filterValues: [
                        {
                            fieldName: 'itemOne',
                            values: [
                                'aaa'
                            ]
                        },
                        {
                            fieldName: 'itemTwo',
                            values: [
                                'bbb'
                            ]
                        }
                    ],
                    expectedLen: 1
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
                    filterValues: [
                        {
                            fieldName: 'name',
                            values: [
                                'aaa'
                            ]
                        }
                    ],
                    expectedLen: 1
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
                    filterValues: [
                        {
                            fieldName: 'name',
                            values: [
                                'bbb'
                            ]
                        }
                    ],
                    expectedLen: 0
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
                            priority: 'aaa',
                            numberOfCreativesInGroup: ''
                        }
                    ],
                    filterValues: [
                        {
                            fieldName: 'name',
                            values: [
                                'aaa'
                            ]
                        }
                    ],
                    expectedLen: 1
                },
                {
                    data: [
                        {
                            name: 'aaa',
                            advertiserName: '',
                            brandName: '',
                            domain: 'bbb',
                            isActiveDisplay: ''
                        },
                        {
                            name: 'aaa',
                            advertiserName: '',
                            brandName: '',
                            domain: 'ccc',
                            isActiveDisplay: ''
                        }
                    ],
                    filterValues: [
                        {
                            fieldName: 'name',
                            values: [
                                'aaa'
                            ]
                        },
                        {
                            fieldName: 'domain',
                            values: [
                                'bbb'
                            ]
                        }
                    ],
                    expectedLen: 1
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
                            brandName: 'aaa',
                            domain: '',
                            isActiveDisplay: ''
                        }
                    ],
                    filterValues: [
                        {
                            fieldName: 'name',
                            values: [
                                'aaa'
                            ]
                        }
                    ],
                    expectedLen: 0
                },
                {
                    data: [
                        {
                            name: '',
                            advertiserName: 'aaa',
                            brandName: 'bbb',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: 'ccc',
                            brandName: '',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: 'ccc',
                            brandName: 'zzz',
                            domain: '',
                            isActiveDisplay: ''
                        },
                        {
                            name: '',
                            advertiserName: 'ddd',
                            brandName: 'zzz',
                            domain: '',
                            isActiveDisplay: ''
                        }
                    ],
                    filterValues: [
                        {
                            fieldName: 'advertiserName',
                            values: [
                                'aaa',
                                'ccc'
                            ]
                        },
                        {
                            fieldName: 'brandName',
                            values: [
                                'bbb',
                                'zzz'
                            ]
                        }
                    ],
                    expectedLen: 2
                }
            ];

        angular.forEach(testObjects, function (testObj) {
            filteredTable = valuesFilter(testObj.data, testObj.filterValues);
            expect(filteredTable.length).toBe(testObj.expectedLen);
        });
    });
});
