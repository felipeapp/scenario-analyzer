/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/10/22
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para gera��o de relat�rio de alunos por cidade p�lo de ensino a dist�ncia.
 * @author David Pereira
 *
 */
@Component("relatorioAlunosPolo") @Scope("request")
public class RelatorioAlunosPoloMBean extends SigaaAbstractController<Discente> {
	/**P�gina do relat�rio anal�tico.*/
	public static final String PAGINA_RELATORIO = "/ead/relatorios/relatorio_alunos_polo_list.jsp";
	/**P�gina do relat�rio sint�tico*/
	public static final String PAGINA_RELATORIO_SINTETICO = "/ead/relatorios/relatorio_numero_alunos.jsp";
	/**Polo selecionado na busca.*/
	private Polo polo = new Polo();
	/**Curso selecionado na busca.*/
	private Curso curso = new Curso();
	/**Indica se o resultado ser� agrupado pelo polo, curso e nome.*/
	private boolean agrupar;
	/**Indica se a busca ser� apenas pelos matriculados.*/
	private boolean matriculados;
	/**Indica se a busca ser� anal�tica ou sint�tica*/
	private boolean analitico;
	/**Resultado da busca.*/
	private List<DiscenteGraduacao> discentes;
	/**Ano da busca*/
	private Integer ano;
	/**Per�odo da busca.*/
	private Integer periodo;
	
	/**
	 * Gera e redireciona para uma p�gina com um  relat�rio de discentes de acordo com o p�lo
	 * JSP: sigaa.war/ead/relatorios/relatorio_alunos_polo_form.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerar() throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		
		if (periodo != null && (periodo.compareTo(1) < 0 || periodo.compareTo(2) > 0)) {
			addMensagemErro("O campo Per�odo deve ser vazio, 1(um) ou 2(dois).");
			return null;
		}
		
		CoordenacaoPolo coordenacaoPolo = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		if  (coordenacaoPolo != null && coordenacaoPolo.getPolo() != null) {
			polo = coordenacaoPolo.getPolo(); 
		}
		
		if (polo.getId() > 0)
			polo = dao.findByPrimaryKey(polo.getId(), Polo.class);
		
		if (curso.getId() > 0)
			curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
		
		//Se n�o filtrar por curso e colocado em ordem alfab�tica todos os cursos.
		if (curso.getId() == 0 || polo.getId() > 0) {
			agrupar = true;	
		}
		
		discentes = dao.findDiscentesByPoloCurso(polo, curso, ano, periodo, matriculados, agrupar,analitico);
		
		if (discentes.size() == 0 ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		if (analitico) {
			return forward(PAGINA_RELATORIO);
		}
		else {
			return forward(PAGINA_RELATORIO_SINTETICO);
		}
	}
	
	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public List<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	public boolean isAgrupar() {
		return agrupar;
	}

	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
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

	public boolean isMatriculados() {
		return matriculados;
	}

	public void setMatriculados(boolean matriculados) {
		this.matriculados = matriculados;
	}

	public boolean isAnalitico() {
		return analitico;
	}

	public void setAnalitico(boolean analitico) {
		this.analitico = analitico;
	}
	
}
