import sys, getopt,subprocess
import ruamel.yaml
yaml = ruamel.yaml.YAML()
yaml.preserve_quotes = True
yaml.allow_duplicate_keys = True

yamlData = None
starcmpPath = "/data/reds-qa/start-components.sh"


def updateBuild(ydata, component, gold_tag):
    image =  ydata['services'][component]['image']
    ydata['services'][component]['image'] = image[:image.rfind(":")+1] + gold_tag
    return ydata

def read(yamlFile):
    inp_fo = open(yamlFile).read()
    data = yaml.load(inp_fo)
    return data

def write(data, yamlFile):
    inp_fo2= open(yamlFile,"w")
    yaml.dump(data,inp_fo2)
    inp_fo2.close()

def main(argv):
    global yamlData
    yamlFile = "/data/reds-qa/docker-compose-components.yml"
    component = ""
    docker_tag = ""

    try:
        opts, args = getopt.getopt(argv,"hc:g:",["component=","gold_tag="])
    except getopt.GetoptError:
        print 'refresh.py -c <component> -g <gold_tag>'
        print (getopt.GetoptError)
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print 'refresh.py -c <component> -g <gold_tag>'
            sys.exit()
        elif opt in ("-c", "--component"):
            component = arg
        elif opt in ("-g", "--gold_tag"):
            docker_tag = arg

    exp = False
    if component=="":
        print ("Missing component name")
        exp = True
    if docker_tag == "":
        print("Missing gold_tag")
        exp = True
    if exp:
        print("run\n python refresh.py -c <component> -g <gold_tag> ")
        sys.exit(2)
    global starcmpPath
    print(starcmpPath)

    yamlData = read(yamlFile)
    print("This Process will run With DB upgrade")
    print("...Updating Build ("+component+")...")
    yamlData = updateBuild(yamlData, component, docker_tag)
    print("...Build Update Completed ("+component+")...")
    print("...Saving Updated yaml ("+component+")...")
    write(yamlData, yamlFile)
    print("...yaml Updated ("+component+")...")
    print("...Removing Existing Docker ("+component+")...")
    subprocess.call("docker stop $(docker ps -a | grep "+component+':'" -m 1 | cut -d ' ' -f 1 )", shell=True)
    subprocess.call("docker rm $(docker ps -a | grep "+component+':'" -m 1 | cut -d ' ' -f 1 )", shell=True)
    print("...Docker Removed ("+component+")...")
    print("...Running Script ("+component+")...")
    subprocess.call(['sh',starcmpPath,yamlFile,component])
    print("...Process with DB upgrade completed ("+component+")...")
    print("**********************************************")
    print("...Component refresh completed successfully...")
    print("**********************************************")
    print("........Validate Commit on Eureka.............")
    print("**********************************************")


if __name__ == "__main__":
   main(sys.argv[1:])


