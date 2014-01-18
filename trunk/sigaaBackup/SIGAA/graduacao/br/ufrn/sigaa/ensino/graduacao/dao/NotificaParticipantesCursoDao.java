/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 02/06/2011
 */
package br.ufrn.sigaa.ensino.graduacao.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * Dao responsável por realizar consultas para auxiliar a notificação de participantes de curso de graduação
 *  
 * @author arlindo
 *
 */
public class NotificaParticipantesCursoDao extends GenericSigaaDAO {
	
	/**
	 * Busca todos os Usuários dos alunos de um curso
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<UsuarioGeral> findUsuarioDiscentesByCurso(int idCurso)	throws DAOException {		
		
		String sql =
				" SELECT d.id_discente, p.id_pessoa, p.nome, p.email, u.id_usuario " +  
				" FROM public.discente d" +
				" inner join comum.pessoa p using (id_pessoa)" +
				" left  join comum.usuario u on u.id_pessoa = p.id_pessoa " +
				" WHERE d.id_curso = ? and d.status = "+StatusDiscente.ATIVO
				+ " ORDER BY p.nome";			
		

		@SuppressWarnings("unchecked")
		List<Map<String,Object>> listagem = getJdbcTemplate().queryForList(sql, new Object[] {idCurso});
		
		List<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		for (Map<String,Object> lista : listagem){
			UsuarioGeral u = new UsuarioGeral();
			u.setIdPessoa(Integer.parseInt(""+lista.get("id_pessoa") ));
			
			if(lista.get("id_usuario") != null) {
				u.setId(Integer.parseInt(""+lista.get("id_usuario") ));
			}
			
			u.setNome((String) lista.get("nome"));
			u.setEmail((String) lista.get("email"));						
			usuarios.add(u);
		}			
		return usuarios;
	}	
	
	/**
	 * Retorna os Usuários dos docentes do curso e ano e período informados.
	 * 
	 * @param idCurso
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<UsuarioGeral> findUsuariosDocentesByCursoPeriodo(int idCurso, int ano, int periodo){
		String sql = "select distinct p.id_pessoa, p.nome, p.email, u.id_usuario "
				+ " from ensino.turma t"
				+ " join ensino.docente_turma dt using (id_turma)"
				+ " left join ensino.docente_externo de using (id_docente_externo)"
				+ " join rh.servidor s on (dt.id_docente = s.id_servidor or de.id_servidor = s.id_servidor)"
				+ " join comum.pessoa p on (p.id_pessoa = s.id_pessoa or p.id_pessoa = de.id_pessoa)"
				+ " left join comum.usuario u on (p.id_pessoa = u.id_pessoa) "
				+ " where t.ano = ? and t.periodo = ? "
				+ " and exists ( select mc.id_curso "
				+ "	from graduacao.reserva_curso rc "
				+ "	join graduacao.matriz_curricular mc using(id_matriz_curricular) "
				+ "	where rc.id_turma = t.id_turma "
				+ "	and mc.id_curso = ? ) " + " order by p.nome";		
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> listagem = getJdbcTemplate().queryForList(sql, new Object[] {ano, periodo, idCurso});
		
		List<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		for (Map<String,Object> lista : listagem){
			UsuarioGeral u = new UsuarioGeral();
			u.setIdPessoa(Integer.parseInt(""+lista.get("id_pessoa") ));
			
			if( lista.get("id_usuario") != null ) {			
				u.setId(Integer.parseInt(""+lista.get("id_usuario") ));
			}
			
			u.setNome((String) lista.get("nome"));
			u.setEmail((String) lista.get("email"));			
			
			usuarios.add(u);
		}
		return usuarios;		
	}	

}
