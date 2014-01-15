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
 *        Classe que contém as regras de negócio para verificar a situação de um usuário
 * no sistema de bibliotecas. 
 * 
 * @author jadson
 * @since 07/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class SituacaoUsuarioValidator {
	
//	/**
//	 *  Verifica a situação do usuário com relação a suspensões e pendências junto a biblioteca.
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
//			situacoes.add(new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_SEM_PENDENCIAS, "Usuário não possui débitos nas bibliotecas.")); 
//		
//		return situacoes; 
//	}
//	
//	
//	
//	/**
//	 * Encontra os empréstimos ativos e cria objetos EmprestimoDTO para passar
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
//			// Se tiver pelo menos um ativo, não pode emitir o documento de quitação.
//			if (emprestimosAtivos.size() > 0)
//				if (quantidadeEmprestimosAtrasados > 0)
//					return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_POSSUI_EMPRESTIMOS_ATRASADOS,  
//							"Usuário possui "+emprestimosAtivos.size()+" empréstimo(s) aberto(s)"
//							+ " e desses "+quantidadeEmprestimosAtrasados+" está(ão) atrasado(s).");
//				else
//					return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_POSSUI_EMPRESTIMOS_ATIVOS,  
//							"Usuário possui "+emprestimosAtivos.size()+" empréstimo(s) aberto(s).");
//
//			return null; // Situação ok.
//		} finally {
//			if (daoEmprestimo != null) daoEmprestimo.close();
//		}
//	}
//	
//	
//	
//	/**
//	 * Configura a suspensão do usuário se ele tiver suspenso.
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
//			// Se está suspenso, então a situação que é mostrada é essa,
//			// mesmo que esteja com empréstimos atrasados também.
//			if (quantidadeDiasSuspensos > 0){
//				
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//				
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(new Date());
//				cal.add(Calendar.DAY_OF_MONTH, quantidadeDiasSuspensos);
//				
//				return new SituacaoUsuarioBiblioteca(ConstantesBiblioteca.SITUACAO_USUARIO_ESTA_SUSPENSO, "Usuário está suspenso até "+sdf.format(cal.getTime())+"");
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
