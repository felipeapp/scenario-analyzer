/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 25/08/2009
 */
package br.ufrn.comum.jsf;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Managed bean para cadastro de chamados por todos os
 * sistemas.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class ChamadoComumMBean extends ComumAbstractController<Mensagem> { 

	private Collection<Mensagem> chamadosUsuario;
	
	//Armazena o chamado a ser exibido no modal panel com detalhes de um chamado do usuário logado.
	private Mensagem chamadoSelecionado;

	private UploadedFile arquivo;
	
	@Autowired
	private TokenGenerator generator;
	
	public ChamadoComumMBean() {
		obj = new Mensagem();
	}
	
	/**
	 * Cria um token de autenticação e redireciona para a tela de abrir chamado.
	 * JSP: Não é chamado por JSPs. Chamado diretamente através de URL através do pretty-faces.
	 * @return
	 */
	public String abrirChamado() {
		TokenAutenticacao key = generator.generateToken(getUsuarioLogado(), getSistema(), getParameter("tipo") );
		return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal() + "/cxpostal/telaChamado.jsf?tipo=" + getParameter("tipo") 
				+ "&idRemetente=" + getUsuarioLogado().getId() + "&idUsuario=" + getParameter("idUsuario")
				+ "&sistema=" + getParameter("sistema") + "&id=" + key.getId() + "&key=" + key.getKey());
	}
	
	/**
	 * Método que é chamado de qualquer sistema para abrir o formulário de abrir chamado que será gerenciado pelo SIGADMIN
	 * @return
	 */
	public String novoChamado() {
		/*TokenAutenticacao key = generator.generateToken(getUsuarioLogado(), getSistema(), getParameter("tipo") );
		return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal() + "/admin/formChamadoAdmin.jsf?tipo=" + getParameter("tipo") 
				+ "&idRemetente=" + getUsuarioLogado().getId() + "&idUsuario=" + getParameter("idUsuario")
				+ "&sistema=" + getParameter("sistema") + "&id=" + key.getId() + "&key=" + key.getKey());*/
		TokenAutenticacao key = generator.generateToken(getUsuarioLogado(), getSistema() );
		return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal() 
				+ "/admin/formChamadoAdmin.jsf?idRemetente=" + getUsuarioLogado().getId() 
				+ "&sistema=" + getParameter("sistema") 
				+ "&id=" + key.getId() 
				+ "&key=" + key.getKey() 
				+ "&entrada=" + getUsuarioLogado().getRegistroEntrada().getId());
	}
	
	/**
	 * Consome o token gerado no método abrirChamado() e direciona para a tela de abrir chamado.
	 * JSP: Não é chamado por JSPs. Chamado diretamente através de URL através do pretty-faces.
	 * @return
	 * @throws IOException
	 * @throws ArqException
	 */
	public String exibirTela() throws IOException, ArqException {
		String key = getParameter("key");
		int idToken = getParameterInt("id");
		 
		if (generator.isTokenValid(idToken, key)) {
			generator.invalidateToken(idToken);

			int idRemetente = getParameterInt("idRemetente");
			getCurrentSession().setAttribute("usuario", getGenericDAO().findByPrimaryKey(idRemetente, UsuarioGeral.class));

			prepareMovimento(ArqListaComando.CADASTRAR);
			return redirect("/chamado/abrirChamado.jsf?tipo=" + getParameter("tipo")
					+ "&idUsuario="+getParameter("idUsuario") + "&idRemetente=" + getParameter("idRemetente"));
		} else {
		    getCurrentResponse().getWriter().write("Acesso negado.");
		    FacesContext.getCurrentInstance().responseComplete();
		    return null;
		}
	}
	
	/**
	 * Cadastra um chamado no banco de dados.
	 * JSP: /shared/chamado/abrirChamado.jsf
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		//verificação de segurança para impedir que usuário com a url e um token válido enviem chamados com outros remetentes.
		if(getParameterInt("idRemetente") != getUsuarioLogado().getId())
			throw new SegurancaException("Usuário não autorizado a realizar essa operação.");
		
		MensagemDAO dao = getDAO(MensagemDAO.class);
		obj.setDataCadastro(new Date());
		obj.setTipo(getParameterInt("tipo"));
		obj.setUsuarioDestino(new UsuarioGeral(getParameterInt("idUsuario")));
		obj.setRemetente(new UsuarioGeral(getParameterInt("idRemetente")));
		obj.setNumChamado(dao.getNextChamado());
		obj.setAutomatica(false);
		obj.setStatusChamadoDesc(-1);
		
		if (arquivo != null) {
			if ((arquivo.getSize()/ 1024) > ParametroHelper.getInstance().getParametroLong(ConstantesParametroGeral.TAMANHO_MAX_ARQUIVO_CHAMADO)) {
				addMensagemErro("Chamado n\343o enviado. O tamanho m\341ximo do arquivo deve ser de 2MB.");
				return null;
			} else {
				try {
					int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
					EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
					obj.setIdArquivo(idArquivo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		execute(new MovimentoCadastro(obj, ArqListaComando.CADASTRAR));
		return redirect("/chamado/sucessoChamado.jsf");
	}
	
	public Collection<Mensagem> getChamadosUsuario() throws DAOException {
		MensagemDAO dao = getDAO(MensagemDAO.class);
		if (chamadosUsuario == null) {
			chamadosUsuario = dao.findChamadosByUsuario(getUsuarioLogado().getId());
		}
		return chamadosUsuario;
	}
	
	public List<SelectItem> getTiposChamadoCombo() {
		return toSelectItems(getDAO(MensagemDAO.class).findTiposChamado());
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public void setChamadoSelecionado(Mensagem chamadoSelecionado) {
		this.chamadoSelecionado = chamadoSelecionado;
	}

	public Mensagem getChamadoSelecionado() {
		return chamadoSelecionado;
	}

}
