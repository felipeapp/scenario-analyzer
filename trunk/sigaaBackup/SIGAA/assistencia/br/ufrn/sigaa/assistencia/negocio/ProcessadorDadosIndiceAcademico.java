package br.ufrn.sigaa.assistencia.negocio;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.DadosIndiceAcademicoDao;
import br.ufrn.sigaa.assistencia.dominio.DadosIndiceAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

public class ProcessadorDadosIndiceAcademico extends AbstractProcessador {
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		DadosIndiceAcademico dadosIndAca = movCad.getObjMovimentado();
		validate(mov);
		
		if (SigaaListaComando.CADASTRAR_INDICES_ACADEMICOS.equals(mov.getCodMovimento())) {
			
			Collection<DadosIndiceAcademico> dados = new ArrayList<DadosIndiceAcademico>();
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class, mov);
			try {
				Map<String, Map<String, Object>> sumarioIndices = 
						dao.relatorioSumarioMediaIndicesAcademicos(0, 0, Boolean.FALSE);
				
				for (String map : sumarioIndices.keySet()) {
					DadosIndiceAcademico dado = new DadosIndiceAcademico();
					dado.setAnoReferencia(dadosIndAca.getAnoReferencia());
					dado.setMatriz(new MatrizCurricular( Integer.parseInt(map)));
					dado.setDataCadastro(new Date());
					Map<String, Object> o = (Map<String, Object>) sumarioIndices.get(map).get("medias");
					dado.setIech( ((BigDecimal) o.get("IECH")).doubleValue() );
					dado.setIepl( ((BigDecimal) o.get("IEPL")).doubleValue() );
					dado.setRegistroCadastro(movCad.getRegistroEntrada());
					dados.add(dado);
				}
				
				dao.getHibernateTemplate().saveOrUpdateAll(dados);
				
			} finally {
				dao.close();
			}

		}
		
		if (SigaaListaComando.REMOVER_INDICES_ACADEMICOS.equals(mov.getCodMovimento())) {
			
			DadosIndiceAcademicoDao dao = getDAO(DadosIndiceAcademicoDao.class, mov);
			try {
				if ( dao.haIndiceCadastrado(dadosIndAca.getAnoReferencia()) )
					dao.inativar(dadosIndAca.getAnoReferencia(), movCad.getRegistroEntrada() );
			} finally {
				dao.close();
			}
		}
			
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		DadosIndiceAcademico dadosIndAca = movCad.getObjMovimentado();
	
		if (SigaaListaComando.CADASTRAR_INDICES_ACADEMICOS.equals(mov.getCodMovimento())) {
			DadosIndiceAcademicoDao dao = getDAO(DadosIndiceAcademicoDao.class, mov);
			try {
				if ( dao.haIndiceCadastrado(dadosIndAca.getAnoReferencia()) )
					throw new NegocioException("Indices já importados para o ano em questão.");
			} finally {
				dao.close();
			}
		}
		
	}
	
}