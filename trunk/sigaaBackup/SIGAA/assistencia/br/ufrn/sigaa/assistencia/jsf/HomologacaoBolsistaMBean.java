package br.ufrn.sigaa.assistencia.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.bolsas.negocio.IntegracaoTipoBolsaHelper;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

@Component @Scope("session")
public class HomologacaoBolsistaMBean extends SigaaAbstractController<BolsaAuxilioPeriodo> {

	private enum Operacao {INCLUSAO, EXCLUSAO;}

	/** Constantes das Views */
	public final String JSP_LISTA_HOMOLOGACAO = "/sae/homologar_bolsista_sipac/lista.jsf";
	public final String JSP_LISTA_FINALIZACAO = "/sae/homologar_bolsista_sipac/lista_finalizacao.jsf";
	
	private Operacao operacao;
	private Collection<BolsaAuxilio> bolsas;
	private AnoPeriodoReferenciaSAE periodoReferencia = null;
	private Municipio municipio;
	
	
	/** Construtor padrão. */
	public HomologacaoBolsistaMBean() {
		clear();
	}

	private void clear() {
		obj = new BolsaAuxilioPeriodo();
		bolsas = new ArrayList<BolsaAuxilio>();
		obj.setBolsaAuxilio( new BolsaAuxilio() );
		obj.getBolsaAuxilio().setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		obj.setAno( CalendarUtils.getAnoAtual() );
		obj.setPeriodo( getPeriodoAtual() );
		municipio = new Municipio();
	}

