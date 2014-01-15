/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '06/07/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.negocio.RelatorioProdutividadeMov;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;

/**
 * Managed Bean para tratar o crud do cadastro de relatórios
 *
 * @author Eric Moura, Ricardo Wendell, Gleydson Lima
 *
 */
@Component("relatorioAtividades") @Scope("session")
public class RelatorioProdutividadeMBean extends AbstractControllerProdocente<RelatorioProdutividade> {

	private static final String CONTEXTO = "/prodocente/producao/relatorios/produtividade";

	private static final String JSP_ADICIONAR_ITENS = "/cadastro/itens.jsp";

	private static final String JSP_RELATORIO_ITENS = "/cadastro/geral.jsp";

	private static final String JSP_VIEW_RELATORIO = "/emissao/rel_pontuacao.jsp";

	private static final String JSP_LIST_RELATORIOS = "/cadastro/lista.jsp";

	private ArrayList<ItemRelatorioProdutividade> itensGrupo = new ArrayList<ItemRelatorioProdutividade>();

	private ArrayList<GrupoRelatorioProdutividade> gruposRelatorio = new ArrayList<GrupoRelatorioProdutividade>();

	private String nomeGrupo;

	private int pontuacaoMaximaGrupo;

	private int indiceGrupo;

	private int indexGrupoAdicionar;

	private DataModel modelGrupo;

	private ArrayList<DataModel> modelItens = new ArrayList<DataModel>();

	private List<GrupoItem> itensRemovidos = new ArrayList<GrupoItem>();

	private List<GrupoRelatorioProdutividade> gruposRemovidos = new ArrayList<GrupoRelatorioProdutividade>();

	private ArrayList<ItemRelatorioProdutividade> itensUtilizados = new ArrayList<ItemRelatorioProdutividade>();

	// itens de submissao do relatorio
	private int idRelatorio;

	private Servidor docenteRelatorio;

	private Integer anoVigencia = CalendarUtils.getAnoAtual() - 1;

	public void initObj() {
		obj = new RelatorioProdutividade();
		setConfirmButton("Cadastrar Relatório");
		obj.setUsuario(new Usuario());
		obj.setGrupoRelatorioProdutividadeCollection(new ArrayList<GrupoRelatorioProdutividade>());
		indiceGrupo=0;
   	    gruposRelatorio = new ArrayList<GrupoRelatorioProdutividade>();
   	    itensUtilizados = new ArrayList<ItemRelatorioProdutividade>();
   	    itensGrupo = new ArrayList<ItemRelatorioProdutividade>();
   	    modelGrupo = new ListDataModel(gruposRelatorio);
   	    nomeGrupo = "";
   	    pontuacaoMaximaGrupo = 0;
   	    gruposRemovidos = new ArrayList<GrupoRelatorioProdutividade>();
   	    itensRemovidos = new ArrayList<GrupoItem>();
   	    modelItens = new ArrayList<DataModel>();
   	    docenteRelatorio = new Servidor();
   	    
	}

	public RelatorioProdutividadeMBean() {
		initObj();
	}

	/* Operação de cadastro relatório*/

