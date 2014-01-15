package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dao.ValidacaoInscricaoSelecaoDao;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Validator para operação de inscrição em um processo seletivo.
 * @author Mário Rizzi
 *
 */
public class InscricaoSelecaoValidator {

	public static final int INCRICAO_SUBMETIDA = 1;
	public static final int DISCENTE_ATIVO = 2;
	public static final int DISCENTE_BLOQUEADO = 3;
	
	/**
	 * Efetua as validações de período de inscrição, permissão de acesso e
	 * se existem vagas ao acessar o formulário de inscrição de um processo seletivo.
	 * @param inscricao
	 * @param lista
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarGeral(InscricaoSelecao inscricao, ListaMensagens lista) 
			throws DAOException, NegocioException{
		validarPossuiAcesso(inscricao, lista);
		validarExisteVagas(inscricao.getProcessoSeletivo(), lista);
		validarDentroPeriodo(inscricao.getProcessoSeletivo(), lista);
	}
	
	/**
	 * Valida se existe vagas disponíveis para o candidato
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarPossuiAcesso(InscricaoSelecao inscricao, ListaMensagens lista)
			throws DAOException, NegocioException {
		//Restringe a inscrição a uma determinada validação
		if( !isEmpty( inscricao.getProcessoSeletivo().getEditalProcessoSeletivo().getRestricaoInscrito() ) ){
			RestricaoInscricaoValidator inscricaoValidator = 
					ReflectionUtils.newInstance( inscricao.getProcessoSeletivo().getEditalProcessoSeletivo().
							getRestricaoInscrito().getClasse() );
			inscricaoValidator.validate(inscricao, lista);
		}
		
	}
	
	/**
	 * Valida se existe vagas disponíveis para o candidato
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarExisteVagas(ProcessoSeletivo processo, ListaMensagens lista)
		throws DAOException, NegocioException {
		
		ValidacaoInscricaoSelecaoDao validacaoDao = DAOFactory.getInstance().getDAO(ValidacaoInscricaoSelecaoDao.class);
		try {
			if( processo.getEditalProcessoSeletivo().isVerificaExisteVaga() 
					&& !validacaoDao.existeVaga( processo )  ){
				lista.addErro("O curso do processo seletivo selecionado não possui mais vagas.");
			}
		}finally{
			validacaoDao.close();
		}
		
	}
	

	/**
	 * Valida se a inscrição esta dentro do prazo de inscrição
	 * @param processo
	 * @param erros
	 */
	public static void validarDentroPeriodo(ProcessoSeletivo processo,
			ListaMensagens erros) {
	
		if( !processo.isInscricoesAbertas() || processo.isInscricoesFinalizadas() ){
			erros.addErro("A inscrição somente é permitida dentro do período de inscrição definido no edital do processo seletivo.");
		}
		
	}
	
	/**
	 * Valida se o discente atende o requisito de não ser bolsista reprovado ou trancado em menos de 1 ano.
	 * @param processo
	 * @param erros
	 * @throws DAOException 
	 */
	public static void validarInscricaoProcessoSeletivo(InscricaoSelecao inscricao, ListaMensagens erros) throws DAOException {
		if ( inscricao.getProcessoSeletivo().getCurso().isFormacaoComplementar() ) {
			InscricaoSelecaoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoDao.class);
			try {
				CalendarioAcademico caledar = CalendarioAcademicoHelper.getCalendario(inscricao.getProcessoSeletivo().getCurso()); 
				Map<Integer, Collection<Discente>> bloqueioEncontrado = dao.permiteInscricaoSelecao(
						inscricao.getPessoaInscricao().getCpf(), caledar.getAno() - 1, caledar.getPeriodo());
				
				if ( !bloqueioEncontrado.isEmpty() ) {
					Set<Integer> chaves = bloqueioEncontrado.keySet();  
					for (Integer chave : chaves) {
						for (Discente discente : bloqueioEncontrado.get(chave)) {
							erros.addErro("Não foi possível realizar a inscrição pois você possui " 
									+ getDescricao(chave).toUpperCase() + " para no curso " + discente.getCurso().getNome().toUpperCase() + ".");
						}
					}
				}
				
			} finally {
				dao.close();
			}
		}
	}

	public static String getDescricao(int tipoValidacao) {
		switch (tipoValidacao) {
			case INCRICAO_SUBMETIDA:
				return "Inscrição Submetida";
			case DISCENTE_ATIVO:
				return "Discente ativo";
			case DISCENTE_BLOQUEADO:
				return "Trancamento Registrado";
			default:
				return "DESCONHECIDO";
		}
	}
	
}