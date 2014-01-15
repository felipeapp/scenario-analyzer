/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.AulaExtraDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioTurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.MaterialTurmaDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.jsf.ForumTurmaMBean;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.negocio.MovimentoGerenciaTopicosAula;
import br.ufrn.sigaa.ava.questionarios.jsf.QuestionarioTurmaMBean;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
/**
 * Managed bean para cadastro de Tópicos de Aula.
 * Controla a inserção, remoção, busca e gerencia de tópicos de aula.
 * Também auxilia na associação de fóruns, enquetes e materiais a um tópico.
 * 
 * @see TopicoAula
 * @author David Pereira
 */
@Component("topicoAula") @Scope("session")
public class TopicoAulaMBean extends CadastroTurmaVirtual<TopicoAula> {
	
	/** Ações que o usuário pode realizar num tópico de aula.*/
	/** Adiciona um arquivo no tópico de aula. */
	public static final int ADICIONAR_ARQUIVO = 1;
	/** Cadastra um tópico filho. */
	public static final int CADASTRAR_SUB_TOPICO = 2;
	/** Cadastra uma referência. */
	public static final int CADASTRAR_REFERENCIA = 3;
	/** Cadastra uma tarefa. */
	public static final int CADASTRAR_TAREFA = 4;
	/** Cadastra um contéudo. */
	public static final int CADASTRAR_CONTEUDO = 5;
	/** Edita o tópico de aula. */
	public static final int EDITAR_TOPICO = 6;
	/** Remove o tópico selecionado. */
	public static final int REMOVER_TOPICO = 7;
	/** Cadastra um fórum. */
	public static final int CADASTRAR_FORUM = 8;
	/** Lança a frequencia da aula. */
	public static final int LANCAR_FREQUENCIA = 9;
	/** Cadastra uma enquete. */
	public static final int CADASTRAR_ENQUETE = 10;
	/** Cadastra um questionário. */
	public static final int CADASTRAR_QUESTIONARIO = 11;
	/** Cadastrar um novo tópico de aula. */
	public static final int CADASTRAR_TOPICO = 12;
	/** Cadastra uma notícia. */
	public static final int CADASTRAR_NOTICIA = 13;
	/** Cadastra um vídeo. */
	public static final int CADASTRAR_VIDEO = 14;
	/** Cadastra um rótulo. */
	public static final int CADASTRAR_ROTULO = 15;
	/** Cadastra um chat. */
	public static final int CADASTRAR_CHAT = 16;
	/** Adiciona um arquivo no tópico de aula. */
	public static final int ADICIONAR_ARQUIVOS = 17;
	/** Adiciona um arquivo no tópico de aula. */
	public static final int ADICIONAR_ARQUIVO_DO_PORTA_ARQUIVOS = 18;
	
	
	/** Link para a página de gerência dos tópicos de aula em lote. */
	public static final String PAGINA_FORM_GERENCIAR_EM_LOTE = "/ava/TopicoAula/gerenciarEmLote.jsp";
	/** Link para página onde os tópicos de aula são listados. */
	public static final String PAGINA_LISTA_TOPICOS = "/ava/TopicoAula/listar.jsf";
	/** Link para a página de impressão dos tópicos de aula. */
	public static final String PAGINA_IMPRIMIR_TOPICOS = "/ava/TopicoAula/topicos_impressao.jsp";
	
	/** Lista de itens contendo as datas dos tópicos de aula da turma. */
	private List<SelectItem> itens;
	/** Lista de tópicos de aula. */
	private List<TopicoAula> topicosAulas; 
	
	/** Id que auxilia na edição de um tópico de aula. */
	private int idTopico = 0;
	
	/** Dados do tópico de aula em lote. */
	private String dadosTopicosEmLote;
	
	/** Data de início e fim do tópico de aula */
	private SelectItem dataInicial, dataFinal;
	
	/** Array de ids dos docentes selecionados na tela de criação de um tópico. */
	private List<String> idsDocentes;
	
	/** Lista o idDocenteTurma e nome dos docentes da turma. */
	private List <SelectItem> docentes;
	
	/** Id do tópico selecionado. */
	private int idTopicoSelecionado;
	
	/** Lista com todos os itens do programa da discipliana - para EAD */
	private List<ItemPrograma> listagemItensProgramaPorDisciplina;
	/** Conjunto com as datas das aulas. */
	private Set<Date> datasAulas;
	/** Conjunto com os meses que terão aula. */
	private Set<Integer> mesesAulas;
	/** Lista de tópicos de aula não publicados. */
	private List<TopicoAula> topicosNaoPublicados;
	/** Lista com todos os tópicos da turma. */
	private List<TopicoAula> todosTopicosTurma = new ArrayList<TopicoAula>();
	
	/** Indica se está exibindo uma tela para impressão. */
	private boolean imprimir;
	
	/** A "idTopico_Ação" a ser realizada sobre um tópico */
	private String acaoTopico;
		
	/** Indica se o topico está sendo removido através de uma aula extra. */
	private boolean remocaoAulaExtra = false;
		
	/**
	 * Verifica se a data final é inferior a data inicial, se for, deixa igual a data inicial.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @param evt
	 */
	public void ajustaDataFinal(ValueChangeEvent evt) {
		object.setFim((Date)evt.getNewValue());
	}
	
	/**
	 * Busca os tópicos de aula e define os que serão visíveis e os que não serão.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.ear/sigaa.war/ava/menu.jsp
	 */
	@Override
	public List<TopicoAula> lista() {
		CursoDao cursoDao = getDAO(CursoDao.class);
		listagemItensProgramaPorDisciplina = cursoDao.findListaItemProgramaByDisciplina(turma().getDisciplina().getId());
		
		List<TopicoAula> listaTopicosVisiveis = getDAO(TopicoAulaDao.class).findByTurma(turma());
		return listaTopicosVisiveis; 
	}
	
	/**
	 * Sobrescrito para realizar a verificação se foram cadastrados os itens do programa do componente da turma selecionada.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TopicoAula/listar.jsp</li>
	 * </ul>
	 */
	@Override
	public String novo (){
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		if (tBean.getTurma().getPolo() != null){
			lista();
			if (isEmpty(listagemItensProgramaPorDisciplina)){
				addMensagem(MensagensTurmaVirtual.NENHUM_ITEM_PROGRAMA);
				return null;
			}
		}
		
		super.novo();
		
		initDocentes();
		
		return null;
	}

	/** Valida um tópico aula. */
	private void validarTopico() throws DAOException {
		FrequenciaAlunoDao fDao = null;
		try {
			fDao = getDAO(FrequenciaAlunoDao.class);
			if (object.isAulaCancelada()){
				boolean frequenciaLancada = false;
				if (object.getData() != null && !turma().isAgrupadora())
					frequenciaLancada = fDao.diaTemFrequencia(object.getData(), turma());
				else {
					List<Integer> idsTurma = new ArrayList<Integer>();
					if ( turma().getSubturmas() != null )
						for ( Turma t : turma().getSubturmas())
							idsTurma.add(t.getId());
					frequenciaLancada = fDao.diaTemFrequencia(object.getData(), idsTurma);
				}
				if ( frequenciaLancada )
					addMensagemErro("Já foi lançada a frequência para a aula neste dia. Não é possível cancelar uma aula que a frequência já foi lançada.");
			}
		} finally {
			if ( fDao != null )
				fDao.close();
		}
		
	}
	
