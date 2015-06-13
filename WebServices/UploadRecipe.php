
<?php 
	
	if (isset($_GET[name])) {
		//CONNECTING
		include 'connect.php';
		
		//SQL BASE
		//-----------------------------------------------------------------------------------------
		$sql_insert_recipe = "INSERT INTO Recipes (name, picture, description, estimatedtime, portionnum) 
										VALUES ('param1', 'param2', 'param3', 'param4', 'param5')";


		

		$sql_create_table_recipes = 'CREATE TABLE Recipes (
			id INT(6) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
			name VARCHAR(50) NOT NULL,
			taste FLOAT DEFAULT 0,
			difficulty FLOAT DEFAULT 0,
			picture TEXT,
			text TEXT NOT NULL,
			estimatedtime INT(3),
			portionnum VARCHAR(30),
			viewnum INT(9) DEFAULT 0,
			tastenum INT(9) DEFAULT 0,
			difficultynum INT(9) DEFAULT 0,
			reg_date TIMESTAMP
		)';

		$sql_drop_table = 'DROP TABLE IF EXISTS TempRecipes';

	//-----------------------------------------------------------------------------------------
		//INSERTED ID ARRAY
		$result = array();
		


		//READ JSON STRING
		//$json_str = $_GET[param];	




		//PARSE FROM STRING TO OBJECT
		//$jsonObj = json_decode($json_str);
		 
		//CREATE RECIPES ARRAY
		//$recipe = $jsonObj->recipe;

		//LAST ID
		$inserted_recipe_id = 0;

		//IMPRIME OS VALORES DECODIFICADOS DO JSON
		// echo "NOME: $_GET[name]<br />";
		// echo "PICTURE: $_GET[picture]<br />";
		// echo "DESCRICAO: $_GET[text]<br />";
		// echo "TEMPO: $_GET[time]<br />";
		// echo "NUMERO DE PORCOES: $_GET[portion]<br />";




		$sql_insert_recipe = str_replace("param1", $_GET[name], $sql_insert_recipe);
		$sql_insert_recipe = str_replace("param2", $_GET[picture], $sql_insert_recipe);
		$sql_insert_recipe = str_replace("param3", $_GET[text], $sql_insert_recipe);
		$sql_insert_recipe = str_replace("param4", $_GET[time], $sql_insert_recipe);
		$sql_insert_recipe = str_replace("param5", $_GET[portion], $sql_insert_recipe);

		


		$stmt = $mysqli->stmt_init();
		if ($stmt->prepare($sql_insert_recipe)) {
			//execultes the query
			$stmt->execute();

			//recovers inserted recipe id
			$inserted_recipe_id = $mysqli->insert_id;
			$result[] = $inserted_recipe_id;

			//closes statement and connection
			$stmt->close();
		} else {
			$result[] = -1;
		}

		$json_str = $_GET[ingredientList];
		$jsonObj = json_decode($json_str);
		$ing = $jsonObj;

		
		$json_str = $_GET[units];
		$jsonObj = json_decode($json_str);
		$uni = $jsonObj;


		$arrlength = count($ing);
		//echo 'LISTA DE INGREDIENTES: '.json_encode($recipe->ingredientList).'<br />';
		//$ing = $recipe->ingredientList;
		//echo 'LISTA DE INGREDIENTES: '.json_encode($ing).'<br />';
		//echo 'LISTA DE UNITS: '.json_encode($recipe->units).'<br />';
		//$uni = $recipe->units;
		//echo 'LISTA DE INGREDIENTES: '.json_encode($ing).'<br />';
		//echo 'LISTA DE INGREDIENTES: '.json_encode($ing[0]).'<br />';
		//echo 'LISTA DE INGREDIENTES: '.json_encode($ing[1]).'<br />';

		for($x = 0; $x < $arrlength; $x++) {
		    //INSERT INTO RECIPES
			$sql_ingredients = "INSERT INTO RecipeIngredientRelation(rid, iid, quantity) VALUES ('param1', 'param2', 'param3')";
			$sql_ingredients = str_replace("param1", $inserted_recipe_id, $sql_ingredients);
			$sql_ingredients = str_replace("param2", $ing[$x], $sql_ingredients);
			//echo "ID INGREDIENT CAMPO $x: $ing[$x]<br />";
			$sql_ingredients = str_replace("param3", $uni[$x], $sql_ingredients);
			//echo "UNIT INGREDIENT CAMPO $x: $uni[$x]<br />";

			$stmt = $mysqli->stmt_init();
			if ($stmt->prepare($sql_ingredients)) {
				//execultes the query
				$stmt->execute();

				//close statement and connection
				$stmt->close();
			}
		}



		$json_str = json_encode($result);
		echo "$json_str";
	} else{
		echo "Sem parâmetros";
	}

	

?>




