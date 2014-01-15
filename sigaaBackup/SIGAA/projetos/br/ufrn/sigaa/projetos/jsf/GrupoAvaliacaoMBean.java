package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.projetos.GrupoAvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.PerguntaAvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.GrupoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.PerguntaAvaliacao;
/**
 * 
 * @author geyson 
 * MBean respons�vel pelo gerenciamento dos grupos e perguntas de avalia��es.
 */
@Component("grupoAvaliacao")
@Scope("request")
public class GrupoAvaliacaoMBean extends SigaaAbstractController<GrupoAvaliacao> {

	/** Cole��o de grupos de avalia��o */
	private Collection<GrupoAvaliacao> grupos = new ArrayList<GrupoAvaliacao>();
	
	/** Cole��o de perguntas de avalia��o */
	private Collection<PerguntaAvaliacao> perguntas = new ArrayList<PerguntaAvaliacao>();
	
	/** Pergunta de avaliacao */
	private PerguntaAvaliacao pergunta;
	
	/** controla o redirecionamento do botao cancelar */ 
	public boolean volta;
	
	/** Construtor */
	public GrupoAvaliacaoMBean(){
		obj = new GrupoAvaliacao();
		pergunta = new PerguntaAvaliacao();
	}
	
	/**
	 * Inicia busca por grupos de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  <li>sigaa.war/projetos/Avaliacoes/Grupo/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarBuscaGrupos() throws DAOException{
		setGrupos(getGenericDAO().findAllAtivos(GrupoAvaliacao.class, "descricao"));
		return forward("/projetos/Avaliacoes/Grupo/lista.jsp");
	}
	
	/**
	 * Inicia busca por perguntas de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  <li>sigaa.war/projetos/Avaliacoes/Pergunta/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarBuscaPerguntas() throws DAOException{	    
		
	    setPerguntas(getGenericDAO().findAllAtivos(PerguntaAvaliacao.class, "descricao"));
		
		return forward("/projetos/Avaliacoes/Pergunta/lista.jsp");
	}
	
	/**
	 * Redireciona para tela de cadastro de pergunta
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li><li>sigaa.war/projetos/Avaliacoes/Pergunta/lista.jsp</li></li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroPergunta() throws ArqException{
		checkChangeRole();
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		pergunta = new PerguntaAvaliacao();
		
		if(getCurrentURL().contains("/sigaa/portais/docente/docente"))
			volta = false;
    	else
    		volta = true;
		
		setConfirmButton("Cadastrar");
		return forward("/projetos/Avaliacoes/Pergunta/form.jsp");
	}
	
	/**
	 * Redireciona para tela de cadastro de grupo
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li><li>sigaa.war/projetos/Avaliacoes/Grupo/lista.jsp</li></li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarGrupo() throws ArqException, NegocioException {
		checkChangeRole();
		this.obj = new GrupoAvaliacao();
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		if(!getCurrentURL().contains("lista"))
			volta = false;
    	else
    		volta = true;
		
		setConfirmButton("Cadastrar");
		return forward("/projetos/Avaliacoes/Grupo/form.jsp");	
	}
	
	/**
	 * Redireciona para tela de altera��o de grupo selecionado
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Grupo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarGrupo() throws ArqException {
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);

		this.obj.setId(getParameterInt("id"));
		setReadOnly(false);
		GrupoAvaliacaoDao dao = getDAO(GrupoAvaliacaoDao.class);
		this.obj =  dao.findByPrimaryKey(obj.getId(), GrupoAvaliacao.class);
		
		//verifica se grupo � usadao por algum question�rio. caso sim a altera��o � interrompida.
		if(dao.findGrupoExistenteQuestionario(this.obj) != null){
			addMensagemErro("O grupo n�o pode ser alterada, pois est� sendo usado em um question�rio de avalia��o.");
			removeOperacaoAtiva();
			return iniciarBuscaGrupos();
		}
		
		setConfirmButton("Alterar");
		return forward("/projetos/Avaliacoes/Grupo/form.jsp");	
	}

	/**
	 * Altera grupo de avalia��o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Grupo/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String alterarGrupo() throws DAOException, SegurancaException{
		checkChangeRole();
		if(obj.getDescricao().isEmpty()){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
			return null;
		}
		if(getGenericDAO().findAtivosByExactField(GrupoAvaliacao.class, "descricao", this.obj.getDescricao()).size() > 0){
			addMensagemErro("J� existe um grupo com esta descri��o.");
			return null;
		}
		this.obj.setAtivo(true);
		PersistDB obj = this.obj;
		if (obj.getId() != 0) {
			
		
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			try {
				execute(mov);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}
		
			removeOperacaoAtiva();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return iniciarBuscaGrupos();
			
		}
		return null;
	}
	
	/**
	 * Redireciona para tela de altera��o de pergunta selecionada
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Pergunta/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarPergunta() throws ArqException{
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);

		this.pergunta.setId(getParameterInt("id"));
		setReadOnly(false);
		PerguntaAvaliacaoDao dao = getDAO(PerguntaAvaliacaoDao.class);
		this.pergunta =  dao.findByPrimaryKey(pergunta.getId(), PerguntaAvaliacao.class);
		//verifica se pergunta � usada por algum question�rio. caso sim a altera��o � interrompida.
		if(dao.findPerguntaExistenteQuestionario(pergunta) != null){
			addMensagemErro("A pergunta n�o pode ser alterada, pois est� sendo usada em um question�rio de avalia��o.");
			removeOperacaoAtiva();
			return iniciarBuscaPerguntas();
		}
			
		
		setConfirmButton("Alterar");
		return forward("/projetos/Avaliacoes/Pergunta/form.jsp");	
	}
	
	/**
	 * Altera pergunta selecionada
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Pergunta/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String alterarPergunta() throws SegurancaException, DAOException{
		checkChangeRole();
		if(pergunta.getDescricao().isEmpty()){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
			return null;
		}
		if(getGenericDAO().findAtivosByExactField(PerguntaAvaliacao.class, "descricao", pergunta.getDescricao()).size() > 0){
			addMensagemErro("J� existe uma pergunta com esta descri��o.");
			return null;
		}
		PersistDB obj = this.pergunta;
		if (obj.getId() != 0) {
			
		
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			try {
				execute(mov);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}
		
			removeOperacaoAtiva();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return iniciarBuscaPerguntas();
			
		}
		return null;
	}
	
	/**
	 * Cadastra grupo de avalia��o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Grupo/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarGrupo() throws ArqException, NegocioException{

		checkChangeRole();
		if(obj.getDescricao().isEmpty()){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
			return null;
		}
		if(getGenericDAO().findAtivosByExactField(GrupoAvaliacao.class, "descricao", this.obj.getDescricao()).size() > 0){
			addMensagemErro("J� existe um grupo com esta descri��o.");
			return null;
		}
		PersistDB obj = this.obj;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {

			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			try {
				execute(mov);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}


			removeOperacaoAtiva();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return preCadastrarGrupo();
		}
		return null;
	}
	
	/**
	 * SelectItem de grupos de avalia��es ativos 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Grupo/form.jsp</li>
	 * </ul>
	 * 
	 */
	public Collection<SelectItem> getAllComboGrupo(){
		try
		{
			return toSelectItems(getGenericDAO().findAllAtivos(GrupoAvaliacao.class, "descricao"), "id", "descricao");
		}
		catch(DAOException e) { notifyError(e); }
		return null;
	}
	
