/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 06/02/2013
 *
 */
package br.ufrn.sigaa.ensino.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Query responsável por retornar os discentes que não confirmaram o vínculo em determinado ano e período.
 *
 * @author Diego Jácome
 *
 */
public class NaoConfirmacaoVinculoGraduacaoQueryDao extends GenericSigaaDAO implements JubilamentoQuery {

	/** Retorna discentes passíveis de exclusão por não confirmação de vínculo. 
	 * @see br.ufrn.sigaa.ensino.dao.JubilamentoQuery#findAlunosPassiveisJubilamento(java.util.List, java.lang.Boolean, java.lang.Boolean, br.ufrn.sigaa.dominio.Unidade, java.lang.Boolean)
	 */
	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException {
		return findDiscentesVinculoNaoConfirmado(anosPeriodo.iterator().next()[0],anosPeriodo.iterator().next()[1]);	
	}

	/** Retorna discentes passíveis de exclusão por não confirmação de vínculo.
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @return
	 */
	private Collection<Discente> findDiscentesVinculoNaoConfirmado(Integer anoIngresso, Integer periodoIngresso) {
		// VESTIBULAR / SiSU
		FormaIngresso[] formasIngresso = {FormaIngresso.VESTIBULAR, FormaIngresso.SISU};
		String sql = " select c.nome as curso, p.nome, p.id_pessoa, d.id_discente, d.matricula,  d.status, d.nivel, d.id_gestora_academica, "+
						" d.ano_ingresso, d.periodo_ingresso, c.id_curso, c.id_modalidade_educacao "+
						" from discente d "+
						" inner join graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao "+
						" inner join comum.pessoa p ON d.id_pessoa = p.id_pessoa "+
						" JOIN curso c ON d.id_curso = c.id_curso "+
						" join ensino.forma_ingresso f on d.id_forma_ingresso = f.id_forma_ingresso "+
						" where "+
						" d.ano_ingresso = "+anoIngresso+" and d.periodo_ingresso = "+periodoIngresso+" "+
						" and d.nivel = '"+NivelEnsino.GRADUACAO+"' "+
						" and d.status = "+StatusDiscente.ATIVO+" "+
						" and f.id_forma_ingresso in " + UFRNUtils.gerarStringIn(formasIngresso) +
						" and d.id_discente not in " +
						" ( "+
						"		select d.id_discente from discente d "+
						"		inner join ensino.validacao_vinculo v on d.id_discente = v.id_discente "+
						" 		join ensino.forma_ingresso f on d.id_forma_ingresso = f.id_forma_ingresso "+
						" 		where "+
						" 		d.ano_ingresso = "+anoIngresso+" and d.periodo_ingresso = "+periodoIngresso+" "+
						" 		and d.nivel = '"+NivelEnsino.GRADUACAO+"' "+
						"		and d.status = "+StatusDiscente.ATIVO+" "+
						" 		and f.id_forma_ingresso in " + UFRNUtils.gerarStringIn(formasIngresso) +
						" )	"+
						" order by c.nome , p.nome";
			
		Collection<Discente> lista = getJdbcTemplate().query(sql, new RowMapper<Discente>() {
			public Discente mapRow(ResultSet rs, int rowNum) throws SQLException {
				Discente d = new Discente();
				d.setId(rs.getInt("id_discente"));
				d.setMatricula(rs.getLong("matricula"));
				d.setPessoa(new Pessoa());
				d.getPessoa().setNome(rs.getString("nome"));
				d.getPessoa().setId(rs.getInt("id_pessoa"));
				d.setStatus(rs.getInt("status"));
				d.setAnoIngresso(rs.getInt("ano_ingresso"));
				d.setPeriodoIngresso(rs.getInt("periodo_ingresso"));
				d.setNivel(NivelEnsino.GRADUACAO);
				d.setGestoraAcademica(new Unidade(Unidade.UNIDADE_DIREITO_GLOBAL));
				d.setCurso(new Curso());
				d.getCurso().setId(rs.getInt("id_curso"));
				d.getCurso().setNome(rs.getString("curso"));
				d.getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("id_modalidade_educacao")));
				return d;
			}
		});
		return lista;
	}

	
}
