/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Classe que os métodos padrões para verifica a situação de um usuário na bilioteca.</p>
 *
 * 
 * @author jadson
 *
 */
public class VerificaSituacaoUsuarioBibliotecaUtil {

	
	/**
	 *  Verifica se o vínculo que foi atribuído ao usuário no cadastro na biblioteca ainda continua ativo.
	 *
	 * @param usuarioBiblioteca
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void verificaVinculoUtilizadoAtivo(UsuarioBiblioteca usuarioBiblioteca) throws NegocioException, DAOException{
	
		if(usuarioBiblioteca.getVinculo() == null || usuarioBiblioteca.getIdentificacaoVinculo() == null){
			throw new NegocioException("O vínculo atualmente utilizado expirou, realize um recadastro para obter um novo vínculo para poder utilizar a biblioteca.");
		}
		
		if(! usuarioBiblioteca.getVinculo().isVinculoBiblioteca()){
			boolean vinculoAtivo = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().isVinculoAtivo(usuarioBiblioteca.getPessoa().getId(), usuarioBiblioteca.getVinculo(), usuarioBiblioteca.getIdentificacaoVinculo());
		
			if(! vinculoAtivo)
				throw new NegocioException("O vínculo atualmente utilizado expirou, realize um recadastro para obter um novo vínculo para poder utilizar a biblioteca.");
			
		}else{
			// Vínculos de bibliotecas não expiram porque elas não podem trocar de vínculo como ocorre com os usuário normais no sistema
		}
	}
	
	
	/**
	 * <p>Verifica se a pessoa ou a biblioteca está bloqueada no sistema.</p>
	 * 
	 * <p>O bloqueio é independente se o usuário possui uma conta ativa atualmente na biblioteca ou não.</p>
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
				throw new NegocioException("O usuário não pode mais realizar o empréstimo pois está bloqueado. Motivo: "+motivo);
		
		
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	/**
	 * Retorna o motivo do bloqueio, caso o usuário esteja
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
	 * Interface para o método: @sse {@link VerificaSituacaoUsuarioBibliotecaUtil#verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa, boolean)}
	 * @param pessoa
	 * @throws NegocioException
	 */
	public static void verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa pessoa)throws NegocioException{
		verificaDadosPessoaCorretosUtilizarBiblioteca(pessoa, true);
	}
	
	
	/**  
	 * <p>VERIFICAÇÃO SE A PESSOA TEM CPF, OS USUÁRIO BIBILIOTECA DEVEM ESTÁ ASSOCIDADOS
	 * A PESSOAS NO SISTEMA QUE POSSUAM CPF, PARA EVITAR QUE USUÁRIOS QUE POSSUEM PESSOAS DUPLICADAS,
	 * POSSSAM CADASTRAR VÁRIOS USUÁRIOS BIBLIOTECAS, JÁ QUE O USUÁRIO BIBLIOTECA É POR PESSOA.</p>
	 * 
	 * @param pessoa
	 */
	public static void verificaDadosPessoaCorretosUtilizarBiblioteca(Pessoa pessoa, boolean somenteAtivos)throws NegocioException{
		
		if(pessoa.getTipo() != PessoaGeral.PESSOA_FISICA)
			throw new NegocioException("Usuário não pode utilizar a biblioteca porque seus dados pessoais estão incompletos. Usuário está cadastrado como pessoa jurídica no sistema.");
		
		if(pessoa.getCpf_cnpj() != null && pessoa.getCpf_cnpj() > 0 && pessoa.isValido() ){
			// OK tem um CPF válido, pode utilizar o sistema
		}else{
			
			// Pessao não tem CPF válido, verifica se é pessoa entrageira //
			if(pessoa.isInternacional()){
				if( StringUtils.isEmpty(pessoa.getPassaporte() ) )
					throw new NegocioException("Usuário não pode utilizar a biblioteca porque seus dados pessoais estão incompletos. Usuário é extrangeiro e não possui passaporte válido.");
			}else{
				
				if( ! verificaAlunoNivelInfantil(pessoa, somenteAtivos) ){// adicionado em 02/04/2012  # 85358 Cria o Vínculo Infantil
					
					// NÃO tem CPF válido, NEM é extrageira, NEM é aluno do ensino infantil
					throw new NegocioException("Usuário não pode utilizar a biblioteca porque seus dados pessoais estão incompletos. Usuário não possui CPF válido nem é aluno ativo de nível infantil.");
				}
			}
		}	
	}

	
	

	/** 
	 * <p>Consulta para saber se o usuário tem um vínculo de discente de nível infantil </p>
	 * 
	 * <p>Nesse caso ele vai poder realizar empréstimos sem CPF</p>
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
			throw new NegocioException("Usuário não pode utilizar a biblioteca porque seus dados pessoais estão incompletos. Usuário não possui CPF válido nem é aluno ativo de nível infantil.");
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	
	
	/** 
	 *  <p>Método que verifica se a pessoa passada possui alguma penalidade na biblioteca </p> 
	 *  
	 *  <p><strong>Importante: Esse método já busca para todas as contas que o usuário teve na biblioteca </strong> </p> 
	 *  
	 *  <p>As penalidades na biblioteca são: Ou a aluno <strong>está suspenso</strong> ou <strong>possui multa não paga</strong> .</p>
	 * 
	 * @param idUsuarioBiblioteca
	 * @return retorna uma lista com as situações de punição do usuário .
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
	 * <p> Verifica se o usuário pussui punições da nas bibliotecas de acordo com as estratégia utilizadas.</p>
	 * 
	 * <p> Lança execeção caso o usuário possua punição.<p>
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
	 *     Verifica se o usuário possu algum emprétimos em aberto ou atrasado para o conta passada, utilizado 
	 *  geralmente para impedir a emissão de diplomas ou cancelamento do vínculo do usuário.
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
			
			// Se tiver pelo menos um ativo, não pode emitir o documento de quitação.
			if (emprestimosAtivos.size() > 0)
				if (quantidadeEmprestimosAtrasados > 0){
					SituacaoUsuarioBiblioteca situacaoAtrasado =  SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS;
					situacaoAtrasado.setDescricaoCompleta("Usuário possui "+emprestimosAtivos.size()+" empréstimo(s) aberto(s) e desses "+quantidadeEmprestimosAtrasados+" está(ão) atrasado(s).");
					lista.add(situacaoAtrasado);
				}
				else{
					SituacaoUsuarioBiblioteca situacaoAtivos =  SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS;
					situacaoAtivos.setDescricaoCompleta("Usuário possui "+emprestimosAtivos.size()+" empréstimo(s) aberto(s).");
					lista.add(situacaoAtivos);
				}
			return lista; 
		} finally {
			if (daoEmprestimo != null) daoEmprestimo.close();
		}
	}
	
	
	/**
	 * Verfifica se o usuário possui empréstimos atrasados
	 *
	 * @param emprestimosAtivos
	 * @throws NegocioException 
	 */
	public static void verificaUsuarioPossuiEmprestimosAtrazados(List<Emprestimo> emprestimosAtivos) throws NegocioException {
		for (Emprestimo emprestimoAtivo : emprestimosAtivos)
			if (emprestimoAtivo.isAtrasado())
				throw new NegocioException("O usuário não pode realizar o empréstimo pois possui empréstimos em atraso");
		
	}
	
	
	
	/** 
	 *  <p> Método que verifica se o usuário possui empréstimos em atraso por um período maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>Método para ser usado por sistema remotos, onde não é possível garantir que as pessas tenham o id iguais.</p>
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
	 *  <p> Método que verifica se o usuário possui empréstimos em atraso por um período maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>Método para ser chamado dentro do SIGAA, onde está bem definido o id da tabela pessoa..</p>
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
	 *  <p>Método interno que verifica se foi passado id da pessoa ou cpf e realiza as consultas de acordo com o que foi passado.</p>
	 * 
	 * @param idPessoa
	 */
	private static ListaMensagens verificaUsuarioPossuiInrregularidadeAdministrativa(Integer idPessoa, Long cpf_cnpj) throws DAOException{
		
		if(idPessoa == null && cpf_cnpj == null)
			throw new java.lang.IllegalArgumentException("É preciso passar o idPessoa ou CPF para consultar os empréstimos do usuário.");
		
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
					lista.addErro("Operação não pôde ser realizada, pois o usuário está com um atraso superior a "+qtdMaximoDiasAtraso+" dia(s) na biblioteca. Incorreu em irregularidade administrativa!"); // Mensagem genérica
			
			}finally{
				if(emprestimoDao != null) emprestimoDao.close();
			}
			
		}
		// else :  verificação não habilitada para o sistema
		
		return lista;
		
	}
	
	
	
	/** 
	 *  <p>Verifica se o discente possui empréstimos <strong>pendentes</strong> na biblioteca, feitos com o vínculo de discente passado 
	 *        <i>(política de empréstimo para aluno , aluno de pós  ou médio/técnico)</i>.</p>
	 * 
	 *  <p>Empréstimos pendentes são os emprétima ativos do usuário, atrasados ou não. Esse método não verifica se o usuário 
	 *  possui débitos a pagar com a biblioteca, como suspensões ou multas.</p>
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
			
			// por padrão não pode cancelar/concluir discente sem antes verifica a sua situação na biblioteca
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
			 *  Se possuir emprétimos ativos que o usuário fez com o discente que ele está querendo concluir/cancelar
			 *  
			 * **************************************************************************************/
			
			if(	qtdEmprestimosAtivoVinculo.compareTo(new Long(0)) > 0){ // ou seja, qtd > 1
				
				lista.addErro("Não é possível alterar o status o discente "+
						discente.getMatriculaNome()+
						" pois ele possui empréstimos ativos nas bibliotecas do sistema " +
						" realizados com este vínculo, será preciso primeiro devolver os materiais emprestados.");
			}
			
			
			return lista;
			
		} finally {
			if (usuarioBibliotecaDao != null)  usuarioBibliotecaDao.close();
			if (emprestimoDao != null) emprestimoDao.close();
		}
	}
	
	
	/**
	 * Este método verifica se o discente possui alguma pendência na biblioteca
	 */
	public static boolean temPendencia( Discente discente ) throws DAOException{
		
		
		ListaMensagens lista = verificaEmprestimoPendenteDiscente(discente);
		
		if(lista.getErrorMessages().size() > 0)
			return true;
		else
			return false;
	}
	
}
