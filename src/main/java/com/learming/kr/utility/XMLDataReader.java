package com.learming.kr.utility;

import com.learming.kr.config.ConfigUtility;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kalyanroy on 16/09/21.
 */
public class XMLDataReader
{
	ConfigUtility configUtility= ConfigUtility.getInstance();
	private String testDataFileType;
	private String testDataFile;

	private XMLDataReader(){
		testDataFile = configUtility.getXmlDataSet();
	}

	public static class XMLDataReaderModule {
		private static final XMLDataReader instance = new XMLDataReader();
	}

	public static XMLDataReader getInstance() {
		return XMLDataReaderModule.instance;
	}

	public Object[][] readDataFromXML(String channel, String feature)
	{
		try
		{
			Object [][] data=null;
			File file=new File(testDataFile);
			DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
			Document doc=dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList datasetList,featuresList=doc.getElementsByTagName("testcase");
			Node featureNode,channelNode;
			int flag=0;
			for(int i=0;i<featuresList.getLength();i++)
			{
				featureNode=featuresList.item(i);
				channelNode=featureNode.getParentNode();
				if(featureNode.getNodeType()==Node.ELEMENT_NODE&&channelNode.getNodeType()==Node.ELEMENT_NODE)
				{
					Element elementFeature=(Element)featureNode;
					Element elementChannel=(Element)channelNode;
					if(elementFeature.getAttribute("name").equalsIgnoreCase(feature)&&elementChannel.getAttribute("name").equalsIgnoreCase(channel))
					{
						datasetList=elementFeature.getElementsByTagName("dataset");
						data=getFieldValuesFromXMLMap(datasetList);
						flag=1;
						break;
					}
				}
			}
			if(flag==0)
				throw new Exception("Invalid channel or feature name.");

			return data;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Object[][] getFieldValuesFromXMLMap(NodeList datasetList) {
		try {
			NodeList fieldList;
			Node field;
			List<Map> total = new LinkedList<Map>();
			int fieldCount = 0, k = 0;
			int numberOfDataSet=datasetList.getLength();
			for (int i = 0; i < datasetList.item(0).getChildNodes().getLength(); i++) {
				field = datasetList.item(0).getChildNodes().item(i);
				if (field.getNodeType() == Node.ELEMENT_NODE)
					fieldCount++;
			}

			for (int i = 0; i < datasetList.getLength() && i < numberOfDataSet; i++) {
				fieldList = datasetList.item(i).getChildNodes();
				Map<String, String> map = new LinkedHashMap<String, String>();
				for (int j = 0; j < fieldList.getLength(); j++) {
					field = fieldList.item(j);
					if (field.getNodeType() == Node.ELEMENT_NODE) {
						if (field.getChildNodes().getLength() > 1||field instanceof CharacterData) {
							System.out.println("Field: " + field.getNodeName() + "\t\tValue: " + nodeToString(field));
							k++;
							map.put(field.getNodeName(), nodeToString(field));
						} else if (field.getChildNodes().item(0) == null) {
							System.out.println("Field: " + field.getNodeName() + "\t\tValue: ");
							k++;
							map.put(field.getNodeName(), "");
						} else {
							System.out.println("Field: " + field.getNodeName() + "\t\tValue: " + field.getChildNodes().item(0).getNodeValue());
							k++;
							if (map.containsKey(field.getNodeName()))
								throw new RuntimeException("Duplicate key Identified at dataset " + (i + 1) + " at line number " + k);
							map.put(field.getNodeName(),field.getChildNodes().item(0).getNodeValue());
						}

					}
				}
				if (k != fieldCount)
					throw new Exception("Inconsistent dataset found at dataset position " + (i + 1) + ". Please check the xml file.");
				k = 0;
				total.add(map);
			}

			Object[][] mapdata = new Object[total.size()][1];

			for (int i = 0; i < total.size(); i++) {
				mapdata[i][0] = total.get(i);
			}
			return mapdata;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}
}
