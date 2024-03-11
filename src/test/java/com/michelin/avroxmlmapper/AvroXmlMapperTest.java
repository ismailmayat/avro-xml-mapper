package com.michelin.avroxmlmapper;

import com.iaag.FlightLegIdentifierType;
import com.iaag.FlightLegType;
import com.iaag.IATA_AIDX_FlightLegNotifRQ;
import com.iaag.Originator;
import com.michelin.avro.AltListItem;
import com.michelin.avro.EmbeddedRecord;
import com.michelin.avro.SubXMLTestModel;
import com.michelin.avro.SubXMLTestModelMultipleXpath;
import com.michelin.avro.TestModelEmptyNamespace;
import com.michelin.avro.TestModelParentRecord;
import com.michelin.avro.TestModelXMLDefaultXpath;
import com.michelin.avro.TestModelXMLMultipleXpath;
import com.michelin.avroxmlmapper.exception.AvroXmlMapperException;
import com.michelin.avroxmlmapper.mapper.AvroXmlMapper;
import com.michelin.avroxmlmapper.utility.GenericUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvroXmlMapperTest {


    @Test
    void testFlight() throws Exception{
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/flight-leg-not-rq-2.xml")), StandardCharsets.UTF_8);
        var result = AvroXmlMapper.convertXmlStringToAvro(input,IATA_AIDX_FlightLegNotifRQ.class);

        var xmlResult = AvroXmlMapper.convertAvroToXmlString(result);

      //  var expectedModel = buildFlightLegTestModel();

        System.out.println(xmlResult);



    }
    @Test
    void testXmlToAvro() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlDefaultXpath.xml")), StandardCharsets.UTF_8);

        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelXMLDefaultXpath.class);

        var expectedModel = buildDefaultXpathTestModel();

        assertEquals(expectedModel, result);
    }

    @Test
    void testXmlToAvroWithCustomXpathSelector() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom1.xml")), StandardCharsets.UTF_8);

        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelXMLMultipleXpath.class, "customXpath1");

        assertEquals(buildMultiXpathTestModel(), result);
    }

    @Test
    void testXmlToAvroWithCustomXpathSelectorAndCustomXmlNamespacesSelector() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom2AndCustomXmlNamespaces.xml")), StandardCharsets.UTF_8);

        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelXMLMultipleXpath.class, "customXpath2", "xmlNamespacesCustom2");

        assertEquals(buildMultiXpathTestModel2(), result);
    }

    @Test
    void testAvroToXml() throws Exception {
        var expectedModel = buildDefaultXpathTestModel();

        var xmlResult = AvroXmlMapper.convertAvroToXmlString(expectedModel);
        var expected = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlDefaultXpath.xml")), StandardCharsets.UTF_8)
                .replaceAll("[\r\n]+", "")
                .replaceAll("(?m)^[ \\t]*", "")
                .replaceAll("(?m)[ \\t]*$", "")
                .replaceAll(">\\s+<", "><")
                .trim();

        assertEquals(expected, xmlResult);
    }

    @Test
    void testAvroToXmlWithCustomXpathSelector() throws Exception {

        var expectedModel = buildMultiXpathTestModel();

        var xmlResult = AvroXmlMapper.convertAvroToXmlString(expectedModel, "customXpath1");
        var expected = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom1.xml")), StandardCharsets.UTF_8)
                .replaceAll("[\r\n]+", "")
                .replaceAll("(?m)^[ \\t]*", "")
                .replaceAll("(?m)[ \\t]*$", "")
                .replaceAll(">\\s+<", "><")
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expected, xmlResult);
    }

    @Test
    void testAvroToXmlWithCustomXpathSelectorAndCustomXmlNamespacesSelector() throws Exception {
        var expectedModel = buildMultiXpathTestModel2();

        var xmlResult = AvroXmlMapper.convertAvroToXmlString(expectedModel, "customXpath2", "xmlNamespacesCustom2");
        var expected = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom2AndCustomXmlNamespaces.xml")), StandardCharsets.UTF_8)
                .replaceAll("[\r\n]+", "")
                .replaceAll("(?m)^[ \\t]*", "")
                .replaceAll("(?m)[ \\t]*$", "")
                .replaceAll(">\\s+<", "><")
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals(expected, xmlResult);
    }

    @Test
    void testAvroToXMLDocument() throws Exception {
        var inputModel = buildDefaultXpathTestModel();

        var xmlResult = AvroXmlMapper.convertAvroToXmlDocument(inputModel);

        var expectedStringUncleaned = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlDefaultXpath.xml")), StandardCharsets.UTF_8);
        var expectedDocument = XMLUnit.getWhitespaceStrippedDocument(XMLUnit.buildControlDocument(expectedStringUncleaned));

        assertEquals(GenericUtils.documentToString(expectedDocument), GenericUtils.documentToString(xmlResult));
    }

    @Test
    void testAvroToXmlDocumentWithCustomXpathSelector() throws Exception {
        var inputModel = buildMultiXpathTestModel();

        var xmlResult = AvroXmlMapper.convertAvroToXmlDocument(inputModel, "customXpath1");

        var expectedStringUncleaned = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom1.xml")), StandardCharsets.UTF_8);
        var expectedDocument = XMLUnit.getWhitespaceStrippedDocument(XMLUnit.buildControlDocument(expectedStringUncleaned));

        assertEquals(GenericUtils.documentToString(expectedDocument), GenericUtils.documentToString(xmlResult));
    }

    @Test
    void testAvroToXmlDocumentWithCustomXpathSelectorAndCustomXmlNamespacesSelector() throws Exception {
        var inputModel = buildMultiXpathTestModel2();

        var xmlResult = AvroXmlMapper.convertAvroToXmlDocument(inputModel, "customXpath2", "xmlNamespacesCustom2");

        var expectedStringUncleaned = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlXpathCustom2AndCustomXmlNamespaces.xml")), StandardCharsets.UTF_8);
        var expectedDocument = XMLUnit.getWhitespaceStrippedDocument(XMLUnit.buildControlDocument(expectedStringUncleaned));

        assertEquals(GenericUtils.documentToString(expectedDocument), GenericUtils.documentToString(xmlResult));
    }


    @Test
    void testFaultyNamespaceXmlToAvro() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlFaultyNamespace.xml")), StandardCharsets.UTF_8);

        AvroXmlMapperException e = assertThrows(AvroXmlMapperException.class, () -> AvroXmlMapper.convertXmlStringToAvro(input, TestModelXMLDefaultXpath.class));

        assertEquals("Failed to parse XML", e.getMessage());
        assertEquals("The default namespace uri provided in the avsc schema (\"http://namespace.uri/default\") is not defined in the XML document. Either fix your avsc schema to match the default namespace defined in the xml, or make sure that the xml document you are converting is not faulty.", e.getCause().getMessage());
    }

    @Test
    void testEmptyDefaultNamespaceXmlToAvro() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlWithoutDefaultNamespace.xml")), StandardCharsets.UTF_8);
        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelEmptyNamespace.class, "specificXpath", "specificXmlNamespaces");

        assertEquals(TestModelEmptyNamespace.newBuilder().setStringField("Hello").setThirdStringField("World").build(), result);
    }

    @Test
    void testEmptyNamespaceXmlToAvro() throws Exception {
        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlWithoutNamespace.xml")), StandardCharsets.UTF_8);
        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelEmptyNamespace.class);

        assertEquals(TestModelEmptyNamespace.newBuilder().setStringField("Hello").setOtherStringField("Hello").setThirdStringField("World").build(), result);
    }

    @Test
    void testEmbeddedRecordXMLToAvro() throws Exception{
        TestModelParentRecord expectedModel = TestModelParentRecord.newBuilder()
                .setEmbeddedRecord(EmbeddedRecord.newBuilder().setStringField("Hello").setOtherStringField("World").build())
                .build();

        var input = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlWithEmbeddedRecord.xml")), StandardCharsets.UTF_8);
        var result = AvroXmlMapper.convertXmlStringToAvro(input, TestModelParentRecord.class);

        assertEquals(expectedModel, result);
    }

    @Test
    void testEmbeddedRecordAvroToXML() throws Exception{
        TestModelParentRecord inputModel = TestModelParentRecord.newBuilder()
                .setEmbeddedRecord(EmbeddedRecord.newBuilder().setStringField("Hello").setOtherStringField("World").setThirdStringField("toto").build())
                .build();

        var expectedString = IOUtils.toString(Objects.requireNonNull(AvroXmlMapperTest.class.getResourceAsStream("/xmlWithEmbeddedRecord.xml")), StandardCharsets.UTF_8);
        var result = AvroXmlMapper.convertAvroToXmlDocument(inputModel);
        var expectedDocument = XMLUnit.getWhitespaceStrippedDocument(XMLUnit.buildControlDocument(expectedString));
        assertEquals(GenericUtils.documentToString(expectedDocument), GenericUtils.documentToString(result));
    }

    private TestModelXMLDefaultXpath buildDefaultXpathTestModel() {

        var mapResult = new HashMap<String, String>();
        mapResult.put("key1", "value1");
        mapResult.put("key2", "value2");
        mapResult.put("key3", "value3");

        return TestModelXMLDefaultXpath.newBuilder()
                .setBooleanField(true)
                .setDateField(Instant.ofEpochMilli(1766620800000L))
                .setQuantityField(BigDecimal.valueOf(52L).setScale(4, RoundingMode.HALF_UP))
                .setStringField("lorem ipsum")
                .setStringMapScenario1(mapResult)
                .setStringMapScenario2(mapResult)
                .setStringList(List.of("item1", "item2", "item3"))
                .setAltList(List.of(AltListItem.newBuilder().setListItemAttribute("attrToto").setListItemContent("toto").build(),AltListItem.newBuilder().setListItemAttribute("attrTutu").setListItemContent("tutu").build()))
                .setRecordListWithDefault(List.of(
                        SubXMLTestModel.newBuilder().setSubStringField("item1").setSubIntField(1).setSubStringFieldFromAttribute("attribute1").build(),
                        SubXMLTestModel.newBuilder().setSubStringField("item2").setSubIntField(2).setSubStringFieldFromAttribute("attribute2").build(),
                        SubXMLTestModel.newBuilder().setSubStringField("item3").setSubIntField(3).setSubStringFieldFromAttribute("attribute3").build()
                ))
                .build();
    }

    private TestModelXMLMultipleXpath buildMultiXpathTestModel() {

        var mapResult = new HashMap<String, String>();
        mapResult.put("key1", "value1");
        mapResult.put("key2", "value2");
        mapResult.put("key3", "value3");

        return TestModelXMLMultipleXpath.newBuilder()
                .setBooleanField(true)
                .setDateField(Instant.ofEpochMilli(1766620800000L))
                .setQuantityField(BigDecimal.valueOf(52L).setScale(4, RoundingMode.HALF_UP))
                .setStringField("lorem ipsum")
                .setStringMapScenario1(mapResult)
                .setStringMapScenario2(mapResult)
                .setStringList(List.of("item1", "item2", "item3"))
                .setRecordListWithDefault(List.of(
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item1").setSubIntField(1).setSubStringFieldFromAttribute("attribute1").build(),
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item2").setSubIntField(2).setSubStringFieldFromAttribute("attribute2").build(),
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item3").setSubIntField(3).setSubStringFieldFromAttribute("attribute3").build()
                ))
                .build();
    }

    private TestModelXMLMultipleXpath buildMultiXpathTestModel2() {

        var mapResult = new HashMap<String, String>();
        mapResult.put("key1", "value1");
        mapResult.put("key2", "value2");
        mapResult.put("key3", "value3");

        return TestModelXMLMultipleXpath.newBuilder()
                .setDateField(Instant.ofEpochMilli(1766620800000L))
                .setStringField("lorem ipsum")
                .setStringMapScenario1(mapResult)
                .setStringMapScenario2(mapResult)
                .setStringList(List.of("item1", "item2", "item3"))
                .setRecordListWithDefault(List.of(
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item1").setSubIntField(1).setSubStringFieldFromAttribute("attribute1").build(),
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item2").setSubIntField(2).setSubStringFieldFromAttribute("attribute2").build(),
                        SubXMLTestModelMultipleXpath.newBuilder().setSubStringField("item3").setSubIntField(3).setSubStringFieldFromAttribute("attribute3").build()
                ))
                .build();
    }

    private IATA_AIDX_FlightLegNotifRQ buildFlightLegTestModel(){

        var originator = new Originator();

        originator.setCompanyShortName("BA");

        var flightLeg = new FlightLegType();

        flightLeg.setLegIdentifier(new FlightLegIdentifierType());

        List<FlightLegType> flightLegs = new ArrayList<>();

        flightLegs.add(flightLeg);

        return IATA_AIDX_FlightLegNotifRQ.newBuilder()
                .setAltLangID("en-us")
                .setVersion(15.1)
                .setTimeStampValue("2024-01-08T16:29:22.997Z")
                .setTarget("Test")
                .setTransactionIdentifier("FLT:VFU")
                .setSequenceNmbr("7186")
                .setPrimaryLangID("en-us")
                .setOriginator(originator)
                .setFlightLeg(flightLegs)
                .setAsynchronousAllowedInd(false)
                .build();


    }
}
