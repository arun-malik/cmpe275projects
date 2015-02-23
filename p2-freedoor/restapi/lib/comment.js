var db = require('../lib/db.js');

var Comment = db.define("comment", {
  commentId: {type: 'serial', key: true},
  commentDesc: String,
  lastUpdated: {type: 'date', time: true},
  offerId: Number,
  userId: Number
});

Comment.sync(function (err) {
  if (err) throw err;
  console.log('Comment DB synced');
});

module.exports = Comment;
