/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 01/10/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.DiaApresentacaoCentro;
import br.ufrn.sigaa.pesquisa.dominio.OrganizacaoPaineisCIC;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoAvaliacaoResumo;

/**
 * Controlador para gerenciar a organiza��o das apresenta��es de trabalhos em pain�is no
 * Congresso de Inicia��o Cient�fica.
 * 
 * @author Leonardo Campos
 *
 */
@Component("organizacaoPaineis") @Scope("request")
public class OrganizacaoPaineisCICMBean extends SigaaAbstractController<OrganizacaoPaineisCIC> {

	public final String JSP_FORM_ORGANIZACAO = "/pesquisa/OrganizacaoPaineisCIC/form_organizacao.jsf";
	public final String JSP_RESUMO_ORGANIZACAO = "/pesquisa/OrganizacaoPaineisCIC/resumo_organizacao.jsf";
	
	private DiaApresentacaoCentro diaApresentacaoCentro;
	
	/**
	 * Construtor padr�o
	 */
	public OrganizacaoPaineisCICMBean() {
		clear();
	}

	/**
	 * Instancia o obj e os campos que ser�o utilizados no caso de uso
	 */
	private void clear() {
		obj = new OrganizacaoPaineisCIC();
		obj.setCongresso(new CongressoIniciacaoCientifica());
		diaApresentacaoCentro = new DiaApresentacaoCentro();
	}
	
	/**
	 * Popula os dados necess�rios para a gera��o da numera��o dos pain�is de resumos do CIC
	 * e encaminha para o formul�rio onde os par�metros da gera��o ser�o definidos.
	 * <br/><br/>
	 * JSP: /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA);
		clear();
		obj.setCongresso(getDAO(CongressoIniciacaoCientificaDao.class).findAtivo());
		Collection<OrganizacaoPaineisCIC> col = getGenericDAO().findByExactField(OrganizacaoPaineisCIC.class, "congresso.id", obj.getCongresso().getId());
		if(col != null && !col.isEmpty())
			addMensagemWarning("J� foi gerada uma numera��o para os pain�is de resumos do congresso atual ("+ obj.getCongresso().getDescricaoResumida() +
				").<br/> Caso confirme esta opera��o, a numera��o anterior ser� descartada e uma nova numera��o ser� gerada.");
		prepareMovimento(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC);
		setOperacaoAtiva(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC.getId());
		return forward(JSP_FORM_ORGANIZACAO);
	}
	
	/**
	 * Esse m�todo tem como finalidade adicionar um dia de apresenta��o ao centro.
	 * <br/><br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/OrganizacaoPaineisCIC/form_organizacao.jsp  
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String adicionarDiaApresentacaoCentro() throws DAOException{
		erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(diaApresentacaoCentro.getCentro().getId(), "Centro/Unidade", erros);
		for(DiaApresentacaoCentro d: obj.getDiasApresentacao())
			if(d.getCentro().getId() == diaApresentacaoCentro.getCentro().getId())
				erros.addErro("Centro/Unidade j� adicionado(a).");
		if(hasErrors())	return null;
		
		ResumoCongressoDao dao = getDAO(ResumoCongressoDao.class);
		
		List<Integer> trabalhosCentro = dao.findTrabalhosAprovadosCentro(obj.getCongresso().getId(), diaApresentacaoCentro.getCentro().getId());
		if(trabalhosCentro == null || trabalhosCentro.isEmpty()){
			erros.addErro("O centro/unidade informado(a) n�o possui trabalhos aprovados.");
			hasErrors();
			return null;
		}
		diaApresentacaoCentro.setListaTrabalhos( trabalhosCentro );
		diaApresentacaoCentro.setNumeroTrabalhos(trabalhosCentro.size());
		diaApresentacaoCentro.setCentro( dao.findByPrimaryKey(diaApresentacaoCentro.getCentro().getId(), Unidade.class));
		diaApresentacaoCentro.setOrganizacao(obj);
		
		obj.getDiasApresentacao().add(diaApresentacaoCentro);
		Collections.sort(obj.getDiasApresentacao());
		diaApresentacaoCentro = new DiaApresentacaoCentro();
		
		return null;
	}
	
	/**
	 * Invoca o processador para persistir a organiza��o das apresenta��es definidas e gerar a numera��o dos pain�is.
	 * <br/><br/>
	 * JSP: /pesquisa/OrganizacaoPaineisCIC/form_organizacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String gerarNumeracaoResumos() throws ArqException {
		if(!checkOperacaoAtiva(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC.getId()))
			return cancelar();
		
		erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(obj.getNumeroPaineis(), "N�mero de pain�is", erros);
		ValidatorUtil.validateEmptyCollection("Informe a organiza��o da apresenta��o dos trabalhos dos centros/unidades", obj.getDiasApresentacao(), erros);
		if(hasErrors()) return null;
		
		MovimentoAvaliacaoResumo mov = new MovimentoAvaliacaoResumo();
		mov.setCodMovimento(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		addMensagemInformation("Numera��o dos Pain�is gerada com sucesso!");
		removeOperacaoAtiva();
		
		return cancelar();
	}

	public DiaApresentacaoCentro getDiaApresentacaoCentro() {
		return diaApresentacaoCentro;
	}

	public void setDiaApresentacaoCentro(DiaApresentacaoCentro diaApresentacaoCentro) {
		this.diaApresentacaoCentro = diaApresentacaoCentro;
	}

}