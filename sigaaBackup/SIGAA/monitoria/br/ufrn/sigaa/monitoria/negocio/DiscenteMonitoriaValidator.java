/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe para auxiliar a validação de dados de discentes de monitoria.
 * 
 * @author Édipo Elder F. Melo
 * @author Ilueny Santos
 *
 */
public class DiscenteMonitoriaValidator {
	
	/**
	 * Valida se há vagas para monitores não remunerados ou bolsistas no projeto informado.
	 *
	 * @param projeto projeto de monitoria
	 * @param discenteMonitoria discente que será inserido no projeto.
	 * @param lista lista de erros
	 * @throws DAOException busca dos dados do projeto 
	 */
	public static void validaLimiteMonitores(final ProjetoEnsino projeto,  DiscenteMonitoria discenteMonitoria, ListaMensagens lista) 
	throws DAOException {

		DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);

		try {

			if (discenteMonitoria.isAssumiuMonitoria() || discenteMonitoria.isConvocado()) {

			    	// @negocio: Somente discentes da graduação podem ser monitores.
				validaDiscenteGraduacao(discenteMonitoria.getDiscente(), lista);

				// @negocio: Discentes concluintes ou com algum tipo de afastamento não podem ser monitores. 
				validaDiscenteConcluinteOuComAfastamento(discenteMonitoria.getDiscente(), lista);


				// @negocio: Verificando o limite de bolsas do projeto.
				if (discenteMonitoria.isVinculoBolsista()) {
					Collection<DiscenteMonitoria> discentesBolsasAtivos = dao.findAtivosByProjeto(projeto.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA, null);
					if (projeto.getBolsasConcedidas() <= (discentesBolsasAtivos.size()) 
						&& (!discentesBolsasAtivos.contains(discenteMonitoria))) {  //Desconsidera o próprio discente retornado na busca como CONVOCADO.
						lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " não poderá assumir a monitoria pois não há mais BOLSAS disponíveis para monitores neste projeto.");
					}
				}

				// @negocio: Verificando o limite de vagas não remuneradas do projeto.
				if (discenteMonitoria.isVinculoNaoRemunerado()) {
					Collection<DiscenteMonitoria> discentesNaoRemuneradosAtivos = dao.findAtivosByProjeto(projeto.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, null);
					if (projeto.getBolsasNaoRemuneradas() <= (discentesNaoRemuneradosAtivos.size()) 
						&& (!discentesNaoRemuneradosAtivos.contains(discenteMonitoria))) { //Desconsidera o próprio discente retornado na busca como CONVOCADO.
						lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " não poderá assumir a monitoria pois não há mais vagas disponíveis para monitores NÃO REMUNERADOS neste projeto.");
					}
				}

			}

		} finally {
			dao.close();
		}

	}

	/**
	 * Verifica se os discentes do projeto tem bolsa na instituição ou em outro projeto de monitoria.
	 *
	 * @param discenteMonitoria discente que será incluído.
	 * @param lista lista de erros.
	 * @throws ArqException uso de métodos direto da arquitetura para verificar se existem bolsas em outros subsistemas (SIPAC)
	 */
	public static void validaDiscenteComBolsa(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws ArqException {

		DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);

		try {

		    	// @negocio: Monitor não pode ser bolsista em outro projeto de monitoria no SIGAA.
			Collection<DiscenteMonitoria> discentesMonitoria = dao.findDiscenteMonitoriaAtivoByDiscente(discenteMonitoria.getDiscente());
			for (DiscenteMonitoria discenteNoBanco : discentesMonitoria) {

        			if ((discenteNoBanco.getId() != 0)
        					&& !discenteNoBanco.getProjetoEnsino().equals(discenteMonitoria.getProjetoEnsino()) //outro projeto
        					&& discenteNoBanco.isVinculoBolsista() && discenteMonitoria.isVinculoBolsista()) { //outro projeto tb é bolsista
        
        				lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() + " não poderá assumir a monitoria pois já faz parte de outro projeto de monitoria.");
        			}        			
			}
        
        		
			if(Sistema.isSipacAtivo()) {
				// @negocio: Monitor não pode possuir bolsas cadastradas no SIPAC.        	
				if (discenteMonitoria.isVinculoBolsista() && (discenteMonitoria.isConvocado() || (discenteMonitoria.isAssumiuMonitoria()))) {        	
					if (IntegracaoBolsas.verificarCadastroBolsaSIPAC(discenteMonitoria.getDiscente().getMatricula(), null) != 0) {        		
						lista.addErro("SIPAC: operação não poderá ser realizada porque " + discenteMonitoria.getDiscente().getMatriculaNome() + " já possui bolsa ativa no sistema.");        			
						lista.addErro("Finalize a bolsa anterior e tente realizar esta operação novamente.");        			
					}        		
				}
			} else {
				//AQUI DEVE TER OUTRAS VALIDAÇÕES PARA VERIFICAR SE O DISCENTE POSSUI OUTRAS BOLSAS SEM SER DE MONITORIA E QUE NAO PERMITAM ACUMULAR
				//NO SIGAA AS BOLSAS NÃO ESTAO CENTRALIZADAS COMO NO SIPAC. UMA CONSULTA NO SIGAA ENVOLVERIA VARIAS TABELAS: 
				//TEM BOLSAS DE MONITORIA ? Que no caso consulta a entidade DiscenteMonitoria, como é feito logo acima.
				//TEM BOLSAS DE EXTENSAO ? Que no caso, é consultada a entidade PlanoTrabalhoExtensao, como é feito em EXTENSÃO
				//TEM BOLSAS DE PESQUISA ?
				//ENFIM, TEM ALGUMA BOLSA QUE NÃO PERMITA ACUMULAR ?
				//OU CENTRALIZA AS BOLSAS OU FAZ A BUSCA EM VÁRIAS TABELAS.				
			}

		} finally {
			dao.close();
		}

	}	

	/** 
	 * Verifica se o discente é concluinte ou está afastado da instituição.
	 *  
	 * @param discenteMonitoria discente para verificação.
	 * @param lista lista de erros reportados.
	 */
	public static void validaDiscenteConcluinteOuComAfastamento(DiscenteAdapter discente, ListaMensagens lista) {

		// @negocio: Somente discentes ativos ou que estão pagando as últimas disciplinas podem ser monitores.
		if ((discente.getStatus() != StatusDiscente.ATIVO) 
				&& (discente.getStatus() != StatusDiscente.FORMANDO)) {
			lista.addErro(discente.getMatriculaNome() + ": Discentes concluintes ou com algum tipo de afastamento não podem participar de projetos de monitoria.");
		}

	}

	/** 
	 * Valida os dados bancários do discente.
	 * 
	 * @param discenteMonitoria discente para verificação dos dados 
	 * @param lista lista de erros
	 */
	public static void validaDadosBancarios(DiscenteMonitoria discenteMonitoria,  ListaMensagens lista) {

		// @negocio: Verificando se o discente está com os dados bancários atualizados.
		if ((discenteMonitoria.getBanco() == null) || (discenteMonitoria.getConta() == null) || (discenteMonitoria.getAgencia() == null)) {
			lista.addErro(discenteMonitoria.getDiscente().getMatriculaNome() 
					+ ": Não possui dados bancários cadastrados no SIGAA.");
		}

	}

	/**
	 * Valida a adição do resultado da seleção de um discente em um projeto de monitoria pelo coordenador do projeto.
	 * 
	 * @param discenteMonitoria discente 
	 * @param prova prova seletiva da qual o discente participou.
	 * @param lista lista de erros reportados.
	 * @param calendario calendário acadêmico atual. Utilizado na verificação da adesão do discente ao cadastro único.
	 * @throws ArqException buscas ao banco de dados e chamadas a outros métodos.
	 */
	public static void validaAdicaoSelecao(DiscenteMonitoria discenteMonitoria,  ProvaSelecao prova,  ListaMensagens lista, 
			CalendarioAcademico calendario) throws ArqException {

		ValidatorUtil.validateRequired(discenteMonitoria.getDiscente(), "Discente", 		lista);
		ValidatorUtil.validateRequired(discenteMonitoria.getNota(), 	"Nota do discente", 	lista);
		ValidatorUtil.validateRequired(discenteMonitoria.getNotaProva(), "Nota da Prova Escrita", lista);


		// @negocio: A média  da seleção da monitoria e a nota da prova escrita deve ser um número entre zero e dez.
		if ((discenteMonitoria.getNota() == null) || (discenteMonitoria.getNota() > 10) || (discenteMonitoria.getNota() < 0)){
			lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}

		if ((discenteMonitoria.getNotaProva() == null) || (discenteMonitoria.getNotaProva() > 10) || (discenteMonitoria.getNotaProva() < 0)){
			lista.addErro("Nota da Prova Escrita: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}

		// @negocio: O campo observação é obrigatório no caso de o discente ser marcado como Não Classificado.
		if (!discenteMonitoria.isClassificado()) {
			ValidatorUtil.validateRequired(discenteMonitoria.getObservacao(), "Observação", lista);
		}

		// @negocio: Só valida se o discente for classificado na prova seletiva ou possui tipo de vínculo Válido.
		if (discenteMonitoria.isClassificado()) {
		    
			// @negocio: Somente discentes da graduação podem ser monitores.
			validaDiscenteGraduacao(discenteMonitoria.getDiscente(), lista);
			
			// @negocio: Discentes concluintes ou com algum tipo de afastamento não podem ser monitores. 
			validaDiscenteConcluinteOuComAfastamento(discenteMonitoria.getDiscente(), lista);

			// Verifica se está no cadastro único
			validaDiscenteInCadastroUnico(discenteMonitoria.getDiscente(), lista, calendario);

			// Verifica se pagou disciplinas obrigatórias com média >= 7.0
			validaCapacidadeTecnica(discenteMonitoria.getDiscente(), prova, lista);

			// Verifica dados bancários do discente
			// Retirado temporariamente, cobrado quando o discente vai assumir a bolsa
			//validaDadosBancarios(discenteMonitoria, lista);
		}

	}

	/**
	 * Somente alunos de graduação podem assumir monitoria.
	 * @param discenteMonitoria
	 * @param lista
	 */
	public static void validaDiscenteGraduacao(DiscenteAdapter discente, ListaMensagens lista) {
	    if (discente.getNivel() != NivelEnsino.GRADUACAO) {
	    	lista.addErro(discente.getMatriculaNome() + " não poderá assumir a monitoria pois não é aluno(a) de graduação.");				
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
                lista.addErro("Este projeto não está associado a um edital portanto não é possível cadastrar resultado de seleção.");
                }
                        	    
                CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(anoAtual);
                if ((cm == null) || (cm.getInicioConfirmacaoMonitoria() == null) || (cm.getFimConfirmacaoMonitoria() == null)) {
                	lista.addErro("Período de efetivação das bolsas de monitoria não foi definido no calendário da Pró-Reitoria de Graduação.");
                }
	    }finally {
		dao.close();
	    }
	    

	    /**
	     * só é possivel cadastrar resultados de seleção caso ainda esteja
	     * no periodo de selecao definido no edital a qual o projeto faz
	     * parte
	     * 
	     * @see EditalMonitoria.fimSelecaoMonitor Date hoje = new Date();
	     *      hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
	     *      DateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
	     * 
	     * 
	     *      Validação retirada por ordem da prograd. Agora, um projeto
	     *      pode ter quantas provas seletivas quiser e pode realizá-las
	     *      a qualquer tempo.
	     * 
	     *      if ( provaSelecao.getProjetoEnsino().getEdital().
	     *      getFimSelecaoMonitor().compareTo(hoje) < 0 ){
	     *      addMensagemErro(
	     *      "O período de seleção do edital a qual este projeto faz parte izou no dia "
	     *      + format.format(provaSelecao.getProjetoEnsino().getEdital().
	     *      getFimSelecaoMonitor()) +
	     *      ", portanto, não é possível cadastrar resultados de seleção;"
	     *      ); return null; }
	     */
	    
	}

	/**
	 * Verifica se o discente já foi cadastrado na prova seletiva informada.
	 * 
	 * @param discenteMonitoria discente que possivelmente já está cadastrado.
	 * @param prova prova onde será verificada a presença do discente.
	 * @param lista lista de erros reportados.
	 */
	public static void validaDiscenteCadastradoSelecao(DiscenteMonitoria discenteMonitoria, ProvaSelecao prova, ListaMensagens lista) {

		for (DiscenteMonitoria sel : prova.getResultadoSelecao()) {
			if (sel.getDiscente().getId() == discenteMonitoria.getDiscente().getId()) {
				lista.addErro("O resultado da seleção deste discente já foi cadastrado.");
			}
		}

	}

	/** 
	 * Verificando se o discente está no cadastro único de acordo com a resolução 169/2008.
	 * 
	 * @param discente discente que será verificado no cadastro único.
	 * @param lista lista de erros reportados
	 * @param calendario calendário acadêmico atual utilizado na busca de adesão do discente.
	 * @throws DAOException busca do cadastro único.
	 */
	public static void validaDiscenteInCadastroUnico(DiscenteAdapter discente, ListaMensagens lista, CalendarioAcademico calendario) 
	throws DAOException {

		AdesaoCadastroUnicoBolsaDao dao = DAOFactory.getInstance().getDAO(AdesaoCadastroUnicoBolsaDao.class);
		try {
			boolean adesao = dao.isAdesaoCadastroUnico(discente.getId(), calendario.getAno(), calendario.getPeriodo());

			if (!adesao) {
				lista.addErro(discente.getMatriculaNome() + " não foi encontrado(a) no Cadastro Único.");
				lista.addErro("A adesão deste(a) discente ao Cadastro Único é obrigatória segundo a resolução 169/2008-CONSEPE.");
			}
		} finally {
			dao.close();
		}

	}

	/**
	 * Certifica de que cada monitor possua pelo menos um orientador.
	 * 
	 * @param discenteMonitoria discente que deve ter pelo menos um orientador, se for bolsista ou não remunerado. 
	 * @param lista lista de erros reportados.
	 */
	public static void validaCadaMonitorComUmOrientador(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) {
		// @negocio: TODOS os monitores do tipo BOLSISTA OU NAO_REMUNERADO DEVEM possuir orientador
		if (discenteMonitoria.isVinculoBolsista()  || discenteMonitoria.isVinculoNaoRemunerado()) {
			if ((discenteMonitoria.getOrientacoes() == null) || (discenteMonitoria.getOrientacoes().isEmpty())) {
				lista.addErro("Cada monitor classificado para este projeto deve possuir no mínimo um orientador.");
			}
		}
	}


	/**
	 * Verifica se o docente tem mais de 2 orientações no projeto. Cada membro docente pode orientar APENAS 2 monitores POR PROJETO.
	 *  
	 * @param equipeDocente docente que só pode ter 2 orientações por projeto. 
	 * @param lista lista de erros reportados.
	 * @throws DAOException busca das orientações do discente.
	 */
	public static void validaMaximoOrientacoesDocentes(EquipeDocente equipeDocente, Date dataInicio, Date dataFim,  ListaMensagens lista)throws DAOException {

		OrientacaoDao dao = DAOFactory.getInstance().getDAO(OrientacaoDao.class);
		int orientacoesAtivas = dao.findOrienatacoesAtivasByEquipeDocente(equipeDocente, dataInicio, dataFim );
		int maxOrientacoesAtivas = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.MAXIMO_ORIENTACOES_ATIVAS);
		try {
			// @negocio: Um orientador so pode ter 2 monitores
			if (orientacoesAtivas > maxOrientacoesAtivas) {
				lista.addWarning("Cada docente pode orientar no máximo " + maxOrientacoesAtivas +" monitores por projeto.<br/>" + equipeDocente.getServidor().getSiapeNome() + "  possui agora mais de 2 orientandos neste projeto.");
			}

		} finally {
			dao.close();
		}

	}


	/**
	 * Valida o número máximo de projetos. Cada Docente pode participar de até 2 projeto ativos contando o ano atual e o ano anterior.
	 * 
	 * @param equipeDocente docente que só pode estar em até de 2 projetos ativos.
	 * @param apartirDoAno ano atual.
	 * @param lista lista de erros reportados.
	 * @throws DAOException busca dos projetos onde o docente participa.
	 */
	public static void validaMaximoProjetosDocente(EquipeDocente equipeDocente, ListaMensagens lista)throws DAOException {

		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
		    
			int totalProjetosAtivos = dao.countOutrosProjetosAtivos(equipeDocente);
			int maxProjetosAtivos;			
			
			//valida max de coordenações para um docente
			if(equipeDocente.isCoordenador() && equipeDocente.getProjetoEnsino().getEditalMonitoria() != null){
				maxProjetosAtivos = equipeDocente.getProjetoEnsino().getEditalMonitoria().getEdital().getRestricaoCoordenacao().getMaxCoordenacoesAtivas();
			}else
				maxProjetosAtivos = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.MAXIMO_PROJETOS_ATIVOS);
			
			if (totalProjetosAtivos >= maxProjetosAtivos) {
				lista.addErro(equipeDocente.getServidor().getSiapeNome() 
						+ " já é docente ativo em "+ maxProjetosAtivos +" projeto(s) de monitoria portanto, não pode ser cadadastrado em outro projeto.");
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida a inscrição do discente em um projeto de monitoria
	 * 1. Verifica se é discente de graduação
	 * 2. Verifica se o discente é concluinte ou está afastado da instituição
	 * 3. Verifica se o discente já se inscreveu para a prova selecionada
	 * 
	 * @param discente discente que será validado se pode realizar a inscrição na prova seletiva.
	 * @param lista lista de erros reportados
	 * @param prova prova onde o aluno fará a inscrição
	 * @throws ArqException pode ser gerado na verificação das capacidades técnicas do discente.
	 */
	public static void validaInscricaoSelecao(DiscenteAdapter discente,  ProvaSelecao prova,  ListaMensagens lista) throws ArqException {

		// @negocio: Somente discentes da graduação podem ser monitores.
		validaDiscenteGraduacao(discente, lista);
		
		// @negocio: Discentes concluintes ou com algum tipo de afastamento não podem ser monitores. 
		validaDiscenteConcluinteOuComAfastamento(discente, lista);

		// Verificando se o discente já se inscreveu para a seleção deste projeto.
		InscricaoSelecaoMonitoriaDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoMonitoriaDao.class);

		try {

			InscricaoSelecaoMonitoria inscricao = dao.findByDiscenteProjeto(discente, prova);
			if (inscricao != null && inscricao.getId() != 0) {
				lista.addErro("Você já realizou a inscrição para este projeto de monitoria portanto não pode realizar novamente.");
			}

			// Verifica se o aluno tem capacidade técnica de participar da seleção
			validaCapacidadeTecnica(discente, prova, lista);

		} finally {
			dao.close();
		}

	}

	/**
	 * Valida a capacidade técnica do discente candidato a bolsa de monitoria do projeto.
	 * Verifica se o discente já cursou as disciplinas obrigatórias do projeto e se o 
	 * projeto não possui disciplinas obrigatórias, verifica se o aluno tem média igual 
	 * ou superior a 7 em pelo menos uma delas.
	 * 
	 * @param discente discente que será validado.
	 * @param prova prova seletiva na qual o discente está tentando se inscrever.
	 * @param lista lista de erros reportados.
	 * @throws ArqException pode ser gerada nas buscas ao banco de dados.
	 */
	public static void validaCapacidadeTecnicaDiscente(DiscenteAdapter discente,  ProvaSelecao prova,  ListaMensagens lista) throws ArqException {

		ComponenteCurricularMonitoriaDao dao = DAOFactory.getInstance().getDAO(ComponenteCurricularMonitoriaDao.class);

		try {
			//Atualiza a prova
			prova = dao.findByPrimaryKey(prova.getId(), ProvaSelecao.class);
			prova.getComponentesObrigatorios().iterator();

			// Componentes novos não tem turmas ainda, assim, qualquer discente pode ser monitor destes componentes
			int situacoesTurmasValidas[] = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_SITUACOES_TURMAS);
			boolean provaPossuiAlgumComponenteNovo = dao.isProvaPossuiAlgumComponenteSemTurmaBySituacao(prova.getId(), situacoesTurmasValidas);

			// Componentes Curriculares marcados pelos docentes como Obrigatórios para prova
			int qtdObrigatorias = dao.findQtdComponentesMonitoriaObrigatoriasByProva(prova.getId(), true);
			int qtdNaoObrigatorias = dao.findQtdComponentesMonitoriaObrigatoriasByProva(prova.getId(), false);

			// Se há disciplinas obrigatórias marcadas na prova, o discente deve ter pago todas com média >= NotaMinimaAprovacaoSelecaoMonitora 
			// Se NÃO existem disciplinas obrigatórias marcadas na prova, o discente pode ter pago qualquer componente com média >= NotaMinimaAprovacaoSelecaoMonitora   
			Collection<ComponenteCurricular> disciplinasNaoCursadas = dao.findDisciplinasProvaNaoCursadas(prova, discente);

			int qtdNaoCursadas = 0;			
			if (!ValidatorUtil.isEmpty(disciplinasNaoCursadas)) {				
				qtdNaoCursadas = disciplinasNaoCursadas.size();
			}

			//@negocio: Se a prova possuir componentes novos (componentes que nunca tiveram turmas), qualquer discente pode ser monitor.
			boolean apto = provaPossuiAlgumComponenteNovo;

			//@negocio: Se a prova possuir componentes obrigatórios o discente deve ter pago todos com média apropriada.
			if (!apto && (qtdObrigatorias > 0) && (qtdNaoCursadas == 0)) {
				apto = true;
			}

			//@negocio:  Se a prova não possuir componentes obrigatórios o discente deve ter pago pelo menos 1 dos opcionais.
			if (!apto && (qtdObrigatorias == 0) && (qtdNaoObrigatorias > qtdNaoCursadas)) {
				apto = true;
			}
			
			if (!apto) {
				String msg = discente.getMatriculaNome() + " não cursou ou não foi aprovado(a) com média igual ou superior a " 
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
	 * Valida a capacidade técnica do discente candidato a bolsa de monitoria do projeto.
	 * Verifica se o discente já cursou as disciplinas obrigatórias do projeto e se o 
	 * projeto não possui disciplinas obrigatórias, verifica se o aluno tem média igual 
	 * ou superior a 7 em pelo menos uma delas.
	 * 
	 * @param discente discente que será validado.
	 * @param prova prova seletiva na qual o discente está tentando se inscrever.
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
			
			//Analisando pré-requisitos para prova.
			Collection<ComponenteCurricular> componentesConcluidos  = dao.findComponentesCurricularesPagos(discente, prova);
			boolean apto = false;
			if (!ValidatorUtil.isEmpty(componentesConcluidos)) {
				apto = ExpressaoUtil.evalComTransitividade(prova.getExpressaoPreRequisitosInscricaoDiscente(), discente.getId(), componentesConcluidos);
			}
			
			if (!apto) {
				String msg = discente.getMatriculaNome() + "  não cursou ou não foi aprovado(a) com média  igual ou superior a " 
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
	 * Valida se o calendário de monitoria permite novos bolsistas
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
				lista.addErro("Período de confirmação da bolsa ainda não foi definido pela Pró-Reitoria de Graduação.");

			}else if (!cm.isPeriodoConfirmacaoMonitoriaEmAberto()) {
				SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Atualmente, a efetivação de monitores em projetos está autorizada no período de " + sp.format(cm.getInicioConfirmacaoMonitoria()) +" até " + sp.format(cm.getFimConfirmacaoMonitoria()) + ".");
				lista.addErro("Para maiores informações entre em contato com a Pró-Reitoria de Graduação.");
			}
		} finally {
			dao.close();
		}	
	}

	/**
	 * Valida os principais dados do cadastro do discente em um projeto de monitoria.
	 * Incluindo Data de início e fim do discente no projeto.
	 * 
	 * @param dm
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaDadosPrincipais(DiscenteMonitoria dm, ListaMensagens lista) throws ArqException{
	    lista.addAll(dm.validate());
	    ValidatorUtil.validaData(Formatador.getInstance().formatarData(dm.getDataInicio()), "Data início", lista);
	    ValidatorUtil.validaData(Formatador.getInstance().formatarData(dm.getDataFim()), "Data fim", lista);
	}
	
	/**
	 * Realiza a validação de discente, verificando todos os requisitos necessários para assumir a bolsa.
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
