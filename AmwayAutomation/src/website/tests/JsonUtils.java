package website.tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.util.ArrayUtil;


/**
 * @author Sarathlal_S
 *
 */

public class JsonUtils {/*


	private final static Logger LOGGER = Logger.getLogger(JsonUtils.class.getName());
	public static APIUrls apiUrls=APIUrls.fetchAPIUrls();
	static String baseUrl=FrameworkProperties.SELENIUM_BASE_URL.replace("/?origin=prod", "").replace("en_us/Automation.html", "").replace("/?origin=beta", "").replace("/?origin=a-i1-prod-ch3", "");

	public static ProductDetails jsonProdFBTDetailsParser(String fbtUrl, ProductDetails productDetails){
		try{
			JSONParser parser = new JSONParser();
			String response=getResponse(fbtUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));

			Object objNew = parser.parse(response);
			JSONObject jsonObject  = (JSONObject) objNew; 	
			JSONArray jsonTempArray = (JSONArray) jsonObject.get("Recommendations");
			JSONArray jsonArrayFinal = null;
			JSONArray jsonArrayProductViewed = null;
			Map<String,String> fbt_Product_Id=new HashMap<String,String>();

			if(!(jsonTempArray.isEmpty())){

				for(int i=0;i<jsonTempArray.size();i++)
				{
					if(((JSONObject)jsonTempArray.get(i)).containsValue("fbt_overview") || ((JSONObject)jsonTempArray.get(i)).containsValue("novariants_fbt_overview"))
					{
						jsonArrayFinal = (JSONArray)((JSONObject)jsonTempArray.get(i)).get("strategyitems");

					}
					if(((JSONObject)jsonTempArray.get(i)).containsValue("pvt") )
					{
						jsonArrayProductViewed = (JSONArray)((JSONObject)jsonTempArray.get(i)).get("strategyitems");

					}

				}
				JSONArray jsonObjectArray = jsonArrayFinal;

				if(null != jsonObjectArray && !(jsonObjectArray.isEmpty())){
					productDetails.setIntFBTProductsCount(jsonObjectArray.size());
					double subtotal=0;
					double price=0;

					JSONObject product=null;
					for(int i=0;i<jsonObjectArray.size();i++){
						product= (JSONObject) jsonObjectArray.get(i);
						String productId=(String)product.get("productId"); 
						String productTitle=(String)product.get("title");
						fbt_Product_Id.put(productId, productTitle);
						String flag=(String)product.get("mapV");
						if(flag=="Y"){
							price=Double.parseDouble((String)product.get("regPrice"));
							subtotal=subtotal+price;
						}else{
							price=Double.parseDouble((String)product.get("curPrice"));
							subtotal=subtotal+price;
						}
					}

					// Used for FBT validation tests
					productDetails.setFbt_Product_Id(fbt_Product_Id);

					DecimalFormat decimalFormat = new DecimalFormat("#.00");
					try {
						subtotal =Double.parseDouble(decimalFormat.format(Double.valueOf(subtotal)));
					} catch (Exception e) {
					}
					productDetails.setSubtotalFBT(subtotal);

				}else{

					LOGGER.log(Level.SEVERE, "Frequently bought together not available " );
					productDetails.setIntFBTProductsCount(0);
				}

				if(null != jsonArrayProductViewed && !(jsonArrayProductViewed.isEmpty())){
					int noOfProd=jsonArrayProductViewed.size();
					JSONObject product=null;
					for(int i=0;i<jsonArrayProductViewed.size();i++){
						product= (JSONObject) jsonArrayProductViewed.get(i);
						String productId=(String)product.get("productId"); 
					}



				}else{

					LOGGER.log(Level.SEVERE, "People Who Viewed this Product Also Viewed section not available " );

				}




			}		

		}catch (Exception e) {
			productDetails.setIntFBTProductsCount(0);
			LOGGER.log(Level.SEVERE, "jsonProdFBTDetailsParser exception for: " + fbtUrl, e);
			//e.printStackTrace();
		}
		return productDetails;
	}



	public static ProductDetails jsonSpecialOfferParserVariants(String SpecialOfferApiUrl, ProductDetails productDetails){
		GreenBoxDetails greenBoxDetails=productDetails.getGreenBoxDetails();

		try {		

			JSONParser parser = new JSONParser();
			String response=getResponse(SpecialOfferApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));

			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			jsonObject=(JSONObject)jsonObject.get("data");
			jsonObject=(JSONObject)jsonObject.get("promodata");
			JSONObject jsonDomain=(JSONObject)jsonObject.get("domain");

			JSONObject jsonSears;
			jsonSears=(JSONObject)jsonDomain.get("sears");

			JSONArray jsonRegularArray;
			try {
				jsonRegularArray = (JSONArray)jsonSears.get("regular");
			} catch (Exception e) {
				try {
					jsonSears=(JSONObject)jsonDomain.get("kmart");	
					jsonRegularArray = (JSONArray)jsonSears.get("regular");
				} catch (Exception e2) {
					jsonSears=(JSONObject)jsonDomain.get("craftsman");	
					jsonRegularArray = (JSONArray)jsonSears.get("regular");
				}

			}




			if(null == jsonRegularArray || jsonRegularArray.isEmpty()){
				greenBoxDetails.setSpecialOffers(false);
				greenBoxDetails.setSpecialOffersCount(0);
			}
			else{
				greenBoxDetails.setSpecialOffers(true);
				greenBoxDetails.setSpecialOffersCount(jsonRegularArray.size());
			}


			JSONObject specialOffersDate=null;
			List <String> expiryDate =new ArrayList<String>();
			for(int i=0;i<jsonRegularArray.size();i++){
				specialOffersDate = (JSONObject) jsonRegularArray.get(i);
				String offerEndDate=(String)specialOffersDate.get("endDt");

				offerEndDate=offerEndDate.substring(0,10);
				offerEndDate=offerEndDate.replaceAll("-", "/");
				SimpleDateFormat fromUser = new SimpleDateFormat("yyyy/MM/dd");
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
				String reformattedStr="";
				try {
					reformattedStr = myFormat.format(fromUser.parse(offerEndDate));
				} catch (Exception e) {
					e.printStackTrace();
				}

				expiryDate.add(reformattedStr);
			}

			greenBoxDetails.setStrSpecialOffersEndDate(expiryDate);

		}catch (Exception e){
			LOGGER.log(Level.SEVERE, "jsonSpecialOfferParserVariants exception for: " + SpecialOfferApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl), e);
			LOGGER.log(Level.SEVERE, "Special offers not Applicable " );
		}
		productDetails.setGreenBoxDetails(greenBoxDetails);	
		return productDetails;
	}

	public static  ProductDetails jsonPDPServiceParser(String partNumber){
		ProductDetails productDetails =new ProductDetails();

		GreenBoxDetails greenBoxDetails=new GreenBoxDetails();		
		if(partNumber.endsWith("p"))
			partNumber = partNumber.replaceAll("p", "P");

		if(partNumber.startsWith("S")&&partNumber.endsWith("P")){
			partNumber=partNumber.substring(0, partNumber.lastIndexOf("P"));
		}else if(!partNumber.startsWith("S")&&!partNumber.endsWith("P")){
			partNumber=partNumber+"P";
		}

		try {
			JSONParser parser = new JSONParser();
			String pdpServiceApi=apiUrls.pdpServiceApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber)
					.replace("PLACEHOLDERFORBASEURL", baseUrl);
			String response=getResponse(pdpServiceApi);
			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject jsonWorker = (JSONObject)jsonObject.get("data");

			JSONObject jsonProductStatusNode = (JSONObject)jsonWorker.get("productstatus");
			String ssin=(String)jsonProductStatusNode.get("ssin");
			greenBoxDetails.setSsin(ssin);
			boolean isVariant = (Boolean)jsonProductStatusNode.get("isVariant");
			greenBoxDetails.setVariant(isVariant);
			boolean flagGrpSeller=false;
			if(!isVariant){
				long offerCnt;
				try{
					offerCnt=(Long)(jsonProductStatusNode.get("offerCount"));
				}catch (Exception e) {
					offerCnt=1;
				}
				int cnt=(int)offerCnt;
				String uid=(String)jsonProductStatusNode.get("uid");
				greenBoxDetails.setStrGroupId(uid);
				greenBoxDetails.setIntSellerCount(cnt);
				if(offerCnt>1){
					flagGrpSeller=true;
				}
			}
			try {

				JSONObject jsonProductMappingNode = (JSONObject)jsonWorker.get("productmapping");
				JSONArray jsonBrandLogoLinksArr=(JSONArray)jsonProductMappingNode.get("brandLogoLinks");		
				greenBoxDetails.setBrandLogoLinks(jsonBrandLogoLinksArr.size());

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Brand Links not available " );
			}


			JSONObject jsonProductNode = (JSONObject)jsonWorker.get("product");

			try
			{
				JSONObject operational = (JSONObject)jsonProductNode.get("operational");
				JSONObject sites = (JSONObject)operational.get("sites");
				if(sites.containsKey("sears"))
				{
					JSONObject searsNode = (JSONObject)sites.get("sears");
					boolean isDispElig = (Boolean)searsNode.get("isDispElig");
					productDetails.setSearsDisplayEligible(isDispElig);
				}
				if(sites.containsKey("kmart"))
				{
					JSONObject kmartNode = (JSONObject)sites.get("kmart");
					boolean isDispElig = (Boolean)kmartNode.get("isDispElig");
					productDetails.setKmartDisplayEligible(isDispElig);
				}
				if(sites.containsKey("craftsman"))
				{
					JSONObject craftsmanNode = (JSONObject)sites.get("craftsman");
					boolean isDispElig = (Boolean)craftsmanNode.get("isDispElig");
					productDetails.setCraftsmanDisplayEligible(isDispElig);
				}
				if(sites.containsKey("kenmore"))
				{
					JSONObject kenmoreNode = (JSONObject)sites.get("kenmore");
					boolean isDispElig = (Boolean)kenmoreNode.get("isDispElig");
					productDetails.setKenmoreDisplayEligible(isDispElig);
				}


			}
			catch(Exception e)
			{
				System.err.println("Fetching isDispElig flag failed");
			}

			try {
				JSONArray jsonSpecsArray = (JSONArray) jsonProductNode.get("specs");

				if(null == jsonSpecsArray || jsonSpecsArray.isEmpty()){
					greenBoxDetails.setSpecAttrCheck(false);
				}
				else{
					greenBoxDetails.setSpecAttrCheck(true);

					for(int i=0;i<jsonSpecsArray.size();i++){
						JSONObject specJsonObj=(JSONObject)jsonSpecsArray.get(i);
						String group=(String)specJsonObj.get("grpName");

						if(group.equalsIgnoreCase("Certifications:")){
							JSONArray jsonCertiAttrArray = (JSONArray) specJsonObj.get("attrs");
							for(int k=0;k<jsonCertiAttrArray.size();k++){
								JSONObject certiAttr=(JSONObject)jsonCertiAttrArray.get(k);
								String certiAttrsName=(String)certiAttr.get("name");
								if(certiAttrsName.equalsIgnoreCase("ENERGY STAR Compliant")){
									String certiAttrsValue=(String)certiAttr.get("val");
									if(certiAttrsValue.equalsIgnoreCase("yes")){
										greenBoxDetails.setEnergyStarCompliant(true);
									}else{
										greenBoxDetails.setEnergyStarCompliant(false);
									}
								}
							}
						}


					}

				}


			} catch (Exception e) {
				greenBoxDetails.setSpecAttrCheck(false);
				greenBoxDetails.setEnergyStarCompliant(false);
			}


			try {
				JSONObject jsonClassificationsNode = (JSONObject)jsonProductNode.get("classifications");
				String enrichmentProviderValue=(String)jsonClassificationsNode.get("enrichmentProvider");
				greenBoxDetails.setEnrichmentProviderValue(enrichmentProviderValue);
				boolean isAutomotive=false;
				if(null!=jsonClassificationsNode.get("isAutomotive")){
					isAutomotive=(Boolean)jsonClassificationsNode.get("isAutomotive");
				}
				greenBoxDetails.setAutomotive(isAutomotive);
			} catch (Exception e) {
				greenBoxDetails.setEnrichmentProviderValue("N");
				greenBoxDetails.setAutomotive(false);
			}


			try {
				List<String> featureHighlightsName=new ArrayList<String>();
				JSONObject jsonCuratedContentsNode = (JSONObject)jsonProductNode.get("curatedContents");
				JSONArray jsonArrCuratedGrp=(JSONArray)jsonCuratedContentsNode.get("curatedGrp");
				greenBoxDetails.setFeatureHighlightsPresent(true);
				for(int curatedGrpCount=0;curatedGrpCount<jsonArrCuratedGrp.size();curatedGrpCount++ ){
					JSONObject curatedGrpDetail = (JSONObject)jsonArrCuratedGrp.get(curatedGrpCount);
					JSONArray 	jsonArrcontent = (JSONArray)curatedGrpDetail.get("content");
					JSONObject contentDetail=(JSONObject)jsonArrcontent.get(0);
					String name= (String)contentDetail.get("name");
					featureHighlightsName.add(name);
				}
				greenBoxDetails.setFeatureHighlightName(featureHighlightsName);

			} catch (Exception e) {
				greenBoxDetails.setFeatureHighlightsPresent(false);
			}

			greenBoxDetails.setStrPartNumber((String) jsonProductNode.get("id"));

			JSONObject jsonProductAltsNode = (JSONObject)jsonProductNode.get("altIds");
			String catentryId="";
			if(jsonProductAltsNode != null){
				catentryId=String.valueOf((Long)jsonProductAltsNode.get("catentryId"));
			}

			greenBoxDetails.setStrParentCatentryId(catentryId);


			List<String> attachments= new ArrayList<String>();
			JSONObject jsonAssetsNode = (JSONObject)jsonProductNode.get("assets");
			// The attachments may not be present for all product types
			try {
				if(jsonAssetsNode.get("attachments") != null){
					JSONArray jsonObjectAttachments= (JSONArray) jsonAssetsNode.get("attachments");
					JSONObject jsonObjectFirst=(JSONObject)jsonObjectAttachments.get(0);
					greenBoxDetails.setFirstAttachment((String)jsonObjectFirst.get("name"));

					for(int i=0;i<jsonObjectAttachments.size();i++){
						jsonObjectFirst=(JSONObject)jsonObjectAttachments.get(i);
						attachments.add((String)jsonObjectFirst.get("name"));
					}
					greenBoxDetails.setAttachments(attachments);
				}
			} catch (Exception e) {
				attachments.add(" ");
				greenBoxDetails.setAttachments(attachments);
			}

			List<String> primaryImagesSrc= new ArrayList<String>();
			List<String> alternateImagesSrc= new ArrayList<String>();
			try {
				if(jsonAssetsNode.get("imgs") != null){
					JSONArray jsonArrImages= (JSONArray) jsonAssetsNode.get("imgs");
					for(int i=0;i<jsonArrImages.size();i++){
						JSONObject imageDetails=(JSONObject)jsonArrImages.get(i);
						String type=(String)imageDetails.get("type");
						JSONArray imageValsArr= (JSONArray) imageDetails.get("vals");

						if(type.equalsIgnoreCase("P")){
							for(int count=0;count<imageValsArr.size();count++){
								JSONObject imageSrc=(JSONObject)imageValsArr.get(count);
								primaryImagesSrc.add((String)imageSrc.get("src"));
							}

						}else if(type.equalsIgnoreCase("A")){
							for(int count=0;count<imageValsArr.size();count++){
								JSONObject imageSrc=(JSONObject)imageValsArr.get(count);
								alternateImagesSrc.add((String)imageSrc.get("src"));
							}

						}

					}

				}

			} catch (Exception e) {
				alternateImagesSrc.add("Exception");
				primaryImagesSrc.add("Exception");
			}
			greenBoxDetails.setPrimaryImagesSrc(primaryImagesSrc);
			greenBoxDetails.setAlternateImagesSrc(alternateImagesSrc);

			try {
				if(jsonAssetsNode.get("videos") != null){
					JSONArray jsonArrVideos= (JSONArray) jsonAssetsNode.get("videos");
					int noOfVideos=jsonArrVideos.size();
					greenBoxDetails.setVideoCount(noOfVideos);					
				}
			} catch (Exception e) {
				greenBoxDetails.setVideoCount(0);		
			}

			try{
				JSONArray jsonArrDesc= (JSONArray) jsonProductNode.get("desc");
				for(int i=0;i<jsonArrDesc.size();i++){
					JSONObject descDetails=(JSONObject)jsonArrDesc.get(i);
					String descType=(String)descDetails.get("type");
					if(descType.equalsIgnoreCase("S")){
						greenBoxDetails.setShortDescPresent(true);
					}
					if(descType.equalsIgnoreCase("L")){
						greenBoxDetails.setLongDescPresent(true);
					}
					if(descType.equalsIgnoreCase("T")){
						greenBoxDetails.setKeyFeatursPresent(true);
						greenBoxDetails.setKeyFeatureValue((String)descDetails.get("val"));
					}
				}

			} catch (Exception e) {
				greenBoxDetails.setShortDescPresent(false);
				greenBoxDetails.setLongDescPresent(false);
				greenBoxDetails.setKeyFeatursPresent(false);
			}

			try{
				JSONObject jsonSeoObject = (JSONObject)jsonProductNode.get("seo");	
				greenBoxDetails.setSeoTitle((String)jsonSeoObject.get("title"));
				greenBoxDetails.setSeoUrl((String)jsonSeoObject.get("url"));

			} catch (Exception e) {
				// TODO: handle exception
			}	





			boolean reqFitment=false;
			try {
				JSONObject jsonAutomotiveNode = (JSONObject)jsonProductNode.get("automotive");	

				String autoFitmentText=(String)jsonAutomotiveNode.get("autoFitment");
				if(autoFitmentText.equalsIgnoreCase("Requires Fitment")){
					reqFitment= true;
				}

			} catch (Exception e) {
				// TODO: handle exception
			}			
			greenBoxDetails.setReqFitment(reqFitment);

			try {
				JSONObject jsonBrandNode = (JSONObject)jsonProductNode.get("brand");	
				greenBoxDetails.setStrProdBrand((String) jsonBrandNode.get("name"));
				JSONObject jsonImgNode = (JSONObject)jsonBrandNode.get("img");
				if(null!=jsonImgNode.get("src")){
					greenBoxDetails.setBrandLogoUrl((String) jsonImgNode.get("src"));
				}


				if(null!=jsonBrandNode.get("brandSizechartUrl")){
					String sizeChartUrl=(String)jsonBrandNode.get("brandSizechartUrl");
					greenBoxDetails.setBrandSizechartUrl(sizeChartUrl);
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Brand Logo not available " );
			}		



			greenBoxDetails.setStrProdTitle((String) jsonProductNode.get("name"));


			if(!flagGrpSeller){
				if(!isVariant){
					JSONObject jsonOfferStatusNode=null;
					try {
						jsonOfferStatusNode=(JSONObject)jsonWorker.get("offerstatus");
						greenBoxDetails.setOnlineStatus((Boolean) jsonOfferStatusNode.get("isOnline"));
						greenBoxDetails.setOnline((Boolean) jsonOfferStatusNode.get("isOnline"));
						greenBoxDetails.setCanDisplay((Boolean)jsonOfferStatusNode.get("canDisplay"));

					} catch (Exception e) {
						e.printStackTrace();
					}



					JSONObject jsonOfferNode=null;;
					try {
						jsonOfferNode = (JSONObject)jsonWorker.get("offer");
						greenBoxDetails.setStrProdTitle((String)jsonOfferNode.get("name"));
					}catch (Exception e) {
						String partNum="";
						if(partNumber.endsWith("P")){
							partNum=partNumber.substring(0, partNumber.lastIndexOf("P"));

						}
						String varOfferApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNum);
						String responseOffer=getResponse(varOfferApi);
						JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);
						JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
						jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
						jsonOfferNode =(JSONObject) jsonVarOfferObject.get("offer");
						greenBoxDetails.setStrProdTitle((String)jsonOfferNode.get("name"));				

					}
					try {
						JSONObject jsonMarketplaceNode = (JSONObject)jsonOfferNode.get("marketplace");
						if(null != jsonMarketplaceNode){
							JSONObject jsonSellerNode = (JSONObject)jsonMarketplaceNode.get("seller");
							greenBoxDetails.setSellerId(String.valueOf((Long) jsonSellerNode.get("id")));
							greenBoxDetails.setStrItemSellerName((String) jsonSellerNode.get("name"));
							greenBoxDetails.setStrItemType((String) jsonMarketplaceNode.get("programType"));

						}

					} catch (Exception e) {
						//e.printStackTrace();
					}

					JSONObject jsonffmNode = (JSONObject)jsonOfferNode.get("ffm");
					greenBoxDetails.setStrSoldBy((String)jsonffmNode.get("soldBy"));
					if(null != jsonffmNode.get("isSResElig"))
						greenBoxDetails.setStoreReserveInd((Boolean) jsonffmNode.get("isSResElig"));

					if(null != jsonffmNode.get("isSpuElig")){
						greenBoxDetails.setSpuEligible((Boolean) jsonffmNode.get("isSpuElig"));
					}else{
						greenBoxDetails.setSpuEligible(false);
					}

					if(null != jsonffmNode.get("isShipElig")){
						greenBoxDetails.setShipEligible((Boolean) jsonffmNode.get("isShipElig"));
					}else{
						greenBoxDetails.setShipEligible(false);
					}

					if(null != jsonffmNode.get("isDeliveryElig")){
						greenBoxDetails.setDeliveryEligible((Boolean) jsonffmNode.get("isDeliveryElig"));
					}else{
						greenBoxDetails.setDeliveryEligible(false);
					}

					if(null != jsonffmNode.get("fulfilledBy"))
						greenBoxDetails.setStrFulfilledBy((String) jsonffmNode.get("fulfilledBy"));
					if(null != jsonffmNode.get("dfltFfmDisplay"))
						greenBoxDetails.setStrDefaultFfmDisplay((String) jsonffmNode.get("dfltFfmDisplay"));
					if(null != jsonffmNode.get("isStoreResElig"))
						greenBoxDetails.setStoreResElig((Boolean) jsonffmNode.get("isStoreResElig"));

					 If offermapping node is present, ffm should be captured from this node
					try
					{
						JSONObject offermapping = (JSONObject)jsonWorker.get("offermapping");
						JSONObject fulfillment = (JSONObject)offermapping.get("fulfillment");

						if(null != fulfillment.get("shipping"))
						{
							greenBoxDetails.setShipEligible((Boolean) fulfillment.get("shipping"));
						}
						else
						{
							greenBoxDetails.setShipEligible(false);
						}

						if(null != fulfillment.get("storepickup"))
						{
							greenBoxDetails.setSpuEligible((Boolean) fulfillment.get("storepickup"));
						}
						else
						{
							greenBoxDetails.setSpuEligible(false);
						}

						if(null != fulfillment.get("delivery"))
						{
							greenBoxDetails.setDeliveryEligible((Boolean) fulfillment.get("delivery"));
						}else
						{
							greenBoxDetails.setDeliveryEligible(false);
						}

					}
					catch(Exception e)
					{
						System.err.println("offermapping node is not present");
					}


					try {
						JSONObject jsonLegalNode = (JSONObject)jsonOfferNode.get("legal");
						boolean isprop=(Boolean)jsonLegalNode.get("isProp65");
						greenBoxDetails.setProp65(isprop);
					} catch (Exception e) {
						greenBoxDetails.setProp65(false);
					}


					try{
						JSONObject jsonShippingNode = (JSONObject)jsonOfferNode.get("shipping");
						boolean isMailable=(Boolean)jsonShippingNode.get("isMailable");
						greenBoxDetails.setMailable(isMailable);

					}
					catch (Exception e) {
						//e.printStackTrace();
					}

					try {
						JSONObject jsonOfferAltsNode = (JSONObject)jsonOfferNode.get("altIds");
						greenBoxDetails.setStrItemCatentryId(String.valueOf(jsonOfferAltsNode.get("catentryId")));  // child catentry id for non variant item
						greenBoxDetails.setStrKsnId(String.valueOf(jsonOfferAltsNode.get("ksn")));
						greenBoxDetails.setStrUPCId(String.valueOf(jsonOfferAltsNode.get("upc")));
					} catch (Exception e) {
						// TODO: handle exception
					}

					greenBoxDetails.setStrChildPartNumber(String.valueOf(jsonOfferNode.get("id")));

					try {
						JSONObject jsonPresellNode = (JSONObject)jsonOfferNode.get("presell");
						String presellDate=(String) jsonPresellNode.get("streetDt");
						presellDate=presellDate.substring(0,10);
						presellDate=presellDate.replaceAll("-", "/");
						SimpleDateFormat fromUser = new SimpleDateFormat("yyyy/MM/dd");
						SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
						String reformattedStr="";
						try {
							reformattedStr = myFormat.format(fromUser.parse(presellDate));
						} catch (Exception e) {
							e.printStackTrace();
						}
						greenBoxDetails.setPresellDate(reformattedStr);
					} catch (Exception e) {
						// TODO: handle exception
						greenBoxDetails.setPresellDate(null);

					}


					greenBoxDetails.setStrProdShortDesc((String) jsonOfferNode.get("desc"));
					greenBoxDetails.setStrProdModelNo((String) jsonOfferNode.get("modelNo"));
					greenBoxDetails.setStrProdBrand((String) jsonOfferNode.get("brandName"));

				}else{
					String varAttributeApi=apiUrls.varAttributeApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
					VariantsDetails variantsDetails=new VariantsDetails();

					JSONObject jsonObjectWorker = null;
					String responseVarAttr=getResponse(varAttributeApi);

					JSONArray jsonArray = (JSONArray) parser.parse(responseVarAttr);

					JSONObject jsonObjectMaster = (JSONObject) jsonArray.get(0);
					jsonObjectMaster =(JSONObject) jsonObjectMaster.get("_blob");
					jsonObjectWorker =(JSONObject) jsonObjectMaster.get("attributes");
					JSONArray jsonAttribureArray = (JSONArray) jsonObjectWorker.get("definingAttrs");

					Map<String, List<String>> definingAttrs = new HashMap<String, List<String>>();
					if(jsonAttribureArray!=null && jsonAttribureArray.size()>0)
					{
						for (int i=0; i < jsonAttribureArray.size(); i++)
						{
							JSONObject definingAttr = (JSONObject) jsonAttribureArray.get(i);								
							String attrKey = (String) definingAttr.get("name");								
							JSONArray attrVals = (JSONArray) definingAttr.get("vals");								
							List<String> attrValList = new ArrayList<String>(); 								

							for (int j=0; j < attrVals.size(); j++){

								JSONObject valName = (JSONObject) attrVals.get(j);
								attrValList.add((String)valName.get("name"));									
							}

							definingAttrs.put(attrKey, attrValList);	

						}
					}

					variantsDetails.setDefiningAttrs(definingAttrs);

					List<VariantsDetails> skuList = new ArrayList<VariantsDetails>();
					Map<String, String> skuAttrs=null;
					VariantsDetails skuDTOObj;

					JSONArray skuWorkerArray = (JSONArray) jsonObjectWorker.get("uids");
					variantsDetails.setSkuCount(skuWorkerArray.size());

					for (int k=0; k < skuWorkerArray.size(); k++){
						skuDTOObj =new VariantsDetails();
						skuAttrs = new HashMap<String, String>();

						JSONObject attrWorker = (JSONObject) skuWorkerArray.get(k);

						JSONArray skuUidDefArray = (JSONArray) attrWorker.get("uidDefAttrs");
						for (int m=0; m < skuUidDefArray.size(); m++){

							JSONObject attrObject = (JSONObject) skuUidDefArray.get(m);
							String attrKey = (String) attrObject.get("attrName");
							String attrVal = (String) attrObject.get("attrVal");
							skuAttrs.put(attrKey, attrVal);

						}
						skuDTOObj.setSkuAttrs(skuAttrs);


						String childpartnumber="";
						try
						{
							skuDTOObj.setId((String)attrWorker.get("offerId"));
							childpartnumber=(String)attrWorker.get("offerId");
						}
						catch (Exception e) 
						{

						}

						skuDTOObj.setUid((String)attrWorker.get("uid"));
						String uid=(String)attrWorker.get("uid");

						long offcnt=(Long)attrWorker.get("offerCnt");
						int offerCnt=(int)offcnt;
						skuDTOObj.setOfferCnt(offerCnt);
						// offerCnt=Integer.parseInt((String)attrWorker.get("offerCnt"));


						if(offerCnt!=1){
							List<GroupedSellerDetails> groupedSellerDetailsList=mpGroupSellerDetailsParser(uid);
							GroupedSellerDetails groupedSellerDetails=null;

							if(null != groupedSellerDetailsList && !groupedSellerDetailsList.isEmpty()){
								Iterator<GroupedSellerDetails> iter = groupedSellerDetailsList.iterator();
								while (iter.hasNext()) {
									groupedSellerDetails = iter.next();
									if (groupedSellerDetails.getIntRank() == 1){
										childpartnumber=groupedSellerDetails.getStrPartNumber();  // Rank 1 partnumber
										skuDTOObj.setSoftLineGroupSeller(true);
										skuDTOObj.setSoftLineGroupSeller(childpartnumber);
									}
								}
							}
						}						


						if(childpartnumber.endsWith("P")){
							childpartnumber=childpartnumber.substring(0,childpartnumber.lastIndexOf("P"));
						}

						String varOfferApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", childpartnumber);//Childpartnumber
						JSONObject jsonOfferObjectWorker = null;
						String responseOffer=getResponse(varOfferApi);
						JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);

						JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
						jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
						jsonOfferObjectWorker =(JSONObject) jsonVarOfferObject.get("offer");

						JSONObject jsonOfferAltsNode = (JSONObject)jsonOfferObjectWorker.get("altIds");
						//skuDTOObj.setStrItemCatentryId(String.valueOf ((Long)jsonOfferAltsNode.get("catentryId")));
						skuDTOObj.setCatentryId(String.valueOf ((Long)jsonOfferAltsNode.get("catentryId")));
						///
						JSONObject skuWorker = (JSONObject) jsonOfferObjectWorker.get("operational");
						skuWorker = (JSONObject) skuWorker.get("sites");
						//modified to take care of missing isOnline and isAvail flags in QA..need to be cleaned up 
						JSONObject skuWorkerSears = (JSONObject) skuWorker.get("sears");
						if(skuWorkerSears != null){
							if(skuWorkerSears.get("isOnline") != null)
								skuDTOObj.setOnline((Boolean)skuWorkerSears.get("isOnline"));
							if(skuWorkerSears.get("isOnline") == null)
							{
								if(skuWorkerSears.get("isReserveIt") != null)
									skuDTOObj.setReserveIt((Boolean)skuWorkerSears.get("isReserveIt"));
							}
							if(skuWorkerSears.get("isAvail") != null)
								skuDTOObj.setAvail((Boolean)skuWorkerSears.get("isAvail"));

							if(skuWorkerSears.get("isDispElig") != null)
								skuDTOObj.setDispElig((Boolean)skuWorkerSears.get("isDispElig"));
						} else {
							JSONObject skuWorkerKmart = (JSONObject) skuWorker.get("kmart");
							if(skuWorkerKmart != null){
								if(skuWorkerKmart.get("isOnline") != null)
									skuDTOObj.setOnline((Boolean)skuWorkerKmart.get("isOnline"));
								if(skuWorkerKmart.get("isAvail") != null)
									skuDTOObj.setAvail((Boolean)skuWorkerKmart.get("isAvail"));
								if(skuWorkerKmart.get("isDispElig") != null)
									skuDTOObj.setDispElig((Boolean)skuWorkerKmart.get("isDispElig"));
							} else {
								JSONObject skuWorkerCraftsman= (JSONObject) skuWorker.get("craftsman");
								if(skuWorkerCraftsman != null){
									if(skuWorkerCraftsman.get("isOnline") != null)
										skuDTOObj.setOnline((Boolean)skuWorkerCraftsman.get("isOnline"));
									if(skuWorkerCraftsman.get("isAvail") != null)
										skuDTOObj.setAvail((Boolean)skuWorkerCraftsman.get("isAvail"));
									if(skuWorkerCraftsman.get("isDispElig") != null)
										skuDTOObj.setDispElig((Boolean)skuWorkerCraftsman.get("isDispElig"));
								}			
							}
						}					
						skuList.add(skuDTOObj);	
					}

					variantsDetails.setSkuList(skuList);
					variantsDetails.setInStockVariants(skuList);
					productDetails.setVariantsDetails(variantsDetails);
				}
			}
			//////////////////////////////////////////

			productDetails.setGreenBoxDetails(greenBoxDetails);
			if(!isVariant){						

				String fbtUrl=apiUrls.fbtApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);

				productDetails=jsonProdFBTDetailsParser(fbtUrl, productDetails);

				if(partNumber.endsWith("P")){
					partNumber=partNumber.replace("P","");
				}

				String pricingUrl=apiUrls.pricingApi.replace("PLACEHOLDERFORPARTNUMBER", partNumber).trim();
				pricingUrl=pricingUrl.replace("PLACEHOLDERFORVARIATION", "0");
				pricingUrl=pricingUrl.replace("PLACEHOLDERFORMEMBERSTATUS", "G");
				productDetails=jsonPriceParserMap(pricingUrl ,productDetails);		



			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return productDetails;

	}

	public static  ProductDetails jsonPDPOfferServiceParser(String partNumber){

		ProductDetails productDetails =new ProductDetails();
		if(partNumber.endsWith("P")){
			partNumber=partNumber.substring(0,partNumber.lastIndexOf("P"));
		}
		try {
			JSONParser parser = new JSONParser();
			String varOfferApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			System.out.println("Offer API: "+varOfferApi);
			JSONObject jsonOfferObjectWorker = null;
			String responseOffer=getResponse(varOfferApi);
			JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);
			JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
			jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
			jsonOfferObjectWorker =(JSONObject) jsonVarOfferObject.get("offer");

			JSONObject jsonShippingNode = (JSONObject)jsonOfferObjectWorker.get("shipping");
			JSONObject jsonMaxQntyPurchasableNode = (JSONObject)jsonShippingNode.get("maxQntyPurchasable");
			jsonMaxQntyPurchasableNode = (JSONObject)jsonMaxQntyPurchasableNode.get("sites");
			jsonMaxQntyPurchasableNode = (JSONObject)jsonMaxQntyPurchasableNode.get("sears");
			String maxValue=jsonMaxQntyPurchasableNode.get("val").toString();
			String wrhsLcns = ((JSONArray)((JSONObject)jsonOfferObjectWorker.get("ffm")).get("wrhsLcns")).get(0).toString();
			String vendorDunsNo = ((JSONObject)jsonOfferObjectWorker.get("ffm")).get("vendorDunsNo").toString();
			String channel = ((JSONObject)jsonOfferObjectWorker.get("ffm")).get("channel").toString();

			productDetails.setWrhcLcns(wrhsLcns);
			productDetails.setVendorDunsNo(vendorDunsNo);
			productDetails.setFfm(channel);
			//productDetails.setMaxOrderValue(maxValue);

		}catch(Exception e){
			e.printStackTrace();
		}
		return productDetails;
	}

	public static String isGivenPartNumberChild(String partNumber) {
		String partNum=partNumber;
		String ssin = "";
		if(partNumber.toUpperCase().endsWith("P")){
			partNum=partNumber.substring(0, partNumber.toUpperCase().lastIndexOf("P"));

		}
		JSONParser parser = new JSONParser();
		JSONObject jsonVarOfferObject = null;
		try{
			String varOfferApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNum);
			String responseOffer=getResponse(varOfferApi);
			JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);
			jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
		}catch(Exception e)
		{
			System.out.println("No response. PartNumber given is parent.");
			return null;
		}
		try{
			jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
			JSONObject jsonOfferNode =(JSONObject) jsonVarOfferObject.get("offer");
			JSONObject jsonIdentityNode = (JSONObject) jsonOfferNode.get("identity");
			ssin = (String)jsonIdentityNode.get("ssin");
			if(ssin.contains(partNum))
			{
				return null;
			}	
		}catch(Exception e)
		{
			System.out.println("Issue with parsing through the offer API");
			return null;
		}
		return ssin;
	}

	public static String jsonResponsePDPOfferService(String partNumber){

		String responseOffer = null;
		if(partNumber.endsWith("P")){
			partNumber=partNumber.substring(0,partNumber.lastIndexOf("P"));
		}
		try {
			String varOfferApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			responseOffer=getResponse(varOfferApi);

		}catch(Exception e){
			e.printStackTrace();
		}
		return responseOffer.replaceAll("\\n", "");
	}

	public static ProductDetails jsonTopRankedOfferApiParser(ProductDetails productDetails){

		GreenBoxDetails greenBoxDetails=productDetails.getGreenBoxDetails();
		try {
			String uid=productDetails.getGreenBoxDetails().getStrGroupId();
			//String partNumber=productDetails.getGreenBoxDetails().getStrPartNumber();
			String partNumber=productDetails.getGreenBoxDetails().getSsin();
			String topRankedApi=apiUrls.topRankedOfferApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORUID", uid).replace("PLACEHOLDERFORBASEURL", baseUrl);
			JSONParser parser = new JSONParser();
			String responseTopRankedOffer=getResponse(topRankedApi);
			Object objTopRanked = parser.parse(responseTopRankedOffer);  
			JSONObject jsonVarOfferObject = (JSONObject) objTopRanked;
			jsonVarOfferObject = (JSONObject)jsonVarOfferObject.get("data");



			try
			{
				JSONObject jsonOfferStatus = (JSONObject)jsonVarOfferObject.get("offerstatus");
				String offerId = (String)jsonOfferStatus.get("offerId");
				greenBoxDetails.setCanDisplay((Boolean)jsonOfferStatus.get("canDisplay"));
				greenBoxDetails.setOfferId(offerId);
				greenBoxDetails.setSsin((String)jsonOfferStatus.get("ssin"));
			}
			catch(Exception e)
			{
				System.err.println("Supress Buy box winner node not found error");
			}


			JSONObject jsonOfferNode = (JSONObject)jsonVarOfferObject.get("offer");

			greenBoxDetails.setStrProdTitle((String)jsonOfferNode.get("name"));
			try {
				JSONObject jsonMarketplaceNode = (JSONObject)jsonOfferNode.get("marketplace");
				if(null != jsonMarketplaceNode){
					JSONObject jsonSellerNode = (JSONObject)jsonMarketplaceNode.get("seller");
					greenBoxDetails.setSellerId(String.valueOf((Long) jsonSellerNode.get("id")));
					greenBoxDetails.setStrItemSellerName((String) jsonSellerNode.get("name"));
					greenBoxDetails.setStrItemType((String) jsonMarketplaceNode.get("programType"));

				}

			} catch (Exception e) {
				//e.printStackTrace();
			}

			JSONObject jsonffmNode = (JSONObject)jsonOfferNode.get("ffm");
			greenBoxDetails.setStrSoldBy((String)jsonffmNode.get("soldBy"));
			if(null != jsonffmNode.get("isSResElig"))
				greenBoxDetails.setStoreReserveInd((Boolean) jsonffmNode.get("isSResElig"));
			if(null != jsonffmNode.get("isSpuElig")){
				greenBoxDetails.setSpuEligible((Boolean) jsonffmNode.get("isSpuElig"));
			}else{
				greenBoxDetails.setSpuEligible(false);
			}

			if(null != jsonffmNode.get("isShipElig")){
				greenBoxDetails.setShipEligible((Boolean) jsonffmNode.get("isShipElig"));
			}else{
				greenBoxDetails.setShipEligible(false);
			}

			if(null != jsonffmNode.get("isDeliveryElig")){
				greenBoxDetails.setDeliveryEligible((Boolean) jsonffmNode.get("isDeliveryElig"));
			}else{
				greenBoxDetails.setDeliveryEligible(false);
			}

			if(null != jsonffmNode.get("fulfilledBy"))
				greenBoxDetails.setStrFulfilledBy((String) jsonffmNode.get("fulfilledBy"));
			if(null != jsonffmNode.get("dfltFfmDisplay"))
				greenBoxDetails.setStrDefaultFfmDisplay((String) jsonffmNode.get("dfltFfmDisplay"));
			if(null != jsonffmNode.get("isStoreResElig"))
				greenBoxDetails.setStoreResElig((Boolean) jsonffmNode.get("isStoreResElig"));
			if(null != jsonffmNode.get("channel"))
				greenBoxDetails.setFfmChannel((String) jsonffmNode.get("channel"));

			 If offermapping node is present, ffm should be captured from this node
			try
			{
				JSONObject offermapping = (JSONObject)jsonVarOfferObject.get("offermapping");
				JSONObject fulfillment = (JSONObject)offermapping.get("fulfillment");

				if(null != fulfillment.get("shipping"))
				{
					greenBoxDetails.setShipEligible((Boolean) fulfillment.get("shipping"));
				}
				else
				{
					greenBoxDetails.setShipEligible(false);
				}

				if(null != fulfillment.get("storepickup"))
				{
					greenBoxDetails.setSpuEligible((Boolean) fulfillment.get("storepickup"));
				}
				else
				{
					greenBoxDetails.setSpuEligible(false);
				}

				if(null != fulfillment.get("delivery"))
				{
					greenBoxDetails.setDeliveryEligible((Boolean) fulfillment.get("delivery"));
				}else
				{
					greenBoxDetails.setDeliveryEligible(false);
				}

			}
			catch(Exception e)
			{
				System.err.println("offermapping node is not present");
			}


			try{
				JSONObject jsonShippingNode = (JSONObject)jsonOfferNode.get("shipping");
				boolean isMailable=(Boolean)jsonShippingNode.get("isMailable");
				greenBoxDetails.setMailable(isMailable);

			}
			catch (Exception e) {

			}


			JSONObject jsonOfferAltsNode = (JSONObject)jsonOfferNode.get("altIds");
			try {
				greenBoxDetails.setStrItemCatentryId(String.valueOf((Long)jsonOfferAltsNode.get("catentryId")));
				greenBoxDetails.setStrKsnId(String.valueOf(jsonOfferAltsNode.get("ksn")));
				greenBoxDetails.setStrUPCId(String.valueOf(jsonOfferAltsNode.get("upc")));

			} catch (Exception e) {
				// TODO: handle exception
			}

			greenBoxDetails.setStrChildPartNumber(String.valueOf(jsonOfferNode.get("id")));


			JSONObject jsonOperationalNode = (JSONObject)jsonOfferNode.get("operational");
			JSONObject jsonSitesNode = (JSONObject)jsonOperationalNode.get("sites");
			try 
			{
				JSONObject jsonSearsNode = (JSONObject)jsonSitesNode.get("sears");
				greenBoxDetails.setOnlineStatus((Boolean) jsonSearsNode.get("isOnline"));
				boolean isAvail=(Boolean)jsonSearsNode.get("isAvail");
				boolean isOnline=(Boolean)jsonSearsNode.get("isOnline");
				greenBoxDetails.setAvail(isAvail);
				greenBoxDetails.setOnline(isOnline);

			} 
			catch (Exception e) 
			{
				System.err.println("Top Ranked api exception : " + e.getMessage());
				try
				{
					JSONObject jsonSearsNode = (JSONObject)jsonSitesNode.get("kmart");
					greenBoxDetails.setOnlineStatus((Boolean) jsonSearsNode.get("isOnline"));
					boolean isAvail=(Boolean)jsonSearsNode.get("isAvail");
					boolean isOnline=(Boolean)jsonSearsNode.get("isOnline");
					greenBoxDetails.setAvail(isAvail);
					greenBoxDetails.setOnline(isOnline);
				}
				catch(Exception exp)
				{
					System.err.println("Top Ranked api exception : " + e.getMessage());
				}
			}


			greenBoxDetails.setStrProdShortDesc((String) jsonOfferNode.get("desc"));
			greenBoxDetails.setStrProdModelNo((String) jsonOfferNode.get("modelNo"));
			greenBoxDetails.setStrProdBrand((String) jsonOfferNode.get("brandName"));

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Brand name not available " );
			//e.printStackTrace();
		}

		productDetails.setGreenBoxDetails(greenBoxDetails);

		return productDetails;
	}

	public static ProductDetails jsonVariantsOfferApiParser(String partNumber , ProductDetails productDetails){

		GreenBoxDetails greenBoxDetails=productDetails.getGreenBoxDetails();

		try {

			JSONParser parser = new JSONParser();
			if(partNumber.endsWith("P")){
				partNumber=partNumber.substring(0,partNumber.lastIndexOf("P"));
			}

			String offerApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);//Childpartnumber


			JSONObject jsonOfferNode = null;
			String responseOffer=getResponse(offerApi);
			JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);

			JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
			jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
			jsonOfferNode =(JSONObject) jsonVarOfferObject.get("offer");

			greenBoxDetails.setStrProdTitle((String)jsonOfferNode.get("name"));
			try {
				JSONObject jsonMarketplaceNode = (JSONObject)jsonOfferNode.get("marketplace");
				if(null != jsonMarketplaceNode){
					JSONObject jsonSellerNode = (JSONObject)jsonMarketplaceNode.get("seller");
					greenBoxDetails.setSellerId(String.valueOf((Long) jsonSellerNode.get("id")));
					greenBoxDetails.setStrItemSellerName((String) jsonSellerNode.get("name"));
					greenBoxDetails.setStrItemType((String) jsonMarketplaceNode.get("programType"));

				}

			} catch (Exception e) {
				//e.printStackTrace();
			}

			JSONObject jsonffmNode = (JSONObject)jsonOfferNode.get("ffm");
			greenBoxDetails.setStrSoldBy((String)jsonffmNode.get("soldBy"));
			if(null != jsonffmNode.get("isSResElig"))
				greenBoxDetails.setStoreReserveInd((Boolean) jsonffmNode.get("isSResElig"));

			if(null != jsonffmNode.get("isSpuElig")){
				greenBoxDetails.setSpuEligible((Boolean) jsonffmNode.get("isSpuElig"));
			}else{
				greenBoxDetails.setSpuEligible(false);
			}

			if(null != jsonffmNode.get("isShipElig")){
				greenBoxDetails.setShipEligible((Boolean) jsonffmNode.get("isShipElig"));
			}else{
				greenBoxDetails.setShipEligible(false);
			}

			if(null != jsonffmNode.get("isDeliveryElig")){
				greenBoxDetails.setDeliveryEligible((Boolean) jsonffmNode.get("isDeliveryElig"));
			}else{
				greenBoxDetails.setDeliveryEligible(false);
			}

			if(null != jsonffmNode.get("fulfilledBy"))
				greenBoxDetails.setStrFulfilledBy((String) jsonffmNode.get("fulfilledBy"));
			if(null != jsonffmNode.get("dfltFfmDisplay"))
				greenBoxDetails.setStrDefaultFfmDisplay((String) jsonffmNode.get("dfltFfmDisplay"));
			if(null != jsonffmNode.get("isStoreResElig"))
				greenBoxDetails.setStoreResElig((Boolean) jsonffmNode.get("isStoreResElig"));



			try{
				JSONObject jsonShippingNode = (JSONObject)jsonOfferNode.get("shipping");
				boolean isMailable=(Boolean)jsonShippingNode.get("isMailable");
				greenBoxDetails.setMailable(isMailable);

			}
			catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Brand Logo not available " );
				//e.printStackTrace();
			}

			try {
				JSONObject jsonOfferAltsNode = (JSONObject)jsonOfferNode.get("altIds");
				greenBoxDetails.setStrItemCatentryId(String.valueOf ((Long)jsonOfferAltsNode.get("catentryId")));  // this is the child catentry id for variant item
				greenBoxDetails.setStrKsnId(String.valueOf(jsonOfferAltsNode.get("ksn")));
				greenBoxDetails.setStrUPCId(String.valueOf(jsonOfferAltsNode.get("upc")));
			} catch (Exception e) {
				// TODO: handle exception
			}

			greenBoxDetails.setStrChildPartNumber(String.valueOf(jsonOfferNode.get("id")));

			JSONObject jsonOperationalNode = (JSONObject)jsonOfferNode.get("operational");
			JSONObject jsonSitesNode = (JSONObject)jsonOperationalNode.get("sites");
			try 
			{
				JSONObject jsonSearsNode = (JSONObject)jsonSitesNode.get("sears");
				greenBoxDetails.setOnlineStatus((Boolean) jsonSearsNode.get("isOnline"));
				boolean isAvail=(Boolean)jsonSearsNode.get("isAvail");
				boolean isOnline=(Boolean)jsonSearsNode.get("isOnline");
				greenBoxDetails.setAvail(isAvail);
				greenBoxDetails.setOnline(isOnline);

			} 
			catch (Exception e) 
			{
				System.err.println("Suppressing Operational node error for Sears  > " + partNumber);
				try
				{
					JSONObject jsonKmartNode = (JSONObject)jsonSitesNode.get("kmart");
					greenBoxDetails.setOnlineStatus((Boolean) jsonKmartNode.get("isOnline"));
					boolean isAvail=(Boolean)jsonKmartNode.get("isAvail");
					boolean isOnline=(Boolean)jsonKmartNode.get("isOnline");
					greenBoxDetails.setAvail(isAvail);
					greenBoxDetails.setOnline(isOnline);
				}
				catch(Exception exp)
				{
					System.err.println("Suppressing Operational node error for Kmart > " + partNumber);
					try
					{
						JSONObject jsonCraftsmanNode = (JSONObject)jsonSitesNode.get("craftsman");
						greenBoxDetails.setOnlineStatus((Boolean) jsonCraftsmanNode.get("isOnline"));
						boolean isAvail=(Boolean)jsonCraftsmanNode.get("isAvail");
						boolean isOnline=(Boolean)jsonCraftsmanNode.get("isOnline");
						greenBoxDetails.setAvail(isAvail);
						greenBoxDetails.setOnline(isOnline);
					}
					catch(Exception exception)
					{
						System.err.println("Suppressing Operational node error for Craftsman > " + partNumber);
					}
				}
			}


			greenBoxDetails.setStrProdShortDesc((String) jsonOfferNode.get("desc"));
			greenBoxDetails.setStrProdModelNo((String) jsonOfferNode.get("modelNo"));
			greenBoxDetails.setStrProdBrand((String) jsonOfferNode.get("brandName"));

		}catch (Exception e) {

			//e.printStackTrace();
		}

		productDetails.setGreenBoxDetails(greenBoxDetails);

		if(ComparisonUtils.isPEDTest())
			return productDetails;

		String fbtUrl=apiUrls.fbtApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
		productDetails=jsonProdFBTDetailsParser(fbtUrl, productDetails);

		if(partNumber.endsWith("P")){ partNumber=partNumber.replace("P",""); }	

		String pricingUrl=apiUrls.pricingApi.replace("PLACEHOLDERFORPARTNUMBER", partNumber).trim();
		pricingUrl=pricingUrl.replace("PLACEHOLDERFORVARIATION", "0");
		pricingUrl=pricingUrl.replace("PLACEHOLDERFORMEMBERSTATUS", "G");
		productDetails=jsonPriceParserMap(pricingUrl, productDetails);

		return productDetails;
	}	


	public static List<GroupedSellerDetails> mpGroupSellerDetailsParser(String uId){
		List<GroupedSellerDetails> groupedSellerDetailsList = new ArrayList<GroupedSellerDetails>();
		GroupedSellerDetails groupedSellerDetails;
		String rankingApiUrl=apiUrls.sellersRankingApiUrl.replace("PLACEHOLDERFORUID", uId).replace("PLACEHOLDERFORBASEURL", baseUrl); 
		//apiUrls.iftRankingApiUrl.replace("PLACEHOLDERFORUID", uId);
		try {
			JSONParser parser = new JSONParser();
			JSONArray jsonGroupsObject = null;
			JSONObject jsonObjectWorker= null;
			String response=getResponse(rankingApiUrl);
			JSONObject jsonMaster = (JSONObject) parser.parse(response);
			jsonGroupsObject=(JSONArray)jsonMaster.get("groups");
			JSONObject jsonObj=(JSONObject)jsonGroupsObject.get(0);
			JSONArray jsonArray = (JSONArray)jsonObj.get("offers");

			if(null != jsonArray && !jsonArray.isEmpty()){

				Iterator<JSONObject> iter = jsonArray.iterator();
				while (iter.hasNext()) {
					jsonObjectWorker = iter.next();
					groupedSellerDetails = new GroupedSellerDetails();

					if(null != jsonObjectWorker.get("id"))
						groupedSellerDetails.setStrPartNumber((String) jsonObjectWorker.get("id"));

					if(null != jsonObjectWorker.get("sellerName")){
						groupedSellerDetails.setStrItemSellerName((String) jsonObjectWorker.get("sellerName"));
					}else{
						groupedSellerDetails.setStrItemSellerName("Sears");
					}

					if(null != jsonObjectWorker.get("totalPrice"))
						try{
							groupedSellerDetails.setDblRegPrice((Double) jsonObjectWorker.get("totalPrice"));
						} catch(Exception e){
							groupedSellerDetails.setDblRegPrice(Double.parseDouble((String) jsonObjectWorker.get("totalPrice")));
						}

					if(null != jsonObjectWorker.get("shippingPrice")){
						try{
							groupedSellerDetails.setDblShipPrice((Double) jsonObjectWorker.get("shippingPrice"));
						} catch(Exception e){
							try {
								groupedSellerDetails.setDblShipPrice(Double.parseDouble((String) jsonObjectWorker.get("shippingPrice")));
							} catch (Exception e2) {
								groupedSellerDetails.setDblShipPrice((double)((Long) jsonObjectWorker.get("shippingPrice")));
							}

						}
					}else{
						groupedSellerDetails.setDblShipPrice(0.00);
					}

					if(null != jsonObjectWorker.get("sellerId"))
						groupedSellerDetails.setStrSellerId((String) jsonObjectWorker.get("sellerId"));
					if(null != jsonObjectWorker.get("rank")){
						long lRank=(Long) jsonObjectWorker.get("rank");
						Integer iRank=(int)lRank;
						groupedSellerDetails.setIntRank(iRank);
					}

					groupedSellerDetailsList.add(groupedSellerDetails);
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"MPGSDetailsParser script exception for " + rankingApiUrl, e);
		}

		return groupedSellerDetailsList;
	}

	*//**
	 * Get the response of List Service API , parse the response and check the
	 * Status of the API response
	 * 
	 * @param url
	 * @return
	 *//*
	public static ReserveItItemDeatils listLookUpAPIParser(String url,
			String checker) {
		JSONParser parser = null;
		String response = getResponse(url);
		JSONObject jsonMaster = null;
		JSONObject jsonObj = null;
		JSONObject jsonObjTemp = null;
		JSONArray jsonArry = null;
		JSONObject jsonreserveListObj = null;
		ReserveItItemDeatils objReserveItItemDeatils = new ReserveItItemDeatils();

		if (checker.equalsIgnoreCase("CREATE")
				|| checker.equalsIgnoreCase("EDIT")) {
			try {
				parser = new JSONParser();
				jsonMaster = (JSONObject) parser.parse(response);
				jsonObj = (JSONObject) jsonMaster.get("SHCReserveList");
				jsonreserveListObj = (JSONObject) jsonObj.get("attributes");
				jsonArry = (JSONArray) jsonreserveListObj.get("attribute");

				// This for loop will get the ListID satus
				for (int i = 0; i < jsonArry.size(); i++) {
					jsonObjTemp = (JSONObject) jsonArry.get(i);

					if (jsonObjTemp.get("name").toString().equals("status")) {
						objReserveItItemDeatils.setStatus(jsonObjTemp.get(
								"value").toString());

					}
					if (jsonObjTemp.get("name").toString().equals("lastCreateduniqueId")) {
						objReserveItItemDeatils.setLastCreateduniqueId(jsonObjTemp.get(
								"value").toString());

					}

				}

				// Parsing through the json to fetch other reserve item details
				jsonreserveListObj = (JSONObject) jsonObj.get("reserveItems");
				jsonArry = (JSONArray) jsonreserveListObj.get("reserveItem");
				jsonObjTemp = (JSONObject) jsonArry.get(0);
				jsonreserveListObj = (JSONObject) jsonObjTemp.get("attributes");
				jsonArry = (JSONArray) jsonreserveListObj.get("attribute");

				for (int i = 0; i < jsonArry.size(); i++) {
					jsonObjTemp = (JSONObject) jsonArry.get(i);

					if (jsonObjTemp.get("name").toString().equals("Divitemsku")) {
						objReserveItItemDeatils.setDivitemsku(jsonObjTemp.get(
								"value").toString());
					}

					if (jsonObjTemp.get("name").toString()
							.equals("thumbnailDescription")) {
						objReserveItItemDeatils
						.setThumbnailDescription(jsonObjTemp.get(
								"value").toString());
					}

					if (jsonObjTemp.get("name").toString().equals("parentId")) {
						objReserveItItemDeatils.setParentId(jsonObjTemp.get(
								"value").toString());
					}

					if (jsonObjTemp.get("name").toString()
							.equals("description")) {
						objReserveItItemDeatils.setDescription(jsonObjTemp.get(
								"value").toString());
					}

				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,
						"Json List API Look Up Parser exception for: " + url, e);
			}
		}

		else if (checker.equalsIgnoreCase("CANCEL")) {
			try {
				parser = new JSONParser();
				jsonMaster = (JSONObject) parser.parse(response);
				objReserveItItemDeatils.setMessage(jsonMaster.get("message")
						.toString());
				objReserveItItemDeatils.setErrorStatus(jsonMaster
						.get("errorStatus").toString());

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,
						"Json List API Look Up Parser exception for: " + url, e);
			}
		}

		return objReserveItItemDeatils;
	}




	public static ProductDetails jsonProdAttrWsParser(String partNumber){
		ProductDetails productDetails =new ProductDetails();
		GreenBoxDetails greenBoxDetails=new GreenBoxDetails();
		try{
			JSONParser parser = new JSONParser();

			String url=apiUrls.prodGBWebserviceUrl.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", partNumber);

			String response=getResponse(url);

			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;

			greenBoxDetails.setStrPartNumber((String) jsonObject.get("parentPartnumber"));
			greenBoxDetails.setStrProdTitle((String) jsonObject.get("title"));
			greenBoxDetails.setStrSoldBy((String) jsonObject.get("soldBy"));
			greenBoxDetails.setProdInStockShipInd((Boolean) jsonObject.get("instockShipInd"));
			greenBoxDetails.setStrProdBrand((String) jsonObject.get("brandName"));
			greenBoxDetails.setStrProdShortDesc((String) jsonObject.get("shortDesc"));
			greenBoxDetails.setStrProdLongDesc((String) jsonObject.get("longDesc"));
			greenBoxDetails.setStrProdModelNo((String) jsonObject.get("modelNo"));
			greenBoxDetails.setSpuEligible((Boolean) jsonObject.get("spuEligibleInd"));
			greenBoxDetails.setStoreReserveInd((Boolean) jsonObject.get("storeReserveInd"));
			greenBoxDetails.setOnlineStatus((Boolean) jsonObject.get("onlineStatus"));
			greenBoxDetails.setStrItemCatentryId((String) jsonObject.get("itemCatentryId"));
			greenBoxDetails.setStrParentCatentryId((String) jsonObject.get("parentCatentryId"));
			greenBoxDetails.setBrandLogoUrl((String) jsonObject.get("brandLogoUrl"));


			try {
				JSONObject jsonObjectRP = (JSONObject) jsonObject.get("regionalPricing");
				greenBoxDetails.setRegionalPrice((Boolean) jsonObjectRP.get("isEnabled"));
				greenBoxDetails.setNationalPrice((Boolean) jsonObjectRP.get("hasNationalPrice"));
			} catch (Exception e) {

			}

			try{
				JSONArray jsonObjectArray = (JSONArray) jsonObject.get("specialOffers");

				if(null == jsonObjectArray || jsonObjectArray.isEmpty()){
					greenBoxDetails.setSpecialOffers(false);
					greenBoxDetails.setSpecialOffersCount(0);
				}
				else{
					greenBoxDetails.setSpecialOffers(true);
					greenBoxDetails.setSpecialOffersCount(jsonObjectArray.size());
				}

				JSONObject specialOffersDate=null;
				List <String> expiryDate =new ArrayList<String>();
				for(int i=0;i<jsonObjectArray.size();i++){
					specialOffersDate = (JSONObject) jsonObjectArray.get(i);
					String offerEndDate=(String)specialOffersDate.get("endDate");
					//offerEndDate=offerEndDate.substring(0,offerEndDate.indexOf("T"));
					offerEndDate=offerEndDate.substring(0,10);
					offerEndDate=offerEndDate.replaceAll("-", "/");
					SimpleDateFormat fromUser = new SimpleDateFormat("yyyy/MM/dd");
					SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
					String reformattedStr="";
					try {
						reformattedStr = myFormat.format(fromUser.parse(offerEndDate));
					} catch (Exception e) {
						e.printStackTrace();
					}

					expiryDate.add(reformattedStr);
				}

				greenBoxDetails.setStrSpecialOffersEndDate(expiryDate);

			}
			catch (Exception e) {
				//e.printStackTrace();
			}


			try{
				JSONArray jsonObjectArray = (JSONArray) jsonObject.get("specAttr");

				if(null == jsonObjectArray || jsonObjectArray.isEmpty()){
					greenBoxDetails.setSpecAttrCheck(false);
				}
				else{
					greenBoxDetails.setSpecAttrCheck(true);
				}
				try{
					for(int i=0;i<jsonObjectArray.size();i++){
						JSONObject specJsonObj=(JSONObject)jsonObjectArray.get(i);
						String group=(String)specJsonObj.get("group");
						if(group.equalsIgnoreCase("Specifications")){
							JSONArray jsonValuesArray = (JSONArray) jsonObject.get("values");
							for(int k=0;k<jsonValuesArray.size();k++){
								String name=(String)specJsonObj.get("name");
								if(name.equalsIgnoreCase("Tire Size")){
									String tireSize=(String)specJsonObj.get("value");
									greenBoxDetails.setTireSize(tireSize);
									break;
								}
							}

							break;
						}
					}


				}catch (Exception e) {

				}


			}
			catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject jsonObjectTemp = (JSONObject) jsonObject.get("fulfillmentOptions");
			greenBoxDetails.setShipEligible((Boolean) jsonObjectTemp.get("shipInd"));
			greenBoxDetails.setDeliveryEligible((Boolean) jsonObjectTemp.get("deliveryInd"));
			DecimalFormat decimalFormat = new DecimalFormat("#.00");

			//Block to check eligiblity for diffferent module like FBT 
			try{
				jsonObjectTemp = (JSONObject) jsonObject.get("config");
				jsonObjectTemp = (JSONObject) jsonObjectTemp.get("modules");
				jsonObjectTemp = (JSONObject) jsonObjectTemp.get("row2");

				JSONArray jsonModuleArray = (JSONArray) jsonObjectTemp.get("col1");
				greenBoxDetails.setFBTEligible(jsonModuleArray.toJSONString().contains("freqBoughtTogetherInline"));
			}catch(Exception e) {
				e.printStackTrace();			
			}

			try{
				JSONArray jsonObjectAttachments= (JSONArray) jsonObject.get("attachments");
				JSONObject jsonObjectFirst=(JSONObject)jsonObjectAttachments.get(0);
				greenBoxDetails.setFirstAttachment((String)jsonObjectFirst.get("name"));	

			}catch (Exception e) {
				// TODO: handle exception
			}

			try{
				jsonObjectTemp = (JSONObject) jsonObject.get("marketPlace");
				if(null != jsonObjectTemp){
					greenBoxDetails.setStrItemType((String) jsonObjectTemp.get("programType"));
					jsonObjectTemp = (JSONObject) jsonObjectTemp.get("primarySellerInfo");
					greenBoxDetails.setStrItemSellerName((String) jsonObjectTemp.get("sellerName"));
					greenBoxDetails.setStrSellerItemUrl((String) jsonObjectTemp.get("sellerItemUrl"));
					greenBoxDetails.setSellerId((String) jsonObjectTemp.get("sellerId"));
					jsonObjectTemp = (JSONObject) jsonObjectTemp.get("price");
					Double dblPrice = 0.00;
					try{
						//dblPrice=(Double) jsonObjectTemp.get("reg");
						dblPrice=Double.parseDouble((String) jsonObjectTemp.get("reg"));
					} catch(Exception e){

					}
					try{
						dblPrice = Double.valueOf(decimalFormat.format(Double.valueOf(dblPrice)));
					} catch (Exception e) {

					}
					greenBoxDetails.setDblRegPrice(dblPrice);

					dblPrice = 0.00;
					try{
						greenBoxDetails.setStrSaleEndDate((String) jsonObjectTemp.get("saleEndDate"));
						//dblPrice=(Double) jsonObjectTemp.get("sale");
						dblPrice=Double.parseDouble((String) jsonObjectTemp.get("sale"));
					} catch(Exception e){

					}
					try{
						//	dblPrice = Double.valueOf(decimalFormat.format(Double.valueOf(dblPrice)));
					} catch (Exception e) {

					}
					greenBoxDetails.setDblSalePrice(dblPrice);



				}
			}catch (Exception e) {
				// Ignore the attempt to get the CPC
			}


			try{
				jsonObjectTemp = (JSONObject) jsonObject.get("product");
				if (null != jsonObjectTemp) {

					try{
						JSONObject jsonObjectoffer = (JSONObject) jsonObjectTemp.get("offer");
						JSONObject jsonObjectSitesNew = (JSONObject) jsonObjectoffer.get("sites");
						JSONObject jsonObjectSearsNew = (JSONObject) jsonObjectSitesNew.get("sears");
						try{
							JSONObject jsonObjectFinancing = (JSONObject) jsonObjectSearsNew.get("financing");
							if(null!=jsonObjectFinancing){
								greenBoxDetails.setFinanceOfferPresent(true);
								String endDateFinanceOffer=(String)jsonObjectFinancing.get("endDt");
								endDateFinanceOffer=endDateFinanceOffer.substring(0,endDateFinanceOffer.indexOf("T"));

							}else{
								greenBoxDetails.setFinanceOfferPresent(false);
							}
						}
						catch (Exception e) {
							greenBoxDetails.setFinanceOfferPresent(false);
						}
					}catch (Exception e) {

					}
					JSONObject jsonObjectTempNew = (JSONObject) jsonObjectTemp.get("ffm");

					try{

						if(null != jsonObjectTempNew.get("fulfilledBy"))
							greenBoxDetails.setStrFulfilledBy((String) jsonObjectTempNew.get("fulfilledBy"));

						if(null != jsonObjectTempNew.get("dfltFfmDisplay"))
							greenBoxDetails.setStrDefaultFfmDisplay((String) jsonObjectTempNew.get("dfltFfmDisplay"));

						if(null != jsonObjectTempNew.get("isStoreResElig"))
							greenBoxDetails.setStoreResElig((Boolean) jsonObjectTempNew.get("isStoreResElig"));

					}catch (Exception e) {
						e.printStackTrace();
					}

					----------------------------------------------------------------------------------------
					// Block to read the variants data

					try{
						JSONObject vartaintsObject  = (JSONObject) jsonObjectTemp.get("variants");

						VariantsDetails variantsDetails=new VariantsDetails();

						variantsDetails.setSkuCount(((Long) vartaintsObject.get("cnt")).intValue());

						JSONArray jsonAttribureArray = (JSONArray) vartaintsObject.get("definingAttrs");

						Map<String, List<String>> definingAttrs = new HashMap<String, List<String>>();

						// block to get the defining Attributes 

						if(jsonAttribureArray!=null && jsonAttribureArray.size()>0)
						{
							for (int i=0; i < jsonAttribureArray.size(); i++)
							{
								JSONObject definingAttr = (JSONObject) jsonAttribureArray.get(i);								
								String attrKey = (String) definingAttr.get("name");								
								JSONArray attrVals = (JSONArray) definingAttr.get("vals");								
								List<String> attrValList = new ArrayList<String>(); 								

								for (int j=0; j < attrVals.size(); j++){

									JSONObject valName = (JSONObject) attrVals.get(j);
									attrValList.add((String)valName.get("name"));									
								}

								definingAttrs.put(attrKey, attrValList);	

							}
						}

						variantsDetails.setDefiningAttrs(definingAttrs);

						////// Changes/////

						JSONArray jsonSkuArray = (JSONArray) vartaintsObject.get("sku");

						List<VariantsDetails> skuList = new ArrayList<VariantsDetails>();
						try{
							for (int n=0; n < jsonSkuArray.size(); n++){

								VariantsDetails skuDTOObj =new VariantsDetails();
								JSONObject aSkuObject = (JSONObject) jsonSkuArray.get(n);

								skuDTOObj.setId((String)aSkuObject.get("id"));
								skuDTOObj.setAttrName((String)aSkuObject.get("name"));

								JSONObject skuWorker = (JSONObject) aSkuObject.get("altIds");
								skuDTOObj.setCatentryId((String)skuWorker.get("catentryId"));

								skuWorker = (JSONObject) aSkuObject.get("operational");
								skuWorker = (JSONObject) skuWorker.get("sites");
								try{
									JSONObject skuWorker1 = (JSONObject) skuWorker.get("sears");
									skuDTOObj.setOnline((Boolean)skuWorker1.get("isOnline"));
									skuDTOObj.setAvail((Boolean)skuWorker1.get("isAvail"));
									skuDTOObj.setReserveIt((Boolean)skuWorker1.get("isReserveIt"));
								}catch (Exception ex) {
									try {
										JSONObject skuWorker2 = (JSONObject) skuWorker.get("kmart");
										skuDTOObj.setOnline((Boolean)skuWorker2.get("isOnline"));
										skuDTOObj.setAvail((Boolean)skuWorker2.get("isAvail"));

									} catch (Exception e) {
										JSONObject skuWorker3 = (JSONObject) skuWorker.get("craftsman");
										skuDTOObj.setOnline((Boolean)skuWorker3.get("isOnline"));
										skuDTOObj.setAvail((Boolean)skuWorker3.get("isAvail"));
									}




								}


								JSONArray skuWorkerArray = (JSONArray) aSkuObject.get("definingAttrs");
								Map<String, String> skuAttrs = new HashMap<String, String>();
								for (int k=0; k < skuWorkerArray.size(); k++){



									JSONObject attrWorker = (JSONObject) skuWorkerArray.get(k);

									JSONObject attrObject = (JSONObject) attrWorker.get("attr");
									String attrKey = (String) attrObject.get("name");

									JSONObject valueObject = (JSONObject) attrWorker.get("val");
									String attrVal = (String) valueObject.get("name");

									skuAttrs.put(attrKey, attrVal);

								}

								skuDTOObj.setSkuAttrs(skuAttrs);
								skuList.add(skuDTOObj);						
							}
						}catch (Exception e) {

						}
						variantsDetails.setSkuList(skuList);
						productDetails.setVariantsDetails(variantsDetails);
					}
					catch(Exception e){

					}

					----------------------------------------------------------------------------------------


					try{
						JSONObject jsonObjectTaxTemp = (JSONObject) jsonObjectTemp.get("taxonomy");
						jsonObjectTaxTemp = (JSONObject) jsonObjectTaxTemp.get("web");
						jsonObjectTaxTemp = (JSONObject) jsonObjectTaxTemp.get("sites");
						jsonObjectTaxTemp = (JSONObject) jsonObjectTaxTemp.get("sears");
						JSONArray jsonObjectTaxArray = (JSONArray)jsonObjectTaxTemp.get("brandLogoLinks");
						List<String> brandLogoLinks = new ArrayList<String>();


						if(jsonObjectTaxArray!=null && jsonObjectTaxArray.size()>0)
						{
							for (int i=0; i < jsonObjectTaxArray.size(); i++)
							{
								JSONObject brandLogoLink = (JSONObject) jsonObjectTaxArray.get(i);

								brandLogoLinks.add((String) brandLogoLink.get("SpecificLink"));	


							}
						}	

						//	greenBoxDetails.setBrandLogoLinks(brandLogoLinks);

					}
					catch (Exception e) {

					}					
					try{
						JSONObject jsonObjectNew = (JSONObject) jsonObjectTemp.get("operational");
						JSONObject jsonObjectSites = (JSONObject) jsonObjectNew.get("sites");
						JSONObject jsonObjectSears = (JSONObject) jsonObjectSites.get("sears");
						boolean isAvail=(Boolean)jsonObjectSears.get("isAvail");
						boolean isOnline=(Boolean)jsonObjectSears.get("isOnline");
						greenBoxDetails.setAvail(isAvail);
						greenBoxDetails.setOnline(isOnline);
					}
					catch (Exception e) {

					}

					try{
						JSONObject jsonObjectShipping = (JSONObject) jsonObjectTemp.get("shipping");
						boolean isMailable=(Boolean)jsonObjectShipping.get("isMailable");
						greenBoxDetails.setMailable(isMailable);

					}
					catch (Exception e) {

					}


					jsonObjectTemp = (JSONObject) jsonObjectTemp.get("marketplace");
					try{
						greenBoxDetails.setStrItemType((String) jsonObjectTemp
								.get("programType"));
					}catch (Exception e) {

					}
					try{
						greenBoxDetails.setStrGroupId((String) jsonObjectTemp
								.get("groupingId"));

					}catch (Exception e) {
						// TODO: handle exception
					}


					try{
						greenBoxDetails.setIntSellerCount((Integer) jsonObjectTemp.get("sellerCnt"));

					} catch(Exception e){
						try{
							greenBoxDetails.setIntSellerCount(Integer.parseInt((String)jsonObjectTemp.get("sellerCnt")));

						} catch (Exception es) {
						}
					}

				}
				productDetails.setGreenBoxDetails(greenBoxDetails);

			} catch (Exception e) {
			}


			if(productDetails.getVariantsDetails().getSkuCount()>1){
				String pricingUrl=apiUrls.pricingApi.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", partNumber.replace("P","")).replace("PLACEHOLDERFORREGIONID", "0").trim();
				pricingUrl=pricingUrl.replace("PLACEHOLDERFORVARIATION", "3");
				productDetails=jsonPriceParserVariants(pricingUrl, productDetails);
			}else{
				String pricingUrl=apiUrls.pricingApi.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", partNumber.replace("P","")).replace("PLACEHOLDERFORREGIONID", "0").trim();
				pricingUrl=pricingUrl.replace("PLACEHOLDERFORVARIATION", "0");
				productDetails=jsonPriceParserMap(pricingUrl ,productDetails);
			}

			String fbtUrl=apiUrls.fbtApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			productDetails=jsonProdFBTDetailsParser(fbtUrl, productDetails);


		}
		catch(Exception e){
			//productDetails = null;
			//LOGGER.log(Level.SEVERE, "jsonProdAttrWsParser exception for: " + strWsUrl, e);
			e.printStackTrace();
		}

		return productDetails;
	}

	public static String getResponse(String url){

		//System.setProperty("http.proxyHost", "166.76.3.199");
		//System.setProperty("http.proxyPort", "80");

		HttpURLConnection conn = null;
		InputStream is = null;
		URL urlObj = null;                      
		BufferedReader rd = null;
		StringBuilder response = new StringBuilder();
		String line = "";

		try
		{
			urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestProperty("Referer", baseUrl);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "SHC Automation/1.0 KTXN");
			conn.addRequestProperty("Accept", "application/json");
			conn.addRequestProperty("Content-Type", "application/json");
			conn.setConnectTimeout(80000);
			conn.setReadTimeout(80000);

			is = conn.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));

			while ((line = rd.readLine()) != null)
			{
				response.append(line);
			}                         
		}
		catch (MalformedURLException mue)
		{
			mue.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally{
			if(null!=conn)
				conn.disconnect();
		}

		return response.toString();
	}


	public static Map<String,Boolean> jsonStorePickupWsParser(String url){


		Map<String,Boolean> storeList = new HashMap<String, Boolean>();
		String higheststoreIndex="0";
		try {

			for(int j=1;j<=3;j++){

				JSONParser parser = new JSONParser();
				String updatedUrl=url.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORSTARTIDX", higheststoreIndex);
				System.out.println(updatedUrl);

				String response=getResponse(updatedUrl);
				Object obj = parser.parse(response);
				JSONObject jsonObject = (JSONObject) obj;
				JSONObject jsonDataObj=(JSONObject)jsonObject.get("data");
				String foundStoreInd=(String)jsonDataObj.get("foundStore");

				higheststoreIndex=(String)(jsonDataObj.get("highestStoreIndex"));

				if(foundStoreInd.equalsIgnoreCase("y")){
					try{	
						JSONArray storeArray = (JSONArray)jsonDataObj.get("storeInfo");

						if(storeArray!=null && storeArray.size()>0)
						{
							for (int i=0; i < storeArray.size(); i++)
							{
								JSONObject store = (JSONObject) storeArray.get(i);
								boolean isReserveritEligStore=(Boolean)store.get("reserveEligible");
								JSONObject storeInfo=(JSONObject)store.get("storeInfo");

								String storeUnitNumKey=(String) storeInfo.get("storeNumber");
								storeList.put(storeUnitNumKey, isReserveritEligStore);

							}
						}

					}catch (Exception e){

					}
				}

			}

		}catch (Exception e){

		}

		return storeList;
	}





	public static ProductDetails populateAutomotiveDetails(String partNumber) {
		String strWsUrl=apiUrls.prodGBWebserviceUrl.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", partNumber);
		System.out.println("Url is "+ strWsUrl);
		ProductDetails productDetails = new ProductDetails();
		GreenBoxDetails greenBoxDetails=new GreenBoxDetails();
		try{
			JSONParser parser = new JSONParser();

			String response=getResponse(strWsUrl);
			Object obj = parser.parse(response);
			JSONObject jsonObjectMaster = (JSONObject) obj;
			jsonObjectMaster = (JSONObject) jsonObjectMaster.get("data");
			jsonObjectMaster = (JSONObject) jsonObjectMaster.get("product");


			greenBoxDetails.setStrPartNumber((String) jsonObjectMaster.get("id"));
			greenBoxDetails.setStrProdTitle((String) jsonObjectMaster.get("name"));
			greenBoxDetails.setStrProdBrand((String)((JSONObject) jsonObjectMaster.get("brand")).get("name"));
			try{
				JSONObject jsonObjectWorker = (JSONObject) jsonObjectMaster.get("automotive");
				if(null == jsonObjectWorker){
					productDetails.setAutomotiveDetails(null);
				} else {
					AutomotiveDetails automotiveDetails = new AutomotiveDetails();
					AutomotiveSkinnySkuDetails automotiveSkinnySkuDetails = new AutomotiveSkinnySkuDetails();

					automotiveDetails.setStrBrandCodeId((String) jsonObjectWorker.get("brandCodeId"));
					automotiveDetails.setStrAutoType((String) jsonObjectWorker.get("autoType"));
					try{
						automotiveDetails.setFitmentRequired(((String)jsonObjectWorker.get("autoFitment")).contains("Requires"));
					}catch (Exception e) {
						automotiveDetails.setFitmentRequired(false);
					}

					automotiveDetails.setBaseFitmentDetails(fetchBaseFitmentDetails(partNumber));

					try{
						//Now fetch the price
						automotiveDetails.setStrPriceRange(fetchSaveStory(productDetails.getGreenBoxDetails().getStrPartNumber()));
					} catch (Exception e) {
						//automotiveDetails.setStrPriceRange(null);
					}

					jsonObjectWorker = (JSONObject) jsonObjectMaster.get("variants");
					try{
						automotiveDetails.setIntSkinnySkuCount(((Long) jsonObjectWorker.get("cnt")).intValue());
						JSONArray jsonArray = (JSONArray) jsonObjectWorker.get("definingAttrs");
						jsonObjectWorker = (JSONObject) jsonArray.get(0);
						automotiveSkinnySkuDetails.setStrId((String) jsonObjectWorker.get("id"));
						automotiveSkinnySkuDetails.setStrName((String) jsonObjectWorker.get("name"));
						automotiveSkinnySkuDetails.setStrPriceRange(fetchSaveStory(automotiveSkinnySkuDetails.getStrId()));
						automotiveDetails.setAutomotiveSkinnySkuDetails(automotiveSkinnySkuDetails);
					}catch (Exception e) {
						automotiveDetails.setIntSkinnySkuCount(0);
						//automotiveDetails.setAutomotiveSkinnySkuDetails(null);
					}


					productDetails.setAutomotiveDetails(automotiveDetails);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//productDetails.setAutomotiveDetails(null);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"script exception", e);
		}


		return productDetails;
	}
	public static BaseFitmentDetails fetchBaseFitmentDetails(String partNumber){
		BaseFitmentDetails baseFitmentDetails=new BaseFitmentDetails();
		String url=apiUrls.greenBoxUvdSkuApi.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
		try {
			//API call to get AUTOBRAND and AUTOPART
			String response="";

			System.out.println("HostException"+url);
			response=getResponse(url);		


			JSONParser parser = new JSONParser();
			Object	obj = parser.parse(response);
			JSONArray jsonArray = (JSONArray) obj;
			String autoBrandPart=(String) jsonArray.get(0);
			String autoBrands=autoBrandPart.substring(0, 4);
			String autoParts=autoBrandPart.substring(4, autoBrandPart.length());
			baseFitmentDetails.setStrAutoBrand(autoBrands);
			baseFitmentDetails.setStrAutoParts(autoParts);
			System.out.println("autoBrandPart:"+autoBrandPart+";"+"autoBrands:"+autoBrands+";"+"autoParts:"+autoParts);
			//API Call to get Base Vehicle Id
			url=apiUrls.baseVehDetails.replace("PLACEHOLDERFORAUTOBRANDS", autoBrands).replace("PLACEHOLDERFORAUTOPARTS", autoParts);
			response=getResponse(url);
			obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("data");
			jsonArray = (JSONArray) jsonObject.get("fitment");
			String fitmentString = (String) jsonArray.get(0);
			String [] fitments=fitmentString.split(";");
			//vehicleId = vehicleId.substring(0, vehicleId.indexOf(";"));
			baseFitmentDetails.setStrBaseVehicleId(fitments[0]);
			baseFitmentDetails.setBed(fitments[1].replace("be_", ""));
			baseFitmentDetails.setBodyStyle(fitments[2].replace("bo_", ""));
			baseFitmentDetails.setPosition(fitments[7].replace("po_", ""));

			//API Call to get fitment details
			url=apiUrls.yearmakemodel.replace("PLACEHOLDERFORAUTOVEHID", fitments[0]);
			response=getResponse(url);
			obj = parser.parse(response);
			jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("data");
			baseFitmentDetails.setStrYear(((Long) jsonObject.get("year")).toString());
			baseFitmentDetails.setStrMakeId(((Long) jsonObject.get("makeId")).toString());
			baseFitmentDetails.setStrMakeName((String) jsonObject.get("makeName"));
			baseFitmentDetails.setStrModelId(((Long) jsonObject.get("modelId")).toString());
			baseFitmentDetails.setStrModelName((String) jsonObject.get("modelName"));

			// API Call to get engine details
			url=apiUrls.engines.replace("PLACEHOLDERFORAUTOYEAR",baseFitmentDetails.getStrYear())
					.replace("PLACEHOLDERFORAUTOMAKE",baseFitmentDetails.getStrMakeId())
					.replace("PLACEHOLDERFORAUTOMODELS",baseFitmentDetails.getStrModelId());
			response=getResponse(url);
			obj = parser.parse(response);
			jsonObject = (JSONObject) obj;
			jsonArray = (JSONArray) jsonObject.get("data");
			jsonObject = (JSONObject) jsonArray.get(0);
			baseFitmentDetails.setStrVehicleId(((Long) jsonObject.get("vehicleId")).toString());
			baseFitmentDetails.setStrSubModelId(((Long) jsonObject.get("subModelId")).toString());
			baseFitmentDetails.setStrSubModelName((String) jsonObject.get("subModelName"));

			// API Call to get full vehicles details
			url=apiUrls.fullvehicles.replace("PLACEHOLDERFORAUTOVEHSPECID",baseFitmentDetails.getStrVehicleId());
			response=getResponse(url);
			obj = parser.parse(response);
			jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("data");
			jsonArray = (JSONArray) jsonObject.get("engines");
			jsonObject = (JSONObject) jsonArray.get(0);
			baseFitmentDetails.setStrEngConfId(((Long) jsonObject.get("engineConfigId")).toString());
			baseFitmentDetails.setStrEngBaseId(((Long) jsonObject.get("engineBaseId")).toString());
			baseFitmentDetails.setStrLiter((String) jsonObject.get("liter"));
			baseFitmentDetails.setStrCylinder((String) jsonObject.get("cylinders"));
			baseFitmentDetails.setStrBlockType((String) jsonObject.get("blockType"));
			baseFitmentDetails.setStrEngDesignName((String) jsonObject.get("engineDesignationName"));
			baseFitmentDetails.setStrCylHeadTypeName((String) jsonObject.get("cylinderHeadTypeName"));

		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE,"script exception", e);
			//e.printStackTrace();
		}

		return baseFitmentDetails;
	}

	private static String fetchSaveStory(String strPartNumber) {
		String strPriceUrlForVar = apiUrls.pricingApi.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", strPartNumber);
		try{
			JSONParser parser = new JSONParser();
			String response=getResponse(strPriceUrlForVar);
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("price-response");
			jsonObject = (JSONObject) jsonObject.get("item-response");
			strPriceUrlForVar= (String) jsonObject.get("save-story");
			if((null != strPriceUrlForVar) && (!strPriceUrlForVar.isEmpty()))
				strPriceUrlForVar = strPriceUrlForVar.substring(0, strPriceUrlForVar.lastIndexOf(">")+1);
			else
				return null;
			return strPriceUrlForVar;
		} catch (Exception e) {
			return null;
		}
	}

	public static ProductDetails  jsonPriceParserMap(String priceApiUrl, ProductDetails productDetails) {
		NewPriceDetails priceDetails =new NewPriceDetails();

		try {
			JSONParser parser = new JSONParser();

			String apiResponse=getResponse(priceApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			parseJsonPriceDetails(jsonObject,priceDetails);	
			productDetails.setNewPriceDetails(priceDetails);					
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonNewPriceParserMap exception for: " + priceApiUrl);
		}


		return productDetails;
	}	

	*//***
	 * Method to fetch pricing Api v2
	 * @param partNumber
	 * @param  zipcode	
	 * @param isVariantParent : true - if pricing is called for Parent part number of a Variant	
	 * @return
	 *//*
	public static NewPriceDetails fetchPricingApi(String partNo, String zipcode, boolean isVariantParent){
		NewPriceDetails priceDetails = new NewPriceDetails();
		String pricingApiUrl  = apiUrls.pricingApi;
		try {
			JSONParser parser = new JSONParser();
			String memberType = "G";
			String partNumber = partNo ;
			pricingApiUrl = pricingApiUrl.replace("PLACEHOLDERFORSITE", getSite().toUpperCase())
					.replace("PLACEHOLDERFORMEMBERSTATUS", memberType).replace("PLACEHOLDERFORZIPCODE", zipcode);
			if(isVariantParent){
				pricingApiUrl = pricingApiUrl.replace("offer=", "ssin=");				
			} else {
				partNumber = removePartNumberEndingP(partNo);
			}
			pricingApiUrl = pricingApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORBASEURL", baseUrl);

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("AuthID", "SWNgo9DsMKkokRn0rq0kIw==");
			String apiResponse = new HttpRequest().getHttpGetResponseWithHeaders(pricingApiUrl, headers);
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			parseJsonPriceDetailsNew(jsonObject,priceDetails);	
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonNewPriceParserMap exception for: " + pricingApiUrl);
		}
		return priceDetails;	
	}

	*//***
	 * Method to fetch collections Pdp Api
	 * @param partNumber
	 * @return Collection object
	 *//*
	public static Collection fetchCollectionApi(String partNo){
		Collection collection = new Collection();
		String collectionApiUrl = apiUrls.pdpCollectionApi;
		try{
			JSONParser parser = new JSONParser();
			collectionApiUrl = collectionApiUrl.replace("PLACEHOLDERFORSITE", getSite().toLowerCase()).replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORPARTNUMBER", partNo);
			String apiResponse = getResponse(collectionApiUrl);
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject)obj;
			parseJsonCollectionApi(jsonObject, collection);
		} catch (Exception e){
			LOGGER.log(Level.SEVERE, "fetchCollectionApi exception for: " + collectionApiUrl);
		}
		return collection;
	}

	*//***
	 * Method to fetch attributes, offers and availability for a softline product. Uses attributes Api from PDP to fetch these details
	 * @param partNumber, isCollection (isCollection is used as business rules are different for collection items.)
	 * @return the details required would be set on the Product passed to the method which can be accessed with CollecitonProduct: getAttributes/getOffers methods.
	 *//*

	public static void fetchSoftlineAttributesAndOffersAndAvailability(CollectionProduct product,  boolean isCollection){
		String attributesApiUrl = apiUrls.pdpAttributesApi;		
		try{
			JSONParser parser = new JSONParser();
			attributesApiUrl = attributesApiUrl.replace("PLACEHOLDERFORSSIN", product.getSsin()).replace("PLACEHOLDERFORISCOLLECTION", Boolean.toString(isCollection)).replace("PLACEHOLDERFORSITE", getSite().toLowerCase())
					.replace("PLACEHOLDERFORBASEURL", baseUrl);
			String apiResponse = getResponse(attributesApiUrl);
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject)obj;
			parseJsonAttributesApi(jsonObject, product);	

		} catch (Exception e){
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "jsonFetchSoftlineAttributesParser exception for: " + attributesApiUrl);
		}
	}



	public static NewPriceDetails parseMapPriceDetails(String pricingUrl, NewPriceDetails priceDetails) {

		try 
		{
			JSONParser parser = new JSONParser();
			String apiResponse=getResponse(pricingUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");
			JSONArray responses = (JSONArray) priceDisplay.get("response");
			JSONObject response = (JSONObject) responses.get(0);

			JSONObject mapViolationMsg = (JSONObject) response.get("mapViolationMsg");
			priceDetails.setMapViolationDesc((String) mapViolationMsg.get("description"));
			priceDetails.setMapViolationDisplay((String) mapViolationMsg.get("display"));
			priceDetails.setMapViolationFlag(Integer.parseInt((String) mapViolationMsg.get("flag")));

			JSONArray indicators = (JSONArray) response.get("indicators");
			for(int i=0; i<indicators.size(); i++)
			{
				JSONObject indicatorFlag = (JSONObject) indicators.get(i);

				if(indicatorFlag.get("indicatorName").toString()
						.equalsIgnoreCase("hotbuy"))
				{
					if(indicatorFlag.get("flag").toString().equals("0"))
					{
						priceDetails.setHotBuy(false);
					}
					else if(indicatorFlag.get("flag").toString().equals("1"))
					{
						priceDetails.setHotBuy(true);
					}
				}

				if(indicatorFlag.get("indicatorName").toString()
						.equalsIgnoreCase("exceptionValue"))
				{
					if(indicatorFlag.get("flag").toString().equals("0"))
					{
						priceDetails.setExceptionValue(false);
					}
					else if(indicatorFlag.get("flag").toString().equals("1"))
					{
						priceDetails.setExceptionValue(true);
					}
				}

				if(indicatorFlag.get("indicatorName").toString()
						.equalsIgnoreCase("dealSetting"))
				{
					String dealSettingFlag = indicatorFlag.get("flag").toString();
					priceDetails.setDealSettingFlag(dealSettingFlag);
					switch (Integer.parseInt(dealSettingFlag))
					{
					case 0:
						priceDetails.setDealSettingIndicator(false);
						break;
					case 1:
					case 2:
					case 3:
						priceDetails.setDealSettingIndicator(true);
						break;					
					default:
						priceDetails.setDealSettingIndicator(false);
						break;
					}
				}
			}

		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}

		return priceDetails;
	}

	public static NewPriceDetails parseBundleMapPriceDetails(String pricingUrl, NewPriceDetails priceDetails) {

		try 
		{
			JSONParser parser = new JSONParser();
			String apiResponse=getResponse(pricingUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			//JSONArray jsonArray = (JSONArray) obj;
			JSONObject dataNode = (JSONObject) jsonObject.get("data");
			//JSONObject dataNode = (JSONObject) rootNode.get("data");
			JSONArray indicatorNode = (JSONArray) dataNode.get("indicators");
			int bundleMapSetting=0;

			for(Object indicatorObject : indicatorNode){
				JSONObject indicatorObj = (JSONObject) indicatorObject;
				if(indicatorObj.get("name").toString().equalsIgnoreCase("mapViolationMsg")){
					{	
						if(Integer.parseInt(indicatorObj.get("exists").toString())!=0)
						{
							bundleMapSetting=Integer.parseInt(indicatorObj.get("exists").toString());
						}

						break;
					}
				}
			}

			priceDetails.setMapViolationFlag(bundleMapSetting);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}

		return priceDetails;
	}

	// new method to support v2 pricing, the old method will be removed once all 

	public static void parseJsonPriceDetailsNew(JSONObject jsonObject, NewPriceDetails priceDetails) {

		try {
			JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");
			JSONArray responses = (JSONArray) priceDisplay.get("response");
			JSONObject response = (JSONObject) responses.get(0);
			String regexForSpanTags = "[<](/)?span[^>]*[>]";

			priceDetails.setPid((String) response.get("offer"));
			priceDetails.setCurrencyCode((String) response.get("currencyCode"));

			priceDetails.setMemberType((String) response.get("memberType"));
			priceDetails.setPriceType((String) response.get("priceType"));

			JSONObject oldPrice = (JSONObject) response.get("oldPrice");  //Regular price
			priceDetails.setOldPriceDisplay(((String) oldPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setOldPriceLabel((String) oldPrice.get("label"));
			priceDetails.setOldPriceNumeric(Double.parseDouble(oldPrice.get("numeric").toString()));

			JSONObject savings = (JSONObject) response.get("savings");  //you save
			priceDetails.setSavingsDisplay(((String) savings.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setSavingsLabel((String) savings.get("label"));
			priceDetails.setSavingsNumeric(Double.parseDouble(savings.get("numeric").toString()));

			JSONObject finalPrice = (JSONObject) response.get("finalPrice");  //final price that is displayed
			priceDetails.setFinalPriceDisplay(((String) finalPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setFinalPriceLabel((String) finalPrice.get("label"));
			priceDetails.setFinalPriceNumeric(Double.parseDouble(finalPrice.get("numeric").toString()));

			JSONObject bonusPointPrice = (JSONObject) response.get("bonusPointPrice");
			priceDetails.setBonusPointPriceDisplay(((String) bonusPointPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setBonusPointPriceLabel((String) bonusPointPrice.get("label"));
			priceDetails.setBonusPointPriceNumeric(Double.parseDouble(bonusPointPrice.get("numeric").toString()));

			JSONObject sywPrice = (JSONObject) response.get("syw");
			priceDetails.setSywPriceDisplay(((String) sywPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setSywPriceLabel((String) sywPrice.get("label"));
			priceDetails.setSywPriceNumeric(Double.parseDouble(sywPrice.get("numeric").toString()));

			JSONObject ccPrice = (JSONObject) response.get("cc");
			priceDetails.setCcPriceDisplay(((String) ccPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setCcPriceLabel((String) ccPrice.get("label"));
			priceDetails.setCcPriceNumeric(Double.parseDouble(ccPrice.get("numeric").toString()));


			JSONObject sywccPrice = (JSONObject) response.get("sywcc");
			priceDetails.setSywccPriceDisplay(((String) sywccPrice.get("display")).replaceAll(regexForSpanTags, ""));
			priceDetails.setSywccPriceLabel((String) sywccPrice.get("label"));
			priceDetails.setSywccPriceNumeric(Double.parseDouble(sywccPrice.get("numeric").toString()));


			JSONArray smartPlanPrices = (JSONArray) response.get("smartPlanPrice");
			JSONObject smartPlanPrice = (JSONObject) smartPlanPrices.get(0); 

			priceDetails.setSmartPlanPriceDisplay((String) smartPlanPrice.get("display"));
			priceDetails.setSmartPlanPriceLabel((String) smartPlanPrice.get("label"));
			priceDetails.setSmartPlanPriceNumeric(Double.parseDouble(smartPlanPrice.get("numeric").toString()));

			JSONArray priceMatchArr=(JSONArray)response.get("priceMatch");
			JSONObject priceMatchArrObj=(JSONObject)priceMatchArr.get(0);
			boolean priceMatchFlag = ((String) priceMatchArrObj.get("flag")).equals("0")?false:true;
			priceDetails.setPriceMatchFlag(priceMatchFlag);
			priceDetails.setPriceMatchDisplay((String) priceMatchArrObj.get("display"));
			priceDetails.setPriceMatchIndicatorName((String) priceMatchArrObj.get("indicatorName"));
			priceDetails.setPriceMatchLabel((String) priceMatchArrObj.get("label"));

			JSONObject mapViolationMsg = (JSONObject) response.get("mapViolationMsg");
			priceDetails.setMapViolationDesc((String) mapViolationMsg.get("description"));
			priceDetails.setMapViolationDisplay((String) mapViolationMsg.get("display"));
			priceDetails.setMapViolationFlag(Integer.parseInt((String) mapViolationMsg.get("flag")));

			JSONObject prices = (JSONObject) response.get("prices");
			JSONObject pricesFinalPrice = (JSONObject) prices.get("finalPrice");
			priceDetails.setPricesFinalPriceMin(Double.parseDouble(pricesFinalPrice.get("min").toString()));

			JSONArray indicators = (JSONArray) response.get("indicators");

			for (int i = 0; i < indicators.size(); i++) {
				JSONObject indicator = (JSONObject) indicators.get(i);
				boolean flag = ((String) indicator.get("flag")).equals("0")?false:true;

				String indicatorName = (String) indicator.get("indicatorName");

				if (indicatorName.equals("clearance")) {
					priceDetails.setClearanceIndicator(flag);
					priceDetails.setClearanceIndicatorName(indicatorName);
				} else if (indicatorName.equals("upp")) {
					priceDetails.setUppIndicator(flag);
				} else if (indicatorName.equals("rebate")) {
					priceDetails.setRebateIndicator(flag);
				} else if (indicatorName.equals("exceptionValue")) {
					priceDetails.setExceptionValue(flag);
				} else if (indicatorName.equals("hotbuy")) {
					priceDetails.setHotBuy(flag);
				} else if (indicatorName.equals("exceptionValue")) {
					priceDetails.setExceptionValue(flag);
				} else if (indicatorName.equals("onlineOnly")) {
					priceDetails.setOnlineOnly(flag);
				} else if (indicatorName.equals("sale")) {
					priceDetails.setSale(flag);
				} else if (indicatorName.equals("sywMemberMessage")) {
					priceDetails.setSywMemberMessageIndicator(flag);
				} else if (indicatorName.equals("showAkHiMsg")) {
					priceDetails.setShowAkHiMsgIndicator(flag);
				} else if (indicatorName.equals("postalCodeRequired")) {
					priceDetails.setPostalCodeRequiredIndicator(flag);
				} else if (indicatorName.equals("dealSetting")) {
					priceDetails.setDealSettingIndicator(flag);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	//method to parse collection api response

	public static void parseJsonCollectionApi(JSONObject jsonObject, Collection collection){

		JSONObject dataNode = (JSONObject)jsonObject.get("data");
		JSONObject collectionNode = (JSONObject)dataNode.get("collection");
		JSONArray productsNode = (JSONArray) collectionNode.get("products");		
		for(int i=0; i< productsNode.size(); i++){
			JSONObject productNode = (JSONObject)productsNode.get(i);
			String ssin = (String)productNode.get("ssin");
			CollectionProduct product = new CollectionProduct(ssin);
			product.setName((String)productNode.get("name"));
			product.setBrand((String)productNode.get("brand"));
			product.setOfferId((String)productNode.get("offerId"));
			product.setRank(Integer.parseInt((String)productNode.get("rank")));	
			JSONObject operationalNode = (JSONObject)productNode.get("operational");
			JSONObject sitesNode = (JSONObject)operationalNode.get("sites");
			if(sitesNode.get("sears") != null){
				JSONObject searsNode = (JSONObject)sitesNode.get("sears");
				product.setSearsEligible((Boolean) searsNode.get("isDispElig"));
			}
			if(sitesNode.get("kmart") != null){
				JSONObject kmartNode = (JSONObject)sitesNode.get("kmart");
				product.setKmartEligibe((Boolean) kmartNode.get("isDispElig"));
			}
			product.setSoftline(((String)productNode.get("catentrySubType")).equals("V")?true:false);
			collection.addProduct(ssin, product);			
		}		
		JSONObject productStatusNode = (JSONObject)dataNode.get("productstatus");
		boolean canDisplay = (Boolean) productStatusNode.get("canDisplay");
		collection.setCanDisplay((Boolean) productStatusNode.get("canDisplay"));
		collection.setSsin((String)productStatusNode.get("ssin"));

		JSONObject productNode = (JSONObject)dataNode.get("product");
		JSONObject seoNode = (JSONObject) productNode.get("seo");
		collection.setTitle(((String)seoNode.get("title")).split("-")[0]);

	}

	// method to parse attributes api for softlines and update fetch all the offers applicable for the softline

	public static void parseJsonAttributesApi(JSONObject jsonObject, CollectionProduct product){
		Map<String, List<String>> attributes = new HashMap<String, List<String>>();
		JSONObject dataNode = (JSONObject)jsonObject.get("data");
		JSONArray attributesNode = (JSONArray) dataNode.get("attributes");	
		for(int i=0; i<attributesNode.size(); i++){
			JSONObject attributeNode =  (JSONObject) attributesNode.get(i);

			String name = (String) attributeNode.get("name");
			JSONArray valuesNode = (JSONArray) attributeNode.get("values");			
			List<String> values = new ArrayList<String>();
			for(int j=0; j<valuesNode.size(); j++){
				String value = (String) ((JSONObject) valuesNode.get(j)).get("name");
				values.add(value);
			}
			attributes.put(name, values);		
		}
		product.setAttributes(attributes);

		JSONArray variantsNode = (JSONArray) dataNode.get("variants");

		for(int k=0; k<variantsNode.size(); k++){
			JSONObject variantNode = (JSONObject) variantsNode.get(k);
			Offer offer = new Offer();
			offer.setOfferCount(Integer.parseInt(variantNode.get("offerCount").toString()));
			offer.setAvailable((Boolean)variantNode.get("isAvailable"));
			offer.setDeliveryAvail((Boolean)variantNode.get("isDeliveryAvail"));
			offer.setShipAvail((Boolean)variantNode.get("isShipAvail"));
			offer.setPickupAvail((Boolean)variantNode.get("isPickupAvail"));
			offer.setuId((String)variantNode.get("uid"));
			if(offer.getOfferCount()==1){
				offer.setOfferId((String)variantNode.get("offerId"));
			}
			//pending implementation for offerCount >1
			JSONArray variantAttributesNode = (JSONArray) variantNode.get("attributes");	
			Map<String, String> variantAttributes = new HashMap<String, String>();
			for (int l=0; l<variantAttributesNode.size(); l++){
				JSONObject attributeDetails = (JSONObject) variantAttributesNode.get(l);
				variantAttributes.put((String)attributeDetails.get("name"), (String)attributeDetails.get("value"));
			}
			offer.setAttributes(variantAttributes);	
			if(product.getOffers() == null){
				product.setOffers(new HashMap<String, Offer>());
			}
			product.getOffers().put(offer.getOfferId(), offer);
		}		
	}


	public static void parseJsonPriceDetails(JSONObject jsonObject, NewPriceDetails priceDetails) {

		try {
			JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");
			JSONArray responses = (JSONArray) priceDisplay.get("response");
			JSONObject response = (JSONObject) responses.get(0);

			priceDetails.setPid((String) response.get("pid"));

			JSONObject currency = (JSONObject) response.get("currency");
			priceDetails.setCurrencyDisply((String) currency.get("display"));
			priceDetails.setCurrencyCode((String) currency.get("code"));

			priceDetails.setMemberType((String) response.get("mbrType"));
			priceDetails.setPriceType((String) response.get("priceType"));

			JSONObject oldPrice = (JSONObject) response.get("oldPrice");  //Regular price
			priceDetails.setOldPriceDisplay((String) oldPrice.get("display"));
			priceDetails.setOldPriceLabel((String) oldPrice.get("label"));
			priceDetails.setOldPriceNumeric(Double.parseDouble(oldPrice.get("numeric").toString()));

			JSONObject savings = (JSONObject) response.get("savings");  //you save
			priceDetails.setSavingsDisplay((String) savings.get("display"));
			priceDetails.setSavingsLabel((String) savings.get("label"));
			priceDetails.setSavingsNumeric(Double.parseDouble(savings.get("numeric").toString()));

			JSONObject finalPrice = (JSONObject) response.get("finalPrice");  //final price that is displayed
			priceDetails.setFinalPriceDisplay((String) finalPrice.get("display"));
			priceDetails.setFinalPriceLabel((String) finalPrice.get("label"));
			priceDetails.setFinalPriceNumeric(Double.parseDouble(finalPrice.get("numeric").toString()));

			JSONObject bonusPointPrice = (JSONObject) response.get("bonusPointPrice");
			priceDetails.setBonusPointPriceDisplay((String) bonusPointPrice.get("display"));
			priceDetails.setBonusPointPriceLabel((String) bonusPointPrice.get("label"));
			priceDetails.setBonusPointPriceNumeric(Double.parseDouble(bonusPointPrice.get("numeric").toString()));

			JSONObject sywPrice = (JSONObject) response.get("syw");
			priceDetails.setSywPriceDisplay((String) sywPrice.get("display"));
			priceDetails.setSywPriceLabel((String) sywPrice.get("label"));
			priceDetails.setSywPriceNumeric(Double.parseDouble(sywPrice.get("numeric").toString()));

			JSONObject ccPrice = (JSONObject) response.get("cc");
			priceDetails.setCcPriceDisplay((String) ccPrice.get("display"));
			priceDetails.setCcPriceLabel((String) ccPrice.get("label"));
			priceDetails.setCcPriceNumeric(Double.parseDouble(ccPrice.get("numeric").toString()));


			JSONObject sywccPrice = (JSONObject) response.get("sywcc");
			priceDetails.setSywccPriceDisplay((String) sywccPrice.get("display"));
			priceDetails.setSywccPriceLabel((String) sywccPrice.get("label"));
			priceDetails.setSywccPriceNumeric(Double.parseDouble(sywccPrice.get("numeric").toString()));


			JSONArray smartPlanPrices = (JSONArray) response.get("smartPlanPrice");
			JSONObject smartPlanPrice = (JSONObject) smartPlanPrices.get(0); // (JSONObject) response.get("smartPlanPrice");////response.get("smartPlanPrice");

			priceDetails.setSmartPlanPriceDisplay((String) smartPlanPrice.get("display"));
			priceDetails.setSmartPlanPriceLabel((String) smartPlanPrice.get("label"));
			priceDetails.setSmartPlanPriceNumeric(Double.parseDouble(smartPlanPrice.get("numeric").toString()));

			JSONArray priceMatchArr=(JSONArray)response.get("priceMatch");
			JSONObject priceMatchArrObj=(JSONObject)priceMatchArr.get(0);
			boolean priceMatchFlag = ((String) priceMatchArrObj.get("flag")).equals("0")?false:true;
			priceDetails.setPriceMatchFlag(priceMatchFlag);
			priceDetails.setPriceMatchDisplay((String) priceMatchArrObj.get("display"));
			priceDetails.setPriceMatchIndicatorName((String) priceMatchArrObj.get("indicatorName"));
			priceDetails.setPriceMatchLabel((String) priceMatchArrObj.get("label"));

			JSONObject mapViolationMsg = (JSONObject) response.get("mapViolationMsg");
			priceDetails.setMapViolationDesc((String) mapViolationMsg.get("description"));
			priceDetails.setMapViolationDisplay((String) mapViolationMsg.get("display"));
			priceDetails.setMapViolationFlag(Integer.parseInt((String) mapViolationMsg.get("flag")));

			JSONArray indicators = (JSONArray) response.get("indicators");

			for (int i = 0; i < indicators.size(); i++) {
				JSONObject indicator = (JSONObject) indicators.get(i);
				boolean flag = ((String) indicator.get("flag")).equals("0")?false:true;

				String indicatorName = (String) indicator.get("indicatorName");

				if (indicatorName.equals("clearance")) {
					priceDetails.setClearanceIndicator(flag);
					priceDetails.setClearanceIndicatorName(indicatorName);
				} else if (indicatorName.equals("upp")) {
					priceDetails.setUppIndicator(flag);
				} else if (indicatorName.equals("rebate")) {
					priceDetails.setRebateIndicator(flag);
				} else if (indicatorName.equals("exceptionValue")) {
					priceDetails.setExceptionValue(flag);
				} else if (indicatorName.equals("hotbuy")) {
					priceDetails.setHotBuy(flag);
				} else if (indicatorName.equals("exceptionValue")) {
					priceDetails.setExceptionValue(flag);
				} else if (indicatorName.equals("onlineOnly")) {
					priceDetails.setOnlineOnly(flag);
				} else if (indicatorName.equals("sale")) {
					priceDetails.setSale(flag);
				} else if (indicatorName.equals("sywMemberMessage")) {
					priceDetails.setSywMemberMessageIndicator(flag);
				} else if (indicatorName.equals("showAkHiMsg")) {
					priceDetails.setShowAkHiMsgIndicator(flag);
				} else if (indicatorName.equals("postalCodeRequired")) {
					priceDetails.setPostalCodeRequiredIndicator(flag);
				} else if (indicatorName.equals("dealSetting")) {
					priceDetails.setDealSettingIndicator(flag);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}



	public static ProductDetails fetchPOPAPriceCmdParser(String partNumber,String zipcode){
		if(partNumber.endsWith("P")){
			partNumber = partNumber.substring(0,partNumber.length()-1);
		}
		String url = apiUrls.popaPricecmdUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORZIPCODE", zipcode);
		ProductDetails productDetails = new ProductDetails();
		Map<String, String> partNumberPriceComb=new HashMap<String, String>();

		try {
			JSONParser parser = new JSONParser();
			String response=getResponse(url.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject responseObject = (JSONObject)jsonObject.get("response");
			JSONArray jsonArr = (JSONArray)responseObject.get("itemDetails");
			JSONObject itemDetailsFirstElement = (JSONObject)jsonArr.get(0);

			jsonArr = (JSONArray)itemDetailsFirstElement.get("optionPrice");

			if(!jsonArr.isEmpty()){
				for(int i=0;i<jsonArr.size();i++){
					JSONObject optionPriceDetail=(JSONObject) jsonArr.get(i);
					String paItemPartNumber = (String)optionPriceDetail.get("PAItemPartNumber");
					String paPrice = (String)optionPriceDetail.get("price");
					partNumberPriceComb.put(paItemPartNumber, paPrice);
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage() );
		}
		finally{
			productDetails.setPaPricePartnumberComb(partNumberPriceComb);
		}

		return productDetails;

	}


	public static ProductDetails jsonProdAttrDpmParser(String strDpmUrl){
		ProductDetails productDetails = new ProductDetails();

		try{
			JSONParser parser = new JSONParser();
			String response=getResponse(strDpmUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			//String response=getResponse("http://qa.ecom.sears.com:4680/content/pdp/eCoupon/userCoupon/Sears/02073092000P?&upc=839724008968&ksn=5717346&pgtTicket=PGT-2233-9UYtlG21bI6odMZvOpC5KEJjCgnx952qyNTXvNkc43Y9AZbugU-cas&priceType=2&price=15.46&quantity=1");
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;

			try {
				JSONObject couponOffersObj = (JSONObject) jsonObject.get("couponOffers");

				double lCouponCount=(Double)couponOffersObj.get("couponCount");
				productDetails.setCouponCount(lCouponCount);

				JSONArray offersObjArr = (JSONArray) couponOffersObj.get("offers");
				JSONObject offersObj=(JSONObject)offersObjArr.get(0);
				JSONArray couponsArr = (JSONArray) offersObj.get("coupons");

				JSONObject couponDetailsObj=null;
				List <String> couponTitle =new ArrayList<String>();
				for(int i=0;i<couponsArr.size();i++){
					couponDetailsObj=(JSONObject)couponsArr.get(i);
					String title=(String)couponDetailsObj.get("title");
					couponTitle.add(title);
				}

				productDetails.setCouponTitle(couponTitle);

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Coupon node not available in eCoupons Api " );

			}


			JSONObject tellurideOffers = (JSONObject) jsonObject.get("tellurideOffers");
			JSONObject tellurideResponse=(JSONObject) tellurideOffers.get("tellurideResponse");
			JSONObject applicableOffersReplyResponse=(JSONObject) tellurideResponse.get("applicableOffersReplyResponse");
			JSONObject rewardsSummary=(JSONObject) applicableOffersReplyResponse.get("rewardsSummary");
			JSONObject points=(JSONObject) rewardsSummary.get("total");
			String dpmPoints=(String) points.get("Points");
			String dpmDollars=(String) points.get("Dollars");
			dpmDollars=dpmDollars.replace("$", "");
			productDetails.setStrProdDpmPoints(dpmPoints);
			productDetails.setStrProdDpmDollars(dpmDollars);

			JSONArray jsonObjectArray = (JSONArray) rewardsSummary.get("rewardPotentialDetails");
			JSONObject rewardPotentialDetails = (JSONObject) jsonObjectArray.get(0);
			String flag=(String)rewardPotentialDetails.get("conditional");

			if(flag.equalsIgnoreCase("N")){
				productDetails.setConditionalFlag(false);
			}else{
				productDetails.setConditionalFlag(true);
			}

			JSONObject offersSet=(JSONObject) applicableOffersReplyResponse.get("offersSet");
			try{
				JSONArray jsonObjectArrayConditional = (JSONArray) offersSet.get("conditional");
				int countConditional=jsonObjectArrayConditional.size();
				JSONObject conditionalOffers=null;
				List <String> x =new ArrayList<String>();
				for(int i=0;i<countConditional;i++){
					conditionalOffers = (JSONObject) jsonObjectArrayConditional.get(i);
					String offerText=(String)conditionalOffers.get("offerName");
					offerText= offerText.trim();
					x.add(offerText);
				}

				productDetails.setStrOffersText(x);
			}
			catch (Exception e) {
				//productDetails.setStrOffersText(null);
			}

			try {
				JSONArray jsonObjectArrayUnconditional = (JSONArray) offersSet.get("unconditional");
				int countUnconditional=jsonObjectArrayUnconditional.size();
				JSONObject unConditionalOffers=null;
				int y =0;
				for(int i=0;i<countUnconditional;i++){
					unConditionalOffers= (JSONObject) jsonObjectArrayUnconditional.get(i);
					int pointsoffer=Integer.parseInt((String)unConditionalOffers.get("offerRewardPotential"));
					y=y+pointsoffer;

				}
				productDetails.setIntpoints(y);
			} catch (Exception e) {

			}

		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonProdAttrDpmParser exception for: " + strDpmUrl, e);
			e.printStackTrace();
		}

		return productDetails;
	}

	public static ProductDetails  jsonReviewRatingsParser(String reviewRatingsApiUrl) {
		ProductDetails productDetails =new ProductDetails();


		try {
			JSONParser parser = new JSONParser();
			String response=getResponse(reviewRatingsApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject data = (JSONObject) jsonObject.get("data");
			long reviewCount=Long.parseLong(data.get("review_count").toString());
			productDetails.setReviewCount(reviewCount);
			long recommendCount=Long.parseLong(data.get("recommend_count").toString());
			productDetails.setRecommendCount(recommendCount);
			long recommendPercentage=Long.parseLong(data.get("recommend_percentage").toString());
			productDetails.setRecommendPercentage(recommendPercentage);
			String overallRating=(String) data.get("overall_rating");							
			productDetails.setOverallRating(overallRating);

			try{

				JSONArray jsonObjectArrayRating = (JSONArray) data.get("overall_rating_breakdown");

				JSONObject overallRatingBreakdown=null;
				List <String> x =new ArrayList<String>();
				for(int i=0;i<jsonObjectArrayRating.size();i++){
					overallRatingBreakdown = (JSONObject) jsonObjectArrayRating.get(i);
					String ratingCount=(String)overallRatingBreakdown.get("count");
					ratingCount=ratingCount.trim();
					x.add(ratingCount);
				}

				productDetails.setRatingCount(x);
			}
			catch (Exception e) {

				//productDetails.setRatingCount(null);
			}

		}
		catch (Exception e) {
			if(FrameworkProperties.DEBUG)
				e.printStackTrace();
		}

		return productDetails;

	}

	//regional pricing validation




	public static DeliveryDetails  jsonDeliveryApisParser(String deliveryPriceUrl,String payload) {

		DeliveryDetails deliveryDetails = new DeliveryDetails();
		try{

			JSONParser parser = new JSONParser();
			String response;
			Object obj;
			JSONObject jsonObject;
			String instockMsg ="";
			String deliveryDate = "";
			try {

				deliveryPriceUrl=deliveryPriceUrl.replace("PLACEHOLDERFORBASEURL", baseUrl);
				response= new HttpRequest().getPostResponse(deliveryPriceUrl,payload);
				obj = parser.parse(response);
				jsonObject = (JSONObject) obj;
				JSONObject jsonObjectData=(JSONObject)jsonObject.get("data");
				JSONArray jsonArrItem=(JSONArray)jsonObjectData.get("items");
				jsonObjectData=(JSONObject)jsonArrItem.get(0);
				String deliveryPrice="";
				try{
					deliveryPrice = (String)jsonObjectData.get("deliveryCharge");
				}
				catch(Exception e){
					deliveryPrice = String.valueOf((Double)jsonObjectData.get("deliveryCharge"));
				}

				deliveryDetails.setDeliveryPrice(Double.valueOf(deliveryPrice));

				boolean offerInd=(Boolean)jsonObjectData.get("hasSpecialOffers");
				//instockMsg=(String)jsonObjectData.get("itemStatus");
				deliveryDate=(String)jsonObjectData.get("arrivalDate");
				deliveryDetails.setOfferInd(offerInd);
				boolean memberFreeDelInd=(Boolean)jsonObjectData.get("isFreeDeliveryForMembers");
				deliveryDetails.setMemberFreeDelInd(memberFreeDelInd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			deliveryDetails.setDeliveryDate(deliveryDate);			
		}
		catch (Exception e) {
			if(FrameworkProperties.DEBUG)
				e.printStackTrace();
		}

		return deliveryDetails;
	}


	public static ProductDetails jsonPriceParserVariants(String priceApiUrl, ProductDetails productDetails) {
		String price = "";
		PriceDetails priceDetails =new PriceDetails();
		boolean isMap = false;
		long mapSetting = 0;
		Double tempPrice = 0.00;
		double minFinalPrice = 0.00;


		try {

			JSONParser parser = new JSONParser();
			String response=getResponse(priceApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl));
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject itemResponse = (JSONObject) jsonObject.get("price-response");

			JSONObject sellPrice = (JSONObject) itemResponse.get("product-response");
			JSONObject finalPrice = (JSONObject) sellPrice.get("final-price");



			try{
				//minFinalPrice = (Double) finalPrice.get("min");
				minFinalPrice= Double.parseDouble((String) finalPrice.get("min"));
			}catch (Exception e) {

			}
			price = Double.toString(minFinalPrice);

			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			try {
				price = decimalFormat.format(Double.valueOf(price));
			} catch (Exception e) {
			}
			priceDetails.setStrPrice(price);

			JSONObject mapDetails = (JSONObject) sellPrice.get("map-details");
			try{
				isMap = (Boolean) mapDetails.get("violation");
			} catch (Exception e) {
				isMap = false;
			}
			priceDetails.setMap(isMap);
			if (isMap) {
				try {
					//mapSetting = (Long) mapDetails.get("setting");
					mapSetting = Long.parseLong((String) mapDetails.get("setting"));
				} catch (Exception e) {

				}
			}
			priceDetails.setMapSetting(mapSetting);

			tempPrice = 0.00;
			JSONObject promoPrice = (JSONObject) sellPrice.get("promo-price");
			try {

				//	tempPrice = (Double) promoPrice.get("min");
				tempPrice = Double.parseDouble((String) promoPrice.get("min"));
			} catch (Exception e) {

			}
			priceDetails.setPromoPrice(tempPrice);

		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonPriceParserVariants exception for: " + priceApiUrl, e);


		}

		productDetails.setPriceDetails(priceDetails);
		return productDetails;
	}

	public static NewPriceDetails jsonItemSaveStoryPriceParser(String itemSaveStoryPriceApiUrl){
		NewPriceDetails newPriceDetailsObj = new NewPriceDetails();
		try{
			JSONParser parser = new JSONParser();
			itemSaveStoryPriceApiUrl=itemSaveStoryPriceApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl);
			String response=getResponse(itemSaveStoryPriceApiUrl);
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;			
			JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");
			JSONArray responses = (JSONArray) priceDisplay.get("response");
			JSONObject responseObject = (JSONObject) responses.get(0);
			JSONObject finalPrice = (JSONObject) responseObject.get("finalPrice");  
			String price = (String) finalPrice.get("display");

			price = price.replaceAll("[<](/)?span[^>]*[>]", "");
			newPriceDetailsObj.setDisPrice(price);


		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonItemSaveStoryPriceParser exception for: " + itemSaveStoryPriceApiUrl, e);
			e.printStackTrace();
		}
		return newPriceDetailsObj;
	}

	public static ShipDetails jsonProdAttrShipOptionParser(String strShipOptionApiUrl, String payLoad){
		ShipDetails shipDetails = new ShipDetails();
		try{
			JSONParser parser = new JSONParser();
			strShipOptionApiUrl=strShipOptionApiUrl.replace("PLACEHOLDERFORBASEURL", baseUrl);
			String response= new HttpRequest().getPostResponse(strShipOptionApiUrl, payLoad);
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject dataObject = (JSONObject) jsonObject.get("data");
			JSONArray itemsArrayObject = (JSONArray) dataObject.get("items");
			if(null != itemsArrayObject && !(itemsArrayObject.isEmpty())){
				JSONObject itemObject = (JSONObject) itemsArrayObject.get(0);
				JSONObject shipObject = (JSONObject) itemObject.get("shipping");

				String arrivalDate = (String)shipObject.get("arrivalDate");

				SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat myFormat = new SimpleDateFormat("EEE MMM dd");
				String reformattedStr="";
				try {
					reformattedStr = myFormat.format(fromUser.parse(arrivalDate));
				} catch (Exception e) {
					e.printStackTrace();
				}


				shipDetails.setArrivalDate(reformattedStr);
				shipDetails.setFree((Boolean)shipObject.get("free"));
				shipDetails.setFreeEligible((Boolean)shipObject.get("freeEligible"));
				shipDetails.setFreeQualified((Boolean)shipObject.get("freeQualified"));
			} else {
				LOGGER.log(Level.SEVERE, "Shipping Modes not available for the requested items" );
			}			

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "jsonShipOptionApiParser exception for: " + strShipOptionApiUrl + "and payload: "+payLoad, e);
			e.printStackTrace();
		}

		return shipDetails;
	}





	*//**
	 * Returns the auto fitment years to be displayed
	 *//*
	public static List<String> getAutoFitmentYears(String autoFitmentUrl){
		return getAutoFitmentParam(autoFitmentUrl, "yearId");
	}
	*//**
	 * Returns the auto fitment make to be displayed
	 *//*
	public static List<String> getAutoFitmentMake(String autoFitmentUrl){
		return getAutoFitmentParam(autoFitmentUrl, "makeName");
	}

	*//**
	 * Returns the auto fitment models to be displayed
	 *//*
	public static List<String> getAutoFitmentModel(String autoFitmentUrl){
		return getAutoFitmentParam(autoFitmentUrl, "subModelName");
	}


	*//**
	 * Returns the auto fitment params to be displayed
	 *//*
	private static List<String> getAutoFitmentParam(String autoFitmentUrl, String param){
		List<String> years = new ArrayList<String>();
		try{
			String formattedURL = baseUrl;
			if(formattedURL != null){
				formattedURL = fetchUrlDomain(formattedURL);
			}
			autoFitmentUrl=autoFitmentUrl.replace("PLACEHOLDERFORBASEURL", formattedURL);
			String response=getResponse(autoFitmentUrl);
			org.json.JSONObject jsonObj = new org.json.JSONObject(response);
			org.json.JSONArray array = jsonObj.getJSONObject("resultBuffer").getJSONArray("data");
			for(int i=0;i<array.length();i++){
				years.add(array.getJSONObject(i).getString(param));
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "getAutoFitmentYears exception for: " + autoFitmentUrl, e);
			e.printStackTrace();
		}
		return years;
	}
	*//****
	 * Will return the full domain + port. The protocol, file path and query string wont be returned.
	 * @param urlValue
	 * @return
	 *//*
	public static String fetchUrlDomain(String urlValue)
	{
		URL pageURL = null;
		String currentUrlDomain = null;
		try{
			pageURL = new URL(urlValue);
		} 
		catch (MalformedURLException e){
			PageAssert.fail(e.getMessage());
		}
		String currentHost = pageURL.getHost();
		int port = pageURL.getPort();
		if(port!=-1){
			currentUrlDomain = currentHost + ":" + port;
		}
		else
			currentUrlDomain = currentHost;
		return pageURL.getProtocol()+"://"+currentUrlDomain;
	}

	public static List<PromotionPDP>  jsonPromoAPIParser(String partNumber) {
		List<PromotionPDP> promoList = new ArrayList<PromotionPDP>();
		if(partNumber.endsWith("P")){
			partNumber = partNumber.substring(0,partNumber.length()-1);
		}

		try {
			JSONParser parser = new JSONParser();
			String promoPDPApi=apiUrls.productPromoApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);		
			String response=getResponse(promoPDPApi);
			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject data = (JSONObject) jsonObject.get("data");

			JSONObject promoData = (JSONObject) data.get("promodata");
			JSONObject domain = (JSONObject) promoData.get("domain");
			JSONObject searsData = (JSONObject) domain.get("sears");
			Set keys = searsData.keySet();
			//JSONArray promoArray = (JSONArray) searsData.get("regular");
			for(Object key : keys.toArray()){
				key = key.toString();
				JSONArray promoArray = (JSONArray) searsData.get(key);
				for(Object promoObj: promoArray){
					JSONObject promo = (JSONObject) promoObj;
					PromotionPDP promoEntity = new PromotionPDP();
					String _id = (String) promo.get("promoId");
					promoEntity.set_id(_id);
					promoEntity.setSource("promoAPI");
					String startDate = (String) promo.get("startDt");
					promoEntity.setStartDate(startDate);

					//Need to confirm about short description

					String endDate = (String) promo.get("endDt");
					promoEntity.setEndDate(endDate);
					String title = (String) promo.get("title");
					promoEntity.setTitle(title);
					String discountType = (String) promo.get("discType");
					promoEntity.setDiscountType(discountType);
					String promoType = (String) promo.get("promoType");
					promoEntity.setPromoType(promoType);

					//no promocode in api response
					//String promoCode = (String) promo.get("promoCode");
					//promoEntity.setPromoCode(promoCode);

					Object discAmt =  promo.get("discAmt");
					String discountValue = "";
					if(discAmt != null){
						discountValue = discAmt.toString();
					}

					promoEntity.setDiscountValue(discountValue);
					promoEntity.setPartNumber(partNumber);
					String status = (String) promo.get("status");
					promoEntity.setStatus(status);
					String thresholdCond = (String) promo.get("thresholdCond");
					promoEntity.setThresholdCondition(thresholdCond);

					Boolean freePromoFlag = (Boolean) promo.get("freePromoFlag");
					String freePromoFlagStr = "";
					if (freePromoFlag != null){
						freePromoFlagStr = freePromoFlag.toString();
					}
					promoEntity.setFreePromoFlag(freePromoFlagStr);
					String grpName = (String) promo.get("grpName");
					promoEntity.setGrpName(grpName);
					String minPurchaseValStr = "";
					Long minPurchaseVal = (Long) promo.get("minPurchaseVal");
					if(minPurchaseVal != null){
						minPurchaseValStr = minPurchaseVal.toString();
					}
					promoEntity.setMinPurchaseVal(minPurchaseValStr);

					String priority = (String) promo.get("priority");
					promoEntity.setPriority(priority);
					String rank = "";
					Long rankVal = (Long) promo.get("rank");
					if(rankVal != null){
						rank = rankVal.toString();
					}
					promoEntity.setRank(rank);
					String wcsPromoId = (String) promo.get("wcsPromoId");
					promoEntity.setWcsPromoId(wcsPromoId);
					promoList.add(promoEntity);
				}
			}

		}
		catch (Exception e) {
			if(FrameworkProperties.DEBUG)
				e.printStackTrace();
		}

		return promoList;

	}



	public static  ProductOption jsonPOPServiceParser(String partNumber, String zipcode){
		ProductOption productOptions =new ProductOption();
		POPAPIResponseDetails popApiDetails=new POPAPIResponseDetails();
		JSONParser parser = new JSONParser();

		if(partNumber.endsWith("P")){
			partNumber=partNumber.replace("P", "");
		}
		try {
			String popServiceApi=apiUrls.popApiResponseUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORZIPCODE", zipcode);
			String response=getResponse(popServiceApi);
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonData = (JSONObject)jsonObject.get("prodOpts");
			int subTagCount=jsonData.keySet().size();
			System.out.println("# of subtags : "+subTagCount);


			try {
				JSONObject jsonOption=(JSONObject)jsonData.get("PA");
				JSONArray jsonArrTypeOpts = (JSONArray)jsonOption.get("typeOpts");
				int length=jsonArrTypeOpts.size();
				Map<String,String> listDesc= new HashMap<String,String>();

				for(int i=0;i<length;i++){

					JSONObject jsonDesc=(JSONObject)jsonArrTypeOpts.get(i);
					String desc= (String)jsonDesc.get("desc");
					JSONObject jsonPrices=(JSONObject)jsonDesc.get("prices");
					String price=(String)jsonPrices.get("sell");
					listDesc.put(desc,price);
					popApiDetails.setPaDescription(listDesc);
				}

				productOptions.setPopApiResponseDetails(popApiDetails);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  




			try {
				JSONObject jsonOptionRa=(JSONObject)jsonData.get("RA");
				JSONArray jsonArrTypeOptsRa = (JSONArray)jsonOptionRa.get("typeOpts");
				int length=jsonArrTypeOptsRa.size();
				Map<String,String> listRaDesc= new HashMap<String,String>();

				for(int i=0;i<length;i++){

					JSONObject jsonDescRa=(JSONObject)jsonArrTypeOptsRa.get(i);
					String descRa= (String)jsonDescRa.get("desc");
					JSONObject jsonPricesRa=(JSONObject)jsonDescRa.get("prices");
					String priceRa=(String)jsonPricesRa.get("sell");
					listRaDesc.put(descRa,priceRa);
					popApiDetails.setRaDescription(listRaDesc);
				}

				productOptions.setPopApiResponseDetails(popApiDetails);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				JSONObject jsonOptionISS=(JSONObject)jsonData.get("ISS");
				JSONArray jsonArrTypeOptsISS = (JSONArray)jsonOptionISS.get("typeOpts");
				int length=jsonArrTypeOptsISS.size();
				Map<String,String> listIssDesc= new HashMap<String,String>();

				for(int i=0;i<length;i++){

					JSONObject jsonDescISS=(JSONObject)jsonArrTypeOptsISS.get(i);
					String descISS= (String)jsonDescISS.get("desc");
					JSONObject jsonPricesISS=(JSONObject)jsonDescISS.get("prices");
					String priceISS=(String)jsonPricesISS.get("sell");
					listIssDesc.put(descISS,priceISS);
					popApiDetails.setIssDescription(listIssDesc);
				}

				productOptions.setPopApiResponseDetails(popApiDetails);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				JSONObject jsonOptionHA=(JSONObject)jsonData.get("HA");
				JSONArray jsonArrTypeOptsHA = (JSONArray)jsonOptionHA.get("typeOpts");
				int length=jsonArrTypeOptsHA.size();
				Map<String,String> listHaDesc= new HashMap<String,String>();

				for(int i=0;i<length;i++){

					JSONObject jsonDescHA=(JSONObject)jsonArrTypeOptsHA.get(i);
					String descHA= (String)jsonDescHA.get("desc");
					JSONObject jsonPricesHA=(JSONObject)jsonDescHA.get("prices");
					String priceHA=(String)jsonPricesHA.get("sell");
					listHaDesc.put(descHA,priceHA);
					popApiDetails.setHaDescription(listHaDesc);
				}

				productOptions.setPopApiResponseDetails(popApiDetails);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				JSONObject jsonOptionPP=(JSONObject)jsonData.get("PP");
				JSONArray jsonArrTypeOptsPP = (JSONArray)jsonOptionPP.get("typeOpts");
				int length=jsonArrTypeOptsPP.size();
				Map<String,String> listPPDesc= new HashMap<String,String>();

				for(int i=0;i<length;i++){

					JSONObject jsonDescPP=(JSONObject)jsonArrTypeOptsPP.get(i);
					String descPP= (String)jsonDescPP.get("desc");
					JSONObject jsonPricesPP=(JSONObject)jsonDescPP.get("prices");
					String pricePP=(String)jsonPricesPP.get("sell");
					listPPDesc.put(descPP,pricePP);
					popApiDetails.setPpDescription(listPPDesc);
				}

				productOptions.setPopApiResponseDetails(popApiDetails);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return productOptions;


	}

	// Added for SPU Stores login comparision
	public static ArrayList<String> jsonStoreDetailsCompareOnSkuChange(String url){


		ArrayList<String> storeList = new ArrayList();
		String higheststoreIndex="0";

		try {


			for(int j=1;j<=3;j++){
				JSONParser parser = new JSONParser();
				String response=getResponse(url.replace("PLACEHOLDERFORBASEURL", baseUrl).replace("PLACEHOLDERFORSTARTIDX", higheststoreIndex));
				Object obj = parser.parse(response);
				JSONObject jsonObject = (JSONObject) obj;
				JSONObject jsonDataObj=(JSONObject)jsonObject.get("data");
				String foundStoreInd=(String)jsonDataObj.get("foundStore");

				higheststoreIndex=(String)(jsonDataObj.get("highestStoreIndex"));

				if(foundStoreInd.equalsIgnoreCase("y")){
					try{
						JSONArray storeArray = (JSONArray)jsonDataObj.get("storeInfo");

						if(storeArray!=null && storeArray.size()>0)
						{
							for (int i=0; i < storeArray.size(); i++)
							{
								JSONObject store = (JSONObject) storeArray.get(i);
								String storeName=(String) store.get("storeName");
								storeList.add(storeName)
								;							}
						}
					}catch (Exception e){
					}
				}
			}

		}
		catch (Exception e){
		}

		return storeList;
	}


	*//**
	 * Method to return a list of Order Item Entity from input JSON String
	 * This method parse the JSON String, and populates Order Item Entity and return a list or Order Items
	 * @param itemDetails
	 * @return List<POEntity.OrderItemEntity>
	 *//*
	public static List<POEntity.OrderItemEntity> getOrderItems(String itemDetailsJSON) {
		List<POEntity.OrderItemEntity> orderItemList = new ArrayList<POEntity.OrderItemEntity>();
		JSONParser parser = new JSONParser();
		JSONObject obj;
		try{
			obj = (JSONObject)parser.parse(itemDetailsJSON);
			JSONArray itemsJsonArrayContent = (JSONArray) obj.get("items");
			int length=itemsJsonArrayContent.size();

			for(int i=0;i<length;i++){
				POEntity.OrderItemEntity orderItem = new POEntity().new OrderItemEntity();
				JSONObject jsonDesc=(JSONObject)itemsJsonArrayContent.get(i);
				orderItem.setProductName((String)jsonDesc.get("productname"));
				orderItem.setFfm(jsonDesc.get("ffm").toString().toUpperCase().trim());
				orderItem.setBasicFfm(POEntity.ffmMap.get(orderItem.getFfm()));
				orderItem.setPartNumber((String)(jsonDesc.get("partnumber")));
				orderItem.setQuantity((String)(jsonDesc.get("quantity")));
				orderItem.setZipcode((String)(jsonDesc.get("zipcode")));
				orderItem.setGiftwrap((String)(jsonDesc.get("GiftWrap")!=null?jsonDesc.get("GiftWrap"):"NO"));
				orderItem.setShipMode((String)(jsonDesc.get("shipmode")!=null?jsonDesc.get("shipmode"):"standard"));
				orderItem.setSmartPlan((String)(jsonDesc.get("smartplan")!=null?jsonDesc.get("smartplan"):"NO"));

				orderItem.setStoreUpload((String)(jsonDesc.get("Storeupload")!=null?jsonDesc.get("Storeupload"):"N"));

				orderItem.setProtectionAgreement((String)(jsonDesc.get("pa")!=null?jsonDesc.get("pa"):"NO"));

				orderItemList.add(orderItem);
			}
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Exception in 'getOrderItems' method. Exception Message : ", e);
			e.printStackTrace();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in 'getOrderItems' method. Exception Message : ", e);
			e.printStackTrace();
		} 
		return orderItemList;
	}
	*//**
	 * Method to return a list of Payment Entity from input JSON String
	 * This method parse the JSON String, and populates payment Entity and return a list or payment types
	 * @param itemDetails
	 * @return List<POEntity.PaymentEntity>
	 *//*
	public static List<POEntity.PaymentEntity> getPaymentList(String paymentListJSON) {
		List<POEntity.PaymentEntity> paymentList = new ArrayList<POEntity.PaymentEntity>();
		JSONParser parser = new JSONParser();
		JSONObject obj;
		try{
			obj = (JSONObject)parser.parse(paymentListJSON);
			JSONArray paymentJsonArrayContent = (JSONArray) obj.get("payment");
			int length=paymentJsonArrayContent.size();

			for(int i=0;i<length;i++){
				POEntity.PaymentEntity paymentDetails = new POEntity().new PaymentEntity();
				JSONObject jsonDesc=(JSONObject)paymentJsonArrayContent.get(i);
				paymentDetails.setAmount((String)(jsonDesc.get("amount")));
				paymentDetails.setCardNumber((String)(jsonDesc.get("cardnumber")));
				paymentDetails.setPaymentType((String)(jsonDesc.get("paymenttype")));
				paymentDetails.setPin((String)(jsonDesc.get("pin")));
				paymentList.add(paymentDetails);
			}
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Exception in 'getPaymentList' method. Exception Message : ", e);
			e.printStackTrace();
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception in 'getPaymentList' method. Exception Message : ", e);
			e.printStackTrace();
		} 
		return paymentList;
	}



	*//**
	 * Method to read PO JSon file
	 * This method reads input json file for POTests and returns a list of POEntities
	 * @param filepath
	 * @return
	 *//*
	public static List<POEntity> readPODetailsFromJson(String filepath){
		JSONParser parser = new JSONParser();


		int jsonArraySize=0;
		Object obj;
		List<POEntity> poEntityList = new ArrayList<POEntity>();
		try {

			obj = parser.parse(new FileReader(filepath));

			JSONArray  jsonObject = (JSONArray ) obj;
			int index = 0;
			while(jsonArraySize < jsonObject.size())
			{
				JSONObject jsonPOObjX = (JSONObject) jsonObject.get(index);

				POEntity poEntity = new POEntity();
				poEntity.setjsonString(jsonPOObjX.toString());
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonPOObj = (JSONObject)jsonParser.parse(jsonPOObjX.toString());


				Long id= (Long) jsonPOObj.get("id");
				poEntity.set_id(String.valueOf(id));

				String couponCode =     (String) jsonPOObj.get("couponcode");
				poEntity.setCouponCode(couponCode==null||couponCode.equalsIgnoreCase("na")?"":couponCode);

				POEntity.UserEntity userEntity =   poEntity.new UserEntity();
				String userId =  (String) jsonPOObj.get("userId");
				if(userId!=null && !userId.equalsIgnoreCase("NA"))
					userEntity.setEmail(userId);
				else
					userEntity.setEmail("NA");
				String userType =  (String) jsonPOObj.get("user");
				if(userType!=null && userType.equalsIgnoreCase("registered"))
					poEntity.setUserType("registered");
				//userEntity.setUserType("registered");
				else if(userType!=null && userType.equalsIgnoreCase("guest"))
					poEntity.setUserType("guest");



				JSONArray itemsJsonArrayContent = (JSONArray) jsonPOObj.get("items");
				List<POEntity.OrderItemEntity> orderItemList = new ArrayList<POEntity.OrderItemEntity>();
				for(int i = 0; i<itemsJsonArrayContent.size(); i++){
					POEntity.OrderItemEntity orderItem = new POEntity().new OrderItemEntity();
					JSONObject jsonDesc=(JSONObject) itemsJsonArrayContent.get(i);

					orderItem.setFfm(jsonDesc.get("ffm").toString().toUpperCase().trim());
					orderItem.setBasicFfm(POEntity.ffmMap.get(orderItem.getFfm()));
					orderItem.setPartNumber((String)(jsonDesc.get("partnumber")));
					orderItem.setQuantity((String)(jsonDesc.get("quantity")));
					orderItem.setZipcode((String)(jsonDesc.get("zipcode")));
					orderItem.setGiftwrap((String)(jsonDesc.get("GiftWrap")!=null?jsonDesc.get("GiftWrap"):"NO"));
					orderItem.setShipMode((String)(jsonDesc.get("shipmode")!=null?jsonDesc.get("shipmode"):"standard"));
					orderItem.setSmartPlan((String)(jsonDesc.get("smartplan")!=null?jsonDesc.get("smartplan"):"NO"));
					orderItem.setStoreUpload((String)(jsonDesc.get("Storeupload")!=null?jsonDesc.get("Storeupload"):"N"));
					orderItem.setProtectionAgreement((String)(jsonDesc.get("pa")!=null?jsonDesc.get("pa"):"NO"));
					orderItem.setTo((String)(jsonDesc.get("to")));
					orderItem.setFrom((String)(jsonDesc.get("from")));
					orderItem.setEmailId((String)(jsonDesc.get("emailid")));
					orderItem.setComments((String)(jsonDesc.get("comments")));
					orderItemList.add(orderItem);
				}




				List<POEntity.PaymentEntity> paymentList = new ArrayList<POEntity.PaymentEntity>();



				try{
					JSONArray paymentJsonArrayContent = (JSONArray) jsonPOObj.get("payment");
					if(paymentJsonArrayContent==null)
						paymentList = null;
					else{
						int length=paymentJsonArrayContent.size();

						for(int i=0;i<length;i++){
							POEntity.PaymentEntity paymentDetails = new POEntity().new PaymentEntity();
							JSONObject jsonDesc=(JSONObject)paymentJsonArrayContent.get(i);
							paymentDetails.setAmount((String)(jsonDesc.get("amount")));
							paymentDetails.setCardNumber((String)(jsonDesc.get("cardnumber")));
							paymentDetails.setPaymentType((String)(jsonDesc.get("paymenttype")));
							paymentDetails.setPin((String)(jsonDesc.get("pin")));
							paymentList.add(paymentDetails);
						}
					}
				} 


				catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Exception in 'getPaymentList' method. Exception Message : ", e);
					e.printStackTrace();
				} 


				poEntity.setExcelRowNumber(index);
				jsonArraySize++; index++;
				poEntity.setItems(orderItemList);
				poEntity.setPaymentList(paymentList);
				poEntityList.add(poEntity);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		return poEntityList;

	}
	public static String getCartId(String response){
		JSONParser parser = new JSONParser();
		String cartId=null;
		try{
			response=response.replaceAll("\\<.*?\\>", "");
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			JSONObject jsonCartObj=(JSONObject)jsonDataObj.get("cart");
			if(jsonCartObj!=null)
				cartId=(String)jsonCartObj.get("cartId");
			else
				cartId=(String)jsonDataObj.get("cartId");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cartId;
	}
	*//**
	 * Get CartId from the response JSON Message passed.
	 * Method accept the json response & parse's the same and return's cardId.
	 * @param response
	 * @return
	 *//*
	public static String getCartIdFromResponse(String response){
		JSONParser parser = new JSONParser();
		String cartId=null;
		try{
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			cartId=(String)jsonDataObj.get("cartId");

		}
		catch(Exception e)
		{
			e.printStackTrace();
			PageAssert.fail("Exception occured in (getCartIdFromResponse() in com.functionalcomponents.utils.JsonUtils) ."
					+ "\t Exception Message :" + e.getMessage());
		}
		return cartId;
	}

	public static String getCartItemId(String response){
		JSONParser parser = new JSONParser();
		String cartItemId=null;

		try{
			response=response.replaceAll("\\<.*?\\>", "");
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			JSONArray jsonArrayContent = (JSONArray) jsonDataObj.get("addedItems");
			JSONObject jsonObjx=(JSONObject)jsonArrayContent.get(0);
			cartItemId=(String)jsonObjx.get("cartItemId");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cartItemId;
	}

	public static String getCartItemIdForPED(String response){
		JSONParser parser = new JSONParser();
		String cartItemId=null;

		try{
			response=response.replaceAll("\\<.*?\\>", "");
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			JSONObject jsonArrayContent = (JSONObject) jsonDataObj.get("cart");
			JSONObject jsonObj = (JSONObject) jsonArrayContent.get("cartItems");	
			Object[] jsonObjx=jsonObj.keySet().toArray();
			cartItemId=(String)jsonObjx[0].toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cartItemId;
	}


	public static void removeCartItems(String cartId,String cartItemId){
		JSONParser parser = new JSONParser();
		String response="";

		try{

			com.shc.automation.Logger.log("proceeding to remove items from cart", TestStepType.STEP);
			String url=FrameworkProperties.SELENIUM_BASE_URL+"/crsp/api/cart/v1/item/remove";
			String payload="{\"RemoveCartItemRequest\":{\"cartId\":\""+cartId+"\",\"cartItemId\":\""+cartItemId+"\"}}";
			response=new HttpRequest().getPostResponse(url,payload);
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			String responseCode=(String)jsonDataObj.get("responseCode");
			if(responseCode.equalsIgnoreCase("0000")){
				com.shc.automation.Logger.log("Successfully removed the item from cart", TestStepType.SUBSTEP);
			}
			else{
				com.shc.automation.Logger.log("Item removal from cart was unsuccessfull", TestStepType.SUBSTEP);
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public static String getresponseCode(String atcUrl)
	{
		JSONParser parser = new JSONParser();
		String responseCode=null;
		try{
			String response=getResponse(atcUrl);
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj=(JSONObject)jsonObject.get("response");
			JSONArray jsonArrayContent = (JSONArray) jsonDataObj.get("addedItems");
			JSONObject jsonObjx=(JSONObject)jsonArrayContent.get(0);
			responseCode=(String)jsonDataObj.get("responseCode");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return responseCode;
	}

	public static ProductDetails mapPriceAPITesting(String partNumber){
		ProductDetails productDetails =new ProductDetails();
		GreenBoxDetails greenBoxDetails=new GreenBoxDetails();
		boolean isOnline=false;
		try {
			JSONParser parser = new JSONParser();
			if(partNumber.endsWith("P")){
				partNumber=partNumber.substring(0,partNumber.lastIndexOf("P"));
			}
			String offerApi=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			JSONObject jsonOfferNode = null;
			String responseOffer=getResponse(offerApi);
			JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);
			JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
			jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");
			jsonOfferNode =(JSONObject) jsonVarOfferObject.get("offer");
			JSONObject jsonOperationalNode = (JSONObject)jsonOfferNode.get("operational");
			JSONObject jsonSitesNode = (JSONObject)jsonOperationalNode.get("sites");
			JSONObject jsonSearsNode = (JSONObject)jsonSitesNode.get("sears");
			greenBoxDetails.setOnlineStatus((Boolean) jsonSearsNode.get("isOnline"));
			isOnline=(Boolean)jsonSearsNode.get("isOnline");
			greenBoxDetails.setOnline(isOnline);
			productDetails.setGreenBoxDetails(greenBoxDetails);

		}
		catch (Exception e) 
		{

		}


		if(isOnline)
		{
			String pricingUrl=apiUrls.pricingApi.replace("PLACEHOLDERFORPARTNUMBER", partNumber).trim();
			pricingUrl=pricingUrl.replace("PLACEHOLDERFORVARIATION", "0");
			pricingUrl=pricingUrl.replace("PLACEHOLDERFORMEMBERSTATUS", "G");
			productDetails=jsonPriceParserMap(pricingUrl, productDetails);
		}
		return productDetails;
	}	


	public static Integer getMapSetting(String partNumber)
	{
		String pricingUrl = apiUrls.pricingApi.replace("PLACEHOLDERFORPARTNUMBER", partNumber).trim();
		pricingUrl = pricingUrl.replace("PLACEHOLDERFORVARIATION", "0");
		pricingUrl = pricingUrl.replace("PLACEHOLDERFORMEMBERSTATUS", "G");
		NewPriceDetails priceDetails; 
		if(ComparisonUtils.isPEDTest())
		{
			ProductDetails prodObj = (ProductDetails) TestContext.get().get(Constants.PRODUCT_DETAILS);
			if(prodObj.getNewPriceDetails()!=null)
			{
				priceDetails = prodObj.getNewPriceDetails();
			}
			else
			{
				priceDetails = new NewPriceDetails();
				prodObj.setNewPriceDetails(priceDetails);
			}
		}
		else	
			priceDetails = new NewPriceDetails();
		priceDetails = parseMapPriceDetails(pricingUrl, priceDetails);
		return priceDetails.getMapViolationFlag();
	}


	public static Integer getBundleMapSetting(String partNumber)
	{
		String pricingUrl = apiUrls.bundlePricing.replace("PLACEHOLDERFORPARTNUMBER", partNumber).trim();
		NewPriceDetails priceDetails = new NewPriceDetails();
		priceDetails = parseBundleMapPriceDetails(pricingUrl, priceDetails);
		return priceDetails.getMapViolationFlag();
	}


	*//**
	 * This method is used to a response back in a JSON format by sending web
	 * call to the web services layers
	 * 
	 * @param baseUrl
	 * @param urls
	 * @return
	 * @throws Exception
	 *//*
	public static JsonNode getResponse(String baseUrl, String serviceURL) throws Exception {

		String url = "";

		// Forming the URL to be test by adding the URL to the box against which
		// to be tested
		url	= baseUrl + serviceURL;

		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

		// Checking if a valid status code is obtained in the response.
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		ObjectMapper m = new ObjectMapper();

		// Obtaining the root node from the JSON response
		JsonNode rootNode = m.readTree(response.getEntity(String.class));

		// Returning the root node
		return rootNode;
	}

	public static boolean irp_dev_host_check(String url){
		try{
			String response = getResponse(url);

			if(response.contains("<input type=\"hidden\" name=\"hostname\" value=\"crsapp302p.dev.ch3.s.com\"/>"))
				return true;
			else
				return false;
		}
		catch(Exception e){
			System.out.println("no response from url :- url");
			return false;
		}

	}

	public static HashMap<String,String> getInStockStatusFromResponse(String response,String mode){
		JSONParser parser = new JSONParser();
		HashMap<String,String> facilitiesMap=new HashMap();
		try{
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray jsonArrayItems=(JSONArray)jsonObject.get("items");
			if(jsonArrayItems!=null){
				for(int i=0;i<jsonArrayItems.size();i++){
					JSONObject jsonObjItem=(JSONObject)jsonArrayItems.get(i);
					JSONArray jsonArrayModes=(JSONArray)jsonObjItem.get("modes");
					for(int j=0;j<jsonArrayModes.size();j++){
						JSONObject jsonObjModes=(JSONObject)jsonArrayModes.get(j);
						JSONArray jsonArrayFacilities=(JSONArray)jsonObjModes.get("facilities");
						if(jsonArrayFacilities!=null){
							for(int k=0;k<jsonArrayFacilities.size();k++){
								JSONObject jsonObjFacilities=(JSONObject)jsonArrayFacilities.get(k);
								String qtyAvailable=jsonObjFacilities.get("qtyAvailable").toString();
								String ffmType=jsonObjFacilities.get("ffmType").toString();
								facilitiesMap.put("qtyAvailable", qtyAvailable);
								facilitiesMap.put("ffmType", ffmType);
							}
						}
						else
							LOGGER.log(Level.SEVERE, "Facilities node not available" );
					}
				}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();

		}
		return facilitiesMap;
	}

	public static ArrayList<String> getErrorStringFromResponse(String response){
		JSONParser parser = new JSONParser();
		ArrayList<String> ar=null;
		try{		
			Object obj= parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;

			JSONObject jsonArrayMetadata=(JSONObject)jsonObject.get("metadata");
			if(jsonArrayMetadata!=null){
				JSONArray jsonArrayError=(JSONArray)jsonArrayMetadata.get("error");
				if(jsonArrayError!=null){
					for(int j=0;j<jsonArrayError.size();j++){
						String error=jsonArrayError.get(j).toString();
						ar.add(error);
						LOGGER.log(Level.SEVERE, "Error message  : " +error);
					}
				}
			}
			else
				LOGGER.log(Level.SEVERE, "No error found" );
		}
		catch(Exception exp){
			exp.printStackTrace();
		}
		return ar;
	}
	*//**
	 * Method to Call Gift Card Service & return a Map of Gift Card's ('Card Number and Pin')
	 * @param url : URL of Gift Card Service
	 * @return
	 *//*
	public static Map<String,String> getGiftCard(String url){
		String response;
		Map<String,String> giftCardMap	= new HashMap<String, String>();
		Get Json response from Gift Card Service(getValidGC) 

		com.shc.automation.Logger.log("Gift Card service, request url: " + url, TestStepType.DATA_CAPTURE);
		response = getResponse(url);
		com.shc.automation.Logger.log("Response : " + response, TestStepType.DATA_CAPTURE);

		System.out.println("Gift Card Service ('getValidGC') Request : " + url);
		System.out.println("Gift Card Service ('getValidGC') Response : " + response);
		JSONParser parser = new JSONParser();
		try{		
			Object obj     			  	=  	parser.parse(response);
			JSONObject jsonObject 	  	= 	(JSONObject) obj;
			JSONArray  giftCardsList  	= 	(JSONArray)jsonObject.get("GiftCards");
			if(giftCardsList.size()==0)
				PageAssert.fail("Gift Card Service returned empty gift card array. Please try after 15 minutes");
			for(int i=0;i<giftCardsList.size();i++){
				JSONObject object 	= (JSONObject) giftCardsList.get(i);
				JSONObject giftCard = (JSONObject) object.get("GiftCard");
				giftCardMap.put(giftCard.get("Number").toString().trim(), giftCard.get("Pin").toString().trim());
			}		
		}catch(Exception exp){
			exp.printStackTrace();
		}
		return giftCardMap;
	}

	public static String getCartIdForViewCartApi(String response){
		JSONParser parser = new JSONParser();
		String cartId=null;
		try{
			response = response.replaceAll("\\<.*?\\>", "");
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonDataObj = (JSONObject)jsonObject.get("response");
			jsonDataObj = (JSONObject)jsonDataObj.get("cart");
			cartId = (String)jsonDataObj.get("cartId");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cartId;
	}


	*//****
	 * Check if item is having variation
	 * @param partNumber
	 * @return
	 *//*
	public static Boolean isVariantItem(String partNumber)
	{
		partNumber = modifyPartNumber(partNumber);

		JSONParser parser = new JSONParser();

		String pdpServiceApi=apiUrls.pdpServiceApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber)
				.replace("PLACEHOLDERFORBASEURL", baseUrl);

		System.out.println("PDPSERVICE Api  URL :"+pdpServiceApi);
		String response=getResponse(pdpServiceApi);
		Object obj;
		try 
		{
			obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject jsonWorker = (JSONObject)jsonObject.get("data");
			JSONObject jsonProductStatusNode = (JSONObject)jsonWorker.get("productstatus");
			return (Boolean)jsonProductStatusNode.get("isVariant");
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		return false;
	}


	public static String modifyPartNumber(String partNumber)
	{
		if(!partNumber.endsWith("P"))
		{
			partNumber=partNumber+"P";
		}
		return partNumber;
	}

	*//***
	 * Capture and store attributes
	 * @param partNumber
	 *//*
	public static void storeOnlineChildPartNumbers(String partNumber)
	{
		partNumber = modifyPartNumber(partNumber);
		ProductDetails productDetails = new ProductDetails();

		try
		{
			//			partNumber = ComparisonUtils.formatPartNumber(partNumber);

			String varAttributeApi = apiUrls.varAttributeApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);

			System.out.println("varAttributeApi API URL :"+varAttributeApi);

			VariantsDetails variantsDetails = new VariantsDetails();

			JSONObject jsonObjectWorker = null;
			String responseVarAttr=getResponse(varAttributeApi);
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(responseVarAttr);

			JSONObject jsonObjectMaster = (JSONObject) jsonArray.get(0);
			jsonObjectMaster =(JSONObject) jsonObjectMaster.get("_blob");
			jsonObjectWorker =(JSONObject) jsonObjectMaster.get("attributes");

			List<VariantsDetails> skuList = new ArrayList<VariantsDetails>();
			Map<String, String> skuAttrs=null;
			VariantsDetails skuDTOObj;

			JSONArray skuWorkerArray = (JSONArray) jsonObjectWorker.get("uids");
			variantsDetails.setSkuCount(skuWorkerArray.size());

			for (int k=0; k < skuWorkerArray.size(); k++){
				skuDTOObj =new VariantsDetails();
				skuAttrs = new HashMap<String, String>();

				JSONObject attrWorker = (JSONObject) skuWorkerArray.get(k);

				JSONArray skuUidDefArray = (JSONArray) attrWorker.get("uidDefAttrs");
				for (int m=0; m < skuUidDefArray.size(); m++)
				{

					JSONObject attrObject = (JSONObject) skuUidDefArray.get(m);
					String attrKey = (String) attrObject.get("attrName");
					String attrVal = (String) attrObject.get("attrVal");
					skuAttrs.put(attrKey, attrVal);

				}

				skuDTOObj.setSkuAttrs(skuAttrs);

				String childpartnumber="";

				try
				{
					childpartnumber = (String)attrWorker.get("offerId");
					skuDTOObj.setId(childpartnumber);
				}
				catch (Exception e) 
				{

				}

				if(childpartnumber.endsWith("P"))
				{
					childpartnumber=childpartnumber.substring(0,childpartnumber.lastIndexOf("P"));
				}

				String varOfferApi = apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", childpartnumber);//Childpartnumber
				JSONObject jsonOfferObjectWorker = null;
				String responseOffer = getResponse(varOfferApi);
				JSONArray jsonOfferArray = (JSONArray) parser.parse(responseOffer);

				JSONObject jsonVarOfferObject = (JSONObject) jsonOfferArray.get(0);
				jsonVarOfferObject = (JSONObject) jsonVarOfferObject.get("_blob");
				jsonOfferObjectWorker = (JSONObject) jsonVarOfferObject.get("offer");

				JSONObject skuWorker = (JSONObject) jsonOfferObjectWorker.get("operational");
				skuWorker = (JSONObject) skuWorker.get("sites");
				try
				{
					JSONObject skuWorker1 = (JSONObject) skuWorker.get("sears");
					skuDTOObj.setOnline((Boolean)skuWorker1.get("isOnline"));
					skuDTOObj.setAvail((Boolean)skuWorker1.get("isAvail"));

					skuList.add(skuDTOObj);

					variantsDetails.setSkuList(skuList);
					productDetails.setVariantsDetails(variantsDetails);

					break;
				}
				catch (Exception ex) 
				{
					//					Assert.fail("Part number is of");
				}		

			}

		} 
		catch (Exception e) 
		{
			System.err.println(e.getMessage());
			//supress exception;
		}

		TestContext.get().put(Constants.PRODUCT_DETAILS, productDetails);

	}


	public static Boolean isSRESEligible(String partNumber, String zipcode)
	{
		partNumber = ComparisonUtils.formatPartNumber(partNumber);

		JSONParser parser = new JSONParser();
		String fulfillmentsApi = apiUrls.fulfillmentsURL.replace("PLACEHOLDERFORPARTNUMBER", partNumber)
				.replace("PLACEHOLDERFORBASEURL", baseUrl)
				.replace("PLACEHOLDERFORZIPCODE", zipcode)
				;
		String response=getResponse(fulfillmentsApi);
		Object obj = null;
		try 
		{
			obj = parser.parse(response);
		} catch (ParseException e) 
		{
			e.printStackTrace();
		}  
		JSONObject jsonObject = (JSONObject) obj;

		JSONObject jsonData = (JSONObject)jsonObject.get("data");
		JSONArray storeInfo = (JSONArray)jsonData.get("storeInfo");

		String ffmType = null;

		if(!(storeInfo.isEmpty()))
		{

			for(int i=0; i<storeInfo.size(); i++)
			{
				JSONObject ffmObj = (JSONObject)storeInfo.get(i);
				ffmType = (String)ffmObj.get("ffmType");
				break;
			}

		}

		if(ffmType.equals("SRES"))
			return true;

		return false;
	}

	public static void topRankingAPI(String partNumber , ProductDetails productDetails)
	{
		GreenBoxDetails greenBoxDetails = productDetails.getGreenBoxDetails();

		String uid = (String)TestContext.get().get("UID");
		String varOfferApi = apiUrls.topRankedOfferApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORUID", uid).replace("PLACEHOLDERFORBASEURL", baseUrl);
		JSONObject jsonOfferObjectWorker = null;
		String responseOffer=getResponse(varOfferApi);

		JSONParser parser = new JSONParser();

		JSONObject jsonVarOfferObject = null;
		try 
		{
			jsonVarOfferObject = (JSONObject)parser.parse(responseOffer);
		} 
		catch (ParseException e1) 
		{
			e1.printStackTrace();
		}

		//		jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("_blob");  /* Commented on Sep 04 for handling top ranked call for child in softline  
		jsonVarOfferObject =(JSONObject) jsonVarOfferObject.get("data");  //Added on Sep 04
		jsonOfferObjectWorker =(JSONObject) jsonVarOfferObject.get("offer");



		JSONObject jsonffmNode = (JSONObject)jsonOfferObjectWorker.get("ffm");
		greenBoxDetails.setStrSoldBy((String)jsonffmNode.get("soldBy"));
		if(null != jsonffmNode.get("isSResElig"))
			greenBoxDetails.setStoreReserveInd((Boolean) jsonffmNode.get("isSResElig"));

		if(null != jsonffmNode.get("isSpuElig")){
			greenBoxDetails.setSpuEligible((Boolean) jsonffmNode.get("isSpuElig"));
		}else{
			greenBoxDetails.setSpuEligible(false);
		}

		if(null != jsonffmNode.get("isShipElig")){
			greenBoxDetails.setShipEligible((Boolean) jsonffmNode.get("isShipElig"));
		}else{
			greenBoxDetails.setShipEligible(false);
		}

		if(null != jsonffmNode.get("isDeliveryElig")){
			greenBoxDetails.setDeliveryEligible((Boolean) jsonffmNode.get("isDeliveryElig"));
		}else{
			greenBoxDetails.setDeliveryEligible(false);
		}

		if(null != jsonffmNode.get("fulfilledBy"))
			greenBoxDetails.setStrFulfilledBy((String) jsonffmNode.get("fulfilledBy"));
		if(null != jsonffmNode.get("dfltFfmDisplay"))
			greenBoxDetails.setStrDefaultFfmDisplay((String) jsonffmNode.get("dfltFfmDisplay"));
		if(null != jsonffmNode.get("isStoreResElig"))
			greenBoxDetails.setStoreResElig((Boolean) jsonffmNode.get("isStoreResElig"));
		if(null != jsonffmNode.get("channel"))
			greenBoxDetails.setFfmChannel((String) jsonffmNode.get("channel"));
		//Added on Sep 04
		 If offermapping node is present, ffm should be captured from this node
		try
		{
			JSONObject offermapping = (JSONObject)jsonVarOfferObject.get("offermapping");
			JSONObject fulfillment = (JSONObject)offermapping.get("fulfillment");

			if(null != fulfillment.get("shipping"))
			{
				greenBoxDetails.setShipEligible((Boolean) fulfillment.get("shipping"));
			}
			else
			{
				greenBoxDetails.setShipEligible(false);
			}

			if(null != fulfillment.get("storepickup"))
			{
				greenBoxDetails.setSpuEligible((Boolean) fulfillment.get("storepickup"));
			}
			else
			{
				greenBoxDetails.setSpuEligible(false);
			}

			if(null != fulfillment.get("delivery"))
			{
				greenBoxDetails.setDeliveryEligible((Boolean) fulfillment.get("delivery"));
			}else
			{
				greenBoxDetails.setDeliveryEligible(false);
			}

		}
		catch(Exception e)
		{
			System.err.println("offermapping node is not present");
		}
	}


	public static boolean isBundleItem(String partNumber){

		boolean isBundle = false;

		if(!partNumber.endsWith("B") && !(partNumber.endsWith("P")||partNumber.endsWith("p")))
		{
			partNumber=partNumber+"P";
		}

		try 
		{
			JSONParser parser = new JSONParser();
			String pdpServiceApi=apiUrls.contentApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			System.out.println(pdpServiceApi);
			String response=getResponse(pdpServiceApi);
			Object obj = parser.parse(response);  
			JSONArray jsonArray = (JSONArray) obj;

			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			JSONObject jsonWorker = (JSONObject)jsonObject.get("_blob");
			if(jsonWorker.containsKey("bundle"))
				isBundle = true;
		}
		catch(Exception e)
		{
			isBundle = false;
		}

		return isBundle;

	}

	public static ProductDetails parseBundleProducts(String partNumber,ProductDetails prodObj){
		if(!partNumber.endsWith("B") && !(partNumber.endsWith("P")||partNumber.endsWith("p")))
		{
			partNumber=partNumber+"P";
		}
		try 
		{
			JSONParser parser = new JSONParser();
			String pdpServiceApi=apiUrls.contentApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			//System.out.println(pdpServiceApi);
			String response=getResponse(pdpServiceApi);
			Object obj = parser.parse(response);  
			JSONArray jsonArray = (JSONArray) obj;

			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			JSONObject jsonWorker = (JSONObject)jsonObject.get("_blob");

			JSONObject bundleNode = (JSONObject)jsonWorker.get("bundle");


			// will enable once bundle fetch instock item method is added
			ArrayList<String> offerId =new ArrayList<String>();
			JSONArray bundleGroup = (JSONArray)bundleNode.get("bundleGroup");

			for (Object object : bundleGroup)
			{
				JSONObject bundleObject=(JSONObject)object;
				if(bundleObject.get("type").toString().equalsIgnoreCase("required")){
					JSONArray productsNode = (JSONArray)bundleObject.get("products");
					JSONObject offerIDOBj=(JSONObject)productsNode.get(0);
					String requirePartNumber=offerIDOBj.get("offerId").toString();
					offerId.add(requirePartNumber);


				}	
			}

			prodObj.setBundleRequiredPartNumber(offerId);

			JSONObject operationalNode = (JSONObject)bundleNode.get("operational");
			JSONObject sitesNode = (JSONObject)operationalNode.get("sites");
			boolean isAvail=false;
			boolean isDispElig=false;
			boolean isOnline=false;

			if(sitesNode.containsKey(FrameworkProperties.SITE_TO_SEARCH_IN_KEYWORD_DB.toLowerCase())){
				JSONObject soldNode = (JSONObject)sitesNode.get(FrameworkProperties.SITE_TO_SEARCH_IN_KEYWORD_DB.toLowerCase());
				isAvail=(Boolean)soldNode.get("isAvail");
				isDispElig=(Boolean)soldNode.get("isDispElig");
				isOnline=(Boolean)soldNode.get("isOnline");
			}
			else
			{
				JSONObject soldNode = (JSONObject)sitesNode.get("sears");
				isAvail=(Boolean)soldNode.get("isAvail");
				isDispElig=(Boolean)soldNode.get("isDispElig");
				isOnline=(Boolean)soldNode.get("isOnline");
			}


			JSONObject searsNode = (JSONObject)sitesNode.get("sears");

			boolean isAvail=(Boolean)searsNode.get("isAvail");
			boolean isDispElig=(Boolean)searsNode.get("isDispElig");
			boolean isOnline=(Boolean)searsNode.get("isOnline");

			prodObj.setSearsDisplayEligible(isDispElig);

			GreenBoxDetails objGreenBoxDetails =new GreenBoxDetails();
			objGreenBoxDetails.setOfferId(partNumber);
			objGreenBoxDetails.setOnlineStatus(isOnline);
			objGreenBoxDetails.setAvail(isAvail);
			objGreenBoxDetails.setIsDontDisplay(isDispElig);

			String bundleTitle = (String)bundleNode.get("name");
			objGreenBoxDetails.setStrProdTitle(bundleTitle);

			JSONObject jsonffmNode = (JSONObject)bundleNode.get("ffm");
			objGreenBoxDetails.setStrSoldBy((String)jsonffmNode.get("soldBy"));

			if(null != jsonffmNode.get("isSpuElig")){
				objGreenBoxDetails.setSpuEligible((Boolean) jsonffmNode.get("isSpuElig"));
			}else{
				objGreenBoxDetails.setSpuEligible(false);
			}

			if(null != jsonffmNode.get("isShipElig")){
				objGreenBoxDetails.setShipEligible((Boolean) jsonffmNode.get("isShipElig"));
			}else{
				objGreenBoxDetails.setShipEligible(false);
			}

			if(null != jsonffmNode.get("isDeliveryElig")){
				objGreenBoxDetails.setDeliveryEligible((Boolean) jsonffmNode.get("isDeliveryElig"));
			}else{
				objGreenBoxDetails.setDeliveryEligible(false);
			}

			if(null != jsonffmNode.get("fulfilledBy"))
				objGreenBoxDetails.setStrFulfilledBy((String) jsonffmNode.get("fulfilledBy"));





			objGreenBoxDetails.setStrPartNumber(partNumber);
			prodObj.setGreenBoxDetails(objGreenBoxDetails);
		}
		catch(Exception e)
		{
			com.shc.automation.Logger.log("Exception in parsing bundle products:"+e.getMessage(), TestStepType.STEP);
			prodObj = null;
		}

		return prodObj;

	}


	public synchronized static List<GreenBoxDetails> topSellerMattress(String[] urlArray){

		String url;
		String solarResponse;
		ProductDetails prodObj = new ProductDetails();

		List<GreenBoxDetails> listOfProductsArrLst = new ArrayList<GreenBoxDetails>();



		for(String searchurl:urlArray)
		{

			com.shc.automation.Logger.log("Search pop semantic url: " + searchurl, TestStepType.DATA_CAPTURE);
			solarResponse = getResponse(searchurl);
			JSONParser parser = new JSONParser();
			String partNumber="";
			String sellerName="";
			if(solarResponse!=null)
			{

				try{		

					Object solarResponseObj     			  	=  	parser.parse(solarResponse);
					JSONObject solarResponsejsonObject 	  	= 	(JSONObject) solarResponseObj;
					JSONObject responseObject=(JSONObject) solarResponsejsonObject.get("response");

					JSONObject solrxObject=(JSONObject) solarResponsejsonObject.get("solrx");
					JSONArray filterArr=(JSONArray)solrxObject.get("filters");
					String filterIndex=getBrandIndex(filterArr,"Brand");
					int index;
					if(filterIndex!=null)
					{
						index=Integer.parseInt(filterIndex)+1;

						JSONArray BrandArr=(JSONArray)filterArr.get(index);  //Brand index
						JSONArray docsArr=(JSONArray)responseObject.get("docs");
						if(docsArr.size()>0&docsArr!=null)
						{
							for(int i=0;i<docsArr.size();i++)
							{
								GreenBoxDetails greenBoxDetails=new GreenBoxDetails();
								JSONObject docsArrObj 	= (JSONObject) docsArr.get(i);
								partNumber=(String) docsArrObj.get("partnumber").toString();
								//	partNumber="SPM9021211624";
								greenBoxDetails.setStrPartNumber(partNumber);
								greenBoxDetails.setStrProdBrand((String) BrandArr.get(0));
								if(JsonUtils.isBundleItem(partNumber))
								{
									prodObj = new ProductDetails();
									prodObj.setBundle(true);

									setFFMMap(partNumber, "DDC");
									TestContext.get().put(Constants.ISBUNDLE, "ISBUNDLE"); // 
									greenBoxDetails.setStrDefaultFfmDisplay("DDC");
									greenBoxDetails.setStrItemSellerName("NA");
								}
								else
								{
									prodObj = Utils.parseProduct(partNumber);
									greenBoxDetails.setStrDefaultFfmDisplay(getFFMDetails(partNumber, prodObj));
									if(prodObj.isGroupSeller())
									{

										List<GroupedSellerDetails> groupedSellerDetailsList=getGroupSellerDetails(prodObj);
										sellerName=retrieveSellerDetailsWithSamePartNumber(groupedSellerDetailsList,partNumber);
										if((sellerName!=null)&& !sellerName.equalsIgnoreCase("NotFound"))//No seller having the same partnumber
											greenBoxDetails.setStrItemSellerName(sellerName);

									}
									else
										greenBoxDetails.setStrItemSellerName("NA");
								}

								listOfProductsArrLst.add(greenBoxDetails);
							}
						}
						else
							PageAssert.fail("doc Array is empty");
					}
					else

						SoftAssert.checkConditionAndContinueOnFailure("Brand filter is not getting for searchurl"+searchurl, filterIndex!=null);

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				SoftAssert.checkConditionAndContinueOnFailure("Response is not getting for searchurl"+searchurl, solarResponse!=null);		
		}
		return listOfProductsArrLst;

	}
	*//****
	 * Update ffm map 
	 * @param partNumber
	 * @param ffm
	 *//*
	private static void setFFMMap(String partNumber, String ffm)
	{
		Map<String, String> FFMMap = new HashMap<String, String>();
		FFMMap.put(partNumber, ffm); // Done for matching what is rendered in
		TestContext.get().put(Constants.FFMMAP, FFMMap); // 
	}
	private static String getFFMDetails(String partNumber, ProductDetails prodObj)
	{
		String ffm = prodObj.getFfm();
		ffm = Utils.fetchFFM(ffm);

		System.out.println("Default FFM >>>>>>>> " + ffm);

		//			Map<String, String> FFMMap = new HashMap<String, String>();
		//			FFMMap.put(partNumber, ffm); // Done for matching what is rendered in
		//			getContext().put(Constants.FFMMAP, FFMMap); // 

		return ffm;

	}
	//getBrandIndex
	private static String getBrandIndex(JSONArray filterArr,String filterName)
	{
		int i;
		if(filterArr!=null && filterArr.size()>0 )
		{
			for(i=1;i<=filterArr.size();i++)
			{
				String brandNode=filterArr.get(i).toString();
				if(brandNode.equalsIgnoreCase("Brand"))
				{
					System.out.println(brandNode);
					break;
				}


			}
			return String.valueOf(i);
		}
		else 
			return null;
	}


	public static List<GroupedSellerDetails> getGroupSellerDetails(ProductDetails productDetails ){

		String sellerNameGv="";
		String prodNumber="";
		String groupIdGb="";
		//long sellerCountGb=0;
		List<GroupedSellerDetails> groupedSellerDetailsList=null;
		//	if(productDetails.getGreenBoxDetails().isVariant()){


		//	}else{		
		//sellerCountGb=productDetails.getGreenBoxDetails().getIntSellerCount();
		groupIdGb=productDetails.getGreenBoxDetails().getStrGroupId(); //UID
		//	}

		//if(sellerCountGb>1){
		groupedSellerDetailsList= JsonUtils.mpGroupSellerDetailsParser(groupIdGb);
		//GroupedSellerDetails groupedSellerDetails=null;
		//	}

		return groupedSellerDetailsList;
	}
	public static String retrieveSellerDetailsWithSamePartNumber(List<GroupedSellerDetails> groupedSellerDetailsList,String partNumber)
	{
		GroupedSellerDetails groupedSellerDetails=null;
		String sellerName="",prodNumber="";
		boolean sellerFound=false;
		int sellerCountGb=0;
		if(null != groupedSellerDetailsList && !groupedSellerDetailsList.isEmpty()){
			sellerCountGb=groupedSellerDetailsList.size();
			Iterator<GroupedSellerDetails> iter = groupedSellerDetailsList.iterator();

			while (iter.hasNext()) {
				groupedSellerDetails = iter.next();
				if (groupedSellerDetails.getStrPartNumber().equalsIgnoreCase(partNumber))
				{

					sellerName=groupedSellerDetails.getStrItemSellerName();
					prodNumber=groupedSellerDetails.getStrPartNumber();
					sellerFound=true;
					break;
				}
			}


		}
		if(sellerFound)
			return sellerName;
		else
			return "NotFound";
	}

	public static ProductDetails populateAutomotiveDetailsForFBMItems(String partNumber) {


		String offerApiUrl=apiUrls.offerApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
		ProductDetails productDetails = new ProductDetails();
		GreenBoxDetails greenBoxDetails=new GreenBoxDetails();
		try{	
			JSONParser parser = new JSONParser();
			String response=getResponse(offerApiUrl);
			Object obj = parser.parse(response);
			JSONArray jsonArray = (JSONArray) obj;
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			JSONObject jsonObjectMaster = (JSONObject) jsonObject.get("_blob");
			//JSONObject jsonObjectSearch=(JSONObject) jsonObject.get("_search");
			jsonObjectMaster = (JSONObject) jsonObjectMaster.get("offer");
			JSONObject jsonClassification = (JSONObject) jsonObjectMaster.get("classifications");
			AutomotiveDetails automotiveDetails = new AutomotiveDetails();
			if(jsonClassification.get("isFitmentRequired")!=null){	
				String isFitmentrequired=	jsonClassification.get("isFitmentRequired").toString();

				if(isFitmentrequired.equals("true"))
				{

					automotiveDetails.setFitmentRequired(true);
					automotiveDetails.setStrBrandCodeId((String) jsonObjectMaster.get("brandCodeId"));
					automotiveDetails.setStrModelNo((String) jsonObjectMaster.get("modelNo"));
					automotiveDetails.setBaseFitmentDetails(fetchBaseFitmentDetailsForFBM(partNumber,automotiveDetails.getStrBrandCodeId() ,automotiveDetails.getStrModelNo()));


				}
			}
			productDetails.setAutomotiveDetails(automotiveDetails);




		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"script exception", e);
		}


		return productDetails;
	}


	public static BaseFitmentDetails fetchBaseFitmentDetailsForFBM(String partNumber, String brandCodeId, String modelNo){
		BaseFitmentDetails baseFitmentDetails=new BaseFitmentDetails();
		try {
			//API Call to get Base Vehicle Id
			String url=apiUrls.baseVehDetails.replace("PLACEHOLDERFORAUTOBRANDS", brandCodeId).replace("PLACEHOLDERFORAUTOPARTS", modelNo);
			String response=getResponse(url);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("data");
			JSONArray jsonArray = (JSONArray) jsonObject.get("fitment");
			String fitmentString = (String) jsonArray.get(0);
			String [] fitments=fitmentString.split(";");
			//vehicleId = vehicleId.substring(0, vehicleId.indexOf(";"));
			baseFitmentDetails.setStrBaseVehicleId(fitments[0]);
			baseFitmentDetails.setBed(fitments[1].replace("be_", ""));
					baseFitmentDetails.setBodyStyle(fitments[2].replace("bo_", ""));
					baseFitmentDetails.setPosition(fitments[7].replace("po_", ""));

			//API Call to get fitment details
			url=apiUrls.yearmakemodel.replace("PLACEHOLDERFORAUTOVEHID", fitments[0]);
			response=getResponse(url);
			obj = parser.parse(response);
			jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("data");
			baseFitmentDetails.setStrYear(((Long) jsonObject.get("year")).toString());
			baseFitmentDetails.setStrMakeId(((Long) jsonObject.get("makeId")).toString());
			baseFitmentDetails.setStrMakeName((String) jsonObject.get("makeName"));
			baseFitmentDetails.setStrModelId(((Long) jsonObject.get("modelId")).toString());
			baseFitmentDetails.setStrModelName((String) jsonObject.get("modelName"));

			url=apiUrls.vehicleIdUrl.replace("PLACEHOLFERFORYEAR",((Long) jsonObject.get("year")).toString()).replace("PLACEHOLDERFORMAKEID", ((Long) jsonObject.get("makeId")).toString());
			response=getResponse(url);
			obj=parser.parse(response);
			jsonObject=(JSONObject)obj;
			jsonArray=(JSONArray)jsonObject.get("data");
			if(!(jsonArray.isEmpty()))
			{
				for(int i=0;i<jsonArray.size();i++)
				{
					JSONObject baseVehicleIdArray=(JSONObject)jsonArray.get(i);
					String baseVehicleId=(String.valueOf( baseVehicleIdArray.get("baseVehicleId")));
					if(baseVehicleId.equals(baseFitmentDetails.getStrBaseVehicleId()))
					{
						String vehicleId= (String.valueOf(baseVehicleIdArray.get("vehicleId")));
						String subModelName=String.valueOf(baseVehicleIdArray.get("subModelName"));
						url=apiUrls.subModelUrl.replace("PLACEHOLDERFORBRAND", brandCodeId).replace("PLACEHOLDERFORMODEL", modelNo).replace("PLACEHOLDERFORVEHICLE", vehicleId);

						response=getResponse(url);

						obj=parser.parse(response);
						jsonObject=(JSONObject)obj;

						jsonObject=(JSONObject) jsonObject.get("data");
						String jsonObjMatches =(String) jsonObject.get("matches");

						if(jsonObjMatches.equalsIgnoreCase("Maybe") || jsonObjMatches.equalsIgnoreCase("Yes"))
						{
							JSONObject jsonBedConfig=(JSONObject) jsonObject.get("vehicleOptions");
							JSONArray jsonBedConfigArray=(JSONArray) jsonBedConfig.get("beds");
							if(!(jsonBedConfigArray==null))
							{
								for(int j=0;j<jsonBedConfigArray.size();j++)
								{
									JSONObject jsonBedConfigId=(JSONObject)jsonBedConfigArray.get(j);
									String bedConfigId=(String.valueOf(jsonBedConfigId.get("bedConfigId")));
									String bedLength=String.valueOf(jsonBedConfigId.get("bedLength"));
									String bedTypeName=String.valueOf(jsonBedConfigId.get("bedTypeName"));
									url=apiUrls.bedConfigUrl.replace("PLACEHOLDERFORBRAND", brandCodeId).replace("PLACEHOLDERFORMODEL", modelNo).replace("PLACEHOLDERFORVEHICLE", vehicleId).replace("PLACEHOLDERFORBEDCONFIG", bedConfigId);
									response=getResponse(url);

									obj=parser.parse(response);
									jsonObject=(JSONObject)obj;

									jsonObject=(JSONObject) jsonObject.get("data");
									String jsonObjBedConfigMatches=(String.valueOf(jsonObject.get("matches")));


									if(jsonObjBedConfigMatches.equalsIgnoreCase("Yes"))
									{

										baseFitmentDetails.setBed(bedConfigId);
										baseFitmentDetails.setbedLength(bedLength);
										baseFitmentDetails.setbedTypeName(bedTypeName);
										break;
									}
									else
									{
										continue;
									}


								}
							}
						}
						else
						{
							continue;
						}
						baseFitmentDetails.setStrVehicleId(vehicleId);

						baseFitmentDetails.setStrBaseVehicleId(baseVehicleId);

						baseFitmentDetails.setStrSubModelName(subModelName);

						break;
					}
					else
					{
						continue;
					}

				}

			}





		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE,"script exception", e);
			//e.printStackTrace();
		}

		return baseFitmentDetails;
	}

	public static Iterator<Object[]> ARSResponseParse(List <String> response)
	{
		List<Object[]> returnListObj=new ArrayList();
		boolean deal=false;
		boolean seeTodayFlag=false;
		String seeToday=System.getenv("See_Today_Special");
		if(null!=seeToday){
			seeTodayFlag=Boolean.parseBoolean(seeToday);
		}
		try{

			String pdplink="";
			int tmp=0;

			for(int i=0;i<response.size();i++){

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(response.get(i));
				JSONObject rootJsonObject = (JSONObject) obj;
				JSONArray productJsonArray = (JSONArray) rootJsonObject.get("products");
				int count=1;


				for(Object product : productJsonArray){

					ARSFeedsDetails arsFeedObj =new ARSFeedsDetails();
					JSONObject productObj = (JSONObject) product;
					boolean instock=true;
					boolean webstatus=true;
					boolean savingsstatus=true;
					boolean department=true;
					if(null!=productObj.get("InStock"))
					{
						if(productObj.get("InStock").toString().equalsIgnoreCase("0"))
							instock=false;
					}
					if(null!=productObj.get("WebStatus"))
					{
						if(productObj.get("WebStatus").toString().equalsIgnoreCase("0")||productObj.get("WebStatus").toString().equalsIgnoreCase(""))
							webstatus=false;
					}	

					if( null!= productObj.get("Savings"))
					{
						if(productObj.get("Savings").toString().equalsIgnoreCase(""))
							savingsstatus=false;
					}

					if(null!= productObj.get("Department"))
					{
						if(productObj.get("Department").toString().equalsIgnoreCase(""))
							department=false;
					}else
					{
						department=false;
					}

					if(null!= seeToday &&seeTodayFlag&&FrameworkProperties.TEST_PROJECT_VALUE.equalsIgnoreCase("KMART")){
						deal=seeTodayDealsParse(productObj,seeTodayFlag);					
						if(productObj.get("PID").toString().equalsIgnoreCase("") || !webstatus|| !instock ||!deal){
							continue;

						}
						else{
							arsFeedObj.setPID(productObj.get("PID").toString().trim());
						}

					}
					else{

						if(productObj.get("PID").toString().equalsIgnoreCase("") || !webstatus|| !instock ||!savingsstatus||!department)
							continue;
						else
							arsFeedObj.setPID(productObj.get("PID").toString().trim());
					}

					if(null!=productObj.get("Link"))
						pdplink=productObj.get("Link").toString().trim();

					if(!pdplink.contains(arsFeedObj.getPID()))
					{
						System.out.println("..............");
						System.out.println("PID:- "+ arsFeedObj.getPID());
						System.out.println("Url:- "+pdplink);
						System.out.println("..............");
						tmp++;
						continue;

					}


					if(null!=productObj.get("SalePrice"))
						if(productObj.get("SalePrice").toString().contains("%"))
							continue;
						else	
							arsFeedObj.setSalePrice(productObj.get("SalePrice").toString().replace("$", "").replaceAll("each", "").trim());

					if(null!=productObj.get("MemberPrice"))
					{
						if(productObj.get("MemberPrice").toString().contains("%"))
						{
							continue;

						}
						else
						{
							arsFeedObj.setMemberPrice(productObj.get("MemberPrice").toString().replace("$", "").replaceAll("each", "").trim());
						}
					}
					else
					{
						arsFeedObj.setMemberPrice("No Member Price Node");
					}


					if(null!=productObj.get("SoldBy")&&(null==seeToday||!seeTodayFlag))
					{
						arsFeedObj.setSoldBy(productObj.get("SoldBy").toString().trim().toLowerCase());
					}
					else{
						arsFeedObj.setSoldBy("");
					}
					if(null!=productObj.get("BrandName"))
						arsFeedObj.setBrandName(productObj.get("BrandName").toString().trim());

					if(null!=productObj.get("Department")&&(null==seeToday||!seeTodayFlag)){
						arsFeedObj.setDepartment(productObj.get("Department").toString().trim());
					}
					else{
						arsFeedObj.setDepartment("");
					}
					if(null!=productObj.get("DescriptionName"))
						arsFeedObj.setTitle(productObj.get("DescriptionName").toString().trim());

					returnListObj.add(new Object[]{arsFeedObj,count});
					count++;


				}

			}
			System.out.println("pdp url mismatch count :-"+tmp);
			System.out.println(returnListObj.size());


		}

		catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return returnListObj.iterator();
	}

	public static ArrayList<String> ARSResponseParseForWritingDeals(List <String> response)
	{
		List<Object[]> returnListObj=new ArrayList();
		ArrayList<String> partnumList=new ArrayList<String>();
		boolean deal=false;
		boolean seeTodayFlag=false;
		String seeToday=System.getenv("See_Today_Special");
		if(null!=seeToday){
			seeTodayFlag=Boolean.parseBoolean(seeToday);
		}
		try{

			String pdplink="";
			int tmp=0;

			for(int i=0;i<response.size();i++){

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(response.get(i));
				JSONObject rootJsonObject = (JSONObject) obj;
				JSONArray productJsonArray = (JSONArray) rootJsonObject.get("products");
				int count=1;


				for(Object product : productJsonArray){

					ARSFeedsDetails arsFeedObj =new ARSFeedsDetails();
					JSONObject productObj = (JSONObject) product;
					boolean instock=true;
					boolean webstatus=true;
					boolean savingsstatus=true;
					boolean department=true;
					if(null!=productObj.get("InStock"))
					{
						if(productObj.get("InStock").toString().equalsIgnoreCase("0"))
							instock=false;
					}
					if(null!=productObj.get("WebStatus"))
					{
						if(productObj.get("WebStatus").toString().equalsIgnoreCase("0")||productObj.get("WebStatus").toString().equalsIgnoreCase(""))
							webstatus=false;
					}	

					if( null!= productObj.get("Savings"))
					{
						if(productObj.get("Savings").toString().equalsIgnoreCase(""))
							savingsstatus=false;
					}

					if(null!= productObj.get("Department"))
					{
						if(productObj.get("Department").toString().equalsIgnoreCase(""))
							department=false;
					}else
					{
						department=false;
					}

					if(null!= seeToday &&seeTodayFlag&&FrameworkProperties.TEST_PROJECT_VALUE.equalsIgnoreCase("KMART")){
						deal=seeTodayDealsParse(productObj,seeTodayFlag);					
						if(productObj.get("PID").toString().equalsIgnoreCase("") || !webstatus|| !instock ||!deal){
							continue;

						}
						else{
							arsFeedObj.setPID(productObj.get("PID").toString().trim());
						}

					}
					else{

						if(productObj.get("PID").toString().equalsIgnoreCase("") || !webstatus|| !instock ||!savingsstatus||!department)
							continue;
						else
							arsFeedObj.setPID(productObj.get("PID").toString().trim());
					}

					if(null!=productObj.get("Link"))
						pdplink=productObj.get("Link").toString().trim();

					if(!pdplink.contains(arsFeedObj.getPID()))
					{
						System.out.println("..............");
						System.out.println("PID:- "+ arsFeedObj.getPID());
						System.out.println("Url:- "+pdplink);
						System.out.println("..............");
						tmp++;
						continue;

					}


					if(null!=productObj.get("SalePrice"))
						if(productObj.get("SalePrice").toString().contains("%"))
							continue;
						else	
							arsFeedObj.setSalePrice(productObj.get("SalePrice").toString().replace("$", "").replaceAll("each", "").trim());

					if(null!=productObj.get("MemberPrice"))
					{
						if(productObj.get("MemberPrice").toString().contains("%"))
						{
							continue;

						}
						else
						{
							arsFeedObj.setMemberPrice(productObj.get("MemberPrice").toString().replace("$", "").replaceAll("each", "").trim());
						}
					}
					else
					{
						arsFeedObj.setMemberPrice("No Member Price Node");
					}


					if(null!=productObj.get("SoldBy")&&(null==seeToday||!seeTodayFlag))
					{
						arsFeedObj.setSoldBy(productObj.get("SoldBy").toString().trim().toLowerCase());
					}
					else{
						arsFeedObj.setSoldBy("");
					}
					if(null!=productObj.get("BrandName"))
						arsFeedObj.setBrandName(productObj.get("BrandName").toString().trim());

					if(null!=productObj.get("Department")&&(null==seeToday||!seeTodayFlag)){
						arsFeedObj.setDepartment(productObj.get("Department").toString().trim());
					}
					else{
						arsFeedObj.setDepartment("");
					}
					if(null!=productObj.get("DescriptionName"))
						arsFeedObj.setTitle(productObj.get("DescriptionName").toString().trim());

					returnListObj.add(new Object[]{arsFeedObj,count});
					partnumList.add(arsFeedObj.getPID());
					count++;


				}

			}
			System.out.println("pdp url mismatch count :-"+tmp);
			System.out.println(returnListObj.size());


		}

		catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return partnumList;
	}





	public static boolean seeTodayDealsParse(JSONObject productObj, boolean flag) {
		// TODO Auto-generated method stub
		boolean status=false;

		String timedDealsStart="";
		String timedDealsEnd="";
		if(flag){

			if(null!=productObj.get("TimedDealsStart")){
				timedDealsStart=(String)productObj.get("TimedDealsStart");
			}

			if(null!=productObj.get("TimedDealsEnd")){
				timedDealsEnd=(String)productObj.get("TimedDealsEnd");
			}


			try{
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				Date datemin=dateFormat.parse(timedDealsStart);
				Date datemax=dateFormat.parse(timedDealsEnd);
				Calendar cal = Calendar.getInstance();
				String dateToday=dateFormat.format(cal.getTime());
				Date datenow=dateFormat.parse(dateToday);

				status=datenow.after(datemin)&&(datenow.before(datemax));
			}



			catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;

	}



	public Iterator<Object[]> memberExclusiveParse(String response,String explicitBonusvalue)
	{
		List<Object[]> returnListObj=new ArrayList<Object[]>();
		try{



			JSONParser parser = new JSONParser();
			Object obj = parser.parse(response);
			JSONObject rootJsonObject = (JSONObject) obj;
			JSONArray productJsonArray = (JSONArray) rootJsonObject.get("products");
			int count=1;

			for(Object product : productJsonArray){

				MemberFeedDetails memberFeedDetails=new MemberFeedDetails();
				JSONObject productObj = (JSONObject) product;


				boolean instock=true;
				boolean webstatus=true;
				boolean department=true;

				if(null!=productObj.get("InStock"))
				{
					if(productObj.get("InStock").toString().equalsIgnoreCase("0"))
						instock=false;
				}
				if(null!=productObj.get("WebStatus"))
				{
					if(productObj.get("WebStatus").toString().equalsIgnoreCase("0")||productObj.get("WebStatus").toString().equalsIgnoreCase(""))
						webstatus=false;
				}	


				if(null!= productObj.get("Department"))
				{
					if(productObj.get("Department").toString().equalsIgnoreCase(""))
						department=false;
				}else
				{
					if(null!=productObj.get("Category") && !productObj.get("Category").toString().equalsIgnoreCase(""))
						department=true;
					else
						department=false;
				}



				if(productObj.get("PID").toString().equalsIgnoreCase("") || !webstatus|| !instock ||!department)
					continue;

				else{
					if(!productObj.get("PID").toString().equalsIgnoreCase(""))
					{
						memberFeedDetails.setPID(productObj.get("PID").toString().trim());

						if(null!=productObj.get("Department"))
							memberFeedDetails.setDepartment(productObj.get("Department").toString());
						else
						{//for kmart we have no department node for member exclusive deals. So we take category for Kmart
							if(null!=productObj.get("Category"))
								memberFeedDetails.setDepartment(productObj.get("Category").toString());
						}
						if(null!=productObj.get("BrandName"))
							memberFeedDetails.setBrandName(productObj.get("BrandName").toString());
						if(null!=productObj.get("DescriptionName"))
							memberFeedDetails.setTitle(productObj.get("DescriptionName").toString());

						if("NA".equalsIgnoreCase(explicitBonusvalue))
						{
							if(null!=productObj.get("BonusMemberMessaging"))
							{
								memberFeedDetails.setBonusMemberMessaging(productObj.get("BonusMemberMessaging").toString());
								String[] test=productObj.get("BonusMemberMessaging").toString().split(" ");
								String bonusValue="";
								for(String tmp : test){
									if(tmp.matches("\\$?+\\d+%?")){
										bonusValue=tmp;
										break;
									}

								}
								bonusValue=	bonusValue.replaceAll("\\$", "").replace("%", "").trim();

								//No bonus value. <There will be message only. Now integer values or discounts>
								if(bonusValue.equalsIgnoreCase(""))
									continue;
								memberFeedDetails.setReward(bonusValue);
							}
						}
						else
						{
							memberFeedDetails.setBonusMemberMessaging("Explicit Bonus value"+explicitBonusvalue);
							memberFeedDetails.setReward(explicitBonusvalue);
						}

						returnListObj.add(new Object[]{memberFeedDetails,count});
						count++;
					}
				}
			}
			System.out.println(returnListObj.size());
		}


		catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return returnListObj.iterator();
	}




	public static List<String> parentOfferParse(ARSFeedsDetails ARSFeedObject)
	{
		List<String> offerIdList=new ArrayList();
		String offerIdApi="";
		JSONParser parser = new JSONParser();
		try{

			offerIdApi=apiUrls.offerParentAPiUrl.replace("PLACEHOLDERFORPARTNUMBER", ARSFeedObject.getPID());
			String offerResponse=JsonUtils.getResponse(offerIdApi);
			int size=0;
			JSONArray jsonOfferArray = (JSONArray) parser.parse(offerResponse);
			size=jsonOfferArray.size();
			if(!(size>1)){
				String offerId=(String) jsonOfferArray.get(0);
				offerIdList.add(offerId);

			}
			else{
				for(int i=0;i<jsonOfferArray.size();i++){
					String offerId=(String) jsonOfferArray.get(i);
					offerIdList.add(offerId);

				}
			}

			//Setting Offer Ids in ARSFeedObject
			ARSFeedObject.setOfferList(offerIdList);
		}

		catch(Exception e){

			System.out.println(offerIdApi);
					  System.out.println(ARSFeedObject.getPID() +":- Expection is calling Parent Offer API< Hardline or Softline check >");
			System.out.println(ARSFeedObject.getPID() +":- Calling Parent SSIN");
			try{
				offerIdApi=apiUrls.contentSSIN.replace("PLACEHOLDERFORPARTNUMBER", ARSFeedObject.getPID());
				String offerResponse=JsonUtils.getResponse(offerIdApi);
				int size=0;
				JSONArray jsonOfferArray = (JSONArray) parser.parse(offerResponse);
				size=jsonOfferArray.size();
				if(!(size>1)){
					String offerId=(String) jsonOfferArray.get(0);
					offerIdList.add(offerId);

				}
				else{
					for(int i=0;i<jsonOfferArray.size();i++){
						String offerId=(String) jsonOfferArray.get(i);
						offerIdList.add(offerId);
					}
				}

				//Setting Offer Ids in ARSFeedObject
				ARSFeedObject.setOfferList(offerIdList);
			}
			catch(Exception exception)
			{
				System.out.println(offerIdApi);
				System.out.println(ARSFeedObject.getPID() +":- Expection is calling Parent Offer API< Hardline or Softline check >");
				System.out.println("Calling Parent SSIN");
				return null;
			}
			return offerIdList ;
		}
		return  offerIdList;
	}

	public static Map<String,Object> gbOfferParse(String offerId,ARSFeedsDetails arsFeedsDetails)
	{
		String gbofferApi="";
		Map<String,Object> details=new HashMap<String,Object>();

		try{
			JSONParser parser = new JSONParser();
			gbofferApi=apiUrls.gbofferAPiUrl.replace("PLACEHOLDERFORPARTNUMBER", offerId);
			String gbofferResponse=JsonUtils.getResponse(gbofferApi);
			Object obj = parser.parse(gbofferResponse);  
			JSONArray jsonArray = (JSONArray) obj;
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			JSONObject blobObject = (JSONObject)jsonObject.get("_blob");
			JSONObject offerObject = (JSONObject)blobObject.get("offer");
			JSONObject operationalNode = (JSONObject)offerObject.get("operational");
			JSONObject siteNode =(JSONObject) operationalNode.get("sites");
			JSONObject soldNode=null;
			boolean isDispElig=false;
			if(arsFeedsDetails.getSoldBy().toLowerCase().equalsIgnoreCase(""))
			{
				JSONObject ffmObject = (JSONObject)offerObject.get("ffm");
				//JSONObject soldByObject = (JSONObject)ffmObject.get("soldBy");
				arsFeedsDetails.setSoldBy(ffmObject.get("soldBy").toString());
			}

			if(siteNode.containsKey(arsFeedsDetails.getSoldBy().toLowerCase())){
				soldNode = (JSONObject)siteNode.get(arsFeedsDetails.getSoldBy().toLowerCase());
				isDispElig=(Boolean)soldNode.get("isDispElig");
				details.put("isDispElig", isDispElig);
				arsFeedsDetails.setDiplayEligible(isDispElig);
			}
			else
			{
				soldNode = (JSONObject)siteNode.get("sears");
				isDispElig=(Boolean)soldNode.get("isDispElig");
				details.put("isDispElig", isDispElig);
				arsFeedsDetails.setDiplayEligible(isDispElig);
			}

			JSONObject identityNode =(JSONObject) offerObject.get("identity");
			String uid=(String)identityNode.get("uid");
			details.put("uid", uid);
			arsFeedsDetails.setUid(uid);
		}
		catch(Exception e){
			System.out.println(gbofferApi);
			System.out.println(arsFeedsDetails.getPID()+ "Expection in offer SKU API call using offer id"+offerId );
			return null;
		}

		return  details;
	}
	public static boolean parseUAS(String offerID,ARSFeedsDetails arsFeedsDetails)
	{
		String uasAPiUrl="";
		boolean isShipAvail=false;
		boolean isPickupAvail=false;
		boolean isDeliveryAvail=false;

		Map<String,Boolean> spuAvailabilityMap=new HashMap<String,Boolean>();
		Map<String,Boolean> shipAvailabilityMap=new HashMap<String,Boolean>();
		Map<String,Boolean> deliveryAvailabilityMap=new HashMap<String,Boolean>();
		try{
			JSONParser parser = new JSONParser();
			uasAPiUrl=apiUrls.uasAPiUrl;
			String payload="{\"items\":[\""+offerID+"\"]}";
			HttpRequest request=new HttpRequest();
			String uasResponse=request.getPostResponse(uasAPiUrl, payload);
			Object obj = parser.parse(uasResponse); 
			JSONObject jsonObj = (JSONObject) obj;
			JSONObject itemMapObj=(JSONObject) jsonObj.get("itemMap");
			if(itemMapObj.containsKey(offerID)){
				JSONObject prodObj=(JSONObject) itemMapObj.get(offerID);

				isShipAvail=(Boolean)prodObj.get("shipAvail");
				shipAvailabilityMap.put(offerID, isShipAvail);
				arsFeedsDetails.setShipAvailabilityMap(shipAvailabilityMap);

				isPickupAvail=(Boolean)prodObj.get("pickupAvail");
				spuAvailabilityMap.put(offerID, isPickupAvail);
				arsFeedsDetails.setSpuAvailabilityMap(spuAvailabilityMap);

				isDeliveryAvail=(Boolean)prodObj.get("deliveryAvail");
				deliveryAvailabilityMap.put(offerID, isDeliveryAvail);
				arsFeedsDetails.setDeliveryAvailabilityMap(deliveryAvailabilityMap);

			}
			if(isShipAvail||isPickupAvail||isDeliveryAvail){
				return true;
			}
		}
		catch(Exception e){
			System.out.println(arsFeedsDetails.getPID() +":- Exception in UAS V3 call for offer id :-  "+ offerID +" -- UAS url --"+ uasAPiUrl);
			return false;
		}
		return false;
	}

	public static MemberFeedDetails parseMemberExclusiveDealsforBonus(String feedResponse, MemberFeedDetails details)
	{
		com.shc.automation.Logger.log("Proceeding to compare Bonus member messaging after ATC", TestStepType.STEP);

		try{

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(feedResponse);
			JSONObject rootJsonObject = (JSONObject) obj;
			JSONObject responseObject = (JSONObject) rootJsonObject.get("response");
			JSONObject sywrObject = (JSONObject) responseObject.get("sywr");
			JSONArray itemArray = (JSONArray) sywrObject.get("items");
			JSONObject jsonObjectItemArray = (JSONObject) itemArray.get(0);
			JSONArray itmOffersArray = (JSONArray) jsonObjectItemArray.get("itmOffers");
			List<String> offerMessageArray=new ArrayList<String>();
			String name="";
			String offerName="";
			for (int i = 0; i < itmOffersArray.size(); i++) {
				JSONObject jsonObjectItem = (JSONObject) itmOffersArray.get(i);
				if(jsonObjectItem.get("name")!=null){
					name=(String)jsonObjectItem.get("name");
					offerMessageArray.add(name);
				}
			}
			details.setItemOffers(offerMessageArray);

			for (int d = 0; d < itmOffersArray.size(); d++) {
				JSONObject jsonItem = (JSONObject) itmOffersArray.get(d);

				if(jsonItem.get("name")!=null){
					offerName=(String)jsonItem.get("name");
					if(offerName.contains(details.getReward()))
					{
						details.setStatus(true);
						break;
					}
				}
			}

		}
		catch(Exception e){
			System.out.println(details.getPID()+" Expection while parsing through bonus details");
			return details;
		}

		return details;
	}

	public static String parseRankingApi(ARSFeedsDetails ARSFeedobj)
	{
		String seller="",sellerObj="";	
		String rankingAPiUrl="";
		try{
			JSONParser parser = new JSONParser();
			rankingAPiUrl=apiUrls.rankingAPiUrl.replace("PLACEHOLDERFORUID", ARSFeedobj.getUid());

			String offerResponse=JsonUtils.getResponse(rankingAPiUrl);
			Object obj = parser.parse(offerResponse); 
			JSONObject jsonObj = (JSONObject) obj;
			JSONArray groupObj=(JSONArray) jsonObj.get("groups");
			JSONObject jsonObject0 = (JSONObject) groupObj.get(0);
			JSONArray offerObj=(JSONArray) jsonObject0.get("offers");
			JSONObject jsonObjectOffer = (JSONObject) offerObj.get(0);

			if(ARSFeedobj.getSoldBy().equalsIgnoreCase("Sears")||ARSFeedobj.getSoldBy().equalsIgnoreCase("Kmart")){
				sellerObj=(String) jsonObjectOffer.get("sellerId");
			}
			else{
				sellerObj=(String) jsonObjectOffer.get("sellerName");
			}
			seller=sellerObj.toString();
		}
		catch(Exception e){
			System.out.println("PID :- "+ ARSFeedobj.getPID()+" : Display Eligible :- "+ARSFeedobj.isDiplayEligible() +"  APi Url:-  " +rankingAPiUrl);

			return null;
		}
		return seller;

	}
	public static ARSFeedsDetails parseJsonPriceDetails(ARSFeedsDetails ARSFeedsDetailsobj) {

		JSONParser parser = new JSONParser();

		String url=APIUrls.fetchAPIUrls().normalItemPricing;


		if(ARSFeedsDetailsobj.getSoldBy().equalsIgnoreCase("Kmart"))
			url=url.replaceAll("PLACEHOLDERFORSTOREID", "10151");	
		else
			url=url.replaceAll("PLACEHOLDERFORSTOREID", "10153");

		if(ARSFeedsDetailsobj.isVariant())
		{
			url=url.replaceAll("PLACEHOLDERFORPIDTYPE", "3");
			url=url.replace("PLACEHOLDERFORPARTNUMBER", ARSFeedsDetailsobj.getPID());
		}
		else 
		{
			//in case of hard line item>> Pass offer id.
			url=url.replaceAll("PLACEHOLDERFORPIDTYPE", "0");
			url=url.replace("PLACEHOLDERFORPARTNUMBER", ARSFeedsDetailsobj.getOfferList().get(0));
		}

		try {
			String apiResponse=getResponse(url);
			Object obj = parser.parse(apiResponse);


			JSONObject jsonObject=(JSONObject)obj;
			JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");
			JSONArray responses = (JSONArray) priceDisplay.get("response");
			JSONObject response = (JSONObject) responses.get(0);
			JSONObject prices = (JSONObject) response.get("prices");
			JSONObject finalPrice = (JSONObject) prices.get("finalPrice");  //final price that is displayed

			DecimalFormat df = new DecimalFormat("0.00");					
			Double salePrice=Double.parseDouble(finalPrice.get("max").toString());
			String price=df.format(salePrice);

			ARSFeedsDetailsobj.setSalePricePricingAPI(price);

			//if we have a member price from feed we will fetch member price value from Pricing API

			if(!ARSFeedsDetailsobj.getMemberPrice().equalsIgnoreCase("")){
				JSONObject sywPrice = (JSONObject) response.get("syw");
				ARSFeedsDetailsobj.setMemberPricePricingAPI(sywPrice.get("numeric").toString());
			}
		}
		catch(Exception e ){
			System.out.println(url);
			System.out.println(ARSFeedsDetailsobj.getPID()+" Expection in calling Pricing Service");	
			return ARSFeedsDetailsobj;	
		}
		return ARSFeedsDetailsobj;
	}

	*//**
	 * Need to Pass parent part number to this API
	 * @param objArsFeedsDetails
	 * @return
	 *//*
	public static ARSFeedsDetails parseContentOfferAPI(ARSFeedsDetails objArsFeedsDetails){
		String pdpServiceApi="";
		try 
		{
			JSONParser parser = new JSONParser();
			pdpServiceApi=apiUrls.contentApiUrl.replace("PLACEHOLDERFORPARTNUMBER", objArsFeedsDetails.getPID());
			String response=getResponse(pdpServiceApi);
			Object obj = parser.parse(response);  
			JSONObject itemNode=null;
			JSONArray jsonArray = (JSONArray) obj;
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			JSONObject jsonWorker = (JSONObject)jsonObject.get("_blob");
			List<String> offerId =new ArrayList<String>();

			if(objArsFeedsDetails.getPID().contains("B"))
			{
				itemNode = (JSONObject)jsonWorker.get("bundle");
				JSONArray bundleGroup = (JSONArray)itemNode.get("bundleGroup");

				for (Object object : bundleGroup)
				{
					JSONObject bundleObject=(JSONObject)object;
					if(bundleObject.get("type").toString().equalsIgnoreCase("required")){
						JSONArray productsNode = (JSONArray)bundleObject.get("products");
						JSONObject offerIDOBj=(JSONObject)productsNode.get(0);
						String requirePartNumber=offerIDOBj.get("offerId").toString();
						offerId.add(requirePartNumber);


					}	
				}

				objArsFeedsDetails.setOfferList(offerId);
			}
			else if(objArsFeedsDetails.getPID().contains("P"))
				itemNode = (JSONObject)jsonWorker.get("content");


			JSONObject operationalNode = (JSONObject)itemNode.get("operational");
			JSONObject sitesNode = (JSONObject)operationalNode.get("sites");

			//This also a hack when feed give improper seller name .In case we have no seller details from feed.
			if(objArsFeedsDetails.getSoldBy().toLowerCase().equalsIgnoreCase(""))
			{
				try
				{
					JSONObject ffmObject = (JSONObject)itemNode.get("ffm");
					objArsFeedsDetails.setSoldBy(ffmObject.get("soldBy").toString());
				}
				catch(Exception e){
					//for softline items we are not getting ffm node in offer parent api.
					//for bundle it works fine
					objArsFeedsDetails.setSoldBy(System.getenv("Site"));
				}
			}

			if(sitesNode.containsKey(objArsFeedsDetails.getSoldBy().toLowerCase())){
				JSONObject soldNode = (JSONObject)sitesNode.get(objArsFeedsDetails.getSoldBy().toLowerCase());
				boolean isDispElig=(Boolean)soldNode.get("isDispElig");
				objArsFeedsDetails.setDiplayEligible(isDispElig);
			}
			else
			{
				JSONObject soldNode = (JSONObject)sitesNode.get("sears");
				boolean isDispElig=(Boolean)soldNode.get("isDispElig");
				objArsFeedsDetails.setDiplayEligible(isDispElig);
			}

		}
		catch(Exception e){
			System.out.println(pdpServiceApi);
			System.out.println(objArsFeedsDetails.getPID()+ ":- Exception in Content API call");
			return objArsFeedsDetails;
		}
		return objArsFeedsDetails;
	}


	public static ARSFeedsDetails parseBundleMapPriceDetails(ARSFeedsDetails arsFeedObject) {
		String pricingUrl="";
		try 
		{
			pricingUrl = apiUrls.bundlePricing.replace("PLACEHOLDERFORPARTNUMBER", arsFeedObject.getPID()).trim();
			JSONParser parser = new JSONParser();
			String apiResponse=getResponse(pricingUrl);
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject dataNode = (JSONObject) jsonObject.get("data");

			if(null!=(JSONObject) dataNode.get("displayPrice"))
			{
				JSONObject displayPrice = (JSONObject) dataNode.get("displayPrice");
				arsFeedObject.setSalePricePricingAPI(displayPrice.get("numericValue").toString());
			}
			else
			{
				arsFeedObject.setSalePricePricingAPI("0");
				System.out.println(arsFeedObject.getPID() +":- No sale price node found from API response ");
			}

			if(null!=(JSONObject) dataNode.get("memberPrice"))
			{
				JSONObject memberPrice = (JSONObject) dataNode.get("memberPrice");
				arsFeedObject.setMemberPricePricingAPI(memberPrice.get("numericValue").toString());
			}
			else
			{
				arsFeedObject.setMemberPricePricingAPI("0");
				System.out.println(arsFeedObject.getPID() +":- No member price node found from API response ");
			}


		}
		catch(Exception e)
		{
			System.out.println(pricingUrl);
			System.err.println(arsFeedObject.getPID()+"  :- Exception in calling Bundle Pricing service");
			return arsFeedObject;
		}

		return arsFeedObject;
	}

	public static List <String> parseKmart(String response) {

		response=StringUtils.trimAllWhitespace(response);
		List <String> feeds=new ArrayList<String>();
		try 
		{
			JSONParser parser = new JSONParser();
			//Object obj=(String)response;
			Object obj2 = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj2;
			Set one=jsonObject.entrySet();
			Iterator iterator =one.iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)iterator.next();
				System.out.println(entry.getKey()   + "  " + entry.getValue());
				feeds.add(entry.getValue().toString());
			}

		}
		catch(Exception e)
		{
			System.out.println("Exception while getting feed values for kmart "+e.getMessage());
		}
		return feeds;


	}

	public Iterator<Object[]> parseSolr(String url,String bonus) {
		List<Object[]> returnListObj=new ArrayList<Object[]>();
		int count=0;
		try 
		{
			JSONParser parser = new JSONParser();
			String response=JsonUtils.getResponse(url);
			String partNumber="";
			JSONObject obj = (JSONObject) parser.parse(response);
			JSONArray productArray=(JSONArray) obj.get("products");
			for (int i = 0; i < productArray.size(); i++) {
				JSONObject product = (JSONObject) productArray.get(i);
				partNumber = product.get("partNumber").toString();
				MemberFeedDetails details=new MemberFeedDetails();
				details.setPID(partNumber);		
				details.setReward(bonus);
				details.setDepartment("NA");
				details.setBrandName("NA");
				details.setTitle("NA");
				details.setBonusMemberMessaging("NA");
				returnListObj.add(new Object[]{details,count});
				count++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnListObj.iterator();

	}

	*//**Method to fetch seller id and seller name from seller storefront response by providing the alphanumeric seller name(Vanity name).
	 * @param searchText
	 * @return
	 *//*
	public static String fetchSellerNameFromSellerStorefront(String vanityName) {
		String sellerNameUrl=apiUrls.sellerName.replace("PLACEHOLDERFORSELLERNAME", vanityName);
		String jsonSellerName =null;
		try{	
			JSONParser parser = new JSONParser();
			String response=getResponse(sellerNameUrl);
			Object obj = parser.parse(response);
			JSONArray jsonArraySellerId = (JSONArray) obj;
			String sellerId=(String)jsonArraySellerId.get(0);
			String sellerIdUrl=apiUrls.sellerId.replace("PLACEHOLDERFORSELLERID", sellerId);
			response=getResponse(sellerIdUrl);
			obj = parser.parse(response);
			JSONArray jsonArray = (JSONArray) obj;
			JSONObject jsonObject=(JSONObject) jsonArray.get(0);
			JSONObject jsonObjBlob=(JSONObject)jsonObject.get("_blob");
			JSONObject jsonObjSeller=(JSONObject) jsonObjBlob.get("seller");
			jsonSellerName=(String)jsonObjSeller.get("name");
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonSellerName;
	}


	public static String fetchAvailableZipCodeBundle(ProductDetails prodObj,String ffm){
		ArrayList<String> bundleRequiredParNumberList=null;

		bundleRequiredParNumberList=prodObj.getBundleRequiredPartNumber();

		String availablezipcode="";
		int succes_count=0;
		int index=0;
		int nextStartIndex=0;
		int partNumberArrayIndex=0;
		//int previousStartIndex=0;

		// contains all required part numbers 

		if(bundleRequiredParNumberList.size()>0)
		{


			for(partNumberArrayIndex=0;partNumberArrayIndex<bundleRequiredParNumberList.size();){

				int oosForAllzipcode=0;

				for (index=nextStartIndex;index<zipCodeArray.length;)
				{

					if(callUASV2API(bundleRequiredParNumberList.get(partNumberArrayIndex),ffm,zipCodeArray[index]))
					{
						succes_count++;
						nextStartIndex=index;
						partNumberArrayIndex++;
						break;
					}
					else
					{
						System.out.println("Tried for Zip Code :- "+zipCodeArray[index] );

						succes_count=0;
						index++;

						if( index < zipCodeArray.length)
						{
							nextStartIndex=index;
							partNumberArrayIndex=0;
							break;
						}
						else{
							//
							oosForAllzipcode=zipCodeArray.length;
							break;
						}
					}
				}//end of zipcode for loop

				// if all the bundleRequiredParNumber Available for any of the zip code
				if(succes_count==bundleRequiredParNumberList.size())
				{
					availablezipcode=zipCodeArray[index];
					System.out.println("Available zipcode found"+availablezipcode );

					break;
				}	

				//in case any of the required partnumber is oos for all the zip code then the bundle product is oos.
				if(oosForAllzipcode==zipCodeArray.length)
				{
					System.out.println("One of the required part number is OOS for all zipcode"); 
					availablezipcode="OOS";
					break;
				}

			}  //end of part number list for loop

		}//end of if loop

		else
		{
			System.out.println("Item with no required part number");
			availablezipcode="OOS";
		}

		return availablezipcode;
	}

	public static String fetchAvailableZipCodeBundleNew(ProductDetails prodObj,String ffm){

		String zip="";
		try{

			ArrayList<String> bundleRequiredParNumberList=null;
			List<String> ziplist=null;
			Map <String,List<String>> mapObj=new HashMap<String, List<String>>(); 
			Set <String> storeSet=null;
			String store="";
			String sold=prodObj.getGreenBoxDetails().getStrSoldBy();

			bundleRequiredParNumberList=prodObj.getBundleRequiredPartNumber();	
			int partNumberArrayIndex=0;

			for(partNumberArrayIndex=0;partNumberArrayIndex<bundleRequiredParNumberList.size();partNumberArrayIndex++){
				TestContext.get().put("PartNumber", bundleRequiredParNumberList.get(partNumberArrayIndex));
				ziplist=Utils.getFulfillmentBasedOnAvailabilityforBundles(bundleRequiredParNumberList.get(partNumberArrayIndex), ffm,sold);
				if(ziplist==null||ziplist.isEmpty()){
					break;
				}
				mapObj.put(bundleRequiredParNumberList.get(partNumberArrayIndex), ziplist);
			}
			if(ziplist==null||ziplist.isEmpty()){
				zip="OOS";
			}
			else{
				List<List<String>> storeList = new ArrayList<List<String>>();
				for(String key:mapObj.keySet()){
					storeList.add(mapObj.get(key));
				}

				if(null!=storeList.get(0)&&!storeList.isEmpty()){
					storeSet=new HashSet<String>(storeList.get(0));
				}

				for(int i=0;i<storeList.size();i++){
					if(!(storeList.get(i).isEmpty())){
						storeSet.retainAll(new HashSet<String>(storeList.get(i)));
					}
				}
				if(!storeSet.isEmpty()){
					store=(String) storeSet.toArray()[0];
				}

				if(!ffm.equalsIgnoreCase("SHIP")){
					zip=parseStore(store);
				}
				else{
					zip=store;
				}
			}
		}
		catch(Exception e){
			com.shc.automation.Logger.log("Exception in fetching zipcode:"+e.getMessage(), TestStepType.STEP);	
			zip="OOS";

		}


		return zip;
	}

	public static String parseStore(String store){

		String zip="";
		String url = "http://szip.prod.global.s.com/gbox/gb/s/data/get/store/" + store+ "?clientId=automation";
		try{
			String response = getResponse(url);
			if (null!=response && !response.isEmpty())
			{
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(response);
				JSONArray jsonArraySellerId = (JSONArray) obj;
				JSONObject searchObjj=(JSONObject) jsonArraySellerId.get(0);
				JSONObject blobObjj=(JSONObject) searchObjj.get("_blob");
				JSONObject unitObjj=(JSONObject) blobObjj.get("unit");
				JSONObject strAddrObjj=(JSONObject) unitObjj.get("strAddr");				
				zip = (String) strAddrObjj.get("zipCd");

			}
		}catch(Exception e){
			e.printStackTrace();

		}
		return zip;
	}


	public static boolean callUASV2API(String partNumber,String ffm,String zipCode){
		String urlUASV2=apiUrls.uasV2Url;

		JSONParser parser = new JSONParser();
		String response;
		String payload;
		String availFlag;
		int qtyAvail=0;
		int qtyThrs=0;
		boolean status=false;
		String store=FrameworkProperties.SITE_TO_SEARCH_IN_KEYWORD_DB;

		if(ffm.equalsIgnoreCase("SHIP")){
			payload=UASV2_Payload_SHIP.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORZIPCODE", zipCode)
					.replace("PLACEORDEFORSTORE", store);
		}
		else if(ffm.equalsIgnoreCase("DELIVERY")||ffm.equalsIgnoreCase("DDC")){	
			payload=UASV2_Payload_Delivery.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORZIPCODE", zipCode)
					.replace("PLACEORDEFORSTORE", store);
		}
		else{
			payload=UASV2_Payload_SPU.replace("PLACEHOLDERFORPARTNUMBER", partNumber).replace("PLACEHOLDERFORZIPCODE", zipCode)
					.replace("PLACEORDEFORSTORE", store);
		}
		try
		{
			response= new HttpRequest().getPostResponse(urlUASV2,payload);
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;				
			JSONArray itemArray=(JSONArray)jsonObject.get("items");
			JSONObject itemArrayObj=(JSONObject) itemArray.get(0);
			JSONArray modeArray=(JSONArray)itemArrayObj.get("modes");
			JSONObject modeArrayObj=(JSONObject) modeArray.get(0);
			JSONArray facilityArray=(JSONArray) modeArrayObj.get("facilities");
			if(facilityArray.size()>0)
			{
				JSONObject facilityArrayObj=(JSONObject) facilityArray.get(0);
				if(ffm.equalsIgnoreCase("SHIP")){
					qtyThrs=Integer.parseInt(facilityArrayObj.get("qtyThreshold").toString());
					qtyAvail=Integer.parseInt(facilityArrayObj.get("qtyAvailable").toString());
					if(qtyAvail>qtyThrs){
						status= true;
					}
				}
				else if(ffm.equalsIgnoreCase("DELIVERY")||ffm.equalsIgnoreCase("DDC")){	
					qtyAvail=Integer.parseInt(facilityArrayObj.get("qtyAvailable").toString());
					if(qtyAvail==1){
						status= true;
					}
				}
				else{
					availFlag=(String) facilityArrayObj.get("availFlag");
					if(availFlag.equalsIgnoreCase("Y")){
						status= true;
					}
				}
			}				
			else
			{
				status=false;
			}
		}

		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}


	static String UASV2_Payload_SPU="{\"items\":[{ \"deliveryStore\": \"09300\",  \"facilities\": [" +
			" \"0001570\"], \"itemSeqenceNo\": \"1\",\"mode\": \"PickUp\", \"partnumber\": \"PLACEHOLDERFORPARTNUMBER\", "+
			" \"quantity\": \"1\",\"storeId\": \"PLACEORDEFORSTORE\",\"zip\": \"PLACEHOLDERFORZIPCODE\" }]}";

	static String UASV2_Payload_Delivery="{ \"items\": [{\"deliveryStore\": \"09300\",\"facilities\": [\"\"],"+
			"	\"itemSeqenceNo\": \"1\", \"mode\": \"delivery\",\"partnumber\": \"PLACEHOLDERFORPARTNUMBER\",\"quantity\": \"1\", "+
			" \"storeId\": \"PLACEORDEFORSTORE\",\"zip\": \"PLACEHOLDERFORZIPCODE\"  }]}" ;

	static String UASV2_Payload_SHIP="{\"memberType\": \"Max\",\"items\": [ {\"itemSeqenceNo\": \"1\","+
			" \"partnumber\": \"PLACEHOLDERFORPARTNUMBER\", \"quantity\": \"1\",\"mode\": \"Ship\","+
			"\"zip\": \"PLACEHOLDERFORZIPCODE\",\"storeId\": \"PLACEORDEFORSTORE\"  } ] }";


	static String[] zipCodeArray ={"60601","60169","60142","60148","60172","77002","10001"};


	public static void arsParseContent(String site,ARSFeedsDetails arsFeedsDetails)
	{
		String contentOfferApi="";
		String ssin="";
		String uid="";
		int offerCnt=1;
		Boolean isVariant=false;
		Boolean canDisplay=false;
		Boolean isAvailable=false;
		Boolean isBundle=false;
		String response="";
		List<String> offerId =new ArrayList<String>();

		try{
			JSONParser parser = new JSONParser();
			contentOfferApi=apiUrls.arsContentOffer.replace("PLACEHOLDERFORPARTNUMBER", arsFeedsDetails.getPID()).replace("PLACEHOLDERFORSITE", site);
			if(arsFeedsDetails.getPID().endsWith("B")){
				arsFeedsDetails.setBundle(true);
				contentOfferApi=contentOfferApi.concat("&isBundle=true");
				isBundle=true;
			}
			response=getResponse(contentOfferApi);
			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject dataObject = (JSONObject)jsonObject.get("data");
			JSONObject ProductObj = (JSONObject)dataObject.get("productstatus");

			// SSIN value  
			if(null!=ProductObj.get("ssin")){
				ssin=(String)ProductObj.get("ssin");
				arsFeedsDetails.setSsin(ssin);
			}


			// uid is captured only for hard line items
			if(null!=ProductObj.get("uid")){
				uid=(String)ProductObj.get("uid");
				arsFeedsDetails.setUid(uid);
			}

			if(null!=ProductObj.get("offerCount")){
				offerCnt=Integer.parseInt(ProductObj.get("offerCount").toString());
				arsFeedsDetails.setOfferCount(offerCnt);
			}

			if(null!=ProductObj.get("isVariant")){
				isVariant=(Boolean)ProductObj.get("isVariant");
				arsFeedsDetails.setVariant(isVariant);
			}

			// we will capture display only for normal hard line items and Bundle from content offer API
			// for grp seller item the value capture from here will be overridden later after Ranking API call.
			if(null!=ProductObj.get("canDisplay")){
				canDisplay=(Boolean)ProductObj.get("canDisplay");
				arsFeedsDetails.setDiplayEligible(canDisplay);
			}



			if(offerCnt==1 && !isBundle && !isVariant)//captured only in the case of normal hard line items without group seller
			{
				// For sold by Sears/Kmart/or any fbm items with offercount ==1 <not grp seller items>
				//note applicable for softline
				if(null!=(JSONObject)dataObject.get("offer"))
				{
					JSONObject offObj = (JSONObject)dataObject.get("offer");
					JSONObject ffmObj = (JSONObject)offObj.get("ffm");
					String sellerName=(String)ffmObj.get("soldBy");
					arsFeedsDetails.setSoldByRankingAPI(sellerName.toLowerCase().trim());
				}

				// applicable for only hardline item with single offercount
				if(null!=dataObject.get("offerstatus")){  
					JSONObject offerObj = (JSONObject)dataObject.get("offerstatus");
					if(null!=offerObj.get("isAvailable")){
						isAvailable=(Boolean)offerObj.get("isAvailable");
						arsFeedsDetails.setAvailable(isAvailable);
					}
				}
				// applicable for only hardline item with single offercount
				if(null!=dataObject.get("offerstatus")){  
					JSONObject offerObj = (JSONObject)dataObject.get("offerstatus");
					//offer id kept for pricing call
					if(null!=offerObj.get("offerId"))
						arsFeedsDetails.setOfferId(offerObj.get("offerId").toString());

				}
			}// end of offercnt==1 if

			//Capturing availability logic for variants
			if(isVariant){
				JSONObject attributesObj = (JSONObject)dataObject.get("attributes");
				JSONArray variantsArray=(JSONArray)attributesObj.get("variants");
				int count=0;
				for(Object variant : variantsArray){

					JSONObject variantObj = (JSONObject)variant;
					// exit criteria
					if(Boolean.parseBoolean(variantObj.get("isAvailable").toString()))
						break;
					count++;
				}

				// none of the variant is available
				if(count==variantsArray.size())
					arsFeedsDetails.setAvailable(false);
				else
					arsFeedsDetails.setAvailable(true);
			}//end of isvariant if loop

			if(isBundle){ //for bundle items

				JSONObject itemNode = (JSONObject)dataObject.get("bundle");
				JSONArray bundleGroup = (JSONArray)itemNode.get("bundleGroup");
				for (Object object : bundleGroup)
				{
					JSONObject bundleObject=(JSONObject)object;
					if(bundleObject.get("type").toString().equalsIgnoreCase("required")){
						JSONArray productsNode = (JSONArray)bundleObject.get("products");
						JSONObject offerIDOBj=(JSONObject)productsNode.get(0);
						String requirePartNumber=offerIDOBj.get("offerId").toString();
						offerId.add(requirePartNumber);
					}	
				}
				arsFeedsDetails.setOfferList(offerId);
			}
		}
		catch(Exception e){
			System.out.println("Exception in calling Content offer APi for Part Number :- "+arsFeedsDetails.getPID()  +"---Url---"+contentOfferApi );
		}

	}	
	public static void arsParseTopRanked(String site,ARSFeedsDetails arsFeedsDetails)
	{
		String topRankedApi="";
		String offerId="";
		String sellerName="";		
		Boolean isAvailable=false;
		Boolean canDisplay=false;

		try{
			JSONParser parser = new JSONParser();
			topRankedApi=apiUrls.arsTopRankedApi.replace("PLACEHOLDERFORPARTNUMBER", arsFeedsDetails.getSsin()).replace("PLACEHOLDERFORSITE", site).replace("PLACEHOLDERFORUID", arsFeedsDetails.getUid());
			String response=getResponse(topRankedApi);
			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject dataObject = (JSONObject)jsonObject.get("data");
			JSONObject offerObj = (JSONObject)dataObject.get("offerstatus");



			if(arsFeedsDetails.getOfferCount()>1){		// group seller items

				if(null!=offerObj.get("canDisplay")){		// can display of topranked item
					canDisplay=(Boolean)offerObj.get("canDisplay");
					arsFeedsDetails.setDiplayEligible(canDisplay);
				}

				if(null!=offerObj.get("isAvailable")){  // isAvailbility of hard line group seller item 
					isAvailable=(Boolean)offerObj.get("isAvailable");
					arsFeedsDetails.setAvailable(isAvailable);
				}


				if(null!=dataObject.get("offerranking")){

					JSONObject rankingObj = (JSONObject)dataObject.get("offerranking");
					//only for group seller
					if(null!=rankingObj.get("id")){           //top ranked offer id kept for pricing call
						offerId=(String)rankingObj.get("id");
						arsFeedsDetails.setOfferId(offerId);
					}

					if(null!=rankingObj.get("sellerName") && null!=rankingObj.get("sellerId")){    
						// This node available only for FBM items we will have seller name and seller id. seller name is taken from "seller name" node
						sellerName=(String)rankingObj.get("sellerName");
						arsFeedsDetails.setSoldByRankingAPI(sellerName.toLowerCase().trim());
					}
					else if(null!=rankingObj.get("sellerId")){  // when Kmart / Sears item become top rank , then we don't have seller name node. we only have seller id node
						sellerName=(String)rankingObj.get("sellerId");
						arsFeedsDetails.setSoldByRankingAPI(sellerName.toLowerCase().trim());
					}

				}



			}

		}
		catch(Exception e){
			System.out.println(arsFeedsDetails.getPID() +":- Exception in Top Rank API call " + "---Url---"+ topRankedApi);	

		}

	}	
	public static void arsParsePricingApi(String site,ARSFeedsDetails arsFeedsDetails)
	{
		String url="";

		url=APIUrls.fetchAPIUrls().arsPricingApi;

		try{
			if(site.equalsIgnoreCase("Kmart"))
				url=url.replaceAll("PLACEHOLDERFORSTOREID", "10151");	
			else
				url=url.replaceAll("PLACEHOLDERFORSTOREID", "10153");

			if(arsFeedsDetails.isVariant())
			{
				url=url.replaceAll("PLACEHOLDERFORPIDTYPE", "3");
				url=url.replace("PLACEHOLDERFORPARTNUMBER", arsFeedsDetails.getPID());	
			}
			else 
			{
				//in case of hard line item>> Pass offer id.
				url=url.replaceAll("PLACEHOLDERFORPIDTYPE", "0");
				// replace any trailing P at the end of offer id
				if(arsFeedsDetails.getOfferCount()==1){
					String PID=arsFeedsDetails.getOfferId();
					url=url.replace("PLACEHOLDERFORPARTNUMBER", PID);					
				}
				else
				{
					url=url.replace("PLACEHOLDERFORPARTNUMBER", arsFeedsDetails.getPID().replace('P', ' ').trim());	
				}
			}


			JSONParser parser = new JSONParser();
			String response=getResponse(url);
			Object obj = parser.parse(response);  
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject responseObj=null;
			JSONObject finalPrice=null;

			if(null!=jsonObject.get("priceDisplay")){
				JSONObject priceDisplay = (JSONObject) jsonObject.get("priceDisplay");

				JSONArray responses = (JSONArray) priceDisplay.get("response");
				responseObj = (JSONObject) responses.get(0);
				JSONObject prices = (JSONObject) responseObj.get("prices");
				finalPrice = (JSONObject) prices.get("finalPrice");  //final price that is displayed

				DecimalFormat df = new DecimalFormat("0.00");	
				if(null!=finalPrice.get("max")){
					Double salePrice=Double.parseDouble(finalPrice.get("max").toString());
					String price=df.format(salePrice);

					arsFeedsDetails.setSalePricePricingAPI(price);
				}
			}

			//if we have a member price from feed we will fetch member price value from Pricing API

			if(null!=arsFeedsDetails.getMemberPrice()&&!arsFeedsDetails.getMemberPrice().equalsIgnoreCase("")){
				JSONObject sywPrice=null;
				if(null!= responseObj.get("syw")){
					sywPrice = (JSONObject) responseObj.get("syw");
				}
				if(null!=sywPrice.get("numeric")){
					arsFeedsDetails.setMemberPricePricingAPI(sywPrice.get("numeric").toString());
				}
			}

		}
		catch(Exception e){
			System.out.println(arsFeedsDetails.getPID() +":- Exception in P pricing call " + "---Url---"+ url);	

		}

	}
	public static void arsParsebundlePricingApi(String site,ARSFeedsDetails arsFeedsDetails)
	{

		String siteSold="";
				String store="";
				String payload="";
		if(site.equalsIgnoreCase("Kmart")){
					store="10151";
					siteSold="kmart";
				}
				else
				{
					store="10153";
					siteSold="sears";
				}
		String payloadTemplate="{\"price-request\":{\"store-id\":"+store+",\"zip-code\":\"60601\","
						+ "\"member-type\":\"G\",\"site\":\""+siteSold+"\",\"price-identifier\":[";
				String ad="";
		for(int i=0;i<arsFeedsDetails.getOfferList().size();i++){
				payload="{\"pid\":\""+arsFeedsDetails.getOfferList().get(i)+"\",\"quantity\":1,\"pid-type\":0}";
				payloadTemplate=payloadTemplate+ad+payload;
				ad=",";
				}
				payloadTemplate=payloadTemplate+"]}}";
		String url="";
		url=APIUrls.fetchAPIUrls().arsBundlePricingApi.replace("PLACEHOLDERFORPARTNUMBER", arsFeedsDetails.getPID());
		try{
			JSONParser parser = new JSONParser();
			String response=getResponse(url);
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject dataNode = (JSONObject) jsonObject.get("data");

			if(null!=(JSONObject) dataNode.get("displayPrice"))
			{
				JSONObject displayPrice = (JSONObject) dataNode.get("displayPrice");
				arsFeedsDetails.setSalePricePricingAPI(displayPrice.get("numericValue").toString());
			}
			else
			{
				arsFeedsDetails.setSalePricePricingAPI("0");
				System.out.println(arsFeedsDetails.getPID() +":- No sale price node found from API response ");
			}

			if(null!=(JSONObject) dataNode.get("memberPrice"))
			{
				JSONObject memberPrice = (JSONObject) dataNode.get("memberPrice");
				arsFeedsDetails.setMemberPricePricingAPI(memberPrice.get("numericValue").toString());
			}
			else
			{
				arsFeedsDetails.setMemberPricePricingAPI("0");
				System.out.println(arsFeedsDetails.getPID() +":- No member price node found from API response ");
			}


		}
		catch(Exception e){
			System.out.println(arsFeedsDetails.getPID() +":- Exception in Bundle pricing call " + "---Url---"+ url);	
		}

	}	


	public static String fetchSubCatIdFromOfferAPI(String partnumber) {
		JSONParser parser = new JSONParser();
		String subCatId="";
		String responseOffer=jsonResponsePDPOfferService(partnumber);
		JSONArray jsonOfferArray;
		try {
			jsonOfferArray = (JSONArray) parser.parse(responseOffer);
			JSONObject jsonVarOfferObjectArray = (JSONObject) jsonOfferArray.get(0);
			JSONObject jsonVarOfferBlob =(JSONObject) jsonVarOfferObjectArray.get("_blob");
			JSONObject jsonOffer =(JSONObject) jsonVarOfferBlob.get("offer");

			JSONObject jsonOfferTaxonomy = (JSONObject)jsonOffer.get("taxonomy");
			JSONObject jsonOfferWeb = (JSONObject)jsonOfferTaxonomy.get("web");
			JSONObject jsonOfferSites = (JSONObject)jsonOfferWeb.get("sites");
			JSONObject jsonOfferSears = (JSONObject)jsonOfferSites.get("sears");
			JSONArray jsonOfferHierarchies = (JSONArray)jsonOfferSears.get("hierarchies");
			JSONObject jsonHierarchiesArray = (JSONObject) jsonOfferHierarchies.get(0);
			JSONArray specificHierarchy =(JSONArray) jsonHierarchiesArray.get("specificHierarchy");
			subCatId=((JSONObject)specificHierarchy.get(specificHierarchy.size()-1)).get("id").toString();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		//System.out.println(subCatId);
		return subCatId;

	}		

	*//**
	 * Call the Deals SOLR query
	 * @param solrbox  >> base url for SOLR call
	 * @param feed >> feed number or the feed
	 *//*
	public static void fecthDealsSOLRFeedDetails(String url){
		String feedResponse="";
		feedResponse=JsonUtils.getResponse(url);
		//feedResponse=feedResponse.substring(feedResponse.indexOf('(')+1,feedResponse.lastIndexOf(')'));
		parseDealsSOLRResponse(feedResponse);
	}

	//the map will contains a temp arsEntity object which store ranking details from SOLR query.
	public static Map<String, ARSFeedsDetails> dealsSOLRDetails = new HashMap<String,ARSFeedsDetails>();

	*//**
	 * Parse the Deals page SOLR response
	 * @param response
	 * the ranking data parsed from SOLR query will be stored in a Map of arsEntity object .
	 *//*
	public static void parseDealsSOLRResponse(String response){
		JSONParser parser = new JSONParser();
		Object obj;

		try {
			obj = parser.parse(response);
			JSONObject rootJsonObject = (JSONObject) obj;
			JSONArray productJsonArray = (JSONArray) rootJsonObject.get("deals");

			for(Object product : productJsonArray){

				ARSFeedsDetails arsFeedObj =new ARSFeedsDetails();
				JSONObject productObj = (JSONObject) product;


				if(null!=productObj.get("rank_1_PID_s"))
					arsFeedObj.setRank_1_pid(productObj.get("rank_1_PID_s").toString().trim());

				if(null!=productObj.get("rank_1_seller_id_s"))
					arsFeedObj.setRank_1_seller_id(productObj.get("rank_1_seller_id_s").toString().trim());

				if(null!=productObj.get("rank_1_seller_name_s"))
					arsFeedObj.setRank_1_seller_name(productObj.get("rank_1_seller_name_s").toString().trim());
				else
					arsFeedObj.setRank_1_seller_name("NO RANK ONE SELLER NAME IN SOLR");

				if(null!=productObj.get("SoldBy"))
					arsFeedObj.setSoldByRankingAPI(productObj.get("SoldBy").toString().trim());
				else
					arsFeedObj.setSoldByRankingAPI("NO SOLD BY IN SOLR");

				if(null!=productObj.get("PID"))
				{
					arsFeedObj.setPID(productObj.get("PID").toString().trim());
					dealsSOLRDetails.put(productObj.get("PID").toString().trim(), arsFeedObj);
				}

			}

		} 

		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	*//**
	 * @param obj
	 *//*
	public static void fetchRankingAPIByPassingUID(ARSFeedsDetails arsObj){
		String url="";
		JSONParser parser = new JSONParser();
		url=APIUrls.fetchAPIUrls().arsTopRankedApiByPassingUID;

		url=url.replaceAll("PLACEHOLDERFORUID", arsObj.getUid() );

		String response=getResponse(url);
		Object obj;
		try {
			obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray groupNode = (JSONArray) jsonObject.get("groups");


			JSONObject jsonObject1 =(JSONObject)groupNode.get(0);

			JSONArray arrayObj= (JSONArray)jsonObject1.get("offers");

			for (Object offer : arrayObj){

				JSONObject objoffer =(JSONObject)offer;


				if(null!=objoffer.get("rank")){

					if(objoffer.get("rank").toString().equalsIgnoreCase("1"))
					{

						if(null!=objoffer.get("id"))
							arsObj.setRank_1_pid(objoffer.get("id").toString().trim());
						else
							arsObj.setRank_1_pid("NO RANK PID FROM RANKING API");

						if(null!=objoffer.get("sellerId"))
							arsObj.setRank_1_seller_id(objoffer.get("sellerId").toString().trim());
						else
							arsObj.setRank_1_seller_id("NO RANK ONE SELLER ID FROM RANKING API");

						if(null!=objoffer.get("sellerName"))
							arsObj.setRank_1_seller_name(objoffer.get("sellerName").toString().trim());
						else
							arsObj.setRank_1_seller_name("NO RANK ONE SELLER NAME FROM RANKING API");

						break;
					}
				}

			}



		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static String getSite(){
		String site=null;	
		if(baseUrl.contains("sears")){
			site="Sears";
		}else if(baseUrl.contains("kmart")){
			site="Kmart";
		}else if(baseUrl.contains("kenmore")){
			site="Kenmore";
		}else if(baseUrl.contains("craftsman")){
			site="Craftsman";
		}
		return site;
	}

	public static String removePartNumberEndingP(String partNumber){
		if(partNumber.toUpperCase().endsWith("P")){
			return partNumber.substring(0, partNumber.length()-1);
		} else {
			return partNumber;
		}
	}


	public static void main(String[] args){
		ARSFeedsDetails arsObj =new ARSFeedsDetails();
		arsObj.setUid("ade74d71-8549-442d-a3e1-011a5e76928a");
		fetchRankingAPIByPassingUID(arsObj);
	}

	public static String fetchSearchImpression(String url){
		String response=getResponse(url);
		Object obj;
		JSONParser parser = new JSONParser();
		String rankAlgorithmNode = null;
		try {
			obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject dataNode =  (JSONObject) jsonObject.get("data");
			JSONObject searchImpressionMsgNode = (JSONObject)dataNode.get("searchImpressionMsg");
			if(searchImpressionMsgNode!=null){
				rankAlgorithmNode =searchImpressionMsgNode.get("rankAlgorithm").toString();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return rankAlgorithmNode;
	}

	public static JSONArray fetchSwatchInfoProductSearchApi(String keyword)
	{
		
		
         JSONParser parser = new JSONParser();
		
        String url=apiUrls.productSeacrhApi.replace("PLACEHOLDERFORKEYWORD", keyword)
				.replace("PLACEHOLDERFORBASEURL", baseUrl);
        
       try
       {
    	
    	com.shc.automation.Logger.log("Getting swatchInfo from productSearch API for partnumber "+keyword, TestStepType.VERIFICATION_SUBSTEP);
    	System.out.println(" Getting swatchInfo from productSearch API for partnumber "+keyword);	
    	String response=getResponse(url);  
    	
		Object objNew = parser.parse(response);
		JSONObject jsonObject  = (JSONObject) objNew; 	
		
		JSONObject jsonDataObject = (JSONObject) jsonObject.get("data");
		JSONArray jsonProductArray = (JSONArray) jsonDataObject.get("products");
		
		JSONObject jsonSwatchInfoObject = (JSONObject)((JSONObject)jsonProductArray.get(0)).get("swatchInfo");

		JSONArray jsonSwatchArray = (JSONArray)jsonSwatchInfoObject.get("swatches");

		
		return jsonSwatchArray;	
		
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
    	   return null;
       }
	
       }

	public static String fetchShortDescPromotionApi(String promoId)
	{
		
		
         JSONParser parser = new JSONParser();
		
        String url=apiUrls.promotionApi.replace("PLACEHOLDERFORPROMO",promoId )
				.replace("PLACEHOLDERFORBASEURL", baseUrl);
        
       try
       {
    	
    	
    	System.out.println("Getting short description from Promotion API for promotion "+promoId);	
    	String response=getResponse(url);  
    	
    	if(response !=null)
    	{
    	com.shc.automation.Logger.log("Getting short description from Promotion Service "+url, TestStepType.VERIFICATION_PASSED);
    	//com.shc.automation.Logger.log("Getting short description from Promotion API for promotion "+promoId, TestStepType.VERIFICATION_SUBSTEP);	
		Object objNew = parser.parse(response);
		JSONObject jsonObject  = (JSONObject) objNew; 	
		
		JSONObject jsonDataObject = (JSONObject) jsonObject.get("data");
		String serviceDesc = (String) jsonDataObject.get("shortDesc");
		
		
		return serviceDesc;	
    	
    	}
       
    	else 
    	{
    		com.shc.automation.Logger.log("Response from  Promotion Service is null "+url, TestStepType.VERIFICATION_FAILED);
            return null;    	
    	}
    	
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
    	   return null;
       }
	
       }

	public static void fetchProductDescriptionContentAPI(String partNumber, 
			ProductDetails prodObj) {
		// TODO Auto-generated method stub
		if(!(partNumber.endsWith("P")||partNumber.endsWith("p")))
		{
			partNumber=partNumber+"P";
		}
		try 
		{
			JSONParser parser = new JSONParser();
			String pdpServiceApi=apiUrls.contentApiUrl.replace("PLACEHOLDERFORPARTNUMBER", partNumber);
			System.out.println(pdpServiceApi);
			String response=getResponse(pdpServiceApi);
			Object obj = parser.parse(response);  
			JSONArray jsonArray = (JSONArray) obj;

			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			JSONObject jsonBlob = (JSONObject)jsonObject.get("_blob");
			JSONObject jsonContent = (JSONObject)jsonBlob.get("content");
			String prodName = (String) jsonContent.get("name");

			if(jsonContent.containsKey("seo"))
			{
				JSONObject jsonSeo = (JSONObject)jsonContent.get("seo");
				if(jsonSeo.containsKey("title"))
					prodObj.getGreenBoxDetails().setSeoTitle((String)jsonSeo.get("title"));

				if(jsonSeo.containsKey("desc"))
					prodObj.getGreenBoxDetails().setSeoDescription((String)jsonSeo.get("desc"));
			}

			if(jsonContent.containsKey("brand"))
			{
				JSONObject jsonBrand = (JSONObject)jsonContent.get("brand");
				if(jsonBrand.containsKey("name"))
				{
					prodObj.getGreenBoxDetails().setStrProdBrand((String)jsonBrand.get("name"));
					prodObj.getGreenBoxDetails().setStrProdTitle((String)jsonBrand.get("name") + " "+prodName);                       
				}
			}

			if(jsonContent.containsKey("desc"))
			{
				JSONArray jsonDescArr = (JSONArray)jsonContent.get("desc");
				for(int i=0; i<jsonDescArr.size(); i++)
				{
					JSONObject tmpDescObj = (JSONObject) jsonDescArr.get(i);
					if(tmpDescObj.containsKey("type") && tmpDescObj.containsKey("val"))
					{
						String descStr = (String)tmpDescObj.get("val");
						if(((String)tmpDescObj.get("type")).equalsIgnoreCase("S"))
						{
							prodObj.getGreenBoxDetails().setStrProdShortDesc(descStr);
							prodObj.getGreenBoxDetails().setShortDescPresent(true);
						}

						else if(((String)tmpDescObj.get("type")).equalsIgnoreCase("L"))
						{
							prodObj.getGreenBoxDetails().setStrProdLongDesc(descStr);
							prodObj.getGreenBoxDetails().setLongDescPresent(true);

						}

						else if(((String)tmpDescObj.get("type")).equalsIgnoreCase("T"))
						{
							prodObj.getGreenBoxDetails().setKeyFeatureValue(descStr);
							prodObj.getGreenBoxDetails().setKeyFeatursPresent(true);
						}
					}
				}
			}
			if(jsonContent.containsKey("assets"))
			{
				JSONObject jsonAssets = (JSONObject)jsonContent.get("assets");
				if(jsonAssets.containsKey("imgs"))
				{
					List<String> primaryImgSrc = new ArrayList<String>();
					List<String> alternativeImgSrc = new ArrayList<String>();

					JSONArray jsonImgArr = (JSONArray)jsonAssets.get("imgs");
					for(int j=0; j<jsonImgArr.size();j++)
					{
						JSONObject tmpImgObject = (JSONObject)jsonImgArr.get(j);
						if(tmpImgObject.containsKey("type") && tmpImgObject.containsKey("vals"))
						{
							JSONArray imgValsArr = (JSONArray)tmpImgObject.get("vals");

							if(((String)tmpImgObject.get("type")).equalsIgnoreCase("P"))
							{
								for(int k=0; k<imgValsArr.size(); k++)
								{
									primaryImgSrc.add((String)((JSONObject)imgValsArr.get(k)).get("src"));
								}
							}

							else if(((String)tmpImgObject.get("type")).equalsIgnoreCase("A"))
							{
								for(int k=0; k<imgValsArr.size(); k++)
								{
									alternativeImgSrc.add((String)((JSONObject)imgValsArr.get(k)).get("src"));
								}
							}
						}
					}

					prodObj.getGreenBoxDetails().setPrimaryImagesSrc(primaryImgSrc);
					prodObj.getGreenBoxDetails().setAlternateImagesSrc(alternativeImgSrc);
				}
			}

		}
		catch(Exception e)
		{
			System.out.println("Faced expection while traversing through Content API for fetching prodcut contents.");
			e.printStackTrace();
		}
	}

*/}