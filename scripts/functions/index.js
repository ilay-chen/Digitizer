const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.insertIntoDB = functions.https.onRequest((req, res) => {
	const len = req.query.num;
	const months = req.query.month;
	var arr = [];
	// return admin.database().ref('/keys/new').push(arr).then(snapshot => {
	// 	//res.redirect(303, snapshot.ref);
	// 	res.send("saved");
	// return null;
	// }).catch(error => {res.send("error");  })
	var database = admin.database().ref('/keys/new')

	return database.once('value', (snapshot) => {
		var arr = snapshot.val();
		if(arr===null)
			arr = [];

		for (var i = 0; i < len; i++) {
			arr.push({
				key: Math.random().toString(36).substring(2),
				months: months
			});
		}
		
		return database.set(arr).then(snapshot => {
			//res.redirect(303, snapshot.ref);
			res.send("saved");
		return null;
		}).catch(error => {res.send("error");  })
     });
    // return admin.database().ref('/keys').push({text: text}).then(snapshot => {
    //     res.redirect(303, snapshot.ref);
	// return null;
    // }).catch(error => {  })
});