package br.ufrn.sigaa.projetos.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.IntervaloCronograma;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe auxiliar para as operações sobre os Cronogramas dos Projetos. 
 * 
 * @author Jean Guerethes
 */
public class CronogramaProjetoHelper {

	/**
	 * Carrega o Cronograma do Plano de Trabalho de Extensão e realiza construção do mesmo.
	 */
	public static List<CronogramaProjeto> submeterCronogramaPlanoTrabalhoExtensao(GenericDAO dao, PlanoTrabalhoExtensao plano, List<CronogramaProjeto> cronogramas) throws DAOException{

	 List<CronogramaProjeto> cronogramasBD = (List<CronogramaProjeto>) dao.findByExactField(CronogramaProjeto.class, "planoTrabalhoExtensao.id", plano.getId());	
	 List<CronogramaProjeto> result = submeterCronograma(dao, cronogramasBD, cronogramas);
	 
	 //Serve para Desassociar, o Cronograma da sessão.
	 for (CronogramaProjeto cronogramaProjeto : cronogramasBD) {
		dao.detach(cronogramaProjeto.getPlanoTrabalhoExtensao());
		dao.detach(cronogramaProjeto);
	 } 
	 
	 return result;
	 
	}

	/**
	 * Carrega o Cronograma do Projeto e realiza construção do mesmo.
	 */
	public static List<CronogramaProjeto> submeterCronogramaProjeto(GenericDAO dao, Projeto projeto, List<CronogramaProjeto> cronogramas) throws DAOException{

		 List<CronogramaProjeto> cronogramasBD = (List<CronogramaProjeto>) dao.findByExactField(CronogramaProjeto.class, "projeto.id", projeto.getId());	
		 List<CronogramaProjeto> result = submeterCronograma(dao, cronogramasBD, cronogramas);
		 
		 //Serve para Desassociar, o Cronograma da sessão.
		 for (CronogramaProjeto cronogramaProjeto : cronogramasBD) {
			dao.detach(cronogramaProjeto.getProjeto());
			dao.detach(cronogramaProjeto);
		 } 
		 
		 return result;
	}
	
	/**
	 * Método responsável pelo gerenciamento do cronograma do projeto e/ou dos planos, sendo o responsável pela gerencia do cadastro ou não de uma novo registro.  
	 * 
	 * @param dao
	 * @param proj
	 * @param cronogramas
	 * @return
	 * @throws DAOException
	 */
	private static List<CronogramaProjeto> submeterCronograma(GenericDAO dao, List<CronogramaProjeto> cronogramasBD, List<CronogramaProjeto> cronogramas) throws DAOException{
		
		if (cronogramasBD.size() == cronogramas.size()) {
			
			// Apenas associa ao novo cronograma os id's do cronograma anterior, evitando assim que seja inseridos novos.
			for (int i = 0; i < cronogramasBD.size(); i++) {
				cronogramas.get(i).setId( cronogramasBD.get(i).getId() );
				verificarIntervaloCronograma(dao, cronogramas, cronogramasBD, i);
			}
		
		} else if ( cronogramas.size() > cronogramasBD.size() ) {

			// Apenas associa ao novo cronograma os id's do cronograma anterior, evitando assim que seja inseridos novos, 
			// até atingir a quantidade de atividades anteriormente informada.
			for (int i = 0; i <  cronogramasBD.size(); i++) {
				cronogramas.get(i).setId( cronogramasBD.get(i).getId() );
				verificarIntervaloCronograma(dao, cronogramas, cronogramasBD, i);
			}
			
		} else {
			
			// Apenas associa ao novo cronograma os id's do cronograma anterior, evitando assim que seja inseridos novos,
			// removendo as atividades que ficaram se associação.
			for (int i = 0; i <  cronogramas.size(); i++) {
				cronogramas.get(i).setId( cronogramasBD.get(i).getId() );
				verificarIntervaloCronograma(dao, cronogramas, cronogramasBD, i);
			}
		}
	
		return cronogramas;
	}

