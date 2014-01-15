/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/06/2008
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/*******************************************************************************
 * MBean Responsável por gerenciar cadastro de fotos de projetos.
 * 
 * @author Ilueny Santos
 ******************************************************************************/
@Component("fotoProjeto")
@Scope("session")
public class FotoProjetoMBean extends SigaaAbstractController<FotoProjeto> {

	private static final int ALTURA_FOTO = 70;
	private static final int LARGURA_FOTO = 70;

	private Projeto projeto = new Projeto();

	private UploadedFile foto;

	private String descricaoFoto;

	public FotoProjetoMBean() {
		obj = new FotoProjeto();
	}

	
	@Override
	public void checkChangeRole() throws SegurancaException {
	    if (!getAcessoMenu().isCoordenadorExtensao() && !getAcessoMenu().isCoordenadorMonitoria()) {
		throw new SegurancaException("Usuário não autorizado a realizar esta operação");
	    }
	}
	
	/**<p>
	 * Inicia o procedimento de envio de fotos do projeto para o
	 * servidor de arquivos.
	 * 
	 * Somente o coordenador do projeto pode realizar esta operação.
	 * </p>
	 * Utilizado por: <ul></li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarAnexarFoto() throws SegurancaException {
	    	checkChangeRole();
	    	
		// Preparar movimento de cadastro
		try {

			int id = getParameterInt("idProjeto", 0);
			projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
			if (projeto == null) {
				addMensagemErro("Ação de Extensão não selecionada!");
			}

		} catch (ArqException e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}

		return forward(ConstantesNavegacao.FOTOS_FORM);

	}

	/**
	 * Adiciona uma foto ao projeto
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String anexarFoto() throws SegurancaException {
	    	checkChangeRole();
	    	
		try {

			if ((descricaoFoto == null) || ("".equals(descricaoFoto.trim()))) {
				addMensagemErro("Descrição da foto é obrigatória!");
				return null;
			}

			if ((foto == null)) {
				addMensagemErro("Informe o nome completo do arquivo de foto.");
				return null;
			}

			if ("image/jpeg".equalsIgnoreCase(foto.getContentType())) {

				int idFotoOriginal = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idFotoOriginal, foto
						.getBytes(), foto.getContentType(), foto.getName());

				int idFotoMini = EnvioArquivoHelper.getNextIdArquivo();
				byte[] fotoMini = UFRNUtils.redimensionaJPG(foto.getBytes(),
						LARGURA_FOTO, ALTURA_FOTO);
				EnvioArquivoHelper.inserirArquivo(idFotoMini, fotoMini, foto
						.getContentType(), foto.getName());

				FotoProjeto foto = new FotoProjeto();
				foto.setDescricao(descricaoFoto);
				foto.setIdFotoOriginal(idFotoOriginal);
				foto.setIdFotoMini(idFotoMini);
				foto.setAtivo(true);
				foto.setProjeto(projeto);

				// cadastrando no banco
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(foto);
				prepareMovimento(ArqListaComando.CADASTRAR);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);

				obj = (FotoProjeto) execute(mov, getCurrentRequest());
				addMessage("Foto Enviada com sucesso!",	TipoMensagemUFRN.INFORMATION);
				descricaoFoto = "";

				// atualizando...
				projeto = getGenericDAO().findByPrimaryKey(obj.getProjeto().getId(), Projeto.class);
				projeto.getFotos().iterator();
			} else {
				addMensagemErro("Arquivo informado não é arquivo de foto.");
				return null;
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return forward(ConstantesNavegacao.FOTOS_FORM);
	}

	/**
	 * Remove a foto da lista de anexos do projeto
	 * <br>
	 * Utilizado por: <ul><li>/sigaa.war/extensao/Fotos/form.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removeFoto() throws ArqException {
	    	checkChangeRole();
	    	
		int id = getParameterInt("idFotoProjeto", 0);
		if (id == 0) {
			addMensagemErro("Não há objeto informado para remoção");
			return null;
		}

		try {

			FotoProjeto foto = getGenericDAO().findByPrimaryKey(id,	FotoProjeto.class);

			// remove do banco de extensão
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(foto);
			prepareMovimento(ArqListaComando.REMOVER);
			mov.setCodMovimento(ArqListaComando.REMOVER);
			execute(mov, getCurrentRequest());
			addMessage("Remoção realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

			// remove do banco de arquivos
			EnvioArquivoHelper.removeArquivo(foto.getIdFotoOriginal());
			EnvioArquivoHelper.removeArquivo(foto.getIdFotoMini());

			projeto = getGenericDAO().findByPrimaryKey(foto.getProjeto().getId(), Projeto.class);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}

		return null;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public String getDescricaoFoto() {
		return descricaoFoto;
	}

	public void setDescricaoFoto(String descricaoFoto) {
		this.descricaoFoto = descricaoFoto;
	}

}
