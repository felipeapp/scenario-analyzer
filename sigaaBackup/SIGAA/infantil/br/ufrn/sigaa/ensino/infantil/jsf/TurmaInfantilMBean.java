/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/11/2009
 *
 */

package br.ufrn.sigaa.ensino.infantil.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.mensagens.MensagensInfantil;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Controlador responsável pelas operações de cadastro/alteração/remoção das turmas do ensino infantil.
 * 
 * @author Leonardo Campos
 *
 */
@Component("turmaInfantilMBean") @Scope("request")
public class TurmaInfantilMBean extends SigaaAbstractController<Turma>{

	/** Indica o ano de cadastro da Turma. */
	private Integer ano;
	
	/** Nível da Turma utilizado na busca. */
	private Integer nivel;
	
	/** Código da Turma utilizado na busca. */
	private String codigo;
	
	/** Indica o docente da Turma. */
	private DocenteTurma docenteTurmaInfantil;
	
	private boolean buscaFichaEvolucao;
	
	/** Indica se a forma de busca pela Turma será pelo Ano. */
	private boolean buscaAno;
	/** Indica se a forma de busca pela Turma será pelo Nível. */
	private boolean buscaNivel;
	/** Indica se a forma de busca pela Turma será pelo Código. */
	private boolean buscaCodigo;
	/** Indica se a forma de busca pela Turma será pelo Professor. */
	private boolean buscaProfessor;
	
	/** Lista de Matriculados */
	private Collection<MatriculaComponente> matriculados;
	
	public TurmaInfantilMBean() {
		clear();
	}