	/**
	 * Serve para associar os id's dos Intervalos do cronograma evitando assim que seja criado uma nova linha, para cada alteração realizada.
	 * 
	 * @param dao
	 * @param cronogramas
	 * @param cronogramasBD
	 * @param i
	 * @throws DAOException
	 */
	private static void verificarIntervaloCronograma(GenericDAO dao,
			List<CronogramaProjeto> cronogramas,
			List<CronogramaProjeto> cronogramasBD, int i) throws DAOException {
		List<IntervaloCronograma> cronoBD = new ArrayList<IntervaloCronograma>(cronogramasBD.get(i).getIntervalos());
		List<IntervaloCronograma> crono = new ArrayList<IntervaloCronograma>(cronogramas.get(i).getIntervalos());
		
		if ( crono.size() == cronoBD.size() ) {
			
			for (int j = 0; j < cronogramasBD.get(i).getIntervalos().size(); j++)
				crono.get(j).setId(cronoBD.get(j).getId());
			
		} else if ( crono.size() > cronoBD.size() ) {
		
			for (int j = 0; j < cronoBD.size(); j++)
				crono.get(j).setId( cronoBD.get(j).getId() );

		} else {
			
			for (int j = 0; j < crono.size(); j++)
				crono.get(j).setId( cronoBD.get(j).getId() );
			
			for (int k = crono.size(); k < cronoBD.size(); k++) {
				dao.detach(cronoBD.get( k ));
				dao.detach(cronoBD.get( k ).getCronograma());
				dao.remove(cronoBD.get( k ));
			}
		}
	}
	
	/**
	 * Responsável por carregar a tela do cronograma de um projeto de pesquisa.
	 * 
	 * @param dao
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public static TelaCronograma carregarTelaCronogramaPesquisa( GenericDAO dao, ProjetoPesquisa projeto ) throws DAOException {
		projeto = dao.findByPrimaryKey( projeto.getId(), ProjetoPesquisa.class );
		
		projeto.getProjeto().setCronograma(new ArrayList<CronogramaProjeto>());
		projeto.getProjeto().getCronograma().addAll(
				dao.findByExactField(CronogramaProjeto.class, "projeto.id", projeto.getProjeto().getId(), "asc", "ordem") );

		return new TelaCronograma(projeto.getEdital().getInicioExecucaoProjetos(), 
						projeto.getEdital().getFimExecucaoProjetos(), 
					projeto.getProjeto().getCronograma());
	}
	
	/**
	 * Responsável por carregar o cronograma de Pesquisa com as datas definidas no Edital de Pesquisa
	 * 
	 * @param dao
	 * @param projeto
	 * @param editalPesquisa
	 * @return
	 * @throws DAOException
	 */
	public static TelaCronograma carregarTelaCronogramaProjetoEditalPesquisa( GenericDAO dao, Projeto projeto, EditalPesquisa editalPesquisa ) throws DAOException {
		projeto = dao.findByPrimaryKey( projeto.getId(), Projeto.class );
		editalPesquisa = dao.findByPrimaryKey( editalPesquisa.getId(), EditalPesquisa.class );
		
		projeto.setCronograma(new ArrayList<CronogramaProjeto>());
		projeto.getCronograma().addAll(
				dao.findByExactField(CronogramaProjeto.class, "projeto.id", projeto.getId(), "asc", "ordem") );

		return new TelaCronograma(editalPesquisa.getInicioExecucaoProjetos(), 
					editalPesquisa.getFimExecucaoProjetos(), 
				projeto.getCronograma());
	}

	public static ListaMensagens validate( TelaCronograma telaCronograma ){
		ListaMensagens lista = new ListaMensagens();
		
		if ( !isEmpty( telaCronograma.getAtividade() )  ) {
		
			for (int i = 0; i < telaCronograma.getAtividade().length; i++) {
				if (ValidatorUtil.isEmpty(telaCronograma.getAtividade()[i]))
					lista.addErro("A Atividade desenvolvida (" + (i+1) + ") não apresenta descrição.");
			}
			
			int k=1;
			for (CronogramaProjeto cronograma : telaCronograma.getCronogramas()) {
				if (cronograma.getIntervalos().isEmpty())
					lista.addErro("A Atividade desenvolvida (" + k + ") não apresenta à definição dos meses.");
				k++;
			}
		
		} else {
			lista.addErro("Informe ao menos uma atividade para o cronograma.");
		}
		 
		return lista;
	}

	public static ListaMensagens validateTamanhoAtividade( TelaCronograma telaCronograma, int tamanho ){
		ListaMensagens lista = new ListaMensagens();
		if ( !isEmpty( telaCronograma.getAtividade() )  ) {
			for (int i = 0; i < telaCronograma.getAtividade().length; i++) {
				if (telaCronograma.getAtividade()[i].trim().length() > tamanho) { 
					lista.addErro("A descrição da atividade (" + (i+1) + ") deve conter no máximo " + tamanho + " caracteres.");
				}
			}
		}
		 
		return lista;
	}

}