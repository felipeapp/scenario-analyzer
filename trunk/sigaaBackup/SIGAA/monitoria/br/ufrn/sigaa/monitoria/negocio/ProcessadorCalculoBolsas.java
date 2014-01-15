/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 * 
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoComparator;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador para cálculo do número de bolsas concedidas por projeto.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorCalculoBolsas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	    	validate(mov);
		CalculoBolsasMov cMov = (CalculoBolsasMov) mov;
		List<ProjetoEnsino> projetos = cMov.getProjetos();
		EditalMonitoria edital = cMov.getEdital();		
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class,mov);
		
		
		try {
		
			// Calculo da média final por projeto
			for (ProjetoEnsino projeto : projetos) {
				projeto.setNumProfessores(dao.findNumProfessoresByProjeto(projeto));
				projeto.setNumDepartamentos(dao.findNumDepartamentosByProjeto(projeto));
				projeto.setNumComponentesCurriculares(dao.findNumComponentesCurriculares(projeto));				
				projeto.setRt(dao.findRtByProjeto(projeto));
				projeto.calculaMedia();
			}
		
			Collections.sort(projetos, new ProjetoComparator());
		
			Integer totalBolsas = Integer.valueOf(edital.getNumeroBolsas());
			double icb = (edital.getNumeroBolsas() * 1.0) / getTotalBolsasSolicitadas(projetos);
			
			if (icb >= 1) {
				// Se o numero de bolsas solicitadas for menor que o total, todos recebem
				for (ProjetoEnsino projeto : projetos) {
					totalBolsas -= projeto.concederTodasBolsas();
				}
			} else {
				for (ProjetoEnsino projeto : projetos) {
					if (totalBolsas > 0)
						totalBolsas -= projeto.concederBolsaProporcionalIcb(icb, totalBolsas);
				}
			}
			
			if (totalBolsas > 0) {
				for (ProjetoEnsino projeto : projetos) {
					if (projeto.getBolsasSolicitadas() > projeto.getBolsasConcedidas()) {
						projeto.adicionarBolsa(1);
						if (--totalBolsas == 0)
							break;
					}
				}
			}
			
			int classificacao = 1;
			for (ProjetoEnsino pm : projetos) {
				dao.updateFields(Projeto.class, pm.getProjeto().getId(), new String[] {"bolsasSolicitadas", "bolsasConcedidas", "media", "classificacao"}, 
						new Object[] {pm.getBolsasSolicitadas(), pm.getBolsasConcedidas(), pm.getMediaFinal(), classificacao++});
				
				dao.updateField(ProjetoEnsino.class, pm.getId(), "bolsasConcedidas", pm.getBolsasConcedidas());				
			}
			
		} finally {
			dao.close();
		}
		
		return projetos;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	    ListaMensagens erros = new ListaMensagens();
	    List<ProjetoEnsino> projetos = ((CalculoBolsasMov) mov).getProjetos();
	    for (ProjetoEnsino projeto : projetos) {
		if (projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_RECOMENDADO) {
		    erros.addErro("Projeto: " + projeto.getAnoTitulo() + " está com a situação '" + projeto.getSituacaoProjeto().getDescricao() 
			    + "' e não pode participar da distribuição de bolsas.");
		    break;
		}
	    }
	    checkValidation(erros);
	}

	
	/**
	 * Retorna o total de bolsas solicitadas de todos os projetos
	 * que vão participar da distribuição das bolsas do edital.
	 * 
	 * @param projetos
	 * @return
	 */
	private int getTotalBolsasSolicitadas(List<ProjetoEnsino> projetos) {
		int total = 0;
		for (ProjetoEnsino projeto : projetos) {
			total += projeto.getBolsasSolicitadas();
		}
		return total;
	}
	
}