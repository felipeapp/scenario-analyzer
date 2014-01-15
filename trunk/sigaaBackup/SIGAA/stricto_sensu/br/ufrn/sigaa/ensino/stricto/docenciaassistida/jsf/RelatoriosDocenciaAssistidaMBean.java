/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 31/05/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.AtividadesDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.RelatorioDiscentesAtendidosDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * Este MBean tem como finalidade de auxiliar na geração de relatórios referentes ao docência assistida.
 * 
 * @author Arlindo Rodrigues
 */
@Component("relatoriosDocenciaAssistidaMBean") @Scope("request")
public class RelatoriosDocenciaAssistidaMBean extends SigaaAbstractController<PlanoDocenciaAssistida> {
	
	/** Lista dos dados encontrados */
	private List<Map<String, Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Lista do detalhamento que será exibido no relatório */
	private List<Map<String, Object>> detalhe = new ArrayList<Map<String,Object>>();
	
	/** Unidade selecionada na busca */
	private Unidade unidade = new Unidade();
	
	/** Ano informado na busca */
	private Integer ano;
	
	/** Período informado na busca */
	private Integer periodo;
	
	/** Nível informado na busca */
	private Character nivel;
	
	/** Descrição do nível selecionado */
	private String descricaoNivel;
	
	/** Modalidade de Bolsa informado na busca */
	private ModalidadeBolsaExterna modalidadeBolsa = new ModalidadeBolsaExterna();	
	
	/** Tipo do relatório selecionado */
	private TipoRelatorioPlano tipoRelatorio;
	
	/**
	 * Enum que indica o tipo do relatório que será gerado
	 * @author arlindo
	 *
	 */
	enum TipoRelatorioPlano {
		
		/** Indica se será gerado o Relatório Quantitativo de Atividades */
		QUANTITATIVO_ATIVIDADE("Quantitativo de Planos de Docência Assistida por Atividade"),
		
		/** Indica se será gerado o Relatório de Alunos atendidos pela Docência Assistida */
		DISCENTES_ATENDIDOS("Quantitativo de Alunos Atendidos por Docência Assistida"),
		
		/** Indica se será gerado o Relatório de Alunos atendidos pela Docência Assistida */
		COMPONENTES_ATENDIDOS("Índices dos Componentes Atendidos por Docência Assistida"),
		
		/** Indica se será gerado o Relatório Quantitativo de planos de  Docência Assistida por status */
		QUANTITATIVO_POR_STATUS("Quantitativo de Planos de Docência Assistida por Status");		
		
		/** Nome do relatório */
		private String nome;
		
		/** Construtor padrão atribuindo o nome*/
		private TipoRelatorioPlano(String nome) {
			this.nome = nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}
		
		public String getNome() {
			return nome;
		}

		/** Retorna o nome do relatório selecionado */
		@Override
		public String toString() {
			return this.nome;
		}
	}
	
	/**
	 * Inicia o relatório de quantitativo de planos por atividade.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarQuantitativoAtividades(){		
		tipoRelatorio = TipoRelatorioPlano.QUANTITATIVO_ATIVIDADE;
		return iniciar(getFormPage());
	}
	
	/**
	 * Inicia o relatório de quantitativo de planos por atividade.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarAlunosAtendidos(){		
		tipoRelatorio = TipoRelatorioPlano.DISCENTES_ATENDIDOS;
		return iniciar(getFormPage());
	}	
	
	/**
	 * Inicia o relatório de quantitativo componentes atendidos por docência assistida e seus índices.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarComponentesAtendidos(){		
		tipoRelatorio = TipoRelatorioPlano.COMPONENTES_ATENDIDOS;
		return iniciar(getFormPage());
	}		
	
	/**
	 * Inicia o relatório selecionado
	 * @param form
	 * @return
	 */
	private String iniciar(String form){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();	
		return forward(form);
	}
	
