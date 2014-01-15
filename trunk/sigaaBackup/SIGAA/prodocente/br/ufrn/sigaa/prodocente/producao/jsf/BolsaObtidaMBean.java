/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.batik.ext.awt.image.codec.ForwardSeekableStream;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.prodocente.BolsaObtidaDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Managed Bean para o caso de uso de Bolsas Obtidas da Produção Intelectual
 * 
 * Gerado pelo CrudBuilder
 */
@Component("bolsaObtida")
@Scope("session")
public class BolsaObtidaMBean extends AbstractControllerProdocente<BolsaObtida> {

	private static final String URL_LISTA_PRODUTIVIDADE = "/prodocente/producao/BolsaObtida/lista_produtividade.jsp";

	private boolean produtividade;

	/**
	 * Campos utilizados para filtrar a consulta de bolsas
	 */
	private boolean filtroDepartamento;
	private boolean filtroServidor;
	private boolean filtroAno;

	private Unidade departamento = new Unidade();
	private Servidor servidor = new Servidor();
	private Integer ano;
	private Integer mesInicial;
	private Integer anoInicial;
	private Integer mesFinal;
	private Integer anoFinal;

	public BolsaObtidaMBean() {
		clear();
	}

	/**
	 * Iniciar cadastro de bolsistas de produtividade
	 * 
	 * Chamada por:
	 * /prodocente/producao/BolsaObtida/lista_produtividade.jsp
	 * /WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 *
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarBolsaProdutividade() throws ArqException, RemoteException, NegocioException {
		clear();
		produtividade = true;
		obj.setInstituicaoFomento( new InstituicaoFomento(InstituicaoFomento.CNPQ) );

		return preCadastrar();
	}

	/**
	 * Reinicia os valores do objeto de domínio.
	 */
	private void clear() {
		boolean produtividade = this.produtividade;

		obj = new BolsaObtida();
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setTipoBolsa(new TipoBolsaProdocente());
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setAnoReferencia( CalendarUtils.getAnoAtual() );

		setProdutividade(produtividade);
		if ( produtividade ) {
			obj.setInstituicaoFomento( new InstituicaoFomento(InstituicaoFomento.CNPQ) );
		}

		filtroAno = true;
		ano = CalendarUtils.getAnoAtual();
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTitulo("");
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setTipoProducao(TipoProducao.BOLSA_OBTIDA);

		if (produtividade) {
			obj.setInstituicaoFomento( new InstituicaoFomento(InstituicaoFomento.CNPQ) );
		} else {
			obj.setServidor(getServidorUsuario());
		}

		// sete a data da produção como a data de início.
		//Isso tem grande impacto pois o relatório funciona anualmente, e acaba sem trazer as produções caso essas datas não sejam setadas.
//		obj.setDataProducao(obj.getPeriodoInicio());
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		if(obj.getTipoBolsa() != null && obj.getTipoBolsa().getId() > 0){
			TipoBolsaProdocente tipoBolsa = new TipoBolsaProdocente();
			try {
				tipoBolsa = getGenericDAO().findByPrimaryKey(obj.getTipoBolsa().getId(), TipoBolsaProdocente.class);
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
			}
			if(tipoBolsa != null && tipoBolsa.isProdutividade())
				addMensagemWarning("A bolsa registrada é de PRODUTIVIDADE, por isso somente será válida a título de pontuação após a validação pela PROPESQ.");
		}
		clear();
		obj = new BolsaObtida();
		resetBean();
	}

	/**
	 * Chamada por:
	 * /prodocente/producao/BolsaObtida/form.jsp
	 * /prodocente/producao/BolsaObtida/lista_produtividade.jsp
	 *  
	 */
	@Override
	public String cancelar() {
		
		if (forwardCancelar != null){
			return forward(forwardCancelar);
		}
		
		if ( produtividade ) {
			return redirectJSF(getSubSistema().getLink());
		} else {
			return super.cancelar();
		}
	}

	/**
	 * Chamada por:
	 * /prodocente/producao/BolsaObtida/lista.jsp
	 * /prodocente/producao/BolsaObtida/lista_produtividade.jsp
	 *  
	 */
	@Override
	public String remover() {
		boolean produtividade = this.produtividade;
		setForwardRemover(false);
		super.remover();
		if (produtividade) {
			return forward(URL_LISTA_PRODUTIVIDADE);
		} else {
			return redirectJSF(getUrlRedirecRemover());
		}
	}

