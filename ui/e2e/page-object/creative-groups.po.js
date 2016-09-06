'use strict';

var creativeGroupsPage = function() {
  this.checkMarkClass = "fa-check";
  this.creativeGroupsCreativeList = element(by.id('creativesGroupCreativeList'));
  this.creativeGroupsHeader = element(by.id('title-page-creative-group'));
  this.creativeGroupsGrid = element(by.css('[data-model="vmCrtvGrpsTab.creativeGroups"]'));
  this.creativeGroupsRow = element.all(by.css('tr[data-ng-repeat$="$data"] [data-title-text="Name"]'));
  this.creativeTab = element(by.id('creativeGroupsTab'));
  this.columnHeader = element.all(by.binding('$column.title(this)'));
  this.columnNames = ["","Name","Priority","Cookie Targeting","Geo Targeting","Day Part Targeting","Group Weight","Default","Creative"];
  this.cookieTarget = element(by.id('doCookieTargeting'));
  this.cookieTargetRequiredMsg = element(by.id('cookieTargetRequired'));
  this.cookieTargetSwitch = element(by.css('#doCookieTargeting:first-of-type + span'));
  this.cookieTargetText = element(by.id('cookieTarget'));
  this.dataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
  this.dayPartTarget = element(by.id('doDaypartTargeting'));
  this.dayPartTargetSwitch = element(by.css('#doDaypartTargeting:first-of-type + span'));
  this.defaultGroup = element(by.id('defaultGroup'));
  this.defaultGroupSwitch = element(by.css('#defaultGroup:first-of-type + span'));
  this.detailsUrl = '/campaigns/id/[-+]?[0-9]*\.?[0-9]*/creative-groups/id/[-+]?[0-9]*\.?[0-9]*/details';
  this.frequencyCap = element(by.id('enableFrequencyCap'));
  this.frequencyCapInput = element(by.id('frequencyCap'));
  this.frequencyCapInvalidMsg = element(by.id('frequencyCapInvalid'));
  this.frequencyCapMaxMsg = element(by.id('frequencyCapInvalidMax'));
  this.frequencyCapRequiredMsg = element(by.id('frequencyCapRequired'));
  this.frequencyCapSwitch = element(by.css('#enableFrequencyCap:first-of-type + span'));
  this.frequencyCapWindowInput = element(by.id('frequencyCapWindow'));
  this.frequencyCapWindowInvalidMsg = element(by.id('frequencyCapWindowInvalid'));
  this.frequencyCapWindowMaxMsg = element(by.id('frequencyCapWindowInvalidMax'));
  this.frequencyCapWindowRequiredMsg = element(by.id('frequencyCapWindowRequired'));
  this.detailsCancelBtn = element(by.css('a[data-ng-click="vmDetails.close($event)"]'));

  this.detailsCancel = function() {
      browser.driver.wait(protractor.until.elementIsVisible(this.detailsCancelBtn));
      expect(this.detailsCancelBtn.isDisplayed()).toBe(true);
      this.detailsCancelBtn.click();
      var el = element(by.css('button[data-ng-click="yes()"]'));
      browser.wait(function () {
          return browser.isElementPresent(el);
      }).then(function () {
          expect(el.isDisplayed()).toBe(true);
          el.click();
      });
  }

  this.getDataByRowColumn = function(index, column) {
    var dataRow = this.dataRows.get(index);
    return dataRow.element(by.css('td[id^=creativeGroup][id$=' + column + ']'));
  }
  this.cgRowByName = function(creativeGroupName) {
      return element(by.cssContainingText('td[data-title-text="Name"]', creativeGroupName));
  }
  this.gridUrl = '/campaigns/id/[-+]?[0-9]*\.?[0-9]*/creative-groups';
  this.headerByName = function(header) {
    return element(by.cssContainingText('span', header));
  }
  this.nameField = element(by.css('input#creativeGroupName'));
  this.nameReadOnly = element(by.css('p#creativeGroupName'));
  this.nameRequiredMsg = element(by.id('creativeGroupNameRequired'));
  this.nameTooLongMsg = element(by.id('creativeGroupNameTooLong'));
  this.priority = element(by.id('enablePriority'));
  this.priorityInput = element(by.id('priority'));
  this.priorityInvalidMsg = element(by.id('priorityInvalid'));
  this.priorityMaxMsg = element(by.id('priorityInvalidMax'));
  this.priorityRequiredMsg = element(by.id('priorityRequired'));
  this.priorityLabel - element(by.id('enablePriorityLabel'));
  this.prioritySwitch = element(by.css('#enablePriority:first-of-type + span'));
  this.sortedText = function(header){
    return  this.textToSortWithoutDefault(header).then(function(trimmedTexts) {
       return trimmedTexts.sort(function(a, b) {
         return isNaN(a) || isNaN(b) ?
           a.toLowerCase().localeCompare(b.toLowerCase()) :
           parseFloat(a) - parseFloat(b);
       });
    });
  }
  this.textToSort = function(header){
    return  this.valuesByHeader(header).map(function(element){
      return element.getText();
    })
  }
  this.textToSortWithoutDefault = function(header){
    return  this.textToSort(header).then(function(textToSplice){
      var length = textToSplice.length;
      return textToSplice.splice(1,length);
    })
  }
  this.valuesByHeader = function(header) {
    return element.all(by.css('td[data-title-text="' + header + '"]'));
  }
  this.weightDist = element(by.id('enableGroupWeight'));
  this.weightDistInput = element(by.id('weight'));
  this.weightDistInvalidMsg = element(by.id('weightInvalid'));
  this.weightDistLabel = element(by.id('enableGroupWeightLabel'));
  this.weightDistRequired = element(by.id('weightRequired'));
  this.weightDistSwitch = element(by.css('#enableGroupWeight:first-of-type + span'));
  this.weightDistMaxMsg = element(by.id('weightInvalidMax'));
  this.xClass = "fa-times";

  //new for us5017
  this.geoTarget = element(by.id('doGeoTargeting'));
  this.geoTargetRequiredMsg = element(by.id('geoTargetRequired'));
  this.geoTargetSwitch = element(by.css('#doGeoTargeting:first-of-type + span'));
  this.countryTab = element(by.id("geoTargetCountryTab"));
  this.countryGrid = element(by.id('geoTargetCountryGrid'));
  this.countryGridRows = element.all(by.css('#geoTargetCountryGrid tr td[data-title-text="Country"]'));
  this.countrySearch = element(by.css('.geo-targeting .te-table-search'));
  this.stateTab = element(by.id('geoTargetStateTab'));
  this.dmaTab = element(by.id('geoTargetDmaTab'));
  this.zipTab = element(by.id('geoTargetZipTab'));
  this.cookieTab = element(by.id('cookieTargetingTab'));
  this.geoTab = element(by.id('geoTargetingTab'));
  this.dayPartTab = element(by.id('dayPartTargetingTab'));
  this.includeCountryBtn = element(by.id('geoCountryAntiTargetInclude'));
  this.excludeCountryBtn = element(by.id('geoCountryAntiTargetExclude'));
  this.countriesSelectAllBtn = this.countryGrid.element(by.css('input[data-ng-model="$table.allSelected"]:first-of-type + span'));
  this.countriesSummary = element(by.id('countriesSummary'));
  this.stateSummary = element(by.id('statesSummary'));
  this.dmasSummary = element(by.id('dmasSummary'));
  this.zipCodesSummary = element(by.id('zipCodesSummary'));
  this.countriesSummaryValues = element.all(by.css('#countriesSummary span.tag'));
  this.countriesSummaryByName  = function(country) {
      return element(by.cssContainingText('#countriesSummary span', country))
    }
  this.countriesSummaryRemoveByName  = function(country) {
    return this.countriesSummaryByName(country).element(by.css('span[data-role="remove"]'));
  }
  this.countriesSummaryValue = element(by.css('#countriesSummary span.tag'));
  this.itemCheckBox = element(by.id(''));
  this.getCheckboxByName = function(item) {
    return element(by.cssContainingText('tr[data-ng-repeat="geo in $data"] td[data-searchable="true"]', item));
  }
  this.getSummaryItemByName = function(category, item) {
      return element(by.cssContainingText('a', item));
  }
  this.getRemoveItemByName = function(category, item) {
      return element(by.cssContainingText('a', item)).element(by.css('span[ng-click="vm.removeGeo(country)"]'));
  }
  this.countriesTable = element(by.css('div[data-model="vm.countries"]'));
  this.statesTable = element(by.css('div[data-model="vm.states"]'));
  this.zipsTable = element(by.css('div[data-model="vm.zips"]'));
  this.countriesSearchInput = this.countriesTable.element(by.css('input[placeholder="Search…"]'));
  this.statesSearchInput = this.statesTable.element(by.css('input[placeholder="Search…"]'));
  this.zipsSearchInput = this.zipsTable.element(by.css('input[placeholder="Search…"]'));

  //new for us5242
  this.includeStateBtn = element(by.id('geoStateAntiTargetInclude'));
  this.excludeStateBtn = element(by.id('geoStateAntiTargetExclude'));
  this.stateGrid = element(by.id('geoTargetStateGrid'));
  this.stateGridRows = element.all(by.css('#geoTargetStateGrid tr td[data-title-text="State"]'));
  this.statesSelectAllBtn = this.stateGrid.element(by.css('input[data-ng-model="$table.allSelected"]:first-of-type + span'));
  this.statesSummaryValues = element.all(by.css('#statesSummary span.tag'));
  this.statesSummaryByName = function(state) {
    return element(by.cssContainingText('#statesSummary span', state))
  };
  this.statesSummaryRemoveByName = function(state) {
    return this.statesSummaryByName(state).element(by.css('span[data-role="remove"]'));
  };

  //new for us5243
  this.includeDmaBtn = element(by.id('geoDmaAntiTargetInclude'));
  this.excludeDmaBtn = element(by.id('geoDmaAntiTargetExclude'));
  this.dmaGrid = element(by.id('geoTargetDmaGrid'));
  this.dmaGridRows = element.all(by.css('#geoTargetDmaGrid tr td[data-title-text="DMA"]'));
  this.dmasTable = element(by.css('div[data-model="vm.dmas"]'));
  this.dmasSearchInput = this.dmasTable.element(by.css('input[placeholder="Search…"]'));
  this.dmasSelectAllBtn = this.dmaGrid.element(by.css('input[data-ng-model="$table.allSelected"]:first-of-type + span'));
  this.dmasSummaryValues = element.all(by.css('#dmasSummary span.tag'));
  this.dmasSummaryByName = function(dma) {
    return element(by.cssContainingText('#dmasSummary span', dma))
  };
  this.dmasSummaryRemoveByName = function(dma) {
    return this.dmasSummaryByName(dma).element(by.css('span[data-role="remove"]'));
  };

  //new for us5244
  this.includeZipBtn = element(by.id('geoZipAntiTargetInclude'));
  this.excludeZipBtn = element(by.id('geoZipAntiTargetExclude'));
  this.zipGrid = element(by.id('geoTargetZipGrid'));
  this.zipGridRows = element.all(by.css('#geoTargetZipGrid tr td[data-title-text="Zip Code"]'));
  this.zipsSummaryValues = element.all(by.css('#zipCodesSummary span.tag'));
  this.zipsSummaryByName = function(zip) {
    return element(by.cssContainingText('#zipCodesSummary span', zip))
  };
  this.zipsSummaryRemoveByName = function(zip) {
    return this.zipsSummaryByName(zip).element(by.css('span[data-role="remove"]'));
  };

  //cookie targeting tab
  this.builderTab = element(by.id('cookieTargetBuilderTab'));
  this.editorTab = element(by.id('cookieTargetEditorTab'));
  this.cookieBuilder = element(by.id('cookieBuilder'));
  this.allCookieDropdowns = element.all(by.css('[class="rule-filter-container"] select'));
  this.allCookieOperatorDropdowns = element.all(by.css('[class="rule-operator-container"] select'));
  this.allCookieValueInputs = element.all(by.css('[class="rule-value-container"] input'));
  this.allCookieListValues = element.all(by.css('[class="rule-value-container"]'));
  this.cookieBuilderAndBtn = element.all(by.cssContainingText('label', 'AND'));
  this.cookieBuilderOrBtn = element.all(by.cssContainingText('label', 'OR'));
  this.cookieBuilderAddRuleBtn = element.all(by.css('button[data-add="rule"]'));
  this.cookieBuilderAddGroupBtn = element.all(by.css('button[data-add="group"]'));
  this.getCookieOption = function(option) {
    return this.allCookieDropdowns.last().element(by.css('option[value="' + option + '"]'));
  }
  this.getOperatorOption = function(option) {
    return this.allCookieOperatorDropdowns.last().element(by.css('option[value="' + option + '"]'));
  }
  this.getValueOption = function(option) {
    return this.allCookieListValues.last().element(by.css('label input[value="' + option + '"]'));
  }
  this.cookieOperatorStringOptions = ["equal","not_equal","greater","greater_or_equal","less","less_or_equal","between","not_between","contain","not_contain","begins_with","ends_with","is_like","is_not_like","is_null","is_not_null","is_any_of","is_none_of","is_blank"];
  this.cookieOperatorNumberOptions = ["equal","not_equal","less","less_or_equal","greater","greater_or_equal","between","not_between","is_null","is_not_null","is_any_of","is_none_of"];
  this.allCookieRuleGroup = element.all(by.css('[class="rules-group-container"]'));
  this.cookieTargetingInvalid = element(by.id('cookieTargetingInvalid'));

  //new for day part targeting tab
  this.iabStandardsDayPartTarget = element(by.id('iabStandard'));
  this.customDayPartTarget = element(by.id('custom'));

  this.addCustomTimesBtn = element(by.id('addCustomOption'));

  this.startTimeHourUp = element(by.css('#startTime a[ng-click="incrementHours()"]'));
  this.startTimeMinuteDown = element(by.css('#startTime a[ng-click="decrementMinutes()"]'));
  this.startTimeAMPM = element(by.css('#daypartCustomStart button[ng-click="toggleMeridian()"]'));

  this.endTime = element(by.id('endTime'));
  this.endTimeHourUp = element(by.css('#endTime a[ng-click="incrementHours()"]'));
  this.endTimeMinuteDown = element(by.css('#endTime a[ng-click="incrementMinutes()"]'));
  this.endTimeAMPM = element(by.css('#daypartCustomEnd button[ng-click="toggleMeridian()"]'));
  this.endTimeHour = this.endTime.element(by.model('hours'));
  this.endTimeHourDown = this.endTime.element(by.css('a[ng-click="decrementHours()"]'));

  this.clearSelectedCustomDayPartTarget = element(by.id('clearSelectedCustomOptions'));
  this.clearAllSelectedDayPartTargets = element(by.id('clearAllCustomOptions'));

  this.daySelectTue = element(by.id('tue'));
  this.daySelectWed = element(by.id('wed'));
  this.daySelectThu = element(by.id('thu'));
  this.daySelectSun = element(by.id('sun'));
  this.timesSelected = element.all(by.css('#customOptions option'));
  this.daytime = element(by.id('daytime'));
  this.earlyMorning = element(by.id('earlyMorning'));
  this.evening = element(by.id('evening'));
  this.lateNight = element(by.id('lateNight'));
  this.weekends = element(by.id('weekends'));

  this.cookieBuilderWarning = element(by.css('.has-error'));
  this.listOfCreativeGroupBtn = element(by.id('listOfCreativeGroupBtn'));

  this.deleteCreativeBtn = element(by.id('deleteCreative'));
  this.deleteWarningPopup = element(by.css('div.modal div.modal-dialog div.modal-content'));
  this.deleteWarningPopupCancel = this.deleteWarningPopup.element(by.css('button.btn[data-ng-click="no()"]'));
  this.deleteWarningPopupDelete = this.deleteWarningPopup.element(by.css('button.btn[data-ng-click="yes()"]'));

  this.legendTable = element(by.id('legend'));
  this.creativeGroupsGrid = element(by.id('creativeGroupsGrid'));

  this.searchInputByGrid = function(parentEl){
    return parentEl.element(by.model('$table.searchTerm'));
  }

  this.searchInputCreativeGroups = this.searchInputByGrid(this.creativeGroupsGrid);
};

module.exports = new creativeGroupsPage();
