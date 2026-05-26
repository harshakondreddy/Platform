FROM fluent/fluentd:v1.2.6-debian-onbuild
COPY entrypoint.sh /bin/entrypoint.sh
RUN ["chmod", "+x", "/bin/entrypoint.sh"]
RUN ["gem", "install", "fluent-plugin-grok-parser", "--no-rdoc", "--no-ri", "--version", "2.1.5"]
RUN ["gem", "install", "fluent-plugin-elasticsearch-timestamp-check"]
RUN ["gem", "install", "fluent-plugin-elasticsearch", "--no-rdoc", "--no-ri", "--version", "1.9.5"]
RUN ["gem", "install", "fluent-plugin-concat", "--no-rdoc", "--no-ri", "--version", "2.3.0"]





