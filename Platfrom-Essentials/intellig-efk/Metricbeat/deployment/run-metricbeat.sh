docker run -d \
  --user=root \
  --volume="$(path for config)/metricbeat.docker.yml:/usr/share/metricbeat/metricbeat.yml:ro" \
  --volume="/var/run/docker.sock:/var/run/docker.sock:ro" \
  --volume="/sys/fs/cgroup:/hostfs/sys/fs/cgroup:ro" \
  --volume="/proc:/hostfs/proc:ro" \
  --volume="/:/hostfs:ro" \
  docker-local.repo.intellig.local/intellig-metricbeat:6.8.3 $1  metricbeat -e \
  -E setup.dashboards.enabled=true \
  -E output.elasticsearch.hosts=["$EFKHOST:9200"] \
  -E setup.kibana.host=<<kibana_host>>:5601
