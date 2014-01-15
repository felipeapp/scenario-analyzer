<ul>
	<li> Solicitações NEE
		<ul>
			<li> 
				<h:commandLink value="Solicitações Pendentes de Parecer" action="#{ solicitacaoApoioNee.listaSolicitacoesPendentesParecer }" onclick="setAba('alunos')"/>
				<span style="color: red; font-weight: bold">(${ fn:length(solicitacaoApoioNee.solicitacaoApoioPendentesParecer) })</span>
			</li>
			<li> <h:commandLink value="Alterar Parecer de NEE" action="#{ solicitacaoApoioNee.alterarParecerSolicitacao }" onclick="setAba('alunos')"/></li>
		</ul>
	</li>
	<li> Dados do Discente
		<ul>
			<li> <h:commandLink action="#{ historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observações do Discente" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ pessoaNecessidadeEspecial.preCadastrar}" value="Gerenciar NEE de Discente" onclick="setAba('alunos')"/> </li>
		</ul>
	</li>

	<li> Acompanhamento Acadêmico
		<ul>
			<li> <h:commandLink value="Visualizar Histórico" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink value="Emitir Atestado de Matrícula" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink value="Visualizar Notas" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declaração de Vínculo" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" value="Índices Acadêmicos do Aluno" onclick="setAba('alunos')"/> </li>
		</ul>
	</li>
</ul>				