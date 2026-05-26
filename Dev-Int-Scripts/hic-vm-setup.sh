sudo bash -c 'cat >> /etc/hosts << EOL2
`hostname -i` dockerhost
EOL2'
echo "Installing Firewalld and Enabling Momentum Ports "
sudo systemctl enable firewalld
sudo systemctl start firewalld
sudo systemctl status firewalld
sudo firewall-cmd --permanent --add-port=8000-32767/tcp
sudo firewall-cmd --permanent --add-port=6781-6785/tcp
sudo firewall-cmd --permanent --add-port=6781-6785/udp
sudo firewall-cmd --permanent --add-port=53/tcp
sudo firewall-cmd --permanent --add-port=53/udp
sudo firewall-cmd --permanent --add-port=15672/tcp
sudo firewall-cmd --permanent --add-port=5672/tcp
sudo firewall-cmd --permanent --add-port=3306/tcp
sudo firewall-cmd --permanent --add-port=1443/tcp
sudo firewall-cmd --permanent --add-port=5701/tcp
sudo firewall-cmd --permanent --add-port=15701/tcp
sudo firewall-cmd --reload
echo "---Firewall Confiuguration Done---"
sudo yum install java-1.8.0-openjdk-devel -y
sudo yum install java-11-openjdk-devel -y
sudo yum install git -y
sudo yum install docker -y
sudo bash -c 'sed -i -e "s/selinux-enabled --/selinux-enabled\=false --/" /etc/sysconfig/docker'
sudo bash -c 'cat > /etc/docker/daemon.json << EOL1
{
    "graph": "/data/docker",
    "bip": "192.168.1.1/24",
    "fixed-cidr": "192.168.1.0/24"
}
EOL1'
sudo bash -c 'cat >> /etc/sysctl.conf << EOL2
vm.max_map_count=262144
net.bridge.bridge-nf-call-iptables=1
net.bridge.bridge-nf-call-ip6tables=1
EOL2'
sudo sysctl -p
sudo useradd qauser  -p $(openssl passwd -1 DevOps@1) -d /data/qauser
sudo systemctl start docker
sudo groupadd docker
sudo systemctl restart docker
sudo usermod -aG docker $USER
sudo usermod -aG docker qauser
sudo curl -L "https://github.com/docker/compose/releases/download/1.23.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod 755 /usr/local/bin/docker-compose
mv /usr/local/bin/docker-compose /usr/bin/
docker-compose --version
exec sudo su -l $USER
docker login igs-wms-docker-stable-local.artifactory-na.honeywell.com -u developer -p developer2017
