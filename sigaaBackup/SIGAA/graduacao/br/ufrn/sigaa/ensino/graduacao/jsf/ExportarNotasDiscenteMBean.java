package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Managed Bean respons�vel por gerenciar a opera��o de exportar os dados
 * do discente como notas, componentes no formato CSV. 
 * @author M�rio Rizzi
 *
 */
@Component("exportarNotasDiscente") @Scope("request") 
public class ExportarNotasDiscenteMBean extends SigaaAbstractController<Object> {
	
	/** Atributo que define o ano selecionado na busca. */
	private Integer ano;
	/** Atributo que define o per�odo selecionado na busca. */
	private Integer periodo;
	
	/** Atributo que define a JSP que realizar� busca para exporta��o. */
	private static final String JSP_FORM_BUSCA = "form_exporta_notas_discente.jsp";
	
	public ExportarNotasDiscenteMBean() {
		clear();
	}
	
	/**
	 * M�todo que inicializa todos os atributos envolvidos nos casos
	 * de uso gerenciados pela classe. 
	 * M�todo n�o invocado por JSP's.
	 */
	private void clear(){
		if( isEmpty(getAno()) )
			setAno(getCalendarioVigente().getAno());
	}
	
	/**
	 * M�todo que verifica o papel permitido para acessar os casos de uso
	 * gerenciados pela classe.
	 * M�todo n�o invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO);
	}
	
	/**
	 * M�todo n�o invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getDirBase()
	 */
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return super.getDirBase();
	}
	
	/**
	 * M�todo n�o invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/relatorios/" + JSP_FORM_BUSCA;
	}
	
	/**
	 * M�todo que inicia o formul�rio de gera��o do arquivo CSV.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){
		clear();
		return forward( getFormPage() );
	}

    /**
     * Gerar o relat�rio no formato CSV.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/form_exporta_notas_discente.jsp</li>
	 * </ul>
     * @return
     * @throws DAOException
     * @throws IOException 
     * @throws JRException 
     * @throws SQLException 
     * @throws SegurancaException 
     */
    public String gerarArquivo() throws DAOException, JRException, IOException, SQLException, SegurancaException {

    	checkChangeRole();
   	  	validate();
   	  	
   	  	
   	  	Collection<Map<String, Object>> listaMapas = getDAO(MatriculaComponenteDao.class).
   	  	exportarNotasDiscente(getCursoAtualCoordenacao().getId(), ano, periodo);
   	  	
   	  	if( isEmpty(listaMapas) )
   	  		addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Notas dos Discentes");
   	  	
        if (hasErrors())
			return null;
		
        String nomeArquivo =  "notas_discentes_" + ano + ".csv";
		
        getCurrentResponse().setContentType("text/csv");
		getCurrentResponse().setCharacterEncoding("iso-8859-15");
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
		
		PrintWriter out = getCurrentResponse().getWriter();
		
		out.println("UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE");
		out.println("SISTEMA INTEGRADO DE GEST�O DE ATIVIDADES ACAD�MICAS");
		out.println("RELAT�RIO DE NOTAS SEMESTRAIS");
		out.println("CURSO;"+getCursoAtualCoordenacao().getDescricao());
		out.println("ANO;"+ano);
		out.println();
		
		out.println( collectionToCSV(listaMapas) );
		FacesContext.getCurrentInstance().responseComplete();
  
        return null;
    }
    
    /**
	 * Transforma um ResultSet em um HashMap para uso em relat�rios
	 * M�todo n�o utilizado por JSP's.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private String collectionToCSV(Collection<Map<String, Object>> collection) throws SQLException {

		List<Map<String, Object>> lista = CollectionUtils.toList(collection);
		StringBuilder sb = new StringBuilder();
		String campo = "";
		campo = "Per�odo;C�digo;Nome;Turma;Siape;Hor�rio;Matr�cula;Nota;Situa��o;";
		sb.append(campo);
		sb.append("\n");
		for (Map<String, Object> map : lista) {
			
			String periodo = map.get("Per�odo") != null ? map.get("Per�odo").toString() : "-";
			String codigo = map.get("C�digo") != null ? map.get("C�digo").toString() : "-";
			String nome = map.get("Nome") != null ? map.get("Nome").toString() : "-";
			String turma = map.get("Turma") != null ? map.get("Turma").toString() : "-";
			String siape = map.get("Siape") != null ? map.get("Siape").toString() : "-";
			String horario = map.get("Hor�rio") instanceof Date ? Formatador.getInstance().formatarData((Date) map.get("Hor�rio")) : map.get("Hor�rio").toString();
			String matricula = map.get("Matr�cula") != null ? map.get("Matr�cula").toString() : "-";
			String nota = map.get("Nota") != null ? map.get("Nota").toString() : "-";
			String situacao = map.get("Situa��o") != null ? map.get("Situa��o").toString() : "-";

			campo = periodo+";"+codigo+";"+nome+";"+turma+";"+siape+";"+horario+";"+matricula+";"+nota+";"+situacao+";";
			campo = campo.replace("\n", " ");
			campo = campo.replace("\r", " ");
			
			sb.append(campo);
			sb.append("\n");
		}
		
	

		return sb.toString();

	}
	
	/**
	 * Retorna o ano selecionado na busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/form_exporta_notas_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * Indica o ano na busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/form_exporta_notas_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o per�odo selecionado na busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/form_exporta_notas_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/**
	 * Indica o periodo na busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/relatorios/form_exporta_notas_discente.jsp</li>
	 * </ul>
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
	/**
	 * M�todo que valida se os campos da busca foram preenchidos.
	 * M�todo n�o invocado por JSP's.
	 */
	private void validate(){
		
		if( isEmpty(getAno()) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
	}
	
}
