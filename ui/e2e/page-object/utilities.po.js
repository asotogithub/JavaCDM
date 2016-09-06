'use strict';

var utilities = function () {
    var MAX_CHECK_ATTEMPTS = 3;

    this.btnModalNo = element(by.id('modalNo'));
    this.btnModalYes = element(by.id('modalYes'));
    this.cancelBtn = element(by.css('a[data-ng-click="vm.cancel($event)"]'));
    this.closeBtn = element(by.css('a[data-ng-click="vm.close($event)"]'));
    this.columnHeaders = element.all(by.binding('$column.title(this)'));
    this.closeWarningMessage = function () {
        var that = this;

        this.btnModalYes.isPresent().then(function (isPresent) {
            if (isPresent) {
                expect(that.btnModalNo.isDisplayed()).toBeTruthy();
                that.btnModalNo.click();
            }
        });
    };
    this.creativeGroupsGrid = element(by.css('[data-model="vm.creativeGroups"]'));
    this.dataRows = element.all(by.css('tr[data-ng-repeat$="$data"]'));
    this.deleteBtn = element(by.id('deleteButton'));
    this.errorMsg = element(by.id('errorMessageText'));
    this.getAllDataByColumn = function (column) {
        return element.all(by.css('td[data-title-text="' + column + '"]')).getText();
    };
    this.getDataByRowColumn = function (index, column) {
        var dataRow = this.dataRows.get(index);
        return dataRow.element(by.css('td[data-title-text="' + column + '"]')).getText();
    };
    this.growlMsg = function (header) {
        return element(by.css('.growl-message'));
    };
    this.headerByName = function (header, nameGrid) {
        if (nameGrid) {
            return element(by.css(nameGrid)).element(by.cssContainingText('span', header));
        }
        else {
            return element(by.cssContainingText('span', header));

        }
    };
    this.requiredMsg = element(by.id('requiredMsg'));
    this.saveBtn = element(by.id('saveButton'));
    this.saveConfirmation = element(by.cssContainingText('div', 'The operation was completed successfully.'));
    this.searchBtn = element(by.css('button > .fa-search'));
    this.searchDropdown = element(by.css('.te-table-search .dropdown-menu'));
    this.searchInput = element(by.css('input.te-table-search'));
    this.searchClear = element(by.css('span.form-control-feedback[data-ng-click="$table.clearSearch()"]'));
    this.sortedText = function (header) {
        return  this.textToSort(header).then(function (trimmedTexts) {
            return trimmedTexts.sort(function (a, b) {
                return isNaN(a) || isNaN(b) ?
                    a.toLowerCase().localeCompare(b.toLowerCase()) :
                    parseFloat(a) - parseFloat(b);
            });
        });
    };
    this.textToSort = function (header) {
        return  this.valuesByHeader(header).map(function (element) {
            return element.getText();
        })
    };
    this.valuesByHeader = function (header) {
        return element.all(by.css('td[data-title-text="' + header + '"]'));
    };

    this.sortValuesByBinding = function (bindingKey) {
        return  this.getValuesByBinding(bindingKey).then(function (trimmedTexts) {
            return trimmedTexts;
        });
    };

    this.getValuesByBinding = function (bindingKey) {
        return this.valuesByBinding(bindingKey).map(function (element) {
            return element.getText();
        })
    };

    this.valuesByBinding = function (bindingKey) {
        return element.all(by.binding(bindingKey));
    };

    this.selectDropdown = function(dropDown, option) {
        return dropDown.element(by.css('option[label="' + option + '"]'));
    };

    /**
     * Workaround for MAC browsers not clearing masked numeric input properly
     * @param element The input element that will be cleared.
     */
    this.clearInput = function (element) {
        return element.getAttribute('value').then(function (text) {
            var backspaceSeries = '',
                textLength = text.length;

            for (var i = 0; i < textLength; i++) {
                backspaceSeries += protractor.Key.BACK_SPACE;
            }

            return element.sendKeys(backspaceSeries);
        })
    };

    /**
     * This function allows check( mark as selected) a given checkbox.
     * @param checkbox the checkbox that will be marked as selected.
     */
    this.ensureCheck = function (checkbox) {
        ensureCheckAux(checkbox, 0);
    };

    /**
     * This is an overloaded function of 'ensureCheck'
     * @param checkbox
     * @param i
     */
    function ensureCheckAux(checkbox, i) {
        expect(i).toBeLessThan(MAX_CHECK_ATTEMPTS);
        if (i >= MAX_CHECK_ATTEMPTS) {
            console.error('WARNING[schedule.po.js]: MAX_CHECK_ATTEMPTS reached.');
            return;
        }
        checkbox.click().then(function () {
            checkbox.isSelected().then(function (value) {
                if (value === true) {
                    return;
                }
                ensureCheckAux(checkbox, i + 1);
            })
        }, function (error) {
            //TODO Use logger to allow save error messages from Protractor.
            console.error('WARNING[schedule.po.js]: Cannot click on element. ');
            ensureCheckAux(checkbox, i + 1);
        });
    }
};

module.exports = new utilities();
