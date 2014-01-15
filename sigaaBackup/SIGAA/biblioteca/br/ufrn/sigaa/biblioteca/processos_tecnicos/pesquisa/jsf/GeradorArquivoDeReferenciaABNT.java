/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/12/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 *
 * <p> Classe que implementa o m�todo que gera o arquivo de refer�ncia para a ABNT, j� que esse 
 * m�todo � gerado de v�rias partes do sistema. </p>
 * 
 * @author jadson
 *
 */
public class GeradorArquivoDeReferenciaABNT {
	
	/**
	 * Gera o arquivo de refer�ncia para a ABNT para o contexto atual do JSF.
	 *
	 * @param titulos
	 * @throws IOException
	 * @throws DAOException
	 */
	public void gerarArquivoContextoAtualFaces(List<CacheEntidadesMarc> titulos) throws IOException, DAOException{
		
		long tempo = System.currentTimeMillis();
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		List<TituloCatalografico> titulosGeracaoFormatoReferencia = new ArrayList<TituloCatalografico>();
		
		for (CacheEntidadesMarc cache : titulos) {
			titulosGeracaoFormatoReferencia.add( new TituloCatalografico(cache.getIdTituloCatalografico()) );
		}
		
		
		String formatoReferencia = new FormatosBibliograficosUtil().gerarFormatosReferencia(titulosGeracaoFormatoReferencia, false);
		
		// Trata a exibi��o de negrito, no caso como a sa�da � uma arquivo texto normal, n�o tem. //
		if(StringUtils.notEmpty(formatoReferencia)){
			formatoReferencia = formatoReferencia.replaceAll(FormatosBibliograficosUtil.INICIO_NEGRITO, "");
			formatoReferencia = formatoReferencia.replaceAll(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
		}
		
		response.setContentType("Content-Type: text/html; charset=ISO2709");
		response.addHeader("Content-Disposition", "attachment; filename=resultado_pesquisa_ABNT.txt");
		
		byte[] buf = formatoReferencia.toString().getBytes();
		ServletOutputStream out = response.getOutputStream();
		out.write(buf);
		
		out.flush();
		out.close();
		
		FacesContext.getCurrentInstance().responseComplete();
		
		System.out.println("Gera��o arquivo para "+titulos.size()+" t�tulos demorou: "+ (System.currentTimeMillis()-tempo)+" ms" );
		
	}
	
}
