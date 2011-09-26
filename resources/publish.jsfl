(function(){
	function getDocumentByURI(uri) {
		var i;
		for (i = 0; i < fl.documents.length; i++) {
			if (fl.documents[i].pathURI === uri) {
				fl.setActiveWindow(fl.documents[i]);
				return fl.documents[i];
			}
		}
		return null;
	}
	
	function publish(documentPath, profileName)	{
		var doc, documentURI, wasOpen, publishProfile;
		
		documentURI = FLfile.platformPathToURI(documentPath);
		
		doc = getDocumentByURI(documentURI);
		if (doc) {
			wasOpen = true;
		} else {
			wasOpen = false;
			doc = fl.openDocument(documentURI);
		}
		doc.publish();
		publishProfile = XML(doc.exportPublishProfileString());
		fl.trace("+++" + publishProfile.PublishFormatProperties[0].flashFileName[0]);

		if (!wasOpen) {
			doc.close(false);
		}
	}
	
	if (args.length !== 2) {
		fl.trace("!!!Error: Requires exactly one argument");
	} else {
		publish(args[1]);
	}
}()); 
