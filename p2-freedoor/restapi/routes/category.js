var express = require('express');
var router = express.Router();
var Category = require('../lib/category.js');
var redisClient = require('../lib/redisClient.js');

require('../routes/product.js')(router);
require('../routes/offer.js')(router);
require('../routes/history.js')(router);
require('../routes/comment.js')(router);

/***** category ****/
router.post('/', function(req, res) {

	var category = req.body;

	Category.create(category, function (err, category) {
		if (err) {
			res.status(500).json({err: err});
		}
		else {
			// respond then cache
			res.status(201).json(category);
			redisClient.set('/category/' + category.categoryId, JSON.stringify(category), function(err, res){
				if (!err) console.log('cached');
			});
		}
	});
});

router.get('/', function(req, res) {
	Category.find({}).offset(+req.query.offset || undefined).limit(+req.query.limit || undefined).run( function (err, categories) {
		if (err) 
			res.status(500).json({err: err});
		else {
			res.json({ categories: categories});
		}
	});
});

router.get('/:categoryId', function(req, res) {
	redisClient.get('/category/' + req.params.categoryId, function(err, doc){
		if (doc){
			res.json(JSON.parse(doc));
			console.log('hit');
		}
		else { // else err or couldn't find doc
			Category.find({categoryId: req.params.categoryId}, function (err, categories) {
				if (err) 
					res.status(500).json({err: err});
				else {
					res.json(categories[0]);
					redisClient.set('/category/' + categories[0].categoryId, JSON.stringify(categories[0]), function(err, res){
						if (!err) console.log('cached');
					});
				}
			});
		}
	});
});

module.exports = router;
