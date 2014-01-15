/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *<p>Dao exclusivos para a consultas que envolvem cadastro de participante. </p>
 * 
 * @author jadson
 *
 */
public class CadastroParticipanteAtividadeExtensaoDao extends GenericSigaaDAO{
	
	/**
	 * <p>Método usado para realizar o login na área restrita de extensão.</p>
	 * <p>Retorna o cadastro ativo e confirmado para o email e senha informados, se tiver algum cadastro 
	 * o usuário pode ser logar no sistema.</p>
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public CadastroParticipanteAtividadeExtensao findCadastroParticipanteByEmailSenha(String email, String senha)throws DAOException {
		
		String projecao = " cadastro.id, cadastro.nome, cadastro.email, cadastro.dataNascimento ";
	
		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  CadastroParticipanteAtividadeExtensao cadastro " +
				" WHERE cadastro.email = :email AND cadastro.senha = :senha " +
				" AND cadastro.ativo = :true AND cadastro.confirmado = :true ";
			
		/* Yes, according to RFC 2821, the local mailbox (the portion before @) is considered case-sensitive. 
		 * However, typically e-mail addresses are not case-sensitive because of the difficulties and confusion 
		 * it would cause between users, the server, and the administrator. Therefore, when sending an e-mail, 
		 * it's safe to assume an e-mail address like "SUPPORT@computerhope.com" is the 
		 * same as "support@computerhope.com." */
		
		Query query = getSession().createQuery(hql);
		query.setString("email", StringUtils.notEmpty(email) ? email.toLowerCase() : email);
		query.setString("senha", senha);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		ArrayList<CadastroParticipanteAtividadeExtensao> c =  new ArrayList<CadastroParticipanteAtividadeExtensao>
					(HibernateUtils.parseTo(query.list(), projecao, CadastroParticipanteAtividadeExtensao.class, "cadastro"));
		
