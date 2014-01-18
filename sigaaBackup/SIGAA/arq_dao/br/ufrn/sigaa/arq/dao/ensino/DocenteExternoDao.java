/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/02/2007
 *
 */		
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;

/**
 * DAO para Consultas específicas à Docente Externo.
 * @author Gleydson
 *
 */
public class DocenteExternoDao extends GenericSigaaDAO {

	/**
	 * Retorna o docente com a pessoa e tipo informado por parâmetro. Só deve existir um no banco.
	 * @param pessoa
	 * @param tipo
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findByPessoa(Pessoa pessoa, int tipo, Unidade unidade) throws DAOException {
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add(Expression.eq("pessoa",pessoa));
		c.add(Expression.eq("unidade",unidade));
		c.add(Expression.eq("tipoDocenteExterno.id",tipo));
		c.add(Expression.isNotNull("matricula"));
		c.add(Expression.ge("prazoValidade", new Date()));
		c.add( Expression.eq("ativo", true) );
		c.setMaxResults(1);
		return (DocenteExterno) c.uniqueResult();
	}

	/**
	 * Retorna um docente externo pelo CPF.
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findByCPF(long cpf) throws DAOException {
		String hql = "select de from DocenteExterno de where de.pessoa.cpf_cnpj = :cpf";
		Query query = getSession().createQuery(hql);
		query.setLong("cpf", cpf);
		
		
		return (DocenteExterno) query.uniqueResult();
	}
	
	/**
	 * Retorna os docentes externos associados à pessoa informada.
	 * Busca apenas docentes que possuem a matrícula setada.
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteExterno> findAtivosByPessoa(Pessoa pessoa) throws DAOException {
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add(Expression.eq("pessoa",pessoa));
		c.add(Expression.eq("ativo",true));
		c.add(Expression.isNotNull("matricula"));
		
		// Verificar se o prazo adicional de acesso também foi excedido
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO)-1);
		Date dataAtual = cal.getTime();
		c.add(Expression.or(Restrictions.isNull("prazoValidade"), Restrictions.gt("prazoValidade",dataAtual)));

		@SuppressWarnings("unchecked")
		List<DocenteExterno> lista = c.list();
		return lista;
	}
	

	/**
	 * Retorna o docente externo ativo associado à unidade informada. 
	 * Só pode haver um docente externo associado à uma unidade.
	 * @param pessoa
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno findAtivoByPessoaUnidadeTipo(Pessoa pessoa, Unidade unidade, TipoDocenteExterno tipo) throws DAOException {
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add(Expression.eq("pessoa",pessoa));
		c.add(Expression.eq("unidade",unidade));
		c.add(Expression.eq("ativo",true));
		c.add(Expression.eq("tipoDocenteExterno.id",tipo.getId()));
		
		c.add(Expression.or(Restrictions.isNull("prazoValidade"), Restrictions.gt("prazoValidade", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))));
		c.add(Expression.isNotNull("matricula"));

		return (DocenteExterno) c.uniqueResult();
	}

	/** Retorna o Docente Externo associado à pessoa que possua a formação e vínculo com a instituição especificados.
	 * @param pessoa
	 * @param formacao
	 * @param instituicaoEnsino
	 * @return
	 * @throws DAOException
	 */
	public DocenteExterno find(Pessoa pessoa, Formacao formacao, InstituicoesEnsino instituicaoEnsino) throws DAOException {
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add(Expression.eq("pessoa", pessoa));
		c.add(Expression.eq("formacao", formacao));
		if (instituicaoEnsino != null && instituicaoEnsino.getId() != 0) {
			c.add(Expression.eq("instituicao", instituicaoEnsino));
		}
		c.addOrder(Order.desc("dataCadastro"));
		c.setMaxResults(1);

		return (DocenteExterno) c.uniqueResult();
	}


