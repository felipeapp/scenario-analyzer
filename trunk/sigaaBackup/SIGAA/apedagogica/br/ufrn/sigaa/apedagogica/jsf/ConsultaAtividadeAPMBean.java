package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import br.ufrn.sigaa.apedagogica.dao.AtividadeAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dao.GrupoAtividadesAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controller que gerencia as consultas das atividades.
 * @author M�rio Rizzi
 *
 */
@Component("consultaAtividadeAP") @Scope("session")
public class ConsultaAtividadeAPMBean 
		extends SigaaAbstractController<AtividadeAtualizacaoPedagogica>{
	
	/** Utilizado no campo autocomplete dos grupos na consulta. */
	private Integer filtroGrupo;
	
	/** Utilizado no campo autocomlpete dos grupos na consulta. */
	private String nomeGrupo;

	/** Nome do Managed Bean respons�vel pela opera��o */
	private OperacaoAtividadeAP operacao;
	
	/** C�digo da opera��o a ser realizada (Deve ser passado como par�metro ou definido no Bean) */
	private int codigoOperacao;

	
	public ConsultaAtividadeAPMBean(){
		
		obj = new AtividadeAtualizacaoPedagogica();
		all = null;
		nomeGrupo = null;
		setResultadosBusca(null);
		clearFiltros();
		
	}
	
	/**
	 * Limpa os filtros da busca.
	 * M�todo n�o invocado por JSP's. 
	 */
	private void clearFiltros(){
		
		filtroGrupo = null;
		nomeGrupo = null;		
	}
	
	/**
	 * Define os pap�is que podem acessar os m�todos da classe.
	 * M�todo n�o invocado por JSP's. 
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#checkChangeRole() 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.GESTOR_PAP);
		
	}

	/**
	 * Define o diret�rio base para as opera��es de CRUD.
	 * M�todo n�o invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getDirBase() 
	 */
	@Override
	public String getDirBase() {
		return "/apedagogica/ConsultaAtividadeAP";
	}
	
	/**
	 * Define o formul�rio de busca do participante.
	 * M�todo n�o invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/busca_atividade.jsp";
	}
	
	/**
	 * Redireciona para a opera��o especificada
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):busca 
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/ConsultaParticipanteAP/busca_participante.jsp</li>
 	 * </ul>
	 * @param discente
	 * @return
	 * @throws ArqException
	 */
	private String redirecionarParticipante(AtividadeAtualizacaoPedagogica participante) throws ArqException {
		operacao = OperacaoAtividadeAP.getOperacao(codigoOperacao);
		OperadorAtividadeAP mBean = (OperadorAtividadeAP) getMBean(operacao.getMBean());
		mBean.setAtividade(participante);
		return mBean.selecionaAtividade();
	}
	
	/**
	 * Realiza a busca dos dados do participante selecionado
	 * e redireciona para o MBean.
	 * <br/>
 	 * Este m�todo n�o � chamado em JSPs.
	 * 
	 * @throws ArqException
	 */
	public void escolheAtividade(ActionEvent e) throws ArqException {
		Integer id = (Integer) e.getComponent().getAttributes().get("idAtividade");
		AtividadeAtualizacaoPedagogica  atividade = null;
		atividade = getGenericDAO().findByPrimaryKey(id,AtividadeAtualizacaoPedagogica.class);
		if (atividade == null) 
			addMensagemErro("Participante n�o encontrado!");
		redirecionarParticipante(atividade);
	}
	
	/**
	 * Popula o resultado da busca.
	 * M�todo n�o invocad por JSP's.
	 */
	private void populaResultado() throws SegurancaException, DAOException{
		checkChangeRole();
		validaFormBusca();
		
		if( !hasErrors() ){
			AtividadeAtualizacaoPedagogicaDAO consultaDAO = getDAO(AtividadeAtualizacaoPedagogicaDAO.class);
			setResultadosBusca(consultaDAO.findAtividadesByGrupo(getFiltroGrupo()));
			if( isEmpty(getResultadosBusca()) )
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}else
			setResultadosBusca(null);
		setFiltroGrupo(null);
	}
	
	/**
	 * Realiza busca conforme os filtros selecionados.
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/form.jsp</li>
 	 * </ul>
	 * @throws SegurancaException 
	 */
	public String buscar() throws DAOException, SegurancaException {
	
		populaResultado();
		
		if(hasErrors())
			return null;
		
		return forward(getFormPage());
		
	}
	
	/**
	 * M�todo utilizado no componente autocomlpete para localizar o 
	 * grupo de atividade digitado no formul�rio de busca.
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		return getDAO(GrupoAtividadesAtualizacaoPedagogicaDAO.class).findByDenominacao(nomeGrupo);
	}
	
	/**
	 * M�todo utilizado no componente autocomlpete para localizar a 
	 *  atividade digitada no formul�rio de busca.
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeAtualizacaoPedagogica> autocompleteAtividade(Object e)
		throws DAOException{
		String atividade = e.toString();
		return getDAO(AtividadeAtualizacaoPedagogicaDAO.class).findByGrupoAtividade(getFiltroGrupo(),atividade);
	}
	
	
	
	/**
	 * Retorna uma cole��o das situa��es dos participantes para ser populado no filtro da busca.
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getStatusCombo(){
		return  toSelectItems(StatusParticipantesAtualizacaoPedagogica.getDescricaostatus());
	}
	
	public OperacaoAtividadeAP getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoAtividadeAP operacao) {
		this.operacao = operacao;
	}

	public int getCodigoOperacao() {
		return codigoOperacao;
	}
	
	public String getNomeGrupo() {
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	/**
	 * Define o c�digo da opera��o e seta a opera��o.
	 * @param codigoOperacaosize="65" 
	 */
	public void setCodigoOperacao(int codigoOperacao) {
		if( codigoOperacao > 0)
			operacao = OperacaoAtividadeAP.getOperacao(codigoOperacao);
		this.codigoOperacao = codigoOperacao;
	}

	/**
	 * Valida se algum filtro foi selecionado.
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	private void validaFormBusca(){
		
		if( isEmpty(getFiltroGrupo()) )
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Grupo Atividade");

	}

	public Integer getFiltroGrupo() {
		return filtroGrupo;
	}

	public void setFiltroGrupo(Integer filtroGrupo) {
		this.filtroGrupo = filtroGrupo;
	}
	
}
