/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.reuni.jsf;

import static br.ufrn.arq.util.UFRNUtils.toList;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.AreaConhecimentoCienciasTecnologia;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FormaAtuacaoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.IndicacaoBolsistaReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * ManagedBean utilizado para o cadastro de planos de trabalho
 * de bolsas REUNI de assistência ao ensino.
 * 
 * @author wendell
 *
 */
@Component("planoTrabalhoReuniBean") @Scope("request")
public class PlanoTrabalhoReuniMBean extends SigaaAbstractController<PlanoTrabalhoReuni> {

	/** Indica se é alteração ou não */
	private boolean alteracao = false;
    /** Atributo para auxílio do cadastro de Plano de trabalho que indica o Curso Selecionado */
	private Curso curso;
	/** Atributo para auxílio do cadastro de Plano de trabalho que indica o Docente Selecionado */
	private Servidor docente;
	/** Combo de Componentes Curriculares Prioritários */
	private Collection<SelectItem> componentesPrioritariosCombo;
	/** Componente Curricular Selecionado */
	private ComponenteCurricular outroComponenteCurricular;
	/** Coleção de Formas de atuação, para auxílio no cadastro */
	private Collection<FormaAtuacaoDocenciaAssistida> formasAtuacao;
	/** Indica se será informada outra forma de atuação no cadastro */
	private boolean opcaoOutrasFormasAtuacao;
	/** Indicação de Bolsista */
	private IndicacaoBolsistaReuni indicacaoBolsista;
	/** Indica se o objeto passou pelo clear ou não */
	private boolean limpo = false;
	
	/** Construtor da classe */
	public PlanoTrabalhoReuniMBean() {
		clear();
	}
	
	/**
	 * Limpa campos do MBean para novos cadastros
	 */
	private void clear() {
		obj = new PlanoTrabalhoReuni();
		instanciarAtributosPlano();
		
		curso = new Curso();
		docente = new Servidor();
		outroComponenteCurricular = new ComponenteCurricular();
		formasAtuacao = null;
		limpo = true;
	}
	
	/**
	 * Verifica se o objeto foi limpo, para um novo cadastro.
	 */
	public String getClear() {
		// Verifica se o objeto foi limpo antes do cadastro, para casos que o usuário utilizar o botão voltar do browser.
		if ( !alteracao && !limpo ){
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirect(getSubSistema().getLink());
		}
		return null;
	}
	
	/**
	 * Inicia o cadastro de um plano de trabalho
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 */
	public String iniciarCadastro() throws DAOException {
		clear();
		obj.setSolicitacao( getSolicitacaoBolsasReuniMBean().getObj() );
		setOperacaoAtiva(SigaaListaComando.SALVAR_SOLICITACAO_BOLSAS_REUNI.getId());
		return forward(getFormPage());
	}
	
	/**
	 * Inicia a alteração de um plano de trabalho
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarAlteracao() throws DAOException {
		if (!isEmpty(obj)) {
			obj = getGenericDAO().refresh(obj);
			obj.getDocentes().size();
			obj.getCursos().size();
			instanciarAtributosPlano();
		}

		alteracao = true;
		setConfirmButton("Confirmar alteração");
		setOperacaoAtiva(SigaaListaComando.SALVAR_SOLICITACAO_BOLSAS_REUNI.getId());
		return forward(getFormPage());
	}
	
	/**
	 * Instância os atributos nulos de um plano de trabalho necessários para o cadastro
	 * 
	 * @param obj
	 */
	private void instanciarAtributosPlano() {
		if (isEmpty(obj.getAreaConhecimento())) {
			obj.setAreaConhecimento(new AreaConhecimentoCienciasTecnologia());
		}
		if (isEmpty(obj.getComponenteCurricular())) {
			obj.setComponenteCurricular(new ComponenteCurricular());
		}
		if (isEmpty(obj.getFormasAtuacao())) {
			obj.setFormasAtuacao(new ArrayList<FormaAtuacaoDocenciaAssistida>());
		} 
		if (!isEmpty(obj.getOutrasFormasAtuacao())) {
			opcaoOutrasFormasAtuacao = true;
		}
		if ( !isEmpty(obj.getJustificativaComponenteCurricular()) ) {
			setOutroComponenteCurricular(obj.getComponenteCurricular());
			obj.setComponenteCurricular(new ComponenteCurricular(-1));
		}
	}


	/**
	 * Altera a linha de ação de plano de trabalho, refletindo em mudanças no formulário
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void alterarLinhaAcao( ValueChangeEvent e ) {
		Integer linhaAcao = (Integer) e.getNewValue();
		if (linhaAcao != null) {
			obj.setLinhaAcao(linhaAcao);
		}
	}
	
	/**
	 * Altera o componente curricular prioritário selecionado, refletindo em mudanças no formulário<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException 
	 */
	public void alterarComponentePrioritario(ValueChangeEvent e ) throws DAOException {
		Integer valor = (Integer) e.getNewValue();
		obj.getComponenteCurricular().setId( valor );

		if ( valor > 0 ) {
			obj.setComponenteCurricular( getGenericDAO().refresh(obj.getComponenteCurricular()) );
		}
		
	}
	
