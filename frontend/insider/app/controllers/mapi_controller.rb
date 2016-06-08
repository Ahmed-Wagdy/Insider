class MapiController < ApplicationController
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
	twitter_keys_id = Rails.application.config.twitter_keys_itr
	twitter_keys_count = Rails.application.config.twitter_keys_count
	tkey = Tkey.find(twitter_keys_id)
	twitter_keys_id = twitter_keys_id % twitter_keys_count + 1
	Rails.application.config.twitter_keys_itr = twitter_keys_id
	client = Twitter::Streaming::Client.new do |config|
	  config.consumer_key        = tkey.consumer_key
	  config.consumer_secret     = tkey.consumer_secret
	  config.access_token        = tkey.access_token
	  config.access_token_secret = tkey.access_token_secret
	end

	# client = Rails.cache.read("client")
	topics = [query,query.gsub(' ','_'),query.gsub(' ','')]

	client.filter(track: topics.join(",")) do |tweet|
		obj = {'id' => tweet.id,'text' => tweet.text,'favorite_count' => tweet.favorite_count,'retweet_count' => tweet.retweet_count,'created_at' => tweet.created_at}
		byebug
	end
  end
end
