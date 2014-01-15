/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.sae.DiasAlimentacaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;
import br.ufrn.sigaa.assistencia.negocio.MovimentoDiasAlimentacao;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por cadastrar/editar os dias de alimentação de cada bolsa auxílio
 * 
 * @author agostinho
 *
 */
@Component("diasAlimentacaoMBean")
@Scope("request")
public class DiasAlimentacaoMBean extends SigaaAbstractController<DiasAlimentacao> implements OperadorDiscente {

	private List<String> selectedItemsCafe;
	private List<String> selectedItemsAlmoco;
	private List<String> selectedItemsJanta;
	
	private List<BolsaAuxilio> listagemBolsaAuxilio;
	private DiscenteAdapter discente;
	private BolsaAuxilio bolsaAuxilio;
	private AnoPeriodoReferenciaSAE anoRefSae;
	/**  Indicação da escolha do menu: tipo de bolsa auxílio . */
	private TipoBolsaAuxilio tipoBolsa;
	/** Parâmetros da busca: com deferimento. */
	private SituacaoBolsaAuxilio situacao;

	/**
	 * Instância os objetos quando inicia a busca de Discentes para que se localize a bolsa
	 * que está associada aquele discente.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.ear/sigaa.war/sae/DiasAlimentacao/view.jsp</ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String instanciar() throws DAOException, SegurancaException {

		obj = new DiasAlimentacao();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRAR_DIAS_ALIMENTACAO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Cadastra/Atualiza os dias da semana que um bolsista possui
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.war/sae/DiasAlimentacao/form.jsp</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
			
		List<DiasAlimentacao> listaDiasAlterados = new ArrayList<DiasAlimentacao>();
		List<DiasAlimentacao> diasAlimentacaosExistentes = getDAO(DiasAlimentacaoDao.class).findDiasAlimentacaoByBolsa(bolsaAuxilio.getId());
		
		if (diasAlimentacaosExistentes.isEmpty()) {

			DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
			
			TipoRefeicaoRU tipoCafe = dao.findByPrimaryKey(TipoRefeicaoRU.CAFE, TipoRefeicaoRU.class);
			TipoRefeicaoRU tipoAlmoco = dao.findByPrimaryKey(TipoRefeicaoRU.ALMOCO, TipoRefeicaoRU.class);
			TipoRefeicaoRU tipoJanta = dao.findByPrimaryKey(TipoRefeicaoRU.JANTA, TipoRefeicaoRU.class);
			
			DiasAlimentacao cafe = new DiasAlimentacao();
				cafe = definirDiasSelecionados(cafe, selectedItemsCafe);
				cafe.setTipoRefeicao(tipoCafe);
				cafe.setBolsaAuxilio(bolsaAuxilio);

			DiasAlimentacao almoco = new DiasAlimentacao();
				almoco = definirDiasSelecionados(almoco, selectedItemsAlmoco);
				almoco.setTipoRefeicao(tipoAlmoco);
				almoco.setBolsaAuxilio(bolsaAuxilio);
			
			DiasAlimentacao janta = new DiasAlimentacao();
				janta = definirDiasSelecionados(janta, selectedItemsJanta);
				janta.setTipoRefeicao(tipoJanta);
				janta.setBolsaAuxilio(bolsaAuxilio);
		
				listaDiasAlterados.add(cafe); listaDiasAlterados.add(almoco); listaDiasAlterados.add(janta);

				MovimentoDiasAlimentacao mov = new MovimentoDiasAlimentacao();
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_DIAS_ALIMENTACAO);
				mov.setDiasAlimentacao(listaDiasAlterados);
				execute(mov);
		}
		else {
			
			DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
			
			TipoRefeicaoRU tipoCafe = dao.findByPrimaryKey(TipoRefeicaoRU.CAFE, TipoRefeicaoRU.class);
			TipoRefeicaoRU tipoAlmoco = dao.findByPrimaryKey(TipoRefeicaoRU.ALMOCO, TipoRefeicaoRU.class);
			TipoRefeicaoRU tipoJanta = dao.findByPrimaryKey(TipoRefeicaoRU.JANTA, TipoRefeicaoRU.class);
			
			for (DiasAlimentacao dia : diasAlimentacaosExistentes) {
				
				if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.CAFE) {
					DiasAlimentacao diaDefinido = definirDiasSelecionados(dia, selectedItemsCafe);
						diaDefinido.setTipoRefeicao(tipoCafe);
						diaDefinido.setBolsaAuxilio(bolsaAuxilio);
						listaDiasAlterados.add(diaDefinido);
				}
				if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.ALMOCO) {
					DiasAlimentacao diaDefinido = definirDiasSelecionados(dia, selectedItemsAlmoco);
						diaDefinido.setTipoRefeicao(tipoAlmoco);
						diaDefinido.setBolsaAuxilio(bolsaAuxilio);
						listaDiasAlterados.add(diaDefinido);
				}
				if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.JANTA) {
					DiasAlimentacao diaDefinido = definirDiasSelecionados(dia, selectedItemsJanta);
						diaDefinido.setTipoRefeicao(tipoJanta);
						diaDefinido.setBolsaAuxilio(bolsaAuxilio);
						listaDiasAlterados.add(diaDefinido);
				}
			}
			
			bolsaAuxilio.setDiasAlimentacao(listaDiasAlterados);
						
			MovimentoDiasAlimentacao mov = new MovimentoDiasAlimentacao();
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_DIAS_ALIMENTACAO);
			mov.setDiasAlimentacao(bolsaAuxilio.getDiasAlimentacao());
			
			execute(mov);
		}		
		
		addMessage("Cadastro realizado com sucesso!", TipoMensagemUFRN.INFORMATION);	
		return forward("/sae/menu.jsf");
	}
	
	/**
	 * Retorna ao menu principal
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.war/sae/DiasAlimentacao/form.jsp</ul>
	 */
	@Override
	public String cancelar() {
		return forward("/sae/menu.jsf"); 
	}
	
