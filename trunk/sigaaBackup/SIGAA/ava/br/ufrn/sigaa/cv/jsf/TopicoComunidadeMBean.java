/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UISelectOne;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.TopicoComunidadeDao;
import br.ufrn.sigaa.ava.jsf.TarefaTurmaMBean;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.cv.dominio.ConfiguracoesComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MaterialComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.cv.negocio.MovimentoCadastroCv;
import br.ufrn.sigaa.cv.negocio.TopicoComunidadeHelper;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;

/**
 * Managed bean para cadastro de Tópicos da Comunidade.
 * 
 * @author David Pereira
 * @author Agostinho
 */
@Component @Scope("request")
public class TopicoComunidadeMBean extends CadastroComunidadeVirtual<TopicoComunidade> {

	/** Data de cadastro do objeto movimentado */
	private SelectItem dataInicial;
	/** Id do tópico selecionado */
	private int idTopicoSelecionado;
	/** Se o objeto está sendo cadastrado*/
	private boolean cadastrar;
	
	/**
	 * Construtor
	 */
	public TopicoComunidadeMBean() {
		instanciar();
	}
	
	/**
	 * Retorna uma lista de tópicos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/TopicoComunidade/listar.jsp
	 * @throws DAOException 
	 */
	@Override
	public List<TopicoComunidade> lista() {
		try {
			ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
			boolean moderador = cvBean.getMembro() != null && cvBean.getMembro().isPermitidoModerar();
			
			return TopicoComunidadeHelper.getTopicosOrdenados(comunidade(),moderador,getUsuarioLogado());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retorna os tópicos com seus conteúdos ordenados.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/principal.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarAllTopicosConteudos() throws DAOException {
		listagem = getTopicos();
		return forward("/cv/TopicoComunidade/listar_topico_conteudo.jsp");
	}
	
	/**
	 * Redireciona para página de listagem com os tópicos carregados.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/TopicoComunidade/listar.jsp
	 */
	@Override
	public String listar() {
		listagem = lista();
		return forward("/cv/TopicoComunidade/listar.jsf");
	}
	
	/**
	 * Notifica a página quando existem mensagens.
	 */
	@Override
	protected String notifyView(Notification notification) {
		addMensagens(notification.getMessages());
		if (object.getTopicoPai() == null)
			object.setTopicoPai(new TopicoComunidade());
		return null;
	}
	
	/**
	 * Antes de remover, limpa a sessão.
	 */
	@Override
	protected void antesRemocao() {		
		getGenericDAO().clearSession();
	}
	
	/**
	 * Remove um tópico da comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/TopicoComunidade/listar.jsp
	 */
	@Override
	public String remover() {
		
			try {
				prepareMovimento(SigaaListaComando.REMOVER_CV);
			} catch (ArqException e1) {
				e1.printStackTrace();
			}
			
			try {
				int id = idTopicoSelecionado;
				if (getParameterInt("id") != null) {
					id = getParameterInt("id");
				}
				
				classe = ReflectionUtils.getParameterizedTypeClass(this);
				object = getGenericDAO().findByPrimaryKey(id, classe);
				object.setComunidade(null);
				antesRemocao();
				
				if (object != null) {
					Notification notification = execute(SigaaListaComando.REMOVER_CV, object, getEspecificacaoRemocao());

					if (notification.hasMessages())
						return notifyView(notification);
				}
				
				flash("Tópico removido com sucesso.");
					
			} catch (DAOException e) {
				addMensagemWarning("Antes de remover um tópico da comunidade, primeiro você precisa remover os itens que estão associados a ele.");
				return null;
			} catch (Exception e) {
				addMensagemWarning("Antes de remover um tópico da comunidade, primeiro você precisa remover os itens que estão associados a ele.");
				return null;
			}
			
		return listar();
	}
	
	/**
	 * Instancia/popula o domínio<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public void instanciar() {
		object = new TopicoComunidade();
		object.setTopicoPai(new TopicoComunidade());
		object.setModoCadastro(true);
	}
	
	/**
	 * Instancia o domínio após Editar um tópico.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public void instanciarAposEditar() {
		if (object == null)
			object = new TopicoComunidade();
		if (object.getTopicoPai() == null)
			object.setTopicoPai(new TopicoComunidade());
	}
	
	/**
	 * Antes de persistir, descobre e seta em object quem é o pai.
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public void antesPersistir() {
		
		if (object.getId() == 0)
			cadastrar = true;
		else
			cadastrar = false;
		
		if (object.getTopicoPai() != null && object.getTopicoPai().getId() == 0) {
			object.setTopicoPai(null);
		} else if (object.getTopicoPai() != null && object.getTopicoPai().getId() != 0) {
			TopicoComunidade pai = null;
			
			try {
				pai = getGenericDAO().findByPrimaryKey(object.getTopicoPai().getId(), TopicoComunidade.class);
			} catch (DAOException e) {
				e.printStackTrace();
			}

			object.setTopicoPai(pai);
		}		
	}
	
	/**
	 * Após persistir reseta a listagem de tópicos.
	 * @throws DAOException 
	 */
	@Override
	protected void aposPersistir() throws DAOException {	
		
		if ( object.isNotificarMembros() ) {
			
			String assunto = null;
			String texto = null;
			
			if (cadastrar){
				assunto = "Novo tópico cadastrado(a) na comunidade virtual: " + comunidade().getNome();
				texto = "Um tópico foi disponibilizado(a) na comunidade virtual: <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a comunidade virtual no SIGAA.</p>";
			}else{
				assunto = "Tópico atualizado(a) na comunidade virtual: " + comunidade().getNome();
				texto = "Tópico foi disponibilizado(a) na comunidade virtual: <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a turma comunidade no SIGAA.</p>";
			}
			notificarComunidade(assunto,texto);
		}
		
		listagem = null;
	}
	
	/**
	 * Força o reset da listagem de tópicos.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 * @return
	 */
	public String getResetTopicos() {
		listagem = null;
		return null;
	}
	
	/**
	 * Busca uma lista de SelectItem com identação de acordo com o seu 
	 * nível na árvore de tópicos.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/arquivo.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/_form.jsp</li>
	 * 	<li>/sigaa.war/cv/IndicacaoReferenciaComunidade/_form.jsp</li>
	 * 	<li>/sigaa.war/cv/ConteudoComunidade/_form.jsp</li>
	 * <ul>
	 */
	public List<SelectItem> getComboIdentado() throws DAOException {

		Collection<TopicoComunidade> topicosNivel = getTopicos();
		List<SelectItem> result = new ArrayList<SelectItem>();
		for (TopicoComunidade topico : topicosNivel)
			result.add(new SelectItem(topico.getId(), UFRNUtils.geraCaracteres("--", topico.getNivel()) + topico.getDescricao()));
		return result;
	}
	
	/**
	 * Retorna um SelectItem sem repetir o próprio tópico atual.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/TopicoComunidade/_form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem>  getComboIdentadoEdicaoSemRepetirEleMesmo() throws DAOException {
		Collection<TopicoComunidade> topicosNivel = getTopicos();
		List<SelectItem> result = new ArrayList<SelectItem>();
		
		int idTopicoComunidadeEdicao = object.getId();

		if (topicosNivel != null) {
			for (TopicoComunidade topico : topicosNivel) {
				if ( idTopicoComunidadeEdicao != topico.getId()) {				
					result.add(new SelectItem(topico.getId(), UFRNUtils.geraCaracteres("--", topico.getNivel()) + topico.getDescricao()));
				}
			}
		}
		return result;
	}

	
	/**
	 * Popula os tópicos da comunidade, organizando a estrutura de
	 * árvore para apresentação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/principal.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/_topicos.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/listar_topico_conteudo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<TopicoComunidade> getTopicos() throws DAOException {
		
		if (!isEmpty(listagem)) {
			return listagem;
		}
		
		listagem = new ArrayList<TopicoComunidade>();
		
		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		boolean moderador = cvBean.getMembro() != null && cvBean.getMembro().isPermitidoModerar();
		
		TopicoComunidadeDao dao = getDAO(TopicoComunidadeDao.class);
		Collection<TopicoComunidade> topicos = dao.findByComunidade(comunidade(),moderador,getUsuarioLogado());
		Map<TopicoComunidade, List<MaterialComunidade>> materiais = dao.findMateriaisByComunidadeUsuario(comunidade(),moderador,getUsuarioLogado());
		
		
		Map<Integer, Integer> referenciaColecao = new HashMap<Integer, Integer>();
		
		// Construir árvore de tópicos
		for (TopicoComunidade topico : topicos) {
			List<MaterialComunidade> materiaisTopico = materiais.get(topico);
			if (!isEmpty(materiaisTopico))
				topico.getMateriais().addAll(materiaisTopico);
			
			referenciaColecao.put(topico.getId(), topico.getTopicoPai() == null ? 0 : topico.getTopicoPai().getId());
		}
		
		// Calcular o nível de cada tópico
		for (TopicoComunidade aula : topicos) {
			int idPai = referenciaColecao.get(aula.getId());
			int nivel = 0;
			while (idPai != 0 && referenciaColecao.get(idPai) != null && nivel < 50) {
				idPai = referenciaColecao.get(idPai);
				nivel++;
			}
			aula.setNivel(nivel);
		}

		ConfiguracoesComunidadeVirtual config = getDAO(ComunidadeVirtualDao.class).findConfiguraçõesByComunidade(comunidade().getId());
		ordenarTopicos(topicos, null, listagem, config);
			
		return listagem;
	}
	
	/**
	 * Faz a ordenação dos tópicos levando em consideração os pais/filhos.
	 * 
	 * @param aulas
	 * @param pai
	 * @param result
	 * @throws DAOException 
	 */
	//Suppress usado para que o CollectionUtils.select possa receber um cast
	//para List<TopicoComunidade>
	@SuppressWarnings("unchecked")
	private void ordenarTopicos(Collection<TopicoComunidade> aulas, final Integer pai, List<TopicoComunidade> result, ConfiguracoesComunidadeVirtual config) throws DAOException {
		
		List<TopicoComunidade> itens = (List<TopicoComunidade>) CollectionUtils.select(aulas, new Predicate() {
			public boolean evaluate(Object o) {
				TopicoComunidade ta = (TopicoComunidade) o;
				if (ta.getTopicoPai() == null) {
					return pai == null;
				} else {
					if (pai == null) return false;
					else return ta.getTopicoPai().getId() == pai;
				}
			}
		});
		
		if (!isEmpty(itens)) {

			if (config == null || config.isOrdemTopicoDecrescente())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						return o2.getDataCadastro().compareTo(o1.getDataCadastro());
					}
				});
			else if ( config.isOrdemTopicoCrescente())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						return o1.getDataCadastro().compareTo(o2.getDataCadastro());
					}
				});
			else if ( config.isOrdemTopicoLivre())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						if ( o1.getOrdem() == null )
							return -1;
						return o1.getOrdem().compareTo(o2.getOrdem());
					}
				});
			
			if (pai == null) {
				if (result == null) result = new ArrayList<TopicoComunidade>();
				result.addAll(itens);
			} else {
				int index = result.indexOf(new TopicoComunidade(pai));
				result.addAll(index + 1, itens);
			}
			
			for (TopicoComunidade ta : itens) {
				ordenarTopicos(aulas, ta.getId(), result, config);
			}
		}
	}
	
	/**
	 * Padrão Specification. Verifica preenchimento de campos antes de cadastrar.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {

		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_CV);
		} catch (ArqException e1) {
			e1.printStackTrace();
		}
		
		ItemPrograma itemPrograma = null;		
		if ( getParameterInt("id") != null ) {
			
			int idItemPrograma = getParameterInt("id", 0);
			CursoDao cursoDao = getDAO(CursoDao.class);
			
			try {
				itemPrograma = cursoDao.findItemProgramaById( idItemPrograma );
			} catch (DAOException e) {
				e.printStackTrace();
			}
			
			if (itemPrograma != null) {
				object.setDescricao( "Aula " + itemPrograma.getAula() );
				object.setConteudo( itemPrograma.getConteudo() );
			}
		}
			
			return new Specification() {
				Notification notification = new Notification();
				
				public Notification getNotification() {
					return notification;
				}

				public boolean isSatisfiedBy(Object objeto) {
					TopicoComunidade topico = (TopicoComunidade) objeto;
					if (isEmpty(topico.getDescricao()))
						notification.addError("É obrigatório informar a descrição do tópico de aula");
					return !notification.hasMessages();
				}
			};
	}

	/**
	 * Esse método é chamado pela página principal da Comunidade Virtual. 
	 * As ações são realizadas de acordo com a opção selecionada pelo usuário. <br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/principal.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/listar_topico_conteudo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void acaoTopico(ValueChangeEvent evt) throws DAOException {
		UISelectOne one = (UISelectOne) evt.getComponent();
		Integer idTopicoSelecionado = Integer.valueOf(one.getAttributes().get("styleClass").toString());
		
		Integer acao = Integer.valueOf(evt.getNewValue().toString());
		switch(acao) {
		case 1:
			ArquivoUsuarioCVMBean arqUsuario = (ArquivoUsuarioCVMBean) getMBean("arquivoUsuarioCVMBean");
			arqUsuario.inserirArquivoComunidade(idTopicoSelecionado);
			break;
		case 2:
			TopicoComunidadeMBean topAula = (TopicoComunidadeMBean) getMBean("topicoComunidadeMBean");
			topAula.cadastrarFilho(idTopicoSelecionado);
			break;
		case 3:
			IndicacaoReferenciaComunidadeMBean indicacao = (IndicacaoReferenciaComunidadeMBean) getMBean("indicacaoReferenciaComunidadeMBean");
			indicacao.inserirReferencia(idTopicoSelecionado);
			break;
		case 4:
			TarefaTurmaMBean tarefa = (TarefaTurmaMBean) getMBean("tarefaTurma");
			tarefa.inserirTarefa(idTopicoSelecionado);
			break;
		case 5:
			ConteudoComunidadeMBean conteudo = (ConteudoComunidadeMBean) getMBean("conteudoComunidadeMBean");
			conteudo.inserirConteudo(idTopicoSelecionado);
			break;
		case 6:
			prepare(SigaaListaComando.ATUALIZAR_CV);

			topAula = (TopicoComunidadeMBean) getMBean("topicoComunidadeMBean");
			object = getGenericDAO().findByPrimaryKey(idTopicoSelecionado, TopicoComunidade.class);
			topAula.setObject(object);
			topAula.instanciarAposEditar();
			forward("/cv/TopicoComunidade/editar.jsp");
			break;
		case 7:
			topAula = (TopicoComunidadeMBean) getMBean("topicoComunidadeMBean");
			topAula.idTopicoSelecionado = idTopicoSelecionado;
			topAula.remover();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Move um discente de um grupo para outro.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void moverTopico (org.richfaces.event.DropEvent event){
		
		Integer idTopicoOrigem = (Integer) event.getDragValue();
		Integer idTopicoDestino = (Integer) event.getDropValue();

		if (ValidatorUtil.isNotEmpty(idTopicoOrigem) && ValidatorUtil.isNotEmpty(idTopicoDestino)){
			try {
				
				MovimentoCadastroCv mov = new MovimentoCadastroCv();					
				mov.setObjMovimentado(new TopicoComunidade(idTopicoOrigem));
				mov.setObjAuxiliar(new TopicoComunidade(idTopicoDestino));
				mov.setComunidade(comunidade());
				
				prepareMovimento(SigaaListaComando.MOVER_TOPICOS_VC);
				mov.setCodMovimento(SigaaListaComando.MOVER_TOPICOS_VC);
				execute(mov);
				listagem = null;
				
			} catch (NegocioException e) {
				addMensagensAjax(e.getListaMensagens());
			} catch (Exception e) {
				tratamentoErroPadrao(e);
			}
		}
	}
	
	/**
	 * Esconde tópico da comunidade para outros membros (exceto administradores ou moderadores).<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/principal.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/_topicos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String esconderTopico () {
		
		GenericDAO dao = null;
		try {
			
			dao = getGenericDAO();
			object = dao.findByPrimaryKey(getParameterInt("id",0), TopicoComunidade.class);
			
			MovimentoCadastroCv mov = new MovimentoCadastroCv();					
			mov.setObjMovimentado(object);
			mov.setComunidade(comunidade());
			
			prepareMovimento(SigaaListaComando.ESCONDE_TOPICOS_CV);
			mov.setCodMovimento(SigaaListaComando.ESCONDE_TOPICOS_CV);
			execute(mov);
			listagem = null;

		} catch (NegocioException e) {
			addMensagensAjax(e.getListaMensagens());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}finally {
			if ( dao != null )
				dao.close();
		}
		return null;
		
	}

	/**
	 * Exibe os tópicos da comunidade.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/principal.jsp</li>
	 * 	<li>/sigaa.war/cv/TopicoComunidade/_topicos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String exibirTopico () {
		
		GenericDAO dao = null;
		try {
			
			dao = getGenericDAO();
			object = dao.findByPrimaryKey(getParameterInt("id",0), TopicoComunidade.class);
			
			MovimentoCadastroCv mov = new MovimentoCadastroCv();					
			mov.setObjMovimentado(object);
			mov.setComunidade(comunidade());
			
			prepareMovimento(SigaaListaComando.EXIBIR_TOPICOS_CV);
			mov.setCodMovimento(SigaaListaComando.EXIBIR_TOPICOS_CV);
			execute(mov);
			listagem = null;

		} catch (NegocioException e) {
			addMensagensAjax(e.getListaMensagens());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}finally {
			if ( dao != null )
				dao.close();
		}
		return null;
	}
		
	/**
	 * Cria um tópico filho e redireciona para página de cadastramento de tópicos.
	 * <br/><br/>Método não invocado por JSP(s):
	 * 
	 * @param idTopico
	 */
	public void cadastrarFilho(Integer idTopico) {
		object = new TopicoComunidade();
		object.setTopicoPai(new TopicoComunidade(idTopico));
		forward("/cv/TopicoComunidade/novo.jsp");
	}

	/**
	 * Retorna um SelectItem com a formatação de Data especificado. 
	 * 
	 * @return
	 */
	public SelectItem getDataCadastro() {
		if (object.getDataCadastro() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy");
			dataInicial = new SelectItem(object.getDataCadastro(), sdf.format(object.getDataCadastro()));
		}

		return dataInicial;
	}

	public void setDataInicial(SelectItem dataInicial) {
		this.dataInicial = dataInicial;
	}

	public List<TopicoComunidade> getTopicosComunidade() {
		return listagem;
	}
	
	public void setTopicosComunidade(List<TopicoComunidade> topicosComunidade) {
		this.listagem = topicosComunidade; 
	}
	
	/**
	 * Cadastra um novo tópico na jsp.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TopicoComunidade/novo.jsp</li>
	 * </ul>
	 * 
	 * @param idTopico
	 */
	@Override
	public String cadastrar () throws ArqException {
		String rs = super.cadastrar();
		
		listagem = null;
		
		return rs;
	}
}