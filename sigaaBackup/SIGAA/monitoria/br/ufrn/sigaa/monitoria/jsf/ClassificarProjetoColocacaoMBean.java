/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 20/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoComparator;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Managed Bean para calcular a colocação de cada projeto
 * de acordo com sua média final.
 *
 * @author David Ricardo
 * @author Ilueny Santos
 *
 */
@Component("classificarProjeto")
@Scope("request")
public class ClassificarProjetoColocacaoMBean extends SigaaAbstractController<ProjetoEnsino> {

	/**
	 * Atributo utilizado para representar o Edital de Monitoria
	 */
	private EditalMonitoria edital = new EditalMonitoria();

	/** Atributo utilizado para representar a lista de projetos */
	private List<ProjetoEnsino> projetos;

	/**
	 * Exibi todos os editais de monitoria.
	 * Permitindo a seleção dos projetos que podem participar da distribuição de bolsas.
	 * 
	 * Chamado por:
	 * /sigaa.war/monitoria/ClassificarProjetos/busca_edital.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<SelectItem> getEditais() throws DAOException {
	    Collection<EditalMonitoria> editais = getDAO(EditalDao.class).findEditalMonitoriaByTipos(null, Edital.MONITORIA,Edital.AMBOS_MONITORIA_E_INOVACAO,Edital.MONITORIA_EOU_INOVACAO);
	    return toSelectItems(editais, "id", "descricaoCompleta");
	}

	/**
	 * Ordena uma coleção de projetos por media final.
	 * 
	 * Chamado por:
	 * /sigaa.war/monitoria/ClassificarProjetos/busca_edital.jsp
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String classificarProjetos() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		edital = dao.findByPrimaryKey(edital.getId(), EditalMonitoria.class);
		projetos = dao.findProjetosClassificacaoByEdital(edital);
		for (ProjetoEnsino projeto : projetos) {
		    projeto.setEditalMonitoria(edital);
		}			
		Collections.sort(projetos, new ProjetoComparator());
		return forward(ConstantesNavegacaoMonitoria.CLASSIFICACAODEPROJETOS_CLASSIFICACAO);
	    } catch(Exception e) {
		notifyError(e);
	    }
	    return null;
	}

	/**
	 * Método que informa o Edital
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado por JSP(s)</li>
	 * </ul>
	 * @return the edital
	 */
	public EditalMonitoria getEdital() {
		return edital;
	}

	/**
	 * Método que seta o Edital
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado por JSP(s)</li>
	 * </ul>
	 * @param edital the edital to set
	 */
	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}

	/**
	 * Método que informa a lista de projetos
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado por JSP(s)</li>
	 * </ul>
	 * @return the projetos
	 */
	public List<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	/**
	 * Método que seta a lista de projetos
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não chamado por JSP(s)</li>
	 * </ul>
	 * @param projetos the projetos to set
	 */
	public void setProjetos(List<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}
}
