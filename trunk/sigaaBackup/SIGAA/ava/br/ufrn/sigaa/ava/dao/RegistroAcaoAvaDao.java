/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/09/2010
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.RegistroAcaoAva;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 *
 * Dao que retorna os registros de ações realizadas nas turmas virtuais. 
 *
 * @author Fred_Castro
 *
 */
public class RegistroAcaoAvaDao extends GenericSigaaDAO {

	/**
	 * Retorna lista de registros de ações realizadas por um usuário na turma.
	 * Utilizado no relatório de acessos da turma virtual.
	 * 
	 * @param turma
	 * @param usuario
	 * @param material
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <RegistroAcaoAva> findRegistrosByGeral(Turma turma, Usuario usuario, AbstractMaterialTurma material, Date inicio, Date fim) throws HibernateException, DAOException{
		
		Criteria c = getSession().createCriteria(RegistroAcaoAva.class);

		if (usuario != null) {
			
		} else if (material != null) {
			
		} else {
			c.add(Restrictions.eq("idEntidade", turma.getId()));
			c.add(Restrictions.eq("entidade", EntidadeRegistroAva.TURMA));
			c.add(Restrictions.eq("acao", AcaoAva.ACESSAR));
		}
		
		if (inicio != null)
			c.add(Restrictions.ge("dataCadastro", inicio));
		
		if (fim != null)
			c.add(Restrictions.le("dataCadastro", fim));
		
		c.addOrder(Order.desc("dataCadastro"));
		c.createCriteria("registroCadastro").createCriteria("usuario").createCriteria("pessoa").addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		List <RegistroAcaoAva> rs = c.list();
		
		return rs;
	}
	
	/**
	 * 
	 * Retorna lista de registros de ações realizadas por um usuário na turma.
	 * Utilizado no relatório de acessos da turma virtual.
	 * 
	 * @param entidade
	 * @param acao
	 * @param idEntidade
	 * @param usuario
	 * @param turma
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> findRegistrosByGeral(Integer entidade, Integer acao, Integer idEntidade, Integer usuario, int turma, Date inicio, Date fim, boolean contar) throws DAOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calFim = null;
		
		if (fim != null){
			calFim = Calendar.getInstance();
			calFim.setTime(fim);
			calFim.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		String sql = "select " +
						"r.id_registro_acao_ava, r.login, r.nome, r.acao, r.entidade, r.descricao, r.id_turma, r.id_entidade, r.data_cadastro, re.id_usuario, r.acao, r.entidade from ava.registro_acao_ava r join comum.registro_entrada re on re.id_entrada = r.id_registro_cadastro where r.id_turma = " + turma
			+ (entidade != null && entidade > 0 ? " and r.entidade = " + entidade : "")
			+ (acao != null && acao > 0 ? " and r.acao = " + acao : "")
			+ (idEntidade != null && idEntidade > 0 ? " and r.id_entidade = " + idEntidade : "")
			+ (usuario != null && usuario > 0 ? " and re.id_usuario = " + usuario : "")
			+ (inicio != null ? " and r.data_cadastro >= '" + sdf.format(inicio) + "'" : "")
			+ (calFim != null ? " and r.data_cadastro <= '" + sdf.format(calFim.getTime()) + "'" : "")
			+ " order by " + (contar ? "re.id_usuario" : "r.data_cadastro") + " desc";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}
	
	/**
	 * Retorna lista de usuários que acessaram a turma informada.
	 * Utilizado no relatório de acessos da turma virtual.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List <SelectItem> findUsuariosQueAcessaramATurma (int idTurma) throws DAOException {
		String sql = "select distinct u.id_usuario, p.nome from ava.registro_acao_ava r join comum.registro_entrada re on re.id_entrada = r.id_registro_cadastro join comum.usuario u using (id_usuario) join comum.pessoa p using (id_pessoa) where r.id_turma = " + idTurma + " order by p.nome";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		List <SelectItem> usuarios = new ArrayList<SelectItem>();
		for (Object [] r : rs)
			usuarios.add(new SelectItem(((Number)r[0]).intValue(), ""+r[1]));
		
		return usuarios;
	}
}