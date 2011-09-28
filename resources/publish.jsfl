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
	
	function publish(documentPath, compileLogPath) {
		var doc, documentURI, compileLogURI, wasOpen, publishProfile;
		
		documentURI = FLfile.platformPathToURI(documentPath);
		compileLogURI = FLfile.platformPathToURI(compileLogPath);
		
		doc = getDocumentByURI(documentURI);
		if (doc) {
			wasOpen = true;
		} else {
			wasOpen = false;
			doc = fl.openDocument(documentURI);
		}
		fl.compilerErrors.clear();
		doc.publish();
		fl.compilerErrors.save(compileLogURI);
		publishProfile = XML(doc.exportPublishProfileString());
		fl.trace("+++" + publishProfile.PublishFormatProperties[0].flashFileName[0]);

		if (!wasOpen) {
			doc.close(false);
		}
	}
	
	if (args.length !== 3) {
		fl.trace("!!!Error: Usage: publish.jsfl DOCUMENT COMPILELOG");
	} else {
		publish(args[1], args[2]);
	}
}()); 
