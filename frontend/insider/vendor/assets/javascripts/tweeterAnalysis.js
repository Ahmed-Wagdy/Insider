function analyzeTweets(query){
	console.log(query);
	var url = '/api/tweetsAnalysis'
	$.ajax({
		url:url,
		type:'GET',
		data:{
			'query' : query
		},
		success:function(response){
		},
		error:function(err,status,error){
			console.log(error);
		},
		complete:function(complete){
		},
		dataType:'json'
	});
}