	/**
	 * Inativa pergunta selecionada
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Pergunta/lista.jsp</li>
	 * </ul>
	 *
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativarPergunta() throws ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		prepareMovimento(ArqListaComando.DESATIVAR);
		
		this.pergunta.setId(getParameterInt("id"));
		setReadOnly(false);

		PerguntaAvaliacaoDao dao = getDAO(PerguntaAvaliacaoDao.class);
		this.pergunta = dao.findByPrimaryKey(pergunta.getId(), PerguntaAvaliacao.class);

		if(dao.findPerguntaExistenteQuestionario(pergunta) != null){
			addMensagemErro("A pergunta n�o pode ser Removida, pois est� sendo usada em um question�rio de avalia��o.");
			removeOperacaoAtiva();
			return iniciarBuscaPerguntas();
		}
		
		PersistDB obj = pergunta;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		setId();

		if (obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getFormPage());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

		
			removeOperacaoAtiva();
			
			return iniciarBuscaPerguntas();

		}

	}
	
	/**
	 * Inativa grupo selecionado
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Grupo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String inativarGrupo() throws ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		prepareMovimento(ArqListaComando.DESATIVAR);
		
		this.obj.setId(getParameterInt("id"));
		setReadOnly(false);
		
		GrupoAvaliacaoDao dao = getDAO(GrupoAvaliacaoDao.class);
		this.obj =  dao.findByPrimaryKey(this.obj.getId(), GrupoAvaliacao.class);

		if(dao.findGrupoExistenteQuestionario(this.obj) != null){
			addMensagemErro("O grupo n�o pode ser Removido, pois est� sendo usado em um question�rio de avalia��o.");
			removeOperacaoAtiva();
			return iniciarBuscaGrupos();
		}
		
		PersistDB obj = this.obj;
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		setId();

		if (obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getFormPage());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

		
			removeOperacaoAtiva();
			
			return iniciarBuscaGrupos();

		}

	}
	
	/**
	 * Cadastra pergunta de avalia��o
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/Pergunta/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarPergunta() throws ArqException{
		checkChangeRole();

		if(pergunta.getDescricao() == null || pergunta.getDescricao().isEmpty() ){
			addMensagem("Descri��o", MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
			return null;
		}
		if(getGenericDAO().findAtivosByExactField(PerguntaAvaliacao.class, "descricao", pergunta.getDescricao()).size() > 0){
			addMensagemErro("J� existe uma pergunta com esta descri��o.");
			return null;
		}
		PersistDB obj = pergunta;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		if (pergunta.getId() == 0) {

			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			try {
				execute(mov);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}


			removeOperacaoAtiva();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return iniciarCadastroPergunta();
		}
		
		return null;
	}
	
	/**
	 * Combo para grupo de avalia��es
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(GrupoAvaliacao.class, "id", "descricao");
	}
	
	
	public String cancelarGrupo() throws SegurancaException {
		if(volta){
			try {
				volta = false;
				return iniciarBuscaGrupos();
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		else
			return super.cancelar();

		return null;
	}
	
	public String cancelarPergunta() throws SegurancaException {
		if(volta){
			try {
				volta = false;
				return iniciarBuscaPerguntas();
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		else
			return super.cancelar();

		return null;
	}

	public void setGrupos(Collection<GrupoAvaliacao> grupos) {
		this.grupos = grupos;
	}


	public Collection<PerguntaAvaliacao> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(Collection<PerguntaAvaliacao> perguntas) {
		this.perguntas = perguntas;
	}

	public PerguntaAvaliacao getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaAvaliacao pergunta) {
		this.pergunta = pergunta;
	}

	public Collection<GrupoAvaliacao> getGrupos() {
		return grupos;
	}

	public boolean isVolta() {
		return volta;
	}

	public void setVolta(boolean volta) {
		this.volta = volta;
	}



	
	
}