	/**
	 * Inicializa os atributos para utilização durante o caso de uso.
	 */
	private void clear() {
		obj = new Turma();
		obj.setDisciplina(new ComponenteCurricular());
		obj.setTipo(Turma.REGULAR);
		obj.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.ABERTA));
		clearDocenteTurma();
	}

	/**
	 * Inicializa as informações do docente da turma.
	 */
	private void clearDocenteTurma() {
		docenteTurmaInfantil = new DocenteTurma();
		docenteTurmaInfantil.setDocente(new Servidor());
		docenteTurmaInfantil.setDocenteExterno(new DocenteExterno());
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/menu.jsp</li>
	 *		<li>sigaa.war/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_INFANTIL);
		clear();
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA.getId());
		setAno(CalendarUtils.getAnoAtual());
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_FORM);
	}
	
	/**
	 * Volta para a listagem das Turmas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/form.jsp</li>
	 *		<li>sigaa.war/infantil/TurmaInfantil/view.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String voltar() throws ArqException{
		clear();
		setReadOnly(false);
		setConfirmButton("Cadastrar");
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA);
	}

	/**
	 * Adiciona um docente selecionado à turma.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/TurmaInfantil/form.jsp</li>
	 *	</ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void adicionarDocenteTurma(ActionEvent evt) throws DAOException {
		obj.setAno(ano == null ? 0 : ano);
		TurmaInfantilDao dao = getDAO(TurmaInfantilDao.class);
		erros = new ListaMensagens();
		int categoriaMembro = getParameterInt("categoriaMembro", CategoriaMembro.DOCENTE);
		DocenteExterno docenteExterno = null;
		Servidor docente = null;
		
		if(categoriaMembro == CategoriaMembro.DOCENTE){
			ValidatorUtil.validateRequiredId(docenteTurmaInfantil.getDocente().getId(), "Docente", erros);
			getCurrentSession().setAttribute("aba", "membro-docente");
			docente = dao.refresh(docenteTurmaInfantil.getDocente());
			if(obj.getAno() > 0 && docenteTurmaInfantil.getDocente() != null 
					&& docenteTurmaInfantil.getDocente().getId() > 0
					&& dao.existsTurmaProfessor(docenteTurmaInfantil.getDocente().getId(), obj)){
				addMensagem(MensagensInfantil.PROFESSOR_JA_POSSUI_TURMA, docente.getNome(), obj.getAno());
			}
		}else{
			ValidatorUtil.validateRequiredId(docenteTurmaInfantil.getDocenteExterno().getId(), "Estagiário", erros);
			getCurrentSession().setAttribute("aba", "membro-externo");
			docenteExterno = dao.refresh(docenteTurmaInfantil.getDocenteExterno());
			if(obj.getAno() > 0 && docenteTurmaInfantil.getDocenteExterno() != null 
					&& docenteTurmaInfantil.getDocenteExterno().getId() > 0
					&& dao.existsTurmaProfessor(docenteTurmaInfantil.getDocenteExterno().getId(), obj)){
				addMensagem(MensagensInfantil.PROFESSOR_JA_POSSUI_TURMA, docenteExterno.getNome(), obj.getAno());
			}
		}
		
		if ( !obj.getDocentesTurmas().isEmpty() ) {
			for (DocenteTurma docTurma : obj.getDocentesTurmas()) {
				if ( docTurma.getDocenteExterno() != null && docTurma.getDocenteExterno().getId() == docenteTurmaInfantil.getDocenteExterno().getId() ) {
					addMensagem(MensagensInfantil.PROFESSOR_JA_CASTRADO_NA_TURMA, docenteExterno.getNome());
				}
				if ( docTurma.getDocente() != null && docTurma.getDocente().getId() == docenteTurmaInfantil.getDocente().getId() ) {
					addMensagem(MensagensInfantil.PROFESSOR_JA_CASTRADO_NA_TURMA, docente.getNome());
				}
			}
		}
		
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
		if(hasErrors()){
			addMensagens(erros);
		} else {
			if(categoriaMembro == CategoriaMembro.DOCENTE)
				docenteTurmaInfantil.setDocenteExterno(null);
			else
				docenteTurmaInfantil.setDocente(null);
			docenteTurmaInfantil.setChDedicadaPeriodo(0);
			obj.addDocenteTurma(docenteTurmaInfantil);
			clearDocenteTurma();
			addMensagem(MensagensInfantil.PROFESSOR_ADICIONADO_COM_SUCESSO);
		}
	}
	
	/**
	 * Remove um docente selecionado da turma.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/form.jsp</li>
	 *	</ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void removerDocenteTurma(ActionEvent evt) throws DAOException {
		int indice = getParameterInt("indice");
		CollectionUtils.removePorPosicao(obj.getDocentesTurmas(), indice);
		addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Professor");
	}
	
	/**
	 * Carrega as informações da turma e encaminha para o formulário de alteração.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		setConfirmButton("Alterar");
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA.getId());
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), Turma.class);
		obj.getDocentesTurmas().size();
		setAno(obj.getAno());
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_FORM);
	}
	
	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String preRemover() {
		super.preRemover();
		try {
			prepareMovimento(SigaaListaComando.REMOVER_TURMA);
		} catch (ArqException e) {
			return tratamentoErroPadrao(e);
		}
		setAno(obj.getAno());
		setOperacaoAtiva(SigaaListaComando.REMOVER_TURMA.getId());
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_VIEW);
	}
	
	/**
	 * Realiza as validações e chama o processador para persistir as informações da turma
	 * do ensino infantil.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		String alteracao = obj.getId() == 0 ? "criada" : "alterada";
		boolean alterar = obj.getId() == 0 ? false : true;
		obj.setAno(ano == null ? 0 : ano);
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA.getId(), SigaaListaComando.ALTERAR_TURMA.getId()))
				return forward(ConstantesNavegacaoInfantil.MENU_INFANTIL);
			erros = new ListaMensagens();
			ValidatorUtil.validaInt(obj.getAno(), "Ano", erros);
			ValidatorUtil.validateRequiredId(obj.getDisciplina().getId(), "Turma", erros);
			ValidatorUtil.validateRequired(obj.getDescricaoHorario(), "Turno", erros);
			ValidatorUtil.validateRequired(obj.getLocal(), "Local", erros);
			ValidatorUtil.validateRequired(obj.getCapacidadeAluno(), "Capacidade", erros);
			if ( obj.getCapacidadeAluno() != null && obj.getCapacidadeAluno() == 0 )
				ValidatorUtil.validaInt(obj.getCapacidadeAluno(), "Capacidade" ,erros);
			TurmaValidator.validaDocentes(obj.getDocentesTurmas(), erros);
			boolean achouServidor = false;
			for(DocenteTurma docenteTurmaInfantil: obj.getDocentesTurmas()){
				if(docenteTurmaInfantil.getDocente() != null)
					achouServidor = true;
			}
			if(!achouServidor)
				erros.addMensagem(MensagensInfantil.PELO_MENOS_UM_DOCENTE_SERVIDOR);
			
			if(hasErrors()){
				addMensagens(erros);
				return null;
			}

			obj.setDisciplina(getGenericDAO().refresh(obj.getDisciplina()));
			obj.setHorarios(null);
			obj.setReservas(null);
			
			TurmaMov mov = new TurmaMov();
			mov.setCodMovimento(getUltimoComando());
			mov.setTurma(obj);
			
			try {
				execute(mov);
			} catch (NegocioException e) {
				addMensagens( e.getListaMensagens() );
				return null;
			}
			
			addMensagemInformation(obj.toString() + " foi " + alteracao + " com sucesso.");
			
			if (alterar)
				return listar();
			else 
				return redirectJSF(getSubSistema().getLink());
		}
	}
	
	/**
	 * Invoca o processador para efetuar a remoção da turma do banco.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() throws ArqException {
		if(!checkOperacaoAtiva(SigaaListaComando.REMOVER_TURMA.getId()))
			return forward(ConstantesNavegacaoInfantil.MENU_INFANTIL);
		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			TurmaMov mov = new TurmaMov();
			mov.setTurma(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_TURMA);
			try {
				execute(mov);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} 
			addMensagem(OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			return listar();
		}
	}
	
	/**
	 * Carrega as turmas do ano atual e redireciona para a tela de listagem/consulta de turmas. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/menu.jsp</li>
	 *	</ul>
	 */
	@Override
	public String listar() throws ArqException {
		clear();
		buscaAno = true;
		ano = CalendarUtils.getAnoAtual();
		buscaFichaEvolucao = true;
		resultadosBusca = getDAO(TurmaDao.class).findGeral(getNivelEnsino(), new Unidade(getUnidadeGestora()), null, null, null, null, new Integer[] { SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE }, 
				CalendarUtils.getAnoAtual(), null, null, null,  new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, null, null, null, null,null,null);
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA);
	}
	
	/**
	 */
	public void carregarTurmasDocente() throws ArqException {
		clear();
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			buscaFichaEvolucao = true;
			resultadosBusca = dao.findByDocente(getUsuarioLogado().getServidor(), NivelEnsino.INFANTIL, CalendarUtils.getAnoAtual(), null, null, SituacaoTurma.ABERTA);
			if ( resultadosBusca.isEmpty() )
				addMensagemErro("Nenhuma Turma Encontrada.");
			
		} catch (Exception e) {
			dao.close();
		}
	}
	
	/**
	 * Valida os critérios de busca informados e consulta por turmas do ensino infantil.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String buscar() throws Exception {
		if(!buscaAno && ! buscaNivel && !buscaCodigo && !buscaProfessor){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
			
		Integer ano = null;
		Integer nivel = null;
		String codigo = null;
		String nomeProfessor = null;
		
		if(buscaAno){
			ValidatorUtil.validaInt(getAno(), "Ano", erros);
			ano = getAno();
		}
		if(buscaNivel){
			ValidatorUtil.validateRequiredId(getNivel(), "Nível", erros);
			nivel = getNivel();
		}
		if(buscaCodigo){
			ValidatorUtil.validateRequired(getCodigo(), "Código", erros);
			codigo = getCodigo();
		}
		if(buscaProfessor){
			ValidatorUtil.validateRequired(docenteTurmaInfantil.getDocente().getPessoa().getNome(), "Professor(a)", erros);
			nomeProfessor = docenteTurmaInfantil.getDocente().getPessoa().getNome();
		}
		if (hasErrors())
			return null;
		
		resultadosBusca = getDAO(TurmaDao.class).findGeral(getNivelEnsino(), new Unidade(getUnidadeGestora()), null, codigo != null ? codigo.toUpperCase() : codigo, null, nomeProfessor, null, ano, null, null, null,  new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, nivel, null, null, null,null,null);
		if(resultadosBusca == null || resultadosBusca.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA);
	}
	
	
	/**
	 * Lista os alunos matriculados na turma.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 */
	public String listarAlunos() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);

		obj = dao.findByPrimaryKey(getParameterInt("id"), Turma.class);

		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.add(SituacaoMatricula.CANCELADO );
		situacoes.add(SituacaoMatricula.NAO_CONCLUIDO );
		matriculados = dao.findMatriculasDadosPessoaisByTurma(getParameterInt("id"), situacoes);

		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_DETALHES);
	}
	
	/**
	 * Lista os alunos matriculados em uma Turma, e redireciona para página de impressão.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException 
	 */
	public String listarAlunosImpressao() throws ArqException{
		TurmaDao dao = getDAO(TurmaDao.class);
		obj = dao.findByPrimaryKey(getParameterInt("id"), Turma.class);
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.add(SituacaoMatricula.CANCELADO );
		situacoes.add(SituacaoMatricula.NAO_CONCLUIDO );
		matriculados = dao.findMatriculasDadosPessoaisByTurma(getParameterInt("id"), situacoes);
		if(matriculados == null || matriculados.isEmpty()){
			addMensagemErro("Nenhum aluno está matriculado nessa turma");
			return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA);
		}
		return forward(ConstantesNavegacaoInfantil.TURMA_INFANTIL_LISTA_IMPRESSAO);
	}
	
	public DocenteTurma getDocenteTurmaInfantil() {
		return docenteTurmaInfantil;
	}

	public void setDocenteTurmaInfantil(DocenteTurma docenteTurmaInfantil) {
		this.docenteTurmaInfantil = docenteTurmaInfantil;
	}
	
	/**
	 * Retorna uma coleção de componentes curriculares do ensino infantil.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/TurmaInfantil/form.jsp</li>
	 *		<li>sigaa.war/infantil/TurmaInfantil/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getNiveisCombo() throws DAOException, ArqException{
		return toSelectItems(getDAO(ComponenteCurricularDao.class).findByUnidadeOtimizado(getUnidadeGestora(), getNivelEnsino(), null), "id", "descricaoResumida");
	}

	public boolean isBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(boolean buscaAno) {
		this.buscaAno = buscaAno;
	}

	public boolean isBuscaNivel() {
		return buscaNivel;
	}

	public void setBuscaNivel(boolean buscaNivel) {
		this.buscaNivel = buscaNivel;
	}

	public boolean isBuscaCodigo() {
		return buscaCodigo;
	}

	public void setBuscaCodigo(boolean buscaCodigo) {
		this.buscaCodigo = buscaCodigo;
	}

	public boolean isBuscaProfessor() {
		return buscaProfessor;
	}

	public void setBuscaProfessor(boolean buscaProfessor) {
		this.buscaProfessor = buscaProfessor;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Collection<MatriculaComponente> getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(Collection<MatriculaComponente> matriculados) {
		this.matriculados = matriculados;
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isBuscaFichaEvolucao() {
		return buscaFichaEvolucao;
	}

	public void setBuscaFichaEvolucao(boolean buscaFichaEvolucao) {
		this.buscaFichaEvolucao = buscaFichaEvolucao;
	}
	
}