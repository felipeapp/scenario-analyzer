/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Controlador responsável por buscar e exibir resumos de convocações de candidatos realizadas.
 * 
 * @author Leonardo Campos
 *
 */
@Component("resumoConvocacaoVestibularMBean") @Scope("request")
public class ResumoConvocacaoVestibularMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivo> {

	/** Constante com o endereço da view do formulário de busca do resumo. */
	private final String JSP_FORM = "/graduacao/convocacao_vestibular/form_resumo.jsp";
	/** Constante com o endereço da view do resumo. */
	private final String JSP_RESUMO = "/graduacao/convocacao_vestibular/view_resumo.jsp";
	
	/** ID do processo seletivo do relatório. */
	private int idProcessoSeletivo;

	/** Indica a fase que está da chamada */
	private int idChamada;
	
	/** Armazena uma coleção de possíveis de chamadas para escolha do usuário. */
	private Collection<SelectItem> chamadasCombo = new ArrayList<SelectItem>(0);
	
	/** Lista de convocações de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscente> convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>(0);
	
	/** Lista de cancelamentos de convocações anteriores gerados por reconvocações de candidatos. */
	private List<CancelamentoConvocacao> cancelamentos = new ArrayList<CancelamentoConvocacao>(0);
	
	/** Comparador utilizado para ordenar as convocações de discentes em um processo seletivo. */
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
	
	/** Construtor padrão. */
	public ResumoConvocacaoVestibularMBean() {
		clear();
	}

	/**
	 * Inicializa os valores dos campos utilizados na operação.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivo();
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>(0);
		cancelamentos = new ArrayList<CancelamentoConvocacao>(0);
		chamadasCombo = new ArrayList<SelectItem>(0);
	}

	/**
	 * Encaminha para a tela do formulário de busca de convocações.
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/convocacao_vestibular/view_resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaFormulario() {
		return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo de convocações.
	 * <br/><br/>Não é chamado por JSP.
	 * @return
	 */
	public String telaResumo() {
		return forward(JSP_RESUMO);
	}
	
	/**
	 * Realiza a checagem de permissões e preenche as informações iniciais
	 * necessárias para a realização do caso de uso.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Busca as informações do resumo da convocação de acordo com os dados
	 * informados e encaminha para a tela de resumo.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega as chamadas já realizadas do processo seletivo selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
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
