package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.EmissaoDocumentoAutenticadoDao;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

@Component("autenticacaoDocumentoTouch") @Scope("request")
public class AutenticacaoDocumentoTouchMBean extends SigaaTouchAbstractController {
	
	/** Define o caminho para a validação do documento. */
	public static final String  JSP_INFORMACOES_DOCUMENTO = "/mobile/touch/public/autenticar_documento.jsp";

	/** Caminho para a página de um documento validado. */
	private static final String JSP_DOCUMENTO_VALIDO = "/mobile/touch/public/documento_autentico.jsp";
	
	private EmissaoDocumentoAutenticado emissao;
	
	/** Indica o texto que deve ser digitado para verficação.  */
	private String captcha;

	private ResourceBundle bundle;

	public AutenticacaoDocumentoTouchMBean(){
		init();
	}
	
	private void init() {
		emissao = new EmissaoDocumentoAutenticado();
	}

	public String iniciarAutenticacao() {
		return forward ("/mobile/touch/public/tipo_documento.jsf");
	}
	
	public String selecionarDocumento() {
		emissao = new EmissaoDocumentoAutenticado();
		
		emissao.setTipoDocumento(Integer.parseInt(getParameter("tipoDocumento")));
		emissao.setSubTipoDocumento(Integer.parseInt(getParameter("subTipoDocumento")));

		return forward(JSP_INFORMACOES_DOCUMENTO);
	}
	
	public String selecionarDocumentoParticipanteCIC() {
		emissao = new EmissaoDocumentoAutenticado();
		emissao.setTipoDocumento(TipoDocumentoAutenticado.CERTIFICADO);
		emissao.setSubTipoDocumento(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_CIC);
		
		return forward(JSP_INFORMACOES_DOCUMENTO);
	}
	
	public String selecionarDocumentoAvaliadorCIC() {
		emissao = new EmissaoDocumentoAutenticado();
		emissao.setTipoDocumento(TipoDocumentoAutenticado.CERTIFICADO);
		emissao.setSubTipoDocumento(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC);
		
		return forward(JSP_INFORMACOES_DOCUMENTO);
	}
	
	public String validar() throws Exception {
		erros = new ListaMensagens();
		
		if(emissao.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO 
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
			if(isEmpty(emissao.getNumeroDocumento())) {
				erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número do Documento");
			}
		} else if(isEmpty(emissao.getIdentificador())) {
			erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Identificador");
		}
		
		if(isEmpty(emissao.getDataEmissao())) {
			erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Emissão");
		}
		
		if(isEmpty(emissao.getCodigoSeguranca())) {
			erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código de Verificação");
		}
		
		if (isEmpty(captcha))
			erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Imagem");
		else { 
			if(!validaCaptcha(captcha)) {
				erros.addErro("O código da imagem não confere.");
				captcha = null;
			}
		}
		
		if (hasErrors()){
			return null;
		}
		
		EmissaoDocumentoAutenticadoDao dao = new EmissaoDocumentoAutenticadoDao();

		EmissaoDocumentoAutenticado emissaoObj = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(emissao.getDataEmissao());
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    
	    Date dataEmissao = cal.getTime();

		if (isTipoDocumentoComNumero()) {
			emissaoObj = dao.findByEmissao(emissao.getNumeroDocumento(), emissao.getCodigoSeguranca(), 
					new java.sql.Date(dataEmissao.getTime()), emissao.getTipoDocumento(), 
					emissao.getSubTipoDocumento());
		} else {
			emissaoObj = dao.findByEmissao(emissao.getIdentificador(), emissao.getCodigoSeguranca(), 
					new java.sql.Date(dataEmissao.getTime()), emissao.getTipoDocumento());
		}

		if (emissaoObj == null) {
				addMensagemErro("Esta emissão não foi encontrada no sistema e portanto não foi validada.");
				return null;
		} else {
			AutValidator aut = getValidador();
			boolean aindaValido = aut.validaDigest(emissaoObj); // verifica
			// se o documento ainda esta va'lido
			if (aindaValido) {
				emissao = emissaoObj;
				return forward(JSP_DOCUMENTO_VALIDO);
			} else {
				addMensagemErro("Documento Vencido. Foi detectado uma alteração em seu estado desde sua emissão até a data de hoje.");
				return null;
			}
		}
	}
	
	public String visualizarEmissao() throws Exception {
		AutValidator aut = getValidador();
		
		aut.exibir(emissao, getCurrentRequest(), getCurrentResponse());
		
		FacesContext.getCurrentInstance().responseComplete();

		return null;
	}
	
	public AutValidator getValidador() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle("br.ufrn.arq.seguranca.autenticacao.validadores");
//			Usar código abaixo após atualização da trf 85845
//			bundle = new ValidadoresResourceBundle();
		}
		
		// Tenta pegar pelo subtipo
		String classValidator = null;
		try{
			classValidator = bundle.getString(emissao.getSubTipoDocumento()
					+ "");
		} catch (java.util.MissingResourceException e) {
			// Se não existir, procura pelo tipo
			classValidator = bundle.getString(emissao.getTipoDocumento() + "");
		}
		
		AutValidator aut = (AutValidator) Class.forName(classValidator)
				.newInstance();
		return aut;
	}
	
	public boolean isTipoDocumentoComIdentificador() {
		return emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.HISTORICO
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.ATESTADO
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_QUITACAO_BIBLIOTECA
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.TERMO_PUBLICACAO_TESE_DISSERTACAO
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.HISTORICO_MEDIO;
	}
	
	public boolean isTipoDocumentoComNumero() {
		return emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO
				|| emissao.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO;
	}

	public EmissaoDocumentoAutenticado getEmissao() {
		return emissao;
	}

	public void setEmissao(EmissaoDocumentoAutenticado emissao) {
		this.emissao = emissao;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

}
