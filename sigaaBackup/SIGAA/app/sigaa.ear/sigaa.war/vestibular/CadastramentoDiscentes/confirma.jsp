<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastramento de Discentes Convocados</h2>

	<h:form>
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>Confirme os dados abaixo para concluir o cadastramento do discente.</p> 
		</div>
		<br/>
		<table class="visualizacao" width="80%">
			<caption>Dados do Discente</caption>
			<tr>
				<th width="35%">Processo Seletivo:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.processoSeletivo.nome }</td>
			</tr>
			<tr>
				<th>Convocação:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.descricao }</td>
			</tr>
			<tr>
				<th>Situação do Candidato:</th>
				<td>
					<h:outputText value="Candidato APROVADO" rendered="#{ cadastramentoDiscenteConvocadoMBean.obj.dentroNumeroVagas }" />
					<h:outputText value="Candidato CLASSIFICADO" rendered="#{not cadastramentoDiscenteConvocadoMBean.obj.dentroNumeroVagas }" />
				</td>
			</tr>
			<tr>
				<th>Status do Discente:</th>
				<td>
					<h:outputText value="#{ cadastramentoDiscenteConvocadoMBean.obj.discente.statusString }" />
				</td>
			</tr>
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
			<c:if test="${ not empty cadastramentoDiscenteConvocadoMBean.outrosVinculos }" >
				<tr>
					<th valign="top" class="rotulo">
						<b>
							<h:outputText value="ATENÇÃO! Ao final do cadastramento o discente terá o vínculo CANCELADO:" rendered="#{ cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id > 0 }" />
							<h:outputText value="Outros vínculos do discente (não serão cancelados):" rendered="#{ cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id == 0 }" />
						</b>
					</th>
					<td>
						<table class="listagem">
						<thead>
							<th style="text-align: right;">Matrícula</th>
							<th style="text-align: left;">Nome</th>
							<th style="text-align: left;">Matriz Curricular</th>
						</thead>
						<c:forEach items="#{ cadastramentoDiscenteConvocadoMBean.outrosVinculos }" var="item" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td style="text-align: right;">${item.matricula }</td>
								<td style="text-align: left;">${item.pessoa.nome }</td>
								<td style="text-align: left;">${item.matrizCurricular.descricao }</td>
							</tr>
						</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${ cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id > 0 }">
				<%@include file="/ensino/plano_matricula_ingressantes/_quadro_horario.jsp" %>
			</c:if>
			<tfoot class="formulario">
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar" action="#{ cadastramentoDiscenteConvocadoMBean.cadastrar }" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscenteConvocadoMBean.formStatusMatricula }" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteConvocadoMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>