/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2011
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>Dao para busca nas multas dos usu�rios na biblioteca </p>
 * 
 * @author jadson
 *
 */
public class MultaUsuariosBibliotecaDao extends GenericSigaaDAO{
	
	
	/**
	 * <p>Retorna o valor geral das multas n�o pagas do usu�rio </p> 
	 *
	 * <p>M�todo geralmente utilizado para saber se o usu�rio est� multado </p>
	 *
	 * @param usuarioBiblioteca
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	
	public BigDecimal findValorMultasNaoPagaDoUsuario(Integer idPessoa, Integer idBiblioteca) throws DAOException {

		if((idPessoa == null || idPessoa <=0)  && ( idBiblioteca == null || idBiblioteca <=0 ) ) throw new DAOException("Deve-se passar o id da pessoa ou biblioteca !!!! ");
		
		String hql = "";
		
		if(idPessoa != null){
			hql = " SELECT m.valor from MultaUsuarioBiblioteca m " +
				" LEFT JOIN m.emprestimo e  " +
				" LEFT JOIN m.usuarioBiblioteca usuarioMulta " +
				
				" LEFT JOIN e.usuarioBiblioteca usuarioEmprestimo " +
				" LEFT JOIN usuarioEmprestimo.pessoa pessoaEmprestimo " +
				
				" LEFT JOIN usuarioMulta.pessoa pessoaMulta " +
				
				" WHERE m.status = "+StatusMulta.EM_ABERTO+" AND m.ativo = :true "+  // as multas que n�o foram quitas e continuam ativas (n�o foram estornadas)
				" AND ( pessoaEmprestimo.id = :idPessoa OR pessoaMulta.id = :idPessoa ) ";
		}
		
		if(idBiblioteca != null){
			hql = " SELECT m.valor from MultaUsuarioBiblioteca m " +
					" LEFT JOIN m.emprestimo e " +
					" LEFT JOIN m.usuarioBiblioteca usuarioMulta   " +
									
					" LEFT JOIN e.usuarioBiblioteca usuarioEmprestimo " +
					" LEFT JOIN usuarioEmprestimo.biblioteca bibliotecaEmprestimo " +
					
					" LEFT JOIN usuarioMulta.biblioteca bibliotecaMulta " +
					
					" WHERE m.status = "+StatusMulta.EM_ABERTO+" AND m.ativo = :true "+  // as multas que n�o foram quitas e continuam ativas (n�o foram estornadas)
					" AND ( bibliotecaEmprestimo.id = :idBiblioteca OR bibliotecaMulta.id = :idBiblioteca ) ";
		}
		
		Query q = getSession().createQuery(hql);		
		if(idPessoa != null) q.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<BigDecimal> valoresMulta = q.list();
		
		BigDecimal valorTotal = new BigDecimal(0);
		
		for (BigDecimal valor : valoresMulta) {
			valorTotal = valorTotal.add(valor);
		}
		
		return valorTotal;
		
	}
	
	
	
	/**
	 * <p>Retorna as multas ativas do usu�rio na biblioteca. </p>
	 * 
	 * <p><strong> RETORNA AS MULSTAS APENAS PARA A CONTA DO USU�RIO PASSADO.</strong> </p>
	 * 
	 * <p>Multas ativas s�o aquelas n�o estornadas e n�o quitadas (com status EM_ABERTO)</p>
	 * 
	 * @param usuarioBiblioteca
	 * @return null se n�o estiver suspenso.
	 * @throws DAOException
	 */
	public List<MultaUsuarioBiblioteca> findMultasAtivasDoUsuario(Integer idPessoa, Integer idBiblioteca) throws DAOException{

		if((idPessoa == null || idPessoa <=0)  && ( idBiblioteca == null || idBiblioteca <=0 ) ) throw new DAOException("Deve-se passar o id da pessoa ou biblioteca !!!! ");
		
		String hql = " SELECT "+
			"  multa.id, multa.valor, multa.manual, pessoaCadastro.nome, multa.dataCadastro, multa.motivoCadastro" +
			", emprestimo.dataEmprestimo, emprestimo.prazo, emprestimo.dataDevolucao" +
			", material.codigoBarras" +
			", COALESCE(biblioteca1.id, biblioteca2.id) as bibliotecaRecolhimento, COALESCE(biblioteca1.descricao, biblioteca2.descricao) as descricaoBibliotecaRecolhimento, COALESCE(unidade1.id, unidade2.id ) as unidadeRecolhimento " + // para verificar qual configura��o de GRU ser� usada na impress�o
			", multa.idGRUQuitacao "+
			", COALESCE ( usuarioMulta.id, usuarioEmprestimo.id ) as idUsuarioBiblioteca " +
			
			" FROM MultaUsuarioBiblioteca multa " +
			" LEFT JOIN multa.emprestimo emprestimo " +
			" LEFT JOIN emprestimo.usuarioBiblioteca  usuarioEmprestimo " +
			" LEFT JOIN usuarioEmprestimo.pessoa pessoaEmprestimo " +
			" LEFT JOIN multa.usuarioBiblioteca  usuarioMulta " +
			" LEFT JOIN usuarioMulta.pessoa pessoaMulta " +
			
			" LEFT JOIN usuarioEmprestimo.biblioteca bibliotecaEmprestimo " +
			" LEFT JOIN usuarioMulta.biblioteca bibliotecaMulta " +
			
			
			" LEFT JOIN multa.usuarioCadastro usuarioCadastro " +
			" LEFT JOIN usuarioCadastro.pessoa pessoaCadastro " +
			
			" LEFT JOIN emprestimo.material material " +
			" LEFT JOIN material.biblioteca biblioteca1 " +
			" LEFT JOIN biblioteca1.unidade unidade1 " +
			" LEFT JOIN multa.bibliotecaRecolhimento biblioteca2 " +
			" LEFT JOIN biblioteca2.unidade unidade2 "+
			
			" WHERE 1 = 1 ";
		
			if(idPessoa != null)
				hql += "  AND ( pessoaEmprestimo.id = :idPessoa  OR pessoaMulta.id = :idPessoa ) ";
			if(idBiblioteca != null)
				hql += "  AND ( bibliotecaEmprestimo.id = :idBiblioteca  OR bibliotecaMulta.id = :idBiblioteca ) ";
		
			hql +=  " AND multa.status = "+StatusMulta.EM_ABERTO+" AND multa.ativo = :true "; // as multas que n�o foram quitas e continuam ativas (n�o foram estornadas)
			
		Query q = getSession().createQuery(hql);
		if(idPessoa != null) q.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<MultaUsuarioBiblioteca> multas = new ArrayList<MultaUsuarioBiblioteca>();
		
		//  HibernateUtils.parseTo n�o funcionou aqui
		for (Object[] objects : linhas) {
			
			MultaUsuarioBiblioteca multa = new MultaUsuarioBiblioteca();
			multa.setId(  (Integer) objects[0] );
			multa.setValor(  (BigDecimal) objects[1] );
			multa.setManual(  (Boolean) objects[2] );
			
			if(multa.isManual()){
				multa.setUsuarioCadastro( new Usuario(0, (String) objects[3],""));
				multa.setDataCadastro((Date) objects[4] );
				multa.setMotivoCadastro((String) objects[5] );
				
				if((Integer) objects[10] != null){ // ser for nulo, por padr�o a GRU vai ser emitida para a biblioteca central
					multa.setBibliotecaRecolhimento(                new Biblioteca(  (Integer) objects[10], (String) objects[11] )   );
					multa.getBibliotecaRecolhimento().setUnidade(   new Unidade   (  (Integer) objects[12] )   );
				}
				
				multa.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[14]));
				
				// Necess�rio para emitir a GRU //
				if(idPessoa != null)
					multa.getUsuarioBiblioteca().setPessoa(new Pessoa(idPessoa));
				if(idBiblioteca != null)
					multa.getUsuarioBiblioteca().setBiblioteca(new Biblioteca(idBiblioteca));
				//////////////////////////////////
				
			}else{
				Emprestimo e = new Emprestimo();
				e.setDataEmprestimo((Date) objects[6] );
				e.setPrazo((Date) objects[7] );
				e.setDataDevolucao((Date) objects[8] );
				
				e.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[14]));
				
				// Necess�rio para emitir a GRU //
				if(idPessoa != null)
					e.getUsuarioBiblioteca().setPessoa(new Pessoa(idPessoa));
				if(idBiblioteca != null)
					e.getUsuarioBiblioteca().setBiblioteca(new Biblioteca(idBiblioteca));
				//////////////////////////////////
				
				Exemplar ex = new Exemplar(); // S� para mostrar o c�digo de barras, biblioteca e unidade, ent�o n�o importa se � exemplar ou fasc�culo
				ex.setCodigoBarras( (String) objects[9] );
				
				// ser a biblioteca do empr�stimos n�o tiver uma configura��o de GRU criada, por padr�o a GRU vai ser emitida para a biblioteca central
				ex.setBiblioteca( new Biblioteca((Integer) objects[10], (String) objects[11]  ) );
				ex.getBiblioteca().setUnidade( new Unidade((Integer) objects[12] ) );
				
				e.setMaterial(ex );
				multa.setEmprestimo( e );
			}
			
			multa.setIdGRUQuitacao( (Integer) objects[13]);
			
			multas.add(multa);
		}
		
		return multas;
		
	}


	

	
	
	
	
	
	
	/**
	 * <p>Retorna todas as multas ativas em aberto no sistema para quais quais foram emitidas a GRU para pagamento.</p>
	 * 
	 * <p>Multas ativas s�o aquelas n�o estornadas e n�o quitadas (com status EM_ABERTO)</p>
	 * 
	 * <p> M�todo usado para dar baixa nos pagamentos realizados de forma autom�tica.</p>
	 * 
	 * @param usuarioBiblioteca
	 * @return null se n�o estiver suspenso.
	 * @throws DAOException
	 */
	public List<MultaUsuarioBiblioteca> findAllMultasAtivasComGRUSistema() throws DAOException{

		
		
		String hql = " SELECT "+
		    "  multa.id, multa.valor, multa.manual, pessoa.nome, multa.dataCadastro, multa.motivoCadastro, multa.numeroReferencia" +
		    ", COALESCE(usuarioBibliotecaManual.id, usuarioBibliotecaAutomatico.id) as usuarioBiblioteca " +
		    ", emprestimo.dataEmprestimo, emprestimo.prazo, emprestimo.dataDevolucao " +
			", material.codigoBarras " +
			", multa.idGRUQuitacao "+
			" FROM MultaUsuarioBiblioteca multa " +
			" LEFT JOIN multa.emprestimo emprestimo  " +
			" LEFT JOIN multa.usuarioBiblioteca usuarioBibliotecaManual " +
			" LEFT JOIN emprestimo.usuarioBiblioteca usuarioBibliotecaAutomatico " +
			" LEFT JOIN emprestimo.material material " +
			" LEFT JOIN multa.usuarioCadastro usuarioCadastro " +
			" LEFT JOIN usuarioCadastro.pessoa pessoa " +
			" WHERE multa.status = "+StatusMulta.EM_ABERTO
			+" AND multa.ativo = trueValue() "         // n�o confirma o pagamento de multas extornadas
			+" AND multa.idGRUQuitacao IS NOT NULL ";  // as multas que n�o foram quitas e continuam ativas (n�o foram estornadas)
		
			
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<MultaUsuarioBiblioteca> multas = new ArrayList<MultaUsuarioBiblioteca>();
		
		//  HibernateUtils.parseTo n�o funcionou aqui
		for (Object[] objects : linhas) {
			
			MultaUsuarioBiblioteca multa = new MultaUsuarioBiblioteca();
			multa.setId(  (Integer) objects[0] );
			multa.setValor(  (BigDecimal) objects[1] );
			multa.setManual(  (Boolean) objects[2] );
			
			multa.setNumeroReferencia((Long) objects[6]);
			
			if(multa.isManual()){
				
				// para impress�o no comprovante
				multa.setUsuarioCadastro( new Usuario(0, (String) objects[3],""));
				multa.setDataCadastro((Date) objects[4] );
				multa.setMotivoCadastro((String) objects[5] );
				
				
				multa.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[7]) );
				
				
			}else{
				Emprestimo e = new Emprestimo();
				
				e.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[7]) );
				
				e.setDataEmprestimo((Date) objects[8] );
				e.setPrazo((Date) objects[9] );
				e.setDataDevolucao((Date) objects[10] );
				
				
				
				Exemplar ex = new Exemplar(); // S� para mostrar o c�digo de barras, biblioteca e unidade, ent�o n�o importa se � exemplar ou fasc�culo
				ex.setCodigoBarras( (String) objects[11] );
				
				e.setMaterial(ex );
				multa.setEmprestimo( e );
			}
			
			multa.setIdGRUQuitacao( (Integer) objects[12]);
			
			multas.add(multa);
		}
		
		return multas;
		
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * <p>Retorna todas as multas ativas em aberto no sistema que possuem o id da GRU passada</p>
	 * 
	 * <p>Multas ativas s�o aquelas n�o estornadas e n�o quitadas (com status EM_ABERTO)</p>
	 * 
	 * <p> M�todo usado para dar baixa nos pagamentos quando o usu�rio gera uma �nica GRU para v�rias multas.</p>
	 * 
	 * <p>Observa��o:  Deve retornar os mesmos dados do m�todo <code>findAllMultasAtivasComGRUSistema() </code> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return null se n�o estiver suspenso.
	 * @see this.findAllMultasAtivasComGRUSistema()
	 * @throws DAOException
	 */
	public List<MultaUsuarioBiblioteca> findAllMultasUsuarioAtivasComMesmaGRUSistema(Integer idPessoa, Integer idBiblioteca, int idGRU) throws DAOException{

		if(idPessoa == null && idBiblioteca == null) throw new DAOException("Deve-se passar o id da pessoa ou biblioteca !!!! ");
		
		String hql = " SELECT "+
		    "  multa.id, multa.valor, multa.manual, pessoa.nome, multa.dataCadastro, multa.motivoCadastro, multa.numeroReferencia" +
		    ", COALESCE(usuarioMulta.id, usuarioEmprestimo.id) as idUsuarioBiblioteca " +
		    ", emprestimo.dataEmprestimo, emprestimo.prazo, emprestimo.dataDevolucao " +
			", material.codigoBarras " +
			", multa.idGRUQuitacao "+
			" FROM MultaUsuarioBiblioteca multa " +
			" LEFT JOIN multa.emprestimo emprestimo  " +
			
			" LEFT JOIN emprestimo.usuarioBiblioteca  usuarioEmprestimo " +
			" LEFT JOIN multa.usuarioBiblioteca  usuarioMulta " +
			
			" LEFT JOIN usuarioEmprestimo.pessoa pessoaEmprestimo " +
			" LEFT JOIN usuarioMulta.pessoa pessoaMulta " +
			
			" LEFT JOIN usuarioEmprestimo.biblioteca bibliotecaEmprestimo " +
			" LEFT JOIN usuarioMulta.biblioteca bibliotecaMulta " +
			
			" LEFT JOIN emprestimo.material material " +
			" LEFT JOIN multa.usuarioCadastro usuarioCadastro " +
			" LEFT JOIN usuarioCadastro.pessoa pessoa " +
			" WHERE multa.status = "+StatusMulta.EM_ABERTO  // as multas que n�o foram quitas e continuam ativas (n�o foram estornadas)
			+" AND multa.ativo = trueValue() " 
			+" AND multa.idGRUQuitacao IS NOT NULL AND multa.idGRUQuitacao = :idGRU ";
			  
			if(idPessoa != null)
				hql += "  AND ( pessoaEmprestimo.id = :idPessoa  OR pessoaMulta.id = :idPessoa ) ";
			if(idBiblioteca != null)
				hql += "  AND ( bibliotecaEmprestimo.id = :idBiblioteca  OR bibliotecaMulta.id = :idBiblioteca ) ";
		
			
		Query q = getSession().createQuery(hql);
		q.setInteger("idGRU", idGRU);
		if(idPessoa != null) q.setInteger("idPessoa", idPessoa);
		if(idBiblioteca != null) q.setInteger("idBiblioteca", idBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<MultaUsuarioBiblioteca> multas = new ArrayList<MultaUsuarioBiblioteca>();
		
		//  HibernateUtils.parseTo n�o funcionou aqui
		for (Object[] objects : linhas) {
			
			MultaUsuarioBiblioteca multa = new MultaUsuarioBiblioteca();
			multa.setId(  (Integer) objects[0] );
			multa.setValor(  (BigDecimal) objects[1] );
			multa.setManual(  (Boolean) objects[2] );
			
			multa.setNumeroReferencia((Long) objects[6]);
			
			if(multa.isManual()){
				
				// para impress�o no comprovante
				multa.setUsuarioCadastro( new Usuario(0, (String) objects[3],""));
				multa.setDataCadastro((Date) objects[4] );
				multa.setMotivoCadastro((String) objects[5] );
				
				
				multa.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[7]) );
				
				
			}else{
				Emprestimo e = new Emprestimo();
				
				e.setUsuarioBiblioteca(new UsuarioBiblioteca((Integer) objects[7]) );
				
				e.setDataEmprestimo((Date) objects[8] );
				e.setPrazo((Date) objects[9] );
				e.setDataDevolucao((Date) objects[10] );
				
				
				
				Exemplar ex = new Exemplar(); // S� para mostrar o c�digo de barras, biblioteca e unidade, ent�o n�o importa se � exemplar ou fasc�culo
				ex.setCodigoBarras( (String) objects[11] );
				
				e.setMaterial(ex );
				multa.setEmprestimo( e );
			}
			
			multa.setIdGRUQuitacao( (Integer) objects[12]);
			
			multas.add(multa);
		}
		
		return multas;
		
	}
	
	
	/**
	 * <p>Realiza a baixa das multas quitadas automaticamente com uma �nica consulta ao banco.</p>
	 * 
	 * <p>S� quita as multas ainda abertas no sistema, caso o usu�rio tenha se antecipado ao arquivo de retorno do banco 
	 * e realizado a quita��o manual, a quita��o autom�tica n�o ser� registrada.</p>
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws HibernateException 
	 */
	public void darBaixaMultasAbertas(List<Integer> idsMultasQuitadasAutomaticamente, Date dataQuitacao) throws DAOException {
		
		Query q = getSession().createSQLQuery( 
				" UPDATE biblioteca.multa_usuario_biblioteca set status = "+StatusMulta.QUITADA_AUTOMATICAMENTE+" , data_quitacao = :dataQuitacao " +
				" WHERE  id_multa_usuario_biblioteca IN "+UFRNUtils.gerarStringIn(idsMultasQuitadasAutomaticamente)+" AND status = "+StatusMulta.EM_ABERTO );
		
		q.setTimestamp("dataQuitacao", dataQuitacao);
		
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao dar baixa nas multas quitadas automaticamente.");
	}
	
	
	
	
	/**
	 * Retorna a unidade da suspens�o para verificar se o usu�rio tem permiss�o de mexer nessa suspens�o.
	 * 
	 * @param suspensao
	 * @return
	 * @throws DAOException
	 */
	public Integer findUnidadeDaMulta(Integer idMulta) throws DAOException {
		
		String projecao = " m.emprestimo.material.biblioteca.unidade.id "; // suspens�es automaticas
		
		String hql =
				" SELECT "+projecao+
				" FROM MultaUsuarioBiblioteca m "+
				" WHERE m.id = :idMulta ";
		
				
		Query q = getSession().createQuery(hql);
		q.setInteger("idMulta", idMulta);
				
				
		return (Integer) q.uniqueResult();
		
	}
	
	
	
}
