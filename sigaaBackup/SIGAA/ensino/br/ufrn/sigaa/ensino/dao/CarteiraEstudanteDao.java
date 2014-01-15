/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/06/2011
 *
 */
package br.ufrn.sigaa.ensino.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO com consultas das informações dos alunos para fins de emissão de carteiras de estudante.
 * 
 * @author Leonardo Campos
 *
 */
public class CarteiraEstudanteDao extends GenericSigaaDAO {

	
	/**
	 * Retorna as informações dos alunos necessárias para a geração do arquivo de importação de acordo
	 * com o nível de ensino, ano e período letivo. 
	 *
	 * @param nivel
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	public List<Map<String, Object>> findInfoEstudantes(char nivel, int ano,
			int periodo, Integer idUnidade, String consulta) throws DAOException, SQLException {
		String consultaSql = "";
		if(consulta != null){
			consultaSql = consulta;
		} else {
		switch (nivel) {
		case 'T':
			if (idUnidade != null) {
				consultaSql = "SELECT p.nome_ascii, "
					+ "       d.matricula, "
					+ "       p.data_nascimento, "
					+ "       e.logradouro AS endereco, "
					+ "       e.numero     AS numero_endereco, "
					+ "       e.complemento, "
					+ "       e.bairro, "
					+ "       m.nome       AS cidade, "
					+ "       e.cep, "
					+ "       p.telefone_fixo, "
					+ "       p.telefone_celular, "
					+ "       p.numero_identidade, "
					+ "       p.email, "
					+ "       p.nome_mae, "
					+ "       t.sigla, "
					+ "       c.nome       AS curso, "
					+ "       p.cpf_cnpj   AS cpf, "
					+ "       p.internacional, "
					+ "       ce.ru "
					+ "FROM   discente d "
					+ "       INNER JOIN comum.pessoa p "
					+ "         ON ( d.id_pessoa = p.id_pessoa ) "
					+ "       INNER JOIN tecnico.discente_tecnico dt "
					+ "         ON ( d.id_discente = dt.id_discente ) "
					+ "       LEFT OUTER JOIN curso c "
					+ "         ON ( d.id_curso = c.id_curso ) "
					+ "       LEFT OUTER JOIN comum.endereco e "
					+ "         ON ( p.id_endereco_contato = e.id_endereco ) "
					+ "       LEFT OUTER JOIN comum.municipio m "
					+ "         ON ( e.id_municipio = m.id_municipio ) "
					+ "       LEFT OUTER JOIN tecnico.turma_entrada_tecnico te "
					+ "         ON ( dt.id_turma_entrada = te.id_turma_entrada ) "
					+ "       LEFT OUTER JOIN tecnico.estrutura_curricular_tecnica et "
					+ "         ON ( te.id_estrutura_curricular = et.id_estrutura_curricular ) "
					+ "       LEFT OUTER JOIN ensino.turno t "
					+ "         ON ( et.id_turno = t.id_turno ) "
					+ "       LEFT OUTER JOIN ensino.carteira_estudante ce "
					+ "         ON ( ce.matricula = d.matricula ) "
					+ "WHERE  d.status IN " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO, StatusDiscente.FORMANDO})
					+ "       AND d.id_gestora_academica = " + idUnidade
					+ "       AND d.id_discente IN (SELECT id_discente "
					+ "                             FROM   ensino.matricula_componente mc "
					+ "                             WHERE  mc.ano = " + ano
					+ "                                    AND mc.periodo = " + periodo
					+ "                                    AND id_situacao_matricula NOT IN "
					+ UFRNUtils.gerarStringIn(new int[]{SituacaoMatricula.EM_ESPERA.getId()
													  , SituacaoMatricula.CANCELADO.getId()
													  , SituacaoMatricula.TRANCADO.getId()
													  , SituacaoMatricula.EXCLUIDA.getId()
													  , SituacaoMatricula.INDEFERIDA.getId()
													  , SituacaoMatricula.DESISTENCIA.getId()})
					+ ") ORDER  BY curso, "
					+ "          sigla, "
					+ "          nome_ascii ";
			}
			break;
		case 'L':
			consultaSql = "SELECT p.nome_ascii, "
				+ "       d.matricula, "
				+ "       p.data_nascimento, "
				+ "       e.logradouro AS endereco, "
				+ "       e.numero     AS numero_endereco, "
				+ "       e.complemento, "
				+ "       e.bairro, "
				+ "       m.nome       AS cidade, "
				+ "       e.cep, "
				+ "       p.telefone_fixo, "
				+ "       p.telefone_celular, "
				+ "       p.numero_identidade, "
				+ "       p.email, "
				+ "       p.nome_mae, "
				+ "       t.sigla, "
				+ "       c.nome       AS curso, "
				+ "       p.cpf_cnpj   AS cpf, "
				+ "       p.internacional, "
				+ "       ce.ru "
				+ "FROM   discente d "
				+ "       INNER JOIN comum.pessoa p "
				+ "         ON ( d.id_pessoa = p.id_pessoa ) "
				+ "       INNER JOIN lato_sensu.discente_lato dl "
				+ "         ON ( d.id_discente = dl.id_discente ) "
				+ "       LEFT OUTER JOIN curso c "
				+ "         ON ( d.id_curso = c.id_curso ) "
				+ "       LEFT OUTER JOIN comum.endereco e "
				+ "         ON ( p.id_endereco_contato = e.id_endereco ) "
				+ "       LEFT OUTER JOIN comum.municipio m "
				+ "         ON ( e.id_municipio = m.id_municipio ) "
				+ "       INNER JOIN lato_sensu.turma_entrada_lato te "
				+ "         ON ( dl.id_turma_entrada = te.id_turma_entrada ) "
				+ "       INNER JOIN ensino.turno t "
				+ "         ON ( te.id_turno = t.id_turno ) "
				+ "       LEFT OUTER JOIN ensino.carteira_estudante ce "
				+ "         ON ( ce.matricula = d.matricula ) "
				+ "WHERE  d.status IN " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO})
				+ " ORDER  BY curso, "
				+ "          p.nome_ascii ";
			break;
		case 'S':
			consultaSql = "SELECT p.nome_ascii, "
				+ "       d.matricula, "
				+ "       p.data_nascimento, "
				+ "       e.logradouro AS endereco, "
				+ "       e.numero     AS numero_endereco, "
				+ "       e.complemento, "
				+ "       e.bairro, "
				+ "       m.nome       AS cidade, "
				+ "       e.cep, "
				+ "       p.telefone_fixo, "
				+ "       p.telefone_celular, "
				+ "       p.numero_identidade, "
				+ "       p.email, "
				+ "       p.nome_mae, "
				+ "       c.nome       AS curso, "
				+ "       u.nome       AS unidade, "
				+ "       m2.nome      AS cidade_curso, "
				+ "       p.cpf_cnpj   AS cpf, "
				+ "       p.internacional, "
				+ "       ce.ru "
				+ "FROM   discente d "
				+ "       LEFT JOIN curso c "
				+ "         ON d.id_curso = c.id_curso "
				+ "       LEFT OUTER JOIN comum.municipio m2 "
				+ "         ON ( c.id_municipio = m2.id_municipio ) "
				+ "       LEFT JOIN comum.unidade u "
				+ "         ON d.id_gestora_academica = u.id_unidade "
				+ "       INNER JOIN comum.pessoa p "
				+ "         ON d.id_pessoa = p.id_pessoa "
				+ "       LEFT JOIN comum.endereco e "
				+ "         ON e.id_endereco = p.id_endereco_contato "
				+ "       LEFT JOIN comum.municipio m "
				+ "         ON m.id_municipio = e.id_municipio "
				+ "       INNER JOIN stricto_sensu.discente_stricto ds "
				+ "         ON ds.id_discente = d.id_discente "
				+ "       LEFT OUTER JOIN ensino.carteira_estudante ce "
				+ "         ON ( ce.matricula = d.matricula ) "
				+ "WHERE  d.status IN " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO})
				+ " ORDER  BY curso, "
				+ "          p.nome_ascii ";
			break;
		case 'G':
			consultaSql = "SELECT p.nome_ascii, "
				+ "       d.matricula, "
				+ "       p.data_nascimento, "
				+ "       upper(sem_acento(e.logradouro)) AS endereco, "
				+ "       e.numero     AS numero_endereco, "
				+ "       upper(sem_acento(e.complemento)) AS complemento, "
				+ "       upper(sem_acento(e.bairro)) AS bairro, "
				+ "       upper(sem_acento(m.nome))    AS cidade, "
				+ "       e.cep, "
				+ "       p.telefone_fixo, "
				+ "       p.telefone_celular, "
				+ "       p.numero_identidade, "
				+ "       p.email, "
				+ "       upper(sem_acento(p.nome_mae)) AS nome_mae, "
				+ "       t.sigla, "
				+ "       c.nome       AS curso, "
				+ "       m2.nome      AS cidade_curso, "
				+ "       p.cpf_cnpj   AS cpf, "
				+ "       p.internacional, "
				+ "       ce.ru "
				+ "FROM   discente d "
				+ "       INNER JOIN comum.pessoa p "
				+ "         ON ( d.id_pessoa = p.id_pessoa ) "
				+ "       INNER JOIN graduacao.discente_graduacao dg "
				+ "         ON ( d.id_discente = dg.id_discente_graduacao ) "
				+ "       LEFT OUTER JOIN curso c "
				+ "         ON ( d.id_curso = c.id_curso ) "
				+ "       LEFT OUTER JOIN comum.municipio m2 "
				+ "         ON ( c.id_municipio = m2.id_municipio ) "
				+ "       LEFT OUTER JOIN comum.endereco e "
				+ "         ON ( p.id_endereco_contato = e.id_endereco ) "
				+ "       LEFT OUTER JOIN comum.municipio m "
				+ "         ON ( e.id_municipio = m.id_municipio ) "
				+ "       LEFT OUTER JOIN graduacao.matriz_curricular mc "
				+ "         ON ( dg.id_matriz_curricular = mc.id_matriz_curricular ) "
				+ "       LEFT OUTER JOIN ensino.turno t "
				+ "         ON ( mc.id_turno = t.id_turno ) "
				+ "       LEFT OUTER JOIN ensino.carteira_estudante ce "
				+ "         ON ( ce.matricula = d.matricula ) "
				+ "WHERE  d.tipo = " + Discente.REGULAR
				+ "		  AND d.status IN " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO, StatusDiscente.FORMANDO})
				+ "       AND d.id_discente IN (SELECT id_discente "
				+ "                             FROM   ensino.matricula_componente mc "
				+ "                             WHERE  mc.ano = " + ano 
				+ "                                    AND mc.periodo = " + periodo
				+ "                                    AND id_situacao_matricula NOT IN " 
				+ UFRNUtils.gerarStringIn(new int[]{SituacaoMatricula.EM_ESPERA.getId()
											  	  , SituacaoMatricula.CANCELADO.getId()
												  , SituacaoMatricula.TRANCADO.getId()
												  , SituacaoMatricula.EXCLUIDA.getId()
												  , SituacaoMatricula.INDEFERIDA.getId()
												  , SituacaoMatricula.DESISTENCIA.getId()})
				+ ") ORDER  BY curso, "
				+ "          sigla, "
				+ "          cidade_curso, "
				+ "          nome_ascii ";
			break;
		// Consulta para os médicos residentes
		case 'R':
			consultaSql = "SELECT p.nome_ascii, "
				+ "       s.siape             AS matricula, "
				+ "       p.data_nascimento, "
				+ "       e.logradouro        AS endereco, "
				+ "       e.numero            AS numero_endereco, "
				+ "       e.complemento, "
				+ "       e.bairro, "
				+ "       m.nome              AS cidade, "
				+ "       e.cep, "
				+ "       p.telefone_fixo, "
				+ "       p.telefone_celular, "
				+ "       p.numero_identidade, "
				+ "       p.email, "
				+ "       p.nome_mae, "
				+ "       'MTN'               AS sigla, "
				+ "       'RESIDENCIA MEDICA' AS curso, "
				+ "       'NATAL'             AS cidade_curso, "
				+ "       p.cpf_cnpj          AS cpf, "
				+ "       p.internacional, "
				+ "       ce.ru "
				+ "FROM   rh.servidor s "
				+ "       INNER JOIN comum.pessoa p "
				+ "         ON ( s.id_pessoa = p.id_pessoa ) "
				+ "       LEFT OUTER JOIN comum.endereco e "
				+ "         ON ( p.id_endereco_contato = e.id_endereco ) "
				+ "       LEFT OUTER JOIN comum.municipio m "
				+ "         ON ( e.id_municipio = m.id_municipio ) "
				+ "       LEFT OUTER JOIN ensino.carteira_estudante ce "
				+ "         ON ( ce.matricula = s.siape ) "
				+ "WHERE  s.id_categoria = " + Categoria.MEDICO_RESIDENTE
				+ "       AND s.id_ativo = " + Ativo.RESIDENTE
				+ "ORDER  BY curso, "
				+ "          sigla, "
				+ "          cidade_curso, "
				+ "          nome_ascii ";
			break;
		}
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(consultaSql);
		return lista;
		

	}
}
