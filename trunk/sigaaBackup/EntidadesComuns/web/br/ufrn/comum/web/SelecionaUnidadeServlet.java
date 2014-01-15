/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIPAC
 * Criado em: 25/02/2008
 */
package br.ufrn.comum.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * @author Itamir Filho
 *
 */
public class SelecionaUnidadeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		//recebendo o parâmetro
		String codUnidade = req.getParameter("codUnidade");
		
		if(codUnidade != null && !codUnidade.trim().equals("")){
			
			//fazendo o parse do codigo
			StringBuffer codUndStr = new StringBuffer();
			String[] codUndArr = codUnidade.split("");
			for(int i = 0 ; i< codUndArr.length ; i++){
				if(codUndArr[i] != ""  && !codUndArr[i].equals("."))
					codUndStr.append(codUndArr[i]);
			}
			
			UnidadeDAOImpl undDAO = new UnidadeDAOImpl(Sistema.COMUM);
			try {
				UnidadeGeral und = null;
				res.setContentType("text/plain");
				PrintWriter out = res.getWriter();
				
				und = undDAO.findByCodigo(Long.parseLong(codUndStr.toString()));
				
				if(und != null){
					if(!und.isOrganizacional())
						out.write("INFORME UMA UNIDADE ORGANIZACIONAL.");
					else	
						out.write(und.getCodigoNome());
				}else
					out.write("UNIDADE NÃO ENCONTRADA.");
				
			} catch (DAOException e) {
				e.printStackTrace();
			}finally{
				undDAO.close();
			}
		}
	}
	

}
