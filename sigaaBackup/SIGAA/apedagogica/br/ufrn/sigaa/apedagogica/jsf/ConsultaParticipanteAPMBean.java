package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.apedagogica.dao.ConsultaParticipanteAtividadeDAO;
import br.ufrn.sigaa.apedagogica.dao.GrupoAtividadesAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controller que gerencia as operações dos participantes das atividades.
 * @author Mário Rizzi
 *
 */
@Component("consultaParticipanteAP") @Scope("session")
public class ConsultaParticipanteAPMBean 
		extends SigaaAbstractController<ParticipanteAtividadeAtualizacaoPedagogica>{
	
	/** Filtros do docente da busca dos participante. */
	private String filtroDocente;
	/** Filtros de atividade da busca dos participante. */
	private Integer filtroAtividade;
	/** Filtros de grupo da busca dos participante. */
	private Integer filtroGrupo;
	/** Filtros da situação da busca dos participante. */
	private Integer filtroSituacao;
	
	/** Utilizado no campop autocomlpete dos grupos na consulta. */
	private String nomeGrupo;
	/** Possui as atividades do grupo selecionado */
	private Collection<SelectItem> atividadesGrupoCombo;
	
	/** Nome do Managed Bean responsável pela operação */
	private OperacaoParticipanteAtividadeAP operacao;
	
	/** Código da operação a ser realizada (Deve ser passado como parâmetro ou definido no Bean) */
	private int codigoOperacao;

	
	public ConsultaParticipanteAPMBean(){
		
		obj = new ParticipanteAtividadeAtualizacaoPedagogica();
		obj.getAtividade().setNome(null);
		obj.getAtividade().getGrupoAtividade().setDenominacao(null);
		all = null;
		atividadesGrupoCombo = new ArrayList<SelectItem>();
		clearFiltros();
		
	}
	
	/**
	 * Limpa os filtros da busca.
	 * Método não invocado por JSP's. 
	 */
	public void clearFiltros(){
		
		filtroDocente = null;
		filtroAtividade = 0;
		filtroGrupo = null;
		filtroSituacao = 0;
		nomeGrupo = null;
	}
	
	/**
	 * Define os papéis que podem acessar os métodos da classe.
	 * Método não invocado por JSP's. 
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#checkChangeRole() 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.GESTOR_PAP, SigaaPapeis.DOCENTE);
		
	}

	/**
	 * Define o diretório base para as operações de CRUD.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getDirBase() 
	 */
	@Override
	public String getDirBase() {
		return "/apedagogica/ConsultaParticipanteAP";
	}
	
	/**
	 * Define o formulário de busca do participante.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/busca_participante.jsp";
	}
	
	/**
	 * Define a tela de impressão d a lista dos participantes.
	 * Método não invocado por JSP's.
	 */
	public String getPrintPage() {
		return getDirBase() + "/lista_participante_impressao.jsp";
	}
	
	/**
	 * Redireciona para a operação especificada
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):busca 
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/ConsultaParticipanteAP/busca_participante.jsp</li>
 	 * </ul>
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	private String redirecionarParticipante(ParticipanteAtividadeAtualizacaoPedagogica participante) throws ArqException {
		operacao = OperacaoParticipanteAtividadeAP.getOperacao(codigoOperacao);
		OperadorParticipanteAtividadeAP mBean = (OperadorParticipanteAtividadeAP) getMBean(operacao.getMBean());
		mBean.setParticipante(participante);
		return mBean.selecionaParticipante();
	}
	
	/**
	 * Realiza a busca dos dados do participante selecionado
	 * e redireciona para o MBean.
	 * <br/>
 	 * Este método não é chamado em JSPs.
	 * 
	 * @throws ArqException
	 */
	public void escolheParticipante(ActionEvent e) throws ArqException {
		Integer id = (Integer) e.getComponent().getAttributes().get("idParticipante");
		ParticipanteAtividadeAtualizacaoPedagogica  participante = null;
		participante = getGenericDAO().findByPrimaryKey(id,ParticipanteAtividadeAtualizacaoPedagogica.class);
		if (participante == null) 
			addMensagemErro("Participante não encontrado!");
		redirecionarParticipante(participante);
	}
	
	/**
	 * Popula o resultado da busca.
	 * Método não invocad por JSP's.
	 */
	private void populaResultado() throws SegurancaException, DAOException{
		
		checkChangeRole();
		validaFormBusca();
		
		Integer[] status = null;
		
		if( !isEmpty(getOperacao().getStatusValidos()))
			status = getOperacao().getStatusValidos();
		
		if( !hasErrors() ){
			ConsultaParticipanteAtividadeDAO consultaDAO = getDAO(ConsultaParticipanteAtividadeDAO.class);
			setResultadosBusca(consultaDAO.findGeral(getFiltroDocente(), null,getFiltroGrupo(), getFiltroAtividade(), status ));
			if( isEmpty(getResultadosBusca()) )
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			else{
				obj = new ParticipanteAtividadeAtualizacaoPedagogica();
			}	
		}
	}
	
	/**
	 * Carrega as atividades no filtro de busca.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarAtividadesGrupo(ActionEvent e) throws DAOException{
		
		setFiltroGrupo((Integer) e.getComponent().getAttributes().get("idGrupo"));
		setFiltroAtividade(0);
				
		if( !isEmpty(getNomeGrupo()) && !isEmpty(getFiltroGrupo()) ){
			GrupoAtividadesAtualizacaoPedagogica grupo = getGenericDAO().
				findByPrimaryKey(filtroGrupo,GrupoAtividadesAtualizacaoPedagogica.class);
			for (AtividadeAtualizacaoPedagogica a : grupo.getAtividades()) {
				SelectItem item = new  SelectItem(a.getId(), a.getNome() + (!isEmpty(a.getCh())?" - CH " + a.getCh() + "h":""));
				if( a.isAtivo() )
					atividadesGrupoCombo.add( item );
			}	
		}else
			atividadesGrupoCombo = new ArrayList<SelectItem>();
			
	}
	
	/**
	 * Realiza busca conforme os filtros selecionados.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/form.jsp</li>
 	 * </ul>
	 * @throws SegurancaException 
	 */
	public String buscar() throws DAOException, SegurancaException {
	
		setResultadosBusca(null);
		populaResultado();
		
		if(hasErrors()){
			setResultadosBusca(null);
			return null;
		}
		
		return forward(getFormPage());
		
	}
	
	/**
	 * Retorna o resultado da consulta para impressão.
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):busca 
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/ConsultaParticipanteAP/busca_participante.jsp</li>
 	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String imprimir() throws SegurancaException, DAOException{
		
		setResultadosBusca(null);
		setAll(null);
		populaResultado();
		clearFiltros();	
		
		if(hasErrors())
			return null;
		
		return forward(getPrintPage());
	}
	
	
	/**
	 * Método utilizado no componente autocomlpete para localizar o 
	 * grupo de atividade digitado no formulário de busca.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoAtividadesAtualizacaoPedagogica> autocompleteGrupoAtividade(Object e)
		throws DAOException{
		nomeGrupo = e.toString();
		setFiltroGrupo(null);
		setFiltroAtividade(0);
		return getDAO(GrupoAtividadesAtualizacaoPedagogicaDAO.class).findByDenominacao(nomeGrupo);
	}
	
	/**
	 * Retorna uma coleção das situações dos participantes para ser populado no filtro da busca.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getStatusCombo(){
		return  toSelectItems(StatusParticipantesAtualizacaoPedagogica.getDescricaostatus());
	}

	public String getFiltroDocente() {
		return filtroDocente;
	}

	public void setFiltroDocente(String filtroDocente) {
		this.filtroDocente = filtroDocente;
	}

	public Integer getFiltroAtividade() {
		return filtroAtividade;
	}

	public void setFiltroAtividade(Integer filtroAtividade) {
		this.filtroAtividade = filtroAtividade;
	}

	/**
	 * Retorna o valor do filtro do grupo selecionado, caso seja nulo
	 * inicializa a coleção de atividades.
	 * @return
	 */
	public Integer getFiltroGrupo() {
		if( isEmpty(this.nomeGrupo) )
			setFiltroGrupo(null);
		if( isEmpty(this.filtroGrupo) )
			atividadesGrupoCombo = new ArrayList<SelectItem>();
		return filtroGrupo;
	}

	/**
	 * Define o grupo no filtro da busca.
	 * @param filtroGrupo
	 */
	public void setFiltroGrupo(Integer filtroGrupo) {
		if( !isEmpty(this.filtroGrupo) ){
			this.filtroAtividade = null;
		}	
		this.filtroGrupo = filtroGrupo;
	}

	public Integer getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(
			Integer filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}
	
	public OperacaoParticipanteAtividadeAP getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoParticipanteAtividadeAP operacao) {
		this.operacao = operacao;
	}

	public int getCodigoOperacao() {
		return codigoOperacao;
	}
	
	/**
	 * Retorna o nome digitado no filtro do grupo (autocomplete), caso
	 * seja nulo a coleção de atividades é inicializada.
	 * @return
	 */
	public String getNomeGrupo() {
		if( isEmpty(this.filtroGrupo) )
			atividadesGrupoCombo = new ArrayList<SelectItem>();
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	/**
	 * Define o código da operação e seta a operação.
	 * @param codigoOperacaosize="65" 
	 */
	public void setCodigoOperacao(int codigoOperacao) {
		if( codigoOperacao > 0)
			operacao = OperacaoParticipanteAtividadeAP.getOperacao(codigoOperacao);
		this.codigoOperacao = codigoOperacao;
	}
	
	public void setAtividadesGrupoCombo(Collection<SelectItem> atividadesGrupoCombo) {
		this.atividadesGrupoCombo = atividadesGrupoCombo;
	}
	
	/**
	 * Retorna coleção de atividades de acordo com o grupo selecionado
	 * para ser populado no filtro da busca.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAtividadesGrupoCombo() throws DAOException {
		if( isEmpty(atividadesGrupoCombo) )
			atividadesGrupoCombo = new ArrayList<SelectItem>();
		
		return atividadesGrupoCombo;
	}

	/**
	 * Valida se algum filtro foi selecionado.
	 * Método não invocado por JSP's.
	 * @return
	 */
	private void validaFormBusca(){
		
		if ( isEmpty(getFiltroDocente()) && (isEmpty(getFiltroGrupo()) || isEmpty(getNomeGrupo())) && isEmpty(getFiltroAtividade())  )
			addMensagem(MensagensAP.PREENCHA_PELO_MENOS_UM_CAMPO);
		else if( !isEmpty(getOperacao().getMapaCamposObrigatorios().get("filtroDocente"))  && isEmpty(getFiltroDocente()) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Participante");
		else{
			if ( !isEmpty(getOperacao().getMapaCamposObrigatorios().get("filtroGrupo"))  && isEmpty(getFiltroGrupo()) && !isEmpty(getNomeGrupo()) )
				addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Grupo de Atividade");
			if ( (!isEmpty(getNomeGrupo()) || !isEmpty(getOperacao().getMapaCamposObrigatorios().get("filtroAtividade"))) && isEmpty(getFiltroAtividade())  )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
		}	

	}
	
}