	/**
	 * Inicia o relatório de quantitativo de planos por status.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarQuantitativoStatus(){		
		tipoRelatorio = TipoRelatorioPlano.QUANTITATIVO_POR_STATUS;
		return iniciar(getFormPage());
	}	
	

	/**
	 * Gera o relatório o relatório selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/plano_docencia_assistida/relatorios/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws HibernateException, DAOException{
		
		if (ano == null || periodo == null || ano < 1900 || periodo < 1 || periodo > 2){
			addMensagemErro("Informe Corretamente o Ano e Período.");
			return null;
		}
		
		String form = null; 
					
		if (tipoRelatorio.equals(TipoRelatorioPlano.QUANTITATIVO_ATIVIDADE))
			form = gerarQuantitativoAtividades();
		else if (tipoRelatorio.equals(TipoRelatorioPlano.DISCENTES_ATENDIDOS))
			form = gerarQuantitativoDiscentes();
		else if (tipoRelatorio.equals(TipoRelatorioPlano.COMPONENTES_ATENDIDOS))
			form = gerarQuantitativoComponentes();		
		else if (tipoRelatorio.equals(TipoRelatorioPlano.QUANTITATIVO_POR_STATUS))
			form = gerarQuantitativoPorStatus();				
		
		if (hasErrors() || form == null)
			return null;
		
		if (ValidatorUtil.isNotEmpty(unidade))
			unidade = getGenericDAO().findByPrimaryKey(unidade.getId(), Unidade.class, "id", "nome");
		
		if (ValidatorUtil.isNotEmpty(modalidadeBolsa))
			modalidadeBolsa = getGenericDAO().findByPrimaryKey(modalidadeBolsa.getId(), ModalidadeBolsaExterna.class, "id", "descricao");	
		
		descricaoNivel = null;
		if (nivel != null && nivel != '0')
			descricaoNivel = NivelEnsino.getDescricao(nivel);		
		
		return forward(form);
	}

	/**
	 * Gera o relatório de quantitativo de planos por status
	 * @return
	 * @throws DAOException
	 */	
	private String gerarQuantitativoPorStatus() throws HibernateException, DAOException {
		RelatorioDiscentesAtendidosDocenciaAssistidaDao dao = getDAO(RelatorioDiscentesAtendidosDocenciaAssistidaDao.class);
		try {
			
			listagem = dao.findQuantitativoStatus(	ano, periodo, unidade, nivel, modalidadeBolsa );
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return "/stricto/plano_docencia_assistida/relatorios/quant_status.jsf";
	}

	/**
	 * Gera o relatório de quantitativo de atividades
	 * @return
	 * @throws DAOException
	 */
	private String gerarQuantitativoAtividades() throws DAOException {
		AtividadesDocenciaAssistidaDao dao = getDAO(AtividadesDocenciaAssistidaDao.class);
		try {
			
			listagem = dao.findQuantAtividades(	ano, periodo, unidade, nivel, modalidadeBolsa );
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			detalhe = dao.findOutrasAtividades(	ano, periodo, unidade, nivel, modalidadeBolsa  );
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return "/stricto/plano_docencia_assistida/relatorios/quant_atividades.jsf";
	}
	
	/**
	 * Gera o relatório de quantitativo de discentes da graduação atendidos pela 
	 * docência assistida agrupados por componente curricular.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private String gerarQuantitativoDiscentes() throws DAOException {
		RelatorioDiscentesAtendidosDocenciaAssistidaDao dao = getDAO(RelatorioDiscentesAtendidosDocenciaAssistidaDao.class);
		try {
			
			listagem = dao.findQuantitativoDiscentes(ano, periodo, unidade, nivel, modalidadeBolsa);
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return "/stricto/plano_docencia_assistida/relatorios/quant_discentes.jsf";
	}	
	
	/**
	 * Gera o relatório de quantitativo de componentes da graduação atendidos pela 
	 * docência assistida.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private String gerarQuantitativoComponentes() throws DAOException {
		RelatorioDiscentesAtendidosDocenciaAssistidaDao dao = getDAO(RelatorioDiscentesAtendidosDocenciaAssistidaDao.class);
		try {
			
			listagem = dao.findQuantitativoComponente(ano, periodo, unidade, nivel, modalidadeBolsa);
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return "/stricto/plano_docencia_assistida/relatorios/quant_componentes.jsf";
	}		
	
	/**
	 * Caminho do form para geração dos relatórios
	 */
	@Override
	public String getFormPage() {
		return "/stricto/plano_docencia_assistida/relatorios/form.jsf";
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public ModalidadeBolsaExterna getModalidadeBolsa() {
		return modalidadeBolsa;
	}

	public void setModalidadeBolsa(ModalidadeBolsaExterna modalidadeBolsa) {
		this.modalidadeBolsa = modalidadeBolsa;
	}

	public List<Map<String, Object>> getDetalhe() {
		return detalhe;
	}

	public void setDetalhe(List<Map<String, Object>> detalhe) {
		this.detalhe = detalhe;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public TipoRelatorioPlano getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioPlano tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	
	/**
	 * Retorna o título do relatório
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/plano_docencia_assistida/relatorios/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getTituloRelatorio(){
		return (tipoRelatorio != null ? tipoRelatorio.getNome() : null);
	}

	public String getDescricaoNivel() {
		return descricaoNivel;
	}

	public void setDescricaoNivel(String descricaoNivel) {
		this.descricaoNivel = descricaoNivel;
	}
}
