var orm = require('orm');

var db = orm.connect('mysql://root:cmpe275@192.168.42.2/cmpe275project2');

db.on('connect', function (err, db) {
  if (err) throw err;
  console.log('connected to db');
});

module.exports = db;
