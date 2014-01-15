/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 10/11/2008 
 */
package br.ufrn.sigaa.ava.jsf;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.negocio.MovimentoArquivoTurma;

/**
 * Managed Bean para atualizar a associação de um arquivo com uma turma.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("session")
public class AtualizaArquivoTurmaMBean extends ControllerTurmaVirtual {

	private UploadedFile arquivo;

	private ArquivoTurma arquivoTurma;
	
	/**
	 * Exibe o formulário para se alterar um arquivo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/aulas.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String preAtualizaAssociacaoArquivo() throws DAOException, NegocioException {
		arquivoTurma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(getParameterInt("id"), ArquivoTurma.class);
		if (arquivoTurma == null)
			throw new NegocioException("Nenhum arquivo marcado para atualização.");
		arquivoTurma.getAula().getId();
		arquivo = null;
		return forward("/ava/PortaArquivos/atualizarArquivo.jsp");
	}
	
	/**
	 * Atualiza uma associação de um arquivo com uma turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/PortaArquivos/atualizarArquivo.jsp
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String atualizaAssociacaoArquivo() throws SQLException, IOException, NegocioException, ArqException {
	
		registrarAcao(arquivoTurma.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.INICIAR_ALTERACAO, arquivoTurma.getId());
		
		if (arquivo != null) {
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
			
			arquivoTurma.getArquivo().setIdArquivo(idArquivo);
			arquivoTurma.getArquivo().setTamanho(arquivo.getSize());
		}
		
		prepare(SigaaListaComando.ATUALIZAR_ARQUIVO);
		
		MovimentoArquivoTurma mat = new MovimentoArquivoTurma();
		mat.setArquivoTurma(arquivoTurma);
		mat.setCodMovimento(SigaaListaComando.ATUALIZAR_ARQUIVO);
		
		execute(mat);
		
		registrarAcao(arquivoTurma.getNome(), EntidadeRegistroAva.ARQUIVO, AcaoAva.ALTERAR, arquivoTurma.getId());
		TopicoAulaMBean taBean = (TopicoAulaMBean) getMBean("topicoAula");
		taBean.setListagem(null);
		taBean.setTopicosAulas(null);
		taBean.setItens(null);
		
		TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
		return tBean.retornarParaTurma();
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public ArquivoTurma getArquivoTurma() {
		return arquivoTurma;
	}

	public void setArquivoTurma(ArquivoTurma arquivoTurma) {
		this.arquivoTurma = arquivoTurma;
	}
	
}
