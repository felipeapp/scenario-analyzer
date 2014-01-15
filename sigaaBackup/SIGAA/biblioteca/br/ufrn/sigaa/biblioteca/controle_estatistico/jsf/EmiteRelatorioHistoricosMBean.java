/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ProrrogacaoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 * Managed Bean responsável por gerenciar a geração dos relatórios citados abaixo:<br/> 
 * 
 * <ul>
 * <li>Histórico de Alterações de um Título</li>
 * <li>Histórico de Alterações de um Material</li>
 * <li>Histórico de Empréstimos de um Material</li>
 * </ul>
 * 
 * @author Fred_Castro
 *
 */
@Component("emiteRelatorioHistoricosMBean")
@Scope("request")
public class EmiteRelatorioHistoricosMBean extends SigaaAbstractController<Object> {
	
	/**
	 * A página de filtro do histórico de alterações de um material
	 */
	public static final String PAGINA_FORM_CONSULTA_ALTERACAO_MATERIAL = "/biblioteca/controle_estatistico/formHistoricoAlteracoesMaterial.jsp";
	/**
	 * A página de filtro do histórico de alterações de um título
	 */
	public static final String PAGINA_FORM_CONSULTA_ALTERACAO_TITULO = "/biblioteca/controle_estatistico/formHistoricoAlteracoesTitulo.jsp";
	/**
	 * A página de filtro do histórico de empréstimos
	 */
	public static final String PAGINA_FORM_CONSULTA_EMPRESTIMOS = "/biblioteca/controle_estatistico/formConsultaEmprestimos.jsp";
	
	/**
	 * A página do histórico de empréstimos
	 */
	public static final String PAGINA_RELATORIO_CONSULTA_EMPRESTIMOS = "/biblioteca/controle_estatistico/relatorioConsultaEmprestimos.jsp";
	/**
	 * A página do histórico de alterações de título
	 */
	public static final String PAGINA_RELATORIO_CONSULTA_HISTORICO_ALTERACOES_TITULO = "/biblioteca/controle_estatistico/relatorioHistoricoAlteracoesTitulo.jsp";
	/**
	 * A página do histórico de alterações de um material
	 */
	public static final String PAGINA_RELATORIO_CONSULTA_HISTORICO_ALTERACOES_MATERIAL = "/biblioteca/controle_estatistico/relatorioHistoricoAlteracoesMaterial.jsp";
	
	/** guarda informações do título cujo o histórico de alterações vai ser emitido */
	private TituloCatalografico tituloCatalografico;
	
	/** guarda informações do material cujo o histórico de alterações vai ser emitido */
	private MaterialInformacional materialInformacional;
	
	/** Mensagem ajax de erro para mostrar ao usuário */
	private String mensagemErroAjax;
	
	/** O código de barras utilizado para buscar o material cujo histórico de alterações vai ser emitido */
	private String codigoBarras;
	
	/**
	 * A data de iníco do período da consulta do relatório.
	 */
	private Date dataInicio;
	/**
	 * A data final do período da consulta do relatório.
	 */
	private Date dataFim;
	
	
	/**
	 * Guarda o histório de empréstimos de um material específico.
	 */
	private Collection <Emprestimo> emprestimos;
	
	/**
	 * Guarda o histório de empréstimos de um exemplar passado como parâmetro
	 */
	private Collection <Emprestimo> historicoEmprestimos;
	
	/**
	 * Guarda os dados do histório de alterações de um título.
	 */
	private List<Object[]> historico;
	
