var db = require('../lib/db.js');

var Category = db.define("category", {
  categoryId: {type: 'serial', key: true},
  categoryName: String
});

Category.sync(function (err) {
  if (err) throw err;
  console.log('Category DB synced');
});

module.exports = Category;
