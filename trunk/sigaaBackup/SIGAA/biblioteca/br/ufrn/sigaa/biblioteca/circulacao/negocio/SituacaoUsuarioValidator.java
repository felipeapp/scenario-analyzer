/*
 * SituacaoUsuarioValidator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;


/**
 *
 *        Classe que cont�m as regras de neg�cio para verificar a situa��o de um usu�rio
 * no sistema de bibliotecas. 
 * 
 * @author jadson
 * @since 07/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class SituacaoUsuarioValidator {
	
//	/**
//	 *  Verifica a situa��o do usu�rio com rela��o a suspens�es e pend�ncias junto a biblioteca.
//	 */
//	public static List<SituacaoUsuarioBiblioteca> verificaSituacaoUsuario(int idUsuarioBiblioteca) throws DAOException, ArqException, NegocioException{
//		
//		List<SituacaoUsuarioBiblioteca> situacoes = new ArrayList<SituacaoUsuarioBiblioteca>();
//		
//		SituacaoUsuarioBiblioteca temp  = verificaSuspensaoUsuario(idUsuarioBiblioteca);
//		
//		if (temp !=null)
//			situacoes.add(temp);
//		
//		temp = verificaEmprestimosUsuario(idUsuarioBiblioteca);
//		
//		if (temp != null)
//			situacoes.add(temp);
//		
//		if(situacoes.size() == 0)
//			situacoes.add(new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_SEM_PENDENCIAS, "Usu�rio n�o possui d�bitos nas bibliotecas.")); 
//		
//		return situacoes; 
//	}
//	
//	
//	
//	/**
//	 * Encontra os empr�stimos ativos e cria objetos EmprestimoDTO para passar
//	 * ao cliente desktop.
//	 */
//	private static SituacaoUsuarioBiblioteca verificaEmprestimosUsuario(int idUsuario) throws DAOException, NegocioException{
//		
//		EmprestimoDao daoEmprestimo = null;
//		
//		try{
//		
//			daoEmprestimo =  DAOFactory.getInstance().getDAO(EmprestimoDao.class, null, null);
//			
//			List<Emprestimo> emprestimosAtivos = daoEmprestimo.findEmprestimosAtivosByUsuarioMaterialBiblioteca(new UsuarioBiblioteca(idUsuario), null, null, false, null, null, null, null);
//			
//			int quantidadeEmprestimosAtrasados = 0;
//			
//			for (Emprestimo emprestimo : emprestimosAtivos)
//				if( emprestimo.isAtrasado())
//					quantidadeEmprestimosAtrasados++;
//			
//			// Se tiver pelo menos um ativo, n�o pode emitir o documento de quita��o.
//			if (emprestimosAtivos.size() > 0)
//				if (quantidadeEmprestimosAtrasados > 0)
//					return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_POSSUI_EMPRESTIMOS_ATRASADOS,  
//							"Usu�rio possui "+emprestimosAtivos.size()+" empr�stimo(s) aberto(s)"
//							+ " e desses "+quantidadeEmprestimosAtrasados+" est�(�o) atrasado(s).");
//				else
//					return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_POSSUI_EMPRESTIMOS_ATIVOS,  
//							"Usu�rio possui "+emprestimosAtivos.size()+" empr�stimo(s) aberto(s).");
//
//			return null; // Situa��o ok.
//		} finally {
//			if (daoEmprestimo != null) daoEmprestimo.close();
//		}
//	}
//	
//	
//	
//	/**
//	 * Configura a suspens�o do usu�rio se ele tiver suspenso.
//	 * @throws DAOException, ArqException 
//	 */
//	private static SituacaoUsuarioBiblioteca verificaSuspensaoUsuario (int idUsuarioBiblioteca) throws DAOException, ArqException{
//	
//		SuspensaoUsuarioBibliotecaDao daoSuspensao = null;
//		
//		try{
//			daoSuspensao =  DAOFactory.getInstance().getDAO(SuspensaoUsuarioBibliotecaDao.class, null, null);
//			
//			int quantidadeDiasSuspensos = daoSuspensao.findQuantidadeDiasSuspenso(new UsuarioBiblioteca(idUsuarioBiblioteca));
//			
//			// Se est� suspenso, ent�o a situa��o que � mostrada � essa,
//			// mesmo que esteja com empr�stimos atrasados tamb�m.
//			if (quantidadeDiasSuspensos > 0){
//				
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//				
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(new Date());
//				cal.add(Calendar.DAY_OF_MONTH, quantidadeDiasSuspensos);
//				
//				return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_ESTA_SUSPENSO, "Usu�rio est� suspenso at� "+sdf.format(cal.getTime())+"");
//			}
//
//			return null;
//		
//		} finally {
//			if (daoSuspensao != null) daoSuspensao.close();
//		}
//		
//	}
	
	
	
	
}
