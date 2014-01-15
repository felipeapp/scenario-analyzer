package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.UFRNUtils.getMensagem;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Faz as valida��es referentes aos dados de discentes de stricto sensu.
 * @author M�rio Rizzi
 *
 */
public class DiscenteStrictoValidator {
	
	/**
	 * Valida a submiss�o de todos dados do discente de stricto-sensu
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarDadosGerais(DiscenteStricto discente, boolean discenteAntigo, Usuario usuario, Movimento mov,ListaMensagens lista)
		throws DAOException, NegocioException {

		validarCamposComuns(discente, discenteAntigo, usuario, lista);
		validarAlteracaoTipoDiscente(discente, usuario, lista);
		validarLimiteBolsaDiscenteRegular(discente, lista);
		validarOrientacao(discente, lista);
		validarLimiteVagasProcessoSeletivo(discente, lista);
		
		if( ! (mov.getCodMovimento().getId() == SigaaListaComando.ALTERAR_DISCENTE_STRICTO.getId())) {		
			validarExisteDiscenteAtivoPessoa(discente, lista);
		}
		
	}

	/**
	 * Valida se existe discente ativo associado a mesma pessoa.
	 * Aplicado somente se a pessoa j� existir na base de dados.
	 * @param discente
	 * @param lista
	 * @param dao
	 * @throws NegocioException
	 * @throws DAOException 
	 */
	public static void validarExisteDiscenteAtivoPessoa(DiscenteStricto discente,	ListaMensagens lista)
			throws NegocioException, DAOException {		
		if( !isEmpty(discente.getPessoa().getId()) ){
			DiscenteStrictoDao dao = DAOFactory.getInstance().getDAO( DiscenteStrictoDao.class );			
			try {				
				Collection<DiscenteStricto> discentes = dao.findAtivoByPessoa( discente.getPessoa().getId() );				
				if(!ValidatorUtil.isEmpty(discentes)){
					for(DiscenteStricto d : discentes) {
						// Ignora caso o status seja defendido ou em homologa��o (tarefa 14306)
						if( d != null && d.getId() != 0 && discente.getId() != d.getId() && 
								(d.getStatus() != StatusDiscente.DEFENDIDO && d.getStatus() != StatusDiscente.EM_HOMOLOGACAO ))
							lista.addErro("J� existe um discente "+d.getStatusString()+" associado a esta pessoa (mat. " + d.getMatricula() + ")");					
					}
				}				
			} 
			finally{
				dao.close();
			}			
		}		
	}
	
