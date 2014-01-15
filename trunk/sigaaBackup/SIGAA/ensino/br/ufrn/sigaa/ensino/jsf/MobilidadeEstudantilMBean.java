/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/05/2010
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MobilidadeEstudantilDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MobilidadeEstudantil;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Managed Bean Responsável por gerenciar as operações de cadastro de Mobilidade Estudantil.
 * 
 * @author Arlindo Rodrigues 
 *
 */
@Component("mobilidadeEstudantil") @Scope("request")
public class MobilidadeEstudantilMBean extends SigaaAbstractController<MobilidadeEstudantil> implements OperadorDiscente {
	/** Comando que será executado. */
	private Comando comando;
	
	/** Identifica se é cadastro ou não. */
	private boolean cadastro;
	
	/** Discente Selecionado  */
	private DiscenteGraduacao discente;
	
	/** Lista com o histórico de movimentações do alunos selecionado */
	private List<MobilidadeEstudantil> historicoMovimentacoes = new ArrayList<MobilidadeEstudantil>();
	
	/**
	 * Construtor da Classe
	 */
	public MobilidadeEstudantilMBean() {
	}
	
	/**
	 * Inicializar os Objetos
	 */
	private void initObj(){
		
		try {		
		
			if (obj == null)
				obj = new MobilidadeEstudantil();
		
			if (isEmpty(obj.getDiscente())){
				obj.setDiscente(new DiscenteGraduacao());
			}
			
			if (isEmpty(obj.getCampusDestino())){
				obj.setCampusDestino(new CampusIes());
			}
		
			if (isEmpty(obj.getCampusOrigem())){
				obj.setCampusOrigem(new CampusIes());
			}
		
			if (isEmpty(obj.getPaisExterna())){
				obj.setPaisExterna(new Pais());
			}
			else {				
				obj.setPaisExterna(getGenericDAO().findByPrimaryKey(obj.getPaisExterna().getId(), Pais.class));				
			}	
		
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
		}	
		
	}

