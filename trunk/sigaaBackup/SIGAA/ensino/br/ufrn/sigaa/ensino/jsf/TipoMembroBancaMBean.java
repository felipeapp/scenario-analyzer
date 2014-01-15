/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoMembroBanca;

/**
 * MBean responsável por recuperar as informações dos tipos de membros de bancas disponíveis
 * 
 * @author arlindo
 *
 */
@Component @Scope("request")
public class TipoMembroBancaMBean extends SigaaAbstractController<TipoMembroBanca> {
	
	/**
	 * Retorna uma coleção de SelecItem de tipos de membros da banca
	 * (Docente interno, Examinador externo, etc.)
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/membros.jsp</li>
     * </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTipoMembroCombo() throws DAOException {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>(0);
		tipos.add(new SelectItem(TipoMembroBanca.EXAMINADOR_INTERNO, "Examinador Docente Interno"));
		tipos.add(new SelectItem(TipoMembroBanca.EXAMINADOR_EXTERNO_A_INSTITUICAO, "Examinador Externo à Instituição"));
		return tipos;
	}	

}
