<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
table.formulario td {text-align: left;}
table.formulario th {font-weight: bold;}
-->
</style>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Componentes Curriculares > Validação de Componente Curricular</h2>

	<h:form id="form">
		<table class="formulario" width="85%">
			<caption class="formulario">Dados Gerais do Componente Curricular</caption>
			<tr>
				<td colspan="2" style="color: navy; text-align: center;">
					${((autorizacaoComponente.obj.ativo)?'Ativo':'Inativo')}
					<c:if test="${autorizacaoComponente.obj.statusInativo > 0}">
						- ${autorizacaoComponente.obj.statusInativoDesc}
					</c:if>
				</td>
			</tr>
			<tr>
				<th width="30%">Código:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.nome}" /></td>
			</tr>
			<tr>
				<th>Créditos de Aula:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.detalhes.crAula}" /> crs. (${autorizacaoComponente.obj.detalhes.chAula} hrs.)</td>
			</tr>
			<tr>
				<th>Créditos de Laboratório:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.detalhes.crLaboratorio}" /> crs. (${autorizacaoComponente.obj.detalhes.chLaboratorio} hrs.)</td>
			</tr>
			<tr>
				<th>Créditos de Estágio:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.detalhes.crEstagio}" /> crs.</td>
			</tr>
			<tr>
				<th>Carga Horária Total:</th>
				<td>
					<h:outputText value="#{autorizacaoComponente.obj.detalhes.chTotal}" /> hrs.
				</td>
			</tr>			
			<tr>
				<th>Pré-Requisitos:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.preRequisito}" /></td>
			</tr>
			<tr>
				<th>Co-Requisito:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.coRequisito}" /></td>
			</tr>
			<tr>
				<th>Equivalência:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.equivalencia}" /></td>
			</tr>
			<tr>
				<th>Unidade Responsável:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.unidade.nome}" /></td>
			</tr>
			<c:if test="${autorizacaoComponente.obj.curso != null and autorizacaoComponente.obj.curso.id > 0}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{autorizacaoComponente.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<c:if test="${autorizacaoComponente.obj.curso == null and autorizacaoComponente.obj.cursoNovo != null}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{autorizacaoComponente.obj.cursoNovo}" /> (Curso Novo)</td>
				</tr>
			</c:if>
			<tr>
				<th>Tipo do Componente Curricular:</th>
				<td><h:outputText value="#{autorizacaoComponente.obj.tipoComponente.descricao}" /></td>
			</tr>
			<c:if test="${not autorizacaoComponente.obj.bloco }">
				<tr>
					<th valign="top">Ementa/Descrição:</th>
					<td><h:outputText value="#{autorizacaoComponente.obj.detalhes.ementa}" /></td>
				</tr>
			</c:if>
			<!-- dados do bloco -->
			<c:if test="${autorizacaoComponente.obj.bloco }">
				<tr>
					<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Sub-unidades do Bloco</caption>
						<c:forEach items="${autorizacaoComponente.obj.subUnidades}" var="unid" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }"> 
								<td>${unid.nome}</td>
								<td width="10%">${unid.crTotal} crs</td>
							</tr>
						</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top">Observações:</th>
				<td>
				<h:inputTextarea value="#{autorizacaoComponente.observacaoCadastro }" cols="60" rows="7" />
				</td>
			</tr>
			<c:set var="obs" value="${autorizacaoComponente.observacoes }" />
			<c:if test="${not empty obs}">
			<tr>
				<th valign="top">Observações já realizadas:</th>
				<td>${obs }</td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Validar Componente"
						onclick="if (!confirm('Tem certeza que deseja VALIDAR o componente escolhido?')) return false"
						action="#{autorizacaoComponente.validar}" id="btaoValidarComponente"/>
					<h:commandButton value="Invalidar Componente"
						onclick="if (!confirm('Tem certeza que deseja INVALIDAR o componente escolhido?')) return false"
						action="#{autorizacaoComponente.invalidar}" id="btaoInvalidarComponente"/>
					<h:commandButton value="Inativar Componente" rendered="#{autorizacaoComponente.obj.ativo}"
						onclick="if (!confirm('Tem certeza que deseja DESATIVAR o componente escolhido?')) return false"
						action="#{autorizacaoComponente.desativar}" id="btaoInativarComponente"/></td>
				</tr>
				<tr>
					<td colspan="2">
					<h:commandButton value="<< Escolher outro Componente"
						action="#{autorizacaoComponente.telaComponentes}" id="btaoEscolherOutroComponente"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{autorizacaoComponente.cancelar}"  id="btaoCancelarOperacao"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
