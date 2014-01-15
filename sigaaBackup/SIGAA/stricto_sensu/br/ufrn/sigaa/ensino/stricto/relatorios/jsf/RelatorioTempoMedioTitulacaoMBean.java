/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 08/02/2011
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.relatorios.dao.RelatorioTempoMedioTitulacaoDao;

/**
 * MBean respons�vel por emitir o relat�rio de tempo m�dio de titula��o dos alunos de p�s-gradua��o.
 * 
 * @author arlindo
 *
 */
@Component @Scope("request")
public class RelatorioTempoMedioTitulacaoMBean extends SigaaAbstractController<DiscenteStricto> {
	
	/** Ano selecionado */
	private int ano;
	/** Unidade selecionada */
	private Unidade unidade = new Unidade();
	/** Data de in�cio informado para gera��o do relat�rio */
	private Date dataInicio;
	/** Data de Fim informado para gera��o do relat�rio */
	private Date dataFim;
	
	/** Lista com o resultado do relat�rio */
	private List<Map<String,Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Enum com os tipos de relat�rios poss�veis */
	private enum TipoRelatorioTitulacaoDefesa {
		/** Enum referente ao relat�rio de Tempo M�dio de defesas por Discente */
		TEMPO_MEDIO_DISCENTES("Relat�rio de Tempo M�dio de Titula��o por Discente"),
		/** Enum referente ao relat�rio de Tempo M�dio de defesas por Orientador */
		TEMPO_MEDIO_ORIENTADORES("Relat�rio de Tempo M�dio de Titula��o por Orientador");
		/** Label que exibe o t�tulo do relat�rio */
		private String label;
		
		private TipoRelatorioTitulacaoDefesa(String label){
			this.label = label;
		}

		public String getLabel() {
			return label;
		}	
	}

	/** Indica qual relat�rio ser� gerado */
	private TipoRelatorioTitulacaoDefesa operacao;
	
	/**
	 * Retorna o nome do relat�rio que est� sendo gerado.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/relatorios/tempo_medio_titulacao/form.jsf</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoTipoRelatorio(){
		return (operacao != null ? operacao.getLabel() : null);
	}	
	
	/** Inicializa os dados */
	private void clear(){
		ano = CalendarUtils.getAnoAtual();
		dataInicio = CalendarUtils.diminuiDataEmUmAno(new Date());
		dataFim = new Date();
		unidade.setId(-1);		
	}
	
	/**
	 * Inicia o formul�rio para emiss�o do relat�rio de Tempo m�dio 
	 * de titula��o de discente. 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 */	
	public String iniciarTempoMedioTitulacaoPorDiscente(){
		operacao = TipoRelatorioTitulacaoDefesa.TEMPO_MEDIO_DISCENTES;
		return iniciar();
	}
	
	/**
	 * Inicia o formul�rio para emiss�o do relat�rio de Tempo m�dio 
	 * de titula��o por orientador. 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 */		
	public String iniciarTempoMedioTitulacaoPorOrientador(){
		operacao = TipoRelatorioTitulacaoDefesa.TEMPO_MEDIO_ORIENTADORES;
		return iniciar();
	}
	
	/**
	 * Inicia o formul�rio para informar os dados para emiss�o do relat�rio
	 * @return
	 */
	private String iniciar(){
		clear();
		return forward(getFormPage());		
	}

	/**
	 * Gera o Relat�rio
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/relatorios/tempo_medio_titulacao/form.jsf</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		ListaMensagens erros = new ListaMensagens(); 
		
		if (dataInicio == null)
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de In�cio");
		
		if (dataFim == null)
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Fim");
		
		ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Data de Fim", erros);		
		
		if (isPortalCoordenadorStricto())
			unidade = getProgramaStricto();
		else
			unidade = getGenericDAO().refresh(unidade);
		
		if (isPortalPpg() && unidade.getId() < 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");

     	if (!erros.isEmpty()){
     		addMensagens(erros);
     		return null;
		}
		
		if (isTitulacaoPorDiscente())
			return gerarTempoMedioTitulacaoPorDiscente();
		else
		if (isTitulacaoPorOrientador())
			return gerarTempoMedioTitulacaoPorOrientador();
		
		return null;
	}
	
	/**
	 * Gerar o relat�rio de tempo m�dio de titula��o de defesas por discentes
	 * @return
	 * @throws DAOException
	 */
	private String gerarTempoMedioTitulacaoPorDiscente() throws DAOException{
		RelatorioTempoMedioTitulacaoDao dao = getDAO(RelatorioTempoMedioTitulacaoDao.class);
		try {
			listagem = dao.findDefesasTempoMedioTitulacao(unidade.getId(), dataInicio, dataFim);
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getTitulacaoPorDiscente());			
	}
	
	/**
	 * Gerar o relat�rio de tempo m�dio de titula��o de defesas por Orientador
	 * @return
	 * @throws DAOException
	 */
	private String gerarTempoMedioTitulacaoPorOrientador() throws DAOException{
		RelatorioTempoMedioTitulacaoDao dao = getDAO(RelatorioTempoMedioTitulacaoDao.class);
		try {
			listagem = dao.findDefesasTempoMedioTitulacaoOrientador(unidade.getId(), dataInicio, dataFim);
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getTitulacaoPorOrientador());			
	}	
	
	/**
	 * Caminho do Formul�rio
	 */
	@Override
	public String getFormPage() {
		return "/stricto/relatorios/tempo_medio_titulacao/form.jsf";
	}
	
	/**
	 * Caminho do Relat�rio por discente
	 */
	private String getTitulacaoPorDiscente() {
		return "/stricto/relatorios/tempo_medio_titulacao/titulacao_por_discente.jsf";
	}

	/**
	 * Caminho do Relat�rio por Orientador
	 */
	private String getTitulacaoPorOrientador() {
		return "/stricto/relatorios/tempo_medio_titulacao/titulacao_por_orientador.jsf";
	}
	
	/**
	 * Verifica se a opera��o � do relat�rio de titula��o por discente
	 * @return
	 */
	private boolean isTitulacaoPorDiscente(){
		return (operacao != null && operacao.equals(TipoRelatorioTitulacaoDefesa.TEMPO_MEDIO_DISCENTES));
	}
	
	/**
	 * Verifica se a opera��o � do relat�rio de titula��o por orientador
	 * @return
	 */
	private boolean isTitulacaoPorOrientador(){
		return (operacao != null && operacao.equals(TipoRelatorioTitulacaoDefesa.TEMPO_MEDIO_ORIENTADORES));
	}	

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public TipoRelatorioTitulacaoDefesa getOperacao() {
		return operacao;
	}

	public void setOperacao(TipoRelatorioTitulacaoDefesa operacao) {
		this.operacao = operacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
}
