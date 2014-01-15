package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.rh.DesignacaoDAO;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Managed Bean responsável em realizar as consultas na área pública do SIGAA dos chefes de departamentos,
 * coordenações dos cursos de graduação e pós-graduação, e por fim os diretores de unidades acadêmicas especializadas.
 * Os dados exibidos são: nome, telefone e e-mail.
 * 
 * @author Mário Rizzi
 *
 */
@Component("consultaPublicaResponsavel")
@Scope(value="request")
public class ConsultaPublicaResponsavelUnidadeCursoMBean extends AbstractController {

	/** Opção de consulta dos chefes de departamentos */
	private static final int CHEFES_DEPARTAMENTOS = 1;
	/** Opção de consulta dos coordenadores e vice-coordenadores de curso de graduação */
	private static final int COORD_CURSO_GRADUACAO = 2;
	/** Opção de consulta dos coordenadores e vice-coordenadores de curso de pós-graduação */
	private static final int COORD_CURSO_POS_GRADUACAO = 3;
	/** Opção de consulta dos diretores das unidades acadêmicas especializadas */
	private static final int DIRETORES_UNIDADES_ACADEMICAS = 4;

	/** Lista de mapas contendo o resultado da consulta */
	private List<Map<String, Object>> listaMapas;

	/** Tipo da consulta que está sendo realizada */
	private Integer tipoConsulta = CHEFES_DEPARTAMENTOS;
	
	public ConsultaPublicaResponsavelUnidadeCursoMBean(){
	}

	/**
	 * Realiza a busca dos responsáveis de acordo com o tipoConsulta setado.
	 * O resultado da consulta é populado em uma lista de mapas e exibido na view.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String buscar() throws DAOException{

		listaMapas = null;

		if(tipoConsulta == 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo da Consulta");

		if(hasErrors())
			return null;

		if(tipoConsulta == CHEFES_DEPARTAMENTOS || tipoConsulta == DIRETORES_UNIDADES_ACADEMICAS){

			DesignacaoDAO desiginacaoDao = getDAO(DesignacaoDAO.class);
			Collection<Designacao> designacoes = new ArrayList<Designacao>(); 

			if(tipoConsulta == CHEFES_DEPARTAMENTOS)
				designacoes = desiginacaoDao.findChefesDiretoresByAtividade(AtividadeServidor.CHEFE_DEPARTAMENTO);
			else {			
				List<Integer> idsAtividadeServidor = new ArrayList<Integer>();
				idsAtividadeServidor.addAll(AtividadeServidor.DIRETOR_CENTRO);
				idsAtividadeServidor.addAll(AtividadeServidor.DIRETOR_UNIDADE);
				designacoes = desiginacaoDao.findChefesDiretoresByAtividade(idsAtividadeServidor);
			}	
			
			Collections.sort(CollectionUtils.toList(designacoes), new Comparator<Designacao>() {
				public int compare(Designacao d1, Designacao d2) {
					return  d1.getUnidadeDesignacao().getNome().compareTo(d2.getUnidadeDesignacao().getNome());
				}
			});
			
			//Popula a lista de mapas com os dados da consulta 
			for (Designacao d : designacoes) 
				addMapa(d.getUnidadeDesignacao().getGestoraAcademica().getNome(),d.getUnidadeDesignacao().getNome(),
						d.getServidorSigaa().getNome(), null, d.getServidorSigaa().getPrimeiroUsuario().getRamal(), 
						d.getServidorSigaa().getPrimeiroUsuario().getEmail());

		}else{

			CoordenacaoCursoDao coordenacaoDao = getDAO(CoordenacaoCursoDao.class);
			Collection<CoordenacaoCurso> coord  = null;

			if( tipoConsulta == COORD_CURSO_GRADUACAO )
				coord = coordenacaoDao.findListaCoordenadores(null, NivelEnsino.GRADUACAO, true,null);
			else{
				
				coord = coordenacaoDao.findByPrograma(null, TipoUnidadeAcademica.PROGRAMA_POS, true, null);
				
				Collections.sort((List<CoordenacaoCurso>)coord, new Comparator<CoordenacaoCurso>() {
					public int compare(CoordenacaoCurso cc1, CoordenacaoCurso cc2) {
						return  cc1.getUnidade().getUnidadeResponsavel().getNome().compareTo(cc2.getUnidade().getUnidadeResponsavel().getNome());
					}
				});
				
			}	
			

			//Popula a  lista de mapas com os dados da consulta
			for (CoordenacaoCurso cc : coord) {

				StringBuilder telefones = new StringBuilder();

				if(!isEmpty(cc.getTelefoneContato1()))
					telefones.append(cc.getTelefoneContato1());
				if(!isEmpty(cc.getTelefoneContato2())){
					telefones.append("/");
					telefones.append(cc.getTelefoneContato2());
				}	

				addMapa( !isEmpty( cc.getCurso() )?cc.getCurso().getUnidade().getNome():cc.getUnidade().getUnidadeResponsavel().getNome(),
						!isEmpty( cc.getCurso() )?cc.getCurso().getDescricao():cc.getUnidade().getNomeAscii(),
						cc.getServidor().getNome(),
						cc.getCargoAcademico().getDescricao(),
						telefones.toString(),
						cc.getEmailContato() );
			}
		}

		return forward("/public/academico/busca_responsavel.jsp");
	}
	
	/**
	 * Cancela a operação e redireciona para tela principal do portal público do sigaa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 **/
	@Override
	public String cancelar() {
		resetBean();
		return redirectJSF("/sigaa/public");
	}

