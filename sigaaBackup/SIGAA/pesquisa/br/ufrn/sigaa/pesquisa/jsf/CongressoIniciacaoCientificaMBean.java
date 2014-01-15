/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2008
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.RestricaoEnvioResumoCIC;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;

/**
 * MBean para realizar o CRUD de Congresso de Iniciação Científica
 * @author Leonardo Campos
 *
 */
@Component("congressoIniciacaoCientifica") @Scope("request")
public class CongressoIniciacaoCientificaMBean extends SigaaAbstractController<CongressoIniciacaoCientifica> {

	private RestricaoEnvioResumoCIC restricao;
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		Collection<CongressoIniciacaoCientifica> congressosCIC = getGenericDAO().findAllAtivos(CongressoIniciacaoCientifica.class, "ano");
		for (CongressoIniciacaoCientifica congressoIniciacaoCientifica : congressosCIC) {
			if (congressoIniciacaoCientifica.isAtivo() && congressoIniciacaoCientifica.getId() != obj.getId()) {
				getGenericDAO().updateField(CongressoIniciacaoCientifica.class, congressoIniciacaoCientifica.getId(), "ativo", false);
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		super.remover();
		int temAtivo = 0, todosInativos = 0;
		Collection<CongressoIniciacaoCientifica> congressosCIC = getGenericDAO().findAll(CongressoIniciacaoCientifica.class, "ano", "asc");
		for (CongressoIniciacaoCientifica congressoIniciacaoCientifica : congressosCIC) {
			if (congressoIniciacaoCientifica.isAtivo()) 
				temAtivo = congressoIniciacaoCientifica.getId();
			else
				todosInativos = congressoIniciacaoCientifica.getId();
		}
		if (temAtivo != 0) 
			return null;
		else
			getGenericDAO().updateField(CongressoIniciacaoCientifica.class, todosInativos, "ativo", true);
		return null;	
	}
	
	public CongressoIniciacaoCientificaMBean() {
		obj = new CongressoIniciacaoCientifica();
		restricao = new RestricaoEnvioResumoCIC();
	}
	
	public void adicionarRestricao() throws DAOException {
		if ( restricao.isCota() )
			ValidatorUtil.validateRequiredId(restricao.getCotaBolsa().getId(), "Cota", erros);
		else if ( !restricao.isCota() ) {
			ValidatorUtil.validateRequiredId(restricao.getTipoBolsa().getId(), "Bolsa de Pesquisa", erros);
			if (isEmpty( restricao.getDataInicial() ) || isEmpty( restricao.getDataFinal() ))
				ValidatorUtil.validateRequired(null, "Período Execução Bolsa", erros);
			if ( !isEmpty( restricao.getDataInicial() ) || !isEmpty( restricao.getDataFinal() ))
			ValidatorUtil.validaOrdemTemporalDatas(restricao.getDataInicial(), restricao.getDataFinal(), false, "Período Execução Bolsa", erros);
		}
		
		if (hasOnlyErrors()) { 
			addMensagens(erros);
			return;
		}
		
		if ( restricao.getCotaBolsa().getId() > 0 ) {
			restricao.setDataInicial(null);
			restricao.setDataFinal(null);
			restricao.setTipoBolsa(null);
			getGenericDAO().initialize(restricao.getCotaBolsa());
		} else if ( restricao.getTipoBolsa().getId() > 0 ) {
			if (isEmpty(restricao.getDataInicial()) || isEmpty(restricao.getDataFinal())) {
				ValidatorUtil.validateRequired(null, "Período Execução Bolsa", erros);
				addMensagens(erros);
				return;
			}
			restricao.setCotaBolsa(null);
			getGenericDAO().initialize(restricao.getTipoBolsa());
		}
		restricao.setCongresso(obj);
		obj.getRestricoes().add(restricao);
		restricao = new RestricaoEnvioResumoCIC();
	}
	
	/**
	 * Retorna para a tela de listagem desde que se trate de uma atualização, 
	 * caso contrário retorna para a tela do módulo de Pesquisa.
	 */
	@Override
	public String cancelar() {
		if ( getConfirmButton().equals("Alterar") ) {
			try {
				return listar();
			} catch (ArqException e) {
				return tratamentoErroPadrao(e);
			}
		} else {
			return super.cancelar();
		}
	}
	
	public void removerRestricao() {
		restricao.setId( getParameterInt("id", 0) );
		restricao.setTipoBolsa( getParameterInt("idTipoBolsa", 0) == 0 ? null : new TipoBolsaPesquisa(getParameterInt("idTipoBolsa", 0)));
		restricao.setCotaBolsa( getParameterInt("idCotaBolsa", 0) == 0 ? null : new CotaBolsas(getParameterInt("idCotaBolsa", 0)));
		obj.removerRestricao(restricao);
		restricao = new RestricaoEnvioResumoCIC();
	}
	
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {

		if (obj.getId() > 0 ) {
			Collection<RestricaoEnvioResumoCIC> restricoes = getGenericDAO().findByExactField(RestricaoEnvioResumoCIC.class, "congresso.id", obj.getId());
			for (RestricaoEnvioResumoCIC restricao : restricoes) {
				if ( !obj.getRestricoes().contains(restricao) )
					getGenericDAO().remove(restricao);
			}
		}
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/congresso_iniciacao_cientifica/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/congresso_iniciacao_cientifica/lista.jsf";
	}
	
	public RestricaoEnvioResumoCIC getRestricao() {
		return restricao;
	}

	public void setRestricao(RestricaoEnvioResumoCIC restricao) {
		this.restricao = restricao;
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		Collection<SelectItem> comboCongresso = new ArrayList<SelectItem>();
		Collection<CongressoIniciacaoCientifica> congressos = getGenericDAO().findAll(CongressoIniciacaoCientifica.class, "id", "desc");
		
		for (CongressoIniciacaoCientifica cic : congressos)
			comboCongresso.add(new SelectItem(cic.getId(), cic.getDescricao()));
		
		return comboCongresso;
	}

}