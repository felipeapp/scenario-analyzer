/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 24/09/2007
 * 
 */
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.NotaItemMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Managed-Bean para avalia��o dos projetos de monitoria
 * @author Gleydson
 *
 */

@Component("avalProjetoMonitoria") @Scope("session")
public class AvaliacaoMBean extends SigaaAbstractController<AvaliacaoMonitoria> {

	/** lista que Agrupa v�rios item de uma avalia��o de projeto de monitoria */
	private List<GrupoItemAvaliacao> grupos;
	
	/** Projeto de Ensino de Monitoria ou de Programas de Apoio � Melhoria da Qualidade do Ensino de Gradua��o da UFRN (PAMQEG) */
	private ProjetoEnsino projeto;

	/** lista de avalia��es feita pelos membros da comiss�o de monitoria, comiss�o cient�fica ou prograd(Pr� Reitoria) */
	private List<AvaliacaoMonitoria> avaliacoes;
	
	/** Atributo utilizado para armazenar a lista de projetos de ensino  */
	private List<ProjetoEnsino> projetosDiscrepancia = new ArrayList<ProjetoEnsino>();

	/** edital de monitoria */
	private EditalMonitoria edital;
	
	
	public AvaliacaoMBean() {
		obj = new AvaliacaoMonitoria();
		projeto = new ProjetoEnsino();
		edital = new EditalMonitoria();
	}