	/**
	 *Adiciona os dados do responsável à lista de mapas
	 * @param agrupador
	 * @param nome
	 * @param cargo
	 * @param telefone
	 * @param email
	 */
	private void addMapa(String agrupador, String unidade, String nome, String cargo, String telefone, String email) {

		if( listaMapas == null)
			listaMapas = new ArrayList<Map<String,Object>>();

		Map<String, Object> mapa = new LinkedHashMap<String, Object>();
		mapa.put("agrupador", agrupador);
		mapa.put("unidade", unidade);
		mapa.put("cargo", cargo);
		mapa.put("nome", nome);
		mapa.put("telefone", telefone);
		mapa.put("email", email);

		listaMapas.add(mapa);

	}

	public Integer getTipoConsulta() {
		return tipoConsulta;
	}

	public void setTipoConsulta(Integer tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	/**
	 * Retorna o título da busca na view.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String getDescricaoConsulta(){
		return getDescricaoConsulta(this.tipoConsulta);
	}

	/**
	 * Retorna o título do tipo da consulta que está sendo realizada.  
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	private String getDescricaoConsulta(Integer tipoConsulta){

		String descricaoConsulta = "";

		if(tipoConsulta.equals(CHEFES_DEPARTAMENTOS)) 
			descricaoConsulta = "Chefes de Departamento"; 
		else if(tipoConsulta.equals(COORD_CURSO_GRADUACAO)) 
			descricaoConsulta = "Coordenadores e Vice-Coordenadores dos Cursos de Graduação";
		else if(tipoConsulta.equals(COORD_CURSO_POS_GRADUACAO))
			descricaoConsulta = "Coordenadores e Vice-Coordenadores dos Cursos de Pós-Graduação";
		else if(tipoConsulta.equals(DIRETORES_UNIDADES_ACADEMICAS)) 
			descricaoConsulta = "Diretores de Centros/Unidades Acadêmicas Especializadas";

		return descricaoConsulta;

	}

	/**
	 * Popula um combo com as opções de consulta
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getComboTiposConsulta(){

		Collection<SelectItem> itens = new ArrayList<SelectItem>();

		itens.add(new SelectItem(CHEFES_DEPARTAMENTOS,
				getDescricaoConsulta(CHEFES_DEPARTAMENTOS)));
		itens.add(new SelectItem(COORD_CURSO_GRADUACAO,
				getDescricaoConsulta(COORD_CURSO_GRADUACAO)));
		itens.add(new SelectItem(COORD_CURSO_POS_GRADUACAO,
				getDescricaoConsulta(COORD_CURSO_POS_GRADUACAO)));
		itens.add(new SelectItem(DIRETORES_UNIDADES_ACADEMICAS,
				getDescricaoConsulta(DIRETORES_UNIDADES_ACADEMICAS)));

		return itens;

	}

	/**
	 * Retorna o resultado a da consulta contendo uma lista de mapas.
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * @return
	 */
	public List<Map<String, Object>> getListaMapas() {
		return listaMapas;
	}

	public void setListaMapas(ArrayList<Map<String, Object>> listaMapas) {
		this.listaMapas = listaMapas;
	}

	/**
	 * Permite visualizar a colunas exclusivas para as consultas
	 * dos chefes e diretores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public boolean isChefesDiretores(){
		return !isEmpty(tipoConsulta) && 
			( tipoConsulta.equals(CHEFES_DEPARTAMENTOS) || tipoConsulta.equals(DIRETORES_UNIDADES_ACADEMICAS) ); 
	}
	
	/**
	 * Permite visualizar a colunas exclusivas para as consultas
	 * dos chefes e diretores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * @return
	 */
	public boolean isDiretores(){
		return !isEmpty(tipoConsulta) && tipoConsulta.equals(DIRETORES_UNIDADES_ACADEMICAS); 
	}
	
	/**
	 * Permite visualizar a colunas exclusivas para as consultas
	 * dos coordenadores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/public/academico/busca_responsavel.jsp</li>
	 *	</ul>
	 * @return
	 */
	public boolean isCoordenadores(){
		return !isEmpty(tipoConsulta) && (tipoConsulta.equals(COORD_CURSO_GRADUACAO) || tipoConsulta.equals(COORD_CURSO_POS_GRADUACAO)); 
	}

}
