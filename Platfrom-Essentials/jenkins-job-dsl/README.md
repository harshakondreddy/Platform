TO BE UPDATED!!
## Testing the DSL's
To test the dsl's run `./gradlew debugXml '-Dpattern=jobs/**/*jobs.groovy' `. This will parse the dsl's under jobs
directory and create jenkins job configuration xml files under `build/debug-xml/jobs/` directory.