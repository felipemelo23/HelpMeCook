
	<?php
	header ('Content-type: text/html; charset=UTF-8'); 
	
	if (isset($_GET[param])) {
		//CONNECTING
		include 'connect.php';
		
		//SQL BASE
		//-----------------------------------------------------------------------------------------
		$sql_name_search = "SELECT id, name, taste, difficulty, picture, description, estimatedtime, portionnum, viewnum
							FROM Recipes WHERE id='param'";
		$sql_update_view = "UPDATE Recipes SET viewnum='param1' WHERE id='param2'";


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
		$sql_name_search = str_replace("param", $_GET[param], $sql_name_search);
		$result = $mysqli->query($sql_name_search);


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

			    $newView = $row->viewnum + 1;

			    $sql_update_view = str_replace("param1", $newView, $sql_update_view);
		        $sql_update_view = str_replace("param2", $_GET[param], $sql_update_view);
		        $mysqli->query($sql_update_view);
			}
			$json_str = json_encode($recipes);
			echo "$json_str";
		} else {
		    echo "-1";
		}



		
	}

	$mysqli->close();
	?>