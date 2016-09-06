'use strict';

var CreativeGroupCreate = (function (campaignName) {

  describe('Create New Creative Group', function() {
    var page = require('../page-object/creative-groups.po'),
        com = require('../page-object/utilities.po'),
        common = require('../utilities/common.spec'),
        cg = require('../utilities/creative-groups.spec'),
        nav = require('../page-object/navigation.po'),
        navigate = require('../utilities/navigation.spec');

    it('should create new creative group, ' + cg.creativeGroupSmoke.creativeGroupName, function() {
      navigate.newCreativeGroup(campaignName);
      common.newCreativeGroup(campaignName, cg.creativeGroupSmoke);
      navigate.creativeGroupDetails(campaignName, cg.creativeGroupSmoke.creativeGroupName);
      expect(page.nameField.getAttribute('value')).toEqual(cg.creativeGroupSmoke.creativeGroupName);
      expect(page.defaultGroup.isSelected()).toBe(false);
      expect(page.weightDist.isEnabled()).toBe(true);
      expect(page.frequencyCap.isEnabled()).toBe(true);
      expect(page.priority.isEnabled()).toBe(true);
      expect(page.cookieTarget.isEnabled()).toBe(true);
    });

  });

});

module.exports = CreativeGroupCreate;


