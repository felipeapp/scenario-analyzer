<ul>
	<li>Aluno
		<ul>
		<li> <h:commandLink id="atualizarDadosDiscenteTecnicoCoord" action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('coordenacao')"/> </li>
		</ul>
	</li>
	<li>Aproveitamento de Disciplina
		<ul>
			<li><h:commandLink
				action="#{aproveitamento.iniciarAproveitamento}"
				value="Aproveitar Disciplina" onclick="setAba('coordenacao')"/></li>
			<li><h:commandLink
				action="#{aproveitamento.iniciarCancelamento}"
				value="Cancelar Aproveitamento" onclick="setAba('coordenacao')"/></li>
		</ul>
	</li>
	<li>Documentos
        	<ul>
        		<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('coordenacao')"/> </li>
                <li><h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('coordenacao')"/></li>
             </ul>
    </li>
	<li>Matr�cula
		<ul>
       		<li><h:commandLink action="#{analiseSolicitacaoMatricula.iniciar}" value="Analisar Solicita��es de Matr�cula" onclick="setAba('coordenacao')"/></li>
			<li><h:commandLink action="#{consultaSolicitacoes.iniciar}" value="Consultar Solicita��es de Matr�cula" onclick="setAba('coordenacao')"/> </li>
     	</ul>
	</li>
	<li>Movimenta��o de Aluno
		<ul>
	        <li><h:commandLink action="#{movimentacaoAluno.iniciarTrancamentoPrograma}" value="Cadastrar Trancamento" onclick="setAba('coordenacao')"/></li>
			<li><h:commandLink action="#{movimentacaoAluno.iniciarRetorno }" value="Retorno Manual de Discente" onclick="setAba('coordenacao')"/> </li>
<%--			<li><h:commandLink action="#{retornoTrancamento.iniciar }" value="Retornar Alunos de Trancamento" onclick="setAba('coordenacao')"/> </li>--%>
			<li><h:commandLink action="#{movimentacaoAluno.iniciarEstorno }" value="Estornar Afastamento" onclick="setAba('coordenacao')"/></li>
		</ul>
	</li>
<%--
 	<li>Orienta��o de Aluno
		<ul>
			<li><h:commandLink action="#{ orientacaoAcademica.iniciar }" value="Cadastrar Orienta��o Acad�mica" onclick="setAba('coordenacao')"/></li>
			<li><h:commandLink action="#{ orientacaoAcademica.gerenciarOrientacoes }" value="Gerenciar Orienta��es Acad�micas" onclick="setAba('coordenacao')"/></li>
	    </ul>
	</li>
--%>
</ul>