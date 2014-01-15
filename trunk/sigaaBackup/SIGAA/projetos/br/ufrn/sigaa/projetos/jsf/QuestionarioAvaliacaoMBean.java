/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/06/2010
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.QuestionarioAvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.GrupoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.ItemAvaliacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.PerguntaAvaliacao;
import br.ufrn.sigaa.projetos.dominio.QuestionarioAvaliacao;

/**
 * MBean respos�vel pelo gerenciamento de avalia��es.
 * 
 * @author Geyson Karlos
 *
 */
@Component("questionarioAvaliacao")
@Scope("session")
public class QuestionarioAvaliacaoMBean extends SigaaAbstractController<QuestionarioAvaliacao> {

	/** Lista de question�rios. */
	public List<QuestionarioAvaliacao> questionarios;
	/** Lista de itens de avalia��o de projeto. */
	public List<ItemAvaliacaoProjeto> itensAux;
	/** Lista de perguntas. */
	public List<PerguntaAvaliacao> perguntas;
	
	/** Refer�ncia ao elemento HTML da tabela superior da view. */
    private HtmlDataTable itensUp;
    /** Refer�ncia ao elemento HTML da tabela inferior da view. */
    private HtmlDataTable itensDown;
    
    /** controla o redirecionamento do botao cancelar */ 
	public boolean volta;
	
	
	/** Construtor padr�o*/
	public QuestionarioAvaliacaoMBean(){
		this.obj = new QuestionarioAvaliacao(); 
		
	}
	
	/** limpa dados no mbean */
	private void clear(){
		questionarios = new ArrayList<QuestionarioAvaliacao>();
		perguntas = new ArrayList<PerguntaAvaliacao>();
		itensUp = new HtmlDataTable();
		itensDown = new HtmlDataTable();
		this.obj = new QuestionarioAvaliacao();
	}
	
