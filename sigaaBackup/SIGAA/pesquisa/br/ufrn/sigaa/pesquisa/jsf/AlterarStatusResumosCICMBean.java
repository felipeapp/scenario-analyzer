/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2009
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * 
 * Controlador para gerenciar a operação de Mudança de Status de Resumos CIC(Congresso de Iniciação Científica)
 *  pelo gestor do módulo de pesquisa.
 * 
 * @author Geyson
 *
 */
@Component("alterarStatusResumos")
@Scope("request")
public class AlterarStatusResumosCICMBean extends SigaaAbstractController<ResumoCongresso> {

	
	private Collection<ResumoCongresso> resumosSelecionados = new ArrayList<ResumoCongresso>();
	private Collection<ResumoCongresso> lista = new ArrayList<ResumoCongresso>();
	
	private boolean filtroCentro;
	private boolean filtroArea;
	private boolean filtroStatus;
	private boolean filtroOrientador;

	private Unidade unidade;
	private AreaConhecimentoCnpq area;
	private Servidor orientador;
	private CongressoIniciacaoCientifica congresso;
	
	private Integer areaId;
	private Integer status;
	private Integer unidadeId;
	private String nomeOrientador;
	
	
	
	/**
	 * Construtor Padrão
	 */
	public AlterarStatusResumosCICMBean(){
		obj = new ResumoCongresso();
		setUnidade(new Unidade());
		setArea(new AreaConhecimentoCnpq());
		setOrientador(new Servidor());
		setResumosSelecionados(new ArrayList<ResumoCongresso>());
		setCongresso(new CongressoIniciacaoCientifica());
	}
	
