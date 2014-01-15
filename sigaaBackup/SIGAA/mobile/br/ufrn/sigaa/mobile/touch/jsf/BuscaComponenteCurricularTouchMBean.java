package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.jsf.ProgramaComponenteCurricularMBean;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

/**
 * MBean responsável pela consulta de Componentes Curriculares no Portal Público do SIGAA Mobile. 
 * 
 * @author Bernardo Ferreira
 */
@Component("buscaComponenteCurricularTouch") @Scope("request")
public class BuscaComponenteCurricularTouchMBean extends SigaaTouchAbstractController<ComponenteCurricular> {
	
	/** Armazena os Componentes Curriculares encontrados */ 
	private Collection<ComponenteCurricular> componentes;
	
	/** Indica o nível dos Componentes Curriculares que serão considerados na busca */
	private char nivel;
	
	/** Indica o campo nome preenchido na busca pelo usuário */ 
	private String nomeBusca;
	
	/** Indica o código do componente curricular a ser considerado na busca */
	private String codigoBusca;
	
	/** Indica o tipo do componente curricular a ser considerado na busca */
	private TipoComponenteCurricular tipoComponenteBusca;
	
	/** Indica a unidade à qual pertence o componente curricular a ser considerado na busca */
	private Unidade unidadeBusca;
	
	public BuscaComponenteCurricularTouchMBean(){
		init();
	}
	
	/** Inicializa as variáveis necessárias */
	private void init() {
		obj = new ComponenteCurricular();
		
		tipoComponenteBusca = new TipoComponenteCurricular();
		unidadeBusca = new Unidade();
	}

	/** 
	 * Encaminha o usuário para a tela de seleção de nível dos componentes curriculares.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/principal.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/busca_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public String iniciarBusca() {
		return forward("/mobile/touch/public/nivel_componente_curricular.jsf");
	}
	
	/** 
	 * Seleciona o nível dos componentes curriculares.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/nivel_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public String selecionaNivelBusca() throws DAOException {
		nivel = getParameterChar("nivelComponente");
		
		return forwardBuscaComponentes();
	}
	
	/** 
	 * Realiza a busca pelos componentes curriculares.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/busca_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public String buscarComponentes() throws DAOException {
		DisciplinaDao disciplinaDao = getDAO(DisciplinaDao.class);
		
		try {
			String codigo = isEmpty(codigoBusca) ? null : codigoBusca;
			String nome = isEmpty(nomeBusca) ? null : nomeBusca;
			TipoComponenteCurricular tipo = isEmpty(tipoComponenteBusca) ? null : tipoComponenteBusca;
			Unidade unidade = isEmpty(unidadeBusca) ? null : unidadeBusca;
			
			if (isEmpty(codigo) && isEmpty(nome) && isEmpty(tipo) && isEmpty(unidade)) {
				addMensagemErro("Especifique um parâmetro para realizar a busca.");
				return null;
			}
			
			componentes = disciplinaDao.findCompleto(codigo, nome, null, null, null, null, null, null, tipo, unidade, nivel, false, true, true, null);
			
			//Ordena os processos seletivos na ordem dos abertos com a data de inscrição mais próxima 
			if(isEmpty(componentes)) {
				addMensagemErro("Nenhum Componente Curricular encontrado com os parâmetros utilizados.");
				return null;
			}
		} catch (LimiteResultadosException e) {
			addMensagemErro("A busca retornou um número muito grande de resultados. Favor refine sua busca.");
			return null;
		} finally {
			disciplinaDao.close();
		}

		return forwardListaComponentes();
	}
	
	/** 
	 * Entra na visualização dos detalhes do componente curricular.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/lista_componentes_curriculares.jsp</li>
	 * </ul>
	 * 
	 * */
	public String view() throws ArqException {
		setId();
		
		if (obj.getId() > 0) {
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),ComponenteCurricular.class);
		} else {
			addMensagemErro("Componente Curricular não selecionado");
			return null;
		}
		
		return forward("/mobile/touch/public/view_componente_curricular.jsf");
	}
	
	/** 
	 * Entra na visualização do programa do componente curricular visualizado.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public String abrirPrograma() throws DAOException {
		ProgramaComponenteCurricularMBean bean = getMBean("programaComponente");
		
		String retorno = bean.gerarRelatorioPrograma();
		
		if (getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) != null){
			addMensagens((ListaMensagens) getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION));
			getCurrentSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		}
		
		return retorno;
	}
	
	/** 
	 * Encaminha o usuário à tela de busca dos componentes curriculares de determinado nível.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/lista_componentes_curriculares.jsp</li>
	 * </ul>
	 * 
	 * */
	public String forwardBuscaComponentes() {
		return forward ("/mobile/touch/public/busca_componente_curricular.jsf");
	}
	
	/** 
	 * Encaminha o usuário à tela de busca de listagem dos componentes curriculares encontrados.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/lista_componentes_curriculares.jsp</li>
	 * </ul>
	 * 
	 * */
	public String forwardListaComponentes() {
		return forward("/mobile/touch/public/lista_componentes_curriculares.jsf");
	}
	
	/** 
	 * Indica se deve exibir a carga horária total do componente curricular.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeCargaHorariaTotal() {
		return !obj.isBloco();
	}
	
	/** 
	 * Indica se deve exibir a carga horária dedicada do docente.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeChDedicadaDocente() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividadeComplementar() || (obj.isModulo())||obj.isAtividadeColetiva() ||
				(obj.getFormaParticipacao() != null && obj.getFormaParticipacao().isPermiteCHDocente());
	}
	
	/** 
	 * Indica se deve exibir a carga horária teórica.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeChTeorico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividade() || obj.isBloco() || obj.isModulo() || obj.isAtividadeColetiva();
	}
	
	/** 
	 * Indica se deve exibir a carga horária prática.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeChPratico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividade() || obj.isBloco() || obj.isModulo() || obj.isAtividadeColetiva();
	}
	
	/** 
	 * Indica se deve exibir a carga horária EAD.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeChEad() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isModulo() || (!isEmpty(obj.getFormaParticipacao()) && obj.getFormaParticipacao().isEspecialColetiva());
	}
	
	/** 
	 * Indica se deve exibir a quantidade de créditos EAD.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeCrEad() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isGraduacao();
	}	
	
	/** 
	 * Indica se deve exibir a carga horária de estágio.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeCrEstagio(){
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.getId() > 0 && obj.getDetalhes().getCrEstagio() > 0; 
	}
	
	/** 
	 * Indica se deve exibir a quantidade de créditos práticos.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeCrPratico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isDisciplina();
	}
	
	/** 
	 * Indica se deve exibir a quantidade de créditos teóricos.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * */
	public boolean isExibeCrTeorico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isDisciplina();
	}

	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}
	
	public String getDescricaoNivel() {
		return NivelEnsino.getDescricao(nivel);
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public String getCodigoBusca() {
		return codigoBusca;
	}

	public void setCodigoBusca(String codigoBusca) {
		this.codigoBusca = codigoBusca;
	}

	public TipoComponenteCurricular getTipoComponenteBusca() {
		return tipoComponenteBusca;
	}

	public void setTipoComponenteBusca(TipoComponenteCurricular tipoComponenteBusca) {
		this.tipoComponenteBusca = tipoComponenteBusca;
	}

	public Unidade getUnidadeBusca() {
		return unidadeBusca;
	}

	public void setUnidadeBusca(Unidade unidadeBusca) {
		this.unidadeBusca = unidadeBusca;
	}

}
