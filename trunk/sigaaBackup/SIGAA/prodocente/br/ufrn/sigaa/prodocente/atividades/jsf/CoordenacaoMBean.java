/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/01/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Coordenacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class CoordenacaoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.Coordenacao> {
	public CoordenacaoMBean() {
		obj = new Coordenacao();
		obj.setServidor(new Servidor());
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Coordenacao.class, "id", "descricao");
	}

	public String cadastrarCurso() throws NegocioException, ArqException {
		obj.setTipoCursoEspecializacao('C');
		return super.cadastrar();
	}

	public String cadastrarEspecializacao() throws NegocioException, ArqException {
		obj.setTipoCursoEspecializacao('E');
		return super.cadastrar();
	}

	@Override
	protected void afterCadastrar() {
		obj = new Coordenacao();
	}
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(new int[]{SigaaPapeis.PRODOCENTE_PPG, SigaaPapeis.GESTOR_LATO});
	}
}