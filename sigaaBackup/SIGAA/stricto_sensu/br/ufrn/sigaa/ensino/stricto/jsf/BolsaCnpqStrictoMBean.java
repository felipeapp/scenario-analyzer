/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.BolsistaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * ManagedBean para gerenciamento de bolsas do CNPq de Stricto Sensu.
 * Tais bolsas s�o cadastradas diretamente na base de dados dos sistemas administrativos.
 * 
 * @author wendell
 *
 */
@Component("bolsaCnpqStrictoBean") @Scope("request")
public class BolsaCnpqStrictoMBean extends SigaaAbstractController<Bolsista> implements OperadorDiscente {

	/** Armazena todos os bolsistas CNPq de uma unidade. */
	private Collection<Bolsista> bolsistas;
	
	public BolsaCnpqStrictoMBean() {
		clear();
	}

	/**
	 * M�todo que inicia o cadastro ou altera��o das bolsas do CNPq.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastro() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_BOLSA_CNPQ_STRICTO);
		
		BolsistaDao dao = getDAO(BolsistaDao.class);
		try{
		if (obj.getIdBolsa() != 0) {
			obj = dao.findByPrimaryKey(obj.getIdBolsa());
			obj.setDiscente( getDAO(DiscenteDao.class).findByMatricula( obj.getDiscente().getMatricula() ) );
			return forward(getFormPage());
		} else {
			BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRO_BOLSA_STRICTO);
			
			return buscaDiscenteMBean.popular();
		}
		}
		finally{
			dao.close();
		}
		
	}

	/** 
	 * M�todo que popula a lista de Bolsistas cadastrados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		clear();
		popularBolsistasCadastrados();
		if (isEmpty(bolsistas)) {
			addMensagemWarning("Ainda n�o h� bolsistas CNPq cadastrados.");
		}
		return forward(getListPage());
	}

	/**
	 * M�todo que popula a lista dos bolsistas cadastrados
	 * 
	 * @throws DAOException
	 */
	private void popularBolsistasCadastrados() throws DAOException {
		BolsistaDao dao = getDAO(BolsistaDao.class);
		try{
			bolsistas = dao.findByUnidade(getProgramaStricto(), Bolsista.BOLSA_CNPQ);
		}
		finally{
			dao.close();
		}
		
	}

	/**
	 * Efetua o cadastro ou altera��o das bolsas CNPq.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		obj.setUnidade(getProgramaStricto());

		// Registrar bolsa
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_BOLSA_CNPQ_STRICTO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens( ne.getListaMensagens() );
			return forward(getFormPage());
		}

		addMensagemInformation("Dados do bolsista " + getDiscente().getNome() + " registrados com sucesso!");
		return listar();
	}

	/**
	 * M�todo que limpa os dados do ManagedBean para um novo cadastro
	 */
	private void clear() {
		obj = new Bolsista();
	}

	/**
	 * M�todo que registra a finaliza��o de uma bolsa do CNPq
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String finalizar() throws ArqException {
		//obj = getBolsistaDao().findByPrimaryKey(obj.getIdBolsa());
		BolsistaDao dao = getDAO(BolsistaDao.class);
		obj = dao.findByPrimaryKey(obj.getIdBolsa());
		prepareMovimento(SigaaListaComando.FINALIZAR_BOLSA_CNPQ_STRICTO);

		// Finalizar bolsa
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_BOLSA_CNPQ_STRICTO);

		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens( ne.getListaMensagens() );
			return null;
		}
		finally{
			dao.close();
		}
		addMensagemInformation("O registro da finaliza��o da bolsa do discente " + getDiscente().getNome() + " foi realizada com sucesso!");
		return listar();
	}

	/**
	 * M�todo que retorna o DAO necess�rio para o registro e consultas de bolsistas do CNPq
	 * 
	 * @return
	 */
	private BolsistaDao getBolsistaDao() {
		return getDAO(BolsistaDao.class);
	}

	public Collection<Bolsista> getBolsistas() {
		return bolsistas;
	}

	public void setBolsistas(Collection<Bolsista> bolsistas) {
		this.bolsistas = bolsistas;
	}

	@Override
	public String getFormPage() {
		return "/stricto/bolsa_cnpq/form.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/stricto/bolsa_cnpq/lista.jsp";
	}
	
	/**
	 * M�todo da interface OperadorDiscente a ser chamado ap�s a sele��o do discente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 *
	 * @return
	 */
	public String selecionaDiscente() throws ArqException {
		// Validar exist�ncia de bolsa ativa para o discente selecionado
		boolean bolsistaJaCadastrado = getBolsistaDao().hasAtivoByDiscente(obj.getDiscente());
		if (bolsistaJaCadastrado) {
			addMensagemErro("J� existe um registro ativo de bolsa para o discente informado");
			return null;
		}

		return forward(getFormPage());
	}

	private Discente getDiscente() {
		return obj.getDiscente();
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscente(discente.getDiscente());
	}
}