	/**
	 * Método que adiciona um grupo ao relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void adicionarGrupo(ActionEvent atl){

		if(!nomeGrupo.equals("") && nomeGrupo != null){
			GrupoRelatorioProdutividade grupoRelatorioProdutividade = new GrupoRelatorioProdutividade();
			grupoRelatorioProdutividade.setTitulo(nomeGrupo);
			grupoRelatorioProdutividade.setPontuacaoMaxima(pontuacaoMaximaGrupo);
			grupoRelatorioProdutividade.setNumeroTopico(indiceGrupo++);
			grupoRelatorioProdutividade.setRelatorioProdutividade(obj);
			gruposRelatorio.add(grupoRelatorioProdutividade);
			nomeGrupo ="";
			pontuacaoMaximaGrupo=0;
		} else {
			addMessage("Nome do grupo não pode ser vazio!", TipoMensagemUFRN.ERROR);
		}
	}

	/**
	 * Método que remove o grupo e seus itens do relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void removerGrupo(ActionEvent atl){
		int indexGrupo = modelGrupo.getRowIndex();

		gruposRemovidos.add(gruposRelatorio.remove(indexGrupo));
		modelItens.remove(indexGrupo);

	}

	/**
	 * Método que adiciona um item ao grupo selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/itens.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public String adicionarItem(){
		//índice do grupo a ser adicionado o item

		//índice de visualização a ser persistido no banco
		int indexTopico = gruposRelatorio.get(indexGrupoAdicionar).getGrupoItemCollection().size() + 1;

		for(ItemRelatorioProdutividade item: itensGrupo){
			if(item.getSelecionado()){
				GrupoItem grupoItem = new GrupoItem();
				grupoItem.setItemRelatorioProdutividade(item);
				grupoItem.setIndiceTopico(indexTopico++);
				grupoItem.setGrupoRelatorioProdutividade(gruposRelatorio.get(indexGrupoAdicionar));
				(gruposRelatorio.get(indexGrupoAdicionar)).getGrupoItemCollection().add(grupoItem);
				//adicionando o item na coleção de itens utilizados
				itensUtilizados.add(item);
			}

		}
		nomeGrupo ="";
		pontuacaoMaximaGrupo=0;
		indexGrupoAdicionar =0;
		return forward(CONTEXTO+JSP_RELATORIO_ITENS);
	}

	/**
	 * Método que remove um item do grupo selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/itens.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void removerItem(ActionEvent atl){
		//índice do grupo a ser removido
		int indexGrupo = modelGrupo.getRowIndex();

		//índice do item a ser removido
		int indexItem = modelItens.get(indexGrupo).getRowIndex();

		ItemRelatorioProdutividade itemRemovido = gruposRelatorio.get(indexGrupo).getGrupoItemCollection().get(indexItem).getItemRelatorioProdutividade();

		itensUtilizados.remove(itemRemovido);

		itensRemovidos.add(gruposRelatorio.get(indexGrupo).getGrupoItemCollection().remove(indexItem));

		reIndexarTopicos(gruposRelatorio.get(indexGrupo).getGrupoItemCollection());

	}

	/**
	 * Método que move o item para cima no relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void moveItemCima(ActionEvent atl){
		//índice do grupo que sera removido o item
		int indexGrupo = modelGrupo.getRowIndex();

		//índice do item a ser removido
		int indexItem = modelItens.get(indexGrupo).getRowIndex();

		if(indexItem!=0){
			GrupoItem grupoItem = gruposRelatorio.get(indexGrupo).getGrupoItemCollection().get(indexItem);
			grupoItem.setIndiceTopico(indexItem);

			gruposRelatorio.get(indexGrupo).getGrupoItemCollection().remove(indexItem);
			gruposRelatorio.get(indexGrupo).getGrupoItemCollection().add(indexItem - 1, grupoItem);
			reIndexarTopicos(gruposRelatorio.get(indexGrupo).getGrupoItemCollection());
		} else
			addMessage("Item já no início da lista!",TipoMensagemUFRN.WARNING);
	}

	/**
	 * Método utilizado para reorganizar os indices depois de mover um item para cima ou para baixo.
	 * @param grupos
	 */
	public void reIndexarTopicos(Collection <GrupoItem> grupos){
		int i = 1;
		for (GrupoItem item : grupos) {
			item.setIndiceTopico(i++);
		}
	}

	/**
	 * Método que move o item para baixo no relatório
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @param atl
	 */
	public void moveItemBaixo(ActionEvent atl){
		//índice do grupo que sera removido o item
		int indexGrupo = modelGrupo.getRowIndex();

		//índice do item a ser removido
		int indexItem = modelItens.get(indexGrupo).getRowIndex();

		if(indexItem < gruposRelatorio.get(indexGrupo).getGrupoItemCollection().size()){
			GrupoItem grupoItem = gruposRelatorio.get(indexGrupo).getGrupoItemCollection().get(indexItem);
			grupoItem.setIndiceTopico(indexItem+1);

			gruposRelatorio.get(indexGrupo).getGrupoItemCollection().remove(indexItem);
			gruposRelatorio.get(indexGrupo).getGrupoItemCollection().add(indexItem+1, grupoItem);
			reIndexarTopicos(gruposRelatorio.get(indexGrupo).getGrupoItemCollection());
		} else
			addMessage("Item já no final da lista!",TipoMensagemUFRN.WARNING);
	}

