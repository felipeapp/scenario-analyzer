/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/11/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TipoTrabalhoConclusaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * MBean responsável pela geração de collections da classe
 * SituacaoTurma  para exibição em JSPs
 * 
 * @author Ricardo Wendell
 *
 */
@Component("tipoTrabalhoConclusao") @Scope("request")
//Suppress necessário por que nesse caso não há forma de parametrizar a superclasse
@SuppressWarnings("unchecked")
public class TipoTrabalhoConclusaoMBean extends SigaaAbstractController {

	public Collection<SelectItem> getCombo() throws DAOException {
		TipoTrabalhoConclusaoDao tipoDao = getDAO( TipoTrabalhoConclusaoDao.class );
		return toSelectItems( tipoDao.findByNivelEnsino(getNivelEnsino()) , "id", "descricao");
	}

}
