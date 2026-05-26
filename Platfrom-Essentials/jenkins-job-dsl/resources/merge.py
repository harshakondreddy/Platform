#Author h276829 Abhishek Roy
#Run the command to execute the file python merge.py customer/customer-morrisanddickson config config_repo
#argument 1 is customer repo folder , argument 2 is the common/config folder location and the last is the git repo cloned folder for the remote scc git repository

import os,sys
import subprocess
import shutil
from xml.etree import ElementTree


def getDictionaryFromProperties(filePath):
    properties={}
    with open(filePath, 'r') as f:
        for line in f:
            line = line.rstrip() #removes trailing whitespace and '\n' chars
            if "=" not in line: continue #skips blanks and comments w/o =
            if line.startswith("#"): continue #skips comments which contain =
            k, v = line.split("=", 1)
            properties[k] = v
    return properties


def ifFileEmpty(filePath):
    file = open(filePath, 'r')
    count=0
    for line in file:
        if('#' not in line[0] or line!=''):
           count+=1
    if(count>0):
        print(filePath,' is not empty')
        return 'false'
    else:
        print(filePath,' is  empty')
        return 'true'

def mergePropertiesFile(srcPath,destPath):
    print('-----------------------------------Merging Properties Files------------------------------------')
    print(srcPath)
    print(destPath)
    print('-----------------------------------------------------------------------------------------------')
    srcDict=getDictionaryFromProperties(srcPath)
    destDict=getDictionaryFromProperties(destPath)
    filename= os.path.basename(srcPath)
    print('tempFolder--',tempFolder,'filename--',filename)
    file = open(os.path.join(tempFolder,filename),'w+')
    #merge the two dictionaries
    destDict.update(srcDict);
    for key in destDict:
            file.write(key+'='+destDict[key]+'\n')
    file.close()

def mergeYmlFile(srcPath,destPath):
    #check if there is a difference
    status, output = subprocess.getstatusoutput('diff --ignore-all-space --ignore-space-change --ignore-blank-lines --ignore-tab-expansion '+srcPath+' '+destPath)
    if(output!=''):
        print('-----------------------------------Merging Yml Files------------------------------------')
        print(srcPath)
        print(destPath)
        print('-----------------------------------------------------------------------------------------------')

        filename_no_extension=srcPath[srcPath.rfind('\\')+1:srcPath.rfind('.')]
        filename=srcPath[srcPath.rfind('\\')+1:]
        print('filename------->',filename)
        if(not os.path.exists('yamls')):
            os.mkdir('yamls')
            print('directory yamls created')
        #if yaml files are non-empty then merge them else copy from one to another
        if(ifFileEmpty(srcPath)=='false' and ifFileEmpty(destPath)=='false'):
            shutil.copy(srcPath,os.path.join('yamls','src_'+filename_no_extension+'.yaml'))
            shutil.copy(destPath,os.path.join('yamls','dest_'+filename_no_extension+'.yaml'))
            #invoke jar to merge the yaml files copied under folder "yamls" and create the output file
            #jar doesnt work on empty files..hence the above if condition
            javacmd_fcp_status, javacmd_fp_output = subprocess.getstatusoutput('java -jar ./yaml-merge-0.0.2-jar-with-dependencies.jar '+
                '--input yamls' +' --output '+os.path.join(tempFolder,filename))
        else:
            if ifFileEmpty(destPath)=='false':
                shutil.copy(destPath,os.path.join(tempFolder,os.path.basename[destPath]))
            if ifFileEmpty(srcPath)=='false':
                shutil.copy(srcPath,os.path.join(tempFolder,os.path.basename[srcPath]))
                
        shutil.rmtree('yamls')

def mergeJsoniles(srcPath,destPath):
    print('-----------------------------------Merging JSON Files------------------------------------')
    print(srcPath)
    print(destPath)
    print('-----------------------------------------------------------------------------------------------')

def mergeXmlFiles(srcPath,destPath):
    print('-----------------------------------Merging XML Files------------------------------------')
    print(srcPath)
    print(destPath)
    print('-----------------------------------------------------------------------------------------------')

    if(ifFileEmpty(srcPath)=='false' and ifFileEmpty(destPath)=='false'):
        files=[srcPath,destPath]
        filename= os.path.basename(srcPath)
        output_file = open(os.path.join(tempFolder,filename),'w+')
        first = None
        for file in files:
            data = ElementTree.parse(file).getroot()
            if first is None:
                first = data
            else:
                first.extend(data)
        if first is not None:
            ElementTree.register_namespace("","http://jboss.org/xml/ns/javax/validation/mapping")
            output_file.write(ElementTree.tostring(first).decode('ASCII'))
        output_file.close()
    else:
        filename= os.path.basename(srcPath)
        if ifFileEmpty(srcPath)=='false':
            shutil.copy2(srcPath,os.path.join(tempFolder))
            #fcp_status, src_fp_output = subprocess.getstatusoutput('xcopy /F /R /Y '+srcPath+' '+os.path.join(tempFolder,filename))
        if ifFileEmpty(destPath)=='false':
            shutil.copy2(destPath,os.path.join(tempFolder))
            #fcp_status, dest_fp_output = subprocess.getstatusoutput('xcopy /F /R /Y '+destPath+' '+os.path.join(tempFolder,filename))




