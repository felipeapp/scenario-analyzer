/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/01/2011
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * MBean respons�vel por emitir a declara��o de prazo m�ximo de integraliza��o de curr�culo
 * 
 * @author arlindo
 *
 */
@Component("declaracaoPrazoMaximoMBean") @Scope("request")
public class DeclaracaoPrazoMaximoMBean extends SigaaAbstractController<Curriculo> implements AutValidator {
	
	/** Descri��o do departamento */
	private String descricaoDepartamento;
	/** Descri��o da Reitoria da Gradua��o */
	private String descricaoReitoriaGraduacao;
	
	/** Guarda o c�digo que vai autenticar o documento do SIGAA. */
	private String codigoSeguranca;
	
	/** Mant�m uma c�pia do comprovante para evitar gerar mais de uma vez se o usu�rio ficar atualizando a p�gina. */
	private EmissaoDocumentoAutenticado comprovante;		
	
	@Override
	public String getViewPage() {
		return "/graduacao/curriculo/declaracao_prazo_maximo.jsp";
	}
	
	/**
	 * Inicia a gera��o da declara��o de prazo m�ximo de integraliza��o de curr�culo
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/curriculo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		int id = getParameterInt("id", 0);

		if (id > 0){
			obj = getGenericDAO().findByPrimaryKey(id, Curriculo.class);		
			if (!ValidatorUtil.isEmpty(obj)){
				carregarDados();
				
				codigoSeguranca = null;				
				gerarCodigoSeguranca();						
				
				return forward(getViewPage());
			}
		}
		
		return null;
		
	}
	
	/**
	 * Carrega os dados para exibi��o da declara��o
	 */
	private void carregarDados(){
		descricaoDepartamento = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR);
		descricaoReitoriaGraduacao = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO);	
		
		/* *************************************************************************************
		 *  Seguran�a inserida para caso o usu�rio tente acessar a p�gina do documento diretamente.
		 * *************************************************************************************/
		getCurrentRequest().setAttribute("liberaEmissao", true);	
		getCurrentRequest().setAttribute("declaracaoPrazoMaximoMBean", this);				
	}
	
	/**
	 *   Gera o C�digo de Seguran�a para a Declara��o
	 */
	private void gerarCodigoSeguranca() throws ArqException{		
		// S� gera outro c�digo de seguran�a e, consequentemente, outro documento se ele j�
		// n�o foi emitido antes, para evitar gera��es desnecess�rias de c�digo de seguran�a.		
		if (codigoSeguranca == null  ){			
			try {										
				comprovante = geraEmissao(
						TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR,
						String.valueOf( getSementeDocumento() ),  // identificador
						null,
						String.valueOf( obj.getId() ), // informa��es complementares - id do curriculo
						SubTipoDocumentoAutenticado.DECLARACAO_PRAZO_MAXIMO_INTEGRALIZACAO_CURRICULO,
						false
				);		
				codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		}
	}		

	public String getDescricaoDepartamento() {
		return descricaoDepartamento;
	}

	public void setDescricaoDepartamento(String descricaoDepartamento) {
		this.descricaoDepartamento = descricaoDepartamento;
	}

	public String getDescricaoReitoriaGraduacao() {
		return descricaoReitoriaGraduacao;
	}

	public void setDescricaoReitoriaGraduacao(String descricaoReitoriaGraduacao) {
		this.descricaoReitoriaGraduacao = descricaoReitoriaGraduacao;
	}

	/**
	 * Exibe o documento validado
	 * <br/><br/>
	 * M�todo n�o chamado por JSPs.
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		try {
			if (ValidatorUtil.isEmpty(comprovante.getDadosAuxiliares()))
				return;
			
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), Curriculo.class);			
			
			if (!isEmpty(obj)){						
				/*  Pega o c�digo de seguran�a do comprovante para reexibir ao usu�rio */
				codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;	
				this.comprovante = comprovante;
				/* *************************************************************************************
				 *   IMPORTANTE TEM QUE REDIRECIONAR PARA A P�GINA DO RELAT�RIO CONFORME O SEU SUBTIPO
				 * *************************************************************************************/
				carregarDados();
				
				getCurrentRequest().getRequestDispatcher(getViewPage()).forward(getCurrentRequest(), getCurrentResponse());					
			}
		}catch (Exception  e) {
			e.printStackTrace();
			tratamentoErroPadrao(e);			
		}		
	}

	/**
	 * Valida o documento emitido
	 * <br/><br/>
	 * M�todo n�o chamado por JSPs.
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		try {						
			if (ValidatorUtil.isEmpty(comprovante.getDadosAuxiliares()))
				return false;
			
			obj = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante.getDadosAuxiliares()), Curriculo.class);
		
			if (isEmpty(obj))
				return false;
			
			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, null);

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;							
			
		}catch (DAOException daoExt) {
			daoExt.printStackTrace();
			return false;
		} catch (ArqException ae) {
			ae.printStackTrace();
			return false;
		}			
		return false;
	}
	
	/**
	 * Retorna a semente do documento.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/curriculo/declaracao_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getSementeDocumento(){
		return (String.valueOf(obj.getId())+String.valueOf(obj.getSemestreConclusaoMaximo())); // semente
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}	
}
