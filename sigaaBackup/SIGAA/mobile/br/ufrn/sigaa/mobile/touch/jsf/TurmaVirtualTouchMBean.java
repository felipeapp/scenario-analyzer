/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dao.MaterialTurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.RegistroAcaoAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;
import br.ufrn.sigaa.mobile.touch.dao.TurmaTouchDao;
import br.ufrn.sigaa.mobile.touch.dao.TurmaVirtualTouchDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/***
 * Classe base para todos os managed-bens da turma virtual touch.
 * 
 * @author Ilueny Santos
 * @author Fred Castro
 *
 */
@Component("turmaVirtualTouchBean")
@Scope("session")
public class TurmaVirtualTouchMBean<T> extends SigaaTouchAbstractController<T> {

	/** Mensagem enviada para o usuário.*/
	//private String mensagem = null;
	
	/** Se o usuário logado é docente */
	private boolean docenteLogado = false;

	/** Se o usuário logado é discente */
	private boolean discenteLogado = false;
	
	/** Servidor logado. */
	protected Servidor servidor;

	/** Docente Externo logado. */
	protected DocenteExterno docenteExterno;
	
	/** Discente logado. */
	protected Discente discente;

	/** Turma que está sendo acessada atualmente. */
	private Turma turma = null;

	/** Define a lista de turmas abertas exibidas na página principal da turma virtual. */
	protected List<Turma> turmas = null; 

	/** Tópico de aula que foi selecionado para exibição. */
	private TopicoAula topicoSelecionado = new TopicoAula();

	/** Armazena o id do próximo tópico da turma virtual. */
	private Integer idProximoTopico = 0;

	/** Armazena o id do tópico anterior da turma virtual. */
	private Integer idTopicoAnterior = 0;
	
	/** Lista de tópicos de aula. */
	private List<TopicoAula> topicosAulas;
	
	/** Indica se deve ser exibida uma mensagem na Turma Virtual mostrando ao usuário que ele 
	 * pode usar o recurso de Swipe. */
	private boolean mensagemDicaSwipe = false;

	/**
	 * Inicializa a turma selecionada.
	 * Não invocado por JSP's
	 * @throws DAOException
	 */
	public void preparaEntrada() throws DAOException {
		Integer idTurma = getParameterInt("idTurma", 0);
		
		if (servidor != null || docenteExterno != null)
			docenteLogado = true;
		
		if (discente != null)
			discenteLogado = true;

		if (idTurma > 0){
			TurmaTouchDao dao = getDAO(TurmaTouchDao.class);
			turma = dao.findByPrimaryKey(idTurma, Turma.class);
			turma.setHorarios(dao.findHorariosByTurma(getTurma()));
	
			//Inicializa subturmas
			List<Turma> subTurmas = turma.getSubturmas();
			if (subTurmas != null) {
				subTurmas = dao.findSubturmasByTurmaFetchDocentes(turma);
				turma.setSubturmas(subTurmas);
			}
	
			//Inicializa curso
			if (turma.getCurso() != null) {
				dao.refresh(turma.getCurso());
			}
			
			//Inicializa polo
			if (turma.getPolo() != null) {
				dao.refresh(turma.getPolo());
				turma.getPolo().getCidade().getNomeUF();
			}
	
			//Inicializa unidade
			Unidade unidade = dao.findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class);
			turma.getDisciplina().setUnidade(unidade);
	
			// Inicializa descrição da turma.
			turma.getDescricao();
	
			// Inicializa programa da disciplina da turma.
			turma.getDisciplina().getPrograma();

			// Prepara o bean de notícias para exibir notícia de destaque.
			NoticiaTurmaTouchMBean nBean = getMBean("noticiaTurmaTouch");
			nBean.setTurma(turma);

		} else {
			addMensagemErro("Turma selecionada não é uma turma válida.");
		}
		
