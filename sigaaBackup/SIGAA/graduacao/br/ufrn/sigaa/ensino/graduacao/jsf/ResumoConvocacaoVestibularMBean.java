/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/01/2012
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.ConvocacaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;

/**
 * Controlador respons�vel por buscar e exibir resumos de convoca��es de candidatos realizadas.
 * 
 * @author Leonardo Campos
 *
 */
@Component("resumoConvocacaoVestibularMBean") @Scope("request")
public class ResumoConvocacaoVestibularMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivo> {

	/** Constante com o endere�o da view do formul�rio de busca do resumo. */
	private final String JSP_FORM = "/graduacao/convocacao_vestibular/form_resumo.jsp";
	/** Constante com o endere�o da view do resumo. */
	private final String JSP_RESUMO = "/graduacao/convocacao_vestibular/view_resumo.jsp";
	
	/** ID do processo seletivo do relat�rio. */
	private int idProcessoSeletivo;

	/** Indica a fase que est� da chamada */
	private int idChamada;
	
	/** Armazena uma cole��o de poss�veis de chamadas para escolha do usu�rio. */
	private Collection<SelectItem> chamadasCombo = new ArrayList<SelectItem>(0);
	
	/** Lista de convoca��es de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscente> convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>(0);
	
	/** Lista de cancelamentos de convoca��es anteriores gerados por reconvoca��es de candidatos. */
	private List<CancelamentoConvocacao> cancelamentos = new ArrayList<CancelamentoConvocacao>(0);
	
	/** Comparador utilizado para ordenar as convoca��es de discentes em um processo seletivo. */
	private Comparator<ConvocacaoProcessoSeletivoDiscente> convocacaoDiscenteComparator = new Comparator<ConvocacaoProcessoSeletivoDiscente>(){
		@Override
		public int compare(ConvocacaoProcessoSeletivoDiscente o1, ConvocacaoProcessoSeletivoDiscente o2) {
			MatrizCurricular matriz1 = o1.getDiscente().getMatrizCurricular();
			MatrizCurricular matriz2 = o2.getDiscente().getMatrizCurricular();
			int cmp = matriz1.getCurso().getMunicipio().getNome().compareTo(matriz2.getCurso().getMunicipio().getNome());
			if (cmp == 0)
				cmp = matriz1.getDescricao().compareTo(matriz2.getDescricao());
			if (cmp == 0)
				cmp = o1.getGrupoCotaConvocado() != null && o2.getGrupoCotaConvocado() == null ? -1 :
					o1.getGrupoCotaConvocado() == null && o2.getGrupoCotaConvocado() != null ? 1 : 0;
			if (cmp == 0 && o1.getGrupoCotaConvocado() != null && o2.getGrupoCotaConvocado() != null)
				cmp = o1.getGrupoCotaConvocado().getDescricao().compareTo(o2.getGrupoCotaConvocado().getDescricao());
			if (cmp == 0)
				cmp = o1.getTipo().compareTo(o2.getTipo());
			return cmp;
		}
	};
	
	/** Construtor padr�o. */
	public ResumoConvocacaoVestibularMBean() {
		clear();
	}

	/**
	 * Inicializa os valores dos campos utilizados na opera��o.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivo();
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>(0);
		cancelamentos = new ArrayList<CancelamentoConvocacao>(0);
		chamadasCombo = new ArrayList<SelectItem>(0);
	}

	/**
	 * Encaminha para a tela do formul�rio de busca de convoca��es.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/convocacao_vestibular/view_resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaFormulario() {
		return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo de convoca��es.
	 * <br/><br/>N�o � chamado por JSP.
	 * @return
	 */
	public String telaResumo() {
		return forward(JSP_RESUMO);
	}
	
	/**
	 * Realiza a checagem de permiss�es e preenche as informa��es iniciais
	 * necess�rias para a realiza��o do caso de uso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		setChamadasCombo( new ArrayList<SelectItem>(0) );
		return telaFormulario();
	}
	
	/**
	 * Busca as informa��es do resumo da convoca��o de acordo com os dados
	 * informados e encaminha para a tela de resumo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/convocacao_vestibular/
	 * form_resumo.jsp</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public String buscar() throws ArqException {
		validateRequiredId(getIdProcessoSeletivo(), "Processo Seletivo", erros);
		validateRequiredId(getIdChamada(), "Chamada", erros);
		if (hasOnlyErrors()) 
			return null;

		setConvocacoes(new ArrayList<ConvocacaoProcessoSeletivoDiscente>(0));
		setCancelamentos(new ArrayList<CancelamentoConvocacao>(0));
		
		ConvocacaoVestibularDao dao = getDAO(ConvocacaoVestibularDao.class);
		setObj(dao.findByPrimaryKey(getIdChamada(), ConvocacaoProcessoSeletivo.class));
		Object[] result = dao.findResumoConvocacao(getIdChamada());
		setConvocacoes((List<ConvocacaoProcessoSeletivoDiscente>) result[0]);
		setCancelamentos((List<CancelamentoConvocacao>) result[1]);
		
		Collections.sort(convocacoes, convocacaoDiscenteComparator);
		
		return telaResumo();
	}
	
	/**
	 * Carrega as chamadas j� realizadas do processo seletivo selecionado.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/convocacao_vestibular/form_resumo.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregarChamadas() throws DAOException {
		ConvocacaoProcessoSeletivoDao convocacaoDao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		
		setChamadasCombo( new ArrayList<SelectItem>(0) );
		setIdChamada(0);
		if (getIdProcessoSeletivo() != 0) {
			Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo = convocacaoDao.findByProcessoSeletivo(getIdProcessoSeletivo());
			getChamadasCombo().addAll(toSelectItems(convocacoesProcessoSeletivo, "id", "descricaoCompleta"));
		}
	}

	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	public int getIdChamada() {
		return idChamada;
	}

	public void setIdChamada(int idChamada) {
		this.idChamada = idChamada;
	}

	public Collection<SelectItem> getChamadasCombo() {
		return chamadasCombo;
	}

	public void setChamadasCombo(Collection<SelectItem> chamadasCombo) {
		this.chamadasCombo = chamadasCombo;
	}

	public List<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(List<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}

	public List<CancelamentoConvocacao> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(List<CancelamentoConvocacao> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}
}
