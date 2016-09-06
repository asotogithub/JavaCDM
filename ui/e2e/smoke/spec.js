

describe('Smoke Test', function() {
  var createCampaign = require('./campaign-create.spec'),
  	  createMedia = require('./media-create.spec'),
  	  createCreativeGroup = require('./creative-group-create.spec'),
  	  creativeUpload = require('./creative.spec'),
  	  createSchedule = require('./schedule-create.spec'),
      login = require('../utilities/login.spec'),
      logout = require('../utilities/logout.spec'),
      username = 'tnguyen@trueffect.com',
      password = 'Trueffect123',
      campaignName = new Date().getTime() + 'Protractor Smoke Campaign',
      ioName = new Date().getTime() + 'Protractor Smoke IO',
      files = {
          gifFile: {
              name : 'pilot-GB_lifestyle_300by250.gif',
              width : '300',
              height : '250',
              type : 'gif',
              path: ''
          },
          html5file: {
              name : 'Leaderboard-MultiClick-Swiffy.zip',
              width : '728',
              height : '90',
              type : 'html5',
              path: ''
          },
      };

  	login(username, password);
    createCampaign(campaignName);
    createMedia(campaignName, ioName);
    createCreativeGroup(campaignName);
    creativeUpload(campaignName, files);
    createSchedule(campaignName);
    logout();

});
