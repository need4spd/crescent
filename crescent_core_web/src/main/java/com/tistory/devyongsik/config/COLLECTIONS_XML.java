package com.tistory.devyongsik.config;

enum COLLECTIONS_XML {
	DEFAULT {
		@Override
		public String getXmlDir() {
			return "collection/collections.xml";
		}
	},
	TEST {
		@Override
		public String getXmlDir() {
			return "collection/test-collections.xml";
		}
	};
	
	public abstract String getXmlDir();
}
