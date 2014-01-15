/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/13 - 18:19:53
 */
package br.ufrn.sigaa.ava.struts;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;

/**
 * Action que lista os arquivos da pasta selecionada no porta-arquivos.
 * 
 * @author David Pereira
 *
 */
public class ListaArquivosAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class, req);
		String idPasta = req.getParameter("idPasta");
		
		try {
			if (idPasta != null) {
				
				List<ArquivoUsuario> arquivos = dao.findArquivosByPasta(Integer.parseInt(idPasta), getUsuarioLogado(req).getId());
				
				// Para uso no EXT, a data deve estar no formato ano/mês/dia.
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				
				res.setContentType("application/xml"); 
				res.getWriter().write("<dataset>\n");
				for (ArquivoUsuario arq : arquivos) {
					
					String nome = arq.getNome();

					nome = nome.replaceAll("&", "&amp;");
					nome = StringUtils.toAscii(nome);
					
					res.getWriter().write("\t<arquivo>\n");
					res.getWriter().write("\t\t<id>"+arq.getId()+"</id>\n");
					res.getWriter().write("\t\t<nome>"+nome+"</nome>\n");
					res.getWriter().write("\t\t<tamanho>"+Math.round(arq.getTamanho()/1024.0)+"</tamanho>\n");
					res.getWriter().write("\t\t<dataarquivo>"+sdf.format(arq.getData())+"</dataarquivo>\n");
					res.getWriter().write("\t\t<idArquivo>"+arq.getIdArquivo()+"</idArquivo>\n");
					res.getWriter().write("\t\t<key>"+UFRNUtils.generateArquivoKey(arq.getIdArquivo())+"</key>\n");
					res.getWriter().write("\t</arquivo>\n");
				}
				res.getWriter().write("</dataset>\n");
			}
			
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
