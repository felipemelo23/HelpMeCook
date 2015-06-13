
	<?php 

	if (isset($_GET[param])) {
		//CONNECTING
		include 'connect.php';
		
		//SQL BASE
		//-----------------------------------------------------------------------------------------
		$sql_name_search = "SELECT id, name, taste, difficulty, picture, description, estimatedtime, portionnum
							FROM Recipes WHERE name LIKE '%param%'";


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

		$sql_create_table_recipes = 'CREATE TABLE Recipes (
			id INT(6) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
			name VARCHAR(50) NOT NULL,
			taste FLOAT DEFAULT 0,
			difficulty FLOAT DEFAULT 0,
			picture TEXT,
			description TEXT NOT NULL,
			estimatedtime INT(3),
			portionnum VARCHAR(30),
			viewnum INT(9) DEFAULT 0,
			tastenum INT(9) DEFAULT 0,
			difficultynum INT(9) DEFAULT 0,
			reg_date TIMESTAMP
		)';

		//-----------------------------------------------------------------------------------------
		$recipes = array();
		$sql_name_search = str_replace("param", $_GET[param], $sql_name_search);
		$result = $mysqli->query($sql_name_search);


		if ($result->num_rows > 0) {
		    // output data of each row
		    while($row = $result->fetch_object()) {
		        $rec = new Recipe();
		        $rec->id = $row->id;
			    $rec->name = utf8_encode($row->name);
			    $rec->taste = $row->taste;
			    $rec->difficulty = $row->difficulty;
			    $rec->picture = utf8_encode($row->picture);
			    $rec->text = utf8_encode($row->description);
			    $rec->estimatedTime = $row->estimatedtime;
			    $rec->portionNum = $row->portionnum;

			    $sql_name_search_interaction = "SELECT * FROM RecipeIngredientRelation WHERE rid='param'";
			    $sql_name_search_interaction = str_replace("param", $row->id, $sql_name_search_interaction);
			    $result2 = $mysqli->query($sql_name_search_interaction);
			    $ing = array();
			    $uni = array();
			    while($row2 = $result2->fetch_object()) {
			    	array_push($rec->ingredientList, $row2->iid);
			    	array_push($rec->units, utf8_encode($row2->quantity));
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
