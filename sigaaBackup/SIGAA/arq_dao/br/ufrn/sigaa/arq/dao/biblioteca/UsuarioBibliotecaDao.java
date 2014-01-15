/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/09/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que implementa os m�todos de busca espec�ficos para usu�rios do sistema da biblioteca.
 *
 * @author Jadson
 * @since 23/09/2008
 * @version 1.0 cria��o da classe
 *
 */
public class UsuarioBibliotecaDao extends GenericSigaaDAO{
	
	
	////////////////// OS quatro m�todos que recuperam as informa��es necess�rios de um usu�rio biblioteca ////////////////
	
	
	/**
	 * Encontra as informa��es da conta do usu�rio passada. Recupera as informa��es de contas j� quitadas. Usuado em situa��es espec�ficas do sistema.
	 * 
	 * <strong> Para as buscas normais do sistema utilizar: {@link UsuarioBibliotecaDao#findInformacoesUsuarioBibliotecaNaoQuitado(int) } </strong>
	 * 
	 * 
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public UsuarioBiblioteca findInformacoesUsuarioBibliotecaAtivos(int idUsuarioBiblioteca) throws DAOException{
		
		String hql = " SELECT "+ConsultasEmprestimoDao.PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA
		+" FROM UsuarioBiblioteca ub " 
		+" LEFT JOIN ub.pessoa pessoa "
		+" LEFT JOIN ub.biblioteca biblioteca "
		+" WHERE ub.id = :idUsuarioBiblioteca and ub.ativo = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setParameter("idUsuarioBiblioteca", idUsuarioBiblioteca);
		
		Object[] objetos = (Object[])  q.uniqueResult();

		if(objetos == null)return null;
		
		UsuarioBiblioteca ub = new UsuarioBiblioteca( (Integer) objetos[0]);
		ub.setSenha((String) objetos[1]);
		ub.setVinculo((VinculoUsuarioBiblioteca) objetos[2]);
		ub.setIdentificacaoVinculo( (Integer) objetos[3]);
		
		Integer idPessoa = (Integer) objetos[4];
		if(idPessoa != null)
			ub.setPessoa( new Pessoa(idPessoa));
		
		Integer idBiblioteca = (Integer) objetos[5];
		if(idBiblioteca != null)
			ub.setBiblioteca( new Biblioteca(idBiblioteca));
		
		ub.setQuitado(  (Boolean) objetos[6] );
		ub.setDataQuitacao(  (Date) objetos[7] );
		
		return ub;
	}
	
	/**
	 * Delega a busca para a j� utilizada na realiza��o dos emprestimo
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public UsuarioBiblioteca findInformacoesUsuarioBibliotecaNaoQuitado(int idUsuarioBiblioteca) throws DAOException{
		ConsultasEmprestimoDao dao = null; 
		UsuarioBiblioteca u = null;
		
		try{
			dao = new ConsultasEmprestimoDao();
			u = dao.findInformacoesUsuarioBibliotecaNaoQuitado(idUsuarioBiblioteca);
		}finally{
			if(dao != null) dao.close();
		}
		return u;
	}
	
	
	
	/**
	 * Retorna os usu�rio biblioteca ativo e n�o quitados a partir da pessoa.
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	public List<UsuarioBiblioteca> findUsuarioBibliotecaAtivoNaoQuitadoByPessoa(int idPessoa) throws DAOException {
		
		String hql = " SELECT "+ConsultasEmprestimoDao.PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA+
				" FROM UsuarioBiblioteca ub "+
				" WHERE (ub.pessoa.id = " + idPessoa + " " +
				") and ub.ativo = trueValue() and quitado = falseValue() ";
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		return montaDadosUsusarioBiblioteca(dadosUsuarios);
		
	}
	
	
	
	/**
	 * <p>Retorna os usu�rio biblioteca ativo a partir da pessoa. Inclusive os j� quitados.</p>
	 * 
	 * <p><strong>Esse m�todo deve ser utilizado apenas no cadastro/recadastro dos usu�rios, na emiss�o do comprovante de quita��o e na emiss�o 
	 * do hist�rio de empr�stimos, o restante das opera��es deve-se buscas apenas o cadastro n�o quitado do usu�rio </strong></p>
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	
	public List<UsuarioBiblioteca> findUsuarioBibliotecaAtivoByPessoa(int idPessoa) throws DAOException {
		
		String hql = " SELECT "+ConsultasEmprestimoDao.PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA+
				" FROM UsuarioBiblioteca ub "+
				" WHERE (ub.pessoa.id = " + idPessoa + " " +
				") and ub.ativo = trueValue() "+
				" ORDER BY ub.dataQuitacao DESC ";
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		return montaDadosUsusarioBiblioteca(dadosUsuarios);
	}
	
	/**
	 * Retorna os usu�rio biblioteca ativo a partir da pessoa.
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	
	public List<UsuarioBiblioteca> findUsuarioBibliotecaAtivoNaoQuitadoByBiblioteca(int idBiblioteca) throws DAOException {
		
		String hql = "SELECT "+ConsultasEmprestimoDao.PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA+
				" FROM UsuarioBiblioteca ub "+
				" WHERE (ub.biblioteca.id = " + idBiblioteca + " " +
				") AND ub.ativo = trueValue() AND quitado = falseValue()  ";
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		return montaDadosUsusarioBiblioteca(dadosUsuarios);
	}
	
	
	/**
	 * <p>Retorna os usu�rios bibliotecas ativos a partir da biblioteca.</p>
	 * 
	 * <p>M�todo utilizado nos empr�stimos intitucionais</p>
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	
	public List<UsuarioBiblioteca> findUsuarioBibliotecaAtivoByBiblioteca(int idBiblioteca) throws DAOException {
		
		String hql = " SELECT "+ConsultasEmprestimoDao.PROJECAO_PADRAO_INFORMACOES_USUARIO_BIBLIOTECA+
				" FROM UsuarioBiblioteca ub "+
				" WHERE (ub.biblioteca.id = " + idBiblioteca + " " +
				") AND ub.ativo = trueValue() "+
				" ORDER BY ub.dataQuitacao DESC ";
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> dadosUsuarios = q.list();
		
		return montaDadosUsusarioBiblioteca(dadosUsuarios);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 *       <p>M�todo que retorna os discentes do usu�rio biblioteca que podem fazer emprestimo, retorna tamb�m o n�vel do discente 
	 *    para saber se ele � gradu��o, p�s, ou n�vel m�dio.</p>
	 *
	 *		<p>Pasando-se os n�veis, o m�todo retorna apenas os discentes que possuiem os n�veis passados</p>
	 *
	 *      <p>Utilizado para saber se a biblioteca vai empr�star para alunos de mesmo centro ou n�o.</p>
	 *
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> findDiscentesBibliotecaByPessoaAndNivel(int idPessoa, List<Character> niveis) throws DAOException {
		
		String hql = "SELECT discente.id, discente.nivel, discente.matricula, curso.nome, gestora.id, gestora.nome, forma.tipoMobilidadeEstudantil from Discente discente "
		+" LEFT JOIN discente.curso curso "                // alguns discentes n�o possuem curso (Especiais)
		+" LEFT JOIN curso.unidade unidadeCurso " 
		+" LEFT JOIN unidadeCurso.gestora gestora " 
		+" LEFT JOIN discente.formaIngresso forma "        // alguns discentes n�o possuem forma de ingresso
		+" WHERE  discente.pessoa.id = " + idPessoa + " " 
		+" AND ( discente.tipo = "+Discente.REGULAR+" OR forma.tipoMobilidadeEstudantil = trueValue() ) " 
		+" AND discente.status in ( ";
		
		/////////// Tras apenas discentes com status ativos /////
		int ptr = 0;
		for (Integer status: StatusDiscente.getAtivos()) {
			if(ptr != StatusDiscente.getAtivos().size()-1)
				hql+= " "+status+", ";
			else
				hql+= " "+status+" ";
			
			ptr++;
		}
		
		hql+=" ) ";
		
		if(niveis != null && niveis.size() > 0){ // Encontra os discentes de um n�vel espec�fico
			hql+= " AND discente.nivel in ( ";
			
			int ptrNiveis = 0;
			for (char nivel: niveis) {
				if(ptrNiveis != niveis.size()-1)
					hql+= " '"+nivel+"', ";
				else
					hql+= " '"+nivel+"' ";
				
				ptrNiveis++;
			}
			
			hql+=" ) ";
		
		}
		
		System.out.println(hql);
		
		Query q = getSession().createQuery(hql);		
		
		@SuppressWarnings("unchecked")
		List<Object> objetosServidores = q.list();
		
		List<Discente> discentes = new ArrayList<Discente>();
		
		for (Object object : objetosServidores) {
			Object[] temp = (Object[]) object;
			Discente dTemp = new Discente( (Integer) temp[0]);
			dTemp.setNivel( (Character) temp[1]);
			dTemp.setMatricula((Long) temp[2]);
			dTemp.getCurso().setNome(""+temp[3]);
			Unidade u = new Unidade();
			
			// se tiver unidade gestora //
			if((Integer) temp[4] != null){
				u.setGestora(new Unidade());
				u.getGestora().setId((Integer) temp[4]);
				u.getGestora().setNome(""+temp[5]);
			}
			dTemp.getCurso().setUnidade(u);
			
			 // guarda com o discente a informa��o se ele � aluno de mobilidade estutantiu  para mostrar ao usu�rio //
			FormaIngresso fi = new FormaIngresso(); 
			fi.setTipoMobilidadeEstudantil( (Boolean) temp[6]);
			dTemp.setFormaIngresso( fi);
			
			discentes.add(dTemp);
		}
		
		return discentes;
		
	}
	
	
	
	
//	/**
//	 * Retorna os ids dos usu�rio biblioteca ativo a partir da pessoa.
//	 * 
//	 * @param idPessoa
//	 * @return List dos ids dos UsuarioBiblioteca encontrados, s� era para sempre retornar apenas 1 id. 
//	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
//	 * @throws DAOException 
//	 */
//	
//	public List<Integer> findIdsUsuarioBibliotecaAtivoNaoQuitadoByPessoa(int idPessoa) throws DAOException {
//		
//		String hql = "select ub.id from UsuarioBiblioteca ub " +
//				"where (ub.pessoa.id = " + idPessoa + " " +
//				") and ub.ativo = trueValue() and ub.quitado = falseValue() ";
//		
//		Query q = getSession().createQuery(hql);		
//		
//		@SuppressWarnings("unchecked")
//		List<Integer> idUsuarios = q.list();
//		
//		return idUsuarios;
//	}
	
