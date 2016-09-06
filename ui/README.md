# UI project

## Requirements

The following components are required to be installed.

* NodeJS
* npm

Since we're using [Yeoman](http://yeoman.io) as web scaffolding tool and [angular-fullstack](https://github.com/DaftMonk/generator-angular-fullstack) generator, please install following dependences to start with the project:

    npm -g install yo
    npm -g install generator-angular-fullstack

Go inside `ui` directory and run following commands to install project dependences:

    npm install
    bower install

### Run in preview mode

The `ui` project can be tested in preview mode using:

    grunt serve

### Run in production mode
Once development is ready, we can run the built application running following command

    grunt serve:dist

## Styleguide

* [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml)
* [Mozilla Developer Guide - Coding Style](https://developer.mozilla.org/en-US/docs/Mozilla/Developer_guide/Coding_Style)
* [Angular Style Guide](https://github.com/johnpapa/angular-styleguide/blob/master/README.md) - Opinionated Angular style guide for teams by @john_papa

### Angular Dependency Lists
1. Manually identify Angular dependencies, as recommended by the [Angular Style Guide](https://github.com/johnpapa/angular-styleguide#manually-identify-dependencies).
1. Keep dependency lists in case-sensitive alphabetical order.

E.g.:

    LoginController.$inject = [
        '$log',
        '$rootScope',
        '$scope',
        '$state',
        'AuthenticationService',
        'OauthService',
        'UserService',
        'UsernameMemoryService'
    ];

    function LoginController($log,
                             $rootScope,
                             $scope,
                             $state,
                             AuthenticationService,
                             OauthService,
                             UserService,
                             UsernameMemoryService) {
        // ...
    }

In Sublime Text, the shortcut key for this is `CTRL+F5`.  Keeping these lists in alphabetical order accomplishes two things:

1. Places Angular dependencies (prefixed with `$`) first, as recommended by [ng-book](https://www.ng-book.com/).
1. Mitigates merge conflicts.

### `bower.json` and `package.json`
1. Always use fixed versions when declaring dependencies in the `bower.json` and `package.json` files so that our builds are reproducible.
1. Keep dependency lists in alphabetical order to mitigate merge conflicts.

Be especially careful when adding dependencies via `bower install --save` or `npm install --save`, as those commands will use version ranges (e.g. `~` or `^`).

### `messages_*.json`
Keep this file in case-sensitive alphabetical order to mitigate merge conflicts.
