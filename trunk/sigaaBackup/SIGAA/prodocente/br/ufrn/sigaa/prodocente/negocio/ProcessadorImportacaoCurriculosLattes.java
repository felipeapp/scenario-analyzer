/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 01/04/2013 
 */
package br.ufrn.sigaa.prodocente.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.xml.sax.SAXException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.prodocente.cnpq.wsclient.WsClienteCVLattes;
import br.ufrn.sigaa.prodocente.dao.CVLattesDao;
import br.ufrn.sigaa.prodocente.lattes.Curriculo;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Processador responsável pela importação automática dos currículos lattes.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorImportacaoCurriculosLattes extends AbstractProcessador {

	
	@Override
	public Object execute(Movimento m) throws NegocioException, ArqException,
			RemoteException {
		
		PessoaLattesMov mov = (PessoaLattesMov) m;
		validate(mov);
		
		CVLattesDao dao = getDAO(CVLattesDao.class, mov);
		try {
			
			if(mov.getAcao() == PessoaLattesMov.ACAO_SINCRONIZAR_IDS)
				WsClienteCVLattes.sincronizarIdentificadoresCNPq(mov.getPessoasLattes());
			
			if(mov.getAcao() == PessoaLattesMov.ACAO_VERIFICAR_DATAS)
				WsClienteCVLattes.verificarDatasUltimaAtualizacao(mov.getPessoasLattes());
			
			if(mov.getAcao() == PessoaLattesMov.ACAO_EXTRAIR_IMPORTAR_CVS){
				WsClienteCVLattes.extrairCurriculos(mov.getPessoasLattes());
				importarCurriculos(mov);
			}
			
			for(PessoaLattes pl: mov.getPessoasLattes())
				dao.updateNoFlush(pl);
			
		} catch (Exception e){
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * Descompacta os arquivos obtidos do web service e invoca o processador para importação dos
	 * currículos na base de dados institucional.
	 *  
	 * @param mov
	 * @throws IOException
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws SAXException
	 */
	private void importarCurriculos(PessoaLattesMov mov) throws IOException, NegocioException, ArqException, SAXException {
		ImportLattesDao dao = getDAO(ImportLattesDao.class, mov);
		try {
			for(PessoaLattes pl: mov.getPessoasLattes()){
				pl.setUltimaVerificacao(new Date());
				
				if(!isEmpty(pl.getAtualizar()) && pl.getAtualizar() && !isEmpty(pl.getIdCnpq()) && !isEmpty(pl.getCvZipado())){
					ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(pl.getCvZipado()));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					if(zis.getNextEntry() != null){
						byte[] buffer = new byte[4096];
						int numBytes;
						while ((numBytes = zis.read(buffer, 0, buffer.length)) != -1)
							baos.write(buffer, 0, numBytes);
						zis.closeEntry();
					}
					Curriculo c = new Curriculo(baos.toByteArray(), dao);
					List<? extends Producao> producoes = c.interpretar();
					
					if (producoes.isEmpty()) {
						// TODO registrar qual currículo está vazio para uso futuro
						continue;
					}
					StringBuilder sb = new StringBuilder();
					for (Producao p : producoes) {
						sb.append("----------------------\n");
						sb.append("Tipo: " + p.getClass().getSimpleName());
						sb.append(" - Titulo: " + p.getTitulo());
						sb.append(" - Ano Ref.: " + p.getAnoReferencia());
						sb.append(" - Seq. Producao: " + p.getSequenciaProducao() + "\n");
					}
					CurriculoLattesMov cmov = new CurriculoLattesMov();
					cmov.setProducoes(producoes);
					cmov.setServidor(pl.getServidor());
					cmov.setPessoa(pl.getPessoa());
					cmov.setConteudo(sb.toString());
					cmov.setXml(new String(baos.toByteArray()));
					cmov.setCodMovimento(SigaaListaComando.IMPORTAR_CURRICULO_LATTES);
					cmov.setUsuarioLogado(new UsuarioGeral(UsuarioGeral.TIMER_SISTEMA));
					
					cmov.setAnoReferencia(null);
					
					ProcessadorImportacaoLattes proc = new ProcessadorImportacaoLattes();
					proc.execute(cmov);
				}
			}
		} finally {
			dao.close();
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		PessoaLattesMov m = (PessoaLattesMov) mov;
		if(ValidatorUtil.isEmpty(m.getPessoasLattes())) {
			throw new NegocioException("Não há pessoas a processar.");
		}
	}

}
