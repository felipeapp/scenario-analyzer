package br.ufrn.sigaa.ouvidoria.negocio;

import java.util.Calendar;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador abstrato que cont�m conmportamentos comuns a outros processadores de manifesta��o.
 * 
 * @author Bernardo
 *
 */
public abstract class AbstractProcessadorManifestacao extends AbstractProcessador {
    
	/**
	 * Cadastra no banco os dados do interessado e da manifesta��o.
	 * 
	 * @param mov
	 * @throws ArqException 
	 */
    protected void cadastrarManifestacao(Movimento mov) throws ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		Manifestacao manifestacao = movimento.getObjMovimentado();
		
		Integer idAnexo = salvaArquivo((UploadedFile)movimento.getObjAuxiliar());
		
		manifestacao.setIdAnexo(idAnexo);
		criarInteressadoManifestacao(manifestacao, mov);
		criarManifestacao(manifestacao, mov);
    }

    /**
	 * Salva um arquivo .pdf ou . doc da solicita��o caso o usu�rio tenha submetido um.
	 * 
	 */
	private Integer salvaArquivo(UploadedFile arquivo) throws ArqException {
		if (arquivo != null) {		
			Integer idArquivo = null;
			
			try {
				idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, 
						arquivo.getBytes(),
						arquivo.getContentType(), 
						arquivo.getName());
				
			} catch (Exception e) {
				e.printStackTrace();
				
				throw new ArqException(e);
			} 
			
			return idArquivo;		
		} else {
			return null;  // valor padr�o para manifesta��o que nao possui anexo
		}
	}
    /**
     * Persiste a manifesta��o.
     * 
     * @param manifestacao
     * @param mov
     * @throws DAOException
     */
    private void criarManifestacao(Manifestacao manifestacao, Movimento mov) throws DAOException {
		gerarNumeroManifestacao(manifestacao, mov);
		
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			dao.create(manifestacao);
		} finally {
			dao.close();
		}
    }

    /**
     * Persiste os dados do interessado.
     * 
     * @param manifestacao
     * @param mov
     * @throws DAOException
     */
    private void criarInteressadoManifestacao(Manifestacao manifestacao, Movimento mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA)) {
				InteressadoNaoAutenticado interessadoNaoAutenticado = manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
				
				interessadoNaoAutenticado.validarEndereco();
				
				dao.create(interessadoNaoAutenticado);
			}
			
			dao.create(manifestacao.getInteressadoManifestacao());
		} finally {
			dao.close();
		}
    }

    /**
     * M�todo respons�vel por gerar o n�mero de protocolo de uma {@link Manifestacao}.
     * A gera��o leva em considera��o o ano do cadastro e o n�mero de manifesta��es j� cadastradas para ele.
     * 
     * @param manifestacao
     * @param mov
     * @throws DAOException
     */
    public void gerarNumeroManifestacao(Manifestacao manifestacao, Movimento mov) throws DAOException {
    	Calendar cal = Calendar.getInstance();
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			int ano = cal.get(Calendar.YEAR);
			
			long numero = dao.getNextSeq("ouvidoria", getNomeSequencia(ano));
			
			manifestacao.setNumero(String.valueOf(numero));
		} finally {
			dao.close();
		}
    }

    /**
     * M�todo que retorna o nome da sequ�ncia utilizada em um determinado ano para gera��o de n�meros de uma {@link Manifestacao}.
     * 
     * @param ano
     * @return
     */
    private String getNomeSequencia(int ano) {
    	return "geracao_numero_manifestacao_" + String.valueOf(ano);
    }
    
    /**
     * Notifica a cria��o da manifesta��o para o interessado.
     * 
     * @param pessoa
     * @param manifestacao
     * @param mov
     * @throws DAOException
     */
    public void notificarCriacaoManifestacao(Pessoa pessoa, Manifestacao manifestacao, Movimento mov) throws DAOException {
    	manifestacao = getGenericDAO(mov).refresh(manifestacao);
    	
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Cadastro de Manifesta��o para a Ouvidoria.");
		mail.setMensagem(getMensagemCadastro(pessoa, manifestacao));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem enviada no cadastro de uma manifesta��o.
     * 
     * @param pessoa
     * @param manifestacao
     * @return
     */
	private String getMensagemCadastro(Pessoa pessoa, Manifestacao manifestacao) {
		String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que o cadastro da manifesta��o de n�mero "+ manifestacao.getNumeroAno() +" foi efetuado com sucesso no sistema. "+
							"<br />Os dados da manifesta��o cadastrada foram os seguintes:" +
							"<br /><br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " + manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifesta��o:</b> " +	manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>T�tulo:</b>  " +	manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " +	manifestacao.getMensagem() +
							"<br /><br /><br />A Ouvidoria agradece sua manifesta��o e se coloca � disposi��o para acompanhamento da mesma.<br />";
		
		if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() != CategoriaSolicitante.COMUNIDADE_EXTERNA) {
			mensagem += "O acompanhamento pode ser feito, tamb�m, atrav�s do seguinte caminho no sistema: ";
			
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.DISCENTE) {
				mensagem += "SIGAA -> Portal do Discente -> Outros -> Ouvidoria -> Acompanhar Manifesta��es.";
			}
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.DOCENTE) {
				mensagem += "SIGAA -> Portal do Docente -> Outros -> Ouvidoria -> Acompanhar Manifesta��es.";
			}
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.TECNICO_ADMINISTRATIVO) {
				mensagem += "SIGRH -> Portal do Servidor -> Servi�os -> Ouvidoria -> Acompanhar Manifesta��es.";
			}
		}
		
		mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
   		            RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, N�O RESPOND�-LO. <br/><br/><br/>";
		
		return mensagem;
	}

}
