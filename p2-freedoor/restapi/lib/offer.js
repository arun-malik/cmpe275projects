var db = require('../lib/db.js');
var Offer = db.define("offer", {
	offerId : {type: 'serial', key: true},
	buyingQty : Number,
	offeredDetails : String,
	buyerStatus : String,
	sellerStatus : String,
	offerExpiry : {type: 'date', time: true},
	productId : Number,
	buyerId : Number,
	lastModified : {type: 'date', time: true},
	comments: String // probably should be an array or separate object
	//lastEvent: string
});


Offer.sync(function (err) {
	if (err) throw err;
	console.log('offer DB synced');
});

module.exports = Offer;
