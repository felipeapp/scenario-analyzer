/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 03/04/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf.relatorios;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controlador respons�vel pela gera��o do relat�rio de carga hor�ria de docentes
 * que participam de projetos de pesquisa em andamento
 * 
 * @author Ricardo Wendell
 */
@Component("relatorioCargaHorariaPesquisaBean")  @Scope("request")
public class RelatorioCargaHorariaDocentesPesquisaMBean extends SigaaAbstractController<Object> {

	/**
	 * Gerar relat�rio da carga hor�ria dos docentes de Pesquisa.
	 * <br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/relatorio.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerar() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		
		List<Map<String, Object>> relatorio = getDAO(MembroProjetoDao.class).findRelatorioCargaHorariaDocentesPesquisa();
		if (relatorio == null || relatorio.isEmpty()) {
			addMensagemErro("N�o foram encontrados projetos em andamento com membros registrados para que este relat�rio possa ser gerado");
		}
		
		getCurrentRequest().setAttribute("relatorio", relatorio);
		return forward("/pesquisa/relatorios/ch_docentes.jsp");
	}
	
}