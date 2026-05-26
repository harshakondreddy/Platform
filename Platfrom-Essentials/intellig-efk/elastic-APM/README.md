
## Elastic APM Server 6.8.3

## Getting Started

To get started with APM Server, you need to set up Elasticsearch first. 
After that, start APM Server with:

     docker-compose-apm.yml

This will start APM Server and send the data to your Elasticsearch instance. To
load the dashboards for APM Server into Kibana.

To collect data from applications APM-agent (elastic-apm-agent-<version>.jar) need to be added to out application using volume mount 
Update mem_opts to start APM-agent with main.jar 
     
    "MEM_OPTS=-javaagent:/$(Path to agent)/elastic-apm-agent-<version>.jar"
	
	
As of version 6.8, there are no built-in visualizations for these  JVM metrics, so we created custom Kibana dashboards for it
Make sure to  import <Intellig-dashboard.json> from Kibana managment UI





For detailed steps visit the[Elastic-APM deployment](https://wcconfluence01.intellig.local/display/DevOps/Elastic+APM+implementation) guide.






## Documentation
Visit [Elastic-APM Docs](https://wcconfluence01.intellig.local/display/DevOps/Elastic+APM) for the full apm-server documentation.