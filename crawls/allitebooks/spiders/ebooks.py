# -*- coding: utf-8 -*-
import scrapy


class EbooksSpider(scrapy.Spider):
    name = 'ebooks'
    allowed_domains = ['allitebooks.org']
    def start_requests(self):
        yield scrapy.Request(url='http://allitebooks.org',callback=self.requestAllPages,dont_filter=True)

    def requestAllPages(self,response):
        nums = response.xpath("//div[@class='pagination clearfix']/a[last()]/text()").get()
        print 'numbers of totalPages is %s' % nums
        
        for num in range(1,int(nums)+1):
            #if num==1 :
             #   yield scrapy.Request('http://allitebooks.org',self.parsePage,True)
            #else:
                yield scrapy.Request('http://allitebooks.org/page/%d/' % num,self.parsePage,True)
            
    def parsePage(self, response):
        urls = response.xpath("//article/div/header/h2/a/@href").getall()
        for url in urls:
            yield response.follow(url,self.parse)
    
    def parse(self, response):
        name = response.xpath("//h1/text()").get()
        img = response.xpath("//div[@class='entry-meta clearfix']/div[1]/a/img/@src").get()
        author = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[1]/a/text()").get()
        isbn = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[2]/text()").get()
        year = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[3]/text()").get()
        pages = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[4]/text()").get()
        lang = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[5]/text()").get()
        size = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[6]/text()").get()
        f0rmat = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[7]/text()").get()
        category = response.xpath("//div[@class='entry-meta clearfix']/div[2]/dl/dd[8]/a/text()").get()
        url = response.xpath("//footer[@class='entry-footer clearfix']/div/span[1]/a/@href").get()
        yield {'name':name,'img':img,'author':author,'isbn':isbn,'year':year,'pages':pages,'lang':lang,'size':size,'format':f0rmat,'category':category,'url':url}
