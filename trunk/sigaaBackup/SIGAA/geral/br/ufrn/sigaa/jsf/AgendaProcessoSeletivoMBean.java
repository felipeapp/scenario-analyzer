/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '08/04/2009'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.graduacao.AgendaProcessoSeletivoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;

/**
 * Managed bean para cadastro de agendas do processo seletivo de transfer�ncia volunt�ria.
 * 
 * @author M�rio Rizzi
 * 
 */
@Component("agendaProcessoSeletivo") @Scope("session") 
public class AgendaProcessoSeletivoMBean extends SigaaAbstractController<AgendaProcessoSeletivo> {


	
	/** Construtor padr�o. */
	public AgendaProcessoSeletivoMBean() {

	}
	
	public Collection<AgendaProcessoSeletivo> getAllDisponiveis(Integer id) throws ArqException{
		all = null;	
		if(getUsuarioLogado()!=null && isPortalGraduacao())
				all = getGenericDAO().findByExactField(AgendaProcessoSeletivo.class, "editalProcessoSeletivo.id", id);
			else
				all = getDAO(AgendaProcessoSeletivoDao.class).findAllDisponiveisByEdital(id);
				
		return all;
	}
	
	public  Collection<SelectItem> getDatasAgendamento(){
		
		List<SelectItem> datasAgendamento = toSelectItems(all, "id", "dataAgendaString");
		return datasAgendamento;
		
	}
	

}