# applicant-service- Set up Jenkins in local

docker run -d -p 49001:8080 -v $PWD/jenkins:/var/jenkins_home:z -t jenkins/Jenkins
the above command was having issue in connecting to docker deamon so used the below command

docker run   -p 8081:8080   -v /var/run/docker.sock:/var/run/docker.sock   --name jenkins   getintodevops/jenkins-withdocker:lts

Changed the port to 8081 if port is in use already.
Refer this from the link:
https://getintodevops.com/blog/the-simple-way-to-run-docker-in-docker-for-ci 

Can delete the used port with below commands
sudo lsof -i :8080
sudo kill -9 PID

check the docker logs for initial login password
docker logs containerId
or we can get by running 
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

Refer to this video for setup
https://www.youtube.com/watch?v=gdbA3vR2eDs&t=1071s
CI CD of docker containers



Create a new pipeline project 
1.	Start Jenkins then install the plugin
2.	Maven, docker, sshAgent and go to Manage Jenkins -> Global Tool Config and set up the path for jdk, docker & maven.

3.	Eneter the github project as
https://github.com/AjitNitrkl/applicant-service/

4.	And pipeline script as below 
node{
   stage('SCM Checkout'){
       git credentialsId: 'f0656284-c083-4ab6-882b-3667cfa64912', url: 'https://github.com/AjitNitrkl/applicant-service'
   }
   stage('Mvn Package'){
     def mvnHome = tool name: 'mavenHome', type: 'maven'
     def mvnCMD = "${mvnHome}/bin/mvn"
     sh "${mvnCMD} clean package"
   }
   
   stage('Build Docker Image'){
     sh 'docker build -t ajitnitrkl/applicant-service .'
   }
   
    stage('Push Docker Image'){
     withCredentials([string(credentialsId: 'docker-hub-pwd', variable: 'dockerHubPwd')])  {
        sh "docker login -u ajitnitrkl -p ${dockerHubPwd}"
     }
     sh 'docker push ajitnitrkl/applicant-service'
   }
   
   stage('Run Container on Dev Server'){
     def dockerRun = 'docker run -p 8082:8080 -d --name applicant-service ajitnitrkl/applicant-service'
     sshagent(['dev-server']) {
       sh "ssh -o StrictHostKeyChecking=no ec2-user@ec2-54-174-40-221.compute-1.amazonaws.com ${dockerRun}"
     }
   }
   
}

mavenHome, dockerHome variable names are those name entered in step 2.
For password of dockerhub use the pieline syntac to generate the script. Use WithCredential: Bind Credential to Variable option and Add as secret text.

For ssh Agent of ec2 user use Ssh Agent and Add as Ssh username with privatekey


Here we are deploying in ec2 server:

Make sure in ec2 we are allowing traffic for port 8082 (for testing we can allow all traffic)
Start an ec2 install docker
1.	ssh -i awskey.pem ec2-54-174-40-221.compute-1.amazonaws.com
2.	sudo yum install -y docker
3.	sudo yum update
4.	check docer version docker –v
5.	on running the build we may get unable to connect docker daemon so we run the below command
sudo usermod -aG docker $(whoami)   --adding ec2-user to docker group. After executing this make sure restart the ec2 server.

NOTE:

If not running we can do like
Running docker directly in ec2 console
'docker run -p 8082:8080 -d --name applicant-service ajitnitrkl/applicant-service
if we get daemon error we can run sudo usermod -aG docker $(whoami) as above and restart ec2 console and also docker service in ec2 as well.
sudo service docker stop && sudo service docker start
Now access the api
http://ec2-54-174-40-221.compute-1.amazonaws.com:8082/
http://ec2-54-174-40-221.compute-1.amazonaws.com:8082/getApplicants

or curl it in logged in ec2 server:

curl http://localhost:8082
curl http://localhost:8082/getApplicants
