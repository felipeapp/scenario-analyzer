/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 * 
 * Criado em:  31/01/2008
 * 
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * MBean que gerencia as pastas de arquivos no porta arquivos da turma virtual.
 * 
 * @author davidpereira
 *
 */
@Component("pastaArquivos") @Scope("request")
public class PastaArquivosMBean extends SigaaAbstractController<PastaArquivos> {

	public PastaArquivosMBean() {
		clear();
	}

	/**
	 * Limpa os dados armazenados no MBean.
	 */
	private void clear() {
		this.obj = new PastaArquivos();
		this.obj.setPai(new PastaArquivos());
	}

	/**
	 * Prepara o objeto antes de cadastrar e alterar.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public void beforeCadastrarAndValidate() {
		obj.setData(new Date());
		obj.setUsuario(getUsuarioLogado());
	}

	/**
	 * Retorna todas as pastas do usuário.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getPastasUsuario() throws DAOException {
		PortaArquivosDao dao = getDAO(PortaArquivosDao.class);
		List<PastaArquivos> pastas = dao.findPastasByUsuario(getUsuarioLogado());
		StringBuffer result = new StringBuffer();

		for (PastaArquivos pasta : pastas)
			result.append("objs.push({ text : '" + pasta.getNome() + "', id: " + pasta.getId() + ", pai: " + pasta.getIdPai() + " });\n");

		return result.toString();
	}

	/**
	 * Realiza o cadastro de uma pasta no porta-arquivos.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/PortaArquivos/index.jsp</li>
	 * </ul>
	 */
	public String cadastrar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_PASTA);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PASTA);
		mov.setObjMovimentado(obj);

		try {
			execute(mov);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		addMensagemInformation("Pasta Cadastrada com sucesso!");
		clear();
		return null;
	}
}
