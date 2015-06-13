<?php 
	
	if (isset($_GET[id]) && isset($_GET[rate]) && isset($_GET[flag])) {
		//CONNECTING
		include 'connect.php';

		//SQL BASE
		//-----------------------------------------------------------------------------------------
		$sql_classify = "SELECT id, taste, difficulty, tastenum, difficultynum FROM Recipes WHERE id='param'";
		$sql_update_taste = "UPDATE Recipes SET taste='param1', tastenum='param2' WHERE id='param3'";
		$sql_update_difficulty = "UPDATE Recipes SET difficulty='param1', difficultynum='param2' WHERE id='param3'";

		//-----------------------------------------------------------------------------------------
		$sql_classify = str_replace("param", $_GET[id], $sql_classify);
		//FLAG PARA TASTE
		if ($_GET[flag] == 0) {
			$result = $mysqli->query($sql_classify);

			if ($result->num_rows > 0) {
		    // output data of each row
		    	$row = $result->fetch_object();
		        $tastenum = $row->tastenum;
		        $taste = $row->taste;
		        // echo "TASTENUM antes: $tastenum <br />";
		        // echo "TASTE antes: $tastenum <br />";

		        $newTaste = (($tastenum * $taste)+$_GET[rate])/($tastenum +1);
		        $tastenum = $tastenum + 1;

		        $sql_update_taste = str_replace("param1", $newTaste, $sql_update_taste);
		        $sql_update_taste = str_replace("param2", $tastenum, $sql_update_taste);
		        $sql_update_taste = str_replace("param3", $_GET[id], $sql_update_taste);
		        // echo "ID: $_GET[id] <br />";
		        echo "[$newTaste]";
		        // echo "newTASTENUM: $tastenum <br />";

		        $mysqli->query($sql_update_taste);
		    }


		} elseif ($_GET[flag] == 1) {
			$result = $mysqli->query($sql_classify);

			if ($result->num_rows > 0) {
		    // output data of each row
		    	$row = $result->fetch_object();
		        $difficultynum = $row->difficultynum;
		        $difficulty = $row->difficulty;
		        // echo "DIFFICULTYNUM antes: $difficultynum <br />";
		        // echo "DIFFICULTY antes: $difficulty <br />";

		        $newDifficulty = (($difficultynum * $difficulty)+$_GET[rate])/($difficultynum +1);
		        $difficultynum = $difficultynum + 1;

		        $sql_update_difficulty = str_replace("param1", $newDifficulty, $sql_update_difficulty);
		        $sql_update_difficulty = str_replace("param2", $difficultynum, $sql_update_difficulty);
		        $sql_update_difficulty = str_replace("param3", $_GET[id], $sql_update_difficulty);
		        // echo "ID: $_GET[id] <br />";
		        echo "[$newDifficulty]";
		        // echo "newDIFFICULTYNUM: $difficultynum <br />";

		        $mysqli->query($sql_update_difficulty);
		    }
		} else {
		    echo "[-1]";
		}
				
	}
	$mysqli->close();
?>