/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/09/2011
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.QuestionarioAplicadoDTO;
import br.ufrn.comum.dominio.Sistema;

/**
 * DAO para buscas de question�rios que se aplicam a todos os sistemas.
 * 
 * @author David Pereira
 *
 */
public class QuestionarioAplicadoDao extends GenericDAOImpl {

	public QuestionarioAplicadoDao(){
		super(Sistema.SIGADMIN);
	}
	
	/**
	 * Busca a lista de question�rios que est�o ativos e que o usu�rio passado como par�metro
	 * est� apto a responder por fazer parte do grupo de destinat�rios do question�rio.
	 * @param idUsuario
	 * @return
	 * @throws DAOException 
	 */
	public boolean verificaQuestionarioIgnorado(String identificador, Integer idUsuario) {
		return !isAllEmpty(getJdbcTemplate().queryForList("SELECT id_questionario_aplicado_ignorado FROM " +
				"	questionario.questionario_aplicado_ignorado WHERE identificador = ? " +
				" AND id_usuario = ? ", new Object[]{identificador, idUsuario}));
	}
	
	/**
	 * Busca a lista de question�rios que est�o ativos e que o usu�rio passado como par�metro
	 * est� apto a responder por fazer parte do grupo de destinat�rios do question�rio.
	 * Utilizado somente quando question�rio aplicado com exibi��o na tela de entrada do sistema.
	 * @param idUsuario
	 * @return
	 * @throws DAOException 
	 */
	public List<QuestionarioAplicadoDTO> buscarQuestionariosAtivos(int idUsuario) {
		GrupoDestinatariosDao dao = new GrupoDestinatariosDao();
		Date dataAtual = CalendarUtils.descartarHoras(new Date());
		
		@SuppressWarnings("unchecked")
		List<QuestionarioAplicadoDTO> questionarios = getJdbcTemplate().query("select * from questionario.questionario_aplicado qa where qa.ativo = true "
				+ "and qa.inicio < ? and qa.fim >= ? and qa.exibicao = 1 "
				+ "and not exists (select id_questionario_respostas from questionario.questionario_respostas "
				+ "				where status = 2 and id_questionario_aplicado = qa.id_questionario_aplicado and id_usuario = ?)"
				+ "and not exists (select id_questionario_aplicado from questionario.questionario_aplicado_ignorado " 
				+ "				where identificador = qa.identificador AND id_usuario = ?) ", 
				new Object[] { CalendarUtils.adicionaDias(dataAtual, 1), dataAtual,idUsuario , idUsuario }, 
				new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				QuestionarioAplicadoDTO dto = new QuestionarioAplicadoDTO();
				dto.setId(rs.getInt("id_questionario_aplicado"));
				dto.setIdentificador(rs.getString("identificador"));
				dto.setAtivo(rs.getBoolean("ativo"));
				dto.setDescricao(rs.getString("descricao"));
				dto.setInicio(rs.getDate("inicio"));
				dto.setFim(rs.getDate("fim"));
				dto.setIdGrupoDestinatarios(rs.getInt("id_grupo_destinario"));
				dto.setTitulo(rs.getString("titulo"));
				dto.setIdQuestionario(rs.getInt("id_questionario"));
				return dto;
			}
		});
		
		for (Iterator<QuestionarioAplicadoDTO> it = questionarios.iterator(); it.hasNext(); ) {
			QuestionarioAplicadoDTO qa = it.next();
			if (!dao.verificaUsuarioGrupo(idUsuario, qa.getIdGrupoDestinatarios())) {
				it.remove();
			}
		}
		
		return questionarios;
	}
	
	/**
	 * Persisti o id do usu�rio e o id do question�rio aplicado com objetivo que o mesmo n�o seja mais 
	 * visualizado pelo usu�rio.
	 * @param idUsuario
	 * @return
	 * @throws DAOException 
	 */
	public void ignorarQuestionarioAplicado(String identificador, Integer idUsuario) {
		
		getJdbcTemplate().update(" INSERT INTO questionario.questionario_aplicado_ignorado " +
								" (id_questionario_aplicado_ignorado, identificador, id_usuario) " +
								" VALUES ( nextval('hibernate_sequence'), ? , ? ); " , 
								new Object[]{identificador, idUsuario});
		
	}

}
