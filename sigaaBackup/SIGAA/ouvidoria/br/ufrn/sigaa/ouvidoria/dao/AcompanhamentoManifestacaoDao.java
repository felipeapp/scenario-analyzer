package br.ufrn.sigaa.ouvidoria.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.AcompanhamentoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas em {@link AcompanhamentoManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class AcompanhamentoManifestacaoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna uma lista de acompanhamentos de acordo com a manifestação passada.
	 * 
	 * @param idManifestacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoManifestacao> findAllAcompanhamentosByManifestacao(int idManifestacao) throws DAOException {
		String hql = "select ac " +
						"from AcompanhamentoManifestacao ac " +
							"inner join ac.manifestacao m " +
						"where m.id = :manifestacao " +
							"and ac.ativo = " + SQLDialect.TRUE;
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("manifestacao", idManifestacao);
		
		@SuppressWarnings("unchecked")
		Collection<AcompanhamentoManifestacao> acompanhamentos = q.list();
		
		popularResponsaveisUnidades(acompanhamentos);
		
		return acompanhamentos;
	}
	
	/**
	 * Método auxiliar para popular os dados da responsabilidade pelas unidades 
	 * responsáveis pelos acompanhamentos encontrados via {@link #findAllAcompanhamentosByManifestacao(int)}.
	 * 
	 * @param acompanhamentos
	 */
	private void popularResponsaveisUnidades(Collection<AcompanhamentoManifestacao> acompanhamentos) {
		Collection<Integer> unidades = new ArrayList<Integer>();
		
		for (AcompanhamentoManifestacao acompanhamento : acompanhamentos) {
			if(isNotEmpty(acompanhamento.getUnidadeResponsabilidade()))
				unidades.add(acompanhamento.getUnidadeResponsabilidade().getId());
		}

		if(unidades.isEmpty())
			return;
		
		String sql = "SELECT p.nome, ru.id_unidade as unidade " +
						"FROM comum.responsavel_unidade ru " +
							"inner join comum.unidade using (id_unidade) " +
							"inner join rh.servidor using (id_servidor) " +
							"inner join comum.pessoa p using (id_pessoa) " +
						"WHERE ru.id_unidade in " + UFRNUtils.gerarStringIn(unidades) + " " +
							"and ru.id_registro_entrada_exclusao is null " + 
							"and ru.nivel_responsabilidade = ? " +
							"and ru.data_inicio <= current_date " +
							"and (ru.data_fim IS NULL OR ru.data_fim >= current_date)" +
						"ORDER BY ru.data_cadastro desc";

		Object[] lista = {String.valueOf(NivelResponsabilidade.CHEFE) };

		try{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = getJdbcTemplate(Sistema.SIPAC).queryForList(sql, lista);
			
			for (Map<String, Object> linha : list) {
				int unidade = (Integer) linha.get("unidade");
				
				for (AcompanhamentoManifestacao acompanhamento : acompanhamentos) {
					if(isNotEmpty(acompanhamento.getUnidadeResponsabilidade()) && acompanhamento.getUnidadeResponsabilidade().getId() == unidade && acompanhamento.getPessoa() == null) {
						acompanhamento.setPessoa(new Pessoa());
						acompanhamento.getPessoa().setNome((String) linha.get("nome"));
					}
				}
			}
		}catch (EmptyResultDataAccessException e) {
		}

	}
}
