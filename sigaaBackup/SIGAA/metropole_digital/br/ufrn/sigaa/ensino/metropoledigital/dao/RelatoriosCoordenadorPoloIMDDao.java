package br.ufrn.sigaa.ensino.metropoledigital.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.relatorios.LinhaRelatorioAprovadosReprovados;


/** Classe responsável por consultas específicas para a geração de relatórios do coordenador de pólo do IMD.
 * @author Rafael Barros
 */
public class RelatoriosCoordenadorPoloIMDDao extends GenericSigaaDAO {

	/**
	 * Busca a lista de alunos ingressantes de uma escola do ensino técnico vinculados aos pólos em que o usuário é coordenador.
	 * @param listaPolos
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findListaGeralAlunos(Collection<Polo> listaPolos) throws DAOException {
		
		String projecao = "id, discente.matricula, discente.pessoa.id, discente.pessoa.nome, discente.pessoa.cpf_cnpj, turmaEntradaTecnico.id, turmaEntradaTecnico.opcaoPoloGrupo.id, turmaEntradaTecnico.opcaoPoloGrupo.polo.id, turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.nome, turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.unidadeFederativa.sigla, turmaEntradaTecnico.especializacao.id, turmaEntradaTecnico.especializacao.descricao";
		
		// Montagem da string de confições para a lista de pólos informada
		int contador = 0;
		String condicoes = "";
		for(Polo p: listaPolos){
			if(contador == 0){
				condicoes += "turmaEntradaTecnico.opcaoPoloGrupo.polo.id = " + p.getId();
			} else if(contador > 0) {
				condicoes += "OR turmaEntradaTecnico.opcaoPoloGrupo.polo.id = " + p.getId();
			}
			contador ++;
		}
		
		
		String hql = "select " + projecao +  " from DiscenteTecnico  where " + condicoes;
		
		hql = hql + " order by turmaEntradaTecnico.opcaoPoloGrupo.id, turmaEntradaTecnico.especializacao.descricao, discente.pessoa.nome";
		
		Query q = getSession().createQuery(hql);

		return HibernateUtils.parseTo(q.list(), projecao, DiscenteTecnico.class);
		
		
	}
	
	/**
	 * Busca a lista de alunos cadastrados no IMD
	 * @param listaPolos
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findListaGeralAlunosCadastrados(Collection<Polo> listaPolos) throws DAOException {
		
		String projecao = "id, discente.status, discente.matricula, discente.pessoa.id, discente.pessoa.nome, discente.pessoa.cpf_cnpj, turmaEntradaTecnico.id, turmaEntradaTecnico.opcaoPoloGrupo.id, turmaEntradaTecnico.opcaoPoloGrupo.polo.id, turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.nome, turmaEntradaTecnico.opcaoPoloGrupo.polo.cidade.unidadeFederativa.sigla, turmaEntradaTecnico.especializacao.id, turmaEntradaTecnico.especializacao.descricao";
		
		// Montagem da string de confições para a lista de pólos informada
		int contador = 0;
		String condicoes = "";
		for(Polo p: listaPolos){
			if(contador == 0){
				condicoes += "turmaEntradaTecnico.opcaoPoloGrupo.polo.id = " + p.getId();
			} else if(contador > 0) {
				condicoes += "OR turmaEntradaTecnico.opcaoPoloGrupo.polo.id = " + p.getId();
			}
			contador ++;
		}
		
		
		String hql = "SELECT " + projecao +  " FROM DiscenteTecnico  WHERE " + condicoes;
		
		hql = hql + " ORDER BY turmaEntradaTecnico.opcaoPoloGrupo.id, turmaEntradaTecnico.especializacao.descricao, discente.pessoa.nome";
		
		Query q = getSession().createQuery(hql);

		return HibernateUtils.parseTo(q.list(), projecao, DiscenteTecnico.class);
		
		
	}
		
}