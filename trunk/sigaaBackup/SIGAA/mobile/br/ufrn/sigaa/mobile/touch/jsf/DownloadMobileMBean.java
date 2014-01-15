/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/05/2012'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;

/**
 * MBean para auxiliar o download de arquivos em dispositivos móveis.
 * 
 * @author Renan de Oliveira
 *
 */
@Component("downloadMobile")
@Scope("request")
public class DownloadMobileMBean extends ControllerTurmaVirtual {

		
	/** Construtor Padrão.
	 * */
	public DownloadMobileMBean () {				
	}

	/**
	 * Método utilizado para registrar os downloads feitos na Turma Virtual do SIGAA Web Mobile
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\ava\topico_aula_discente.jsp</li>
	 *    <li>sigaa.war\mobile\touch\ava\topico_aula_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void registrarDownload () throws ArqException {		
		Integer id = getParameterInt("id", 0);
		//Integer tid = Integer.parseInt(getCurrentRequest().getParameter("tid"));
		//String key = UFRNUtils.generateArquivoKey(id);
		
		registrarAcao(EnvioArquivoHelper.recuperaNomeArquivo(id), EntidadeRegistroAva.ARQUIVO, AcaoAva.ACESSAR, id);
		
		//redirect("/verArquivo?idArquivo=" + id + "&key=" + key + "&salvar=true");
		
		registrarLogAcessoDiscenteTurmaVirtual(Arquivo.class.getName(), id, turma().getId());
	}


}