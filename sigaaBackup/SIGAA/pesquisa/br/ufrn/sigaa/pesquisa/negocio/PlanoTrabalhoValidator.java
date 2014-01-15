/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validaData;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LimiteCotaExcepcional;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;

/**
 * Classe para validações do plano de trabalho
 * 
 * @author ricardo
 * 
 */
public class PlanoTrabalhoValidator {

	/** Retorna o Dao, para que não seja necessário instanciar um novo Dao. */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
	/**
	 * Valida as informações principais do plano de trabalho 
	 * que constam na primeira tela do cadastro.
	 * 
	 * @param planoTrabalho
	 * @param lista
	 */
	public static void validarDadosGerais(PlanoTrabalho planoTrabalho,
			PlanoTrabalhoForm pForm, boolean gestor,
			ListaMensagens lista) throws DAOException {
		
		// validação dos campos obrigatórios
		if (!(planoTrabalho.getOrientador() == null || planoTrabalho.getExterno() == null)) {
			if (planoTrabalho.getOrientador().getId() == 0 || planoTrabalho.getOrientador() == null) 
				planoTrabalho.setOrientador(new Servidor());
				
			if (planoTrabalho.getExterno().getId() == 0 || planoTrabalho.getExterno() == null)
				planoTrabalho.setExterno(new DocenteExterno());
				
			if (planoTrabalho.getExterno().getId() == 0 && planoTrabalho.getOrientador().getId() == 0) {
				validateRequired(null, "Orientador", lista);
			}
		}
		
		validateRequired(planoTrabalho.getTitulo(), "Título", lista);
		if(!gestor)
		    validateRequired(planoTrabalho.getIntroducaoJustificativa(), "Introdução e Justificativa", lista);
		validateRequired(planoTrabalho.getObjetivos(), "Objetivos", lista);
		validateRequired(planoTrabalho.getMetodologia(), "Metodologia", lista);
		String tipoPlanoTrabalho = pForm != null ? pForm.isCadastroVoluntario() ? "do Vínculo" : "da Bolsa" : "";
		validateRequiredId(planoTrabalho.getTipoBolsa().getId(), "Tipo " + tipoPlanoTrabalho , lista);
		validateRequired(planoTrabalho.getReferencias(), "Referências", lista);
		
		validateRequiredId(planoTrabalho.getBolsaDesejada(), "Tipo de Bolsa que deseja concorrer", lista);
		
		if(planoTrabalho.getIntroducaoJustificativa() != null && planoTrabalho.getIntroducaoJustificativa().trim().length() > 10000)
			lista.addErro("A introdução e justificativa deve possuir no máximo 10.000 caracteres.");
		if(planoTrabalho.getObjetivos() != null && planoTrabalho.getObjetivos().trim().length() > 10000)
			lista.addErro("Os objetivos devem possuir no máximo 10.000 caracteres.");
		if(planoTrabalho.getMetodologia() != null && planoTrabalho.getMetodologia().trim().length() > 10000)
			lista.addErro("A metodologia deve possuir no máximo 10.000 caracteres.");
		
		if(!gestor) {
    		// validação se o título do plano de trabalho é o mesmo título do projeto
    		if(StringUtils.toAscii(planoTrabalho.getTitulo().trim()).equalsIgnoreCase(
    				StringUtils.toAscii(planoTrabalho.getProjetoPesquisa().getTitulo().trim()))){
    			lista.addErro("O título do plano de trabalho não pode ser o mesmo do projeto de pesquisa no qual está inserido");
    		}
		}
		
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class);
		try {
		    if(!gestor) {
    			// validação se há planos de trabalhos duplicados dentro do mesmo projeto
    			Collection<PlanoTrabalho> planos = dao.findByProjeto(planoTrabalho.getProjetoPesquisa());
    			for(PlanoTrabalho p: planos){
    				if(p.getId() != planoTrabalho.getId()){
    					if( p.getStatus() != TipoStatusPlanoTrabalho.EXCLUIDO && checkIgualdadeConteudo(planoTrabalho, p) )
    						lista.addErro("Não pode haver planos de trabalho iguais dentro do mesmo projeto de pesquisa");
    				}
    			}
		    }
		    
			// validações dependentes do Form Bean, caso não passado, ignoradas
			if (pForm != null) {
				if(pForm.isSolicitacaoCota()){
					if(pForm.isPermissaoGestor())
						validateRequiredId(planoTrabalho.getCota().getId(), "É necessário informar a cota (período) da bolsa", lista);
					if(pForm.isCadastroVoluntario())
						validateRequiredId(planoTrabalho.getCota().getId(), "É necessário informar uma Cota.", lista);
					else
						validateRequiredId(planoTrabalho.getEdital().getId(), "É necessário informar o edital para o qual a cota está sendo solicitada", lista);
				}else{
						planoTrabalho.setDataInicio(validaData(pForm.getDataInicio(), "Data de Início do Plano de Trabalho", lista));
						planoTrabalho.setDataFim(validaData(pForm.getDataFim(),	"Data de Fim do Plano de Trabalho", lista));
				}
			}
		
		} finally {
			dao.close();
		}
		
	}

	/**
	 * Verifica se o conteúdo de dois planos de trabalho é o mesmo
	 * 
	 * @param planoTrabalho
	 * @param p
	 * @return
	 */
	private static boolean checkIgualdadeConteudo(PlanoTrabalho planoTrabalho,
			PlanoTrabalho p) {
		return StringUtils.toAscii(planoTrabalho.getTitulo().trim()).equalsIgnoreCase(StringUtils.toAscii(p.getTitulo().trim()))
				|| ( p.getIntroducaoJustificativa() != null && StringUtils.toAscii(planoTrabalho.getIntroducaoJustificativa().trim()).equalsIgnoreCase(StringUtils.toAscii(p.getIntroducaoJustificativa().trim()))) 
				|| StringUtils.toAscii(planoTrabalho.getObjetivos().trim()).equalsIgnoreCase(StringUtils.toAscii(p.getObjetivos().trim()));
	}


	/**
	 * Valida diversas restrições dos parâmetros do módulo de pesquisa 
	 * e do edital para o qual se está submetendo o plano de trabalho,
	 * se for o caso.
	 * 
	 * @param planoTrabalho
	 * @param lista
	 * @throws DAOException
	 */
	public static void validarRestricoes(PlanoTrabalho planoTrabalho,
			Usuario usuario, ListaMensagens lista)
			throws DAOException {
		ParametroHelper pHelper = ParametroHelper.getInstance();
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class);
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
		ColaboradorVoluntarioDao colaboradorDao = getDAO(ColaboradorVoluntarioDao.class);

		try {
			// Se a bolsa do plano de trabalho em questão está vinculada a uma cota			
			if (planoTrabalho.getTipoBolsa() != null && planoTrabalho.getTipoBolsa().isVinculadoCota()) {
				// Verifica as restrições do Edital
				if(planoTrabalho.getEdital() != null && planoTrabalho.getEdital().getId() > 0) {
					EditalPesquisa edital = planoDao.findByPrimaryKey(planoTrabalho.getEdital().getId(), EditalPesquisa.class);
					
					Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
					if (planoTrabalho.getStatus() != TipoStatusPlanoTrabalho.AGUARDANDO_RESUBMISSAO 
					        && planoTrabalho.getStatus() != TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES 
					        && planoTrabalho.getStatus() != TipoStatusPlanoTrabalho.NAO_APROVADO 
							&& !usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA)
							&& (hoje.before(edital.getInicioSubmissao()) || hoje
									.after(edital.getFimSubmissao()))) {
						lista.addErro("O período de submissões do edital não está mais vigente.");
						return;
					}
					// Buscar parâmetros
					LimiteCotaExcepcional limiteCotaExcepcional = null;
					long totalCotasOrientador = 0;
					if (planoTrabalho.getOrientador() != null){ 
						 limiteCotaExcepcional = planoDao.findLimiteCotaExcepcionalByServidor(planoTrabalho.getOrientador());	
						 totalCotasOrientador = planoDao.findTotalSolicitacoesByMembro(planoTrabalho.getOrientador(), edital);	
					}
					
					int limiteCotasOrientador = pHelper.getParametroInt(ConstantesParametro.LIMITE_COTAS_ORIENTADOR);
					int limiteCotasOrientadorExterno = pHelper.getParametroInt(ConstantesParametro.LIMITE_COTAS_ORIENTADOR);
					int limiteCotasProjeto = pHelper.getParametroInt(ConstantesParametro.LIMITE_COTAS_PROJETO);
					// Buscar total de cotas solicitadas
					long totalCotasProjeto = planoDao.findTotalSolicitacoesByProjeto(planoTrabalho.getProjetoPesquisa(), edital);
					
					if(limiteCotaExcepcional == null){
						// Comparar os totais com os limites
						if (planoTrabalho.getId() <= 0
								&& totalCotasOrientador >= limiteCotasOrientador) {
							lista.addErro("Este orientador já atingiu o limite estabelecido de "
											+ limiteCotasOrientador
											+ " cotas por docente para o edital: <br />"
											+ edital.getDescricao());
						}
						if (planoTrabalho.getId() <= 0
								&& totalCotasProjeto >= limiteCotasProjeto) {
							lista.addErro("Este projeto de pesquisa já atingiu o limite estabelecido de "
											+ limiteCotasProjeto
											+ " cotas por projeto para o edital: <br />"
											+ edital.getDescricao());
						}
					} else {
						// Verificar apenas o limite de cotas excepcional
						if(planoTrabalho.getId() <= 0
								&& totalCotasOrientador >= limiteCotaExcepcional.getLimite()){
							lista.addErro("Este orientador já atingiu o limite de cotas excepcional de "
									+ limiteCotaExcepcional.getLimite()
									+ " cotas.");
						}
					}

					if (planoTrabalho.getOrientador() != null && planoTrabalho.getOrientador().getId() != 0){
					// Verificar se a informação da titulação do orientador está registrada no sistema.
						if (edital.getTitulacaoMinimaCotas() != EditalPesquisa.SEM_RESTRICAO) {
							if(planoTrabalho.getOrientador().getFormacao() == null
									|| (planoTrabalho.getOrientador().getFormacao() != null 
											&& planoTrabalho.getOrientador().getFormacao().getId() >= Formacao.FORMACAO_PADRAO)){
								lista.addErro("A titulação do orientador não está registrada no sistema. " +
										"Por favor, procure o DAP para regularizar a situação e em seguida tente novamente.");
							}
	
							// Validar titulação mínima para solicitação de cotas, definida no edital
							if (planoTrabalho.getOrientador().getFormacao() != null && planoTrabalho.getOrientador().getFormacao().getId() < edital.getTitulacaoMinimaCotas()) {
								Formacao formacao = planoDao.findByPrimaryKey(edital.getTitulacaoMinimaCotas(), Formacao.class);
								lista.addErro("O orientador selecionado não possui a titulação mínima, definida no edital para a solicitação de cotas como "
														+ formacao.getDenominacao());
							}
						}
						// Validações específicas do edital para voluntários
						if(edital.getVoluntario() != null && edital.getVoluntario()){

							long totalVoluntariosOrientador = planoDao.findTotalSolicitacoesByMembro(planoTrabalho.getOrientador(), edital, true);
							int limiteVoluntariosOrientador = 1;
							
							if(planoTrabalho.getOrientador().getFormacao() != null && !colaboradorDao.isColaboradorVoluntario(planoTrabalho.getOrientador())){
								if (planoTrabalho.getOrientador().getFormacao().getId() == Formacao.DOUTOR) {
									if(orientacaoDao.findTotalOrientandosAtivosNivel(planoTrabalho.getOrientador(), NivelEnsino.DOUTORADO) > 0)
										limiteVoluntariosOrientador = 4;
									else if(orientacaoDao.findTotalOrientandosAtivosNivel(planoTrabalho.getOrientador(), NivelEnsino.MESTRADO) > 0)
										limiteVoluntariosOrientador = 2;
								} 
							}
							
							if (planoTrabalho.getId() <= 0
									&& totalVoluntariosOrientador >= limiteVoluntariosOrientador) {
								lista.addErro("Este orientador já atingiu o limite estabelecido de "
												+ limiteVoluntariosOrientador
												+ " planos voluntários por docente definido no edital: <br />"
												+ edital.getDescricao());
							}
						}
					}
					if (planoTrabalho.getExterno().getId() != 0) {
						if (planoTrabalho.getExterno().getFormacao() == null ||
								planoTrabalho.getExterno().getFormacao() != null 
								&& planoTrabalho.getExterno().getFormacao().getId() >= Formacao.FORMACAO_PADRAO) 
							lista.addErro("A titulação do orientador não está registrada no sistema. " +
									"Por favor, procure o DAP para regularizar a situação e em seguida tente novamente.");
						
						if (planoTrabalho.getExterno().getFormacao() != null && planoTrabalho.getExterno().getFormacao().getId() < edital.getTitulacaoMinimaCotas()) {
							Formacao formacao = planoDao.findByPrimaryKey(edital.getTitulacaoMinimaCotas(), Formacao.class);
							lista.addErro("O orientador selecionado não possui a titulação mínima, definida no edital para a solicitação de cotas como "
									+ formacao.getDenominacao());
						}
						
						if ( usuario.getVinculoAtivo().getDocenteExterno() != null 
									&& !usuario.getVinculoAtivo().getDocenteExterno().isColaboradorVoluntario() ) {
							lista.addErro("Apenas colaborador voluntário pode solicitar cota de bolsa.");
						}
						
						int totalPlanos = planoDao.findTotalPlanosDocenteExterno(planoTrabalho.getExterno(), planoTrabalho.getEdital()); 
						if ( totalPlanos >= limiteCotasOrientadorExterno ) {
							lista.addErro("Um docente externo pode apenas solicitar " + limiteCotasOrientador + " cotas de bolsa.");
						}
					}
					
					/**
					 * Verificando se o servidor selecionado pode atuar como orientador do plano de trabalho em questão.
					 */
					if( planoTrabalho.getEdital().isApenasCoordenadorOrientaPlano() && planoTrabalho.getOrientador() != null &&  
							planoTrabalho.getOrientador().getId() != planoTrabalho.getProjetoPesquisa().getCoordenador().getId() ){
						lista.addErro("Apenas o coordenador do projeto pode orientar planos de trabalhos vinculados a este edital.");
					}

					if (planoTrabalho.getOrientador() == null) {
						lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Orientador");
						planoTrabalho.setOrientador(new Servidor());
					}
					
				}
			} else { // Se a bolsa não está vinculada a cota
				// Verifica apenas se o período definido para o plano de trabalho 
				// está dentro do período do projeto
				ProjetoPesquisa projeto = planoTrabalho.getProjetoPesquisa();
				if( (planoTrabalho.getDataInicio() != null && planoTrabalho.getDataFim() != null)
						&& (planoTrabalho.getDataInicio().before(projeto.getDataInicio()) ||
						planoTrabalho.getDataFim().after(projeto.getDataFim()))){
					Formatador f = Formatador.getInstance();
					lista.addErro("O período do plano de trabalho deve estar " +
							"compreendido no período do projeto de pesquisa a ele associado: "
							+ f.formatarData(projeto.getDataInicio()) + " a " + f.formatarData(projeto.getDataFim()));
				}
			}

		} finally {
			planoDao.close();
			orientacaoDao.close();
			colaboradorDao.close();
		}
	}

	/**
	 * Valida a quantidade de bolsistas definida no edital
	 * 
	 * @param planoTrabalho
	 * @param lista
	 */
	public static void validarNumeroDeBolsistas(PlanoTrabalho planoTrabalho, ArrayList<MensagemAviso> lista) throws DAOException {
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class);
		PlanoTrabalho planoBD = (planoTrabalho.getId() > 0 ? planoDao.findByPrimaryKey(planoTrabalho.getId(), PlanoTrabalho.class) : null);
		EditalPesquisa edital = new EditalPesquisa();
		if(planoTrabalho.getEdital() != null){
			edital = planoDao.findByPrimaryKey(planoTrabalho.getEdital().getId(), EditalPesquisa.class);
			planoTrabalho.setEdital(edital);
		}else if(planoBD != null && planoBD.getEdital() != null){
			edital = planoDao.findByPrimaryKey(planoBD.getEdital().getId(), EditalPesquisa.class);
			planoTrabalho.setEdital(edital);
		} 
		
		// Essa validação só vale para bolsas distribuídas através de um edital
		if(planoTrabalho.getEdital() != null && planoTrabalho.getEdital().isDistribuicaoCotas() 
				&& planoTrabalho.getTipoBolsa().getId() != TipoBolsaPesquisa.A_DEFINIR
				&& planoTrabalho.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO){
			try {
				TipoBolsaPesquisa tipoBolsa = planoDao.findByPrimaryKey(planoTrabalho.getTipoBolsa().getId(), TipoBolsaPesquisa.class);
				long bolsasDistribuidas = planoDao.getNumeroBolsasDistribuidasEdital(planoTrabalho.getTipoBolsa().getId(), planoTrabalho.getEdital().getId());
				long bolsasEdital = 0; 
				for(Cotas c: edital.getCotas()){
					if(c.getTipoBolsa().getId() == planoTrabalho.getTipoBolsa().getId())
						bolsasEdital += c.getQuantidade();
				}
				
				
				// Se estiver cadastrando um novo plano OU alterando o tipo de bolsa de um plano existente
				if( planoBD == null || ( planoBD != null && planoBD.getTipoBolsa().getId() != planoTrabalho.getTipoBolsa().getId() ) ){
					if(++bolsasDistribuidas > bolsasEdital){
						lista.add(new MensagemAviso("Não é possível registrar esse plano com bolsa do tipo "+ 
								tipoBolsa.getDescricaoResumida() + 
								" pois a quantidade definida no edital " +
								edital.getDescricao() +
								" foi excedida.", TipoMensagemUFRN.ERROR));
					}
				}
				
				
			} finally {
				planoDao.close();
			}
		}
			
	}

	/**
	 * Valida o cronograma do plano de trabalho que consta na segunda tela do cadastro.
	 * 
	 * @param planoTrabalho
	 * @param lista
	 */
	public static void validarCronograma(PlanoTrabalho planoTrabalho,
			ListaMensagens lista) {
		ValidatorUtil.validateEmptyCollection(
				"Informe ao menos uma atividade para o cronograma",
				planoTrabalho.getCronogramas(), lista);

		for (CronogramaProjeto crono : planoTrabalho.getCronogramas()) {
			if (crono.getDescricao().trim().length() > 300) {
				lista.addErro("A descricao da atividade <i>"
						+ crono.getDescricao()
						+ "<i> deve conter no máximo 300 caracteres.");
			}
		}
	}

}