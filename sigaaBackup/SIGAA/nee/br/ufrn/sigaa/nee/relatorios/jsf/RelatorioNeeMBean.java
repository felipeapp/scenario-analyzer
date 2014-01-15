/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 28/02/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.nee.RelatorioNeeSqlDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * MBean responsável or controlar os relatórios pertinentes ao módulo NEE.
 * @author Rafael Gomes
 *
 */
@Component("relatorioNee") @Scope("request")
public class RelatorioNeeMBean extends AbstractRelatorioGraduacaoMBean{
	
	/** Lista contendo alguns dos dados dos discentes com NEE encontrados. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Objeto Processo Seletivo utilizado no filtro do relatório.*/
	private ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();
	/** Objeto Tipo de Necessidade Educacional Especial utilizado no filtro do relatório.*/
	private TipoNecessidadeEspecial tipoNecessidade = new TipoNecessidadeEspecial();
	/** Campo utilizado para armazenar o nome do curso utilizado no filtro do relatório.*/
	private String nomeCurso;
	
	/** Campo de filtro de pesquisa por Processo Seletivo. */
	private boolean filtroProcessoSeletivo;
	
	/** Campo de filtro de pesquisa por Tipo de Necessidade Especial. */
	private boolean filtroTipoNecessidade;
	
	/** Campo de filtro de pesquisa por Ano de Ingresso do Discente. */
	private boolean filtroAnoIngresso;
	
	/** Link do formulário do Relatório de Alunos com Necessidades Especiais por Processo Seletivo. */
	private static final String JSP_SELECIONA_ALUNO_VESTIBULAR = "seleciona_alunos_necessides_por_vestibular.jsp";
	/** Link do Relatório de Alunos com Necessidades Especiais por Processo Seletivo. */
	private static final String JSP_REL_ALUNO_VESTIBULAR = "rel_alunos_necessides_por_vestibular.jsp";
	
	/** Contexto das páginas utilizadas. */
	private static final String CONTEXTO ="/nee/relatorios/";
	/** Lista do filtros utilizados na geração do relatório.*/
	private List<String> filtroList = new ArrayList<String>();
	
	
	/**
	 * Construtor Padrão
	 * Inicializa os campos do formulário
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public RelatorioNeeMBean()  {
		initObj();
	}


	/**
	 * Checa se o usuário tem permissão para realizar a operação e em seguida o redireciona 
	 * para a tela para o preenchimento do ano, do período e de qual forma o usuário gostaria que 
	 * o relatório quantitativo de turmas e disciplinas por departamento.<br/>
	 * 
	 * JSP: sigaa.war/nee/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioAlunosPorVestibular() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO + JSP_SELECIONA_ALUNO_VESTIBULAR);
	}
	
	/**
	 * Verifica se o ano e período foram realmente preenchidos e realiza a busca, caso o resultado não retorne nenhum
	 * registro é exibido ao usuário e não há a geração do relatório.<br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/nee/relatorios/seleciona_alunos_necessides_por_vestibular.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioAlunosPorVestibular() throws DAOException {
		
		filtros.put(RelatorioNeeSqlDao.PROCESSO_SELETIVO, isFiltroProcessoSeletivo());
		filtros.put(RelatorioNeeSqlDao.TIPO_NECESSIDADE, isFiltroTipoNecessidade());
		filtros.put(RelatorioNeeSqlDao.CURSO, isFiltroCurso());
		filtros.put(RelatorioNeeSqlDao.ANO_INGRESSO, isFiltroAnoIngresso());
		
		RelatorioNeeSqlDao dao = getDAO(RelatorioNeeSqlDao.class);
		listaDiscente = dao.findAlunosNeePorProcessoSeletivo(processoSeletivo, tipoNecessidade, nomeCurso, ano, filtros);
		
		if (listaDiscente.size() > 0) {
			filtroList = new ArrayList<String>();
			if ( filtros.get(RelatorioNeeSqlDao.ANO_INGRESSO) )
				filtroList.add("<b>Ano de Ingresso: </b>" + ano);
			if ( filtros.get(RelatorioNeeSqlDao.PROCESSO_SELETIVO) ){
				dao.initialize(processoSeletivo);
				filtroList.add("<b>Processo Seletivo: </b>" + processoSeletivo.getNome());
			}	
			if ( filtros.get(RelatorioNeeSqlDao.TIPO_NECESSIDADE) ){
				dao.initialize(tipoNecessidade);
				filtroList.add("<b>Tipo de Necessidade Educacional Especial: </b>" + tipoNecessidade.getDescricao());
			}
			if ( filtros.get(RelatorioNeeSqlDao.CURSO) )
				filtroList.add("<b>Curso: </b>" + nomeCurso);
			
			return forward(CONTEXTO + JSP_REL_ALUNO_VESTIBULAR);	
		}
		addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	
		return null;
	}	
		
	
	
	/** GET AND SET */
	/**
	 *  the listaDiscente
	 */
	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}


	/**
	 *  listaDiscente the listaDiscente to set
	 */
	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}


	/**
	 *  the processoSeletivo
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}


	/**
	 *  processoSeletivo the processoSeletivo to set
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}


	/**
	 *  the tipoNecessidade
	 */
	public TipoNecessidadeEspecial getTipoNecessidade() {
		return tipoNecessidade;
	}


	/**
	 *  tipoNecessidade the tipoNecessidade to set
	 */
	public void setTipoNecessidade(TipoNecessidadeEspecial tipoNecessidade) {
		this.tipoNecessidade = tipoNecessidade;
	}


	/**
	 *  the nomeCurso
	 */
	public String getNomeCurso() {
		return nomeCurso;
	}


	/**
	 *  nomeCurso the nomeCurso to set
	 */
	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}


	/**
	 *  the filtroProcessoSeletivo
	 */
	public boolean isFiltroProcessoSeletivo() {
		return filtroProcessoSeletivo;
	}


	/**
	 *  filtroProcessoSeletivo the filtroProcessoSeletivo to set
	 */
	public void setFiltroProcessoSeletivo(boolean filtroProcessoSeletivo) {
		this.filtroProcessoSeletivo = filtroProcessoSeletivo;
	}


	/**
	 *  the filtroTipoNecessidade
	 */
	public boolean isFiltroTipoNecessidade() {
		return filtroTipoNecessidade;
	}


	/**
	 *  filtroTipoNecessidade the filtroTipoNecessidade to set
	 */
	public void setFiltroTipoNecessidade(boolean filtroTipoNecessidade) {
		this.filtroTipoNecessidade = filtroTipoNecessidade;
	}


	/**
	 *  the filtroAnoIngresso
	 */
	public boolean isFiltroAnoIngresso() {
		return filtroAnoIngresso;
	}


	/**
	 *  filtroAnoIngresso the filtroAnoIngresso to set
	 */
	public void setFiltroAnoIngresso(boolean filtroAnoIngresso) {
		this.filtroAnoIngresso = filtroAnoIngresso;
	}


	/**
	 *  the contexto
	 */
	public static String getContexto() {
		return CONTEXTO;
	}


	/**
	 *  the filtroList
	 */
	public List<String> getFiltroList() {
		return filtroList;
	}


	/**
	 *  filtroList the filtroList to set
	 */
	public void setFiltroList(List<String> filtroList) {
		this.filtroList = filtroList;
	}
	
}
