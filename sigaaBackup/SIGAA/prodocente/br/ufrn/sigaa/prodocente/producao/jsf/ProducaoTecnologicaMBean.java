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

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
public class ProducaoTecnologicaMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica> {
	public ProducaoTecnologicaMBean() {
		obj = new ProducaoTecnologica();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProducaoTecnologica.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		obj.setAnoReferencia(CalendarUtils.getAno(obj.getDataProducao()));
		
		super.beforeCadastrarAndValidate();
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		//tem o tipo participação igual a texto discussão
		return getTipoParticipacao(TipoProducao.TEXTO_DISCUSSAO);
	}
	

}
