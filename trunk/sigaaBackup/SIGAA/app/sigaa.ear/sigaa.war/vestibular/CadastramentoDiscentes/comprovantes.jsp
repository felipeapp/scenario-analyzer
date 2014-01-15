<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
form {
	display: inline; 
}
</style>
<f:view>
	<h2><ufrn:subSistema /> > Cadastramento de Discentes Convocados</h2>

		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>O discente foi cadastrado com sucesso! Clique nos documentos abaixo para imprimir.</p> 
		</div>
		<br/>
		<table class="visualizacao" width="80%">
			<caption>Dados do Discente</caption>
			<tr>
				<th>Processo Seletivo:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.processoSeletivo.nome }</td>
			</tr>
			<tr>
				<th>Convocação:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.descricao }</td>
			</tr>
			<tr>
				<th>Status do Candidato:</th>
				<td>
					${ cadastramentoDiscenteConvocadoMBean.obj.discente.statusString }
					<h:outputText value="(candidato dentro do número de vagas)" rendered="#{ cadastramentoDiscenteConvocadoMBean.obj.dentroNumeroVagas }" />
				</td>
			</tr>
			<c:if test="${ !cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro }">
				<tr>
					<th>Matrícula:</th>
					<td>
						${ cadastramentoDiscenteConvocadoMBean.obj.discente.matricula }
					</td>
				</tr>
			</c:if>
			<tr>
				<th>CPF:</th>
				<td>
					<ufrn:format type="cpf" valor="${ cadastramentoDiscenteConvocadoMBean.obj.inscricaoVestibular.pessoa.cpf_cnpj }"></ufrn:format>
				</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.inscricaoVestibular.pessoa.nome }</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.matrizCurricular.descricao }</td>
			</tr>
			<tr>
				<th>Status:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.discente.discente.statusString }</td>
			</tr>
			<tr>
				<th>Possui Matrícula em Componentes Curriculares:</th>
				<td><ufrn:format type="simnao" valor="${ cadastramentoDiscenteConvocadoMBean.matriculado  }" /> </td>
			</tr>
			<c:if test="${ cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id > 0 }">
				<%@include file="/ensino/plano_matricula_ingressantes/_quadro_horario.jsp" %>
			</c:if>
			<tfoot class="formulario">
				<tr>
					<td colspan="2">
						<c:if test="${ cadastramentoDiscenteConvocadoMBean.matriculado }">
							<h:form id="formComprovante">
								<h:commandButton value="Atestado de Matrícula" action="#{ cadastramentoDiscenteConvocadoMBean.atestadoMatricula }" 
									id="atestadoMatricula" onclick="$('formComprovante').target='_blank'" />
							</h:form>
						</c:if>
						<h:form id="form">
							<h:commandButton value="Comprovantes do Cadastramento" action="#{ cadastramentoDiscenteConvocadoMBean.comprovantesCadastramento }" id="comprovantesCadastramento"/>
							<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscenteConvocadoMBean.formSelecionaConvocado }" id="voltar"/>
							<h:commandButton value="Novo Cadastro" action="#{ cadastramentoDiscenteConvocadoMBean.iniciarCadastramento }" id="novoCadastro"/>
							<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteConvocadoMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
						</h:form>
					</td>
				</tr>
			</tfoot>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>