/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;

import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;

public class ProcessamentoInscricaoFiscalDao extends GenericSigaaDAO {

	public int findCountColegiosAProcessar(int idProcessoSeletivo) {
		return getJdbcTemplate().queryForInt("select count(*) "
				+ " from vestibular.local_aplicacao_prova_processo_seletivo"
				+ " inner join vestibularprocesso_seletivo using (id_processo_seletivo)"
				+ " inner join vestibular.local_aplicacao_prova using (id_local_aplicacao_prova)");
	}

	public Collection<LocalAplicacaoProva> findColegiosAProcessar(int idProcessoSeletivoVestibular) {
		return null;
	}

}
