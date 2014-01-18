/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/05
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.seguranca.log.Logger;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.Sistema;

/**
 * DAO para inserir o PerfilPessoa no banco comum e atualizar nos 
 * bancos do SIPAC e SIGAA
 * 
 * @author David Pereira
 *
 */
public class PerfilPessoaDAO {
	/** DataSource do SIPAC */
	private DataSource sipacDs = null;
	/** DataSource do Sigaa */
	private DataSource sigaaDs = null;
	/** DataSource do Sigrh */
	private DataSource sigrhDs = null;
	/** DataSource do Comum */
	private DataSource comumDs = null;
	/** JDBCTemplate do SIPAC */
	private JdbcTemplate sipacTemplate = null;
	/** JDBCTemplate do SIGRH */
	private JdbcTemplate sigrhTemplate = null;
	/** JDBCTemplate do Sigaa */
	private JdbcTemplate sigaaTemplate = null;
	/** JDBCTemplate do Comum */
	private JdbcTemplate comumTemplate = null;
	/** Indica se o sigaa está ativo */
	private boolean sigaaAtivo;
	/** Indica se o sipac está ativo */
	private boolean sipacAtivo;
	/** Indica se o sigrh está ativo */
	private boolean sigrhAtivo;
	/** Instancia do PerfilPessoaDao */
	private static PerfilPessoaDAO instance = new PerfilPessoaDAO();
	/** Construtor padrão */
	private PerfilPessoaDAO() {
		Logger.debug("pegando dao");
		
		sigaaAtivo = Sistema.isSigaaAtivo();
		sipacAtivo = Sistema.isSipacAtivo();
		sigrhAtivo = Sistema.isSigrhAtivo();
		
		comumDs = Database.getInstance().getComumDs();

		if (sipacAtivo)
			sipacDs = Database.getInstance().getSipacDs();
		if (sigrhAtivo)
			sigrhDs = Database.getInstance().getSigrhDs();
		if (sigaaAtivo)
			sigaaDs = Database.getInstance().getSigaaDs();
	}
	/** Retorna a instância atual */
	public static PerfilPessoaDAO getDao() {
		return instance;
	}
	
