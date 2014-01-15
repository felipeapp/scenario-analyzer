/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/10/2008
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.monitoria.ComponenteCurricularMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.InscricaoSelecaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.OrientacaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;

/** 
 * Classe para auxiliar a valida��o de dados de discentes de monitoria.
 * 
 * @author �dipo Elder F. Melo
 * @author Ilueny Santos
 *
 */
public class DiscenteMonitoriaValidator {
	
	/**
	 * Valida se h� vagas para monitores n�o remunerados ou bolsistas no projeto informado.
	 *
	 * @param projeto projeto de monitoria
	 * @param discenteMonitoria discente que ser� inserido no projeto.
	 * @param lista lista de erros
	 * @throws DAOException busca dos dados do projeto 
	 */
	public static void validaLimiteMonitores(final ProjetoEnsino projeto,  DiscenteMonitoria discenteMonitoria, ListaMensagens lista) 
	throws DAOException {

		DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);

		try {

			if (discenteMonitoria.isAssumiuMonitoria() || discenteMonitoria.isConvocado()) {

			    	// @negocio: Somente discentes da gradua��o podem ser monitores.
				validaDiscenteGraduacao(discenteMonitoria.getDiscente(), lista);

				// @negocio: Discentes concluintes ou com algum tipo de afastamento n�o podem ser monitores. 
				validaDiscenteConcluinteOuComAfastamento(discenteMonitoria.getDiscente(), lista);


				// @negocio: Verificando o limite de bolsas do projeto.
				if (discenteMonitoria.isVinculoBolsista()) {
					Collection<DiscenteMonitoria> discentesBolsasAtivos = dao.findAtivosByProjeto(projeto.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA, null);
					if (projeto.getBolsasConcedidas() <= (discentesBolsasAtivos.size()) 
						&& (!discentesBolsasAtivos.contains(discenteMonitoria))) {  //Desconsidera o pr�prio discente retornado na busca como CONVOCADO.
						lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " n�o poder� assumir a monitoria pois n�o h� mais BOLSAS dispon�veis para monitores neste projeto.");
					}
				}

				// @negocio: Verificando o limite de vagas n�o remuneradas do projeto.
				if (discenteMonitoria.isVinculoNaoRemunerado()) {
					Collection<DiscenteMonitoria> discentesNaoRemuneradosAtivos = dao.findAtivosByProjeto(projeto.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, null);
					if (projeto.getBolsasNaoRemuneradas() <= (discentesNaoRemuneradosAtivos.size()) 
						&& (!discentesNaoRemuneradosAtivos.contains(discenteMonitoria))) { //Desconsidera o pr�prio discente retornado na busca como CONVOCADO.
						lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " n�o poder� assumir a monitoria pois n�o h� mais vagas dispon�veis para monitores N�O REMUNERADOS neste projeto.");
					}
				}

			}

		} finally {
			dao.close();
		}

	}

	/**
	 * Verifica se os discentes do projeto tem bolsa na institui��o ou em outro projeto de monitoria.
	 *
	 * @param discenteMonitoria discente que ser� inclu�do.
	 * @param lista lista de erros.
	 * @throws ArqException uso de m�todos direto da arquitetura para verificar se existem bolsas em outros subsistemas (SIPAC)
	 */
	public static void validaDiscenteComBolsa(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws ArqException {

		DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);

		try {

		    	// @negocio: Monitor n�o pode ser bolsista em outro projeto de monitoria no SIGAA.
			Collection<DiscenteMonitoria> discentesMonitoria = dao.findDiscenteMonitoriaAtivoByDiscente(discenteMonitoria.getDiscente());
			for (DiscenteMonitoria discenteNoBanco : discentesMonitoria) {

        			if ((discenteNoBanco.getId() != 0)
        					&& !discenteNoBanco.getProjetoEnsino().equals(discenteMonitoria.getProjetoEnsino()) //outro projeto
        					&& discenteNoBanco.isVinculoBolsista() && discenteMonitoria.isVinculoBolsista()) { //outro projeto tb � bolsista
        
        				lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " n�o poder� assumir a monitoria pois j� faz parte de outro projeto de monitoria.");
        			}        			
			}
        
        		
			if(Sistema.isSipacAtivo()) {
				// @negocio: Monitor n�o pode possuir bolsas cadastradas no SIPAC.        	
				if (discenteMonitoria.isVinculoBolsista() && (discenteMonitoria.isConvocado() || (discenteMonitoria.isAssumiuMonitoria()))) {        	
					if (IntegracaoBolsas.verificarCadastroBolsaSIPAC(discenteMonitoria.getDiscente().getMatricula(), null) != 0) {        		
						lista.addErro("SIPAC: opera��o n�o poder� ser realizada porque " + discenteMonitoria.getDiscente().getMatriculaNome() + " j� possui bolsa ativa no sistema.");        			
						lista.addErro("Finalize a bolsa anterior e tente realizar esta opera��o novamente.");        			
					}        		
				}
			} else {
				//AQUI DEVE TER OUTRAS VALIDA��ES PARA VERIFICAR SE O DISCENTE POSSUI OUTRAS BOLSAS SEM SER DE MONITORIA E QUE NAO PERMITAM ACUMULAR
				//NO SIGAA AS BOLSAS N�O ESTAO CENTRALIZADAS COMO NO SIPAC. UMA CONSULTA NO SIGAA ENVOLVERIA VARIAS TABELAS: 
				//TEM BOLSAS DE MONITORIA ? Que no caso consulta a entidade DiscenteMonitoria, como � feito logo acima.
				//TEM BOLSAS DE EXTENSAO ? Que no caso, � consultada a entidade PlanoTrabalhoExtensao, como � feito em EXTENS�O
				//TEM BOLSAS DE PESQUISA ?
				//ENFIM, TEM ALGUMA BOLSA QUE N�O PERMITA ACUMULAR ?
				//OU CENTRALIZA AS BOLSAS OU FAZ A BUSCA EM V�RIAS TABELAS.				
			}

		} finally {
			dao.close();
		}

	}	

	/** 
	 * Verifica se o discente � concluinte ou est� afastado da institui��o.
	 *  
	 * @param discenteMonitoria discente para verifica��o.
	 * @param lista lista de erros reportados.
	 */
	public static void validaDiscenteConcluinteOuComAfastamento(DiscenteAdapter discente, ListaMensagens lista) {

		// @negocio: Somente discentes ativos ou que est�o pagando as �ltimas disciplinas podem ser monitores.
		if ((discente.getStatus() != StatusDiscente.ATIVO) 
				&& (discente.getStatus() != StatusDiscente.FORMANDO)) {
			lista.addErro(discente.getMatriculaNome() + ": Discentes concluintes ou com algum tipo de afastamento n�o podem participar de projetos de monitoria.");
		}

	}

	/** 
	 * Valida os dados banc�rios do discente.
	 * 
	 * @param discenteMonitoria discente para verifica��o dos dados 
	 * @param lista lista de erros
	 */
	public static void validaDadosBancarios(DiscenteMonitoria discenteMonitoria,  ListaMensagens lista) {

		// @negocio: Verificando se o discente est� com os dados banc�rios atualizados.
		if ((discenteMonitoria.getBanco() == null) || (discenteMonitoria.getConta() == null) || (discenteMonitoria.getAgencia() == null)) {
			lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() 
					+ ": N�o possui dados banc�rios cadastrados no SIGAA.");
		}

	}

	/**
	 * Valida a adi��o do resultado da sele��o de um discente em um projeto de monitoria pelo coordenador do projeto.
	 * 
	 * @param discenteMonitoria discente 
	 * @param prova prova seletiva da qual o discente participou.
	 * @param lista lista de erros reportados.
	 * @param calendario calend�rio acad�mico atual. Utilizado na verifica��o da ades�o do discente ao cadastro �nico.
	 * @throws ArqException buscas ao banco de dados e chamadas a outros m�todos.
	 */
	public static void validaAdicaoSelecao(DiscenteMonitoria discenteMonitoria,  ProvaSelecao prova,  ListaMensagens lista, 
			CalendarioAcademico calendario) throws ArqException {

		ValidatorUtil.validateRequired(discenteMonitoria.getDiscente(), "Discente", 		lista);
		ValidatorUtil.validateRequired(discenteMonitoria.getNota(), 	"Nota do discente", 	lista);
		ValidatorUtil.validateRequired(discenteMonitoria.getNotaProva(), "Nota da Prova Escrita", lista);


		// @negocio: A m�dia  da sele��o da monitoria e a nota da prova escrita deve ser um n�mero entre zero e dez.
		if ((discenteMonitoria.getNota() == null) || (discenteMonitoria.getNota() > 10) || (discenteMonitoria.getNota() < 0)){
			lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}

		if ((discenteMonitoria.getNotaProva() == null) || (discenteMonitoria.getNotaProva() > 10) || (discenteMonitoria.getNotaProva() < 0)){
			lista.addErro("Nota da Prova Escrita: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}

		// @negocio: O campo observa��o � obrigat�rio no caso de o discente ser marcado como N�o Classificado.
		if (!discenteMonitoria.isClassificado()) {
			ValidatorUtil.validateRequired(discenteMonitoria.getObservacao(), "Observa��o", lista);
		}

		// @negocio: S� valida se o discente for classificado na prova seletiva ou possui tipo de v�nculo V�lido.
		if (discenteMonitoria.isClassificado()) {
		    
			// @negocio: Somente discentes da gradua��o podem ser monitores.
			validaDiscenteGraduacao(discenteMonitoria.getDiscente(), lista);
			
			// @negocio: Discentes concluintes ou com algum tipo de afastamento n�o podem ser monitores. 
			validaDiscenteConcluinteOuComAfastamento(discenteMonitoria.getDiscente(), lista);

			// Verifica se est� no cadastro �nico
			validaDiscenteInCadastroUnico(discenteMonitoria.getDiscente(), lista, calendario);

			// Verifica se pagou disciplinas obrigat�rias com m�dia >= 7.0
			validaCapacidadeTecnica(discenteMonitoria.getDiscente(), prova, lista);

			// Verifica dados banc�rios do discente
			// Retirado temporariamente, cobrado quando o discente vai assumir a bolsa
			//validaDadosBancarios(discenteMonitoria, lista);
		}

	}

	/**
	 * Somente alunos de gradua��o podem assumir monitoria.
	 * @param discenteMonitoria
	 * @param lista
	 */
	public static void validaDiscenteGraduacao(DiscenteAdapter discente, ListaMensagens lista) {
	    if (discente.getNivel() != NivelEnsino.GRADUACAO) {
	    	lista.addErro(discente.getMatriculaNome() + " n�o poder� assumir a monitoria pois n�o � aluno(a) de gradua��o.");				
	    }
	}
	
	/**
	 * Verifica se o docente pode iniciar o cadastro da prova seletiva.
	 * 
	 * @param prova
	 * @param anoAtual
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaIniciarCadastroSelecao(ProvaSelecao prova, int anoAtual, ListaMensagens lista) throws ArqException {
	    
	    ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
	    try {
                if (prova.getProjetoEnsino().getProjeto().isInterno() && prova.getProjetoEnsino().getEditalMonitoria() == null) {
                lista.addErro("Este projeto n�o est� associado a um edital portanto n�o � poss�vel cadastrar resultado de sele��o.");
                }
                        	    
                CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(anoAtual);
                if ((cm == null) || (cm.getInicioConfirmacaoMonitoria() == null) || (cm.getFimConfirmacaoMonitoria() == null)) {
                	lista.addErro("Per�odo de efetiva��o das bolsas de monitoria n�o foi definido no calend�rio da Pr�-Reitoria de Gradua��o.");
                }
	    }finally {
		dao.close();
	    }
	    

	    /**
	     * s� � possivel cadastrar resultados de sele��o caso ainda esteja
	     * no periodo de selecao definido no edital a qual o projeto faz
	     * parte
	     * 
	     * @see EditalMonitoria.fimSelecaoMonitor Date hoje = new Date();
	     *      hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
	     *      DateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
	     * 
	     * 
	     *      Valida��o retirada por ordem da prograd. Agora, um projeto
	     *      pode ter quantas provas seletivas quiser e pode realiz�-las
	     *      a qualquer tempo.
	     * 
	     *      if ( provaSelecao.getProjetoEnsino().getEdital().
	     *      getFimSelecaoMonitor().compareTo(hoje) < 0 ){
	     *      addMensagemErro(
	     *      "O per�odo de sele��o do edital a qual este projeto faz parte izou no dia "
	     *      + format.format(provaSelecao.getProjetoEnsino().getEdital().
	     *      getFimSelecaoMonitor()) +
	     *      ", portanto, n�o � poss�vel cadastrar resultados de sele��o;"
	     *      ); return null; }
	     */
	    
	}

	/**
	 * Verifica se o discente j� foi cadastrado na prova seletiva informada.
	 * 
	 * @param discenteMonitoria discente que possivelmente j� est� cadastrado.
	 * @param prova prova onde ser� verificada a presen�a do discente.
	 * @param lista lista de erros reportados.
	 */
	public static void validaDiscenteCadastradoSelecao(DiscenteMonitoria discenteMonitoria, ProvaSelecao prova, ListaMensagens lista) {

		for (DiscenteMonitoria sel : prova.getResultadoSelecao()) {
			if (sel.getDiscente().getId() == discenteMonitoria.getDiscente().getId()) {
				lista.addErro("O resultado da sele��o deste discente j� foi cadastrado.");
			}
		}

	}

	/** 
	 * Verificando se o discente est� no cadastro �nico de acordo com a resolu��o 169/2008.
	 * 
	 * @param discente discente que ser� verificado no cadastro �nico.
	 * @param lista lista de erros reportados
	 * @param calendario calend�rio acad�mico atual utilizado na busca de ades�o do discente.
	 * @throws DAOException busca do cadastro �nico.
	 */
	public static void validaDiscenteInCadastroUnico(DiscenteAdapter discente, ListaMensagens lista, CalendarioAcademico calendario) 
	throws DAOException {

		AdesaoCadastroUnicoBolsaDao dao = DAOFactory.getInstance().getDAO(AdesaoCadastroUnicoBolsaDao.class);
		try {
			boolean adesao = dao.isAdesaoCadastroUnico(discente.getId(), calendario.getAno(), calendario.getPeriodo());

			if (!adesao) {
				lista.addErro(discente.getMatriculaNome() + " n�o foi encontrado(a) no Cadastro �nico.");
				lista.addErro("A ades�o deste(a) discente ao Cadastro �nico � obrigat�ria segundo a resolu��o 169/2008-CONSEPE.");
			}
		} finally {
			dao.close();
		}

	}

	/**
	 * Certifica de que cada monitor possua pelo menos um orientador.
	 * 
	 * @param discenteMonitoria discente que deve ter pelo menos um orientador, se for bolsista ou n�o remunerado. 
	 * @param lista lista de erros reportados.
	 */
	public static void validaCadaMonitorComUmOrientador(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) {
		// @negocio: TODOS os monitores do tipo BOLSISTA OU NAO_REMUNERADO DEVEM possuir orientador
		if (discenteMonitoria.isVinculoBolsista()  || discenteMonitoria.isVinculoNaoRemunerado()) {
			if ((discenteMonitoria.getOrientacoes() == null) || (discenteMonitoria.getOrientacoes().isEmpty())) {
				lista.addErro("Cada monitor classificado para este projeto deve possuir no m�nimo um orientador.");
			}
		}
	}


	/**
	 * Verifica se o docente tem mais de 2 orienta��es no projeto. Cada membro docente pode orientar APENAS 2 monitores POR PROJETO.
	 *  
	 * @param equipeDocente docente que s� pode ter 2 orienta��es por projeto. 
	 * @param lista lista de erros reportados.
	 * @throws DAOException busca das orienta��es do discente.
	 */
	public static void validaMaximoOrientacoesDocentes(EquipeDocente equipeDocente, Date dataInicio, Date dataFim,  ListaMensagens lista)throws DAOException {

		OrientacaoDao dao = DAOFactory.getInstance().getDAO(OrientacaoDao.class);
		int orientacoesAtivas = dao.findOrienatacoesAtivasByEquipeDocente(equipeDocente, dataInicio, dataFim );
		int maxOrientacoesAtivas = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.MAXIMO_ORIENTACOES_ATIVAS);
		try {
			// @negocio: Um orientador so pode ter 2 monitores
			if (orientacoesAtivas > maxOrientacoesAtivas) {
				lista.addWarning("Cada docente pode orientar no m�ximo " + maxOrientacoesAtivas +" monitores por projeto.<br/>" + equipeDocente.getServidor().getSiapeNome() + "  possui agora mais de 2 orientandos neste projeto.");
			}

		} finally {
			dao.close();
		}

	}


	/**
	 * Valida o n�mero m�ximo de projetos. Cada Docente pode participar de at� 2 projeto ativos contando o ano atual e o ano anterior.
	 * 
	 * @param equipeDocente docente que s� pode estar em at� de 2 projetos ativos.
	 * @param apartirDoAno ano atual.
	 * @param lista lista de erros reportados.
	 * @throws DAOException busca dos projetos onde o docente participa.
	 */
	public static void validaMaximoProjetosDocente(EquipeDocente equipeDocente, ListaMensagens lista)throws DAOException {

		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
		    
			int totalProjetosAtivos = dao.countOutrosProjetosAtivos(equipeDocente);
			int maxProjetosAtivos;			
			
			//valida max de coordena��es para um docente
			if(equipeDocente.isCoordenador() && equipeDocente.getProjetoEnsino().getEditalMonitoria() != null){
				maxProjetosAtivos = equipeDocente.getProjetoEnsino().getEditalMonitoria().getEdital().getRestricaoCoordenacao().getMaxCoordenacoesAtivas();
			}else
				maxProjetosAtivos = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.MAXIMO_PROJETOS_ATIVOS);
			
			if (totalProjetosAtivos >= maxProjetosAtivos) {
				lista.addErro(equipeDocente.getServidor().getSiapeNome() 
						+ " j� � docente ativo em "+ maxProjetosAtivos +" projeto(s) de monitoria portanto, n�o pode ser cadadastrado em outro projeto.");
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida a inscri��o do discente em um projeto de monitoria
	 * 1. Verifica se � discente de gradua��o
	 * 2. Verifica se o discente � concluinte ou est� afastado da institui��o
	 * 3. Verifica se o discente j� se inscreveu para a prova selecionada
	 * 
	 * @param discente discente que ser� validado se pode realizar a inscri��o na prova seletiva.
	 * @param lista lista de erros reportados
	 * @param prova prova onde o aluno far� a inscri��o
	 * @throws ArqException pode ser gerado na verifica��o das capacidades t�cnicas do discente.
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente,  ProvaSelecao prova,  ListaMensagens lista) throws ArqException {

		// @negocio: Somente discentes da gradua��o podem ser monitores.
		validaDiscenteGraduacao(discente, lista);
		
		// @negocio: Discentes concluintes ou com algum tipo de afastamento n�o podem ser monitores. 
		validaDiscenteConcluinteOuComAfastamento(discente, lista);

		// Verificando se o discente j� se inscreveu para a sele��o deste projeto.
		InscricaoSelecaoMonitoriaDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoMonitoriaDao.class);

		try {

			InscricaoSelecaoMonitoria inscricao = dao.findByDiscenteProjeto(discente, prova);
			if (inscricao != null && inscricao.getId() != 0) {
				lista.addErro("Voc� j� realizou a inscri��o para este projeto de monitoria portanto n�o pode realizar novamente.");
			}

			// Verifica se o aluno tem capacidade t�cnica de participar da sele��o
			validaCapacidadeTecnica(discente, prova, lista);

		} finally {
			dao.close();
		}

	}

	/**
	 * Valida a capacidade t�cnica do discente candidato a bolsa de monitoria do projeto.
	 * Verifica se o discente j� cursou as disciplinas obrigat�rias do projeto e se o 
	 * projeto n�o possui disciplinas obrigat�rias, verifica se o aluno tem m�dia igual 
	 * ou superior a 7 em pelo menos uma delas.
	 * 
	 * @param discente discente que ser� validado.
	 * @param prova prova seletiva na qual o discente est� tentando se inscrever.
	 * @param lista lista de erros reportados.
	 * @throws ArqException pode ser gerada nas buscas ao banco de dados.
	 */
	public static void validaCapacidadeTecnicaDiscente(DiscenteAdapter discente,  ProvaSelecao prova,  ListaMensagens lista) throws ArqException {

		ComponenteCurricularMonitoriaDao dao = DAOFactory.getInstance().getDAO(ComponenteCurricularMonitoriaDao.class);

		try {
			//Atualiza a prova
			prova = dao.findByPrimaryKey(prova.getId(), ProvaSelecao.class);
			prova.getComponentesObrigatorios().iterator();

			// Componentes novos n�o tem turmas ainda, assim, qualquer discente pode ser monitor destes componentes
			int situacoesTurmasValidas[] = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_SITUACOES_TURMAS);
			boolean provaPossuiAlgumComponenteNovo = dao.isProvaPossuiAlgumComponenteSemTurmaBySituacao(prova.getId(), situacoesTurmasValidas);

			// Componentes Curriculares marcados pelos docentes como Obrigat�rios para prova
			int qtdObrigatorias = dao.findQtdComponentesMonitoriaObrigatoriasByProva(prova.getId(), true);
			int qtdNaoObrigatorias = dao.findQtdComponentesMonitoriaObrigatoriasByProva(prova.getId(), false);

			// Se h� disciplinas obrigat�rias marcadas na prova, o discente deve ter pago todas com m�dia >= NotaMinimaAprovacaoSelecaoMonitora 
			// Se N�O existem disciplinas obrigat�rias marcadas na prova, o discente pode ter pago qualquer componente com m�dia >= NotaMinimaAprovacaoSelecaoMonitora   
			Collection<ComponenteCurricular> disciplinasNaoCursadas = dao.findDisciplinasProvaNaoCursadas(prova, discente);

			int qtdNaoCursadas = 0;			
			if (!ValidatorUtil.isEmpty(disciplinasNaoCursadas)) {				
				qtdNaoCursadas = disciplinasNaoCursadas.size();
			}

			//@negocio: Se a prova possuir componentes novos (componentes que nunca tiveram turmas), qualquer discente pode ser monitor.
			boolean apto = provaPossuiAlgumComponenteNovo;

			//@negocio: Se a prova possuir componentes obrigat�rios o discente deve ter pago todos com m�dia apropriada.
			if (!apto && (qtdObrigatorias > 0) && (qtdNaoCursadas == 0)) {
				apto = true;
			}

			//@negocio:  Se a prova n�o possuir componentes obrigat�rios o discente deve ter pago pelo menos 1 dos opcionais.
			if (!apto && (qtdObrigatorias == 0) && (qtdNaoObrigatorias > qtdNaoCursadas)) {
				apto = true;
			}
			
			if (!apto) {
				String msg = discente.getMatriculaNome() + " n�o cursou ou n�o foi aprovado(a) com m�dia igual ou superior a " 
				+ prova.getProjetoEnsino().getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora() + " na(s) seguinte(s) disciplina(s): <br/>"; 

				Collection<ComponenteCurricular> componentesConcluidos  = dao.findComponentesCurricularesPagos(discente, prova);
				boolean erro = false;
				for (ComponenteCurricular componente : disciplinasNaoCursadas) {
					// Verifica se o discente cursou uma disciplina equivalente
					if (componente.getEquivalencia() == null || !ExpressaoUtil.eval(componente.getEquivalencia(), componentesConcluidos)) {
						msg += componente.getCodigo() + " - " + componente.getDetalhes().getNome() + ";<br/>";
						erro = true;
					}
				}
				if (erro) {
					lista.addErro(msg);
				}
			}
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Valida a capacidade t�cnica do discente candidato a bolsa de monitoria do projeto.
	 * Verifica se o discente j� cursou as disciplinas obrigat�rias do projeto e se o 
	 * projeto n�o possui disciplinas obrigat�rias, verifica se o aluno tem m�dia igual 
	 * ou superior a 7 em pelo menos uma delas.
	 * 
	 * @param discente discente que ser� validado.
	 * @param prova prova seletiva na qual o discente est� tentando se inscrever.
	 * @param lista lista de erros reportados.
	 * @throws ArqException pode ser gerada nas buscas ao banco de dados.
	 */
	public static void validaCapacidadeTecnica(DiscenteAdapter discente,  ProvaSelecao prova,  ListaMensagens lista) throws ArqException {
		ComponenteCurricularMonitoriaDao dao = DAOFactory.getInstance().getDAO(ComponenteCurricularMonitoriaDao.class);

		try {
			//Atualiza a prova
			prova = dao.findByPrimaryKey(prova.getId(), ProvaSelecao.class);
			if (prova.getProjetoEnsino().getEditalMonitoria() != null)
				prova.getProjetoEnsino().getEditalMonitoria().getId();
			prova.getComponentesObrigatorios().iterator();
			
			//Analisando pr�-requisitos para prova.
			Collection<ComponenteCurricular> componentesConcluidos  = dao.findComponentesCurricularesPagos(discente, prova);
			boolean apto = false;
			if (!ValidatorUtil.isEmpty(componentesConcluidos)) {
				apto = ExpressaoUtil.evalComTransitividade(prova.getExpressaoPreRequisitosInscricaoDiscente(), discente.getId(), componentesConcluidos);
			}
			
			if (!apto) {
				String msg = discente.getMatriculaNome() + "  n�o cursou ou n�o foi aprovado(a) com m�dia  igual ou superior a " 
				+ prova.getProjetoEnsino().getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora() + " na(s) seguinte(s) disciplina(s): <br/>"; 
						
				Collection<ComponenteCurricular> disciplinasNaoCursadas = dao.findDisciplinasProvaNaoCursadas(prova, discente);
				boolean erro = false;
				for (ComponenteCurricular componente : disciplinasNaoCursadas) {
					// Verifica se o discente cursou uma disciplina equivalente
					if (componente.getEquivalencia() == null || !ExpressaoUtil.eval(componente.getEquivalencia(), componentesConcluidos)) {
						msg += componente.getCodigo() + " - " + componente.getDetalhes().getNome() + ";<br/>";
						erro = true;
					}
				}
				if (erro) {
					lista.addErro(msg);
				}
			}
			
		} finally {
			dao.close();
		}		
	}





	/**
	 * 
	 * Valida se o calend�rio de monitoria permite novos bolsistas
	 * 
	 * @param discenteMonitoria
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaPeriodoConfirmacaoMonitoria(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws DAOException {

		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
			CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if ((cm == null) || (!cm.isAtivo())) {
				lista.addErro("Per�odo de confirma��o da bolsa ainda n�o foi definido pela Pr�-Reitoria de Gradua��o.");

			}else if (!cm.isPeriodoConfirmacaoMonitoriaEmAberto()) {
				SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Atualmente, a efetiva��o de monitores em projetos est� autorizada no per�odo de " + sp.format(cm.getInicioConfirmacaoMonitoria()) +" at� " + sp.format(cm.getFimConfirmacaoMonitoria()) + ".");
				lista.addErro("Para maiores informa��es entre em contato com a Pr�-Reitoria de Gradua��o.");
			}
		} finally {
			dao.close();
		}	
	}

	/**
	 * Valida os principais dados do cadastro do discente em um projeto de monitoria.
	 * Incluindo Data de in�cio e fim do discente no projeto.
	 * 
	 * @param dm
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaDadosPrincipais(DiscenteMonitoria dm, ListaMensagens lista) throws ArqException{
	    lista.addAll(dm.validate());
	    ValidatorUtil.validaData(Formatador.getInstance().formatarData(dm.getDataInicio()), "Data in�cio", lista);
	    ValidatorUtil.validaData(Formatador.getInstance().formatarData(dm.getDataFim()), "Data fim", lista);
	}
	
	/**
	 * Realiza a valida��o de discente, verificando todos os requisitos necess�rios para assumir a bolsa.
	 * 
	 * @param discenteMonitoria
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaDiscenteAssumirMonitoria(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws ArqException{

	    	validaDadosPrincipais(discenteMonitoria, lista);
		validaDiscenteComBolsa(discenteMonitoria, lista);
		validaDiscenteConcluinteOuComAfastamento(discenteMonitoria.getDiscente(),	lista);
		validaLimiteMonitores(discenteMonitoria.getProjetoEnsino(), discenteMonitoria, lista); 
		validaCadaMonitorComUmOrientador(discenteMonitoria, lista);
		validaPeriodoConfirmacaoMonitoria(discenteMonitoria, lista);

	}

}
