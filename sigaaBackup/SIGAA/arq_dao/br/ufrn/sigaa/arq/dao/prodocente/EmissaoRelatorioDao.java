/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;

public class EmissaoRelatorioDao extends GenericSigaaDAO {


	public Collection<EmissaoRelatorio> findRankingByClassificacao(ClassificacaoRelatorio classificacaoRelatorio, Integer tipoUnidadeAcademica) throws DAOException{
		return findRankingByClassificacao(classificacaoRelatorio, tipoUnidadeAcademica, null);
	}

	@SuppressWarnings("unchecked")
	public Collection<EmissaoRelatorio> findRankingByClassificacao(ClassificacaoRelatorio classificacaoRelatorio, Integer tipoUnidadeAcademica, Integer idCentro) throws DAOException{

		// Dados da projeção
		String projecao = "id, servidor.id, servidor.pessoa.nome, " +
				"servidor.unidade.id, servidor.unidade.sigla, servidor.unidade.nome, " +
				"servidor.unidade.gestora.id, servidor.unidade.gestora.sigla, servidor.unidade.gestora.nome, " +
				"ipi, fppi, ipiOriginal, motivoAlteracaoIPI, classificacaoRelatorio.id";

		StringBuilder hql = new StringBuilder();
		hql.append(" select " + projecao);
		hql.append(" from EmissaoRelatorio where classificacaoRelatorio.id = " + classificacaoRelatorio.getId());

		if(idCentro != null)
			hql.append(" and servidor.unidade.gestora.id = " + idCentro);

		hql.append(" order by ");
		if (tipoUnidadeAcademica != null) {
			if (tipoUnidadeAcademica == TipoUnidadeAcademica.DEPARTAMENTO) {
				hql.append(" servidor.unidade.gestora.codigo, servidor.unidade.nome, ");
			}
			if (tipoUnidadeAcademica == TipoUnidadeAcademica.CENTRO) {
				hql.append(" servidor.unidade.gestora.codigo, ");
			}
		}
		hql.append(" fppi desc, ipi desc ");

		List<Object[]> result = getSession().createQuery(hql.toString()).list();
		Collection<EmissaoRelatorio> ranking = new ArrayList<EmissaoRelatorio>();
		for (Object[] linha : result) {
			int i = 0;
			EmissaoRelatorio emissao = new EmissaoRelatorio();
			emissao.setId( (Integer) linha[i++] );

			Servidor servidor = new Servidor((Integer) linha[i++]);
			emissao.setServidor(servidor);
			servidor.getPessoa().setNome( (String) linha[i++] );

			Unidade unidade = new Unidade();
			Unidade gestora = new Unidade();
			unidade.setGestora(gestora);
			servidor.setUnidade(unidade);
			
			unidade.setId((Integer) linha[i++]);
			unidade.setSigla((String) linha[i++]);
			unidade.setNome((String) linha[i++]);
			
			gestora.setId((Integer) linha[i++]);
			gestora.setSigla((String) linha[i++]);
			gestora.setNome((String) linha[i++]);

			emissao.setIpi((Double) linha[i++]);
			emissao.setFppi((Double) linha[i++]);
			emissao.setIpiOriginal((Double) linha[i++]);
			emissao.setMotivoAlteracaoIPI((String) linha[i++]);
			emissao.setClassificacaoRelatorio(new ClassificacaoRelatorio());
			emissao.getClassificacaoRelatorio().setId((Integer) linha[i++]);

			ranking.add(emissao);
		}

		return ranking;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> findFppiMaxCentroByClassificacao(ClassificacaoRelatorio classificacao) throws DAOException {
		Map<Integer, Double> medias = new HashMap<Integer, Double>();

		try {

			StringBuilder hql =  new StringBuilder();
			hql.append("SELECT servidor.unidade.gestora.id as centro, " +
				 		" max( fppi ) as maxfppi ");
			hql.append(" FROM EmissaoRelatorio ");
			hql.append(" WHERE classificacaoRelatorio.id = " + classificacao.getId() );

			hql.append(" GROUP BY servidor.unidade.gestora.id ");

			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			for (Object[] object : lista) {
				medias.put((Integer) object[0], (Double) object[1] != null ? (Double) object[1] : 0);
			}

			return medias;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * @param classificacao
	 */
	public void atualizarMedias(ClassificacaoRelatorio classificacao) {
		update("update prodocente.emissao_relatorio " +
			"set fppi = cast( (ipi - ( " +
				" select ipi_medio from prodocente.emissao_relatorio_media erm,  rh.servidor s, comum.unidade u" +
				" where prodocente.emissao_relatorio.id_servidor = s.id_servidor " +
				" and s.id_unidade = u.id_unidade" +
				" and u.id_gestora = erm.id_unidade" +
				" and erm.id_classificacao_relatorio = ?" +
			" )) / (" +
			" select ipi_desvpad from prodocente.emissao_relatorio_media erm,  rh.servidor s, comum.unidade u" + 
			" where prodocente.emissao_relatorio.id_servidor = s.id_servidor " + 
			" and s.id_unidade = u.id_unidade" + 
			" and u.id_gestora = erm.id_unidade" + 
			" and erm.id_classificacao_relatorio = ?" + 
			" ) as numeric(20,2) )" +
			" where id_classificacao_relatorio = ?",
			new Object[] { classificacao.getId(), classificacao.getId(), classificacao.getId() } );
	}


}
