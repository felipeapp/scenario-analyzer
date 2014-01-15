package br.ufrn.comum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.ProcessoInteressado;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoInteressado;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.integracao.dto.DocumentoDTO;
import br.ufrn.integracao.dto.InteressadoDTO;
import br.ufrn.integracao.dto.MovimentoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.integracao.dto.UnidadeDTO;


/**
 * Classe DAO para acesso aos processos dos protocolos, com consultas específicas.
 * 
 * @author Itamir Filho
 *
 */
public class ProcessoDAOImpl extends GenericDAOImpl implements ProcessoDAO {
	
	public ProcessoDAOImpl() {
		setSistema(Sistema.SIPAC);
	}

	/**
	 * Busca um processo por identificador
	 */
	public ProcessoDTO findByIdentificador(int numero, int ano, int dv) throws DAOException {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("select p.id_processo as id, p.data_cadastro as data_cadastro, p.num_protocolo as numero, p.ano as ano, p.assunto as assunto, ");
		sql.append("p.observacao as obs, p.status as status, mov.data_envio_origem as data_envio, mov.data_recebimento_destino as data_recebimento, "); 
		sql.append("u.sigla as sigla_origem, u.nome as nome_origem, u.codigo_unidade as codigo_origem, ");
		sql.append("u2.id_unidade as id_unidade_mov, u2.sigla as sigla_mov, u2.nome as nome_mov, u2.codigo_unidade as codigo_mov, ");
		sql.append("doc.datadocumento as data_documento, tipo.denominacao as denominacao, doc.observacao as observacao, tipo_proc.denominacao as desc_tipo_proc, ");
		sql.append("inter.idinteressado as id_interessado, inter.nome as nome_int, inter.identificador as identific, mov.id_movimento as id_movimento, ");
		sql.append("u.id_unidade as id_unidade_orig, doc.iddocumento as id_documento "); 
		
		sql.append("from comum.unidade u, comum.unidade u2,  protocolo.interessado inter,  protocolo.tipoprocesso tipo_proc, ");
		sql.append("protocolo.movimento mov, protocolo.processo_interessado procint, ");
		sql.append("protocolo.processo p left join protocolo.documento doc on doc.id_processo = p.id_processo ");
		sql.append("left join protocolo.tipodocumento tipo on tipo.idtipodocumento = doc.idtipodocumento ");
		
		sql.append("where p.num_protocolo = ? and p.ano = ?  and u.id_unidade = p.id_unidade_origem and u2.id_unidade = mov.id_unidade_destino "); 
		sql.append("and inter.idinteressado = procint.idinteressado and tipo_proc.idtipoprocesso = p.id_tipo_processo "); 
		sql.append("and p.id_processo = mov.id_processo and p.id_processo = procint.id_processo ");
		
		if(dv > 0)
			sql.append("and p.dv = ?");

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = Database.getInstance().getSipacConnection(); 

			ps = con.prepareStatement(sql.toString()) ;
			ps.setInt(1, numero);
			ps.setInt(2, ano);
			
			if(dv > 0)
				ps.setInt(3, dv);
			
			rs = ps.executeQuery();

			ProcessoDTO processo = null;
			ArrayList<MovimentoDTO> movimentos = new ArrayList<MovimentoDTO>();
			ArrayList<DocumentoDTO> documentos = new ArrayList<DocumentoDTO>();
			ArrayList<InteressadoDTO> interessados = new ArrayList<InteressadoDTO>();
			
			while (rs.next()){
				processo = new ProcessoDTO();
				processo.setId(rs.getInt("id"));
				processo.setNumProtocolo(rs.getInt("numero"));
				processo.setDataCadastro(rs.getDate("data_cadastro"));
				processo.setAno(rs.getInt("ano"));
				processo.setAssunto(rs.getString("assunto"));
				processo.setObservacao(rs.getString("obs"));
				processo.setStatus(rs.getInt("status"));
				processo.setDescricaoTipoProcesso(rs.getString("desc_tipo_proc"));
				
				Unidade undOrigem = new Unidade();
				undOrigem.setId(rs.getInt("id_unidade_orig"));
				undOrigem.setSigla(rs.getString("sigla_origem"));
				undOrigem.setNome(rs.getString("nome_origem"));
				undOrigem.setCodigo(rs.getLong("codigo_origem"));
				
				MovimentoDTO mov = new MovimentoDTO();
				mov.setId(rs.getInt("id_movimento"));
				mov.setDataEnvio(rs.getDate("data_envio"));
				mov.setDataRecebimento(rs.getDate("data_recebimento"));
				
				UnidadeDTO undDestino = new UnidadeDTO();
				undDestino.setId(rs.getInt("id_unidade_mov"));
				undDestino.setSigla(rs.getString("sigla_mov"));
				undDestino.setDenominacao(rs.getString("nome_mov"));
				undDestino.setCodigoUnidade(rs.getLong("codigo_mov"));
				
				mov.setUndDestino(undDestino);
				
				if(!movimentos.contains(mov))
					movimentos.add(mov);
				
				DocumentoDTO doc = new DocumentoDTO();
				
				Integer idDocumento = rs.getInt("id_documento");
				if (idDocumento != null){
					doc.setId(rs.getInt("id_documento"));
					doc.setDataDocumento(rs.getDate("data_documento"));
					doc.setTipoDocumenoDescricao(rs.getString("denominacao"));
					doc.setObservacao(rs.getString("observacao"));
					
					if(!documentos.contains(doc))
						documentos.add(doc);
				}
				
				InteressadoDTO interessado = new InteressadoDTO();
				interessado.setId(rs.getInt("id_interessado"));
				interessado.setIdentificador(rs.getString("identific"));
				interessado.setNome(rs.getString("nome_int"));
				
				if(!interessados.contains(interessado))					
					interessados.add(interessado);
			}

			if(processo != null){
				processo.setInteressados(interessados);
				processo.setMovimentacoes(movimentos);
				processo.setDocumentos(documentos);
			}

			return processo;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
	}

