<?php
	
	//CONNECTING
	$mysqli = new mysqli('mysql.hostinger.com.br', 'u299697274_app', 'mg1~0q/#yp;1PRpwKFi', 
				'u299697274_csm');

	/* check connection */
	if (mysqli_connect_errno()) {
		printf("Connect failed: %s\n", mysqli_connect_error());
	    exit();
	}
	//$mysqli->set_charset("utf8");
	$mysqli->query("SET NAMES 'utf8'");
	$mysqli->query('SET character_set_connection=utf8');
	$mysqli->query('SET character_set_client=utf8');
	$mysqli->query('SET character_set_results=utf8');
	

	//SQL BASE
	//-----------------------------------------------------------------------------------------
	// $sql_insert_recipe = "INSERT INTO Recipes (name, picture, text, estimatedtime, portionnum) 
	// 								VALUES ('param1', 'param2', 'param3', 'param4', 'param5')";


	// $sql_ingredients = "INSERT INTO RecipeIngredientRelation(rid, iid, quantity) VALUES ('param1', 'param2', 'param3')";

	// $sql_create_table_recipes = 'CREATE TABLE Recipes (
	// 	id INT(6) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	// 	name VARCHAR(50) NOT NULL,
	// 	taste FLOAT DEFAULT 0,
	// 	difficulty FLOAT DEFAULT 0,
	// 	picture TEXT,
	// 	text TEXT NOT NULL,
	// 	estimatedtime INT(3),
	// 	portionnum VARCHAR(30),
	// 	viewnum INT(9) DEFAULT 0,
	// 	tastenum INT(9) DEFAULT 0,
	// 	difficultynum INT(9) DEFAULT 0,
	// 	reg_date TIMESTAMP
	// )';

	// $sql_drop_table = 'DROP TABLE IF EXISTS TempRecipes';

	//-----------------------------------------------------------------------------------------
?>

