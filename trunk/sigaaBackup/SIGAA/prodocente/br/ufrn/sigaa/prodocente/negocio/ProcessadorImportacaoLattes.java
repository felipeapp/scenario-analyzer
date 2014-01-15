/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador para importar publica��es do curr�culo Lattes
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
						// Produ��es n�o importadas por causa do ano de referencia
						detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
								+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
								+ " - Ano: " + p.getAnoReferencia() + "\n";
						
						conteudoLido.append("ANO INVALIDO -- " + detalhesProducao + "\n");
						cMov.setProducoesNaoImportadas(true);
					}
				} else {
					// Todas as produ��es independente do ano
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
	 * Realiza a importa��o das produ��es para o banco.
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
		// Apenas as que ainda n�o foram importadas 
		Producao pImportada = dao.isProducaoImportada(p, cMov.getServidor());
		if (pImportada == null) {
			p.setServidor(cMov.getServidor());
			p.setLeituraXml(leitura);
			p.setDataCadastro(new Date());
			p.setAtivo(true);
								
			dao.create(p);
			
			detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
					+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
					+ " - ID Produ��o: " + p.getId() + "\n";
			
			conteudoImportado.append(detalhesProducao);
			conteudoLido.append("IMPORTADO -- " + detalhesProducao + "\n");
			producoesImportadas = true;
			
		} else {
			detalhesProducao = "Tipo: " + p.getClass().getSimpleName() + " - Titulo: " + p.getTitulo() 
					+ " - Ano Ref.: " + p.getAnoReferencia() + " - Seq. Producao: " + p.getSequenciaProducao() 
					+ " - ID Produ��o: " + pImportada.getId() + "\n";

			conteudoLido.append("IMPORTADO ANTERIORMENTE -- " + detalhesProducao + "\n");
			producoesImportadas = true;
		}
		return producoesImportadas;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {}

}
