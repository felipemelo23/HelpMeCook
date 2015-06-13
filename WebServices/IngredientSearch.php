<?php 
	
	if (isset($_GET[in])) {
		//CONNECTING
		include 'connect.php';

		$json_str = $_GET[in];	

		//PARSE FROM STRING TO OBJECT
		$jsonObj = json_decode($json_str);
		 
		//CREATES INGREDIENT ID ARRAY
		$ingredients = $jsonObj;


		//$sql_ingredient_search_base = str_replace("param1", $recipe->name, $sql_insert_recipe);
		
		//BASE
		//-----------------------------------------------------------------------------------------
		
		class Recipe { 
			public $id;
		    public $name;
		    public $taste;
		    public $difficulty;
		    public $picture;
		    public $ingredientList = array();
		    public $units = array();
		    public $text;
		    public $estimatedTime;
		    public $portionNum;

		}

		//-----------------------------------------------------------------------------------------
		$recipes = array();
		$inner_query = array();



		//PREPARANDO A QUERY SQL
		//------------------------------------------------------------------------------------------

		$ingredients_size = 0;
		foreach ($ingredients as $i) {
			$sql_in_ingredient_list = "(SELECT rid FROM RecipeIngredientRelation WHERE iid = 'param') AS T$i";
			$sql_in_ingredient_list = str_replace("param", $i, $sql_in_ingredient_list);
			array_push($inner_query,$sql_in_ingredient_list);
		}
		$sql_in_ingredient_list = join(" NATURAL JOIN ",$inner_query);
		//echo "INNER: $sql_in_ingredient_list <br />";

		$sql_ingredient_search_base = "SELECT id, name, taste, difficulty, picture, description, estimatedtime, portionnum
							FROM Recipes WHERE id IN (SELECT rid FROM (param1))";
		
		//------------------------------------------------------------------------------------------
		//TRABALHANDO COM out
		if (isset($_GET[out])) {
			$json_str = $_GET[out];	

			//PARSE FROM STRING TO OBJECT
			$jsonObj = json_decode($json_str);
			 
			//CREATES INGREDIENT ID ARRAY
			$ingredients = $jsonObj;

			$inner_query_out = array();



		// 	//PREPARANDO A QUERY SQL
			//------------------------------------------------------------------------------------------
			$ingredients_size = 0;
			foreach ($ingredients as $i) {
				$sql_in_ingredient_list_out = "(SELECT rid FROM RecipeIngredientRelation WHERE iid = 'param') AS U$i";
				$sql_in_ingredient_list_out = str_replace("param", $i, $sql_in_ingredient_list_out);
				array_push($inner_query_out,$sql_in_ingredient_list_out);
			}
			$sql_in_ingredient_list_out = join(" NATURAL JOIN ",$inner_query_out);

			$sql_ingredient_search_base = "SELECT id, name, taste, difficulty, picture, description, estimatedtime, portionnum
								FROM Recipes WHERE id IN (SELECT rid FROM (param1)) AND id NOT IN (SELECT rid FROM (param2))";
									$sql_ingredient_search_base = str_replace("param2", $sql_in_ingredient_list_out, $sql_ingredient_search_base);
			}

		//Subistitue param1 pela query formada anteriormente pela união das querys dos ingredientes
		$sql_ingredient_search_base = str_replace("param1", $sql_in_ingredient_list, $sql_ingredient_search_base);

		//echo "SQL: $sql_ingredient_search_base <br>";



		$result = $mysqli->query($sql_ingredient_search_base);


		if ($result->num_rows > 0) {
		    // output data of each row
		    while($row = $result->fetch_object()) {
		        $rec = new Recipe();
		        $rec->id = $row->id;
			    $rec->name = $row->name;
			    $rec->taste = $row->taste;
			    $rec->difficulty = $row->difficulty;
			    $rec->picture = $row->picture;
			    $rec->text = $row->description;
			    $rec->estimatedTime = $row->estimatedtime;
			    $rec->portionNum = $row->portionnum;

			    $sql_name_search_interaction = "SELECT * FROM RecipeIngredientRelation WHERE rid='param'";
			    $sql_name_search_interaction = str_replace("param", $row->id, $sql_name_search_interaction);
			    $result2 = $mysqli->query($sql_name_search_interaction);
			    $ing = array();
			    $uni = array();
			    while($row2 = $result2->fetch_object()) {
			    	array_push($rec->ingredientList, $row2->iid);
			    	array_push($rec->units, $row2->quantity);
			    	//$ing[] = $row2->iid;
			    	//$uni[] = $row2->quantity;
			    }
			    array_push($recipes, $rec);
			}
			$json_str = json_encode($recipes);
			echo "$json_str";
		} else {
		    echo "-1";
		}
	}

	$mysqli->close();
?>