
<ul>
	<li> Matrículas
		<ul>
		<li> <h:commandLink id="alteracaoStatusMatriculainiciar" action="#{alteracaoStatusMatricula.iniciar}" value="Alterar Status de Matrículas em Turmas e Aproveitamentos" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="alteracaoStatusMatriculaPorTurma" action="#{alteracaoStatusMatricula.iniciarAlterarSituacaoTurma}" value="Alterar Status de Matrícula por Turma" onclick="setAba('dae-programa')" /> </li>		
		<li> <h:commandLink id="alteracaoStatusMatriculainiciarTrancamentoMatricula" action="#{alteracaoStatusMatricula.iniciarTrancamentoMatricula}" value="Trancar Matrículas em Turmas" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="matriculaGraduacaoiniciarMatriculasRegulares" action="#{matriculaGraduacao.iniciarMatriculasRegulares}" value="Matricular Aluno" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="matriculaGraduacaoiniciarEspecial" action="#{matriculaGraduacao.iniciarEspecial}"	value="Matricular Aluno Especial" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="matriculaGraduacaoiniciarCompulsoria" action="#{matriculaGraduacao.iniciarCompulsoria}" value="Matrícula Compulsória" onclick="setAba('dae-programa')"/> </li>
 		<li> <h:commandLink id="matriculaGraduacaoiniciarForaPrazo" action="#{matriculaGraduacao.iniciarForaPrazo}" value="Matricular Fora do Prazo" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="matriculaGraduacaoiniciarMatriculaFerias" action="#{matriculaGraduacao.iniciarMatriculaFerias}" value="Matricular Aluno Em Turma de Férias" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="planoMatriculaBeanbuscarDiscente" action="#{planoMatriculaBean.buscarDiscente}" value="Consultar Planos de Matrícula" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="consultaSolicitacoesiniciar" action="#{consultaSolicitacoes.iniciar}" value="Consultar Solicitações de Matrículas" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="listaIndeferimentosiniciar" action="#{listaIndeferimentos.iniciar}" value="Consultar Indeferimentos" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="analiseSolicitacaoMatriculainiciarAlunoEspecial" action="#{analiseSolicitacaoMatricula.iniciarAlunoEspecial}"	value="Analisar Solicitações de Matrícula de Aluno Especial" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="trabalhoFinalCurso" action="#{trabalhoFimCurso.listar}"	value="Trabalho Final de Curso" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="validacaoVinculoIngressante" action="#{validacaoVinculo.iniciar}" value="Validação de Vínculo de Ingressante" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="matricularDiscentePlanoMatricula" action="#{matricularDiscentePlanoMatriculaMBean.iniciar}" value="Matricular Discente em Plano de Matrícula" onclick="setAba('dae-programa')"/> </li>
		</ul>
	</li>

	<li> Programa
		<ul>
		
		<li> <h:commandLink id="trancamentoProgramainiciarSubmeterTrancamento" action="#{trancamentoPrograma.iniciarSubmeterTrancamento}" value="Analisar Solicitações de Trancamento de Programa" onclick="setAba('dae-programa')" />	</li>
		<li> <h:commandLink id="movimentacaoAlunoiniciarTrancamentoPrograma" action="#{movimentacaoAluno.iniciarTrancamentoPrograma}" value="Trancar Programa" onclick="setAba('dae-programa')" />	</li>
		<li> <h:commandLink id="movimentacaoAlunoiniciarRetorno" action="#{movimentacaoAluno.iniciarRetorno}" value="Retorno Manual de Discente" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="movimentacaoAlunoiniciarCancelamentoTrancamento" action="#{movimentacaoAluno.iniciarCancelamentoTrancamento}" value="Cancelar Trancamentos Futuros" onclick="setAba('dae-programa')" /> </li>
		
		<li> <h:commandLink id="movimentacaoAlunoiniciarCancelamentoPrograma" action="#{movimentacaoAluno.iniciarCancelamentoPrograma}" value="Cancelar Programa" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="movimentacaoAlunoiniciarConclusaoPrograma" action="#{movimentacaoAluno.iniciarConclusaoPrograma}" value="Concluir Programa" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="colacaoColetivainiciarColacaoColetiva" action="#{colacaoColetiva.iniciarColacaoColetiva}" value="Conclusão Coletiva de Programa" onclick="setAba('dae-programa')" /> </li>

		<li> <h:commandLink id="movimentacaoAlunoiniciarEstorno" action="#{movimentacaoAluno.iniciarEstorno}" value="Estornar Operação" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="colacaoColetivainiciarEstornoColetivo" action="#{colacaoColetiva.iniciarEstornoColetivo}" value="Estornar Conclusão Coletiva" onclick="setAba('dae-programa')" /> </li>
		
		<li> <h:commandLink id="cancelamentoIniciar" action="#{jubilamentoMBean.iniciar}" value="Processar Cancelamento de Aluno" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="retornoTrancamentoiniciar" action="#{retornoTrancamento.iniciar}" value="Retornar Alunos de Trancamento" onclick="setAba('dae-programa')"/> </li>
		<%-- 
		<li> <h:commandLink id="movimentacaoAlunoiniciarRetorno" action="#{movimentacaoAluno.iniciarRetorno}" value="Retornar Aluno Trancado" onclick="setAba('dae-programa')"/> </li>
		
		<li> <h:commandLink id="requerimentotelaBusca" action="#{requerimento.telaBusca}" value="Buscar Requerimento de Trancamento" onclick="setAba('dae-programa')"/> </li>
		--%>
		<li> <h:commandLink id="iniciarAlteracaoDataColacao" action="#{colacaoColetiva.iniciarAlteracaoDataColacao}" value="Alterar Data de Colação Coletiva" onclick="setAba('dae-programa')" /> </li>
		</ul>
	</li>
	
	<li> Turmas
		<ul>
		<li> <h:commandLink id="turmaGraduacaoBeanpreCadastrar" action="#{turmaGraduacaoBean.preCadastrar}" value="Criar Turma" onclick="setAba('dae-programa')" />	</li>
		<li> <h:commandLink id="turmaGraduacaoBean_iniciarProbasica" action="#{turmaGraduacaoBean.iniciarProbasica}" value="Criar Turma PROBÁSICA" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="buscaTurmaBeanpopularBuscaGeral" action="#{buscaTurmaBean.popularBuscaGeral}" value="Consultar, Alterar ou Remover" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="converterTurmaRegularIndividualMBean_iniciarConversaoTurma" action="#{converterTurmaRegularIndividualMBean.iniciarConversaoTurma}" value="Converter Turma Regular em Ensino Individual" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="planoMatriculaIngressantesMBean_listar" action="#{planoMatriculaIngressantesMBean.listar}" value="Planos de Matrículas de Discentes Ingressantes" onclick="setAba('dae-programa')"/> </li>
		</ul>
	</li>
	<li> Transferência Voluntária
		<ul>
		<li><h:commandLink value="Cadastrar Novo Processo Seletivo" action="#{processoSeletivo.preCadastrar}"  onclick="setAba('dae-programa')"/></li>
		<li><h:commandLink value="Gerenciar Processos Seletivos" action="#{processoSeletivo.listar}" onclick="setAba('dae-programa')"/></li>
		<li><h:commandLink value="Questionários para Processos Seletivos" action="#{questionarioBean.gerenciarProcessosSeletivos}" onclick="setAba('dae-programa')" /></li>
		</ul>
	</li>

	<li> Registro de Atividades Acadêmicas Específicas
		<ul>
		<li> <h:commandLink id="registroAtividadeiniciarMatricula" action="#{registroAtividade.iniciarMatricula}" value="Matricular" onclick="setAba('dae-programa')" /> </li>
		<li> <h:commandLink id="registroAtividadeiniciarConsolidacao" action="#{registroAtividade.iniciarConsolidacao}" value="Consolidar" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="registroAtividadeiniciarValidacao" action="#{registroAtividade.iniciarValidacao}" value="Validar" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="registroAtividadeiniciarExclusao" action="#{registroAtividade.iniciarExclusao}" value="Excluir" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink id="registroAtividadeiniciarBusca" action="#{ orientacaoAtividade.iniciarBusca }" value="Buscar" onclick="setAba('dae-programa')"> <f:param name="regAtividadesEspecificas" value="true"/> </h:commandLink> </li>
		<li> <h:commandLink action="#{registroAtividade.iniciarAlterarTrabalhoEstagio }" value="Alterar Atividades de TCC/Estágio" onclick="setAba('dae-programa')" id="linkParaIniciarAlteracaoPeriodo"/> </li>
			
		<h:outputText value="#{registroAtividade.create}" />
		<c:if test="${registroAtividade.permissaoMatriculaCompulsoria}">
		<li> Registros Compulsórios
			<ul>
			<li>
				<h:commandLink id="registroAtividadeiniciarMatricula2" action="#{registroAtividade.iniciarMatricula}" value="Matricular compulsoriamente" onclick="setAba('dae-programa')">
					<f:param name="compulsoria" value="true"/>
				</h:commandLink>
			</li>
			<li>
				<h:commandLink id="registroAtividadeiniciarValidacao2" action="#{registroAtividade.iniciarValidacao}" value="Validar compulsoriamente" onclick="setAba('dae-programa')">
					<f:param name="compulsoria" value="true"/>
				</h:commandLink>
			</li>
			</ul>
		</li>
		</c:if>
		</ul>
	</li>
	<li> Prorrogação de Prazo
		<ul>
			<li> <h:commandLink id="prorrogacaobuscarDiscente" action="#{prorrogacao.buscarDiscente}" value="Prorrogar Prazo de Conclusão" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink id="prorrogacaobuscarDiscenteAnteciparPrazo" action="#{prorrogacao.buscarDiscenteAnteciparPrazo}" value="Antecipar Prazo de Conclusão" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink id="prorrogacaobuscarDiscenteCancelarProrrogacao" action="#{prorrogacao.buscarDiscenteCancelarProrrogacao}" value="Cancelar Prorrogação de Prazo de Conclusão" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink action="#{discenteGraduacao.iniciarAcrescimoPerfilInicial}" value="Alterar Perfil Inicial do Aluno"  onclick="setAba('discente')"/> </li>
		</ul>
	</li>

	<li> Mobilidade Estudantil
		<ul>
			<li> <h:commandLink id="mobilidadeEstudantiliniciar" action="#{mobilidadeEstudantil.iniciar}" value="Cadastrar" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink id="mobilidadeEstudantiliniciarAteracao" action="#{mobilidadeEstudantil.iniciarAteracao}" value="Listar/Alterar" onclick="setAba('discente')"/> </li>
		</ul>
	</li>
	
	<li> Região de Campus para Matrícula
		<ul>
			<li> <h:commandLink value="Cadastrar" action="#{regiaoMatriculaBean.preCadastrar}" onclick="setAba('cdp-cadastros')" id="botaoCadastrarRegiaoMatricula"/> </li>
			<li> <h:commandLink value="Listar/Alterar" action="#{regiaoMatriculaBean.listar}" onclick="setAba('cdp-cadastros')" id="botaoListarRegiaoMatricula"/> </li>
		</ul>
	</li>
</ul>