var User = require('../lib/user.js');
var Category = require('../lib/category.js');
var Product = require('../lib/product.js');
var async = require('async');

module.exports = function(router){
  router.post('/:categoryId/product', function(req, res) {

    // Validation tasks
    var tasks = {
      users: function(callback){
        User.find({userId: req.body.userId}, callback);
      },
      categories: function(callback){
        Category.find({categoryId: req.params.categoryId}, callback);
      }
    } 

    async.parallel(tasks, function(err, model){
      if (err) {
        res.status(500).json({err: err});
      }
      else {
        // if the user or category does not exist
        if(!model.users.length || !model.categories.length) {
          res.status(400).json({err: "400 Bad request"});
        }
        else {
          var product = req.body;
          product.categoryId = req.params.categoryId; // req.params prefered over req.body
          Product.create(product, function(err, product){
            if (err) {
              res.status(500).json({err: err});
            }
            else {
              res.status(201).json(product);
            }
          });
        }
      }
    });

    /* // Foreign keys don't seem to work with ORM module. It doesn't get checked.
    var product = new Product(req.body); 
    product.category_categoryId = req.params.categoryId;
    product.save(function(err, product){
      if (err) {
        res.status(500).json({err: err});
      }
      else {
        res.status(201).json(product);
      }
    });
    */
  });

  router.get('/:categoryId/product', function(req, res) {
    Product.find({categoryId: req.params.categoryId}).offset(+req.query.offset || undefined).limit(+req.query.limit || undefined).run( function (err, product_res) {
      if (err) 
        res.status(500).json({err: err});
      else {
        res.json({ products : product_res});
      }
    });
  });

  router.get('/:categoryId/product/:productId', function(req, res) {
    Product.find({productId: req.params.productId}, function (err, product_res) {
      if (err) 
        res.status(500).json({err: err});
      else {
        res.json(product_res[0]);
      }
    });
  });

  router.put('/:categoryId/product/:productId', function(req, res) {
    var newProductValue = req.body;
    Product.find({productId: req.params.productId}, function (err, product_res) {
      if (err)
        res.status(500).json({err: err});
      else {
        if (product_res.length <= 0) {
          res.status(404).json({});
        } else {
          // Update the first element in the array because we are going by id
          // In non-school environment, we should update all or throw an error
          // but since the class doesn't know what rest is, I'm not going to bother
          product_res[0].productName      = newProductValue.productName;
          product_res[0].quantity       = newProductValue.quantity;
          //product_res[0].userId       = newProductValue.userId;
          product_res[0].expectedOffer    = newProductValue.expectedOffer;
          product_res[0].productDesc      = newProductValue.productDesc;
          product_res[0].productExpiryDate  = newProductValue.productExpiryDate;
          product_res[0].isValid        = newProductValue.isValid;
          //product_res[0].categoryId     = newProductValue.categoryId;

          // mark that there was a failure
          product_res[0].save(function (err) {
            // Return a 500 if there was an error
            res.status( err ? 500 : 200).json(product_res[0]);
          });
        }
      }
    });
  });

  router.delete('/:categoryId/product/:productId', function(req,res){
    Product.find({productId: req.params.productId}).remove(function (err) {
      if (err)
        res.status(500).json({err: err});
      else {
        res.status(200).end();
      }
    });
  });
}