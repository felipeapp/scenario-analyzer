/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/09/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para a busca das suspensões dos usuários da biblioteca.
 *
 * @author jadson
 * @since 24/09/2008
 * @version 1.0 criacao da classe
 *
 */
public class SuspensaoUsuarioBibliotecaDao extends GenericSigaaDAO{
	
	
	/**
	 * <p>Retorna a data do fim da suspensão do usuário, se for igual ou posterior à data atual. </p>
	 * 
	 * <p><strong>Para todas as conta que o usuário possua na biblioteca. Isso é importante!!! </strong></p>
	 * 
	 * @param usuarioBiblioteca
	 * @return null se não estiver suspenso.
	 * @throws DAOException
	 */
	public Date findFimSuspensao(Integer idPessoa, Integer idBiblioteca) throws DAOException{

		if( (idPessoa == null || idPessoa <=0)  && ( idBiblioteca == null || idBiblioteca <=0 ) ) throw new DAOException("Deve-se passar o id da pessoa ou biblioteca !!!! ");
		
		/* Verifica as suspensões que foram criadas automaticamente pelo sistema */
		String hql = "";
		String hql2 = "";
		
		if(idPessoa != null){
			hql = " SELECT s.dataFim from SuspensaoUsuarioBiblioteca s where s.emprestimo.usuarioBiblioteca.pessoa.id = :idPessoa " +
					" AND s.dataFim >= :hoje and s.ativo = :true ORDER BY s.dataFim DESC ";
		
		}
		
		if(idBiblioteca != null){
			hql = " SELECT s.dataFim from SuspensaoUsuarioBiblioteca s where s.emprestimo.usuarioBiblioteca.biblioteca.id = :idBiblioteca " +
					" AND s.dataFim >= :hoje and s.ativo = :true ORDER BY s.dataFim DESC ";
		}
		
		Query q = getSession().createQuery(hql);
		if(idPessoa != null) q.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q.setInteger("idBiblioteca", idBiblioteca);
		q.setDate("hoje", CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0) );
		q.setBoolean("true", true);
		q.setMaxResults(1);
		
		Date data = (Date) q.uniqueResult();
		
		/* ******************************************************************************** */
		
		
		
		/* Verifica as suspensões que foram criadas manualmente   */
		
		if(idPessoa != null){
			hql2 = "select s.dataFim from SuspensaoUsuarioBiblioteca s where s.usuarioBiblioteca.pessoa.id = :idPessoa " +
					" AND s.dataFim >= :hoje and s.ativo = :true ORDER BY s.dataFim DESC ";
		}
		
		if(idBiblioteca != null){
			hql2 = "select s.dataFim from SuspensaoUsuarioBiblioteca s where s.usuarioBiblioteca.biblioteca.id = :idBiblioteca " +
					" AND s.dataFim >= :hoje and s.ativo = :true ORDER BY s.dataFim DESC ";
		}
		