	/**
	 * Conta a quantidade de usu�rio biblioteca ativo a partir da pessoa, sempre era para existir apenas 1.
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	
	public Long countUsuariosBibliotecaAtivoNaoQuitadoByPessoa(int idPessoa) throws DAOException {
		
		String hql = "select count(ub.id) from UsuarioBiblioteca ub " +
				"where (ub.pessoa.id = " + idPessoa + " " +
				") and ub.ativo = trueValue() and ub.quitado = falseValue() ";
		
		Query q = getSession().createQuery(hql);
	
		return (Long) q.uniqueResult();
	}
	
	
	
	
	
	
	/**
	 *  Monta os dados da consulta de usu�rios da biblioteca
	 *
	 * @param dadosUsuarios
	 * @return
	 */
	private List<UsuarioBiblioteca> montaDadosUsusarioBiblioteca(List<Object> dadosUsuarios  ){
		
		
		
		List<UsuarioBiblioteca> usuarios = new ArrayList<UsuarioBiblioteca>();
		
		
		for (Object object : dadosUsuarios) {
			Object[] dados = (Object[]) object;
			
			UsuarioBiblioteca ub = new UsuarioBiblioteca( (Integer) dados[0]);
			ub.setSenha((String) dados[1]);
			ub.setVinculo((VinculoUsuarioBiblioteca) dados[2]);
			ub.setIdentificacaoVinculo( (Integer) dados[3]);
			
			Integer idPessoa = (Integer) dados[4];
			if(idPessoa != null)
				ub.setPessoa( new Pessoa(idPessoa));
			
			Integer idBiblioteca = (Integer) dados[5];
			if(idBiblioteca != null)
				ub.setBiblioteca( new Biblioteca(idBiblioteca));
			
			ub.setQuitado(  (Boolean) dados[6] );
			ub.setDataQuitacao(  (Date) dados[7] );
			
			usuarios.add( ub );
		}
		
		return usuarios;
		
	}
	

