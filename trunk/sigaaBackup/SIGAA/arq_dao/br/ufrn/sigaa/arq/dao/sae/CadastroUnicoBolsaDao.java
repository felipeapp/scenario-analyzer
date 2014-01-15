/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.relatorio.dominio.LinhaRespostaQuestionario;
import br.ufrn.sigaa.assistencia.relatorio.dominio.RelatorioQuestionarioCadastroUnico;
import br.ufrn.sigaa.questionario.dominio.Alternativa;

/**
 * DAO responsável pelas consultas ao cadastro único
 * 
 * @author Henrique André
 *
 */
public class CadastroUnicoBolsaDao extends GenericSigaaDAO {

	/**
	 * So pode existir um único Cadastro Único por vez.
	 * Este método traz o ativo mais atual.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public FormularioCadastroUnicoBolsa findUnicoAtivo() throws DAOException {
		Criteria c = getSession().createCriteria(FormularioCadastroUnicoBolsa.class);
		c.add(Restrictions.eq("status", FormularioCadastroUnicoBolsa.EM_VIGOR));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		return (FormularioCadastroUnicoBolsa) c.uniqueResult();
	}
	
	/**
	 * Retorna como foi respondido o questionário do cadastro único
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioQuestionarioCadastroUnico> contabilizarRespostas() {
		String sql = "select  (select pergunta from questionario.pergunta p where p.id_pergunta = r.id_pergunta ) as per, " +
				"			  (select alternativa from questionario.alternativa_pergunta ap2 where ap2.ativo = trueValue() and ap2.id_alternativa_pergunta = r.id_alternativa ) as alter, r.id_pergunta, id_alternativa, count(r.id_alternativa) as total" +
				"	from questionario.resposta r " +
				"		inner join questionario.questionario_respostas qr on (r.id_questionario_respostas = qr.id_questionario_respostas) " +
				"		inner join questionario.questionario q on (qr.id_questionario = q.id_questionario) " +
				"		inner join sae.cadastro_unico_bolsa cu on (q.id_questionario = cu.id_questionario) " +
				"		inner join questionario.alternativa_pergunta ap on ( ap.id_alternativa_pergunta = r.id_alternativa ) " +
				"	where ap.ativo = trueValue() and cu.status = " + FormularioCadastroUnicoBolsa.EM_VIGOR +
				"	group by r.id_pergunta, id_alternativa " +
				"	order by r.id_pergunta, id_alternativa";

		return (List<RelatorioQuestionarioCadastroUnico>) getJdbcTemplate().query(sql, new ResultSetExtractor(){

			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				List<RelatorioQuestionarioCadastroUnico> resultado = new ArrayList<RelatorioQuestionarioCadastroUnico>();
				
				int idPerguntaAtual = 0;
				RelatorioQuestionarioCadastroUnico rel = new RelatorioQuestionarioCadastroUnico();
				while(rs.next()) {
					
					idPerguntaAtual = rs.getInt("id_pergunta");
					
					if (rel.getPergunta().getId() != idPerguntaAtual) {
						rel = new RelatorioQuestionarioCadastroUnico();
						rel.getPergunta().setId(idPerguntaAtual);
						rel.getPergunta().setDescricao(rs.getString("per"));
						resultado.add(rel);
					}
					
					long total = rs.getLong("total");
					
					Alternativa alternativa = new Alternativa();
					alternativa.setId(rs.getInt("id_alternativa"));
					alternativa.setAlternativa(rs.getString("alter"));
					
					LinhaRespostaQuestionario linha = new LinhaRespostaQuestionario();
					linha.setAlternativa(alternativa);
					linha.setTotal(total);
					linha.setRelatorio(rel);
					
					rel.setTotalParticipantes(  rel.getTotalParticipantes() + total  );
					rel.addAlternativa(linha);						
					
				}
				
				return resultado;
			}
			
		});
	}
	
}
