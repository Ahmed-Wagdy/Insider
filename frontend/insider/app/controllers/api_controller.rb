class ApiController < ApplicationController
  def index
	require 'wikipedia'
  	@page = Wikipedia.find( 'iphone' )

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
  end
end