	/**
	 * Valida os dados da orienta��o acad�mica
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarOrientacao(DiscenteStricto discente, ListaMensagens lista) throws DAOException, NegocioException {
	
		if ( discente.getCoOrientacao() != null && discente.getOrientacao() != null
				&& discente.getCoOrientacao().getIdDocente() > 0 && discente.getOrientacao().getIdDocente() > 0 
				&& discente.getCoOrientacao().getIdDocente() == discente.getOrientacao().getIdDocente()) {
			lista.addErro("Selecione um orientador diferente do co-orientador");
		} 
		
		/** se o usu�rio selecionar o orientador � obrigat�rio informar a data de in�cio da orienta��o */
		if( discente.getOrientacao() != null && discente.getOrientacao().getIdDocente() > 0 ){
			validateRequired( discente.getOrientacao().getInicio() , "In�cio da Orienta��o", lista);
		}
		/** se o usu�rio selecionar o co-orientador � obrigat�rio informar a data de in�cio da co-orienta��o */
		if( discente.getCoOrientacao() != null && discente.getCoOrientacao().getIdDocente() > 0 ){
			validateRequired( discente.getCoOrientacao().getInicio() , "In�cio da Co-Orienta��o", lista);
		}
		
	}
	
	/**
	 * Valida os dados da orienta��o acad�mica
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarLimiteBolsaDiscenteRegular(DiscenteStricto discente, ListaMensagens lista) throws DAOException, NegocioException {
	
		OrientacaoAcademicaDao orientacaoDAO = DAOFactory.getInstance().getDAO(OrientacaoAcademicaDao.class);
		EquipeProgramaDao equipeDAO = DAOFactory.getInstance().getDAO(EquipeProgramaDao.class);
		
		Integer totalOrientacoes = null;
		EquipePrograma orientador = null;
		
		/** se o orientador, selecionado na opera��o de cadastro de discente, j� ultrapassou o limite de bolsas. */
		if( !isEmpty( discente.getOrientacao() )  
				&& ( !isEmpty( discente.getOrientacao().getServidor() )	
						|| !isEmpty( discente.getOrientacao().getDocenteExterno() ) ) ){
		
			try{
				
				if ( discente.getOrientacao().isExterno() ) {
					totalOrientacoes = orientacaoDAO.findTotalOrientacoesNivelTipoServidor(
							null, discente.getOrientacao().getDocenteExterno(), discente.getDiscente().getNivel(), discente.getTipo());
					orientador = equipeDAO.findByProgramaMembro(discente.getCurso().getUnidade().getId(),
							null, discente.getOrientacao().getDocenteExterno().getId());
				} else {
					totalOrientacoes = orientacaoDAO.findTotalOrientacoesNivelTipoServidor(
							discente.getOrientacao().getServidor(), null, discente.getDiscente().getNivel(), discente.getTipo());
					orientador = equipeDAO.findByProgramaMembro(discente.getCurso().getUnidade().getId(),
							discente.getOrientacao().getServidor().getId(), null);
				}
				
			}finally{
				orientacaoDAO.close();
				equipeDAO.close();
			}
			
			if (discente.getDiscente().getNivel() == NivelEnsino.MESTRADO) {
				if (discente.getTipo().equals(Discente.REGULAR)){ 
					if (orientador != null && orientador.getMaxOrientandoRegularMestrado() != null 
							&& totalOrientacoes > orientador.getMaxOrientandoRegularMestrado()) {
						getMensagem(MensagensPortalCoordenadorStricto.LIMITE_BOLSISTA, discente.getTipoString());
					}else if(orientador != null && orientador.getMaxOrientandoEspecialMestrado() != null 
							&& totalOrientacoes > orientador.getMaxOrientandoEspecialMestrado()) {
						getMensagem(MensagensPortalCoordenadorStricto.LIMITE_BOLSISTA, discente.getTipoString());
					}
				}
			}else{
				if (discente.getTipo().equals(Discente.REGULAR)) {
					if (orientador != null && orientador.getMaxOrientandoRegularDoutorado() != null &&
							totalOrientacoes > orientador.getMaxOrientandoRegularDoutorado() ) {
						getMensagem(MensagensPortalCoordenadorStricto.LIMITE_BOLSISTA, discente.getTipoString());
					}else if (orientador != null && orientador.getMaxOrientandoEspecialDoutorado() != null 
							&& totalOrientacoes > orientador.getMaxOrientandoEspecialDoutorado()) {
						getMensagem(MensagensPortalCoordenadorStricto.LIMITE_BOLSISTA, discente.getTipoString());
					}
				}
			}
			
		}	
		
	}
	
	/** 
	 * Valida se a quantidade j� cadastrada de discente � igual ou superior a quandidade vagas disponibilizadas,
	 * caso a forma de ingresso seja atrav�s de processo seletivo, opera��o seja de cadastrar discente,
	 *  e o par�metro {@link ParametrosStrictoSensu#CADASTRAR_DISCENTE_POR_NUMERO_VAGAS}
	 * for verdadeiro.
	 * @throws DAOException 
	 */
	public static void validarLimiteVagasProcessoSeletivo(DiscenteStricto discente, ListaMensagens lista) throws DAOException{
		
		DiscenteStrictoDao dao = DAOFactory.getInstance().getDAO(DiscenteStrictoDao.class);
		
		if( discente.getId() == 0 && discente.getTipo().equals(Discente.REGULAR)	
				&& !isEmpty( discente.getFormaIngresso() )
				&& discente.getFormaIngresso().equals( FormaIngresso.PROCESSO_SELETIVO ) 
				&&	!isEmpty( discente.getProcessoSeletivo() )
				&& ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CADASTRAR_DISCENTE_POR_NUMERO_VAGAS) ){
				
			try{
				
				long qtdDiscenteRegulares = dao.findQuantidateDiscentesByProcessoSeletivo(discente.getProcessoSeletivo().getId(), discente.getTipo());
				
				if( qtdDiscenteRegulares >= discente.getProcessoSeletivo().getVaga() ){
					lista.addMensagem("N�o � poss�vel cadastrar o discente como Regular, " +
							"pois ultrapassa o n�mero de vagas disponibilizadas para o processo seletivo.");
				}
				
			}finally{
				dao.close();
			}
			
		}
		
	}
	
	/**
	 * Valida se o discente � oriundo de outra institu��o deve informar a institui��o, caso contr�rio
	 * o discente deve ser do tipo {@link Discente#REGULAR} ou {@link Discente#ESPECIAL}
	 * 
	 * @param discente
	 * @param alunoOutraInstituicao
	 * @param lista
	 */
	public static void validarAlunoOutraInstituicao(DiscenteStricto discente, boolean alunoOutraInstituicao,
			ListaMensagens lista){

		if(alunoOutraInstituicao){
			validateRequired( discente.getInstituicaoEnsinoRede().getId(), "Outra Institui��o de Ensino", lista);
		}else if( discente.getTipo().equals(Discente.EM_ASSOCIACAO) ){
			lista.addErro("Tipo: Favor informar o tipo do aluno, como REGULAR ou ESPECIAL.");
		}
		
	}
	
	/**
	 * valida os campos obrigat�rios.
	 * 
	 * @param discente
	 * @param discenteAntigo
	 * @param usuario
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validarCamposComuns(DiscenteStricto discente, boolean discenteAntigo, Usuario usuario, ListaMensagens lista) throws DAOException{
		
		validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequired(discente.getPeriodoIngresso(), "Per�odo de Ingresso", lista);
		validateRequired(discente.getMesEntrada(), "M�s de Entrada", lista);
		validateRequired(discente.getOrigem(), "Origem do discente", lista);
		
		if( !(discente.getTipo() == Discente.ESPECIAL) )
			validateRequired(discente.getCurriculo(), "Curr�culo", lista);
		
		if( isEmpty( discente.getTipo() ) ){
			lista.addErro( "Tipo: Campo obrigat�rio n�o informado." );
		}
		validateRequired(discente.getFormaIngresso(), "Forma de Ingresso", lista);
		
		boolean processoObrigatorio = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.OBRIGATORIO_INFORMAR_PROCESSO_SELETIVO_CADASTRO_DISCENTE);
		
		if (!usuario.isUserInRole(SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO) && discente.isRegular() && discente.getId() == 0 && processoObrigatorio)
			ValidatorUtil.validateRequired(discente.getProcessoSeletivo(), "Processo Seletivo", lista);

		validateRequired(discente.getGestoraAcademica(), "Programa", lista);
		
		// discente regular
		if (discente.getTipo() == 1)
			validateRequired(discente.getCurso(), "Curso", lista);
		else 
			validateRequired(discente.getCurso(), "N�vel", lista);
		
		validateRequired(discente.getArea(),"�rea de Concentra��o", lista);
		
		//Somente executa a valida��o se o n�vel do discente ou do curso estiver definido
		if( !isEmpty(discente.getCurso()) || NivelEnsino.getNiveisStricto().contains(discente.getNivel()) ){
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
			// inteiros maiores ou igual a zero
			// (ano [no m�nimo ano anterior], per�odo)
			validateMinValue(discente.getPeriodoIngresso(), 1, "Per�odo de Ingresso", lista);
			validateMaxValue(discente.getPeriodoIngresso(), param.getQuantidadePeriodosRegulares(), "Per�odo de Ingresso", lista);
		}	

		if (discenteAntigo) {
			validateRequired(discente.getMatricula(), "Matr�cula", lista);
			validateRequired(discente.getStatus(), "Status", lista);
			validateMaxValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual(), "Ano de Ingresso", lista);
		}else if( discente.getId() == 0 ){
			validateMinValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual() - 1, "Ano de Ingresso", lista);
		}
		
	}
	
	/**
	 * valida para os casos dos coordenadores dos programas se o tipo do discente foi alterado.
	 * @throws DAOException 
	 */
	public static void validarAlteracaoTipoDiscente(DiscenteStricto discente, Usuario usuario, ListaMensagens lista)
		throws DAOException {
	
		DiscenteStrictoDao dao = DAOFactory.getInstance().getDAO(DiscenteStrictoDao.class);
		
		if( discente.getId() > 0 && !usuario.isUserInRole(SigaaPapeis.PPG)){
			DiscenteStricto discOriginal = dao.findByPrimaryKey( discente.getId() , DiscenteStricto.class);
			if( !discente.getTipo().equals( discOriginal.getTipo() ) ){
				UFRNUtils.getMensagem(MensagensPortalCoordenadorStricto.NAO_E_POSSIVEL_ALTERAR_TIPO_DO_DISCENTE);
			}
		}
		
	}
	
}
