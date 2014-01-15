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

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("capitulo")
@Scope("session")
public class CapituloMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.Capitulo> {
	public CapituloMBean() {
		obj = new Capitulo();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Capitulo.class, "id", "descricao");
	}
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.CAPITULO_LIVROS);
		obj.setServidor(getServidorUsuario());

		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		obj = new Capitulo();
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(Capitulo) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES);
	}

}