	/**
	 * Inicia o cadastro de Mobilidade Estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException {	
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);
		getCurrentSession().setAttribute("comando", SigaaListaComando.CADASTRAR_MOBILIDADE_ESTUDANTIL);
		cadastro = true;
		obj = new MobilidadeEstudantil();
		initObj();
		return buscarDiscente();
	}

	
	/**
	 * Inicia a alteração de Mobilidade Estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAteracao() throws ArqException {	
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);
		comando = SigaaListaComando.ALTERAR_MOBILIDADE_ESTUDANTIL;
		// setado o comando na sessão para não perder ao redirecionado para a busca de discente.
		getCurrentSession().setAttribute("comando", comando);
		prepareMovimento(comando);
		cadastro = false;
		return buscarDiscente();
	}	
	
	/**
	 * Inicia a Busca Geral de Discente
	 * Não Chamado por JSP's.
	 * @return
	 */
	private String buscarDiscente(){
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");				
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MOBILIDADE_ESTUDANTIL);
		return buscaDiscenteMBean.popular();		
	}
	
	/**
	 * Realiza o cancelamento da Mobilidade Estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cancelarMobilidade() throws SegurancaException, ArqException, NegocioException{
		comando = SigaaListaComando.CANCELAR_MOBILIDADE_ESTUDANTIL;
		// setado o comando na sessão para não perder ao redirecionado para a busca de discente.
		getCurrentSession().setAttribute("comando", comando);
		prepareMovimento(comando);		
		return cadastrar();
	}
	
	
	/**
	 * Realiza a alteração da Mobilidade Estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String alterarMobilidade() throws SegurancaException, ArqException, NegocioException{
		initObj();
		cadastro = true;
		return forward(getFormPage());
	}
	
	/**
	 * Cadastra a mobilidade estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {	
		comando = (Comando) getCurrentSession().getAttribute("comando");
		if (comando.equals(SigaaListaComando.CADASTRAR_MOBILIDADE_ESTUDANTIL) || comando.equals(SigaaListaComando.ALTERAR_MOBILIDADE_ESTUDANTIL)){
			obj.setDiscente(discente);
			
			erros.addAll(obj.validate().getMensagens());
			
			if (hasErrors() || !confirmaSenha()){
				initObj();
				return null;	
			}
		} else if (comando.equals(SigaaListaComando.CANCELAR_MOBILIDADE_ESTUDANTIL)){
			/* Seta os valores de cancelamento da Mobilidade Estudantil */
			obj.setDataCancelamento(new Date());
			obj.setRegistroCancelamento(getUsuarioLogado().getRegistroEntrada());	
			obj.setAtivo(false);
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		ArrayList<PersistDB> col = new ArrayList<PersistDB>();
		col.add(obj);
		col.add(getCalendarioVigente());
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(historicoMovimentacoes);
		mov.setColObjMovimentado(col);
		try {			
			// Seta a operação
			mov.setCodMovimento(comando);
			// Tenta executar a operação
			execute(mov, getCurrentRequest());		
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);							
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}			
		return cancelar();
	}
	
	/**
	 * Cancela a operação redirecionando para a listagem se for alteração ou cancelamento.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		if (comando.equals(SigaaListaComando.CADASTRAR_MOBILIDADE_ESTUDANTIL) || ValidatorUtil.isEmpty(historicoMovimentacoes)){
			return super.cancelar();			
		} else {
			cadastro = false;
			historicoMovimentacoes.clear();
			return forward(getListPage());				
		}
	}
	
	/**
	 * Retorna todas os tipos de modalidade estudantil para o combo.
	 * 
	 * Método chamado pela JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/form.jsp</li>
	 * </ul>
	 * 
	 * @return 
	 */
	public List<SelectItem> getAllTiposCombo() {
		List<SelectItem> tipos = new ArrayList<SelectItem>();
		tipos.add(new SelectItem(MobilidadeEstudantil.INTERNA, MobilidadeEstudantil.getDescricaoTipo(MobilidadeEstudantil.INTERNA)));
		tipos.add(new SelectItem(MobilidadeEstudantil.EXTERNA, MobilidadeEstudantil.getDescricaoTipo(MobilidadeEstudantil.EXTERNA)));
		return tipos;
	}	
	
	/**
	 * Retorna todas os Sub-Tipos de modalidade estudantil para o combo.
	 * 
	 * Método chamado pela JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/form.jsp</li>
	 * </ul>
	 * 
	 * @return 
	 */
	public List<SelectItem> getAllSubTiposCombo() {
		List<SelectItem> subTipos = new ArrayList<SelectItem>();
		if (obj.getTipo() == MobilidadeEstudantil.INTERNA){
			subTipos.add(new SelectItem(MobilidadeEstudantil.COMPULSORIA, MobilidadeEstudantil.getDescricaoSubTipo(MobilidadeEstudantil.INTERNA, MobilidadeEstudantil.COMPULSORIA)));
			subTipos.add(new SelectItem(MobilidadeEstudantil.VOLUNTARIA, MobilidadeEstudantil.getDescricaoSubTipo(MobilidadeEstudantil.INTERNA,MobilidadeEstudantil.VOLUNTARIA)));			
		} else if (obj.getTipo() == MobilidadeEstudantil.EXTERNA) {
			subTipos.add(new SelectItem(MobilidadeEstudantil.NACIONAL, MobilidadeEstudantil.getDescricaoSubTipo(MobilidadeEstudantil.EXTERNA, MobilidadeEstudantil.NACIONAL)));
			subTipos.add(new SelectItem(MobilidadeEstudantil.INTERNACIONAL, MobilidadeEstudantil.getDescricaoSubTipo(MobilidadeEstudantil.EXTERNA, MobilidadeEstudantil.INTERNACIONAL)));			
		}
		return subTipos;
	}
	/**
	 * Caminho da JSP do Formulário de cadastro de mobilidade estudantil.
	 * <br/><br/>
	 * Método não Chamado por JSP's.
	 */
	@Override
	public String getFormPage() {
		return "/ensino/mobilidade_estudantil/form.jsp";
	}
	
	/**
	 * Caminho da JSP da listagem de mobilidade estudantil.
	 * <br/><br/>
	 * Método não Chamado por JSP's.
	 */
	@Override
	public String getListPage() {
		return "/ensino/mobilidade_estudantil/lista.jsp";
	}

	/**
	 * Seleciona o discente escolhido e redireciona para a página referente a ação.
	 * Chamado a partir do BuscaDiscenteMBean
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		if (isEmpty(discente)){
			addMensagemErro("Discente não informado!");
			return null;
		}
		initObj();
		comando = (Comando) getCurrentSession().getAttribute("comando");
		prepareMovimento(comando);
		if (SigaaListaComando.CADASTRAR_MOBILIDADE_ESTUDANTIL.equals(comando)){
			cadastro = true;
			obj.setAno(getCalendarioVigente().getAno());
			obj.setPeriodo(getCalendarioVigente().getPeriodo());	
			return forward(getFormPage());
		} else return forward(getListPage());
	}

	/**
	 * Seta o discente selecionado na busca por discente.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 * Método não invocado por JSP.
	 */
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = (DiscenteGraduacao) discente;		
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	/**
	 * Retorna o histórico de movimentações de Mobilidade Estudantil.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/mobilidade_estudantil/historico.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<MobilidadeEstudantil> getHistoricoMovimentacoes() throws DAOException {
		/* Realiza a busca dos histórico de registro de mobilidade do aluno selecionado */
		if (historicoMovimentacoes.isEmpty()){
			MobilidadeEstudantilDao dao = getDAO(MobilidadeEstudantilDao.class);
			try {
				historicoMovimentacoes = dao.findByDiscente(discente, null);
			} finally {
				if (dao != null)
					dao.close();
			}						
		}
		int idDiscenteHistorico = -1;
		try{
			idDiscenteHistorico = historicoMovimentacoes.get(0).getDiscente().getId();
		}catch (Exception e) {}
		if(idDiscenteHistorico != -1 && idDiscenteHistorico != discente.getId()){
			MobilidadeEstudantilDao dao = getDAO(MobilidadeEstudantilDao.class);
			try {
				historicoMovimentacoes = dao.findByDiscente(discente, null);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		return historicoMovimentacoes;
	}

	public void setHistoricoMovimentacoes(
			List<MobilidadeEstudantil> historicoMovimentacoes) {
		this.historicoMovimentacoes = historicoMovimentacoes;
	}

	public boolean isCadastro() {
		return cadastro;
	}

	public void setCadastro(boolean cadastro) {
		this.cadastro = cadastro;
	}	
}