	/**
	 * Consulta por um material, dado seu código de barras.
	 * Usado na página /sigaa.war/biblioteca/controle_estatistico/formConsultaEmprestimos.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public void consultarCodigoBarras (ActionEvent e) throws DAOException, SegurancaException {
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class);
		materialInformacional = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
		
		if (materialInformacional != null){
			materialInformacional.setInformacao( BibliotecaUtil.obtemDadosMaterialInformacional(materialInformacional.getId()));
			
			mensagemErroAjax = "";
			codigoBarras = "";

		} else
			mensagemErroAjax = "Material não encontrado.";		
	}
	
	/**
	 * Exibe o formulário para a geração do relatório de consultas as alterações em um material catalográfico.
	 * Usado em /sigaa.war/biblioteca/menus/controle_estatistico.jsp
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarConsultaAlteracaoMaterial () throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		materialInformacional = new Exemplar();
		
		return forward(PAGINA_FORM_CONSULTA_ALTERACAO_MATERIAL);
	}

	/**
	 * Exibe o formulário para a geração do relatório de consultas a alterações em um título catalográfico.
	 * Usado em /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulossCatalograficos.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String iniciarConsultaAlteracaoTitulo() throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		GenericDAO dao = getGenericDAO();
		
		Integer idTitulo = getParameterInt("idTitulo");
		
		tituloCatalografico = dao.findByPrimaryKey(idTitulo, TituloCatalografico.class);
		tituloCatalografico.setFormatoReferencia(new FormatosBibliograficosUtil().gerarFormatoReferencia(tituloCatalografico, true));
		
		return forward(PAGINA_FORM_CONSULTA_ALTERACAO_TITULO);
	}
	
	/**
	 * Inicia o caso de uso para emitir o relatório do histórico de empréstimos de um material.
	 * Usado em: <li>
	 * 			<ul>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</ul>
	 *          <ul>/sigaa.war/biblioteca/menus/circulacao.jsp</ul>
	 *          </li>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarConsultaEmprestimos () throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		materialInformacional = null;
		codigoBarras = "";
		dataFim = new Date();
		dataInicio = CalendarUtils.adicionarAnos(dataFim, -1);
		return forward(PAGINA_FORM_CONSULTA_EMPRESTIMOS);
	}
	
	
	/**
	 * Gera o relatório de empréstimos de um material catalográfico em um determinado período.
	 * Usado em /sigaa.war/biblioteca/controle_estatistico/formConsultaEmprestimos.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String gerarRelatorioEmprestimos () throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		EmprestimoDao dao = null;
		ProrrogacaoEmprestimoDao pDao = null;
		MaterialInformacionalDao daoMaterial = null;			
		
		try{
			int idExemplar = 0;
			if (getParameter("idExemplar") != null){
				idExemplar = getParameterInt("idExemplar");
				
			}
			
			dao = getDAO(EmprestimoDao.class);
			pDao = getDAO(ProrrogacaoEmprestimoDao.class);
			
			if (materialInformacional == null && idExemplar > 0) {
				daoMaterial = getDAO(MaterialInformacionalDao.class);			
				materialInformacional = daoMaterial.findByPrimaryKey(idExemplar, MaterialInformacional.class);								
			}
		
			if (materialInformacional != null){
				emprestimos = dao.findEmprestimosByMaterial(materialInformacional, dataInicio, dataFim, null);
				
				if (emprestimos != null && !emprestimos.isEmpty()){
					
					for (Emprestimo e : emprestimos)
						e.setProrrogacoes(pDao.findProrrogacoesByEmprestimoTipo(e, TipoProrrogacaoEmprestimo.RENOVACAO));
					
					return forward(PAGINA_RELATORIO_CONSULTA_EMPRESTIMOS);
				}
				
				addMensagemErro("O material selecionado não possui empréstimos no período especificado.");
				return null;
			}
			
			addMensagemErro("Selecione um material.");
			return null;
		} finally {
			if (dao != null)
				dao.close();
			
			if (daoMaterial != null)
				daoMaterial.close();
			
			if (pDao != null)
				pDao.close();				
		}
	}
	
	/**
	 * Limpa coleção de histórico de empréstimos
	 * Chamado por: /sigaa.war/biblioteca/controle_estatistico/formHistoricoEmprestimo.jsp
	 * @return
	 */
	public String getLimpaHistoricoEmprestimos(){
		historicoEmprestimos = null;		
		return null;
	}	
	
	
	/**
	 * Retorna todos os empréstimos realizados para o exemplar passado como parâmetro
	 * Chamado por: /sigaa.war/biblioteca/controle_estatistico/formHistoricoEmprestimo.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public Collection <Emprestimo> getHistoricoEmprestimos()throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		EmprestimoDao dao = null;
		ProrrogacaoEmprestimoDao pDao = null;
		MaterialInformacionalDao daoMaterial = null;			
		
		try{			
			if (historicoEmprestimos == null){				
				int idExemplar = getParameterInt("idExemplar");
				
				dao = getDAO(EmprestimoDao.class);
				pDao = getDAO(ProrrogacaoEmprestimoDao.class);
				
				daoMaterial = getDAO(MaterialInformacionalDao.class);		
	
				materialInformacional = daoMaterial.findByPrimaryKey(idExemplar, MaterialInformacional.class);	
			
				if (materialInformacional != null){
					historicoEmprestimos = dao.findEmprestimosByMaterial(materialInformacional, null, null, 10);
					for (Emprestimo e : historicoEmprestimos)
						e.setProrrogacoes(pDao.findProrrogacoesByEmprestimoTipo(e, TipoProrrogacaoEmprestimo.RENOVACAO));													
				} else			
					addMensagemErro("Selecione um material.");
			}
		} finally {
			if (dao != null)
				dao.close();
			
			if (daoMaterial != null)
				daoMaterial.close();
			
			if (pDao != null)
				pDao.close();				
		}
		return historicoEmprestimos;
	}
	
	
	/**
	 * Gera o relatório de histórico de alterações a um Título Catalográfico.
	 * Usado em /sigaa.war/biblioteca/controle_estatistico/formHistoricoAlteracoesTitulo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String gerarRelatorioHistoricoAlteracoesTitulo () throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		TituloCatalograficoDao dao = null;
		historico = null;
		
		try {
			if (tituloCatalografico != null){
				dao = getDAO(TituloCatalograficoDao.class);
				
				historico = dao.findAlteracoesByTituloPeriodo(tituloCatalografico.getId(), dataInicio, dataFim, null);
				
				return forward(PAGINA_RELATORIO_CONSULTA_HISTORICO_ALTERACOES_TITULO);
			}
		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}
	
	/**
	 * Gera o relatório de histórico de alterações a um Material Catalográfico.
	 * Usado em /sigaa.war/biblioteca/controle_estatistico/formHistoricoAlteracoesMaterial.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String gerarRelatorioHistoricoAlteracoesMaterial () throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO);
		
		MaterialInformacionalDao dao = null;
		historico = null;
		
		try {
			if ( !StringUtils.isEmpty(codigoBarras) ){
				dao = getDAO(MaterialInformacionalDao.class);
				materialInformacional = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
				
				if (materialInformacional == null || materialInformacional.getId() <= 0){
					addMensagemErro("O código de barras digitado não pertence a um material cadastrado.");
				} else {
					int idTitulo = dao.findIdTituloMaterial(materialInformacional.getId());
					
					TituloCatalografico t = new TituloCatalografico(idTitulo);
					t.setFormatoReferencia(new FormatosBibliograficosUtil().gerarFormatoReferencia(t, true));
					materialInformacional.setTituloCatalografico(t);
					historico = dao.findAlteracoesByMaterialPeriodo(materialInformacional.getId(), dataInicio, dataFim);
					
					return forward(PAGINA_RELATORIO_CONSULTA_HISTORICO_ALTERACOES_MATERIAL);
				}
			} else
				addMensagemErro ("Digite o código de barras de um material.");
		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}

	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}

	public void setTituloCatalografico(TituloCatalografico tituloCatalografico) {
		this.tituloCatalografico = tituloCatalografico;
	}

	public MaterialInformacional getMaterialInformacional() {
		return materialInformacional;
	}

	public void setMaterialInformacional(MaterialInformacional materialInformacional) {
		this.materialInformacional = materialInformacional;
	}

	public String getMensagemErroAjax() {
		return mensagemErroAjax;
	}

	public void setMensagemErroAjax(String mensagemErroAjax) {
		this.mensagemErroAjax = mensagemErroAjax;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
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

	public Collection<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(Collection<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	/**
	 * Retorna os dados do histório formato para exibição na página html.  
	 * Um nome mais propriado para essse método seria getHistoricoFormatadoEmHTML();
	 *
	 * @return
	 */
	public List<Object[]> getHistorico() {
		
		for (Object[] object : historico)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return historico;
	}

	public void setHistorico(List<Object[]> historico) {
		this.historico = historico;
	}
	
	
}
