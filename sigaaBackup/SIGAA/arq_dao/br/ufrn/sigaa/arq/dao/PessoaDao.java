/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/10/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/** Classe para consultas específicas em Pessoa.
 * @author Édipo Elder F. Melo
 *
 */
public class PessoaDao extends GenericSigaaDAO {

	/** Retorna uma lista de Pessoa por nome e tipo informados.
	 * @param nome
	 * @param tipo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findByNomeTipo(String nome, char tipo, PagingInformation paging) throws DAOException {

		try {
			Query q = getSession().createQuery("select p.id, p.nome, p.cpf_cnpj from Pessoa p " +
				"where " + UFRNUtils.toAsciiUpperUTF8("p.nome") + " like "
						+ UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'") +
						"and p.tipo = '" + tipo + "' and p.cpf_cnpj is not null order by p.nome asc" );
			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			List<Pessoa> pessoas = new ArrayList<Pessoa>();

			for (Iterator<Object[]> it = result.iterator(); it.hasNext(); ) {
				Object[] linha = it.next();
				Pessoa p = new Pessoa();
				p.setId((Integer) linha[0]);
				p.setNome((String) linha[1]);
				p.setCpf_cnpj((Long) linha[2]);

				pessoas.add(p);
			}

			return pessoas;

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}
	
	/** Retorna uma lista de Pessoa por nome e tipo informados que não é servidor nem docente externo.
	 * @param nome
	 * @param tipo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findByNomeTipoNaoServidorNaoDocenteExterno(String nome, char tipo) throws DAOException {

		try {
			
			Query q = getSession().createQuery(" select p " +
											   " from Pessoa p " +
											   " where " + UFRNUtils.toAsciiUpperUTF8("p.nome") + " like " +
											   UFRNUtils.toAsciiUTF8("'" + nome.toUpperCase() + "%'") +
											   " and p.tipo = '" + tipo + "' and p.cpf_cnpj is not null" +
											   " and p.id not in " +
											   "	( 	select p.id " +
											   "		from Servidor as s " +
											   "		inner join s.pessoa as p" +
											   "	) " +
											   " and p.id not in " +
											   "	( 	select p.id " +
											   "		from DocenteExterno as de " +
											   "		inner join de.pessoa as p " +
											   "		where de.ativo = trueValue()  " +
											   "	) " +
											   " order by p.nome asc" );
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma lista de Pessoa por CPF/CNPJ e tipo informados.
	 * @param num
	 * @param tipo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findByCpfCnpjTipo(long num, char tipo, PagingInformation paging) throws DAOException {

		Query q = getSession().createQuery("select new Pessoa(p.id, p.nome, p.cpf_cnpj) from Pessoa p " +
				"where cpf_cnpj=" + num +
						" and p.tipo = '" + tipo + "'");
		try {
			preparePaging(paging, q);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		@SuppressWarnings("unchecked")
		Collection<Pessoa> lista = q.list();
		return lista;

	}
	
	/** Retorna um mapa com o par <CPF, Pessoa> a ser usado como cache em operações de processamento em lote.
	 * @param cpfs
	 * @return
	 * @throws DAOException
	 */
	public Map<Long, Pessoa> findByCpfCnpj(Collection<Long> cpfs) throws DAOException {
		Map<Long, Pessoa> mapa = new TreeMap<Long, Pessoa>();
		if (!isEmpty(cpfs)) {
			Criteria c = getSession().createCriteria(Pessoa.class);
			c.add(Restrictions.in("cpf_cnpj", cpfs));
			@SuppressWarnings("unchecked")
			List<Pessoa> pessoas = c.list();
			for (Long cpf : cpfs) {
				for (Pessoa p : pessoas) {
					if (p.getCpf_cnpj().equals(cpf)) {
						mapa.put(cpf, p);
					}
				}
			}
		}
		return mapa;
	}

