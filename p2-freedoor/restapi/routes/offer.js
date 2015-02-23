var Offer=require('../lib/offer.js');
var History = require('../lib/history.js');
var async = require('async');
var Product = require('../lib/product.js');
var Category = require('../lib/category.js');
var User = require('../lib/user.js');

module.exports = function(router){
  
router.post('/:categoryId/product/:productId/offer', function(req, res) {

  console.log(req.params);

  //check product null
if ( typeof req.params.productId === 'undefined' && !req.params.productId)
{ 

  res.status(404).json({err: "product not found!!"});
  return;
}

//check category null
if ( typeof req.params.categoryId === 'undefined' && !req.params.categoryId)
{ 
  res.status(404).json({err: "Category not found!!"});
  return;
}

//check user null
if ( typeof req.body.buyerId === 'undefined' && !req.body.buyerId)
{ 
  res.status(404).json({err: "Buyer not found!!"});
  return;
}



if (["accepted", "rejected", "pending"].indexOf(req.body.buyerStatus.trim().toLowerCase()) <0)
{ 
  res.status(404).json({err: "Buyer status is invalid"});
  return;
}

if ( ["accepted", "rejected", "pending"].indexOf(req.body.sellerStatus.toLowerCase()) < 0)
{ 
  res.status(404).json({err: "Seller status is invalid"});
  return;
}



Category.find({categoryId: req.params.categoryId}, function (err, category_res) 
    {

    if(category_res.length === 0){
   
      res.status(404).json({err: "requested category not found"});
      return;
    }
    });
    
  Product.find({productId: req.params.productId}, function (err, product_res) 
  {
  if(product_res.length === 0){
    res.status(404).json({err: "requested product not found"});
    return;
  }

  });
  
  User.find({userId: req.body.buyerId}, function (err, users) 
      {
       
      if(users.length === 0){
        res.status(404).json({err: "requested buyer does not exist"});
        return;
      }});
      


  if(req.body.buyerStatus==="accepted" && req.body.sellerStatus==="accepted")
  { 
     Product.find({productId: req.params.productId}, function (err, product_res) 
      {
        var prodQuant=product_res[0].quantity;
    
          if (prodQuant>=req.body.buyingQty)
          {
            prodQuant-=req.body.buyingQty;
            product_res[0].quantity= prodQuant;
              if(prodQuant===0)
              {
                product_res[0].isValid=false;
              }
            product_res[0].save();
          }
          else 
          {
            res.status(500).json({err: "requested product quantity is not available"});
          } 

      });
    
  } //if condition to check sellerStatus and buyerStatus

  var offer = req.body;
  offer.productId = req.params.productId;
  offer.lastModified = new Date();
  Offer.create(offer, function (err, offer_res) {
      if (err) 
      res.status(500).json({err: err});
      else {
        res.status(201).json(offer_res);
      }
  });
}); //End of post method

  router.get('/:categoryId/product/:productId/offer', function(req, res) {

    Offer.find({productId: req.params.productId}, function (err, offer_res) {
      if (err) 
        res.status(500).json({err: err});
      else {
        res.json({ offers: offer_res });
      }
    });
  });

  router.get('/:categoryId/product/:productId/offer/:offerId', function(req, res) {

    Offer.find({offerId: req.params.offerId}, function (err, offers) {
      if (err) 
        res.status(500).json({err: err});
      else {
        if(!offers.length)
          res.status(404).end();
        else
          res.json(offers[0]);
      }
    });
  });

  router.put('/:categoryId/product/:productId/offer/:offerId', function(req, res) {

    var newOffer = req.body;
    var modified = {};
    var isModified = false;


      if (["accepted", "rejected", "pending"].indexOf(req.body.buyerStatus.trim().toLowerCase()) <0)
{ 
  res.status(404).json({err: "Buyer status is invalid"});
  return;
}

if ( ["accepted", "rejected", "pending"].indexOf(req.body.sellerStatus.toLowerCase()) < 0)
{ 
  res.status(404).json({err: "Seller status is invalid"});
  return;
}






    if ( typeof req.params.productId === 'undefined' && !req.params.productId)
    { 

      res.status(404).json({err: "product not found!!"});
      return;
    }

//check category null
    if ( typeof req.params.categoryId === 'undefined' && !req.params.categoryId)
    { 
      res.status(404).json({err: "Category not found!!"});
      return;
    }

//check user null
    if ( typeof req.body.buyerId === 'undefined' && !req.body.buyerId)
    { 
      res.status(404).json({err: "Buyer not found!!"});
      return;
    }

    Category.find({categoryId: req.params.categoryId}, function (err, category_res) 
    {

      if(category_res.length === 0)
      {
     
        res.status(404).json({err: "requested category not found"});
        return;
      }
    });
    
    Product.find({productId: req.params.productId}, function (err, product_res) 
    {
      if(product_res.length === 0)
      {
        res.status(404).json({err: "requested product not found"});
        return;
      }
    });
  
   User.find({userId: req.body.buyerId}, function (err, users) 
   {
       
      if(users.length === 0)
      {
        res.status(404).json({err: "requested buyer does not exist"});
        return;
      }
   });

 

    Offer.find({offerId: req.params.offerId}, function (err, offers) {
      if (err) 
        res.status(500).json({err: err});
      else { 
        if (!offers.length){ // didn't find offer
          res.status(404).end();
        }
        else { // found offer
          var offer = offers[0]; // get the first one

          // try to update offer
          for (prop in newOffer){
            if (!(/Id$/.test(prop)) && prop !== 'lastModified'){ // if not an id and not lastModified
              var newOfferValue = newOffer[prop];
              var currentOfferValue = offer[prop];
              if (newOfferValue !== currentOfferValue){ // if new offer value is different
                offer[prop] = newOfferValue; // update
                modified[prop] = {old: currentOfferValue, 'new': newOfferValue};
                isModified = true;
              }
            }
          }

          if(!isModified){ // not modified
            res.json(offer);
          }
          else
          { // modified
            // Prepare items to insert/update

            if(req.body.buyerStatus==="accepted" && req.body.sellerStatus==="accepted")
            { 
                Product.find({productId: req.params.productId}, function (err, product_res) 
                  {
                    var prodQuant=product_res[0].quantity;
                
                      if (prodQuant>=req.body.buyingQty)
                      {
                        prodQuant-=req.body.buyingQty;
                        product_res[0].quantity= prodQuant;
                          if(prodQuant===0)
                          {
                            product_res[0].isValid=false;
                          }
                        product_res[0].save();

                     }//prodquant if 
                     else 
                      {
                        res.status(500).json({err: "requested product quantity is not available"});
                        return;
                      }   
                   });
                
            }
            
                offer.offerExpiry = offer.offerExpiry ? new Date(offer.offerExpiry) : offer.offerExpiry;
                offer.lastModified = new Date();
                var history = {
                  modified: modified,
                  lastModified: offer.lastModified,
                  offerId: req.params.offerId
                };

                // Prepare insert/update task
                var tasks = [
                  function(callback){
                    offer.save(callback);
                  },
                  function(callback){
                    History.create(history, callback);
                  }
                ];

                async.parallel(tasks, function(err, data){
                  if (err){
                    res.status(500).json({err: err});
                    // It would be nice to rollback.
                  }
                  else {
                    res.json(offer);
                  }
                });
           
            

         
         }// else modified
        } // end found offer
      } // end Offer.find !err
    }); // end Offer.find
  }); // end router.put

  router.delete('/:categoryId/product/:productId/offer/:offerId', function(req,res){
    Offer.find({offerId: req.params.offerId}).remove(function (err) {
      if (err)
        res.status(500).json({err: err});
      else {
        res.status(200).end();
      }
    });
  });
}
