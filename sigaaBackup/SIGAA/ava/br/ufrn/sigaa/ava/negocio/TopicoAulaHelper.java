/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/07/2010
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.ParecerPlanoReposicaoAula;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe auxiliar para Tópicos de Aula.
 * 
 * @author Bernardo
 *
 */
public class TopicoAulaHelper {

	/**
	 * Cria um Tópico de Reposição de aula de acordo com os dados do parecer passado como parâmetro.
	 * 
	 * @param parecer
	 * @return
	 * @throws DAOException 
	 */
	public static TopicoAula criar(ParecerPlanoReposicaoAula parecer) throws DAOException {
		DocenteTurmaDao dao = DAOFactory.getInstance().getDAO(DocenteTurmaDao.class);
		
		DadosAvisoFalta dados = parecer.getPlanoAula().getFaltaHomologada().getDadosAvisoFalta();
		TopicoAula topico = new TopicoAula();
		topico.setData(parecer.getPlanoAula().getDataAulaReposicao());
		topico.setFim(topico.getData());
		Formatador format = Formatador.getInstance();
		topico.setDescricao("Reposição da Aula do Dia "
				+ format.formatarData(parecer.getPlanoAula()
						.getFaltaHomologada().getDadosAvisoFalta()
						.getDataAula()));
		topico.setConteudo(parecer.getPlanoAula().getDidatica());
		topico.setVisivel(true);
		topico.adicionarDocenteTurma(dao.findByDocenteTurma(dados.getDocente().getId(), null, dados.getTurma().getId()));
		
		topico.setTurma(dados.getTurma());
		
		return topico;
		
	}
	
	/**
	 * Cria um Tópico de Aula que representa uma aula extra.
	 * 
	 * @param aula
	 * @param servidor
	 * @return
	 */
	public static TopicoAula criar(AulaExtra aula, Servidor servidor, DocenteExterno docenteExterno) throws DAOException {
		
		TurmaDao dao = null;
		TopicoAula t = null;
		
		try {
			t = new TopicoAula();
			dao = DAOFactory.getInstance().getDAO(TurmaDao.class);
			
			String tipoAula;
			if (aula.getTipo().equals(AulaExtra.ENSINO_INDIVIDUAL))
				tipoAula = " - Aula de Ensino Individual";
			else {
				tipoAula = " - Aula Extra ";
				tipoAula += aula.getTipo().equals(AulaExtra.ADICIONAL) ? "Adicional" : "de Reposição";
			}
			
			t.setDescricao(aula.getDescricao()+tipoAula);

			if ( aula.getObservacoes() != "" )
				t.setConteudo(aula.getObservacoes());
			else
				t.setConteudo(aula.getTipo().equals(AulaExtra.ENSINO_INDIVIDUAL) ? "Aula de Ensino Individual." : "Aula Extra.");
			
			t.setDataCadastro(aula.getCriadoEm());
			t.setData(aula.getDataAula());
			t.setFim(aula.getDataAula());
			t.setVisivel(true);
			t.setAulaExtra(aula);
			
			List<String> cadastrarEm = new ArrayList<String>();
			cadastrarEm.add(String.valueOf(aula.getTurma().getId()));
					
			Collection<DocenteTurma> docentes = dao.findByExactField(DocenteTurma.class, "turma.id", aula.getTurma().getId());
			for ( DocenteTurma docente : docentes ){
				
				Servidor docenteTopico = docente.getDocente();
				DocenteExterno docenteExternoTopico = docente.getDocenteExterno();
				
				if ( servidor != null && docenteTopico != null && servidor.getId() == docenteTopico.getId()){
					t.adicionarDocenteTurma(docente);
					break;
				}
				
				if ( docenteExterno != null && docenteExternoTopico != null && docenteExterno.getId() == docenteExternoTopico.getId()){
					t.adicionarDocenteTurma(docente);
					break;
				}
			}
			}finally{
				if (dao != null)
					dao.close();
			}
			
		return t;		
	}
}