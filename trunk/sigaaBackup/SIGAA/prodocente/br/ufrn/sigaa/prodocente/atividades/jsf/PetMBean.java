/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.ProgramaEducacaoTutorialDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Controlador responsável pelo cadastro de Programas
 * de Educação Tutorial
 * 
 * @author wendell
 *
 */

@Component("petBean") @Scope("request")
public class PetMBean extends SigaaAbstractController<ProgramaEducacaoTutorial> {
	
	Collection<ProgramaEducacaoTutorial> grupos;
	
	public PetMBean() {
		clear();
	}

	private void clear() {
		obj = new ProgramaEducacaoTutorial();
		grupos = null;
	}

	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		String forward = super.preCadastrar();
		prepareMovimento(SigaaListaComando.CADASTRAR_PET);
		return forward;
	}
	
	@Override
	public String atualizar() throws ArqException {
		String forward = super.atualizar();
		prepareMovimento(SigaaListaComando.CADASTRAR_PET);
		return forward;
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		
		// Validar dados
		ListaMensagens erros = obj.validate();
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}
		
		// Persistir
		obj.setCodMovimento(SigaaListaComando.CADASTRAR_PET);
		execute(obj);
		addMensagemInformation("Programa de Educação Tutorial cadastrado com sucesso!");
		
		return forward(getListPage());
	}
	
	@Override
	public String inativar() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.CADASTRAR_PET);

		// Inativar 
		populateObj(true);
		obj.setAtivo(false);
		
		// Persistir
		obj.setCodMovimento(SigaaListaComando.CADASTRAR_PET);
		execute(obj);
		addMensagemInformation("Programa de Educação Tutorial removido com sucesso!");
		
		clear();
		return forward(getListPage());
	}
	
	@Override
	public Collection<ProgramaEducacaoTutorial> getAllAtivos() {
		if ( grupos == null ) {
			grupos = getDAO(ProgramaEducacaoTutorialDao.class).findAllAtivos();
		}
		return grupos;
	}
	
	public Collection<SelectItem> getAllAtivosCombo() {
		return toSelectItems(getAllAtivos(), "id", "descricao");
	}
	
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.CDP);
	}
	
	@Override
	public String getDirBase() {
		return "/prodocente/atividades/Pet";
	}
}
