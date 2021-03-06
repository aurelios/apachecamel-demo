package br.com.apachecamel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;
import org.mortbay.jetty.HttpSchemes;

public class OrderFileToHttpSoapRoute {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				errorHandler(deadLetterChannel("file:error").
						logExhaustedMessageHistory(true).
						maximumRedeliveries(3).
						redeliveryDelay(2000).
						onRedelivery(new Processor() {

							@Override
							public void process(Exchange exchange) throws Exception {
								int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
								int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
								System.out.println("Redelivery " + counter + "/" + max);
							}
						})
				);

				from("file:orders?delay=5s&noop=true").
						routeId("rota-pedidos").
						to("validator:pedido.xsd").
					multicast().
						to("direct:soap").
						to("direct:http");

				from("direct:http").
						routeId("rota-http").
						setProperty("pedidoId", xpath("/pedido/id/text()")).
						setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).
						split().
						xpath("/pedido/itens/item").
						filter().
						xpath("/item/formato[text()='EBOOK']").
						setProperty("ebookId", xpath("/item/livro/codigo/text()")).
						marshal().xmljson().
						log("Sending to HTTP - ${id}").
								setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).
						setHeader(Exchange.HTTP_QUERY,simple("ebookId=${property.ebookId}&pedidoId=${property.pedidoId}&clienteId=${property.clienteId}")).
						to("http4://localhost:8080/webservices/ebook/item");

				from("direct:soap").
						routeId("rota-soap").
						to("xslt:pedido-para-soap.xslt").
						log("Sending to SOAP - ${id}").
						setHeader(Exchange.CONTENT_TYPE,constant("text/xml")).
						to("http4://localhost:8080/webservices/financeiro");
			}

		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
