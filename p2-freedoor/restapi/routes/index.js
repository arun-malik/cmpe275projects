var express = require('express');
var router = express.Router();
var os = require('os');

router.get('/', function(req, res) {
  res.json({

    project : {
      title : "freedoor",
      subtitle : "rest api for a barter system",
      class : {
        course : "cmpe 275",
        instructor : "John Gash",
        semester: "fall 2014",
        notes : "project 2"
      },
      authors: [
          "Prasad Bidwai",
          "Sai Karra",
          "Asley Love",
          "Arun Malik",
          "Ryan Vo"
      ],
      technologies : [
          "node.js",
          "express.js",
          "node-orm",
          "mysql // Ugh..."
      ]
    },

    system : {
      hostname: os.hostname(),
      arch: os.arch(),
      os: os.type(),
      osplatform: os.platform(),
      osrelease : os.release(),
      uptime: os.uptime(),
      loadavg: os.loadavg(),
      totalmem: os.totalmem(),
      freemem: os.freemem(),
      cpus: os.cpus(),
      network: os.networkInterfaces()
    }
  });
});

module.exports = router;
