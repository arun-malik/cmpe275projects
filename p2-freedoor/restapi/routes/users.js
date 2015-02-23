var express = require('express');
var router = express.Router();
var User = require('../lib/user.js');

router.post('/', function(req, res) {

	var user = req.body;

	User.create(user, function (err, users) {
		if (err) 
			res.status(500).json({err: err});
		else {
			res.status(201).json(users);
		}
	});
});

router.get('/', function(req, res) {

	User.find({}).offset(+req.query.offset || undefined).limit(+req.query.limit || undefined).run( function (err, users) {
		if (err) 
			res.status(500).json({err: err});
		else {
			res.json(users);
		}
	});
});

router.get('/:userId', function(req, res) {

	User.find({userId: req.params.userId}, function (err, users) {
		if (err) 
			res.status(500).json({err: err});
		else {
			if (!users.length)
				res.status(404).end();
			else
				res.json(users[0]);
		}
	});
});

module.exports = router;
