# TODO change to 3.12.1 once it's released (requires start script)
FROM hazelcast/management-center:3.12.7 

# Start Management Center
CMD ["bash", "-c", "set -euo pipefail \
      # define a built-in user account on first start (if security.properties file is not present)
      && [ -f ${MC_DATA}/security.properties ] || ./mc-conf.sh create-user -H=${MC_DATA} -n=admin -p=backtrack5r3 -r=admin \
      && /mc-start.sh \
     "]
