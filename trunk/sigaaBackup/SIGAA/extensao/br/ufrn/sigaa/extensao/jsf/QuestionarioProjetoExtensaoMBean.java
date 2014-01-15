package br.ufrn.sigaa.extensao.jsf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.jsf.VerTelaAvisoLogonMBean;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.questionario.PerguntaQuestionarioDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.GrupoQuestionarioExtensao;
import br.ufrn.sigaa.extensao.dominio.LinhaQuestionarioProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.QuestionarioProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;
import br.ufrn.sigaa.vestibular.dominio.LinhaQuestionarioRespostas;

@Component @Scope("request")
public class QuestionarioProjetoExtensaoMBean extends SigaaAbstractController<QuestionarioProjetoExtensao> {

	private Collection<QuestionarioProjetoExtensao> questionarios;
	/** Coleção utilizada na geração do relatório estatístico do questionário sócio econômico. */
	private Collection<LinhaQuestionarioRespostas> linhaQuestionarioRespostas = new ArrayList<LinhaQuestionarioRespostas>();
	
    public QuestionarioProjetoExtensaoMBean() {
    	clear();
    }

	private void clear() {
		obj = new QuestionarioProjetoExtensao();
		obj.setProjeto(new Projeto());
		obj.setQuestionario(new Questionario());
		obj.setTipoAtividade(new TipoAtividadeExtensao());
		questionarios = new ArrayList<QuestionarioProjetoExtensao>();
	}
	
	@Override
	public String getDirBase() {
		return "/extensao/QuestionarioProjeto";
	}

	public String listarAcoesPendentesQuestionario() throws DAOException {
		clear();
		if ( getQuestionarios().isEmpty() )
			return redirectJSF("/verMenuPrincipal.do");
		else
			return redirectJSF("/extensao/QuestionarioProjeto/notificacaoQuestionarioProjeto.jsp");
	}	
	
	public Collection<SelectItem> getAllQuestionarioExtensao() throws ArqException {
		QuestionarioDao dao = getDAO(QuestionarioDao.class);
		try {
			Collection<Questionario> questionarios = 
						dao.findByTipo(TipoQuestionario.QUESTIONARIO_ACAO_EXTENSAO);

			ArrayList<SelectItem> combo = new ArrayList<SelectItem>();
			for (Questionario quest : questionarios) 
				combo.add(new SelectItem(quest.getId(), quest.getTitulo()));
			return combo;
			
		} finally {
			dao.close();
		}
	}

	public Collection<SelectItem> getAllEditaisExtensao() throws ArqException {
		EditalDao dao = getDAO(EditalDao.class);
		try {
			Collection<Edital> editais = 
						dao.findAllAtivosByTipo(Edital.EXTENSAO);
			ArrayList<SelectItem> combo = new ArrayList<SelectItem>();
			for (Edital edit : editais) 
				combo.add(new SelectItem(edit.getId(), edit.getDescricao()));
			return combo;
			
		} finally {
			dao.close();
		}
	}

	public Collection<SelectItem> getAllGrupoQuestionarioExtensao() throws ArqException {
		Collection<GrupoQuestionarioExtensao> gruposQuestionario = 
					getGenericDAO().findAll(GrupoQuestionarioExtensao.class);

		ArrayList<SelectItem> combo = new ArrayList<SelectItem>();
		for (GrupoQuestionarioExtensao grupoQuest : gruposQuestionario) 
			combo.add(new SelectItem(grupoQuest.getId(), grupoQuest.getDescricao()));
		return combo;
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		prepareMovimento(SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO);
		setOperacaoAtiva(SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO.getId());
		return forward(getFormPage());
	}
	
	public String listar() throws ArqException {
		clear();
		QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class);
		try {
			setQuestionarios(dao.carregarPerguntaSelecao());
			setOperacaoAtiva(SigaaListaComando.REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO.getId());
			prepareMovimento(SigaaListaComando.REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO);
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}

