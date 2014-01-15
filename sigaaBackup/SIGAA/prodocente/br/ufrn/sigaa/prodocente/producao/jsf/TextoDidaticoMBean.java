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
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("textoDidatico")
@Scope("session")
public class TextoDidaticoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico> {
	public TextoDidaticoMBean() {
		obj = new TextoDidatico();
		obj.setTextoDiscussao(false);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TextoDidatico.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.TEXTO_DIDATICO);
		obj.setServidor(getServidorUsuario());
		
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new TextoDidatico();
	}


	@Override
	public void popularObjeto(Producao p) {
		obj =(TextoDidatico) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.AUDIO_VISUAIS);
	}
}
