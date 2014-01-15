package br.ufrn.sigaa.projetos.jsf;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;

/**
 * Controlador responsável por efetuar buscas de avaliações de projetos.
 * 
 * @author Ilueny
 *
 */
@Scope(value = "session")
@Component(value = "buscaAvaliacoesProjetosBean")
public class BuscaAvaliacoesProjetosMBean extends SigaaAbstractController<Avaliacao> {

	
	//Atributos usados para a inserção de informações em telas de buscas.
	/** Título do projeto utilizado na busca. */
	private String buscaTitulo;
	/** Ano do projeto utilizado na busca. */
	private Integer buscaAno = CalendarUtils.getAnoAtual();
	/** Unidade do projeto utilizada na busca. */
	private int buscaUnidade;	
	/** Situação do projeto utilizada na busca. */
	private int buscaSituacao;
	/** Avaliador do projeto utilizado na busca. */
	private AvaliadorProjeto buscaAvaliador = new AvaliadorProjeto();    
	
	//Atributos usados para a seleção de opções de busca em telas de busca.
	/** Indica se a busca será feita por Título do projeto. */
	private boolean checkBuscaTitulo;	
	/** Indica se a busca será feita por Ano do projeto. */
	private boolean checkBuscaAno;	
	/** Indica se a busca será feita por Unidade do projeto. */
	private boolean checkBuscaUnidade;	
	/** Indica se a busca será feita por Situação do projeto. */
	private boolean checkBuscaSituacao;	
	/** Indica se a busca será feita por Avaliador do projeto. */
	private boolean checkBuscaAvaliador;	
	
	/** Referência para o elemento HTML da tabela que apresenta a lista de avaliações resultado da busca. */
	private UIData uiData = new HtmlDataTable();
	/** Lista de projetos que armazena o resultado da busca. */
	private List<Projeto> projetos = new ArrayList<Projeto>();

	/** Tipo do projeto utilizado na busca. */
	private int tipoProjeto = TipoProjeto.ASSOCIADO;
	
	/**
	 * Contrutor padrão.
	 */
	public BuscaAvaliacoesProjetosMBean() {
		obj = new Avaliacao();
	}
	
	/**
	 * Inicia a busca de avaliações de projetos.
	 * 
     *  <ul>
     *  	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
     *  </ul>
     *  
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBusca() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		projetos = new ArrayList<Projeto>();
		uiData = new HtmlDataTable();
		if(isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO) && getSubSistema().getId() == SigaaSubsistemas.ACOES_ASSOCIADAS.getId())
	    	tipoProjeto = TipoProjeto.ASSOCIADO;
	    else if(isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && getSubSistema().getId() == SigaaSubsistemas.PESQUISA.getId())
	    	tipoProjeto = TipoProjeto.PESQUISA;
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_BUSCA);
	}
	
	/** 
	 * Filtra Avaliações de projetos.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
	 * 
	 * @throws SegurancaException
	 * @throws DAOException 
	 */	
	public String buscar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		try {
			Integer ano = null;
			String titulo = null;
			TipoSituacaoAvaliacao situacao = null;
			Usuario usuarioAvaliador = null;
			Unidade unidadeProponente = null;
			
			ListaMensagens lista = new ListaMensagens();			
			projetos = new ArrayList<Projeto>();
			uiData = new HtmlDataTable();


			if (checkBuscaAno) {
			    ano = buscaAno;
			    ValidatorUtil.validateRequired(ano, "Ano", lista);
			}
			
			if (checkBuscaTitulo) {
			    titulo = buscaTitulo;
			    ValidatorUtil.validateRequired(titulo, "Título", lista);
			}
			
			if (checkBuscaAvaliador) {
			    usuarioAvaliador = new Usuario(buscaAvaliador.getUsuario().getId());
			    ValidatorUtil.validateRequired(buscaAvaliador.getUsuario(), "Avaliador(a)", lista);
			}
			
			if (checkBuscaUnidade) {
			    unidadeProponente = new Unidade(buscaUnidade);
			    ValidatorUtil.validateRequiredId(buscaUnidade, "Unidade Proponente", lista);
			}
			
			if (checkBuscaSituacao) {
			    situacao = new TipoSituacaoAvaliacao(buscaSituacao);
			    ValidatorUtil.validateRequiredId(buscaSituacao, "Situação da Avaliação", lista);
			}
			
			if (!checkBuscaAno && !checkBuscaTitulo && !checkBuscaUnidade
					&& !checkBuscaSituacao && !checkBuscaAvaliador) {
			    lista.addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);   
			}
			