	/** Busca por docentes externos por nome e tipo de unidade acadêmica.
	 * @param nome Nome, ou parte do nome, do docente.
	 * @param tipoUnidadeAcademica tipo de unidade acadêmica do docente (opcional).
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByNomeUnidade(String nome, Integer tipoUnidadeAcademica) throws DAOException{
		if( tipoUnidadeAcademica != null )
			return findByNomeUnidade(nome, null, false, true,tipoUnidadeAcademica);
		else
			return findByNomeUnidade(nome, null, false, true);
	}

	/**
	 * Busca do docente externo pelo nome e/ou unidade e/ou tipo da unidade acadêmica (Departamento, Programa pós...)
	 * @param nome
	 * @param idUnidade
	 * @param tipoUnidadeAcademica
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByNomeUnidade(String nome, Integer idUnidade, boolean matricula, boolean validos,Integer... tipoUnidadeAcademica) throws DAOException{


		
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add( Expression.eq("ativo", true) );
		
		if (validos) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
			cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO) - 1);
			Date dataLimite = cal.getTime();
			c.add( Expression.ge("prazoValidade", dataLimite) );
		}
		
		
		Criteria cPessoa = c.createCriteria("pessoa");
		if ( tipoUnidadeAcademica != null && tipoUnidadeAcademica.length > 0)
			c.createCriteria("unidade").add( Expression.in("tipoAcademica", tipoUnidadeAcademica) );

		if(matricula) {
			c.add( Expression.isNotNull("matricula") );
		}
		
		if( nome != null ) {
			cPessoa.add(Expression.ilike("nome", nome, MatchMode.ANYWHERE));
		}

		if( idUnidade != null ) {
			c.add( Expression.eq("unidade.id", idUnidade) );
		}

		cPessoa.addOrder( Order.desc("nome") );

		@SuppressWarnings("unchecked")
		Collection<DocenteExterno> lista = c.list();
		return lista;

	}

	/**
	 * Retorna todos os docentes externos de um departamento (inclusive os inativos)
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByUnidade(int idUnidade) throws DAOException {
		Query q = getSession().createQuery("select new DocenteExterno(de.id, de.pessoa.cpf_cnpj, de.pessoa.nome) from DocenteExterno de where de.unidade.id = :unidade");
		q.setInteger("unidade", idUnidade);
		
		@SuppressWarnings("unchecked")
		Collection<DocenteExterno> lista = q.list();
		return lista;
	}
	
	/**
	 * Retornar todos os Docentes Externos Ativos e dentro do prazo de Validade. 
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByAtivosValido(Pessoa pessoa) throws DAOException {
		Criteria c = getSession().createCriteria(DocenteExterno.class);
		c.add(Expression.eq("pessoa",pessoa));
		c.add(Expression.eq("ativo",true));
		
		// Verificar se o prazo adicional de acesso também foi excedido
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO)-1);
		Date dataAtual = cal.getTime();
		c.add(Expression.or(Restrictions.isNull("prazoValidade"), Restrictions.gt("prazoValidade",dataAtual)));

		@SuppressWarnings("unchecked")
		List<DocenteExterno> lista = c.list();
		return lista;
	}

	/**
	 * Realizar a busca pelo nome da pessoa.
	 * 
	 * @param nomePessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteExterno> findByPessoaDocenteExternoAtivo(String nomePessoa) throws DAOException {
		StringBuilder query = new StringBuilder("from DocenteExterno de where ");
		if (!("".equals(nomePessoa))) {
			query.append(" lower(de.pessoa.nome) like lower(:pessoa) and de.ativo = :ativo" );
		}else{
			query.append(" (de.prazoValidade >= :hoje or de.prazoValidade is null))");
		}

		Query q  = getSession().createQuery(query.toString());
		if (!"".equals(nomePessoa)){
			q.setParameter("pessoa", "%"+ nomePessoa + "%");
			q.setBoolean("ativo", true);
		}else {
			q.setMaxResults(200);
			q.setDate("hoje", new Date());
		}
		@SuppressWarnings("unchecked")
		Collection<DocenteExterno> lista = q.list();
		return lista;
	}

	/**
	 * Retorna o usuário ativo do docente externo através do registro de pessoa informado.
	 * @param idDocente
	 * @return
	 * @throws DAOException
	 */
	public Usuario findUsuarioByDocenteExterno(DocenteExterno docenteExterno) throws DAOException {
		try {
			Criteria c = getCriteria(Usuario.class);
			c.add(Expression.eq("pessoa.id", docenteExterno.getPessoa().getId()));
			c.addOrder( Order.desc("id") );
			c.setMaxResults(1);
			return (Usuario) c.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o identificador da pessoa do docente externo através do identificador desse docente.
	 * @param idDocente
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdPessoaByDocenteExterno(int idDocenteExterno) throws DAOException {
		try {
			Query q = getSession().createQuery("select p.id from DocenteExterno d join d.pessoa p where d.id = "+idDocenteExterno);
			q.setMaxResults(1);
			return (Integer) q.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
}