	/**
	 * Redireciona para p�gina com lista de avalia��es.
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>"sigaa.war\portais\docente\menu_docente.jsp"</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarBuscaQuestionarios() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		clearSessionWEB();
		clear();
		questionarios = (List<QuestionarioAvaliacao>) getGenericDAO().findAllAtivos(QuestionarioAvaliacao.class, "descricao");
		return forward("/projetos/Avaliacoes/Questionario/lista.jsp");
	}
	
	/**
	 * Redireciona para tela de altera��o do question�rio
     * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		itensAux = new ArrayList<ItemAvaliacaoProjeto>();
		
		setOperacaoAtiva(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS.getId());
		prepareMovimento(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS);

		QuestionarioAvaliacaoDao dao = getDAO(QuestionarioAvaliacaoDao.class);
		setId();
		PersistDB obj = this.obj;
		setReadOnly(false);

		this.obj = (QuestionarioAvaliacao) dao.findByPrimaryKey(obj.getId(), obj.getClass());
		
		// Verifica se foi utilizado o bot�o voltar do navegador ap�s o question�rio ter sido removido
		if(!this.obj.isAtivo()){
			addMensagemErro("Selecione um question�rio v�lido."); 
			return null;
		}
		
		this.obj.setItensAvaliacao(dao.findAtivosByExactField(ItemAvaliacaoProjeto.class, "questionario.id", this.obj.getId()));
		
		if(dao.findQuestionarioExistente(this.obj) != null){
			addMensagemErro("O Question�rio n�o pode ser alterado, pois est� sendo usado em um modelo de avalia��o ativo.");
			removeOperacaoAtiva();
			return iniciarBuscaQuestionarios();
		}
		
		perguntas = (List<PerguntaAvaliacao>) getGenericDAO().findAllAtivos(PerguntaAvaliacao.class, "descricao");
		
		for (ItemAvaliacaoProjeto it : this.obj.getItensAvaliacao()) {
			if(perguntas.contains(it.getPergunta()))
				perguntas.remove(it.getPergunta());
		}
		
		for (PerguntaAvaliacao per : perguntas) {
			ItemAvaliacaoProjeto itemAvaliacao = new ItemAvaliacaoProjeto();
			itemAvaliacao.setPergunta(per);
			itemAvaliacao.setGrupo(new GrupoAvaliacao());
			itensAux.add(itemAvaliacao);
		}
		
		setConfirmButton("Alterar");
		return forward("/projetos/Avaliacoes/Questionario/form.jsf");
	}
	
	/**
	 * Remove question�rio
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		QuestionarioAvaliacaoDao dao = getDAO(QuestionarioAvaliacaoDao.class);
		setId();
		PersistDB obj = this.obj;
		setReadOnly(false);

		this.obj = (QuestionarioAvaliacao) dao.findByPrimaryKey(obj.getId(), obj.getClass());
		
		// Verifica se foi utilizado o bot�o voltar do navegador ap�s o question�rio ter sido removido
		if(!this.obj.isAtivo()){
			addMensagemErro("Selecione um question�rio v�lido."); 
			return null;
		}
		
		this.obj.setItensAvaliacao(dao.findAtivosByExactField(ItemAvaliacaoProjeto.class, "questionario.id", this.obj.getId()));
		setOperacaoAtiva(SigaaListaComando.REMOVER_QUESTIONARIO_ITENS.getId());
		prepareMovimento(SigaaListaComando.REMOVER_QUESTIONARIO_ITENS);
		
		if(dao.findQuestionarioExistente(this.obj) != null){
			addMensagemErro("O Question�rio n�o pode ser removido, pois est� sendo usado em um modelo de avalia��o ativo.");
			removeOperacaoAtiva();
			return iniciarBuscaQuestionarios();
		}
		
		if (hasErrors()) {
			addMensagens(erros);
		}
		else{
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(this.obj);
			if (obj != null) {
				mov.setCodMovimento(SigaaListaComando.REMOVER_QUESTIONARIO_ITENS);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
				removeOperacaoAtiva();
				return iniciarBuscaQuestionarios();
			}
		}
		return null;
	}
	
	/**
	 * Cadastra e Altera question�rio
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		checkChangeRole();
		ValidatorUtil.validateRequired(this.obj.getDescricao(), "T�tulo", erros);
		if(this.obj.getItensAvaliacao() == null || this.obj.getItensAvaliacao().size() <= 0)
			addMensagemErro("Selecione os itens para o question�rio.");
		
		// Verifica se no cadastro ou altera��o do question�rio, est� se inserindo um question�rio com mesma descri��o
		if(getGenericDAO().findAtivosByExactField(QuestionarioAvaliacao.class, "descricao", this.obj.getDescricao()).size() > 0){
			addMensagemErro("J� existe um question�rio com esta descri��o.");
		}
		if (hasErrors()) {
			addMensagens(erros);
			return null;
		}else{
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(this.obj);
			if (obj != null) {
				
				mov.setCodMovimento(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
				removeOperacaoAtiva();
				return iniciarBuscaQuestionarios();
			}
		}
		return iniciarBuscaQuestionarios();
	}
	
	/**
	 * Redireciona para p�gina para cadastro de avalia��o.
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroQuestionario() throws ArqException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		clearSessionWEB();
		clear();
		itensAux = new ArrayList<ItemAvaliacaoProjeto>();
		setOperacaoAtiva(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS.getId());
		prepareMovimento(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS);
		perguntas = (List<PerguntaAvaliacao>) getGenericDAO().findAllAtivos(PerguntaAvaliacao.class, "descricao");
		for (PerguntaAvaliacao per : perguntas) {
			ItemAvaliacaoProjeto itemAvaliacao = new ItemAvaliacaoProjeto();
			itemAvaliacao.setPergunta(per);
			itemAvaliacao.setGrupo(new GrupoAvaliacao());
			itensAux.add(itemAvaliacao);
		}
		
		if(getCurrentURL().contains("/sigaa/portais/docente/docente"))
			volta = false;
    	else
    		volta = true;
		
		setConfirmButton("Cadastrar");
		return forward("/projetos/Avaliacoes/Questionario/form.jsp");
		
	}
	
	
	/**
	 * Adiciona item ao question�rio
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String adicionarItemAvaliacao() throws DAOException,  SegurancaException {

		ItemAvaliacaoProjeto item = (ItemAvaliacaoProjeto) itensUp.getRowData();
		
		ValidatorUtil.validateRequired(item.getGrupo(), "Grupo", erros);
		ValidatorUtil.validaDoublePositivo(item.getPeso(), "Peso", erros);
		ValidatorUtil.validaDoublePositivo(item.getNotaMaxima(), "Nota M�xima", erros);
		
		if(!hasErrors()){

			getGenericDAO().initialize(item.getGrupo());
			item.setQuestionario(this.obj);
			this.obj.getItensAvaliacao().add(item);
			item.setQuestionario(obj);
			itensAux.remove(item);
			return null;
		}
		else{
			addMensagens(erros);
			return null;
		}


	}
	
	/**
	 * Remove item ao question�rio
	 * <br />
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String removeItem() throws DAOException{
		ItemAvaliacaoProjeto item = (ItemAvaliacaoProjeto) itensDown.getRowData();
		obj.getItensAvaliacao().remove(item);
		itensAux.add(item);
		return null;
	}
	
	/**
	 * Cancela a opera��o atual.
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String cancelarQuestionario() throws SegurancaException {
		if(getConfirmButton().equals("Alterar")){
			try {
				volta = false;
				return iniciarBuscaQuestionarios();
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		else
			return super.cancelar();

		return null;
	}
	
	/**
	 * Carrega as informa��es do question�rio e encaminha para a tela de resumo.
	 * M�todo Chamado por:
	 * <ul>
	 *  <li>sigaa.war/projetos/Avaliacoes/Questionario/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewQuestionario() throws DAOException{
		itensAux = new ArrayList<ItemAvaliacaoProjeto>();
		
		QuestionarioAvaliacaoDao dao = getDAO(QuestionarioAvaliacaoDao.class);
		setId();
		PersistDB obj = this.obj;

		this.obj = (QuestionarioAvaliacao) dao.findByPrimaryKey(obj.getId(), obj.getClass());
		this.obj.setItensAvaliacao(dao.findAtivosByExactField(ItemAvaliacaoProjeto.class, "questionario.id", this.obj.getId()));
		
		perguntas = (List<PerguntaAvaliacao>) getGenericDAO().findAllAtivos(PerguntaAvaliacao.class, "descricao");
		
		for (ItemAvaliacaoProjeto it : this.obj.getItensAvaliacao()) {
			if(perguntas.contains(it.getPergunta()))
				perguntas.remove(it.getPergunta());
		}
		
		for (PerguntaAvaliacao per : perguntas) {
			ItemAvaliacaoProjeto itemAvaliacao = new ItemAvaliacaoProjeto();
			itemAvaliacao.setPergunta(per);
			itemAvaliacao.setGrupo(new GrupoAvaliacao());
			itensAux.add(itemAvaliacao);
		}
		
		return forward("/projetos/Avaliacoes/Questionario/view.jsp");
	}
	
	public List<PerguntaAvaliacao> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaAvaliacao> perguntas) {
		this.perguntas = perguntas;
	}


	public Collection<QuestionarioAvaliacao> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(List<QuestionarioAvaliacao> questionarios) {
		this.questionarios = questionarios;
	}

	public List<ItemAvaliacaoProjeto> getItensAux() {
		return itensAux;
	}

	public void setItensAux(List<ItemAvaliacaoProjeto> itensAux) {
		this.itensAux = itensAux;
	}

	public HtmlDataTable getItensUp() {
		return itensUp;
	}

	public void setItensUp(HtmlDataTable itensUp) {
		this.itensUp = itensUp;
	}

	public HtmlDataTable getItensDown() {
		return itensDown;
	}

	public void setItensDown(HtmlDataTable itensDown) {
		this.itensDown = itensDown;
	}

	public boolean isVolta() {
		return volta;
	}

	public void setVolta(boolean volta) {
		this.volta = volta;
	}

	
}
