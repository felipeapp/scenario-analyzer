/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AulaFrequencia;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.ImportacaoDadosTurmasAnterioresMBean;
import br.ufrn.sigaa.ava.jsf.IndicacaoReferenciaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaInternaBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ensino.dominio.PlanoCurso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * MBean responsável pelas operações do plano de curso de uma turma virtual.
 * 
 * @author Daniel Augusto
 *
 */
@Scope("request")
@Component("planoCurso")
public class PlanoCursoMBean extends SigaaAbstractController<PlanoCurso> implements PesquisarAcervoBiblioteca{
	
	/** Indica se o usuário acessou o caso de uso enquanto tentava acessar a turma virtual. */
	private boolean acessoTurmaVirtual;
	
	/** Indica se o usuário acessou o caso de uso pela tela de turmas virtuais abertas. */
	private boolean acessoTurmasAbertas;
	
	/** Indica se, na adição de uma indicação de referência, um título catalográfico da biblioteca foi selecionado. */
	private boolean livroSelecionado = true;
	
	/** Lista com todas as referências da turma. */
	private List <IndicacaoReferencia> referencias;
	
	/** Lista com todas as referências Básicas da turma. */
	private List <IndicacaoReferencia> referenciasBasicas;
	
	/** Lista com todas as referências Complementares da turma. */
	private List <IndicacaoReferencia> referenciasComplementares;
	
	/** Lista com todos os tópicos de aula da turma. */
	private List <TopicoAula> topicosAula;
	
	/** Lista com todas as avaliações da turma. */
	private List <DataAvaliacao> avaliacoes;
	
	/** A indicação de referência que está sendo alterada no momento. */
	private IndicacaoReferencia indicacaoReferencia;
	
	/** O tópico de aula que está sendo alterado no momento. */
	private TopicoAula topicoAula;
	
	/** A avaliação que está sendo alterada no momento. */
	private DataAvaliacao avaliacao;
	
	/** A turma a qual pertence o plano de curso que está sendo gerenciado. */
	private Turma turma;
	
	/** Tempo em milisegundos do início do tópico de aula. */
	private Long inicioTA;
	
	/** Tempo em milisegundos do fim do tópico de aula. */
	private Long fimTA;
	
	/** O tipo da indicação de referência. */
	private Character tipoIR;
	
	/** Indica se a indicação de referência é básica ou complementar. */
	private String tipoIndicacaoReferencia;
	
	/** Lista de tópicos de aula o select. */
	private List<SelectItem> aulasCombo;
	
	/** Lista de tópicos de aula o select, com a data início do tópico a ser alterado em destaque. */
	private List<SelectItem> inicioTACombo;
	
	/** Lista de tópicos de aula o select, com a data fim do tópico a ser alterado em destaque. */
	private List<SelectItem> fimTACombo;
	
	/** Lista de avaliações para o select. */
	private List <SelectItem> avaliacoesCombo;
	
	/** Indica se a tela deve rolar até o formulário de inserção de indicação de referência. */
	private boolean scrollIR;
	
	/** A quantidade de unidades da turma. */
	private Integer numUnidades;
	
	/** Variável que indica se é para não validar os campos do plano de curso. Usada no a4j:poll do formulário. */
	private boolean naoValidar;
	
	/** Guarda as informações MARC de uma catalogação no acervo da biblioteca, para gerar o formato de referência com base nessa catalogação. */
	private CacheEntidadesMarc titulo;
	
	/** Define se as aulas lançadas no plano devem ser validadas ou não. */
	private boolean verificaAulasLancadas = true;
	
	/** Se a pagina está sendo carregada pela importação de dados. */
	private boolean carregadaImportacao = false;
	
	/** Mensagem de sucesso exibida no formulário de cadastro de tópico aula. */
	private String mensagemSucessoTopicoAula;
	
	/** Mensagem de erro exibida no formulário de cadastro de tópico aula. */
	private String mensagemErroTopicoAula;
	
	/** Mensagem de sucesso exibida no formulário de cadastro de avaliação. */
	private String mensagemSucessoAvaliacao;
	
	/** Mensagem de erro exibida no formulário de cadastro de avaliação. */
	private String mensagemErroAvaliacao;
	
	/** Mensagem de sucesso exibida no formulário de cadastro de referência. */
	private String mensagemSucessoReferencia;
	
	/** Mensagem de erro exibida no formulário de cadastro de referência. */
	private String mensagemErroReferencia;
	
	/** Se o tópico será alterado. */
	private boolean alterarTopico = false;
	
	/** Se a avaliação será alterada. */
	private boolean alterarAvaliacao = false;
	
	/** Se a indicação de referência será alterada. */
	private boolean alterarReferencia = false;
	
	public PlanoCursoMBean() {
		inicializar();
	}
	
	/**
	 * Inicializa os objetos do bean.
	 * @throws NegocioException 
	 */
	private void inicializar(){
		
		obj = new PlanoCurso();
		
		referencias = new ArrayList <IndicacaoReferencia> ();
		referenciasBasicas = referenciasComplementares = null;
		topicosAula = new ArrayList <TopicoAula> ();
		avaliacoes = new ArrayList <DataAvaliacao> ();
		
		indicacaoReferencia = new IndicacaoReferencia();
		indicacaoReferencia.setTipo(IndicacaoReferencia.TIPO_LIVRO);
		tipoIndicacaoReferencia = "" + IndicacaoReferencia.TIPO_INDICACAO_BASICA;
		
		topicoAula = new TopicoAula ();
		
		avaliacao = new DataAvaliacao ();
		
		avaliacoesCombo = null;
		aulasCombo = null;
		
		livroSelecionado = false;
		verificaAulasLancadas = true;
	}
	
