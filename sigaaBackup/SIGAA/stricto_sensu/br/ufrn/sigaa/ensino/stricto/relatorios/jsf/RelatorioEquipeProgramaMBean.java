/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 27/05/2008
 *
 */

package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;

/**
 * MBean que auxilia na geração do relatório de Equipe do Programa Stricto Sensu.
 * @author Victor Hugo
 */
@Component("relatorioEquipePrograma") @Scope("session")
public class RelatorioEquipeProgramaMBean extends RelatoriosStrictoMBean {

	/** Constantes que indicam o tipo de docente 
     *  0 - docentes internos
     *  1 - docentes externos
     * -1 - todos
     */	
	private static final int DOCENTE_INTERNO = 0;
	
	private static final int DOCENTE_EXTERNO = 1;
	
	private static final int TODOS_DOCENTES = -1;
	
	/** Indica qual o tipo do membro selecionado */
    private int tipoMembros;

    /** Lista de Tipos de Membros */
    private List<SelectItem> tiposMembros = null;

    /** Inicializa os tipos de membros */
    public void inicializarTiposMembros(){
    	tiposMembros = new ArrayList<SelectItem>();
    	tiposMembros.add( new SelectItem(TODOS_DOCENTES, "Todos") );
    	tiposMembros.add( new SelectItem(DOCENTE_INTERNO, "Docentes Internos") );
    	tiposMembros.add( new SelectItem(DOCENTE_EXTERNO, "Docentes Externos") );
    }

    /** Nome do relatório a ser gerado */
    public static final String NOME_RELATORIO = "trf6366_Stricto_Sensu_Equipe_de_Programa.jasper";

    /** Inicializa os objetos */
    @Override
    protected void clear() {
    	super.clear();
    	inicializarTiposMembros();
    }

    /**
     * Inicializa o caso de uso.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul> 
     */
   public String iniciar() {
	   clear();
       titulo = "Equipe dos Programas";
       origemDados = Sistema.SIGAA;
       tipoUnidade = PROGRAMA;
       tipoMembros = -1;
       return forward("/stricto/relatorios/equipe_programa.jsp");
   }

   /**
    * Método que gera o relatório do caso de uso, de acordo com as opções
    * informadas pelo usuário.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/relatorios/equipe_programa.jsp</li>
	 * </ul> 
    */
   @Override
   public String gerarRelatorio() throws DAOException {
	   
	   EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
	   int total = 0;
	   Integer idUnidade = null;
	   if( unidade.getId() > 0 )
		   idUnidade = unidade.getId();
	   
	   if( tipoMembros == TODOS_DOCENTES )
		   total = dao.countTotalMembrosPrograma(idUnidade);
	   else if( tipoMembros == DOCENTE_INTERNO )
		   total = dao.countTotalServidoresPrograma(idUnidade);
	   else if( tipoMembros == DOCENTE_EXTERNO )
		   total = dao.countTotalDocentesExternosPrograma(idUnidade);
	   
	   
		relatorio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO);
		parametros = new HashMap<String, Object>();
		parametros.put("programa", unidade.getId());
		parametros.put("externo", tipoMembros);
		parametros.put("totalDocentes", total);
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());

		return super.gerarRelatorio();
	}

	public int getTipoMembros() {
		return tipoMembros;
	}
	
	public void setTipoMembros(int tipoMembros) {
		this.tipoMembros = tipoMembros;
	}
	
	public List<SelectItem> getTiposMembros() {
		return tiposMembros;
	}
	
	public void setTiposMembros(List<SelectItem> tiposMembros) {
		this.tiposMembros = tiposMembros;
	}


}
