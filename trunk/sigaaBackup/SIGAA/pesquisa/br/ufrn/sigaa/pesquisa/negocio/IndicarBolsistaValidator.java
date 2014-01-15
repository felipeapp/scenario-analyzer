/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CotaDocenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.TipoConta;

/**
 * Valida a Indicação de um bolsista, verificando os dados e regras de negócio envolvidas na aceitação do bolsista. Como por exemplo 
 * a verificação se o aluno indicado à bolsa já está vinculado a outra bolsa.
 * 
 * @author Victor Hugo
 * @author Ricardo Wendell
 * @author Leonardo Campos
 *
 */
public class IndicarBolsistaValidator {

	/**
	 * Método invocado para realizar a validação do discente
	 * 
	 * @param plano
	 * @param planoForm
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaIndicacao(PlanoTrabalho plano, PlanoTrabalhoForm planoForm, ListaMensagens lista) throws ArqException{

		ValidatorUtil.validateRequired(plano.getMembroProjetoDiscente().getId(), "É necessário selecionar um discente para a indicação", lista);

		if( planoForm != null ){

			if ( planoForm.getBolsistaAtual() != null ){
				planoForm.getBolsistaAtual().setDataFim( ValidatorUtil.validaData(planoForm.getDataFinalizacao(), "Data da Finalização", lista) );
				ValidatorUtil.validateRequired(planoForm.getBolsistaAtual().getMotivoSubstituicao(), "Motivo da Substituição", lista);
			}
			planoForm.getObj().getMembroProjetoDiscente().setDataInicio( ValidatorUtil.validaData(planoForm.getDataIndicacao(), "Data da Indicação", lista) );
		}

		PlanoTrabalhoDao planoDao = new PlanoTrabalhoDao();
		CotaDocenteDao cotaDocenteDao = new CotaDocenteDao();
		ParametrosGestoraAcademicaDao paramDao = new ParametrosGestoraAcademicaDao();
		try {
			PlanoTrabalho planoTemp = planoDao.findByPrimaryKey( plano.getId(), PlanoTrabalho.class );

			if( planoTemp != null && planoTemp.getMembroProjetoDiscente() != null ){
				if( planoTemp.getMembroProjetoDiscente().getDiscente().getId() == plano.getMembroProjetoDiscente().getDiscente().getId() ){
					lista.addErro("A substituição só deve ser efetuada no caso da troca de bolsistas");
				}
			}

			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();

			// Verificar discente
			if (plano.getMembroProjetoDiscente().getDiscente().getId() <= 0) {
				lista.addErro("É necessário informar um discente para realizar a indicação");
			} else {
				Discente discente = planoDao.findByPrimaryKey(plano.getMembroProjetoDiscente().getDiscente().getId(), Discente.class);
				if(discente.getAnoIngresso() != null && discente.getPeriodoIngresso() != null){
					if(discente.getAnoIngresso() > cal.getAno()){
						lista.addErro("O discente não pode ser indicado pois seu ano/semestre de entrada é posterior ao atual.");
					} else if(discente.getAnoIngresso() == cal.getAno() && discente.getPeriodoIngresso() > cal.getPeriodo()){
						lista.addErro("O discente não pode ser indicado pois seu ano/semestre de entrada é posterior ao atual.");
					}
				}

				if (planoForm != null && planoForm.getTipoBolsa() != TipoBolsaPesquisa.VOLUNTARIO
						&& planoForm.getTipoBolsa() != TipoBolsaPesquisa.VOLUNTARIO_IT) {

					// verifica se o discente está no cadastro único
					if (planoForm != null && planoTemp != null && planoTemp.getMembroProjetoDiscente() == null
							&& planoTemp.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO
							&& planoTemp.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO_IT) {
						if (planoForm.getDiscentesAdesao() == null
								|| planoForm.getDiscentesAdesao().isEmpty()) {
							lista.addErro("Nenhum discente registrou interesse em participar.");
						} else {
							if (!verificaAdesao(discente, planoForm)) {
								lista.addErro("O discente: " + discente.getNome() + " não registrou interesse.");
							}
						}
					}
				}

				String lattes = PerfilPessoaDAO.getDao().findLattesByDiscente(discente.getId());

				if (isEmpty(lattes)){
					lista.addErro("É obrigatório constar o endereço do Currículo Lattes do CNPq no Perfil deste discente " +
							"(Portal do Discente -> Atualizar Foto e Perfil). <br/> Caso o discente não possua ainda um currículo cadastrado, ele deve " +
							"acessar a Plataforma Lattes no endereço http://lattes.cnpq.br/ e, após efetuar o cadastro, atualizar seu Perfil no SIGAA com " +
							"o endereço do seu currículo.");
				}

				//Verifica se o nível do discente é permitido pela bolsa
				if (plano.getTipoBolsa().getNiveisPermitidos() != null && !plano.getTipoBolsa().getNiveisPermitidos().contains(String.valueOf(discente.getNivel()))){
					lista.addErro("Não é possível indicar este discente pois o nível do mesmo difere do nível exigido por esta bolsa.");
				}

				//Verifica se o discente possui a média geral (IRA) mínimo necessário (definido no edital) para assumir a bolsa.
				if( !ValidatorUtil.isEmpty(planoTemp.getEdital()) && planoTemp.getEdital().getIndiceChecagem() != null ){
					IndiceAcademicoDiscente indiceDiscente = discente.getIndice( planoTemp.getEdital().getIndiceChecagem().getId() );
					if( planoTemp.getEdital().getValorMinimoIndiceChecagem() > indiceDiscente.getValor() )
						lista.addErro("Não é possível indicar este discente pois o " + indiceDiscente.getIndice().getNome() 
								+ " do discente (" + indiceDiscente.getValor() + ") é inferior ao mínimo exigido no edital (" + planoTemp.getEdital().getValorMinimoIndiceChecagem() + ").");
				}

				// Verificar se o discente possui todos os dados atualizados 
				Collection<String> dadosIncompletos = new ArrayList<String>();
				// CPF
				if (discente.getPessoa().getCpf_cnpj() == null || discente.getPessoa().getCpf_cnpj() == 0) {
					dadosIncompletos.add("CPF");
				}
				// Identidade
				if (discente.getPessoa().getIdentidade() == null || StringUtils.isEmpty( discente.getPessoa().getIdentidade().getNumero() )) {
					dadosIncompletos.add("Identidade");
				}
				// E-mail
				if ( StringUtils.isEmpty(discente.getPessoa().getEmail()) ) {
					dadosIncompletos.add("E-mail");
				}
				if (!dadosIncompletos.isEmpty()) {
					lista.addMensagem(MensagensPesquisa.INDICAR_BOLSISTA_DADOS_DESATUALIZADOS, dadosIncompletos);
				}
			}

			// Verificar se o discente já está associado ativamente a um outros plano de trabalho
			PlanoTrabalho planoTrabalho = planoDao.findByDiscenteAtivo( plano.getMembroProjetoDiscente().getDiscente().getId() );
			if( planoTrabalho != null ){
				lista.addErro("O discente informado já está associado a um outro plano de trabalho em andamento <br>" +
						"de título \"" + planoTrabalho.getTitulo() + "\"<br>" +
						"orientado pelo(a) professor(a) " + planoTrabalho.getOrientador().getPessoa().getNome() + ".<br>" +
						"Para ser indicado para um novo plano, o orientador do aluno precisa antes finalizá-lo no plano anterior, " +
						"não esquecendo de que o aluno precisa ter enviado o relatório final e o parecer do orientador deve ser dado antes da finalização.");
			}

			// Verifica se o discente informado já apresenta bolsa (remunerada) ativa cadastrada no sipac, em extensão e monitoria.
			if (plano.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO
					&& plano.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO_IT
					&& plano.getTipoBolsa().getId() != TipoBolsaPesquisa.A_DEFINIR) {
				Discente discente = planoDao.findByPrimaryKey(plano.getMembroProjetoDiscente().getDiscente().getId(), Discente.class);
				if ( discente != null ) {
					if (Sistema.isSipacAtivo()) {
						int bolsa = IntegracaoBolsas.verificarCadastroBolsaSIPAC( discente.getMatricula(), null );

						if ( bolsa > 0 ) 
							lista.addErro("O discente " + discente.getPessoa().getNome() +" já apresenta bolsa cadastrada no " 
									+ RepositorioDadosInstitucionais.get("siglaSipac") + ".");
					}

					boolean bolsistaExtensao = IntegracaoBolsas.verificarIndicacaoExtensao(discente.getMatricula());
					boolean bolsistaMonitoria = IntegracaoBolsas.verificarIndicacaoMonitoria(discente.getMatricula());

					if ( bolsistaExtensao )
						lista.addErro("O discente " + discente.getPessoa().getNome() + " já apresenta bolsa cadastrada em um projeto de extensão.");
					if ( bolsistaMonitoria )
						lista.addErro("O discente " + discente.getPessoa().getNome() + " já apresenta bolsa cadastrada em um projeto de monitoria.");
				} else {
					lista.addErro("Discente não encontrado ou não compatível com o tipo de bolsa selecionada.");
				}
			}


			if (plano.getTipoBolsa().isVinculadoCota() && plano.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO) {
				if (planoForm != null && planoForm.getTipoBolsa() != TipoBolsaPesquisa.VOLUNTARIO
						&& planoForm.getTipoBolsa() != TipoBolsaPesquisa.VOLUNTARIO_IT) {
					ValidatorUtil.validateRequiredId(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getBanco().getId(), "Banco", lista);
					ValidatorUtil.validateRequired(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getAgencia(), "Agência", lista);
					ValidatorUtil.validateRequired(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getNumero(), "Conta", lista);
					ValidatorUtil.validateRequiredId(plano.getMembroProjetoDiscente().getTipoConta(), "Tipo Conta", lista);

					if (plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getNumero().contains("[@!#$%¨&*+´`^~;:?áÁéÉíÍóÓúÚãÃçÇ|?,./{}<>+=*¹²³£¢¬ªº°']\"") ) { 
						lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conta");
					}
					if (plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getAgencia().contains("[@!#$%¨&*+´`^~;:?áÁéÉíÍóÓúÚãÃçÇ|?,./{}<>+=*¹²³£¢¬ªº°']\"") ) { 
						lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Agência");
					}

				}
			}
			else if ( plano.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR ) {
				if(planoForm != null){
					TipoBolsaPesquisa tipoBolsaForm = planoDao.findByPrimaryKey(planoForm.getTipoBolsa(), TipoBolsaPesquisa.class);
					if(tipoBolsaForm.isVinculadoCota() && tipoBolsaForm.getId() != TipoBolsaPesquisa.VOLUNTARIO) {
						ValidatorUtil.validateRequiredId(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getBanco().getId(), "Banco", lista);
						ValidatorUtil.validateRequired(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getAgencia(), "Agência", lista);
						ValidatorUtil.validateRequired(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getNumero(), "Conta", lista);
						ValidatorUtil.validateRequiredId(plano.getMembroProjetoDiscente().getTipoConta(), "Tipo Conta", lista);
					}
				}
			}

			if( lista.isEmpty() && plano.getTipoBolsa().getEntidadeFinanciadora().getId() == ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.ID_ENTIDADE_FINANCIADORA_CNPQ) ){
				if( plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getBanco().getId() != Banco.BANCO_DO_BRASIL )
					lista.addMensagem(MensagensPesquisa.BOLSA_CNPQ_BB);
				if( plano.getMembroProjetoDiscente().getTipoConta() != TipoConta.CONTA_CORRENTE )
					lista.addMensagem(MensagensPesquisa.BOLSA_CNPQ_CONTA_CORRENTE);
			}

			if ( plano.getDataFim().before(new Date()) )
				lista.addErro("Não é possível realizar a indicação do discente pois o plano não está mais em vigência.");

			// No caso de definição do tipo de bolsa, verificar se o docente ainda possui cotas disponíveis
			// para o tipo selecionado
			if ( plano.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR ) {
				Collection<CotaDocente> cotasRecebidas = cotaDocenteDao.findByDocentePeriodoCota( plano.getOrientador() , plano.getCota()) ;

				// Se o docente tiver recebido cotas, validar a quantidade disponível para indicação
				if ( cotasRecebidas != null && !cotasRecebidas.isEmpty() ) {
					Map<Integer, Object[]> tiposBolsaRecebidas = new HashMap<Integer, Object[]>();

					for(CotaDocente cotaDocente: cotasRecebidas){
						if(cotaDocente.getQtdCotas() > 0){
							for(Cotas c: cotaDocente.getCotas()){

								int id = c.getTipoBolsa().getId();
								if(tiposBolsaRecebidas.containsKey(id))
									tiposBolsaRecebidas.put(id, new Object[]{(Integer)tiposBolsaRecebidas.get(id)[0] + c.getQuantidade(), c.getTipoBolsa().getDescricaoResumida()});
								else
									tiposBolsaRecebidas.put(id, new Object[]{c.getQuantidade(), c.getTipoBolsa().getDescricaoResumida()});

							}
						}
					}

					for(Integer idTipoBolsaRecebida: tiposBolsaRecebidas.keySet()) {
						// Verificar se o docente ainda tem bolsas disponíveis do tipo selecionado
						if(planoForm != null && planoForm.getTipoBolsa() == idTipoBolsaRecebida 
								&& (Integer)tiposBolsaRecebidas.get(idTipoBolsaRecebida)[0] <= planoDao.findByTipoBolsaOrientador(idTipoBolsaRecebida, plano.getOrientador().getId(), plano.getCota().getId()).size()){
							lista.addErro("Você não possui mais cotas "+ (String)tiposBolsaRecebidas.get(idTipoBolsaRecebida)[1] +" disponíveis para definição de novas bolsas.");
						}
					}

					if(planoForm != null){
						if(planoForm.getTipoBolsa() == TipoBolsaPesquisa.PIBIC || planoForm.getTipoBolsa() == TipoBolsaPesquisa.PIBIT){
							if(plano.getMembroProjetoDiscente().getDiscente().getPessoa().getContaBancaria().getBanco().getId() != Banco.BANCO_DO_BRASIL)
								lista.addMensagem(MensagensPesquisa.BOLSA_CNPQ_BB);
						}
					}
				}

			}
		} finally {
			planoDao.close();
			paramDao.close();
			cotaDocenteDao.close();
		}

		planoDao.close();

	}

	/**
	 * Verifica se o discente aderiu ao cadastro único de bolsas.
	 * @param discente
	 * @param planoForm
	 * @return
	 */
	private static boolean verificaAdesao(Discente discente,
			PlanoTrabalhoForm planoForm) {
		for(AdesaoCadastroUnicoBolsa a: planoForm.getDiscentesAdesao()){
			if(a.getDiscente().getId() == discente.getId())
				return true;
		}
		return false;
	}