	public List<ProcessoDTO> findByInteressado(String identificador) throws DAOException {
		return findByInteressado(identificador, 0);
	}
	
	/**
	 * Busca processos por interessados.
	 */
	public List<ProcessoDTO> findByInteressado(String identificador, int tipo) throws DAOException {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("select p.id_processo as id, p.data_cadastro as data_cadastro, p.num_protocolo as numero, p.ano as ano, p.assunto as assunto, ");
		sql.append("p.observacao as obs, p.status as status , tipo_proc.denominacao as desc_tipo_proc, ");
		sql.append("u.sigla as sigla_origem, u.nome as nome_origem, u.codigo_unidade as codigo_origem ");
		
		sql.append("from comum.unidade u, comum.unidade u2,  protocolo.interessado inter, protocolo.tipodocumento tipo, protocolo.tipoprocesso tipo_proc, protocolo.processo p, ");
		sql.append("protocolo.movimento mov, protocolo.processo_interessado procint, protocolo.documento doc "); 
		
		sql.append(" where inter.identificador like ? ");
		
		if (tipo != 0)
			sql.append(" and inter.tipo = ? "); 
		
		sql.append(" and u.id_unidade = p.id_unidade_origem and u2.id_unidade = mov.id_unidade_destino "); 
		sql.append("and inter.idinteressado = procint.idinteressado and tipo.idtipodocumento = doc.idtipodocumento and tipo_proc.idtipoprocesso = p.id_tipo_processo "); 
		sql.append("and p.id_processo = mov.id_processo and p.id_processo = procint.id_processo and p.id_processo = doc.id_processo ");
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = Database.getInstance().getSipacConnection(); 

			ps = con.prepareStatement(sql.toString()) ;
			ps.setString(1, identificador);
			
			if (tipo != 0)
				ps.setInt(2, tipo);
			
			rs = ps.executeQuery();

			List<ProcessoDTO> processos = new ArrayList<ProcessoDTO>();
			
			while (rs.next()){
				ProcessoDTO processo = new ProcessoDTO();
				processo.setId(rs.getInt("id"));
				processo.setNumProtocolo(rs.getInt("numero"));
				processo.setDataCadastro(rs.getDate("data_cadastro"));
				processo.setAno(rs.getInt("ano"));
				processo.setAssunto(rs.getString("assunto"));
				processo.setObservacao(rs.getString("obs"));
				processo.setStatus(rs.getInt("status"));
				processo.setDescricaoTipoProcesso(rs.getString("desc_tipo_proc"));
				
				UnidadeDTO undOrigem = new UnidadeDTO();
				undOrigem.setSigla(rs.getString("sigla_origem"));
				undOrigem.setDenominacao(rs.getString("nome_origem"));
				undOrigem.setCodigoUnidade(rs.getLong("codigo_origem"));				
				
				processo.setUnidadeOrigem(undOrigem);
				
				processos.add(processo);
			}

			return processos;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
	}
	