		if(c.size() > 0)
			return c.get(0); // usuário possui cadastro confirmado e é unico com o email passado.
		else
			return null;   // usuário não cadastrado
		
	}
		
	
	
	/**
	 * <p>Método usado buscas um cadastro pelo código de acesso e confirmar esse cadastro ou a alteração de senha</p>
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public CadastroParticipanteAtividadeExtensao findCadastroByCodigoAcesso(String codigoAcessoConfirmacao, Integer idCadatroParticipante, boolean confirmado) throws DAOException {
		
		String projecao = " cadastro.id, cadastro.nome, cadastro.email, cadastro.dataNascimento, cadastro.confirmado, cadastro.ativo, cadastro.senha, cadastro.senhaGerada ";
	
		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  CadastroParticipanteAtividadeExtensao cadastro " +
				" WHERE cadastro.codigoAcessoConfirmacao = :codigoAcessoConfirmacao AND cadastro.id = :idCadatroParticipante " +
				" AND cadastro.ativo = :true ";
			
		Query query = getSession().createQuery(hql);
		query.setString("codigoAcessoConfirmacao", codigoAcessoConfirmacao);
		query.setInteger("idCadatroParticipante", idCadatroParticipante);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		ArrayList<CadastroParticipanteAtividadeExtensao> c =  new ArrayList<CadastroParticipanteAtividadeExtensao>
					(HibernateUtils.parseTo(query.list(), projecao, CadastroParticipanteAtividadeExtensao.class, "cadastro"));
		
		if(c.size() > 0)
			return c.get(0); // usuário possui cadastro confirmado e é unico com o email passado.
		else
			return null;   // usuário não cadastrado
		
	}
	
	
	
	/**
	 * <p>Método usado buscas um cadastro com as informação usada para validar a alteração de senha de um usuário na área pública.</p>
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public CadastroParticipanteAtividadeExtensao findCadastroByInformacoesAlterarSenha(String email, Date dataNascimento, boolean estrangeiro, Long cpf, String passaporte) throws DAOException {
		
		/// IMPORANTE precisa das informações do CPF e passaporte e se é estragério para gerar o novo código de acesso.
		String projecao = " cadastro.id, cadastro.nome, cadastro.email, cadastro.cpf, cadastro.passaporte, cadastro.estrangeiro, cadastro.ativo, cadastro.senha, cadastro.senhaGerada ";
	
		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  CadastroParticipanteAtividadeExtensao cadastro " +
				" WHERE cadastro.email = :email AND cadastro.dataNascimento = :dataNascimento " +
				" AND cadastro.ativo = :true ";
		
		if(estrangeiro){
			hql += " AND passaporte = :passaporte ";
		}else{
			hql += " AND cpf = :cpf ";
		}
		
		Query query = getSession().createQuery(hql);
		query.setString("email", StringUtils.notEmpty(email) ? email.toLowerCase() : email);
		query.setDate("dataNascimento", dataNascimento);
		
		if(estrangeiro){
			query.setString("passaporte", passaporte);
		}else{
			if(cpf != null)
				query.setLong("cpf", cpf);
			else
				query.setLong("cpf", 0l);
		}
		
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		ArrayList<CadastroParticipanteAtividadeExtensao> c =  new ArrayList<CadastroParticipanteAtividadeExtensao>
					(HibernateUtils.parseTo(query.list(), projecao, CadastroParticipanteAtividadeExtensao.class, "cadastro"));
		
		if(c.size() > 0)
			return c.get(0); // usuário possui cadastro confirmado e é unico com o email passado.
		else
			return null;   // usuário não cadastrado
		
	}
	
	
	/**
	 * <p>Método que retornas as informações básicas do cadastro a partir do id .</p>
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public CadastroParticipanteAtividadeExtensao findCadastroParticipanteById(int idCadatroParticipante)throws DAOException {
		
		String projecao = " cadastro.id, cadastro.nome, cadastro.cpf, cadastro.passaporte, cadastro.dataNascimento, cadastro.email ";
	
		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  CadastroParticipanteAtividadeExtensao cadastro " +
				" WHERE cadastro.id = :idCadatroParticipante " +
				" AND cadastro.ativo = :true AND cadastro.confirmado = :true ";
			
		Query query = getSession().createQuery(hql);
		query.setInteger("idCadatroParticipante", idCadatroParticipante);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		ArrayList<CadastroParticipanteAtividadeExtensao> c =  new ArrayList<CadastroParticipanteAtividadeExtensao>
					(HibernateUtils.parseTo(query.list(), projecao, CadastroParticipanteAtividadeExtensao.class, "cadastro"));
		
		if(c.size() > 0)
			return c.get(0); // usuário possui cadastro confirmado e é unico com o email passado.
		else
			return null;   // usuário não cadastrado
		
	}
	
	
	/**
	 * <p>Método que implementa a busca padrão de participantes no sistema..</p>
	 * 
	 * <p>Usado quando o coordenador vai cadatrar algum participante, com o cadatro é único ele deve buscar para saber se já não existem.</p>
	 * 
	 * @return
	 * @throws DAOException
	 */	
	public List<CadastroParticipanteAtividadeExtensao> buscaPadraoParticipante(String cpf, String passaporte, String nome, String email)throws DAOException {
		
		String projecao = " cadastro.id, cadastro.cpf, cadastro.passaporte, cadastro.nome, cadastro.email, cadastro.dataNascimento ";
	
		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  CadastroParticipanteAtividadeExtensao cadastro " +
				" WHERE cadastro.ativo = :true AND cadastro.confirmado = :true ";
		
		if(cpf != null)
			hql+= " AND cadastro.cpf = :cpf ";
		if(passaporte != null)
			hql+= " AND cadastro.passaporte = :passaporte ";
		if(nome != null)
			hql+= " AND cadastro.nomeAscii like :nome ";
		if(email != null)
			hql+= " AND cadastro.email = :email ";
		
		hql+= " ORDER BY cadastro.nome ";
		
		Query query = getSession().createQuery(hql);
		
		if(cpf != null)
			query.setBigInteger("cpf", new BigInteger( cpf.replaceAll("\\D","") ) );
		if(passaporte != null)
			query.setString("passaporte", passaporte);
		if(nome != null)
			query.setString("nome", "%"+StringUtils.toAsciiAndUpperCase(nome)+"%");
		if(email != null)
			query.setString("email", email);
		
		
		query.setBoolean("true", true);
		
		query.setMaxResults(301);
		
		@SuppressWarnings("unchecked")
		ArrayList<CadastroParticipanteAtividadeExtensao> cadastros =  new ArrayList<CadastroParticipanteAtividadeExtensao>
					(HibernateUtils.parseTo(query.list(), projecao, CadastroParticipanteAtividadeExtensao.class, "cadastro"));
		
		return cadastros;
		
	}
	
	
	/**
	 * Retorna uma pessoa pelo seu nome, caso este inicie com o nome passado como parâmetro. 
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Pessoa> findPessoaInternaByNome(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(Pessoa.class);
		
		c.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("nome"))
				.add(Projections.property("cpf_cnpj"))
				.add(Projections.property("dataNascimento"))
				);
		
		//c.add(Expression.sql("upper(nome_ascii) like upper('"+ StringUtils.toAscii(StringUtils.escapeBackSlash(nome)) + "%')"));
		c.add(Restrictions.ilike("nomeAscii", StringUtils.toAsciiAndUpperCase(nome), MatchMode.ANYWHERE));
		c.addOrder(Order.asc("nome"));
		
		List<Pessoa> resultado = new ArrayList<Pessoa>();
		
		for (Object[] linha : (List<Object[]>)c.list()) {
			Pessoa pessoa = new Pessoa();

			pessoa.setId((Integer) linha[0]);
			pessoa.setNome((String) linha[1]);
			pessoa.setCpf_cnpj((Long) linha[2]);
			pessoa.setDataNascimento ( (Date) linha[3]);
			
			resultado.add(pessoa);
		}
		
		return resultado;
	}
	
	
	/**
	 * Retorna uma pessoa pelo seu nome, caso este inicie com o nome passado como parâmetro. 
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Pessoa findInformacoesPessoaInternaById(int idPessoa) throws DAOException {
		
		String projecao = " pessoa.id_pessoa, pessoa.nome,  pessoa.cpf_cnpj, pessoa.passaporte, pessoa.internacional, pessoa.data_nascimento, COALESCE( usuario.email, pessoa.email) as email, " +
				" enderecoContato.logradouro, enderecoContato.numero, enderecoContato.complemento, " +
				" enderecoContato.bairro, enderecoContato.cep, municipio.id_municipio, municipio.nome, unidadeFederativa.id_unidade_federativa, unidadeFederativa.sigla, " +
				" pessoa.telefone_fixo, pessoa.telefone_celular   ";
		
		String sql = " SELECT "+projecao+" FROM comum.pessoa pessoa "+
				     " LEFT JOIN comum.endereco enderecoContato ON pessoa.id_endereco_contato = enderecoContato.id_endereco "+
				     " LEFT JOIN comum.municipio municipio ON enderecoContato.id_municipio = municipio.id_municipio "+
				     " LEFT JOIN comum.unidade_federativa unidadeFederativa ON enderecoContato.id_unidade_federativa = unidadeFederativa.id_unidade_federativa"+
				     " LEFT JOIN comum.usuario usuario ON usuario.id_pessoa = pessoa.id_pessoa "+
				     " WHERE pessoa.id_pessoa = :idPessoa "+
				     " ORDER BY usuario.data_cadastro DESC ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idPessoa", idPessoa);
		q.setMaxResults(1);

		
		for (Object[] linha : (List<Object[]>)q.list()) {
			Pessoa pessoa = new Pessoa();

			pessoa.setId((Integer) linha[0]);
			pessoa.setNome((String) linha[1]);
			if( linha[2] != null)
				pessoa.setCpf_cnpj( ( (BigInteger) linha[2]).longValue());
			pessoa.setPassaporte((String) linha[3]);
			pessoa.setInternacional((Boolean) linha[4]);
			pessoa.setDataNascimento ( (Date) linha[5]);
			
			if(linha[6] != null)
				pessoa.setEmail( (String) linha[6] ); // se o mail do usuário aqui
			
			pessoa.setEnderecoContato( new Endereco());
			
			pessoa.getEnderecoContato().setLogradouro((String) linha[7]);
			pessoa.getEnderecoContato().setNumero((String) linha[8]);
			pessoa.getEnderecoContato().setComplemento((String) linha[9]);
			pessoa.getEnderecoContato().setBairro((String) linha[10]);
			pessoa.getEnderecoContato().setCep((String) linha[11]);
			
			if(linha[12] != null)
				pessoa.getEnderecoContato().setMunicipio( new Municipio((Integer) linha[12], (String) linha[13]));
			
			if(linha[14] != null)
				pessoa.getEnderecoContato().setUnidadeFederativa( new UnidadeFederativa( (Integer) linha[14], (String) linha[15]));
			
			pessoa.setTelefone((String) linha[16]);
			pessoa.setCelular((String) linha[17]);
			
			return pessoa;
		}

		return null;
	}
	
	
}
