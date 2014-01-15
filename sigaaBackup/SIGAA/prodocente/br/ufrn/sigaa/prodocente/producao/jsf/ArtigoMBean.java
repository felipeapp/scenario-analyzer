/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("artigo")
@Scope("session")
public class ArtigoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.Artigo> {


	public ArtigoMBean() {
		obj = new Artigo();
	}

	@Override
	public void afterAtualizar() throws ArqException {
		super.afterAtualizar();
		
		if (obj.getTipoPeriodico() == null) {
			obj.setTipoPeriodico(new TipoPeriodico());
		}
		
		if (obj.getTipoParticipacao() == null) {
			obj.setTipoParticipacao(new TipoParticipacao());
		}
		
		if (obj.getTipoRegiao() == null) {
			obj.setTipoRegiao(new TipoRegiao(TipoRegiao.NAO_INFORMADO));
		}
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(Artigo.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES);
		obj.setServidor(getServidorUsuario());
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		obj = new Artigo();
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(Artigo) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES);
	}

	@Override
	public Collection getDadosEspecificoDaProducao() throws DAOException {
		ProducaoDao dao = getDAO(ProducaoDao.class);
		return dao.findProducaoProjection(Artigo.class.getSimpleName(),getServidorUsuario(), getPaginacao(), 0, 0, 0);
	}
	
}