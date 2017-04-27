'use strict';

const awsIot = require('aws-iot-device-sdk');

let client, iotTopic;
const IoT = { 

    connect: (topic, iotEndpoint, region, accessKey, secretKey, sessionToken) => {

        iotTopic = topic;

        client = awsIot.device({
            region: region,
            protocol: 'wss',
            accessKeyId: accessKey,
            secretKey: secretKey,
            sessionToken: sessionToken,
            port: 443,
            host: iotEndpoint
        });

        client.on('connect', onConnect);
        client.on('message', onMessage);            
        client.on('error', onError);
        client.on('reconnect', onReconnect);
        client.on('offline', onOffline);
        client.on('close', onClose);     
    },

    send: (message) => {
        client.publish(iotTopic, message);
    }  
}; 

const onConnect = () => {
    client.subscribe(iotTopic);
    $('.connect').toggleClass('hidden');
    console.log('Connected');
};

const onMessage = (topic, message) => {
    showMessage(message);
};

const onError = (error) => {
    console.log(`Error: {error}`);
};

const onReconnect = () => {
    console.log('Reconnect');
};

const onOffline = () => {
    console.log('Offline');
};

const onClose = () => {
    console.log('Start connection to endpoint ...');
    console.log('Connection failed');
};

let iotKeys;

$(document).ready(() => {

    let userName;

    const getKeys = () => {
        console.log('Get keys ...');
        $('.login').toggleClass('hidden');
        $('.get-key').toggleClass('hidden');
        $.ajax({
            url: window.lambdaEndpoint,
            success: (res) => {
                console.log(`Got the keys ... Endpoint: ${res.iotEndpoint},
                      Region: ${res.region},
                      AccessKey: ${res.accessKey},
                      SecretKey: ${res.secretKey},
                      SessionToken: ${res.sessionToken}`);

                iotKeys = res; // save the keys
                userName = $('#username').val();

                $('.get-key').toggleClass('hidden');
                $('.ing').toggleClass('hidden');
                connect();
            }
        });
    };

    const connect = () => {
        console.log('Start connection to endpoint ...');
        const iotTopic = '/serverless/pubsub';
        IoT.connect(iotTopic,
                    iotKeys.iotEndpoint,
                    iotKeys.region,
                    iotKeys.accessKey,
                    iotKeys.secretKey,
                    iotKeys.sessionToken);
    };

    $('#btn-login').on('click', getKeys);

    const sendMessage = () => {
        const msg = $('#message').val();
        const message = {
            userName: userName,
            message: msg,
            sessionName: iotKeys.sessionName
        };
        IoT.send(JSON.stringify(message));
        $('#message').val('');
    };

    $('#btn-send').on('click', sendMessage);
    $('#message').on('keypress', (e) => {
        if (e.keyCode == 13){
            sendMessage();
        }
    });
});

const showMessage = (msg) => {
    const message = JSON.parse(msg);
    const date = (new Date()).toTimeString().slice(0, 8);
    let position;
    if (message.sessionName == iotKeys.sessionName) {
        position = 'left';
    } else {
        position = 'right';
    }
    let text = message.message;
    $("#messages").prepend(`<div class='message ${position}'>
        <div class='user'>${message.userName}</div>
        <div class='text'>${text}</div>
    </div>`);
};