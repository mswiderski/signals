package org.jbpm.test.executor;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.jms.FireAndForgetResponseHandler;


public class SignalKieServerCommand implements Command, Reoccurring {

    public ExecutionResults execute(CommandContext arg0) throws Exception {
                
        InitialContext ctx = new InitialContext();
        
        ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/JmsXA");
        Queue requestQueue = (Queue) ctx.lookup("queue/KIE.SERVER.REQUEST");
        Queue responseQueue = (Queue) ctx.lookup("queue/KIE.SERVER.RESPONSE");
        
        KieServicesConfiguration configuration = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue, "maciek", "maciek@pwd1");
        
        configuration.setMarshallingFormat(MarshallingFormat.XSTREAM);
        configuration.setCapabilities(Arrays.asList(new String[]{"BPM"}));
        configuration.setTimeout(1000000);
        KieServicesClient kieServicesClient =  KieServicesFactory.newKieServicesClient(configuration);
        
        ProcessServicesClient processClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
        processClient.setResponseHandler(new FireAndForgetResponseHandler());
      
        processClient.signal("test-project_1.0.0", "check", null);
        
        return new ExecutionResults();
    }

    public Date getScheduleTime() {
        long fiveMinutes = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        return new Date(System.currentTimeMillis() + fiveMinutes);
    }

}
