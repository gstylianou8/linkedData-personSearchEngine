<!DOCTYPE html>
<html>
<body>
	<h2>Search for People</h2>
	<form action = './search.php' method='get'>
		<input type = 'text' name = 'k', size = '50' value = '<?php echo $_GET['k']; ?>' />
		<input type = 'submit' name = 'Search' /> 		
	</form>
	<hr />

	<?php
		$value = $_GET['k'];
		exec("java -jar search.jar $value", $output);
		
		for ($x = 0; $x < sizeof($output); $x++) {
    		echo $output[$x]."<br>";
		} 
	?>
</body>
</html>