			if (lista.isEmpty()) {
			    ProjetoDao dao = getDAO(ProjetoDao.class);
			    projetos = dao.findByAvaliacoes(usuarioAvaliador, situacao, titulo, ano, unidadeProponente, tipoProjeto);
			}else {
			    addMensagens(lista);
			}

		} catch (LimiteResultadosException e) {
		    addMensagemWarning(e.getMessage());
		}
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_BUSCA);
	}
	
	/**
	 * Retorna o total de avaliações localizadas.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
	 * 
	 * @return
	 */
	public int getTotalAvaliacoesLocalizadas() {
		int total = 0;
		for (Projeto pj : projetos) {
			total += pj.getAvaliacoes().size();
		}
		return total;
	}
	
	/**
	 * Retorna todos os tipos de situações ativas possíveis para 
	 * seleção entre os parâmetros da busca.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */	
	public Collection<SelectItem> getAllSituacaoAvaliacaoCombo() throws ArqException {		
		return toSelectItems(getGenericDAO().findAllAtivos(TipoSituacaoAvaliacao.class, "descricao"), "id", "descricao");
	}
	
	
	/**
     * Método utilizado para visualizar avalições.
     * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
     * 
     * @return
     */
	public String view() throws ArqException, RemoteException, NegocioException {
		AvaliacaoProjetoMBean bean = getMBean("avaliacaoProjetoBean");
		bean.setConfirmButton("");
		bean.setUiData(uiData);
		return bean.view();
	}

	/**
     * Método utilizado para prepara a remoção de avalições.
     * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
     * 
     * @return
	 * @throws NegocioException 
	 * @throws RemoteException 
	 * @throws ArqException 
     */
	@Override
	public String preRemover()  {
		AvaliacaoProjetoMBean bean = getMBean("avaliacaoProjetoBean");
		bean.setUiData(uiData);
		return bean.preRemover();
	}

	
	/**
	 * Remove uma avaliação do projeto.
	 * 
     *  Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
     *  </ul>
     *   
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String remover() throws SegurancaException, DAOException {
		AvaliacaoProjetoMBean bean = getMBean("avaliacaoProjetoBean");
		bean.remover();
		return buscar();
	}
	
	
	public String getBuscaTitulo() {
	    return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
	    this.buscaTitulo = buscaTitulo;
	}

	public Integer getBuscaAno() {
	    return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
	    this.buscaAno = buscaAno;
	}

	public int getBuscaUnidade() {
	    return buscaUnidade;
	}

	public void setBuscaUnidade(int buscaUnidade) {
	    this.buscaUnidade = buscaUnidade;
	}

	public int getBuscaSituacao() {
	    return buscaSituacao;
	}

	public void setBuscaSituacao(int buscaSituacao) {
	    this.buscaSituacao = buscaSituacao;
	}

	public boolean isCheckBuscaTitulo() {
	    return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
	    this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaAno() {
	    return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
	    this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaUnidade() {
	    return checkBuscaUnidade;
	}

	public void setCheckBuscaUnidade(boolean checkBuscaUnidade) {
	    this.checkBuscaUnidade = checkBuscaUnidade;
	}

	public boolean isCheckBuscaSituacao() {
	    return checkBuscaSituacao;
	}

	public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
	    this.checkBuscaSituacao = checkBuscaSituacao;
	}

	public AvaliadorProjeto getBuscaAvaliador() {
		return buscaAvaliador;
	}

	public void setBuscaAvaliador(AvaliadorProjeto buscaAvaliador) {
		this.buscaAvaliador = buscaAvaliador;
	}

	public boolean isCheckBuscaAvaliador() {
		return checkBuscaAvaliador;
	}

	public void setCheckBuscaAvaliador(boolean checkBuscaAvaliador) {
		this.checkBuscaAvaliador = checkBuscaAvaliador;
	}

	public List<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<Projeto> projetos) {
		this.projetos = projetos;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	public int getTipoProjeto() {
		return tipoProjeto;
	}

	public void setTipoProjeto(int tipoProjeto) {
		this.tipoProjeto = tipoProjeto;
	}
	
}
