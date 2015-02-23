var db = require('../lib/db.js');

var History = db.define('history', {
  offerHistoryId: {type: 'serial', key: true}, // the auto-incrementing primary key
  modified: Object,
  lastModified: {type: 'date', time: true},
  offerId: Number
});

History.sync(function (err) {
  if (err) throw err;
  console.log('history synced');
});

module.exports = History;
