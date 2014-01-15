/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 08/04/2009
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;

/**
 * MBean responsável pelas consultas e geração de relatórios dos discentes por
 * linha de pesquisa.
 * 
 * 
 * @author Júlio Cesar
 */
@Component("relatorioDiscentesLinhaPesquisa")
@Scope("request")
public class RelatorioDiscentesLinhaPesquisaMBean extends SigaaAbstractController<LinhaPesquisa> {
	/** Unidade selecionada */
	private Unidade unidade;
    /** Lista de discentes stricto */
	private Collection<DiscenteStricto> discentes = new ArrayList<DiscenteStricto>();
	/** Caminho da JSP do Relatório */
	public final String JSP_DISCENTES_LINHA_PESQUISA = "/stricto/relatorios/relatorios_discente_linha_pesquisa.jsp";

	/**
	 * Gera o relatório de discentes de um programa, agrupados por linhas de pesquisa 
	 * <br/><br/>
	 * Chamado por:
	 * <ul>	
	 *     <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorio() throws DAOException, SegurancaException {

		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG,
				SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_GRADUACAO, SigaaPapeis.GESTOR_TECNICO);

		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		
		try {
			if (isPortalCoordenadorStricto() || isPortalPpg()) {
				
				if( getProgramaStricto() !=  null)
					unidade = getProgramaStricto();
				if( isEmpty(unidade) || unidade.getId() <= 0 ){
					addMensagemErro("Selecione o programa.");
					return null;
				}
				discentes = dao.findDiscentesByProgramaParaRelatorio(unidade.getId());
			}
		} finally {
			dao.close();
		}

		return forward(JSP_DISCENTES_LINHA_PESQUISA);
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Collection<DiscenteStricto> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteStricto> discentes) {
		this.discentes = discentes;
	}
}
