/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 04/05/2009
 * 
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultaMaterialInformacional;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultasDiariasMateriais;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoRegistroConsultaMaterialLeitor;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 * <p>MBean respons�vel por registrar os materiais consultados com leitor �tico.</p> 
 * <p>Esse caso de uso gera 2 registros: RegistroConsultaMaterialInformacional (qualitativo) e RegistroConsultasDiariasMateriais (quantitativo).</p> 
 * 
 * @author Fred_Castro
 *
 */

@Component("registroConsultasDiariaisMateriaisLeitorMBean")
@Scope("request")
public class RegistroConsultasDiariaisMateriaisLeitorMBean extends SigaaAbstractController<RegistroConsultaMaterialInformacional> {

	/** O form de registro das consultas com leitor */
	public static final String PAGINA_FORM = "/biblioteca/RegistroConsultasDiariasMaterialLeitor/form.jsp";
	
	/** Guarda os c�digos de barra adicionados pelo usu�rio */
	private List <String> codigosBarras;
	
	/** Os materiais cujas consulta vai ser registrada */
	private List <MaterialInformacional> materiais; 
	
	/** O c�digo de barras digitado pelo usu�rio */
	private String codigoBarras;
	
	/** O turno da consulta */
	private int turno;
	
	/** A data da consulta. */
	private Date data;
	
	/**
	 * Inicia o caso de registro de consulta de materiais nas estates das bibliotecas com o uso do leitor �ptico.
	 * <br/>
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/menus/cadastros.jsp
	 */
	public String iniciarRegistroConsulta () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REGISTRAR_CONSULTAS_DIARIA_MATERIAIS_LEITOR);
		
		iniciarDadosFormulario();
		
		return telaFormRegistroConsultaLeitor();
	}
	
	/** Inicia os dados do formul�rio de registro de consulta */
	private void iniciarDadosFormulario (){
		turno = RegistroConsultasDiariasMateriais.TURNO_MATUTINO;
		data = new Date();
		codigoBarras = "";
		materiais = new ArrayList <MaterialInformacional> ();
		codigosBarras = new ArrayList<String>();
	}
	
	/**
	 * Adiciona o c�digo de barras digitado pelo usu�rio � lista de materiais consultados.
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/RegistroConsultasDiariasMaterialLeitor/form.jsp
	 */
	public void adicionarMaterial (ActionEvent e) throws DAOException{
		
		if (StringUtils.isEmpty(codigoBarras)){
			addMensagemErroAjax("Informe um c�digo de barras v�lido para um material informacional");
		}else{ 
			if (!codigosBarras.contains(codigoBarras)){
				
				MaterialInformacional m = getDAO(MaterialInformacionalDao.class).findMaterialAtivoByCodigoBarras(codigoBarras);
				
				if(m != null ){   // Material Encontrado
					
					if(! isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
						try{
							checkRole(m.getBiblioteca().getUnidade() 
									, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
									, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
									, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
									, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
						}catch (SegurancaException se) {
							addMensagemErroAjax("O material n�o pertence a sua biblioteca. ");
							return;
						}
					}
					
					codigosBarras.add(codigoBarras);
					materiais.add( m );
				}else{
					addMensagemErroAjax("O material de c�digo de barras \""+codigoBarras+"\" n�o foi encontrado ");
				}	
				
			}else
				addMensagemErroAjax("O material de c�digo de barras \""+codigoBarras+"\" j� foi adicionado � lista.");
		}
	}
	
	
	
	/**
	 * Prepara a lista de Materiais Catalogr�ficos cuja consulta ser� registrada e chama o processador que realizar� o registro.
	 * <br/>
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/RegistroConsultasDiariaisMateriaisLeitorMBean/form.jsp
	 * 
	 */
	public String registrarConsultas () throws HibernateException, ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (materiais == null || materiais.isEmpty()){
			addMensagemErro("Nenhum Material foi adicionado para registrar a sua consulta.");
			return telaFormRegistroConsultaLeitor();
		}
		
		
		try {
			
			MovimentoRegistroConsultaMaterialLeitor mov = new MovimentoRegistroConsultaMaterialLeitor(materiais, turno, data);
			mov.setCodMovimento(SigaaListaComando.REGISTRAR_CONSULTAS_DIARIA_MATERIAIS_LEITOR);
			execute(mov);
			
			addMensagemInformation("Consultas registradas com sucesso. ");
			return cancelar();
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
	}

	
	/**
	 * Redireciona para a p�gina de registro da consulta de materiais com o leitor.<br/><br/>
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/RegistroConsultasDiariasMaterialLeitor/form.jsp
	 * @return
	 */
	public String telaFormRegistroConsultaLeitor(){
		return forward(PAGINA_FORM);
	}
	
	
	
	///////////////     sets e gets ///////////////////////
	
	public List <String> getCodigosBarras() {
		return codigosBarras;
	}

	public void setCodigosBarras(List<String> codigosBarras) {
		this.codigosBarras = codigosBarras;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	
	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
}
