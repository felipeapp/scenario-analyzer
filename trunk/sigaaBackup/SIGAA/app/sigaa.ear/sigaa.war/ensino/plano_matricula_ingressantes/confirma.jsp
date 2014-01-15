<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Planos de Matrícula de Discentes Ingressantes</h2>
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Confirme os dados abaixo antes de concluir o cadastro do Plano de Matrícula de Discentes Ingressantes.</p>
	</div>
	<br/>
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Confirme os Dados do Plano de Matrícula</caption>
			<tbody>
				<tr>
					<th class="rotulo">Ano-Período:</th>
					<td>${ planoMatriculaIngressantesMBean.obj.ano }.${ planoMatriculaIngressantesMBean.obj.periodo }</td>
				</tr>
				<tr>
					<th class="rotulo">Código:</th>
					<td>
						<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.descricao }" rendered="#{ not empty planoMatriculaIngressantesMBean.obj.descricao }"/>
						<h:outputText value="Será atribuído automaticamente" rendered="#{ empty planoMatriculaIngressantesMBean.obj.descricao }"/>
					 </td>
				</tr>
				<tr>
					<th class="rotulo">Curso:</th>
					<td>
					 	<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.curso.nome }" />
					</td>
				</tr>
				<c:if test="${ planoMatriculaIngressantesMBean.obj.graduacao }">
					<tr>
						<th class="rotulo">Matriz Curricular:</th>
						<td>
							<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.matrizCurricular.descricao }"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="rotulo">Capacidade:</th>
					<td>
						<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.capacidade }" />
					</td>
				</tr>
				<%@include file="/ensino/plano_matricula_ingressantes/_quadro_horario.jsp" %>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{planoMatriculaIngressantesMBean.confirmButton }" action="#{planoMatriculaIngressantesMBean.cadastrar}" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{planoMatriculaIngressantesMBean.formDadosGerais}" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{planoMatriculaIngressantesMBean.listar}" id="cancelar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
