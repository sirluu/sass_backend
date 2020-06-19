package jp.co.japantaxi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class CustomHeaderFilter implements Filter {

  private final Logger log = LoggerFactory.getLogger(CustomHeaderFilter.class);
  
  public CustomHeaderFilter() {
    log.info("CORSFilter init");
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void destroy() {}
 
  @Value("${domain.allowed}")
  private String domainAllowed;
  
  @Autowired
  Properties properties; 
  
  // List of domains allowed to access the server
  private static final String[] flowers = { "https://companies-integrations.dev-partner.japantaxi.jp" };
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    // Make sure that we are working with HTTP (that is, against HttpServletRequest and
    // HttpServletResponse objects)
    if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
	List<String> allowedOrigins = new ArrayList<>(Arrays.asList(flowers));
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;
      
      // Access-Control-Allow-Origin
      String origin = request.getHeader("Origin");
      if(origin != null && !origin.isEmpty()) {
    	  allowedOrigins.add(origin); // always right.
      }
      if(domainAllowed != null && !domainAllowed.isEmpty()) {
    	  allowedOrigins.add(domainAllowed);
      }
      response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
      response.setHeader("Vary", "Origin");
      // Access-Control-Max-Age
      response.setHeader("Access-Control-Max-Age", "3600");
      // Access-Control-Allow-Credentials
      response.setHeader("Access-Control-Allow-Credentials", "true");
      // Access-Control-Allow-Methods
      response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
      // Access-Control-Allow-Headers
      response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, Language, x-xsrf-token");
    }
    chain.doFilter(req, res);
  }
}
