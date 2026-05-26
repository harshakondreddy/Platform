cd /data
wget https://www.python.org/ftp/python/2.7.15/Python-2.7.15.tgz
tar xzf Python-2.7.15.tgz
cd Python-2.7.15
./configure --enable-optimizations
make altinstall
curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py"
python2.7 get-pip.py
pip install ruamel.yaml
sudo yum install epel-release
sudo yum install python-pip
pip install requests
sudo rm -rf Python-2.7.15.tgz
