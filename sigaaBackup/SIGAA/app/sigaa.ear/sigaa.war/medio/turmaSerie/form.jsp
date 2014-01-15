<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>
<link href="/sigaa/css/ensino/busca_turma.css" rel="stylesheet" type="text/css" />

<f:view>
<a4j:keepAlive beanName="turmaSerie"/>
<%@include file="/medio/turmaSerie/panel.jsp"%>
<h2> <ufrn:subSistema /> &gt; ${turmaSerie.labelCombo} Turma</h2>

<%-- Instruções para o discente --%>
	<div class="descricaoOperacao">
		<p> Caro(a) Usuário(a), </p> <br />

		<p>
			Selecione a(s) informação(s) que deseja para visualizar as turmas de ensino médio,
			nesta operação será possível criar uma nova turma baseada nas disciplinas inseridas 
			no currículo da série selecionada, no qual as disciplinas serão inseridas automaticamente
			na turma criada. 
		</p>
	</div>	

<h:form id="form">
	<table class="formulario" style="width: 95%">
		<caption>Dados das Turmas por Série do Ensino Médio</caption>
		<h:inputHidden value="#{turmaSerie.obj.id}" />
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td><h:inputText value="#{turmaSerie.obj.ano}" size="10" maxlength="4" onkeyup="return formatarInteiro(this);" /></td>
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
	
		<div style="width: 90%; margin: auto; margin-top: 10px;">
			<center>
				<div class="infoAltRem">
					<h:outputLink value="#_self" id="link" tabindex="1000">
						<h:graphicImage value="/img/adicionar.gif" styleClass="hidelink" id="imgFormula" />  Cadastrar Nova Turma
			        	<rich:componentControl for="panel" attachTo="link" operation="show" event="onclick" params="" />
					</h:outputLink>
					<h:graphicImage value="/img/table_go.png" style="overflow: visible; margin-right: -3px;" id="subTurma"/>: Gerenciar Turma
					<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</div>
			</center>
			<c:forEach var="turma" items="#{turmaSerie.turmaDisciplinas}" varStatus="status">
				<table class="listagem" style="margin: 7px 0 5px 0;" width="100%">
					<caption class="listagem">
						${turma.ano} - ${turma.serie.descricaoCompleta} (Turma ${turma.nome} - ${turma.turno.descricao})  ${!turma.ativo? '[INATIVA]':'' }
						<h:commandLink action="#{turmaSerie.atualizar}" id="gerenciarTurma">
							<h:graphicImage value="/img/table_go.png" style="float: right; margin: 0 2px 5px 0;" title="Gerenciar Turma" />
							<f:param name="id" value="#{turma.id}"/>
						</h:commandLink>
					</caption>
					<thead>
						<tr>
							<th width="10%">Disciplinas</th>
							<th width="30%">Docente(s)</th>
							<th width="20%">Situação</th>
							<th width="7%">Horário</th>
							<th width="7%">Local</th>
							<th width="7%">Mat./Cap.</th>
							<th width="5%" align="right"></th>
						</tr>
					</thead>
					<c:if test="${not empty turma.disciplinas}">
						<c:forEach var="linha" items="#{turma.disciplinas}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
								<td>${linha.turma.disciplina.nome} </td>
								<td id="colDocente">${empty linha.turma.docentesNomesCh ? linha.turma.situacaoTurma.descricao : linha.turma.docentesNomesCh}</td>
								<td>${linha.turma.situacaoTurma.descricao} </td>
								<c:set var="posDescricaoHorario" value="${fn:indexOf(linha.turma.descricaoHorario,' ')}"/>
								<td id="colHorario">${linha.turma.descricaoHorario}</td>
								<td>${linha.turma.local}</td>
								<td style="text-align: right;"> ${linha.turma.qtdMatriculados}/${linha.turma.capacidadeAluno} alunos</td>
								<td width="3%" align="right"><img src="${ctx}/img/biblioteca/emprestimos_ativos.png" onclick="exibirOpcoes(${linha.turma.id});" style="cursor: pointer" title="Visualizar Menu"/></td>
							</tr>
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none" id="trOpcoes${linha.turma.id}">
								<td colspan="9">
					            <c:set var="bgCor" value="${ status.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8' }" />
					             
								<ul class="listaOpcoes">
								
									<li id="btnSelecionarTurma">
										<h:commandLink action="#{turmaMedio.listaAlunosMatriculados}" title="Listar Alunos" id="sltbtnSelecionarTurma">
											<f:param name="id" value="#{linha.turma.id}" />
											<f:param name="idTurmaSerie" value="#{turma.id}"/>
											Listar Alunos
										</h:commandLink>
									</li>
									
									<li id="Alterar">
										<h:commandLink styleClass="noborder" title="Alterar Disciplina" action="#{turmaMedio.atualizar}"  id="sltAlterar">
											<f:param name="id" value="#{linha.turma.id}" />
											Alterar Disciplina
										</h:commandLink>
									</li>
									
									<li id="btnfecharTurma">
										<h:commandLink styleClass="noborder" title="Consolidar Disciplina" action="#{consolidarDisciplinaMBean.selecionarDisciplina}" id="sltbtnfecharTurma">
											<f:param name="id" value="#{linha.id}" />
											Consolidar Disciplina
										</h:commandLink>
									</li>
									
									<li id="btnRemover">
										<h:commandLink styleClass="noborder" title="Remover Disciplina" action="#{turmaSerie.removerDisciplina}" id="sltbtnRemover">
											<f:param name="id" value="#{linha.turma.id}" />
											Remover Disciplina
										</h:commandLink>
									</li>
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
	
	<br/>
	<div class="obrigatorio" style="width: 90%"> Campos de preenchimento obrigatório. </div>
	
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