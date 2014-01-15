/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/11/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;


import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;

/**
 * Controlador responsável por emitir certificados de participação para alunos
 * que apresentaram resumos no Congresso de Iniciação Científica.
 * 
 * @author Leonardo Campos
 *
 */
@Component("certificadoCIC")
@Scope("session")
public class CertificadoCICMBean extends SigaaAbstractController<Object> implements AutValidator {

	private ResumoCongresso resumo;
	
	private boolean verificando = false;
	private EmissaoDocumentoAutenticado comprovante;
	
	public CertificadoCICMBean() {
		
	}
	
	/**
	 * Esse método tem como função a emissão do relatório do certificado CIC.
	 * <br><br>
	 * JSP: Não é invocado por JSP;
	 * Esse método também é invocado pelo método exibir()
	 * @return
	 * @throws Exception
	 */
	public String emitirCertificado() throws Exception {
		
		// Preparar dados para a geração do certificado
		ArrayList<ResumoCongresso> resumos = new ArrayList<ResumoCongresso>();
		resumos.add(resumo);
		
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		// gerando código de autenticação...
		if (!verificando) {

			comprovante = geraEmissao(
					TipoDocumentoAutenticado.CERTIFICADO,
					((Integer) resumo.getId()).toString(),
					gerarSementeCertificado(), null,SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_CIC, true);
		}
		
		parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
		parametros.put("data_certificado", Formatador.getInstance().formatarDataDiaMesAno( comprovante.getDataEmissao() ) );
		parametros.put("numero_documento", comprovante.getNumeroDocumento());

		// Gerar certificado
		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("certificado_cic.jasper"),
				parametros,
				new JRBeanCollectionDataSource(resumos));

		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=certificado_autor.pdf");
		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
		
		FacesContext.getCurrentInstance().responseComplete();
		
		return null;
			
	}

	public ResumoCongresso getResumo() {
		return resumo;
	}

	public void setResumo(ResumoCongresso resumo) {
		this.resumo = resumo;
	}
	
	/**
	 * Gerar semente para validação do documento
	 * <br><br>
	 * JSP: Não é invocado por JSP
	 * Esse método é invocado pelos métodos: emitirCertificado() e validaDigest
	 * @return
	 */
	private String gerarSementeCertificado() {
		StringBuilder builder = new StringBuilder();

		builder.append(resumo.getId());
		builder.append(resumo.getAutor().getId());
		builder.append(resumo.getOrientador().getId());
		builder.append(TipoDocumentoAutenticado.CERTIFICADO);

		return builder.toString();
	}
	
	/**
	 * Esse método tem como função exibir o certificado do CIC para o usuário.
	 * <br><br>
	 * JSP: não invocado por jsp;
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		try {
			this.comprovante = comprovante;
			verificando = true;
			resumo = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), ResumoCongresso.class);
			emitirCertificado();
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/**
	 * Esse método tem como função verificar se o certificado válido ou não.
	 * <br><br>
	 * JSP: não invocado por jsp;
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {		
		try {
			resumo = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), ResumoCongresso.class);
			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeCertificado());
			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}
}