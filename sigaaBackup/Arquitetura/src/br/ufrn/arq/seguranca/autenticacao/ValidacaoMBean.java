/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.seguranca.autenticacao;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import br.ufrn.arq.dao.EmissaoDocumentoAutenticadoDao;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.web.jsf.AbstractController;

/**
 * Managed bean para realizar a validação de documentos de maneira genérica.
 * 
 * @author Gleydson Lima
 * 
 */
public class ValidacaoMBean extends AbstractController {

	/** Define o caminho comum dentro dos contextos do SIGAA e SIGRH para visualização da autenticidade do documento. */
	public static final String AUTENTICO = "/public/autenticidade/autentico.jsp";
	/** Define o caminho comum dentro dos contextos do SIGAA e SIGRH para seleção do tipo de documento para autenticação. */
	public static final String SELECIONA_TIPO_DOCUMENTO = "/public/autenticidade/tipo_documento.jsp";
	/** Define o caminho comum dentro dos contextos do SIGAA e SIGRH para validação do documento. */
	public static final String INFORMACOES_DOCUMENTO = "/public/autenticidade/form.jsp";

	/** Define a emissão do documento já autenticado. */
	private EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
	
	/** Indica o texto que deve ser digitado para verficação.  */
	private String captcha;

	/** Indica o bundle. */
	private static ResourceBundle bundle;  

	/**
	 * Método não invocado por JSP's
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public AutValidator getValidador() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		if (bundle == null) {
			bundle = ResourceBundle
					.getBundle("br.ufrn.arq.seguranca.autenticacao.validadores");
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

	/**
	 * Redireciona o usuário para página de captação das informações do
	 * documento selecionado
	 * JSP: /sigaa.war/public/autenticidade/tipo_documento.jsp
	 * @return
	 */
	public String selecionarDocumento() {

		emissao = new EmissaoDocumentoAutenticado();
		emissao.setTipoDocumento(Integer
				.parseInt(getParameter("tipoDocumento")));
		emissao.setSubTipoDocumento(Integer
				.parseInt(getParameter("subTipoDocumento")));

		return forward(INFORMACOES_DOCUMENTO);
	}
	
	/**
	 * Define o tipo e subtipo para o certificado de participante do CIC
	 * e redireciona o usuário para página de captação das informações do
	 * documento selecionado.
	 * JSP: /sigaa.war/public/autenticidade/tipo_documento.jsp
	 * @return
	 */
	public String selecionarDocumentoParticipanteCIC() {
		
		emissao = new EmissaoDocumentoAutenticado();
		emissao.setTipoDocumento(TipoDocumentoAutenticado.CERTIFICADO);
		emissao.setSubTipoDocumento(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_CIC);
		
		return forward(INFORMACOES_DOCUMENTO);
	}
	
	/**
	 * Define o tipo e subtipo para o certificado de avaliador do CIC
	 * e redireciona o usuário para página de captação das informações do
	 * documento selecionado.
	 * JSP: /sigaa.war/public/autenticidade/tipo_documento.jsp
	 * @return
	 */
	public String selecionarDocumentoAvaliadorCIC() {
		
		emissao = new EmissaoDocumentoAutenticado();
		emissao.setTipoDocumento(TipoDocumentoAutenticado.CERTIFICADO);
		emissao.setSubTipoDocumento(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC);
		
		return forward(INFORMACOES_DOCUMENTO);
	}

	/**
	 * Valida dados de entrada necessárias para validação e emissão de documentos.
	 */
	private ListaMensagens validarDadosEmissao() {

		ListaMensagens lista = new ListaMensagens();

		if ((emissao.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
				|| (emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)) {
			validateRequired(emissao.getNumeroDocumento(), "Número do Documento", lista);
		}else{
			validateRequired(emissao.getIdentificador(), "Identificador", lista);
		}
		validateRequired(emissao.getDataEmissao() , "Data de Emissão", lista);
		validateRequired(emissao.getCodigoSeguranca(), "Código de Verificação", lista);
		if(!validaCaptcha(captcha))
			lista.addErro("Imagem: Valor inválido.");
							
		return lista;
	}
		
	/**
	 * Efetua as validações necessárias e redireciona de acordo com o contexto.
	 * Verifica se o documento é válido
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/autenticidade/form.jsp</li>
	 *  <li>/sigrh.war/public/autenticidade/form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws Exception
	 */
	public String validar() throws Exception {
		
		erros = validarDadosEmissao();
		
		if (hasErrors()) {
			return null;
		}
		
		EmissaoDocumentoAutenticadoDao dao = new EmissaoDocumentoAutenticadoDao();

		EmissaoDocumentoAutenticado emissaoObj = null;

		if ((emissao.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
				|| (emissao.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)) {

			emissaoObj = dao.findByEmissao(emissao.getNumeroDocumento(),
					emissao.getCodigoSeguranca(), new java.sql.Date(emissao
							.getDataEmissao().getTime()), emissao
							.getTipoDocumento(), emissao
							.getSubTipoDocumento());

		} else {
			emissaoObj = dao.findByEmissao(emissao.getIdentificador(),
					emissao.getCodigoSeguranca(), new java.sql.Date(emissao
							.getDataEmissao().getTime()), emissao
							.getTipoDocumento());
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
				return forward(AUTENTICO);
			} else {
				addMensagemErro("Documento Vencido. Foi detectado uma alteração em seu estado desde sua emissão até a data de hoje.");
				return null;
			}
		}
	} 

	/**
	 * Visualiza o documento validado
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/autenticidade/autentico.jsp</li>
	 *  <li>/sigrh.war/public/autenticidade/autentico.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String visualizarEmissao() throws Exception {

		AutValidator aut = getValidador();
		aut.exibir(emissao, getCurrentRequest(), getCurrentResponse());
		FacesContext.getCurrentInstance().responseComplete();

		return null;
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/autenticidade/form.jsp</li>
	 *  <li>/sigrh.war/public/autenticidade/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public EmissaoDocumentoAutenticado getEmissao() {
		return emissao;
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/autenticidade/form.jsp</li>
	 *  <li>/sigrh.war/public/autenticidade/form.jsp</li>
	 * </ul>
	 * @param emissao
	 */
	public void setEmissao(EmissaoDocumentoAutenticado emissao) {
		this.emissao = emissao;
	}

	/**
	 * Método não invocado por JSP's.
	 * @return
	 */
	public String getCaptcha() {
		return captcha;
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/autenticidade/form.jsp</li>
	 *  <li>/sigrh.war/public/autenticidade/form.jsp</li>
	 * </ul>
	 * @param captcha
	 */
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	/**
	 * Informa, na página de autenticação qual o tipo de documento selecionado
	 * na página de seleção de documentos
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * @return
	 */
	public String getDocumentoSelecionado() {
		String result = "";

		switch (emissao.getTipoDocumento()) {
		case TipoDocumentoAutenticado.ATESTADO:
			result = "ATESTADO";
			break;

		default:
			break;
		}
		return result;
	}
}