	public List<ProcessoDTO> findByAluno(long matricula, char nivel) throws DAOException {
		if(nivel == NivelEnsino.GRADUACAO)
			return findByInteressado(String.valueOf(matricula), TipoInteressado.ALUNO);
		else
			return findByInteressado(String.valueOf(matricula), TipoInteressado.ALUNO_POS);
	}
	
	/**
	 * Retorna todos os tipos de documentos ativos no sistema
	 * @return
	 * @throws DAOException
	 */
	public int findNextNumero(int radical, int ano) throws DAOException{

		Connection con = null;
		try {
			con = Database.getInstance().getSipacConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT NEXTVAL('protocolo.protocolo_" + radical + "_" + ano + "')");
			rs.next();
			return rs.getInt(1);
			
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Cria o processo e retorna o id do mesmo.
	 * @param processo
	 * @return
	 */
	public int criarProcesso(ProcessoDTO processo) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
		insert.withTableName("protocolo.processo")
			.usingColumns("id_processo", "num_protocolo", "ano", "assunto", "id_usuario", "observacao", 
						  "data_cadastro", "dv", "id_tipo_processo", "status", "id_unidade_origem", "radical");
		
		int pk = getJdbcTemplate().queryForInt("select nextval('protocolo.seq_idprocesso')");
		
		insert.execute(new MapSqlParameterSource()
			.addValue("id_processo", pk)
			.addValue("num_protocolo", processo.getNumProtocolo())
			.addValue("ano", processo.getAno())
			.addValue("assunto", processo.getAssunto())
			.addValue("id_usuario", processo.getIdUsuario())
			.addValue("observacao", processo.getObservacao())
			.addValue("data_cadastro", processo.getDataCadastro())
			.addValue("dv", processo.getDV())
			.addValue("id_tipo_processo", processo.getIdtipoProcesso())
			.addValue("status", processo.getStatus())
			.addValue("id_unidade_origem", processo.getUnidadeOrigem().getId())
			.addValue("radical", Integer.parseInt(ProcessoDTO.numFixo))
		);
		return pk;
	}
	
	/**
	 * Cria o movimento do processo.
	 * @param movimento
	 * @return
	 */
	public int criarMovimento(MovimentoDTO movimento) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
		insert.withTableName("protocolo.movimento")
			.usingColumns("id_movimento", "data_envio_origem", "id_usuario_origem", "id_unidade_origem", "id_unidade_destino", "id_processo" );
		int pk = getJdbcTemplate().queryForInt("select nextval('protocolo.seq_idmovimento')");
		insert.execute(new MapSqlParameterSource()
			.addValue("id_movimento", pk)
			.addValue("data_envio_origem", new Date())
			.addValue("id_usuario_origem", movimento.getUsuarioOrigem())
			.addValue("id_unidade_origem", movimento.getUndOrigem().getId())
			.addValue("id_unidade_destino", movimento.getUndDestino().getId())
			.addValue("id_processo", movimento.getIdProcesso())
		);
		return pk;
	}
	
	/**
	 * Atualiza o movimento atual do processo.
	 * @param processo
	 */
	public void atualizarMovimentoAtual(ProcessoDTO processo) {
		getJdbcTemplate().update("update protocolo.processo set id_movimento_atual = ? where id_processo = ?", 
				new Object[]{ processo.getIdMovimentoAtual(), processo.getId() });
	}

