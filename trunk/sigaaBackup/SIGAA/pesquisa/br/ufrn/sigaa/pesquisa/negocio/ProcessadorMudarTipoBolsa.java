/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/08/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * Processador responsável por mudar o tipo de bolsa de planos de trabalho
 * @author leonardo
 *
 */
public class ProcessadorMudarTipoBolsa extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		PlanoTrabalho planoNovo = (PlanoTrabalho) mov;
		GenericDAO dao = getDAO(mov);
		PlanoTrabalho plano = dao.findByPrimaryKey(planoNovo.getId(), PlanoTrabalho.class);
		
		// Se existir um discente associado ao plano de trabalho
		if(plano.getMembroProjetoDiscente() !=  null && plano.getMembroProjetoDiscente().getId() > 0){
			
			MembroProjetoDiscente membro = plano.getMembroProjetoDiscente();
			MembroProjetoDiscente membroAntigo = dao.refresh( membro );

			// Finaliza a bolsa anterior
			membroAntigo.setDataFim( new Date() );
			membroAntigo.setDataFinalizacao( new Date() );
			dao.update( membroAntigo );
			
			// Cria nova associação entre o discente e o plano de trabalho
			MembroProjetoDiscente membroNovo = new MembroProjetoDiscente();
			membroNovo.setDiscente( membroAntigo.getDiscente() );
			membroNovo.setBolsistaAnterior(membroAntigo);
			membroNovo.setDataInicio(new Date());
			membroNovo.setDataIndicacao(new Date());
			membroNovo.setPlanoTrabalho(plano);
			membroNovo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			membroNovo.setTipoBolsa( planoNovo.getTipoBolsa() );
			plano.setMembroProjetoDiscente(membroNovo);
		}

		plano.setTipoBolsa( planoNovo.getTipoBolsa() );
		dao.update(plano);
		
		return plano;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
