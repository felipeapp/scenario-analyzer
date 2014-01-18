/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.dominio.BloqueioUsuarioInativo;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.dominio.InteressadoProcessoSeletivo;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Managed bean para cadastro e listagem de interessados no seletivos
 * 
 * @author Diego Jácome
 * 
 */
@Component("interessadoProcessoSeletivo") 
@Scope("session") 
public class InteressadoProcessoSeletivoMBean extends SigaaAbstractController<InteressadoProcessoSeletivo>  {

	/** Relatório de listagem dos responsáveis pela divulgação do processo seletivo. */
	public static final String JSP_LISTAGEM_INTERESSADOS_PROCESSO_SELETIVO = "/stricto/processo_seletivo/lista_interessados.jsp";
	
	/** Lista de interessados para responder ao processo seletivo */
	private ArrayList<InteressadoProcessoSeletivo> listagemInteressados;
	
	
	/**
	 * Método responsável por acessar o caso de uso de criar alterar listagem de interessados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarStrictoSensu() throws ArqException{
		return iniciarListagemInteressados();  
	}
	
	/**
	 * Método responsável por acessar o caso de uso de criar alterar listagem de interessados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarLatoSensu() throws ArqException{
		return iniciarListagemInteressados();  
	}
	
	/**
	 * Método responsável por acessar o caso de uso de criar alterar listagem de interessados.
	 * <br />
	 * Método Não chamado por JSPs:
	 */
	public String iniciarListagemInteressados () throws ArqException {
		
		GenericDAO dao = null;
		try {			
			obj = new InteressadoProcessoSeletivo();
			obj.setPessoa(new Pessoa());
			obj.setNivel(getNivelEnsino());
			dao = getGenericDAO();
			listagemInteressados = (ArrayList<InteressadoProcessoSeletivo>) dao.findByExactField(InteressadoProcessoSeletivo.class, new String[] { "nivel", "ativo" }, new Object[] { obj.getNivel(), true });
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAGEM_INTERESSADOS_PROCESSO_SELETIVO);
	}
	
	/**
	 * Método responsável por cadastrar um novo interessado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/stricto/menus/processo_seletivo/listagem_interessados.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws ArqException
	 * @throws ParseException 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		UsuarioDao dao = null;
		try {
			dao = getDAO(UsuarioDao.class);
			
			prepareMovimento(ArqListaComando.CADASTRAR);
			
			if (isEmpty(obj.getPessoa())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Interessado");
				return null;
			}
			
			obj.setUsuario(dao.findPrimeiroUsuarioByPessoa(obj.getPessoa().getId()));
			Collection<InteressadoProcessoSeletivo> copia = dao.findByExactField(InteressadoProcessoSeletivo.class, new String [] { "pessoa.id" ,"nivel", "ativo"}, new Object[] { obj.getPessoa().getId(), obj.getNivel(), true});
			
			if (!isEmpty(copia)){
				addMensagemErro("Está pessoa já está cadastrada na lista de interessados");
				return null;
			}
			
			super.cadastrar();
			return iniciarListagemInteressados();
		} finally {
			if (dao!=null)
				dao.close();
		}
	}
	
	/**
	 * Método responsável por inativar um interessado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/stricto/menus/processo_seletivo/listagem_interessados.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws ArqException
	 * @throws ParseException 
	 */
	@Override
	public String inativar() throws SegurancaException, ArqException, NegocioException {
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, InteressadoProcessoSeletivo.class);
		prepareMovimento(ArqListaComando.DESATIVAR);
		super.inativar();
		return iniciarListagemInteressados();
	}
	
	public void setListagemInteressados(ArrayList<InteressadoProcessoSeletivo> listagemInteressados) {
		this.listagemInteressados = listagemInteressados;
	}

	public ArrayList<InteressadoProcessoSeletivo> getListagemInteressados() {
		return listagemInteressados;
	}
	
	/**
	 * Diretório base da lista
	 * 
	 * @return
	 */
	@Override
	public String getListPage() {

		return JSP_LISTAGEM_INTERESSADOS_PROCESSO_SELETIVO;
	}
}
