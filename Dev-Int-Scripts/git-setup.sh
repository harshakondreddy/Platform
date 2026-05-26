sudo yum install git -y
git version
cat /dev/zero | ssh-keygen -q -N "" > /dev/null
echo "------ ADD BELOW SSH KEY TO BIT-BUCKET PROFILE. -----"
echo " i.e. https://bitbucket.honeywell.com/plugins/servlet/ssh/account/keys/add "
echo "AFTER THAT YOU WILL BE ABLE TO CLONE REPOSITORY WITH SSH (NO PWD REQUIRED)"
echo "------------------------------------------------------"
cat ~/.ssh/id_rsa.pub