	/**
	 * Retorna as informa��es de um Usuario ativo que s�o usadas do sistema de biblioteca a partir da pessoa
	 * , caso a pessoa possuia mais de um usu�rio no sistema retorna o �ltimo que foi atualizado e vai usar 
	 * as informa��es deste (email, login, foto).
	 * 
	 * @param idPessoa
	 * @return List de UsuarioBiblioteca, era para sempre retornar apenas 1 UsuarioBiblioteca.
	 *          Quem chamar esse m�todo precisa tratar o erro de quando exitem mais de 1 usu�rio para a mesma pessoa.
	 * @throws DAOException 
	 */
	
	public Usuario findUsuarioMaisRecenteAtivoByPessoa(int idPessoa) throws DAOException {
		
		String hql = "select u.id, u.idFoto, u.email, u.login from Usuario u " +
				"WHERE u.pessoa.id = " + idPessoa + " " +
				"AND u.inativo = falseValue() ORDER BY id_usuario DESC " ; // o �ltimo criado
		
		Query q = getSession().createQuery(hql);		
		q.setMaxResults(1);
		
		Object[] dadosUsuarios = (Object[]) q.uniqueResult();
		
		Usuario usuario = new Usuario();
		if(dadosUsuarios != null){   // Se a pessoa tem usu�rio 
			usuario.setId((Integer) dadosUsuarios[0]);
			usuario.setIdFoto((Integer) dadosUsuarios[1]);
			usuario.setEmail((String) dadosUsuarios[2]);
			usuario.setLogin((String) dadosUsuarios[3]);
		}
		
		return usuario;
	}
	
	
	
