/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/09/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;

/**
 * 
 * <p>
 * Dao exclusivo para a consulta do relatório.
 * </p>
 * 
 * 
 * @author jadson
 * 
 */
public class RelatorioOcorrenciasPerdaMaterialDao extends
		RelatoriosBibliotecaDao {

	/**
	 * Retorna o quantitativo de ocorrências de perda registradas no sistema.
	 * Utilizado no relatório sintético.
	 * 
	 */
	@SuppressWarnings("null")
	public List<Object[]> countOcorrenciasPerdaMaterial(
			Collection<Integer> idBibliotecas, Date inicioPeriodo,
			Date fimPeriodo) throws DAOException {

		String idsBibliotecaString = " ";
		if (idBibliotecas != null && idBibliotecas.size() > 0)
			idsBibliotecaString = " AND b.id_biblioteca IN "
					+ UFRNUtils.gerarStringIn(idBibliotecas);

		// Conta quantas ocorrências estão em aberto
		String sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca, "
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" WHERE emp.ativo = trueValue() "
				+ "AND emp.situacao = "
				+ Emprestimo.EMPRESTADO // EM ABERTO
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialEmAberto = q.list();

		// Conta quantas ocorrências reposto similar existem
		sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca, "
				// + " devolucao.tipo as tipoDevolucao, "
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" WHERE emp.ativo = trueValue()  "
				+ " AND emp.situacao <> "
				+ Emprestimo.EMPRESTADO
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " AND (devolucao.tipo =  "
				+ TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_SIMILAR // RESPOSTO
																					// SIMILAR
				+ " OR devolucao IS NULL) "
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q2 = getSession().createSQLQuery(sql);
		q2.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q2.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialRespostoSimilar = q2.list();

		// Conta quantas ocorrências reposto equivalente existem
		sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca,"
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" LEFT JOIN biblioteca.material_informacional AS mSubstituior ON mSubstituior.id_material_que_eu_substituo  = mi.id_material_informacional "
				+ " WHERE emp.ativo = trueValue()  "
				+ " AND mSubstituior.id_material_informacional IS NULL " // REPOSTO
																			// EQUIVALENTE
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " AND devolucao.tipo =  "
				+ TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q3 = getSession().createSQLQuery(sql);
		q3.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q3.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialRepostoEquivalente = q3.list();

		// Conta quantas ocorrências substituido existem
		sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca,"
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" LEFT JOIN biblioteca.material_informacional AS mSubstituior ON mSubstituior.id_material_que_eu_substituo  = mi.id_material_informacional "
				+ " WHERE emp.ativo = trueValue()  "
				+ " AND mSubstituior.id_material_informacional IS NOT NULL " // SUBSTITUIDO
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " AND devolucao.tipo =  "
				+ TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q4 = getSession().createSQLQuery(sql);
		q4.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q4.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialSubstituido = q4.list();

		// Conta quantas ocorrências de não resposto existem
		sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca,"
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" WHERE emp.ativo = trueValue()  "
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " AND devolucao.tipo =  "
				+ TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO // NÃO
																						// REPOSTO
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q5 = getSession().createSQLQuery(sql);
		q5.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q5.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialNaoReposto = q5.list();

		// Conta quantas ocorrências de não baixados existem
		sql = " SELECT "
				+ " b.id_biblioteca idBiblioteca, "
				+ " b.descricao as descricaoBiblioteca,"
				+ " COUNT (DISTINCT emp.id_emprestimo ) as qtdComunicacoes "
				+ // conta a quantidade de empréstimos com prorrogação por perda
				" FROM biblioteca.prorrogacao_emprestimo AS pro 	"
				+ " INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"
				+ " INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "
				+ " INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional  = mi.id_situacao_material_informacional "
				+ " LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ // adciona o tipo de devolução
				" WHERE emp.ativo = trueValue()  "
				+ " AND situacao.situacao_de_baixa = falseValue() " // NÃO
																	// BAIXADO
				+ idsBibliotecaString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "
				+ " AND devolucao.tipo =  "
				+ TipoDevolucaoMaterialPerdido.USUARIO_ENTREGOU_MATERIAL_EQUIVALENTE
				+ " GROUP BY b.id_biblioteca, b.descricao "
				+ " ORDER BY b.descricao ";

		Query q6 = getSession().createSQLQuery(sql);
		q6.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q6.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> listaOcorrenciasMaterialNaoBaixado = q6.list();

		// Lista final
		List<Object[]> lista = new ArrayList<Object[]>();

		// array que guarda o resultado de todas as buscas feitas ao banco
		Object[] novaBiblioteca;
		
		// adiciona todas as ocorrências EM ABERTO
		for (Object[] biblioteca : listaOcorrenciasMaterialEmAberto) {
			novaBiblioteca = new Object[8];
			novaBiblioteca[0] = biblioteca[0];
			novaBiblioteca[1] = biblioteca[1];
			novaBiblioteca[2] = biblioteca[2];
			novaBiblioteca[3] = 0;
			novaBiblioteca[4] = 0;
			novaBiblioteca[5] = 0;
			novaBiblioteca[6] = 0;
			novaBiblioteca[7] = 0;
			lista.add(novaBiblioteca);
		}

		boolean existia = false;
		
		// adiciona todas as ocorrências REPOSTO SIMILAR fazendo os ajustes necessários
		for (Object[] bibliotecaSimilar : listaOcorrenciasMaterialRespostoSimilar) {
			for (Object[] biblioteca : lista) {
				if (biblioteca[0].equals(bibliotecaSimilar[0])) {
					biblioteca[3] = bibliotecaSimilar[2];
					existia = true;
				}
			}
			if (!existia) {
				novaBiblioteca = new Object[8];
				novaBiblioteca[0] = bibliotecaSimilar[0];
				novaBiblioteca[1] = bibliotecaSimilar[1];
				novaBiblioteca[2] = 0;
				novaBiblioteca[3] = bibliotecaSimilar[2];
				novaBiblioteca[4] = 0;
				novaBiblioteca[5] = 0;
				novaBiblioteca[6] = 0;
				novaBiblioteca[7] = 0;
				lista.add(novaBiblioteca);
			} else
				existia = false;
		}

		// adiciona todas as ocorrências REPOSTO EQUIVALENTE fazendo os ajustes necessários
		for (Object[] bibliotecaEquivalente : listaOcorrenciasMaterialRepostoEquivalente) {
			for (Object[] biblioteca : lista) {
				if (biblioteca[0].equals(bibliotecaEquivalente[0])) {
					biblioteca[4] = bibliotecaEquivalente[2];
					existia = true;
				}
			}
			if (!existia) {
				novaBiblioteca = new Object[8];
				novaBiblioteca[0] = bibliotecaEquivalente[0];
				novaBiblioteca[1] = bibliotecaEquivalente[1];
				novaBiblioteca[2] = 0;
				novaBiblioteca[3] = 0;
				novaBiblioteca[4] = bibliotecaEquivalente[2];
				novaBiblioteca[5] = 0;
				novaBiblioteca[6] = 0;
				novaBiblioteca[7] = 0;
				lista.add(novaBiblioteca);
			} else
				existia = false;
		}

		// adiciona todas as ocorrências SUBSTITUÍDO fazendo os ajustes necessários
		for (Object[] bibliotecaSubstituido : listaOcorrenciasMaterialSubstituido) {
			for (Object[] biblioteca : lista) {
				if (biblioteca[0].equals(bibliotecaSubstituido[0])) {
					biblioteca[5] = bibliotecaSubstituido[2];
					existia = true;
				}
			}
			if (!existia) {
				novaBiblioteca = new Object[8];
				novaBiblioteca[0] = bibliotecaSubstituido[0];
				novaBiblioteca[1] = bibliotecaSubstituido[1];
				novaBiblioteca[2] = 0;
				novaBiblioteca[3] = 0;
				novaBiblioteca[4] = 0;
				novaBiblioteca[5] = bibliotecaSubstituido[2];
				novaBiblioteca[6] = 0;
				novaBiblioteca[7] = 0;
				lista.add(novaBiblioteca);
			} else
				existia = false;
		}

		// adiciona todas as ocorrências NÃO REPOSTO fazendo os ajustes necessários
		for (Object[] bibliotecaNaoReposto : listaOcorrenciasMaterialNaoReposto) {
			for (Object[] biblioteca : lista) {
				if (biblioteca[0].equals(bibliotecaNaoReposto[0])) {
					biblioteca[6] = bibliotecaNaoReposto[2];
					existia = true;
				}
			}
			if (!existia) {
				novaBiblioteca = new Object[8];
				novaBiblioteca[0] = bibliotecaNaoReposto[0];
				novaBiblioteca[1] = bibliotecaNaoReposto[1];
				novaBiblioteca[2] = 0;
				novaBiblioteca[3] = 0;
				novaBiblioteca[4] = 0;
				novaBiblioteca[5] = 0;
				novaBiblioteca[6] = bibliotecaNaoReposto[2];
				novaBiblioteca[7] = 0;
				lista.add(novaBiblioteca);
			} else
				existia = false;
		}

		// adiciona todas as ocorrências NÃO BAIXADO fazendo os ajustes necessários
		for (Object[] bibliotecaNaoBaixado : listaOcorrenciasMaterialNaoBaixado) {
			for (Object[] biblioteca : lista) {
				if (biblioteca[0].equals(bibliotecaNaoBaixado[0])) {
					biblioteca[7] = bibliotecaNaoBaixado[2];
					existia = true;
				}
			}
			if (!existia) {
				novaBiblioteca = new Object[8];
				novaBiblioteca[0] = bibliotecaNaoBaixado[0];
				novaBiblioteca[1] = bibliotecaNaoBaixado[1];
				novaBiblioteca[2] = 0;
				novaBiblioteca[3] = 0;
				novaBiblioteca[4] = 0;
				novaBiblioteca[5] = 0;
				novaBiblioteca[6] = 0;
				novaBiblioteca[7] = bibliotecaNaoBaixado[2];
				lista.add(novaBiblioteca);
			} else
				existia = false;
		}

		return lista;
	}

	/**
	 * Retorna a listagem de ocorrências de perda registradas no sistema.
	 * Utilizado no relatório analítico.
	 * 
	 */
	public List<Object[]> listaOcorrenciasPerdaMaterial(
			Collection<Integer> idBibliotecas, Date inicioPeriodo,
			Date fimPeriodo) throws DAOException {

		String sqlBibliotecasString = " ";
		if (idBibliotecas != null && idBibliotecas.size() > 0)
			sqlBibliotecasString = " AND bibliotecaMaterial.id_biblioteca IN "
					+ UFRNUtils.gerarStringIn(idBibliotecas);

		String sql = "SELECT emp.id_emprestimo,  "
				+ "  mi.codigo_barras, si.descricao situacao, si.situacao_de_baixa, tit.id_titulo_catalografico, mi.informacoes_titulo_material_baixado, bibliotecaMaterial.descricao biblioteca"
				+ ", devolucao.tipo, devolucao.motivo_nao_entrega_material "
				+ ", COALESCE( pessoaPerdeuMaterial.nome, bibliotecaPerdeuMaterial.descricao  ) AS nomeUsuario "
				+ ", matSubstituidor.codigo_barras AS substituidor "
				+ ", COALESCE( pessoaDevolucao.nome, pessoaDevolucao2.nome) as operadorDevolucao, COALESCE (devolucao.data_criacao, emp.data_devolucao ) as dataDevolucao "
				+ ", pro.data_anterior, pro.data_atual, pessoaCadatroProrrogacao.nome operadorComunicacao, pro.motivo "
				+

				" FROM biblioteca.emprestimo AS emp  "
				+

				" INNER JOIN biblioteca.prorrogacao_emprestimo AS pro ON emp.id_emprestimo = pro.id_emprestimo "
				+

				" LEFT JOIN biblioteca.devolucao_material_perdido AS devolucao  ON emp.id_emprestimo = devolucao.id_emprestimo "
				+ " INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional "
				+ " INNER JOIN biblioteca.situacao_material_informacional AS si ON si.id_situacao_material_informacional = mi.id_situacao_material_informacional "
				+ " INNER JOIN biblioteca.biblioteca AS bibliotecaMaterial ON mi.id_biblioteca = bibliotecaMaterial.id_biblioteca "
				+

				// Informação de quem perdeu o material //
				" INNER JOIN biblioteca.usuario_biblioteca usu ON emp.id_usuario_biblioteca = usu.id_usuario_biblioteca "
				+ " LEFT  JOIN comum.pessoa pessoaPerdeuMaterial ON pessoaPerdeuMaterial.id_pessoa = usu.id_pessoa 	"
				+ " LEFT  JOIN biblioteca.biblioteca bibliotecaPerdeuMaterial ON bibliotecaPerdeuMaterial.id_biblioteca = usu.id_biblioteca 	"
				+

				// Informação de quem criou a comunicação //
				" INNER JOIN comum.registro_entrada entradaProrrogacao ON entradaProrrogacao.id_entrada = pro.id_registro_cadastro "
				+ " INNER JOIN comum.usuario usuarioCadatroProrrogacao ON usuarioCadatroProrrogacao.id_usuario = entradaProrrogacao.id_usuario "
				+ " INNER JOIN comum.pessoa pessoaCadatroProrrogacao ON pessoaCadatroProrrogacao.id_pessoa = usuarioCadatroProrrogacao.id_pessoa "
				+

				// informação de quem devolveu o emprestimo material perdido //
				" LEFT JOIN comum.registro_entrada entradaDevolucao ON entradaDevolucao.id_entrada = devolucao.id_registro_criacao "
				+ " LEFT  JOIN comum.usuario usuarioDevolucao ON usuarioDevolucao.id_usuario = entradaDevolucao.id_usuario "
				+ " LEFT  JOIN comum.pessoa pessoaDevolucao ON pessoaDevolucao.id_pessoa = usuarioDevolucao.id_pessoa "
				+

				// informação de quem devolveu o emprestimo material perdido,
				// para empréstimos realizandos antes de ser salvo a informação
				// da devolução //
				" LEFT  JOIN comum.usuario usuarioDevolucao2 ON usuarioDevolucao2.id_usuario = emp.id_usuario_realizou_devolucao "
				+ " LEFT  JOIN comum.pessoa pessoaDevolucao2 ON pessoaDevolucao2.id_pessoa = usuarioDevolucao2.id_pessoa "
				+

				// informações se o mateiral foi substituído
				" LEFT  JOIN biblioteca.material_informacional AS matSubstituidor ON ( matSubstituidor.id_material_que_eu_substituo IS NOT NULL AND matSubstituidor.id_material_que_eu_substituo = mi.id_material_informacional ) "
				+

				// / Informação do Título do material //
				" LEFT  JOIN biblioteca.exemplar                AS exe ON mi.id_material_informacional = exe.id_exemplar "
				+ " LEFT  JOIN biblioteca.fasciculo               AS fas ON mi.id_material_informacional = fas.id_fasciculo  "
				+ " LEFT  JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
				+ " INNER JOIN biblioteca.titulo_catalografico    AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico ) "
				+

				" WHERE emp.ativo = trueValue() "
				+ sqlBibliotecasString
				+ " AND pro.tipo = "
				+ TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL
				+ " AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo )  "
				+ " ORDER BY bibliotecaMaterial.descricao, nomeUsuario, pro.data_cadastro ASC ";

		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo",
				CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q.setTimestamp("fimPeriodo",
				CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
	}

}
