/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */

package br.ufrn.sigaa.ava.questionarios.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.questionario.dao.CategoriaPerguntaQuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.CategoriaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;



/**
 * MBean que gerencia a criação, alteração e remoção das categorias de perguntas do banco de questões dos questionários da turma virtual.
 * 
 * @author Fred_Castro
 *
 */
@Component("categoriaPerguntaQuestionarioTurma")
@Scope("request")
public class CategoriaPerguntaQuestionarioTurmaMBean extends SigaaAbstractController <CategoriaPerguntaQuestionarioTurma> {

	/** Indica se é para voltar ao questionário após realizar a ação atual */
	private boolean voltarAoQuestionario;
	
	/** Indica se é para voltar ao cadastro de pergunta após cadastrar a categoria. */
	private boolean voltarAoCadastroDePergunta;
	
	/** A categoria selecionada para compartilhamento. */
	private CategoriaPerguntaQuestionarioTurma categoriaSelecionada;
	
	/** Identificador da pessoa selecionada para compartilhamento. */
	private Integer idServidor;
	
	/** Mensagem de sucesso em operações de compartilhamento. */
	private String mensagemSucessoCompartilhamento;
	
	/** Mensagem de erro em operações de compartilhamento. */
	private String mensagemErroCompartilhamento;
	
	/** Lista de categorias compartilhadas com o usuário atual. */
	private List <CategoriaPerguntaQuestionarioTurma> categoriasCompartilhadas;
	
	/** Lista de categorias com as perguntas e as alternativas das perguntas. carregadas para impressão do relatório */
	private List <CategoriaPerguntaQuestionarioTurma> listaCategoriasRelatorio;
	
	/** Lista de categorias compartilhada com as perguntas e as alternativas das perguntas. carregadas para impressão do relatório */
	private List <CategoriaPerguntaQuestionarioTurma> listaCategoriasCompartilhadasRelatorio;
	
	/**
	 * Lista de perguntas de uma categoria.
	 */
	private List <PerguntaQuestionarioTurma> perguntas = new ArrayList <PerguntaQuestionarioTurma> ();
	
	/** Construtor padrão */
	public CategoriaPerguntaQuestionarioTurmaMBean(){
		obj = new CategoriaPerguntaQuestionarioTurma();
	}
	
