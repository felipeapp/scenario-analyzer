/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.TrancamentoMatriculaUtil;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.mensagens.MensagensGerais;

/**
 * <p>
 * Classe com m�todos de valida��o das regras de neg�cio relativas ao trancamento de matr�cula 
 * em componentes curriculares (Gradua��o e P�s-Gradua��o)
 * </p>
 * <p>
 * Validar o n�mero m�ximo de trancamentos j� feitos para um componente curricular e se o tipo
 * do componente curricular � v�lido para trancamento, s�o verifica��es realizadas por esta classe.
 * </p>
 *
 * @author Ricardo Wendell
 * @author Victor Hugo
 */
public class TrancamentoMatriculaValidator {

	/**
	 * <h2>ATEN��O:</h2>
	 * <p>
	 * QUALQUER ALTERA��O NESTE M�TODO DE INCLUS�O DE VALIDA��O, DEVE SER MODIFICADO
	 * EM SolicitacaoTrancamentoDao, POIS L� FAZ PROJE��O!
	 * </p>
	 * <h3>Descri��o:</h3>
	 * <p>
	 * Valida se � poss�vel realizar o trancamento da matr�cula no componente curricular especificada.
	 * Existem v�rios motivos para que n�o seja poss�vel trancar uma matr�cula, como por exemplo, exceder o n�mero
	 * de trancamentos para um componente curricular ou se o tipo de componente � v�lido para este tipo de opera��o. 
	 * </p>
	 * 
	 * @param matricula
	 * @param erros
	 * @throws DAOException
	 */
	public static void validar(MatriculaComponente matricula, ListaMensagens erros) throws DAOException {

		// LEIA ACIMA SE FOR MUDAR ALGO!!
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(matricula.getDiscente());

		// Validar tipo do componente
		if  (!isTipoComponenteValido(matricula)) {
			erros.addErro("A disciplina " + matricula.getComponenteDescricaoResumida() + " n�o pode ser trancada. N�o � permitido o trancamento de matr�cula em m�dulos ou atividades acad�micas.");
			//addMensagemErro("A disciplina " + matricula.getComponenteDescricaoResumida() + " n�o pode ser trancada. N�o � permitido o trancamento de matr�cula em m�dulos ou atividades acad�micas.", erros);
		}

		// Verificar se a matr�cula � em uma turma de f�rias
		Integer quantidadePeriodosRegulares = parametros.getQuantidadePeriodosRegulares() != null ? parametros.getQuantidadePeriodosRegulares() : 2;
		if ( matricula.getTurma() != null && matricula.getTurma().getPeriodo() > quantidadePeriodosRegulares) {
			erros.addErro("N�o � permitido o trancamento de matr�cula em disciplina oferecida em per�odo letivo especial de f�rias.");
			//addMensagemErro("N�o � permitido o trancamento de matr�cula em disciplina oferecida em per�odo letivo especial de f�rias.", erros);
		}

		// LEIA ACIMA SE FOR MUDAR ALGO!!					

		if (parametros != null) {
			int maxTrancamentosMatricula = parametros.getMaxTrancamentosMatricula();
			// Validar se a disciplina j� ultrapassou o limite de trancamentos
			if (!isDentroLimiteTrancamentos(matricula, maxTrancamentosMatricula)) {
				erros.addErro("A disciplina " + matricula.getComponenteDescricaoResumida() + " n�o pode ser trancada. N�o � permitido o trancamento de matr�cula em uma mesma" +
						" disciplina por mais de " + maxTrancamentosMatricula + " vez(es)," +
						" em per�odos letivos consecutivos ou n�o.");
				/*addMensagemErro("A disciplina " + matricula.getComponenteDescricaoResumida() + " n�o pode ser trancada. N�o � permitido o trancamento de matr�cula em uma mesma" +
						" disciplina por mais de " + maxTrancamentosMatricula + " vezes," +
						" em per�odos letivos consecutivos ou n�o.", erros);*/
			}
		} else if (parametros == null && !matricula.getDiscente().isStricto()) {
			erros.addErro("N�o foram encontrados os par�metros necess�rios para a realiza��o desta opera��o." +
					" Por favor, contate os administradores do sistema.");
			/*addMensagemErro("N�o foram encontrados os par�metros necess�rios para a realiza��o desta opera��o." +
					" Por favor, contate os administradores do sistema.", erros);*/
		}
		
	}