	/**
	 *  M�todo que retorna o nome do usu�rio e o email a a partir de um usu�rio da biblioteca.
	 *
	 * @param usuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Object[] findNomeEmailUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) throws DAOException{
		StringBuilder sql = new StringBuilder();
		
		sql.append(
				" SELECT DISTINCT " + 
				" 	pessoa.id_pessoa, " +  
				" 	pessoa.nome, " + 
				" 	usuario.email AS emailUsuario, " + 
				" 	pessoa.email AS emailPessoa, " + 
				" 	usuario.data_cadastro, " + 
				" 	biblioteca.descricao, " + 
				" 	biblioteca.email AS emailBiblioteca " + 
				" FROM " + 
				" 	biblioteca.usuario_biblioteca usuarioBiblioteca " + 
				" 	LEFT JOIN comum.pessoa pessoa ON (usuarioBiblioteca.id_pessoa = pessoa.id_pessoa) " + 
				" 	LEFT JOIN comum.usuario usuario ON (pessoa.id_pessoa = usuario.id_pessoa) " + 
				" 	LEFT JOIN biblioteca.biblioteca biblioteca ON (usuarioBiblioteca.id_biblioteca = biblioteca.id_biblioteca) " + 
				" WHERE " + 
				" 	usuarioBiblioteca.id_usuario_biblioteca = " + usuarioBiblioteca.getId() + 
				" ORDER BY usuario.data_cadastro DESC "
				);

		/* 
		 * informacoesUsuario[0] == nome Usuario
		 * informacoesUsuario[1] == email Usuario
		 */
		Object[] informacoesUsuario = new Object[2];
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> informacoesUsuarioTempList = q.list();
		
		Object[] informacoesUsuarioTemp = informacoesUsuarioTempList.size() > 0 ? informacoesUsuarioTempList.get(0) : null;
		
		if(informacoesUsuarioTemp != null) {
			
			if(informacoesUsuarioTemp[0] != null && (Integer)informacoesUsuarioTemp[0] > 0){        /* se � pessoa */
				informacoesUsuario[0] = informacoesUsuarioTemp[1]; // retorna o nome da pessoa.
				
				if (!StringUtils.isEmpty((String) informacoesUsuarioTemp[2])) {                     /* se tem email em usu�rio */
					informacoesUsuario[1] = informacoesUsuarioTemp[2];  // retorna email do usu�rio
				} else {
					informacoesUsuario[1] = informacoesUsuarioTemp[3];  // retorna email da pessoa
				}
			}else{                                                                                  /* se � biblioteca */
				informacoesUsuario[0] = informacoesUsuarioTemp[5]; // retorna o nome da biblioteca.
				informacoesUsuario[1] = informacoesUsuarioTemp[6]; // retorna o email da biblioteca.
			}
			
		}
		
		return  informacoesUsuario;
	}	
	
	
	/**
	 * <p>Retornas o cpf e o id do usu�rio da pessoa do usu�rio biblioteca </p>
	 * 
	 * <p> Utilizando para cadastrar as digitais de um usu�rio da biblioteca no banco, j� que a tabela identifica��o pessoa pede o cpf e id_usuario</p>
	 * 
	 * digitais[0] = ditital direita
	 * digitais[1] = ditital esqueda
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Long findCPFPessoaByUsuarioBibliotecaq(int idUsuarioBiblioteca) throws DAOException {
		
		Query q = getSession().createQuery(" SELECT p.cpf_cnpj FROM UsuarioBiblioteca ub "
				+" INNER JOIN ub.pessoa p "
				+" WHERE ub.id = :idUsuarioBiblioteca AND ub.ativo = :true  ");
		
		q.setInteger("idUsuarioBiblioteca", idUsuarioBiblioteca);
		q.setBoolean("true", true);
		
		return (Long) q.uniqueResult();
		
	}
	
	
}
