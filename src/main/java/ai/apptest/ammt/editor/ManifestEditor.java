package ai.apptest.ammt.editor;


import ai.apptest.ammt.editor.decode.AXMLDoc;
import ai.apptest.ammt.editor.decode.BTagNode;
import ai.apptest.ammt.editor.decode.BXMLNode;
import ai.apptest.ammt.editor.decode.StringBlock;
import ai.apptest.ammt.editor.utils.Pair;
import ai.apptest.ammt.editor.utils.TypedValue;
import com.sun.org.apache.xpath.internal.operations.Bool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://github.com/ZengRun/AxmlEditor
 */
public class ManifestEditor {

	private AXMLDoc doc;
	private BXMLNode applicationNode;
	private StringBlock mStringBlock;

	public ManifestEditor(String inputFile) throws Exception {
		this(new File(inputFile));
	}

	public ManifestEditor(File inputFile) throws Exception {
		this.doc = new AXMLDoc();
		this.doc.parse(new FileInputStream(inputFile));
		this.applicationNode = this.doc.getApplicationNode();
		this.mStringBlock = this.doc.getStringBlock();
	}

	///////////
	private BTagNode findTagNode(String tag, String name) {
		int nameAttr = this.mStringBlock.putString("name");
		int nameValue = this.mStringBlock.putString(name);
		for(BXMLNode node : this.applicationNode.getChildren()){
			BTagNode tagNode = (BTagNode) node;
			if(tag.equals(this.mStringBlock.getStringFor(tagNode.getName()))){
				int attrString = tagNode.getAttrStringForKey( nameAttr );
				if (attrString != -1 && this.mStringBlock.getStringFor(attrString).equals(name)) {
					return tagNode;
				}
			}
		}

		return null;
	}

	///////////
	private void addIntentFilter(BTagNode node, int namespace, int nameAttr, Pair<String, String> attr) {
		List<Pair<String, String>> filters = new ArrayList<>();
		for (String filter : attr.second.split("[ \t]*\\|[ \t]*")) {
			String[] fv = filter.split("[ \t]*=[ \t]*");
			if (fv.length != 2) {
				continue;
			}

			filters.add(new Pair<>(fv[0], fv[1]));
		}

		if (filters.size() == 0) {
			return;
		}

		int nameTag = this.mStringBlock.putString(attr.first);
		BTagNode filterNode = new BTagNode(-1, nameTag);
		filterNode.prepare();
		for (Pair<String, String> filter : filters) {
			int ftag = this.mStringBlock.putString(filter.first);
			int fval = this.mStringBlock.putString(filter.second);

			BTagNode fNode = new BTagNode(-1, ftag);
			// android:name
			BTagNode.Attribute fAttr = new BTagNode.Attribute(namespace, nameAttr, TypedValue.TYPE_STRING);
			fAttr.setString(fval);
			fNode.setAttribute(fAttr);

			fNode.prepare();
			filterNode.addChild(fNode);
		}

		node.prepare();
		node.addChild(filterNode);

	}

	public void addTag(String tag, String name, List<Pair<String, String>> attrs) {
		int namespace = this.mStringBlock.putString("http://schemas.android.com/apk/res/android");
		int nameTag = this.mStringBlock.putString(tag);

		int nameAttr = this.mStringBlock.putString("name");
		int nameValue = this.mStringBlock.putString(name);

		BTagNode node = new BTagNode(-1, nameTag);
		BTagNode.Attribute nameAttrNode = new BTagNode.Attribute(namespace, nameAttr, TypedValue.TYPE_STRING);
		nameAttrNode.setString(nameValue);
		node.setAttribute(nameAttrNode);

		for (Pair<String, String> attr : attrs) {
			if (attr.first.equals("intent-filter")) {
				this.addIntentFilter(node, namespace, nameAttr, attr);
				continue;
			}


			int nattr = this.mStringBlock.putString(attr.first);
			BTagNode.Attribute attrNode = null;
			if (attr.second.equals("true") || attr.second.equals("false")) {
				attrNode = new BTagNode.Attribute(namespace, nattr, TypedValue.TYPE_INT_BOOLEAN);
				attrNode.setBoolean(Boolean.valueOf(attr.second));

			} else {
				int vattr = this.mStringBlock.putString(attr.second);
				attrNode = new BTagNode.Attribute(namespace, vattr, TypedValue.TYPE_STRING);
				attrNode.setString(vattr);
			}

			node.setAttribute(attrNode);
		}

		node.prepare();
		BXMLNode application = this.doc.getApplicationNode(); //manifest node
		application.prepare();
		application.addChild(node);

	}