	/**
	 * Retorna um PerfilPessoa pelo id
	 */
	public PerfilPessoa get(Integer idPerfil) {
		try {
			if (idPerfil == null) {
				return null;
			}
		
			return (PerfilPessoa) getComumTemplate().queryForObject("select * from comum.perfil_pessoa where id_perfil = " + idPerfil, new RowMapper() {
	
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					PerfilPessoa pp = new PerfilPessoa();
					pp.setId(rs.getInt("id_perfil"));
					pp.setAreas(rs.getString("areas"));
					pp.setDescricao(rs.getString("descricao"));
					pp.setFormacao(rs.getString("formacao"));
					pp.setEndereco(rs.getString("endereco"));
					pp.setSala(rs.getString("sala"));
					pp.setEnderecoLattes(rs.getString("endereco_lattes"));
					pp.setAssinatura(rs.getString("assinatura"));
					pp.setOcultarEmail(rs.getBoolean("ocultar_email"));
					
					int idDiscente = rs.getInt("id_discente");
					pp.setIdDiscente(idDiscente == 0 ? null : idDiscente);
					int idServidor = rs.getInt("id_servidor");
					pp.setIdServidor(idServidor == 0 ? null : idServidor);
					int idTutor = rs.getInt("id_tutor");
					pp.setIdTutor(idTutor == 0 ? null : idTutor);
					
					return pp;
				}
			
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Localiza o perfil pelo id da Pessoa
	 * 
	 * @param idDiscente
	 * @return
	 */
	public PerfilPessoa findByPessoa(Integer idPessoa) {
		try {
			if (idPessoa == null) {
				return null;
			}
		
			return (PerfilPessoa) getComumTemplate().queryForObject("select * from comum.perfil_pessoa where id_pessoa = " + idPessoa, new RowMapper() {
	
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					PerfilPessoa pp = new PerfilPessoa();
					pp.setId(rs.getInt("id_perfil"));
					pp.setAreas(rs.getString("areas"));
					pp.setDescricao(rs.getString("descricao"));
					pp.setFormacao(rs.getString("formacao"));
					pp.setEndereco(rs.getString("endereco"));
					pp.setSala(rs.getString("sala"));
					pp.setEnderecoLattes(rs.getString("endereco_lattes"));
					pp.setAssinatura(rs.getString("assinatura"));
					pp.setOcultarEmail(rs.getBoolean("ocultar_email"));
					
					int idDiscente = rs.getInt("id_discente");
					pp.setIdDiscente(idDiscente == 0 ? null : idDiscente);
					int idServidor = rs.getInt("id_servidor");
					pp.setIdServidor(idServidor == 0 ? null : idServidor);
					int idTutor = rs.getInt("id_tutor");
					pp.setIdTutor(idTutor == 0 ? null : idTutor);
					
					return pp;
				}
			
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Localiza o perfil pelo id do discente
	 * 
	 * @param idDiscente
	 * @return
	 */
	public PerfilPessoa findByDiscente(Integer idDiscente) {
		try {
			if (idDiscente == null) {
				return null;
			}
		
			return (PerfilPessoa) getComumTemplate().queryForObject("select * from comum.perfil_pessoa where id_discente = " + idDiscente, new RowMapper() {
	
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					PerfilPessoa pp = new PerfilPessoa();
					pp.setId(rs.getInt("id_perfil"));
					pp.setAreas(rs.getString("areas"));
					pp.setDescricao(rs.getString("descricao"));
					pp.setFormacao(rs.getString("formacao"));
					pp.setEndereco(rs.getString("endereco"));
					pp.setSala(rs.getString("sala"));
					pp.setEnderecoLattes(rs.getString("endereco_lattes"));
					pp.setAssinatura(rs.getString("assinatura"));
					pp.setOcultarEmail(rs.getBoolean("ocultar_email"));
					
					int idDiscente = rs.getInt("id_discente");
					pp.setIdDiscente(idDiscente == 0 ? null : idDiscente);
					int idServidor = rs.getInt("id_servidor");
					pp.setIdServidor(idServidor == 0 ? null : idServidor);
					int idTutor = rs.getInt("id_tutor");
					pp.setIdTutor(idTutor == 0 ? null : idTutor);
					
					return pp;
				}
			
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}	
	
	
	/**
	 * Localiza o perfil pelo id do discente
	 * 
	 * @param idDiscente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PerfilPessoa> findByAreas(String areas, List<Integer> ids) {
		try {
			if (isEmpty(areas)) {
				return null;
			}
			
			String sql = "select id_discente from comum.perfil_pessoa pp where areas ilike ? and id_discente in " + gerarStringIn(ids);
			
			return getComumTemplate().query(sql, new Object[] { "%" + areas + "%" }, new RowMapper() {
	
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					PerfilPessoa pp = new PerfilPessoa();
					pp.setIdDiscente(rs.getInt("id_discente"));
					
					return pp;
				}
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}		
	
	/**
	 * Busca todos os perfis passados como parâmetro
	 * 
	 * @param ids
	 * @return
	 */
	public Collection<PerfilPessoa> getByIds(Collection<Integer> ids) {
		if (isEmpty(ids)) {
			return null;
		}
		List<Map<String, Object>> lista = getComumTemplate().queryForList("select p.id_pessoa as idPessoa, p.id_perfil, p.descricao, p.formacao, p.endereco_lattes from comum.perfil_pessoa p where id_perfil in " + gerarStringIn(ids) );
		Collection<PerfilPessoa> retorno = new ArrayList<PerfilPessoa>();
		for (Map<String, Object> linha : lista) {
			PerfilPessoa pp = new PerfilPessoa();
			pp.setId((Integer) linha.get("id_perfil"));
			pp.setIdPessoa((Integer) linha.get("idPessoa"));
			pp.setDescricao((String) linha.get("descricao"));
			pp.setFormacao((String) linha.get("formacao"));
			pp.setAssinatura((String) linha.get("assinatura"));
			if(!isEmpty(pp.getFormacao()))
				pp.setFormacao(StringUtils.limitTxt(pp.getFormacao(),240));
			pp.setEnderecoLattes((String) linha.get("endereco_lattes"));
			retorno.add(pp);
		}
		
		return retorno;
	}
	
	/**
	 * Cria um novo perfil
	 */
	public void create(PerfilPessoa perfil) {
		int id = getComumTemplate().queryForInt("(select nextval('perfil_seq'))");
		perfil.setId(id);
		
		String sql = "insert into comum.perfil_pessoa (id_perfil, descricao, formacao, areas, sala, endereco, telefone, id_servidor, id_discente, id_tutor, endereco_lattes, assinatura, ocultar_email, id_pessoa) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getComumTemplate().update(sql, new Object[] { perfil.getId(), perfil.getDescricao(), perfil.getFormacao(), perfil.getAreas(), perfil.getSala(), perfil.getEndereco(), perfil.getTelefone(), perfil.getIdServidor(), perfil.getIdDiscente(), perfil.getIdTutor(), perfil.getEnderecoLattes(), perfil.getAssinatura(), perfil.isOcultarEmail(), perfil.getIdPessoa() });
	}

	/**
	 * Atualiza um perfil
	 */
	public void update(PerfilPessoa perfil) {
		String sql = "update comum.perfil_pessoa set assinatura=?, descricao=?, formacao=?, areas=?, sala=?, endereco=?, telefone=?, id_servidor=?, id_discente=?, id_tutor=?, endereco_lattes = ?, ocultar_email = ?, id_pessoa = ? where id_perfil=?";
		getComumTemplate().update(sql, new Object[] { perfil.getAssinatura(), perfil.getDescricao(), perfil.getFormacao(), perfil.getAreas(), perfil.getSala(), perfil.getEndereco(), perfil.getTelefone(), perfil.getIdServidor(), perfil.getIdDiscente(), perfil.getIdTutor(), perfil.getEnderecoLattes(), perfil.isOcultarEmail(), perfil.getIdPessoa(), perfil.getId() });
	}

	/**
	 * Atualiza a foto de um perfil
	 * 
	 * @param idUsuario
	 * @param idArquivo
	 */
	public void atualizaFotoUsuario(int idUsuario, Integer idArquivo) {
		String sql = "update comum.usuario set id_foto = ? where id_usuario = ?";
		if(sipacAtivo)
			getSipacTemplate().update(sql, new Object[] { idArquivo, idUsuario });
		else if(sigrhAtivo)
			getSigrhTemplate().update(sql, new Object[] { idArquivo, idUsuario });
		getComumTemplate().update(sql, new Object[] { idArquivo, idUsuario });
		if (sigaaAtivo)
			getSigaaTemplate().update(sql, new Object[] { idArquivo, idUsuario });
	}

	/**
	 * Atualiza o perfil de um servidor
	 * 
	 * @param idServidor
	 * @param perfil
	 * @param idArquivo
	 */
	public void atualizaPerfilServidor(Integer idServidor, PerfilPessoa perfil, Integer idArquivo) {
		if (idArquivo == null) {
			String sql = "update rh.servidor set id_perfil = ? where id_servidor = ?";
			
			if(sipacAtivo)
				getSipacTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdServidor() });
			else if(sigrhAtivo)
				getSigrhTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdServidor() });
		
			if (sigaaAtivo)
				getSigaaTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdServidor() });
			//getComumTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdServidor() });
		} else {
			String sql = "update rh.servidor set id_foto = ?, id_perfil = ? where id_servidor = ?";
			
			if(sipacAtivo)
				getSipacTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdServidor() });
			else if(sigrhAtivo)
				getSigrhTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdServidor() });
			
