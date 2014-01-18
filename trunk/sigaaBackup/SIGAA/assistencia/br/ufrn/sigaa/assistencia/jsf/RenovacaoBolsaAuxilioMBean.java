package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.dao.sae.RenovacaoBolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.negocio.BolsaAuxilioValidation;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *	MBean responsável pelo CRUD das Bolsas Auxílio Alimentação/Residência
 *	Usada pelos coordenadores do SAE e pelos alunos
 *         
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class RenovacaoBolsaAuxilioMBean extends SigaaAbstractController<BolsaAuxilio> {

	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private AnoPeriodoReferenciaSAE anoReferencia;
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private Object bolsaAuxiliar;
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
    private int[] transportesIds = new int[0];
    /** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private int[] documentosIds = new int[0];
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private Collection<BolsaAuxilio> bolsas;
	
	public RenovacaoBolsaAuxilioMBean() {
		clear();
	}
		
	/**
	 * Inicializa os atributos do MBean
	 */
	public void clear() {
		obj = new BolsaAuxilio();
		obj.setResidencia(new ResidenciaUniversitaria());
		obj.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		obj.setResidencia(new ResidenciaUniversitaria());
		documentosIds = new int[0];
		transportesIds = new int[0];
		bolsas = new ArrayList<BolsaAuxilio>();
	}	
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		RenovacaoBolsaAuxilioDao dao = getDAO(RenovacaoBolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoRefDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			clear();
			obj.setDiscente( getUsuarioLogado().getDiscente() );

			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());
			if ( !CalendarUtils.isDentroPeriodo(cal.getInicioMatriculaOnline(), cal.getFimMatriculaOnline()) ) {
				addMensagemErro("Não está dentro do período de renovação de bolsas.");
				return null;
			}

			obj.setSituacaoBolsa(new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_SOLICITADA_RENOVACAO));
			AnoPeriodoReferenciaSAE anoPeriodoRef = anoPeriodoRefDao.anoPeriodoVigente();
			bolsas = dao.findAllBolsaPassivelRenovacao(obj.getDiscente().getId(), anoPeriodoRef, true);
			
			anoReferencia = new AnoPeriodoReferenciaSAE();
			anoReferencia.setAno( cal.getAno() );
			anoReferencia.setPeriodo( cal.getPeriodo() );
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_DISCENTE);
		} finally {
			dao.close();
			anoPeriodoRefDao.close();
		}
	
		return forward("/sae/BolsaAuxilio/renovar_bolsa.jsf");
	}
	
	/**
	 *  Método chamado pela(s) seguinte(s) JSP's:
	 *  <br/>
	 *  <ul>
	 *  	<li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String iniciarRenovacaoBolsaAuxilio() throws ArqException, NegocioException {
		RenovacaoBolsaAuxilioDao dao = getDAO(RenovacaoBolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoRefDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			AnoPeriodoReferenciaSAE anoPeriodoRef = anoPeriodoRefDao.anoPeriodoVigente();
			dao.registrarResposta(getUsuarioLogado(), anoPeriodoRef.getAno(), anoPeriodoRef.getPeriodo(), true);
		} finally {
			dao.close();
			anoPeriodoRefDao.close();
		}
		
		return preCadastrar();
	}

	public String naoRenovacaoBolsaAuxilio() throws DAOException {
		RenovacaoBolsaAuxilioDao dao = getDAO(RenovacaoBolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoRefDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			AnoPeriodoReferenciaSAE anoPeriodoRef = anoPeriodoRefDao.anoPeriodoVigente();
			dao.registrarResposta(getUsuarioLogado(), anoPeriodoRef.getAno(), anoPeriodoRef.getPeriodo(), false);
		} finally {
			dao.close();
			anoPeriodoRefDao.close();
		}
		return redirect("/verPortalDiscente.do");
	}
	
	public String naoSolicitarRenovacaoBolsa() throws ArqException{
		BolsaAuxilio bolsa = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), BolsaAuxilio.class);
		try {
			prepareMovimento(SigaaListaComando.ALTERAR_BOLSA_AUXILIO);
			MovimentoCadastro movCad = new MovimentoCadastro();
			bolsa.setSolicitadaRenovacao(Boolean.FALSE);
			movCad.setObjMovimentado(bolsa);
			movCad.setObjAuxiliar(null);
			movCad.setCodMovimento(SigaaListaComando.ALTERAR_BOLSA_AUXILIO);
			
			execute(movCad);

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		if ( getBolsas().isEmpty() ) {
			return redirect("/verPortalDiscente.do");
		}
		
		return forward("/sae/BolsaAuxilio/renovar_bolsa.jsf");
	}
	
	public String selecionaTipoBolsa() throws DAOException {
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {
			BolsaAuxilio bolsa = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), BolsaAuxilio.class);
			obj.getTipoBolsaAuxilio().setId( bolsa.getTipoBolsaAuxilio().getId() );
			obj.setTipoBolsaAuxilio( getGenericDAO().findByPrimaryKey(obj.getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class) );
			obj.setTermoConcordancia(Boolean.TRUE);

			Pessoa pessoa = pessoaDao.findCompleto(obj.getDiscente().getPessoa().getId());
			obj.getDiscente().setPessoa(pessoa);
			if (  obj.getTurnoAtividade() == null || obj.getTurnoAtividade().isEmpty() )
				obj.setTurnoAtividade(BolsaAuxilio.APENAS_UM_TURNO);
			obj.setSituacaoBolsa(new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_SOLICITADA_RENOVACAO));
			
			BolsaAuxilioValidation.validacaoDadosBasicoSemData(getUsuarioLogado(), obj, erros);
			BolsaAuxilioValidation.validacaoBolsaPromisaes(obj, erros);
			
			if ( hasErrors() ) {
				addMensagens(erros);
				return null;
			}
			
			BolsaAuxilioMBean mBean = getMBean("bolsaAuxilioMBean");

			mBean.setObj(obj);
			mBean.getObj().getDiscente().setPessoa(pessoa);
			mBean.getObj().setRenovacao(true);
			mBean.getObj().setBolsaAuxilioOriginal(bolsa);
			mBean.carregarQuestionarioRespostas();
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO.getId());
		
			return mBean.direcionar();
			
		} finally {
			pessoaDao.close();
		}
		
	}
	
	public AnoPeriodoReferenciaSAE getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(AnoPeriodoReferenciaSAE anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public Object getBolsaAuxiliar() {
		return bolsaAuxiliar;
	}

	public void setBolsaAuxiliar(Object bolsaAuxiliar) {
		this.bolsaAuxiliar = bolsaAuxiliar;
	}

	public int[] getTransportesIds() {
		return transportesIds;
	}

	public void setTransportesIds(int[] transportesIds) {
		this.transportesIds = transportesIds;
	}

	public int[] getDocumentosIds() {
		return documentosIds;
	}

	public void setDocumentosIds(int[] documentosIds) {
		this.documentosIds = documentosIds;
	}

	public Collection<BolsaAuxilio> getBolsas() throws DAOException {
		RenovacaoBolsaAuxilioDao dao = getDAO(RenovacaoBolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoRefDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			obj.setDiscente( getUsuarioLogado().getDiscente() );
			AnoPeriodoReferenciaSAE anoPeriodoRef = anoPeriodoRefDao.anoPeriodoVigente();
			bolsas = dao.findAllBolsaPassivelRenovacao(obj.getDiscente().getId(), anoPeriodoRef, true);
		} finally {
			dao.close();
			anoPeriodoRefDao.close();
		}
		return bolsas;
	}

	public void setBolsas(Collection<BolsaAuxilio> bolsas) {
		this.bolsas = bolsas;
	}

}