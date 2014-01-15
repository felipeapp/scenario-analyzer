/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por chamar o Jasper do relatório
 * de discentes de pós-graduação e prazo de conclusão.
 * 
 * @author leonardo
 *
 */
@Component("relatorioPrazoMaximoPosBean")
@Scope("request")
public class RelatorioPrazoMaximoPosMBean extends SigaaAbstractController<Object> {

	/** Define o valor para o formato PDF do relatório.  */
	private static final String FORMATO_PDF = "pdf";
	/** Define o valor para o formato XLS do relatório.  */
	private static final String FORMATO_XLS = "xls";
	
	/** Unidade ao qual o relatório se restringe. */
	private Unidade unidade = new Unidade();
	
	/** Nome do arquivo Jasper do relatório. */
	private String nomeRelatorio;
	
	/** Tipo de discentes do relatório (REGULAR/ESPECIAL)*/
	private int tipoDiscente;
	/** Percentual atual do Progresso */
	private int percentual;
	/** Valor total do progresso */
	private int total;
	/** Indica se a barra de progresso está ativo ou não */
	private boolean ativo;
	/** Indica o texto a ser exibido na barra de progresso */
	private String textoProgresso;
	/** Indica o formato de saída do relatório. */
	private String formato;
	
	
	/** Construtor padrão. */
	public RelatorioPrazoMaximoPosMBean() {}

	/** 
	 * Inicia a geração do relatório.
	 * <br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/stricto/menu_coordenador.jsp</li>
	 *   <li>/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG );
		
		unidade = new Unidade();
		ativo = false;
		total = 0;
		percentual = 0;		
		nomeRelatorio = "trf8426_PosPrazoMaximo";
		tipoDiscente = Discente.REGULAR;
		formato = FORMATO_PDF;
		
		return forward("/stricto/relatorios/form_prazo_maximo.jsf");
		
	}
	
	/**
	 * Realiza os Cálculos para Gera o relatório de discentes de pós-graduação ativos e respectivos prazos de conclusão.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerarRelatorio() throws ArqException {
		
		//Verificação quando usuário acessa a partir do módulo do portal coordenação de stricto-sensu
		if( getProgramaStricto() !=  null)
			unidade = getProgramaStricto();
		
		if( isEmpty(unidade) || unidade.getId() <= 0 ){
			addMensagemErroAjax("Selecione o programa.");
			textoProgresso = "Geração Cancelada...";
			return null;
		}
					
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		try {
			Collection<DiscenteStricto> discentes = dao.findAtivosByPrograma(unidade.getId(),tipoDiscente);
			try {
					prepareMovimento(SigaaListaComando.CALCULAR_PRAZO_CONCLUSAO_DISCENTE_STRICTO_EM_LOTE);
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setCodMovimento( SigaaListaComando.CALCULAR_PRAZO_CONCLUSAO_DISCENTE_STRICTO_EM_LOTE);
					mov.setColObjMovimentado(discentes);
					execute(mov);
			} catch (NegocioException ex) {
				ex.printStackTrace();
				addMensagens(ex.getListaMensagens());
			}		
			
			textoProgresso = "Concluído...";
        } finally {
           if (dao != null)
        	   dao.close();
        }
		return null;
	}
	
	/**
	 * Gera o relatório de discentes de pós-graduação ativos e respectivos prazos de conclusão.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	public String exibirListagem() throws ArqException{
		
		gerarRelatorio();
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		try{
			// Gerar relatório        	
	        // Popular parâmetros do relatório
	        HashMap<String, Object> parametros = new HashMap<String, Object>();
	        parametros.put("unidade", unidade.getId() );
	        parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
	        parametros.put("tipoDiscente", tipoDiscente);
	
	        // Preencher relatório            
	        List<Map<String, Object>> listaPrazoMaximo = dao.findPrazoMaximoConclusaoByPrograma(unidade.getId(),tipoDiscente);
			JRDataSource jrds = new JRBeanCollectionDataSource(listaPrazoMaximo);
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper"), parametros, jrds);                        
	
	        // Exportar relatório de acordo com o formato escolhido
	        String nomeArquivo = nomeRelatorio + "." + formato;	        
			
			if(formato.equals(FORMATO_PDF)){
				getCurrentResponse().setContentType("application/" + formato);
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo+"\"");
				JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());	        
			}else
				JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
			
	        FacesContext.getCurrentInstance().responseComplete();
			
		} catch (Exception e) {
	        e.printStackTrace();
	        notifyError(e);
	        addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
	        return null;		
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
	
	/** 
	 * Retorna a unidade ao qual o relatório se restringe. 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a unidade ao qual o relatório se restringe.
	 * Método não invocado por JSP.
	 * @param unidade
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Retorna o nome do arquivo Jasper do relatório. 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	/** Seta o nome do arquivo Jasper do relatório.
	 * Método não invocado por JSP.
	 * @param nomeRelatorio
	 */
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	/** Retorna o tipo de discentes do relatório (REGULAR/ESPECIAL).
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getTipoDiscente() {
		return tipoDiscente;
	}

	/** Seta o tipo de discentes do relatório (REGULAR/ESPECIAL).
	 * Método não invocado por JSP.
	 * @param tipoDiscente
	 */
	public void setTipoDiscente(int tipoDiscente) {
		this.tipoDiscente = tipoDiscente;
	}

	/**
	 * Indica o percentual atual do progresso da ação.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPercentual() {
		return percentual;
	}

	/**
	 * Método não invocado por JSP.
	 * @param percentual
	 */
	public void setPercentual(int percentual) {
		this.percentual = percentual;
	}

	/**
	 * Método não invocado por JSP.
	 * @return
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Método não invocado por JSP.
	 * @param total
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Indica se ação está em progresso.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * Método não invocado por JSP.
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Indica o prgresso após ter selecionado a geração do relatório.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getTextoProgresso() {
		return textoProgresso;
	}

	/**
	 * Método não invocado por JSP.
	 * @param textoProgresso
	 */
	public void setTextoProgresso(String textoProgresso) {
		this.textoProgresso = textoProgresso;
	}

	/**
	 * Retorna o formato de saída do relatório selecionado. 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/**
	 * Método não invocado por JSP.
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}
	
	/**
	 * Popula um componente com os formatos de geração de relatório disponíveis.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/stricto/relatorios/form_prazo_maximo.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getDescricaoFormato(){
		
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		
		itens.add(new SelectItem(FORMATO_PDF,"PDF"));
		itens.add(new SelectItem(FORMATO_XLS, "XLS"));
		
		return itens;
		
	}
	
}
