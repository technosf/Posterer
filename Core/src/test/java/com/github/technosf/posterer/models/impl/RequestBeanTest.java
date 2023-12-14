package com.github.technosf.posterer.models.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.eclipse.jdt.annotation.Nullable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.HttpHeader;
import com.github.technosf.posterer.models.Request;

public class RequestBeanTest
{
    static String ENDPOINT = "file://abc/123.prop";

    static Request REQUEST = mock(Request.class); // place holder

    RequestBean classUnderTest = new RequestBean();
    @Nullable
    RequestBean copy;


    @BeforeClass
    public void beforeClass() throws URISyntaxException
    {
        expect(REQUEST.getUri()).andStubReturn(new URI(ENDPOINT));
        expect(REQUEST.getEndpoint()).andStubReturn(ENDPOINT);
        expect(REQUEST.getPayload()).andStubReturn("payload");
        expect(REQUEST.getContentType()).andStubReturn("ContentType");
        expect(REQUEST.getMethod()).andStubReturn("method");
        expect(REQUEST.getSecurity()).andStubReturn("security");
        expect(REQUEST.getBase64()).andStubReturn(true);
        expect(REQUEST.getHeaders()).andStubReturn(new ArrayList< HttpHeader >());
        expect(REQUEST.getAuthenticate()).andStubReturn(true);
        expect(REQUEST.getUsername()).andStubReturn("");
        expect(REQUEST.getPassword()).andStubReturn("");
        replay(REQUEST);
        classUnderTest = new RequestBean(REQUEST);
    }


    @Test
    public void constructUri() throws URISyntaxException
    {
        assertEquals(RequestBean.constructUri(ENDPOINT), new URI(ENDPOINT));
    }


    @Test
    public void copy()
    {
        copy = classUnderTest.copy();
    }


    @Test(dependsOnMethods = "copy")
    public void equals()
    {
        assertEquals(classUnderTest, copy);
    }


    @Test
    public void getBase64()
    {
        assertEquals(classUnderTest.getBase64(), REQUEST.getBase64());
    }


    @Test
    public void getContentType()
    {
        assertEquals(classUnderTest.getContentType(), REQUEST.getContentType());
    }


    @Test
    public void getEndpoint()
    {
        assertEquals(classUnderTest.getEndpoint(), REQUEST.getEndpoint());
    }


    @Test
    public void getPayload()
    {
        assertEquals(classUnderTest.getPayload(), REQUEST.getPayload());
    }


    @Test
    public void getPayloadRaw()
    {
        assertEquals(classUnderTest.getPayloadRaw(), REQUEST.getPayload());
    }


    //    @Test
    //    public void getTimeout()
    //    {
    //        assertEquals(classUnderTest.getTimeout(), REQUEST.getTimeout());
    //    }

    @Test
    public void getURI()
    {
        assertEquals(classUnderTest.getUri(), REQUEST.getUri());
    }


//    @Test
//    public void hashCodeTest()
//    {
//        assertEquals(classUnderTest.hashCode(), 1539683135);
//    }


    @Test
    public void isActionable()
    {
        assertTrue(classUnderTest.isActionable());
    }


    //    @Test
    //    public void setHttpPassword()
    //    {
    //        throw new RuntimeException("Test not implemented");
    //    }
    //
    //
    //    @Test
    //    public void setMethod()
    //    {
    //        throw new RuntimeException("Test not implemented");
    //    }

//    @Test
//    public void toStringTest()
//    {
//        assertEquals(classUnderTest.toString(),
//                "1539683135\nfile://abc/123.prop\nmethod\nsecurity\nContentType\ntrue");
//    }
}
