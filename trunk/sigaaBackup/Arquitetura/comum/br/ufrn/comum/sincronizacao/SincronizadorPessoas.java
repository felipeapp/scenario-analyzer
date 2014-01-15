/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2006
 */
package br.ufrn.comum.sincronizacao;

import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;

/**
 * Classe para sincronizar o cadastro de pessoas nos três bancos
 * (Comum, Administrativo e Acadêmico).
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class SincronizadorPessoas {
	
	private static final SincronizadorPessoas MOCK = new SincronizadorPessoas((DataSource) null) {
		public void sincronizarEmailPessoa(PessoaGeral p) { }
		public int sincronizarPessoa(PessoaGeral p) { return -1; }
	};

	private JdbcTemplate jt;
	
	private SincronizadorPessoas(DataSource ds) {
		if (ds != null)
			this.jt = new JdbcTemplate(ds);
	}

	/**
	 * Verifica se uma pessoa existe no banco que está sendo sincronizado através do CPF
	 * @param p
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	private boolean existePessoa(PessoaGeral p) {
		if (buscaPessoaPorCpf(p) != -1)
			return true;
		return false;
	}
	
	/**
	 * Verifica se uma pessoa existe no banco que está sendo sincronizado através do ID.
	 * @param idPessoa
	 * @return
	 */
	private boolean existePessoa(int idPessoa) {
		
		try {
			
			int pessoaDestino = jt.queryForInt("SELECT id_pessoa FROM comum.pessoa WHERE id_pessoa = ?", new Object[] {idPessoa});
			
			return pessoaDestino >= 0;
			
		}catch(EmptyResultDataAccessException e) {
			return false;
		}
		
	}

	/**
	 * Busca uma pessoa no banco do SIGAA
	 * @param p
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	private int buscaPessoaPorCpf(PessoaGeral p) {
		if (p.getCpf_cnpj() != null) {
			try {
				return jt.queryForInt("select id_pessoa from comum.pessoa where cpf_cnpj = ?", new Object[] { p.getCpf_cnpj() });
			} catch(EmptyResultDataAccessException e) {
				return -1;
			}
		}

		return -1;
	}
	
	/**
	 * Sincroniza o banco do sigaa com sipac.
	 * @param p
	 * @throws DAOException
	 */
	public int sincronizarPessoa(PessoaGeral p) {
		if (!existePessoa(p)) {
			p.setId(getNextIdPessoa());
			jt.update("insert into comum.pessoa "
					+ "(id_pessoa, nome, data_nascimento, sexo, passaporte, cpf_cnpj, "
					+ "email, endereco, tipo, valido, funcionario, nome_ascii) values "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] {p.getId(), p.getNome(), p.getDataNascimento(), String.valueOf(p.getSexo()), p.getPassaporte(), p.getCpf_cnpj(), p.getEmail(),
							p.getEndereco(), String.valueOf(p.getTipo()), p.isValido(), p.isFuncionario() == null ? false : p.isFuncionario(), StringUtils.toAscii(p.getNome()) });
			return p.getId();
		} else {
			int idPessoa = buscaPessoaPorCpf(p);
			jt.update("update comum.pessoa set "
					+ "nome = ?, data_nascimento = ?, sexo = ?, passaporte = ?, cpf_cnpj = ?, "
					+ "email = ?, endereco = ?, tipo = ?, valido = ?, funcionario = ?, nome_ascii = ? where id_pessoa = ?", 
					new Object[] { p.getNome(), p.getDataNascimento(), String.valueOf(p.getSexo()), p.getPassaporte(), p.getCpf_cnpj(), p.getEmail(),
							p.getEndereco(), String.valueOf(p.getTipo()), p.isValido(), p.isFuncionario() == null ? false : p.isFuncionario(), StringUtils.toAscii(p.getNome()), idPessoa });
			return idPessoa;
		}
	}

	/**
	 * Sincroniza o Email da Pessoa passada como parâmetro
	 * @param p
	 * @throws DAOException
	 */
	public void sincronizarEmailPessoa(PessoaGeral p) {
		jt.update("update comum.pessoa set email = ? where cpf_cnpj = ?", new Object[] { p.getEmail(), p.getCpf_cnpj() });
	}

	public static SincronizadorPessoas usandoDataSource(DataSource ds) {
		if (ds == null) {
			return MOCK;
		} else {
			return new SincronizadorPessoas(ds);
		}
	}
	
	public static SincronizadorPessoas usandoSistema(Movimento mov, int sistema) {
		
		 DataSource ds = null;
		 
		 if (Sistema.isSistemaAtivo(sistema)) {
			 ds = Database.getInstance().getDataSource(sistema);
		 }
		 
		 SincronizadorPessoas sincronizadorPessoas = usandoDataSource(ds);
		 
		 return sincronizadorPessoas;
	 }
	
	public static int getNextIdPessoa() {
		return new JdbcTemplate(Database.getInstance().getComumDs()).queryForInt("select nextval('comum.pessoa_seq')");
	}

	/**
	 * Cria o objeto de batch de INSERT para a sincronização.
	 * @return
	 */
	public BatchSqlUpdate prepareBatchInsertDeSincronizacao() {
		
		BatchSqlUpdate insertBatch = new BatchSqlUpdate(jt.getDataSource(), "insert into comum.pessoa "
					+ "(id_pessoa, nome, data_nascimento, sexo, passaporte, cpf_cnpj, "
					+ "email, endereco, tipo, valido, funcionario) values "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.DATE));
		insertBatch.declareParameter(new SqlParameter(Types.CHAR));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.BIGINT));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.CHAR));
		insertBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		insertBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		
		insertBatch.setBatchSize(500);
		
		insertBatch.compile();
		
		return insertBatch;
	}

	/**
	 * Cria o objeto de batch de UPDATE para a sincronização.
	 * @return
	 */
	public BatchSqlUpdate prepareBatchUpdateDeSincronizacao() {
		
		BatchSqlUpdate updateBatch = new BatchSqlUpdate(jt.getDataSource(), "update comum.pessoa set "
				+ "nome = ?, data_nascimento = ?, sexo = ?, passaporte = ?, cpf_cnpj = ?, "
				+ "email = ?, endereco = ?, tipo = ?, valido = ?, funcionario = ? where id_pessoa = ?");
	
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.DATE));
		updateBatch.declareParameter(new SqlParameter(Types.CHAR));
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.BIGINT));
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.CHAR));
		updateBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		updateBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		
		updateBatch.setBatchSize(5000);
		
		updateBatch.compile();
		
		return updateBatch;
	}

	/**
	 * Sincroniza a pessoa usando o objeto de atualização por batch.
	 * Os métodos {@link #prepareBatchInsertDeSincronizacao()} e {@link #prepareBatchUpdateDeSincronizacao()}
	 * podem ser usados para gerar o batches.
	 * 
	 * @param pessoa
	 * @param insertBatch
	 * @param updateBatch
	 * @return idPessoa atualizado
	 */
	public int sincronizarPessoaUsandoBatch(PessoaGeral pessoa, BatchSqlUpdate insertBatch, BatchSqlUpdate updateBatch) {
		
		int idPessoa = buscaPessoaPorCpf(pessoa);
		char sexo = 'N';
		
		if (Character.isLetter(pessoa.getSexo()) && (pessoa.getSexo() == 'N' || pessoa.getSexo() == 'M' 
			|| pessoa.getSexo() == 'F')) {
			sexo = Character.toUpperCase(pessoa.getSexo());
		}
		
		if (idPessoa < 0) {
			
			idPessoa = pessoa.getId();
			
			if (existePessoa(idPessoa)) {
				
				idPessoa = getNextIdPessoa();
				System.out.println("PESSOA EXISTENTE: " + pessoa.getId() + ". Recuperando próximo ID: " + idPessoa);
			}
			
			insertBatch.update(new Object[] {idPessoa, pessoa.getNome(), pessoa.getDataNascimento(), 
					sexo, pessoa.getPassaporte(), pessoa.getCpf_cnpj(), pessoa.getEmail(),
					pessoa.getEndereco(), String.valueOf(pessoa.getTipo()), pessoa.isValido(), 
					(pessoa.isFuncionario() == null ? false : pessoa.isFuncionario())});
			
		} else {
			
			updateBatch.update(new Object[] {pessoa.getNome(), pessoa.getDataNascimento(), 
					sexo, pessoa.getPassaporte(), pessoa.getCpf_cnpj(), pessoa.getEmail(), pessoa.getEndereco(), 
					pessoa.getTipo(), pessoa.isValido(), 
					(pessoa.isFuncionario() == null ? false : pessoa.isFuncionario()), idPessoa});
		}
		
		return idPessoa;
	}
}
