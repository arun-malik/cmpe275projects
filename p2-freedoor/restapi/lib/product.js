//var datetime = require('datetime'); unused
var db = require('../lib/db.js');
var Category = require('../lib/category.js');

var Product = db.define("product", {
  productId: {type: 'serial', key: true},
  productName: String,
  quantity: Number,
  userId: Number,		//Need to be int? yes
  expectedOffer: String,
  productDesc: String,
  productExpiryDate: {type: 'date', time: true},
  isValid: Boolean,		//Need to be bit?
  categoryId: Number,
  lastUpdated: {type: 'date', time: true}		//Need to be DateTime?
});

// Foreign keys don't seem to work with ORM module. It doesn't get checked.
/*Product.hasOne('category', Category, { required: true, alwaysValidate: true, key: true});*/

Product.sync(function (err) {
  if (err) throw err;
  console.log('Product DB synced');
});

module.exports = Product;
