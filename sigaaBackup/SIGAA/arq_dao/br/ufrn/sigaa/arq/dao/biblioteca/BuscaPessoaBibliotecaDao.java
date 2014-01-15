/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/11/2010
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p>Dao que cont�m as busca de pessoas utilizados na biblioteca </p>
 *
 * <p> <i> (Centraliza as informa��es para facilizar a manuten��o)</i> </p>
 * 
 * @author jadson
 *
 */
public class BuscaPessoaBibliotecaDao   extends GenericSigaaDAO{

	/**
	 * Parte da proje��o FIXA na busca de usu�rios na biblioteca, esses 6 campos s�o obrigados serem recuperados e nessa ordem
	 */
	public static final String PROJECAO_FIXA_CONSULTA_PESSOA = " p.cpf_cnpj, p.passaporte, p.internacional, p.id_pessoa, p.nome, p.data_nascimento ";
	
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_CPF = 0;              
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_PASSAPORTE = 1;        
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_INTERNACIONAL = 2;     
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_ID_PESSOA = 3;         
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_NOME = 4;             
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_DATA_NASCIMENTO = 5;  
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_ID_USUARIO_BIBLIOTECA = 6;
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_QUANTIDADE_EMPRESTIMOS = 7; 
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_LOGIN_USUARIO = 8;         
    	
    /** Posicao de retorno do campo na consulta */ 
	public static final String PROJECAO_FIXA_CONSULTA_BIBLIOTECA = " b.id, b.identificador, b.descricao ";
	
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_ID_BIBLIOTECA = 0;              
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_IDENTIFICADOR_BIBLIOTECA = 1;        
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_DESCRIACAO_BIBLIOTECA = 2;    
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_ID_USUARIO_BIBLIOTECA_BIBLIOTECA = 3;
    /** Posicao de retorno do campo na consulta */ 
	public static final int POSICAO_QUANTIDADE_EMPRESTIMOS_BILIOTECA = 4; 
	