	/**
	 * Valida se o tipo do componente curricular cuja matricula ser� trancada permite a opera��o.
	 * Discente de stricto pode trancar qualquer componentes de qualquer tipo.
	 * @param matricula
	 * @return
	 */
	public static boolean isTipoComponenteValido(MatriculaComponente matricula) {
		
		if (matricula.getDiscente().isStricto())
			return true;
		
		if (TipoComponenteCurricular.getNaoAtividades().contains(matricula.getComponente().getTipoComponente()))
			return true;
		
		if (matricula.getComponente().isAtividade() && matricula.getComponente().getFormaParticipacao().getId() == FormaParticipacaoAtividade.ESPECIAL_COLETIVA)
			return true;
		
		return false;
	}

	/**
	 * Valida se a disciplina selecionada n�o ultrapassou o limite de trancamentos estabelecido.
	 *
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public static boolean isDentroLimiteTrancamentos(MatriculaComponente matricula, int maxTrancamentos) throws DAOException {
		
		MatriculaComponenteDao matriculaDao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class, null, null);
		
		try{		
		Collection<MatriculaComponente> trancamentos = null;		
		trancamentos = matriculaDao.findByDiscenteEDisciplina(matricula.getDiscente(), matricula.getComponente(), SituacaoMatricula.TRANCADO);
		if (trancamentos!= null && trancamentos.size() >= maxTrancamentos) {
			return false;
		}		
		return true;
		
		} finally {
			matriculaDao.close();
		}
	}

	/**
	 * Verifica se todos os co-requisitos das disciplinas selecionadas para serem trancadas tamb�m est�o sendo trancados
	 * @param matriculas matriculas a serem trancadas
	 * @return true se todos os co-requisitos tamb�m est�o sendo trancados, false caso contrario
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static List<MatriculaComponente> verificarCorequisitos(DiscenteAdapter discente, Collection<MatriculaComponente> matriculasParaTrancar) throws DAOException{

		MatriculaComponenteDao matDao = DAOFactory.getInstance().getDAO( MatriculaComponenteDao.class );
		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO( DiscenteDao.class );
		SolicitacaoTrancamentoMatriculaDao solicitacaoDao = DAOFactory.getInstance().getDAO( SolicitacaoTrancamentoMatriculaDao.class );
		/** MAPA DAS DISCIPLINAS QUE ESTAO SENDO TRANCADAS E QUE POSSUEM COREQUISITOS
		 * <COMPONENTE TRANCADO, COLECAO DE COREQUISITOS QUE SERAO TRANCADOS TAMBEM>
		 */
		List<MatriculaComponente> listaCorequisitos = new ArrayList<MatriculaComponente>();
		try{
			Collection<MatriculaComponente> matriculasAluno = matDao.findByDiscente(discente, SituacaoMatricula.MATRICULADO);
			//Collection<ComponenteCurricular> componentesPagos = discenteDao.findComponentesCurriculares(discente, SituacaoMatricula.getSituacoesPagas(), null);
	
	
			/** cole��o de ids das disciplinas que est�o marcadas para trancar */
			Collection<Integer> idsParaTrancar = new ArrayList<Integer>();
			Map<Integer, String> idsEquivalentesParaTrancar = new HashMap<Integer, String>();
			for( MatriculaComponente mc : matriculasParaTrancar ){
				idsParaTrancar.add( mc.getComponente().getId() );
				idsEquivalentesParaTrancar.put( mc.getComponente().getId(), mc.getComponente().getDetalhes().getEquivalencia() );
			}
	
			/**
			 * Carregando as matr�culas que possuem solicita��o de trancamento pendentes.
			 * N�o deve verificar a express�o de co-requisitos destas pois elas tamb�m ser�o trancadas!
			 */
			MatriculaComponente matUm = matriculasParaTrancar.iterator().next();
			Collection<SolicitacaoTrancamentoMatricula>solicitacoesRealizadas = solicitacaoDao.findByDiscenteAnoPeriodo(discente.getId(), matUm.getAno(), matUm.getPeriodo(), SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
			Collection<MatriculaComponente> matriculasComSolicitacoesAbertas = new ArrayList<MatriculaComponente>();
			for( SolicitacaoTrancamentoMatricula sol : solicitacoesRealizadas ){
				matriculasComSolicitacoesAbertas.add( sol.getMatriculaComponente() );
			}
	
			for( MatriculaComponente mat : matriculasAluno ){ // matriculadas no semestre
	
				if ( matriculasParaTrancar.contains(mat) ) //n�o testa as matriculas que ser�o trancadas
					continue;
	
				if( matriculasComSolicitacoesAbertas.contains(mat) )/** n�o testa as matr�culas que possuem solicitac�o de trancamento pendentes */
					continue;
	
				ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(mat.getComponenteCoRequisito());
	
				if( arvore != null ){
					if( arvore.eval(idsParaTrancar, idsEquivalentesParaTrancar) ){
						listaCorequisitos.add(mat);
					}
				}
			}
	
		}finally {
			matDao.close();
			discenteDao.close();
			solicitacaoDao.close();	
			
		}
		
		return listaCorequisitos;
	}