	/**
	 * Verifica se um plano de trabalho pode ter seu bolsista removido
	 * 
	 * @param plano
	 * @param lista
	 * @throws DAOException
	 * @throws ParseException 
	 */
	public static void validaRemocao(PlanoTrabalho plano, PlanoTrabalhoForm planoForm, ListaMensagens lista) throws DAOException, ParseException{

		if( planoForm != null ){

			if ( planoForm.getBolsistaAtual() != null ){
				planoForm.getBolsistaAtual().setDataFim( ValidatorUtil.validaData(planoForm.getDataFinalizacao(), "Data da Finalização", lista) );
				ValidatorUtil.validateRequired(planoForm.getMotivo(), "Motivo da Finalização", lista);

				if (!lista.isErrorPresent()) {
					
					if (planoForm.getMotivo().equals("OUTROS") && planoForm.getBolsistaAtual().getMotivoSubstituicao().equals("")) 
						lista.addErro("Especifique o motivo para a Finalização.");

					if (CalendarUtils.compareTo(CalendarUtils.parseDate(planoForm.getDataInicio(), "dd/MM/yyyy"), 
							planoForm.getBolsistaAtual().getDataFim()) > 0) 
						lista.addErro("A data da finalização do Bolsista não pode ser inferior a data de Início do Plano de Trabalho.");
				
					if (CalendarUtils.compareTo(CalendarUtils.parseDate(planoForm.getDataFim(), "dd/MM/yyyy"), 
							planoForm.getBolsistaAtual().getDataFim()) < 0) 
						lista.addErro("A data da finalização do Bolsista não pode ser superior a data final do Plano de Trabalho.");
				}
			}
		}
		
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		try {
			PlanoTrabalho planoTemp = dao.findByPrimaryKey( plano.getId(), PlanoTrabalho.class );
			
			if( planoTemp.getMembroProjetoDiscente() == null || planoTemp.getMembroProjetoDiscente().getId() <= 0
					|| planoTemp.getMembroProjetoDiscente().isInativo())
				lista.addErro("O plano de trabalho selecionado não possui um bolsista definido.");
		} finally {
			dao.close();
		}
	}

}
