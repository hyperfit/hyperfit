package org.hyperfit.resource.html5;




import org.hyperfit.exception.HyperfitException;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.resource.html5.controls.form.JsoupHtml5Form;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hyperfit.Helpers.makeSet;
import static org.junit.Assert.*;


public class Html5ResourceTest {

    static Element makeAnchor(String rel, String href) {
        return makeAnchor(rel, href, null);
    }

    static Element makeAnchor(String rel, String href, String title) {
        return makeAnchor(rel, href, title, null);
    }

    static Element makeAnchor(String rel, String href, String title, String name){
        Element anchor = new Element(Tag.valueOf("a"), "");
        anchor.attr("rel", rel);
        anchor.attr("href", href);

        if(title != null) {
            anchor.attr("title", title);
        }

        if(name != null){
            anchor.attr("name", name);
        }
        return anchor;
    }


    static Element makeLink(String rel, String href){
        return makeLink(rel, href, null);
    }

    static Element makeLink(String rel, String href, String title){
        //per http://www.w3.org/TR/1999/REC-html401-19991224/struct/links.html#edef-LINK
        Element link = new Element(Tag.valueOf("link"), "");
        link.attr("rel", rel);
        link.attr("href", href);

        if(title != null) {
            link.attr("title", title);
        }

        //Note link does not support name field

        return link;
    }

    static Element makeForm(String formName) {
        Element form = new Element(Tag.valueOf("form"), "");

        form.attr("name", formName);

        return form;
    }

    Document doc;
    Element body;
    Element head;

    @Before
    public void setUp(){
        doc = Jsoup.parse("<html>" +
        "<head>" +
        "</head>" +
        "" +
        "<body>" +
        "</body>" +
        "" +
        "" +
        "</html>");

        body = doc.body();
        head = doc.head();
    }


    @Test(expected = NullPointerException.class)
    public void testDataSourceNull() {
        new Html5Resource((Document)null);
    }


    @Test
    public void testGetLinkAnchorTag() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";

