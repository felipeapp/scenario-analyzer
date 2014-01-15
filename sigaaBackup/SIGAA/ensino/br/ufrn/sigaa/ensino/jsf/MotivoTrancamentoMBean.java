/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jul 3, 2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;

/**
 * MBean que tem as funções que retornam comboboxes de objetos
 * MotivoTrancamento existentes no banco de dados.
 * @author Victor Hugo
 */
@Component("motivoTrancamento")
@Scope("request")
public class MotivoTrancamentoMBean extends SigaaAbstractController<MotivoTrancamento> {

	public MotivoTrancamentoMBean() {
		obj = new MotivoTrancamento();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(MotivoTrancamento.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(MotivoTrancamento.class, "id", "descricao");
	}
	
	public Collection<SelectItem> getAllExibirCombo() {
		return toSelectItems(getAllExibir(),"id","descricao");
	}	
	
	/**
	 * Retorna todos os motivos de trancamento
	 * @return
	 */
	public Collection<MotivoTrancamento> getAllExibir() {
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			return dao.findByExactField(MotivoTrancamento.class, "exibir", true);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<MotivoTrancamento>();
		} finally {
			if(dao != null){
				dao.close();
			}
		}
	}

}
