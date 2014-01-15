<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> Residências em Saúde </h2>
	
	<h:form>
		<input type="hidden" name="aba" id="aba" />
	
		<div id="operacoes-subsistema" class="reduzido">

			<div id="cadastros" class="aba">
				<ul>
					<li> Componentes Curriculares
						<ul>
							<li><h:commandLink value="Cadastrar Componente Curricular" action="#{componenteCurricular.preCadastrar}" onclick="setAba('cadastros')" /></li>
							<li><h:commandLink value="Buscar/Alterar Componente Curricular" action="#{componenteCurricular.listar}" onclick="setAba('cadastros')"/></li>
						</ul>
					</li>
					<li> Estruturas Curriculares
						<ul>
							<li>
								<h:commandLink value="Cadastrar Currículo" action="#{curriculo.preCadastrar}" onclick="setAba('cadastros')">
									<f:param value="R" name="nivel" />
								</h:commandLink>
							</li>
							<li>
								<h:commandLink value="Buscar/Alterar Currículo"  action="#{curriculo.preListar}" onclick="setAba('cadastros')">
									<f:param value="R" name="nivel" />
								</h:commandLink>
							</li>
						</ul>
					</li>
					<li> Turmas
						<ul>
							<li><h:commandLink value="Criar Turma" action="#{turmaResidenciaMedica.preCadastrar}" onclick="setAba('cadastros')" /></li>
							<li><h:commandLink value="Buscar/Alterar Turma" action="#{buscaTurmaBean.popularBuscaGeral}" onclick="setAba('cadastros')" /></li>
						</ul>
					</li>
				</ul>
			</div>
			
			<div id="alunos" class="aba">
				<ul>
				
					<li> Alunos
						<ul>
							<li> <h:commandLink action="#{discenteResidenciaMedica.iniciarCadastroDiscenteNovo}" value="Cadastrar Discente" onclick="setAba('alunos')" /> </li>
							<li> <h:commandLink action="#{discenteResidenciaMedica.atualizar}" value="Buscar/Alterar Discente" onclick="setAba('alunos')" /> </li>
							<li> <h:commandLink action="#{alteracaoDadosDiscente.iniciar}" value="Alterar Dados Pessoais de Discente" onclick="setAba('alunos')" /> </li>
							<li> <h:commandLink action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status do Discente"  onclick="setAba('alunos')" id="linkAlterarStatusDiscentes"/> </li>
							<li> <h:commandLink action="#{excluirDiscente.iniciar}" value="Excluir Aluno"  onclick="setAba('alunos')"/> </li>
						</ul>
					</li>
					
					<li> Histórico
						<ul>
							<li> <h:commandLink action="#{implantarHistorico.iniciar}" value="Implantar Histórico do Aluno" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink	action="#{historico.buscarDiscente}" value="Emitir Histórico" onclick="setAba('alunos')"/>  </li>
						</ul>
					</li>
					
					<li> Registro de Atividades
						<ul>
						<li> <h:commandLink	action="#{registroAtividade.iniciarMatricula }" value="Matricular" onclick="setAba('alunos')" /> </li>
						<li> <h:commandLink action="#{registroAtividade.iniciarConsolidacao }" value="Consolidar" onclick="setAba('alunos')"/> </li>
						<li> <h:commandLink action="#{registroAtividade.iniciarValidacao }" value="Validar" onclick="setAba('alunos')"/> </li>
						<li> <h:commandLink action="#{registroAtividade.iniciarExclusao }" value="Excluir" onclick="setAba('alunos')"/> </li>
						</ul>
					</li>
					
					<li> Matrículas
						<ul>
							<li> <h:commandLink id="matricular" action="#{matriculaGraduacao.iniciarMatriculasRegulares}" value="Matricular Aluno" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink id="matricularEmLote" action="#{matriculaResidenciaMedica.iniciarMatriculaEmLote}" value="Matricular Alunos em Lote" onclick="setAba('alunos')"/> </li>
						</ul>
					</li>
					
					<li> Outras Operações
						<ul>
							<li> <h:commandLink id="trancamentoPrograma" action="#{movimentacaoAluno.iniciarTrancamentoPrograma}" value="Trancar Programa" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink id="cancelarTrancamentoPrograma" action="#{movimentacaoAluno.iniciarCancelamentoTrancamento}" value="Cancelar Trancamento de Programa" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink id="cancelarPrograma" action="#{movimentacaoAluno.iniciarCancelamentoPrograma}" value="Cancelamento de Programa" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink id="concluirPrograma" action="#{movimentacaoAluno.iniciarConclusaoProgramaLato}" value="Conclusão de Programa" onclick="setAba('alunos')"/> </li>
						</ul>
					</li>
					
				</ul>
				
			</div>
			
			<div id="producoes" class="aba">
				<ul>
				
					<li> Carga Horária em Residência
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{residenciaMedica.preCadastrar}" onclick="setAba('producoes')"/></li>
							<li> <h:commandLink value="Consultar/Alterar" action="#{residenciaMedica.telaBusca}" onclick="setAba('producoes')"/></li>
						</ul>
					</li>
					
					<c:if test="${not acesso.coordenadorResidenciaMedica}">
						<li>Manutenção de Coordenadores
							<ul>
								<li><h:commandLink action="#{coordenacaoCurso.iniciar}" value="Identificar Coordenador" onclick="setAba('producoes')" id="identificacaoDeCoord"/></li>
								<li><a href="${ctx}/ensino/coordenacao_curso/lista.jsf" onclick="setAba('producoes')" id="SubstituirCoord">Substituir Coordenador </a></li>
								<li><h:commandLink action="#{coordenacaoCurso.listar}" value="Listar Coordenadores" onclick="setAba('producoes')" id="listarCoordenacoes"/></li>
							</ul>
						</li>
					</c:if>
				</ul>
			</div>
			
			<div id="relatorios" class="aba">
				<ul>
					<li> Geral
						<ul>
							<li> <h:commandLink value="Total de Alunos Cadastrados por Programa" action="#{relatorioAlunosCadastradosResidenciaMBean.iniciar}" onclick="setAba('relatorios')"/></li>
						</ul>
					</li>
				</ul>
			</div>			
			
		</div>
	</h:form>
	<c:set var="hideSubsistema" value="true" />
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>

<script>
	var Abas = {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	        abas.addTab('cadastros', "Cadastros");
	        abas.addTab('alunos', "Alunos");
	        abas.addTab('producoes', "Produção Intelectual");
	        abas.addTab('relatorios', "Relatórios");
	        <c:if test="${empty sessionScope.aba}">
	    		abas.activate('cadastros');
	   	 	</c:if>
	        <c:if test="${sessionScope.aba != null}">
		    	abas.activate('${sessionScope.aba}');
		    </c:if>
	    }
	};
	
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<c:remove var="aba" scope="session" />