def createRepoFilesDictionaries():

    for root, dirs, files in os.walk(repoFolderPath):
        for file in files:
            if file.endswith('.properties') or file.endswith('.yml') or file.endswith('.json') or file.endswith('.xml'):
                if str(file) in base_properties:
                    repo_properties[str(file)]=str(os.path.join(root,file))

def mergeFiles(srcPath,destPath):
    if srcPath.endswith('.properties'):
        mergePropertiesFile(srcPath,destPath)
    if srcPath.endswith('.yml'):
        mergeYmlFile(srcPath,destPath)
#    if srcPath.endswith('.json'):
#        mergeJsoniles(srcPath,destPath)
    if srcPath.endswith('.xml'):
        mergeXmlFiles(srcPath,destPath)

def pushtoGitRepository():
    createRepoFilesDictionaries()
    customerName=customerGitRepoFolderPath[customerGitRepoFolderPath.rfind("\\")+1:len(customerGitRepoFolderPath)]
    print('-------------------------------Pushing to SCC git repo-----------------------------------')
    for key in repo_properties:
        srcFile  = os.path.join(tempFolder,key)
        destFile =  repo_properties[key]
        if(os.path.isfile(srcFile)):
            print('copying from ',srcFile,' to ', destFile )
            shutil.copy2(srcFile,os.path.dirname(destFile))
            #currentDir = os.getcwd()
            #os.chdir(os.path.join(currentDir,repoFolderPath))
            #filename=os.path.basename(destFile)
            #tobeCommitedFileName=''
            #for fileSection in filename:
            #    tobeCommitedFileName=os.path.join(tobeCommitedFileName,fileSection)
            #print('git add "'+tobeCommitedFileName+'"')
            #add_error,add_output=subprocess.getstatusoutput('git add "'+tobeCommitedFileName+'"')
            #print(add_output)
            #os.chdir(currentDir)

#os.chdir(os.path.join(currentDir,repoFolderPath))
    print("changing directory to repoLocation")
    os.chdir(repoFolderPath)
    add_error,add_output=subprocess.getstatusoutput('git add .')
    commit_error,commit_output = subprocess.getstatusoutput('git commit -m "FILES_MERGED FROM customer'+customerName+'and common config AT `date "+%Y%m%d-%H%M%S"`"')
    print(commit_output)
#os.chdir(currentDir)





base_properties={}
custom_properties={}
print(sys.argv)
customerGitRepoFolderPath=sys.argv[1]#'customer/customer-morrisanddickson'
commonConfigLocation=sys.argv[2]#'config'
repoFolderPath=sys.argv[3]#'config_repo'
tempFolder='temp'
repo_properties={}


#cleanup tempFolder before Merge.Temp folder holds merged files before copying to Repo location
print('cleaning up config directories')
if(not os.path.exists(tempFolder)):
    os.mkdir(tempFolder)
else:
    shutil.rmtree(tempFolder)
    os.mkdir(tempFolder)
if(os.path.exists('yamls')):
    shutil.rmtree('yamls')
    os.mkdir('yamls')

#create dictionary of files from customer folder by traversing
for root, dirs, files in os.walk(customerGitRepoFolderPath):
    for file in files:
        if file.endswith('.properties') or file.endswith('.yml') or file.endswith('.json') or file.endswith('.xml'):
            custom_properties[str(file)]=str(os.path.join(root,file))

print("---------------------------------------------------------------")

#create dictionary of files from base folder by traversing
for root, dirs, files in os.walk(commonConfigLocation):
    for file in files:
        if file.endswith('.properties') or file.endswith('.yml') or file.endswith('.json') or file.endswith('.xml'):
            if str(file) in custom_properties: #check if file key exists in customer dictionary and needs to be merged
                base_properties[str(file)]=str(os.path.join(root,file))

for key in base_properties:
    mergeFiles(custom_properties[key],base_properties[key])

pushtoGitRepository()

