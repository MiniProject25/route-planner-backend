package com.project.routeplanner.parser;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class SaxParser {

    public static void parseOSM(String filePath, OsmHandler osmHandler) throws SAXException, IOException, ParserConfigurationException {
        // creating a factory
        SAXParserFactory factory = SAXParserFactory.newInstance();

        // create a sax parser
        SAXParser saxparser = factory.newSAXParser();

        // parse the osm file using our custom osm handler
        saxparser.parse(new File(filePath), osmHandler);
    }
}