	/**
	 * Verifica se o discente selecionou todas as sub-unidades de cada bloco para trancar.
	 * se ele trancar uma subunidade do bloco necessariamente tem que trancar todas
	 * @param discente
	 * @param matriculasParaTrancar
	 * @return
	 * @throws DAOException
	 */
	public static Collection<MatriculaComponente> verificarSubunidades(Collection<MatriculaComponente> matriculasParaTrancar) throws DAOException{

		MatriculaComponenteDao dao = DAOFactory.getInstance().getDAO( MatriculaComponenteDao.class );
		
		try {
			DiscenteAdapter discente = matriculasParaTrancar.iterator().next().getDiscente();
			Collection<MatriculaComponente> matriculasAluno = dao.findByDiscente(discente, SituacaoMatricula.MATRICULADO);
			
			/** lista de subunidades que tamb�m devem ser trancadas e que o usu�rio n�o selecionou para trancar */
			Collection<MatriculaComponente> subunidadesTrancar = new HashSet<MatriculaComponente>();
			
			/** blocos que o discente ir� trancar */
			Collection<ComponenteCurricular> blocos = new HashSet<ComponenteCurricular>();
			
			/** componentes que o discente N�O SELECIONOU PARA TRANCAR */
			//Collection<MatriculaComponente> matriculasNaoSelecionadas = new HashSet<MatriculaComponente>();
			Collection<ComponenteCurricular> componentesNaoSelecionados = new HashSet<ComponenteCurricular>();
			
			for( MatriculaComponente mc : matriculasAluno ){
				if( !matriculasParaTrancar.contains(mc) ){
					//matriculasNaoSelecionadas.add(mc);
					componentesNaoSelecionados.add( mc.getComponente() );
				}
			}
			
			for( MatriculaComponente mc : matriculasParaTrancar ){
				if( mc.getComponente().isSubUnidade() ){
					ComponenteCurricular bloco = mc.getComponente().getBlocoSubUnidade();
					if( !blocos.contains( bloco ) )
						blocos.add( dao.findByPrimaryKey(bloco.getId(), ComponenteCurricular.class) );
				}
			}
			
			for( ComponenteCurricular bloco : blocos ){
				for( ComponenteCurricular subunidade : bloco.getSubUnidades() ){
					
					if( componentesNaoSelecionados.contains( subunidade ) ){
						
						for( MatriculaComponente mat : matriculasAluno  ){
							if( mat.getComponente().equals( subunidade ) )
								subunidadesTrancar.add(mat);
						}
						
					}
					
				}
			}
			
			return subunidadesTrancar;
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica se j� foi solicitado trancamento para alguma das disciplinas
	 * @param discente
	 * @param matriculasParaTrancar
	 * @param ano
	 * @param periodo
	 * @return Retorna uma String com o nome das disciplinas para o qual j� foram solicitadas o trancamento ou trancadas!
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validarSolicitacao(DiscenteAdapter discente, Collection<MatriculaComponente> matriculasParaTrancar, int ano, int periodo, ListaMensagens listaErros) throws DAOException {

		SolicitacaoTrancamentoMatriculaDao dao = DAOFactory.getInstance().getDAO( SolicitacaoTrancamentoMatriculaDao.class );
		StringBuilder nomesDisciplinas = new StringBuilder();
		boolean jaSolicitou = false;
		try{
			Collection<SolicitacaoTrancamentoMatricula> solicitacoesRealizadas = dao.findByDiscenteAnoPeriodo(discente.getId(), ano, periodo, SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.TRANCADO, SolicitacaoTrancamentoMatricula.VISTO);
			for( SolicitacaoTrancamentoMatricula solicitacao : solicitacoesRealizadas ){
				if( matriculasParaTrancar.contains(solicitacao.getMatriculaComponente()) ){
					jaSolicitou = true;
				}
				nomesDisciplinas.append(solicitacao.getMatriculaComponente().getComponenteDescricaoResumida() + ";<br>");
			}
		}finally {
			dao.close();
		}
		
		validarTrancamentoAnteriores(discente, matriculasParaTrancar, ano, periodo, listaErros);
			
		if( jaSolicitou )
			listaErros.addErro("N�o � poss�vel solicitar o trancamento de uma matr�cula duas vezes. Voc� j� solicitou o trancamento da(s) seguinte(s) matr�cula(s): <br>" + nomesDisciplinas.toString());
			//ValidatorUtil.addMensagemErro("N�o � poss�vel solicitar o trancamento de uma matr�cula duas vezes. Voc� j� solicitou o trancamento da(s) seguinte(s) matr�cula(s): <br>" + nomesDisciplinas.toString(), listaErros.getMensagens());
		
		for( MatriculaComponente mc : matriculasParaTrancar ){
			if( mc.getAno() != ano || mc.getPeriodo() != periodo )
				listaErros.addErro("N�o � poss�vel solicitar o trancamento da disciplina " + mc.getComponenteCodigoNome() + " pois esta turma � do per�odo " + mc.getAnoPeriodo() + " e o ano.per�odo corrente � " + ano +"." + periodo + ".");
		}
	}

	/**
	 * <p>
	 * Verifica se o discente j� havia trancado a mesma disciplina no mesmo semestre.
	 * </p>
	 * <p>
	 * Uma forma disto ocorrer � quando a aluno tranca o componente curricular, 
	 * o DAE rematricula o aluno e depois o aluno deseja trancar novamente o mesmo componente.<br/>
	 * 
	 * Este segundo trancamento n�o pode ser realizado.
	 * </p> 
	 * 
	 * @param discente
	 * @param matriculasParaTrancar
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public static void validarTrancamentoAnteriores(DiscenteAdapter discente, Collection<MatriculaComponente> matriculasParaTrancar, int ano, int periodo, ListaMensagens erros) throws DAOException {
		
		MatriculaComponenteDao matriculaDao = new MatriculaComponenteDao();
		try{
			Collection<MatriculaComponente> trancamentos = matriculaDao.findByDiscente(discente, ano, periodo, SituacaoMatricula.TRANCADO);
			
			for (MatriculaComponente desejandoTrancar : matriculasParaTrancar) {
				for (MatriculaComponente matriculaTrancada : trancamentos) {
					if (  desejandoTrancar.getTurma() != null && desejandoTrancar.getTurma().getDisciplina().equals(matriculaTrancada.getTurma().getDisciplina()) ) {
						erros.addMensagem(MensagensGerais.TRANCAMENTO_MATRICULA_MESMA_TURMA_NO_MESMO_SEMESTRE, matriculaTrancada.getComponenteNome());
					}
				}
			}
		}finally{
			matriculaDao.close();
		}
	}
	
	/**
	 * Verifica se uma solicita��o de trancamento pode ser cancelada
	 * caso a disciplina tenha co-requisito e este tamb�m esta solicitado para trancamento, a solicita��o do co-requisito tamb�m ser� cancelada.
	 *
	 * @param solicitacao
	 * @param erros
	 * @return List<SolicitacaoTrancamentoMatricula> A lista de solicita��o de trancamento dos co-requisitos que tamb�m ser�o cancelados
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public static List<SolicitacaoTrancamentoMatricula> validaCancelamentoSolicitacao( SolicitacaoTrancamentoMatricula solicitacao,
			 CalendarioAcademico cal,  ListaMensagens erros ) throws DAOException{

		if( solicitacao.getSituacao() == SolicitacaoTrancamentoMatricula.TRANCADO )
			erros.addErro("N�o � poss�vel cancelar uma solicita��o de trancamento de uma disciplina que j� foi trancada.");

		if( solicitacao.getSituacao() == SolicitacaoTrancamentoMatricula.CANCELADO )
			erros.addErro("N�o � poss�vel cancelar uma solicita��o de trancamento de uma disciplina que j� foi cancelada.");

		Calendar data = Calendar.getInstance();
		data.setTime( DateUtils.truncate(solicitacao.getDataCadastro(), Calendar.DAY_OF_MONTH) );
		data.add(Calendar.DAY_OF_MONTH, 7);

		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		if( hoje.compareTo(data.getTime()) > 0 || !solicitacao.isPodeCancelar() || (!cal.isStricto() && !cal.isPeriodoTrancamentoTurmas())){
			erros.addErro("S� � poss�vel cancelar uma solicita��o de trancamento at� no m�ximo sete (7) dias depois de t�-la solicitado ou at� o prazo m�ximo de trancamento definido no calend�rio acad�mico.");
			return null;
		}


		/**
		 * VERIFICANDO COREQUISITOS TRANCADOS, PARA CANCELAR A SOLICITACAO DO PRE-REQUISITO TAMBEM
		 */
		List<SolicitacaoTrancamentoMatricula> corequisitosParaCancelar = new ArrayList<SolicitacaoTrancamentoMatricula>();

		DiscenteAdapter discente = solicitacao.getMatriculaComponente().getDiscente();
		SolicitacaoTrancamentoMatriculaDao dao = DAOFactory.getInstance().getDAO( SolicitacaoTrancamentoMatriculaDao.class );
		try{
			Collection<SolicitacaoTrancamentoMatricula> solicitacoesDiscente = dao.findByDiscenteAnoPeriodo(discente.getId(), solicitacao.getMatriculaComponente().getAno(), solicitacao.getMatriculaComponente().getPeriodo(), SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.TRANCADO, SolicitacaoTrancamentoMatricula.VISTO);
	
			Collection<Integer> ids = new ArrayList<Integer>(); //cole��o dos ids das disciplinas que o aluno esta matriculado com exce��o das que est�o sendo solicitadas para trancar
			for( SolicitacaoTrancamentoMatricula sol : solicitacoesDiscente ){
				ids.add(sol.getMatriculaComponente().getComponente().getId());
			}
	
			ids.remove( solicitacao.getMatriculaComponente().getComponente().getId());
	
			ArvoreExpressao arvore = ArvoreExpressao.fromExpressao( solicitacao.getMatriculaComponente().getComponenteCoRequisito() );
	
			if( arvore != null ){
				Collection<Integer> matches = null;
				if( arvore.eval(ids) ){
					matches = arvore.getMatches();
				}
	
				if( matches != null && !matches.isEmpty() ){
					for( Integer i : matches ){
						Collection<SolicitacaoTrancamentoMatricula> corequisitosSolicitados = dao.findByDiscenteComponenteSituacao(discente.getId(), i, SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
						if( corequisitosSolicitados != null && !corequisitosSolicitados.isEmpty() ){
							for( SolicitacaoTrancamentoMatricula stm : corequisitosSolicitados ){
								corequisitosParaCancelar.add( stm );
							}
						}
					}
				}
			}
		}finally{
			dao.close();
		}
		return corequisitosParaCancelar;
	}

	/**
	 * Valida o trancamento de matr�cula quando � turma de componente curricular de bloco.
	 * @param solicitacao
	 * @param erros
	 * @return
	 * @throws DAOException
	 */
	public static List<SolicitacaoTrancamentoMatricula> validaCancelamentoBloco( SolicitacaoTrancamentoMatricula solicitacao,
			ListaMensagens erros ) throws DAOException {

		/** este m�todo s� valida cancelamento de solicita��o de turma de subunidades, se n�o for n�o deve ser executado */
		if( !solicitacao.getMatriculaComponente().getComponente().isSubUnidade() )
			return null;

		SolicitacaoTrancamentoMatriculaDao dao = DAOFactory.getInstance().getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		ComponenteCurricular bloco = dao.refresh( solicitacao.getMatriculaComponente().getComponente().getBlocoSubUnidade() );
		List<SolicitacaoTrancamentoMatricula> solicitacoesSubunidades = (List<SolicitacaoTrancamentoMatricula>) dao.findPendetesByBlocoDiscente(bloco, solicitacao.getMatriculaComponente().getDiscente());

		for (Iterator<SolicitacaoTrancamentoMatricula> it = solicitacoesSubunidades.iterator(); it.hasNext();) {
			SolicitacaoTrancamentoMatricula s = it.next();
			if( s.equals(solicitacao) )
				it.remove();
		}

		StringBuffer msg = new StringBuffer("A disciplina que voc� est� cancelando � uma subunidade de um bloco, o trancamento nas outras subunidades deste bloco tamb�m ser�o canceladas: <br/>");
		for( SolicitacaoTrancamentoMatricula s : solicitacoesSubunidades ){
			msg.append( s.getMatriculaComponente().getComponenteDescricaoResumida() + ";<br/>" );
		}

		erros.addWarning( msg.toString());
		return solicitacoesSubunidades;
	}
	
	/**
	 * Quando m�dulo ou stricto, valida se a turma ainda pode ser trancada.
	 * 
	 * @param turma
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void validarDataLimite(Turma turma, CalendarioAcademico calendario) throws NegocioException, DAOException {
		
		if (turma.isGraduacao() && !turma.getDisciplina().isModulo()) {
			return ;
		}
		
		Date limite = TrancamentoMatriculaUtil.calcularPrazoLimiteTrancamentoTurma(turma, calendario);
		if(limite!=null){
			Date dataLimite = CalendarUtils.descartarHoras(limite);
			Date hoje = CalendarUtils.descartarHoras(new Date());			
			if (hoje.after(dataLimite)) {
				throw new NegocioException("N�o � permitido Trancar o Componente Curricular " + turma.getDisciplina().getCodigoNome() + ", pois a data limite era " + Formatador.getInstance().formatarDataDiaMesAno(dataLimite));
			}
		}
		
	}
	
}
