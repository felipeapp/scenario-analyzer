<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>
<link href="/sigaa/css/ensino/busca_turma.css" rel="stylesheet" type="text/css" />

<f:view>
<a4j:keepAlive beanName="turmaSerie"/>
	<ufrn:subSistema teste="portalDiscente">
		<%@include file="/portais/discente/medio/menu_discente_medio.jsp" %>
	</ufrn:subSistema>
<h2> <ufrn:subSistema /> &gt; Listar/Alterar Turma</h2>

<h:form id="form">
	<table class="formulario" style="width: 95%">
		<caption>Dados das Turmas por Série do Ensino Médio</caption>
		<h:inputHidden value="#{turmaSerie.obj.id}" />
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td><h:inputText value="#{turmaSerie.obj.ano}" size="5" maxlength="4" onkeyup="return formatarInteiro(this);" /></td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{turmaSerie.obj.serie.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{turmaSerie.carregarSeriesByCurso }" style="width: 75%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th>Série:</th>
				<td>
					<h:selectOneMenu value="#{ turmaSerie.obj.serie.id }" style="width: 75%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turmaSerie.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{turmaSerie.filtroInativos}" styleClass="noborder" /> 
				</th>
				<td> Listar Turmas Inativas.</td>
			</tr>
		</tbody>
		<tfoot>
		   	<tr>
				<td colspan="2">
					<h:commandButton value="Listar Turmas" action="#{turmaSerie.listarTurmasAnual}" id="listarTurmas" />
					<h:commandButton value="Cancelar" action="#{turmaSerie.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   	</tr>
		</tfoot>	
	</table>
	
	<%-- Tabela de Turmas --%>
	<c:if test="${not empty turmaSerie.turmaDisciplinas}">
		<div style="width: 90%; margin: auto; margin-top: 10px;">
			<center>
				<div class="infoAltRem">
					<c:if test="${turmaSerie.pemissaoMedio}"><h:graphicImage value="/img/table_go.png" style="overflow: visible; margin-right: 0px;" id="subTurma"/>: Gerenciar Turma</c:if>
					<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</div>
			</center>
			<c:set var="_curso"/>
			<c:forEach var="turma" items="#{turmaSerie.turmaDisciplinas}" varStatus="status">
				<c:set var="cursoAtual" value="${turma.serie.cursoMedio.id}"/>
				<c:if test="${_curso != cursoAtual}">
					<c:set var="_curso" value="${cursoAtual}"/>
					<br/>
					<table class="listagem" width="100%" style="margin-bottom: 0px; padding-bottom: 0px;">
						<caption style="color:black; background: #C8D5EC;">
							${turma.serie.cursoMedio.nome}
						</caption>	
					</table>
				</c:if>	
				<table class="listagem" style="margin: 7px 0 5px 0;" width="90%">
					<caption class="listagem">
						${turma.ano} - ${turma.serie.descricaoCompleta} (Turma ${turma.dependencia ?'Dependência':turma.nome} - ${turma.turno.descricao})  ${!turma.ativo? '[INATIVA]':'' } 
						<h:commandLink action="#{turmaSerie.atualizar}" id="gerenciarTurma" rendered="#{turmaSerie.pemissaoMedio}">
							<h:graphicImage value="/img/table_go.png" style="float: right; margin: 0 2px 5px 0;" title="Gerenciar Turma" />
							<f:param name="id" value="#{turma.id}"/>
							<f:param name="isDependencia" value="#{turma.dependencia}"/>
						</h:commandLink>
					</caption>
					<thead>
						<tr>
							<th width="20%">Disciplinas</th>
							<th width="30%">Docente(s)</th>
							<th width="15%">Situação</th>
							<th width="7%">Horário</th>
							<th width="7%">Local</th>
							<th width="10%" style="text-align: right;">Mat./Cap.</th>
							<th width="2%" align="center"></th>
						</tr>
					</thead>
					<c:if test="${not empty turma.disciplinas}">
						<c:forEach var="linha" items="#{turma.disciplinas}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
								<td>${linha.turma.disciplina.nome} </td>
								<td id="colDocente">${empty linha.turma.docentesNomesCh ? linha.turma.situacaoTurma.descricao : linha.turma.docentesNomesCh}</td>
								<td>${linha.turma.situacaoTurma.descricao} </td>
								<c:set var="posDescricaoHorario" value="${fn:indexOf(linha.turma.descricaoHorario,' ')}"/>
								<td id="colHorario">${linha.turma.descricaoHorario}</td>
								<td>${linha.turma.local}</td>
								<td style="text-align: right;"> ${linha.turma.qtdMatriculados}/${linha.turma.capacidadeAluno} alunos</td>
								<td width="3%" align="center"><img src="${ctx}/img/biblioteca/emprestimos_ativos.png" onclick="exibirOpcoes(${linha.turma.id});" style="cursor: pointer" title="Visualizar Menu"/></td>
							</tr>
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none" id="trOpcoes${linha.turma.id}">
								<td colspan="7">
					            <c:set var="bgCor" value="${ status.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8' }" />
					             
								<ul class="listaOpcoes">
								
									<li id="btnSelecionarTurma">
										<h:commandLink action="#{turmaMedio.listaAlunosMatriculados}" title="Listar Alunos" id="sltbtnSelecionarTurma">
											<f:param name="id" value="#{linha.turma.id}" />
											<f:param name="idTurmaSerie" value="#{turma.id}"/>
											Listar Alunos
										</h:commandLink>
									</li>
									<c:if test="${turmaSerie.pemissaoMedio}">
									<li id="Alterar">
										<h:commandLink styleClass="noborder" title="Alterar Disciplina" action="#{turmaMedio.alterarDisciplina}"  id="sltAlterar">
											<f:param name="id" value="#{linha.turma.id}" />
											<f:param name="idTurmaSerie" value="#{turma.id}"/>
											Alterar Disciplina
										</h:commandLink>
									</li>
									</c:if>
									<c:if test="${turmaSerie.pemissaoMedio}">
									<li id="btnfecharTurma">
										<h:commandLink styleClass="noborder" title="Consolidar Disciplina" action="#{consolidarDisciplinaMBean.selecionarDisciplina}" id="sltbtnfecharTurma">
											<f:param name="id" value="#{linha.id}" />
											<f:param name="pageBack" value="listarTurma" />
											Consolidar Disciplina
										</h:commandLink>
									</li>
									</c:if>
									<c:if test="${turmaSerie.coordenadorMedio || acesso.pedagogico}">
									<li id="btnNotasDiscente">
										<h:commandLink styleClass="noborder" title="Notas dos alunos" action="#{relatorioConsolidacao.notasDiscente}" id="sltbtnNotasDiscente">
											<f:param name="idTurma" value="#{linha.turma.id}" />
											<f:param name="turmasEAD" value="#{linha.turma.ead}" />
											Notas dos alunos
										</h:commandLink>
									</li>
									</c:if>
									<c:if test="${turmaSerie.pemissaoMedio and linha.turma.consolidada}">
									<li id="btnReabrir">
										<h:commandLink styleClass="noborder" title="Reabrir Turma" action="#{turmaMedio.reabrirTurma}" onclick="#{reabrir}">
											<f:param name="id" value="#{linha.turma.id}" />
											<f:param name="idTurmaSerie" value="#{turma.id}"/>
											Reabrir Turma
										</h:commandLink>
									</li>
									</c:if>
									<c:if test="${turmaSerie.pemissaoMedio}">
									<li id="btnRemover">
										<h:commandLink styleClass="noborder" title="Remover Disciplina" action="#{turmaSerie.removerDisciplina}" id="sltbtnRemover" onclick="return confirm('Deseja realmente remover a disciplina?')">
											<f:param name="id" value="#{linha.id}" />
											<f:param name="idTurmaSerie" value="#{turma.id}"/>
											Remover Disciplina
										</h:commandLink>
									</li>
									</c:if>
									<c:if test="${turmaSerie.pemissaoMedio || acesso.pedagogico}">
									<li id="btnGerarDiarioClasse">
										<h:commandLink styleClass="noborder" title="Diário de Classe" action="#{diarioClasseMedio.gerarDiarioClasse}" id="sltbtnGerarDiarioClasse">
											<f:param name="id" value="#{linha.turma.id}" />
											Diário de Classe
										</h:commandLink>									
									</li>
									</c:if>
									<c:if test="${turmaSerie.pemissaoMedio}">
									<li id="btnTurmaVirtual">
										<h:commandLink styleClass="noborder" title="Visualizar Turma Virtual" action="#{turmaVirtual.entrar}" id="sltbtnTurmaVirtual">
											<f:param name="idTurma" value="#{linha.turma.id}" />
											Visualizar Turma Virtual
										</h:commandLink>
									</li>
									</c:if>
									<li style="clear: both; float: none; background-image: none;"></li>
								</ul>
								
								</td>
							</tr>			
						</c:forEach>
					</c:if>
					<c:if test="${empty turma.disciplinas}">
						<tr><td colspan="7">Não há disciplinas vinculadas a esta turma.</td></tr>
					</c:if>
				</table>
			</c:forEach>
		</div>
	</c:if>
	<br/>
	<center>
		 <h:graphicImage url="/img/required.gif"/>
		 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	
</h:form>
</f:view>
<script type="text/javascript">
<!--
	function exibirPainel(id) {
		PainelDetalhar.show('/sigaa/geral/turma/painelOpcoes.jsf?id='+id, 'Opções', 300, 300);
	}
	
	function exibirOpcoes(idDiscente){
		var linha = 'trOpcoes'+ idDiscente;
		$(linha).toggle();
	}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>