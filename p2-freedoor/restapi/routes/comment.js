var Comment=require('../lib/comment.js');

/*****------------------- Comments -------------------------****/
module.exports = function(router){
//**** POST COMMENT ****//
  router.post('/:categoryId/product/:productId/offer/:offerId/comment', function(req, res) {

    var comment = req.body;

  Comment.create(comment, function (err, comment_res) {
      if (err) 
        res.status(500).json({err: err});
      else {
        res.status(201).json(comment_res);
      }
    });
  });

  //*** GET COMMENT(S) ****//
  router.get('/:categoryId/product/:productId/offer/:offerId/comment', function(req, res) {

  Comment.find({offerId:req.params.offerId}).offset(+req.query.offset || undefined).limit(+req.query.limit || undefined).run( 
        function (err, comment_res) {
      if (err) 
        res.status(500).json({err: err});
      else {
        res.json(comment_res);
      }
    });
  });

  //*** UPDATE COMMENT ****//
  router.put('/:categoryId/product/:productId/offer/:offerid/comment/:commentId', function(req, res) {
    Comment.find({commentId: req.params.commentId}, function (err, comment_res) {
      if (err)
        res.status(500).json({err: err});
      else {
        console.log(comment_res);
      }
    });
  });

  //*** DELETE COMMENT ****//
  router.delete('/:categoryId/product/:productIdoffer/:offerid/comment/:commentId', function(req,res){
    Comment.find({commentId: req.params.commentId}).remove(function (err) {
      if (err)
        res.status(500).json({err: err});
      else {
        res.status(200);
      }
    });
  });
}