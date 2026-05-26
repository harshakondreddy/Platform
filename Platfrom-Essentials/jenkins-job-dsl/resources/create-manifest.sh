rm -rf vf-ci-refactor-dev
branch="18.2"
git clone ssh://git@bitbucket.intellig.local:7999/~h276829/vf-ci-refactor-dev.git
cd vf-ci-refactor-dev
DATETIMESTAMP=`date "+%Y%m%d%H%M%S"`
#MANIFEST_FILE=wes-manifest-$branch-$DATETIMESTAMP.json

MANIFEST_FILE=wes-manifest-$DATETIMESTAMP.json

temp_file=temp.json

echo '{"manifests":[' > $temp_file
data=''
for f in component-manifests/*.json
do
data+=`cat $f`
data+=","
done


echo $data | awk 'gsub(/,$/,x)' | cat >> $temp_file
echo "]}" >> $temp_file
cat $temp_file | jq '.' | cat > $MANIFEST_FILE 
rm $temp_file

git add $MANIFEST_FILE
git commit -m 'created manifest file'
git push origin master
cd ..
