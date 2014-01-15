<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>
<style>
	table.listagem th { font-weight:bold }
	h3, h4 { font-size: 1.3em; font-weight: bold; text-align: center; font-variant: small-caps; }
	h4 { font-size: 1 em; margin-bottom: 15px; color: #555; }
</style>

<f:view>
<h3>Comprovante de Solicitação de Cadastro de Componente Curricular</h3>
<br>
<h:outputText value="#{componenteCurricular.carregarComponente}" />
	<h:form id="form">
		<table class="listagem">
			<tr>
				<td colspan="2">  <br/> </td>
			</tr>
			<tr>
				<th width="30%">Código:</th>
				<td><h:outputText value="#{componenteCurricular.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.nome}" /></td>
			</tr>
			<c:if test="${componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th>Créditos Teóricos:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.crAula}" /> crs. (${componenteCurricular.obj.detalhes.chAula} hrs.)</td>
			</tr>
			<tr>
				<th>Créditos Práticos:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.crLaboratorio}" /> crs. (${componenteCurricular.obj.detalhes.chLaboratorio} hrs.)</td>
			</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.atividade}">
			<tr>
				<th>Carga Horária Teórica:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> hrs.</td>
			</tr>
			<tr>
				<th>Carga Horária Prática:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /> hrs.</td>
			</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.bloco}">
			<tr>
				<th>Carga Horária Total:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> hrs.</td>
			</tr>
			</c:if>
			<tr>
				<th>Pré-Requisitos:</th>
				<td><sigaa:expressao expr="${componenteCurricular.obj.preRequisito}"/></td>
			</tr>
			<tr>
				<th>Co-Requisito:</th>
				<td><sigaa:expressao expr="${componenteCurricular.obj.coRequisito}"/></td>
			</tr>
			<tr>
				<th>Equivalência:</th>
				<td><sigaa:expressao expr="${componenteCurricular.obj.equivalencia}"/></td>
			</tr>
			<tr>
				<th>Unidade Responsável:</th>
				<td><h:outputText value="#{componenteCurricular.obj.unidade.nome}" /></td>
			</tr>
			<c:if test="${not empty componenteCurricular.obj.curso.id}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{componenteCurricular.obj.curso.descricao}" /></td>
				</tr>
			</c:if>			
			<tr>
				<th>Matriculável On-Line:</th>
				<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.matriculavel}" /></td>
			</tr>
			<c:if test="${not componenteCurricular.obj.atividade}">
				<tr>
					<th>Precisa Nota:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.necessitaMediaFinal}" /></td>
				</tr>
			</c:if>
			<tr>
				<th>Pode Criar Turma Sem Solicitação:</th>
				<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.turmasSemSolicitacao}" /></td>
			</tr>
			<tr>
				<th>Tipo do Componente Curricular:</th>
				<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" /></td>
			</tr>
			<c:if test="${componenteCurricular.obj.atividade}">
				<tr>
					<th>Tipo de Atividade:</th>
					<td><h:outputText value="#{componenteCurricular.obj.tipoAtividade.descricao}" /></td>
				</tr>
			</c:if>
			<c:if test="${not componenteCurricular.obj.bloco }">
				<tr>
					<th valign="top">Ementa/Descrição:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.ementa}" /></td>
				</tr>
			</c:if>
			<!-- dados do bloco -->
			<c:if test="${componenteCurricular.obj.bloco }">
				<tr>
					<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Sub-unidades do Bloco</caption>
						<c:forEach items="${componenteCurricular.obj.subUnidades}" var="unid">
							<tr>
								<td>${unid.nome}</td>
								<td width="15%">${unid.tipoComponente.descricao}</td>
								<td width="10%">${unid.chTotal} hrs.</td>
							</tr>
						</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2">  <br/><br/><br/><br/> </td>
			</tr>
			<tr>
				<td colspan="2" align="center"> ______________________________________________________  </td>
			</tr>
			<tr>
				<td colspan="2" align="center"> 
					<h:outputText value="#{componenteCurricular.nomeChefeUnidade}" rendered="#{not empty componenteCurricular.nomeChefeUnidade}"/>
					<div style="font-size: xx-small"> ${componenteCurricular.cargo}<br>${componenteCurricular.obj.unidade.nome}</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">  <br/> </td>
			</tr>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>