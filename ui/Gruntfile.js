// Generated on 2015-05-07 using generator-angular-fullstack 2.0.13
'use strict';

module.exports = function (grunt) {
  var localConfig;
  try {
    localConfig = require('./server/config/local.env');
  } catch(e) {
    localConfig = {};
  }
  var apiVersion = grunt.option('apiVersion') || 'latest';
  var browser_version = grunt.option('browser_version') || '36.0';
  var os = grunt.option('os') || 'OS X';
  var os_version = grunt.option('os_version') || 'Mavericks';
  var resolution = grunt.option('resolution') || '1280x1024';
  var jsonString = '{' ;
          jsonString = jsonString + '"apiVersion": "' + apiVersion + '"';
          jsonString = jsonString + '}';
  var jscsReporter = 'console';
  var jscsReporterOutput = null;
  var jshintReporter = require('jshint-stylish');
  var jshintServerOutput = null;
  var jshintServerTestOutput = null;
  var jshintClientOutput = null;
  var jsHintClientTestOutput = null;
  if(process.env.BUILD_SERVER === 'true') {
    jshintReporter = 'checkstyle';
    jshintServerOutput = 'test-results/cs-jshint-server.xml';
    jshintServerTestOutput = 'test-results/cs-jshint-server-test.xml';
    jshintClientOutput = 'test-results/cs-jshint-client.xml';
    jsHintClientTestOutput = 'test-results/cs-jshint-client-test.xml';
    jscsReporter = 'checkstyle';
    jscsReporterOutput = 'test-results/cs-jscs.xml';
  }

  // Load grunt tasks automatically, when needed
  require('jit-grunt')(grunt, {
    express: 'grunt-express-server',
    useminPrepare: 'grunt-usemin',
    ngtemplates: 'grunt-angular-templates',
    cdnify: 'grunt-google-cdn',
    protractor: 'grunt-protractor-runner',
    protractor_regression: 'grunt-protractor-runner',
    injector: 'grunt-asset-injector',
    buildcontrol: 'grunt-build-control'
  });

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    pkg: grunt.file.readJSON('package.json'),
    yeoman: {
      // configurable paths
      client: require('./bower.json').appPath || 'client',
      dist: 'dist'
    },
    shell: {
      protractor_webdriver_manager_update: {
        options: {
          stdout: true
        },
        command: 'npm run update-webdriver'
      }
    },
    express: {
      options: {
        port: process.env.PORT || 9000
      },
      dev: {
        options: {
          script: 'server/app.js',
          debug: true
        }
      },
      prod: {
        options: {
          script: 'dist/server/app.js'
        }
      }
    },
    open: {
      server: {
        url: 'http://localhost:<%= express.options.port %>'
      }
    },
    watch: {
      injectJS: {
        files: [
          '<%= yeoman.client %>/{app,components}/**/*.js',
          '!<%= yeoman.client %>/{app,components}/**/*.spec.js',
          '!<%= yeoman.client %>/{app,components}/**/*.mock.js',
          '!<%= yeoman.client %>/app/app.js'],
        tasks: ['injector:scripts']
      },
      injectCss: {
        files: [
          '<%= yeoman.client %>/{app,components}/**/*.css'
        ],
        tasks: ['injector:css']
      },
      mochaTest: {
        files: ['server/**/*.spec.js'],
        tasks: ['env:test', 'mochaTest']
      },
      jsTest: {
        files: [
          '<%= yeoman.client %>/{app,components}/**/*.spec.js',
          '<%= yeoman.client %>/{app,components}/**/*.mock.js'
        ],
        tasks: ['newer:jshint:client', 'newer:jshint:clientTest', 'newer:jscs', 'karma']
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        files: [
          '{.tmp,<%= yeoman.client %>}/{app,components}/**/*.css',
          '{.tmp,<%= yeoman.client %>}/{app,components}/**/*.html',
          '{.tmp,<%= yeoman.client %>}/{app,components}/**/*.js',
          '!{.tmp,<%= yeoman.client %>}{app,components}/**/*.spec.js',
          '!{.tmp,<%= yeoman.client %>}/{app,components}/**/*.mock.js',
          '<%= yeoman.client %>/assets/images/{,*//*}*.{png,jpg,jpeg,gif,webp,svg}'
        ],
        options: {
          livereload: true
        }
      },
      express: {
        files: [
          'server/**/*.{js,json}'
        ],
        tasks: ['express:dev', 'wait'],
        options: {
          livereload: true,
          nospawn: true //Without this option specified express won't be reloaded
        }
      }
    },

    // Make sure code styles are up to par and there are no obvious mistakes
    jshint: {
      options: {
        jshintrc: '<%= yeoman.client %>/.jshintrc',
        reporter: jshintReporter
      },
      server: {
        options: {
          jshintrc: 'server/.jshintrc',
          reporterOutput: jshintServerOutput
        },
        src: [
          'server/**/*.js',
          '!server/**/*.spec.js'
        ]
      },
      serverTest: {
        options: {
          jshintrc: 'server/.jshintrc-spec',
          reporterOutput: jshintServerTestOutput
        },
        src: ['server/**/*.spec.js']
      },
      client: {
        options: {
          reporterOutput: jshintClientOutput
        },
        src: [
          '<%= yeoman.client %>/{app,components}/**/*.js',
          '!<%= yeoman.client %>/{app,components}/**/*.spec.js',
          '!<%= yeoman.client %>/{app,components}/**/*.mock.js',
          '!<%= yeoman.client %>/app/angle/directives/**/*.js',
          '!<%= yeoman.client %>/app/cm/locale/translate/**/*.js'
        ]
      },
      clientTest: {
        options: {
          jshintrc: '<%= yeoman.client %>/.jshintrc-spec',
          reporterOutput: jsHintClientTestOutput
        },

        src: [
          '<%= yeoman.client %>/{app,components}/**/*.spec.js',
          '<%= yeoman.client %>/{app,components}/**/*.mock.js'
        ]
      }
    },

    jscs: {
      client: {
        options: {
          config: '.jscsrc',
          reporter: jscsReporter,
          reporterOutput: jscsReporterOutput
        },

        src: [
          '<%= yeoman.client %>/{app,components}/**/*.js',
          '!<%= yeoman.client %>/app/cm/locale/translate/*.js',
          '!<%= yeoman.client %>/app/angle/directives/**/*.js',
          '!<%= yeoman.client %>/app/config/constants.js'
        ]
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        files: [{
          dot: true,
          src: [
            '.tmp',
            '<%= yeoman.dist %>/*',
            '!<%= yeoman.dist %>/.git*',
            '!<%= yeoman.dist %>/.openshift',
            '!<%= yeoman.dist %>/Procfile',
            'test-results'
          ]
        }]
      },
      e2elogs: 'e2e/*.log',
      server: '.tmp',
      install: {
        files: [{
          dot: true,
          src: [
            'node_modules',
            'app/bower_components'
          ]
        }]
      }
    },

    // Add vendor prefixed styles
    autoprefixer: {
      options: {
        browsers: ['last 1 version']
      },
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/',
          src: '{,*/}*.css',
          dest: '.tmp/'
        }]
      }
    },

    // Debugging with node inspector
    'node-inspector': {
      custom: {
        options: {
          'web-host': 'localhost'
        }
      }
    },

    // Use nodemon to run server in debug mode with an initial breakpoint
    nodemon: {
      debug: {
        script: 'server/app.js',
        options: {
          nodeArgs: ['--debug-brk'],
          env: {
            PORT: process.env.PORT || 9000
          },
          callback: function (nodemon) {
            nodemon.on('log', function (event) {
              console.log(event.colour);
            });

            // opens browser on initial server start
            nodemon.on('config:update', function () {
              setTimeout(function () {
                require('open')('http://localhost:8080/debug?port=5858');
              }, 500);
            });
          }
        }
      }
    },

    // Automatically inject Bower components into the app
    wiredep: {
      target: {
        src: '<%= yeoman.client %>/index.html',
        ignorePath: '<%= yeoman.client %>/',
        exclude: [
          /bootstrap-sass-official/,
          '/json3/',
          '/es5-shim/'
        ]
      },
        test: {
            devDependencies: true,
            src: 'files.karma.conf.js',
            ignorePath:  /\.\.\//,
            fileTypes: {
                js: {
                    block: /(([\s\t]*)\/\/\s*bower:*(\S*))(\n|\r|.)*?(\/\/\s*endbower)/gi,
                    detect: {
                        js: /'(.*\.js)'/gi
                    },
                    replace: {
                        js: '\'{{filePath}}\','
                    }
                }
            }
        }
    },

    // Renames files for browser caching purposes
    rev: {
      dist: {
        files: {
          src: [
            '<%= yeoman.dist %>/public/{,*/}*.js',
            '<%= yeoman.dist %>/public/{,*/}*.css',
            '<%= yeoman.dist %>/public/assets/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
            '<%= yeoman.dist %>/public/assets/fonts/*'
          ]
        }
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      html: ['<%= yeoman.client %>/index.html'],
      options: {
        dest: '<%= yeoman.dist %>/public'
      }
    },

    // Performs rewrites based on rev and the useminPrepare configuration
    usemin: {
      html: ['<%= yeoman.dist %>/public/{,*/}*.html'],
      css: ['<%= yeoman.dist %>/public/{,*/}*.css'],
      js: ['<%= yeoman.dist %>/public/{,*/}*.js'],
      options: {
        assetsDirs: [
          '<%= yeoman.dist %>/public',
          '<%= yeoman.dist %>/public/assets/images'
        ],
        // This is so we update image references in our ng-templates
        patterns: {
          js: [
            [/(assets\/images\/.*?\.(?:gif|jpeg|jpg|png|webp|svg))/gm, 'Update the JS to reference our revved images']
          ]
        }
      }
    },

    // The following *-min tasks produce minified files in the dist folder
    imagemin: {
      dist: {
        files: [{
          expand: true,
          cwd: '<%= yeoman.client %>/assets/images',
          src: '{,*/}*.{png,jpg,jpeg,gif}',
          dest: '<%= yeoman.dist %>/public/assets/images'
        }]
      }
    },

    svgmin: {
      dist: {
        files: [{
          expand: true,
          cwd: '<%= yeoman.client %>/assets/images',
          src: '{,*/}*.svg',
          dest: '<%= yeoman.dist %>/public/assets/images'
        }]
      }
    },

    // Allow the use of non-minsafe AngularJS files. Automatically makes it
    // minsafe compatible so Uglify does not destroy the ng references
    ngAnnotate: {
      dist: {
        files: [{
          expand: true,
          cwd: '.tmp/concat',
          src: '*/**.js',
          dest: '.tmp/concat'
        }]
      }
    },

    // Package all the html partials into a single javascript payload
    ngtemplates: {
      options: {
        // This should be the name of your apps angular module
        module: 'uiApp',
        htmlmin: {
          collapseBooleanAttributes: true,
          collapseWhitespace: true,
          removeAttributeQuotes: true,
          removeEmptyAttributes: true,
          removeRedundantAttributes: true,
          removeScriptTypeAttributes: true,
          removeStyleLinkTypeAttributes: true
        },
        usemin: 'app/app.js'
      },
      main: {
        cwd: '<%= yeoman.client %>',
        src: ['{app,components}/**/*.html'],
        dest: '.tmp/templates.js'
      },
      tmp: {
        cwd: '.tmp',
        src: ['{app,components}/**/*.html'],
        dest: '.tmp/tmp-templates.js'
      }
    },

    // Replace Google CDN references
    cdnify: {
      dist: {
        html: ['<%= yeoman.dist %>/public/*.html']
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= yeoman.client %>',
          dest: '<%= yeoman.dist %>/public',
          src: [
            '*.{ico,png,txt}',
            '.htaccess',
            'bower_components/**/*',
            'assets/images/{,*/}*.{webp}',
            'assets/fonts/**/*',
            'assets/js/**/*',
            'index.html',
            'views/**/*.html'
          ]
        }, {
          expand: true,
          cwd: '.tmp/images',
          dest: '<%= yeoman.dist %>/public/assets/images',
          src: ['generated/*']
        }, {
          expand: true,
          dest: '<%= yeoman.dist %>',
          src: [
            'package.json',
            'server/**/*'
          ]
        }]
      },
      test: {
        files: [{
          src: 'server/templates/image-350x250.jpg',
          dest: 'server/templates/.tmp/image-i-350x250.jpg'
        }]
      },
      styles: {
        expand: true,
        cwd: '<%= yeoman.client %>',
        dest: '.tmp/',
        src: ['{app,components}/**/*.css']
      }
    },

    buildcontrol: {
      options: {
        dir: 'dist',
        commit: true,
        push: true,
        connectCommits: false,
        message: 'Built %sourceName% from commit %sourceCommit% on branch %sourceBranch%'
      },
      heroku: {
        options: {
          remote: 'heroku',
          branch: 'master'
        }
      },
      openshift: {
        options: {
          remote: 'openshift',
          branch: 'master'
        }
      }
    },

    // Run some tasks in parallel to speed up the build process
    concurrent: {
      server: [
      ],
      test: [
      ],
      debug: {
        tasks: [
          'nodemon',
          'node-inspector'
        ],
        options: {
          logConcurrentOutput: true
        }
      },
      dist: [
        'imagemin',
        'svgmin'
      ]
    },

    // Test settings
    karma: {
        options: {
            configFile: 'karma.conf.js',
            singleRun: true
        },

        test: {
            options: {
                files: '<%= testFiles %>'
            },

            junitReporter: {
                outputFile: 'test-results/karma-group-' + '<%= groupNumber %>' + '-results.xml',
                suite: '(Test Files <%= groupNumber %>)'
            }
        }
    },

    mochaTest: {
      options: {
        reporter: 'spec'
      },
      src: ['server/**/*.spec.js']
    },

    protractorParams: JSON.parse(jsonString),
    browser_version: browser_version,
    os: os,
    os_version: os_version,
    resolution: resolution,

    protractor: {
      options: {
        configFile: 'e2e/protractor.conf.js',
       keepAlive: true
      },
      smoke: {
        options: {
          configFile: 'e2e/protractor-smoke.conf.js',
          args: {
            params: '<%= protractorParams %>',
            browser: 'chrome',
            capabilities: {
              browser_version: '<%= browser_version %>',
              os: '<%= os %>',
              os_version: '<%= os_version %>',
              resolution: '<%= resolution %>'
            }
          }
        }
      },
      smokeFirefox: {
        options: {
          configFile: 'e2e/protractor-smoke-ff.conf.js',
          args: {
            params: '<%= protractorParams %>',
            capabilities: {
              browser_version: '<%= browser_version %>',
              os: '<%= os %>',
              os_version: '<%= os_version %>',
              resolution: '<%= resolution %>'
            }
          }
        }
      },
      regression: {
        options: {
          configFile: 'e2e/protractor.conf.js',
          args: {
            params: '<%= protractorParams %>',
            browser: 'chrome',
            capabilities: {
              browser_version: '<%= browser_version %>',
              os: '<%= os %>',
              os_version: '<%= os_version %>',
              resolution: '<%= resolution %>'
            }
          }
        }
      }
    },

    env: {
      test: {
        NODE_ENV: 'test'
      },
      prod: {
        NODE_ENV: 'production'
      },
      all: localConfig
    },

    injector: {
      options: {

      },
      // Inject application script files into index.html (doesn't include bower)
      scripts: {
        options: {
          transform: function(filePath) {
            filePath = filePath.replace('/client/', '');
            filePath = filePath.replace('/.tmp/', '');
            return '<script src="' + filePath + '"></script>';
          },
          starttag: '<!-- injector:js -->',
          endtag: '<!-- endinjector -->'
        },
        files: {
          '<%= yeoman.client %>/index.html': [
              ['{.tmp,<%= yeoman.client %>}/{app,components}/**/*.js',
               '!{.tmp,<%= yeoman.client %>}/app/app.js',
               '!{.tmp,<%= yeoman.client %>}/{app,components}/**/*.spec.js',
               '!{.tmp,<%= yeoman.client %>}/{app,components}/**/*.mock.js'
              ]
            ]
        }
      },

      // Inject vendor css into index.html
      // Note that a new usemin block has been defined in index.html:
      // <!-- build:css(client) app/_vendor.css -->
      // This is on purpose as it was not possible to make the minified vendor.css file to contain Angle styles cleanly.
      // One of the selectors (body) was lost due to the way how cssmin task works. By default it will remove duplicate
      // selectors. That means that the new "_vendor.css" needs to be separated out.
        'vendor-css': {
        options: {
          transform: function(filePath) {
            filePath = filePath.replace('/client/', '');
            filePath = filePath.replace('/.tmp/', '');
            return '<link rel="stylesheet" href="' + filePath + '">';
          },

          starttag: '<!-- vendor:css -->',
          endtag: '<!-- endvendor -->'
        },
        files: {
          '<%= yeoman.client %>/index.html': [
            '<%= yeoman.client %>/vendor/**/*.css'
          ]
        }
      },
      // Inject vendor js into index.html
      'vendor-js': {
        options: {
          transform: function(filePath) {
            filePath = filePath.replace('/client/', '');
            filePath = filePath.replace('/.tmp/', '');
              return '<script src="' + filePath + '"></script>';
          },

          starttag: '<!-- vendor:js -->',
          endtag: '<!-- endvendor -->'
        },
        files: {
          '<%= yeoman.client %>/index.html': [
            '<%= yeoman.client %>/vendor/**/*.js'
          ]
        }
      },

      // Inject component css into index.html
      css: {
        options: {
          transform: function(filePath) {
            filePath = filePath.replace('/client/', '');
            filePath = filePath.replace('/.tmp/', '');
            return '<link rel="stylesheet" href="' + filePath + '">';
          },
          starttag: '<!-- injector:css -->',
          endtag: '<!-- endinjector -->'
        },
        files: {
          '<%= yeoman.client %>/index.html': [
            '<%= yeoman.client %>/{app,components}/**/*.css'
          ]
        }
      }
    },

    // TODO: rlt 20150512 - The output of this plugin lets loose a "use strict"; in the global namespace.
    //                      This needs to be resolved sooner or later.
    jsonAngularTranslate: {
      generateTranslationsScripts: {
        options: {
            moduleName: 'uiApp.translate',
            extractLanguage: /..(?=\.[^.]*$)/,
            hasPreferredLanguage: true
        },
          files: [
              {
                  expand: true,
                  cwd: '<%= yeoman.client %>/app/cm/locale/json',
                  src: '*.json',
                  dest: '<%= yeoman.client %>/app/cm/locale/translate',
                  ext: '.js'
              }
          ]
      }
    },

    ngconstant: {
        options: {
            wrap: '(function () {\n\n\'use strict\';\n\n {%= __ngModule %}})();',
            space: '  ',
            name: 'constants',
            dest: '<%= yeoman.client %>/app/config/constants.js',
            constants: {
                CONSTANTS: grunt.file.readJSON('client/app/config/constants.json')
            }
        },
        build: {
        }
    }
  });

  grunt.loadNpmTasks('grunt-json-angular-translate');
  grunt.loadNpmTasks('grunt-ng-constant');
  grunt.loadNpmTasks('grunt-protractor-webdriver');
  // Used for delaying livereload until after server has restarted
  grunt.registerTask('wait', function () {
    grunt.log.ok('Waiting for server reload...');

    var done = this.async();

    setTimeout(function () {
      grunt.log.writeln('Done waiting!');
      done();
    }, 1500);
  });

  grunt.registerTask('express-keepalive', 'Keep grunt running', function() {
    this.async();
  });

  grunt.registerTask('serve', function (target) {
    if (target === 'dist') {
      return grunt.task.run(['build', 'env:all', 'env:prod', 'express:prod', 'wait', 'open', 'express-keepalive']);
    }

    if (target === 'debug') {
      return grunt.task.run([
        'clean:server',
        'env:all',
        'concurrent:server',
        'jsonAngularTranslate',
        'injector',
        'wiredep',
        'autoprefixer',
        'concurrent:debug'
      ]);
    }

    grunt.task.run([
      'clean:server',
      'env:all',
      'concurrent:server',
      'jsonAngularTranslate',
      'ngconstant',
      'injector',
      'wiredep',
      'autoprefixer',
      'express:dev',
      'wait',
      'open',
      'watch'
    ]);
  });

  grunt.registerTask('server', function () {
    grunt.log.warn('The `server` task has been deprecated. Use `grunt serve` to start a server.');
    grunt.task.run(['serve']);
  });

  grunt.registerTask('test', function(target) {
    if (target === 'server') {
      return grunt.task.run([
        'env:all',
        'env:test',
        'mochaTest'
      ]);
    }

    else if (target === 'client') {
      return grunt.task.run([
        'clean:server',
        'env:all',
        'ngconstant',
        'jsonAngularTranslate',
        'concurrent:test',
        'autoprefixer',
        'karmaSplit'
      ]);
    }

    else if (target === 'e2e') {
      return grunt.task.run([
        'clean:e2elogs',
        'copyCreativesForProtractor',
        'shell:protractor_webdriver_manager_update',
        'protractor:regression'
      ]);
    }

    else if (target === 'smoke') {
      return grunt.task.run([
        'clean:e2elogs',
        'shell:protractor_webdriver_manager_update',
        'protractor:smoke'
      ]);
    }

    else if (target === 'smokeFirefox') {
      return grunt.task.run([
        'clean:e2elogs',
        'shell:protractor_webdriver_manager_update',
        'protractor:smokeFirefox'
      ]);
    }

    else grunt.task.run([
      'test:server',
      'test:client'
    ]);
  });

  grunt.registerTask('build', [
    'clean:dist',
    'concurrent:dist',
    'jsonAngularTranslate',
    'ngconstant',
    'injector',
    'wiredep',
    'useminPrepare',
    'autoprefixer',
    'ngtemplates',
    'concat',
    'ngAnnotate',
    'copy:dist',
    'cdnify',
    'cssmin',
    'uglify',
    'rev',
    'usemin'
  ]);

  grunt.registerTask('default', [
    'newer:jshint',
    'newer:jscs',
    'test',
    'build'
  ]);

  grunt.registerTask('verify', [
    'test',
    'jshint',
    'jscs'
  ]);

  grunt.registerTask('karmaSplit', function () {
      var groupSize = 30,  //How many test files should be en each test group
          count = require('./groupTests.karma.conf.js').countTests(),
          totalGroups = (count % groupSize == 0) ? (count / groupSize) : ((count / groupSize) + 1),
          arrayTasks = [];
      for (var i = 1; i <= totalGroups; i++) {
          var start = groupSize * (i - 1) + 1,
              end = (groupSize * i > count) ? count : (groupSize * i);
          arrayTasks.push('karmaRunner:' + start + ':' + end);
      }
      grunt.task.run(arrayTasks);
  });

  grunt.registerTask('karmaRunner', function (start, end) {
      var testFiles = require('./files.karma.conf.js').concat(require('./groupTests.karma.conf.js').divideTests(start, end));
      grunt.config.set('testFiles', testFiles);
      grunt.config.set('groupNumber', start + ' to ' + end);
      grunt.task.run(['karma']);
  });

  grunt.registerTask('copyCreativesForProtractor', function () {
    var auxFilesArray,
        copyConfig = grunt.config.get('copy'),
        count = 11;   //How many copies should be made

    auxFilesArray = copyConfig.test.files;
    for (var i = auxFilesArray.length - 1; i >= 0; i--) {
      var fileItem = auxFilesArray[i];
      auxFilesArray.splice(i, 1);
      for (var j = 1; j <= count; j++) {
        auxFilesArray.push({src: fileItem.src, dest: fileItem.dest.replace('-i', '-' + j)})
      }
    }
    grunt.config.set('copy', copyConfig);
    grunt.task.run(['copy:test']);
  });
};
