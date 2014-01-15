/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável por emitir o relatório de tempo médio de titulação dos alunos de pós-graduação.
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
	/** Data de início informado para geração do relatório */
	private Date dataInicio;
	/** Data de Fim informado para geração do relatório */
	private Date dataFim;
	
	/** Lista com o resultado do relatório */
	private List<Map<String,Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Enum com os tipos de relatórios possíveis */
	private enum TipoRelatorioTitulacaoDefesa {
		/** Enum referente ao relatório de Tempo Médio de defesas por Discente */
		TEMPO_MEDIO_DISCENTES("Relatório de Tempo Médio de Titulação por Discente"),
		/** Enum referente ao relatório de Tempo Médio de defesas por Orientador */
		TEMPO_MEDIO_ORIENTADORES("Relatório de Tempo Médio de Titulação por Orientador");
		/** Label que exibe o título do relatório */
		private String label;
		
		private TipoRelatorioTitulacaoDefesa(String label){
			this.label = label;
		}

		public String getLabel() {
			return label;
		}	
	}

	/** Indica qual relatório será gerado */
	private TipoRelatorioTitulacaoDefesa operacao;
	
	/**
	 * Retorna o nome do relatório que está sendo gerado.
	 * Método chamado pela seguinte JSP:
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
	 * Inicia o formulário para emissão do relatório de Tempo médio 
	 * de titulação de discente. 
	 * Método chamado pela seguinte JSP:
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
	 * Inicia o formulário para emissão do relatório de Tempo médio 
	 * de titulação por orientador. 
	 * Método chamado pela seguinte JSP:
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
	 * Inicia o formulário para informar os dados para emissão do relatório
	 * @return
	 */
	private String iniciar(){
		clear();
		return forward(getFormPage());		
	}

	/**
	 * Gera o Relatório
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/relatorios/tempo_medio_titulacao/form.jsf</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		ListaMensagens erros = new ListaMensagens(); 
		
		if (dataInicio == null)
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Início");
		
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
	 * Gerar o relatório de tempo médio de titulação de defesas por discentes
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
	 * Gerar o relatório de tempo médio de titulação de defesas por Orientador
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
	 * Caminho do Formulário
	 */
	@Override
	public String getFormPage() {
		return "/stricto/relatorios/tempo_medio_titulacao/form.jsf";
	}
	
	/**
	 * Caminho do Relatório por discente
	 */
	private String getTitulacaoPorDiscente() {
		return "/stricto/relatorios/tempo_medio_titulacao/titulacao_por_discente.jsf";
	}

	/**
	 * Caminho do Relatório por Orientador
	 */
	private String getTitulacaoPorOrientador() {
		return "/stricto/relatorios/tempo_medio_titulacao/titulacao_por_orientador.jsf";
	}
	
	/**
	 * Verifica se a operação é do relatório de titulação por discente
	 * @return
	 */
	private boolean isTitulacaoPorDiscente(){
		return (operacao != null && operacao.equals(TipoRelatorioTitulacaoDefesa.TEMPO_MEDIO_DISCENTES));
	}
	
	/**
	 * Verifica se a operação é do relatório de titulação por orientador
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
