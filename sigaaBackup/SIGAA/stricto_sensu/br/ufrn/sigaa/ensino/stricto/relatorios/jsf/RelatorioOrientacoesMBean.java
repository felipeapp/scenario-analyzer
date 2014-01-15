/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Apr 23, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;

/**
 * MBean para gerar relat�rios espec�ficos do coordenador de programa STRICTO
 * @author Victor Hugo
 */
@Component("relatorioOrientacoes") 
@Scope("request")
public class RelatorioOrientacoesMBean extends SigaaAbstractController<OrientacaoAcademica>{
	
	/** Unidade que representa o programa do discente */
	private Unidade unidade = new Unidade();
	
	/** Curso do discente */
	private Curso curso = new Curso();
	
	/**
	 * Situa��o das orienta��es que devem ser trazidas
	 * null - todas
	 * TRUE - ativa
	 * FALSE - inativas
	 */
	private Boolean ativo;
	
	/** 
	 * O relat�rio pode ser anal�tico ou sint�tico, este atributo controla isso...
	 * TRUE - sint�tico
	 * FALSE - anal�tico
	 */
	private boolean sintetico = false;

	/**
	 * Guarda o status do discente escolhido.
	 */
	private StatusDiscente statusDiscente = new StatusDiscente();
	
	/** Constantes das views */
	
	/** P�gina dos filtros para a gera��o do relat�rio */
	public static final String JSP_FORM_ORIENTACOES = "/stricto/relatorios/form_orientacoes.jsp";
	/** P�gina do relat�rio anal�tico */
	public static final String JSP_ORIENTACOES = "/stricto/relatorios/orientacoes.jsp";
	/** P�gina do relat�rio sint�tico */
	public static final String JSP_ORIENTACOES_SINTETICO = "/stricto/relatorios/orientacoes_sintetico.jsp";

	/** Armazena as orienta��es encontradas do discente */
	List<OrientacaoAcademica> orientacoes = new ArrayList<OrientacaoAcademica>();

	/**
	 * Vai para a pagina de filtro do relat�rio
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 * 	<li> sigaa.war/stricto/menu_coordenador.jsp </li>
	 *  <li> sigaa.war/stricto/menus/relatorios.jsp </li>  
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				   SigaaPapeis.SECRETARIA_POS, 
				   SigaaPapeis.PPG,
				   SigaaPapeis.COORDENADOR_CURSO, 
				   SigaaPapeis.SECRETARIA_GRADUACAO,
				   SigaaPapeis.GESTOR_TECNICO);
		
		unidade = new Unidade();
		statusDiscente = new StatusDiscente();
		
		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_GRADUACAO ) )
			curso = getCursoAtualCoordenacao();
		
		return forward(JSP_FORM_ORIENTACOES);
	}

	/**
	 * Gera o relat�rio de Orientandos por Orientador, de acordo com os par�metros informados.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 * 	<li> sigaa.war/stricto/relatorios/form_orientacoes.jsp</li>  
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String gerarRelatorio() throws ArqException {
		
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				   SigaaPapeis.SECRETARIA_POS, 
				   SigaaPapeis.PPG,
				   SigaaPapeis.COORDENADOR_CURSO, 
				   SigaaPapeis.SECRETARIA_GRADUACAO,
				   SigaaPapeis.GESTOR_TECNICO);
		
		statusDiscente.setDescricao( StatusDiscente.getDescricao(statusDiscente.getId()) );
		
		OrientacaoAcademicaDao dao = getDAO( OrientacaoAcademicaDao.class );
		
		if( isPortalCoordenadorStricto() || isPortalPpg() ){
			
			if(  isPortalCoordenadorStricto() && getProgramaStricto() !=  null)
				unidade = getProgramaStricto();
			if( isPortalCoordenadorStricto() && (isEmpty(unidade) || unidade.getId() <= 0) ){
				addMensagemErro("Selecione o programa.");
				return null;
			}
			
			if (unidade!= null && unidade.getId() > 0  && isPortalPpg()){
				UnidadeDao uniDao = getDAO(UnidadeDao.class);
				unidade = uniDao.findByPrimaryKey(unidade.getId(), Unidade.class);
			}
			
			if( sintetico )
				orientacoes = dao.findTotalOrientacoesByProgramaOrCursoDiscente( unidade.getId(), null, ativo, statusDiscente );
			else
				orientacoes = dao.findByProgramaOrCursoDiscente( unidade.getId(), null, ativo, statusDiscente );
			
		} else{
			
			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_GRADUACAO ) )
				curso = getCursoAtualCoordenacao();
			if( isEmpty(curso) || curso.getId() <= 0 ){
				addMensagemErro("Selecione o curso.");
				return null;
			}
			
			if( sintetico )
				orientacoes = dao.findTotalOrientacoesByProgramaOrCursoDiscente( null, curso.getId(), ativo, statusDiscente );
			else
				orientacoes = dao.findByProgramaOrCursoDiscente( null, curso.getId(), ativo, statusDiscente );
		}

		if( sintetico ){
			
			Collections.sort( orientacoes, new Comparator<OrientacaoAcademica>(){
				public int compare(OrientacaoAcademica o1, OrientacaoAcademica o2) {
					return o1.getNomeOrientador().compareTo(o2.getNomeOrientador());
				}
			});
			if (isEmpty(orientacoes)){
				addMensagemErro("Nenhuma orienta��o foi encontrada com os par�metros informados.");
				return null;
			}else
			return forward(JSP_ORIENTACOES_SINTETICO);
			
		} else{			
			
			if(Sistema.isSipacAtivo()) {
			
				Map<Integer, String> bolsasSIPAC = IntegracaoBolsas.findBolsistasAtivosDiscente(new java.sql.Date(new java.util.Date().getTime()), new java.sql.Date(new java.util.Date().getTime()));			
							
				for (OrientacaoAcademica listaOrientacao : orientacoes ) {
					listaOrientacao.setTipoBolsa( bolsasSIPAC.get(listaOrientacao.getDiscente().getId() ) );
				}					

			}
			
			Collections.sort( orientacoes );
			if (isEmpty(orientacoes)){
				addMensagemErro("Nenhuma orienta��o foi encontrada com os par�metros informados.");
				return null;
			}else
				return forward(JSP_ORIENTACOES);
		}
	}

	/**
	 * Retorna a descri��o do status do discente
	 * 
	 * @return Descri��o do status do discente
	 */
	public String getStatusDescricao(){
		if(statusDiscente.getId() == 0)
			return "TODOS";
		else
			return StatusDiscente.getDescricao(statusDiscente.getId());
	}	
	
	public List<OrientacaoAcademica> getOrientacoes() {
		return orientacoes;
	}

	public void setOrientacoes(List<OrientacaoAcademica> orientacoes) {
		this.orientacoes = orientacoes;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isSintetico() {
		return sintetico;
	}

	public void setSintetico(boolean sintetico) {
		this.sintetico = sintetico;
	}

	public StatusDiscente getStatusDiscente() {
		return statusDiscente;
	}

	public void setStatusDiscente(StatusDiscente statusDiscente) {
		this.statusDiscente = statusDiscente;
	}

}
