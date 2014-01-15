<%@page pageEncoding="ISO-8859-1" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> > Gerenciar NEE de Discente</h2>
	<h:outputText value="#{pessoaNecessidadeEspecial.create}" />
	
	<div class="descricaoOperacao" style="width: 90%">
		<h4> Caro usuário, </h4>
		<p>
			Abaixo estão listados os dados pessoais do aluno, no qual existe a possibilidade do 
			gerenciamento das Necessidades Especiais Educacionais.
		</p>
	</div>
	
	<%-- Dados pessoais --%>
	<c:set var="discente" value="#{pessoaNecessidadeEspecial.discente}"/>
	<%@include file="/geral/discente/dados_pessoais.jsp"%>
	
	<h:form id="formulario">
		
		<table class="formulario" width="100%">				
			<tbody>
				<tr>
					<td class="subFormulario"> Necessidades Especiais Educacionais </td>
				</tr>
				<tr>
					<td align="center">
						<t:selectManyCheckbox value="#{pessoaNecessidadeEspecial.tiposNecessidadesEspeciaisSelecionadas}" id="opcaoTipo" layout="pageDirection" layoutWidth="3">
							<f:selectItems value="#{tipoNecessidadeEspecial.allCombo}"/>
						</t:selectManyCheckbox>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td>
						<h:commandButton id="btnCadastrar" value="#{pessoaNecessidadeEspecial.confirmButton}" action="#{pessoaNecessidadeEspecial.cadastrar}"/>
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{pessoaNecessidadeEspecial.voltar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
	</h:form>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>