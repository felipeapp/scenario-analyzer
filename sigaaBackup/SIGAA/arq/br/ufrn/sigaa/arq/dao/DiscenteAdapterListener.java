/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/03/2010
 */
package br.ufrn.sigaa.arq.dao;

import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;

import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.stricto.dominio.RenovacaoAtividadePos;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;

/**
 * Interceptor utilizado para corrigir as instâncias de {@link DiscenteAdapter}
 * em classes de domínio que possuem {@link DiscenteAdapter} como atributo.
 * O atributo deve ser mudado para o tipo {@link Discente}.
 * 
 * @author David Pereira
 *
 */
public class DiscenteAdapterListener extends DefaultSaveOrUpdateEventListener {

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
		corrigirDiscenteAdapter(event.getObject());
	}
	
	/**
	 * Método para correção dependendo do tipo de Discente. 
	 * @param entity
	 */
	private void corrigirDiscenteAdapter(Object entity) {
		if (entity instanceof MatriculaComponente) {
			MatriculaComponente matricula = (MatriculaComponente) entity;
			matricula.setDiscente(matricula.getDiscente().getDiscente());
		} else if (entity instanceof Fiscal) {
			Fiscal fiscal = (Fiscal) entity;
			if (fiscal.getDiscente() != null)
				fiscal.setDiscente(fiscal.getDiscente().getDiscente());
		} else if (entity instanceof MovimentacaoAluno) {
			MovimentacaoAluno mov = (MovimentacaoAluno) entity;
			mov.setDiscente(mov.getDiscente().getDiscente());
		} else if (entity instanceof InscricaoFiscal) {
			InscricaoFiscal inscricaoFiscal = (InscricaoFiscal) entity;
			if (inscricaoFiscal.getDiscente() != null) 
				inscricaoFiscal.setDiscente(inscricaoFiscal.getDiscente().getDiscente());
		} else if (entity instanceof Estagiario) {
			Estagiario e = (Estagiario) entity;
			e.setDiscente(e.getDiscente().getDiscente());
		} else if (entity instanceof RenovacaoAtividadePos) {
			RenovacaoAtividadePos renovacao = (RenovacaoAtividadePos) entity;
			renovacao.setDiscente(renovacao.getDiscente().getDiscente());
		} else if (entity instanceof AvaliadorCIC) {
			AvaliadorCIC avaliador = (AvaliadorCIC) entity;
			if( avaliador.getDiscente()!= null ){
				avaliador.setDiscente(avaliador.getDiscente().getDiscente());
			}
				
		}
	}

}