	/** Retorna uma lista de Pessoa por CPF/CNPJ informados.
	 * @param cpfCnpj
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findByCpfCnpj(long cpfCnpj) throws DAOException {
		Query q = getSession().createQuery("select new Pessoa(p.id, p.nome, p.cpf_cnpj) from Pessoa p " +
				"where cpf_cnpj=" + cpfCnpj);
		@SuppressWarnings("unchecked")
		Collection<Pessoa> lista = q.list();
		return lista;
	}

	/** Indica se exite o cadastro de uma pessoa com o mesmo CPF.
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public boolean existePessoa(Pessoa pessoa) throws DAOException {
		StringBuffer hql = new StringBuffer();

		hql.append("SELECT id FROM Pessoa WHERE ");
		hql.append("cpf_cnpj = " + pessoa.getCpf_cnpj());
		hql.append(" and id<>"+pessoa.getId());

		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Integer> l = q.list();
		return (l != null && l.size() > 0);
	}

	/** Retorna o ID do registro da pessoa, buscando por CPF.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdByCpf(long cpf) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT id FROM Pessoa WHERE ");
		hql.append("cpf_cnpj = " + cpf);
		Query q = getSession().createQuery(hql.toString());

		@SuppressWarnings("unchecked")
		List<Integer> l = q.list();
		if ((l != null && l.size() > 0)) {
			return l.iterator().next();
		} else
			return 0;
	}

	/** Encontra o registro mais recente de uma pessoa, de acordo com o CPF informado.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findMaisRecenteByCPF(long cpf) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM Pessoa WHERE ");
		hql.append("cpf_cnpj = " + cpf);
		hql.append(" ORDER BY ultimaAtualizacao desc");
		Query q = getSession().createQuery(hql.toString());
		q.setMaxResults(1);
		return  (Pessoa) q.uniqueResult();
	}

	/** Encontra o registro de uma pessoa, de acordo com o CPF informado.
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findByCpf(long cpf) throws DAOException {
		return findByCpf(cpf, false);
	}

	/** Encontra o registro de uma pessoa, de acordo com o CPF informado.
	 * @param cpf
	 * @param leve
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findByCpf(long cpf, boolean leve) throws DAOException {
		try {
			StringBuilder query = new StringBuilder();

			if ( leve  ) {
				query.append(" select new Pessoa(p.id, p.nome, p.cpf_cnpj) ");
			}

			query.append("from Pessoa p where valido = trueValue() and cpf_cnpj =  " + cpf);

			// Buscar pessoas de origem graduação
			return (Pessoa) getSession().createQuery(query.toString()).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Encontra o registro de uma pessoa, de acordo com o passaporte informado.
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findByPassaporte(String passaporte) throws DAOException {
		try {
			@SuppressWarnings("unchecked")
			Collection<Pessoa> lista = getSession().createQuery("from Pessoa where valido=trueValue() and passaporte='"+passaporte+"'").list();
			if (lista != null && lista.size() > 0)
				return lista.iterator().next();
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna o registro de uma Pessoa, buscando pelo o ID, de forma otimizada.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findCompleto(int id) throws DAOException {

		Pessoa pessoa = null;
		try {
			getSession().clear();
			pessoa = (Pessoa) getSession().createCriteria(Pessoa.class)
            	.add( Restrictions.idEq(id) )
            	.uniqueResult();

			if (pessoa == null) return null;

			pessoa.setEnderecoContato( (Endereco) HibernateUtils.getTarget( pessoa.getEnderecoContato() ));
			if (pessoa.getEnderecoContato() != null) {
				Endereco endereco = pessoa.getEnderecoContato();
				endereco.setMunicipio((Municipio) initializeClone( endereco.getMunicipio()) );
				if (endereco.getMunicipio() != null) {
					endereco.getMunicipio().setUnidadeFederativa( (UnidadeFederativa) HibernateUtils.getTarget( endereco.getMunicipio().getUnidadeFederativa() ) );
				}
				endereco.setPais( (Pais) HibernateUtils.getTarget(endereco.getPais()) );
				endereco.setUnidadeFederativa( (UnidadeFederativa) initializeClone(endereco.getUnidadeFederativa()) );
			}

			if (pessoa.getIdentidade() != null) {
				Identidade identidade = pessoa.getIdentidade();
				identidade.setUnidadeFederativa( (UnidadeFederativa) initializeClone( identidade.getUnidadeFederativa()) );
			}

			if (pessoa.getTituloEleitor() != null) {
				TituloEleitor titulo = pessoa.getTituloEleitor();
				titulo.setUnidadeFederativa( (UnidadeFederativa) initializeClone( titulo.getUnidadeFederativa()) );
			}

			if (pessoa.getContaBancaria() != null) {
				pessoa.setContaBancaria( (ContaBancaria) HibernateUtils.getTarget( pessoa.getContaBancaria()));
				ContaBancaria conta = pessoa.getContaBancaria();
				conta.setBanco((Banco) HibernateUtils.getTarget( conta.getBanco()));
			}

			pessoa.setEstadoCivil( (EstadoCivil) HibernateUtils.getTarget( pessoa.getEstadoCivil() ) );
			pessoa.setMunicipio((Municipio) initializeClone( pessoa.getMunicipio() ) );
			pessoa.setPais((Pais) HibernateUtils.getTarget( pessoa.getPais() ) );
			pessoa.setTipoRaca( (TipoRaca) HibernateUtils.getTarget( pessoa.getTipoRaca() ) );
			pessoa.setUnidadeFederativa( (UnidadeFederativa) initializeClone( pessoa.getUnidadeFederativa()) );

		} catch (Exception e) {
			throw new DAOException(e);
		}
		return pessoa;
	}

	/** Retorna uma lista de  {@link PessoaDto}, de acordo com o nome e tipo informado.
	 * @param nome
	 * @param tipoPessoa
	 * @return
	 */
	public List<PessoaDto> findPessoaByNome(String nome, int tipoPessoa) {
		
		StringBuilder sql = new StringBuilder();
		
		final int tipoPessoaABuscar = tipoPessoa;
		if (tipoPessoa == 1) { // todos
			sql.append("select p.nome, cpf_cnpj from comum.pessoa p where ");
		}

		if (tipoPessoa == 3) { // por discente
			sql.append("select cpf_cnpj, d.matricula, p.nome, c.nome, u.sigla from discente d ");
			sql.append("inner join comum.pessoa p on p.id_pessoa = d.id_pessoa ");
			sql.append("inner join curso c on c.id_curso = d.id_curso ");
			sql.append("inner join comum.unidade u using(id_unidade) ");
			sql.append("where d.status in (1, 2, 8, 9) and ");
		}

		if (tipoPessoa == 2) { // por servidor
			sql.append("select cpf_cnpj, siape, p.nome, lotacao from rh.servidor s ");
			sql.append("inner join comum.pessoa p on p.id_pessoa = s.id_pessoa where ");
		}
		
			sql.append(UFRNUtils.convertUtf8UpperLatin9("p.nome") 
				+ " like "
				+ UFRNUtils.toAsciiUpperUTF8("'" + nome.trim() + "%'"));
			sql.append("order by p.nome");
		@SuppressWarnings("unchecked")
		List<PessoaDto> lista = getJdbcTemplate().query(sql.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				PessoaDto p = new PessoaDto();
				
				if (tipoPessoaABuscar == 1) { // todos
					p.setCpf(rs.getLong("cpf_cnpj"));
					p.setNome(rs.getString("nome"));
				}
				if (tipoPessoaABuscar == 2) { // servidor
					p.setCpf(rs.getLong("cpf_cnpj"));
					p.setNome(rs.getString("nome"));
					p.setSiape(rs.getLong("siape"));
					p.setLotacao(rs.getString("lotacao"));
				}
				if (tipoPessoaABuscar == 3) { // discente
					p.setCpf(rs.getLong("cpf_cnpj"));
					p.setMatricula(rs.getLong("matricula"));
					p.setNome(rs.getString(3));
					p.setCurso(rs.getString(4));
					p.setCentro(rs.getString(5));
				}
				
				return p;
			}
		});
		return lista;
	}


	/**
	 * localiza o tipo do dedo cadastrado no sistema (direito ou esquerdo)
	 * @param cpf
	 * @param tipoDedo
	 * @return
	 * @throws SQLException
	 */
	public String findTipoDedoCadastradoByCPF(Long cpf, String tipoDedo) throws SQLException {
		
		Connection conComum = getDataSource(Sistema.COMUM).getConnection();
		Statement st = null;
		ResultSet rs = null;
		try {
			String sqlRipoDedo = 
				"select * from comum.identificacao_pessoa where cpf = " + cpf + 
				" and dedo_coletado = " + "'" + tipoDedo + "'";
	
			String sqlCpf = 
				"select * from comum.identificacao_pessoa where cpf = " + cpf;
				
			String sql = "";
			if (tipoDedo == null)
				sql = sqlCpf;
			else
				sql = sqlRipoDedo;
				
			st = conComum.createStatement();
	        rs = st.executeQuery(sql);
	
			while (rs.next())
	        	return rs.getString("dedo_coletado");
	        				
		} finally {
			if (rs!=null)
				rs.close();
			if (st!=null)
				st.close();
		}
        return null;
	}
	
	/**
	 * Se não existir digital cadastrada, grava a identificação da pessoa 
	 * 
	 * @param cpf
	 * @param imagem = fluxo bytes que representa a foto do usuário
	 * @param digital = fluxo bytes que representa os pontos biométricos da digital do usuário
	 * @param tipoDedo
	 * @param idUsuarioLogadoDesktop
	 * @param imagemDigital = fluxo bytes que representa a digital do usuário
	 * @return
	 * @throws DAOException
	 */
	public boolean gravarIdentificacao(Long cpf, byte[] imagem, byte[] digital, String tipoDedo, int idUsuarioLogadoDesktop, byte[] imagemDigital) throws DAOException {
		UsuarioGeral usuarioGeral = getUsuarioGeralByID(idUsuarioLogadoDesktop);
		setUsuario(usuarioGeral);
		setSistema(Sistema.COMUM);
		
		update("INSERT INTO comum.IDENTIFICACAO_PESSOA VALUES (?,?,?,?,?,?,?)", 
				new Object[] { cpf, imagem, digital, tipoDedo, idUsuarioLogadoDesktop, new Date(), imagemDigital} );
		
		return true;
	}

	/**
	 * Recupera o Usuário Geral de acordo com o idUsuarioLogado
	 * @param idUsuarioLogado
	 * @return
	 * @throws DAOException
	 */
	private UsuarioGeral getUsuarioGeralByID(int idUsuarioLogado) throws DAOException {
			UsuarioDAO daoArq = new UsuarioDAO();
			UsuarioGeral usuarioGeral;
			usuarioGeral = daoArq.findByPrimaryKey(idUsuarioLogado, UsuarioGeral.class);
			RegistroEntrada registroEntrada = new RegistroEntrada(); registroEntrada.setId(idUsuarioLogado);
			usuarioGeral.setRegistroEntrada(registroEntrada);
		return usuarioGeral;
	}
	
	/**
	 * Atualiza os dados do discente caso já exista digital cadastrada
	 * 
	 * @param cpf
	 * @param imagem = fluxo bytes que representa a foto do usuário
	 * @param digital = fluxo bytes que representa os pontos biométricos da digital do usuário
	 * @param tipoDedo
	 * @param idUsuarioLogadoDesktop
	 * @param imagemDigital = fluxo bytes que representa a imagem digital do usuário
	 * @return
	 * @throws DAOException
	 */
	public boolean updateIdentificacao(Long cpf, byte[] imagem, byte[] digital, String tipoDedo, int idUsuarioLogadoDesktop, byte[] imagemDigital) throws DAOException {
		UsuarioGeral usuarioGeral = getUsuarioGeralByID(idUsuarioLogadoDesktop);
		setUsuario(usuarioGeral);
		setSistema(Sistema.COMUM);
		
		update("UPDATE comum.IDENTIFICACAO_PESSOA " +
				"SET cpf = ?, foto = ?, digital = ?, dedo_coletado = ?, id_usuario = ?, data_cadastro = ?,  imagem_digital = ? " +
				"where cpf = ? and dedo_coletado = ? ",				
				new Object[] {cpf, imagem, digital, tipoDedo, idUsuarioLogadoDesktop, new Date(), imagemDigital, cpf, tipoDedo});
		
		return true;
	}

	/** Retorna a permissão de cadastro de uma pessoa.
	 * @param idUsuario
	 * @return
	 */
	public String findTipoPermissaoCadastroDigitalPessoa(int idUsuario) {
		return (String) getJdbcTemplate(getDataSource(Sistema.SIGAA)).queryForObject("SELECT permissao_cadastro from sae.permissao_cadastro_pessoas where id_usuario = ?", 
				new Object[] {idUsuario}, String.class);
	}

    /** Verifica se existe digital cadastrada para uma lista de {@link PessoaDto}
     * @param pessoas
     * @return
     */
    public List<PessoaDto> verificarExistenciaDigital(List<PessoaDto> pessoas) {
        
    	Set<Long> cpfsBuscados = new HashSet<Long>();
    	for (PessoaDto pessoasLocalizada : pessoas) {
    		cpfsBuscados.add(pessoasLocalizada.getCpf());
    	}
    	
    	String sql = "SELECT * FROM comum.IDENTIFICACAO_PESSOA where cpf in " + UFRNUtils.gerarStringIn(cpfsBuscados);
    	
        @SuppressWarnings("unchecked")
		List<PessoaDto> pessoasDigitais = (List<PessoaDto>) getJdbcTemplate( getDataSource(Sistema.COMUM) ).query(sql, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

                List<PessoaDto> pessoasDigitais = new ArrayList<PessoaDto>(); 
                
                while (rs.next()) {
                	PessoaDto pessoaDTO = new PessoaDto();
                    pessoaDTO.setCpf(rs.getLong(1));
                    pessoaDTO.setDigital(rs.getBytes(2));
                    
                    pessoasDigitais.add(pessoaDTO);
                }
                
                return pessoasDigitais;
            }
          });
        
		for (int j = 0; j < pessoasDigitais.size(); j++) {
			for (int i = 0; i < pessoas.size(); i++) {
				if ( pessoas.get(i).getCpf() == pessoasDigitais.get(j).getCpf() ) {
					pessoas.get(i).setDigital(pessoasDigitais.get(j).getDigital());
				}
			}
		}
        return pessoas;
    }


	/** Retorna a imagem de uma digital, buscando por CPF.
	 * @param cpf
	 * @return
	 */
	public List<byte[]> findDigitalDiscenteByCPF(Long cpf) {
        String sql = "SELECT digital FROM comum.IDENTIFICACAO_PESSOA where cpf = ? ";
        
		@SuppressWarnings("unchecked")
		List<byte[]> digital = getJdbcTemplate(Sistema.COMUM).query(sql.toString(),  new Object[] { cpf }, new RowMapper() {
			public byte[] mapRow(ResultSet rs, int arg1) throws SQLException {
				byte[] b = rs.getBytes("digital");
				return b;
			}
        });
        return digital;
	}
	
	/**
	 * Retorna a data da última atualização dos dados pessoais de uma pessoa.
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Date findUltimaAtualizacao(int idPessoa) throws DAOException {
		Date data = (Date) getSession().createQuery("select ultimaAtualizacao from Pessoa where id = "+idPessoa).setMaxResults(1).uniqueResult();
		return data != null ? data : new Date(Long.MIN_VALUE);
	}
	
	public Pessoa findLeveByServidor(int idServidor) throws HibernateException, DAOException {
	    String projecao = "p.id, p.nome, p.email";
	    String hql = "SELECT " + projecao + " FROM Servidor s JOIN s.pessoa p WHERE s.id = :servidor";
	    
	    Query q = getSession().createQuery(hql);
	    
	    q.setInteger("servidor", idServidor);
	    q.setMaxResults(1);
	    
	    @SuppressWarnings("unchecked")
	    List<Pessoa> pessoas = (List<Pessoa>) HibernateUtils.parseTo(q.list(), projecao, Pessoa.class, "p");
	    
	    return isEmpty(pessoas) ? null : pessoas.get(0);
	}
	
	public Pessoa findLeveByDiscente(int idDiscente) throws HibernateException, DAOException {
	    String projecao = "p.id, p.nome, p.email";
	    String hql = "SELECT " + projecao + " FROM Discente d JOIN d.pessoa p WHERE d.id = :discente";
	    
	    Query q = getSession().createQuery(hql);
	    
	    q.setInteger("discente", idDiscente);
	    q.setMaxResults(1);
	    
	    @SuppressWarnings("unchecked")
	    List<Pessoa> pessoas = (List<Pessoa>) HibernateUtils.parseTo(q.list(), projecao, Pessoa.class, "p");
	    
	    return isEmpty(pessoas) ? null : pessoas.get(0);
	}
}
