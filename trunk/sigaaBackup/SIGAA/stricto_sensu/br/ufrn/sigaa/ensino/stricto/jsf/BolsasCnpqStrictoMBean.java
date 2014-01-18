package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.BolsistaSigaaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Bolsas;
import br.ufrn.sigaa.ensino.dominio.Bolsistas;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.TipoBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;


@Component("bolsasCnpqStrictoMBean") @Scope("session")
public class BolsasCnpqStrictoMBean extends SigaaAbstractController<Bolsistas> implements OperadorDiscente {

	
	/** Armazena todos os bolsistas CNPq de uma unidade. */
	private Collection<Bolsistas> bolsistas;
	
	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscente(discente.getDiscente());		
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		boolean bolsistaJaCadastrado = getBolsistaSigaaDao().hasAtivoByDiscente(obj.getDiscente());
		if (bolsistaJaCadastrado) {
			addMensagemErro("Já existe um registro ativo de bolsa para o discente informado");
			return null;
		}

		obj.setCurso(obj.getDiscente().getCurso());
		return forward(getFormPage());
	}
	
	private BolsistaSigaaDao getBolsistaSigaaDao() {
		return getDAO(BolsistaSigaaDao.class);
	}
	
	/** 
	 * Método que popula a lista de Bolsistas cadastrados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
			addMensagemWarning("Ainda não há bolsistas CNPq cadastrados.");
		}
		return forward(getListPage());
	}
	
	/**
	 * Método que limpa os dados do ManagedBean para um novo cadastro
	 */
	private void clear() {
		obj = new Bolsistas();
	}
	
	/**
	 * Método que popula a lista dos bolsistas cadastrados
	 * 
	 * @throws DAOException
	 */
	private void popularBolsistasCadastrados() throws DAOException {
		BolsistaSigaaDao dao = getDAO(BolsistaSigaaDao.class);
		try{
			bolsistas = dao.findByUnidade(getProgramaStricto(), Bolsistas.BOLSA_CNPQ);
		}
		finally{
			dao.close();
		}
		
	}
	
	/**
	 * Método que inicia o cadastro ou alteração das bolsas do CNPq.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/lista_bolsista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastro() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_BOLSA_CNPQ_STRICTO);
		
		BolsistaSigaaDao dao = getDAO(BolsistaSigaaDao.class);
		try{
		if (!isEmpty(obj.getBolsa())) {
			
			Bolsas bolsa = dao.findByPrimaryKey(obj.getBolsa().getId(),Bolsas.class);
			obj = bolsa.getBolsista();
			obj.setBolsa(bolsa);
			return forward(getFormPage());
		} else {
			obj.setBolsa(new Bolsas());
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
	 * Efetua o cadastro ou alteração das bolsas CNPq.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/form_bolsista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		obj.getBolsa().setUnidade(getProgramaStricto());
		obj.getBolsa().setUnidadeTrabalho(getProgramaStricto());
		obj.getBolsa().setTipoBolsa(new TipoBolsas());
		obj.getBolsa().getTipoBolsa().setId(TipoBolsas.BOLSA_CNPQ);

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

		addMensagemInformation("Dados do bolsista " + obj.getDiscente().getNome() + " registrados com sucesso!");
		return listar();
	}
	
	/**
	 * Método que registra a finalização de uma bolsa do CNPq
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/stricto/bolsa_cnpq/lista_bolsista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String finalizar() throws ArqException {
		//obj = getBolsistaDao().findByPrimaryKey(obj.getIdBolsa());
		BolsistaSigaaDao dao = getDAO(BolsistaSigaaDao.class);		
		Bolsas bolsa = dao.findByPrimaryKey(obj.getBolsa().getId(),Bolsas.class);
		obj = bolsa.getBolsista();
		obj.setBolsa(bolsa);
		
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
		addMensagemInformation("O registro da finalização da bolsa do discente " + obj.getDiscente().getNome() + " foi realizada com sucesso!");
		return listar();
	}

	
	@Override
	public String getFormPage() {
		return "/stricto/bolsa_cnpq/form_bolsista.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/stricto/bolsa_cnpq/lista_bolsista.jsp";
	}

	public Collection<Bolsistas> getBolsistas() {
		return bolsistas;
	}

	public void setBolsistas(Collection<Bolsistas> bolsistas) {
		this.bolsistas = bolsistas;
	}
	
	
	

}
