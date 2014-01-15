package br.ufrn.sigaa.assistencia.negocio;

import java.io.IOException;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dao.CalendarioBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;

public class ProcessadorCalendarioSAE extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		AnoPeriodoReferenciaSAE anoPeriodo = movCad.getObjMovimentado();
		validate(mov);
		
		CalendarioBolsaAuxilioDao dao = getDAO(CalendarioBolsaAuxilioDao.class, movCad);
		try {

			AnoPeriodoReferenciaSAE anoBD = dao.findByPrimaryKey(anoPeriodo.getId(), AnoPeriodoReferenciaSAE.class);
			if ( anoPeriodo.getId() > 0 ) {
				for (CalendarioBolsaAuxilio calendar : anoBD.getCalendario()){
					if ( !anoPeriodo.getCalendario().contains(calendar) ) {
						calendar.setAtivo(Boolean.FALSE);
						dao.update(calendar);
					}
				}
				
				dao.detach(anoBD);
			}

			anoPeriodo.setAtivo(Boolean.TRUE);
			
			// Anexar arquivo
			if (movCad.getObjAuxiliar() != null) {
				anexarArquivo(anoPeriodo, (UploadedFile) movCad.getObjAuxiliar());
			}
			
			// Remover arquivo anexo
			if ( anoPeriodo.getIdArquivo() == null && anoBD != null && anoBD.getIdArquivo() != null){
				EnvioArquivoHelper.removeArquivo(anoBD.getIdArquivo());
			}
			
			dao.createOrUpdate(anoPeriodo);
			
			if ( anoPeriodo.isVigente() ) {
				dao.inativarCalendarios(anoPeriodo);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return null;
	}
		
	/**
	 * Anexa arquivo ao Ano Período. Caso seja inserido um novo arquivo em um ano
	 * período que já possui arquivo anexado, este será substituído pelo novo.
	 * 
	 * @param anoPeriodo
	 * @param file
	 * @throws IOException
	 */
	private void anexarArquivo(AnoPeriodoReferenciaSAE anoPeriodo, UploadedFile file) throws IOException{
		// Verifica se já existe arquivo anexado
		if (anoPeriodo.getIdArquivo() != null && anoPeriodo.getIdArquivo() > 0){
			EnvioArquivoHelper.inserirArquivo(anoPeriodo.getIdArquivo(), file.getBytes(),file.getContentType(), file.getName());
		} else {
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
			anoPeriodo.setIdArquivo(idArquivo);
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens mensagens = new ListaMensagens();
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		AnoPeriodoReferenciaSAE ano = movCad.getObjMovimentado();

		if (ano.getId() <= 0){ 
			AnoPeriodoReferenciaSAEDao dao = getDAO(AnoPeriodoReferenciaSAEDao.class, movCad);
			try{
				for (AnoPeriodoReferenciaSAE anoBD : getDAO(AnoPeriodoReferenciaSAEDao.class, movCad).findAll(AnoPeriodoReferenciaSAE.class)) {
					if (anoBD.getAnoPeriodo().equals(ano.getAnoPeriodo())){
						mensagens.addErro("Ano Período já cadastrado");
						break;
					}
				}
			} finally{
				dao.close();			
			}
		}
		checkValidation(mensagens);
	}
	
}
