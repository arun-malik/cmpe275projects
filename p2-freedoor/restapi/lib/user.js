var db = require('../lib/db.js');

var User = db.define("user", {
  userId: {type: 'serial', key: true}, // the auto-incrementing primary key
  firstName: String,
  lastName: String,
  emailId: String,
  mobile: String
});

User.sync(function (err) {
  if (err) throw err;
  console.log('user synced');
});

module.exports = User;