	/**
	 * Filtrar a as bolsas de produtividade
	 *
	 * Chamada por:
	 * /prodocente/producao/BolsaObtida/lista_produtividade.jsp
	 * WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String filtrarProdutividade() throws DAOException {
		produtividade = true;
		BolsaObtidaDao bolsaDao = getDAO( BolsaObtidaDao.class );

		validarFiltros();
		if (hasErrors()) {
			return null;
		}

		Collection<BolsaObtida> lista = bolsaDao.findBolsistasProdutividade(
				(filtroDepartamento ? departamento : null),
				(filtroServidor ? servidor : null),
				(filtroAno ? ano : null) );

		if (!lista.isEmpty()) {
			getCurrentRequest().setAttribute("lista", lista);
		}

		return forward(URL_LISTA_PRODUTIVIDADE);
	}

	/**
	 * Realizar validação das informações
	 */
	private void validarFiltros() {
		ListaMensagens erros = new ListaMensagens();
		if (filtroDepartamento) {
			ValidatorUtil.validateRequired(departamento, "Departamento", erros);
		}
		if (filtroServidor) {
			ValidatorUtil.validateRequired(servidor, "Docente", erros);
		}
		if (filtroAno) {
			ValidatorUtil.validaInt(ano, "Ano", erros);
		}

		addMensagens(erros);
	}
	
	/**
	 * Chamada por:
	 * /prodocente/nova_producao.jsp
	 * /prodocente/producao/BolsaObtida/lista.jsp
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new BolsaObtida();
		obj.getInstituicaoFomento().setId(InstituicaoFomento.UFRN);
		obj.setTipoBolsa(new TipoBolsaProdocente());
		obj.setServidor(new Servidor());
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		setDirBase("/prodocente/producao/");
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(getFormPage());
		}
	}
	
	private void montarData(){
		if ( mesInicial == null || anoInicial == null || mesFinal == null || anoFinal == null ) {
			 addMensagemErro("Período: Campo não informado.");
		} else if ( !(mesInicial >= 1) || !(mesInicial <= 12) || !(anoInicial <= 1900) && !(anoInicial <= CalendarUtils.getAnoAtual() + 5) ) {
			addMensagemErro("Período Inicial: Data Informada Incorretamente.");
		} else if ( !(mesFinal >= 1) && !(mesFinal <= 12) && !(anoFinal <= 1900) && !(anoFinal <= CalendarUtils.getAnoAtual() + 20) ) {
			addMensagemErro("Período Final: Data Informada Incorretamente.");
		} else { 
			obj.setPeriodoInicio( CalendarUtils.createDate(01, mesInicial - 1, anoInicial) );
			obj.setPeriodoFim( CalendarUtils.createDate(01, mesFinal - 1, anoFinal) );
			
			if ( obj.getPeriodoInicio().after(obj.getPeriodoFim())) {
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Período");
			}
		}			
	}
	
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		anoInicial = CalendarUtils.getAno( obj.getPeriodoInicio() );
		mesInicial = (CalendarUtils.getMesByData( obj.getPeriodoInicio() ) + 1);
		anoFinal = CalendarUtils.getAno( obj.getPeriodoFim() );
		mesFinal = (CalendarUtils.getMesByData( obj.getPeriodoFim() ) + 1);
		return forward(getFormPage());
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		montarData();
		if (hasErrors())
			return null;
		
		super.cadastrar();
		
		if (hasErrors()) {
			return null;
		} else {
			if (isPesquisa()) {
				return cancelar();
			} else {
				return preCadastrar(); 
			}
		}
	}

	/**
	* Método não é invocado por jsp
	*/
	public void popularObjeto(BolsaObtida p) {
		obj = p;
	}

	/**
	* Método não é invocado por jsp
	*/
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.BOLSA_OBTIDA);
	}

	/**
	* Método não é invocado por jsp
	*/
	public Collection<SelectItem> getAllCombo() {
		return getAll(BolsaObtida.class, "id", "descricao");
	}

	public boolean isProdutividade() {
		return this.produtividade;
	}

	public void setProdutividade(boolean produtividade) {
		this.produtividade = produtividade;
	}

	public Integer getAno() {
		return this.ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Unidade getDepartamento() {
		return this.departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public boolean isFiltroAno() {
		return this.filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroDepartamento() {
		return this.filtroDepartamento;
	}

	public void setFiltroDepartamento(boolean filtroDepartamento) {
		this.filtroDepartamento = filtroDepartamento;
	}

	public boolean isFiltroServidor() {
		return this.filtroServidor;
	}

	public void setFiltroServidor(boolean filtroServidor) {
		this.filtroServidor = filtroServidor;
	}

	public Integer getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}

	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Integer getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}
	
}