	/**
	 * Inicia os dados necessários de docentes, adicionando o logado no tópico e 
	 * o colocando como selecionado caso a turma possua mais de um docente. <br />
	 * Método não invocado por JSPs.
	 */
	public void initDocentes() {
		idsDocentes = new ArrayList<String>();
		
		DocenteTurmaDao dao = null;
		
		// Faz o formulário iniciar com o docente logado sendo o docente que ministrará a aula.
		try {
			dao = getDAO(DocenteTurmaDao.class);
			List <DocenteTurma> dts = (List<DocenteTurma>) dao.findByTurma(turma().getId());
			
			if(dts.size() == 0) {
				object.adicionarDocenteTurma(null);
			}
			else {
				for (DocenteTurma dt : dts){
					int idPessoa = dt.getDocente() != null ? dt.getDocente().getPessoa().getId() : dt.getDocenteExterno().getPessoa().getId();
					if (getUsuarioLogado().getPessoa().getId() == idPessoa) {
						idsDocentes.add(String.valueOf(dt.getId()));
						object.adicionarDocenteTurma(dt);
					}
				}
			}
			
		} catch (DAOException e){
			tratamentoErroPadrao(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Verifica se existe uma aula extra associada ao tópico passado e, caso verdadeiro, popula os dados no objeto trabalhado.
	 */
	private void initAulaExtra(int idTopico) {
		AulaExtraDao dao = getDAO(AulaExtraDao.class);
		
		try {
			AulaExtra aula = dao.findByTopicoAula(idTopico);
			
			object.setAulaExtra(aula);
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
	}
				
	/**
	 * Exibe um tópico de aula com sua descrição completa.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp 
	 */
	@Override
	public String mostrar() throws DAOException {
		int idTopico = 0;
		TurmaVirtualDao dao = null;
		
		try{
			dao = getDAO(TurmaVirtualDao.class);
			//Se o usuário estiver editando o tópico, ele já está armazenado como #object.
			idTopico = getParameterInt("id");		
			
			registrarLogAcessoDiscenteTurmaVirtual(getClasse().getName(), idTopico, turma().getId());
	        object = getGenericDAO().findByPrimaryKey(idTopico, getClasse());
	        
			registrarAcao(object.getDescricao(), EntidadeRegistroAva.TOPICO_AULA, AcaoAva.ACESSAR, idTopico);
	        
			if(isEmpty(object) || !object.getAtivo().booleanValue()) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return cancelar();
			}
	        
			Map<TopicoAula, List<AbstractMaterialTurma>> materiais = getDAO(TurmaVirtualDao.class).findMateriaisByTurma(turma());
			
			List<AbstractMaterialTurma> materiaisTopico = materiais.get(object);
			if (!isEmpty(materiaisTopico))
				object.getMateriais().addAll(materiaisTopico);
			
	        if (acessoPublico())
	            return forward("/ava/" + getClasse().getSimpleName() + "/mostrarPublico.jsp");
	        else
	            return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
		} finally {
			if ( dao != null )
				dao.close();
		}
    }
	
	/**
	 * Padrão Specification. Exibe as mensagens de informação para o usuário.<br/><br/>
	 * 
	 * Não invocado por JSPs.
	 */
	@Override
	protected String notifyView(Notification notification) {
		addMensagens(notification.getMessages());
		if (object.getTopicoPai() == null)
			object.setTopicoPai(new TopicoAula());
		return null;
	}
	
	/**
	 * Antes de remover, limpa a sessão.<br/><br/>
	 * 
	 * Não invocado por JSPs.
	 */
	@Override
	protected void antesRemocao() {		
			getGenericDAO().clearSession();
	}
	
	/**
	 * Remove os tópicos de aula filhos.<br/><br/>
	 * 
	 *  Não invocado por JSPs. 
	 */
	
	public String removerTopicosFilhos( TopicoAula topicoPai ) throws ArqException {
		
		List<TopicoAula> topicosFilhos = (List<TopicoAula>) getGenericDAO().findAtivosByExactField(TopicoAula.class, "topicoPai.id", topicoPai.getId());
		
		for (TopicoAula topicoFilho : topicosFilhos)
		{
			if ( topicoFilho.getTopicoPai().equals(topicoPai) )
			{
				topicoFilho.setAtivo(false);

				Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, topicoFilho, getEspecificacaoRemocao());
				prepareMovimento(SigaaListaComando.ATUALIZAR_AVA);
				
				if (notification.hasMessages())
					return notifyView(notification);
				
				String notify = removerTopicosFilhos(topicoFilho);
				
				if ( notify != null )
					return notify;
				
			}	
		}
		
		return null;
	}
	
	/**
	 * Remove um tópico de aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp 
	 */
	@Override
	public String remover() {
		
		GenericDAO dao = null;

		try {
			dao = getGenericDAO();

			if ( !remocaoAulaExtra ) {
				int id = idTopicoSelecionado;
				if (getParameterInt("id") != null)
					id = getParameterInt("id");
	
				object = getGenericDAO().findByPrimaryKey(id, getClasse());
			}
			remocaoAulaExtra = false;
			TopicoAula topico = object;
			
			registrarAcao(object.getDescricao(), EntidadeRegistroAva.TOPICO_AULA, AcaoAva.INICIAR_REMOCAO, object.getId());
			
			if ( topico.getAtivo() )
			{
				prepareMovimento(SigaaListaComando.ATUALIZAR_AVA);
				
				String notify = removerTopicosFilhos(topico);
				
				if ( notify != null )
					return notify;
				
				topico.setAtivo(false);
				Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, topico, getEspecificacaoRemocao());
	
				if (notification.hasMessages())
					return notifyView(notification);
	
				listagem = null;		
				topicosAulas = null;
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO,"Tópico");
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.TOPICO_AULA, AcaoAva.REMOVER, object.getId());

			}	
			else	
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			} finally {
				if ( dao != null )
					dao.close();
			}
		
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}
	
	/**
	 * Instância o objeto inicialmente para poder ser utilizado pelo MBean.<br/><br/>
	 * 
	 * Não invocado por JSPs. 
	 */
	@Override
	public void instanciar() {
		Date melhorData = null;
		
		if (turma().getDataInicio() != null && turma().getDataFim() != null)
			melhorData = CalendarUtils.dataMaisProxima(new Date(), getDatasAulas());
		
		object = new TopicoAula();
		object.setTopicoPai(new TopicoAula());
		object.setModoCadastro(true);
		
		if (melhorData == null) {
			object.setData(new Date());
			object.setFim(new Date());
		}
		else { 
			object.setData(melhorData);
			object.setFim(melhorData);
		}
	}
	
	/**
	 * Instância o objeto (tópico de aula) após editar.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 */
	@Override
	public void instanciarAposEditar() {
		if (object == null)
			object = new TopicoAula();
		if (object.getTopicoPai() == null)
			object.setTopicoPai(new TopicoAula());
	}
	
	/**
	 * Chamado antes de persistir o tópico de aula.<br/><br/>
	 * 
	 * Não invocado por JSPs 
	 */
	@Override
	public void antesPersistir() {
		
		if (object.containsDocenteTurma(new DocenteTurma(0)))
			object.setDocentesTurma(null);
		
		if (object.getTopicoPai() != null && object.getTopicoPai().getId() == 0) {
			object.setTopicoPai(null);
		} else if (object.getTopicoPai() != null && object.getTopicoPai().getId() != 0) {
			TopicoAula pai = null;
			
			try {
				pai = getGenericDAO().findByPrimaryKey(object.getTopicoPai().getId(), TopicoAula.class);
			} catch (DAOException e) {
				e.printStackTrace();
			}

			object.setTopicoPai(pai);
		}
	}

	/**
	 * De acordo com o tópico selecionado, atualiza o mesmo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp 
	 */
	@Override
	public String atualizar() {
		
		registrarAcao(object.getDescricao(), EntidadeRegistroAva.TOPICO_AULA, AcaoAva.INICIAR_ALTERACAO, object.getId());
		
		try {
			validarTopico();
		} catch (DAOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (!hasErrors()) {
			object.setTurma(turma());
			
			object.setDocentesTurma(null);
			
			if(idsDocentes != null && idsDocentes.size() > 0) {
				for (String id : idsDocentes) {
					object.adicionarDocenteTurma(new DocenteTurma(Integer.valueOf(id)));
				}
			}
			
			antesPersistir();
			
			Specification specification = getEspecificacaoAtualizacao();
			if (specification == null || specification instanceof NullSpecification ) 
				specification = getEspecificacaoCadastro();
			
			Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);
			
			if (notification.hasMessages()) {
				prepare(SigaaListaComando.ATUALIZAR_AVA);
				return notifyView(notification);
			}
		
			registrarAcao(object.getDescricao(), EntidadeRegistroAva.TOPICO_AULA, AcaoAva.ALTERAR, object.getId());

			listagem = null;
			flash("Tópico de aula atualizado com sucesso.");
			aposPersistir();
					
			try {
				if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
					TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
					return tBean.retornarParaTurma();
				} else 
					return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
			} catch (DAOException e){
				throw new TurmaVirtualException(e);
			}
		}
		return null;
	}

	/**
	 * Método executado após persistir um tópico de aula.<br/><br/>
	 * 
	 * Não invocado por JSPs 
	 */
	@Override
	protected void aposPersistir() {
		topicosAulas = null;
	}
	
	/**
	 * Reseta os tópicos de aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.ear/sigaa.war/ava/aulas.jsp
	 * @return
	 */
	public String getResetAulas() {
		topicosAulas = null;
		return null;
	}
	
	/**
	 * Edita um tópico de aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp 
	 */
	@Override
	public String editar() {
		
		TopicoAulaDao dao = null;
		DocenteTurmaDao dtDao = null;
		idsDocentes = new ArrayList<String>();
		
		try {
			dao = getDAO(TopicoAulaDao.class);
			dtDao = getDAO(DocenteTurmaDao.class);
			
				object = dao.findAndFetch(getParameterInt("id",idTopico), TopicoAula.class, "docentesTurma");
				
				if(!object.getAtivo().booleanValue()) {
					addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
					return cancelar();
				}
				
				idTopico = 0;
				
				if (object.getTopicoPai() != null)
					object.getTopicoPai().setTopicoPai( object.getTopicoPai() );
				
				if (isTurmaComVariosDocentes()) {
					for (DocenteTurma dt : object.getDocentesTurma()) {
						idsDocentes.add(String.valueOf(dt.getId()));
					}
					//Garantir que a lista será recarregada para mostrar todos os professores.
					docentes = null;
				}
				else if(!isTurmaComVariosDocentes() && !isEmpty(turma().getDocentesTurmas())) {
					object.adicionarDocenteTurma(turma().getDocentesTurmas().iterator().next());
				}
				else {
					object.adicionarDocenteTurma(null);
				}
				
				Map<TopicoAula, List<AbstractMaterialTurma>> materiais = getDAO(TurmaVirtualDao.class).findMateriaisByTurma(turma());
				
				List<AbstractMaterialTurma> materiaisTopico = materiais.get(object);
				if (!isEmpty(materiaisTopico))
					object.getMateriais().addAll(materiaisTopico);
				
				instanciarAposEditar();
				prepare(SigaaListaComando.ATUALIZAR_AVA);
				
				paginaOrigem = getParameter("paginaOrigem");
				
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} finally {
			if (dao != null)
				dao.close();
			if(dtDao != null)
				dtDao.close();
		}
		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");
	}
	
	/**
	 *  Torna tópicos de aula filhos visíveis ou invisíveis para o discente.<br/><br/>
	 * 
	 *  Não invocado por JSPs.  
	 *  @param topicoPai
	 *  @param visible
	 */
	
	public String esconderOuExibirTopicosFilhos( TopicoAula topicoPai , boolean visible ) throws ArqException {
		
		List<TopicoAula> topicosFilhos = (List<TopicoAula>) getGenericDAO().findAtivosByExactField(TopicoAula.class, "topicoPai.id", topicoPai.getId());
		
		for (TopicoAula topicoFilho : topicosFilhos)
		{
			if ( topicoFilho.getTopicoPai().equals(topicoPai) )
			{
				topicoFilho.setVisivel(visible);

				Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, topicoFilho, getEspecificacaoAtualizacao());
				prepareMovimento(SigaaListaComando.ATUALIZAR_AVA);
				
				if (notification.hasMessages())
					return notifyView(notification);
				
				String notify = removerTopicosFilhos(topicoFilho);
				
				if ( notify != null )
					return notify;
				
			}	
		}
		
		return null;
	}
	
	/**
	 * Torna um tópico de aula invisível para os discentes.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp
	 * @throws ArqException 
	 */
	public String esconderTopicoDiscente() throws ArqException {
		prepare(SigaaListaComando.ATUALIZAR_AVA);
		object = getDAO(TopicoAulaDao.class).findByPrimaryKey(getParameterInt("id",0), TopicoAula.class);
		Boolean voltarPrincipal = getParameterBoolean("voltar_para_a_turma");
		
		String notify = esconderOuExibirTopicosFilhos(object,false);
		if ( notify != null )
			return notify;

		object.setVisivel(false);
		String res =  atualizar();
		
		if (!voltarPrincipal)
			return res;
		else
			return forward("/ava/index.jsf"); 
	}
	
	/**
	 * Esconde um tópico de aula dos discentes. .<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp
	 * @throws ArqException 
	 */
	public String exibirTopicoDiscente() throws ArqException {
		prepare(SigaaListaComando.ATUALIZAR_AVA);
		object = getDAO(TopicoAulaDao.class).findByPrimaryKey(getParameterInt("id",0), TopicoAula.class);
		TopicoAula pai = object.getTopicoPai();
		Boolean voltarPrincipal = getParameterBoolean("voltar_para_a_turma");

		if ( pai == null || (pai != null && pai.isVisivel()) ) 
		{
			String notify = esconderOuExibirTopicosFilhos(object,true);
			if ( notify != null )
				return notify;
			
			object.setVisivel(true);
			String res = atualizar();
			
			if (!voltarPrincipal)
				return res;
			else
				return forward("/ava/index.jsf"); 
		}
		else
		{
			Notification notification = new Notification();
			if (!pai.isVisivel())
				notification.addError("É obrigatório exibir o tópico pai antes de tentar exibir o tópico filho.");
			String notify = notifyView(notification);
			return notify;
		}	
	}
	
	/**
	 * Busca uma lista de SelectItem com endentação de acordo com o seu 
	 * nível na árvore de tópicos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 */
	public List<SelectItem> getComboIdentado() throws DAOException {
		Collection<TopicoAula> aulasNivel = getAulas();
		List<SelectItem> result = new ArrayList<SelectItem>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		boolean permissaoDocente = tBean.isPermissaoDocente();
		
		if ( aulasNivel != null ) 
			for (TopicoAula aula : aulasNivel)
				if (permissaoDocente || aula.isVisivel())
					result.add(new SelectItem(aula.getId(), UFRNUtils.geraCaracteres("--", aula.getNivel()) + "(" + sdf.format(aula.getData()) + " - " + sdf.format(aula.getFim()) + ") " + aula.getDescricao()));
		return result;
	}
	
	/**
	 * Exibe os tópicos endentados sem repetir o próprio tópico.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem>  getComboIdentadoEdicaoSemRepetirEleMesmo() throws DAOException {
		Collection<TopicoAula> aulasNivel = getAulas();
		List<SelectItem> result = new ArrayList<SelectItem>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		int idTopicoAulaEdicao = object.getId();
		
		for (TopicoAula aula : aulasNivel) {
			if ( idTopicoAulaEdicao != aula.getId()) {				
				result.add(new SelectItem(aula.getId(), UFRNUtils.geraCaracteres("--", aula.getNivel()) + "(" + sdf.format(aula.getData()) + " - " + sdf.format(aula.getFim()) + ") " + aula.getDescricao()));
			}
		}
		return result;
	}

	/**
	 * Exibe todos os tópicos de aula da turma e organiza por nível cada tópico.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<TopicoAula> getAulas() throws DAOException {

		if (topicosAulas == null) {
			topicosAulas = new ArrayList<TopicoAula>();
			
			TurmaVirtualDao dao = null;
			MaterialTurmaDao mDao = null;
			
			try {
				dao = getDAO(TurmaVirtualDao.class);
				mDao = getDAO(MaterialTurmaDao.class);
				
				Collection<TopicoAula> aulas = dao.findAulasByTurma(turma());
				
				//Ordenando materiais buscados
				Map<TopicoAula, List<AbstractMaterialTurma>> materiaisBuscados = dao.findMateriaisByTurma(turma());				
				Map<TopicoAula, List<AbstractMaterialTurma>> materiais = mDao.findOrdemMaterialTurma(turma(), materiaisBuscados);
				
				// Constrói a árvore de aulas
				Map<Integer, Integer> referenciaColecao = new HashMap<Integer, Integer>();
				for (TopicoAula aula : aulas) {
					List<AbstractMaterialTurma> materiaisTopico = materiais.get(aula);
					if (!isEmpty(materiaisTopico))
						aula.setMateriais(materiaisTopico);
					
					referenciaColecao.put(aula.getId(), aula.getTopicoPai() == null ? 0 : aula.getTopicoPai().getId());
				}
				
				// chama o algoritmo que irá adicionar os níveis de cada tópico
				for (TopicoAula aula : aulas) {
					int idPai = referenciaColecao.get(aula.getId());
					int nivel = 0;
					while (idPai != 0 && referenciaColecao.get(idPai) != null && nivel < 50) {
						idPai = referenciaColecao.get(idPai);
						nivel++;
					}
					aula.setNivel(nivel);
				}
	
				ordenarTopicos(aulas, null, topicosAulas);
			
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return topicosAulas;
	}

	/**
	 * Ordena os tópicos de aula para exibir corretamente na tela.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 * @param aulas
	 * @param result 
	 */
	private void ordenarTopicos(Collection<TopicoAula> aulas, final Integer pai, List<TopicoAula> result) {
		@SuppressWarnings("unchecked")
		List<TopicoAula> itens = (List<TopicoAula>) CollectionUtils.select(aulas, new Predicate() {
			public boolean evaluate(Object o) {
				TopicoAula ta = (TopicoAula) o;
				if (ta.getTopicoPai() == null) {
					return pai == null;
				} else {
					if (pai == null) return false;
					else return ta.getTopicoPai().getId() == pai;
				}
			}
		});
		if (!isEmpty(itens)) {
			Collections.sort(itens, new Comparator<TopicoAula>() {
				public int compare(TopicoAula o1, TopicoAula o2) {
					return o1.getData().compareTo(o2.getData());
				}
			});
			
			if (pai == null) {
				if (result == null) result = new ArrayList<TopicoAula>();
				result.addAll(itens);
			} else {
				int index = result.indexOf(new TopicoAula(pai));
				result.addAll(index + 1, itens);
			}
			
			for (TopicoAula ta : itens) {
				ordenarTopicos(aulas, ta.getId(), result);
			}
		}
	}


	/**
	 * Retorna todas as aulas dessa disciplina de acordo com o horário.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 */
	public List<SelectItem> getAulasCombo() {
		if (itens == null) {
			itens = new ArrayList<SelectItem>();
			Set<Date> datasAulas = getDatasAulas();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			for (Date data : datasAulas) {
				itens.add(new SelectItem(data.getTime(), sdf.format(data)));
			}
		}

		return itens;
	}
	
	/**
	 * Retorna as datas das aulas, removendo os feriados.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * @return
	 */
	public Set<Date> getDatasAulas() {
		if (datasAulas == null) {
			try {
				if (isEmpty(turma().getSubturmas())) {
					Turma t = turma();
					t.setDisciplina(getGenericDAO().findAndFetch(t.getDisciplina().getId(), ComponenteCurricular.class, "unidade"));
					CalendarioAcademico c = CalendarioAcademicoHelper.getCalendario(t);
					datasAulas = TurmaUtil.getDatasAulasTruncate(turma(), c);
				} else {
					datasAulas = new TreeSet<Date>();
					for (Turma t : turma().getSubturmas()) {
						CalendarioAcademico c = CalendarioAcademicoHelper.getCalendario(t);
						datasAulas.addAll(TurmaUtil.getDatasAulasTruncate(getGenericDAO().findByPrimaryKey(t.getId(), Turma.class), c));
					}
				}
				if(isEmpty(datasAulas))
					datasAulas = CalendarUtils.getDiasUteis(turma().getDataInicio(), turma().getDataFim());
			} catch (DAOException e) {
				throw new TurmaVirtualException(e);
			}
			
			/** removendo feriados da lista de datas disponíveis para criar tópicos de aula*/
			if( getFeriadosTurma() == null)
				setFeriadosTurma( TurmaUtil.getFeriados(turma()) );
			for (Iterator<Date> it = datasAulas.iterator(); it.hasNext();) {
				Date dt = it.next();
				if( getFeriadosTurma().contains(dt) )
					it.remove();
			}
		}
		
		return datasAulas;
	}
	
	/**
	 * Retorna os meses de aula usando o calendário acadêmico.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * @return
	 */
	public Set<Integer> getMesesAulas() {
		if (mesesAulas == null) {
			try {
				mesesAulas = new TreeSet<Integer>();
				CalendarioAcademico c = CalendarioAcademicoHelper.getCalendario(turma());
				Set<Date> datas = TurmaUtil.getDatasAulasTruncate(turma(), c);
				for (Date d : datas) {
					mesesAulas.add(CalendarUtils.getMesByData(d));
				}
				
			} catch (DAOException e) {
				throw new TurmaVirtualException(e);
			}
		}
		
		return mesesAulas;
	}
	
	/**
	 * Retorna uma lista de SelectItens contendo os docentes da turma. .<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ava/TopicoAula/_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List <SelectItem> getDocentes () throws HibernateException, DAOException{
		
		if (docentes == null){
			docentes = new ArrayList <SelectItem> ();
		
			DocenteTurmaDao dao = null;
			
			try {
				dao = getDAO(DocenteTurmaDao.class);
				List <DocenteTurma> dts = (List<DocenteTurma>) dao.findByTurma(turma().getId());
				
				for (DocenteTurma dt : dts){
					Pessoa pessoa = dt.getDocente() != null ? dt.getDocente().getPessoa() : dt.getDocenteExterno().getPessoa();
					docentes.add(new SelectItem(dt.getId(), pessoa.getNome()));
					
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return docentes;
	}
	
	/**
	 * Verifica se a turma possui mais de um docente.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isTurmaComVariosDocentes() throws HibernateException, DAOException {
		return turma().getDocentesTurmas() == null ? false : turma().getDocentesTurmas().size() > 1;
	}

	/**
	 * Verifica se o tópico de aula atende os requisitos para ser cadastrado (realiza a validação do objeto)
	 * usando o Padrão Specification.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 */
	@Override
	public Specification getEspecificacaoCadastro() {

		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
		} catch (ArqException e1) {
			e1.printStackTrace();
		}
		
			return new Specification() {
				Notification notification = new Notification();
				
				public Notification getNotification() {
					return notification;
				}
	
				public boolean isSatisfiedBy(Object objeto) {
									
					TopicoAula topico = (TopicoAula) objeto;
					if (topico.getData() == null)						
						notification.addError("A data de início é obrigatória.");
					if (topico.getFim() == null)
						notification.addError("A data de fim é obrigatória.");
					if (topico.getData() != null && topico.getFim() != null && topico.getData().after(topico.getFim()))
						notification.addError("A data fim não pode ser maior que a data início");
					if (isEmpty(topico.getDescricao()))
						notification.addError("É obrigatório informar a descrição do tópico de aula");
					if (topico.isAulaCancelada()){
						if( topico.getData().compareTo(topico.getFim()) != 0 )
							notification.addError("Para cancelar a aula a data fim precisa ser igual a data início");
					}
						
					if (topico.getTopicoPai() != null) { // Validar se a data do filho está contida na data do tópico pai
						TopicoAula pai = topico.getTopicoPai();
						boolean inicioOk = topico.getData().equals(pai.getData()) || topico.getData().after(pai.getData());
						boolean fimOk = topico.getFim().equals(pai.getFim()) || topico.getFim().before(pai.getFim());
						
						if (!inicioOk || !fimOk) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							notification.addError("As datas do tópico de aula devem estar contidas no intervalo de datas do tópico pai (" + 
									sdf.format(pai.getData()) + " a " + sdf.format(pai.getFim()) + ")");
						}
					}
					
					return !notification.hasMessages();

				}
			};
	}

	/**
	 * De acordo com o item selecionado no combobox executa a ação equivalente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * @param evt
	 * @throws ArqException 
	 */
	public String acaoTopico() throws ArqException {
		
		// A variável acaoTopico é uma string com o valor idTopico_acao
		String [] aux = acaoTopico.split("_");
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		Integer idTopicoSelecionado = Integer.valueOf(Integer.parseInt(aux[0]));
		Integer acao = Integer.valueOf(Integer.parseInt(aux[1]));
		
		switch(acao) {
		case ADICIONAR_ARQUIVO:
			ArquivoUsuarioMBean arqUsuario = (ArquivoUsuarioMBean) getMBean("arquivoUsuario");
			arqUsuario.inserirArquivoTurma(idTopicoSelecionado);
			break;
		case ADICIONAR_ARQUIVO_DO_PORTA_ARQUIVOS:
			MenuTurmaMBean menuBean = getMBean("menuTurma");
			getCurrentRequest().getSession().setAttribute("idTopicoSelecionado", idTopicoSelecionado);
			return menuBean.acessarPortaArquivos();
			
		case ADICIONAR_ARQUIVOS:
			ArquivoUsuarioMBean arqsUsuario = (ArquivoUsuarioMBean) getMBean("arquivoUsuario");
			arqsUsuario.inserirVariosArquivosTurma(idTopicoSelecionado);
			break;
		case CADASTRAR_SUB_TOPICO:
			cadastrarFilho(idTopicoSelecionado);
			break;
		case CADASTRAR_REFERENCIA:
			IndicacaoReferenciaMBean indicacao = (IndicacaoReferenciaMBean) getMBean("indicacaoReferencia");
			indicacao.inserirReferencia(idTopicoSelecionado);
			break;
		case CADASTRAR_TAREFA:
			TarefaTurmaMBean tarefa = (TarefaTurmaMBean) getMBean("tarefaTurma");
			tarefa.inserirTarefa(idTopicoSelecionado);
			break;
		case CADASTRAR_CONTEUDO:
			ConteudoTurmaMBean conteudo = (ConteudoTurmaMBean) getMBean("conteudoTurma");
			conteudo.inserirConteudo(idTopicoSelecionado);
			break;
		case CADASTRAR_VIDEO:
			VideoTurmaMBean vBean = getMBean ("videoTurma");
			vBean.novoVideo(idTopicoSelecionado);
			break;
		case EDITAR_TOPICO:
			idTopico = idTopicoSelecionado;
			editar ();
			break;
		case REMOVER_TOPICO:
			this.idTopicoSelecionado = idTopicoSelecionado;
			remover();
			break;
		case CADASTRAR_FORUM:
			ForumTurma ft = new ForumTurma();
			ft.setTurma(turma());
			ft.getTopicoAula().setId(idTopicoSelecionado);
			
			ForumTurmaMBean ftBean = getMBean("forumTurmaBean");
			ftBean.setObj(ft);
			ftBean.preCadastrar();
			
			break;
		case LANCAR_FREQUENCIA:
			
			GenericDAO dao = null;
			
			try {
				dao = getGenericDAO();
				TopicoAula t = dao.findByPrimaryKey(idTopicoSelecionado, TopicoAula.class);
				
				MenuTurmaMBean menu = getMBean ("menuTurma");
				
				// Se a turma não tem subturmas
				if (isEmpty(tBean.getTurma().getSubturmas()))
					menu.acessarLancarFrequencia();
				// Se tem
				else
					menu.acessarLancarFrequenciaST();
				
				// Se o tópico de aula é somente para um dia
				if (t.getData().equals(t.getFim())){
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(t.getData());
					
					redirect("/ava/FrequenciaAluno/form.jsf?dia=" + cal.get(Calendar.DAY_OF_MONTH) + "&mes=" + cal.get(Calendar.MONTH) + "&ano=" + cal.get(Calendar.YEAR));
				}
				
			} finally {
				if (dao != null)
					dao.close();
			}
			
			
			
			
			break;
		case CADASTRAR_ENQUETE:
			EnqueteMBean eBean = getMBean("enquete");
			eBean.novoParaTurma(idTopicoSelecionado);
			break;
		case CADASTRAR_QUESTIONARIO:
			QuestionarioTurmaMBean qBean = getMBean("questionarioTurma");
			qBean.novoQuestionarioParaTopico(idTopicoSelecionado);
			break;
		case CADASTRAR_TOPICO:
			idsDocentes = null;
			novo();
			break;
		case CADASTRAR_NOTICIA:
			NoticiaTurmaMBean nBean = getMBean("noticiaTurma");
			nBean.novo();
			break;
		case CADASTRAR_ROTULO:
			RotuloTurmaMBean rBean = getMBean("rotuloTurmaBean");
			rBean.preCadastrar(new TopicoAula(idTopicoSelecionado));
			break;			
		case CADASTRAR_CHAT:
			ChatTurmaMBean cBean = getMBean("chatTurmaBean");
			cBean.preCadastrar(new TopicoAula(idTopicoSelecionado));
			break;			

		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * Cadastra um tópico de aula filho.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @param idTopico
	 */
	public void cadastrarFilho(Integer idTopico) {
		object = new TopicoAula();
		initDocentes();
		initAulaExtra(idTopico);
		object.setTopicoPai(new TopicoAula(idTopico));
		forward("/ava/TopicoAula/novo.jsp");
	}
	
	/**
	 * Exibe o formulário para se cadastrarem todos os tópicos de aula para uma turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/listar.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarGerenciaEmLote () throws ArqException{
		prepareMovimento(SigaaListaComando.GERENCIAR_TOPICOS_AULA_EM_LOTE);
		
		Turma t = turma();
		
		// Os horários da turma.
		List <HorarioTurma> horariosAulas = null;
		// As aulas extra da turma.
		List <AulaExtra> aulasExtra = null;
		
		// Se a turma não tem subturmas, pega os seus horários e aulas extras
		if (isEmpty(t.getSubturmas())){
			horariosAulas = (List<HorarioTurma>) getGenericDAO().findByExactField(HorarioTurma.class, "turma.id", t.getId(), "asc", "dataInicio");
			aulasExtra = (List<AulaExtra>) getGenericDAO().findByExactField(AulaExtra.class, "turma.id", t.getId(), "asc", "dataAula");
		} else {
			// Caso contrário, pega os horários e aulas extras das subturmas.
			List <Integer> ids = new ArrayList <Integer> ();
			ids.add(t.getId());
			
			for (Turma st : t.getSubturmas())
				ids.add(st.getId());
			
			horariosAulas = getDAO(HorarioTurmaDao.class).findByIdsTurmas(ids);
			aulasExtra = getDAO(AulaExtraDao.class).findByIdsTurmas(ids);
		}
		

		// Se a turma não tem aula extra ou horário definido, não pode usar este caso de uso.
		if (horariosAulas.isEmpty() && aulasExtra.isEmpty()){
			addMensagem(MensagensTurmaVirtual.TURMA_DEVE_POSSUIR_HORARIO_OU_AULAS_EXTRAS);
			return null;
		}
		
		// Busca os tópicos já criados.
		
		List <TopicoAula> auxTA = (List<TopicoAula>) getGenericDAO().findAtivosByExactField(TopicoAula.class, "turma.id", t.getId(), "data", "asc");
		todosTopicosTurma = new ArrayList <TopicoAula> ();
		
		for (TopicoAula ta : auxTA)
			if (ta.getTopicoPai() == null){
				ta.setDescricao(ta.getDescricao().replaceAll("\\\"", "&quot;"));
				todosTopicosTurma.add(ta);
			}
		

		List <HorarioTurma> auxHT = new ArrayList <HorarioTurma> ();
		
		// Remove os horários repetidos
		for (HorarioTurma ht : horariosAulas){
			boolean adicionar = true;
			
			for (HorarioTurma aux : auxHT)
				if (aux.getDataInicio().equals(ht.getDataInicio()) && aux.getDia() == ht.getDia()){
					adicionar = false;
					break;
				}
			
			if (adicionar)
				auxHT.add(ht);
		}
		horariosAulas = auxHT;
		
		// Datas que devem ter tópicos de aula.
		List <Date> horarios = new ArrayList <Date> ();
		
		// Adiciona as datas das aulas extras.
		for (AulaExtra ae : aulasExtra){
			int i = -1;
			for (Date d : horarios)
				if (d.getTime() > ae.getDataAula().getTime()){
					i = horarios.indexOf(d);
					break;
				}
			
			boolean adicionar = true;
			for (TopicoAula ta : todosTopicosTurma)
				if (ta.getData().equals(ae.getDataAula()) && ta.isSelecionado()){
					ta.setSelecionado(true);
					adicionar = false;
					break;
				}
				
			if (adicionar){
				if (i >= 0)
					horarios.add(i, ae.getDataAula());
				else
					horarios.add(ae.getDataAula());
			}
		}
		
		// Adiciona as datas das aulas
		for (HorarioTurma ht : horariosAulas){
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(ht.getDataInicio());
			cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(""+ht.getDia()));
			
			// Se o horario inicia na metade da semana mas o dia da semana do horario é antes da data
			// inicial, então pula uma semana
			if (cal.getTime().getTime() < ht.getDataInicio().getTime())
				cal.add(Calendar.DAY_OF_MONTH, 7);
			
			List <Date> datas = new ArrayList <Date> ();
			
			while (cal.getTime().getTime() <= ht.getDataFim().getTime()){
				datas.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 7);
			}
			
			for (Date da : datas){
				int i = -1;
				for (Date d : horarios)
					if (d.getTime() > da.getTime()){
						i = horarios.indexOf(d);
						break;
					}
				
				boolean adicionar = true;
				for (TopicoAula ta : todosTopicosTurma)
					if (ta.getData().equals(da) && ta.isSelecionado()){
						ta.setSelecionado(true);
						adicionar = false;
						break;
					}
					
				if (adicionar){
					if (i >= 0)
						horarios.add(i, da);
					else
						horarios.add(da);
				}
			}
		}
		
		
		auxTA = new ArrayList <TopicoAula> ();
		
		for (TopicoAula ta : todosTopicosTurma)
			ta.setSelecionado(false);
		
		// Adiciona os tópicos de aula à lista.
		for (Date d : horarios){
			
			boolean adicionar = true;
			
			int i = -1;
			for (TopicoAula ta : todosTopicosTurma)
				if (d.getTime() > ta.getData().getTime() && !ta.isSelecionado()){
					ta.setSelecionado(true);
					auxTA.add(ta);
				} else if (d.getTime() == ta.getData().getTime() && !ta.isSelecionado()){
					ta.setSelecionado(true);
					adicionar = false;
					auxTA.add(ta);
				}
					
			if (adicionar){
				TopicoAula topicoAula = new TopicoAula ();
				topicoAula.setData(d);
				topicoAula.setFim(d);
				topicoAula.setTurma(t);
				
				if (i >= 0)
					auxTA.add(i, topicoAula);
				else
					auxTA.add(topicoAula);
			}
		}
		
		todosTopicosTurma = auxTA;
		
		return forward (PAGINA_FORM_GERENCIAR_EM_LOTE);
	}
	
	/**
	 * Cadastra ou altera os tópicos de aula de acordo com os dados providos pelo docente na
	 * tela de gerenciar tópicos de aula em lote.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/gerenciarEmLote.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String gerenciarEmLote () throws ArqException{
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
		
			List <TopicoAula> topicos = new ArrayList <TopicoAula> ();

			String [] aux = dadosTopicosEmLote.split("%!%");
			
			Turma turma = turma();
			
			for (String a : aux){
				String [] auxTopico = a.split("%@%");
				if (auxTopico.length > 1 && !StringUtils.isEmpty(auxTopico[1])){
					int id = Integer.parseInt(auxTopico[0]);
					
					TopicoAula ta = new TopicoAula();
					
					if (id != 0)
						ta = dao.findByPrimaryKey(id, TopicoAula.class);
					else {
						ta.setTurma(turma);
						ta.setId(id);
					}
					
					ta.setDescricao(auxTopico[1]);
					
					String[] dataInicioArray = auxTopico[2].split("/");
					if (dataInicioArray != null && dataInicioArray.length == 3){
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, Integer.parseInt(dataInicioArray[2]));
						cal.set(Calendar.MONTH, Integer.parseInt(dataInicioArray[1])-1);
						cal.set(Calendar.DATE, Integer.parseInt(dataInicioArray[0]));
						cal.set(Calendar.HOUR_OF_DAY, 0);
					    cal.set(Calendar.MINUTE, 0);
					    cal.set(Calendar.SECOND, 0);
					    cal.set(Calendar.MILLISECOND, 0);
						ta.setData(cal.getTime());
					} else {
						addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Data de Início");
					}
					
					String[] dataFimArray = auxTopico[3].split("/");
					if (dataFimArray != null && dataFimArray.length == 3){
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, Integer.parseInt(dataFimArray[2]));
						cal.set(Calendar.MONTH, Integer.parseInt(dataFimArray[1])-1);
						cal.set(Calendar.DATE, Integer.parseInt(dataFimArray[0]));
						cal.set(Calendar.HOUR_OF_DAY, 0);
					    cal.set(Calendar.MINUTE, 0);
					    cal.set(Calendar.SECOND, 0);
					    cal.set(Calendar.MILLISECOND, 0);
						ta.setFim(cal.getTime());
					} else {
						addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Data de Fim");
					}
					
					topicos.add(ta);
				}
			}
		
			MovimentoGerenciaTopicosAula mov = new MovimentoGerenciaTopicosAula (topicos);
			
			if (hasErrors())
				return null;
			
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			todosTopicosTurma = null;
			topicosAulas = null;
			listagem = null;
		
			return forward(PAGINA_LISTA_TOPICOS);
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}  finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Exibe a página principal da turma em formato de impressão.<br/><br/>
	 * 
	 * Método usado na seguinte JSP: sigaa.war/ava/aulas.jsp
	 * @return
	 */
	public String imprimirTopicos (){
		
		imprimir = true;
		
		return forward(PAGINA_IMPRIMIR_TOPICOS);
	}

	public List<ItemPrograma> getListagemItensProgramaPorDisciplina() {
		return listagemItensProgramaPorDisciplina;
	}

	public void setListagemItensProgramaPorDisciplina(List<ItemPrograma> listagemItensProgramaPorDisciplina) {
		this.listagemItensProgramaPorDisciplina = listagemItensProgramaPorDisciplina;
	}
	
	/**
	 * Exibe a data inicial do tópico.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @return
	 */
	public SelectItem getDataInicial() {
		if (object.getData() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			dataInicial = new SelectItem(object.getData().getTime(), sdf.format(object.getData()));
		}

		return dataInicial;
	}

	/**
	 * Exibe a data final do tópico aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @return
	 */
	public SelectItem getDataFinal() {
		if (object.getData() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			dataFinal = new SelectItem(object.getFim().getTime(), sdf.format(object.getFim()));
		}
		return dataFinal;
	}

	/**
	 * Exibe página cronograma de aulas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/gantt.jsp
	 */
	public String gantt() {
		return forward("/ava/TopicoAula/gantt.jsp");
	}

	/**
	 * Faz a busca dos tópicos que vão fazer parte do cronograma de aulas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/gantt.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<TopicoAula> getAulasGantt() throws DAOException {
				if (topicosAulas == null) {
			topicosAulas = (List<TopicoAula>) getDAO(TurmaVirtualDao.class).findAulasByTurma(turma());
		}
		
		return topicosAulas;
	}
	/**
	 * Inicia o cadastro do tópico de aula, populando os docentes escolhidos.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/TopicoAula/novo.jsp</li>
	 * <li>/sigaa.war/ava/TopicoAula/form.jsp</li>
	 * </ul>
	 */
	public String cadastrar() throws ArqException {
		
		int idItem = getParameterInt("id", 0);
		ItemPrograma objItem;
		
		if (idItem != 0)  {
			
			objItem = getGenericDAO().findByPrimaryKey(idItem, ItemPrograma.class);
			object.setConteudo(objItem.getConteudo()); 
			object.setDescricao("Aula "+objItem.getAula());
		}			
		
		validarTopico();
		
		if (!hasErrors()) {
			if(idsDocentes != null && idsDocentes.size() > 0) {
				for (String id : idsDocentes) {
					object.adicionarDocenteTurma(new DocenteTurma(Integer.valueOf(id)));
				}
			}
			else {
				object.setDocentesTurma(null);
			}
			
			return super.cadastrar();
		}
		return null;
	}
	
	/**
	 * Retorna o nome do docente adicionado no tópico.
	 * Usado nos casos onde a turma possui apenas um docente.
	 * 
	 * @return
	 */
	public String getNomeDocente() {
		if(object.getDocentesTurma() != null && object.getDocentesTurma().size() > 0)
			return object.getDocentesTurma().iterator().next().getDocenteNome();
		else
			return null;
	}

	/**
	 * Indica se o tópico vai ser cadastrado em várias turmas.<br/><br/>
	 * 
	 * Não invocado por JSPs
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	public void setDataFinal(SelectItem dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public void setDatasAulas(Set<Date> datasAulas) {
		this.datasAulas = datasAulas;
	}

	public void setDataInicial(SelectItem dataInicial) {
		this.dataInicial = dataInicial;
	}

	public List<TopicoAula> getTodosTopicosTurma() {
		if (todosTopicosTurma == null){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return new ArrayList <TopicoAula> ();
		}	
		
		return todosTopicosTurma;
	}

	public void setTodosTopicosTurma(List<TopicoAula> todosTopicosTurma) {
		this.todosTopicosTurma = todosTopicosTurma;
	}

	public List<TopicoAula> getTopicosNaoPublicados() {
		return topicosNaoPublicados;
	}
	public void setTopicosNaoPublicados(List<TopicoAula> topicosNaoPublicados) {
		this.topicosNaoPublicados = topicosNaoPublicados;
	}
	public void setTopicosAulas(List<TopicoAula> topicosAulas) {
		this.topicosAulas = topicosAulas; 
	}
	
	public void setItens(List<SelectItem> itens) {
		this.itens = itens;
	}
	
	/**
	 * Seta a data de início do tópico.
	 * 
	 * @param time
	 */
	public void setDataInicio (Long time){
		object.setData(new Date(time));
	}
	
	/**
	 * Seta a data final do tópico.
	 * 
	 * @param time
	 */
	public void setDataFim (Long time){
		object.setFim(new Date(time));
	}
	
	/**
	 * Retorna a data inicial do tópico aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @return
	 */
	public Long getDataInicio(){
		
		if (object != null && object.getData() != null)
			return object.getData().getTime();
		
		return null;
	}
	
	/**
	 * Retorna a data final do tópico aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/TopicoAula/_form.jsp
	 * @return
	 */
	public Long getDataFim(){
		if (object != null && object.getFim() != null)
			return object.getFim().getTime();
		
		return null;
	}

	public boolean isImprimir() {
		return imprimir;
	}

	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}
	
	public String getDadosTopicosEmLote() {
		return dadosTopicosEmLote;
	}

	public void setDadosTopicosEmLote(String dadosTopicosEmLote) {
		this.dadosTopicosEmLote = dadosTopicosEmLote;
	}

	/**
	 * Seta a variável imprimir como false para que o bean volte ao normal após o usuário visualizar a página
	 * no formato de impressão.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/aulas.jsp
	 * <ul>
	 *   <li>/sigaa.war/ava/aulas.jsp</li>
	 *   <li>/sigaa.war/ava/participantes_impressao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getConcluirImpressao (){
		imprimir = false;
		return "";
	}

	public int getIdTopicoSelecionado() {
		return idTopicoSelecionado;
	}

	public void setIdTopicoSelecionado(int idTopicoSelecionado) {
		this.idTopicoSelecionado = idTopicoSelecionado;
	}

	public String getAcaoTopico() {
		return acaoTopico;
	}

	public void setAcaoTopico(String acaoTopico) {
		this.acaoTopico = acaoTopico;
	}

	public List<String> getIdsDocentes() {
		return idsDocentes;
	}

	public void setIdsDocentes(List<String> idsDocentes) {
		this.idsDocentes = idsDocentes;
	}

	public void setRemocaoAulaExtra(boolean remocaoAulaExtra) {
		this.remocaoAulaExtra = remocaoAulaExtra;
	}

	public boolean isRemocaoAulaExtra() {
		return remocaoAulaExtra;
	}
	
	/**
	 * Retorna uma coleção de {@link SelectItem} referentes as ações possíveis 
	 * para um tópico de aula dentro da turma virtual.

	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/aulas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAcoesTopico() {
		Collection<SelectItem> acoes = new ArrayList<SelectItem>();
		
		acoes.add(new SelectItem(montarValorItem(0), "-- SELECIONE UMA AÇÃO --"));
		acoes.add(new SelectItem(montarValorItem(ADICIONAR_ARQUIVOS), "Adicionar Arquivos"));
		acoes.add(new SelectItem(montarValorItem(ADICIONAR_ARQUIVO_DO_PORTA_ARQUIVOS), "Adicionar Arquivo do Porta-Arquivos"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_CHAT), "Cadastrar Chat"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_CONTEUDO), "Cadastrar Conteúdo"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_ENQUETE), "Cadastrar Enquete"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_FORUM), "Cadastrar Fórum"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_QUESTIONARIO), "Cadastrar Questionário"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_ROTULO), "Cadastrar Rótulo"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_REFERENCIA), "Cadastrar Referência (Site, Livro, ...)"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_SUB_TOPICO), "Cadastrar Sub-Tópico"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_TAREFA), "Cadastrar Tarefa"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_VIDEO), "Cadastrar Vídeo"));
		acoes.add(new SelectItem(montarValorItem(EDITAR_TOPICO), "Editar Tópico"));
		acoes.add(new SelectItem(montarValorItem(REMOVER_TOPICO), "Remover Tópico"));
		acoes.add(new SelectItem(montarValorItem(LANCAR_FREQUENCIA), "Lançar Frequência"));
		acoes.add(new SelectItem(montarValorItem(-1), "----------------------------------------------------", "----------------------------------------------------", true));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_TOPICO), "Novo Tópico de Aula"));
		acoes.add(new SelectItem(montarValorItem(CADASTRAR_NOTICIA), "Nova Notícia"));
		
		return acoes;
	}
	
	/**
	 * Monta o valor a ser utilizado no {@link SelectItem} correspondente a ação passada.
	 * O valor é montado seguindo o seguinte modelo: '<code>idTopico_acao</code>'
	 * 
	 * @param acao
	 * @return
	 */
	private String montarValorItem(int acao) {
		TopicoAula aula = (TopicoAula) getCurrentRequest().getAttribute("aula");
		if ( aula != null ) {
			return aula.getId() + "_" + acao;
		} else {
			return "";
		}
	}


}