db = connect('mongodb://localhost/RnW');


db.createCollection("Users", );

db.Users.createIndex({"mail":1}, {unique:true});

db.createCollection("Texts", );

db.createCollection("Reports", );