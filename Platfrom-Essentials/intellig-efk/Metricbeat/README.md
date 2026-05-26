# Metricbeat 6.8.0

Metricbeat is a lightweight shipper for metrics.

## Getting Started

To get started with Metricbeat, First need to set up Elasticsearch first. 
After that, start Metricbeat docker container with:

     ./run-metricbeat.sh

metricbeat.yml should be mounted to /usr/share/metricbeat/ and make sure to change ownership  as root user

    --volume="/$(path to file)/metricbeat.docker.yml:/usr/share/metricbeat/metricbeat.yml:ro" \

This will start Metricbeat and send the data to your Elasticsearch instance and  load the dashboards for Metricbeat to Kibana


For detail steps visit the [metricbeat deployment](https://wcconfluence01.intellig.local/display/DevOps/Metricbeat+Implementation) guide.




## Documentation

Visit [Elastic.Metricbeat Docs](https://wcconfluence01.intellig.local/display/DevOps/Metricbeat) for the full Metricbeat documentation.



## Release notes

https://www.elastic.co/guide/en/beats/libbeat/6.8/release-notes-6.8.0.html