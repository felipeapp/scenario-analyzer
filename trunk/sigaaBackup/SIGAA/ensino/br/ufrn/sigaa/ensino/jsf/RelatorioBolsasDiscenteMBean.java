/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 05/02/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.BolsistaDao;
import br.ufrn.sigaa.arq.dao.sae.ConfiguracoesPeriodoResultadoDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * ManagedBean responsável pela emissão de relatórios de bolsas de discentes
 * 
 * @author wendell
 *
 */
@Component("relatorioBolsasDiscenteBean") @Scope("request")
public class RelatorioBolsasDiscenteMBean extends SigaaAbstractController<Bolsista> {

	Collection<Bolsista> bolsas;
	
	/**
	 * Listar as bolsas pagas pela instituição, nas quais o discente participou 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarBolsasInstituicao() throws DAOException {
		if ( isEmpty(getDiscenteUsuario()) ) {
			addMensagemErro("Somente discentes tem acesso a esta consulta");
			return null;
		}
		
		BolsistaDao dao = getDAO(BolsistaDao.class);
		Collection<Bolsista> allBolsasLocalizadas = dao.findByDiscente( getDiscenteUsuario() );
		bolsas = new ArrayList<Bolsista>();
		
		for (Bolsista bolsista : allBolsasLocalizadas)
			if ( verificarPeriodoResultados(bolsista.getIdTipoBolsa(), bolsista.getDiscente()) )
				bolsas.add(bolsista);		
		
		if (isEmpty(bolsas)) {
			addMensagemWarning("Não foram encontradas bolsas remuneradas pela instituição para este vínculo.");
			return null;
		}
		
		return forward("/ensino/relatorios/bolsas_instituicao.jsp");
	}
	
	/**
     * Verifica se o SAE autorizou a exibição do resultado do processo seletivo para os alunos de 
     * determinado tipo de bolsa/município.
     * 
     * @param idTipoBolsa
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    private boolean verificarPeriodoResultados(int idTipoBolsa, Discente obj) throws HibernateException, DAOException {
    	
    	//converte idTipoBolsa do SIPAC para valor equivalente no SIGAA
    	boolean eBolsaSAE = false;
    	if (idTipoBolsa == TipoBolsa.BOLSA_AUXILIO_ALIMENTACAO) {
    		idTipoBolsa = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ALIMENTACAO );
    		eBolsaSAE = true;
    	}
    	if (idTipoBolsa == TipoBolsa.BOLSA_AUXILIO_RESIDENCIA_GRADUACAO) {
    		idTipoBolsa = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.RESIDENCIA_GRADUACAO );
    		eBolsaSAE = true;
    	}
    	if (idTipoBolsa == TipoBolsa.BOLSA_AUXILIO_RESIDENCIA_POS) {
    		idTipoBolsa = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.RESIDENCIA_POS );
    		eBolsaSAE = true;
    	}
    		
    	// apenas bolsas do SAE devem ser verificadas. outras bolsas podem ser exibidas para discentes a qualquer momento
    	if (eBolsaSAE) {
	        List<CalendarioBolsaAuxilio> periodoResultados = getDAO(ConfiguracoesPeriodoResultadoDAO.class).verificarResultadoProcessoSeletivo(idTipoBolsa);
	        Discente discente = getDAO(DiscenteDao.class).findByMatricula(obj.getMatricula());
	        
	        if (discente != null) {
	        	for (CalendarioBolsaAuxilio it : periodoResultados)
	                if (discente.getCurso().getMunicipio().getId() == it.getMunicipio().getId()) {
	                	if (CalendarUtils.isDentroPeriodo(it.getInicioDivulgacaoResultado(), it.getFimDivulgacaoResultado()) )
	                		return true;
	                }
	        }
	        return false;
    	}
    	
    	return true;
    }

	public Collection<Bolsista> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<Bolsista> bolsas) {
		this.bolsas = bolsas;
	}
	
}
