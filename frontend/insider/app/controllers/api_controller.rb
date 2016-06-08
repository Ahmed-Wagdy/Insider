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
	    'Keywords' => 'sony',
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

	#get reviews
	require 'net/http'
	require 'uri'

	#url = URI.parse(@item['ItemLinks']['ItemLink'][2]['URL'])
	#res = Net::HTTP.get_response(url)
	#@data = Hash.from_xml(res.to_s).to_json

	require 'open-uri'
	@doc = Nokogiri::HTML(open(@item['ItemLinks']['ItemLink'][2]['URL']))
	 

	# foursquare API
	fs_client = Foursquare2::Client.new(:client_id => 'DFE2PJPNV0TVYZJ4ZPIUBHW4HQODKZPSYC5VOBC4BZWPZ5TK', :client_secret => 'IGACMN05NXCYRXTIMUAX0NOZZEUBKBWJLLMHYANXKBZEUHJH',:oauth_token => 'SNSENWFLDJNXMO54XDUOBIOVS4YRBX1RNAHCWJNQ4IVCDA2T',:api_version => '20140806')
	Rails.cache.write("fs_client",fs_client)

	# twitter API
	twitter_keys_id = Rails.application.config.twitter_keys_itr
	twitter_keys_count = Rails.application.config.twitter_keys_count
	tkey = Tkey.find(twitter_keys_id)
	twitter_keys_id = twitter_keys_id % twitter_keys_count + 1
	Rails.application.config.twitter_keys_itr = twitter_keys_id
	twitter_client = Twitter::Streaming::Client.new do |config|
	  config.consumer_key        = tkey.consumer_key
	  config.consumer_secret     = tkey.consumer_secret
	  config.access_token        = tkey.access_token
	  config.access_token_secret = tkey.access_token_secret
	end
	Rails.cache.write('twitter_client',twitter_client)
  end

  def searchList
  	query = params[:query]
  	type = params[:type]
  	if type == 'place'
  		require "net/http"
		require "uri"
		fs_client = Rails.cache.read("fs_client")
		# byebug
		# client_ip = request.remote_ip
		# client_ip = '41.234.19.65'
		client_ip = '81.21.107.92'
		uri = URI.parse('http://freegeoip.net/json/'+client_ip)
		response = Net::HTTP.get_response(uri)
		location = JSON.parse(response.body)
		geo = location["latitude"].to_s + ',' + location["latitude"].to_s
		@places = fs_client.search_venues(:ll => geo, :query => query)
		byebug
  	elsif type == 'product'
  	end
  	
  end

  def tweetsAnalysis
  	query = params[:query]
  	twitter_client = Rails.cache.read("twitter_client")
	topics = [query,query.gsub(' ','_'),query.gsub(' ','')]
	twitter_client.filter(track: topics.join(",")) do |tweet|
		obj = {'id' => tweet.id,'text' => tweet.text,'favorite_count' => tweet.favorite_count,'retweet_count' => tweet.retweet_count,'created_at' => tweet.created_at}
		byebug
	end
>>>>>>> c495ffe0ea0d85939bc102ef3ec11942501371cb
  end
end
