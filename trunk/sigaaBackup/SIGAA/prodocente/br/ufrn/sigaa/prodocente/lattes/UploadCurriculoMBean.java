/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.lattes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.xml.sax.SAXParseException;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensProdocente;
import br.ufrn.sigaa.prodocente.negocio.CurriculoLattesMov;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Managed bean para importação do currículo Lattes. 
 * 
 * @author David Pereira
 *
 */
public class UploadCurriculoMBean extends SigaaAbstractController<Producao> {

	/** Componente utilizado no upload do arquivo para importação */
	private UploadedFile arquivo;
	
	/** Ano de referência da importação */
	private String anoReferencia = String.valueOf(Curriculo.ANO_REFERENCIA);
	
	/**
	 * Realiza a importação das produções contidas no arquivo Lattes informado.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/producao/ImportLattes/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String importarCurriculo() throws ArqException {
		
		checkDocenteRole();
		prepareMovimento(SigaaListaComando.IMPORTAR_CURRICULO_LATTES);
		ImportLattesDao dao = getDAO(ImportLattesDao.class);
		
		try {
			Curriculo c = new Curriculo(arquivo, dao);
			List<? extends Producao> producoes = c.interpretar();
						
			if (producoes.isEmpty()) {
				addMensagem(MensagensProdocente.ARQUIVO_NAO_POSSUI_PRODUCOES);
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (Producao p : producoes) {
				sb.append("----------------------\n");
				sb.append("Tipo: " + p.getClass().getSimpleName());
				sb.append(" - Titulo: " + p.getTitulo());
				sb.append(" - Ano Ref.: " + p.getAnoReferencia());
				sb.append(" - Seq. Producao: " + p.getSequenciaProducao() + "\n");
			}
			CurriculoLattesMov mov = new CurriculoLattesMov();
			mov.setProducoes(producoes);
			mov.setServidor(getServidorUsuario());
			mov.setPessoa(getUsuarioLogado().getPessoa());
			mov.setConteudo(sb.toString());
			mov.setXml(new String(arquivo.getBytes()));
			mov.setCodMovimento(SigaaListaComando.IMPORTAR_CURRICULO_LATTES);
			
			mov.setAnoReferencia(Integer.parseInt(anoReferencia));
			execute(mov);
			
			if (mov.isProducoesNaoImportadas()) 
				addMensagemWarning("Existem produções não importadas por causa do ano de referência.");
			
			if (mov.getProducoes().isEmpty())
				addMessage("Nenhuma produção foi importada!", TipoMensagemUFRN.ERROR);
			else
				addMessage("Dados importados com sucesso!", TipoMensagemUFRN.INFORMATION);
			
		} catch(IOException e) { 
			addMensagemErro("O arquivo enviado é inválido!");
			return null;
		} catch(SAXParseException e) {
			addMensagemErro("O arquivo enviado é inválido!");
			return null;
		} catch(Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}
	
		return forward("/portais/docente/docente.jsp");
	}
	
	/**
	 * Retorna os anos de referências validos.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/producao/ImportLattes/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<SelectItem> getAnosPossiveis() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for (int ano = CalendarUtils.getAnoAtual() - 20; ano <= CalendarUtils.getAnoAtual(); ano++)
			itens.add(new SelectItem(String.valueOf(ano), String.valueOf(ano)));
		return itens;
	}
	
	public UploadedFile getArquivo() {
		return arquivo;
	}
	
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public String getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(String anoReferencia) {
		this.anoReferencia = anoReferencia;
	}
	
	
}
