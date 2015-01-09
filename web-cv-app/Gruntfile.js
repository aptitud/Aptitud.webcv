module.exports = function (grunt) {

    // Project configuration.
    grunt.initConfig({
        watch: {
            all: {
                options: {livereload: true},
                files: ['*.js', '*.html', '*.css']
            }
        },


        // grunt-contrib-connect will serve the files of the project
        // on specified port and hostname
        connect: {
            all: {
                options: {
                    base: 'src/main/webapp',
                    port: 8080,
                    hostname: "0.0.0.0",
                    // Prevents Grunt to close just after the task (starting the server) completes
                    // This will be removed later as `watch` will take care of that
                    keepalive: true
                }
            }
        }

    });

    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-connect');

    // Default task(s).
    grunt.registerTask('default', []);

    grunt.registerTask('server', ['connect']);

};