	/**
	 * Método que retorna a página de inserção dos itens de um grupo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String buscarItens(){
		// índice do grupo que sera removido o item
		indexGrupoAdicionar = modelGrupo.getRowIndex();

		nomeGrupo = gruposRelatorio.get(indexGrupoAdicionar).getTitulo();
		pontuacaoMaximaGrupo = gruposRelatorio.get(indexGrupoAdicionar).getPontuacaoMaxima();
		return forward(CONTEXTO+JSP_ADICIONAR_ITENS);
	}

	/**
	 * Redirecionar para pagina de cadastro do relatorio.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/itens.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String cancelarAdicionaItem() {
		return forward(CONTEXTO + JSP_RELATORIO_ITENS);
	}

	/**
	 * Cancelar cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/form.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/itens.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cancelar() {
		initObj();
		return super.cancelar();
	}

	/**
	 * Iniciar o cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String preCadastrar() {
		initObj();
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(CONTEXTO + JSP_RELATORIO_ITENS);
		}
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String atualizar() {
		try {

			setId();
			PersistDB obj = this.obj;
			initObj();

			setReadOnly(false);

			this.obj = getGenericDAO().findByPrimaryKey(obj
					.getId(), RelatorioProdutividade.class);

			gruposRelatorio = new ArrayList<GrupoRelatorioProdutividade>();
			gruposRelatorio.addAll(this.obj.getGrupoRelatorioProdutividadeCollection());

			for(GrupoRelatorioProdutividade gruporel : gruposRelatorio){
				for (GrupoItem grupoItemRel: gruporel.getGrupoItemCollection()){
					itensUtilizados.add(grupoItemRel.getItemRelatorioProdutividade());
				}
			}
			modelGrupo = new ListDataModel(gruposRelatorio);
			indiceGrupo = gruposRelatorio.size();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		setConfirmButton("Alterar Relatório");

		return forward(CONTEXTO + JSP_RELATORIO_ITENS);
	}


	@Override
	public String preRemover() {
		try {

			setId();
			PersistDB obj = this.obj;
			initObj();

			//comando = SigaaListaComando.REMOVER_RELATORIO_PRODUTIVIDADE;

			setReadOnly(true);

			this.obj = getGenericDAO().findByPrimaryKey(obj
					.getId(), RelatorioProdutividade.class);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		setConfirmButton("Remover Relatório");

		return forward(CONTEXTO + JSP_RELATORIO_ITENS);
	}

	/**
	 * Retorna uma coleção de Relátorios de Produtividade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/ipi/lista.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/emissao/selecao_relatorio.jsp</li>
	 *	</ul>
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAllAtivo(RelatorioProdutividade.class, "id", "titulo");
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s): relatorioAtividades
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() {
		MovimentoCadastro mov = new MovimentoCadastro();
		try{
			ReflectionUtils.evalProperty(obj, "ativo");
		} catch (Exception e) {  }

		try {
			prepareMovimento(ArqListaComando.DESATIVAR);

			obj.setId(getParameterInt("id"));
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			addMensagemErro("Não há objeto informado para remoção");
			return null;
		} else {
			mov.setCodMovimento(ArqListaComando.DESATIVAR);
			try {
				execute(mov, (HttpServletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());

				addMessage("Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			} catch (Exception e) {
				addMensagemErro("Erro Inesperado: " + e.getMessage());
				e.printStackTrace();
				return forward(CONTEXTO + JSP_LIST_RELATORIOS);
			}
			resetBean();
			setResultadosBusca(null);
			afterRemover();
			return forward(CONTEXTO + JSP_LIST_RELATORIOS);
		}
	}

	/**
	 * Responsável por submeter um Relatório de Produtividade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterRelatorio() throws ArqException {

		//Testar se não existe grupos vazios
		for(GrupoRelatorioProdutividade gruporel: gruposRelatorio){
			if(gruporel.getGrupoItemCollection().isEmpty()){
				addMessage("Não é permitido cadastrar relatório com grupo vazio! ("+gruporel.getTitulo()+")",
						TipoMensagemUFRN.ERROR);
				return null;
			}
		}

		Comando comando;
		if(obj.getId()==0)
			comando = SigaaListaComando.CADASTRAR_RELATORIO_PRODUTIVIDADE;
		else
			comando = SigaaListaComando.ALTERAR_RELATORIO_PRODUTIVIDADE;

		RelatorioProdutividadeMov mov = new RelatorioProdutividadeMov();
		obj.setUsuario(getUsuarioLogado());
		obj.setDataCriacao(new Date());
		obj.setGrupoRelatorioProdutividadeCollection(gruposRelatorio);
		obj.setAtivo(true);
		try {
			prepareMovimento(comando);
		} catch (ArqException e1) {
			e1.printStackTrace();
		}
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setGrupoItemExcluidos(itensRemovidos);
		mov.setGrupoRelatorioProdutividadeExcluidos(gruposRemovidos);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		super.afterCadastrar();

		if (comando.equals(SigaaListaComando.CADASTRAR_RELATORIO_PRODUTIVIDADE)) {
			addMessage("Relatório cadastrado com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			initObj();
			return forward(CONTEXTO + JSP_LIST_RELATORIOS);
		} else {
			addMessage("Relatório alterado com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			initObj();
			return forward(CONTEXTO + JSP_LIST_RELATORIOS);
		}
	}

	/*Operações de emissão do relatório*/

