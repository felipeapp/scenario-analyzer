<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Planos de Matrícula de Discentes Ingressantes</h2>
	<h:form id="form">
	
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Abaixo estão listados os Planos de Matrículas de Discentes Ingressantes cadastrados.</p>
	</div>
	<table class="formulario" width="85%">
		<caption>Filtrar os Planos de Matrícula em Turmas</caption>
		<tbody>
			<tr>
				<th width="10%">Ano-Período: </th>
				<td>
					<h:selectOneMenu value="#{planoMatriculaIngressantesMBean.anoPeriodo}" id="anoPeriodo">
						<f:selectItems value="#{planoMatriculaIngressantesMBean.anoPeriodoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
			<th class="${ planoMatriculaIngressantesMBean.portalCoordenadorGraduacao ? 'rotulo' : 'obrigatorio'}"> Curso:</th>
			<td>
				<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.curso.nome }" rendered="#{ planoMatriculaIngressantesMBean.portalCoordenadorGraduacao}" />
				<h:selectOneMenu value="#{ planoMatriculaIngressantesMBean.idCurso }" onchange="$('form:checkCurso').checked = true;" style="width: 95%;" 
						id="selectCurso" rendered="#{ not planoMatriculaIngressantesMBean.portalCoordenadorGraduacao}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{ cursoGrad.allCombo }" />
				</h:selectOneMenu>
			</td>
		</tr>
		</tbody>	
		<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Filtrar" action="#{planoMatriculaIngressantesMBean.filtrar}" id="filtrar"/>
				<h:commandButton value="Cancelar" action="#{planoMatriculaIngressantesMBean.cancelar}" id="cancelarFiltro" onclick="#{ confirm }"/>
			</td>
		</tr>
		</tfoot>
	</table>
	<br/>
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{planoMatriculaIngressantesMBean.preCadastrar}" value="Cadastrar"/>
		<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover
	</div>
		<table class="listagem">
			<caption>Planos de Matrícula em Turmas Cadastrados (${ fn:length(planoMatriculaIngressantesMBean.resultadosBusca) })</caption>
			<thead>
				<tr>
					<th>Ano-Período</th>
					<th>Código</th>
					<th>
						<h:outputText value="Matriz Curricular" rendered="#{ planoMatriculaIngressantesMBean.obj.graduacao}" />
						<h:outputText value="Curso" rendered="#{ not planoMatriculaIngressantesMBean.obj.graduacao}" />
					</th>
					<th>Turmas</th>
					<th style="text-align: right;">Capacidade</th>
					<th style="text-align: right;">Discentes Atendidos</th>
					<th width="2%"></th>
					<th width="2%"></th>
					<th width="2%"></th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${ not empty planoMatriculaIngressantesMBean.resultadosBusca }">
					<c:forEach items="#{ planoMatriculaIngressantesMBean.resultadosBusca }" var="item" varStatus="status">
					
					
						<c:if test="${nomeMatriz ne item.matrizCurricular.descricao}">
							<c:set var="nomeMatriz" value="${item.matrizCurricular.descricao}"/>
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td colspan="9" class="subFormulario">${nomeMatriz}</td>
							</tr>
						</c:if>
						
					
					
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${ item.ano }.${ item.periodo }</td>
							<td>${ item.descricao }</td>
							<td>
								<h:outputText value="#{ item.matrizCurricular.descricao }" rendered="#{planoMatriculaIngressantesMBean.obj.graduacao}" />
								<h:outputText value="#{ item.curso.nome }" rendered="#{ not planoMatriculaIngressantesMBean.obj.graduacao}" />
							</td>
							<td>
								<c:forEach items="#{ item.turmas }" varStatus="status2" var="turma">
									<c:if test="${ status2.index > 0 }">, </c:if>
									${ turma.descricaoResumida }
								</c:forEach>
							</td>
							<td style="text-align: right;">${ item.capacidade }</td>
							<td style="text-align: right;">${ item.qtdDiscentesAtivos }</td>
							<td>
								<h:commandLink title="Visualizar" style="border: 0;" action="#{planoMatriculaIngressantesMBean.view}">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/view.gif" alt="Visualizar" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Alterar" style="border: 0;" action="#{planoMatriculaIngressantesMBean.atualizar}" >
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Remover" style="border: 0;" action="#{planoMatriculaIngressantesMBean.remover}" 
									onclick="#{ confirmDelete }">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				<c:if test="${ empty planoMatriculaIngressantesMBean.all }">
					<tr>
						<td colspan="9" style="text-align: center">Não há planos de matrícula em lotes cadastrados.</td>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="9" style="text-align: center">
						<h:commandButton value="Cancelar" action="#{planoMatriculaIngressantesMBean.cancelar}" id="btnListar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
