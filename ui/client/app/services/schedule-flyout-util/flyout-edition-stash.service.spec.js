'use strict';

describe('Service:FlyoutEditionStashService', function () {
    var FlyoutEditionStashService,
        creative1Checked = {
            checked: true,
            clickThroughUrl: 'https://www.google.com',
            clickthroughs: undefined,
            creativeGroupId: 4,
            creativeId: 5,
            field: 'schedule',
            flightDateEnd: '01/01/2016',
            flightDateStart: '02/02/2016',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 55
        },
        creative1Hash = '8cb2237d0679ca88db6464eac60da96345513964',
        creative1Unchecked = {
            checked: false,
            clickThroughUrl: 'https://www.google.com',
            clickthroughs: undefined,
            creativeGroupId: 4,
            creativeId: 5,
            field: 'schedule',
            flightDateEnd: '01/01/2016',
            flightDateStart: '02/02/2016',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 55
        },
        creative2Checked = {
            checked: true,
            clickThroughUrl: 'https://www.trueffect.com',
            clickthroughs: undefined,
            creativeGroupId: 6,
            creativeId: 7,
            field: 'schedule',
            flightDateEnd: '03/03/2017',
            flightDateStart: '04/04/2017',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 77
        },
        creative2Hash = '21d67ffaa881b4838488942459b09be543901d46',
        creative2Unchecked = {
            checked: false,
            clickThroughUrl: 'https://www.trueffect.com',
            clickthroughs: undefined,
            creativeGroupId: 6,
            creativeId: 7,
            field: 'schedule',
            flightDateEnd: '03/03/2017',
            flightDateStart: '04/04/2017',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 77
        },
        creativeExtraProperties = {
            aaa: 'hello',
            checked: true,
            clickThroughUrl: 'https://www.google.com',
            clickthroughs: undefined,
            creativeGroupId: 4,
            creativeId: 5,
            field: 'schedule',
            flightDateEnd: '01/01/2016',
            flightDateStart: '02/02/2016',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 55,
            zzzz: 9000
        },
        group1Checked = {
            checked: true,
            creativeGroupId: 4,
            field: 'group',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 44
        },
        group1Hash = '7110eda4d09e062aa5e4a390b0a572ac0d2c0220',
        group1Unchecked = {
            checked: false,
            creativeGroupId: 4,
            field: 'group',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 44
        },
        group2Checked = {
            checked: true,
            creativeGroupId: 6,
            field: 'group',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 66
        },
        group2Hash = '229be39e04f960e46d8a64cadc8b4534e6bfc364',
        group2Unchecked = {
            checked: false,
            creativeGroupId: 6,
            field: 'group',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2,
            weight: 66
        },
        placementChecked = {
            checked: true,
            field: 'placement',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2
        },
        placementUnchecked = {
            checked: false,
            field: 'placement',
            placementId: 3,
            siteId: 1,
            siteSectionId: 2
        },
        sectionChecked = {
            checked: true,
            field: 'section',
            siteId: 1,
            siteSectionId: 2
        },
        sectionUnchecked = {
            checked: false,
            field: 'section',
            siteId: 1,
            siteSectionId: 2
        },
        siteChecked = {
            checked: true,
            field: 'site',
            siteId: 1
        },
        siteUnchecked = {
            checked: false,
            field: 'site',
            siteId: 1
        };

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_FlyoutEditionStashService_) {
        FlyoutEditionStashService = _FlyoutEditionStashService_;
    }));

    describe('save()', function () {
        it('should save the edited values of a flyout row', function () {
            FlyoutEditionStashService.save(creative1Checked);
            expect(FlyoutEditionStashService.getCreatives()[creative1Hash]).toEqual(creative1Checked);
        });

        it('should not save an empty object', function () {
            FlyoutEditionStashService.save({});
            expect(Object.keys(FlyoutEditionStashService.getCreatives()).length).toEqual(0);
        });

        it('should not save unwanted properties on the object', function () {
            FlyoutEditionStashService.save(creativeExtraProperties);
            expect(FlyoutEditionStashService.getCreatives()[creative1Hash]).toEqual(creative1Checked);
        });

        it('should update the edited values of a previously saved flyout row', function () {
            FlyoutEditionStashService.save(creative2Checked);
            expect(FlyoutEditionStashService.getCreatives()[creative2Hash]).toEqual(creative2Checked);
            FlyoutEditionStashService.save(creative2Unchecked);
            expect(FlyoutEditionStashService.getCreatives()[creative2Hash]).toEqual(creative2Unchecked);
        });
    });

    describe('load()', function () {
        it('should update a site with the edited values', function () {
            var siteRow = angular.copy(siteUnchecked);

            FlyoutEditionStashService.save(siteChecked);
            FlyoutEditionStashService.load(siteRow);
            expect(siteRow).toEqual(siteChecked);
        });

        it('should update a section with the edited values', function () {
            var sectionRow = angular.copy(sectionChecked);

            FlyoutEditionStashService.save(sectionUnchecked);
            FlyoutEditionStashService.load(sectionRow);
            expect(sectionRow).toEqual(sectionUnchecked);
        });

        it('should update a placement with the edited values', function () {
            var placementRow = angular.copy(placementChecked);

            FlyoutEditionStashService.save(placementChecked);
            FlyoutEditionStashService.load(placementRow);
            expect(placementRow).toEqual(placementChecked);
        });

        it('should update a group with the edited values', function () {
            var groupRow = angular.copy(group1Unchecked);

            FlyoutEditionStashService.save(group1Unchecked);
            FlyoutEditionStashService.load(groupRow);
            expect(groupRow).toEqual(group1Unchecked);
        });

        it('should update a group weight when the same group under a different site was edited', function () {
            var editedGroup = angular.copy(group1Checked),
                expectedResult = angular.copy(group2Checked),
                notSavedGroup = angular.copy(group2Unchecked);

            editedGroup.creativeGroupId = group2Unchecked.creativeGroupId;
            editedGroup.weight = 99;
            expectedResult.weight = 99;
            FlyoutEditionStashService.save(editedGroup);
            FlyoutEditionStashService.load(notSavedGroup);
            expect(notSavedGroup).toEqual(expectedResult);
        });

        it('should update a creative with the edited values', function () {
            var creativeRow = angular.copy(creative1Checked);

            FlyoutEditionStashService.save(creative1Unchecked);
            FlyoutEditionStashService.load(creativeRow);
            expect(creativeRow).toEqual(creative1Unchecked);
        });
    });

    describe('bulkLoadGroups()', function () {
        it('should update a list of groups with the edited values', function () {
            var groupList = [angular.copy(group1Unchecked), angular.copy(group2Unchecked)];

            FlyoutEditionStashService.save(group1Checked);
            FlyoutEditionStashService.save(group2Unchecked);
            FlyoutEditionStashService.bulkLoadGroups(groupList);
            expect(groupList).toEqual([group1Checked, group2Unchecked]);
        });
    });

    describe('bulkLoadCreatives()', function () {
        it('should update a list of creatives with the edited values', function () {
            var creativeList = [angular.copy(creative1Unchecked), angular.copy(creative2Unchecked)];

            FlyoutEditionStashService.save(creative1Checked);
            FlyoutEditionStashService.save(creative2Checked);
            FlyoutEditionStashService.bulkLoadCreatives(creativeList);
            expect(creativeList).toEqual([creative1Checked, creative2Checked]);
        });
    });

    describe('getCheckedRows()', function () {
        it('should return all the checked saved rows', function () {
            var expectedResult = [sectionChecked, group1Checked, creative2Checked];

            FlyoutEditionStashService.save(siteUnchecked);
            FlyoutEditionStashService.save(sectionChecked);
            FlyoutEditionStashService.save(placementUnchecked);
            FlyoutEditionStashService.save(group1Checked);
            FlyoutEditionStashService.save(group2Unchecked);
            FlyoutEditionStashService.save(creative1Unchecked);
            FlyoutEditionStashService.save(creative2Checked);
            expect(FlyoutEditionStashService.getCheckedRows()).toEqual(expectedResult);
        });
    });

    describe('getGroups()', function () {
        it('should return all the saved groups data', function () {
            var expectedResult = {};

            expectedResult[group1Hash] = group1Checked;
            expectedResult[group2Hash] = group2Unchecked;
            FlyoutEditionStashService.save(group1Checked);
            FlyoutEditionStashService.save(group2Unchecked);
            expect(FlyoutEditionStashService.getGroups()).toEqual(expectedResult);
        });
    });

    describe('getCreatives()', function () {
        it('should return all the saved creatives data', function () {
            var expectedResult = {};

            expectedResult[creative1Hash] = creative1Unchecked;
            expectedResult[creative2Hash] = creative2Checked;
            FlyoutEditionStashService.save(creative1Unchecked);
            FlyoutEditionStashService.save(creative2Checked);
            expect(FlyoutEditionStashService.getCreatives()).toEqual(expectedResult);
        });
    });

    describe('uncheckAll()', function () {
        it('should set all saved rows to unchecked', function () {
            FlyoutEditionStashService.save(siteChecked);
            FlyoutEditionStashService.save(sectionUnchecked);
            FlyoutEditionStashService.save(placementChecked);
            FlyoutEditionStashService.save(group1Unchecked);
            FlyoutEditionStashService.save(group2Checked);
            FlyoutEditionStashService.save(creative1Checked);
            FlyoutEditionStashService.save(creative2Unchecked);
            expect(FlyoutEditionStashService.getCheckedRows().length).toEqual(4);
            FlyoutEditionStashService.uncheckAll();
            expect(FlyoutEditionStashService.getCheckedRows().length).toEqual(0);
        });
    });

    describe('clear()', function () {
        it('should clear all saved data', function () {
            FlyoutEditionStashService.save(siteUnchecked);
            FlyoutEditionStashService.save(sectionChecked);
            FlyoutEditionStashService.save(placementUnchecked);
            FlyoutEditionStashService.save(group1Checked);
            FlyoutEditionStashService.save(group2Unchecked);
            FlyoutEditionStashService.save(creative1Unchecked);
            FlyoutEditionStashService.save(creative2Checked);
            expect(Object.keys(FlyoutEditionStashService.getGroups()).length).toEqual(2);
            expect(Object.keys(FlyoutEditionStashService.getCreatives()).length).toEqual(2);
            FlyoutEditionStashService.clear();
            expect(Object.keys(FlyoutEditionStashService.getGroups()).length).toEqual(0);
            expect(Object.keys(FlyoutEditionStashService.getCreatives()).length).toEqual(0);
        });
    });

    describe('buildKey()', function () {
        it('should build a unique id using sha1 to generate a hashed value', function () {
            var row1 = angular.copy(creative1Checked),
                row2 = angular.copy(creative2Checked),
                row3 = angular.copy(creative2Checked);

            row3.creativeId = 8;
            row1.uuid = FlyoutEditionStashService.buildKey(row1);
            row2.uuid = FlyoutEditionStashService.buildKey(row2);
            row1.uuid = FlyoutEditionStashService.buildKey(row3);
            expect(row1.uuid).toNotEqual(row2.uuid);
            expect(row2.uuid).toNotEqual(row3.uuid);
            expect(row1.uuid).toNotEqual(row3.uuid);
        });

        it('should support large input ids and generate a unique id', function () {
            var row1 = angular.copy(creative1Checked),
                row2 = angular.copy(creative2Checked),
                row3;

            row1.siteId = 1234567890;
            row1.siteSectionId = 9876543210;
            row1.placementId = 6123457895;
            row1.creativeGroupId = 1234159489;
            row1.creativeId = 1764894289;
            row2.siteId = 1591594454;
            row2.siteSectionId = 5491955491;
            row2.placementId = 4916823485;
            row2.creativeGroupId = 5549137564;
            row2.creativeId = 8151069735;
            row3 = angular.copy(row2);
            row3.creativeId = 3334599752;
            row1.uuid = FlyoutEditionStashService.buildKey(row1);
            row2.uuid = FlyoutEditionStashService.buildKey(row2);
            row1.uuid = FlyoutEditionStashService.buildKey(row3);
            expect(row1.uuid).toNotEqual(row2.uuid);
            expect(row2.uuid).toNotEqual(row3.uuid);
            expect(row1.uuid).toNotEqual(row3.uuid);
        });
    });
});
