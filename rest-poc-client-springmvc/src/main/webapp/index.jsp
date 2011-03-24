<html>
<head>
    <title>Standard REST usage</title>
	<script src="http://code.jquery.com/jquery-latest.js"></script>
	<link rel="stylesheet" href="http://dev.jquery.com/view/trunk/plugins/autocomplete/demo/main.css" type="text/css" />
	<link rel="stylesheet" href="http://dev.jquery.com/view/trunk/plugins/autocomplete/jquery.autocomplete.css" type="text/css" />
	<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/lib/jquery.bgiframe.min.js"></script>
	<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/lib/jquery.dimensions.js"></script>
	<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/autocomplete/jquery.autocomplete.js"></script>
	<script type="text/javascript">

		$(document).ready(
			
			function list() {
				$.ajax({
                    url : '/jersey-poc-server/resources/resources/product/list',
                    success : function(data) {
                        $("#list-response").html(data);
                    }
                });
			}
				
			function() {
				// find by description
			    var data = "aspegic aspirine doliprane prozac name".split(" ");
			    $("#find-product-by-description-term").autocomplete(data);
                $("#find-product-by-description-term").onChange(function () {
                    $.ajax({
                        url : '/jersey-poc-server/resources/product/find/by/description/' + $("#find-product-by-description-term").val(),
                        success : function(data) {
                            $("#list-response").html(data);
                        }
                    });
                	
                });
			}
			
            function() {
            	// list
                $("#list-products-button").onClick(list());

            }

            function() {
                // add
                var name = $("#add-product-name").val(); 
                var description = $("#add-product-description").val(); 
                $("#add-product-button").onClick(function() {
                    $.ajax({
                    	type : 'PUT',
                        url : '/jersey-poc-server/resources/product/add?name=' + name + '&description=' + description,
                        success : function(data) {
                            $("#add-response").html(data);
                            $("#update-product-id").html(data);
                            list();
                        }
                    });
                });
            }
            
            function() {
                // update
                var id = $("#update-product-id").val(); 
                var name = $("#update-product-name").val(); 
                var description = $("#update-product-description").val(); 
                $("#update-product-button").onClick(function() {
                    $.ajax({
                        type : 'POST',
                        url : '/jersey-poc-server/resources/product/update/id/' + id + '?name=' + name + '&description=' + description,
                        success : function(data) {
                            $("#update-response").html(data);
                            list();
                        }
                    });
                });
            }
            
            function() {
                // delete
                var id = $("#update-product-id").val(); 
                $("#delete-product-button").onClick(function() {
                    $.ajax({
                        url : '/jersey-poc-server/resources/product/delete/id/' + id,
                        success : function(data) {
                            $("#delete-response").html(data);
                            list();
                        }
                    });
                });
            }
		);
	
	</script>

</head>
<body>

<h4>Jersey RESTful Web Application!</h4>
<fieldset>
    <legend>List products</legend>
    <div><input id="list-products-button" type="button" value="list"/></div>
    <div id="list-response"></div>
</fieldset>
<fieldset>
    <legend>Add product</legend>
    <div>Name : <input id="add-product-name" type="text"/>&nbsp;Description : <textarea id="add-product-description" rows="3" COLS=40></textarea></div>
    <div><input id="add-product-button" type="button" value="Add"/></div>
    <div id="add-response"></div>
</fieldset>
<fieldset>
    <legend>Update product</legend>
    <div>Id : <input id="update-product-id" type="text"/>&nbsp;Name : <input id="update-product-name" type="text"/>&nbsp;Description : <textarea id="update-product-description" rows="3" COLS=40></textarea></div>
    <div><input type="button" value="Update"/></div>
    <div id="update-response"></div>
</fieldset>
<fieldset>
    <legend>Delete product</legend>
    <div>Id : <input id="delete-product-id" type="text"/></div>
    <div><input type="button" value="Delete"/></div>
    <div id="delete-response"></div>
</fieldset>
<fieldset>
    <legend>Find product by description</legend>
    <div>Term : <input id="find-product-by-description-term" type="text"/></div>
</fieldset>

</body>
</html>
