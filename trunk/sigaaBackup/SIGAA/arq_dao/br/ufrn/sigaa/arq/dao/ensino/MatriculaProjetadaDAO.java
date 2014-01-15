/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/10/2009
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * DAO responsável por calcular a matrícula projetada do curso.
 * @author Arlindo Rodrigues
 *
 */
public class MatriculaProjetadaDAO extends GenericSigaaDAO  {
	
	/**
	 * Retorna a matrícula projetada para cada ano e de cada curso.
	 * @param anoIni
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findMatriculaProjetada(int anoIni, int anoFim) throws DAOException{
		String sqlconsulta = 
		"		select ovc.ano, c.id_curso, c.nome as curso, u.nome as unidade, sum(vagas_periodo_1 + vagas_periodo_2) as totalvagas, " 
		+"	       sesu.codigo, sesu.fator_retencao, sesu.peso_grupo, sesu.duracao_padrao, "
		+"	       coalesce( ( sum(vagas_periodo_1 + vagas_periodo_2) *  sesu.duracao_padrao * (1 + sesu.fator_retencao) ),0) as total "
		+" from curso c" 
		+" inner join ensino.oferta_vagas_curso ovc on c.id_curso = ovc.id_curso "
		+" inner join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso " 
		+" inner join ensino.area_sesu sesu on sesu.id_area_sesu = c.id_area_sesu "
		+" inner join comum.unidade u on u.id_unidade = c.id_unidade "
		+" where ovc.ano between "+anoIni+" and "+anoFim
		+" and  c.nivel = 'G' "
		+" group by ovc.ano, c.id_curso, c.nome, u.nome, sesu.codigo, sesu.fator_retencao,  sesu.peso_grupo, sesu.duracao_padrao "
		+" order by ovc.ano, u.nome, c.nome ";	
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}		
	
	/**
	 * Gera o relatório de matrículas projetadas em formato .csv
	 * @param anoIni
	 * @param anoFim
	 * @return
	 * @throws ArqException
	 */
	public String findMatriculaProjetadaCSV(int anoIni, int anoFim) throws ArqException{
		Connection con = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();		
		
			String sqlconsulta = 
			"		select ovc.ano, c.id_curso, c.nome as curso, u.nome as unidade, sum(vagas_periodo_1 + vagas_periodo_2) as totalvagas, " 
			+"	       sesu.codigo, sesu.fator_retencao, sesu.peso_grupo, sesu.duracao_padrao, "
			+"	       coalesce( ( sum(vagas_periodo_1 + vagas_periodo_2) *  sesu.duracao_padrao * (1 + sesu.fator_retencao) ),0) as total "
			+" from curso c" 
			+" inner join ensino.oferta_vagas_curso ovc on c.id_curso = ovc.id_curso "
			+" inner join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso " 
			+" inner join ensino.area_sesu sesu on sesu.id_area_sesu = c.id_area_sesu "
			+" inner join comum.unidade u on u.id_unidade = c.id_unidade "
			+" where ovc.ano between "+anoIni+" and "+anoFim
			+" and  c.nivel = 'G' "
			+" group by ovc.ano, c.id_curso, c.nome, u.nome, sesu.codigo, sesu.fator_retencao,  sesu.peso_grupo, sesu.duracao_padrao "
			+" order by ovc.ano, u.nome, c.nome ";
			
			PreparedStatement st = con.prepareStatement(sqlconsulta);
			
			ResultSet rs = st.executeQuery();

			return UFRNUtils.resultSetToCSV(rs);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}		

}