	@Override
	public String remover() throws ArqException {
		obj.getQuestionario().setId(getParameterInt("idQuestionario", 0));
		obj.setTipoGrupo(getParameterInt("tipoGrupo", 0));
		obj.setTipoAtividade(new TipoAtividadeExtensao(getParameterInt("idTipoAtividade", 0)));
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO);
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		return listar();
	}
	
	public String salvar() throws NegocioException, ArqException {
		if ( !isOperacaoAtiva(SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO.getId()) ) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA);
			return redirectJSF(getSubSistema().getLink());
		}
		
		erros.addAll(obj.validate());
		if ( hasErrors() )
			return null;
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO);
			
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return redirectJSF(getSubSistema().getLink());
	}

	public String iniciarRespostaQuestionario() throws DAOException {
		clear();
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), QuestionarioProjetoExtensao.class));
		QuestionarioRespostasMBean mBnea = getMBean("questionarioRespostasBean");
		mBnea.inicializar(obj.getQuestionario());
		return forward(getDirBase() + "/preenchimento_questionario.jsp");
	}
	
	public String cadastrarRespostas() throws NegocioException, ArqException {
		try {
			prepareMovimento(SigaaListaComando.RESPONDER_QUESTIONARIO_PROJETO);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.RESPONDER_QUESTIONARIO_PROJETO);
			
			QuestionarioRespostasMBean mBnea = getMBean("questionarioRespostasBean");
			mBnea.getRespostasModel();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjAuxiliar(mBnea.getObj());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		return listarAcoesPendentesQuestionario();
	}
	
	public void getAllQuestionariosPessoa() throws DAOException {
		QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class);
		try {
			setQuestionarios(dao.findQuestionarioByPessoa(getUsuarioLogado().getPessoa()));
		} finally {
			dao.close();
		}
	}

	public String exportarRespostas() throws DAOException, IOException {
		obj.getQuestionario().setId(getParameterInt("idQuestionario", 0));
		obj.setTipoGrupo(getParameterInt("tipoGrupo", 0));
		obj.setTipoAtividade(new TipoAtividadeExtensao(getParameterInt("idTipoAtividade", 0)));
		QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class);
		try {
			List<LinhaQuestionarioProjetoExtensao> respostasQuestionario = 
					dao.exportarRespostaQuestionario(obj.getQuestionario().getId(), obj.getTipoGrupo(), obj.getTipoAtividade().getId()); 
			
			if ( respostasQuestionario.size() == 0 ) {
				addMensagemErro("O Questionário ainda não foi respondido.");
				return null;
			}
			
			HttpServletResponse res = getCurrentResponse();
			PrintWriter out = res.getWriter();
			
			res.setContentType("text/csv");
			res.setCharacterEncoding("iso-8859-15");
			res.setHeader("Content-disposition", "attachment; filename=\"respostasQuestionario.csv\"");
			
			out.print( respostasQuestionario.get(0).cabecalho() );
			out.println();
			for (LinhaQuestionarioProjetoExtensao linhaQuestionarioProjetoExtensao : respostasQuestionario) {
				out.print( linhaQuestionarioProjetoExtensao.getTituloProjeto() + ";" + linhaQuestionarioProjetoExtensao.respostas() );
				out.println();
			}
			
			FacesContext.getCurrentInstance().responseComplete();
			
		} finally{
			dao.close();
		}
		
		return null;
	}
	
	public String verEstatistica() throws DAOException {
		int idQuestionario = getParameterInt("id", 0);
		int idTipoAtividade = getParameterInt("idTipoAtividade", 0);
		QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class);
		PerguntaQuestionarioDao perguntaDao = getDAO(PerguntaQuestionarioDao.class);
		try {
			obj.setQuestionario( dao.findByPrimaryKey(idQuestionario, Questionario.class) );
			if ( obj.getQuestionario() == null ) {
				addMensagemWarning("Processo Seletivo não encontrado.");
				return null;
			}
	        
			Collection<Integer> tipoQuestao = new ArrayList<Integer>();  
			Collection<PerguntaQuestionario> perguntaQuestionario = perguntaDao.findAllPerguntasQuestionario(obj.getQuestionario()); 
			linhaQuestionarioRespostas.clear();
			for (PerguntaQuestionario pq : perguntaQuestionario) {
				if (!tipoQuestao.contains(pq.getTipo())) {
					linhaQuestionarioRespostas.addAll(dao.findByEstatisticaQuestionarioSocioEconomico(obj.getQuestionario(), pq.getTipo(), idTipoAtividade));
					if (pq.getTipo() == PerguntaQuestionario.UNICA_ESCOLHA 
							||pq.getTipo() == PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO) {
						
						tipoQuestao.add(PerguntaQuestionario.UNICA_ESCOLHA);
						tipoQuestao.add(PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO);
					}else{
						tipoQuestao.add(pq.getTipo());
					}
				}
			}
			if (linhaQuestionarioRespostas.size() == 0) {
				addMensagemErro("O Questionário "+obj.getQuestionario().getTitulo()+" ainda não foi respondido.");
				return null;
			}else{
				Collections.sort( (List<LinhaQuestionarioRespostas>) linhaQuestionarioRespostas, new Comparator<LinhaQuestionarioRespostas>(){
					public int compare(LinhaQuestionarioRespostas lQR1,	LinhaQuestionarioRespostas lQR2) {						
						return new Integer(lQR1.getOrdem()).compareTo(new Integer(lQR2.getOrdem())); 					
					}
				});		
			}
		} finally{
			dao.close();
			perguntaDao.close();
		}
		return forward(getDirBase() + "/rel_estat_ficha_acompanhamento.jsp");
	}
	
	public String pularQuestionarios() throws DAOException {
		for ( QuestionarioProjetoExtensao  questionario : getQuestionarios()) {
			if ( questionario.isObrigatoriedade() ) {
				addMensagemErro("Não é possível prosseguir, pois o questionário '" + questionario.getQuestionario().getTitulo() +
						"' é obrigatório para a seguinte ação '" + questionario.getProjeto().getTitulo() + "'.");
			}
		}
		if ( hasErrors() )
			return null;
		else
			return redirect("/verMenuPrincipal.do");
	}
	
	public String mudarObrigatoriedade() throws ArqException {
		QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class);
		try {
			obj.getQuestionario().setId(getParameterInt("idQuestionario", 0));
			obj.setTipoGrupo(getParameterInt("tipoGrupo", 0));
			obj.setTipoAtividade(new TipoAtividadeExtensao(getParameterInt("idTipoAtividade", 0)));
			obj.setObrigatoriedade( getParameterBoolean("obrigatoriedade"));
			dao.mudarObrigatoriedadeQuestionario(obj);
		} finally {
			dao.close();
		}
		return listar();
	}
	
	public Collection<QuestionarioProjetoExtensao> getQuestionarios() throws DAOException {
		if ( questionarios  == null || questionarios.isEmpty() )
			getAllQuestionariosPessoa();
		return questionarios;
	}

	public void setQuestionarios(Collection<QuestionarioProjetoExtensao> questionarios) {
		this.questionarios = questionarios;
	}

	public Collection<LinhaQuestionarioRespostas> getLinhaQuestionarioRespostas() {
		return linhaQuestionarioRespostas;
	}

	public void setLinhaQuestionarioRespostas(
			Collection<LinhaQuestionarioRespostas> linhaQuestionarioRespostas) {
		this.linhaQuestionarioRespostas = linhaQuestionarioRespostas;
	}
	
}