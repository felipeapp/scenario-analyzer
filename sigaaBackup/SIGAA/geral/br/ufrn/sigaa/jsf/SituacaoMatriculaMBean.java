/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

public class SituacaoMatriculaMBean extends AbstractControllerCadastro<SituacaoMatricula> {

	public SituacaoMatriculaMBean() {
		clear();
	}

	private void clear(){
		obj = new SituacaoMatricula();
	}
	
	@Override
	public String cancelar() {
		resetBean();
		forward(getListPage());
		return null;
	}

	@Override
	public Collection<SelectItem> getAllCombo(){
		return  getAllAtivo(SituacaoMatricula.class, "id", "descricao");
	}

	public Collection<SelectItem> getAproveitamentosCombo(){
		GenericDAO dao = getGenericDAO();
		
		dao.setSistema(getSistema());
		try {
			Collection<SituacaoMatricula> situacoes = dao.findAllAtivos(SituacaoMatricula.class, "descricao");
			situacoes.retainAll(SituacaoMatricula.getSituacoesAproveitadas());
			return toSelectItems(situacoes, "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}
	}

	/**
	 * Para o usuário ser direcionado para a tela de listagem logo após um cadastro. 
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	
	@Override
	public Collection<SituacaoMatricula> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<SituacaoMatricula> mesmaSituacao = dao.findByExactField(SituacaoMatricula.class, 
					"descricao", obj.getDescricao());
		for (SituacaoMatricula as : mesmaSituacao) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Situação Matricula");
				return null;
			}
		}
		return super.cadastrar();
	}
	
}