		Query q2 = getSession().createQuery(hql2);
		if(idPessoa != null) q2.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q2.setInteger("idBiblioteca", idBiblioteca);
		q2.setDate("hoje",CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0));
		q2.setBoolean("true", true);
		q2.setMaxResults(1);
		
		Date data2 = (Date) q2.uniqueResult();
		
		/* ******************************************************************************** */
		
		
		// Se uma das duas for nula, retorna a outra.
		if (data == null)
			return data2;
		
		if (data2 == null)
			return data;
		
		// Se o usuário possuir tanto suspensoes novas quanto migradas, retorna a que acaba por último.
		return data.after(data2) ? data : data2;
	}
	


	
	
	
	
	/**
	 * Busca as suspensões ativas que ocorrerão depois da data final da suspensão passada, recupera as informações do empréstimos ou usuário que gerou a suspensão.
	 * 
	 * @param suspensao
	 * @return
	 * @throws DAOException
	 */
	public List <SuspensaoUsuarioBiblioteca> findSuspensoesFuturasDoUsuario(Integer idPessoa, Integer idBiblioteca, Date dataLimite) throws DAOException {
		
		if((idPessoa == null || idPessoa <=0)  && ( idBiblioteca == null || idBiblioteca <=0 ) ) throw new DAOException("Deve-se passar o id da pessoa ou biblioteca !!!! ");
		
		String projecao = " s.id, s.dataInicio, s.dataFim, s.dataCadastro, s.manual, s.ativo, " +
				" uCadastro.id, pessoaCadastro.nome, s.motivoCadastro, " + // suspensões manuais
				" COALESCE ( usuarioSuspensao.id, e.usuarioBiblioteca.id ) as idUsuarioBiblioteca, " +
				" e.id, e.dataEmprestimo, e.prazo, e.dataDevolucao, m.id, m.codigoBarras "; // suspensões automaticas
		
		String hql =
				" SELECT "+projecao+
				" FROM SuspensaoUsuarioBiblioteca s " +
				" LEFT JOIN s.emprestimo e " +
				" LEFT JOIN e.usuarioBiblioteca  usuarioEmprestimo " +
				" LEFT JOIN usuarioEmprestimo.pessoa pessoaEmprestimo " +
				" LEFT JOIN s.usuarioBiblioteca  usuarioSuspensao " +
				" LEFT JOIN usuarioSuspensao.pessoa pessoaSuspensao " +
				
				" LEFT JOIN usuarioEmprestimo.biblioteca bibliotecaEmprestimo " +
				" LEFT JOIN usuarioSuspensao.biblioteca bibliotecaSuspensao " +
				
				" LEFT JOIN s.usuarioCadastro uCadastro "+
				" LEFT JOIN uCadastro.pessoa pessoaCadastro "+
				" LEFT JOIN e.material m "+
				" WHERE 1 = 1  ";
		
				if(idPessoa != null)
					hql += "  AND ( pessoaEmprestimo.id = :idPessoa  OR pessoaSuspensao.id = :idPessoa ) ";
				if(idBiblioteca != null)
					hql += "  AND ( bibliotecaEmprestimo.id = :idBiblioteca  OR bibliotecaSuspensao.id = :idBiblioteca ) ";
				
				hql += " AND s.dataFim >= :dataLimite AND s.ativo = :true ORDER BY s.dataFim ASC "; // Ainda não vencerem e não foram estornadas e tem ou emprestimo ou suspensao para o usuário  
				
		
		Query q = getSession().createQuery(hql);
		
		if(idPessoa != null) q.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q.setInteger("idBiblioteca", idBiblioteca);
		
		q.setDate("dataLimite", dataLimite);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<SuspensaoUsuarioBiblioteca> retorno = new ArrayList<SuspensaoUsuarioBiblioteca>();
		
		for (Object[] objects : linhas) {
			SuspensaoUsuarioBiblioteca  s = new SuspensaoUsuarioBiblioteca();
			s.setId( (Integer) objects[0]);
			s.setDataInicio( (Date) objects[1]);
			s.setDataFim( (Date) objects[2]);
			s.setDataCadastro( (Date) objects[3]);
			s.setManual( (Boolean) objects[4]);
			s.setAtivo( (Boolean) objects[5]);
			
			if(s.isManual() && objects[6] != null){
				Usuario u = new Usuario( (Integer) objects[6]);
				u.setPessoa( new Pessoa(0, (String)  objects[7]));
				s.setUsuarioCadastro( u );
				s.setMotivoCadastro( (String) objects[8] );
			}
			
		
			
			if(s.isManual()){ // se tem usuário associado à suspensão 
				s.setUsuarioBiblioteca( new UsuarioBiblioteca((Integer)  objects[9]));
				
				if(idPessoa != null)
					s.getUsuarioBiblioteca().setPessoa(new Pessoa(idPessoa));
				if(idBiblioteca != null)
					s.getUsuarioBiblioteca().setBiblioteca(new Biblioteca(idBiblioteca));
				
				
			}else{
				
				Emprestimo e = new Emprestimo();
				
				e.setUsuarioBiblioteca( new UsuarioBiblioteca((Integer)  objects[9]));
				
				if(idPessoa != null)
					e.getUsuarioBiblioteca().setPessoa(new Pessoa(idPessoa));
				if(idBiblioteca != null)
					e.getUsuarioBiblioteca().setBiblioteca(new Biblioteca(idBiblioteca));
				
				e.setId( (Integer)  objects[10]);
				e.setDataEmprestimo( (Date)  objects[11]);
				e.setPrazo( (Date)  objects[12]);
				e.setDataDevolucao( (Date)  objects[13]);
				
				
				Exemplar ex = new Exemplar(); /// tato faz exemplar ou fascículo, quero apenas o id e código de barras
				ex.setId( (Integer)  objects[14] );
				ex.setCodigoBarras( (String)  objects[15] );
				e.setMaterial(ex);
				
				s.setEmprestimo(e);
			}
			
			retorno.add(s);
		}
		
		//Não funciona: return new ArrayList<SuspensaoUsuarioBiblioteca>( HibernateUtils.parseTo(linhas, projecao, SuspensaoUsuarioBiblioteca.class, "s"));
		//List <SuspensaoUsuarioBiblioteca> lista = q.list ();
		return retorno;
	}
	
	
	/**
	 * Retorna a unidade da suspensão para verificar se o usuário tem permissão de mexer nessa suspensão.
	 * 
	 * @param suspensao
	 * @return
	 * @throws DAOException
	 */
	public Integer findUnidadeDaSuspensao(Integer idSuspensao) throws DAOException {
		
		String projecao = " s.emprestimo.material.biblioteca.unidade.id "; // suspensões automaticas
		
		String hql =
				" SELECT "+projecao+
				" FROM SuspensaoUsuarioBiblioteca s "+
				" WHERE s.id = :idSuspensao ";
		
				
		Query q = getSession().createQuery(hql);
		q.setInteger("idSuspensao", idSuspensao);
				
				
		return (Integer) q.uniqueResult();
		
	}
}
