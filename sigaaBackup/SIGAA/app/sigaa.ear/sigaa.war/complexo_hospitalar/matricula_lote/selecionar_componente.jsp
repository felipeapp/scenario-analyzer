<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Matricular Alunos em Lote </h2>
<br/>

<f:view>
	<c:set value="outras_turmas" var="pagina"></c:set>
	
	<div class="descricaoOperacao">
		Utilize o formulário abaixo para buscar por turmas abertas. Da lista de Resultados da Busca, 
		selecione uma turma da lista através do botão <b>Selecionar Turmas</b>, localizado ao lado 
		direito de cada turma para prosseguir.
	</div>

	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Buscar Turmas Abertas</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkCodigo" value="#{matriculaResidenciaMedica.boolDadosBusca[0]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkCodigo">Código do Componente:</label></td>
					<td><h:inputText size="10" maxlength="9" value="#{matriculaResidenciaMedica.dadosBuscaTurma.disciplina.codigo }"
						onchange="marcaCheckBox('form:checkCodigo')" onkeyup="CAPS(this)" id="txtCodigo"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNome" value="#{matriculaResidenciaMedica.boolDadosBusca[1]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNome">Nome do Componente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaResidenciaMedica.dadosBuscaTurma.disciplina.nome }"
						onchange="marcaCheckBox('form:checkNome')" onkeyup="CAPS(this)" id="txtNome"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkHorario" value="#{matriculaResidenciaMedica.boolDadosBusca[2]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkHorario">Horário:</label></td>
					<td><h:inputText size="10" maxlength="20" value="#{matriculaResidenciaMedica.dadosBuscaTurma.descricaoHorario}"
						onchange="marcaCheckBox('form:checkHorario')" onkeyup="CAPS(this)" id="txtHorario"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNomeDocente" value="#{matriculaResidenciaMedica.boolDadosBusca[3]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNomeDocente">Nome do Docente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaResidenciaMedica.dadosBuscaTurma.nomesDocentes}"
						onchange="marcaCheckBox('form:checkNomeDocente')" onkeyup="CAPS(this)" id="txtNomeDocente"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaResidenciaMedica.boolDadosBusca[4]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkUnidade">Unidade Responsável:</label></td>
					<td><h:selectOneMenu style="width: 400px"
						value="#{matriculaResidenciaMedica.dadosBuscaTurma.disciplina.unidade.id}"
						onchange="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{matriculaResidenciaMedica.buscarTurmas}" id="buscar"/>
						<h:commandButton value="Cancelar" action="#{matriculaResidenciaMedica.cancelar}" id="cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<c:set value="#{matriculaResidenciaMedica.resultadoTurmasBuscadas }" var="resultadoTurmasBuscadas" />
		
		<c:if test="${empty resultadoTurmasBuscadas}">
			<br>
			<center style="font-style: italic;">
				Informe critérios para refinar a busca de turmas.
			</center>
		</c:if>
		<c:if test="${not empty resultadoTurmasBuscadas}">
			<br>
			<div class="infoAltRem">
				<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
				<img src="/sigaa/img/seta.gif">: Selecionar Turma
			</div>

			<%-- TURMAS ENCONTRADAS --%>
			<table class="listagem" id="lista-turmas-extra">
			<caption>Turmas Abertas Encontradas
			</caption>

			<thead>
			<tr>
				<th> </th>
				<th> Turma </th>
				<th> Docente(s) </th>
				<th> Tipo </th>
				<th> Horário </th>
				<th> Local </th>
				<th> Capacidade </th>
				<th></th>
			</tr>
			</thead>
			
			<tbody>
			<c:if test="${not empty resultadoTurmasBuscadas}">
				<c:set var="disciplinaAtual" value="0" />
				<c:forEach items="#{resultadoTurmasBuscadas}" var="turma" varStatus="s">
	
					<%-- Componente Curricular --%>
					<c:if test="${ disciplinaAtual != turma.disciplina.id}">
						<c:set var="disciplinaAtual" value="${turma.disciplina.id}" />
						<tr class="disciplina" >
						<td colspan="9" style="font-variant: small-caps;">
							<a href="javascript:void(0);" onclick="PainelComponente.show(${turma.disciplina.id});"
								title="Ver Detalhes do Componente Curricular">
							${turma.disciplina.codigo} - ${turma.disciplina.detalhes.nome}
							</a>
						</td></tr>
					</c:if>
	
					<c:set value="turma_${turma.id}CHK" var="idCheckbox"></c:set>
					<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small" id="turma_${turma.id}TR">
						<td width="2%">
							<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id});" title="Ver Detalhes dessa turma">
								<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="Ver detalhes da turma" class="noborder" />
							</a>
						</td>
						<td width="8%">Turma ${turma.codigo}</td>
						<td>${turma.docentesNomes}</td>
						<td width="10%">${turma.tipoString}</td>
						<td width="10%">${turma.descricaoHorario}</td>
						<td width="10%">${turma.local}</td>
						<td width="10%">${turma.capacidadeAluno} alunos</td>
						<td>
							<h:commandLink action="#{matriculaResidenciaMedica.selecionarTurma}" title="Selecionar Turma">
								<h:graphicImage url="/img/seta.gif" />
								<f:param name="turmaSelecionada" value="#{turma.id }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</c:if>
			</tbody>
		</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
