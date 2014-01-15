/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 15, 2007
 *
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.MudancaPoloAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador utilizado para alteração de pólo
 * 
 * @author Victor Hugo
 *
 */
public class ProcessadorAlterarPolo extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {

		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		GenericDAO dao = getGenericDAO(mov);

		if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_POLO_DISCENTE) ){

			DiscenteGraduacao discente = (DiscenteGraduacao) mov.getObjMovimentado();

			DiscenteGraduacao discenteAntigo = dao.findByPrimaryKey(discente.getId() , DiscenteGraduacao.class);

			/* registrando a mudança de polo do discente */
			MudancaPoloAluno mudanca = new MudancaPoloAluno();
			mudanca.setDiscenteGraduacao(discente);
			mudanca.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
			mudanca.setData( new Date() );
			if( discenteAntigo.isDiscenteEad() ){
				mudanca.setPoloAntigo(discenteAntigo.getPolo());
				mudanca.setPoloNovo( discente.getPolo() );
			} else if( discenteAntigo.getCurso().isProbasica() ){
				mudanca.setCursoAntigo(discenteAntigo.getCurso());
				mudanca.setCursoNovo( discente.getCurso() );
			}

			dao.create(mudanca);

			/* alterando o polo do discente*/
			if( discente.isDiscenteEad() ){
				dao.updateField(DiscenteGraduacao.class, discente.getId(), "polo", discente.getPolo().getId());
			} else if( discenteAntigo.getCurso().isProbasica() ){
				dao.updateField(Discente.class, discente.getId(), "curso", discente.getCurso().getId());
			}

		}


		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_POLO_DISCENTE) ){
			checkRole(SigaaPapeis.DAE, mov);
		}

	}

}