	/**
	 * Seta o dias da semana que irão aparece na JSP marcados ou não de acordo
	 * com o estado do objeto que está vindo do banco.
	 * @param lista
	 */
	private void definirDiasExistentes(List<DiasAlimentacao> lista) {
		
		for (DiasAlimentacao dia : lista) {
		
			if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.CAFE) {
				if (dia.getSegunda())
					selectedItemsCafe.add(TipoRefeicaoRU.SEGUNDA);
				if (dia.getTerca())
					selectedItemsCafe.add(TipoRefeicaoRU.TERCA);
				if (dia.getQuarta())
					selectedItemsCafe.add(TipoRefeicaoRU.QUARTA);
				if (dia.getQuinta())
					selectedItemsCafe.add(TipoRefeicaoRU.QUINTA);
				if (dia.getSexta())
					selectedItemsCafe.add(TipoRefeicaoRU.SEXTA);
				if (dia.getSabado())
					selectedItemsCafe.add(TipoRefeicaoRU.SABADO);
				if (dia.getDomingo())
					selectedItemsCafe.add(TipoRefeicaoRU.DOMINGO);
			}
			if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.ALMOCO) {
				if (dia.getSegunda())
					selectedItemsAlmoco.add(TipoRefeicaoRU.SEGUNDA);
				if (dia.getTerca())
					selectedItemsAlmoco.add(TipoRefeicaoRU.TERCA);
				if (dia.getQuarta())
					selectedItemsAlmoco.add(TipoRefeicaoRU.QUARTA);
				if (dia.getQuinta())
					selectedItemsAlmoco.add(TipoRefeicaoRU.QUINTA);
				if (dia.getSexta())
					selectedItemsAlmoco.add(TipoRefeicaoRU.SEXTA);
				if (dia.getSabado())
					selectedItemsAlmoco.add(TipoRefeicaoRU.SABADO);
				if (dia.getDomingo())
					selectedItemsAlmoco.add(TipoRefeicaoRU.DOMINGO);
			}
			if (dia.getTipoRefeicao().getId() == TipoRefeicaoRU.JANTA) {
				if (dia.getSegunda())
					selectedItemsJanta.add(TipoRefeicaoRU.SEGUNDA);
				if (dia.getTerca())
					selectedItemsJanta.add(TipoRefeicaoRU.TERCA);
				if (dia.getQuarta())
					selectedItemsJanta.add(TipoRefeicaoRU.QUARTA);
				if (dia.getQuinta())
					selectedItemsJanta.add(TipoRefeicaoRU.QUINTA);
				if (dia.getSexta())
					selectedItemsJanta.add(TipoRefeicaoRU.SEXTA);
				if (dia.getSabado())
					selectedItemsJanta.add(TipoRefeicaoRU.SABADO);
				if (dia.getDomingo())
					selectedItemsJanta.add(TipoRefeicaoRU.DOMINGO);
			}
		}
	}
	
	/**
	 * De acordo com os parâmetros informados define o que será marcado como true/false
	 * para os dia de semana.
	 *  
	 * @param dia
	 * @param lista
	 * @return
	 */
	private DiasAlimentacao definirDiasSelecionados(DiasAlimentacao dia, List<String> lista) {
		
		if (lista.isEmpty()) {
			dia.setSegunda(false);
			dia.setTerca(false);
			dia.setQuarta(false);
			dia.setQuinta(false);
			dia.setSexta(false);
			dia.setSabado(false);
			dia.setDomingo(false);
		}
		for (String idSemana : lista) {
			if (idSemana.equals(TipoRefeicaoRU.SEGUNDA))
				dia.setSegunda(true); 
			if (!lista.contains(TipoRefeicaoRU.SEGUNDA))
				dia.setSegunda(false);
			
			if (idSemana.equals(TipoRefeicaoRU.TERCA))
				dia.setTerca(true); 
			if (!lista.contains(TipoRefeicaoRU.TERCA))
				dia.setTerca(false);
			
			if (idSemana.equals(TipoRefeicaoRU.QUARTA))
				dia.setQuarta(true); 
			if (!lista.contains(TipoRefeicaoRU.QUARTA))
				dia.setQuarta(false);
			
			if (idSemana.equals(TipoRefeicaoRU.QUINTA))
				dia.setQuinta(true); 
			if (!lista.contains(TipoRefeicaoRU.QUINTA))
				dia.setQuinta(false);
			
			if (idSemana.equals(TipoRefeicaoRU.SEXTA))
				dia.setSexta(true); 
			if (!lista.contains(TipoRefeicaoRU.SEXTA))
				dia.setSexta(false);
			
			if (idSemana.equals(TipoRefeicaoRU.SABADO))
				dia.setSabado(true); 
			if (!lista.contains(TipoRefeicaoRU.SABADO))
				dia.setSabado(false);
			
			if (idSemana.equals(TipoRefeicaoRU.DOMINGO))
				dia.setDomingo(true);
			if (!lista.contains(TipoRefeicaoRU.DOMINGO))
				dia.setDomingo(false);
		}
		return dia;
	}
	
	public String iniciarRelatorioMapaAcessoRU() throws DAOException {
	    anoRefSae = getAnoPeriodoReferenciaSAE();
	    tipoBolsa = new TipoBolsaAuxilio();
	    situacao = new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
		return forward("/sae/DiasAlimentacao/busca.jsf");
	}
	
	/**
	 * Gera o mapa de acesso ao RU. Esse mapa serve para indicar quais discentes terão acesso
	 * gratuitamente ao restaurante de acordo com os dias e tipos de refeições. 
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>sigaa.ear/sigaa.war/sae/menu.jsp</ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String relatorioMapaAcessoRU() throws HibernateException, DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);

		try {
			if ( !isEmpty(anoRefSae.getAno()) && !isEmpty(anoRefSae.getPeriodo()) ) {
				listagemBolsaAuxilio = dao.findDiscentesDiasAlimentacaoMapa(null, anoRefSae, tipoBolsa.getId(), situacao.getId());
			} else {
				addMensagemErro("Informe corretamente o ano e/ou período.");
				return null;
			}
			if (listagemBolsaAuxilio == null)
				listagemBolsaAuxilio = new ArrayList<BolsaAuxilio>();

			dao.initialize(tipoBolsa);
			dao.initialize(situacao);

		} finally {
			dao.close();
		}
		
		return forward("/sae/DiasAlimentacao/relatorio_mapa_acesso.jsf");
	}
	
	/**
	 * Instância/limpa os objetos usados na classe
	 */
	private void clear() {
		selectedItemsCafe = new ArrayList<String>();
		selectedItemsAlmoco = new ArrayList<String>();
		selectedItemsJanta = new ArrayList<String>();
		obj = new DiasAlimentacao();
	}
	
	/**
	 * Localiza os dias de alimentação de acordo com a bolsa selecionada
	 * e exibe o form para cadastramento/alteração de dias 
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/sae/busca_bolsa_auxilio.jsp </ul>
	 * @param discente
	 * @param bolsa 
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public String iniciarCadastroDiasAlimentacao(Discente discente, BolsaAuxilio bolsa) throws HibernateException, ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_DIAS_ALIMENTACAO);
		prepareMovimento(SigaaListaComando.ATUALIZAR_DIAS_ALIMENTACAO);
		clear();
		this.discente = discente;
		
		bolsaAuxilio = bolsa;
		
		if ( ValidatorUtil.isEmpty( bolsaAuxilio )) {
			int tipoBolsa = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ALIMENTACAO );
			bolsaAuxilio = buscarBolsaByDiscente(this.discente, tipoBolsa);
		}
		
		if ( bolsaAuxilio.getId() == 0 ) {
			addMensagemWarning("Esse aluno não possui bolsa auxílio para o Ano/Período que foi definido pelo SAE.");
			return null;
		}
		else {
			DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
			try {
				definirDiasExistentes(dao.findDiasAlimentacaoByBolsa(bolsaAuxilio.getId()));
			} finally {
				dao.close();
			}
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
		}
	}

	/**
	 * Localiza a bolsa auxílio de acordo com o discente selecionado
	 * @param discente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private BolsaAuxilio buscarBolsaByDiscente(DiscenteAdapter discente, int tipoBolsa) throws HibernateException, DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
		BolsaAuxilio bolsaAuxilio = null;
		try {
			AnoPeriodoReferenciaSAE calendarioReferenciaSAE = getAnoPeriodoReferenciaSAE();
			bolsaAuxilio = dao.findBolsaAuxilio(discente.getPessoa().getCpf_cnpj(), discente.getPessoa().getPassaporte(), 
					calendarioReferenciaSAE, discente.getPessoa().isInternacional(), tipoBolsa); // sempre só vai existir um calendário
		} finally {
			dao.close();
		}
		return bolsaAuxilio;
	}

	private BolsaAuxilio buscarLastBolsaByDiscente(DiscenteAdapter discente) throws HibernateException, DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
		BolsaAuxilio bolsaAuxilio = dao.findBolsaAuxilioDiscente(discente.getId()); 
		return bolsaAuxilio;
	}
	
	
	/**
	 * Retorna o Ano/Período de referência que foi definido pelo SAE.
	 * Sempre só vai existir um Ano/Período de referência.
	 * @return
	 * @throws DAOException 
	 */
	private AnoPeriodoReferenciaSAE getAnoPeriodoReferenciaSAE() throws DAOException {
		ArrayList<AnoPeriodoReferenciaSAE> calendarioReferenciaSAE = (ArrayList<AnoPeriodoReferenciaSAE>) getDAO(GenericDAOImpl.class).findAll(AnoPeriodoReferenciaSAE.class);
		return calendarioReferenciaSAE.get(0);
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public String selecionaDiscente() throws ArqException {
		return null;
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {		
	}

	public List<String> getSelectedItemsCafe() {
		return selectedItemsCafe;
	}

	public void setSelectedItemsCafe(List<String> selectedItemsCafe) {
		this.selectedItemsCafe = selectedItemsCafe;
	}

	public List<String> getSelectedItemsAlmoco() {
		return selectedItemsAlmoco;
	}

	public void setSelectedItemsAlmoco(List<String> selectedItemsAlmoco) {
		this.selectedItemsAlmoco = selectedItemsAlmoco;
	}

	public List<String> getSelectedItemsJanta() {
		return selectedItemsJanta;
	}

	public void setSelectedItemsJanta(List<String> selectedItemsJanta) {
		this.selectedItemsJanta = selectedItemsJanta;
	}

	public List<BolsaAuxilio> getListagemBolsaAuxilio() {
		return listagemBolsaAuxilio;
	}

	public void setListagemBolsaAuxilio(List<BolsaAuxilio> listagemBolsaAuxilio) {
		this.listagemBolsaAuxilio = listagemBolsaAuxilio;
	}

	public BolsaAuxilio getBolsaAuxilio() {
		return bolsaAuxilio;
	}

	public void setBolsaAuxilio(BolsaAuxilio bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}

	public AnoPeriodoReferenciaSAE getAnoRefSae() {
		return anoRefSae;
	}

	public void setAnoRefSae(AnoPeriodoReferenciaSAE anoRefSae) {
		this.anoRefSae = anoRefSae;
	}

	public TipoBolsaAuxilio getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaAuxilio tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public SituacaoBolsaAuxilio getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoBolsaAuxilio situacao) {
		this.situacao = situacao;
	}
	
}