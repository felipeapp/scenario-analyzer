/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 12/05/2011
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoNormalizacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoSolicitacaoDocumento;

/**
 * MBean respons�vel pelos atendimento e solicita��o de normaliza��o de obra realizada por discentes ou servidores � biblioteca.
 * 
 * @author Felipe Rivas
 */
@Component("solicitacaoNormalizacaoMBean")
@Scope("request")
public class SolicitacaoNormalizacaoMBean extends AbstractSolicitacaoServicoDocumentoMBean<SolicitacaoNormalizacao> {

	/** P�gina onde o usu�rio faz uma nova solicita��o de normaliza��o */
	public static final String PAGINA_NOVA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoNormalizacao.jsp";
	
	/** P�gina onde o usu�rio pode visualizar os dados da sua solicita��o */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoNormalizacao.jsp";
	
	/** O comprovante de solicita��o de normaliza��o. */
	public static final String PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoNormalizacao.jsp";
	
	/** P�gina onde o bibliotec�rio pode visualizar os dados da sua solicita��o */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoNormalizacaoAtendimento.jsp";
	
	public SolicitacaoNormalizacaoMBean() throws DAOException {
		super();
	}

	@Override
	public TipoServicoInformacaoReferencia getTipoServico() {
		return TipoServicoInformacaoReferencia.NORMALIZACAO;
	}

	@Override
	protected void preencherDados() {
		
	}

	@Override
	protected void sincronizarDados(GenericDAO dao) throws DAOException {
		obj.setTipoDocumento(dao.refresh(obj.getTipoDocumento()));
	}

	@Override
	protected Comando getMovimentoCadastrar() {
		return SigaaListaComando.CADASTRAR_SOLICITACAO_NORMALIZACAO;
	}

	@Override
	protected Comando getMovimentoAlterar() {
		return SigaaListaComando.ALTERAR_SOLICITACAO_NORMALIZACAO;
	}

	/*@Override
	protected Comando getMovimentoValidar() {
		return SigaaListaComando.VALIDAR_SOLICITACAO_NORMALIZACAO;
	}*/

	@Override
	protected Comando getMovimentoAtender() {
		return SigaaListaComando.ATENDER_SOLICITACAO_NORMALIZACAO;
	}

	@Override
	protected Comando getMovimentoCancelar() {
		return SigaaListaComando.CANCELAR_SOLICITACAO_NORMALIZACAO;
	}

	@Override
	protected MovimentoSolicitacaoDocumento instanciarMovimento() {
		return new MovimentoSolicitacaoDocumento();
	}

	@Override
	protected void prepararVisualizar() throws ArqException {
		
	}

	@Override
	protected void prepararAtender() {

	}

	@Override
	protected void confirmarAtender(MovimentoCadastro mov) throws NegocioException {
		
	}

	@Override
	public String getPropriedadeServico() {
		return "realizaNormalizacao";
	}

	@Override
	protected void inicializarDados() {
		obj.setTipoDocumento(new TipoDocumentoNormalizacaoCatalogacao());
	}

	@Override
	protected String telaNovaSolicitacaoServico() {
		return forward(PAGINA_NOVA_SOLICITACAO);
	}

	@Override
	protected String telaVisualizarSolicitacao() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO);
	}

	@Override
	protected String telaVisualizarDadosSolicitacaoAtendimento() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO);
	}

	@Override
	protected String telaVisualizaComprovante() {
		return forward(PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO);
	}
	
}