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
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoSociedade;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("participacaoSociedade")
@Scope("session")
public class ParticipacaoSociedadeMBean	extends	AbstractControllerProdocente<ParticipacaoSociedade> {
	public ParticipacaoSociedadeMBean() {
		obj = new ParticipacaoSociedade();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(ParticipacaoSociedade.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setTipoProducao(TipoProducao.PARTICIPACAO_SOCIEDADE_CIENTIFICA_CULTURAIS);
		obj.setServidor(getServidorUsuario());
		obj.setTitulo("");
	}

	@Override
	protected void afterCadastrar() {
		obj = new ParticipacaoSociedade();
	}


	@Override
	public void popularObjeto(Producao p) {
		obj =(ParticipacaoSociedade) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.PARTICIPACAO_SOCIEDADE_CIENTIFICA_CULTURAIS);
	}
}
