/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaProjetadaDAO;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioCursoSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * MBean responsável pela geração de relatórios de índice de trancamento, 
 * solicitações de matrículas e matrículas projetadas.
 * @author Eric Moura
 *
 */

public class RelatorioCursoMBean extends AbstractRelatorioGraduacaoMBean {

	private List<Map<String,Object>> listaCurso = new ArrayList<Map<String,Object>>();
	private List<Map<String,Object>> listaDetalhe = new ArrayList<Map<String,Object>>();
	private List<Map<String, Object>> listaTotais = new ArrayList<Map<String, Object>>();
	
	//Constantes de navegação - paginas dos relatórios
	//Listas
	private final String JSP_RELATORIO_INDICE_TRANCAMENTO = "lista_indice_trancamento.jsp";
	private final String JSP_RELATORIO_LIMITE_IDEAL_SEMESTRE= "lista_limite_ideal_semestre.jsp";
	private final String JSP_RELATORIO_TOTAL_SOLICITACOES_MATRICULA= "lista_totais_solicitacoes_matriculas.jsp";
	private final String JSP_SELECIONA_MATRICULAS_PROJETADAS_CURSO = "matricula_projetada.jsp";
	private final String JSP_RELATORIO_MATRICULAS_PROJETADAS_CURSO = "lista_matriculas_projetadas.jsp";
	private final String JSP_RELATORIO_DETALHE_MATRICULAS_PROJETADAS_CURSO = "lista_matriculas_projetadas_curso.jsp";

	public RelatorioCursoMBean() {
		initObj();
		setAmbito("curso/");

	}

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean#carregarTituloRelatorio()
	 */
	/**
	 * Método responsável por trazer as informações do curso e centro para os seus respectivos atributos.
	 * Não é chamado por nenhuma JSP.
	 */
	@Override
	public void carregarTituloRelatorio() throws DAOException {
		super.carregarTituloRelatorio();

		RelatorioCursoSqlDao dao = getDAO(RelatorioCursoSqlDao.class);
		if(centro!=null && centro.getId()!=0 )
			centro = dao.findByPrimaryKey(centro.getId(), Unidade.class);
		else if(curso!=null && curso.getId()!=0 )
			curso = dao.findByPrimaryKey(curso.getId(), Curso.class);

		dao.close();
	}

