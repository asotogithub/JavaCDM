module.exports = function (grunt) {

    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Project configuration.
    grunt.initConfig({

        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*!\n' +
        ' * Trueffect\'s Angle Bootstrap Theme v<%= pkg.version %>\n' +
        ' * Copyright 2015-<%= grunt.template.today("yyyy") %> <%= pkg.author %>\n' +
        ' */\n',

        // Task configuration.
        copy: {
            angleCSS: {
                src: 'vendor/angle/css/app.css',
                dest: 'dist/css/f2-app.css'
            }
        },

        clean: {
            dist: ['dist']
        },

        less: {
            development: {
                files: [
                    {
                        src: 'less/bootstrap-theme.less',
                        dest: 'dist/css/f1-bootstrap-theme.css'
                    },
                    {
                        src: 'less/angle-theme.less',
                        dest: 'dist/css/f3-angle-theme.css'
                    }
                ]
            }
        },

        cssmin: {
            options: {
                compatibility: 'ie8',
                keepSpecialComments: '*',
                noAdvanced: true

            },
            core: {
                files: {
                    'dist/css/f1-bootstrap-theme.min.css': 'dist/css/f1-bootstrap-theme.css',
                    'dist/css/f2-app.min.css': 'dist/css/f2-app.css',
                    'dist/css/f3-angle-theme.min.css': 'dist/css/f3-angle-theme.css'
                }
            }
        },

        usebanner: {
            options: {
                position: 'top',
                banner: '<%= banner %>'
            },
            files: {
                src: 'dist/css/*.css'
            }
        },

        csscomb: {
            options: {
                config: '.csscomb.json'
            },
            dist: {
                expand: true,
                cwd: 'dist/css/',
                src: ['*.css', '!*.min.css'],
                dest: 'dist/css/'
            }
        }
    });

    // CSS distribution task.
    grunt.registerTask('dist-css', ['less', 'usebanner', 'csscomb', 'cssmin']);

    // Full distribution task.
    grunt.registerTask('build', ['clean', 'copy:angleCSS', 'dist-css']);
};