	public String popular() throws ArqException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);	
		if( !Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}
		
		clear();
		setOperacao(Operacao.INCLUSAO);
		return forward(JSP_LISTA_HOMOLOGACAO);
	}
	
	public String popularFinalizar() throws ArqException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		
		if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}
		
		clear();
		setOperacao(Operacao.EXCLUSAO);
		return forward(JSP_LISTA_FINALIZACAO);
	}
	
	public String buscar() throws ArqException {
		
		ValidatorUtil.validateRequiredId(obj.getBolsaAuxilio().getTipoBolsaAuxilio().getId(), "Modalidade da Bolsa", erros);
		if ( obj.getAno() == null || obj.getPeriodo() == null )
			ValidatorUtil.validateRequired(null, "Ano-Período", erros);

		if ( hasErrors() ) {
			bolsas = new ArrayList<BolsaAuxilio>();
			return null;
		}
		
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		try {
			boolean incluir = !(getOperacao().equals(Operacao.INCLUSAO)); 
			
			if ( obj.getBolsaAuxilio().getTipoBolsaAuxilio().isResidenciaGraduacao() ) {
				bolsas = dao.findBolsas(obj.getBolsaAuxilio().getTipoBolsaAuxilio().getId(), 0, 
						SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA, obj.getAno(), obj.getPeriodo(), 0, municipio.getId(), '0', incluir, ' ', 0);
			} else {
				bolsas = dao.findBolsas(obj.getBolsaAuxilio().getTipoBolsaAuxilio().getId(), 0, 
						SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, obj.getAno(), obj.getPeriodo(), 0, municipio.getId(), '0', incluir, ' ', 0);
			}
		} finally {
			dao.close();
		}

		if(bolsas.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}

		return redirectMesmaPagina();
	}

	private void carregarPeriodoReferencia() throws DAOException {
		if ( periodoReferencia == null || !(periodoReferencia.getAno().equals(obj.getAno())) || !(periodoReferencia.getPeriodo().equals(obj.getPeriodo())) ) {
			AnoPeriodoReferenciaSAEDao dao = getDAO(AnoPeriodoReferenciaSAEDao.class);
			try {
				periodoReferencia = dao.findCalendarioAnoPeriodo(obj.getAno(), obj.getPeriodo());
			} finally {
				dao.close();
			}
		}
	}
	
	private Date getDataFimBolsa(TipoBolsaAuxilio tipoBolsa, Discente discente) throws DAOException {
		carregarPeriodoReferencia();
		if ( periodoReferencia != null && periodoReferencia.getCalendario() != null ) {
			for (CalendarioBolsaAuxilio calendario : periodoReferencia.getCalendario()) {
				if ( calendario.isAtivo() && tipoBolsa.getId() == calendario.getTipoBolsaAuxilio().getId() 
						&& discente.getCurso().getMunicipio().getId() == calendario.getMunicipio().getId() ) {
					return calendario.getFimExecucaoBolsa();
				}
			}
		}
		return null;
	}
	
	private Date getDataInicioBolsa(TipoBolsaAuxilio tipoBolsa, Discente discente) throws DAOException {
		carregarPeriodoReferencia();
		if ( periodoReferencia != null && periodoReferencia.getCalendario() != null ) {
			for (CalendarioBolsaAuxilio calendario : periodoReferencia.getCalendario()) {
				if ( calendario.isAtivo() && tipoBolsa.getId() == calendario.getTipoBolsaAuxilio().getId() 
						&& discente.getCurso().getMunicipio().getId() == calendario.getMunicipio().getId() ) {
					return calendario.getInicioExecucaoBolsa();
				}
			}
		}
		return null;
	}
	
	public String homologarBolsas() throws ArqException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
		Date hoje = new Date();
		periodoReferencia = null;
		for (BolsaAuxilio bolsa : bolsas) {
			if (bolsa.isSelecionado()) {
				InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
				solicitacao.setCodigoBanco(bolsa.getDiscente().getPessoa().getContaBancaria().getBanco().getCodigo());
				solicitacao.setAgenciaBancaria(bolsa.getDiscente().getPessoa().getContaBancaria().getAgencia());
				solicitacao.setNumeroContaBancaria(bolsa.getDiscente().getPessoa().getContaBancaria().getNumero());
				solicitacao.setDataFim(getDataFimBolsa(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente()));
				Date dataInicio = getDataInicioBolsa(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente());
				if ( dataInicio != null && hoje.after(dataInicio) ) {
					solicitacao.setDataInicio(hoje);
				} else {
					solicitacao.setDataInicio(dataInicio);
				}
				solicitacao.setIdCurso(bolsa.getDiscente().getCurso().getId());
				solicitacao.setIdPessoa(bolsa.getDiscente().getPessoa().getId());
				solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
				solicitacao.setIdUnidadeLocalTrabalho(ParametroHelper.getInstance().getParametroInt(ParametrosSAE.ID_UNIDADE_PROAE));
				solicitacao.setIdUnidadeResponsavel(ParametroHelper.getInstance().getParametroInt(ParametrosSAE.ID_UNIDADE_PROAE));
				solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
				solicitacao.setJustificativa("[HOMOLOGAÇÃO DE BOLSA REALIZADA ATRAVÉS DO MENU DA PROAE]");
				solicitacao.setMatricula(bolsa.getDiscente().getMatricula());
				solicitacao.setObservacao("[SOLICITAÇÃO DE BOLSA REALIZADA VIA SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".]");
				solicitacao.setNivel(String.valueOf(bolsa.getDiscente().getNivel()));
				solicitacao.setTipoBolsa(IntegracaoTipoBolsaHelper.getBolsaSIPAC(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente()));
				solicitacao.setIdDiscenteProjeto(bolsa.getId());
				solicitacao.setCpf( bolsa.getDiscente().getPessoa().getCpf_cnpj() );
				solicitacoesCadastro.add(solicitacao);
			}
		}

	    try {
	        prepareMovimento(SigaaListaComando.CADASTRAR_BOLSISTA_SIPAC);
	        MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
	        mov.setSolicitacoes(solicitacoesCadastro);
	        mov.setCodMovimento(SigaaListaComando.CADASTRAR_BOLSISTA_SIPAC);
	        execute(mov);
	        addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	        
	        //atualiza a tela de busca
	        popular();
	        
	    } catch (NegocioException e) {
	        notifyError(e);
	        addMensagens(e.getListaMensagens());
	    }		

	    return redirectMesmaPagina();		
	}
	
	/**
	 * Realiza a chamada de exclusão de bolsas no SIPAC.
	 * <br><br>
	 * JSP: /pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String finalizarBolsas() throws ArqException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
		Date hoje = new Date();
		for (BolsaAuxilio bolsa : bolsas) {
			if (bolsa.isSelecionado()) {
				InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
				solicitacao.setCodigoBanco(bolsa.getDiscente().getPessoa().getContaBancaria().getBanco().getCodigo());
				solicitacao.setAgenciaBancaria(bolsa.getDiscente().getPessoa().getContaBancaria().getAgencia());
				solicitacao.setNumeroContaBancaria(bolsa.getDiscente().getPessoa().getContaBancaria().getNumero());
				solicitacao.setDataFim(getDataFimBolsa(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente()));
				Date dataInicio = getDataInicioBolsa(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente());
				if ( dataInicio != null && hoje.after(dataInicio) ) {
					solicitacao.setDataInicio(hoje);
				} else {
					solicitacao.setDataInicio(dataInicio);
				}
				solicitacao.setIdCurso(bolsa.getDiscente().getCurso().getId());
				solicitacao.setIdPessoa(bolsa.getDiscente().getPessoa().getId());
				solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
				solicitacao.setIdUnidadeLocalTrabalho(ParametroHelper.getInstance().getParametroInt(ParametrosSAE.ID_UNIDADE_PROAE));
				solicitacao.setIdUnidadeResponsavel(ParametroHelper.getInstance().getParametroInt(ParametrosSAE.ID_UNIDADE_PROAE));
				solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
				solicitacao.setJustificativa("[FINALIZAÇÃO DE BOLSA REALIZADA ATRAVÉS DO MENU DA PROAE]");
				solicitacao.setMatricula(bolsa.getDiscente().getMatricula());
				solicitacao.setObservacao("[FINALIZAÇÃO DE BOLSA REALIZADA VIA SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".]");
				solicitacao.setNivel(String.valueOf(bolsa.getDiscente().getNivel()));
				solicitacao.setTipoBolsa(IntegracaoTipoBolsaHelper.getBolsaSIPAC(bolsa.getTipoBolsaAuxilio(), bolsa.getDiscente()));
				solicitacao.setIdDiscenteProjeto(bolsa.getId());
				solicitacao.setCpf( bolsa.getDiscente().getPessoa().getCpf_cnpj() );
				solicitacao.setIdBolsa( bolsa.getTipoBolsaSIPAC() );
				solicitacoesCadastro.add(solicitacao);
			}
		}
		
	    try {
	        prepareMovimento(SigaaListaComando.REMOVER_BOLSISTA_SIPAC);
	        MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
	        mov.setSolicitacoes(solicitacoesCadastro);
	        mov.setCodMovimento(SigaaListaComando.REMOVER_BOLSISTA_SIPAC);
	        execute(mov);
	        addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	        
	        //atualiza a tela de busca
	        popularFinalizar();
	        
	    } catch (NegocioException e) {
	        notifyError(e);
	        addMensagens(e.getListaMensagens());
	    }		

	    return redirectMesmaPagina();		
	}
	
	public Collection<BolsaAuxilio> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<BolsaAuxilio> bolsas) {
		this.bolsas = bolsas;
	}

	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}

	public AnoPeriodoReferenciaSAE getPeriodoReferencia() {
		return periodoReferencia;
	}

	public void setPeriodoReferencia(AnoPeriodoReferenciaSAE periodoReferencia) {
		this.periodoReferencia = periodoReferencia;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
	
}