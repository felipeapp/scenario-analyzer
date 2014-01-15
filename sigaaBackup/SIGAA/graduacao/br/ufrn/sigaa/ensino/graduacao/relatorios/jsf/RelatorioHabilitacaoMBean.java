/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioHabilitacaoSqlDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;

/**
 * @author Eric Moura
 *
 */
public class RelatorioHabilitacaoMBean extends AbstractRelatorioGraduacaoMBean {

	List<Map<String,Object>> listaHabilitacao = new ArrayList<Map<String,Object>>();

	private GrauAcademico modalidade = new GrauAcademico();
	private Curso curso = new Curso();


	//Constantes de navega��o - paginas dos relat�rios
	//Listas
	private final String JSP_RELATORIO_ENFASE = "lista_enfase.jsp";

	private final String CONTEXTO ="/graduacao/relatorios/habilitacao/";

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean#carregarTituloRelatorio()
	 */
	@Override
	public void carregarTituloRelatorio() throws DAOException {
		RelatorioHabilitacaoSqlDao dao = getDAO(RelatorioHabilitacaoSqlDao.class);
		if(modalidade!=null && modalidade.getId()!=0 )
			modalidade =  dao.findByPrimaryKey(modalidade.getId(), GrauAcademico.class);
		else if(curso!=null && curso.getId()!=0 )
			curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
		dao.close();
	}

	/**
	 * M�todo que repassa pra view a Rela��o de �nfase -Curso+Modalidade+Enfase
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaEnfase() throws DAOException{

		carregarTituloRelatorio();
		RelatorioHabilitacaoSqlDao dao = getDAO(RelatorioHabilitacaoSqlDao.class);
		listaHabilitacao = dao.findListaEnfase(curso, modalidade);
		dao.close();
		return forward(CONTEXTO+JSP_RELATORIO_ENFASE);

	}




}