	/**
	 * Responsável por montar o Relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsp</li>
	 *	</ul>
	 */
	public String montarRelatorio() throws ArqException {
		
		GenericDAO dao = getGenericDAO();
		if (docenteRelatorio == null || docenteRelatorio.getId() == 0 || "".equals(docenteRelatorio.getPessoa().getNome())) {
			if (isAcessoPrivilegiado() || getAcessoMenu().isChefeDepartamento()) {
				addMensagemErro("Selecione um docente.");
				clear();
				return null;
			} else {
				docenteRelatorio = getServidorUsuario();
			}
		}
			
		if (this.anoVigencia == null || this.anoVigencia <= 0 || this.anoVigencia > CalendarUtils.getAnoAtual()){
			addMensagemErro("Informe um ano válido.");
			clear();
			return null;
		}

		FormacaoAcademicaRemoteService serviceFormacao = getMBean("formacaoAcademicaInvoker");
		
		docenteRelatorio = dao.findByPrimaryKey(docenteRelatorio.getId(), Servidor.class);

		obj = RelatorioHelper.montarRelatorioProdutividade(
				dao.findByPrimaryKey( idRelatorio, RelatorioProdutividade.class),
				docenteRelatorio,
				anoVigencia, serviceFormacao);

		return forward(CONTEXTO + JSP_VIEW_RELATORIO);
	}

	/**
	 * Verifica se o usuário tem acesso previlegiado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/emissao/seleciona_docente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException 
	 */
	public boolean isAcessoPrivilegiado() throws DAOException {
		return getUsuarioLogado().isUserInRole(new int[] {SigaaPapeis.GESTOR_PESQUISA,
				SigaaPapeis.PORTAL_PLANEJAMENTO, SigaaPapeis.MEMBRO_COMITE_PESQUISA }) || isChefeDeUnidadeComLotacao();
	}

