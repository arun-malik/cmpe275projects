var History = require('../lib/history.js');

module.exports = function(router){
  router.get('/:categoryId/product/:productId/offer/:offerId/history', function(req, res) {

    History.find({offerId: req.params.offerId}).offset(+req.query.offset || undefined).limit(+req.query.limit || undefined).run( function (err, histories) {
    if (err) 
        res.status(500).json({err: err});
      else {
        res.json({history: histories});
      }
    });
  });
}