	/**
	 * Retorna as pessoas Idepedentemente se elas possuem cadastro no sistema de biblioteca ou n�o.
	 * 
	 * <p><strong>M�todo especial, utilizado apenas na verifica��o da situa��o do usu�rio, porque nesse caso,
	 * o comprovante deve ser impresso mesmo se a pessoa nunca tenha feito cadastro no sistema de bibliotecas.</strong></p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []> findPessoasBiblioteca(String cpf, String  passaporte, String matricula, String siape, String nome) throws HibernateException, DAOException{
		
		String buscaDiscente = "";
		String buscaServidor = "";
		
		boolean passouCriterioDeBusca = false;
		
		
		if (!StringUtils.isEmpty(matricula))
			buscaDiscente = "inner join discente d on d.id_pessoa = p.id_pessoa ";
		
		if (!StringUtils.isEmpty(siape))
			buscaServidor = "inner join rh.servidor s on s.id_pessoa = p.id_pessoa ";
		
		String sql =
			" select distinct "+PROJECAO_FIXA_CONSULTA_PESSOA+" from comum.pessoa p " +
			buscaDiscente +
			buscaServidor;
		
		sql += " where  p.tipo = '"+PessoaGeral.PESSOA_FISICA+"' AND  valido = trueValue() ";
		
		if (!StringUtils.isEmpty(matricula)){
			sql += " AND d.matricula = :matricula ";
			passouCriterioDeBusca = true;
		}
		
		if (!StringUtils.isEmpty(siape)){
			sql += " AND s.siape = :siape ";
			passouCriterioDeBusca = true;
		}
			
		if (!StringUtils.isEmpty(cpf)){
			sql += " AND p.cpf_cnpj = :cpf ";
			passouCriterioDeBusca = true;
		}
		
		if (!StringUtils.isEmpty(passaporte)){
			sql += " AND p.passaporte = :passaporte ";
			passouCriterioDeBusca = true;
		}
		
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 ){
			sql += " AND p.nome_ascii like :nome ";
			passouCriterioDeBusca = true;
		}
		
		if(! passouCriterioDeBusca )
			sql += " AND 1 = 0 ";
		
		sql += " ORDER BY p.nome ";
		
		sql += BDUtils.limit(100);
		
		Query q = getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(matricula)){
			try{
				q.setLong("matricula", Long.parseLong(matricula));
			}catch(NumberFormatException nfe){
				return new ArrayList<Object[]>();
			}
		}
			
		if (!StringUtils.isEmpty(siape)){
			try{
				q.setInteger("siape", Integer.parseInt(siape));
			}catch(NumberFormatException nfe){
				return new ArrayList<Object[]>();
			}
		}	
			
		if (!StringUtils.isEmpty(cpf))
			q.setLong("cpf", Long.parseLong(cpf));
		
		
		if (!StringUtils.isEmpty(passaporte))
			q.setString("passaporte", passaporte);
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 )
			q.setString("nome", "%"+StringUtils.toAscii(nome).toUpperCase()+"%");
		
		
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		
		// Algumas pessoa n�o tem CPF, nem passaporte 
		for (Object[] objects : lista) {
			if(objects[POSICAO_CPF] == null)
				objects[POSICAO_CPF] = "";
			
			if(objects[POSICAO_PASSAPORTE] == null)
				objects[POSICAO_PASSAPORTE] = "";
		} 
		
		return lista;
	}
	
	
	
	
	
	/**
	 * Retorna a pessoa que possuem cadastro no sistema de biblioteca. Idepedentemente se a pessoa pode fazer empr�timos ou n�o.
	 * 
	 * <p><strong>Esse deve ser o �nico m�todo usado para recuperar os usu�rios da biblioteca na parte de circu��o</strong></p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []> findPessoasComUsuarioBibliotecaAtivo(String cpf, String  passaporte, String matricula, String siape, String nome) throws DAOException{
		
		String buscaDiscente = "";
		String buscaServidor = "";
		
		boolean passouCriterioDeBusca = false;
		
		
		if (!StringUtils.isEmpty(matricula))
			buscaDiscente = "inner join discente d on d.id_pessoa = p.id_pessoa ";
		
		if (!StringUtils.isEmpty(siape))
			buscaServidor = "inner join rh.servidor s on s.id_pessoa = p.id_pessoa ";
		
		String sql =
			" select distinct "+PROJECAO_FIXA_CONSULTA_PESSOA+", ub.id_usuario_biblioteca, 0, text('') as login "
			+"FROM comum.pessoa p " +
			" join biblioteca.usuario_biblioteca ub on ub.id_pessoa = p.id_pessoa " +
			buscaDiscente +
			buscaServidor;
		
		sql += " where ub.ativo = trueValue() AND ub.quitado = falseValue() ";
		
		if (!StringUtils.isEmpty(matricula)){
			sql += " AND d.matricula = :matricula ";
			passouCriterioDeBusca = true;
		}
		
		if (!StringUtils.isEmpty(siape)){
			sql += " AND s.siape = :siape ";
			passouCriterioDeBusca = true;
		}
			
		if (!StringUtils.isEmpty(cpf)){
			sql += " AND p.cpf_cnpj = :cpf ";
			passouCriterioDeBusca = true;
		}
		
		if (!StringUtils.isEmpty(passaporte)){
			sql += " AND p.passaporte = :passaporte ";
			passouCriterioDeBusca = true;
		}
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 ){
			sql += " AND p.nome_ascii like :nome ";
			passouCriterioDeBusca = true;
		}
		
		if(! passouCriterioDeBusca )
			sql += " AND 1 = 0 ";
		
		sql += " ORDER BY p.nome ";
		
		sql += BDUtils.limit(100);
		
		Query q = getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(matricula))
			q.setLong("matricula", Long.parseLong(matricula));
			
		if (!StringUtils.isEmpty(siape)){
			try{
				q.setInteger("siape", Integer.parseInt(siape));
			}catch(NumberFormatException nfe){
				return new ArrayList<Object[]>();
			}
		}	
			
		if (!StringUtils.isEmpty(cpf))
			q.setLong("cpf", Long.parseLong(cpf));
		
		if (!StringUtils.isEmpty(passaporte))
			q.setString("passaporte", passaporte);
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 )
			q.setString("nome", "%"+StringUtils.toAscii(nome).toUpperCase()+"%");
		
		
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		
		// Algumas pessoa n�o tem CPF, nem passaporte 
		for (Object[] objects : lista) {
			if(objects[0] == null)
				objects[0] = "";
			
			if(objects[1] == null)
				objects[1] = "";
		} 
		
		return lista;
	}
	
	
	/**
	 * Retorna as informa��es de uma biblioteca para ser usada principalmente nas buscas de circula��o.
	 * 
	 * <p><strong>Esse deve ser o �nico m�todo usado para recuperar os usu�rios da biblioteca das biblioteca na parte de circu��o</strong></p>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <Object []>  findBibliotecaComUsuarioBibliotecaAtivo(int idBiblioteca, boolean buscarAsPessoasSemCadastroBiblioteca) throws DAOException {
		
		String hql = "SELECT "+PROJECAO_FIXA_CONSULTA_BIBLIOTECA+", ub.id, 0 FROM UsuarioBiblioteca ub ";
		hql += " INNER JOIN ub.biblioteca b "; // S� tr�s as biblioteca que possuem usu�rio biblioteca cadastrados
		hql += " WHERE b.ativo = trueValue()  AND ub.ativo = trueValue() AND b.id = :idBiblioteca ";
		
		if(! buscarAsPessoasSemCadastroBiblioteca)
			hql += "  AND ub.quitado = falseValue() ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setMaxResults(100);
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		return lista;
	}
	
	
	
	/**
	 * Retorna a pessoa que possua um usu�rio externo cadastrado no sistema, pelo cpf ou nome. 
	 *     Idepedentemente se a pessoa pode fazer empr�timos ou n�o.
	 * 
	 * <p><strong>Esse deve ser o �nico m�todo usado para recuperar os usu�rios externos na parte de circu��o</strong></p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []> findPessoaComUsuarioExternoBibliotecaAtivo(String cpf, String passaporte, String nome) throws HibernateException, DAOException{
		
	
		
		String sql =
			" select distinct "+PROJECAO_FIXA_CONSULTA_PESSOA+", ub.id_usuario_biblioteca, 0 from comum.pessoa p " +
			" inner join biblioteca.usuario_biblioteca ub on ub.id_pessoa = p.id_pessoa " +
			" inner join biblioteca.usuario_externo_biblioteca ubExt on ubExt.id_usuario_biblioteca = ub.id_usuario_biblioteca " +
			" where ubExt.ativo = trueValue() AND ub.ativo = trueValue() AND ub.quitado = falseValue() ";
		
		if(StringUtils.isEmpty(cpf) && StringUtils.isEmpty(nome)  && StringUtils.isEmpty(passaporte))
			sql += " AND 1 = 0 ";
		
		if (!StringUtils.isEmpty(cpf)){
			sql += " AND p.cpf_cnpj = :cpf ";
		}
		
		if (!StringUtils.isEmpty(passaporte)){
			sql += " AND p.passaporte = :passaporte ";
		}
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 )
			sql += " AND p.nome_ascii like :nome ";
		
		sql += BDUtils.limit(100);
		
		Query q = getSession().createSQLQuery(sql);
			
		if (!StringUtils.isEmpty(cpf))
			q.setLong("cpf", Long.parseLong(cpf));
		
		if (!StringUtils.isEmpty(passaporte))
			q.setString("passaporte", passaporte);
		
		if (!StringUtils.isEmpty(nome) && nome.length() >= 3 )
			q.setString("nome", "%"+StringUtils.toAscii(nome).toUpperCase()+"%");
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		
		// Algumas pessoa n�o tem CPF, nem passaporte 
		for (Object[] objects : lista) {
			if(objects[POSICAO_CPF] == null)
				objects[POSICAO_CPF] = "";
			
			if(objects[POSICAO_PASSAPORTE] == null)
				objects[POSICAO_PASSAPORTE] = "";
		} 
		
		return lista;
	}



	
	/* *********************************************************************
	 * 
	 * M�todos que retornam as posi��es no array de retorno da pesquisa para serem 
	 * acess�veis a partir da p�gina de resultados da consulta de usu�rios da biblioteca
	 * 
	 ***********************************************************************/

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoCpf() {
		return POSICAO_CPF;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoPassaporte() {
		return POSICAO_PASSAPORTE;
	}
	
	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoInternacional() {
		return POSICAO_INTERNACIONAL;
	}
	
	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoIdPessoa() {
		return POSICAO_ID_PESSOA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoNome() {
		return POSICAO_NOME;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoDataNascimento() {
		return POSICAO_DATA_NASCIMENTO;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoIdUsuarioBiblioteca() {
		return POSICAO_ID_USUARIO_BIBLIOTECA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoQuantidadeEmprestimos() {
		return POSICAO_QUANTIDADE_EMPRESTIMOS;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoLoginUsuario() {
		return POSICAO_LOGIN_USUARIO;
	}
	
	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoIdBiblioteca() {
		return POSICAO_ID_BIBLIOTECA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoIdentificadorBiblioteca() {
		return POSICAO_IDENTIFICADOR_BIBLIOTECA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoDescricaoBiblioteca() {
		return POSICAO_DESCRIACAO_BIBLIOTECA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoIdUsuarioBibliotecaBiblioteca() {
		return POSICAO_ID_USUARIO_BIBLIOTECA_BIBLIOTECA;
	}

	/**
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public  int getPosicaoQuantidadeEmprestimosBiblioteca() {
		return POSICAO_QUANTIDADE_EMPRESTIMOS_BILIOTECA;
	}
	
}