	/**
	 * Indica se o usuário é responsável por alguma unidade que tenha docente lotado.
	 * @return
	 * @throws DAOException
	 */
	private boolean isChefeDeUnidadeComLotacao() throws DAOException {
		if (getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isResponsavel()) {
			ServidorDao dao = getDAO(ServidorDao.class);
			Collection<Servidor> docentes = dao.findByDocente(getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
			
			if (isNotEmpty(docentes))
				return true;
		}
		
		return false;
	}

	/**
	 * @return the anoVigencia
	 */
	public Integer getAnoVigencia() {
		return anoVigencia;
	}

	/**
	 * @param anoVigencia the anoVigencia to set
	 */
	public void setAnoVigencia(Integer anoVigencia) {
		this.anoVigencia = anoVigencia;
	}

	/**
	 * @return the gruposRelatorio
	 */
	public ArrayList<GrupoRelatorioProdutividade> getGruposRelatorio() {
		return gruposRelatorio;
	}

	/**
	 * @param gruposRelatorio the gruposRelatorio to set
	 */
	public void setGruposRelatorio(ArrayList<GrupoRelatorioProdutividade> gruposRelatorio) {
		this.gruposRelatorio = gruposRelatorio;
	}

	/**
	 * @return the nomeGrupo
	 */
	public String getNomeGrupo() {
		return nomeGrupo;
	}

	/**
	 * @param nomeGrupo the nomeGrupo to set
	 */
	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	/**
	 * @return the pontuacaoMaximaGrupo
	 */
	public int getPontuacaoMaximaGrupo() {
		return pontuacaoMaximaGrupo;
	}

	/**
	 * @param pontuacaoMaximaGrupo the pontuacaoMaximaGrupo to set
	 */
	public void setPontuacaoMaximaGrupo(int pontuacaoMaximaGrupo) {
		this.pontuacaoMaximaGrupo = pontuacaoMaximaGrupo;
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/itens.jsp</li>
	 *	</ul>
	 * @return the itensGrupo
	 * @throws DAOException
	 */
	public ArrayList<ItemRelatorioProdutividade> getItensGrupo() throws DAOException {
		itensGrupo = new ArrayList<ItemRelatorioProdutividade>();
		GenericDAO dao = getGenericDAO();

		Collection<ItemRelatorioProdutividade> itens = dao
				.findAll(ItemRelatorioProdutividade.class,"numeroTopico","asc");
		itensGrupo.addAll(itens);
		itensGrupo.removeAll(itensUtilizados);
		return itensGrupo;
	}


	/**
	 * @return the modelGrupo
	 */
	public DataModel getModelGrupo() {
		return modelGrupo;
	}


	/**
	 * @param modelGrupo the modelGrupo to set
	 */
	public void setModelGrupo(DataModel modelGrupo) {
		this.modelGrupo = modelGrupo;
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/produtividade/cadastro/geral.jsp</li>
	 *	</ul>
	 * @return the modelItem
	 */
	public DataModel getModelItem() {
		int indexGrupo = modelGrupo.getRowIndex();
		DataModel modelItem;

		try{
			modelItem = modelItens.get(indexGrupo);

		} catch (IndexOutOfBoundsException e) {
			GrupoRelatorioProdutividade grupo = gruposRelatorio.get(indexGrupo);
			modelItem = new ListDataModel(grupo.getGrupoItemCollection());
			modelItens.add(indexGrupo, modelItem);
		}
		return modelItem;
	}


	/**
	 * @return the docenteRelatorio
	 */
	public Servidor getDocenteRelatorio() {
		return docenteRelatorio;
	}


	/**
	 * @param docenteRelatorio the docenteRelatorio to set
	 */
	public void setDocenteRelatorio(Servidor docenteRelatorio) {
		this.docenteRelatorio = docenteRelatorio;
	}


	/**
	 * @return the idRelatorio
	 */
	public int getIdRelatorio() {
		return idRelatorio;
	}


	/**
	 * @param idRelatorio the idRelatorio to set
	 */
	public void setIdRelatorio(int idRelatorio) {
		this.idRelatorio = idRelatorio;
	}

	public String getEscopoBuscaServidores() {
		if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO)){
			return "todos";
		} else if (isUserInRole(SigaaPapeis.MEMBRO_COMITE_PESQUISA)){
				return "centro";
		} else {
			return "D";
		}

	}

	private void clear(){
		initObj();
	}

}