	/**
	 * Selecionar um componente que não está na lista de componentes prioritários<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarOutroComponente(ActionEvent e) throws DAOException {
		ComponenteCurricular componente = (ComponenteCurricular) e.getComponent().getAttributes().get("componente");
		if (!isEmpty(componente)) {
			outroComponenteCurricular = getGenericDAO().refresh(componente);
		}
	}	
	
	/**
	 * Adiciona um curso ao plano de trabalho<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void adicionarCurso(ActionEvent e) throws DAOException {
		if ( !isEmpty(curso) ) {
			obj.adicionarCurso( getGenericDAO().refresh(curso) );
			curso = new Curso();
		}
	}

	/**
	 * Remove um curso do plano de trabalho<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void removerCurso(ActionEvent e) throws DAOException {
		curso = (Curso) e.getComponent().getAttributes().get("curso");
		if ( !isEmpty(curso) ) {
			obj.removerCurso(  getGenericDAO().refresh(curso) );
			curso = new Curso();
		}
	}
	
	/**
	 * Adiciona um docente ao plano de trabalho<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void adicionarDocente(ActionEvent e) throws DAOException {
		docente = (Servidor) e.getComponent().getAttributes().get("docente");
		if (!isEmpty(docente)) {
			obj.adicionarDocente( getGenericDAO().refresh(docente) );
			docente = new Servidor();
		}
	}
	
	/**
	 * Remove um docente do plano de trabalho<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void removerDocente(ActionEvent e) throws DAOException {
		docente = (Servidor) e.getComponent().getAttributes().get("docente");
		if ( !isEmpty(docente) ) {
			obj.removerDocente( getGenericDAO().refresh(docente) );
			docente = new Servidor();
		}
	}
	
	/**
	 * Cadastra o Plano de Trabalho.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		if ( isOperacaoAtiva(SigaaListaComando.SALVAR_SOLICITACAO_BOLSAS_REUNI.getId()) || isOperacaoAtiva(SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI.getId())){
		
			obj.setFormasAtuacao( getFormasAtuacaoSelecionadas() );
			
			ListaMensagens erros = obj.validate();
			if (!isEmpty(obj.getLinhaAcao()) && obj.getLinhaAcao() == PlanoTrabalhoReuni.LINHA_ACAO_1) {
				validarComponenteCurricular(erros);
			}
			
			if ( !erros.isEmpty() ) {
				addMensagens(erros);
				return null;
			} else {
				if (!isOpcaoOutrasFormasAtuacao()) {
					obj.setOutrasFormasAtuacao(null);
				}
				
				if ( !isEmpty(obj.getAreaConhecimento()) ) {
					obj.setAreaConhecimento(getGenericDAO().refresh(obj.getAreaConhecimento()));
				}
				
			}
			
			setOperacaoAtiva(null);
			limpo = false;
			
			if ( alteracao ) {
				return getSolicitacaoBolsasReuniMBean().alterarPlanoTrabalho(obj);
			} else {
				return getSolicitacaoBolsasReuniMBean().adicionarPlanoTrabalho(obj);
			}
		}else {
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirect(getSubSistema().getLink());
		}
	}
	
	/**
	 * Muda o status do Plano de Trabalho - (Apenas para PPG)<br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/view_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String aprovarReprovar() throws ArqException, NegocioException{
		obj.setStatus((obj.getStatus() == PlanoTrabalhoReuni.APROVADO ? PlanoTrabalhoReuni.SUBMETIDO : PlanoTrabalhoReuni.APROVADO));	
		// Prepara o movimento, setando o objeto
		prepareMovimento(ArqListaComando.ALTERAR);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);		
		try {			
			// Seta a operação como alterar
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			// Tenta executar a operação
			executeWithoutClosingSession(mov, getCurrentRequest());			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}				
		return forward(getViewPageSolicitacao());		
	}	
	
	/**
	 * Valida o Componente Curricular
	 * @param erros
	 */
	private void validarComponenteCurricular(ListaMensagens erros) {
		if ( obj.getComponenteCurricular().getId() == -1  ) {
			ValidatorUtil.validateRequired(outroComponenteCurricular, "Outro componente curricular", erros);
			ValidatorUtil.validateRequired(obj.getJustificativaComponenteCurricular(), "Justificativa de seleção de outro componente curricular", erros);

			if (erros.isEmpty()) {
				obj.setComponenteCurricular(outroComponenteCurricular);
			}
		} else {
			ValidatorUtil.validateRequired(obj.getComponenteCurricular(), "Componente curricular", erros);
			obj.setJustificativaComponenteCurricular(null);
		}
	}