	/**
	 * Cria um interessado.
	 * @param processo
	 */
	public int criarInteressado(InteressadoDTO interessado) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
		insert.withTableName("protocolo.interessado")
			.usingColumns("idinteressado", "identificador", "nome", "tipo", "ativo", "id_pessoa", "id_servidor", "id_unidade", "id_aluno");
		int pk = getJdbcTemplate().queryForInt("select nextval('protocolo.seq_idinteressado')");
		insert.execute(new MapSqlParameterSource()
			.addValue("idinteressado", pk)
			.addValue("identificador", interessado.getIdentificador())
			.addValue("nome", interessado.getNome())
			.addValue("tipo", interessado.getIdTipo())
			.addValue("ativo", interessado.isAtivo())
			.addValue("id_pessoa", interessado.getIdPessoa())
			.addValue("id_servidor", interessado.getIdServidor())
			.addValue("id_unidade", interessado.getIdUnidade())
			.addValue("id_aluno", interessado.getIdAluno())
		);
		return pk;
	}
	
	/**
	 * Associa um interessado a um processo.
	 * @param processo
	 */
	public int criaInteressadoProcesso(ProcessoInteressado interessadoProcesso) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
		insert.withTableName("protocolo.processo_interessado")
			.usingColumns("id_processo_interessado", "id_processo", "idinteressado");
		int pk = getJdbcTemplate().queryForInt("select nextval('protocolo.seq_interessado_processo')");
		insert.execute(new MapSqlParameterSource()
			.addValue("id_processo_interessado", pk)
			.addValue("id_processo", interessadoProcesso.getIdProcesso())
			.addValue("idinteressado", interessadoProcesso.getIdInteressado())
		);
		return pk;
	}
	
	/**
	 * Recebe um processo. Esse movimento refere-se ao movimento atual do processo.
	 */
	public void receberProcesso(MovimentoDTO movimento) {
		getJdbcTemplate().update("update protocolo.movimento set (id_usuario_destino, data_recebimento_destino) values (?,?) where id_movimento = ?", 
				new Object[]{ movimento.getUsuarioDestino() , new Date(), movimento.getId()});
	}
	
	/**
	 * Envia um processo. Esse movimento será criado.
	 */
	public int enviarProcesso(MovimentoDTO movimento) {
		return criarMovimento(movimento);
	}

	RowMapper processoMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			ProcessoDTO proc = new ProcessoDTO();
			proc.setId(rs.getInt("id_processo"));
			proc.setAssunto(rs.getString("assunto"));
			proc.setAno(rs.getInt("ano"));
			proc.setStatus(rs.getInt("status"));
			proc.setNumProtocolo(rs.getInt("num_protocolo"));
			proc.setIdtipoProcesso(rs.getInt("id_tipo_processo"));
			proc.setObservacao(rs.getString("observacao"));
			proc.setUnidadeOrigem(new UnidadeDTO());
			proc.getUnidadeOrigem().setDenominacao(rs.getString("unidade"));
			proc.getUnidadeOrigem().setSigla(rs.getString("sigla"));
			return proc;
		}
	};
	
	/**
	 * Busca processos de acordo com número e/ou o ano do processo informado.
	 * 
	 * @param numero
	 * @param ano
	 * @param tipoProcesso
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 * @throws SQLException 
	 */
	public List<ProcessoDTO> findByNumeroAno(Integer numero,Integer ano,Integer tipoProcesso, Integer idUnidade,PagingInformation paginacao) throws DAOException, SQLException {
			Statement st = null;
			Statement countSt = null;
		try{
			String sql ="SELECT p.*,u.nome as unidade,u.sigla as sigla FROM PROTOCOLO.PROCESSO p, comum.unidade u WHERE u.id_unidade = p.id_unidade_origem ";

			if(numero != null)
				sql += " AND p.NUM_PROTOCOLO = " + numero;
			
			if(ano != null)
				sql += " AND p.ANO = " + ano;
			
			if(tipoProcesso != null)
				sql += " AND p.ID_TIPO_PROCESSO = " + tipoProcesso;
			
			if(idUnidade > 0)
				sql += " AND p.id_unidade_origem = " + idUnidade;
		
			if (paginacao != null) {
				countSt = getConnection().createStatement();
				String contador = "SELECT count(p.*) FROM PROTOCOLO.PROCESSO p , comum.unidade u WHERE u.id_unidade = p.id_unidade_origem ";
					if(numero != null)
						contador += " AND p.NUM_PROTOCOLO = " + numero;
					
					if(ano != null)
						contador += " AND p.ANO = " + ano;
					
					if(tipoProcesso != null)
						contador += " AND p.ID_TIPO_PROCESSO = " + tipoProcesso;
					
					if(idUnidade > 0)
						contador += " AND p.id_unidade_origem = " + idUnidade;
			
				ResultSet countRs = countSt.executeQuery(contador);
				countRs.next();
				
				int count = countRs.getInt(1);
					paginacao.setTotalRegistros(count);
					sql += " " + BDUtils.limit(paginacao.getTamanhoPagina()) + " offset " + ((paginacao.getPaginaAtual() - 1) * paginacao.getTamanhoPagina());
			}
			
			st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(sql);
			ArrayList<ProcessoDTO> list = new ArrayList<ProcessoDTO>();

			while (rs.next()) {
				ProcessoDTO proc = new ProcessoDTO();
				proc.setId(rs.getInt("id_processo"));
				proc.setAssunto(rs.getString("assunto"));
				proc.setAno(rs.getInt("ano"));
				proc.setStatus(rs.getInt("status"));
				proc.setNumProtocolo(rs.getInt("num_protocolo"));
				proc.setIdtipoProcesso(rs.getInt("id_tipo_processo"));
				proc.setObservacao(rs.getString("observacao"));
				proc.setUnidadeOrigem(new UnidadeDTO());
				proc.getUnidadeOrigem().setDenominacao(rs.getString("unidade"));
				proc.getUnidadeOrigem().setSigla(rs.getString("sigla"));
				list.add(proc);
			}
			
			return list;
			
			}catch (Exception e) {
				throw new DAOException(e);
			}
	}
	
	/**
	 * Busca processos de acordo com número e/ou o ano do processo informado, e se deseja retornar processos classificados ou não classificados.
	 * 
	 * @param numero
	 * @param ano
	 * @param tipoProcesso
	 * @param paginacao
	 * @param classificado
	 * @return
	 * @throws DAOException
	 * @throws SQLException 
	 */
	public List<ProcessoDTO> findByNumeroAnoClassificados(Integer numero,Integer ano,Integer tipoProcesso, Integer idUnidade,PagingInformation paginacao, boolean classificado) throws DAOException, SQLException {
			Statement st = null;
			Statement countSt = null;
		try{
			String sql ="SELECT p.*,u.nome as unidade,u.sigla as sigla FROM PROTOCOLO.PROCESSO p, comum.unidade u WHERE u.id_unidade = p.id_unidade_origem ";
			if (classificado) {
				sql += " and p.id_processo in (select id_processo from comissoes.processo_comissao) ";
			} else {
				sql += " and p.id_processo not in (select id_processo from comissoes.processo_comissao) ";
			}
			if(numero != null)
				sql += " AND p.NUM_PROTOCOLO = " + numero;
			
			if(ano != null)
				sql += " AND p.ANO = " + ano;
			
			if(tipoProcesso != null)
				sql += " AND p.ID_TIPO_PROCESSO = " + tipoProcesso;
			
			if(idUnidade > 0)
				sql += " AND p.id_unidade_origem = " + idUnidade;
		
			if (paginacao != null) {
				countSt = getConnection().createStatement();
				String contador = "SELECT count(p.*) FROM PROTOCOLO.PROCESSO p , comum.unidade u WHERE u.id_unidade = p.id_unidade_origem ";
					if(numero != null)
						contador += " AND p.NUM_PROTOCOLO = " + numero;
					
					if(ano != null)
						contador += " AND p.ANO = " + ano;
					
					if(tipoProcesso != null)
						contador += " AND p.ID_TIPO_PROCESSO = " + tipoProcesso;
					
					if(idUnidade > 0)
						contador += " AND p.id_unidade_origem = " + idUnidade;
			
				ResultSet countRs = countSt.executeQuery(contador);
				countRs.next();
				
				int count = countRs.getInt(1);
					paginacao.setTotalRegistros(count);
					sql += " " +
					BDUtils.limit(getPageSize()) + " offset " + (getPageNum() - 1) * getPageSize();
			}
			
			st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(sql);
			ArrayList<ProcessoDTO> list = new ArrayList<ProcessoDTO>();

			while (rs.next()) {
				ProcessoDTO proc = new ProcessoDTO();
				proc.setId(rs.getInt("id_processo"));
				proc.setAssunto(rs.getString("assunto"));
				proc.setAno(rs.getInt("ano"));
				proc.setStatus(rs.getInt("status"));
				proc.setNumProtocolo(rs.getInt("num_protocolo"));
				proc.setIdtipoProcesso(rs.getInt("id_tipo_processo"));
				proc.setObservacao(rs.getString("observacao"));
				proc.setUnidadeOrigem(new UnidadeDTO());
				proc.getUnidadeOrigem().setDenominacao(rs.getString("unidade"));
				proc.getUnidadeOrigem().setSigla(rs.getString("sigla"));
				list.add(proc);
			}
			
			return list;
			
			}catch (Exception e) {
				throw new DAOException(e);
			}
	}
	
	/**
	 * Busca o processo de acordo com identificador do processo
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public ProcessoDTO findByChave(Integer id) throws DAOException {

			String sql ="SELECT p.*,u.nome as unidade,u.sigla as sigla FROM PROTOCOLO.PROCESSO p, comum.unidade u WHERE u.id_unidade = p.id_unidade_origem AND p.ID_PROCESSO = ?  " ;
			
			Object[] lista = {id};
			try{
				return (ProcessoDTO) getJdbcTemplate(Sistema.SIPAC).queryForObject(sql, lista, processoMapper);
			}catch (EmptyResultDataAccessException e) {
				return null;
			}

	}
	
	
}
