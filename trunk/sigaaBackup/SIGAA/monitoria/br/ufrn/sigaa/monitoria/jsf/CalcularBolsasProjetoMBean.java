/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 20/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.CalculoBolsasMov;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Calcula a quantidade de bolsas que um projeto
 * tem direito.
 *
 * @author David Ricardo
 * @author Ilueny Santos
 *
 */
@Component("calcularBolsas")
@Scope("request")
public class CalcularBolsasProjetoMBean extends SigaaAbstractController<Object> {

	/**
	 * Atributo utilizado para representar o Edital
	 */
	private EditalMonitoria edital = new EditalMonitoria();

	/**
	 * Atributo utilizado para armazenar a lista de projetos
	 */
	private List<ProjetoEnsino> projetos;


	/**
	 * Exibi todos os editais de monitoria.
	 * Permitindo a seleção dos projetos que podem participar da distribuição de bolsas.
	 * 
	 * Chamado por:
	 * /sigaa.war/monitoria/CalcularBolsas/busca_edital.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<SelectItem> getEditais() throws ArqException {
	    prepareMovimento(SigaaListaComando.CALCULAR_BOLSAS_PROJETO);			
	    Collection<EditalMonitoria> editais = getDAO(EditalDao.class).findEditalMonitoriaByTipos(null, Edital.MONITORIA,Edital.AMBOS_MONITORIA_E_INOVACAO,Edital.MONITORIA_EOU_INOVACAO);
	    return toSelectItems(editais, "id", "descricaoCompleta");
	}
	
	/**
	 * Realiza o cálculo da quantidade de bolsas de cada projeto.
	 *
	 * Chamado por:
	 * /sigaa.war/monitoria/CalcularBolsas/busca_edital.jsp
	 *  
	 * @return
	 * @throws SegurancaException
	 */
	@SuppressWarnings("unchecked")	
	public String calcularBolsas() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);
	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		edital = dao.findByPrimaryKey(edital.getId(), EditalMonitoria.class);
		projetos = dao.findBySituacaoEdital(edital, TipoSituacaoProjeto.MON_RECOMENDADO);
		CalculoBolsasMov mov = new CalculoBolsasMov();
		mov.setEdital(edital);
		mov.setProjetos(projetos);
		mov.setCodMovimento(SigaaListaComando.CALCULAR_BOLSAS_PROJETO);
		projetos = (List<ProjetoEnsino>) execute(mov, getCurrentRequest());
		return forward(ConstantesNavegacaoMonitoria.DISTRIBUICAOBOLSAS_CLASSIFICACAO);		
	    } catch(NegocioException e) {
		addMensagemErro(e.getMessage());
	    } catch(Exception e) {		
		notifyError(e);
	    }
	    return null;
	}

	/**
	 * Método que informa o Edital de Monitoria
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return the edital
	 */
	public EditalMonitoria getEdital() {
		return edital;
	}

	/**
	 * Método que seta o Edital de Monitoria 
	 * @param edital
	 */
	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}

	/**
	 * Métodos que informa a lista de projetos
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
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
	 * 		<li>Não é chamdo por JSP(s)</li>
	 * </ul>
	 * @param projetos the projetos to set
	 */
	public void setProjetos(List<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}
	
}
