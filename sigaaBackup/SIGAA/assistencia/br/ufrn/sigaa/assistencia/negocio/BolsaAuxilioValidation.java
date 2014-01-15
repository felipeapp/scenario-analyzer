package br.ufrn.sigaa.assistencia.negocio;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dao.RestricaoSolicitacaoBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.RestricaoSolicitacaoBolsaAuxilio;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class BolsaAuxilioValidation {

	public static void validacaoDadosBasico(UsuarioGeral usuarioLogado, BolsaAuxilio bolsaAuxilio, ListaMensagens lista) throws DAOException {
		validacaoSolicitacaoBolsa(bolsaAuxilio, lista);
		verificarExistenciaDadosBancarios(bolsaAuxilio);
		verificarAculumoBolsaAuxilio(bolsaAuxilio, lista);
		verificarPeriodoSolicitacao(usuarioLogado, bolsaAuxilio, lista);
		verificarCadastroUnico(bolsaAuxilio, lista);
	}
	
	private static void validacaoSolicitacaoBolsa(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) throws DAOException {
		PessoaDao dao = DAOFactory.getInstance().getDAO(PessoaDao.class);
		try {
			dao.initialize(bolsaAuxilio.getTipoBolsaAuxilio());
			
			ValidatorUtil.validateRequiredId(bolsaAuxilio.getTipoBolsaAuxilio().getId(), "Bolsa Desejada", lista);
			if ( !bolsaAuxilio.isTermoConcordancia() )
				lista.addWarning("Para prosseguir, voc� precisa estar ciente das instru��es dessa p�gina e informar isso ao sistema.");
			
			bolsaAuxilio.getDiscente().setPessoa(dao.findAndFetch(bolsaAuxilio.getDiscente().getPessoa().getId(), Pessoa.class, "contaBancaria"));
			if ( !verificarExistenciaDadosBancarios(bolsaAuxilio) )
				lista.addErro("Aten��o: voc� n�o possui conta banc�ria cadastrada no " + RepositorioDadosInstitucionais.get("siglaSigaa") + 
						". Voc� precisa cadastrar sua conta banc�ria atrav�s do link \"Meus Dados Pessoais\"");
			
			//Discentes do programa PEC-G n�o podem solicitar bolsa do tipo Resid�ncia.
			if ( ( bolsaAuxilio.getTipoBolsaAuxilio().isResidenciaGraduacao() || bolsaAuxilio.getTipoBolsaAuxilio().isResidenciaPos() ) &&  
					bolsaAuxilio.getDiscente().getFormaIngresso().getId() == FormaIngresso.ALUNO_PEC_G.getId() ) {
				lista.addErro("Discentes conveniados do programa PEC-G n�o podem concorrer a esse tipo de bolsa.");
			}
		} finally {
			dao.close();
		}
	}
	
	/** Verifica se o discente possui dados banc�rios cadastrados no caso de altera��o da solicita��o. */
	public static boolean verificarExistenciaDadosBancarios(BolsaAuxilio bolsaAuxilio ) {
		if (bolsaAuxilio.getDiscente().getPessoa().getContaBancaria() != null){
			if (!ValidatorUtil.isEmpty(bolsaAuxilio.getDiscente().getPessoa().getContaBancaria().getNumero()) &&
					!ValidatorUtil.isEmpty(bolsaAuxilio.getDiscente().getPessoa().getContaBancaria().getAgencia()) &&
					!ValidatorUtil.isEmpty(bolsaAuxilio.getDiscente().getPessoa().getContaBancaria().getBanco())){
				return true;
			}	
		}
		return false;
	}
	
	public static void validacaoBolsaPromisaes(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) throws DAOException {
		if ( bolsaAuxilio.getTipoBolsaAuxilio().isPromisaes() ) {
			if ( ( bolsaAuxilio.getTipoBolsaAuxilio().isResidenciaGraduacao() || bolsaAuxilio.getTipoBolsaAuxilio().isResidenciaPos() ) &&  
					bolsaAuxilio.getDiscente().getFormaIngresso().getId() == FormaIngresso.ALUNO_PEC_G.getId() ) {
				lista.addErro("Apenas os discentes conveniados do programa PEC-G n�o podem concorrer a esse tipo de bolsa.");
			}
		}
	}
	
	private static void verificarCadastroUnico(BolsaAuxilio bolsaAuxilio, ListaMensagens lista) throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = DAOFactory.getInstance().getDAO(AdesaoCadastroUnicoBolsaDao.class);
		try {
			boolean discenteComCadastroUnico = dao.isAdesaoCadastroUnico(bolsaAuxilio.getDiscente().getId(), 
					CalendarioAcademicoHelper.getCalendario(bolsaAuxilio.getDiscente()).getAno(), 
						CalendarioAcademicoHelper.getCalendario(bolsaAuxilio.getDiscente()).getPeriodo());
			
			if ( !discenteComCadastroUnico ) {
				lista.addErro("Discente n�o localizado no Cadastro �nico. Para solicitar Bolsa Aux�lio � necess�rio aderir ao Cadastro �nico.");
			}
			
			if ( !bolsaAuxilio.getDiscente().isRegular() ) {
				lista.addErro("Apenas discente Regular pode solicitar bolsa auxilio.");
			}
			
		} finally {
			dao.close();
		}
	}
	
	private static void verificarPeriodoSolicitacao(UsuarioGeral usuarioLogado, BolsaAuxilio bolsaAuxilio, ListaMensagens lista) throws DAOException {
		if ( bolsaAuxilio.getTipoBolsaAuxilio().getId() > 0 ) {
			AnoPeriodoReferenciaSAEDao dao = DAOFactory.getInstance().getDAO(AnoPeriodoReferenciaSAEDao.class);
			try {
				if (usuarioLogado.isUserInRole(SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO))
					return;
				boolean temPeriodoCadastrado = false;
				AnoPeriodoReferenciaSAE	calendarioVigente = dao.anoPeriodoVigente();
				for (CalendarioBolsaAuxilio calendario : calendarioVigente.getCalendario()) {
					if ( bolsaAuxilio.getDiscente().getCurso().getMunicipio().getId() == calendario.getMunicipio().getId() && 
							bolsaAuxilio.getTipoBolsaAuxilio().getId() == calendario.getTipoBolsaAuxilio().getId()) {
						temPeriodoCadastrado = true;
						if ( !CalendarUtils.isDentroPeriodo(calendario.getInicio(), calendario.getFim()) ) {
							lista.addErro("N�o est� dentro do per�odo de solicita��o do aux�lio.");
						}
						
						if ( bolsaAuxilio.getDiscente().isCadastrado(calendarioVigente.getAno(), calendarioVigente.getPeriodo()) ) {
							if ( !calendario.isAlunoNovato() ) {
								lista.addErro("N�o est� aberto o per�odo de solicita��o de bolsa de "
										+ bolsaAuxilio.getTipoBolsaAuxilio().getDenominacao() + " para discentes Ingressantes.");
							}
						} else {
							if ( !calendario.isAlunoVeterano() ) {
								lista.addErro("N�o est� aberto o per�odo de solicita��o de bolsa de "
										+ bolsaAuxilio.getTipoBolsaAuxilio().getDenominacao() + " para discentes Veteranos.");
							}
						}
					}
				}
				
				if ( !temPeriodoCadastrado )
					lista.addErro("Ainda n�o foi cadastrado per�odo de solicita��o para a bolsa de " + bolsaAuxilio.getTipoBolsaAuxilio().getDenominacao() +".");
				
			} finally {
				dao.close();
			}
		}   
	}

	private static void verificarAculumoBolsaAuxilio(BolsaAuxilio bolsa, ListaMensagens lista) throws DAOException {
		RestricaoSolicitacaoBolsaAuxilioDao dao = DAOFactory.getInstance().getDAO(RestricaoSolicitacaoBolsaAuxilioDao.class);
		BolsaAuxilioDao bolsaDao  = DAOFactory.getInstance().getDAO(BolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoReferenciaDao = DAOFactory.getInstance().getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			 AnoPeriodoReferenciaSAE anoReferencia = anoReferenciaDao.anoPeriodoVigente();
			 Collection<RestricaoSolicitacaoBolsaAuxilio> restricoes = dao.findAllRestricoes(bolsa.getTipoBolsaAuxilio());
			 Collection<BolsaAuxilioPeriodo> bolsas = bolsaDao.findAllBolsasDiscenteAnoPeriodo(bolsa.getDiscente(), anoReferencia.getAno(), anoReferencia.getPeriodo());
			 
			 for (RestricaoSolicitacaoBolsaAuxilio restricao : restricoes) {
				for (BolsaAuxilioPeriodo bolsaAuxilio : bolsas) {
					if ( bolsaAuxilio.getBolsaAuxilio().getTipoBolsaAuxilio().getId() == restricao.getBolsasAuxilioRestricao().getId() &&  
							bolsaAuxilio.getBolsaAuxilio().getSituacaoBolsa().getId() == restricao.getSituacao().getId() ) {
						
						lista.addErro("N�o � permitido solicitar bolsa " + bolsa.getTipoBolsaAuxilio().getDenominacao() + " apresentando a bolsa "
						 + bolsaAuxilio.getBolsaAuxilio().getTipoBolsaAuxilio().getDenominacao() + " com a situa��o " + bolsaAuxilio.getBolsaAuxilio().getSituacaoBolsa().getDenominacao() + "." );
					}
				}
			 }
			 
		} finally {
			dao.close();
			bolsaDao.close();
		}
	}
	
}