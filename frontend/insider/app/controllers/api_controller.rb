class ApiController < ApplicationController
  def index

    # wikipedia API
    require 'wikipedia'

    # amazon API
    require 'vacuum'
    amazon_request = Vacuum.new('UK')
    #hast it on git hub only for keys

    amazon_request.configure(
        aws_access_key_id: 'AKIAIOFOSMSJUOFHJX5A',
        aws_secret_access_key: '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',
        associate_tag: 'tag'
    )
    Rails.cache.write("amazon_request", amazon_request)


    #asin = @response.to_h['ItemSearchResponse']['Items']['Item'][0]['ASIN']


    #get reviews
    require 'net/http'
    require 'uri'
    require 'open-uri'


    # foursquare API
    fs_client = Foursquare2::Client.new(:client_id => 'DFE2PJPNV0TVYZJ4ZPIUBHW4HQODKZPSYC5VOBC4BZWPZ5TK', :client_secret => 'IGACMN05NXCYRXTIMUAX0NOZZEUBKBWJLLMHYANXKBZEUHJH', :oauth_token => 'SNSENWFLDJNXMO54XDUOBIOVS4YRBX1RNAHCWJNQ4IVCDA2T', :api_version => '20140806')
    Rails.cache.write("fs_client", fs_client)

    # twitter API
    twitter_keys_id = Rails.application.config.twitter_keys_itr
    twitter_keys_count = Rails.application.config.twitter_keys_count
    tkey = Tkey.find(twitter_keys_id)
    twitter_keys_id = twitter_keys_id % twitter_keys_count + 1
    Rails.application.config.twitter_keys_itr = twitter_keys_id
    twitter_client = Twitter::Streaming::Client.new do |config|
      config.consumer_key = tkey.consumer_key
      config.consumer_secret = tkey.consumer_secret
      config.access_token = tkey.access_token
      config.access_token_secret = tkey.access_token_secret
    end
    Rails.cache.write('twitter_client', twitter_client)
  end

  def searchList
    query = params[:query]
    @query = query
    @myQuery=params[:query]
    type = params[:type]
    # require 'wikipedia'
    #@query_summary = Wikipedia.find(query)

    if type == 'place'
      require "net/http"
      require "uri"
      fs_client = Rails.cache.read("fs_client")
      # client_ip = request.remote_ip
      client_ip = '41.234.17.232'
      uri = URI.parse('http://freegeoip.net/json/'+client_ip)
      response = Net::HTTP.get_response(uri)
      location = JSON.parse(response.body)
      geo = location["latitude"].to_s + ',' + location["longitude"].to_s
      @places = fs_client.search_venues(:ll => geo, :query => query).venues

      render 'searchListPlace'
    elsif type == 'product'
      amazon_request = Rails.cache.read("amazon_request")
      response = amazon_request.item_search(
          query: {
              'Keywords' => query,
              'SearchIndex' => 'All'
          }
      )
      @products = response.to_h['ItemSearchResponse']['Items']['Item']
      render 'searchListProduct'
    end
  end

  def searchProfile
    # render 'searchProfileProduct'
      
      type = params[:type]
      itemid = params[:itemid]
      
      if type == 'place'
        fs_client = Rails.cache.read("fs_client")
        @item = fs_client.venue(itemid)
        byebug
        render 'searchProfilePlace'

      elsif type == 'product'
        amazon_request = Rails.cache.read("amazon_request")
        @result = amazon_request.item_lookup(
       query: {
         'ItemId' => itemid
       }
      )
      @item = @result.to_h['ItemLookupResponse']['Items']['Item']
      #crawler = Cobweb.new(:follow_redirects => false)
     #crawler.start(@item['ItemLinks']['ItemLink'][2]['URL'])
     @alldoc = Nokogiri::HTML(open(@item['ItemLinks']['ItemLink'][2]['URL']))
  
      render 'searchProfileProduct'
      #@doc = Nokogiri::HTML(open(@item['ItemLinks']['ItemLink'][2]['URL']))
      end
  end

  def tweetsAnalysis
    byebug
    require 'net/http'
    query = params[:query]
    postData = Net::HTTP.post_form(URI.parse('http://localhost:8080/stream/kafka'), {'keyword'=> query.gsub(' ', '')})
    # Net::HTTP.get_print(postData)
    puts "******************************************************************************"
    twitter_client = Rails.cache.read("twitter_client")
    topics = [query, query.gsub(' ', '_'), query.gsub(' ', '')]
    twitter_client.filter(track: topics.join(",")) do |tweet|
      obj = {'id' => tweet.id, 'text' => tweet.text, 'favoriteCount' => tweet.favorite_count, 'retweetCount' => tweet.retweet_count, 'createdAt' => tweet.created_at}

      # sending the twitter obj to a kafka topic, a kafka server should be running

      $kafka_producer.produce(tweet.text, topic: query.gsub(' ', ''))

      
      

      # producer.produce(obj, topic: query.gsub(' ', ''))
      byebug  

    end
  end
end