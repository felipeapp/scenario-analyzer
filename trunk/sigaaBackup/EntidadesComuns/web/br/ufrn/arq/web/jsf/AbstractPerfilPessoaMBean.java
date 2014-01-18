/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/05
 */
package br.ufrn.arq.web.jsf;

import java.io.IOException;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.negocio.MovimentoPerfilPessoa;

/**
 * Managed Bean para o cadastro dos perfis de pessoas.
 * 
 * @author David Pereira
 *
 */
public abstract class AbstractPerfilPessoaMBean extends AbstractControllerCadastro<PerfilPessoa> {

	private UploadedFile foto;
	
	public AbstractPerfilPessoaMBean() {
		obj = getPerfilUsuario();
		if (obj == null) {
			obj = new PerfilPessoa();
		}
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}
	
	@Override
	public String cadastrar() throws ArqException, NegocioException {

		// Validar dados do perfil
		ListaMensagens erros = obj.validate();
		
		if (erros == null)  	
			erros = new ListaMensagens();
		if (getFoto() != null) {
			if (!getFoto().getContentType().contains("image"))
				erros.addErro("Somente arquivos de imagem podem ser enviados. Selecione arquivos do tipo jpeg, gif, png ou bmp.");
			else {
				// tenta manipular o arquivo para verificar se realmente é uma imagem.
				try{
					UFRNUtils.redimensionaJPG(getFoto().getBytes(), PerfilPessoa.WIDTH_FOTO, PerfilPessoa.HEIGTH_FOTO);
				} catch (IllegalArgumentException e) {
					tratamentoErroPadrao(e, "O arquivo enviado não é arquivo de imagem válido.");
				} catch (IOException e){
					tratamentoErroPadrao(e, "Não foi possível abrir o arquivo enviado.");
				}
			}
		}

		if (erros!=null && !erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}

		prepareMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		setTipoPerfil(obj);

		// Popular movimento
		MovimentoPerfilPessoa perfilMov = new MovimentoPerfilPessoa();
		perfilMov.setCodMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		perfilMov.setPerfil(obj);
		perfilMov.setFoto(foto);

		// Seta idFoto no movimento para excluir a foto 
		if(getParameterInt("idFoto") != null){
			perfilMov.setIdFoto(getParameterInt("idFoto"));
		}
		// Executar atualização do perfil
		execute(perfilMov, getCurrentRequest());

		setPerfilPortal(obj);

		addMessage("Perfil atualizado com sucesso!", TipoMensagemUFRN.INFORMATION);
		return cancelar();

	}
	

	public abstract void setPerfilPortal(PerfilPessoa perfil);
	
	public abstract PerfilPessoa getPerfilUsuario();

	public abstract void setTipoPerfil(PerfilPessoa perfil);
	
}
