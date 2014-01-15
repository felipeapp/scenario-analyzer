/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/07/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.helper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

/**
 * <p>Classe que contém as ações comuns e auxiliares para emissão de certificados de extenção.</p>
 * 
 * @author jadson
 */
public class CertificadoExtensaoHelper {
	
	/** Atributo utilizado para representar o nome do Certificado */
	public static final String CERTIFICADO = "trf10188_CertificadoProex";
	
	
	
	/**
	 * Método que preenche e gera o PDF dos certificados de extensão.
	 *
	 * @param parametros
	 * @param codigoSeguranca
	 * @param textoCertificado
	 * @param numeroDocumento
	 * @param nomeCoordenador
	 * @param dataEmissaoCertificado
	 * @param currentResponse
	 * @param nomeCertificado
	 * @throws JRException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void emitirCertificado(String codigoSeguranca, String textoCertificado
			, String numeroDocumento, String nomeCoordenador, Date dataEmissaoCertificado,
			String nomeCertificado, HttpServletResponse currentResponse) throws JRException, IOException{
		
		Connection con = null;
		HashMap<Object, Object> parametros = new HashMap<Object, Object>();
		
		String cidade = RepositorioDadosInstitucionais.get("cidadeInstituicao");
		String proReitorExtensao = ParametroHelper.getInstance().getParametro(ParametrosExtensao.NOME_PRO_REITOR_EXTENSAO);
		String universidade = RepositorioDadosInstitucionais.get("nomeInstituicao").toUpperCase();
		String proReitoriaExtensao =  "PRÓ-REITORIA DE EXTENSÃO"; // fixo por enquanto, nenhuma univeridade até o momento usa outra nome
		
		String cominhoLogoTipoInstituicao = RepositorioDadosInstitucionais.get("logoInstituicao");
		
		try{
			// Preencher declaração
			con = Database.getInstance().getSigaaConnection();
		
			parametros.put("universidade", universidade);
			parametros.put("pro_reitoria", proReitoriaExtensao); 
			
			// setando parâmetros
			parametros.put("codigoSeguranca", codigoSeguranca);
			parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
	
			parametros.put("cidade", cidade+", "+ Formatador.getInstance().formatarDataDiaMesAno(dataEmissaoCertificado));
			parametros.put("texto", textoCertificado);
			parametros.put("pro_reitor", proReitorExtensao);
			parametros.put("coordenacao", nomeCoordenador);
			parametros.put("numero_documento", numeroDocumento);
	
			
			parametros.put("caminhoLogoTipoInstituicao",  cominhoLogoTipoInstituicao);
			
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(CERTIFICADO + ".jasper"), parametros, con);
	
			currentResponse.setContentType("application/pdf");
			currentResponse.addHeader("Content-Disposition", "attachment; filename=CERTIFICADO_PROEX_"+nomeCertificado+".pdf");
			JasperExportManager.exportReportToPdfStream(prt, currentResponse.getOutputStream());
			FacesContext.getCurrentInstance().responseComplete();
		
		}finally{
			Database.getInstance().close(con);
		}
	}
	

}
