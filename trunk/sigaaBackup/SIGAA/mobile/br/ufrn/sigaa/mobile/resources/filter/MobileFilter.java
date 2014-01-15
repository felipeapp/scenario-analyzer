package br.ufrn.sigaa.mobile.resources.filter;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import br.ufrn.arq.seguranca.log.Logger;

public class MobileFilter implements Filter {

	private int minSupportedVersion = Integer.MIN_VALUE;

	@Override
	public void init(FilterConfig arg0) throws ServletException {

		try {

			ResourceBundle res = PropertyResourceBundle.getBundle("br.ufrn.sigaa.mobile.resources.SIGAAMobile");

			minSupportedVersion = Integer.parseInt(res.getString("rest.protocol.minVersion"));

			// minSupportedVersion =
			// ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.VERSAO_MINIMA_PROTOCOLO_MOBILE);
		} catch (Exception e) {

			// Logger.error("Parâmetro VERSAO_MINIMA_PROTOCOLO_MOBILE [" +
			// ConstantesParametroGeral.VERSAO_MINIMA_PROTOCOLO_MOBILE +
			// "] não definido");
			Logger.error("Parâmetro rest.protocol.minVersion, no ResourceBundle: br.ufrn.sigaa.mobile.resources.SIGAAMobile, não definido");

		}

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String versionStr = request.getHeader("X-MobileProtocolVersion");

		int mobileVersion = 0;

		try {
			mobileVersion = Integer.parseInt(versionStr);

		} catch (Exception ex) {
			mobileVersion = -1;
		}

		if (mobileVersion >= minSupportedVersion) {

			chain.doFilter(req, res);
		} else {

			response.addHeader("X-MobileRequiredProtocolVersion", String.valueOf(minSupportedVersion));
			response.setStatus(HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED);
		}

	}

	@Override
	public void destroy() {

	}

}
