class ApiController < ApplicationController
  def index
  	# wikipedia API
	require 'wikipedia'
	query = 'iphone'
  	@page = Wikipedia.find( query )

  	# amazon API
	require 'vacuum'
	request = Vacuum.new('UK')
	request.configure(
	    aws_access_key_id: 'AKIAIOFOSMSJUOFHJX5A',
	    aws_secret_access_key: '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',
	    associate_tag: 'tag'
	)
	@response = request.item_search(
	  query: {
	    'Keywords' => query,
	    'SearchIndex' => 'All'
	  }
	)
	asin = @response.to_h['ItemSearchResponse']['Items']['Item'][0]['ASIN']
	@result = request.item_lookup(
	  query: {
	    'ItemId' => asin
	  }
	)
	@item = @result.to_h['ItemLookupResponse']['Items']['Item']

	# twitter API
	client = Twitter::Streaming::Client.new do |config|
	  config.consumer_key        = "uFnrltP2gGJ4Muyub303AHiL9"
	  config.consumer_secret     = "VJxvTEG0iE3xNaeKs2iNuqsBLIhb728hWWnBe53Ebnmwe6tigY"
	  config.access_token        = "739405916559249408-s2kaVDajJqO4e5NC3RLLrzz2p0n8o0k"
	  config.access_token_secret = "7jgGcGjK5gz837v8rGoK2kryZqRX8wJArrv79CcvyYt4C"
	end

	client = Rails.cache.read("client")
	topics = [query,query.gsub(' ','_'),query.gsub(' ','')]
	client.filter(track: topics.join(",")) do |tweet|
		obj = {'id' => tweet.id,'text' => tweet.text,'favorite_count' => tweet.favorite_count,'retweet_count' => tweet.retweet_count,'created_at' => tweet.created_at}
		byebug
	end
  end
end
