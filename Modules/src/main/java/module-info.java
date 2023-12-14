module com.github.technosf.posterer.modules {
    exports com.github.technosf.posterer.modules;
    exports com.github.technosf.posterer.utils.ssl;

    requires com.github.technosf.posterer.core;
    requires org.eclipse.jdt.annotation;
    requires org.apache.commons.io;
    requires org.apache.commons.configuration2;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.slf4j;
    requires com.google.guice;
    requires javafx.controls;
}
