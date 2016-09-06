// list of files / patterns to exclude in karma tests

module.exports = [
    'client/bower_components/angular-scenario/angular-scenario.js',
    'client/bower_components/**/*.spec.js',
    //TODO: The implementation of these unit tests files are currently in progress.
    'client/app/cm/main/main.controller.spec.js',
    'client/app/services/cdm/cdm.service.spec.js',
    'client/app/services/oauth/oauth.service.spec.js'
]
