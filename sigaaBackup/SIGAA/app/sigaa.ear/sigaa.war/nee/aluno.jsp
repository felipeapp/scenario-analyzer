<ul>
	<li> Solicita��es NEE
		<ul>
			<li> 
				<h:commandLink value="Solicita��es Pendentes de Parecer" action="#{ solicitacaoApoioNee.listaSolicitacoesPendentesParecer }" onclick="setAba('alunos')"/>
				<span style="color: red; font-weight: bold">(${ fn:length(solicitacaoApoioNee.solicitacaoApoioPendentesParecer) })</span>
			</li>
			<li> <h:commandLink value="Alterar Parecer de NEE" action="#{ solicitacaoApoioNee.alterarParecerSolicitacao }" onclick="setAba('alunos')"/></li>
		</ul>
	</li>
	<li> Dados do Discente
		<ul>
			<li> <h:commandLink action="#{ historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observa��es do Discente" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ pessoaNecessidadeEspecial.preCadastrar}" value="Gerenciar NEE de Discente" onclick="setAba('alunos')"/> </li>
		</ul>
	</li>

	<li> Acompanhamento Acad�mico
		<ul>
			<li> <h:commandLink value="Visualizar Hist�rico" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink value="Emitir Atestado de Matr�cula" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink value="Visualizar Notas" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declara��o de V�nculo" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" value="�ndices Acad�micos do Aluno" onclick="setAba('alunos')"/> </li>
		</ul>
	</li>
</ul>				