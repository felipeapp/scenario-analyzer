
package br.ufrn.shared.wireless.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.shared.wireless.dao.WirelessDAO;

/**
 * Action para consulta de autenticação pela rede wireless
 * 
 * @author David Pereira
 *
 */
public class ConsultaWirelessAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ConsultaWirelessForm wForm = (ConsultaWirelessForm) form;
		UsuarioDAO uDao = new UsuarioDAO();
		WirelessDAO wDao = new WirelessDAO();
		
		try {
			switch(wForm.getAcao()) {
			case 22: //ConstantesAction.CONFIRMAR:
			
				if (wForm.getTipoConsulta() == ConsultaWirelessForm.CONSULTA_USUARIO) { // Consulta por usu‡rios
					UsuarioGeral u = uDao.findByLogin(wForm.getUsuario().getLogin());
					if (u == null) {
						addErro("cadastro.usuario.busca.login", req);
						return mapping.findForward("sucesso");
					}
					
					List<Object[]> resultado = wDao.findConexoes(u, null, null);
					req.setAttribute("resultado", resultado);
					
				} else { // Consulta por intervalo de datas
					Date dataInicio = Formatador.getInstance().parseDate(wForm.getDataInicio());
					Date dataFim = Formatador.getInstance().parseDate(wForm.getDataFim());
					
					if (dataInicio == null) {
						addErro("compras.liquidacao.relatorios.dataInicioInvalida", req);
						return mapping.findForward("sucesso");
					}
					
					if (dataFim == null) {
						addErro("compras.liquidacao.relatorios.dataFimInvalida", req);
						return mapping.findForward("sucesso");
					}
					
					if (dataFim.before(dataInicio)) {
						addErro("compras.liquidacao.relatorios.dataInicioMaiorFim", req);
						return mapping.findForward("sucesso");
					}
					
					List<Object[]> resultado = wDao.findConexoes(null, dataInicio, dataFim);
					req.setAttribute("resultado", resultado);
					
				}
			
				return mapping.findForward("sucesso");
			case  24: //ConstantesAction.CANCELAR:
				return mapping.findForward("cancelar");
			}
		} finally {
			uDao.close();
			wDao.close();
		}
		
		return mapping.findForward("sucesso");
	
	}
	
}
