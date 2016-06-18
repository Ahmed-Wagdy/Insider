function drawSentimentChart(){
	var url = '/api/getSentimentData'
	$.ajax({
		url:url,
		type:'GET',
		data:{
		},
		success:function(response){
			drawChart(response.xAxis,response.yAxis,'bar','sentimentChart');
		},
		error:function(err,status,error){
			console.log(error);
		},
		complete:function(complete){
		},
		dataType:'json'
	});
}