        body.appendChild(makeAnchor(rel, href, title));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink hyperLink = resource.getLink(rel);
        assertEquals(href, hyperLink.getHref());
        assertEquals(title, hyperLink.getTitle());
        assertEquals(null, hyperLink.getName());
    }


    @Test
    public void testGetLinkLinkTag() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";

        head.appendChild(makeLink(rel, href, title));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink hyperLink = resource.getLink(rel);
        assertEquals(href, hyperLink.getHref());
        assertEquals(title, hyperLink.getTitle());
        assertEquals(null, hyperLink.getName());
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkWithMultipleMatchesThrows() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";

        body.appendChild(makeAnchor(rel, href, title));
        body.appendChild(makeAnchor(rel, href, title));

        Html5Resource resource = new Html5Resource(doc);
        resource.getLink(rel);
    }


    @Test
    public void testGetLinkWithSingleResourceAndNameAnchorEncoding() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";
        String name = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href, title, name));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink hyperLink = resource.getLink(rel, name);
        assertEquals(href, hyperLink.getHref());
        assertEquals(title, hyperLink.getTitle());
        assertEquals(name, hyperLink.getName());
    }



    @Test
    public void testGetLinkWithMultipleResourcesArrayAndName() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink hyperLink = resource.getLink(rel, name2);
        assertEquals(href2, hyperLink.getHref());
        assertEquals(title2, hyperLink.getTitle());
        assertEquals(name2, hyperLink.getName());
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkWithMultipleResourcesArrayAndNameError() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        Html5Resource resource = new Html5Resource(doc);
        resource.getLink(rel, UUID.randomUUID().toString());
    }


    @Test
    public void testGetLinksNoLinksAtAll() {
        Html5Resource resource = new Html5Resource(doc);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksNoHeadOrBody() {
        doc = Jsoup.parse("<html></html>");
        Html5Resource resource = new Html5Resource(doc);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }



    @Test
    public void testGetLinksSingleAndMultipleLinksAnchorAndLinkTags() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String href3 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/3";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String title3 = "a title 3";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));
        head.appendChild(makeLink(rel, href3, title3));


        String otherRel = UUID.randomUUID().toString();
        String href4 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/4";
        String title4 = "a title 4";
        head.appendChild(makeLink(otherRel, href4, title4));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks();

        assertEquals(4, links.length);
        assertThat(links, arrayContainingInAnyOrder(
            allOf(
                hasProperty("title", equalTo(title1)),
                hasProperty("rel", equalTo(rel)),
                hasProperty("href", equalTo(href1)),
                hasProperty("name", equalTo(name1))
            ),
            allOf(
                hasProperty("title", equalTo(title2)),
                hasProperty("rel", equalTo(rel)),
                hasProperty("href", equalTo(href2)),
                hasProperty("name", equalTo(name2))
            ),
            allOf(
                hasProperty("title", equalTo(title3)),
                hasProperty("rel", equalTo(rel)),
                hasProperty("href", equalTo(href3)),
                hasProperty("name", nullValue())
            ),
            allOf(
                hasProperty("title", equalTo(title4)),
                hasProperty("rel", equalTo(otherRel)),
                hasProperty("href", equalTo(href4)),
                hasProperty("name", nullValue())
            )

        ));

    }

    @Test
    public void testGetLinksAnchorTag() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";

        body.appendChild(makeAnchor(rel, href, title));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] hyperLinks = resource.getLinks(rel);
        assertEquals(1, hyperLinks.length);
        assertEquals(href, hyperLinks[0].getHref());
        assertEquals(title, hyperLinks[0].getTitle());
        assertEquals(null, hyperLinks[0].getName());
    }


    @Test
    public void testGetLinksLinkTag() {
        String href = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String rel = "bb:promotions";

        head.appendChild(makeLink(rel, href, title));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] hyperLinks = resource.getLinks(rel);
        assertEquals(1, hyperLinks.length);
        assertEquals(href, hyperLinks[0].getHref());
        assertEquals(title, hyperLinks[0].getTitle());
        assertEquals(null, hyperLinks[0].getName());
    }


    @Test
    public void testGetLinksWithMultipleMatchingAnchorTags() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks(rel);
        assertEquals(2, links.length);
        assertEquals(href1, links[0].getHref());
        assertEquals(title1, links[0].getTitle());
        assertEquals(name1, links[0].getName());
        assertEquals(href2, links[1].getHref());
        assertEquals(title2, links[1].getTitle());
        assertEquals(name2, links[1].getName());
    }

    @Test
    public void testGetLinksWithMultipleMatchingLinkTags() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";


        head.appendChild(makeLink(rel, href1, title1));
        head.appendChild(makeLink(rel, href2, title2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks(rel);
        assertEquals(2, links.length);
        assertEquals(href1, links[0].getHref());
        assertEquals(title1, links[0].getTitle());
        assertNull(links[0].getName());
        assertEquals(href2, links[1].getHref());
        assertEquals(title2, links[1].getTitle());
        assertNull(links[1].getName());
    }


    @Test
    public void testGetLinksWithMultipleMatchingAnchorAndLinkTags() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();


        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeLink(rel, href2, title2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks(rel);
        assertEquals(2, links.length);
        //TODO: probably shoudl change this to arrayContainingInAnyOrder
        assertEquals(href1, links[0].getHref());
        assertEquals(title1, links[0].getTitle());
        assertEquals(name1, links[0].getName());
        assertEquals(href2, links[1].getHref());
        assertEquals(title2, links[1].getTitle());
        assertNull(links[1].getName());
    }

    @Test
    public void testGetLinksEncodedAsAnchorWithSingleResourceAndName() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks(rel, name2);
        assertEquals(1, links.length);
        assertEquals(href2, links[0].getHref());
        assertEquals(title2, links[0].getTitle());
    }


    @Test
    public void testGetLinksEncodedAsAnchorAndLinkWithSingleResourceAndName() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeLink(rel, href1, title1));
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        Html5Resource resource = new Html5Resource(doc);
        HyperLink[] links = resource.getLinks(rel, name2);
        assertEquals(1, links.length);
        assertEquals(href2, links[0].getHref());
        assertEquals(title2, links[0].getTitle());
    }





    //TOOD: HOW DO YOU RETRIEVE A COMPLEX PROPERTY?

    @Test(expected = HyperResourceException.class)
    public void testGetLinkMissingNode() {
        new Html5Resource(doc).getLink("bb:promotions");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkNullPath() {
        new Html5Resource(doc).getLink(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkEmptyRelationship() {

        new Html5Resource(doc).getLink("");
    }




    @Test(expected = HyperResourceException.class)
    public void testGetPathAsMissingNode() {
        new Html5Resource(doc).getPathAs(String.class, "_embedded", "promotionResourceList", "title");
    }

    @Test(expected = HyperResourceException.class)
    public void testGetPathAsNullPath() {
        new Html5Resource(doc).getPathAs(String.class, null);
    }



    @Test
    public void testEquals(){
        //two hal json resources are equal if their underlying json is equal
        Html5Resource resource1 = new Html5Resource(doc);
        Html5Resource resource2 = new Html5Resource(doc);

        assertEquals(resource1, resource2);
        Document otherDoc = Jsoup.parse("<html><span>other node</span></html>");
        Html5Resource resource3 = new Html5Resource(otherDoc);

        assertNotEquals(resource1, resource3);

    }


    @Test
    public void testHasLink() {

        String rel = "bb:promotions";

        //test it before the link is there
        Html5Resource resource = new Html5Resource(doc);
        assertFalse(resource.hasLink(rel));


        //Now add as anchor
        body.appendChild(makeAnchor(rel, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        resource = new Html5Resource(doc);
        assertTrue(resource.hasLink(rel));

        //now clear it out
        body.html("");
        resource = new Html5Resource(doc);
        assertFalse(resource.hasLink(rel));

        //now add link
        body.appendChild(makeLink(rel, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        resource = new Html5Resource(doc);
        assertTrue(resource.hasLink(rel));
    }


    @Test
    public void testHasLinkWithName() {
        String rel = "bb:promotions";
        String name = UUID.randomUUID().toString();

        //test it before the link is there
        Html5Resource resource = new Html5Resource(doc);
        assertFalse(resource.hasLink(rel, name));

        //Now put it in there as anchor
        body.appendChild(makeAnchor(rel, UUID.randomUUID().toString(), UUID.randomUUID().toString(), name));
        resource = new Html5Resource(doc);
        assertTrue(resource.hasLink(rel, name));

    }


    @Test
    public void testHasPath() {

        Element dataSection = body.appendElement("section");
        dataSection.addClass("data");

        Element spanNode = dataSection.appendElement("span");
        spanNode.attr("name", "errorMessage");

        String message = UUID.randomUUID().toString();
        spanNode.text(message);

        Html5Resource resource = new Html5Resource(doc);

        assertTrue(resource.hasPath("errorMessage"));

        assertFalse(resource.hasPath(UUID.randomUUID().toString()));

        assertFalse(resource.hasPath("errorMessage", UUID.randomUUID().toString()));

        assertFalse(resource.hasPath(UUID.randomUUID().toString(), "errorMessage"));
    }


    @Test
    public void testGetPathAs() {

        Element dataSection = body.appendElement("section");
        dataSection.addClass("data");

        Element spanNode = dataSection.appendElement("span");
        spanNode.attr("name", "errorMessage");

        String message = UUID.randomUUID().toString();
        spanNode.text(message);

        Html5Resource resource = new Html5Resource(doc);

        assertEquals(message, resource.getPathAs(String.class, "errorMessage"));
    }


    @Test
    public void testCanResolveLinkLocal() {

        Html5Resource resource = new Html5Resource(doc);

        assertFalse("always false, embedded not currently supported", resource.canResolveLinkLocal("rel"));

    }


    @Test(expected = HyperResourceException.class)
    public void testResolveLinkLocal() {


        Html5Resource resource = new Html5Resource(doc);
        resource.resolveLinkLocal("*");
    }

    @Test(expected = HyperResourceException.class)
    public void testResolveLinksLocal() {

        Html5Resource resource = new Html5Resource(doc);
        resource.resolveLinksLocal("*");
    }

    @Test
    public void testIsLinkMultiEncodedAsAnchor() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeAnchor(rel, href1, title1, name1));

        Html5Resource resource = new Html5Resource(doc);
        assertFalse(resource.isMultiLink(rel));

        //add a second
        head.appendChild(makeAnchor(rel, href2, title2, name2));

        resource = new Html5Resource(doc);
        assertTrue(resource.isMultiLink(rel));

    }


    @Test
    public void testIsLinkMultiEncodedAsLink() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();

        head.appendChild(makeLink(rel, href1, title1));

        Html5Resource resource = new Html5Resource(doc);
        assertFalse(resource.isMultiLink(rel));

        //add a second
        head.appendChild(makeLink(rel, href2, title2));

        resource = new Html5Resource(doc);
        assertTrue(resource.isMultiLink(rel));

    }


    @Test
    public void testIsLinkMultiEncodedAsLinkAndAnchor() {
        String rel = "bb:promotions";
        String href1 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/1";
        String href2 = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions/2";
        String title1 = "a title 1";
        String title2 = "a title 2";

        head.appendChild(makeLink(rel, href1, title1));

        Html5Resource resource = new Html5Resource(doc);
        assertFalse(resource.isMultiLink(rel));

        //add a second
        body.appendChild(makeAnchor(rel, href2, title2));

        resource = new Html5Resource(doc);
        assertTrue(resource.isMultiLink(rel));

    }





    @Test
    public void testGetProfilesWithArrayOfOneProfileEncodedAsLink(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions";

        head.appendChild(makeLink(relationship, linkHref1));

        Html5Resource resource = new Html5Resource(doc);
        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1), actual);

    }

    @Test
    public void testGetProfilesWithArrayOfOneProfileEncodedAsAnchor(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions";

        body.appendChild(makeAnchor(relationship, linkHref1));

        Html5Resource resource = new Html5Resource(doc);
        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1), actual);

    }

    @Test
    public void testGetProfilesWithArrayOfTwoProfileEncodedAsLink(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions-subtype";
        String linkHref2 = "http://host/profiles/promotions";

        head.appendChild(makeLink(relationship, linkHref1));
        head.appendChild(makeLink(relationship, linkHref2));

        Html5Resource resource = new Html5Resource(doc);


        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1, linkHref2), actual);


    }


    @Test
    public void testGetProfilesWithArrayOfTwoProfileEncodedAsAnchor(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions-subtype";
        String linkHref2 = "http://host/profiles/promotions";

        body.appendChild(makeAnchor(relationship, linkHref1));
        body.appendChild(makeAnchor(relationship, linkHref2));

        Html5Resource resource = new Html5Resource(doc);


        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1, linkHref2), actual);


    }


    @Test
    public void testGetProfilesWithArrayOfTwoProfileEncodedAsAnchorAndLink(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions-subtype";
        String linkHref2 = "http://host/profiles/promotions";

        head.appendChild(makeLink(relationship, linkHref1));
        body.appendChild(makeAnchor(relationship, linkHref2));

        Html5Resource resource = new Html5Resource(doc);


        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1, linkHref2), actual);


    }

    @Test
    public void testHasForm(){
        String formName = UUID.randomUUID().toString();
        body.appendChild(makeForm(formName));

        Html5Resource resource = new Html5Resource(doc);
        assertTrue(resource.hasForm(formName));

        assertFalse(resource.hasForm(UUID.randomUUID().toString()));

    }



    @Test(expected = HyperfitException.class)
    public void testVerifyGetFormNoMatchesThrows(){
        Html5Resource resource = new Html5Resource(doc);

        resource.getForm(UUID.randomUUID().toString());
    }


    @Test(expected = HyperfitException.class)
    public void testVerifyGetFormMultipleMatchesThrows(){
        Html5Resource resource = new Html5Resource(doc);

        String formName = UUID.randomUUID().toString();
        body.appendChild(makeForm(formName));
        body.appendChild(makeForm(formName));

        resource.getForm(UUID.randomUUID().toString());
    }

    @Test
    public void testGetForm(){
        String formName = UUID.randomUUID().toString();
        Element formElement = makeForm(formName);
        body.appendChild(formElement);

        Html5Resource resource = new Html5Resource(doc);

        Form actual = resource.getForm(formName);
        JsoupHtml5Form expected = new JsoupHtml5Form(formElement);
        assertEquals(expected, actual);

    }

    @Test
    public void testGetForms(){
        String form1Name = UUID.randomUUID().toString();
        Element form1Element = makeForm(form1Name);
        body.appendChild(form1Element);

        String form2Name = UUID.randomUUID().toString();
        Element form2Element = makeForm(form2Name);
        body.appendChild(form2Element);

        Html5Resource resource = new Html5Resource(doc);

        Form[] actual = resource.getForms();
        Form[] expected = new Form[]{
            new JsoupHtml5Form(form1Element),
            new JsoupHtml5Form(form2Element)
        };
        assertThat(actual, arrayContainingInAnyOrder(expected));

    }






    @Test
    public void testGetFormsNoForms(){


        Html5Resource resource = new Html5Resource(doc);

        Form[] actual = resource.getForms();
        Form[] expected = new Form[0];
        assertArrayEquals(expected, actual);

    }
}
