/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controlador para gerenciar a organização das apresentações de trabalhos em painéis no
 * Congresso de Iniciação Científica.
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
	 * Construtor padrão
	 */
	public OrganizacaoPaineisCICMBean() {
		clear();
	}

	/**
	 * Instancia o obj e os campos que serão utilizados no caso de uso
	 */
	private void clear() {
		obj = new OrganizacaoPaineisCIC();
		obj.setCongresso(new CongressoIniciacaoCientifica());
		diaApresentacaoCentro = new DiaApresentacaoCentro();
	}
	
	/**
	 * Popula os dados necessários para a geração da numeração dos painéis de resumos do CIC
	 * e encaminha para o formulário onde os parâmetros da geração serão definidos.
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
			addMensagemWarning("Já foi gerada uma numeração para os painéis de resumos do congresso atual ("+ obj.getCongresso().getDescricaoResumida() +
				").<br/> Caso confirme esta operação, a numeração anterior será descartada e uma nova numeração será gerada.");
		prepareMovimento(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC);
		setOperacaoAtiva(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC.getId());
		return forward(JSP_FORM_ORGANIZACAO);
	}
	
	/**
	 * Esse método tem como finalidade adicionar um dia de apresentação ao centro.
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
				erros.addErro("Centro/Unidade já adicionado(a).");
		if(hasErrors())	return null;
		
		ResumoCongressoDao dao = getDAO(ResumoCongressoDao.class);
		
		List<Integer> trabalhosCentro = dao.findTrabalhosAprovadosCentro(obj.getCongresso().getId(), diaApresentacaoCentro.getCentro().getId());
		if(trabalhosCentro == null || trabalhosCentro.isEmpty()){
			erros.addErro("O centro/unidade informado(a) não possui trabalhos aprovados.");
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
	 * Invoca o processador para persistir a organização das apresentações definidas e gerar a numeração dos painéis.
	 * <br/><br/>
	 * JSP: /pesquisa/OrganizacaoPaineisCIC/form_organizacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String gerarNumeracaoResumos() throws ArqException {
		if(!checkOperacaoAtiva(SigaaListaComando.GERAR_NUMERACAO_PAINEIS_RESUMO_CIC.getId()))
			return cancelar();
		
		erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(obj.getNumeroPaineis(), "Número de painéis", erros);
		ValidatorUtil.validateEmptyCollection("Informe a organização da apresentação dos trabalhos dos centros/unidades", obj.getDiasApresentacao(), erros);
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
		
		addMensagemInformation("Numeração dos Painéis gerada com sucesso!");
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