	public void removeTag(String tag, String name) {
		throw new NotImplementedException();
	}


	////////////


	public void addAttr(String tag, String name, String attr, String value) {
		throw new NotImplementedException();
	}

	public void removeAttr(String tag, String name, String attr) {
		throw new NotImplementedException();
	}

	public void modifyAttr(String tag, String name, String attr, String value) throws Exception {
		BTagNode node = this.findTagNode(tag, name);
		if (node != null) {
			int attrName = this.mStringBlock.putString(attr);
			BTagNode.Attribute attribute = node.getAttributeForKey(attrName);

			if (attribute != null) {
				int attrValue = this.mStringBlock.putString(value);
				attribute.setValue(attribute.getTypeValue(), attrValue);

			} else {
				throw new Exception("Not Found the attr '" + attr + "' of tag " + tag + " '" + name + "'");
			}
		} else {
			throw new Exception("Not Found the tag " + tag + " '" + name + "'");
		}
	}

	public void addApptestaiReceiver() {
		int namespace = this.mStringBlock.putString("http://schemas.android.com/apk/res/android");
		int nameTag = this.mStringBlock.putString("receiver");

		int nameAttr = this.mStringBlock.putString("name");
		int nameValue = this.mStringBlock.putString("ai.apptest.ApptestaiWCDEReceiver");

		int enabledAttr = this.mStringBlock.putString("enabled");
		int exportedAttr = this.mStringBlock.putString("exported");


		BTagNode receiverNode = new BTagNode(-1, nameTag);

		BTagNode.Attribute nameAttrNode = new BTagNode.Attribute(namespace, nameAttr, TypedValue.TYPE_STRING);
		nameAttrNode.setString(nameValue);
		receiverNode.setAttribute(nameAttrNode);

		BTagNode.Attribute exportedAttrNode = new BTagNode.Attribute(namespace, exportedAttr, TypedValue.TYPE_INT_BOOLEAN);
		exportedAttrNode.setBoolean(true);
		receiverNode.setAttribute(exportedAttrNode);

		BTagNode.Attribute enabeldAttrNode = new BTagNode.Attribute(namespace, enabledAttr, TypedValue.TYPE_INT_BOOLEAN);
		enabeldAttrNode.setBoolean(true);
		receiverNode.setAttribute(enabeldAttrNode);

		receiverNode.prepare();
		BXMLNode application = this.doc.getApplicationNode(); //manifest node
		application.prepare();
		application.addChild(receiverNode);
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		doc.build(bos);
		return bos.toByteArray();
	}

	public void saveAs(File outputFile) throws IOException {
		this.doc.build(new FileOutputStream(outputFile));
	}
//
//	public static void main(String[] args) {
//		try{
//			AXMLDoc doc = new AXMLDoc();
//			doc.parse(new FileInputStream(new File("/Users/kyang/Downloads//AndroidManifest.xml")));
//
//
//			StringBlock sb = doc.getStringBlock();
//			int namespace = sb.putString("http://schemas.android.com/apk/res/android");
//			int nameTag = sb.putString("receiver");
//
//			int nameAttr = sb.putString("name");
//			int nameValue = sb.putString("ai.apptest.WebContentsDebuggingEnableReceiver");
//
//			int enabledAttr = sb.putString("enabled");
//			int exportedAttr = sb.putString("exported");
//
//
//			BTagNode receiverNode = new BTagNode(-1, nameTag);
//
//			BTagNode.Attribute nameAttrNode = new BTagNode.Attribute(namespace, nameAttr, TypedValue.TYPE_STRING);
//			nameAttrNode.setString(nameValue);
//			receiverNode.setAttribute(nameAttrNode);
//
//			BTagNode.Attribute exportedAttrNode = new BTagNode.Attribute(namespace, exportedAttr, TypedValue.TYPE_INT_BOOLEAN);
//			exportedAttrNode.setBoolean(true);
//			receiverNode.setAttribute(exportedAttrNode);
//
//			BTagNode.Attribute enabeldAttrNode = new BTagNode.Attribute(namespace, enabledAttr, TypedValue.TYPE_INT_BOOLEAN);
//			enabeldAttrNode.setBoolean(true);
//			receiverNode.setAttribute(enabeldAttrNode);
//
//			receiverNode.prepare();
//			BXMLNode application = doc.getApplicationNode(); //manifest node
//			application.prepare();
//			application.addChild(receiverNode);
//
//
//
//
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			doc.build(bos);
//			XmlDecompressor xmlDecompressor = new XmlDecompressor();
//			System.out.println("Output AndroidManifest.xml\n" + xmlDecompressor.decompressXml(bos.toByteArray()));
//			bos.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
}
