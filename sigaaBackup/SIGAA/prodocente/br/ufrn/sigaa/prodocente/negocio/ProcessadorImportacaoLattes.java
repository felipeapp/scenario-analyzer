/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.prodocente.lattes.LeituraXML;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Processador para importar publicações do currículo Lattes
 * 
 * @author David Pereira
 *
 */
public class ProcessadorImportacaoLattes extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		CurriculoLattesMov cMov = (CurriculoLattesMov) mov;
		ImportLattesDao dao = getDAO(ImportLattesDao.class, mov);
		
		try {
			LeituraXML leitura = new LeituraXML();
			leitura.setData(new Date());
			leitura.setConteudoXml(cMov.getXml());
			leitura.setServidor(cMov.getServidor());
			leitura.setPessoa(cMov.getPessoa());
			leitura.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(leitura);
			
			List<? extends Producao> producoes = cMov.getProducoes();
			StringBuilder conteudoLido = new StringBuilder();
			StringBuilder conteudoImportado = new StringBuilder();
			
			boolean producoesImportadas = false;
			
			for (Producao p : producoes) {
				String detalhesProducao = null;
				
				if(cMov.getAnoReferencia() != null){
					// Apenas do ano especificado
					if (cMov.getAnoReferencia().equals(p.getAnoReferencia())) {					
						
						producoesImportadas = importar(cMov, dao, leitura,
								conteudoLido, conteudoImportado, p);
						
					} else {
						// Produções não importadas por causa do ano de referencia
						detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
								+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
								+ " - Ano: " + p.getAnoReferencia() + "\n";
						
						conteudoLido.append("ANO INVALIDO -- " + detalhesProducao + "\n");
						cMov.setProducoesNaoImportadas(true);
					}
				} else {
					// Todas as produções independente do ano
					producoesImportadas = importar(cMov, dao, leitura,
							conteudoLido, conteudoImportado, p);
				}
			}
			leitura.setConteudoImportado(conteudoImportado.toString());
			leitura.setConteudoLido(conteudoLido.toString());
			dao.update(leitura);		
			
			if (!producoesImportadas)
				cMov.getProducoes().removeAll(producoes);
			
		} finally {
			dao.close();
		}
		return null;
	}

	/**
	 * Realiza a importação das produções para o banco.
	 * 
	 * @param cMov
	 * @param dao
	 * @param leitura
	 * @param conteudoLido
	 * @param conteudoImportado
	 * @param p
	 * @return
	 * @throws DAOException
	 */
	private boolean importar(CurriculoLattesMov cMov, ImportLattesDao dao,
			LeituraXML leitura, StringBuilder conteudoLido,
			StringBuilder conteudoImportado, Producao p) throws DAOException {
		boolean producoesImportadas;
		String detalhesProducao;
		// Apenas as que ainda não foram importadas 
		Producao pImportada = dao.isProducaoImportada(p, cMov.getServidor());
		if (pImportada == null) {
			p.setServidor(cMov.getServidor());
			p.setLeituraXml(leitura);
			p.setDataCadastro(new Date());
			p.setAtivo(true);
								
			dao.create(p);
			
			detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
					+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
					+ " - ID Produção: " + p.getId() + "\n";
			
			conteudoImportado.append(detalhesProducao);
			conteudoLido.append("IMPORTADO -- " + detalhesProducao + "\n");
			producoesImportadas = true;
			
		} else {
			detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
					+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
					+ " - ID Produção: " + pImportada.getId() + "\n";

			conteudoLido.append("IMPORTADO ANTERIORMENTE -- " + detalhesProducao + "\n");
			producoesImportadas = true;
		}
		return producoesImportadas;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {}

}