		validaPermissaoAcessoTurma();
	}
	
	

	/**
	 * Checa se o usuário tem permissão para acessar a turma.
	 * Não invocado por JSP's
	 */
	public void validaPermissaoAcessoTurma() {
		try {
			if (turma != null) {
				// Verifica se o docente publicou a turma para acesso público.
				ConfiguracoesAva conf;
				conf = getDAO(TurmaVirtualTouchDao.class).findConfiguracoes(turma);
				boolean permiteAcessoPublico = false;		
				if (conf != null && conf.isPermiteVisualizacaoExterna()){
					permiteAcessoPublico = true;
				}
	
				if (!isDiscenteLogado() && !isDocenteLogado() && !permiteAcessoPublico) {
					addMensagemErro("Esta turma é restrita a seus participantes.");
				}
			}
		} catch (DAOException e) {
			notifyError(e);
		}
	}
	
	/**
	 * Exibe somente o tópico selecionado pelo usuário
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>sigaa.war\mobile\touch\topico_aula.jsp</li>
	 *  </ul>
	 * @param e
	 * @throws DAOException 
	 */
	public String exibirTopico () throws DAOException {
		mensagemDicaSwipe = getParameterBoolean("msgSwipe");
		
		Integer id = topicoSelecionado.getId();

		// Indo para última aula já apresentada
		if (id == null || id <= 0) {
			topicoSelecionado = new TopicoAula();
			if (!topicosAulas.isEmpty()) {
				id = topicosAulas.get(0).getId();
				
				for (TopicoAula t : topicosAulas) {
					if (new Date().before(t.getData())) {
						break;
					}
					id = t.getId();
				}
			}
		}
		
		
		topicoSelecionado.setId(id);
		int indice = topicosAulas.indexOf(topicoSelecionado);

		if (indice >= 0) {
			topicoSelecionado = new TopicoAula();
			BeanUtils.copyProperties(topicosAulas.get(indice), topicoSelecionado);

			if (indice < topicosAulas.size() -1) {
				idProximoTopico = topicosAulas.get(indice + 1).getId();
			} else {
				idProximoTopico = 0;
			}

			if (indice > 0) {
				idTopicoAnterior = topicosAulas.get(indice - 1).getId();
			} else {
				idTopicoAnterior = 0;
			}
		}
		
		return forward(getPaginaTopicoAula());
	}
	
	
	/**
	 * Exibe todos os tópicos de aula da turma e organiza por nível cada tópico.<br/><br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>sigaa.war\mobile\touch\topico_aula.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public List<TopicoAula> getAulas() throws DAOException {

		topicosAulas = new ArrayList<TopicoAula>();
		
		TurmaVirtualDao dao = null;
		MaterialTurmaDao mDao = null;
		
		try {
			dao = getDAO(TurmaVirtualDao.class);
			mDao = getDAO(MaterialTurmaDao.class);
			
			Collection<TopicoAula> aulas = dao.findAulasByTurma(turma);
			
			//Ordenando materiais buscados
			Map<TopicoAula, List<AbstractMaterialTurma>> materiaisBuscados = dao.findMateriaisByTurma(turma);				
			Map<TopicoAula, List<AbstractMaterialTurma>> materiais = mDao.findOrdemMaterialTurma(turma, materiaisBuscados);
			
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

			if (discenteLogado){
				Iterator<TopicoAula> it = aulas.iterator();
				while (it.hasNext()){
					if (!it.next().isVisivel())
						it.remove();
				}
			}
			
			ordenarTopicos(aulas, null, topicosAulas);
		
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return topicosAulas;
	}
	
	/**
	 * Método utilizado para acessar uma turma virtual.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>sigaa.war\mobile\touch\portal_discente.jsp</li>	 
	 *   </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String entrarTurma() throws ArqException {
		topicoSelecionado = null;
		idProximoTopico = 0;
		idTopicoAnterior = 0;
		
		erros.getMensagens().clear();
		preparaEntrada();
	    if (!hasOnlyErrors()){
	    	// Registra a entrada do usuário na turma.
	    	registrarAcao(null, EntidadeRegistroAva.TURMA, AcaoAva.ACESSAR, getTurma().getId());
	    	setSubSistemaAtual(SigaaSubsistemas.SIGAA_MOBILE);
	    	
			return viewAulasTurma();
	    }
	    
	    return null;
	}
	
	/**
	 * Direciona o usuário para a página inicial da turma virtual.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String viewAulasTurma () throws DAOException {
		getAulas();
		setTopicoSelecionado(new TopicoAula());
		return exibirTopico();		
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
	 * Registra o acesso de usuário a uma entidade da turma virtual.
	 * 
	 * @param entidadeLida
	 * @param idEntidade
	 * @param idTurmaVirtual
	 */
	protected void registrarLogAcessoTurmaVirtual(String entidadeLida, int idEntidade, int idTurmaVirtual) {
	    if (getUsuarioLogado() != null) {
	        Integer sistema = getSistema();
	        Usuario user = getUsuarioLogado();
	        LogProcessorDelegate.getInstance().writeAcessoLog(entidadeLida, idEntidade, user, sistema, idTurmaVirtual);
	    }
	}

	
	/**
	 * Registra uma ação sobre uma entidade da turma virtual.
	 * Não invocado por JSP's
	 * @param entidade o tipo da entidade que sofreu a ação
	 * @param acao a ação realizada sobre a entidade
	 * @param ids as ids dos objetos que sofreram a ação.
	 */
	public void registrarAcao (String descricao, EntidadeRegistroAva entidade, AcaoAva acao,  int ... ids){
		Comando comandoAtivo = null;
		
		try {
			
			RegistroAcaoAva object = new RegistroAcaoAva(getUsuarioLogado(), descricao, entidade, acao, turma.getId(), ids);			
			MovimentoCadastro mov = new MovimentoCadastro(object);			
			comandoAtivo = getUltimoComando();			
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			prepareMovimento(ArqListaComando.CADASTRAR);			
			executeWithoutClosingSession(mov);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (ArqException e) {
			notifyError(e);
		} finally {
			try {
				if (comandoAtivo != null)
					prepareMovimento(comandoAtivo);
			} catch (ArqException e){
				notifyError (e);
			}
		}
	}
	
	/**
	 * Lista todas as aulas da turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula_discente.jsp</li>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarTodasAulas(){
		if (ValidatorUtil.isEmpty(getTurma())) {
			addMensagemErro("Selecione uma turma.");
			return null;
		}
		return forward(getPaginaListarAulas ());
	}
	
	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/menu.jsp</li>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws DAOException
	 */
	public String listarTodasMinhasTurmas () throws ArqException {
		carregarTurmas(true);
		return forward(getPaginaTurmas());
	}
	
	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/menu.jsp</li>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws DAOException
	 */
	public String listarMinhasTurmas () throws ArqException {
		carregarTurmas(false);
		return forward(getPaginaTurmas());
	}
	
	/**
	 * Exibe o portal do usuário, de acordo com seu vínculo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/turmas_discentes.jsp</li>
	 *    <li>sigaa.war/mobile/touch/ava/turmas_docentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String acessarPortal () {
		PortalDocenteTouchMBean pDocente = getMBean("portalDocenteTouch");
		PortalDiscenteTouchMBean pDiscente = getMBean("portalDiscenteTouch");
		
		if (pDocente.getServidor() != null || pDocente.getDocenteExterno() != null)
			return pDocente.acessarPortal();

		return pDiscente.acessarPortal();
	}
	
	/**
	 * Encaminha o usuário para a página da Biblioteca.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/portal_discente.jsp</li>
	 *    <li>sigaa.war/mobile/touch/portal_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String forwardBiblioteca () {
		return forward("/mobile/touch/biblioteca/biblioteca.jsp");
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public boolean isDocenteLogado() {
		return docenteLogado;
	}

	public void setDocenteLogado(boolean docenteLogado) {
		this.docenteLogado = docenteLogado;
	}

	public boolean isDiscenteLogado() {
		return discenteLogado;
	}

	public TopicoAula getTopicoSelecionado() {
		return topicoSelecionado;
	}

	public void setTopicoSelecionado(TopicoAula topicoSelecionado) {
		this.topicoSelecionado = topicoSelecionado;
	}

	public Integer getIdProximoTopico() {
		return idProximoTopico;
	}

	public void setIdProximoTopico(Integer idProximoTopico) {
		this.idProximoTopico = idProximoTopico;
	}

	public Integer getIdTopicoAnterior() {
		return idTopicoAnterior;
	}

	public void setIdTopicoAnterior(Integer idTopicoAnterior) {
		this.idTopicoAnterior = idTopicoAnterior;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<TopicoAula> getTopicosAulas() {
		return topicosAulas;
	}

	public void setTopicosAulas(List<TopicoAula> topicosAulas) {
		this.topicosAulas = topicosAulas;
	}

	public String getPaginaPrincipal(){
		//Deve ser implementado por PortalDiscente ou PortalDocente conforme o caso;
		return null;
	}
	
	public String getPaginaListarAulas () {
		//Deve ser implementado por PortalDiscente ou PortalDocente conforme o caso;
		return null;
	}
	
	/** 
	 * Carrega todas as turmas do discente ou docente logado.
	 * Este método não é chamado por JSP.
	 * @throws ArqException 
	 */
	public void carregarTurmas (boolean todas) throws ArqException {
		//Deve ser implementado por PortalDiscente e PortalDocente conforme o caso;
	}

	public String getPaginaTopicoAula () {
		//Deve ser implementado por PortalDiscente e PortalDocente conforme o caso;
		return null;
	}
	
	public String getPaginaTurmas () {
		//Deve ser implementado por PortalDiscente e PortalDocente conforme o caso;
		return null;
	}
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/*public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}*/

	public void setDiscenteLogado(boolean discenteLogado) {
		this.discenteLogado = discenteLogado;
	}

	public boolean getMensagemDicaSwipe() {
		return mensagemDicaSwipe;
	}

	public void setMensagemDicaSwipe(boolean msgDicaSwipe) {
		this.mensagemDicaSwipe = msgDicaSwipe;
	}
}
