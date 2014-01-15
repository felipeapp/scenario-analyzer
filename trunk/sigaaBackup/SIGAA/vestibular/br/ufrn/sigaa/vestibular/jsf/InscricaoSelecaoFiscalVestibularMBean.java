/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProvaProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;
import br.ufrn.sigaa.vestibular.negocio.InscricaoFiscalValidator;

/** Controller responsável pela inscrição para a seleção de fiscais do vestibular.
 * @author Édipo Elder F. Melo
 *
 */
@Scope("session")
@Component("inscricaoSelecaoFiscalVestibular")
public class InscricaoSelecaoFiscalVestibularMBean extends
		SigaaAbstractController<InscricaoFiscal> {

	/** Lista de locais de prova optativos para trabalhar. */
	private List<LocalAplicacaoProva> listaLocalAplicacaoProva;
	
	/** Indica se o processo seletivo para o qual vai se inscrever já está setado. */
	private boolean processoSeletivoSetado;
	
	/** Indica se exibe no formulário, a opção de viajar para outras cidades. */
	private boolean exibeOutrasCidades = false;
	
	/** Município de origem de inscrição do fiscal. */
	private Municipio municipio;
	
	/** Arquivo de foto 3x4 do fiscal. */
	private UploadedFile foto;

	/** Boleano que indica se o usuário selecionou ou não o município */
	private boolean selecionarMunicipio;

	/** Armazena uma coleção de municípios */
	private Collection<Municipio> municipios;
	
	/** Indica se a foto é passivel de modificação por parte do candidato */
	private boolean fotoPassivelModificacao = true;
	
	/** Boleano responsável por informar se o candidato pode realizar nova inscrição */
	private boolean novaInscricao = true;

	/** Resposável pelo armazenamento da informação do Fiscal */
	private Fiscal fiscal;

	/** Armazena o local de prova do fiscal */
	private LocalAplicacaoProvaProcessoSeletivo localProva;
	
	/** Constante responsável pelo armazenamento do número dos processos seletivos anteriores */
	public static final int NUMERO_PROCESSO_SELETIVO_ANTERIOR = 2; 
	
	/** Construtor padrão. */
	public InscricaoSelecaoFiscalVestibularMBean() {
		obj = new InscricaoFiscal();
		obj.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular());
	}

	/** Carrega a lista de locais de aplicação.
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public void carregaListaLocalAplicacao() throws DAOException,
			NegocioException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		// discente
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			if (getUsuarioLogado().getDiscenteAtivo().getCurso().isADistancia()) {
				listaLocalAplicacaoProva = dao.findByProcessoSeletivo(obj
						.getProcessoSeletivoVestibular().getId());
			} else {
				listaLocalAplicacaoProva = dao.findByProcessoSeletivoMunicipio(
						this.obj.getProcessoSeletivoVestibular().getId(),
						municipio.getId());
			}
		} else if (getUsuarioLogado().getServidorAtivo() != null) {
			// servidor
			listaLocalAplicacaoProva = dao.findByProcessoSeletivoMunicipio(
					this.obj.getProcessoSeletivoVestibular().getId(), municipio
							.getId());
		} else {
			addMensagemErro("Não foi possível determinar o perfil (discente ou servidor) do usuário.");
		}
	}
	
	/**
	 * Responsável pelo carregamento do local de prova do município selecionado.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String selecionaMunicipio(ValueChangeEvent evt) throws DAOException, NegocioException {
		int id = (Integer) evt.getNewValue();
		municipio = getGenericDAO().refresh(new Municipio(id));
		validaProcessoSeletivo();
		carregaListaLocalAplicacao();
		return null;	
	}
	
	/**
	 * Método responsável pelo carregamento dos locais de prova do processo seletivo selecionado.  
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String selecionaProcessoSeletivo(ValueChangeEvent evt) throws DAOException, NegocioException{
		int id = (Integer) evt.getNewValue();
		obj.setProcessoSeletivoVestibular(getGenericDAO().refresh(new ProcessoSeletivoVestibular(id)));
		municipio = new Municipio();
		municipios = null;
		validaProcessoSeletivo();	
		return null;
	}
	
	/**
	 * Responsável por validar o processo seletivo e carregar os locais de prova.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public void validaProcessoSeletivo() throws DAOException, NegocioException {
		// se o usuário possui foto no seu perfil, utiliza-a na inscrição
		if (getUsuarioLogado().getIdFoto() != null && getUsuarioLogado().getIdFoto() > 0) {
			obj.setIdFoto(getUsuarioLogado().getIdFoto());
		}
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			// verifica se cumpre os pré-requisitos para ser fiscal
			obj.setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
			InscricaoFiscalValidator.checaDiscenteServidor(obj.getDiscente().getDiscente(), erros);
			InscricaoFiscalValidator.validaInscricaoDiscente(obj.getDiscente().getDiscente(),
					this.obj.getProcessoSeletivoVestibular(), erros);
		} else if (getUsuarioLogado().getServidorAtivo() != null) {
			obj.setServidor( 
					getGenericDAO().findAndFetch(getUsuarioLogado().getServidorAtivo().getId(), 
							br.ufrn.sigaa.pessoa.dominio.Servidor.class, "cargo") );
			InscricaoFiscalValidator.validaInscricaoServidor(obj
					.getServidor(), this.obj.getProcessoSeletivoVestibular(),
					erros);
		} else {
			addMensagemErro("Não foi possível determinar o perfil (discente ou servidor) do usuário.");
		}
		if (hasOnlyErrors()) {
			return;
		}
		// determina se o fiscal é novato ou experiente
		obj.setNovato(true);
		FiscalDao fiscalDao = getDAO(FiscalDao.class);
		for (Fiscal fiscal : fiscalDao.findByPessoa(obj.getPessoa(), NUMERO_PROCESSO_SELETIVO_ANTERIOR)) {
			if (fiscal.getReserva() != null && fiscal.getReserva() == false) {
				obj.setNovato(false);
				break;
			}
		}
		
		carregaListaLocalAplicacao();
	}

	/** Exibe o comprovante de inscrição.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menu_servidor.jsp</li>
	 * </ul>
	 *  
	 * @return /vestibular/comprovante_inscricao_fiscal.jsp
	 * @throws DAOException
	 */
	public String exibeComprovanteInscricao() throws DAOException {
		ProcessoSeletivoVestibularDao psDao = getDAO(ProcessoSeletivoVestibularDao.class);
		ProcessoSeletivoVestibular ps = psDao.findUltimoPeriodoInscricaoFiscal();
		if (ps == null) {
			addMensagemErro("Não há Processo Seletivo com período de inscrição de Fiscais definido.");
			return null;
		}
		InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
		InscricaoFiscal inscricao = dao.findByPessoaProcessoSeletivo(
				getUsuarioLogado().getPessoa().getId(), ps.getId());
		if (inscricao == null) {
			if (getUsuarioLogado().getDiscenteAtivo() != null) {
				inscricao = dao.findByDiscenteProcessoSeletivo(
						getUsuarioLogado().getDiscenteAtivo().getId(), ps
								.getId());
			} else if (getUsuarioLogado().getServidorAtivo() != null) {
				inscricao = dao.findByPessoaProcessoSeletivo(getUsuarioLogado()
						.getServidorAtivo().getPessoa().getId(), ps.getId());
			} else {
				addMensagemErro("Não foi possível determinar o perfil (discente ou servidor) do usuário.");
			}
			if (inscricao == null) {
				addMensagemErro("Não há registro de sua inscrição para seleção de fiscais");
				return null;
			}
		}
		getCurrentRequest().setAttribute("inscricao", inscricao);
		return forward("/vestibular/comprovante_inscricao_fiscal.jsp");
	}

	/** Exibe o resultado do processamento da seleção de fiscais.
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menu_servidor.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/resultado_processamento_fiscal.jsp
	 * @throws DAOException
	 */
	public String exibeResultadoDoProcessamento() throws DAOException {
		ProcessoSeletivoVestibularDao psDao = getDAO(ProcessoSeletivoVestibularDao.class);
		ProcessoSeletivoVestibular ps = psDao.findUltimoPeriodoInscricaoFiscal();
		if (ps == null) {
			addMensagemErro("Não há Processo Seletivo com período de inscrição de Fiscais definido.");
			return null;
		}
		if (!ps.isSelecaoFiscalProcessada() || !ps.isInformaResultadoSelecao()) {
			addMensagemErro("O processamento da seleção de fiscais ainda não foi realizado");
			return null;
		}
		InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
		InscricaoFiscal inscricao = dao.findByPessoaProcessoSeletivo(
				getUsuarioLogado().getPessoa().getId(), ps.getId());
		if (inscricao == null) {
			if (getUsuarioLogado().getDiscenteAtivo() != null)
				inscricao = dao.findByDiscenteProcessoSeletivo(
						getUsuarioLogado().getDiscenteAtivo().getId(), ps
								.getId());
			if (inscricao == null) {
				addMensagemErro("Não há registro de sua inscrição para seleção de fiscais");
				return null;
			}
		}
		// resultado da seleção
		FiscalDao fiscalDao = getDAO(FiscalDao.class);
		Fiscal fiscal = fiscalDao.findByPessoaProcessoSeletivo(inscricao
				.getPessoa(), ps);
		if (fiscal == null) {
			String motivo = "";
			double iraMin = 0;
			if (inscricao.getDiscente() != null
					&& inscricao.getDiscente().isGraduacao())
				iraMin = fiscalDao.findIraMinSelecao(ps, inscricao
						.getDiscente().getCurso());
			if (iraMin < 0)
				motivo = "As vagas dos fiscais do seu curso foi preenchida com fiscais que já trabalharam em Vestibulares anteriores (RECADASTRO)";
			else
				motivo = "o IRA mínimo para seleção do seu curso foi " + iraMin;
			if (inscricao.getDiscente() != null
					&& inscricao.getDiscente().isStricto())
				motivo = "a média geral mínima para seleção do seu curso foi "
						+ fiscalDao.findMediaGeralMinSelecao(ps, inscricao
								.getDiscente().getCurso());
			if (inscricao.getServidor() != null)
				motivo = inscricao.getObservacao();
			getCurrentRequest().setAttribute("motivo", motivo);
		}
		// local de prova e horário da reunião
		LocalAplicacaoProvaProcessoSeletivo local = null;
		if (fiscal != null && fiscal.getLocalAplicacaoProva() != null) {
			LocalAplicacaoProvaProcessoSeletivoDao localPSDao = getDAO(LocalAplicacaoProvaProcessoSeletivoDao.class);
			local = localPSDao.findByProcessoSeletivoLocalAplicacao(ps,
					new LocalAplicacaoProva(fiscal.getLocalAplicacaoProva()
							.getId()));
		}
		obj = inscricao;
		this.fiscal = fiscal;
		this.localProva = local;
		return forward("/vestibular/resultado_processamento_fiscal.jsp");
	}
	
	/** 
	 * Recebe o upload e valida a foto 3x4 do candidato.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws IOException
	 */
	public String enviarFoto() {
		if (foto != null) {
			try {
				ImageIO.read(foto.getInputStream());
			} catch (Exception e) {	
				e.printStackTrace();
				addMensagemErro("A foto enviada apresenta erro, por favor, selecione outra foto.");
			}
			if ("image/jpeg".equalsIgnoreCase(foto.getContentType())
					||"image/pjpeg".equalsIgnoreCase(foto.getContentType())) {
			} else {
				addMensagemErro("Arquivo informado não é arquivo de foto válido. Envie um arquivo no formato JPG.");
			}
		} else {
			addMensagemErro("Por favor, envie um arquivo com a foto.");
		}
		if (!hasOnlyErrors()) {
			addMessage("Foto Enviada com sucesso!",	TipoMensagemUFRN.INFORMATION);
		}else{
			foto = null;
		}
		return null;
	}

	/** 
	 * Retorna uma coleção de SelectItem de locais de aplicação de prova.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getLocalAplicacaoProvaCombo()
			throws DAOException {
		return toSelectItems(listaLocalAplicacaoProva, "id", "nomeBairro");
	}

	/** Retorna o município de origem de inscrição do fiscal. 
	 * @return
	 */
	public Municipio getMunicipio() {
		return municipio;
	}

	/** 
	 * Retorna uma coleção de SelectItem de Processos Seletivos com período de inscrição de fiscais ativos.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getProcessoSeletivoCombo()
			throws HibernateException, DAOException {
		InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
		return toSelectItems(dao
				.findProcessosSeletivosNaoInscritos(getUsuarioLogado()
						.getPessoa().getId()), "id", "nome");
	}

	/**
	 * Inicializa os atributos a serem utilizados
	 */
	private void init(){
		foto = null;
		// conta bancária por enquanto não é obrigatória
		obj = new InscricaoFiscal();
		obj.setContaBancaria(null);
		obj.setPessoa(getUsuarioLogado().getPessoa());
		this.selecionarMunicipio = false;
		this.municipio = new Municipio();
	}
	
	/** Inicia a inscrição para a seleção de fiscais.
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menu_servidor.jsp</li>
	 * <ul>
	 * 
	 * @return /vestibular/inscricao_fiscal.jsp
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarInscricaoFiscal() throws ArqException, NegocioException {
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class);
		ProcessoSeletivoVestibularDao psDao = getDAO(ProcessoSeletivoVestibularDao.class);
		ProcessoSeletivoVestibular ps = psDao.findUltimoPeriodoInscricaoFiscal();
		if (ps == null) {
			addMensagemErro("Não há Processo Seletivo com período de inscrição de Fiscais definido.");
			return null;
		} 
		setObj(inscricaoFiscalDao.findByPessoaProcessoSeletivo(getUsuarioLogado().getPessoa().getId(), ps.getId()));
		
		if (obj == null){ 
			novaInscricao = true;
			init();
		
			/* Esse trecho de código deve ser executado antes de tudo */
			// se for de natal, exibir a opção de fiscalizar em outras cidades
			if (getUsuarioLogado().getDiscenteAtivo() != null) {
				DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
				if (!discente.isGraduacao() && !discente.isStricto()) {
					addMensagemErro("Somente alunos de cursos de graduação ou pós-graduação stricto sensu podem se inscrever para seleção de fiscais");
					return null;
				}
				if (!getUsuarioLogado().getDiscenteAtivo().isRegular()) {
					addMensagemErro("Somente discentes regulares podem participar da seleção para fiscal");
					return null;
				}
				this.municipio = getUsuarioLogado().getDiscenteAtivo().getCurso()
						.getMunicipio();
				if (getUsuarioLogado().getDiscenteAtivo().getCurso().getMunicipio()
						.getId() == Municipio.ID_MUNICIPIO_PADRAO) {
					this.exibeOutrasCidades = true;
				} else {
					this.exibeOutrasCidades = false;
				}
			} else if (getUsuarioLogado().getServidorAtivo() != null) {
				if ( getUsuarioLogado().getServidorAtivo().getUnidade() == null ) 
					this.municipio = psDao.findByPrimaryKey(getUsuarioLogado().getServidorAtivo().getId(), br.ufrn.sigaa.pessoa.dominio.Servidor.class).getUnidade().getMunicipio();
				else
					this.municipio = getUsuarioLogado().getServidorAtivo().getUnidade().getMunicipio();
				
				if ( municipio == null )
					addMensagemErro("O munícipio da sua unidade não foi localizado, por favor, entre em contato com o suporte.");

			} else {
				addMensagemErro("Não foi possível determinar o perfil (discente ou servidor) do usuário.");
			}
			
			if (hasOnlyErrors())
				return null;
			
			InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
			Collection<ProcessoSeletivoVestibular> processos = dao
					.findProcessosSeletivosNaoInscritos(getUsuarioLogado()
							.getPessoa().getId());
			switch (processos.size()) {
			case 0:
				InscricaoFiscalValidator.isFiscalInscrito(getUsuarioLogado()
						.getPessoa(), ps, erros);
				break;
			case 1:
				obj.setProcessoSeletivoVestibular(processos.iterator().next());
				carregaListaLocalAplicacao();
				this.processoSeletivoSetado = true;
				break;
			default:
				this.processoSeletivoSetado = false;
			}
			/* fim do trecho de código */
			
			// Está no período de inscrições?
			InscricaoFiscalValidator.validaPeriodoInscricao(erros);
	
			if (hasOnlyErrors()) {
				return null;
			}
			
			if (processoSeletivoSetado) {
				// se for de município onde não será aplicado prova, permite que o mesmo selecione o município
				if (getUsuarioLogado().getDiscenteAtivo() != null) {
					LocalAplicacaoProvaProcessoSeletivoDao daoM = getDAO(LocalAplicacaoProvaProcessoSeletivoDao.class);
					municipios = daoM.findMunicipiosByProcessoSeletivo(obj.getProcessoSeletivoVestibular());
					if (municipios != null && !municipios.contains(municipio)){
						this.selecionarMunicipio = true;
						this.municipio = new Municipio();
					}
				}
				validaProcessoSeletivo();
			}else{
				this.selecionarMunicipio = true;
				this.municipio = new Municipio();
			}
			
			if (hasOnlyErrors()) {
				return null;
			}
		}else
			novaInscricao = false;

		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_FISCAL);
		return forward("/vestibular/inscricao_fiscal.jsp");
	}

	/** Conclui a inscrição para a seleção de fiscais do vestibular.
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return {@see #getSubSistema()} ou /vestibular/menu_servidor.jsp
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String inscrever() throws SegurancaException, ArqException, NegocioException {
		validaProcessoSeletivo();		
		erros.addAll(obj.validate());
		if (obj.getIdFoto() == null)
			validateRequired(foto, "Foto 3x4", erros);
		else
			validateRequiredId(obj.getIdFoto(), "Foto 3x4", erros);
		if (hasErrors())
			return null;
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setObjMovimentado(obj);
		movimento.setObjAuxiliar(foto);
		movimento.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_FISCAL);
		execute(movimento);
		addMensagemInformation("Sua inscrição foi gravada com sucesso com o número "
				+ obj.getNumeroInscricao()
				+ ". Aguarde o processamento da seleção.");
		if (getUsuarioLogado().getDiscenteAtivo() != null)
			return getSubSistema().getForward();
		else
			return forward("/vestibular/menu_servidor.jsp");
	}
	
	/**
	 * Serve para realizar alterações na Incrição da Seleção dos Fiscais do Vestibular.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			if (foto != null ) {
				// remove a foto anterior
				if (obj.getIdFoto() != null) {
					EnvioArquivoHelper.removeArquivo(obj.getIdFoto());
				}
				int idFoto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idFoto,foto.getBytes(),
						foto.getContentType(), foto.getName());
				obj.setIdFoto( idFoto );
				obj.setPermiteAlterarFoto(true);
				getGenericDAO().updateFields(InscricaoFiscal.class, obj.getId(), 
						new String[] {"idFoto","statusFoto"}, 
						new Object[] {idFoto, StatusFoto.NAO_ANALISADA});
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Foto");
				return redirectJSF(getSubSistema().getLink());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** 
	 * Renderiza a foto no formulário de upload de foto.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @param g2d
	 * @param obj
	 * @throws IOException
	 */
	public void imagemFoto(Graphics2D g2d, Object obj) throws IOException {
		 BufferedImage imagem = ImageIO.read(foto.getInputStream());
		 double largura = imagem.getWidth(null);
		 double altura = imagem.getHeight(null);
		 largura = 150/largura;
		 altura = 200/altura;
		 AffineTransform xform = AffineTransform.getScaleInstance(largura, largura);
		 g2d.drawRenderedImage(imagem, xform);
     }

	/** Indica se exibe no formulário, a opção de viajar para outras cidades. 
	 * @return
	 */
	public boolean isExibeOutrasCidades() {
		return exibeOutrasCidades;
	}

	/** Indica se o processo seletivo para o qual vai se inscrever já está setado. 
	 * @return
	 */
	public boolean isProcessoSeletivoSetado() {
		return processoSeletivoSetado;
	}

	/** Seta se exibe no formulário, a opção de viajar para outras cidades. 
	 * @param exibeOutrasCidades
	 */
	public void setExibeOutrasCidades(boolean exibeOutrasCidades) {
		this.exibeOutrasCidades = exibeOutrasCidades;
	}

	/** Seta se o processo seletivo para o qual vai se inscrever já está setado. 
	 * @param processoSeletivoSetado
	 */
	public void setProcessoSeletivoSetado(boolean processoSeletivoSetado) {
		this.processoSeletivoSetado = processoSeletivoSetado;
	}
	
	/**
	 * Retorna todos os municipios do processos seletivos selecionado.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/inscricao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMunicipiosCombo() throws DAOException{
		if (municipios == null) {
			LocalAplicacaoProvaProcessoSeletivoDao dao = getDAO(LocalAplicacaoProvaProcessoSeletivoDao.class);
			municipios = dao.findMunicipiosByProcessoSeletivo(obj.getProcessoSeletivoVestibular());
		}
		return toSelectItems(municipios, "id", "nome");
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public boolean isSelecionarMunicipio() {
		return selecionarMunicipio;
	}

	public boolean isFotoPassivelModificacao() {
		return fotoPassivelModificacao;
	}

	public void setFotoPassivelModificacao(boolean fotoPassivelModificacao) {
		this.fotoPassivelModificacao = fotoPassivelModificacao;
	}

	public boolean isNovaInscricao() {
		return novaInscricao;
	}

	public void setNovaInscricao(boolean novaInscricao) {
		this.novaInscricao = novaInscricao;
	}

	public Fiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(Fiscal fiscal) {
		this.fiscal = fiscal;
	}

	public LocalAplicacaoProvaProcessoSeletivo getLocalProva() {
		return localProva;
	}

	public void setLocalProva(LocalAplicacaoProvaProcessoSeletivo localProva) {
		this.localProva = localProva;
	}
	
}