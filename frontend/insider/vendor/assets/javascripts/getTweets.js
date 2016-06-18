function getTweets(){
	console.log("dfghjkl")
	var url = '/api/getTweets'
	$.ajax({
		url:url,
		type:'GET',
		data:{},
		success:function(response){
			console.log(response);
		},
		error:function(err,status,error){
			console.log(error);
		},
		complete:function(complete){
		},
		dataType:'json'
	});
}