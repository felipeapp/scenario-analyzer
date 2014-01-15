<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
form {
	display: inline; 
}
</style>
<f:view>
	<h2><ufrn:subSistema /> > Matricular Discente Usando Plano de Matrículas</h2>

		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>O discente foi matriculado com sucesso! Clique nos documentos abaixo para imprimir.</p> 
		</div>
		<br/>
		<table class="visualizacao" width="80%">
			<caption>Dados do Discente</caption>
			<tr>
				<th>CPF:</th>
				<td>
					<ufrn:format type="cpf" valor="${ matricularDiscentePlanoMatriculaMBean.obj.pessoa.cpf_cnpj }"></ufrn:format>
				</td>
			</tr>
			<tr>
				<th>Matrícula:</th>
				<td>
					${ matricularDiscentePlanoMatriculaMBean.obj.matricula }
				</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${ matricularDiscentePlanoMatriculaMBean.obj.discente.pessoa.nome }</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${ matricularDiscentePlanoMatriculaMBean.obj.matrizCurricular.descricao }</td>
			</tr>
			<c:if test="${ matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.id > 0 }">
				<%@include file="/ensino/plano_matricula_ingressantes/_quadro_horario.jsp" %>
			</c:if>
			<tfoot class="formulario">
				<tr>
					<td colspan="2">
						<c:if test="${ matricularDiscentePlanoMatriculaMBean.matriculado }">
							<h:form id="formComprovante">
								<h:commandButton value="Atestado de Matrícula" action="#{ matricularDiscentePlanoMatriculaMBean.atestadoMatricula }" 
									id="atestadoMatricula" onclick="$('formComprovante').target='_blank'" />
							</h:form>
						</c:if>
						<h:form id="form">
							<h:commandButton value="Nova Matrícula" action="#{ matricularDiscentePlanoMatriculaMBean.iniciar }" id="novoCadastro"/>
							<h:commandButton value="Cancelar" action="#{ matricularDiscentePlanoMatriculaMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
						</h:form>
					</td>
				</tr>
			</tfoot>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>