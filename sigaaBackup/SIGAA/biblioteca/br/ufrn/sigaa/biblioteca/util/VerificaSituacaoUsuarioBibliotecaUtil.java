/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dao.BloqueioUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p> Classe que os m�todos padr�es para verifica a situa��o de um usu�rio na bilioteca.</p>
 *
 * 
 * @author jadson
 *
 */
public class VerificaSituacaoUsuarioBibliotecaUtil {

	
	/**
	 *  Verifica se o v�nculo que foi atribu�do ao usu�rio no cadastro na biblioteca ainda continua ativo.
	 *
	 * @param usuarioBiblioteca
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void verificaVinculoUtilizadoAtivo(UsuarioBiblioteca usuarioBiblioteca) throws NegocioException, DAOException{
	
		if(usuarioBiblioteca.getVinculo() == null || usuarioBiblioteca.getIdentificacaoVinculo() == null){
			throw new NegocioException("O v�nculo atualmente utilizado expirou, realize um recadastro para obter um novo v�nculo para poder utilizar a biblioteca.");
		}
		
		if(! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
			boolean vinculoAtivo = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().isVinculoAtivo(usuarioBiblioteca.getPessoa().getId(), usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo());
		
			if(! vinculoAtivo)
				throw new NegocioException("O v�nculo atualmente utilizado expirou, realize um recadastro para obter um novo v�nculo para poder utilizar a biblioteca.");
			
		}else{
			// V�nculos de bibliotecas n�o expiram porque elas n�o podem trocar de v�nculo como ocorre com os usu�rio normais no sistema
		}
	}
	
	
	/**
	 * <p>Verifica se a pessoa ou a biblioteca est� bloqueada no sistema.</p>
	 * 
	 * <p>O bloqueio � independente se o usu�rio possui uma conta ativa atualmente na biblioteca ou n�o.</p>
	 *
	 * @param usuarioBiblioteca
	 * @return 
	 * @throws NegocioException
	 * @throws DAOException 
	 */
	public static void verificaUsuarioBloqueado(Integer idPessoa, Integer idBiblioteca) throws NegocioException, DAOException{
		
		BloqueioUsuarioBibliotecaDao dao = null;
		
		try{
		
			dao =  DAOFactory.getInstance().getDAO(BloqueioUsuarioBibliotecaDao.class, null, null);
			
			String motivo = dao.isUsuarioBibliotecaBloqueado(idPessoa, idBiblioteca);
		
			if(motivo != null )
				throw new NegocioException("O usu�rio n�o pode mais realizar o empr�stimo pois est� bloqueado. Motivo: "+motivo);
		
		
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	/**
	 * Retorna o motivo do bloqueio, caso o usu�rio esteja
	 *
	 * @param usuarioBiblioteca
	 * @return 
	 * @throws NegocioException
	 * @throws DAOException 
	 */
	public static String getMotivoBloqueadoUsuario(UsuarioBiblioteca usuarioBiblioteca) throws NegocioException, DAOException{
		
		BloqueioUsuarioBibliotecaDao dao = null;
		
		try{
		
			dao =  DAOFactory.getInstance().getDAO(BloqueioUsuarioBibliotecaDao.class, null, null);
			
		
			String motivo = null;
			if(! usuarioBiblioteca.getVinculo().isVinculoBiblioteca())
				motivo = dao.isUsuarioBibliotecaBloqueado(usuarioBiblioteca.getPessoa().getId(), null);
			if(usuarioBiblioteca.getVinculo().isVinculoBiblioteca())
				motivo = dao.isUsuarioBibliotecaBloqueado(null, usuarioBiblioteca.getBiblioteca().getId());
		
			return motivo;
		
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	/**
	 * Interface para o m�todo: @sse {@link VerificaSituacaoUsuarioBibliotecaUtil#verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa, boolean)}
	 * @param pessoa
	 * @throws NegocioException
	 */
	public static void verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa pessoa)throws NegocioException{
		verificaDadosPessoaCorretosUtilizarBiblioteca(pessoa, true);
	}
	
	
	/**  
	 * <p>VERIFICA��O SE A PESSOA TEM CPF, OS USU�RIO BIBILIOTECA DEVEM EST� ASSOCIDADOS
	 * A PESSOAS NO SISTEMA QUE POSSUAM CPF, PARA EVITAR QUE USU�RIOS QUE POSSUEM PESSOAS DUPLICADAS,
	 * POSSSAM CADASTRAR V�RIOS USU�RIOS BIBLIOTECAS, J� QUE O USU�RIO BIBLIOTECA � POR PESSOA.</p>
	 * 
	 * @param pessoa
	 */
	public static void verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa pessoa, boolean somenteAtivos)throws NegocioException{
		
		if(pessoa.getTipo() != PessoaGeral.PESSOA_FISICA)
			throw new NegocioException("Usu�rio n�o pode utilizar a biblioteca porque seus dados pessoais est�o incompletos. Usu�rio est� cadastrado como pessoa jur�dica no sistema.");
		
		if(pessoa.getCpf_cnpj() != null && pessoa.getCpf_cnpj() > 0 && pessoa.isValido() ){
			// OK tem um CPF v�lido, pode utilizar o sistema
		}else{
			
			// Pessao n�o tem CPF v�lido, verifica se � pessoa entrageira //
			if(pessoa.isInternacional()){
				if( StringUtils.isEmpty(pessoa.getPassaporte() ) )
					throw new NegocioException("Usu�rio n�o pode utilizar a biblioteca porque seus dados pessoais est�o incompletos. Usu�rio � extrangeiro e n�o possui passaporte v�lido.");
			}else{
				
				if( ! verificaAlunoNivelInfantil(pessoa, somenteAtivos) ){// adicionado em 02/04/2012  # 85358 Cria o V�nculo Infantil
					
					// N�O tem CPF v�lido, NEM � extrageira, NEM � aluno do ensino infantil
					throw new NegocioException("Usu�rio n�o pode utilizar a biblioteca porque seus dados pessoais est�o incompletos. Usu�rio n�o possui CPF v�lido nem � aluno ativo de n�vel infantil.");
				}
			}
		}	
	}

	
	

	/** 
	 * <p>Consulta para saber se o usu�rio tem um v�nculo de discente de n�vel infantil </p>
	 * 
	 * <p>Nesse caso ele vai poder realizar empr�stimos sem CPF</p>
	 * 
	 * @param pessoa
	 * @return
	 * @throws NegocioException
	 */
	private static boolean  verificaAlunoNivelInfantil(Pessoa pessoa, boolean somenteAtivos)throws NegocioException {
		ConsultasEmprestimoDao dao = null;
		
		try{
			dao =  DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			return dao.pessoaPossuiVinculoInfantil(pessoa.getId(), somenteAtivos);
			
		} catch (DAOException e) {
			throw new NegocioException("Usu�rio n�o pode utilizar a biblioteca porque seus dados pessoais est�o incompletos. Usu�rio n�o possui CPF v�lido nem � aluno ativo de n�vel infantil.");
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	
	
	/** 
	 *  <p>M�todo que verifica se a pessoa passada possui alguma penalidade na biblioteca </p> 
	 *  
	 *  <p><strong>Importante: Esse m�todo j� busca para todas as contas que o usu�rio teve na biblioteca </strong> </p> 
	 *  
	 *  <p>As penalidades na biblioteca s�o: Ou a aluno <strong>est� suspenso</strong> ou <strong>possui multa n�o paga</strong> .</p>
	 * 
	 * @param idUsuarioBiblioteca
	 * @return retorna uma lista com as situa��es de puni��o do usu�rio .
	 */
	public static List<SituacaoUsuarioBiblioteca>  verificaUsuarioPossuiPunicoesBiblioteca(Integer idPessoa, Integer idBiblioteca) throws DAOException{
		
		List<SituacaoUsuarioBiblioteca>  lista = new ArrayList<SituacaoUsuarioBiblioteca>();
		
		List<PunicaoAtrasoEmprestimoStrategy>  punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
		
		for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
			SituacaoUsuarioBiblioteca situacao  = punicaoStrategy.verificaPunicoesUsuario(idPessoa, idBiblioteca);
			if(situacao != null)
				lista.add( situacao );
		}
		
		return lista;
	}
	
	
	/**
	 * <p> Verifica se o usu�rio pussui puni��es da nas bibliotecas de acordo com as estrat�gia utilizadas.</p>
	 * 
	 * <p> Lan�a exece��o caso o usu�rio possua puni��o.<p>
	 *  
	 * @throws NegocioException 
	 * @throws DAOException 
	 *
	 */
	public static void verificaUsuarioPossuiPunicoes(Integer idPessoa, Integer idBiblioteca) throws NegocioException, DAOException {
		
		List<SituacaoUsuarioBiblioteca> situacoesUsuario = verificaUsuarioPossuiPunicoesBiblioteca(idPessoa, idBiblioteca);
		
		ListaMensagens listaErros = new ListaMensagens();
		
		for (SituacaoUsuarioBiblioteca situacao : situacoesUsuario) {
			listaErros.addErro(situacao.getDescricaoCompleta());
		}
		if(listaErros.getErrorMessages().size() > 0 )
			throw new NegocioException(listaErros);
	}
	
	
	
	/**
	 *     Verifica se o usu�rio possu algum empr�timos em aberto ou atrasado para o conta passada, utilizado 
	 *  geralmente para impedir a emiss�o de diplomas ou cancelamento do v�nculo do usu�rio.
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static List<SituacaoUsuarioBiblioteca> verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(int idUsuarioBiblioteca) throws DAOException, NegocioException{
		
		List<SituacaoUsuarioBiblioteca> lista = new ArrayList<SituacaoUsuarioBiblioteca>();
		
		EmprestimoDao daoEmprestimo = null;
		
		try{
		
			daoEmprestimo =  DAOFactory.getInstance().getDAO(EmprestimoDao.class, null, null);
			
			List<Emprestimo> emprestimosAtivos = daoEmprestimo.findEmprestimosAtivosByUsuarioComInfoExtritamenteNecessarias(new UsuarioBiblioteca(idUsuarioBiblioteca));
			
			int quantidadeEmprestimosAtrasados = 0;
			
			for (Emprestimo emprestimo : emprestimosAtivos)
				if( emprestimo.isAtrasado())
					quantidadeEmprestimosAtrasados++;
			
			// Se tiver pelo menos um ativo, n�o pode emitir o documento de quita��o.
			if (emprestimosAtivos.size() > 0)
				if (quantidadeEmprestimosAtrasados > 0){
					SituacaoUsuarioBiblioteca situacaoAtrasado =  SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS;
					situacaoAtrasado.setDescricaoCompleta("Usu�rio possui "+emprestimosAtivos.size()+" empr�stimo(s) aberto(s) e desses "+quantidadeEmprestimosAtrasados+" est�(�o) atrasado(s).");
					lista.add(situacaoAtrasado);
				}
				else{
					SituacaoUsuarioBiblioteca situacaoAtivos =  SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS;
					situacaoAtivos.setDescricaoCompleta("Usu�rio possui "+emprestimosAtivos.size()+" empr�stimo(s) aberto(s).");
					lista.add(situacaoAtivos);
				}
			return lista; 
		} finally {
			if (daoEmprestimo != null) daoEmprestimo.close();
		}
	}
	
	
	/**
	 * Verfifica se o usu�rio possui empr�stimos atrasados
	 *
	 * @param emprestimosAtivos
	 * @throws NegocioException 
	 */
	public static void verificaUsuarioPossuiEmprestimosAtrazados(List<Emprestimo> emprestimosAtivos) throws NegocioException {
		for (Emprestimo emprestimoAtivo : emprestimosAtivos)
			if (emprestimoAtivo.isAtrasado())
				throw new NegocioException("O usu�rio n�o pode realizar o empr�stimo pois possui empr�stimos em atraso");
		
	}
	
	
	
	/** 
	 *  <p> M�todo que verifica se o usu�rio possui empr�stimos em atraso por um per�odo maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>M�todo para ser usado por sistema remotos, onde n�o � poss�vel garantir que as pessas tenham o id iguais.</p>
	 * 
	 * @param idPessoa
	 */
	public static void verificaUsuarioPossuiInrregularidadeAdministrativa(long cpf_cnpj) throws DAOException, NegocioException{
		
		ListaMensagens lista = verificaUsuarioPossuiInrregularidadeAdministrativa(null, cpf_cnpj);
		
		if(lista.isErrorPresent()){
			throw new NegocioException(lista);
		}
	}
	
	
	
	/** 
	 *  <p> M�todo que verifica se o usu�rio possui empr�stimos em atraso por um per�odo maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>M�todo para ser chamado dentro do SIGAA, onde est� bem definido o id da tabela pessoa..</p>
	 * 
	 * 
	 * @param idPessoa
	 */
	public static void verificaUsuarioPossuiInrregularidadeAdministrativa(int idPessoa) throws DAOException, NegocioException{
		
		ListaMensagens lista = verificaUsuarioPossuiInrregularidadeAdministrativa(idPessoa, null);
		
		if(lista.isErrorPresent()){
			throw new NegocioException(lista);
		}
	}
	
	
	/**
	 *  <p>M�todo interno que verifica se foi passado id da pessoa ou cpf e realiza as consultas de acordo com o que foi passado.</p>
	 * 
	 * @param idPessoa
	 */
	private static ListaMensagens verificaUsuarioPossuiInrregularidadeAdministrativa(Integer idPessoa, Long cpf_cnpj) throws DAOException{
		
		if(idPessoa == null && cpf_cnpj == null)
			throw new java.lang.IllegalArgumentException("� preciso passar o idPessoa ou CPF para consultar os empr�stimos do usu�rio.");
		
		ListaMensagens lista = new ListaMensagens();
		
		int qtdMaximoDiasAtraso = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_USUARIO_INCORRE_IRREGULARIDADE_ADMINISTRATIVA);
		
		if(qtdMaximoDiasAtraso > 0){
			EmprestimoDao emprestimoDao = null;
			
			try{
				emprestimoDao = DAOFactory.getInstance().getDAO(EmprestimoDao.class);
			
				int qtdEmprestimosAtrasados = 0;
				
				if(idPessoa != null)
					qtdEmprestimosAtrasados = emprestimoDao.countEmprestimosAtrasadosVencidoPorDeterminadoPeriodo(idPessoa, qtdMaximoDiasAtraso);
				
				if(cpf_cnpj != null)
					qtdEmprestimosAtrasados = emprestimoDao.countEmprestimosAtrasadosVencidoPorDeterminadoPeriodo(cpf_cnpj, qtdMaximoDiasAtraso);
				
				if( qtdEmprestimosAtrasados > 0)
					lista.addErro("Opera��o n�o p�de ser realizada, pois o usu�rio est� com um atraso superior a "+qtdMaximoDiasAtraso+" dia(s) na biblioteca. Incorreu em irregularidade administrativa!"); // Mensagem gen�rica
			
			}finally{
				if(emprestimoDao != null) emprestimoDao.close();
			}
			
		}
		// else :  verifica��o n�o habilitada para o sistema
		
		return lista;
		
	}
	
	
	
	/** 
	 *  <p>Verifica se o discente possui empr�stimos <strong>pendentes</strong> na biblioteca, feitos com o v�nculo de discente passado 
	 *        <i>(pol�tica de empr�stimo para aluno , aluno de p�s  ou m�dio/t�cnico)</i>.</p>
	 * 
	 *  <p>Empr�stimos pendentes s�o os empr�tima ativos do usu�rio, atrasados ou n�o. Esse m�todo n�o verifica se o usu�rio 
	 *  possui d�bitos a pagar com a biblioteca, como suspens�es ou multas.</p>
	 * 
	 * @param movimento
	 */
	public static ListaMensagens  verificaEmprestimoPendenteDiscente(Discente discente) throws DAOException{
		
		ListaMensagens lista = new ListaMensagens();
		
		UsuarioBibliotecaDao usuarioBibliotecaDao = null;
		EmprestimoDao emprestimoDao = null;
		
		try {
				
			usuarioBibliotecaDao =  DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			emprestimoDao = DAOFactory.getInstance().getDAO(EmprestimoDao.class);
			
			// por padr�o n�o pode cancelar/concluir discente sem antes verifica a sua situa��o na biblioteca
			Long qtdEmprestimosAtivoVinculo = 1l; 
		
			
			ObtemVinculoUsuarioBibliotecaStrategy estrategiaUtilizada = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
			
			List<Character> niveisPos = Arrays.asList(estrategiaUtilizada.getNiveisAlunosPosGraduacaoBiblioteca());
			List<Character> niveisGraduacao = Arrays.asList(estrategiaUtilizada.getNiveisAlunosGraduacaoBiblioteca());
			List<Character> niveisMedio = Arrays.asList(estrategiaUtilizada.getNiveisAlunosMedioTecnicoBiblioteca());
			List<Character> niveisInfantil = Arrays.asList(estrategiaUtilizada.getNiveisAlunosInfantilBiblioteca());
			
			if( niveisPos.contains(discente.getNivel()) ){
				qtdEmprestimosAtivoVinculo = emprestimoDao.countEmprestimosAtivosPorVinculoUsuario(discente.getPessoa().getId(), VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO, discente.getId());
			}else{
				if( niveisGraduacao.contains(discente.getNivel())){
					qtdEmprestimosAtivoVinculo = emprestimoDao.countEmprestimosAtivosPorVinculoUsuario(discente.getPessoa().getId(),  VinculoUsuarioBiblioteca.ALUNO_GRADUACAO, discente.getId());
					
				}else{
					if( niveisMedio.contains(discente.getNivel())){
						qtdEmprestimosAtivoVinculo = emprestimoDao.countEmprestimosAtivosPorVinculoUsuario(discente.getPessoa().getId(), VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO,  discente.getId());
					} else{
						if( niveisInfantil.contains(discente.getNivel())){
							qtdEmprestimosAtivoVinculo = emprestimoDao.countEmprestimosAtivosPorVinculoUsuario(discente.getPessoa().getId(), VinculoUsuarioBiblioteca.ALUNO_INFANTIL,  discente.getId());
						} else{
							qtdEmprestimosAtivoVinculo = 0l;
						}
					}
				
				}
			}
			
			/* **************************************************************************************  
			 * 
			 *  Se possuir empr�timos ativos que o usu�rio fez com o discente que ele est� querendo concluir/cancelar
			 *  
			 * **************************************************************************************/
			
			if(	qtdEmprestimosAtivoVinculo.compareTo(new Long(0)) > 0){ // ou seja, qtd > 1
				
				lista.addErro("N�o � poss�vel alterar o status o discente "+
						discente.getMatriculaNome()+
						" pois ele possui empr�stimos ativos nas bibliotecas do sistema " +
						" realizados com este v�nculo, ser� preciso primeiro devolver os materiais emprestados.");
			}
			
			
			return lista;
			
		} finally {
			if (usuarioBibliotecaDao != null)  usuarioBibliotecaDao.close();
			if (emprestimoDao != null) emprestimoDao.close();
		}
	}
	
	
	/**
	 * Este m�todo verifica se o discente possui alguma pend�ncia na biblioteca
	 */
	public static boolean temPendencia( Discente discente ) throws DAOException{
		
		
		ListaMensagens lista = verificaEmprestimoPendenteDiscente(discente);
		
		if(lista.getErrorMessages().size() > 0)
			return true;
		else
			return false;
	}
	
}