	/**
	 * Lista de avalia��es de todos os projetos distribu�dos para o usu�rio logado avaliar
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/lista.jsp</li>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/projetos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<AvaliacaoMonitoria> getAllProjetosAvaliacao() {
		try {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
			
			if(isEmpty(getUsuarioLogado().getServidor())) {
				avaliacoes = new ArrayList<AvaliacaoMonitoria>();
			} else {
				avaliacoes = dao.findAvaliacoesProjetoByUsuario(getUsuarioLogado().getServidor().getId(), 
						TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO, 
						new Integer[]{StatusAvaliacao.AGUARDANDO_AVALIACAO, StatusAvaliacao.AVALIACAO_EM_ANDAMENTO,	StatusAvaliacao.AVALIADO});			
			}			
			return avaliacoes;
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<AvaliacaoMonitoria>();
		}
	}



	


	/**
	 * Lista de avalia��es dos projetos distribu�dos para o usu�rio logado avaliar com status de
	 * aguardando avalia��o.
	 * <br />
	 * M�todo chamado pelas(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs.</li>
	 * </ul> 
	 *
	 * @return
	 */
	public List<AvaliacaoMonitoria> getProjetosNaoAvaliados() {
	    try {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);			
		return dao.findAvaliacoesProjetoByUsuario(getUsuarioLogado().getServidor().getId(),
			TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO, new Integer[]{StatusAvaliacao.AGUARDANDO_AVALIACAO});
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao listar projetos do usu�rio pendentes de avalia��o!");
		return new ArrayList<AvaliacaoMonitoria>();
	    }
	}

	
	
	
	/**
	 * Lista de avalia��es dos projetos distribu�dos para o usu�rio logado avaliar com status de avaliado
	 * <br />
	 * M�todo chamado pela(s) seuinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<AvaliacaoMonitoria> getProjetosAvaliados() {
	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		return dao.findAvaliacoesProjetoByUsuario(getUsuarioLogado().getServidor().getId(), 
			TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO, new Integer[]{StatusAvaliacao.AVALIADO});
	    } catch (DAOException e) {
		notifyError(e);
		return new ArrayList<AvaliacaoMonitoria>();
	    }
	}

	public List<GrupoItemAvaliacao> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoItemAvaliacao> grupos) {
		this.grupos = grupos;
	}

		
	/**
	 * Escolhe uma avalia��o pra visualiz�-la
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/AvaliadorAtividadeExtensao/projetos_avaliador.jsp</li>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/projetos.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 *  <li>sigaa.war/monitoria/VisualizarAvaliacoes/lista.jsp</li>
	 *  <li>sigaa.war/projetos/MembroComissao/projetos_avaliador.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 */
	public String view() {
	    AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
	    try {
		int id = getParameterInt("idAvaliacao",0);			
		obj = dao.findByPrimaryKey(id, AvaliacaoMonitoria.class);			
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro(e.getMessage());
	    }
	    return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_VIEW);
	}
	
	
	/**
	 * Escolhe um projeto que ainda n�o foi avaliado para avalia��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/projetos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String escolherAvaliacao() {

		if ((getAcessoMenu() != null) 
				&& ((getAcessoMenu().isComissaoMonitoria())	|| (getAcessoMenu().isMonitoria()))) {

			try {
				
				int id = getParameterInt("idAvaliacao");			
				obj = getGenericDAO().findByPrimaryKey(id, AvaliacaoMonitoria.class);
				
				for (NotaItemMonitoria nota : obj.getNotasItem()) {
					obj.addNota(nota.getNota());
					nota.getItemAvaliacao().getGrupo().addNota(nota.getNota());
				}
				
				// Carrega os grupos pra montar o formul�rio de avalia��o
				carregaGruposAvaliacoesAtivos(obj.getNotasItem());				
				prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
				
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}
	
			return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_FORM);
			
		}else{
			addMensagemErro("Opera��o n�o autorizada para o usu�rio atual!");
			return null;
		}

	}



	/**
	 * Carrega do banco todos os grupos de avalia��es que estiverem ativos
	 * os grupos comp�em o formul�rio de avalia��o do projeto de monitoria
	 * @param notas
	 * 
	 * N�o � chamado por JSPs.
	 * 
	 * @throws DAOException
	 */
	private void carregaGruposAvaliacoesAtivos(List<NotaItemMonitoria> notas)
			throws DAOException {
		
		AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
		// Pega todos os grupos de formul�rio de avalia��o de projetos
		grupos = (List<GrupoItemAvaliacao>) dao.findByGruposAtivosDoTipo(new Character('P'));

		for ( GrupoItemAvaliacao grupo : grupos ) {
			
			if (isEmpty(notas)) {
				for ( ItemAvaliacaoMonitoria item : grupo.getItens() ) {
					if (item.isAtivo()){
						NotaItemMonitoria notaItem = new NotaItemMonitoria();
						notaItem.setItemAvaliacao(item);
						grupo.getNotas().add(notaItem);
					}
				}
			} else {
				for (NotaItemMonitoria nota : notas) {
					if (nota.getItemAvaliacao().getGrupo().equals(grupo))
						grupo.getNotas().add(nota);
				}
			}
			
		}
	}
	
	
	
	
	/**
	 * Popula as notas e redireciona para tela de confirma��o da avalia��o do projeto 
	 * pelo docente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ParseException
	 * @throws SegurancaException 
	 */
	public String avaliar()  throws ParseException, SegurancaException {
		
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		popularNotas();
		
		// Seta as notas carregadas nos grupos na avalia��o
		obj.setNotasItem(new ArrayList<NotaItemMonitoria>());
		for (GrupoItemAvaliacao grupo : grupos) {
			for (NotaItemMonitoria nota : grupo.getNotas()) {
				nota.setAvaliacao(obj);
				obj.addNotaItemMonitoria(nota);
			}
		}
		
		// Popula Nota da avalia��o
		obj.calcularMedia();

		ListaMensagens lista = obj.validate();
		if (lista != null && lista.getMensagens().size() > 0 ) {
			addMensagens(lista);
			return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_FORM);
		}
		
		return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_CONFIRMAR_AVALIACAO);
	}
	
	

	/**
	 * Confirmar avalia��o de um projeto de monitoria
	 * feita por um membro da comiss�o de monitoria (docente)
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/confirmar_avaliacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws ParseException
	 */
	public String confirmarAvaliacao() throws SegurancaException{
		
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		//avaliar

		try {
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_COMISSAO);

			obj = (AvaliacaoMonitoria) execute(mov, getCurrentRequest());
			addMensagemInformation("Avalia��o realizada com sucesso. A m�dia do projeto � " +
					Formatador.getInstance().formatarDecimalInt(obj.getNotaAvaliacao()) + ".");

			// Prepara MBean pra avaliar outro projeto
			obj = new AvaliacaoMonitoria();
			grupos = new ArrayList<GrupoItemAvaliacao>();			

		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		resetBean();
		return  forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_PROJETOS);
	}


	/**
	 * Buscar uma avalia��o j� realizada para modificar as suas notas
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String alterarAvaliacao() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		
		AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);

		try {
			
			int id = getParameterInt("idAvaliacao");			
			obj = dao.findByPrimaryKey(id, AvaliacaoMonitoria.class);

			grupos = (List<GrupoItemAvaliacao>) dao.findByGruposAtivosDoTipo(new Character('P'));
			
			for ( GrupoItemAvaliacao grupo : grupos ) { // Busca notas da avalia��o anterior
				grupo.getItens().iterator();
				for ( ItemAvaliacaoMonitoria item : grupo.getItens() ) {
					NotaItemMonitoria notaItem = new NotaItemMonitoria();
					notaItem.setItemAvaliacao(item);
					notaItem.setAvaliacao(obj);
					if (obj.getNotasItem().indexOf(notaItem) != -1) {
						notaItem = obj.getNotasItem().get(obj.getNotasItem().indexOf(notaItem));
						grupo.getNotas().add(notaItem);
					}
				}
			}

			prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);

		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_FORM);
	}


	/**
	 * M�todo utilizado para remover uma avalia��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerAvaliacao() {
		return null;
	}

	
	/**
	 * Escolhe o edital de monitoria na tela
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void escolheEdital(ValueChangeEvent evt) {
	    if ( evt.getNewValue() != null ) {
		Integer idEdital = new Integer(evt.getNewValue().toString());
		edital.setId(idEdital);			
	    }
	}

	
	
	/**
	 * Lista todos os projetos, de todos os editais, com notas discrepantes entre os avaliadores
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public Collection<ProjetoEnsino> getProjetosDiscrepancia() throws DAOException, SegurancaException {
	    return projetosDiscrepancia;
	}
	

	/**
	 * Prepara para avaliar projeto por discrep�ncia.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 *  	<li>sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String escolheProjetoDiscrepancia() throws ArqException {	
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		int id = getParameterInt("id");
		MembroComissaoDao dao = getDAO(MembroComissaoDao.class);
		projeto = dao.findByPrimaryKey(id, ProjetoEnsino.class);
		
		MembroComissao membroComissao = dao.findByUsuario(getUsuarioLogado(), MembroComissao.MEMBRO_COMISSAO_MONITORIA);
		if (ValidatorUtil.isEmpty(membroComissao)){
			addMensagemErro("Somente Gestores que fazem parte da Comiss�o de Monitoria podem realizar esta opera��o.");
			return null;
		}
		
		// Prepara MBean pra avaliar projeto por discrep�ncia...
		obj = new AvaliacaoMonitoria();
			obj.setProjetoEnsino(projeto);
			obj.setAvaliacaoPrograd(true);  /// Indica que a avalia��o � por discrep�ncia...
			obj.setDataDistribuicao(new Date());
			obj.setResumoSid(null);
			obj.setTipoAvaliacao(dao.findByPrimaryKey(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO, TipoAvaliacaoMonitoria.class));
			obj.setStatusAvaliacao(dao.findByPrimaryKey(StatusAvaliacao.AVALIADO, StatusAvaliacao.class));
			obj.setDataAvaliacao(new Date());
			obj.setAvaliador(membroComissao);
			
		
		grupos = new ArrayList<GrupoItemAvaliacao>();
		carregaGruposAvaliacoesAtivos(null);

		prepareMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);

		return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_DISCREPANCIA_FORM);

	}
	
	/**
	 * M�todo utilizado para informar as avalia��es do Projeto
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li></li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<AvaliacaoMonitoria> getAvaliacoesProjeto() throws DAOException {
		return avaliacoes;
	}

	
	
	
	/**
	 * Popula as notas e redireciona para tela de confirma��o da avalia��o do projeto 
	 * pelo docente
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ParseException
	 * @throws SegurancaException 
	 */
	public String avaliaProjetoDiscrepancia()  throws ParseException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		popularNotas();
		
		// Seta as notas carregadas nos grupos na avalia��o
		obj.setNotasItem(new ArrayList<NotaItemMonitoria>());
		for (GrupoItemAvaliacao grupo : grupos) {
			for (NotaItemMonitoria nota : grupo.getNotas()) {
				nota.setAvaliacao(obj);
				obj.addNotaItemMonitoria(nota);
			}
		}

		// Popula Nota da avalia��o
		obj.calcularMedia();
		
		ListaMensagens lista = obj.validate();
		if (lista != null && lista.getMensagens().size() > 0 ) {
			addMensagens(lista);
			return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_DISCREPANCIA_FORM);
		}
		
		return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_CONFIRMAR_AVALIACAO);
		
	}

	/**
	 * 
	 * Avalia��o final feita pela PROGRAD.
	 * em caso de notas discrepantes entre os avaliadores do projeto a PROGRAD faz a avalia��o final.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/AvaliacaoMonitoria/confirmar_avaliacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ParseException 
	 * @throws SegurancaException 
	 */
	public String confirmarAvaliacaoDiscrepancia() throws ParseException, SegurancaException {		

		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		try {
			
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO_MONITORIA);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_PROGRAD);

			obj = (AvaliacaoMonitoria) execute(mov, getCurrentRequest());
			addMensagemInformation("Avalia��o realizada com sucesso. A m�dia do projeto � " +
					Formatador.getInstance().formatarDecimalInt(obj.getNotaAvaliacao()) + ".");

			//prepara Mbean pra avaliar outro projeto
			obj = new AvaliacaoMonitoria();
			grupos = new ArrayList<GrupoItemAvaliacao>();
			resetBean();

		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return forward(ConstantesNavegacaoMonitoria.AVALIACAOPROJETO_DISCREPANCIA_LISTA);
	}




	/**
	 * M�todo utilizado para informar o Projeto
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_form.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_list.jsp</li>
	 * </ul>
	 * 
	 * @return the projeto
	 */
	public ProjetoEnsino getProjeto() {
		return projeto;
	}



	/**
	 * M�topo utilizado para setar o Projeto
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param projeto the projeto to set
	 */
	public void setProjeto(ProjetoEnsino projeto) {
		this.projeto = projeto;
	}

	
	
	/**
	 * Carrega todas as notas do formul�rio de avalia��o nos grupos do MBean
	 * 
	 * N�o � chamado por JSPs.
	 * 
	 * @throws ParseException
	 */
	private void popularNotas() throws ParseException {

		if(grupos == null){
			
			try {
				// Pega todos os grupos de formul�rio de avalia��o de projetos
				AvaliacaoMonitoriaDao dao = getDAO(AvaliacaoMonitoriaDao.class);
				grupos = (List<GrupoItemAvaliacao>) dao.findByGruposAtivosDoTipo(new Character('P'));
				
			} catch(Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}

		}

		for (GrupoItemAvaliacao grupo : grupos) {
			int i = 0;
			for (NotaItemMonitoria item : grupo.getNotas()) {
				double nota = getNota("nota_" + grupo.getId() + "_" + i++);
				item.setNota(nota);
			}
		}
		
		
	}


	/**
	 * Pega uma nota em request. Se a nota n�o existir, retorna zero.
	 * Usado pelo popularNotas()
	 * 
	 * N�o � chamado por JSPs.
	 * 
	 * @param paramName
	 * @return
	 * @throws ParseException
	 * @throws NegocioException 
	 */
	private double getNota(String paramName) throws ParseException {
		String param = getParameter(paramName);
		//Pega uma nota em request. Se a nota n�o existir, retorna zero.
		if (param != null && !"".equals(param.trim())) {
			param = param.trim().replaceAll(",", ".");
			return Double.parseDouble(param);
		}

		return 0.0;
	}

	
	/**
	 * Calcula a nota total de acordo com a soma das notas que os grupos de avaliadores deram ao projeto.  
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_form.jsp</li></ul>
	 * 
	 * @return
	 */
	public double getNotaTotal() {
		double total = 0.0;
		if (grupos != null) {
			for (GrupoItemAvaliacao grupo : grupos) {
				total += grupo.getTotalGrupo();
			}
		}
		return total;
	}


	public EditalMonitoria getEdital() {
		return edital;
	}

	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}
	
	public List<AvaliacaoMonitoria> getAvaliacoes() {
		return avaliacoes;
	}


	public void setAvaliacoes(List<AvaliacaoMonitoria> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public void setProjetosDiscrepancia(List<ProjetoEnsino> projetosDiscrepancia) {
	    this.projetosDiscrepancia = projetosDiscrepancia;
	}

}   