	/**
	 * Inicia o caso de uso de importar as informações de uma turma anterior para este plano de curso.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarImportacaoDados () throws DAOException{
		ImportacaoDadosTurmasAnterioresMBean iBean = getMBean ("importacaoDadosTurma");
		return iBean.iniciarPlanoCurso(turma);
	}
	
	/**
	 * Inicializa o caso de uso de gerenciar o plano de curso de uma turma a partir da turma virtual.<br/><br/>
	 * 
	 * Mètodo não invocado por JSPs.
	 * 
	 * @param turma
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarPlanoCurso(Turma turma) throws ArqException {
		this.turma = turma;
		acessoTurmaVirtual = true;
		
		return gerenciarPlanoCurso();
	}
	
	/**
	 * Inicializa o caso de uso de gerenciar o plano de curso de uma turma a partir da turma virtual.<br/><br/>
	 * 
	 * Mètodo não invocado por JSPs.
	 * 
	 * @param turma
	 * @return
	 * @throws ArqException
	 */
	public String visualizarPlanoCurso(Turma turma) throws ArqException {
		this.turma = turma;
		acessoTurmaVirtual = true;
		
		GenericDAO dao = null;

		try {
			inicializar();
			
			int idTurma = getParameterInt("idTurma", 0);
			
			if (idTurma == 0){
				if (turma == null || turma.getId() == 0){
					addMensagem (MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
					return null;
				} else
					idTurma = turma.getId();
			}
		
			dao = getGenericDAO();
		
			turma = dao.findByPrimaryKey(idTurma, Turma.class);
			obj = dao.findByExactField(PlanoCurso.class, "turma", turma.getId(), true);

			
			if (obj == null || !obj.isFinalizado()) {
				obj = new PlanoCurso();
				obj.setTurma(turma);
				addMensagem(MensagensTurmaVirtual.TURMA_NAO_POSSUI_PLANO);
				return null;
			}
			
			// Busca os tópicos de aulas, as avaliações e as indicações de referências já cadastradas para a turma.
			topicosAula = (List <TopicoAula>) dao.findAtivosByExactField(TopicoAula.class, "turma.id", turma.getId(), "data", "asc");
			avaliacoes = getDAO(TurmaVirtualDao.class).buscarDatasAvaliacao(turma);  
			referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
			referenciasBasicas = referenciasComplementares = null;
			
			return forward ("/ava/PlanoCurso/visualizar.jsp");
		
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Ação inicial ao entrar no plano de curso. Caso não exista um plano cadastrado para a turma, 
	 * será solicitado o cadastrado do mesmo, caso contrário, será apresentada as informações para a 
	 * alteração do plano de curso. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\ava\turmasAbertas.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarPlanoCurso() throws ArqException {
		
		GenericDAO dao = null;

		try {
			inicializar();
			
			checkRole(SigaaPapeis.DOCENTE);
			
			int idTurma = getParameterInt("idTurma", 0);
			
			if (idTurma == 0){
				if (turma == null || turma.getId() == 0){
					addMensagem (MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
					return null;
				} else
					idTurma = turma.getId();
			}
		
			dao = getGenericDAO();
		
			turma = dao.findByPrimaryKey(idTurma, Turma.class);
			if(turma.isSubTurma())
				turma = turma.getTurmaAgrupadora();
			obj = dao.findByExactField(PlanoCurso.class, "turma", turma.getId(), true);

			String acao;
			
			// Busca os tópicos de aulas, as avaliações e as indicações de referências já cadastradas para a turma.
						
			topicosAula = (List <TopicoAula>) dao.findAtivosByExactField(TopicoAula.class, "turma.id", turma.getId(), "data", "asc");
			avaliacoes = getDAO(TurmaVirtualDao.class).buscarDatasAvaliacao(turma);
			referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
			referenciasBasicas = new ArrayList<IndicacaoReferencia>();
			referenciasComplementares = new ArrayList<IndicacaoReferencia>();
			
			for ( IndicacaoReferencia r : referencias )
				if ( r.getTipoIndicacao() == null || r.isIndicacaoBasica() )
					referenciasBasicas.add(r);
				else
					referenciasComplementares.add(r);
			
			if (obj == null) {
				obj = new PlanoCurso();
				obj.setTurma(turma);
				if (!carregadaImportacao)
					addMensagem(MensagensTurmaVirtual.TURMA_NAO_POSSUI_PLANO);
				acao = "Cadastrar Plano";
			} else {
				if (!carregadaImportacao)
					addMensagem(MensagensTurmaVirtual.TURMA_JA_POSSUI_PLANO_CADASTRADO);
				acao = "Alterar Plano";
			}
			carregadaImportacao = false;
			
			// Descobre a quantidade de avaliações que a turma deve ter.
			numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
			
			//Em último caso verifica a quantidade máxima de avaliações possíveis, utilizando os
			//parâmetros da gestora acadêmica. 
			if( numUnidades == null ) {
				if( NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()) ) {
					ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalStricto();
					numUnidades = new Integer(parametros.getQtdAvaliacoes());					
				}
			}
			
			setConfirmButton(acao);
			TurmaVirtualMBean turmaMBean = getMBean("turmaVirtual");
			if (turmaMBean.getTurma() == null)
				turmaMBean.setTurma(turma);
			
			return forward(getDirBase() + "/form.jsp");
			
		} catch (NegocioException e) {
			// TODO Auto-generated catch block
			tratamentoErroPadrao(e);
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Muda o tipo da indicação de referência selecionada para básica.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/ava/PlanoCurso/form.jsp</li>
	 *   </ul>
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void mudarTipoIndicacaoParaBasica (ActionEvent evt) throws ArqException{
		mudarTipoIndicacao(IndicacaoReferencia.TIPO_INDICACAO_BASICA);
	}
	
	/**
	 * Muda o tipo da indicação de referência selecionada para complementar.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/ava/PlanoCurso/form.jsp</li>
	 *   </ul>
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void mudarTipoIndicacaoParaComplementar (ActionEvent evt) throws ArqException{
		mudarTipoIndicacao(IndicacaoReferencia.TIPO_INDICACAO_COMPLEMENTAR);
	}

	/**
	 * Popula as referências com informações sobre seus exemplares.
	 * 
	 * Método chamado não invocado por JSPs
	 * @param evt
	 * @throws ArqException
	 */
	public void exibirExemplares( ActionEvent evt ) throws DAOException {
		
		ExemplarDao eDao = null;
		Integer id = getParameterInt("indiceIR");
		IndicacaoReferencia ir = null;
		
		try {
			eDao = getDAO(ExemplarDao.class);

			for (IndicacaoReferencia r : referencias){
				if (r.getId() == id){
					ir = r;
					break;
				}
			}
			
			IndicacaoReferencia ref = null;
			
			if ( ir != null && ir.isLivro() && ir.getExemplares() == null) {
				ref = new IndicacaoReferencia();
				eDao.lock(ir);
				ref.setExemplares(eDao.findAllExemplarAtivosComPrazoByTitulo(ir.getTituloCatalografico().getId()));
			}	
			
			setIndicacaoReferencia(ref);
		
		} finally {
			if (eDao != null)
				eDao.close();
		}	
	}
	
	/**
	 * Muda o tipo da indicação de referência selecionada para básica ou complementar de acordo com o tipo passado.
	 * 
	 * @param novoTipo
	 * @throws ArqException
	 */
	private void mudarTipoIndicacao (int novoTipo) throws ArqException {
		int indiceIR = getParameterInt("indiceIR", -1);
		for (IndicacaoReferencia i : referencias)
			if (i.getId() == indiceIR){
				i = getGenericDAO().findByPrimaryKey(i.getId(), IndicacaoReferencia.class);
				
				if ( i.getMaterial() != null )
					i.setMaterial(getGenericDAO().refresh(i.getMaterial()));
				
				i.setTipoIndicacao(novoTipo);
			
				IndicacaoReferenciaMBean ir = getMBean("indicacaoReferencia");
				ir.atualizarSimples(i);
				
			}
		referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
		referenciasBasicas = null;
		referenciasComplementares = null;
	}
	
	/**
	 * Método salvar relativo ao a4j:poll da gerencia de plano de cursos. Chama o método salvar ignorando as validações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/PlanoCurso/form.jsp</li>
	 * </ul>
	 * 
	 * @see #salvar()
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String salvarPoll () throws SegurancaException, ArqException {
		naoValidar = true;
		return salvar();
	}
	
	/**
	 * Método adicionado na inclusão de um plano de curso.
	 * <br /><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\ava\PlanoCurso\form.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws SegurancaException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public String salvar() throws SegurancaException, ArqException {
		
		// Só deve validar se o docente quiser concluir ou estiver alterando o plano de curso. Para ambos os casos,
		// o obj virá com o atributo "finalizado" = true
		if (!naoValidar && obj.isFinalizado()){
			if (StringUtils.isEmpty(obj.getMetodologia()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Metodologia");
				
			if (StringUtils.isEmpty(obj.getProcedimentoAvalicao()))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Procedimento de Avaliação");
			
			if (!turma.isEad() && verificaAulasLancadas){
				// Verifica se a quantidade mínima de tópicos de aulas será lançada
				int chAula = turma.getDisciplina().getDetalhes().getChAula();
				int chTotal = turma.getDisciplina().getDetalhes().getChTotal();
				double ajusteChAula = chAula / (1.0 * chTotal); // Considerar apenas a CH de Aula para a % de tópicos de aula.
				int porcMin = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PORCENTAGEM_MINIMA_AULAS_DIARIO);
				int aulasMinimas = (int) Math.floor(ajusteChAula * porcMin);
				
				Set<AulaFrequencia> aulas = new TreeSet<AulaFrequencia>();
				
				if(turma.getSubturmas() == null || turma.getSubturmas().isEmpty())
					aulas = CollectionUtils.toSet(TurmaUtil.getListagemAulas(turma));
				else {
					for (Turma t : turma.getSubturmas()) {
						aulas.addAll(CollectionUtils.toSet(TurmaUtil.getListagemAulas(t)));
					}
				}
				
				List<AulaFrequencia> listagemAulas = new ArrayList<AulaFrequencia>();
				listagemAulas.addAll(aulas);
				turma.getDisciplina().setUnidade(getGenericDAO().refresh(turma.getDisciplina().getUnidade()));	
				int unidade = 0;
				if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO)
					unidade = UnidadeGeral.UNIDADE_DIREITO_GLOBAL;
				else
					unidade = turma.getDisciplina().getUnidade().getId();				
				ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma, new br.ufrn.sigaa.dominio.Unidade(unidade));
					
				double porcAulas = TurmaUtil.getPorcentagemAulasLancadas(listagemAulas, turma, param);
				if (aulasMinimas > porcAulas)
					addMensagem(MensagensTurmaVirtual.TURMA_COM_POUCOS_TOPICOS, porcMin + "%");
			}
	
			if (referencias == null || referencias.size() == 0)
				addMensagem(MensagensTurmaVirtual.TURMA_SEM_REFERENCIAS);
			
			if (avaliacoes == null || avaliacoes.size() == 0)
				addMensagem(MensagensTurmaVirtual.TURMA_SEM_AVALIACOES);
			
			if (avaliacoes!= null && numUnidades!= null && avaliacoes.size() < numUnidades)
				addMensagem(MensagensTurmaVirtual.TURMA_COM_POUCAS_AVALIACOES, numUnidades);
		}
		
		if (!hasErrors()){
			MovimentoCadastro mov = new MovimentoCadastro(obj);
			
			if (obj.getId() == 0){
				prepareMovimento(ArqListaComando.CADASTRAR);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
			} else {
				prepareMovimento(ArqListaComando.ALTERAR);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
			}
			
			try {
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		}
		
		naoValidar = false;
		
		return null;
	}
	
	/**
	 * Método adicionado na inclusão de um plano de curso. Este método irá validar o plano de curso.
	 * <br /><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/ava/PlanoCurso/form.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws SegurancaException
	 * @throws NegocioException
	 * @throws DAOException
	 */	
	public String salvarEFinalizar () throws SegurancaException, ArqException {
		
		obj.setFinalizado(true);
		salvar();
		
		if (!hasOnlyErrors()){
			TurmaVirtualMBean turmaBean = (TurmaVirtualMBean) getMBean("turmaVirtual");
			turmaBean.setTurma(turma);
			
			return (acessoTurmaVirtual) ? turmaBean.entrar() : forwardCadastrar();
		}
		
		obj.setFinalizado(false);
		
		return null;
	}
	
	/**
	 * Método utilizado para voltar para a tela anterior da gerência de plano de curso.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/ava/PlanoCurso/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String voltar() throws ArqException {
		TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
		if(acessoTurmaVirtual) {
			return tvBean.entrar();
		}
		if(acessoTurmasAbertas) {
			return tvBean.iniciarTurmasAbertas();
		}
		return forward("/ensino/selecionaTurmaPlanoCurso.jsf");
	}
	
	/**
	 * Método utilizado para validar a referência no plano de curso.
	 * <br/>
	 * Não invocado por JSP(s)
	 * @return
	 */
	private boolean validarReferencia() {
		
		boolean ok = true;
		boolean livroAssociado = false;
		ListaMensagens erros = new ListaMensagens();
		
		// Se não é Livro, não associa a um título catalográfico da biblioteca.
		if (indicacaoReferencia.getTipo() != IndicacaoReferencia.TIPO_LIVRO)
			indicacaoReferencia.setTituloCatalografico(null);
		else
			livroAssociado = indicacaoReferencia.getTituloCatalografico() != null;
		
		if (StringUtils.isEmpty(indicacaoReferencia.getDescricao()))
			indicacaoReferencia.setDescricao(indicacaoReferencia.getTitulo());
		else
			indicacaoReferencia.setTitulo(indicacaoReferencia.getDescricao());
		
		// Valida os campos digitados.
		
		if (tipoIndicacaoReferencia == null || Integer.valueOf(tipoIndicacaoReferencia) < 1){
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo da referência");
			ok = false;
		} else
			indicacaoReferencia.setTipoIndicacao(Integer.valueOf(tipoIndicacaoReferencia));
		
		if (StringUtils.isEmpty(indicacaoReferencia.getTitulo())){
			mensagemErroReferencia += "Informa o título da referência.</br>";
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
			ok = false;
		}
			
		if (indicacaoReferencia.getTituloCatalografico() == null && indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_LIVRO || indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_REVISTA || indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_ARTIGO){
			if (!livroAssociado && StringUtils.isEmpty(indicacaoReferencia.getAutor())){
				erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Autor");
				ok = false;
			}

			if (!livroAssociado && StringUtils.isEmpty(indicacaoReferencia.getAno())){
				erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
				ok = false;
			}
			
			if (indicacaoReferencia.getTipo() != IndicacaoReferencia.TIPO_ARTIGO){
				if (!livroAssociado && StringUtils.isEmpty(indicacaoReferencia.getEditora())){
					erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Editora");
					ok = false;
				}
				
				if (!livroAssociado && StringUtils.isEmpty(indicacaoReferencia.getEdicao())){
					indicacaoReferencia.setEdicao("");
				}
			}
		}
		
		if (indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_SITE && isUrlVazio()){
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Endereço (URL)");
			ok = false;
		}
		
		if (indicacaoReferencia.getUrl() != null && !isUrlVazio()){
			ValidatorUtil.validateUrl(indicacaoReferencia.getUrl(), "Endereço (URL)", erros);
			ValidatorUtil.validateMaxLength(indicacaoReferencia.getUrl(), 300, "Endereço (URL)", erros);
		}
		
		if (erros.isErrorPresent())
			ok = false;
		
		if (!ok){
			mensagemErroReferencia = "";
			for (MensagemAviso m : erros.getMensagens())
				mensagemErroReferencia += m.getMensagem() + "<br/>";
			
			livroSelecionado = true;
			
			mensagemSucessoReferencia = null;
		} 
		
		return ok;
	}

	/**
	 * Verifica se o url está vazio.
	 * Não invocado por JSPs
	 * @return
	 */
	private boolean isUrlVazio() {
		return indicacaoReferencia.getUrl() == null || indicacaoReferencia.getUrl().trim().equals("http://") || 
		indicacaoReferencia.getUrl().trim().equals("https://");
	}
	
	/**
	 * Adiciona uma indicação de referência ao plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void adicionarIndicacaoReferencia (ActionEvent evt) throws ArqException, NegocioException {
		
		indicacaoReferencia.setTipo(tipoIR.charValue());

		boolean ok = validarReferencia();
		boolean livroAssociado = indicacaoReferencia.getTituloCatalografico() != null; // Verifica se tem algum livro associado ao acervo das bibliotecas no sistema.

		
		if (ok){
			
			indicacaoReferencia.setId(0);
			indicacaoReferencia.setTurma(turma);
			indicacaoReferencia.setAula(null);
			
			if (indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_LIVRO && !livroAssociado)
				indicacaoReferencia.setDescricao(indicacaoReferencia.getAutor() + ". <strong>" + indicacaoReferencia.getTitulo() + "</strong>. " + indicacaoReferencia.getEdicao() + ". " + indicacaoReferencia.getEditora() + ". " + indicacaoReferencia.getAno());
			
			try {
				IndicacaoReferenciaMBean ir = getMBean("indicacaoReferencia");
				ir.cadastrarSimples(indicacaoReferencia,turma);
				
				mensagemSucessoReferencia = "Indicação de referência cadastrada com sucesso!";
				
				indicacaoReferencia = new IndicacaoReferencia();
				indicacaoReferencia.setTipo(IndicacaoReferencia.TIPO_LIVRO);
				tipoIndicacaoReferencia = null;
				//tipoIndicacaoReferencia = "" + IndicacaoReferencia.TIPO_INDICACAO_BASICA;
				livroSelecionado = false;
				referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
				referenciasBasicas = referenciasComplementares = null;
				
			} catch (ArqException e){
				mensagemErroReferencia = e.getMessage();
				mensagemSucessoReferencia = null;
			}
		}
	}
	
	/**
	 * altera uma indicação de referência do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void alterarIndicacaoReferencia (ActionEvent evt) throws ArqException, NegocioException {
		
		indicacaoReferencia.setTipo(tipoIR.charValue());

		boolean ok = validarReferencia();
		boolean livroAssociado = false;

		
		if (ok) {
			
			indicacaoReferencia.setTurma(turma);
			
			if (indicacaoReferencia.getTipo() == IndicacaoReferencia.TIPO_LIVRO && !livroAssociado)
				indicacaoReferencia.setDescricao(indicacaoReferencia.getAutor() + ". <strong>" + indicacaoReferencia.getTitulo() + "</strong>. " + indicacaoReferencia.getEdicao() + ". " + indicacaoReferencia.getEditora() + ". " + indicacaoReferencia.getAno());
			
			
			if (indicacaoReferencia.getAula() != null && indicacaoReferencia.getAula().getId() == 0) {
				indicacaoReferencia.setAula(null);
			}
			
			try {
								
				IndicacaoReferenciaMBean ir = getMBean("indicacaoReferencia");
				ir.atualizarSimples(indicacaoReferencia);
				mensagemSucessoReferencia = "Indicação de referência alterada com sucesso!";
				indicacaoReferencia = new IndicacaoReferencia();
				livroSelecionado = false;
				//tipoIndicacaoReferencia = "" + IndicacaoReferencia.TIPO_INDICACAO_BASICA;
				referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
				referenciasBasicas = referenciasComplementares = null;
				alterarReferencia = false;
			} catch (ArqException e){
				mensagemErroReferencia = e.getMessage();
				mensagemSucessoReferencia = null;
			}
		}
	}
	
	/**
	 * Inicia a edição de uma referência do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void preEditarReferencia (ActionEvent evt) throws ArqException, IllegalAccessException, InvocationTargetException {
		
		Integer indiceIR = getParameterInt("indiceIR", -1);
		if (indiceIR != null && indiceIR >= 0){
		
			setAlterarReferencia(true);
			indicacaoReferencia = new IndicacaoReferencia();

			int indice = 0;
			for (IndicacaoReferencia r : referencias){
				if (r.getId() == indiceIR){
					break;
				}
				indice ++;
			}		
			
			indicacaoReferencia = getGenericDAO().findByPrimaryKey(referencias.get(indice).getId(), IndicacaoReferencia.class);
			indicacaoReferencia.setMaterial(getGenericDAO().refresh(indicacaoReferencia.getMaterial()));
			// O tipo indicacao nao esta sendo trazido pela consulta, sendo necessario modifica-lo manualmente.
			indicacaoReferencia.setTipoIndicacao(referencias.get(indice).getTipoIndicacao());
			tipoIR = indicacaoReferencia.getTipo();
			if (indicacaoReferencia.getTipoIndicacao() == null || indicacaoReferencia.getTipoIndicacao() == IndicacaoReferencia.TIPO_INDICACAO_BASICA)
				tipoIndicacaoReferencia = "1";
			else
				tipoIndicacaoReferencia = "2";
		} else
			addMensagemAjax(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
	}
	
	/**
	 * Zera o tópico que estava sendo inserida.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void limparTopico (ActionEvent evt) throws ArqException {
		topicoAula = new TopicoAula();
		setAlterarTopico(false);
	}
	
	/**
	 * Zera a avaliação que estava sendo inserida.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void limparAvaliacao (ActionEvent evt) throws ArqException {
		avaliacao = new DataAvaliacao();
		alterarAvaliacao = false;
		avaliacoesCombo = null;
	}
	
	/**
	 * Zera a indicação de referência que estava sendo inserida.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void limparReferencia (ActionEvent evt) throws ArqException {
		indicacaoReferencia = new IndicacaoReferencia();
		indicacaoReferencia.setTipo(IndicacaoReferencia.TIPO_LIVRO);
		tipoIndicacaoReferencia = null;
		alterarReferencia = false;
		//tipoIndicacaoReferencia = "" + IndicacaoReferencia.TIPO_INDICACAO_BASICA;
		livroSelecionado = false;
	}
	
	/**
	 * Zera a indicação de referência ao mudar o tipo.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void mudarTipo ( ValueChangeEvent evt) throws ArqException {
		indicacaoReferencia = new IndicacaoReferencia();
		indicacaoReferencia.setTipo(IndicacaoReferencia.TIPO_LIVRO);
		tipoIndicacaoReferencia = null;
		alterarReferencia = false;
		//tipoIndicacaoReferencia = "" + IndicacaoReferencia.TIPO_INDICACAO_BASICA;
		livroSelecionado = false;
	}
	
	/**
	 * Remove uma indicação de referência do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void removerIndicacaoReferencia (ActionEvent evt) throws ArqException{
		int indiceIR = getParameterInt("indiceIR", -1);
		
		if (indiceIR >= 0){
			IndicacaoReferencia ir = null;
			int indice = 0;
			for (IndicacaoReferencia r : referencias){
				if (r.getId() == indiceIR){
					ir = r;
					break;
				}
				indice ++;
			}

			IndicacaoReferenciaMBean referencia = getMBean("indicacaoReferencia");
			referencia.removerSimples(ir);
			alterarReferencia = false;
			
			referencias = getDAO(TurmaVirtualDao.class).findReferenciasTurma(turma);
			referenciasBasicas = referenciasComplementares = null;
			mensagemSucessoReferencia = "Indicação de referência removida com sucesso!";
						
		} else
			addMensagemAjax(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
	}
	
	/**
	 * Adiciona uma indicação de referência ao plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void adicionarTopicoAula (ActionEvent evt) throws ArqException {
		
		adicionarTAEad();
		
		boolean ok = true;
		
		if (!turma.isEad() && StringUtils.isEmpty(topicoAula.getDescricao())){
			mensagemErroTopicoAula = "A descrição do tópico de aula deve ser preenchida.";
			ok = false;
		}
		
		if (ok){
			// Configura as datas selecionadas.
			Date auxData = new Date();
			auxData.setTime(inicioTA);
			topicoAula.setData(auxData);
			
			auxData = new Date();
			auxData.setTime(fimTA);
			topicoAula.setFim(auxData);
			
			topicoAula.setTurma(turma);
			
			try {
				prepareMovimento(ArqListaComando.CADASTRAR);
				MovimentoCadastro mov = new MovimentoCadastro(topicoAula);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				execute(mov);
				
				mensagemSucessoTopicoAula = "Tópico de aula cadastrado com sucesso!";
				
				topicoAula = new TopicoAula();
				topicosAula = (List <TopicoAula>) getGenericDAO().findAtivosByExactField(TopicoAula.class, "turma.id", turma.getId(), "data", "asc");
			} catch (NegocioException e){
				mensagemErroTopicoAula = "";
				for (MensagemAviso m : e.getListaMensagens().getMensagens())
					mensagemErroTopicoAula = m.getMensagem() + "<br/>";
			}
		}
	}
	
	/**
	 * Adiciona a adição de um tópico de aula EAD no plano de curso.<br/><br/>
	 * 
	 * Método não invocado por JSP's
	 * 
	 * @return
	 * @throws ArqException 
	 */
	private void adicionarTAEad () throws DAOException {
		GenericDAO dao = null;
		if ( turma.isEad() ){
			
			Integer id = getParameterInt("form:idItemEad",0);
			dao = getGenericDAO();
			ItemPrograma item = dao.findByPrimaryKey(id, ItemPrograma.class);
			topicoAula = new TopicoAula();
						
			if ( item != null ){
				topicoAula.setDescricao("Aula " + item.getAula());
				topicoAula.setConteudo(item.getConteudo());
			}	
			
			if (StringUtils.isEmpty(topicoAula.getDescricao()))
				mensagemErroTopicoAula = "Um tópico de aula deve ser selecionado.";
		}
	}
	
	/**
	 * Altera um tópico de aula do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void alterarTopicoAula (ActionEvent evt) throws ArqException {
		
		boolean ok = true;
		
		if (StringUtils.isEmpty(topicoAula.getDescricao())){
			mensagemErroTopicoAula = "A descrição do tópico de aula deve ser preenchida.";
			ok = false;
		}
		
		if ( turma.getPolo() != null ){
			mensagemErroTopicoAula = "Tópicos de Aula de turmas de ensino à distância não podem ser modificados.";
			ok = false;
		}
		
		if (ok){
			// Configura as datas selecionadas.
			Date auxData = new Date();
			auxData.setTime(inicioTA);
			topicoAula.setData(auxData);
			
			auxData = new Date();
			auxData.setTime(fimTA);
			topicoAula.setFim(auxData);
			
			topicoAula.setTurma(turma);
			
			try {
				prepareMovimento(ArqListaComando.ALTERAR);
				MovimentoCadastro mov = new MovimentoCadastro(topicoAula);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				execute(mov);
				
				mensagemSucessoTopicoAula = "Tópico de aula alterado com sucesso!";
				
				topicoAula = new TopicoAula();
				topicosAula = (List <TopicoAula>) getGenericDAO().findAtivosByExactField(TopicoAula.class, "turma.id", turma.getId(), "data", "asc");
				alterarTopico = false;
			} catch (NegocioException e){
				mensagemErroTopicoAula = "";
				for (MensagemAviso m : e.getListaMensagens().getMensagens())
					mensagemErroTopicoAula = m.getMensagem() + "<br/>";
			}
		}
	}	
	
	/**
	 * Inicia a edição de um tópico de aula do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void preEditarTopicoAula (ActionEvent evt) throws ArqException, IllegalAccessException, InvocationTargetException {
		
		Integer indiceTA = getParameterInt("indiceTA");
		if (indiceTA != null && indiceTA >= 0){
			setAlterarTopico(true);
			topicoAula = new TopicoAula();
			BeanUtils.copyProperties(topicoAula, topicosAula.get(indiceTA));
			
			String conteudo = StringUtils.stripHtmlTags(topicoAula.getConteudo());
			topicoAula.setConteudo(StringUtils.unescapeHTML(conteudo));
			
			inicioTA = topicoAula.getData().getTime();
			fimTA = topicoAula.getFim().getTime();
			inicioTACombo = null;
			fimTACombo = null;
		} else
			addMensagemAjax(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
	}
	
	/**
	 * Remove uma indicação de referência do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void removerTopicoAula (ActionEvent evt) throws ArqException {
		
		Integer indiceTA = getParameterInt("indiceTA");
		if (indiceTA != null && indiceTA >= 0){
		
			TopicoAula ta = topicosAula.get(indiceTA);
			
			try { 
				prepareMovimento(ArqListaComando.DESATIVAR);
				MovimentoCadastro mov = new MovimentoCadastro(ta);
				mov.setCodMovimento(ArqListaComando.DESATIVAR);
				execute(mov);
				
				addMensagemAjax(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Aula");
				
				int aux = indiceTA;
			
				topicosAula.remove(aux);
				
			} catch (NegocioException e){
				addMensagensAjax(e.getListaMensagens());
			}
		} else
			addMensagemAjax(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
	}
	
	/**
	 * Adiciona uma indicação de referência ao plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void adicionarAvaliacao (ActionEvent evt) throws ArqException {
				
		boolean ok = validarAvaliacao(false);
		
		if (ok){
			avaliacao.setTurma(turma);
			
			try {
				prepareMovimento(ArqListaComando.CADASTRAR);
				MovimentoCadastro mov = new MovimentoCadastro(avaliacao);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				
				execute (mov);
				
				//addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				mensagemSucessoAvaliacao = "Avaliação cadastrada com sucesso!";
				
	 			avaliacao = new DataAvaliacao();
	 			avaliacoes = getDAO(TurmaVirtualDao.class).buscarDatasAvaliacao(turma);
	 			
			} catch (NegocioException e){
				mensagemErroAvaliacao = "";
				for (MensagemAviso m : e.getListaMensagens().getMensagens())
					mensagemErroAvaliacao += m.getMensagem() + "<br/>";
			}
		}
	}

	/**
	 * Método utilizado para validar a referência no plano de curso.
	 * <br/>
	 * Não invocado por JSP(s)
	 * @return
	 */
	private boolean validarAvaliacao(boolean alterar) {
		boolean ok = true;
		
		// Se o docente não selecionou a avaliação.
		mensagemErroAvaliacao = "";
		if (avaliacao.getDescricao().equals("0")){
			mensagemErroAvaliacao += "Informe a descrição da avaliação<br/>";
			ok = false;
		}
		
		if (avaliacao.getData() == null){
			mensagemErroAvaliacao += "Informe a data da avaliação<br/>";
			ok = false;
		}
		
		if (StringUtils.isEmpty(avaliacao.getHora())){
			mensagemErroAvaliacao += "Informe a hora da avaliação<br/>";
			ok = false;
		}
		
		if (!alterar && avaliacoes.size() >= numUnidades){
			mensagemErroAvaliacao += "Esta turma já tem o número máximo de avaliações<br/>";
			ok = false;
		}
		
		if (ok)
			for (DataAvaliacao a : avaliacoes){
				
				// Verifica se a avaliação foi inserida via turma virtual.
				String exp = "[";
				for (int i = 1; i <= numUnidades; i++)
					exp += i;
				exp += "]ª Avaliação";
			    Pattern padrao = Pattern.compile(exp);  
			    Matcher pesquisa = padrao.matcher(a.getDescricao());  
				
				if (a.getId() != avaliacao.getId() && a.getDescricao().equals(avaliacao.getDescricao())){
					addMensagemAjax(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Avaliação");
					mensagemErroAvaliacao += "Esta avaliação já foi cadastrada<br/>";
					ok = false;
				}
				
				if (pesquisa.matches() && a.getDescricao().compareTo(avaliacao.getDescricao()) < 0 && a.getData().compareTo(avaliacao.getData()) > 0) {
					addMensagemErroAjax("A " + avaliacao.getDescricao() + " não pode ser realizada antes da " + a.getDescricao() + ".");
					mensagemErroAvaliacao += "A " + avaliacao.getDescricao() + " não pode ser realizada antes da " + a.getDescricao() + "<br/>";
					ok = false;
				}
				
				if (pesquisa.matches() &&  a.getDescricao().compareTo(avaliacao.getDescricao()) > 0 && a.getData().compareTo(avaliacao.getData()) < 0) {
					mensagemErroAvaliacao += "A " + avaliacao.getDescricao() + " não pode ser realizada depois da " + a.getDescricao() + "<br/>";
					ok = false;
				}
				
				if (a.getId() != avaliacao.getId() && !a.getDescricao().equals(avaliacao.getDescricao()) && a.getData().compareTo(avaliacao.getData()) == 0) {
					mensagemErroAvaliacao += "Não pode existir mais de uma avaliação no mesmo dia<br/>";
					ok = false;
				}
				
			}
		
		if (StringUtils.isEmpty(mensagemErroAvaliacao))
			mensagemErroAvaliacao = null;
		return ok;
	}
	
	/**
	 * Altera um tópico de aula do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void alterarDataAvaliacao (ActionEvent evt) throws ArqException {
		
		boolean ok = validarAvaliacao(true);
		
		if (ok){
			avaliacao.setTurma(turma);
			
			try {
				prepareMovimento(ArqListaComando.ALTERAR);
				MovimentoCadastro mov = new MovimentoCadastro(avaliacao);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				
				execute (mov);
				
				//addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				mensagemSucessoAvaliacao = "Avaliação alterada com sucesso!";
				
	 			avaliacao = new DataAvaliacao();
	 			avaliacoes = getDAO(TurmaVirtualDao.class).buscarDatasAvaliacao(turma);
	 			alterarAvaliacao = false;
	 			avaliacoesCombo = null;
	 			
			} catch (NegocioException e){
				mensagemErroAvaliacao = "";
				for (MensagemAviso m : e.getListaMensagens().getMensagens())
					mensagemErroAvaliacao += m.getMensagem() + "<br/>";
			}
		}
	}	
	
	/**
	 * Inicia a edição de um tópico de aula do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void preEditarAvaliacao (ActionEvent evt) throws ArqException, IllegalAccessException, InvocationTargetException {
		
		Integer indiceAV = getParameterInt("indiceAV", -1);
		if (indiceAV != null && indiceAV >= 0){
			
			setAlterarAvaliacao(true);
			avaliacao = new DataAvaliacao();
			BeanUtils.copyProperties(avaliacao, avaliacoes.get(indiceAV));
			
			String exp = "[";
			for (int i = 1; i <= numUnidades; i++)
				exp += i;
			exp += "]ª Avaliação";
			
			Pattern padrao = Pattern.compile(exp);  
			Matcher pesquisa = padrao.matcher(avaliacao.getDescricao());   
		  
			if (!pesquisa.matches()){
				avaliacoesCombo = new ArrayList <SelectItem> ();
				avaliacoesCombo.add(new SelectItem(avaliacao.getDescricao()));
				for (int i = 1; i <= numUnidades; i++)
					avaliacoesCombo.add(new SelectItem(i+"ª Avaliação"));
			}	
		} else
			addMensagemAjax(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
	}
	
	/**
	 * Remove uma indicação de referência do plano.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void removerAvaliacao (ActionEvent evt) throws ArqException{
		int indiceAV = getParameterInt("indiceAV", -1);
		
		if (indiceAV >= 0){
			DataAvaliacao av = avaliacoes.get(indiceAV);
			
			try {
				prepareMovimento(ArqListaComando.ALTERAR);  
				av.setAtivo(false);  
				MovimentoCadastro mov = new MovimentoCadastro(av);  
				mov.setCodMovimento(ArqListaComando.ALTERAR);  
				execute (mov);
				
				//addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Avaliação");
			
				avaliacoes.remove(indiceAV);
				
			} catch (NegocioException e){
				addMensagensAjax(e.getListaMensagens());
			}
		} else
			addMensagemAjax(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
	}
	
	/**
	 * Realiza uma busca no acervo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 */
	public String pesquisarNoAcervo (){
		titulo = null;
		PesquisaInternaBibliotecaMBean pBean = getMBean ("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, "/ava/PlanoCurso/paginaAcoesBuscaAcervoBiblioteca.jsp");
	}
	
	////////////////////  Método na pesqusia no acervo da Biblioteca //////////////////////////
	
	
	/**
	 * Após recuperar o fluxo do título selecionado, prepara a indicação de referência com os dados do título. 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/paginaAcoesBuscaAcervoBiblioteca.jsp
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		mensagemSucessoReferencia = null;

		scrollIR = true;
		
		if (turma == null) {
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			turma = tBean.getTurma();	
		}
			
		// Se selecionou um título, vai preencher com os dados do Acervo
		if (titulo != null){
			
			titulo = BibliotecaUtil.obtemDadosTituloCache(titulo.getIdTituloCatalografico());
			
			TituloCatalografico tituloCatalografico = getGenericDAO().findByPrimaryKey(titulo.getIdTituloCatalografico(), TituloCatalografico.class);
		
			indicacaoReferencia.setDescricao(new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloCatalografico, true));
			indicacaoReferencia.setTituloCatalografico(new TituloCatalografico (titulo.getIdTituloCatalografico()));
			indicacaoReferencia.setTitulo(titulo.getTitulo());
			indicacaoReferencia.setAutor(titulo.getAutor());
			
			StringBuilder editoraTemp =  new StringBuilder();
			
			for (String editora : titulo.getEditorasFormatadas()) {
				editoraTemp.append(editora+" ");
			} 
			indicacaoReferencia.setEditora(editoraTemp.toString());
			
			
			StringBuilder anoTemp =  new StringBuilder();
			
			for (String ano : titulo.getAnosFormatados()) {
				anoTemp.append(ano+" ");
			} 
			indicacaoReferencia.setAno(anoTemp.toString());
			
			indicacaoReferencia.setEdicao(titulo.getEdicao());
			
			
		// Se não passou os dados do acervo da biblitoeca, vai abrir os campos para o usuário digitar
		} else {
			
			indicacaoReferencia.setTituloCatalografico(null);
			indicacaoReferencia.setTitulo(null);
			indicacaoReferencia.setAutor(null);
			indicacaoReferencia.setEditora(null);
			indicacaoReferencia.setAno(null);
			indicacaoReferencia.setEdicao(null);
			
		}
		
		tipoIndicacaoReferencia = null;
		
		livroSelecionado = true;   
		
		return forward(getDirBase() + "/form.jsf");
	}

	
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.titulo = titulo;
	}

	
	/**
	 * Ação ao clicar no botão voltar na tela da pesquisa no acervo.
	 * Método não chamado por JSPs
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBusca()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {	
		// Volta para a página de formulário do cadastro de plano de curso, caso o usuário utiliza o botão voltar na busca no acervo da biblioteca
		livroSelecionado = false;
		return forward(getDirBase() + "/form.jsf");
	}
	
	/**
	 *  Método para indicar se o botão voltar deve ser mostrado ao não na tela da pesquisa no acervo.
	 *  Método não chamado por JSPs
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * Retorna uma lista de selectItems com as datas de aulas da turma selecionada.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getAulasCombo() throws NegocioException, DAOException {
		
		if (aulasCombo == null) {
			
			aulasCombo = new ArrayList<SelectItem>();			
			Set<Date> datasAulas = null;
			
			if (turma == null){
				addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
				" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectJSF(getSubSistema().getLink());
				return aulasCombo;
			}	
			
			if(turma.getSubturmas() == null || turma.getSubturmas().isEmpty())
				datasAulas = TurmaUtil.getDatasAulasSemFeriados(turma, CalendarioAcademicoHelper.getCalendario(turma), true);
			else {
				datasAulas = new TreeSet<Date>();
				for (Turma t : turma.getSubturmas()) {
					datasAulas.addAll(TurmaUtil.getDatasAulasSemFeriados(t, CalendarioAcademicoHelper.getCalendario(t), true));
				}
			}
			
			// Caso não encontre horários de aula, carregar todos os dias entre o começo e o final da turma
			if(datasAulas == null || datasAulas.isEmpty()) {
				
				if (turma.getDataInicio() == null || turma.getDataFim() == null){
					addMensagemErro("Esta Turma não está com horários definidos.");
					redirectJSF(getSubSistema().getLink());
					return aulasCombo;
				}
				
				verificaAulasLancadas = false;
				datasAulas = CalendarUtils.getDiasUteis(turma.getDataInicio(), turma.getDataFim());
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			for (Date data : datasAulas) {
				aulasCombo.add(new SelectItem(data.getTime(), sdf.format(data)));
			}
		}

		return aulasCombo;
	}
	
	/**
	 * Retorna uma lista de selectItems com as datas de aulas da turma selecionada, 
	 * com a data início do tópico atual em destaque para a alteração.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getInicioTACombo() throws NegocioException, DAOException {
		
		if (inicioTACombo == null) {
			
			inicioTACombo = new ArrayList<SelectItem>();			
			Set<Date> datasAulas = null;
					
			if(turma.getSubturmas() == null || turma.getSubturmas().isEmpty())
				datasAulas = TurmaUtil.getDatasAulasSemFeriados(turma, CalendarioAcademicoHelper.getCalendario(turma), true);
			else {
				datasAulas = new TreeSet<Date>();
				for (Turma t : turma.getSubturmas()) {
					datasAulas.addAll(TurmaUtil.getDatasAulasSemFeriados(t, CalendarioAcademicoHelper.getCalendario(t), true));
				}
			}
			
			// Caso não encontre horários de aula, carregar todos os dias entre o começo e o final da turma
			if(datasAulas == null || datasAulas.isEmpty()) {				
				verificaAulasLancadas = false;
				datasAulas = CalendarUtils.getDiasUteis(turma.getDataInicio(), turma.getDataFim());
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			if (alterarTopico){
				inicioTACombo.add(new SelectItem(inicioTA, sdf.format(inicioTA)));
			}
			
			for (Date data : datasAulas) {
				inicioTACombo.add(new SelectItem(data.getTime(), sdf.format(data)));
			}
		}

		return inicioTACombo;
	}
	
	/**
	 * Retorna uma lista de selectItems com as datas de aulas da turma selecionada, 
	 * com a data final do tópico atual em destaque para a alteração.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getFimTACombo() throws NegocioException, DAOException {
		
		if (fimTACombo == null) {
			
			fimTACombo = new ArrayList<SelectItem>();			
			Set<Date> datasAulas = null;
			
			if(turma.getSubturmas() == null || turma.getSubturmas().isEmpty())
				datasAulas = TurmaUtil.getDatasAulasSemFeriados(turma, CalendarioAcademicoHelper.getCalendario(turma), true);
			else {
				datasAulas = new TreeSet<Date>();
				for (Turma t : turma.getSubturmas()) {
					datasAulas.addAll(TurmaUtil.getDatasAulasSemFeriados(t, CalendarioAcademicoHelper.getCalendario(t), true));
				}
			}
			
			// Caso não encontre horários de aula, carregar todos os dias entre o começo e o final da turma
			if(datasAulas == null || datasAulas.isEmpty()) {							
				verificaAulasLancadas = false;
				datasAulas = CalendarUtils.getDiasUteis(turma.getDataInicio(), turma.getDataFim());
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			if (alterarTopico){
				fimTACombo.add(new SelectItem(fimTA, sdf.format(fimTA)));
			}
			
			for (Date data : datasAulas) {
				fimTACombo.add(new SelectItem(data.getTime(), sdf.format(data)));
			}
		}

		return fimTACombo;
	}
	
	/**
	 * Retorna o diretório base.
	 * 
	 * Método não invocado por JSP´s
	 */
	@Override
	public String getDirBase() {
		return "/ava/PlanoCurso";
	}
	
	/**
	 * Redefinição da página após o cadastrar.
	 * 
	 * Método não invocado por JSP´s
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		return forward(TurmaVirtualMBean.LISTA_TURMAS_ABERTAS);
	}
	
	/**
	 * Redefinição da página após o remover.
	 * 
	 * Método não invocado por JSP´s
	 * @return
	 */
	@Override
	protected String forwardRemover() {
		return TurmaVirtualMBean.LISTA_TURMAS_ABERTAS;
	}
	
	/**
	 * Checa as permissões do usuário para realizar o gerenciamento do plano de curso.
	 * 
	 * Método não invocado por JSP´s
	 * @throws SegurancaException
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DOCENTE);
	}

	public void setAcessoTurmaVirtual(boolean acessoTurmaVirtual) {
		this.acessoTurmaVirtual = acessoTurmaVirtual;
	}

	public boolean isAcessoTurmaVirtual() {
		return acessoTurmaVirtual;
	}

	public boolean isAcessoTurmasAbertas() {
		return acessoTurmasAbertas;
	}

	public void setAcessoTurmasAbertas(boolean acessoTurmasAbertas) {
		this.acessoTurmasAbertas = acessoTurmasAbertas;
	}

	public List<IndicacaoReferencia> getReferencias() {
		return referencias;
	}
	
	/**
	 * Pega uma lista de referencias basicas.
	 * @return List <IndicacaoReferencia>
	 */
	public List <IndicacaoReferencia> getReferenciasBasicas (){
		if (referenciasBasicas == null){
			referenciasBasicas = new ArrayList <IndicacaoReferencia> ();
			
			for (IndicacaoReferencia r : referencias)
					if (r.getTipoIndicacao() == null || IndicacaoReferencia.TIPO_INDICACAO_BASICA == r.getTipoIndicacao())
					referenciasBasicas.add(r);
		}
		
		return referenciasBasicas;
	}
	
	/**
	 * Pega uma lista de referencias caomplementares.
	 * @return List <IndicacaoReferencia>
	 */
	public List <IndicacaoReferencia> getReferenciasComplementares (){
		if (referenciasComplementares == null){
			referenciasComplementares = new ArrayList <IndicacaoReferencia> ();
			
			for (IndicacaoReferencia r : referencias)
				if (r.getTipoIndicacao() != null && IndicacaoReferencia.TIPO_INDICACAO_COMPLEMENTAR == r.getTipoIndicacao())
					referenciasComplementares.add(r);
		}
		
		return referenciasComplementares;
	}

	/**
	 * muda o valor da Lista com todas as referências da turma pelo valor passado. 
	 * @param referencias
	 */
	public void setReferencias(List<IndicacaoReferencia> referencias) {
		this.referencias = referencias;
		referenciasBasicas = referenciasComplementares = null;
	}

	public IndicacaoReferencia getIndicacaoReferencia() {
		return indicacaoReferencia;
	}

	public void setIndicacaoReferencia(IndicacaoReferencia indicacaoReferencia) {
		this.indicacaoReferencia = indicacaoReferencia;
	}
	
	/**
	 * Retorna a lista de todos os tipos de indicação de referência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * @return
	 */
	public List <SelectItem> getListaTiposIR (){
		return toSelectItems(IndicacaoReferencia.getAllTipos());
	}

	public String getTipoIR() {
		return tipoIR == null ? null : ""+tipoIR;
	}

	public void setTipoIR(String tipoIR) {
		this.tipoIR = tipoIR == null ? null : tipoIR.toCharArray()[0];
	}
	
	public List<TopicoAula> getTopicosAula() {
		return topicosAula;
	}

	public void setTopicosAula(List<TopicoAula> topicosAula) {
		this.topicosAula = topicosAula;
	}

	public List<DataAvaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<DataAvaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public TopicoAula getTopicoAula() {
		return topicoAula;
	}

	public void setTopicoAula(TopicoAula topicoAula) {
		this.topicoAula = topicoAula;
	}

	public DataAvaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(DataAvaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Long getInicioTA() {
		return inicioTA;
	}

	public void setInicioTA(Long inicioTA) {
		this.inicioTA = inicioTA;
	}

	public Long getFimTA() {
		return fimTA;
	}

	public void setFimTA(Long fimTA) {
		this.fimTA = fimTA;
	}
	
	public List <SelectItem> getTopicosAulaSI (){
		return toSelectItems(topicosAula, "id", "descricao");
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	
	/**
	 * Retorna os SelectItems para os nomes das avaliações.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/ava/PlanoCurso/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <SelectItem> getAvaliacoesCombo () throws DAOException{
		
		if (avaliacoesCombo == null || avaliacoesCombo.isEmpty()){
		
			avaliacoesCombo = new ArrayList <SelectItem> ();
			
			if (numUnidades != null)
				for (int i = 1; i <= numUnidades; i++)
					avaliacoesCombo.add(new SelectItem(i+"ª Avaliação"));
		}
		
		return avaliacoesCombo;
	}

	public boolean isLivroSelecionado() {
		return livroSelecionado;
	}

	public void setLivroSelecionado(boolean livroSelecionado) {
		this.livroSelecionado = livroSelecionado;
	}
	
	/**Indica se a tela deve rolar até o formulário de inserção de indicação de referência.
	 * @return true caso a tela deva rola e falso caso contrário.
	 */
	public boolean isScrollIR (){
		boolean aux =  scrollIR;
		scrollIR = false;
		return aux;
	}

	public String getTipoIndicacaoReferencia() {
		return tipoIndicacaoReferencia;
	}

	public void setTipoIndicacaoReferencia(String tipoIndicacaoReferencia) {
		this.tipoIndicacaoReferencia = tipoIndicacaoReferencia;
	}

	public void setTipoIR(Character tipoIR) {
		this.tipoIR = tipoIR;
	}

	public void setCarregadaImportacao(boolean carregadaImportacao) {
		this.carregadaImportacao = carregadaImportacao;
	}

	public boolean isCarregadaImportacao() {
		return carregadaImportacao;
	}

	public String getMensagemSucessoTopicoAula() {
		String m = mensagemSucessoTopicoAula;
		mensagemSucessoTopicoAula = null;
		return m;
	}

	public void setMensagemSucessoTopicoAula(String mensagemSucessoTopicoAula) {
		this.mensagemSucessoTopicoAula = mensagemSucessoTopicoAula;
	}

	public String getMensagemErroTopicoAula() {
		String m = mensagemErroTopicoAula;
		mensagemErroTopicoAula = null;
		return m;
	}

	public void setMensagemErroTopicoAula(String mensagemErroTopicoAula) {
		this.mensagemErroTopicoAula = mensagemErroTopicoAula;
	}

	public String getMensagemSucessoAvaliacao() {
		String m = mensagemSucessoAvaliacao;
		mensagemSucessoAvaliacao = null;
		return m;
	}

	public void setMensagemSucessoAvaliacao(String mensagemSucessoAvaliacao) {
		this.mensagemSucessoAvaliacao = mensagemSucessoAvaliacao;
	}

	public String getMensagemErroAvaliacao() {
		String m = mensagemErroAvaliacao;
		mensagemErroAvaliacao = null;
		return m;
	}

	public void setMensagemErroAvaliacao(String mensagemErroAvaliacao) {
		this.mensagemErroAvaliacao = mensagemErroAvaliacao;
	}

	public String getMensagemSucessoReferencia() {
		return mensagemSucessoReferencia;
	}

	public void setMensagemSucessoReferencia(String mensagemSucessoReferencia) {
		this.mensagemSucessoReferencia = mensagemSucessoReferencia;
	}

	public String getMensagemErroReferencia() {
		String m = mensagemErroReferencia;
		mensagemErroReferencia = null;
		return m;
	}

	public void setMensagemErroReferencia(String mensagemErroReferencia) {
		this.mensagemErroReferencia = mensagemErroReferencia;
	}

	public void setAlterarTopico(boolean alterarTopico) {
		this.alterarTopico = alterarTopico;
	}

	public boolean isAlterarTopico() {
		return alterarTopico;
	}

	public void setAlterarAvaliacao(boolean alterarAvaliacao) {
		this.alterarAvaliacao = alterarAvaliacao;
	}

	public boolean isAlterarAvaliacao() {
		return alterarAvaliacao;
	}

	public void setAlterarReferencia(boolean alterarReferencia) {
		this.alterarReferencia = alterarReferencia;
	}

	public boolean isAlterarReferencia() {
		return alterarReferencia;
	}
	
	/**
	 * Busca os tópicos de aula e define os que serão visíveis e os que não serão.<br/><br/>
	 * 
	 */
	public List<ItemPrograma> getTopicosEad() {
		
		if (turma == null)
			return new ArrayList<ItemPrograma>();
		
		CursoDao cursoDao = getDAO(CursoDao.class);
		List<ItemPrograma> listagemItensProgramaPorDisciplina = cursoDao.findListaItemProgramaByDisciplina(turma.getDisciplina().getId());
		
		if ( listagemItensProgramaPorDisciplina != null ) {
			Collections.sort(listagemItensProgramaPorDisciplina, new Comparator<ItemPrograma>(){
				public int compare(ItemPrograma i1, ItemPrograma i2) {
					return i1.getAula() - i2.getAula();
				}
			});
		}
		
		return listagemItensProgramaPorDisciplina; 
	}
}
