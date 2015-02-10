/**
	 * I will check any relation target has '#' characters and replace with encoded value %23 </br>
	 * MSWord is decoding %23 character to '#' resulting POI is failing to load / parse the relation (links) and skipped
	 * @param _id
	 */
	private void checkAndUpdateRelationTarget(String _id) {
		InputStream stream = null;
		try {
			PackagePart relPart = document.getPackage().getPartsByName(java.util.regex.Pattern.compile("/word/_rels/document.xml.rels")).get(0);
			SAXReader reader = new SAXReader();
			stream = relPart.getInputStream();
			Document xmlRelationshipsDoc = reader.read(stream);

			// Browse default types
			Element root = xmlRelationshipsDoc.getRootElement();

			for (Iterator<?> i = root.elementIterator(PackageRelationship.RELATIONSHIP_TAG_NAME); i.hasNext();) {
				Element element = (Element) i.next();
				String id = element.attribute(PackageRelationship.ID_ATTRIBUTE_NAME).getValue();
				if(id.equalsIgnoreCase(_id)) {
					String type = element.attribute(PackageRelationship.TYPE_ATTRIBUTE_NAME).getValue();
					Attribute targetModeAttr = element.attribute(PackageRelationship.TARGET_MODE_ATTRIBUTE_NAME);
					TargetMode targetMode = TargetMode.INTERNAL;
					if (targetModeAttr != null) {
						targetMode = targetModeAttr.getValue().toLowerCase().equals("internal") ? TargetMode.INTERNAL : TargetMode.EXTERNAL;
					}
					String value = element.attribute(PackageRelationship.TARGET_ATTRIBUTE_NAME).getValue();
					relPart.removeRelationship(_id);
					value = value.replaceAll("#", "%23");
					document.getPackagePart().addRelationship(PackagingURIHelper.toURI(value), targetMode, type, _id);
				}				
			}
		} catch (Exception e) {
			//do nothing
		} finally {
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
				}
		}
	}
	