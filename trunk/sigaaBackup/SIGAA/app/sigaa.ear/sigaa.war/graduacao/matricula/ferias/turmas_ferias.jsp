<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<style>

table.listagem tr.disciplina td{
	font-weight: normal;
}

tr.disciplina td {
	background: none;
}

</style>


<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2> <ufrn:subSistema /> &gt; Matrícula em Turma de Férias </h2>
	<c:set value="#{confirmacaoMatriculaFeriasBean.obj.discente}" var="discente"></c:set>
	<%@ include file="/graduacao/info_discente.jsp"%>

	<h:form>
	<input type="hidden" value="${confirmacaoMatriculaFeriasBean.obj.discente.curriculo.id}" name="id" />

	<%-- Instruções --%>
	<div class="descricaoOperacao" style="width: 80%;">
		<p> Caro Aluno,
		</p>
		<p>
			Abaixo encontra-se as turmas de férias do período ${confirmacaoMatriculaFeriasBean.cal.anoPeriodoFeriasVigente}, selecione a turma que
			deseja se matricular. <br/>
		</p>
		<br />
		<p>
			Dúvidas sobre as disciplinas do seu currículo?
				<h:commandLink action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank" />
			para ver os detalhes de sua estrutura curricular.
		</p>
	</div>
	</h:form>
	
	<h:form>
	<%-- Legenda --%>
	<div class="infoAltRem">
		<h4> Legenda</h4>
		<img src="/sigaa/img/seta.gif">: Solicitar Matrícula
		<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
		<c:if test="${matriculaGraduacao.discente.graduacao}">
			<img src="/sigaa/img/graduacao/matriculas/matricula_tem_reservas.png" alt="" class="noborder" />: Turma possui reservas para seu curso
		</c:if>
	</div>

		<table class="listagem" id="lista-turmas-curriculo" >
			<caption>Turmas de Férias Abertas para período ${confirmacaoMatriculaFeriasBean.cal.anoPeriodoFeriasVigente} (${ fn:length(confirmacaoMatriculaFeriasBean.turmasFerias) })</caption>

			<thead>
			<tr>
				<th></th>
				<th> Turma </th>
				<th> Docente(s) </th>
				<th> Situação </th>
				<th> Horário </th>
				<th> Local </th>
				<th width="5%"></th>
			</tr>
			</thead>
			
			<tbody>

			
			<c:set var="semestreAtual" value="0" />
			<c:set var="unidadeAtual" value="0" />
			<h:outputText value="#{util.create}"></h:outputText>
			<c:forEach items="#{confirmacaoMatriculaFeriasBean.turmasFerias}" var="_turma" varStatus="status">

				<%-- Componente Curricular --%>
				<c:if test="${ unidadeAtual != _turma.disciplina.unidade.id}">
					<c:set var="unidadeAtual" value="${_turma.disciplina.unidade.id}" />
					<tr class="periodo" >
						<td colspan="7" style="font-size: x-small">
							${_turma.disciplina.unidade.sigla} - ${_turma.disciplina.unidade.nome}
						</td>
					</tr>
				</c:if>
				
				
					
				<c:set value="${status.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8'}" var="stLinha" />
				<c:if test="${not empty confirmacaoMatriculaFeriasBean.confirmacaoAnterior.turma  and confirmacaoMatriculaFeriasBean.confirmacaoAnterior.turma.id == _turma.id}">
					<c:set value="#FEEAC5" var="stLinha" />
				</c:if>
				<tr class="disciplina" style=" background-color: ${stLinha}" >
					
					<td>
					<a href="javascript:void(0);" onclick="PainelTurma.show(${_turma.id})" title="Ver detalhes da turma">
						<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt=""
							class="noborder" />
					</a>
					</td>
					
					<td style="font-size: x-small; background-color: ${cor} }">
						<a href="javascript:void(0);" style="${sugestao.podeMatricular?'':'color: #666'}"
							onclick="PainelComponente.show(${_turma.disciplina.id}, '#nivel_${sugestao.nivel}')" title="Ver Detalhes do Componente Curricular">
						${_turma.descricaoSemDocente}
						<c:if test="${not empty confirmacaoMatriculaFeriasBean.confirmacaoAnterior.turma  and confirmacaoMatriculaFeriasBean.confirmacaoAnterior.turma.id == _turma.id}">
							(Matrícula solicitada)
						</c:if>
						</a>
					</td>
					<td style="font-size: xx-small; text-align: left; " >
						${_turma.docentesNomesCh}
					</td>
					<td style="font-size: x-small; text-align: left; " >${_turma.situacaoTurma.descricao}</td>
					<td style="font-size: x-small; text-align: left; " >${_turma.descricaoHorario}</td>
					<td style="font-size: x-small; text-align: left; " >${_turma.local}</td>
					<td style="text-align: center;">
						<h:commandLink action="#{confirmacaoMatriculaFeriasBean.iniciarConfirmacao}" style="border: 0;">
							<f:param name="id" value="#{_turma.id}" />
							<h:graphicImage url="/img/seta.gif" alt="Solicitar Matrícula" title="Solicitar Matrícula"/>
						</h:commandLink>
					</td>
					
				</tr>

				<%-- Turma --%>
				<c:set value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" var="stLinha" />
			</c:forEach>
			
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<h:commandButton value="Cancelar" action="#{confirmacaoMatriculaFeriasBean.cancelar}" id="cancel" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
</f:view>
<script type="text/javascript">
<!--
remarcarTurmasSubmetidas();

//-->
</script>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script>
	document.location = '#';
</script>