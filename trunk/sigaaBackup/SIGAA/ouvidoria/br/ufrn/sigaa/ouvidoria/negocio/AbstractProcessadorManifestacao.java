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
 * Processador abstrato que contém conmportamentos comuns a outros processadores de manifestação.
 * 
 * @author Bernardo
 *
 */
public abstract class AbstractProcessadorManifestacao extends AbstractProcessador {
    
	/**
	 * Cadastra no banco os dados do interessado e da manifestação.
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
	 * Salva um arquivo .pdf ou . doc da solicitação caso o usuário tenha submetido um.
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
			return null;  // valor padrão para manifestação que nao possui anexo
		}
	}
    /**
     * Persiste a manifestação.
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
     * Método responsável por gerar o número de protocolo de uma {@link Manifestacao}.
     * A geração leva em consideração o ano do cadastro e o número de manifestações já cadastradas para ele.
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
     * Método que retorna o nome da sequência utilizada em um determinado ano para geração de números de uma {@link Manifestacao}.
     * 
     * @param ano
     * @return
     */
    private String getNomeSequencia(int ano) {
    	return "geracao_numero_manifestacao_" + String.valueOf(ano);
    }
    
    /**
     * Notifica a criação da manifestação para o interessado.
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
		mail.setAssunto("Cadastro de Manifestação para a Ouvidoria.");
		mail.setMensagem(getMensagemCadastro(pessoa, manifestacao));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem enviada no cadastro de uma manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @return
     */
	private String getMensagemCadastro(Pessoa pessoa, Manifestacao manifestacao) {
		String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que o cadastro da manifestação de número "+ manifestacao.getNumeroAno() +" foi efetuado com sucesso no sistema. "+
							"<br />Os dados da manifestação cadastrada foram os seguintes:" +
							"<br /><br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " + manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifestação:</b> " +	manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>Título:</b>  " +	manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " +	manifestacao.getMensagem() +
							"<br /><br /><br />A Ouvidoria agradece sua manifestação e se coloca à disposição para acompanhamento da mesma.<br />";
		
		if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() != CategoriaSolicitante.COMUNIDADE_EXTERNA) {
			mensagem += "O acompanhamento pode ser feito, também, através do seguinte caminho no sistema: ";
			
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.DISCENTE) {
				mensagem += "SIGAA -> Portal do Discente -> Outros -> Ouvidoria -> Acompanhar Manifestações.";
			}
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.DOCENTE) {
				mensagem += "SIGAA -> Portal do Docente -> Outros -> Ouvidoria -> Acompanhar Manifestações.";
			}
			if(manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.TECNICO_ADMINISTRATIVO) {
				mensagem += "SIGRH -> Portal do Servidor -> Serviços -> Ouvidoria -> Acompanhar Manifestações.";
			}
		}
		
		mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
   		            RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>";
		
		return mensagem;
	}

}
