package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.CriterioSolicitacaoRenovacao;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRenovacaoBolsa;

@Component @Scope("request")
public class CriterioSolicitacaoRenovacaoMBean extends SigaaAbstractController<CriterioSolicitacaoRenovacao> {

	public CriterioSolicitacaoRenovacaoMBean() {
		clear();
	}

	private void clear() {
		obj = new CriterioSolicitacaoRenovacao();
		obj.setSituacaoBolsa(new SituacaoBolsaAuxilio());
		obj.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		obj.setTipoRenovacao(new TipoRenovacaoBolsa());
	}

	@Override
	public String getDirBase() {
		return "/sae/CriterioSolicitacaoRenovacao";
	}

	@Override
	public String listar() throws ArqException {
		all = null;
		return super.listar();
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		return super.preCadastrar();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		erros.addAll(obj.validate());
		if (hasErrors())
			return null;
		
		Collection<CriterioSolicitacaoRenovacao> criterios = getGenericDAO().
			findByExactField(CriterioSolicitacaoRenovacao.class, 
				new String[] { "situacaoBolsa.id", "tipoBolsaAuxilio.id", "tipoRenovacao.id", "ativo" }, 
				new	Object[] { obj.getSituacaoBolsa().getId(), obj.getTipoBolsaAuxilio().getId(), obj.getTipoRenovacao().getId(), Boolean.TRUE });
		
		if ( !criterios.isEmpty() ) {
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Critério de Renovação de Bolsa");
			return null;
		}

		return super.cadastrar();
	}
	
	@Override
	public Collection<CriterioSolicitacaoRenovacao> getAll() throws ArqException {
		if ( all == null || all.isEmpty() ) {
			all = getGenericDAO().findByExactField(CriterioSolicitacaoRenovacao.class, "ativo", 
					Boolean.TRUE, "ASC", "tipoBolsaAuxilio.denominacao");
		}
		return all;
	}

	public Collection<SelectItem> getAllTipoRenovacao() throws ArqException {
		return toSelectItems(getGenericDAO().findAll(TipoRenovacaoBolsa.class), "id", "descricao") ;
	}
	
}