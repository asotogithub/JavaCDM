(function () {
    'use strict';

    angular
        .module('uiApp')
        .factory('SMEventsFilterService', SMEventsFilterService);

    SMEventsFilterService.$inject = [
        'Utils',
        'lodash'
    ];

    function SMEventsFilterService(
        Utils,
        lodash) {
        var dataMap = [],
            service = {
                loadFilters: loadFilters,
                updateGroupFilter: updateGroupFilter,
                updateTagTypeFilter: updateTagTypeFilter
            };

        return service;

        function loadFilters(model, filterOptions, filterValues) {
            var eventTypeList = [],
                groupList = [],
                tagTypeList = [];

            lodash.forEach(model, function (value) {
                mappingFilters(value);

                if (lodash.indexOf(eventTypeList, value.eventTypeName) < 0) {
                    eventTypeList.push(value.eventTypeName);
                }

                if (lodash.indexOf(groupList, value.groupName) < 0) {
                    groupList.push(value.groupName);
                }

                if (lodash.indexOf(tagTypeList, value.tagTypeName) < 0) {
                    tagTypeList.push(value.tagTypeName);
                }
            });

            dataMap = lodash.mapKeys(dataMap, function (value, key) {
                return key;
            });

            filterOptions[0].value = lodash.clone(lodash.sortBy(eventTypeList), true);
            filterOptions[1].value = lodash.clone(lodash.sortBy(groupList), true);
            filterOptions[2].value = lodash.clone(lodash.sortBy(tagTypeList), true);

            updateFilters(filterValues, eventTypeList, groupList, tagTypeList);
        }

        function mappingFilters(event) {
            var dataEventType = dataMap[event.eventTypeName],
                dataGroup = {};

            if (Utils.isUndefinedOrNull(dataEventType)) {
                dataGroup[event.groupName] = [event.tagTypeName];
                dataMap[event.eventTypeName] = dataGroup;
            }
            else {
                dataGroup = dataEventType[event.groupName];

                if (Utils.isUndefinedOrNull(dataGroup)) {
                    dataEventType[event.groupName] = [event.tagTypeName];
                }
                else if (lodash.indexOf(dataGroup, event.tagTypeName) < 0) {
                    dataGroup.push(event.tagTypeName);
                }
            }
        }

        function updateFilters(filterValues, eventTypeList, groupList, tagTypeList) {
            filterValues[0].values = eventTypeList;
            filterValues[1].values = groupList;
            filterValues[2].values = tagTypeList;
        }

        function updateGroupFilter(filterOptions, newValue, oldValue) {
            var groupSelected = [],
                groupOptions = [],
                newValueSelect = lodash.difference(newValue[0].values, oldValue),
                selectAux;

            lodash.forEach(dataMap, function (eventTypeValue, eventTypeId) {
                if (lodash.indexOf(newValueSelect, eventTypeId) >= 0) {
                    lodash.forEach(eventTypeValue, function (groupValue, groupId) {
                        if (lodash.indexOf(groupSelected, groupId) < 0) {
                            groupSelected.push(groupId);
                        }
                    });
                }
            });

            lodash.forEach(dataMap, function (eventTypeValue, eventTypeId) {
                if (lodash.indexOf(newValue[0].values, eventTypeId) >= 0) {
                    lodash.forEach(eventTypeValue, function (groupValue, groupId) {
                        if (lodash.indexOf(groupOptions, groupId) < 0) {
                            groupOptions.push(groupId);
                        }
                    });
                }
            });

            selectAux = lodash.intersection(groupOptions, newValue[1].values);

            lodash.forEach(groupSelected, function (value) {
                if (lodash.indexOf(selectAux, value) < 0) {
                    selectAux.push(value);
                }
            });

            newValue[1].values = selectAux;
            filterOptions[1].value = lodash.clone(lodash.sortBy(groupOptions), true);

            updateTagTypeFilter(filterOptions, newValue);
        }

        function updateTagTypeFilter(filterOptions, filterValues) {
            var tagType = [],
                newValue;

            lodash.forEach(dataMap, function (eventTypeValue, eventTypeId) {
                if (lodash.indexOf(filterValues[0].values, eventTypeId) >= 0) {
                    lodash.forEach(eventTypeValue, function (groupValue, groupId) {
                        if (lodash.indexOf(filterValues[1].values, groupId) >= 0) {
                            tagType = lodash.union(tagType, groupValue);
                        }
                    });
                }
            });

            newValue = lodash.difference(tagType, filterOptions[2].value);

            lodash.forEach(newValue, function (value) {
                filterValues[2].values.push(value);
            });

            filterValues[2].values = lodash.intersection(tagType, filterValues[2].values);
            filterOptions[2].value = lodash.clone(lodash.sortBy(tagType), true);
        }
    }
})();

