/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/02/2011
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesVideoTurma;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ava.jsf.VideoTurmaMBean;
import br.ufrn.sigaa.ava.util.MimeTypeVideoUtil;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Processador para associar v�deos com o conte�do das aulas
 * no portal da turma.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorCadastroVideoTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_VIDEO_TURMA)) {
			return salvar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.INATIVAR_VIDEO_TURMA)) {
			inativar(mov);
		}
		return null;
	}
	
	/**
	 * Criar v�deo.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object salvar(Movimento mov) throws NegocioException, ArqException, RemoteException {		
		MovimentoCadastroVideoTurma pMov = (MovimentoCadastroVideoTurma) mov;		
		GenericDAO dao = null;
		TurmaDao tDao = null;
		
		try {
			VideoTurma video = pMov.getVideo();
			UploadedFile arquivo = pMov.getArquivoEnviado();
			Integer idArquivoPA = pMov.getIdArquivoPA();
			Character enviar = pMov.getEnviar();
			tDao = getDAO(TurmaDao.class, mov);
			
			List<String> cadastrarEm = pMov.getCadastrarEm();
			// Alterando o v�deo
			if ( video.getId() > 0 ){
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(video.getTurma().getId()));
			}
				
			Integer idArquivoAntigo = null;
			
			for (String tid : cadastrarEm) {
				Turma t = tDao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));

				VideoTurma vCopy = new VideoTurma();
				try { 
					BeanUtils.copyProperties(vCopy, video);
				} catch (Exception e) {
					throw new ArqException(e);
				}
				
				// Se o docente enviou um arquivo
				byte[] bytes = null;
				if (enviar == VideoTurmaMBean.VIDEO_INTERNO && arquivo != null)
					bytes = arquivo.getBytes();
				else if (enviar == VideoTurmaMBean.VIDEO_PORTA_ARQUIVOS){
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					EnvioArquivoHelper.recuperaArquivo(os, idArquivoPA);
					bytes = os.toByteArray();
				}
				
				if ((enviar == VideoTurmaMBean.VIDEO_INTERNO && arquivo != null && arquivo.getSize() > 0) ||
					(enviar == VideoTurmaMBean.VIDEO_PORTA_ARQUIVOS && idArquivoPA != null && idArquivoPA > 0)){
					idArquivoAntigo = formatarArquivo(arquivo, idArquivoPA, enviar, vCopy, bytes);
					
				// Se mudou para link externo e j� tiver um arquivo salvo, marca-o para remo��o.
				} else if (!StringUtils.isEmpty(vCopy.getLink()))
					idArquivoAntigo = vCopy.getIdArquivo();
				
				// Se existia um arquivo antigo, remove-o.
				if (idArquivoAntigo != null)
					EnvioArquivoHelper.removeArquivo(idArquivoAntigo);

				boolean cadastrar = vCopy.getId() == 0;
				// Cria ou atualiza o v�deo
				dao = getGenericDAO(pMov);
				if (cadastrar) {
					vCopy.setTurma(t);
					vCopy.setTopicoAula(getTopicoEquivalente(t, video, pMov));
					vCopy.setMaterial(new MaterialTurma(TipoMaterialTurma.VIDEO));
					vCopy.setDataUltimaTentativaConversao(new Date());
					MaterialTurmaHelper.definirNovoMaterialParaTopico(vCopy, vCopy.getTopicoAula(), t);
				}
				
				// Se estiver atualizando o v�deo, salva somente os campos alterados no form, para n�o ter perigo de apagar as informa��es inseridas pelo conversor.
				if (vCopy.getId() > 0){
					dao.updateFields(VideoTurma.class, vCopy.getId(), new String [] {
																					"titulo", "descricao", "topicoAula.id", "telaCheia",
																					"altura", "converter", "idArquivo", "link", "erro",
																					"mensagemConversao", "contentType", "idArquivoConvertido"
																	}, new Object [] {
																					vCopy.getTitulo(), vCopy.getDescricao(),
																					vCopy.getTopicoAula() == null ? null : vCopy.getTopicoAula().getId(), vCopy.isTelaCheia(),
																					vCopy.getAltura(), vCopy.isConverter(), vCopy.getIdArquivo(),
																					vCopy.getLink(), vCopy.isErro(), vCopy.getMensagemConversao(), vCopy.getContentType(), vCopy.getIdArquivoConvertido()
																	});
				} else
					dao.create(vCopy);
				
				MaterialTurmaHelper.atualizarMaterial(dao, vCopy, cadastrar);
				
				// Se o v�deo est� em um formato que deve ser convertido, aciona a thread de convers�o.
				// ParametrosTurmaVirtual.ENDERECO_VIDEO_CONVERTER
				if (vCopy.isConverter() && bytes != null){
					ConfiguracoesVideoTurma videoConfig = MimeTypeVideoUtil.getConfiguracoesVideoByMimeType(bytes);
					ConverterVideoThread cThread = new ConverterVideoThread(vCopy,videoConfig,bytes,ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.ENDERECO_VIDEO_CONVERTER));
					cThread.start();
				}
			}	
		
		} catch (IOException e) {
			throw new ArqException (e);
		} finally {
			if (dao != null)
				dao.close();
			if (tDao != null)
				tDao.close();
		}
		
		return null;
	}

	/**
	 * Dependendo do mimetype do arquivo, indica se o v�deo dever� ser convertido ou n�o. Verificando
	 * se o arquivo realmente � de v�deo ou est� em um formato que n�o precisa ser convertido, como 
	 * swf ou flv. Em adi��o, caso o v�deo esteja sendo atualizado, trocando o arquivo de v�deo, marca 
	 * o arquivo antigo para remo��o.	
	 * 
	 * @param arquivo
	 * @param vCopy
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private Integer formatarArquivo(UploadedFile arquivo, Integer idArquivoPA, Character enviar, VideoTurma vCopy, byte[] bytes)
			throws IOException, NegocioException {
		Integer idArquivoAntigo;
		
		String nome = null;
		String contentType = null;
		
		if (enviar == VideoTurmaMBean.VIDEO_INTERNO){
			nome = arquivo.getName();
			contentType = arquivo.getContentType();
		} else if (enviar == VideoTurmaMBean.VIDEO_PORTA_ARQUIVOS){
			nome = EnvioArquivoHelper.recuperaNomeArquivo(idArquivoPA);
			contentType = EnvioArquivoHelper.recuperaContentTypeArquivo(idArquivoPA);
		}
		
		// para verifica��es do IE que faz upload como appliation/octet-stream
		String mimeTypeVideo = contentType; 
			
		// Se no mimetype do v�deo h� a palavra "video", aceita.
		boolean formatoOk = mimeTypeVideo != null && mimeTypeVideo.contains("video");
		
		// Se o mime type do arquivo recebido n�o for suportado, verifica se o conte�do do arquivo � um FLV
		if ( !formatoOk && bytes != null ) {
			formatoOk = MimeTypeVideoUtil.isFormatoVideoOk(bytes);
			mimeTypeVideo = MimeTypeVideoUtil.getMimeTypeVideo(bytes);	
		}	
		
		if ( MimeTypeVideoUtil.isCWS(bytes) )
			throw new NegocioException ("Prezado(a) docente, apesar do formato do v�deo ser SWF, seu formato interno est� comprimido e devido a isso n�o poder� ser inserido na turma virtual.");			

			
		if (!formatoOk)
			throw new NegocioException ("Prezado(a) docente, somente arquivos de V�deo podem ser enviados. Selecione um arquivo de v�deo e tente novamente.");			
		
		// Se o arquivo n�o estiver em um dos formatos para n�o converter, deve ser convertido.
		vCopy.setConverter(true);
		
		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
		EnvioArquivoHelper.inserirArquivo(idArquivo, bytes, mimeTypeVideo, nome);
		
		// Se o v�deo j� tinha um arquivo, marca-o para remo��o.
		idArquivoAntigo = vCopy.getIdArquivo();
		
		vCopy.setIdArquivo(idArquivo);
		vCopy.setLink(null);		
		vCopy.setContentType(contentType);
		
		// Remove o v�deo convertido para que volte a exibir a tela de "Video sendo convertido", caso o video v� para a convers�o.
		vCopy.setIdArquivoConvertido(null);
		
		vCopy.setErro(false);
		vCopy.setMensagemConversao(null);
		return idArquivoAntigo;
	}

	/**
	 * Inativa o v�deo.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void inativar(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastroVideoTurma pMov = (MovimentoCadastroVideoTurma) mov;		
		GenericDAO dao = getGenericDAO(mov);		
		try {
			VideoTurma video = pMov.getVideo();			
			video = dao.findByPrimaryKey(video.getId(), VideoTurma.class, "id", "material.id", "topicoAula.id");
			
			if (video.getIdArquivo() != null)
				EnvioArquivoHelper.removeArquivo(video.getIdArquivo());
			
			if (video.getIdArquivoConvertido() != null)
				EnvioArquivoHelper.removeArquivo(video.getIdArquivoConvertido());
			
			dao.updateFields(VideoTurma.class, video.getId(), new String [] {"ativo", "idArquivo", "idArquivoConvertido"}, new Object [] {false, null, null});
			dao.updateField(MaterialTurma.class, video.getMaterial().getId(), "ativo", false);
			MaterialTurmaHelper.reOrdenarMateriais(video.getAula());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * M�todo que retorna o t�pico de aula de uma outra turma onde ser� cadastrada ao v�deo.
	 * Usado quando o v�deo deve ser cadastrado em mais de uma turma.
	 * @param v
	 * @param dom
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private TopicoAula getTopicoEquivalente(Turma t, DominioTurmaVirtual dom, MovimentoCadastroVideoTurma  mov) throws DAOException, NegocioException {
		TopicoAulaDao dao = null;
		
		try {
			dao = getDAO(TopicoAulaDao.class, mov);
			TopicoAula aula = (TopicoAula) ReflectionUtils.getFieldValue(dom, "topicoAula");
			aula = dao.findByPrimaryKey(aula.getId(), TopicoAula.class, "id", "descricao");
			TopicoAula equivalente = dao.findByDescricao(t, aula.getDescricao());
			if (equivalente == null)
				throw new NegocioException("N�o � poss�vel cadastrar na turma " + t + " porque n�o existe o t�pico de aula " + aula.getDescricao() + " cadastrado nela.");
			return equivalente;
		}finally{
			if ( dao != null )
				dao.close();
		}
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}