	/** Retorna uma coleção de SelectItem de Status de Resumos CIC.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllStatusCombo() throws DAOException {
		return toSelectItems(obj.getTiposStatus());
	}
	
	/**
	 * Retorna uma coleção de SelectItem das Áreas de Conhecimento de Resumos CIC.
	 * @return
	 */
	public Collection<SelectItem> getAllAreaCombo() {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findGrandeAreasConhecimentoCnpq(), "id",
					"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
		}
	}
	
	/**
	 *  Redireciona para a página de visualização dos dados dos resumos.
	 *  JSP: sigaa.war/pesquisa/alterar_status_resumosCIC/form.jsp
	 * @return
	 */
	public String visualizarResumo(){
		int id = getParameterInt("id");
		return redirect("/pesquisa/resumoCongresso.do?id="+id+"&dispatch=view");
	}
	
	/**
	 * Adiciona resumos selecionados na coleção de resumos Selecionados e redireciona para a página de visualização de resumos.
	 * JSP: sigaa.war/pesquisa/alterar_status_resumosCIC/form.jsp
	 * @return
	 */
	public String mudancaStatusSelecionados(){
		setId();
		for (ResumoCongresso resumo : lista) {
			if(resumo.isSelecionado()){
				resumosSelecionados.add(resumo);	
				
			}
		}
		obj.setStatus(0);
		return forward("/pesquisa/alterar_status_resumosCIC/alerar_status_resumosCIC.jsp");
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a busca de resumos
	 * JSP: sigga/pesquisa/menu.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException 
	 */
	public String iniciarMudancaStatus() throws ArqException{
		checkListRole();
		prepareMovimento(SigaaListaComando.ALTERAR_STATUS_RESUMOS_CIC);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_RESUMOS_CIC.getId());
		return forward("/pesquisa/alterar_status_resumosCIC/form.jsp"); 
	}
	
	/**
	 * Retorna lista de Resumos de acordo com filtro(s) passado(s).
	 * JSP: sigaa.war/pesquisa/alterar_status_resumosCIC/form.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResumoCongresso> buscarResumos() throws DAOException{
		ResumoCongressoDao dao = getDAO(ResumoCongressoDao.class);
		validaCampos();
		
		if(!hasErrors()){
			if(unidadeId != null){
				UnidadeDao daoUni = getDAO(UnidadeDao.class);
				unidade = daoUni.findByPrimaryKey(unidade.getId(), Unidade.class);
				lista = dao.filter(congresso.getId(), areaId, unidade, null, null, status, null, 
						nomeOrientador, null, null);
			}else
				lista = dao.filter(congresso.getId(), areaId, null, null, null, status, null, nomeOrientador, null, null);
		
		if(lista.size() > 0){
			setResultadosBusca(lista);
		}
		else{
			addMensagemWarning("Nenhum registro foi encontrado.");
			return null; 
		}
		
		return lista;
		}
		
		return null;
	}
	
	/**
	 * valida filtros.
	 * JSP: Não invocado por JSP.
	 * @throws DAOException
	 */
	public void validaCampos() throws DAOException{
		areaId = null;
		nomeOrientador = null;
		status = null;
		unidadeId = null;
		
		if(isFiltroArea()){
			areaId = area.getId();
			if(area.getId() <= 0)
				addMensagemErro("Área de Conhecimento: preencher campo selecionado.");
		}else
			setArea(new AreaConhecimentoCnpq());
	
		if(isFiltroCentro()){
			unidadeId = unidade.getId();
			if(unidade.getId() <= 0){
				addMensagemErro("Unidade/Centro: preencher campo selecionado.");			
			}
		}else
			setUnidade(new Unidade());
		
		if(isFiltroOrientador()){
			if(orientador.getPessoa().getNome().isEmpty())
				addMensagemErro("Orientador: preencher campo selecionado.");
			else{
				ServidorDao daoSeridor = getDAO(ServidorDao.class);
				setOrientador(daoSeridor.findByPrimaryKey(orientador.getId(), Servidor.class));
				nomeOrientador = orientador.getPessoa().getNome();
			}
		}else
			setOrientador(new Servidor());
		
		if(isFiltroStatus()){
			status = obj.getStatus();
			if(obj.getStatus() == 0)
				addMensagemErro("Status: preencher campo selecionado.");
		}else
			obj.setStatus(0);
	}
	

	/**
	 * Realiza alteração de status nos resumos selecionados.
	 * JSP: sigaa.war/pesquisa/alterar_status_resumosCIC/alterar_status_resumosCIC.jsp
	 * @return
	 * @throws ArqException
	 */
	public String mudarStatus() throws ArqException{
		setId();
		
		if(!checkOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_RESUMOS_CIC.getId())){
			return forward("/pesquisa/menu.jsp");
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		
		if(obj.getStatus() == 0){
			addMensagemErro("Selecione novo status.");
			return null;
		}
		
		if(resumosSelecionados.size() <= 0){
			addMensagemWarning("Selecione algum Resumo para realizar a mudança de status.");
			return null;
		}
		if(!(resumosSelecionados == null))
			mov.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_RESUMOS_CIC);
		
		for (ResumoCongresso resumo : resumosSelecionados) {
			if(resumo.getStatus() == obj.getStatus()){
				addMensagemWarning(resumo.getTitulo() + ": já possui o Status selecionado");
			}
		}
		
		mov.setObjMovimentado(obj);
		mov.setColObjMovimentado(resumosSelecionados);
		try {
			execute(mov);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		} 
		addMensagem(OPERACAO_SUCESSO);
		return forward("/pesquisa/menu.jsp");
	}
	

	public Collection<ResumoCongresso> getResumosSelecionados() {
		return resumosSelecionados;
	}


	public void setResumosSelecionados(
			Collection<ResumoCongresso> resumosSelecionados) {
		this.resumosSelecionados = resumosSelecionados;
	}


	public Collection<ResumoCongresso> getLista() {
		return lista;
	}


	public void setLista(Collection<ResumoCongresso> lista) {
		this.lista = lista;
	}


	public boolean isFiltroCentro() {
		return filtroCentro;
	}


	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}


	public boolean isFiltroArea() {
		return filtroArea;
	}


	public void setFiltroArea(boolean filtroArea) {
		this.filtroArea = filtroArea;
	}


	public boolean isFiltroStatus() {
		return filtroStatus;
	}


	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}


	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}


	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}




	public Unidade getUnidade() {
		return unidade;
	}




	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}




	public AreaConhecimentoCnpq getArea() {
		return area;
	}




	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public Servidor getOrientador() {
		return orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNomeOrientador() {
		return this.nomeOrientador;
	}

	public void setNomeOrientador(String nomeOrientador) {
		this.nomeOrientador = nomeOrientador;
	}

	public Integer getUnidadeId() {
		return unidadeId;
	}

	public void setUnidadeId(Integer unidadeId) {
		this.unidadeId = unidadeId;
	}
	
	
	
	
}