			if (sigaaAtivo)
				getSigaaTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdServidor() });
			//getComumTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdServidor() });
		}
	}
	
	/**
	 * Atualiza o perfil da pessoa
	 * 
	 * @param idPessoa
	 * @param perfil
	 */
	public void atualizaPerfilPessoa(Integer idPessoa, PerfilPessoa perfil) {	
		String sql = "update comum.pessoa set id_perfil = ? where id_pessoa = ?";
		if(sipacAtivo)
			getSipacTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdPessoa() });
		else if(sigrhAtivo)
			getSigrhTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdPessoa() });
		
	}

	/**
	 * Atualiza o perfil do discente
	 * 
	 * @param idDiscente
	 * @param perfil
	 * @param idArquivo
	 */
	public void atualizaPerfilDiscente(Integer idDiscente, PerfilPessoa perfil, Integer idArquivo) {
		if (sigaaAtivo) {
			if (idArquivo == null) {
				String sql = "update discente set id_perfil = ? where id_discente = ?";
				getSigaaTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdDiscente() });
			} else {
				String sql = "update discente set id_foto = ?, id_perfil = ? where id_discente = ?";
				getSigaaTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdDiscente() });
			}
		}
	}

	/**
	 * Atualiza o perfil do tutor
	 * 
	 * @param idTutor
	 * @param perfil
	 * @param idArquivo
	 */
	public void atualizaPerfilTutor(Integer idTutor, PerfilPessoa perfil, Integer idArquivo) {
		if (sigaaAtivo) {
			if (idArquivo == null) {
				String sql = "update ead.tutor_orientador set id_perfil = ? where id_tutor_orientador = ?";
				getSigaaTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdTutor() });
			} else {
				String sql = "update ead.tutor_orientador set id_foto = ?, id_perfil = ? where id_tutor_orientador = ?";
				getSigaaTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdTutor() });
			}
		}
	}

	/**
	 * Atualiza o perfil do Docente Externo
	 * 
	 * @param idDocenteExterno
	 * @param perfil
	 * @param idArquivo
	 */
	public void atualizaPerfilDocenteExterno(Integer idDocenteExterno, PerfilPessoa perfil, Integer idArquivo) {
		if (sigaaAtivo) {
			if (idArquivo == null) {
				String sql = "update ensino.docente_externo set id_perfil = ? where id_docente_externo = ?";
				getSigaaTemplate().update(sql, new Object[] { perfil.getId(), perfil.getIdDocenteExterno() });
			} else {
				String sql = "update ensino.docente_externo set id_foto = ?, id_perfil = ? where id_docente_externo = ?";
				getSigaaTemplate().update(sql, new Object[] { idArquivo, perfil.getId(), perfil.getIdDocenteExterno() });
			}
		}
	}	
	
	/**
	 * Busca o endereço do currículo Lattes do aluno
	 * 
	 * @param idDiscente
	 * @return
	 */
	public String findLattesByDiscente(Integer idDiscente) {
		String curriculoLattes = null;
		try {
			String sql = "select endereco_lattes from comum.perfil_pessoa where id_discente = ? " + BDUtils.limit(1);
			curriculoLattes = (String) getComumTemplate().queryForObject(sql, new Object[] { idDiscente }, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
		return curriculoLattes;
	}
	
	/**
	 * Atualiza o Lattes do discente
	 */
	public void updateLattes(String lattes, Integer idDiscente) {
		String sql = "update comum.perfil_pessoa set endereco_lattes = ? where id_discente = ?";
		getComumTemplate().update(sql, new Object[] { lattes, idDiscente });
	}	
	
	/**
	 * Retorna uma conexão com a base COMUM 
	 * 
	 * @return the comumTemplate
	 */
	public JdbcTemplate getComumTemplate() {
		if (comumTemplate == null) {
			comumTemplate = new JdbcTemplate(comumDs);
		}
		return comumTemplate;
	}

	/**
	 * Retorna uma conexão com a base SIGAA
	 * 
	 * @return the sigaaTemplate
	 */
	public JdbcTemplate getSigaaTemplate() {
		if (sigaaTemplate == null) {
			sigaaTemplate = new JdbcTemplate(sigaaDs);
		}
		return sigaaTemplate;
	}

	/**
	 * Retorna uma conexão com a base SIPAC
	 * 
	 * @return the sipacTemplate
	 */
	public JdbcTemplate getSipacTemplate() {
		if (sipacTemplate == null) {
			sipacTemplate = new JdbcTemplate(sipacDs);
		}
		return sipacTemplate;
	}
	
	/**
	 * Retorna o template do SIGRH.
	 * @return
	 */
	public JdbcTemplate getSigrhTemplate() {
		if (sigrhTemplate == null) {
			sigrhTemplate = new JdbcTemplate(sigrhDs);
		}
		return sigrhTemplate;
	}
	
	/** Método main para executar o dao */
	public static void main(String[] args) {
		PerfilPessoaDAO.getDao();
	}
	
}