	/**
	 * Método que remove o objeto, verificando se o mesmo existe.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		
		populateObj(true);

		GenericDAO dao = getGenericDAO();
		
		List <PerguntaQuestionarioTurma> perguntas = (List<PerguntaQuestionarioTurma>) dao.findAtivosByExactField (PerguntaQuestionarioTurma.class, "categoria.id", obj.getId());
		
		if (perguntas.size() > 0)
			addMensagemErro("Esta categoria não pode ser removida pois há perguntas associadas a ela");
		else {
			dao.detach(obj);
	
			// Se o objeto a remover foi encontrado, desativa
			if (obj != null && obj.isAtivo()){
				prepareMovimento(ArqListaComando.ALTERAR);
	
				obj.setAtivo(false);
	
				cadastrar();
			} else
				addMensagemErro("Esta categoria já foi removida");
	
			all = null;
			categoriasCompartilhadas = null;
		}

		return forward(getListPage());
	}
	
	/**
	 * Método que remove uma pergunta de uma categoria.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 */
	public String removerPergunta() throws ArqException {
		
		int idPergunta = getParameterInt("idPergunta");

		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			PerguntaQuestionarioTurma pergunta = dao.findByPrimaryKey(idPergunta, PerguntaQuestionarioTurma.class);
			
			dao.detach(pergunta);
			
			prepareMovimento(ArqListaComando.ALTERAR);

			pergunta.setAtivo(false);
			
			MovimentoCadastro mov = new MovimentoCadastro ();
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			mov.setObjMovimentado(pergunta);
			
			execute(mov);
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	
			all = null;
			categoriasCompartilhadas = null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getListPage());
	}
	
	/**
	 * Exibe o formulário para cadastro de uma nova categoria.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 */
	public String preCadastrar() throws ArqException, NegocioException {
		
		voltarAoCadastroDePergunta = getParameterBoolean("voltar_cadastro_pergunta");
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		obj = new CategoriaPerguntaQuestionarioTurma();
		
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que ja' foi removido.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		voltarAoCadastroDePergunta = getParameterBoolean("voltar_cadastro_pergunta");
		// Tenta pegar o objeto do banco
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null && obj.isAtivo())
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new CategoriaPerguntaQuestionarioTurma();
		addMensagemErro("Essa categoria foi removida.");
		return forward(getListPage());
	}
	
	/**
	 * Metodo que cadastra ou altera uma categoria.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formCategoria.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {

		// Verifica se o objeto nao foi removido
		if (obj == null){
			addMensagemErro("Essa categoria foi removida.");
			return forward(getListPage());
		}
		
		// Valida o objeto
		ListaMensagens lista = obj.validate();
	
		// Se ocorreram erros, exiba-os e retorne.
		if (lista != null && !lista.isEmpty()){
			addMensagens(lista);
			return forward(getFormPage());
		}
	
		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			// Se for operacao de cadastrar, a id do objeto sera' igual a zero
			if (obj.getId() == 0){
				// Seta a operacao como cadastrar
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				// Faz com que o usuário logado seja o dono do objeto.
				obj.setDono(getUsuarioLogado());
				
			} else {
				// Seta a operacao como alterar
				mov.setCodMovimento(ArqListaComando.ALTERAR);
			}

			// Tenta executar a operacao
			execute(mov);
			
			// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
			addMessage("Operação Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			
			int idCategoria = obj.getId();
			
			obj = new CategoriaPerguntaQuestionarioTurma();
			
			all = null;
			categoriasCompartilhadas = null;
			
			if (voltarAoCadastroDePergunta){
				QuestionarioTurmaMBean qMBean = getMBean ("questionarioTurma");
				return qMBean.iniciarAdicionarPergunta(idCategoria);
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}

		// Prepara o movimento para permitir a insercao de um novo objeto
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	/**
	 * Volta ao cadastro de uma nova pergunta.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formCategoria.jsp</li>
	 * </ul>
	 */
	public String voltarAoCadastroDePergunta () throws DAOException {
		QuestionarioTurmaMBean qMBean = getMBean ("questionarioTurma");
		return qMBean.iniciarAdicionarPergunta(-1);
	}
	
	/**
	 * Método que remove o objeto, verificando se o mesmo existe.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * 		<li>/ava/QuestionarioTurma/pergunta.jsp</li>
	 * </ul>
	 */
	@Override
	public Collection <CategoriaPerguntaQuestionarioTurma> getAll() throws DAOException{
		
		if (all == null){
			all = getGenericDAO().findAtivosByExactField(CategoriaPerguntaQuestionarioTurma.class, "dono.id", getUsuarioLogado().getId(), "nome", "asc");
			
			for  (CategoriaPerguntaQuestionarioTurma c : all){
				List <PerguntaQuestionarioTurma> perguntasAtivas = new ArrayList<PerguntaQuestionarioTurma> ();
				for (PerguntaQuestionarioTurma p : c.getPerguntas())
					if (p.isAtivo())
						perguntasAtivas.add(p);
				c.setPerguntas(perguntasAtivas);
			}
		}
		
		return all;
	}
	
	/**
	 * Exibe as perguntas da categoria.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 */
	public void  exibirPerguntas (ActionEvent e) throws DAOException {
		int idCategoria = getParameterInt("idCategoria", 0);
		boolean compartilhada = getParameterBoolean("compartilhada"); 
		
		QuestionarioTurmaDao dao = null;
		try {
			
			CategoriaPerguntaQuestionarioTurma categoria = null;
			
			List <CategoriaPerguntaQuestionarioTurma> cs = compartilhada ? getCategoriasCompartilhadas() : (List<CategoriaPerguntaQuestionarioTurma>) getAll();
			for (CategoriaPerguntaQuestionarioTurma c : cs)
				if (c.getId() == idCategoria){
					categoria = c;
					break;
				}
			
			if (categoria != null){
				if (!categoria.isExibirPerguntas()){
					dao = getDAO(QuestionarioTurmaDao.class);
					List <PerguntaQuestionarioTurma> ps = dao.findPerguntasByCategoria(idCategoria);
					categoria.setPerguntas(ps);
				}
				
				categoria.setExibirPerguntas(!categoria.isExibirPerguntas());
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Prepara o formulário de compartilhametno com a categoria selecionada pelo docente.
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarCategoria (ActionEvent e) throws DAOException {
		
		mensagemSucessoCompartilhamento = null;
		mensagemErroCompartilhamento = null;
		
		int idCategoria = getParameterInt("idCategoria");
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			categoriaSelecionada = dao.findByPrimaryKey (idCategoria, CategoriaPerguntaQuestionarioTurma.class);
			
			if (!categoriaSelecionada.isAtivo()){
				addMensagemErro("Essa categoria foi removida.");
				categoriaSelecionada = null;
				redirectJSF(getListPage());
				return;
			}
			
			if (categoriaSelecionada.getDono().getId() != getUsuarioLogado().getId()){
				mensagemErroCompartilhamento = "Esta categoria pertence a outro docente.";
				categoriaSelecionada = null;
				return;
			}
					
			Collections.sort(categoriaSelecionada.getPessoasComCompartilhamento(), new Comparator <Pessoa> () {
				@Override
				public int compare(Pessoa p1, Pessoa p2) {
					return p1.getNome().compareTo(p2.getNome());
				}
			});
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Gerar um documento para impressao.
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategoria.jsp.jsp</li>
	 * </ul>
	 */
	public String gerarRelatorioPerguntas() throws Exception {
		
		int idCategoria = getParameterInt("id", 0);
		QuestionarioTurmaDao dao = getDAO(QuestionarioTurmaDao.class);

		obj = dao.findByPrimaryKey(idCategoria, CategoriaPerguntaQuestionarioTurma.class);
		if (obj == null || !obj.isAtivo()){
			addMensagemErro("Essa categoria foi removida.");
			return forward(getListPage());
		}
		
		if (perguntas == null) {
			perguntas = new ArrayList<PerguntaQuestionarioTurma>();
		}
		
		perguntas = dao.findPerguntasByCategoria(idCategoria);
		
		for(PerguntaQuestionarioTurma p:perguntas) {
			if (p.isUnicaEscolha()) {
				char letraAlternativa = 'a';
				for(AlternativaPerguntaQuestionarioTurma a:p.getAlternativas()) {		
					a.setAlternativa(letraAlternativa+") "+a.getAlternativa());
					letraAlternativa++;
				}
				
			}
		}
		
		if (perguntas.isEmpty()) {
			addMensagemWarning("Não há perguntas na categoria selecionada para geração do relatório.");
			return null;
		}

		return forward("/ava/QuestionarioTurma/relatorioQuestoes.jsf");
	
	}
	
	/**
	 * Gerar um documento para impressao contendo as perguntas de todas categorias.
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategoria.jsp.jsp</li>
	 * </ul>
	 */
	public String gerarRelatorioTodasPerguntas() throws Exception {
		
		CategoriaPerguntaQuestionarioTurmaDao cDao = null;
		
		try {
			cDao = getDAO(CategoriaPerguntaQuestionarioTurmaDao.class);
			listaCategoriasRelatorio = cDao.findCategoriasByDono(getUsuarioLogado().getId());
			listaCategoriasCompartilhadasRelatorio = cDao.findCategoriasByCompartilhamento(getUsuarioLogado().getPessoa().getId());

		} finally {
			if (cDao != null)
				cDao.close();
		}
		
		return forward("/ava/QuestionarioTurma/relatorioTodasQuestoes.jsf");
	}
	
	public List<PerguntaQuestionarioTurma> getPerguntas() {
		return perguntas;
	} 
	
	/**
	 * pergas alternativas da pergunta atual
	 * * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/RelatorioQuestoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getAlternativas() {
		
		PerguntaQuestionarioTurma pergunta = new PerguntaQuestionarioTurma();
				
		for(PerguntaQuestionarioTurma p: perguntas) {
			if (p.isMultiplaEscolha() || p.isUnicaEscolha()) {
				pergunta = p;
				perguntas.remove(p);
				break;
			}
			
		}
		 
		return toSelectItems(pergunta.getAlternativas(),"id","alternativa");
		
		
	}
	
	/**
	 * Busca docentes externos e servidores ativos com usuário para permitir que o docente compartilhe a categoria de questões.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Object []> autoCompleteNomeDocente(Object event) throws DAOException{
		String nome = event.toString();
		return (List<Object []>) getDAO(CategoriaPerguntaQuestionarioTurmaDao.class).buscarDocentesPorNome(nome);
	}
	
	/**
	 * Compartilha a categoria selecionda com o usuário selecionado.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void cadastrarCompartilhamento (ActionEvent e) throws ArqException {
		GenericDAO dao = null;
		
		mensagemSucessoCompartilhamento = null;
		mensagemErroCompartilhamento = null;
		
		try {
			
			dao = getGenericDAO();
			
			Integer idPessoaSelecionada = null;
			Servidor servidor = dao.findByPrimaryKey(idServidor, Servidor.class, "pessoa");
			
			if (servidor != null && servidor.getPessoa() != null)
				idPessoaSelecionada = servidor.getPessoa().getId();
			
			if (idPessoaSelecionada == 0 || idPessoaSelecionada == getUsuarioLogado().getPessoa().getId()){
				mensagemErroCompartilhamento = "Por favor, escolha outro usuário para compartilhar esta categoria.";
				return;
			}
			
			categoriaSelecionada = dao.refresh(categoriaSelecionada);
			
			if (!categoriaSelecionada.isAtivo()){
				mensagemErroCompartilhamento = "Esta categoria foi removida.";
				categoriaSelecionada = null;
				return;
			}
			
			boolean encontrado = false;
			for (Pessoa p : categoriaSelecionada.getPessoasComCompartilhamento())
				if (p.getId() == idPessoaSelecionada){
					encontrado = true;
					break;
				}
			
			if (!encontrado){
				categoriaSelecionada.getPessoasComCompartilhamento().add(dao.findByPrimaryKey(idPessoaSelecionada, Pessoa.class));
				
				Collections.sort(categoriaSelecionada.getPessoasComCompartilhamento(), new Comparator <Pessoa> () {
					@Override
					public int compare(Pessoa p1, Pessoa p2) {
						return p1.getNome().compareTo(p2.getNome());
					}
				});
				
				MovimentoCadastro mov = new MovimentoCadastro(categoriaSelecionada);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				
				prepareMovimento(ArqListaComando.ALTERAR);
				execute(mov);
				
				mensagemSucessoCompartilhamento = "Categoria compartilhada com sucesso!";
				idPessoaSelecionada = null;
			} else
				mensagemErroCompartilhamento = "Esta categoria já está compartilhada com este usuário";
		} catch (NegocioException ex) {
			mensagemErroCompartilhamento = "";
			
			for (MensagemAviso s : ex.getListaMensagens().getMensagens())
				mensagemErroCompartilhamento += "<br/>" + s.getMensagem();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Remove o compartilhamento da categoria selecionada com o usuário selecionado.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formPermissao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void removerCompartilhamento(ActionEvent e) throws ArqException {
		int idPessoa = getParameterInt("idPessoa");
		
		mensagemSucessoCompartilhamento = null;
		mensagemErroCompartilhamento = null;
		
		List <Pessoa> ps = categoriaSelecionada.getPessoasComCompartilhamento();
		
		for (int i = 0; i < ps.size(); i++){
			if (ps.get(i).getId() == idPessoa){
				ps.remove(i);
				break;
			}
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro(categoriaSelecionada);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			
			prepareMovimento(ArqListaComando.ALTERAR);
			execute(mov);
			
			mensagemSucessoCompartilhamento = "Compartilhamento removido com sucesso!";
		} catch (NegocioException ex){
			tratamentoErroPadrao(ex);
		}
	}
	
	/**
	 * Retorna todas as categorias do usuário para serem exibidas em um combobox.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/pergunta.jsp</li>
	 * </ul>
	 */
	public Collection <SelectItem> getAllCombo() throws DAOException{
		List<SelectItem> categoriasCombo = toSelectItems(getAll(), "id", "nome");
		categoriasCombo.addAll(toSelectItems(getCategoriasCompartilhadas(), "id", "nome"));
		
		return categoriasCombo;
	}
	
	/**
	 * Retorna a quantidade de categorias que o usuário possui.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		return getAll().size();
	}
	
	/**
	 * Exibe todas as categorias do usuário.<br/><br/>
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formCategoria.jsp</li>
	 * 		<li>/ava/QuestionarioTurma/questionario.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar () {
		
		String voltar = getParameter("voltar_ao_questionario");
		
		if (!StringUtils.isEmpty (voltar))
			voltarAoQuestionario = Boolean.valueOf(voltar);
		
		return forward(getListPage());
	}
	
	/**
	 * Retorna a página da listagem
	 */
	@Override
	public String getListPage (){
		return "/ava/QuestionarioTurma/listarCategorias.jsf";
	}
	
	/**
	 * Retorna a página do formulário
	 */
	@Override
	public String getFormPage () {
		return "/ava/QuestionarioTurma/formCategoria.jsf";
	}

	public boolean isVoltarAoQuestionario() {
		return voltarAoQuestionario;
	}

	public void setVoltarAoQuestionario(boolean voltarAoQuestionario) {
		this.voltarAoQuestionario = voltarAoQuestionario;
	}

	public boolean isVoltarAoCadastroDePergunta() {
		return voltarAoCadastroDePergunta;
	}

	public void setVoltarAoCadastroDePergunta(boolean voltarAoCadastroDePergunta) {
		this.voltarAoCadastroDePergunta = voltarAoCadastroDePergunta;
	}

	public CategoriaPerguntaQuestionarioTurma getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(
			CategoriaPerguntaQuestionarioTurma categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public String getMensagemSucessoCompartilhamento() {
		return mensagemSucessoCompartilhamento;
	}

	public void setMensagemSucessoCompartilhamento(String mensagemSucessoCompartilhamento) {
		this.mensagemSucessoCompartilhamento = mensagemSucessoCompartilhamento;
	}

	public String getMensagemErroCompartilhamento() {
		return mensagemErroCompartilhamento;
	}

	public void setMensagemErroCompartilhamento(String mensagemErroCompartilhamento) {
		this.mensagemErroCompartilhamento = mensagemErroCompartilhamento;
	}

	/**
	 * Retorna a lista de categorias compartilhadas para o docente
	 * Não invocado por JSPs.
	 */
	public List<CategoriaPerguntaQuestionarioTurma> getCategoriasCompartilhadas() throws DAOException {
		CategoriaPerguntaQuestionarioTurmaDao dao = null;
		
		if (categoriasCompartilhadas == null){
			try {
				dao = getDAO(CategoriaPerguntaQuestionarioTurmaDao.class);
				categoriasCompartilhadas = dao.listarCategoriasCompartilhadas(getUsuarioLogado().getPessoa().getId());
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		return categoriasCompartilhadas;
	}

	public void setCategoriasCompartilhadas(
			List<CategoriaPerguntaQuestionarioTurma> categoriasCompartilhadas) {
		this.categoriasCompartilhadas = categoriasCompartilhadas;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public Integer getIdServidor() {
		return idServidor;
	}

	public void setListaCategoriasRelatorio(List <CategoriaPerguntaQuestionarioTurma> listaCategoriasRelatorio) {
		this.listaCategoriasRelatorio = listaCategoriasRelatorio;
	}

	public List <CategoriaPerguntaQuestionarioTurma> getListaCategoriasRelatorio() {
		return listaCategoriasRelatorio;
	}

	public void setListaCategoriasCompartilhadasRelatorio(
			List <CategoriaPerguntaQuestionarioTurma> listaCategoriasCompartilhadasRelatorio) {
		this.listaCategoriasCompartilhadasRelatorio = listaCategoriasCompartilhadasRelatorio;
	}

	public List <CategoriaPerguntaQuestionarioTurma> getListaCategoriasCompartilhadasRelatorio() {
		return listaCategoriasCompartilhadasRelatorio;
	}

}