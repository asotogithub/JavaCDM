'use strict';

var Schedule = function() {
  var page = require('../page-object/utilities.po');

  this.gridByContext = function(parentEl){
    return parentEl.element(by.css('.table'));
  };
  this.selectAllByContext = function(parentEl){
    return parentEl.element(by.css('[data-ng-model="$table.allSelected"]:first-of-type + span'));
  };
  this.checkboxesByContext = function(parentEl){
    return parentEl.all(by.css('td div.checkbox.c-checkbox span.fa.fa-check'));
  };
  this.placementAssignmentInput = function(parentEl) {
      return parentEl.element(by.css('[data-te-table-select="vm.selectPlacements($selection)"] input.te-table-search'));
  };
  this.creativeAssignmentInput = function(parentEl) {
      return parentEl.element(by.css('[data-te-table-select="vm.selectCreatives($selection)"] input.te-table-search'));
  };
  this.cancelBtn = element(by.id('cancelButton'));
  this.closeBtn = element(by.css('.close'));
  this.columnNames = ["Site Detail","Plct Status","Associations","Weight","Flight Dates","CT URL"];
  this.sortableColumns = ["Site Detail"];
  this.scheduleDataRows = element.all(by.css('#scheduleGrid [role="row"][id^=row'));
  this.flyoutDataRows = element.all(by.css('#flyoutGrid [role="row"][id^=row'));
  this.rowCreative = element.all(by.css('.jqx-tree-grid-title .creative'));
  this.rowCreativeGroup = element.all(by.css('.jqx-tree-grid-title .creative-group'));
  this.rowExpand = element.all(by.css('#scheduleGrid .jqx-tree-grid-collapse-button'));
  this.rowCollapse = element.all(by.css('#scheduleGrid .jqx-tree-grid-expand-button'));
  this.rowPlacement = element.all(by.css('.placement'));
  this.domainFieldReadOnly = element(by.id('domain'));
  this.pendingAssignSection = element(by.id('pendingScheduleAssignments'));
  this.pendingAssignDelete = element(by.id('deleteButton'));
  this.pendingAssignGrid = this.gridByContext(this.pendingAssignSection);
  this.pendingAssignSelectAll = this.selectAllByContext(this.pendingAssignSection);
  this.availPlacementsSection = element(by.css('[data-model="vm.placements"]'));
  this.availPlacementsGrid = this.gridByContext(this.availPlacementsSection);
  this.availPlacementsColumnNames = ["Site","Placement","Section","Size"];
  this.availPlacementsSelectAll = this.selectAllByContext(this.availPlacementsSection);
  this.availPlacementsCheckboxes = this.checkboxesByContext(this.availPlacementsSection);
  this.availCreativesSection = element(by.css('[data-model="vm.creatives"]'));
  this.availCreativesGrid = this.gridByContext(this.availCreativesSection);
  this.availCreativesColumnNames = ["Group","Placement","Section","Size"];
  this.availCreativesSelectAll = this.selectAllByContext(this.availCreativesSection);
  this.availCreativesCheckboxes = this.checkboxesByContext(this.availCreativesSection);
  this.creativeAssignationModal = element(by.id('addGroupCreativeAssociations'));
  this.creativeAssignationLeftContainerRows = element.all(by.css('[data-model="inputModel"] tbody tr'));
  this.creativeAssignationRightContainerRows = element.all(by.css('[data-model="outputModel"] tbody tr'));
  this.gridSearchInput = element(by.css('input .te-table-search'));
  this.moveAllCreativeToInsertionList = element(by.css('[data-ng-click="vm.addToCreativeInsertionList()"]'));
  this.moveAllCreativeToRightBtn = element(by.css('[data-ng-click="addAllAssociation()"]'));
  this.removeCreativeInsertionBtn = element.all(by.css('[data-ng-click="vm.removeItems(obj)"]'));
  this.saveCreativeInsertionBtn = element(by.css('[data-ng-click="vm.saveAndClose()"]'));
  this.scheduleAssignmentModal = element(by.id('addScheduleAssignments'));
  this.scheduleAssignmentModalPlacementsInput = this.placementAssignmentInput(this.scheduleAssignmentModal);
  this.scheduleAssignmentModalCreativeInput = this.creativeAssignmentInput(this.scheduleAssignmentModal);
  this.scheduleAssignmentSave = this.scheduleAssignmentModal.element(by.id('saveButton'));
  this.scheduleGridPaging = element(by.css('#scheduleGrid .jqx-grid-pager'));
  this.scheduleTab = element(by.css('.schedule-tab'));
  this.scheduleGrid = element(by.id('scheduleGrid'));
  this.flyoutGrid = element(by.id('flyoutGrid'));
  this.dismissDuplicateDialog = element(by.id('dismissButton'));
  this.scheduleViewSearchOptionBtn = element(by.css('#scheduleGrid .fa.fa-search'));
  this.flyoutSearchOptionBtn = element(by.css('#flyoutGrid .fa.fa-search'));
  this.searchClearByGrid = function(parentEl){
    return parentEl.element(by.css('span.form-control-feedback[data-ng-click="vm.clearSearch()"]'));
  }
  this.searchInputByGrid = function(parentEl){
    return parentEl.element(by.model('vm.searchTerm'));
  }
  this.firstRowByGrid = function(parentEl){
    return parentEl.element(by.css('[role="row"][id^=row'));
  }
  this.searchButtonSchedule = element(by.css('#scheduleGrid .btn.customTreeGridSearchButton'));
  this.searchClearSchedule = this.searchClearByGrid(this.scheduleGrid);
  this.searchInputSchedule = this.searchInputByGrid(this.scheduleGrid);
  this.searchClearFlyout = this.searchClearByGrid(this.flyoutGrid);
  this.searchInputFlyout = this.searchInputByGrid(this.flyoutGrid);
  this.firstRowSchedule = this.firstRowByGrid(this.scheduleGrid);
  this.lastCtrUrlSchedule = element.all(by.css('#scheduleGrid table.jqx-grid-table tr td.jqx-cell.jqx-item')).last();
  this.searchClear = element(by.css('span.form-control-feedback[data-ng-click="vm.clearSearch()"]'));
  this.searchInput = element.all(by.model('vm.searchTerm'));
  this.sortedText = function(index){
    return  this.textToSort(index).then(function(trimmedTexts) {
       return trimmedTexts.sort(function(a, b) {
         return isNaN(a) || isNaN(b) ?
           a.toLowerCase().localeCompare(b.toLowerCase()) :
           parseFloat(a) - parseFloat(b);
       });
    });
  }
  this.textToSort = function(index){
    return  this.valuesByIndex(index).map(function(element){
      return element.getText();
    })
  }
  this.valuesByIndex = function(index) {
    var child = index + 1;
    return element.all(by.css('[role="row"] td:nth-child(' + child + ')'));
  }
  this.pendingAssignTextByHeader = function(index){
    return  this.pendingAssignValuesByHeader(index).map(function(element){
      return element.getText();
    })
  }
  this.pendingAssignValuesByHeader = function(header) {
    return element.all(by.css('#pendingScheduleAssignments td[data-title-text="' + header + '"]'));
  }
  this.headerByNameScheduleGrid = function(header) {
    return this.scheduleGrid.element(by.cssContainingText('span', header));
  }
  this.headerByNameFlyoutGrid = function(header) {
    return this.flyoutGrid.element(by.cssContainingText('span', header));
  }
  this.changePivotDropdown = function(pivot) {
      return element(by.cssContainingText('option', pivot)).click();
  }
  this.checkUncheckScheduleSearchOptions = function(searchOption) {
      browser.actions().mouseMove(this.scheduleViewSearchOptionBtn).perform();
      this.scheduleViewSearchOptionBtn.click();
      element.all(by.cssContainingText('#scheduleGrid .dropdown-menu label',searchOption)).each(function(foundElem){
          foundElem.getText().then(function (text) {
              if(text === searchOption) {
                  foundElem.click();
              }
          });
      });
      browser.actions().mouseMove(this.scheduleViewSearchOptionBtn).perform();
      return this.scheduleViewSearchOptionBtn.click();
  }
  this.checkUncheckFlyoutSearchOptions = function(searchOption) {
    this.flyoutSearchOptionBtn.click();
    element.all(by.cssContainingText('#flyoutGrid .dropdown-menu label',searchOption)).each(function(foundElem){
      foundElem.getText().then(function (text) {
        if(text === searchOption) {
          foundElem.click();
        }
      });
    });
    return this.scheduleViewSearchOptionBtn.click();
  }
  this.bulkDropdown = element(by.id('bulk-dropdown'));
  this.bulkDropdownList = element(by.id('bulk-dropdown-list'));
  this.importAll = element(by.id('importAll'));
  this.exportAll = element(by.id('exportAll'));
  this.downloadSuccessful = element(by.id('downloadSuccessful'));

  //Import
  this.cancelImportBtn = element(by.id('cancel-import'));
  this.downloadInvalidFile = element(by.id('downloadInvalidFile'));
  this.processImportBtn = element(by.id('process-import'));
  this.selectFileBtn = element(by.id('select-file'));
  this.upload = element(by.css('input[type="file"]'));

  //Import Errors
  this.cancelImportErrorsBtn = element(by.id('cancel-import-errors'));
  this.completeImportBtn = element(by.id('complete-import'));
  this.exportIssuesBtn = element(by.id('export-issues'));
  this.exportIssuesSuccessful = element(by.id('exportIssuesSuccessful'));
  this.issuesRows = element.all(by.css('#issuesGrid tbody tr'));
  this.rowsErrors = element(by.id('rowsErrors'));
  this.rowsUpdate = element(by.id('rowsUpdate'));
  this.totalRowsImport = element(by.id('totalRowsImport'));

  //Flyout
  this.modalTitle = element(by.css('.modal-title'));
  this.accordian = element(by.id('accordion'));
  this.expandLeft = element(by.id('expandoLeft'));
  this.expandRight = element(by.id('expandoRight'));
  this.modalClose = element(by.id('expandoClose'));
  this.sectionOne = element(by.css('#headingOne a'));
  this.sectionOneData = element(by.id('collapseOne'));
  this.sectionTwo = element(by.css('#headingTwo a'));
  this.sectionTwoData = element(by.id('collapseTwo'));
  this.sectionThree = element(by.css('#headingThree a'));
  this.sectionThreeData = element(by.id('collapseThree'));
  this.sectionTagAssoc = element(by.css('#headingTagAssociations a'));
  this.sectionTagAssocData = element(by.id('collapseTagAssociations'));
  this.tagAssociationsPanel = element(by.id('tagAssociationsPanel'));
  this.flyoutAddCreativesByGroupBtn = element(by.css('[data-ng-click="vm.showAddGroupCreativeDialog()"]'));
  this.flyoutAddCreativesBySiteBtn = element(by.css('[data-ng-click="vm.showAddScheduleDialog()"]'));
  this.flyoutCancelBtn = element(by.css('[data-ng-click="vm.close()"]'));
  this.flyoutCreativeInsertionEditWeight = element(by.css('td .jqx-input-content'));
  this.flyoutCreativeInsertionEditCT = element(by.css('td .jqx-widget-content'));
  this.flyoutDelete = this.flyoutGrid.element(by.css('[data-ng-click="vm.hierarchicalRemove()"]'));
  this.flyoutCheckboxes = element.all(by.css('[class="customTreeGridCheckbox"]'));
  this.flyoutRowExpand = element.all(by.css('#flyoutGrid .jqx-tree-grid-collapse-button'));
  this.flyoutGridCells = element.all(by.css('#flyoutGrid [role="gridcell"]'));
  this.flyoutLastCtrUrl = element.all(by.css('#flyoutGrid table.jqx-grid-table tr td.jqx-cell.jqx-item')).last();
  this.flyoutLegend = element(by.css('.legend-padding'));
  this.flyoutSave = element(by.id('saveButton'));
  this.flyoutValidationMessage = element(by.css('.jqx-grid-validation-bootstrap'));
  this.trafficBtn = element(by.id('traffic'));
  this.trafficConfirmBtn = element(by.css('[data-ng-click="vm.traffic()"]'));
  this.trafficModalWindow = element(by.id('trafficModalWindow'));
  this.trafficDialogCancel = element(by.id('dialog-cancel'));
  this.trafficDialogOkButton = element(by.css('.modal-content button[data-ng-click="yes()"]'));
  this.trafficSelectDomainsList = element(by.id('selectDomains'));
  this.trafficDomainSelection = element(by.id('domainSelection'));
  this.internalList = element.all(by.repeater('contact in vmCampaignReview.contacts'));
  this.checkBoxesList =  element.all(by.css('input[id^="usrName"]'));
  this.checkBoxesListText = element.all(by.binding('contact.realName'));
  this.domainList = element(by.id('selectDomains')).all(by.tagName('option'));
  this.traffickingContactList = element(by.id('traffickingContactsSite'));
  this.trafficSiteContactTable = element(by.id('trafficSiteContact'));
  this.trafficSiteContactMainCheckbox = element(by.model('vmSiteTrafficContacts.allSelected'));
  this.trafficSiteContactCheckboxesList =  element.all(by.css('input[id^="trafficContact"]'));
  this.btnModalYes = element(by.id('modalYes'));
  this.btnModalNo = element(by.id('modalNo'));
  this.flyoutPaging = element(by.css('#scheduleInsertionsContainer .jqx-grid-pager'));

  //Edit click-through popup
  this.editCTUrl = element(by.id('editCTUrlPopup'));
  this.editCTUrlAddBtn = element(by.css('[data-ng-click="vm.addCTUrl()"]'));
  this.editCTUrlRemoveBtn = element.all(by.css('[data-ng-click="vm.removeCTUrl($index)"]')).last();
  this.clickthroughs = element.all(by.repeater('item in vm.clickthroughs'));
  this.newCTUrlField = element(by.id('2'));
  this.editCTUrlSaveBtn = element(by.css('[data-ng-click="vm.save()"]'));

  this.flyoutBulkEditBtn = this.flyoutGrid.element(by.css('[data-ng-click="vm.showBulkEditOptions()"]'));
  this.flyoutApplytoBtn = this.flyoutGrid.element(by.css('[data-ng-click="vmEdit.applyTo()"]'));

  this.ctUrl = element(by.id('ctUrl'));
  this.inputStartDate = element(by.id('inputstartDate'));
  this.inputEndDate = element(by.id('inputendDate'));
  this.groupWeight = element(by.id('groupWeight'));
  this.creativeWeigth = element(by.css('#weight'));
  this.trafficDoNotShowAgain =  element(by.css('[class="fa fa-check margin-left0"]'));
  this.plusBtn =  element(by.css('[class="btn btn-primary"]'));

  //Flyout filters
  this.flyoutFilterBtn = element(by.id('filterOptions'));
  this.flyoutFilterDropdown = function(index) {
    return element(by.id('scheduleInsertionsContainer')).all(by.css('.multiselect-parent')).get(index);
  };
  this.flyoutFilterDropdownOption = function(filterIndex, optionIndex) {
    return this.flyoutFilterDropdown(filterIndex).all(by.css('li.multicheckbox-option a')).get(optionIndex);
  };
  this.flyoutFilterLabel = function(filterIndex) {
    return this.flyoutFilterDropdown(filterIndex).element(by.css('div.btn.dropdown-toggle'));
  };

  //Flyout Tracking Tag Associations
  this.tagAssociationsGridRows = this.sectionTagAssocData.all(by.css('div.te-table tr.actionable-row'));
  this.htmlContentBtn = element(by.id('htmlContentBtn'));
  this.htmlSecureContentBtn = element(by.id('htmlSecureContentBtn'));
  this.getTrackingTagButtonByRowIndex = function(index, buttonName){
    return element(by.repeater('trackingTag in $data').row(index)).element(by.id(buttonName));
  };
  this.popover = element(by.css('.popover'));
  this.deleteTagAssociationButton = element(by.css('[data-te-table-btn-click="vmTagAssociations.deleteAssociations($selection)"]'));
  this.cancelDialogButton = element(by.css('.modal-content button[data-ng-click="no()"]'));
  this.deleteDialogButton = element(by.css('.modal-content button[data-ng-click="yes()"]'));
};

module.exports = new Schedule();
