var redis = require("redis");

// If something is wrong with redis, don't queue.
client = redis.createClient(6379, '127.0.0.1', {enable_offline_queue: false});

client.on('connect', function(err, res){
  console.log('connected to redis');
});

// Don't really do anything with errors.
client.on('error', function(err){
  console.log(err);
});

module.exports = client;