	/**
	 * Cancela a operação
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/view_solicitacao.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		clear();
		setOperacaoAtiva(null);
		return forward(getSolicitacaoBolsasReuniMBean().getFormPage());
	}
	
	@Override
	public String getFormPage() {
		return "/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp";
	}

	@Override
	public String getViewPage() {
		return "/stricto/solicitacao_bolsas_reuni/view_plano_trabalho.jsp";
	}
	
	/**
	 * Retorna a JSP de visualização da Solicitação
	 * @return
	 */
	private String getViewPageSolicitacao(){
		return getSolicitacaoBolsasReuniMBean().getViewPage();
	}

	
	/**
	 * Retorna as Formas de Atuação
	 * @return
	 * @throws DAOException
	 */
	public Collection<FormaAtuacaoDocenciaAssistida> getFormasAtuacao() throws DAOException {
		if (formasAtuacao == null) {
			formasAtuacao = getGenericDAO().findAllAtivos(FormaAtuacaoDocenciaAssistida.class, "descricao");
			for (FormaAtuacaoDocenciaAssistida formaAtuacao : formasAtuacao) {
				formaAtuacao.setSelecionada( obj.getFormasAtuacao().contains(formaAtuacao) );
			}
		}
		return formasAtuacao;
	}
	
	/**
	 * Retorna as Formas de Atuação Selecionadas
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FormaAtuacaoDocenciaAssistida> getFormasAtuacaoSelecionadas() throws DAOException {
		
		return CollectionUtils.select(getFormasAtuacao(), new Predicate() {
			public boolean evaluate(Object obj) {
				return ((FormaAtuacaoDocenciaAssistida) obj).isSelecionada();
			}
		});
	}

	/**
	 * Combo de Componentes Curricular Prioritários.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getComponentesPrioritariosCombo() throws DAOException {
		if (componentesPrioritariosCombo == null && obj.getEdital() != null) {
			Collection<ComponenteCurricular> componentesPrioritarios = getGenericDAO().refresh(obj.getEdital()).getComponentesPrioritarios();

			// Ordenar componentes
			componentesPrioritarios = toList(componentesPrioritarios);
			Collections.sort((List<ComponenteCurricular>) componentesPrioritarios, new Comparator<ComponenteCurricular>() {
				public int compare(ComponenteCurricular c1,	ComponenteCurricular c2) {
					return new CompareToBuilder()
						.append(c1.getCodigo(), c2.getCodigo())
						.append(c1.getNome(), c2.getNome())
						.toComparison();
				}
			});
			
			componentesPrioritariosCombo = toSelectItems(componentesPrioritarios, "id", "codigoNome");
		}
		return componentesPrioritariosCombo; 
	}
	
	/**
	 * Retorna o MBean de Solicitação de Bolsas Reuni.
	 * @return
	 */
	private  SolicitacaoBolsasReuniMBean getSolicitacaoBolsasReuniMBean() {
		return getMBean("solicitacaoBolsasReuniBean");
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public void setFormasAtuacao(Collection<FormaAtuacaoDocenciaAssistida> formasAtuacao) {
		this.formasAtuacao = formasAtuacao;
	}

	public void setComponentesPreferenciaisCombo(
			Collection<SelectItem> componentesPreferenciaisCombo) {
		this.componentesPrioritariosCombo = componentesPreferenciaisCombo;
	}

	public boolean isOpcaoOutrasFormasAtuacao() {
		return opcaoOutrasFormasAtuacao;
	}

	public void setOpcaoOutrasFormasAtuacao(boolean opcaoOutrasFormasAtuacao) {
		this.opcaoOutrasFormasAtuacao = opcaoOutrasFormasAtuacao;
	}

	public ComponenteCurricular getOutroComponenteCurricular() {
		return outroComponenteCurricular;
	}

	public void setOutroComponenteCurricular(
			ComponenteCurricular outroComponenteCurricular) {
		this.outroComponenteCurricular = outroComponenteCurricular;
	}
	
	/**
	 * Redireciona o usuário para a página de visualização dos dados da solicitação<br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/solicitacao_bolsas_reuni/_colunas_plano_trabalho.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		populateObj();
		
		// Carrega a indicação do plano de trabalho.
		IndicacaoBolsistaReuniDao indicacaoDao = getDAO(IndicacaoBolsistaReuniDao.class);
		try {
			indicacaoBolsista = indicacaoDao.findByPlanoTrabalho(obj);
		} finally {
			if (indicacaoDao != null)
				indicacaoDao.close();
		}
		
		return forward(getViewPage());
	}

	public IndicacaoBolsistaReuni getIndicacaoBolsista() {
		return indicacaoBolsista;
	}

	public void setIndicacaoBolsista(IndicacaoBolsistaReuni indicacaoBolsista) {
		this.indicacaoBolsista = indicacaoBolsista;
	}		
}
