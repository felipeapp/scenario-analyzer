<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="matriculaMedio"/>
<h2> <ufrn:subSistema /> &gt; Matricular Aluno em Dependência</h2>
<c:set value="#{matriculaMedio.obj.discenteMedio}" var="discente" />
<%@ include file="/medio/discente/info_discente.jsp"%>

<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption>Matricular discente de ensino médio</caption>
		<h:inputHidden value="#{matriculaMedio.obj.id}" />
		
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{matriculaMedio.obj.turmaSerie.serie.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{matriculaMedio.carregarSeriesByCurso }" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série:</th>
				<td>
					<h:selectOneMenu value="#{ matriculaMedio.obj.turmaSerie.serie.id }" style="width: 95%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ matriculaMedio.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td>
					<h:inputText value="#{matriculaMedio.ano}" size="4" maxlength="4" 
					onkeyup="return formatarInteiro(this);"/> 
				</td>
			</tr>										
			<tfoot>
		   	<tr>
				<td colspan="2">
					<h:commandButton value="Buscar Turmas" action="#{matriculaMedio.buscarTurmas}" id="buscar" />
					<h:commandButton value="<< Voltar" action="#{matriculaMedio.voltarBuscaDiscente}" id="voltar"/>
					<h:commandButton value="Cancelar" action="#{matriculaMedio.cancelar}" onclick="#{confirm}" id="cancelarBusca" />
				</td>
		   	</tr>
		</tfoot>
		
	</table>
	<br/>
	<c:if test="${not empty matriculaMedio.listaTurmaSeries}">	
	<table class="listagem" style="margin: 7px 0 5px 0;" width="100%">
	<caption>Listagem das Turmas</caption>
		<tr>
			<td>
				<c:forEach var="turma" items="#{matriculaMedio.listaTurmaSeries}" varStatus="status">
					<table class="subListagem" style="margin: 7px 0 5px 0;" width="100%">
						<caption style="text-align: center; background: #C8D5EC;">
							${turma.ano} - ${turma.serie.descricaoCompleta} (Turma ${turma.dependencia ?'Dependência':turma.nome}) ${!turma.ativo? '[INATIVA]':'' }
						</caption>
						<thead>
							<tr class="periodo">
								<th width="2%"></th>
								<th width="10%">Disciplinas</th>
								<th width="30%">Docente(s)</th>
								<th width="20%">Situação</th>
								<th width="7%">Horário</th>
								<th width="7%">Local</th>
								<th width="7%">Mat./Cap.</th>
							</tr>
						</thead>
						<c:if test="${not empty turma.disciplinas}">
							<c:forEach var="linha" items="#{turma.disciplinas}" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
									<td> 
										<input type="checkbox" name="disciplinas" value="${linha.id}" id="mat${linha.id}"> 
									</td>
									<td>${linha.turma.disciplina.nome} </td>
									<td id="colDocente">${empty linha.turma.docentesNomesCh ? linha.turma.situacaoTurma.descricao : linha.turma.docentesNomesCh}</td>
									<td>${linha.turma.situacaoTurma.descricao} </td>
									<c:set var="posDescricaoHorario" value="${fn:indexOf(linha.turma.descricaoHorario,' ')}"/>
									<td id="colHorario">${linha.turma.descricaoHorario}</td>
									<td>${linha.turma.local}</td>
									<td style="text-align: right;"> ${linha.turma.qtdMatriculados}/${linha.turma.capacidadeAluno} alunos</td>
								</tr>		
							</c:forEach>
						</c:if>
						<c:if test="${empty turma.disciplinas}">
							<tr><td colspan="7">Não há disciplinas vinculadas a esta turma.</td></tr>
						</c:if>
					</table>
				</c:forEach>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td style="text-align: center;">
					<c:if test="${not empty matriculaMedio.listaTurmaSeries}">
				   	<h:commandButton value="Matricular Aluno" action="#{matriculaMedio.matricularDependencias}" id="matricular" />
					<h:commandButton value="Cancelar" action="#{matriculaMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
					</c:if>
				</td>
			</tr>
		</tfoot>
	</table>	
	</c:if>
	<br/>
	<div class="obrigatorio" style="width: 90%"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>