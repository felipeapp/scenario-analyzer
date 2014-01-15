/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("participacaoComissaoOrgEventos")
@Scope("session")
public class ParticipacaoComissaoOrgEventosMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos> {
	public ParticipacaoComissaoOrgEventosMBean() {
		obj = new ParticipacaoComissaoOrgEventos();

	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ParticipacaoComissaoOrgEventos.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.PARTICIPACAO_COMISSAO_ORGANIZACAO_EVENTO);
		obj.setServidor(getServidorUsuario());
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setTitulo(""); //se o título for nulo da erro de constraint no banco.

	}

	@Override
	protected void afterCadastrar() {
		obj = new ParticipacaoComissaoOrgEventos();
	}


	@Override
	public void popularObjeto(Producao p) {
		obj =(ParticipacaoComissaoOrgEventos) p;
	}

}
