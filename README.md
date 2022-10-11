# ATA-Capstone-Project

Follow the instructions in the course for completing the group Capstone project.

### Fill out the environment variables
Complete `setupEnvironment.sh` with the group repo name and the github username of the team member holding the repo.
Confirm these are in lower case.
The repo owner should confirm that all team members have been added to collaborate on the repo.

### To create the Lambda Example table in DynamoDB:

You must do this for the ServiceLambda to work!

```
aws cloudformation create-stack --stack-name user-table --template-body file://LambdaExampleTable.yml --capabilities CAPABILITY_IAM
```

### To deploy the Development Environment

Run `./deployDev.sh`

As you are taking a break from work, use the END LAB button in Vocareum instead of removing the pipeline each time.
The End Lab button will pause the lab and resources, not allowing the budget to be used. When you're ready to start again,
click the Start Lab button to begin again with renewed AWS credentials.

To tear down the deployment then run `./cleanupDev.sh`

### To deploy the CI/CD Pipeline

Fill out `setupEnvironment.sh` with the url of the github repo and the username (in all lowercase) of the 
team member who is maintaining the repo. Confirm that the team member has added your username as a contributor to the repo.

Run `./createPipeline.sh`

As you are taking a break from work, use the END LAB button in Vocareum instead of removing the pipeline each time.
The End Lab button will pause the lab and resources, not allowing the budget to be used. When you're ready to start again,
click the Start Lab button to begin again with renewed AWS credentials.

To teardown the pipeline, run `./cleanupPipeline.sh`

###Running the app on the browser:

Step 1: Start vocareum and have the credentials on your computer

Step 2: start docker -> you can use `local-dynamobd.sh` script in the repo. type `docker ps` into terminal and it should show the container
            eg:

            $ docker ps
            CONTAINER ID   IMAGE                   COMMAND                  CREATED              STATUS              PORTS                    NAMES
            3386bc089f37   amazon/dynamodb-local   "java -jar DynamoDBL…"   About a minute ago   Up About a minute   0.0.0.0:8000->8000/tcp   lbc-app-dynamodb

step 3: bootRunDev

Step 4: go to `localhost:5001/summary.html` in the browser

step 5: type `dynamodb-admin` into the terminal
            eg:

           ` $ dynamodb-admin`

            `DYNAMO_ENDPOINT is not defined (using default of http://localhost:8000)`
            `database endpoint:    http://localhost:8000`
            `region:               us-east-1`
            `accessKey:            ASIA6ELVVV4WAKX3WJGU`
            
            dynamodb-admin listening on http://:::8001 (alternatively http://0.0.0.0:8001)

step 6: go to `http://localhost:8001` to see the local DDB GUI

step 7: open terminal in intelliJ or go to project in any other terminal and run this script to hydrate DB
`aws dynamodb batch-write-item --request-items file://gameSummarySeedData.json --endpoint-url http://localhost:8000`

### Closing down the app

step 1: close all the windows opened in the browser

step 2: close the `dynamodb-admin` in terminal eg. `ctrl + c` kills in gitbash

step 3: stop the docker container
- `docker ps` shows the open containers
- copy the container ID and enter into `docker stop {containerId}` run in terminal
  - eg: `$ docker stop 44eb1ad3e554`

step 4: close/stop bootRunDev

step 5: shut down vocareum




