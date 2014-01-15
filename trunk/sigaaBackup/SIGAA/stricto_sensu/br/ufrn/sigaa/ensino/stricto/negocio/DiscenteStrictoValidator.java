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
 * Faz as validações referentes aos dados de discentes de stricto sensu.
 * @author Mário Rizzi
 *
 */
public class DiscenteStrictoValidator {
	
	/**
	 * Valida a submissão de todos dados do discente de stricto-sensu
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
	 * Aplicado somente se a pessoa já existir na base de dados.
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
						// Ignora caso o status seja defendido ou em homologação (tarefa 14306)
						if( d != null && d.getId() != 0 && discente.getId() != d.getId() && 
								(d.getStatus() != StatusDiscente.DEFENDIDO && d.getStatus() != StatusDiscente.EM_HOMOLOGACAO ))
							lista.addErro("Já existe um discente "+d.getStatusString()+" associado a esta pessoa (mat. " + d.getMatricula() + ")");					
					}
				}				
			} 
			finally{
				dao.close();
			}			
		}		
	}
	
	/**
	 * Valida os dados da orientação acadêmica
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
		
		/** se o usuário selecionar o orientador é obrigatório informar a data de início da orientação */
		if( discente.getOrientacao() != null && discente.getOrientacao().getIdDocente() > 0 ){
			validateRequired( discente.getOrientacao().getInicio() , "Início da Orientação", lista);
		}
		/** se o usuário selecionar o co-orientador é obrigatório informar a data de início da co-orientação */
		if( discente.getCoOrientacao() != null && discente.getCoOrientacao().getIdDocente() > 0 ){
			validateRequired( discente.getCoOrientacao().getInicio() , "Início da Co-Orientação", lista);
		}
		
	}
	
	/**
	 * Valida os dados da orientação acadêmica
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
		
		/** se o orientador, selecionado na operação de cadastro de discente, já ultrapassou o limite de bolsas. */
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
	 * Valida se a quantidade já cadastrada de discente é igual ou superior a quandidade vagas disponibilizadas,
	 * caso a forma de ingresso seja através de processo seletivo, operação seja de cadastrar discente,
	 *  e o parâmetro {@link ParametrosStrictoSensu#CADASTRAR_DISCENTE_POR_NUMERO_VAGAS}
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
					lista.addMensagem("Não é possível cadastrar o discente como Regular, " +
							"pois ultrapassa o número de vagas disponibilizadas para o processo seletivo.");
				}
				
			}finally{
				dao.close();
			}
			
		}
		
	}
	
	/**
	 * Valida se o discente é oriundo de outra institução deve informar a instituição, caso contrário
	 * o discente deve ser do tipo {@link Discente#REGULAR} ou {@link Discente#ESPECIAL}
	 * 
	 * @param discente
	 * @param alunoOutraInstituicao
	 * @param lista
	 */
	public static void validarAlunoOutraInstituicao(DiscenteStricto discente, boolean alunoOutraInstituicao,
			ListaMensagens lista){

		if(alunoOutraInstituicao){
			validateRequired( discente.getInstituicaoEnsinoRede().getId(), "Outra Instituição de Ensino", lista);
		}else if( discente.getTipo().equals(Discente.EM_ASSOCIACAO) ){
			lista.addErro("Tipo: Favor informar o tipo do aluno, como REGULAR ou ESPECIAL.");
		}
		
	}
	
	/**
	 * valida os campos obrigatórios.
	 * 
	 * @param discente
	 * @param discenteAntigo
	 * @param usuario
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validarCamposComuns(DiscenteStricto discente, boolean discenteAntigo, Usuario usuario, ListaMensagens lista) throws DAOException{
		
		validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		validateRequired(discente.getPeriodoIngresso(), "Período de Ingresso", lista);
		validateRequired(discente.getMesEntrada(), "Mês de Entrada", lista);
		validateRequired(discente.getOrigem(), "Origem do discente", lista);
		
		if( !(discente.getTipo() == Discente.ESPECIAL) )
			validateRequired(discente.getCurriculo(), "Currículo", lista);
		
		if( isEmpty( discente.getTipo() ) ){
			lista.addErro( "Tipo: Campo obrigatório não informado." );
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
			validateRequired(discente.getCurso(), "Nível", lista);
		
		validateRequired(discente.getArea(),"Área de Concentração", lista);
		
		//Somente executa a validação se o nível do discente ou do curso estiver definido
		if( !isEmpty(discente.getCurso()) || NivelEnsino.getNiveisStricto().contains(discente.getNivel()) ){
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
			// inteiros maiores ou igual a zero
			// (ano [no mínimo ano anterior], período)
			validateMinValue(discente.getPeriodoIngresso(), 1, "Período de Ingresso", lista);
			validateMaxValue(discente.getPeriodoIngresso(), param.getQuantidadePeriodosRegulares(), "Período de Ingresso", lista);
		}	

		if (discenteAntigo) {
			validateRequired(discente.getMatricula(), "Matrícula", lista);
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
