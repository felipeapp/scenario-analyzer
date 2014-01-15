/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/05/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Convenio;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioExternoBiblioteca;
import br.ufrn.sigaa.dominio.Unidade;

/**
 *
 *    DAO que contém os métodos de busca para os usuários externos da biblioteca.
 *
 * @author Fred_Castro
 * @since 20/05/2009
 * @version 1.0 Criação da classe.
 *
 */
public class UsuarioExternoBibliotecaDao extends GenericSigaaDAO{

	
	
	
	/**
	 * <p>Busca o usuário externo do UsuarioBiblioteca com vínculo de usuário externo.<p/>
	 * <p><strong>Só era para retornar um usuário externo por usuário biblioteca. </strong> <p/>
	 * 
	 * @param p
	 * @return
	 * @throws DAOException
	 */
	
	public List<UsuarioExternoBiblioteca> findAtivoNaoQuitadoByUsuarioBiblioteca(int idUsuarioBiblioteca) throws DAOException {
		
		String hql = "select ueb.id, ueb.usuarioBiblioteca, ueb.prazoVinculo, ueb.documento, ueb.cancelado, ueb.motivoCancelamento, ueb.ativo "
			+" FROM UsuarioExternoBiblioteca ueb " 
			+" WHERE ueb.usuarioBiblioteca.id = " + idUsuarioBiblioteca + " " 
			+" AND ueb.ativo = trueValue() AND ueb.usuarioBiblioteca.ativo = trueValue() AND ueb.usuarioBiblioteca.quitado = falseValue() ";
		
		Query q = getSession().createQuery(hql);
		
		List<UsuarioExternoBiblioteca> usuarios = new ArrayList<UsuarioExternoBiblioteca>();
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		for (Object object : dadosUsuarios) {
			Object[] dados = (Object[]) object;
			
			/*
			 *  Busca a unidade e convenico do usuário Externo Biblioteca  que não são obrigatórios
			 *  Com normalmente só existe um UsuárioExterno por usuário Biblioteca, não vai ter problemas fazer uma nova pesquisa
			 */
			String hql2 = "SELECT unidade FROM UsuarioExternoBiblioteca ueb WHERE ueb.id = :idUsuarioExterno ";
			
			q = getSession().createQuery(hql2);
			q.setInteger("idUsuarioExterno", (Integer) dados[0]);
			Unidade u = (Unidade) q.uniqueResult() != null ?  (Unidade) q.uniqueResult(): new Unidade() ;
			
			String hql3 = "SELECT convenio FROM UsuarioExternoBiblioteca ueb WHERE ueb.id = :idUsuarioExterno ";
			
			q = getSession().createQuery(hql3);
			q.setInteger("idUsuarioExterno", (Integer) dados[0]);
			Convenio c = (Convenio) q.uniqueResult()!= null ?  (Convenio) q.uniqueResult(): new Convenio() ;
			
			usuarios.add(new UsuarioExternoBiblioteca( (Integer) dados[0], (UsuarioBiblioteca) dados[1], (Date) dados[2], (String)dados[3], u, c, (Boolean) dados[4], (String) dados[5], (Boolean) dados[6] ) );
		}
		
		// Só é para existir um cadastrado para o usuário externo, se retorna mais de um precisa ser 
		// informado ao usuário para que os outros seja desativados
		return usuarios;
		
	}
	
	
	
	/**
	 * <p>Busca o usuário externo ativo de um UsuarioBiblioteca ativo por pessoa.<p/>
	 * <p><strong>Só era para retornar um usuário externo ativo por pessoa</strong> <p/>
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	
	public List<UsuarioExternoBiblioteca> findUsuarioExternoBibliotecaNaoQuitadoByPessoa(int idPessoa) throws DAOException {
		
		String hql = "select ub.id, ub.usuarioBiblioteca, ub.prazoVinculo, ub.documento, u, c, ub.cancelado, ub.motivoCancelamento, ub.ativo from UsuarioExternoBiblioteca ub " +
		"left join ub.unidade u "+
		"left join ub.convenio c "+
		"where ub.usuarioBiblioteca.pessoa.id = " + idPessoa + " " +
		" and ub.ativo = trueValue() and ub.usuarioBiblioteca.ativo = trueValue() and ub.usuarioBiblioteca.quitado = falseValue()";
		
		Query c = getSession().createQuery(hql);
		
		List<UsuarioExternoBiblioteca> usuarios = new ArrayList<UsuarioExternoBiblioteca>();
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = c.list();
		
		for (Object object : dadosUsuarios) {
			Object[] dados = (Object[]) object;
			usuarios.add(new UsuarioExternoBiblioteca( (Integer) dados[0], (UsuarioBiblioteca) dados[1], (Date) dados[2], (String)dados[3],  (Unidade) dados[4], (Convenio) dados[5], (Boolean) dados[6], (String) dados[7], (Boolean) dados[8] ) );
		}
		
		// Só é para existir um cadastrado para o usuário externo, se retorna mais de um precisa ser 
		// informado ao usuário para que os outros seja desativados
		return usuarios;
		
	}	
	
}
