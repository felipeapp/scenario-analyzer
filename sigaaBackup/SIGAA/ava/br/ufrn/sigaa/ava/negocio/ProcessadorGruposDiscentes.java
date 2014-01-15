/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/09/2010
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para gerenciar os grupos de discentes de uma turma virtual.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorGruposDiscentes extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoGrupoDiscentes personalMov = (MovimentoGrupoDiscentes) mov;
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(personalMov);
			
			for (GrupoDiscentes g : personalMov.getGrupos()){
				
				// Cria o novo grupo
				if (g.getId() == 0){
					g.setTurma(personalMov.getTurma());
					dao.create(g);
					
				// Desativa o grupo alterado e cria um novo para manter o histórico.
				} else if (g.isAlterado()){
					GrupoDiscentes g2 = new GrupoDiscentes();
					g2.setTurma(personalMov.getTurma());
					g2.setNome(g.getNome());
					
					List <Discente> discentes = new ArrayList <Discente> ();
					for (Discente d : g.getDiscentes())
						discentes.add(d);
					
					g2.setDiscentes(discentes);
					
					// Salva a cópia
					dao.create(g2);
					
					// Desativa o grpo antigo.
					dao.updateField(GrupoDiscentes.class, g.getId(), "ativo", "falseValue()");
					
					dao.updateField(GrupoDiscentes.class, g2.getId(), "grupoPai", g);
				}
			}
			
			for (GrupoDiscentes g : personalMov.getGruposARemover()){
				g.setAtivo(false);
				dao.createOrUpdate(g);
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}