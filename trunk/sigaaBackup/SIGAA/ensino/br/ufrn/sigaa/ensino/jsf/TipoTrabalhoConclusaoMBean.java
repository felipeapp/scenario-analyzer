/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 * MBean respons�vel pela gera��o de collections da classe
 * SituacaoTurma  para exibi��o em JSPs
 * 
 * @author Ricardo Wendell
 *
 */
@Component("tipoTrabalhoConclusao") @Scope("request")
//Suppress necess�rio por que nesse caso n�o h� forma de parametrizar a superclasse
@SuppressWarnings("unchecked")
public class TipoTrabalhoConclusaoMBean extends SigaaAbstractController {

	public Collection<SelectItem> getCombo() throws DAOException {
		TipoTrabalhoConclusaoDao tipoDao = getDAO( TipoTrabalhoConclusaoDao.class );
		return toSelectItems( tipoDao.findByNivelEnsino(getNivelEnsino()) , "id", "descricao");
	}

}
