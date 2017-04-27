# Serverless Chat

This project is a Scala conversion of the Diego Zanon's project (https://github.com/zanon-io/serverless-notifications) where
the notification system has been adapted to create a chat service.

## Usage

1. Inside the **create-role** folder, run `npm install` and `node index` to create an IoT role. I've named the role as **serverless-chat**. If you want to rename, modify this file and the **Handler.scala** file

2. Package the scala project with `mvn clean package`

3. Deploy the Lambda function with `serverless deploy`

4. Call the web page (implemented by **index.html**) with a query string that is the *Lambda* endpoint code (the first part of the uri), otherwize change the endpoint inside the file. The web page is placed inside the **frontend** folder

5. To modify the IoT client, follow these steps:  
    5.1 Browse the **iot** folder  
    5.2 Edit the **index.js** file   
    5.3 Install dependencies with `npm install`      
    5.4 Run `node make-bundle`  
    5.5 Replace the **bundle.js** inside the **frontend** folder by this new **bundle.js** file  
