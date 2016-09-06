'use strict';

var CreativeGroupsDetailGeo = (function () {

    describe('CookieTargeting', function() {

        var page = require('../page-object/creative-groups.po'),
            com = require('../page-object/utilities.po'),
            common = require('../utilities/common.spec'),
            navigate = require('../utilities/navigation.spec'),
            campaignName = 'Protractor Campaign',
            creativeGroupName = new Date().getTime() + 'Protractor Cookie Targeting Group',
            cookieTextValue= '[cp_type] = 2',
            cookieString = {
              cookieName: '[te_new]',
              operator: 'contain',
              operand: 'textvalue'
            },
            cookieNumber = {
              cookieName: '[cp_type]',
              operator: 'greater_or_equal',
              operand: '22222'
            },
            cookieStringList = {
              cookieName: '[newcookie]',
              operator: 'equal',
              operand: 'value1'
            },
            cookieStringBlank = {
              cookieName: '[te_new]',
              operator: 'is_blank'
            },
            cookieNumberNotNull = {
              cookieName: '[cp_type]',
              operator: 'is_not_null'
            },
            cookieStringListValues = ['value1','value2','value3'],
            cookieOptions = ["[te_new]","[cp_type]","[newcookie]"],
            editorValue = 'Contains([te_new], \'textvalue\') AND [cp_type] >= 22222 AND [newcookie] = \'value1\' AND ( IsNullOrEmpty([te_new]) OR [cp_type] Is Not Null )';

        it('should display creative group cookie targeting', function() {
            navigate.newCreativeGroup(campaignName);
            expect(com.saveBtn.isEnabled()).toBe(false);
        });

        it('should display cookie tab and enable cookie targeting', function() {
            expect(page.cookieTab.isDisplayed()).toBe(true);
            page.cookieTab.click();

            expect(page.builderTab.isDisplayed()).toBe(true);
            expect(page.builderTab.getAttribute('class')).toContain('active');
            expect(page.editorTab.isDisplayed()).toBe(true);
            expect(page.cookieBuilder.isDisplayed()).toBe(true);
            page.editorTab.click();
            expect(page.cookieTargetText.isEnabled()).toBe(false);
            page.builderTab.click();
            expect(page.cookieTarget.isEnabled()).toBe(true);
            expect(page.cookieTarget.isSelected()).toBe(false);

            page.cookieTargetSwitch.click();
            expect(page.cookieTarget.isSelected()).toBe(true);
            page.editorTab.click();
            expect(page.cookieTargetText.isEnabled()).toBe(true);
        });

        it('should display validation messages on invalid rules in the editor', function() {
            page.editorTab.click();

            //invalid filter
            page.cookieTargetText.sendKeys('[foo] = bar');
            page.builderTab.click();
            expect(page.cookieTargetingInvalid.isDisplayed()).toBe(true);
            expect(page.cookieTargetingInvalid.getText()).toContain('Invalid field \'[foo]\'.');
            page.cookieTargetText.clear();

            //operator not accepted
            page.cookieTargetText.sendKeys('[foo] != bar');
            page.builderTab.click();
            expect(page.cookieTargetingInvalid.isDisplayed()).toBe(true);
            expect(page.cookieTargetingInvalid.getText()).toContain('Invalid SQL operation \'!=\'.');
            page.cookieTargetText.clear();

            //invalid operator
            page.cookieTargetText.sendKeys('[cp_type] => bar');
            page.builderTab.click();
            expect(page.cookieTargetingInvalid.isDisplayed()).toBe(true);
            expect(page.cookieTargetingInvalid.getText()).toContain('Unexpected \'OPERATOR\'');
            page.cookieTargetText.clear();
        });

        it('should display cookie builder', function() {
            page.builderTab.click();
            expect(page.cookieBuilderAndBtn.first().isPresent()).toBe(true);
            expect(page.cookieBuilderOrBtn.first().isPresent()).toBe(true);
            expect(page.cookieBuilder.isDisplayed()).toBe(true);
            expect(page.cookieBuilderAddRuleBtn.first().isDisplayed()).toBe(true);
            expect(page.cookieBuilderAddGroupBtn.first().isDisplayed()).toBe(true);
        });

        it('should display all cookie options', function() {
            page.cookieBuilderAddRuleBtn.first().click();
            page.allCookieDropdowns.first().click();
            cookieOptions.forEach(function(option){
              expect(page.getCookieOption(option).isDisplayed()).toBe(true);
            });
        });

        it('should display string operators for cookie of type string', function() {
            page.allCookieDropdowns.first().click();
            page.getCookieOption(cookieString.cookieName).click();
            page.allCookieOperatorDropdowns.first().click();
            page.cookieOperatorStringOptions.forEach(function(option){
              expect(page.getOperatorOption(option).isDisplayed()).toBe(true);
            });
        });

        it('should display number operators for cookie of type number', function() {
            page.allCookieDropdowns.first().click();
            page.getCookieOption(cookieNumber.cookieName).click();
            page.allCookieOperatorDropdowns.first().click();
            page.cookieOperatorNumberOptions.forEach(function(option){
              expect(page.getOperatorOption(option).isDisplayed()).toBe(true);
            });
        });

        it('should only allow numeric values into the operand field for cookie of type number', function() {
            page.allCookieDropdowns.first().click();
            page.getCookieOption(cookieNumber.cookieName).click();
            page.allCookieValueInputs.last().sendKeys('foo');
            page.allCookieValueInputs.sendKeys(protractor.Key.TAB);
            expect(page.cookieBuilderWarning.isDisplayed()).toBe(true);
        });

        it('should add a rule for cookie of type string', function() {
            page.allCookieDropdowns.first().click();
            page.getCookieOption(cookieString.cookieName).click();
            page.allCookieOperatorDropdowns.first().click();
            page.getOperatorOption(cookieString.operator).click();
            expect(page.allCookieValueInputs.first().isDisplayed()).toBe(true);
            expect(page.allCookieValueInputs.first().getAttribute('type')).toEqual('text');
            page.allCookieValueInputs.first().sendKeys(cookieString.operand);
            page.allCookieValueInputs.first().sendKeys(protractor.Key.TAB);

            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieString.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieString.operator);
            expect(page.allCookieValueInputs.last().getAttribute('value')).toEqual(cookieString.operand);
        });

        it('should add a rule for cookie of type number', function() {
            page.cookieBuilderAddRuleBtn.first().click();
            page.allCookieDropdowns.last().click();
            page.getCookieOption(cookieNumber.cookieName).click();
            page.allCookieOperatorDropdowns.last().click();
            page.getOperatorOption(cookieNumber.operator).click();
            expect(page.allCookieValueInputs.last().isDisplayed()).toBe(true);
            page.allCookieValueInputs.last().sendKeys(cookieNumber.operand);
            page.allCookieValueInputs.sendKeys(protractor.Key.TAB);

            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieNumber.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieNumber.operator);
            expect(page.allCookieValueInputs.last().getAttribute('value')).toEqual(cookieNumber.operand);
        });

        it('should add a rule for cookie of type string list', function() {
            page.cookieBuilderAddRuleBtn.first().click();
            page.allCookieDropdowns.last().click();
            page.getCookieOption(cookieStringList.cookieName).click();
            page.allCookieOperatorDropdowns.last().click();
            page.getOperatorOption(cookieStringList.operator).click();
            expect(page.allCookieListValues.last().isDisplayed()).toBe(true);
            cookieStringListValues.forEach(function(option) {
              expect(page.getValueOption(option).isDisplayed()).toBe(true);
            })
            page.getValueOption(cookieStringList.operand).click();

            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieStringList.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieStringList.operator);
            expect(page.allCookieListValues.last().$('label input:checked').getAttribute('value')).toContain(cookieStringList.operand);
        });

        it('should allow user to add a new group', function() {
            page.cookieBuilderAddGroupBtn.first().click();
            page.cookieBuilderOrBtn.last().click();

            page.allCookieDropdowns.last().click();
            page.getCookieOption(cookieStringBlank.cookieName).click();
            page.allCookieOperatorDropdowns.last().click();
            page.getOperatorOption(cookieStringBlank.operator).click();
            expect(page.allCookieValueInputs.last().isDisplayed()).toBe(false);

            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieStringBlank.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieStringBlank.operator);

            //Add another rule
            page.cookieBuilderAddRuleBtn.get(1).click();
            page.allCookieDropdowns.last().click();
            page.getCookieOption(cookieNumberNotNull.cookieName).click();
            page.allCookieOperatorDropdowns.last().click();
            page.getOperatorOption(cookieNumberNotNull.operator).click();
            expect(page.allCookieValueInputs.last().isDisplayed()).toBe(false);

            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieNumberNotNull.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieNumberNotNull.operator);
        });

        it('should display rules correctly in editor', function() {
            page.editorTab.click();
            expect(page.cookieTargetText.getAttribute('value')).toEqual(editorValue);
        });

        it('should save creative group, ' + creativeGroupName + ', with cookie rule entered in cookie builder', function() {
            page.nameField.sendKeys(creativeGroupName);
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName);
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName);
            page.cookieTab.click();

            expect(page.allCookieDropdowns.first().$('option:checked').getText()).toEqual(cookieString.cookieName);
            expect(page.allCookieOperatorDropdowns.first().$('option:checked').getAttribute('value')).toEqual(cookieString.operator);
            expect(page.allCookieValueInputs.first().getAttribute('value')).toEqual(cookieString.operand);
            expect(page.allCookieDropdowns.get(1).$('option:checked').getText()).toEqual(cookieNumber.cookieName);
            expect(page.allCookieOperatorDropdowns.get(1).$('option:checked').getAttribute('value')).toEqual(cookieNumber.operator);
            expect(page.allCookieValueInputs.get(1).getAttribute('value')).toEqual(cookieNumber.operand);
            expect(page.allCookieDropdowns.get(2).$('option:checked').getText()).toEqual(cookieStringList.cookieName);
            expect(page.allCookieOperatorDropdowns.get(2).$('option:checked').getAttribute('value')).toEqual(cookieStringList.operator);
            expect(page.allCookieListValues.get(2).$('label input:checked').getAttribute('value')).toContain(cookieStringList.operand);
            expect(page.allCookieDropdowns.get(3).$('option:checked').getText()).toEqual(cookieStringBlank.cookieName);
            expect(page.allCookieOperatorDropdowns.get(3).$('option:checked').getAttribute('value')).toEqual(cookieStringBlank.operator);
            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieNumberNotNull.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieNumberNotNull.operator);
            page.editorTab.click();
            expect(page.cookieTargetText.getAttribute('value')).toEqual(editorValue);
        });

        it('delete creative group ' + creativeGroupName, function() {
            common.deleteCreativeGroup(campaignName, creativeGroupName);
        });

        it('should save creative group, ' + creativeGroupName + ' 2, with cookie rule entered in text editor', function() {
            navigate.newCreativeGroup(campaignName);
            page.nameField.sendKeys(creativeGroupName + ' 2');
            page.cookieTab.click();
            page.cookieTargetSwitch.click();
            page.editorTab.click();
            page.cookieTargetText.sendKeys(cookieTextValue);
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieTextValue);
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName + ' 2');
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName + ' 2');
            page.cookieTab.click();

            expect(page.allCookieDropdowns.first().$('option:checked').getText()).toEqual('[cp_type]');
            expect(page.allCookieOperatorDropdowns.first().$('option:checked').getAttribute('value')).toEqual('equal');
            expect(page.allCookieValueInputs.first().getAttribute('value')).toEqual('2');
            page.editorTab.click();
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieTextValue);
        });

        it('should update creative group cookie targeting value', function() {
            navigate.creativeGroupDetails(campaignName, creativeGroupName + ' 2');
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName + ' 2');
            page.cookieTab.click();
            page.cookieBuilderAddRuleBtn.first().click();

            page.allCookieDropdowns.last().click();
            page.getCookieOption(cookieString.cookieName).click();
            page.allCookieOperatorDropdowns.last().click();
            page.getOperatorOption(cookieString.operator).click();
            expect(page.allCookieValueInputs.last().isDisplayed()).toBe(true);
            expect(page.allCookieValueInputs.last().getAttribute('type')).toEqual('text');
            page.allCookieValueInputs.last().sendKeys(cookieString.operand);
            page.allCookieValueInputs.last().sendKeys(protractor.Key.TAB);

            expect(page.allCookieDropdowns.first().$('option:checked').getText()).toEqual('[cp_type]');
            expect(page.allCookieOperatorDropdowns.first().$('option:checked').getAttribute('value')).toEqual('equal');
            expect(page.allCookieValueInputs.first().getAttribute('value')).toEqual('2');
            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieString.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieString.operator);
            expect(page.allCookieValueInputs.last().getAttribute('value')).toEqual(cookieString.operand);
            page.editorTab.click();
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieTextValue + ' AND Contains([te_new], \'textvalue\')');
        });

        it('should save updated creative group then delete it', function() {
            com.saveBtn.click();
            expect(com.errorMsg.isPresent()).toBe(false);
            navigate.creativeGroupDetails(campaignName, creativeGroupName + ' 2');
            expect(page.nameField.getAttribute('value')).toEqual(creativeGroupName + ' 2');
            page.cookieTab.click();

            expect(page.allCookieDropdowns.first().$('option:checked').getText()).toEqual('[cp_type]');
            expect(page.allCookieOperatorDropdowns.first().$('option:checked').getAttribute('value')).toEqual('equal');
            expect(page.allCookieValueInputs.first().getAttribute('value')).toEqual('2');
            expect(page.allCookieDropdowns.last().$('option:checked').getText()).toEqual(cookieString.cookieName);
            expect(page.allCookieOperatorDropdowns.last().$('option:checked').getAttribute('value')).toEqual(cookieString.operator);
            expect(page.allCookieValueInputs.last().getAttribute('value')).toEqual(cookieString.operand);
            page.editorTab.click();
            expect(page.cookieTargetText.getAttribute('value')).toEqual(cookieTextValue + ' AND Contains([te_new], \'textvalue\')');
        });

        it('delete creative group ' + creativeGroupName + ' 2', function() {
            common.deleteCreativeGroup(campaignName, creativeGroupName + ' 2');
        });

    });

});

module.exports = CreativeGroupsDetailGeo;

