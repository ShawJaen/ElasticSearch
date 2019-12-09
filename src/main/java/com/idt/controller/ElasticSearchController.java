package com.idt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;




@Controller
public class ElasticSearchController {
	
	@Autowired
	private TransportClient client;
	
	@RequestMapping("getbyes.do")
	@ResponseBody
	public Object getbyes() {
		GetResponse response = client.prepareGet("index1", "blog", "10").execute().actionGet();
		System.out.println(response.getSource());
		return response.getSource();
	}
	/**
	 * 创建索引
	 * @throws IOException 
	 */
	@RequestMapping("createindex.do")
	@ResponseBody
	public void createindex() throws IOException {
		IndicesExistsResponse response =  client.admin().indices().prepareExists("indexblogs").execute().actionGet();
		if (response.isExists()) {
			client.admin().indices().prepareDelete("indexblogs").execute().actionGet();
		}
		client.admin().indices().prepareCreate("indexblogs").execute().actionGet();
		new XContentFactory();
		XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
				.startObject("blogs").startObject("properties")
				.startObject("title").field("type", "string")
				.field("store", "yes").field("analyzer", "ik_smart")
				.field("search_analyzer", "ik_smart").endObject()
				.startObject("content").field("type","string")
				.field("store", "yes").field("analyzer", "ik_smart")
				.field("search_analyzer", "ik_smart").endObject()
				.endObject().endObject().endObject();
		PutMappingRequest mapping = Requests.putMappingRequest("indexblogs").type("blogs")
				.source(builder);
		client.admin().indices().putMapping(mapping).actionGet();
/*		//获取admin的Api
		AdminClient adminClient = Client.admin();
		//对索引操作
		IndicesAdminClient indices = adminClient.indices();
		//准备创建索引
		indices.prepareCreate("blog").setSettings(
				Settings.builder()
				//指定索引分区数量
				.put("index.number_of_shards",4)
				//指定索引副本数量
				.put("index.number_of_replicas",2))
		.get();*/
	}
	
	/**
	 * 删除索引
	 */
	@RequestMapping("deleteindex.do")
	@ResponseBody
	public void deleteindex() {
		DeleteIndexResponse response = client.admin().indices().prepareDelete("blogs").get();
		System.out.println(response);
	}
	
	/**
	 * 添加数据
	 */
	@RequestMapping("insertdata.do")
	@ResponseBody
	public void insertdata() {
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "1、什么是lucene");
		map.put("content", "Lucene是一个全文搜索框架，而不是应用产品。因此它并不像http://www.baidu.com/ 或者google Desktop那么拿来就能用，它只是提供了一种工具让你能实现这些产品。");
		map.put("url", "http://www.baidu.com/");
		client.prepareIndex("indexblogs", "blogs").setSource(map).execute().actionGet();
	}
	
	/**
	 * 遇到我用的这个版本高亮语法改了之前有addHighlightedField..一系列语法，现在改为highlightBuilder
	 */
	@RequestMapping("searchdata.do")
//	@ResponseBody
	public String searchdata(Model m) {
		PageBean<blogsBean> wr = new PageBean<blogsBean>();
		wr.setIndex(1);
		MultiMatchQueryBuilder queryBuilder = new MultiMatchQueryBuilder("elasticsearch全文搜索拿来就用",
				new String[] {"title","content"});
		HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        highlightBuilder.field("title");      //高亮查询字段
        highlightBuilder.field("content");    //高亮查询字段
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style=\"color:red\">");   //高亮设置
        highlightBuilder.postTags("</span>");
		SearchResponse response=null;
		response = client.prepareSearch("indexblogs")
				.setTypes("blogs").setQuery(queryBuilder).highlighter(highlightBuilder)
				.setFrom(0)
				.setSize(10)
				.execute().actionGet();
		SearchHits searchHits = response.getHits();
		wr.setTotalCount((int) searchHits.getTotalHits());
		System.out.println("一共查询的记录："+searchHits.getTotalHits());
		Iterator<SearchHit> iterator = searchHits.iterator();
		while (iterator.hasNext()) {
			blogsBean bean =new blogsBean();
			SearchHit searchHit = (SearchHit) iterator.next();
			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
			HighlightField highlightField = highlightFields.get("content");
			if (highlightField != null) {
				Text[] texts = highlightField.getFragments();
				String content="";
				for (Text text : texts) {
					content +=text;
				}
				System.out.println("内容："+content);
				bean.setContent(content);
			}
			bean.setTitle(searchHit.getSource().get("title").toString());
			bean.setUrl(searchHit.getSource().get("url").toString());
			wr.setBean(bean);
		}
		m.addAttribute("page",wr);
		return "/index";
	}
	
	
	
	
	/*@RequestMapping("/search.do")
	public String search(String keyword,int num,int count,Model m){
		PageBean<HtmlBean> page =service.search(keyword, num, count);
		m.addAttribute("page", page);
		return "/index.jsp";
	}*/
	
	
	
	
}