	/**
	 * Método que repassa pra view o índice de trancamento e cancelamentos em disciplinas no curso.
	 * Método chamado por: /graduacao/relatorios/curso/seleciona_indice_trancamento.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaCursoIndiceTrancamento() throws DAOException{
		carregarTituloRelatorio();
		RelatorioCursoSqlDao dao = getDAO(RelatorioCursoSqlDao.class);
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null)
			matrizCurricular.getCurso().setId(idCurso);
		if(curso.getUnidade().getId() !=0 )
			matrizCurricular.getCurso().getUnidade().setId(curso.getUnidade().getId());
		if(filtros.get(AbstractRelatorioSqlDao.ANO_PERIODO))
			ano=0;
		listaCurso = dao.findListaCursoIndiceTrancamento(matrizCurricular, ano, periodo, filtros);
		dao.close();
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_INDICE_TRANCAMENTO);

	}

	/**
	 * Método que repassa pra view o índice de trancamento e cancelamentos em disciplinas no curso.
	 * Não é chamado por nenhuma JSP. 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaCursoLimiteIdealSemestre() throws DAOException{
		carregarTituloRelatorio();
		RelatorioCursoSqlDao dao = getDAO(RelatorioCursoSqlDao.class);
		listaCurso = dao.findListaCursoLimiteIdealSemestre(centro, curso);
		dao.close();
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_LIMITE_IDEAL_SEMESTRE);
	}	
	
	/**
	 * Exibe relatório de Totais de solicitações por matricula.
	 * Método chamado por: /graduacao/menus/relatorios_dae.jsp
	 * @return
	 * @throws DAOException
	 */	
	public String gerarRelatorioTotaisSolicitacoesMatricula() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);		
		try{
			CalendarioAcademico cal = getCalendarioVigente();
			listaCurso = dao.findTotaisSolicitacoesMatricula(cal.getAno(), cal.getPeriodo());
			return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_TOTAL_SOLICITACOES_MATRICULA);
		} finally {
			if (dao != null)
				dao.close();
		}		
	}
	
	/**
	 * Método responsável por chamar o JSP que define o ano inicio e fim para iniciar a listagem.
	 * Método Chamado por: /portais/rh_plan/abas/graduacao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciaRelatorioMatriculasProjetadasCurso() throws SegurancaException, DAOException{
		checkListRole();
  	    return forward(CONTEXTO+getAmbito()+JSP_SELECIONA_MATRICULAS_PROJETADAS_CURSO);
	}
	
	/**
	 * Exibe o relatório analítico de matriculas projetadas por ano
	 * Método Chamado por: /graduacao/relatorios/curso/matricula_projetada.jsp
	 * @return
	 * @throws DAOException
	 */
	public String geraRelatorioMatriculasProjetadasCurso() throws DAOException {
		if (ano > anoFim)
			addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Ano inicio", "Ano fim");
		if (ano == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano Inicio");		
		if (anoFim == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Ano Fim");
		
		if (!hasErrors()){
			MatriculaProjetadaDAO dao = getDAO(MatriculaProjetadaDAO.class);
			listaCurso = dao.findMatriculaProjetada(ano, anoFim);
			
			if (listaCurso.size() == 0) 
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			else {
				// Agrupa os totais da lista por ano. 
				for (Map<String, Object> linha : listaCurso){
					
					int ano = Integer.parseInt(""+linha.get("ano"));
					Float total = Float.parseFloat(""+linha.get("total"));
					
					boolean inserido = false;
					
					for (Map <String, Object> aux : listaTotais){
						Integer auxAno = Integer.parseInt(""+aux.get("ano"));
						Float auxTotal = Float.parseFloat(""+aux.get("total"));
						
						if (auxAno == ano){
							total += auxTotal;
							aux.put("total", total);
							inserido = true;
							break;
						}
					}
					if (!inserido){
						Map <String, Object> inserir = new HashMap <String, Object> ();
						inserir.put("ano", ano);
						inserir.put("total", total);
						listaTotais.add(inserir);
					}
				}
				return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MATRICULAS_PROJETADAS_CURSO);			
			}
		}		
		return null;			
	}
	
	/**
	 * Exibe o detalhamento de matriculas projetadas por centro e curso.
	 * Método Chamado por: /graduacao/relatorios/curso/lista_matricula_projetada.jsp
	 * @return
	 * @throws DAOException
	 */
	public String geraDetalhamentoMatriculasProjetadasCurso() throws DAOException{
		MatriculaProjetadaDAO dao = getDAO(MatriculaProjetadaDAO.class);
		try {
			ano = getParameterInt("ano");		
			listaCurso = dao.findMatriculaProjetada(ano, ano);				
			return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_DETALHE_MATRICULAS_PROJETADAS_CURSO);			
		} finally {
			if (dao != null)
				dao.close();			
		}				
	}
	
	/**
	 * Exibe o detalhamento de matriculas projetadas por centro e curso no formato CSV.
	 * Método Chamado por: /graduacao/relatorios/curso/lista_matricula_projetada.jsp
	 * @return
	 */
	public String gerarDetalhamentoMatriculasProjetadasCursoCSV(){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		
		MatriculaProjetadaDAO dao = getDAO(MatriculaProjetadaDAO.class);
		
		try {
			getCurrentResponse().setContentType("application/csv");
			getCurrentResponse().setHeader("Content-Disposition", "inline; filename=lista_matriculas_projetadas_curso_"+sdf.format(new Date())+".csv");
			
			PrintWriter out = getCurrentResponse().getWriter();
			
			out.println(dao.findMatriculaProjetadaCSV(ano, ano));
			
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}	
	
	/**
	 * @return the listaCurso
	 */
	public int getNumeroRegistosEncontrados() {
		if(listaCurso!=null)
			return listaCurso.size();
		else
			return 0;
	}

	/**
	 * @return the listaCurso
	 */
	public List<Map<String, Object>> getListaCurso() {
		return listaCurso;
	}

	/**
	 * @param listaCurso the listaCurso to set
	 */
	public void setListaCurso(List<Map<String, Object>> listaCurso) {
		this.listaCurso = listaCurso;
	}

	public List<Map<String, Object>> getListaDetalhe() {
		return listaDetalhe;
	}

	public void setListaDetalhe(List<Map<String, Object>> listaDetalhe) {
		this.listaDetalhe = listaDetalhe;
	}

	public List<Map<String, Object>> getListaTotais() {
		return listaTotais;
	}

	public void setListaTotais(List<Map<String, Object>> listaTotais) {
		this.listaTotais = listaTotais;
	}

	
	
}