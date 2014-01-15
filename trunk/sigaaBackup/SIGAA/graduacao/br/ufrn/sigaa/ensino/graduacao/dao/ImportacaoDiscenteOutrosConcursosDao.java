/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/01/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.LinhaImpressaoDocumentosConvocados;

/** Classe responsável por consultas específicas no processo de importação de discentes aprovados em concursos externos como,
 * por exemplo, o SiSU.
 * @author Édipo Elder F. de Melo
 *
 */
public class ImportacaoDiscenteOutrosConcursosDao extends GenericSigaaDAO {

	/** Retorna uma coleção com todas matrizes curriculares que tiveram discentes importados em um processo de importação especificado.
	 * @param idImportacao ID do processo de importação.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findAllMatrizesByImportacao(int idImportacao) throws HibernateException, DAOException {
		String hql = "select distinct matrizCurricular" +
				" from ImportacaoDiscenteOutrosConcursos importacao" +
				" inner join importacao.discentesCadastrados discentes" +
				" inner join discentes.matrizCurricular matrizCurricular" +
				" where importacao.id = :idImportacao";
		Query q = getSession().createQuery(hql);
		q.setInteger("idImportacao", idImportacao);
		@SuppressWarnings("unchecked")
		List<MatrizCurricular> lista = q.list();
		// orderna por município e curso
		Comparator<MatrizCurricular> comparator = new Comparator<MatrizCurricular>() {
			@Override
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				int cmp = o1.getCurso().getMunicipio().getNome().compareTo(o2.getCurso().getMunicipio().getNome());
				if (cmp == 0)
					cmp = o1.getCurso().getNome().compareTo(o2.getCurso().getNome());
				return cmp;
			}
		};
		Collections.sort(lista, comparator);
		return lista;
	}

	/** Retorna uma coleção de dados a serem utilizados na geração de documentos para o cadastramento de discentes importados.
	 * @param idImportacao
	 * @param idsMatrizCurricular
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<LinhaImpressaoDocumentosConvocados> findAllImportados(int idImportacao, List<Integer> idsMatrizCurricular) throws HibernateException, DAOException {
		String projecao = "convocacao.curso, " +
				"convocacao.habilitacao, " +
				"convocacao.descricao, " +
				"convocacao.sigla, " +
				"convocacao.matricula, " +
				"convocacao.nome, " +
				"convocacao.endereco, " +
				"convocacao.enderecoNumero, " +
				"convocacao.enderecoComplemento, " +
				"convocacao.enderecoBairro, " +
				"convocacao.cep, " +
				"convocacao.cidade, " +
				"convocacao.estado, " +
				"convocacao.tel, " +
				"convocacao.email, " +
				"convocacao.celular, " +
				"convocacao.dataNascimento, " +
				"convocacao.cidadeNascimento, " +
				"convocacao.estadoNascimento, " +
				"convocacao.paisNascimento, " +
				"convocacao.sexo, " +
				"convocacao.estadoCivil, " +
				"convocacao.nomePai, " +
				"convocacao.nomeMae, " +
				"convocacao.cpf, " +
				"convocacao.rg, " +
				"convocacao.orgaoRG, " +
				"convocacao.estadoRG, " +
				"convocacao.tituloEleitorNumero, " +
				"convocacao.tituloEleitorSecao, " +
				"convocacao.tituloEleitoZona, " +
				"convocacao.tituloEleitoEstado, " +
				"convocacao.nomeEscola, " +
				"convocacao.anoConclusao, " +
				"convocacao.semestreAprocacao, " +
				"convocacao.municipioCurso, " +
				"convocacao.concurso, " +
				"convocacao.semestreMaximoConclusao, " +
				"convocacao.matrizCurricular";
		String campos = " c.nome as col_01," +
				" ha.nome as col_02," +
				" ga.descricao as col_03," +
				" t.sigla as col_04," +
				" d.matricula as col_05," +
				" pv.nome as col_06," +
				" en.logradouro as col_07," +
				" en.numero as col_08," +
				" en.complemento as col_09," +
				" en.bairro as col_00," +
				" en.cep as col_11," +
				" m.nome as col_12," +
				" uf.sigla as col_13," +
				" pv.telefone_fixo as col_14," +
				" pv.email as col_15," +
				" pv.telefone_celular as col_16," +
				" pv.data_nascimento as col_17," +
				" mNascimento.nome as col_18," +
				" uf2.sigla as col_19," +
				" p.nome as col_20," +
				" pv.sexo as col_21," +
				" ec.descricao as col_22," +
				" pv.nome_pai as col_23," +
				" pv.nome_mae as col_24," +
				" pv.cpf_cnpj as col_25," +
				" pv.numero_identidade as col_26," +
				" pv.orgao_expedicao_identidade as col_27," +
				" uf3.sigla as col_28," +
				" pv.numero_titulo_eleitor as col_29," +
				" pv.secao_titulo_eleitor as col_30," +
				" pv.zona_titulo_eleitor as col_31," +
				" uf4.sigla as col_32," +
				" pv.segundograuescola as col_33," +
				" pv.segundograuanoconclusao as col_34," +
				" d.periodo_ingresso as col_35," +
				" m2.nome as col_36," +
				" idoc.descricao as col_37," +
				" curr.semestre_conclusao_maximo as col_38," +
				" mc.id_matriz_curricular as col_39";
		StringBuilder sql = new StringBuilder("SELECT distinct ").append(campos)
			.append(" from graduacao.importacao_discente_outros_concurso idoc" +
				" inner join graduacao.discentes_importados_outros_concursos using (id_importacao_discente_outros_concurso)" +
				" inner join discente d using (id_discente) " +
				" inner join graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
				" inner join graduacao.curriculo curr ON (d.id_curriculo = curr.id_curriculo) " +
				" inner join graduacao.matriz_curricular mc on (dg.id_matriz_curricular = mc.id_matriz_curricular) " +
				" inner join curso c ON (mc.id_curso = c.id_curso) " +
				" inner join ensino.turno t ON (mc.id_turno = t.id_turno) " +
				" left join ensino.grau_academico ga ON (mc.id_grau_academico = ga.id_grau_academico) " +
				" left join graduacao.habilitacao ha ON (mc.id_habilitacao = ha.id_habilitacao) " +
				" left join comum.pessoa pv ON (d.id_pessoa = pv.id_pessoa) " +
				" left join comum.endereco en ON (en.id_endereco = pv.id_endereco_contato) " +
				" left join comum.municipio m ON (en.id_municipio = m.id_municipio) " +
				" left join comum.municipio mNascimento ON (pv.id_municipio_naturalidade = mNascimento.id_municipio) " +
				" left join comum.unidade_federativa uf ON (uf.id_unidade_federativa = m.id_unidade_federativa) " +
				" left join comum.unidade_federativa uf2 ON (uf2.id_unidade_federativa = pv.id_uf_naturalidade) " +
				" left join comum.pais p ON (uf2.id_pais = p.id_pais) " +
				" left join comum.estado_civil ec ON (pv.id_estado_civil = ec.id_estado_civil) " +
				" left join comum.unidade_federativa uf3 ON (uf3.id_unidade_federativa = pv.id_uf_identidade) " +
				" left join comum.unidade_federativa uf4 ON (uf4.id_unidade_federativa = pv.id_uf_titulo_eleitor) " +
				" left join comum.municipio m2 ON (c.id_municipio = m2.id_municipio) " +
				" WHERE idoc.id_importacao_discente_outros_concurso = :idImportacao");
 	if (!isEmpty(idsMatrizCurricular))
 		sql.append(" AND mc.id_matriz_curricular in ")
 		.append(UFRNUtils.gerarStringIn(idsMatrizCurricular));
 	sql.append( " order by m2.nome, c.nome, ha.nome, ga.descricao, t.sigla, d.periodo_ingresso, pv.nome");
	
	Query q = getSession().createSQLQuery(sql.toString());
	q.setInteger("idImportacao", idImportacao);
	@SuppressWarnings("unchecked")
	List<Object[]> bulk = q.list();
	Collection<LinhaImpressaoDocumentosConvocados> lista = HibernateUtils.parseTo(bulk, projecao, LinhaImpressaoDocumentosConvocados.class, "convocacao");
	return lista;
	}

	/** Retorna uma coleção de discentes que foram importados em um processo de importação e (opcionalmente) em uma matriz curricular.
	 * @param idImportacao ID do processo de importação (obrigatório)
	 * @param idMatrizCurricular ID da matriz curricular dos discentes (opcional, caso seja zero não restringirá a consulta)
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesImportados(int idImportacao, int idMatrizCurricular) throws HibernateException, DAOException {
		StringBuilder projecao = new StringBuilder("d.id, d.discente.id, d.discente.pessoa.id, d.discente.pessoa.nome," +
				" d.discente.anoIngresso, d.discente.periodoIngresso, d.matrizCurricular.id," +
				" d.discente.status, d.discente.matricula");
		StringBuilder hql = new StringBuilder("select ").append(projecao).append(
				" from ImportacaoDiscenteOutrosConcursos importacao" +
				" inner join importacao.discentesCadastrados d" +
				" inner join d.matrizCurricular matrizCurricular" +
				" where importacao.id = :idImportacao");
		if (idMatrizCurricular > 0)
			hql.append(" and d.matrizCurricular.id = :idMatrizCurricular");
		hql.append(" order by d.discente.pessoa.nomeAscii");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idImportacao", idImportacao);
		if (idMatrizCurricular > 0)
			q.setInteger("idMatrizCurricular", idMatrizCurricular);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao.toString(), DiscenteGraduacao.class, "d");
	}
	
	/** Retorna valores para o suggestionBox usar para fazer equivalência entre valores importados e dados do banco.
	 * @param classe
	 * @param field
	 * @param valor
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<PersistDB> findSuggestionEquivalencia(Class<PersistDB> classe, String field, String valor, boolean somenteAtivos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select obj from ")
			.append(classe.getName())
			.append(" obj where upper(")
			.append(field)
			.append(") like upper(:valor)");
		if (somenteAtivos)
			hql.append(" and obj.ativo = true");
		hql.append(" order by ").append(field);
		Query q = getSession().createQuery(hql.toString());
		q.setString("valor", valor);
		@SuppressWarnings("unchecked")
		Collection<PersistDB> lista = q.list();
		return lista;
	}
}
