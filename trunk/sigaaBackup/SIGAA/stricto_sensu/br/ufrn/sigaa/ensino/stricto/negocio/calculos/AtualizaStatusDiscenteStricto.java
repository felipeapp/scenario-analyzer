/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Verifica se o status do discente deve ser alterado, se necessário já atualiza.
 * 
 * @author David Pereira
 *
 */
public class AtualizaStatusDiscenteStricto extends CalculosDiscenteChainNode<DiscenteStricto> {

	@Override
	public void processar(DiscenteStricto d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov.getUsuarioLogado());
		
		Collection<MatriculaComponente> defesas = dao.findAtividades(d, new TipoAtividade(TipoAtividade.TESE), SituacaoMatricula.APROVADO);

		// Identifica se o discente possui defesas aprovadas 
		boolean cumpriuDefesas = !isEmpty(defesas);
		
		try {
			
			if( d.isAtivo() && cumpriuDefesas ){
				// se o discente estiver com o status ATIVO e já tiver cumprido a atividade de defesa então ele deve ir para o status DEFENDIDO
				dao.updateField(Discente.class, d.getId(), "status", StatusDiscente.DEFENDIDO);
			} else if( d.isDefendido() && !cumpriuDefesas ){
				// se o discente estiver com o status DEFENDIDO e não tiver cumprido a atividade de defesa então ele deve ir para o status ATIVO
				dao.updateField(Discente.class, d.getId(), "status", StatusDiscente.ATIVO);
			}
			
		} finally{
			dao.close();
		}
	}
	
}
